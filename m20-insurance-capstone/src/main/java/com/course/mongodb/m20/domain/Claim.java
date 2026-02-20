package com.course.mongodb.m20.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Document(collection = "insurance_claims")
@CompoundIndexes({
    @CompoundIndex(name = "policy_id_idx", def = "{'policyId': 1}"),
    @CompoundIndex(name = "claim_status_idx", def = "{'claimStatus': 1, 'claimType': 1}"),
    @CompoundIndex(name = "customer_id_idx", def = "{'customerId': 1}")
})
public class Claim {

    @Id
    private String id;

    @Indexed
    @Field("claim_number")
    private String claimNumber;

    @Indexed
    @Field("policy_id")
    private String policyId;

    @Indexed
    @Field("customer_id")
    private String customerId;

    @Field("claim_type")
    private ClaimType claimType;

    @Field("claim_status")
    private ClaimStatus claimStatus;

    @Field("description")
    private String description;

    @Field("incident_date")
    private Instant incidentDate;

    @Field("incident_location")
    private String incidentLocation;

    @Field("claim_amount")
    private BigDecimal claimAmount;

    @Field("approved_amount")
    private BigDecimal approvedAmount;

    @Field("deductible")
    private BigDecimal deductible;

    @Field("currency")
    private String currency;

    @Field("filed_date")
    private Instant filedDate;

    @Field("last_updated")
    private Instant lastUpdated;

    @Field("adjuster_id")
    private String adjusterId;

    @Field("documents")
    private List<ClaimDocument> documents;

    @Field("examinations")
    private List<Examination> examinations;

    @Field("payments")
    private List<Payment> payments;

    @Field("notes")
    private List<ClaimNote> notes;

    @Field("coverage_details")
    private Map<String, Object> coverageDetails;

    private List<String> tags;

    public Claim() {
        this.documents = new ArrayList<>();
        this.examinations = new ArrayList<>();
        this.payments = new ArrayList<>();
        this.notes = new ArrayList<>();
        this.tags = new ArrayList<>();
    }

    public Claim(String claimNumber, String policyId, String customerId, ClaimType claimType) {
        this();
        this.claimNumber = claimNumber;
        this.policyId = policyId;
        this.customerId = customerId;
        this.claimType = claimType;
        this.claimStatus = ClaimStatus.SUBMITTED;
        this.filedDate = Instant.now();
        this.lastUpdated = Instant.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getClaimNumber() { return claimNumber; }
    public void setClaimNumber(String claimNumber) { this.claimNumber = claimNumber; }
    public String getPolicyId() { return policyId; }
    public void setPolicyId(String policyId) { this.policyId = policyId; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public ClaimType getClaimType() { return claimType; }
    public void setClaimType(ClaimType claimType) { this.claimType = claimType; }
    public ClaimStatus getClaimStatus() { return claimStatus; }
    public void setClaimStatus(ClaimStatus claimStatus) { this.claimStatus = claimStatus; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Instant getIncidentDate() { return incidentDate; }
    public void setIncidentDate(Instant incidentDate) { this.incidentDate = incidentDate; }
    public String getIncidentLocation() { return incidentLocation; }
    public void setIncidentLocation(String incidentLocation) { this.incidentLocation = incidentLocation; }
    public BigDecimal getClaimAmount() { return claimAmount; }
    public void setClaimAmount(BigDecimal claimAmount) { this.claimAmount = claimAmount; }
    public BigDecimal getApprovedAmount() { return approvedAmount; }
    public void setApprovedAmount(BigDecimal approvedAmount) { this.approvedAmount = approvedAmount; }
    public BigDecimal getDeductible() { return deductible; }
    public void setDeductible(BigDecimal deductible) { this.deductible = deductible; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public Instant getFiledDate() { return filedDate; }
    public void setFiledDate(Instant filedDate) { this.filedDate = filedDate; }
    public Instant getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(Instant lastUpdated) { this.lastUpdated = lastUpdated; }
    public String getAdjusterId() { return adjusterId; }
    public void setAdjusterId(String adjusterId) { this.adjusterId = adjusterId; }
    public List<ClaimDocument> getDocuments() { return documents; }
    public void setDocuments(List<ClaimDocument> documents) { this.documents = documents; }
    public List<Examination> getExaminations() { return examinations; }
    public void setExaminations(List<Examination> examinations) { this.examinations = examinations; }
    public List<Payment> getPayments() { return payments; }
    public void setPayments(List<Payment> payments) { this.payments = payments; }
    public List<ClaimNote> getNotes() { return notes; }
    public void setNotes(List<ClaimNote> notes) { this.notes = notes; }
    public Map<String, Object> getCoverageDetails() { return coverageDetails; }
    public void setCoverageDetails(Map<String, Object> coverageDetails) { this.coverageDetails = coverageDetails; }
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public void assignAdjuster(String adjusterId) {
        this.adjusterId = adjusterId;
        this.claimStatus = ClaimStatus.UNDER_REVIEW;
        this.lastUpdated = Instant.now();
    }

    public void approve(BigDecimal approvedAmount) {
        this.approvedAmount = approvedAmount;
        this.claimStatus = ClaimStatus.APPROVED;
        this.lastUpdated = Instant.now();
    }

    public void deny(String reason) {
        this.claimStatus = ClaimStatus.DENIED;
        addNote("Claim denied: " + reason);
        this.lastUpdated = Instant.now();
    }

    public void addDocument(ClaimDocument document) {
        this.documents.add(document);
        this.lastUpdated = Instant.now();
    }

    public void addNote(String note) {
        ClaimNote claimNote = new ClaimNote();
        claimNote.setNote(note);
        claimNote.setCreatedAt(Instant.now());
        this.notes.add(claimNote);
        this.lastUpdated = Instant.now();
    }

    public void addPayment(Payment payment) {
        this.payments.add(payment);
        this.lastUpdated = Instant.now();
    }

    public void close() {
        this.claimStatus = ClaimStatus.CLOSED;
        this.lastUpdated = Instant.now();
    }

    public enum ClaimType {
        AUTO,
        HOME,
        HEALTH,
        LIFE,
        PROPERTY,
        LIABILITY,
        TRAVEL
    }

    public enum ClaimStatus {
        SUBMITTED,
        UNDER_REVIEW,
        PENDING_DOCUMENTS,
        ASSESSMENT,
        APPROVED,
        DENIED,
        PAID,
        CLOSED,
        REOPENED
    }

    public static class ClaimDocument {
        @Field("document_id")
        private String documentId;

        private String type;
        private String name;

        @Field("file_url")
        private String fileUrl;

        @Field("uploaded_at")
        private Instant uploadedAt;

        @Field("uploaded_by")
        private String uploadedBy;

        public ClaimDocument() {}

        public ClaimDocument(String documentId, String type, String name) {
            this.documentId = documentId;
            this.type = type;
            this.name = name;
            this.uploadedAt = Instant.now();
        }

        public String getDocumentId() { return documentId; }
        public void setDocumentId(String documentId) { this.documentId = documentId; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getFileUrl() { return fileUrl; }
        public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }
        public Instant getUploadedAt() { return uploadedAt; }
        public void setUploadedAt(Instant uploadedAt) { this.uploadedAt = uploadedAt; }
        public String getUploadedBy() { return uploadedBy; }
        public void setUploadedBy(String uploadedBy) { this.uploadedBy = uploadedBy; }
    }

    public static class Examination {
        @Field("examination_id")
        private String examinationId;

        private String type;

        @Field("scheduled_date")
        private Instant scheduledDate;

        private String location;

        @Field("examiner_name")
        private String examinerName;

        private String findings;

        private ExaminationStatus status;

        public Examination() {}

        public Examination(String examinationId, String type, Instant scheduledDate) {
            this.examinationId = examinationId;
            this.type = type;
            this.scheduledDate = scheduledDate;
            this.status = ExaminationStatus.SCHEDULED;
        }

        public String getExaminationId() { return examinationId; }
        public void setExaminationId(String examinationId) { this.examinationId = examinationId; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public Instant getScheduledDate() { return scheduledDate; }
        public void setScheduledDate(Instant scheduledDate) { this.scheduledDate = scheduledDate; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        public String getExaminerName() { return examinerName; }
        public void setExaminerName(String examinerName) { this.examinerName = examinerName; }
        public String getFindings() { return findings; }
        public void setFindings(String findings) { this.findings = findings; }
        public ExaminationStatus getStatus() { return status; }
        public void setStatus(ExaminationStatus status) { this.status = status; }

        public enum ExaminationStatus {
            SCHEDULED,
            COMPLETED,
            CANCELLED,
            NO_SHOW
        }
    }

    public static class Payment {
        @Field("payment_id")
        private String paymentId;

        private BigDecimal amount;

        @Field("payment_date")
        private Instant paymentDate;

        private PaymentMethod method;

        private String reference;

        private PaymentStatus status;

        public Payment() {}

        public Payment(String paymentId, BigDecimal amount, PaymentMethod method) {
            this.paymentId = paymentId;
            this.amount = amount;
            this.method = method;
            this.paymentDate = Instant.now();
            this.status = PaymentStatus.PENDING;
        }

        public String getPaymentId() { return paymentId; }
        public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
        public Instant getPaymentDate() { return paymentDate; }
        public void setPaymentDate(Instant paymentDate) { this.paymentDate = paymentDate; }
        public PaymentMethod getMethod() { return method; }
        public void setMethod(PaymentMethod method) { this.method = method; }
        public String getReference() { return reference; }
        public void setReference(String reference) { this.reference = reference; }
        public PaymentStatus getStatus() { return status; }
        public void setStatus(PaymentStatus status) { this.status = status; }

        public enum PaymentMethod {
            CHECK,
            BANK_TRANSFER,
            CREDIT_CARD,
            CASH
        }

        public enum PaymentStatus {
            PENDING,
            PROCESSING,
            COMPLETED,
            FAILED
        }
    }

    public static class ClaimNote {
        @Field("note_id")
        private String noteId;

        private String note;

        @Field("created_at")
        private Instant createdAt;

        @Field("created_by")
        private String createdBy;

        public ClaimNote() {
            this.noteId = java.util.UUID.randomUUID().toString();
            this.createdAt = Instant.now();
        }

        public String getNoteId() { return noteId; }
        public void setNoteId(String noteId) { this.noteId = noteId; }
        public String getNote() { return note; }
        public void setNote(String note) { this.note = note; }
        public Instant getCreatedAt() { return createdAt; }
        public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
        public String getCreatedBy() { return createdBy; }
        public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    }
}
