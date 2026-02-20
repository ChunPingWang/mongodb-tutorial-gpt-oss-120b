package com.course.mongodb.m17.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

@Document(collection = "metrics_data")
@CompoundIndexes({
    @CompoundIndex(name = "metric_time_idx", def = "{'metricType': 1, 'timestamp': -1}"),
    @CompoundIndex(name = "service_time_idx", def = "{'serviceName': 1, 'timestamp': -1}")
})
public class MetricsData {

    @Id
    private String id;

    @Field("metric_type")
    private MetricType metricType;

    @Field("service_name")
    private String serviceName;

    @Field("endpoint")
    private String endpoint;

    private double value;

    private String unit;

    @Field("timestamp")
    private Instant timestamp;

    @Field("response_time_ms")
    private long responseTimeMs;

    @Field("status_code")
    private int statusCode;

    @Field("error_count")
    private int errorCount;

    @Field("request_count")
    private long requestCount;

    @Field("cpu_usage")
    private double cpuUsage;

    @Field("memory_usage")
    private double memoryUsage;

    @Field("disk_usage")
    private double diskUsage;

    @Field("active_connections")
    private int activeConnections;

    private String environment;

    private String version;

    public MetricsData() {
    }

    public MetricsData(MetricType metricType, String serviceName, double value, String unit) {
        this.metricType = metricType;
        this.serviceName = serviceName;
        this.value = value;
        this.unit = unit;
        this.timestamp = Instant.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public MetricType getMetricType() { return metricType; }
    public void setMetricType(MetricType metricType) { this.metricType = metricType; }
    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }
    public String getEndpoint() { return endpoint; }
    public void setEndpoint(String endpoint) { this.endpoint = endpoint; }
    public double getValue() { return value; }
    public void setValue(double value) { this.value = value; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
    public long getResponseTimeMs() { return responseTimeMs; }
    public void setResponseTimeMs(long responseTimeMs) { this.responseTimeMs = responseTimeMs; }
    public int getStatusCode() { return statusCode; }
    public void setStatusCode(int statusCode) { this.statusCode = statusCode; }
    public int getErrorCount() { return errorCount; }
    public void setErrorCount(int errorCount) { this.errorCount = errorCount; }
    public long getRequestCount() { return requestCount; }
    public void setRequestCount(long requestCount) { this.requestCount = requestCount; }
    public double getCpuUsage() { return cpuUsage; }
    public void setCpuUsage(double cpuUsage) { this.cpuUsage = cpuUsage; }
    public double getMemoryUsage() { return memoryUsage; }
    public void setMemoryUsage(double memoryUsage) { this.memoryUsage = memoryUsage; }
    public double getDiskUsage() { return diskUsage; }
    public void setDiskUsage(double diskUsage) { this.diskUsage = diskUsage; }
    public int getActiveConnections() { return activeConnections; }
    public void setActiveConnections(int activeConnections) { this.activeConnections = activeConnections; }
    public String getEnvironment() { return environment; }
    public void setEnvironment(String environment) { this.environment = environment; }
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    public void incrementErrorCount() {
        this.errorCount++;
    }

    public void incrementRequestCount() {
        this.requestCount++;
    }

    public enum MetricType {
        RESPONSE_TIME,
        THROUGHPUT,
        ERROR_RATE,
        CPU_USAGE,
        MEMORY_USAGE,
        DISK_USAGE,
        ACTIVE_CONNECTIONS,
        CUSTOM
    }
}
