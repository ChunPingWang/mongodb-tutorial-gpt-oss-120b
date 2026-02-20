package com.course.mongodb.m17;

import com.course.mongodb.m17.domain.MetricsData;
import com.course.mongodb.m17.service.MetricsDataService;
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
class MetricsDataServiceTest {

    @Mock
    private MetricsDataRepository repository;

    @InjectMocks
    private MetricsDataService service;

    @Test
    void recordMetric_shouldSaveNewMetric() {
        MetricsData metric = new MetricsData(
            MetricsData.MetricType.THROUGHPUT,
            "order-service",
            150.0,
            "req/s"
        );
        when(repository.save(any(MetricsData.class))).thenReturn(metric);

        MetricsData result = service.recordMetric(
            MetricsData.MetricType.THROUGHPUT,
            "order-service",
            150.0,
            "req/s"
        );

        assertNotNull(result);
        assertEquals(MetricsData.MetricType.THROUGHPUT, result.getMetricType());
        assertEquals("order-service", result.getServiceName());
    }

    @Test
    void recordResponseTime_shouldTrackErrors() {
        MetricsData metric = new MetricsData(
            MetricsData.MetricType.RESPONSE_TIME,
            "payment-service",
            250.0,
            "ms"
        );
        metric.setStatusCode(500);
        metric.setRequestCount(1);
        metric.setErrorCount(1);
        
        when(repository.save(any(MetricsData.class))).thenReturn(metric);

        MetricsData result = service.recordResponseTime(
            "payment-service",
            "/api/pay",
            250,
            500
        );

        assertNotNull(result);
        assertEquals(500, result.getStatusCode());
        assertTrue(result.getErrorCount() > 0);
    }

    @Test
    void findLatestByService_shouldReturnMostRecent() {
        MetricsData metric = new MetricsData(
            MetricsData.MetricType.CPU_USAGE,
            "user-service",
            45.5,
            "percent"
        );
        when(repository.findFirstByServiceNameOrderByTimestampDesc("user-service"))
            .thenReturn(Optional.of(metric));

        Optional<MetricsData> result = service.findLatestByService("user-service");

        assertTrue(result.isPresent());
        assertEquals("user-service", result.get().getServiceName());
    }

    @Test
    void calculateErrorRate_shouldComputeCorrectly() {
        MetricsData metric = new MetricsData(
            MetricsData.MetricType.RESPONSE_TIME,
            "api-gateway",
            100.0,
            "ms"
        );
        metric.setRequestCount(100);
        metric.setErrorCount(5);
        
        when(repository.findByServiceNameAndTimestampBetween(anyString(), any(), any()))
            .thenReturn(java.util.List.of(metric));

        double errorRate = service.calculateErrorRate("api-gateway", 
            java.time.Instant.now().minusSeconds(3600), 
            java.time.Instant.now());

        assertEquals(5.0, errorRate, 0.01);
    }
}
