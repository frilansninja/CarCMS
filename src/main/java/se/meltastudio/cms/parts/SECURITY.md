# Spare Parts Module - Security Implementation

## Multi-Tenant Isolation

### Company-Level Access Control

All spare parts operations are strictly isolated by Company:

1. **PartsService Layer**
   - `searchParts()`: Loads suppliers only for the user's company
   - `getPartDetails()`: Verifies supplier is enabled for user's company
   - `addPartLine()`: Verifies work order belongs to user's company via vehicle → workplace → company
   - `placeOrder()`: Verifies part line's work order belongs to user's company
   - `getWorkOrderPartLines()`: Verifies work order belongs to user's company
   - `cancelPartLine()`: Verifies part line's work order belongs to user's company

2. **PartsController Layer**
   - All endpoints require authentication (`@AuthenticationPrincipal`)
   - `getWorkOrderWithAuth()` helper verifies work order access before any operation
   - User company is extracted from authenticated user, never from request parameters

### Database-Level Security

1. **CustomerSupplierConfig**
   - Always filtered by `company` in repository queries
   - Only enabled suppliers are returned: `findEnabledSuppliersByCompanyOrderByPriority()`

2. **WorkOrderPartLine**
   - Linked to WorkOrder → Vehicle → Workplace → Company chain
   - Access verified through this relationship
   - No direct company_id column needed (follows existing pattern)

### Authentication

- Spring Security integration via `@AuthenticationPrincipal`
- User resolved from SecurityContext
- Username looked up in UserRepository to get full User entity with Company

### Authorization Checks

**Pattern used consistently:**
```java
if (!entity.getCompany().getId().equals(user.getCompany().getId())) {
    throw new PartsServiceException("Access denied");
}
```

Applied at:
- Work order access (via vehicle → workplace → company)
- Supplier configuration access
- Part line access

### Audit Trail

1. **WorkOrderPartLine.createdBy**
   - Tracks which user added the part
   - Foreign key to User table

2. **Immutable Snapshots**
   - `snapshotJson` field stores complete supplier response
   - Enables audit of what data was used for decisions
   - Stored at part line creation time

3. **Timestamps**
   - `createdAt` and `updatedAt` on all entities
   - Automatically managed via JPA lifecycle hooks

### Sensitive Data Handling

1. **Supplier Credentials**
   - Stored in `credentialsRef` field (not directly in database)
   - TODO: Implement external secrets vault integration
   - Never included in API responses
   - Never included in snapshot JSON

2. **Pricing Data**
   - Base price and final price stored separately
   - Markup calculation auditable
   - No customer pricing data shared between companies

### Cross-Tenant Attack Prevention

**Prevented Scenarios:**
1. ❌ User from Company A cannot search parts for Company B's work orders
2. ❌ User cannot add parts to work orders in other companies
3. ❌ User cannot view part lines from other companies' work orders
4. ❌ User cannot place orders using other companies' supplier configs
5. ❌ Supplier configurations are company-specific

**Attack Vector Mitigations:**
- Parameter tampering: Work order ID verified to belong to user's company
- Privilege escalation: All operations check company ownership
- Data leakage: Repository queries filter by company
- IDOR (Insecure Direct Object Reference): Entity access verified via relationship chain

## Security Checklist

- [x] Authentication required on all endpoints
- [x] Company isolation on all database queries
- [x] Work order ownership verified before operations
- [x] Supplier configs are company-specific
- [x] Audit trail via createdBy and timestamps
- [x] Immutable snapshots for compliance
- [x] No cross-tenant data leakage
- [x] Credentials not exposed in responses
- [ ] TODO: External secrets management for supplier credentials
- [ ] TODO: Rate limiting per company
- [ ] TODO: Audit logging for sensitive operations
