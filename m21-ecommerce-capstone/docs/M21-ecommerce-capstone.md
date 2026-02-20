# M21: 電商平台 Capstone

## 學習目標
建構電商核心系統，強調高併發與最終一致性

---

## M21-DOC-01: 電商平台架構設計

### Polyglot Persistence 架構

```
┌─────────────────────────────────────────────────────────────────┐
│                         電商平台架構                              │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│   ┌─────────────┐  ┌─────────────┐  ┌─────────────┐          │
│   │  商品目錄    │  │   庫存      │  │   訂單      │          │
│   │  (MongoDB)  │  │(MongoDB +   │  │  (MongoDB)  │          │
│   │             │  │  Redis)     │  │             │          │
│   └──────┬──────┘  └──────┬──────┘  └──────┬──────┘          │
│          │                 │                 │                  │
│          │                 │                 │                  │
│   ┌──────┴──────┐  ┌──────┴──────┐  ┌──────┴──────┐          │
│   │  搜尋引擎   │  │  購物車      │  │   支付      │          │
│   │ (Read Model)│  │(Redis+Mongo)│  │  (Service)  │          │
│   └─────────────┘  └─────────────┘  └─────────────┘          │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 資料模型設計

| Collection | 用途 | 特性 |
|------------|------|------|
| products | 商品主檔 | 多型商品結構 |
| inventory | 庫存 | 原子更新 |
| carts | 購物車 | Redis 即時 + MongoDB 持久化 |
| orders | 訂單 | Event Sourcing |
| customers | 客戶資料 | 客戶 360 |

### 高併發庫存扣減策略

**Atomic Update:**

```java
// 使用 findAndModify 原子更新
Query query = new Query(Criteria.where("sku").is(sku)
    .and("quantity").gte(requestedQuantity));

Update update = new Update()
    .inc("quantity", -requestedQuantity)
    .set("version", version + 1);

FindAndModifyOptions options = new FindAndModifyOptions()
    .returnNew(true);

Inventory updated = mongoTemplate.findAndModify(
    query, update, options, Inventory.class);
```

**Optimistic Lock:**

```java
@Version
private Long version;

// 樂觀鎖更新
@Retryable(StaleStateException.class)
public boolean deductStock(String sku, int quantity) {
    Inventory inv = repository.findBySku(sku);
    if (inv.getQuantity() >= quantity) {
        inv.setQuantity(inv.getQuantity() - quantity);
        repository.save(inv);
        return true;
    }
    return false;
}
```

---

## M21-LAB-01: 完整系統實作

### 實作重點

1. **Product Catalog: 多型商品 + 多維搜尋**
2. **Shopping Cart: Redis + MongoDB 混合**
3. **Order SAGA: 庫存 → 付款 → 出貨**
4. **CQRS: 商品列表 Read Model**
5. **Change Streams: 庫存變動即時同步**
6. **壓力測試: 模擬秒殺場景**
7. **完整 BDD Test Suite**

### Product Catalog

```java
// 多型商品
public sealed interface Product 
    permits SimpleProduct, ConfigurableProduct, DigitalProduct {
    String getProductId();
    ProductStatus getStatus();
}

@TypeAlias("simple")
public record SimpleProduct(
    String productId,
    String name,
    BigDecimal price,
    ProductStatus status,
    int stock
) implements Product {}

@TypeAlias("configurable")
public record ConfigurableProduct(
    String productId,
    String name,
    BigDecimal basePrice,
    ProductStatus status,
    List<Variant> variants
) implements Product {}
```

### Shopping Cart

```java
@Service
public class CartService {
    
    @Autowired
    private RedisTemplate<String, CartItem> redisCart;
    
    @Autowired
    private CartRepository mongoCart;
    
    public void addToCart(String customerId, String sku, int quantity) {
        // Redis: 即時更新
        String key = "cart:" + customerId;
        redisCart.opsForHash().put(key, sku, quantity);
        
        // MongoDB: 定期持久化
        // 或者異步同步
    }
}
```

### Order SAGA

```java
@Service
public class OrderSagaOrchestrator {
    
    @Transactional
    public Order placeOrder(PlaceOrderCommand cmd) {
        // Step 1: 庫存扣減
        inventoryService.deduct(cmd.getItems());
        
        // Step 2: 付款
        paymentService.charge(cmd.getCustomerId(), cmd.getTotalAmount());
        
        // Step 3: 建立訂單
        Order order = orderRepository.save(cmd.toOrder());
        
        // Step 4: 通知
        notificationService.sendOrderConfirmation(order);
        
        return order;
    }
}
```

### CQRS Read Model

```java
// Read Model: 商品列表
@Document("product_list_views")
public class ProductListView {
    @Id
    private String productId;
    private String name;
    private BigDecimal price;
    private String primaryImage;
    private boolean inStock;
    private BigDecimal rating;
}
```

### Change Streams 庫存同步

```java
@Service
public class InventorySyncService {
    
    @PostConstruct
    public void listen() {
        mongoTemplate.changeStream("inventory")
            .filter(Aggregation.newAggregation(
                match(Criteria.where("operationType").is("update"))
            ))
            .doOnNext(event -> {
                // 更新 Read Model
                productViewService.updateStock(
                    event.getFullDocument()
                );
                // 同步到搜尋引擎
                searchService.updateIndex(event.getFullDocument());
            })
            .subscribe();
    }
}
```

### 壓力測試: 秒殺場景

```java
@Test
void flashSaleStressTest() throws InterruptedException {
    int threads = 100;
    int itemsPerThread = 10;
    CountDownLatch latch = new CountDownLatch(threads);
    
    for (int i = 0; i < threads; i++) {
        executor.submit(() -> {
            try {
                for (int j = 0; j < itemsPerThread; j++) {
                    inventoryService.deductStock("SKU_限量", 1);
                }
            } finally {
                latch.countDown();
            }
        });
    }
    
    latch.await();
    
    // 驗證: 庫存不應為負數
    Inventory finalStock = inventoryRepository.findBySku("SKU_限量");
    assertThat(finalStock.getQuantity()).isGreaterThanOrEqualTo(0);
}
```

### BDD Test Suite

```gherkin
Feature: 電商訂單流程
  Scenario: 正常下單
    Given 商品庫存 100 件
    When 客戶下單購買 2 件
    Then 庫存扣減為 98 件
    And 訂單狀態為 CONFIRMED

  Scenario: 庫存不足
    Given 商品庫存 1 件
    When 客戶嘗試下單購買 2 件
    Then 下單失敗
    And 回應庫存不足

  Scenario: 購物車功能
    Given 客戶已將商品加入購物車
    When 查看購物車
    Then 顯示商品清單與總金額

  Scenario: 秒殺活動
    Given 秒殺商品庫存 10 件
    When 100 位客戶同時搶購
    Then 只有 10 位成功
    And 其他 90 位收到庫存不足通知
```
