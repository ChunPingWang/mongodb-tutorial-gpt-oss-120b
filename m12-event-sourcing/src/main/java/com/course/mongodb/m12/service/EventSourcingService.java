package com.course.mongodb.m12.service;

import com.course.mongodb.m12.domain.DomainEvent;
import com.course.mongodb.m12.domain.EventStore;
import com.course.mongodb.m12.repository.EventStoreRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class EventSourcingService {

    private final EventStoreRepository repository;
    private final ObjectMapper objectMapper;

    public EventSourcingService(EventStoreRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    public DomainEvent.AccountOpenedEvent openAccount(String accountHolderName, 
                                                      String accountType, 
                                                      BigDecimal initialBalance) {
        DomainEvent.AccountOpenedEvent event = DomainEvent.AccountOpenedEvent.create(
            accountHolderName, accountType, initialBalance);
        saveEvent(event);
        return event;
    }

    public DomainEvent.FundsDepositedEvent depositFunds(String accountId, 
                                                        BigDecimal amount, 
                                                        String depositMethod) {
        int version = getLatestVersion(accountId) + 1;
        DomainEvent.FundsDepositedEvent event = DomainEvent.FundsDepositedEvent.create(
            accountId, version, amount, depositMethod);
        saveEvent(event);
        return event;
    }

    public void saveEvent(DomainEvent event) {
        try {
            String eventData = objectMapper.writeValueAsString(event);
            EventStore eventStore = EventStore.fromDomainEvent(event, eventData);
            repository.save(eventStore);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize event", e);
        }
    }

    public List<EventStore> getEventsForAggregate(String aggregateId) {
        return repository.findByAggregateIdOrderByVersionAsc(aggregateId);
    }

    public List<EventStore> getEventsByType(String eventType) {
        return repository.findByEventType(eventType);
    }

    public Optional<DomainEvent> reconstructAggregate(String aggregateId) {
        List<EventStore> events = getEventsForAggregate(aggregateId);
        if (events.isEmpty()) {
            return Optional.empty();
        }
        try {
            String eventData = events.get(events.size() - 1).getEventData();
            return Optional.of(objectMapper.readValue(eventData, DomainEvent.class));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize event", e);
        }
    }

    private int getLatestVersion(String aggregateId) {
        List<EventStore> events = repository.findByAggregateIdOrderByVersionAsc(aggregateId);
        return events.isEmpty() ? 0 : events.get(events.size() - 1).getVersion();
    }
}
