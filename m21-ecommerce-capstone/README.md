# M21: Ecommerce Capstone

This module demonstrates a comprehensive ecommerce order domain model using MongoDB.

## Domain Model: Order

The `Order` aggregate represents an ecommerce order with:

### Order Status
- PENDING - Order created, awaiting payment
- CONFIRMED - Order confirmed, payment received
- PROCESSING - Order being prepared
- SHIPPED - Order shipped to customer
- DELIVERED - Order delivered
- CANCELLED - Order cancelled
- REFUNDED - Order refunded

### Payment Status
- PENDING - Awaiting payment
- PAID - Payment received
- FAILED - Payment failed
- REFUNDED - Payment refunded
- PARTIALLY_REFUNDED - Partial refund

### Components

1. **Order Items**: Products in the order
   - Item ID, Product ID, Name, SKU
   - Quantity, Price, Discount, Line Total

2. **Shipping Address**: Delivery address
   - Street, City, State, Postal Code, Country

3. **Billing Address**: Billing address
   - Street, City, State, Postal Code, Country

### Order Totals

- **Subtotal**: Sum of item prices
- **Tax Amount**: Calculated tax (8%)
- **Shipping Cost**: Shipping fee
- **Discount Amount**: Applied discounts
- **Total Amount**: Final total

### Operations

1. **Order Creation**
   - Create new order
   - Add items to order

2. **Address Management**
   - Set shipping address
   - Set billing address

3. **Payment Processing**
   - Set shipping method
   - Apply discounts
   - Process payment

4. **Fulfillment**
   - Ship order (with tracking)
   - Mark as delivered

5. **Order Management**
   - Cancel order
   - Add notes

## Design Patterns

1. **Aggregate Pattern**: Order as root aggregate
2. **Embedded Documents**: Order items, addresses
3. **Value Objects**: Order status, payment status

## Running Tests

```bash
./gradlew :m21-ecommerce-capstone:test
```
