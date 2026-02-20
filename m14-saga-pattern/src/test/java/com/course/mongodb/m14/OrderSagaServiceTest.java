package com.course.mongodb.m14;

import com.course.mongodb.m14.domain.OrderSaga;
import com.course.mongodb.m14.service.OrderSagaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderSagaServiceTest {

    @Mock
    private OrderSagaRepository repository;

    @InjectMocks
    private OrderSagaService service;

    @Test
    void startSaga_shouldCreateNewSaga() {
        OrderSaga saga = new OrderSaga("order123", "customer456");
        when(repository.save(any(OrderSaga.class))).thenReturn(saga);

        OrderSaga result = service.startSaga("order123", "customer456");

        assertNotNull(result);
        assertEquals("order123", result.getOrderId());
        assertEquals(OrderSaga.SagaStatus.STARTED, result.getStatus());
    }

    @Test
    void advanceSaga_shouldMoveToNextStep() {
        OrderSaga saga = new OrderSaga("order123", "customer456");
        when(repository.findBySagaId("saga1")).thenReturn(Optional.of(saga));
        when(repository.save(any(OrderSaga.class))).thenReturn(saga);

        OrderSaga result = service.advanceSaga("saga1");

        assertEquals(OrderSaga.SagaStatus.IN_PROGRESS, result.getStatus());
        assertEquals(1, result.getCurrentStep());
    }

    @Test
    void advanceSaga_shouldCompleteWhenAllStepsDone() {
        OrderSaga saga = new OrderSaga("order123", "customer456");
        saga.advanceToNextStep();
        saga.advanceToNextStep();
        saga.advanceToNextStep();
        saga.advanceToNextStep();
        
        when(repository.findBySagaId("saga1")).thenReturn(Optional.of(saga));
        when(repository.save(any(OrderSaga.class))).thenReturn(saga);

        OrderSaga result = service.advanceSaga("saga1");

        assertEquals(OrderSaga.SagaStatus.COMPLETED, result.getStatus());
    }

    @Test
    void compensate_shouldStartCompensation() {
        OrderSaga saga = new OrderSaga("order123", "customer456");
        when(repository.findBySagaId("saga1")).thenReturn(Optional.of(saga));
        when(repository.save(any(OrderSaga.class))).thenReturn(saga);

        OrderSaga result = service.compensate("saga1");

        assertEquals(OrderSaga.SagaStatus.COMPENSATING, result.getStatus());
        assertTrue(result.getCompensatedSteps() > 0);
    }

    @Test
    void failSaga_shouldMarkAsFailed() {
        OrderSaga saga = new OrderSaga("order123", "customer456");
        when(repository.findBySagaId("saga1")).thenReturn(Optional.of(saga));
        when(repository.save(any(OrderSaga.class))).thenReturn(saga);

        OrderSaga result = service.failSaga("saga1", "Payment failed");

        assertEquals(OrderSaga.SagaStatus.FAILED, result.getStatus());
        assertEquals("Payment failed", result.getErrorMessage());
    }

    @Test
    void findByOrderId_shouldReturnSaga() {
        OrderSaga saga = new OrderSaga("order123", "customer456");
        when(repository.findByOrderId("order123")).thenReturn(Optional.of(saga));

        Optional<OrderSaga> result = service.findByOrderId("order123");

        assertTrue(result.isPresent());
        assertEquals("customer456", result.get().getCustomerId());
    }
}
