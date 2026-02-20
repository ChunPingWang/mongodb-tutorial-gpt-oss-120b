package com.course.mongodb.m12.repository;

import com.course.mongodb.m12.domain.EventStore;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventStoreRepository extends MongoRepository<EventStore, String> {
    List<EventStore> findByAggregateIdOrderByVersionAsc(String aggregateId);
    List<EventStore> findByEventType(String eventType);
}
