package com.course.mongodb.m17.repository;

import com.course.mongodb.m17.domain.MetricsData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface MetricsDataRepository extends MongoRepository<MetricsData, String> {
    List<MetricsData> findByMetricType(MetricsData.MetricType metricType);
    List<MetricsData> findByServiceName(String serviceName);
    List<MetricsData> findByServiceNameAndTimestampBetween(String serviceName, Instant start, Instant end);
    List<MetricsData> findByMetricTypeAndTimestampBetween(MetricsData.MetricType metricType, Instant start, Instant end);
    Optional<MetricsData> findFirstByServiceNameOrderByTimestampDesc(String serviceName);
    List<MetricsData> findTop100ByServiceNameOrderByTimestampDesc(String serviceName);
}
