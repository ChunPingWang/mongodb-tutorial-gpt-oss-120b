package com.course.mongodb.m14.repository;

import com.course.mongodb.m14.domain.OrderSaga;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderSagaRepository extends MongoRepository<OrderSaga, String> {
    Optional<OrderSaga> findBySagaId(String sagaId);
    Optional<OrderSaga> findByOrderId(String orderId);
    List<OrderSaga> findByStatus(OrderSaga.SagaStatus status);
}
