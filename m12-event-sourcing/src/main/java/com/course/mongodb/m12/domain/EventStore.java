package com.course.mongodb.m12.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

@Document(collection = "event_store")
public class EventStore {

    @Id
    private String id;

    @Field("aggregate_id")
    private String aggregateId;

    @Field("event_type")
    private String eventType;

    @Field("event_data")
    private String eventData;

    @Field("occurred_at")
    private Instant occurredAt;

    @Field("version")
    private int version;

    public EventStore() {
    }

    public EventStore(String aggregateId, String eventType, String eventData, int version) {
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.eventData = eventData;
        this.occurredAt = Instant.now();
        this.version = version;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getAggregateId() { return aggregateId; }
    public void setAggregateId(String aggregateId) { this.aggregateId = aggregateId; }
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public String getEventData() { return eventData; }
    public void setEventData(String eventData) { this.eventData = eventData; }
    public Instant getOccurredAt() { return occurredAt; }
    public void setOccurredAt(Instant occurredAt) { this.occurredAt = occurredAt; }
    public int getVersion() { return version; }
    public void setVersion(int version) { this.version = version; }

    public static EventStore fromDomainEvent(DomainEvent event, String eventData) {
        String eventType = event.getClass().getSimpleName();
        return new EventStore(event.aggregateId(), eventType, eventData, event.version());
    }
}
