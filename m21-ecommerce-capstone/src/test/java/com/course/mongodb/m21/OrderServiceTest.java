package com.course.mongodb.m21;

import com.course.mongodb.m21.domain.Order;
import com.course.mongodb.m21.domain.Order.Address;
import com.course.mongodb.m21.service.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository repository;

    @InjectMocks
    private OrderService service;

    @Test
    void createOrder_shouldCreateNewOrder() {
        Order order = new Order("ORD123", "cust456", "customer@example.com");
        when(repository.save(any(Order.class))).thenReturn(order);

        Order result = service.createOrder("cust456", "customer@example.com");

        assertNotNull(result);
        assertEquals("cust456", result.getCustomerId());
        assertEquals(Order.OrderStatus.PENDING, result.getOrderStatus());
    }

    @Test
    void addItem_shouldAddItemToOrder() {
        Order order = new Order("ORD123", "cust456", "customer@example.com");
        order.setCurrency("USD");
        
        when(repository.findByOrderNumber("ORD123")).thenReturn(Optional.of(order));
        when(repository.save(any(Order.class))).thenReturn(order);

        Order result = service.addItem(
            "ORD123", "prod001", "Laptop", 1, new BigDecimal("999.99")
        );

        assertEquals(1, result.getItems().size());
    }

    @Test
    void processPayment_shouldUpdateStatus() {
        Order order = new Order("ORD123", "cust456", "customer@example.com");
        when(repository.findByOrderNumber("ORD123")).thenReturn(Optional.of(order));
        when(repository.save(any(Order.class))).thenReturn(order);

        Order result = service.processPayment("ORD123", "CREDIT_CARD", "pay_123");

        assertEquals(Order.PaymentStatus.PAID, result.getPaymentStatus());
        assertEquals(Order.OrderStatus.CONFIRMED, result.getOrderStatus());
    }

    @Test
    void shipOrder_shouldSetTracking() {
        Order order = new Order("ORD123", "cust456", "customer@example.com");
        order.setPaymentStatus(Order.PaymentStatus.PAID);
        
        when(repository.findByOrderNumber("ORD123")).thenReturn(Optional.of(order));
        when(repository.save(any(Order.class))).thenReturn(order);

        Order result = service.shipOrder("ORD123", "TRACK123");

        assertEquals(Order.OrderStatus.SHIPPED, result.getOrderStatus());
        assertEquals("TRACK123", result.getTrackingNumber());
    }

    @Test
    void cancelOrder_shouldUpdateStatus() {
        Order order = new Order("ORD123", "cust456", "customer@example.com");
        when(repository.findByOrderNumber("ORD123")).thenReturn(Optional.of(order));
        when(repository.save(any(Order.class))).thenReturn(order);

        Order result = service.cancelOrder("ORD123", "Customer request");

        assertEquals(Order.OrderStatus.CANCELLED, result.getOrderStatus());
        assertEquals("Customer request", result.getCancellationReason());
    }

    @Test
    void findByOrderNumber_shouldReturnOrder() {
        Order order = new Order("ORD123", "cust456", "customer@example.com");
        when(repository.findByOrderNumber("ORD123")).thenReturn(Optional.of(order));

        Optional<Order> result = service.findByOrderNumber("ORD123");

        assertTrue(result.isPresent());
        assertEquals("ORD123", result.get().getOrderNumber());
    }
}
