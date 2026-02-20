package com.course.mongodb.m16.service;

import com.course.mongodb.m16.domain.ChangeEvent;
import com.course.mongodb.m16.repository.ChangeEventRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChangeEventService {

    private final ChangeEventRepository repository;

    public ChangeEventService(ChangeEventRepository repository) {
        this.repository = repository;
    }

    public ChangeEvent recordEvent(ChangeEvent.OperationType operationType, 
                                   String collectionName, String documentKey) {
        ChangeEvent event = new ChangeEvent(operationType, collectionName, documentKey);
        return repository.save(event);
    }

    public ChangeEvent recordFullEvent(ChangeEvent event) {
        return repository.save(event);
    }

    public Optional<ChangeEvent> findById(String id) {
        return repository.findById(id);
    }

    public List<ChangeEvent> findByCollection(String collectionName) {
        return repository.findByCollectionName(collectionName);
    }

    public List<ChangeEvent> findByOperationType(ChangeEvent.OperationType operationType) {
        return repository.findByOperationType(operationType);
    }

    public List<ChangeEvent> findByProcessed(ChangeEvent.EventStatus status) {
        return repository.findByProcessed(status);
    }

    public Optional<ChangeEvent> findByDocumentKey(String documentKey) {
        return repository.findByDocumentKey(documentKey);
    }

    public ChangeEvent markProcessed(String eventId) {
        ChangeEvent event = repository.findById(eventId)
            .orElseThrow(() -> new IllegalArgumentException("Event not found"));
        
        event.markProcessed();
        return repository.save(event);
    }

    public ChangeEvent markFailed(String eventId, String reason) {
        ChangeEvent event = repository.findById(eventId)
            .orElseThrow(() -> new IllegalArgumentException("Event not found"));
        
        event.markFailed(reason);
        return repository.save(event);
    }

    public List<ChangeEvent> findRecentEvents(int limit) {
        return repository.findAll().stream()
            .sorted((a, b) -> b.getReceivedAt().compareTo(a.getReceivedAt()))
            .limit(limit)
            .toList();
    }
}
