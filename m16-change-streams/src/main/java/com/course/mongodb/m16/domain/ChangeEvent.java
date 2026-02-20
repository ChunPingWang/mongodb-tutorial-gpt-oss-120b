package com.course.mongodb.m16.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.Map;

@Document(collection = "change_events")
public class ChangeEvent {

    @Id
    private String id;

    @Field("operation_type")
    private OperationType operationType;

    @Field("collection_name")
    private String collectionName;

    @Field("document_key")
    private String documentKey;

    @Field("full_document")
    private Map<String, Object> fullDocument;

    @Field("ns")
    private Namespace namespace;

    @Field("document_uuid")
    private String documentUUID;

    @Field("wall_time")
    private Instant wallTime;

    @Field("cluster_time")
    private Long clusterTime;

    @Field("txn_number")
    private Long txnNumber;

    @Field("lsid")
    private Map<String, Object> lsid;

    @Field("update_description")
    private UpdateDescription updateDescription;

    private Instant receivedAt;

    private EventStatus processed;

    public ChangeEvent() {
    }

    public ChangeEvent(OperationType operationType, String collectionName, String documentKey) {
        this.operationType = operationType;
        this.collectionName = collectionName;
        this.documentKey = documentKey;
        this.receivedAt = Instant.now();
        this.processed = EventStatus.RECEIVED;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public OperationType getOperationType() { return operationType; }
    public void setOperationType(OperationType operationType) { this.operationType = operationType; }
    public String getCollectionName() { return collectionName; }
    public void setCollectionName(String collectionName) { this.collectionName = collectionName; }
    public String getDocumentKey() { return documentKey; }
    public void setDocumentKey(String documentKey) { this.documentKey = documentKey; }
    public Map<String, Object> getFullDocument() { return fullDocument; }
    public void setFullDocument(Map<String, Object> fullDocument) { this.fullDocument = fullDocument; }
    public Namespace getNamespace() { return namespace; }
    public void setNamespace(Namespace namespace) { this.namespace = namespace; }
    public String getDocumentUUID() { return documentUUID; }
    public void setDocumentUUID(String documentUUID) { this.documentUUID = documentUUID; }
    public Instant getWallTime() { return wallTime; }
    public void setWallTime(Instant wallTime) { this.wallTime = wallTime; }
    public Long getClusterTime() { return clusterTime; }
    public void setClusterTime(Long clusterTime) { this.clusterTime = clusterTime; }
    public Long getTxnNumber() { return txnNumber; }
    public void setTxnNumber(Long txnNumber) { this.txnNumber = txnNumber; }
    public Map<String, Object> getLsid() { return lsid; }
    public void setLsid(Map<String, Object> lsid) { this.lsid = lsid; }
    public UpdateDescription getUpdateDescription() { return updateDescription; }
    public void setUpdateDescription(UpdateDescription updateDescription) { this.updateDescription = updateDescription; }
    public Instant getReceivedAt() { return receivedAt; }
    public void setReceivedAt(Instant receivedAt) { this.receivedAt = receivedAt; }
    public EventStatus getProcessed() { return processed; }
    public void setProcessed(EventStatus processed) { this.processed = processed; }

    public void markProcessed() {
        this.processed = EventStatus.PROCESSED;
    }

    public void markFailed(String reason) {
        this.processed = EventStatus.FAILED;
    }

    public enum OperationType {
        INSERT,
        UPDATE,
        REPLACE,
        DELETE,
        DROP,
        RENAME,
        INVALIDATE
    }

    public enum EventStatus {
        RECEIVED,
        PROCESSED,
        FAILED,
        SKIPPED
    }

    public static class Namespace {
        @Field("db")
        private String db;

        @Field("coll")
        private String coll;

        public String getDb() { return db; }
        public void setDb(String db) { this.db = db; }
        public String getColl() { return coll; }
        public void setColl(String coll) { this.coll = coll; }
    }

    public static class UpdateDescription {
        @Field("updated_fields")
        private Map<String, Object> updatedFields;

        @Field("removed_fields")
        private java.util.List<String> removedFields;

        public Map<String, Object> getUpdatedFields() { return updatedFields; }
        public void setUpdatedFields(Map<String, Object> updatedFields) { this.updatedFields = updatedFields; }
        public java.util.List<String> getRemovedFields() { return removedFields; }
        public void setRemovedFields(java.util.List<String> removedFields) { this.removedFields = removedFields; }
    }
}
