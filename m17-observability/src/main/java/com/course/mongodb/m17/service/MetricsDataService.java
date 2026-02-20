package com.course.mongodb.m17.service;

import com.course.mongodb.m17.domain.MetricsData;
import com.course.mongodb.m17.repository.MetricsDataRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class MetricsDataService {

    private final MetricsDataRepository repository;

    public MetricsDataService(MetricsDataRepository repository) {
        this.repository = repository;
    }

    public MetricsData recordMetric(MetricsData.MetricType metricType, String serviceName, 
                                    double value, String unit) {
        MetricsData metric = new MetricsData(metricType, serviceName, value, unit);
        return repository.save(metric);
    }

    public MetricsData recordResponseTime(String serviceName, String endpoint, 
                                          long responseTimeMs, int statusCode) {
        MetricsData metric = new MetricsData(
            MetricsData.MetricType.RESPONSE_TIME, 
            serviceName, 
            responseTimeMs, 
            "ms"
        );
        metric.setEndpoint(endpoint);
        metric.setResponseTimeMs(responseTimeMs);
        metric.setStatusCode(statusCode);
        
        if (statusCode >= 400) {
            metric.incrementErrorCount();
        }
        metric.incrementRequestCount();
        
        return repository.save(metric);
    }

    public MetricsData recordSystemMetrics(String serviceName, double cpuUsage, 
                                            double memoryUsage, double diskUsage) {
        MetricsData metric = new MetricsData(
            MetricsData.MetricType.CPU_USAGE,
            serviceName,
            cpuUsage,
            "percent"
        );
        metric.setCpuUsage(cpuUsage);
        metric.setMemoryUsage(memoryUsage);
        metric.setDiskUsage(diskUsage);
        
        return repository.save(metric);
    }

    public List<MetricsData> findByServiceName(String serviceName) {
        return repository.findByServiceName(serviceName);
    }

    public List<MetricsData> findByMetricType(MetricsData.MetricType metricType) {
        return repository.findByMetricType(metricType);
    }

    public List<MetricsData> findMetricsInRange(String serviceName, Instant start, Instant end) {
        return repository.findByServiceNameAndTimestampBetween(serviceName, start, end);
    }

    public Optional<MetricsData> findLatestByService(String serviceName) {
        return repository.findFirstByServiceNameOrderByTimestampDesc(serviceName);
    }

    public List<MetricsData> findRecentMetrics(String serviceName) {
        return repository.findTop100ByServiceNameOrderByTimestampDesc(serviceName);
    }

    public double calculateAverageResponseTime(String serviceName, Instant start, Instant end) {
        List<MetricsData> metrics = repository.findByServiceNameAndTimestampBetween(serviceName, start, end);
        
        return metrics.stream()
            .filter(m -> m.getMetricType() == MetricsData.MetricType.RESPONSE_TIME)
            .mapToLong(MetricsData::getResponseTimeMs)
            .average()
            .orElse(0.0);
    }

    public double calculateErrorRate(String serviceName, Instant start, Instant end) {
        List<MetricsData> metrics = repository.findByServiceNameAndTimestampBetween(serviceName, start, end);
        
        long totalRequests = metrics.stream()
            .filter(m -> m.getMetricType() == MetricsData.MetricType.RESPONSE_TIME)
            .mapToLong(MetricsData::getRequestCount)
            .sum();
        
        long totalErrors = metrics.stream()
            .filter(m -> m.getMetricType() == MetricsData.MetricType.RESPONSE_TIME)
            .mapToInt(MetricsData::getErrorCount)
            .sum();
        
        if (totalRequests == 0) return 0.0;
        
        return (double) totalErrors / totalRequests * 100.0;
    }
}
