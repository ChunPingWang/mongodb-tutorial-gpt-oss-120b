package com.course.mongodb.m21.service;

import com.course.mongodb.m21.domain.Order;
import com.course.mongodb.m21.domain.Order.Address;
import com.course.mongodb.m21.domain.Order.OrderItem;
import com.course.mongodb.m21.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository repository;

    public OrderService(OrderRepository repository) {
        this.repository = repository;
    }

    public Order createOrder(String customerId, String customerEmail) {
        String orderNumber = generateOrderNumber();
        Order order = new Order(orderNumber, customerId, customerEmail);
        order.setCurrency("USD");
        
        return repository.save(order);
    }

    public Optional<Order> findByOrderNumber(String orderNumber) {
        return repository.findByOrderNumber(orderNumber);
    }

    public List<Order> findByCustomerId(String customerId) {
        return repository.findByCustomerId(customerId);
    }

    public List<Order> findByStatus(Order.OrderStatus status) {
        return repository.findByOrderStatus(status);
    }

    public Order addItem(String orderNumber, String productId, String productName, 
                        int quantity, BigDecimal price) {
        Order order = repository.findByOrderNumber(orderNumber)
            .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        
        if (order.getOrderStatus() != Order.OrderStatus.PENDING) {
            throw new IllegalStateException("Cannot add items to order in current status");
        }
        
        OrderItem item = new OrderItem(productId, productName, quantity, price);
        order.addItem(item);
        
        return repository.save(order);
    }

    public Order setShippingAddress(String orderNumber, Address address) {
        Order order = repository.findByOrderNumber(orderNumber)
            .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        
        order.setShippingAddress(address);
        
        return repository.save(order);
    }

    public Order setBillingAddress(String orderNumber, Address address) {
        Order order = repository.findByOrderNumber(orderNumber)
            .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        
        order.setBillingAddress(address);
        
        return repository.save(order);
    }

    public Order setShippingMethod(String orderNumber, String shippingMethod, BigDecimal shippingCost) {
        Order order = repository.findByOrderNumber(orderNumber)
            .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        
        order.setShippingMethod(shippingMethod);
        order.setShippingCost(shippingCost);
        order.calculateTotals();
        
        return repository.save(order);
    }

    public Order applyDiscount(String orderNumber, BigDecimal discountAmount) {
        Order order = repository.findByOrderNumber(orderNumber)
            .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        
        order.setDiscountAmount(discountAmount);
        order.calculateTotals();
        
        return repository.save(order);
    }

    public Order processPayment(String orderNumber, String paymentMethod, String paymentReference) {
        Order order = repository.findByOrderNumber(orderNumber)
            .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        
        order.setPaymentMethod(paymentMethod);
        order.confirmPayment(paymentReference);
        
        return repository.save(order);
    }

    public Order shipOrder(String orderNumber, String trackingNumber) {
        Order order = repository.findByOrderNumber(orderNumber)
            .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        
        if (order.getPaymentStatus() != Order.PaymentStatus.PAID) {
            throw new IllegalStateException("Cannot ship order without payment");
        }
        
        Instant estimatedDelivery = Instant.now().plus(5, ChronoUnit.DAYS);
        order.ship(trackingNumber, estimatedDelivery);
        
        return repository.save(order);
    }

    public Order deliverOrder(String orderNumber) {
        Order order = repository.findByOrderNumber(orderNumber)
            .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        
        order.deliver();
        
        return repository.save(order);
    }

    public Order cancelOrder(String orderNumber, String reason) {
        Order order = repository.findByOrderNumber(orderNumber)
            .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        
        if (order.getOrderStatus() == Order.OrderStatus.SHIPPED || 
            order.getOrderStatus() == Order.OrderStatus.DELIVERED) {
            throw new IllegalStateException("Cannot cancel shipped or delivered order");
        }
        
        order.cancel(reason);
        
        return repository.save(order);
    }

    public Order addNote(String orderNumber, String note) {
        Order order = repository.findByOrderNumber(orderNumber)
            .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        
        order.addNote(note);
        
        return repository.save(order);
    }

    private String generateOrderNumber() {
        return "ORD" + System.currentTimeMillis() + String.format("%04d", (int)(Math.random() * 10000));
    }
}
