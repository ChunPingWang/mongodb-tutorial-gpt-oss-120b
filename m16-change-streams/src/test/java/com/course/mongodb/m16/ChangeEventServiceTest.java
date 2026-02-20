package com.course.mongodb.m16;

import com.course.mongodb.m16.domain.ChangeEvent;
import com.course.mongodb.m16.service.ChangeEventService;
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
class ChangeEventServiceTest {

    @Mock
    private ChangeEventRepository repository;

    @InjectMocks
    private ChangeEventService service;

    @Test
    void recordEvent_shouldCreateNewEvent() {
        ChangeEvent event = new ChangeEvent(
            ChangeEvent.OperationType.INSERT, 
            "customers", 
            "cust123"
        );
        when(repository.save(any(ChangeEvent.class))).thenReturn(event);

        ChangeEvent result = service.recordEvent(
            ChangeEvent.OperationType.INSERT, 
            "customers", 
            "cust123"
        );

        assertNotNull(result);
        assertEquals(ChangeEvent.OperationType.INSERT, result.getOperationType());
        assertEquals("customers", result.getCollectionName());
        assertEquals(ChangeEvent.EventStatus.RECEIVED, result.getProcessed());
    }

    @Test
    void markProcessed_shouldUpdateStatus() {
        ChangeEvent event = new ChangeEvent(
            ChangeEvent.OperationType.UPDATE,
            "accounts",
            "acc456"
        );
        when(repository.findById("event123")).thenReturn(Optional.of(event));
        when(repository.save(any(ChangeEvent.class))).thenReturn(event);

        ChangeEvent result = service.markProcessed("event123");

        assertEquals(ChangeEvent.EventStatus.PROCESSED, result.getProcessed());
    }

    @Test
    void markFailed_shouldUpdateStatus() {
        ChangeEvent event = new ChangeEvent(
            ChangeEvent.OperationType.DELETE,
            "orders",
            "ord789"
        );
        when(repository.findById("event123")).thenReturn(Optional.of(event));
        when(repository.save(any(ChangeEvent.class))).thenReturn(event);

        ChangeEvent result = service.markFailed("event123", "Processing error");

        assertEquals(ChangeEvent.EventStatus.FAILED, result.getProcessed());
    }

    @Test
    void findByDocumentKey_shouldReturnEvent() {
        ChangeEvent event = new ChangeEvent(
            ChangeEvent.OperationType.REPLACE,
            "products",
            "prod123"
        );
        when(repository.findByDocumentKey("prod123")).thenReturn(Optional.of(event));

        Optional<ChangeEvent> result = service.findByDocumentKey("prod123");

        assertTrue(result.isPresent());
        assertEquals("prod123", result.get().getDocumentKey());
    }
}
