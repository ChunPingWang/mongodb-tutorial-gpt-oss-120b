package com.course.mongodb.m14.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.UUID;

@Document(collection = "order_sagas")
public class OrderSaga {

    @Id
    private String id;

    @Field("saga_id")
    private String sagaId;

    @Field("order_id")
    private String orderId;

    @Field("customer_id")
    private String customerId;

    private SagaStatus status;

    @Field("current_step")
    private int currentStep;

    @Field("compensated_steps")
    private int compensatedSteps;

    @Field("error_message")
    private String errorMessage;

    @Field("started_at")
    private Instant startedAt;

    @Field("completed_at")
    private Instant completedAt;

    private SagaStep current;

    public OrderSaga() {
    }

    public OrderSaga(String orderId, String customerId) {
        this.sagaId = UUID.randomUUID().toString();
        this.orderId = orderId;
        this.customerId = customerId;
        this.status = SagaStatus.STARTED;
        this.currentStep = 0;
        this.compensatedSteps = 0;
        this.startedAt = Instant.now();
        this.current = SagaStep.CREATE_ORDER;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getSagaId() { return sagaId; }
    public void setSagaId(String sagaId) { this.sagaId = sagaId; }
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public SagaStatus getStatus() { return status; }
    public void setStatus(SagaStatus status) { this.status = status; }
    public int getCurrentStep() { return currentStep; }
    public void setCurrentStep(int currentStep) { this.currentStep = currentStep; }
    public int getCompensatedSteps() { return compensatedSteps; }
    public void setCompensatedSteps(int compensatedSteps) { this.compensatedSteps = compensatedSteps; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    public Instant getStartedAt() { return startedAt; }
    public void setStartedAt(Instant startedAt) { this.startedAt = startedAt; }
    public Instant getCompletedAt() { return completedAt; }
    public void setCompletedAt(Instant completedAt) { this.completedAt = completedAt; }
    public SagaStep getCurrent() { return current; }
    public void setCurrent(SagaStep current) { this.current = current; }

    public void advanceToNextStep() {
        this.currentStep++;
        this.current = SagaStep.values()[Math.min(this.currentStep, SagaStep.values().length - 1)];
    }

    public void complete() {
        this.status = SagaStatus.COMPLETED;
        this.completedAt = Instant.now();
    }

    public void fail(String errorMessage) {
        this.status = SagaStatus.FAILED;
        this.errorMessage = errorMessage;
    }

    public void compensate() {
        this.status = SagaStatus.COMPENSATING;
        this.compensatedSteps++;
    }

    public void compensateComplete() {
        if (this.compensatedSteps >= this.currentStep) {
            this.status = SagaStatus.COMPENSATED;
            this.completedAt = Instant.now();
        }
    }

    public enum SagaStatus {
        STARTED,
        IN_PROGRESS,
        COMPLETED,
        FAILED,
        COMPENSATING,
        COMPENSATED
    }

    public enum SagaStep {
        CREATE_ORDER,
        RESERVE_INVENTORY,
        PROCESS_PAYMENT,
        SHIP_ORDER,
        NOTIFY_CUSTOMER
    }
}
