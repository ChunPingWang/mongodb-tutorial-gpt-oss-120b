# M14: SAGA Pattern

## 學習目標
實作跨 Aggregate 的分散式交易協調

---

## M14-DOC-01: SAGA Pattern 與 MongoDB

### Choreography SAGA vs Orchestration SAGA

**Choreography (事件驅動)：**
```
┌─────────┐     ┌─────────┐     ┌─────────┐
│  Order  │────▶│ Payment │────▶│ Shipping│
│ Service │     │ Service │     │ Service │
└─────────┘     └─────────┘     └─────────┘
     ▲               ▲               ▲
     │               │               │
     └───────────────┴───────────────┘
              Domain Events
```

**Orchestration (協調者)：**
```
┌─────────────────────────────────────────┐
│           Order Saga Orchestrator       │
│  ┌─────────────────────────────────────┐ │
│  │  Order → Payment → Shipping        │ │
│  └─────────────────────────────────────┘ │
└─────────────────────────────────────────┘
```

### Compensating Transaction 設計

```java
public class OrderSaga {
    
    public void execute(OrderSagaData data) {
        try {
            // Step 1: Create Order
            Order order = orderService.createOrder(data.getOrderRequest());
            data.setOrderId(order.getId());
            
            // Step 2: Reserve Inventory (compensate: release inventory)
            inventoryService.reserve(data.getItems());
            
            // Step 3: Process Payment (compensate: refund)
            paymentService.charge(data.getCustomerId(), data.getTotalAmount());
            
            // Step 4: Create Shipping (compensate: cancel)
            shippingService.create(data.getShippingRequest());
            
        } catch (Exception e) {
            compensate(data);
            throw e;
        }
    }
    
    private void compensate(OrderSagaData data) {
        if (data.getShippingId() != null) {
            shippingService.cancel(data.getShippingId());
        }
        if (data.getPaymentId() != null) {
            paymentService.refund(data.getPaymentId());
        }
        if (data.getOrderId() != null) {
            orderService.cancel(data.getOrderId());
        }
    }
}
```

### SAGA State Machine

```
    ┌─────────┐
    │ CREATED │
    └────┬────┘
         │
         ▼
┌────────────────┐     ┌─────────────────┐
│ INVENTORY     │ ──▶ │ PAYMENT         │
│ RESERVED      │     │ PROCESSING      │
└────────────────┘     └────────┬────────┘
                                │
                                ▼
                    ┌─────────────────────┐
                    │ SHIPPING            │
                    │ CREATED             │
                    └──────────┬──────────┘
                               │
                               ▼
                    ┌─────────────────────┐
                    │ COMPLETED            │
                    └─────────────────────┘

COMPENSATING:
    ANY_STATE ──▶ CANCELLED
```

---

## M14-DOC-02: 冪等性與重試機制

### Idempotency Key 設計

```java
public record PaymentRequest(
    String idempotencyKey,
    String customerId,
    BigDecimal amount,
    String orderId
) {
    // 每次重試使用相同的 idempotencyKey
    // 確保只扣款一次
}
```

### Outbox Pattern

```java
// 訂單表 + 訊息表 (在同一 Transaction 中)
@Transactional
public void createOrder(OrderRequest request) {
    Order order = orderRepository.save(request.toOrder());
    
    // 寫入 Outbox
    OutboxMessage message = OutboxMessage.builder()
        .aggregateId(order.getId())
        .eventType("OrderCreated")
        .payload(serialize(order))
        .build();
    outboxRepository.save(message);
}

// 獨立執行緒發送 Outbox 訊息
@Scheduled(fixedDelay = 1000)
public void processOutbox() {
    List<OutboxMessage> messages = outboxRepository.findUnprocessed();
    for (OutboxMessage msg : messages) {
        eventPublisher.publish(msg.getEventType(), msg.getPayload());
        msg.markAsProcessed();
        outboxRepository.save(msg);
    }
}
```

### MongoDB Unique Index 保證冪等性

```java
@Indexed(unique = true, sparse = true)
private String idempotencyKey;
```

---

## M14-LAB-01: 電商下單 SAGA

### BDD Feature

```gherkin
Feature: 訂單建立 SAGA
  Scenario: 正常下單流程
    Given 商品 "P001" 庫存 10 件
    And 客戶 "C001" 錢包餘額 5000 元
    When 客戶下單購買 2 件 商品單價 1000 元
    Then 庫存扣減至 8 件
    And 錢包餘額扣減至 3000 元
    And 訂單狀態為 CONFIRMED

  Scenario: 付款失敗補償
    Given 商品 "P001" 庫存 10 件
    And 客戶 "C001" 錢包餘額 500 元
    When 客戶下單購買 2 件 商品單價 1000 元
    Then 庫存恢復至 10 件 (補償)
    And 訂單狀態為 CANCELLED
```

### Orchestration SAGA 實作

```java
@Service
public class OrderSagaOrchestrator {
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private InventoryService inventoryService;
    
    @Autowired
    private WalletService walletService;
    
    @Transactional
    public Order placeOrder(PlaceOrderCommand command) {
        OrderSagaData data = new OrderSagaData(command);
        
        try {
            // Step 1: Create Order (compensate: cancel order)
            Order order = orderService.create(command.toOrderRequest());
            data.setOrderId(order.getId());
            
            // Step 2: Reserve Inventory (compensate: release inventory)
            inventoryService.reserve(command.getItems());
            data.setInventoryReserved(true);
            
            // Step 3: Charge Wallet (compensate: refund)
            walletService.charge(command.getCustomerId(), command.getTotalAmount());
            data.setPaymentCharged(true);
            
            orderService.confirm(order.getId());
            return order;
            
        } catch (Exception e) {
            compensate(data);
            throw new OrderFailedException(e.getMessage());
        }
    }
    
    private void compensate(OrderSagaData data) {
        if (data.isPaymentCharged()) {
            walletService.refund(data.getCustomerId(), data.getTotalAmount());
        }
        if (data.isInventoryReserved()) {
            inventoryService.release(data.getItems());
        }
        if (data.getOrderId() != null) {
            orderService.cancel(data.getOrderId());
        }
    }
}
```

---

## M14-LAB-02: 保險投保 SAGA

### 投保流程

```
核保 → 建立保單 → 建立收費排程 → 通知客戶
```

### 補償操作

| 步驟 | 補償操作 |
|------|---------|
| 核保通過 | 取消核保結果 |
| 建立保單 | 終止保單 |
| 建立收費排程 | 取消排程 |
| 通知客戶 | 發送取消通知 |
