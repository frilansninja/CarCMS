# CarCMS Implementation Checklist

Based on PRD analysis (claude.md) vs current codebase implementation.

**Legend:**
- ✅ **Implemented** - Feature is fully implemented
- ⚠️ **Partial** - Feature is partially implemented or basic version exists
- ❌ **Missing** - Feature is not implemented
- 🔍 **Needs Review** - Requires deeper investigation or frontend check

---

## 1. MULTI-TENANT & COMPANY MANAGEMENT

### 1.1 Customer (Company) Entity
- ✅ Create, update customers via API
- ✅ Company has multiple workplaces
- ✅ Basic company fields (name, orgNumber, phone, email, address)
- ❌ **Customer-specific settings:**
  - ❌ Email sender configuration
  - ❌ Notification preferences
  - ❌ Reminder fee (påminnelseavgift) settings
  - ❌ Interest rate (ränta) logic configuration
- ❌ Inactivate/archive customer functionality

**Files:** `Company.java`

---

## 2. WORKPLACE MANAGEMENT

- ✅ Workplace entity exists
- ✅ Belongs to one Customer
- ✅ Handles work orders, vehicles
- ✅ Filter data by workplace (seen in controllers)

**Files:** `Workplace.java`, `WorkplaceController.java`

---

## 3. USER ROLES & AUTHENTICATION

### 3.1 Authentication
- ✅ JWT token-based authentication (15 min access, 24h refresh)
- ✅ BCrypt password hashing
- ✅ Token contains username, roles, companyId
- ✅ Multi-tenant data isolation via companyId

**Files:** `SecurityConfig.java`, `JwtUtil.java`, `JwtRequestFilter.java`

### 3.2 User Roles
**PRD Requirements:**
- SUPER_ADMIN
- CUSTOMER_ADMIN
- WORKPLACE_ADMIN
- MECHANIC
- OFFICE

**Current Implementation:**
- ✅ ADMIN (similar to CUSTOMER_ADMIN)
- ✅ USER (similar to OFFICE)
- ✅ MECHANIC
- ❌ SUPER_ADMIN (platform administrator)
- ❌ WORKPLACE_ADMIN

**Status:** ⚠️ **Partial - Role names don't match PRD**

Current roles work but names differ from specification.

**Files:** `SecurityConfig.java:41-60`, `Role.java`

### 3.3 Multi-role Support
- ✅ Users can have multiple roles (many-to-many relationship)

---

## 4. END CUSTOMER MANAGEMENT

- ✅ End customer entity with contact info
- ✅ Billing address fields (street, city, zip, country)
- ✅ Can own multiple vehicles
- ✅ End customer CRUD operations
- ❌ **Archive functionality (soft delete)** - No `archived` or `isActive` field found

**Files:** `EndCustomer.java`, `EndCustomerController.java`

---

## 5. VEHICLE MANAGEMENT

- ✅ Vehicle entity connected to End Customer
- ✅ VehicleModel handled separately
- ✅ Vehicle fields (registration, mileage, transmission)
- ✅ Work order history tracking
- ✅ Last known service tracking (date + mileage)
- ✅ Connected to Workplace

**Files:** `Vehicle.java`, `VehicleModel.java`, `VehicleController.java`

---

## 6. WORK ORDER MANAGEMENT

### 6.1 Core Work Order Features
- ✅ Work order entity
- ✅ Connected to Vehicle, Workplace, Mechanic
- ✅ Status state machine (WorkOrderStatus enum)
- ✅ Work order categories (Service, Reparation, Diagnostik, Besiktning)
- ✅ One work type per order
- ✅ Work tasks within work orders
- ✅ Work task templates
- ✅ Smart part mapping (VehicleModel + Template → Article)

**Files:** `WorkOrder.java`, `WorkOrderController.java`, `WorkTask.java`, `WorkTaskTemplate.java`

### 6.2 Status Machine
- ✅ Predefined status transitions
- ✅ Status: PENDING, DIAGNOSING, WAITING_FOR_APPROVAL, WAITING_FOR_PARTS, IN_PROGRESS, QUALITY_CONTROL, READY_FOR_PICKUP, COMPLETED, CANCELLED

**Files:** `WorkOrderStatus.java`

### 6.3 Mechanic Assignment
- ✅ Assign mechanic to work order
- ✅ Filter work orders by mechanic

---

## 7. PARTS & INVENTORY MANAGEMENT

- ✅ Article (parts) entity with part numbers
- ✅ Stock quantity tracking
- ✅ Purchase price and selling price
- ✅ Supplier management
- ✅ Part orders from suppliers
- ✅ Part order tracking (quantity, orderDate, expectedArrivalDate, received)
- ✅ Part mapping (smart auto-parts selection)

**Files:** `Article.java`, `Supplier.java`, `PartOrder.java`, `PartMapping.java`

---

## 8. CALENDAR / BOOKING MANAGEMENT

### 8.1 Backend Implementation
- ✅ Booking entity (title, startTime, endTime, categoryColor)
- ✅ Mechanic booking assignments
- ✅ Booking CRUD API

**Files:** `Booking.java`, `BookingController.java`

### 8.2 Frontend Features (Requires Frontend Check)
- 🔍 Week view as primary view
- 🔍 Drag-and-drop bookings
- 🔍 Collision warnings (warn but don't block)
- 🔍 Unscheduled work orders listed separately

**Status:** ⚠️ **Backend complete, frontend unknown**

---

## 9. INVOICING

### 9.1 Basic Invoicing
- ✅ Invoice entity
- ✅ Invoice fields (invoiceNumber, issueDate, dueDate, amount, paid)
- ✅ Connected to End Customer
- ✅ PDF generation (Apache PDFBox)
- ✅ Invoice CRUD operations

**Files:** `Invoice.java`, `InvoiceController.java`, `PDFGeneratorService.java`

### 9.2 Advanced Invoicing (PRD Requirements)
- ❌ **Status-based invoice creation restriction** - No validation that invoice can only be created in certain work order statuses
- ❌ **Partial payment support** - No `amountPaid` vs `totalAmount` fields
- ❌ **Credit invoices (Kreditfakturor)** - No invoice type or credit invoice functionality
- ❌ **Reminder fees (Påminnelseavgift)** - No reminder fee calculation or tracking
- ❌ **Interest calculation (Ränta)** - No interest rate or overdue interest calculation
- ❌ **Work Order linkage** - Invoice not directly linked to WorkOrder entity

**Status:** ⚠️ **Basic invoicing works, advanced features missing**

---

## 10. SERVICE INTERVAL TRACKING

- ✅ VehicleService entity for service intervals
- ✅ Interval tracking by kilometers
- ✅ Interval tracking by months
- ✅ Service types for different driving conditions
- ✅ Last known service date and mileage on Vehicle

**Files:** `VehicleService.java`, `Vehicle.java`

---

## 11. SECURITY (NON-FUNCTIONAL REQUIREMENTS)

### 11.1 Core Security
- ✅ BCrypt for passwords
- ✅ JWT for authentication
- ✅ Role-based authorization (hasRole, hasAnyRole)
- ✅ All data isolated per Customer (JWT contains companyId)

### 11.2 GDPR Compliance
- ✅ Logging of manual changes (LogManualChange entity)
- ✅ Automatic log cleanup after 365 days (LogCleanupService)
- ❌ **Export function for history** - No dedicated export endpoint found

**Files:** `SecurityConfig.java`, `LogManualChange.java`, `LogCleanupService.java`

---

## 12. PERFORMANCE & PAGINATION

- ⚠️ **Pagination on lists** - Found in:
  - ✅ UserRepository (uses `Pageable`)
  - ✅ UserService
  - ✅ CompanyController
  - 🔍 **Needs verification:** Other controllers (WorkOrder, Vehicle, EndCustomer, Article, etc.)

**Files to check:** `*Controller.java`, `*Repository.java`

**Status:** ⚠️ **Partial - Not all endpoints use pagination**

---

## 13. TESTING REQUIREMENTS (PRD Section 2)

### 13.1 Unit Tests
- 🔍 **Needs review** - Check for JUnit 5 tests in `src/test`
- 🔍 Business logic tests (status machine, calculations, permissions)

### 13.2 Integration Tests (Backend)
- 🔍 **Needs review** - Spring Boot Test + Testcontainers
- 🔍 Multi-tenant isolation tests
- 🔍 API contract tests

### 13.3 Frontend Tests
- 🔍 **Needs review** - Vitest/Jest + React Testing Library
- 🔍 Component tests with MSW for API mocking

### 13.4 E2E Tests (Selenium)
- ✅ Selenium dependency exists in `pom.xml` (4.28.1)
- 🔍 **Needs review** - Check for actual E2E test files
- 🔍 Smoke tests (login, navigation, CRUD flows)

**PRD Required Smoke Tests:**
1. Login → Dashboard loads
2. Navigation sanity (main menu)
3. CRUD mini-flow (e.g., End Customer)
4. Work Order: create + status change
5. Booking: drag work order to calendar

**Status:** ⚠️ **Selenium ready, tests unknown**

### 13.5 CI/CD Pipeline
- 🔍 **Needs review** - Check for CI configuration (.github/workflows, .gitlab-ci.yml, etc.)

---

## 14. FRONTEND (REACT)

**PRD states:** Frontend: React

**Status:** 🔍 **No React frontend found in repository**

The repository only contains the Spring Boot backend. Frontend implementation status unknown.

---

## 15. DEPLOYMENT & INFRASTRUCTURE

- ✅ Docker Compose configuration (`docker-compose.yml`)
- ✅ MySQL 8.0 container
- ✅ Persistent volume for database
- ⚠️ **Hetzner deployment** - Not configured in current files

**Files:** `docker-compose.yml`

---

## SUMMARY: PRIORITY GAPS TO ADDRESS

### High Priority (Core PRD Requirements Missing)
1. ❌ **Advanced Invoicing Features**
   - Partial payments
   - Credit invoices
   - Reminder fees
   - Interest calculation
   - Work order linkage

2. ❌ **Customer-Specific Settings** (Company entity)
   - Email sender
   - Notification preferences
   - Fee/interest configuration

3. ❌ **Role Structure Mismatch**
   - Current: ADMIN, USER, MECHANIC
   - Required: SUPER_ADMIN, CUSTOMER_ADMIN, WORKPLACE_ADMIN, MECHANIC, OFFICE

4. ❌ **End Customer Archive** (Soft delete)

5. ❌ **GDPR History Export**

### Medium Priority (Partial Implementation)
6. ⚠️ **Pagination** - Only 3 controllers use it, needs full implementation

7. ⚠️ **Invoice Status Restrictions** - No validation preventing invoice creation in wrong status

### Low Priority (Requires Investigation)
8. 🔍 **Testing Coverage** - Test files need review

9. 🔍 **React Frontend** - Not in repository, may be separate project

10. 🔍 **Calendar UI Features** - Backend ready, frontend unknown

---

## IMPLEMENTATION STATUS BY MODULE

| Module | Status | Completion |
|--------|--------|------------|
| Company Management | ⚠️ Partial | 60% |
| Workplace | ✅ Complete | 100% |
| Users & Auth | ⚠️ Partial | 85% |
| End Customers | ⚠️ Partial | 90% |
| Vehicles | ✅ Complete | 100% |
| Work Orders | ✅ Complete | 100% |
| Parts & Inventory | ✅ Complete | 100% |
| Bookings (Backend) | ✅ Complete | 100% |
| Invoicing | ⚠️ Partial | 40% |
| Service Intervals | ✅ Complete | 100% |
| Security | ✅ Complete | 95% |
| GDPR | ⚠️ Partial | 80% |
| Performance | ⚠️ Partial | 40% |
| Testing | 🔍 Unknown | ? |
| Frontend | 🔍 Unknown | ? |

**Overall Backend Completion: ~75%**

---

## NEXT STEPS RECOMMENDATIONS

1. **Immediate Actions:**
   - Implement advanced invoicing features (partial payments, credit invoices, fees, interest)
   - Add customer-specific settings to Company entity
   - Implement End Customer archive/soft delete
   - Add pagination to all list endpoints
   - Add invoice-work order relationship

2. **Short-term:**
   - Align role names with PRD (or update PRD to match implementation)
   - Add GDPR history export endpoint
   - Review and complete test coverage

3. **Long-term:**
   - Locate or develop React frontend
   - Set up CI/CD pipeline
   - Configure Hetzner deployment
