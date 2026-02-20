package com.course.mongodb.m18.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Document(collection = "schema_versions")
@CompoundIndexes({
    @CompoundIndex(name = "version_idx", def = "{'version': -1}"),
    @CompoundIndex(name = "applied_idx", def = "{'appliedAt': -1}")
})
public class SchemaVersion {

    @Id
    private String id;

    private String version;

    private String description;

    @Field("script")
    private String migrationScript;

    private String checksum;

    @Field("applied_by")
    private String appliedBy;

    @Field("applied_at")
    private Instant appliedAt;

    private SchemaState state;

    @Field("execution_time_ms")
    private long executionTimeMs;

    @Field("error_message")
    private String errorMessage;

    private Map<String, Object> metadata;

    @Field("rollback_script")
    private String rollbackScript;

    private List<String> affectedCollections;

    public SchemaVersion() {
    }

    public SchemaVersion(String version, String description, String migrationScript) {
        this.version = version;
        this.description = description;
        this.migrationScript = migrationScript;
        this.state = SchemaState.PENDING;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getMigrationScript() { return migrationScript; }
    public void setMigrationScript(String migrationScript) { this.migrationScript = migrationScript; }
    public String getChecksum() { return checksum; }
    public void setChecksum(String checksum) { this.checksum = checksum; }
    public String getAppliedBy() { return appliedBy; }
    public void setAppliedBy(String appliedBy) { this.appliedBy = appliedBy; }
    public Instant getAppliedAt() { return appliedAt; }
    public void setAppliedAt(Instant appliedAt) { this.appliedAt = appliedAt; }
    public SchemaState getState() { return state; }
    public void setState(SchemaState state) { this.state = state; }
    public long getExecutionTimeMs() { return executionTimeMs; }
    public void setExecutionTimeMs(long executionTimeMs) { this.executionTimeMs = executionTimeMs; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    public String getRollbackScript() { return rollbackScript; }
    public void setRollbackScript(String rollbackScript) { this.rollbackScript = rollbackScript; }
    public List<String> getAffectedCollections() { return affectedCollections; }
    public void setAffectedCollections(List<String> affectedCollections) { this.affectedCollections = affectedCollections; }

    public void markApplied(String appliedBy) {
        this.state = SchemaState.APPLIED;
        this.appliedBy = appliedBy;
        this.appliedAt = Instant.now();
    }

    public void markFailed(String errorMessage) {
        this.state = SchemaState.FAILED;
        this.errorMessage = errorMessage;
    }

    public enum SchemaState {
        PENDING,
        APPLIED,
        FAILED,
        ROLLED_BACK
    }
}
