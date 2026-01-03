PRD: Spare Parts Search & Ordering (MVP, API-first)
1. Objective

Implement an MVP “Spare Parts” capability in the CMS (CMS bilar) that allows a workshop user to:

Search spare parts for a specific vehicle using vehicle identity data (reg no / VIN, make, model, year, engine if available).

View search results with prices and availability.

Select parts to attach to a Work Order as “planned parts”.

Optionally place an order with a supplier (if supplier supports ordering).

Persist an immutable “purchase snapshot” for traceability (supplier, part identifiers, price, markup, availability at time of order).

Key constraint: Do not build a full internal parts catalog now. Only persist minimal snapshots needed for work orders, audit, and later scaling.

2. Scope
In scope (MVP)

Backend: supplier-agnostic “PartsService” with adapter-based supplier integrations

Search parts by:

free text query (“brake pad”, “oil filter”, part number)

optional category filter (Brakes, Filters, Suspension, etc.)

Results normalized into one internal DTO format

Attach part to a work order as a “WorkOrderPartLine”

Store a “PartSnapshot” when user adds or orders a part

Simple caching (short TTL) for search results

Feature flags per customer: enabled suppliers, ordering enabled/disabled, markups enabled/disabled

Out of scope (for MVP)

Full internal part catalog with regular sync

Advanced vehicle fitment engine

Supplier contract management UI

Automated supplier failover ordering (search failover is in; order failover later)

3. Users & Roles

Admin: configure which suppliers are enabled for a Customer, set defaults (markup rules)

Mechanic / Workshop user: search parts, attach parts to work orders, (optionally) place orders

Superadmin: system-wide supplier configuration / keys

Access rule: users can only access parts data for their own Customer.

4. Non-functional requirements

Multi-tenant: isolate supplier settings and markups per Customer

Auditability: store what was displayed/used at decision time

Performance: search should respond < 2s for cached; < 5s uncached (best-effort)

Robustness: supplier API failures should degrade gracefully

Testability: unit tests for adapters + service logic; integration tests with mocks; optional end-to-end later

5. Supplier strategy

Start with one supplier integration. The design must support:

N suppliers per Customer (3–4 typical)

Search fallback: if Supplier A returns 0 or errors, try Supplier B, etc.

Per-customer supplier priority list

Ordering:

MVP: ordering only for the first supplier if supported; otherwise “request quote” or “manual order” is represented as a saved line item without placing a supplier order.

6. Data model (MVP)

Use these entities (names can be adjusted to your style):

CustomerSupplierConfig

id

customerId

supplierCode (e.g., “SUPPLIER_X”)

priority (int)

enabled (bool)

orderingEnabled (bool)

credentialsRef (pointer to secrets store or encrypted blob ref)

createdAt, updatedAt

WorkOrderPartLine

id

workOrderId

supplierCode

supplierPartId (string)

oemNumber (string, nullable)

partName (string)

quantity (int)

unitPriceExVat (decimal)

vatRate (decimal)

markupType (enum: NONE, PERCENT, FIXED) [optional in MVP]

markupValue (decimal) [optional in MVP]

finalUnitPriceExVat (decimal)

currency (string)

availabilityStatus (enum or string)

deliveryEstimateDays (int, nullable)

snapshotJson (text/jsonb) // immutable supplier response snapshot (sanitized)

status (enum: PLANNED, ORDERED, CANCELLED)

supplierOrderReference (string, nullable)

createdByUserId

createdAt, updatedAt

PartsSearchCache (optional: in-memory first)

key: hash(customerId + vehicleKey + query + category)

storedAt

ttlSeconds

resultsJson

7. API (Backend endpoints)

All endpoints require authentication and use the logged-in user’s Customer context.

GET /api/work-orders/{workOrderId}/parts/search

Query params:

q (string, required)

category (string, optional)

limit (int, optional default 20)

Response: list of PartSearchResultDto

POST /api/work-orders/{workOrderId}/parts/lines

Body: AddPartLineRequest

supplierCode

supplierPartId

quantity

selectedOffer (if supplier gives multiple offers) [optional]

manualMarkup (optional)

Behavior:

Fetch details for the selected part (if needed)

Compute final pricing (markup rules)

Persist WorkOrderPartLine with snapshotJson

Return created line

POST /api/work-orders/{workOrderId}/parts/lines/{lineId}/order

Behavior:

If orderingEnabled for supplier/customer: place supplier order

Save supplierOrderReference + update status=ORDERED

Store order response snapshot

GET /api/work-orders/{workOrderId}/parts/lines

List part lines for the work order

DELETE /api/work-orders/{workOrderId}/parts/lines/{lineId}

Soft delete or set status=CANCELLED (prefer soft cancel for audit)

8. Internal service contracts

Define supplier-agnostic interfaces:

PartsService

searchParts(customerId, vehicleContext, query, category, limit) -> List<PartOffer>

getPartDetails(customerId, supplierCode, supplierPartId, vehicleContext) -> PartDetails

addWorkOrderPartLine(workOrderId, request, userContext) -> WorkOrderPartLine

placeOrder(workOrderId, lineId, userContext) -> OrderResult

SupplierAdapter (interface)

SupplierCode getCode()

List<PartOffer> searchParts(SupplierContext ctx, VehicleContext v, PartSearchQuery q)

PartDetails getPartDetails(SupplierContext ctx, VehicleContext v, String supplierPartId)

OrderResult placeOrder(SupplierContext ctx, OrderRequest req) (optional; default throws UnsupportedOperation)

VehicleContext

regNo

vin

make

model

year

engineCode / engineDisplacement (optional)

any supplier-required mapping identifiers (optional)

9. Pricing & markup (MVP-simple)

MVP: markup optional, but structure must allow it

Rules (initial):

per Customer: default markup percent

per Supplier: override markup percent

manual override at time of adding line (stored on line for traceability)

Pricing calculation stores:

base unit price (from supplier)

markup applied

final unit price

currency, VAT rate if provided

10. Error handling

Supplier API timeout: 3–5 seconds per supplier, configurable

Search fallback: if supplier errors, try next supplier; record errors in logs

If all suppliers fail: return 502 with safe message + correlation id

If supplier returns “no fitment”: show empty results (not error)

11. Acceptance criteria

A user can search for parts for a given Work Order’s vehicle

Results show at least: supplier, part name, price, availability, supplierPartId, OEM if available

User can add a part to work order and the system stores a snapshot of supplier data

If ordering enabled, user can place an order and reference is persisted

Supplier failures do not crash the system; fallback works

Unit tests cover:

PartsService orchestration logic

Adapter normalization

Markup calculation

Snapshot persistence

Integration tests with mocked supplier endpoints

Step-by-step Integration Plan (for Claude Code)
Step 0: Establish codebase conventions

Identify existing packages and naming conventions.

Create a new module/package, e.g.:

com.meltacars.parts (or similar)

api (controllers, DTO)

domain (entities)

service (PartsService)

supplier (adapters)

pricing (markup)

persistence (repositories)

Deliverable: folder structure + placeholder classes compiled.

Step 1: Define DTOs and internal models

Implement:

VehicleContext

PartSearchQuery

PartOffer / PartSearchResultDto

PartDetails

OrderRequest, OrderResult

AddPartLineRequest

Deliverable: all DTO classes + mappers skeleton.

Step 2: Implement persistence entities + repositories

Implement JPA entities:

CustomerSupplierConfig

WorkOrderPartLine

Repositories:

CustomerSupplierConfigRepository

WorkOrderPartLineRepository

Deliverable: migrations (Flyway/Liquibase) + repositories + basic tests.

Step 3: Implement SupplierAdapter interface and adapter registry

Create SupplierAdapter interface.

Create SupplierAdapterRegistry that auto-wires a List<SupplierAdapter> and maps by code.

Add feature: detect “ordering supported”.

Deliverable: registry + unit tests.

Step 4: Implement PartsService (core orchestration)

Implement:

Load enabled suppliers for customer sorted by priority

Search logic:

loop suppliers in priority order

call adapter.searchParts

normalize results to internal format

aggregate results (MVP: either first non-empty supplier OR merge; choose one approach)

caching (in-memory map with TTL is fine for MVP)

Add part line logic:

optionally call getPartDetails to enrich

compute pricing + markup

persist WorkOrderPartLine with snapshotJson (supplier response)

Place order logic:

verify orderingEnabled

call adapter.placeOrder

persist reference + snapshot

Deliverable: PartsService + unit tests with mocked adapters.

Step 5: Add REST controllers

Controllers:

PartsController under /api/work-orders/{workOrderId}/parts

Endpoints:

search

add line

list lines

order line

cancel line

Deliverable: controller tests (MockMvc) and response validation.

Step 6: Implement first supplier adapter

Pick the supplier you have best access to right now (API key, docs). Implement:

search endpoint mapping to PartOffer

details endpoint mapping

order endpoint mapping (if available)

If ordering not available: implement search + details only; order throws UnsupportedOperation.

Deliverable: adapter + contract tests using WireMock (mock supplier API).

Step 7: Security & tenant boundaries

Ensure CustomerId is always derived from auth context, not passed from client

Verify WorkOrder belongs to customer

Log correlation IDs for supplier calls

Deliverable: security checks + tests for cross-tenant access denial.

Step 8: Caching

MVP caching:

Key: customerId + vehicleKey + q + category + limit

TTL: 15–60 minutes configurable

Store as JSON string or object

Deliverable: cache wrapper + tests.

Step 9: Observability

Structured logs per supplier call: duration, supplierCode, status, error type

Metrics hooks if you have Micrometer:

supplier.api.calls

supplier.api.failures

parts.search.cache.hit

Deliverable: logging + optional metrics.

Step 10: QA checklist and done definition

End-to-end manual test:

Create work order with vehicle

Search “oil filter”

Add part

View lines

Order part (if enabled)

Run unit + integration tests

Verify DB snapshots do not store secrets

Deliverable: documented QA steps + test pass.

Implementation Notes / Decisions Claude Code must make (explicit)

Claude Code should decide and document:

Aggregation strategy:

Option A (simplest): return first non-empty supplier’s results

Option B: merge results from multiple suppliers (requires dedupe logic)
MVP recommendation: Option A

Snapshot format:

Store sanitized supplier response JSON (remove secrets)

Timeouts + retries:

One retry max on transient failures; strict timeout per supplier call

Ordering:

Only for suppliers that support it; otherwise expose “manual ordering” workflow via PLANNED lines