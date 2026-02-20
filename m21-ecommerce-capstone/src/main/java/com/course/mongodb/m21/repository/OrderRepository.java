package com.course.mongodb.m21.repository;

import com.course.mongodb.m21.domain.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {
    Optional<Order> findByOrderNumber(String orderNumber);
    List<Order> findByCustomerId(String customerId);
    List<Order> findByOrderStatus(Order.OrderStatus status);
    List<Order> findByPaymentStatus(Order.PaymentStatus status);
    boolean existsByOrderNumber(String orderNumber);
}
