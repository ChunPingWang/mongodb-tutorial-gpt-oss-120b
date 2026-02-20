package com.course.mongodb.m14.service;

import com.course.mongodb.m14.domain.OrderSaga;
import com.course.mongodb.m14.repository.OrderSagaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderSagaService {

    private final OrderSagaRepository repository;

    public OrderSagaService(OrderSagaRepository repository) {
        this.repository = repository;
    }

    public OrderSaga startSaga(String orderId, String customerId) {
        OrderSaga saga = new OrderSaga(orderId, customerId);
        return repository.save(saga);
    }

    public Optional<OrderSaga> findBySagaId(String sagaId) {
        return repository.findBySagaId(sagaId);
    }

    public Optional<OrderSaga> findByOrderId(String orderId) {
        return repository.findByOrderId(orderId);
    }

    public List<OrderSaga> findByStatus(OrderSaga.SagaStatus status) {
        return repository.findByStatus(status);
    }

    public OrderSaga advanceSaga(String sagaId) {
        OrderSaga saga = repository.findBySagaId(sagaId)
            .orElseThrow(() -> new IllegalArgumentException("Saga not found"));
        
        if (saga.getStatus() != OrderSaga.SagaStatus.STARTED && 
            saga.getStatus() != OrderSaga.SagaStatus.IN_PROGRESS) {
            throw new IllegalStateException("Saga cannot be advanced in current state");
        }
        
        saga.advanceToNextStep();
        
        if (saga.getCurrentStep() >= OrderSaga.SagaStep.values().length - 1) {
            saga.complete();
        } else {
            saga.setStatus(OrderSaga.SagaStatus.IN_PROGRESS);
        }
        
        return repository.save(saga);
    }

    public OrderSaga compensate(String sagaId) {
        OrderSaga saga = repository.findBySagaId(sagaId)
            .orElseThrow(() -> new IllegalArgumentException("Saga not found"));
        
        saga.compensate();
        repository.save(saga);
        
        return compensateStep(saga);
    }

    private OrderSaga compensateStep(OrderSaga saga) {
        switch (saga.getCurrent()) {
            case PROCESS_PAYMENT -> compensatePayment(saga);
            case RESERVE_INVENTORY -> compensateInventory(saga);
            case CREATE_ORDER -> compensateOrder(saga);
            default -> {}
        }
        
        saga.compensateComplete();
        
        if (saga.getStatus() == OrderSaga.SagaStatus.COMPENSATING) {
            return compensateStep(saga);
        }
        
        return repository.save(saga);
    }

    private void compensatePayment(OrderSaga saga) {
        saga.setErrorMessage("Payment reversed for order: " + saga.getOrderId());
    }

    private void compensateInventory(OrderSaga saga) {
        saga.setErrorMessage("Inventory released for order: " + saga.getOrderId());
    }

    private void compensateOrder(OrderSaga saga) {
        saga.setErrorMessage("Order cancelled: " + saga.getOrderId());
    }

    public OrderSaga failSaga(String sagaId, String errorMessage) {
        OrderSaga saga = repository.findBySagaId(sagaId)
            .orElseThrow(() -> new IllegalArgumentException("Saga not found"));
        
        saga.fail(errorMessage);
        return repository.save(saga);
    }
}
