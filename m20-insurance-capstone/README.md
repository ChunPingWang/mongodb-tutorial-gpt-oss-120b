# M20: Insurance Capstone

This module demonstrates a comprehensive insurance claim domain model using MongoDB.

## Domain Model: Claim

The `Claim` aggregate represents an insurance claim with:

### Claim Types
- AUTO - Auto insurance claims
- HOME - Home insurance claims
- HEALTH - Health insurance claims
- LIFE - Life insurance claims
- PROPERTY - Property insurance claims
- LIABILITY - Liability insurance claims
- TRAVEL - Travel insurance claims

### Claim Status
- SUBMITTED - Claim submitted
- UNDER_REVIEW - Being reviewed by adjuster
- PENDING_DOCUMENTS - Awaiting documents
- ASSESSMENT - Under assessment
- APPROVED - Approved for payment
- DENIED - Claim denied
- PAID - Payment completed
- CLOSED - Claim closed
- REOPENED - Previously closed, reopened

### Components

1. **Documents**: Supporting documents
   - Document ID, Type, Name, File URL
   - Upload timestamp, Uploaded by

2. **Examinations**: Claim examinations
   - Examination ID, Type, Scheduled date
   - Location, Examiner name, Findings

3. **Payments**: Claim payments
   - Payment ID, Amount, Date
   - Payment method, Reference, Status

4. **Notes**: Claim notes and comments
   - Note ID, Content, Created at, Created by

### Operations

1. **Claim Filing**
   - File new claim
   - Set claim details

2. **Claim Processing**
   - Assign adjuster
   - Add documents
   - Add notes
   - Approve/Deny

3. **Payment Processing**
   - Process payments
   - Track total paid

4. **Claim Lifecycle**
   - Close claim
   - Reopen claim

## Design Patterns

1. **Aggregate Pattern**: Claim as root aggregate
2. **Embedded Documents**: Documents, examinations, payments, notes
3. **Value Objects**: Claim types, statuses, payment methods

## Running Tests

```bash
./gradlew :m20-insurance-capstone:test
```
