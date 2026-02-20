package com.course.mongodb.m12;

import com.course.mongodb.m12.domain.DomainEvent;
import com.course.mongodb.m12.domain.EventStore;
import com.course.mongodb.m12.service.EventSourcingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventSourcingServiceTest {

    @Mock
    private EventStoreRepository repository;

    @Test
    void openAccount_shouldCreateAccountOpenedEvent() {
        ObjectMapper objectMapper = new ObjectMapper();
        EventSourcingService service = new EventSourcingService(repository, objectMapper);

        DomainEvent.AccountOpenedEvent result = service.openAccount(
            "John Doe", "SAVINGS", new BigDecimal("1000"));

        assertNotNull(result);
        assertEquals("John Doe", result.accountHolderName());
        assertEquals("SAVINGS", result.accountType());
        assertEquals(new BigDecimal("1000"), result.initialBalance());

        verify(repository, times(1)).save(any(EventStore.class));
    }

    @Test
    void depositFunds_shouldCreateFundsDepositedEvent() {
        ObjectMapper objectMapper = new ObjectMapper();
        EventSourcingService service = new EventSourcingService(repository, objectMapper);
        when(repository.findByAggregateIdOrderByVersionAsc("account123"))
            .thenReturn(List.of());

        DomainEvent.FundsDepositedEvent result = service.depositFunds(
            "account123", new BigDecimal("500"), "CASH");

        assertNotNull(result);
        assertEquals(new BigDecimal("500"), result.amount());
        assertEquals("CASH", result.depositMethod());
        assertEquals(1, result.version());
    }

    @Test
    void getEventsForAggregate_shouldReturnEventsInOrder() {
        ObjectMapper objectMapper = new ObjectMapper();
        EventSourcingService service = new EventSourcingService(repository, objectMapper);

        EventStore event1 = new EventStore("account123", "AccountOpenedEvent", "{}", 1);
        EventStore event2 = new EventStore("account123", "FundsDepositedEvent", "{}", 2);
        when(repository.findByAggregateIdOrderByVersionAsc("account123"))
            .thenReturn(List.of(event1, event2));

        List<EventStore> events = service.getEventsForAggregate("account123");

        assertEquals(2, events.size());
        assertEquals(1, events.get(0).getVersion());
        assertEquals(2, events.get(1).getVersion());
    }

    @Test
    void saveEvent_shouldSerializeAndSaveEvent() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        EventSourcingService service = new EventSourcingService(repository, objectMapper);

        DomainEvent.AccountOpenedEvent event = DomainEvent.AccountOpenedEvent.create(
            "John Doe", "SAVINGS", new BigDecimal("1000"));

        service.saveEvent(event);

        ArgumentCaptor<EventStore> captor = ArgumentCaptor.forClass(EventStore.class);
        verify(repository).save(captor.capture());
        assertEquals("account123", captor.getValue().getAggregateId());
    }
}
