PRD – CMS för bilverkstäder (v0.1 – Draft)
1. Översikt
1.1 Syfte

Syftet med systemet är att tillhandahålla ett webbaserat CMS för bilverkstäder som stödjer hela arbetsflödet från kundhantering till arbetsorder, bokning, fakturering och uppföljning.

Systemet ska vara multi-tenant, där varje kund (bilverkstad eller kedja) arbetar isolerat i samma system.

1.2 Målgrupp

Fristående bilverkstäder

Mindre till medelstora verkstadskedjor

Administrativ personal

Mekaniker

Verkstadsansvariga

1.3 Produktvision

Ett modernt, modulärt och tekniskt robust verkstadssystem som:

Är enkelt att använda i vardagen

Är tillräckligt flexibelt för verkliga verkstadsflöden

Kan byggas ut stegvis utan omstrukturering

2. Mål och icke-mål
2.1 Affärsmål

Möjliggöra effektiv hantering av flera verkstäder i samma system

Minska administrativ overhead för verkstadspersonal

Skapa en stabil grund för framtida tilläggstjänster

2.2 Tekniska mål

Backend-first design

Tydlig domänmodell

Konsekvent API-struktur

Lätt att driftsätta (Docker-baserat)

2.3 Icke-mål (v1)

SMS-notifieringar

Avancerad BI / statistik

Multispråkstöd

Integration mot bokföringssystem

3. Användarroller
Roll	Beskrivning
SUPER_ADMIN	Systemadministratör (plattform)
CUSTOMER_ADMIN	Administratör för kund (företag)
WORKPLACE_ADMIN	Ansvarig för verkstad
MECHANIC	Mekaniker
OFFICE	Administrativ personal

Behörigheter begränsas alltid till kundens egen data.

4. Systemöversikt
4.1 Arkitektur

Backend: Java, Spring Boot

Frontend: React

Databas: MariaDB

Auth: Token-baserad autentisering (JWT)

Deployment: Docker, Hetzner

4.2 Multi-tenant-modell

Alla entiteter kopplas till Customer

Dataseparation sker logiskt via foreign keys

Ingen fysisk databasseparation i v1

5. Funktionella krav (sammanfattning)
5.1 Customer (Company)

Skapa, uppdatera och inaktivera kunder

Kund kan ha flera Workplaces

Kundspecifika inställningar:

E-postavsändare

Notifieringar

Påminnelseavgifter

Räntelogik

5.2 Workplace

Tillhör exakt en Customer

Hanterar:

Arbetsorder

Bokningar

Mekaniker

Möjlighet att filtrera data per workplace

5.3 Users & Roles

Användare tillhör en Customer

Stöd för flera roller per användare

Roller styr funktionalitet och vyer

5.4 End Customers

Slutkunder till verkstaden

Kontaktuppgifter och fakturaadress

Kan äga flera fordon

Kan arkiveras (ej raderas)

5.5 Vehicles

Kopplas till End Customer

Fordonsmodell hanteras separat (VehicleModel)

Historik över arbetsorder

5.6 Work Orders

Kopplas till Vehicle, Workplace och Mechanic

Statusmaskin (fördefinierade övergångar)

Ett work type per order

Underlag för fakturering

5.7 Calendar / Booking

Veckovy som primär vy

Drag-and-drop-bokningar

Kollisioner ska varnas men inte blockeras

Oschemalagda arbetsorder listas separat

5.8 Invoicing

Faktura kan endast skapas i vissa statusar

Stöd för:

Delbetalningar

Kreditfakturor

Påminnelseavgift

Ränta

6. Icke-funktionella krav
6.1 Säkerhet

BCrypt för lösenord

JWT för autentisering

Rollbaserad auktorisering

All data isolerad per Customer

6.2 GDPR

Loggning av manuella ändringar

Automatisk rensning efter 365 dagar (konfigurerbart)

Exportfunktion för historik

6.3 Prestanda

Paginering på alla listor

Backend-stödd filtrering och sortering

7. Antaganden och begränsningar

Systemet används primärt på desktop

Offline-stöd ingår ej

Endast webbgränssnitt i v1

8. Framtida utökningar

SMS

Bokföringsintegration

Avancerad rapportering

API för tredjepartsappar

Mobilanpassad vy


Test- och kvalitetskrav
1) Kvalitetsmål

Systemet ska ha en teststrategi som möjliggör att varje iteration kan verifieras med hög automatiseringsgrad.

Målet är att:

Minimera regressionsfel när nya moduler tillkommer.

Säkerställa att grundflöden alltid fungerar (navigation, CRUD, auth, statusflöden).

Tidigt fånga upp:

trasiga knappar/länkar

felaktiga API-anrop

500/4xx-fel som bryter UI

fel som bara uppstår i integrerade flöden (frontend ↔ backend)

2) Testtyper och krav
2.1 Enhetstester (Unit tests)

Krav:

Affärslogik ska enhetstestas (t.ex. statusmaskin för work orders, beräkningslogik för ränta/påminnelseavgift, behörighetslogik).

Enhetstester ska vara snabba och köras vid varje build.

Rekommenderad stack:

JUnit 5

Mockito

AssertJ (valfritt men bra för läsbarhet)

2.2 Integrationstester (Backend)

Krav:

API-kontrakt och databasintegration testas automatiskt.

Multi-tenant-isolering testas: en användare i Customer A ska inte kunna läsa/ändra data i Customer B.

Standardflöden ska testas för de viktigaste endpoints.

Rekommenderad stack:

Spring Boot Test

Testcontainers (MariaDB)

MockMvc eller WebTestClient

Praktisk princip: “alla controllers” behöver inte total täckning, men alla kritiska flöden ska ha minst ett integrationstest.

2.3 Frontend-enhetstester / komponenttester

Krav:

Kritiska UI-komponenter testas: listor, formulär, validering, felhantering.

API-anrop mockas och UI valideras.

Rekommenderad stack:

Vitest eller Jest

React Testing Library

MSW (Mock Service Worker) för API-mock

2.4 End-to-end (E2E) med Selenium

Krav:

E2E-svit ska kunna köras automatiskt (CI) och lokalt.

E2E ska verifiera att:

navigation fungerar (meny/länkar)

centrala användarflöden fungerar

inga oväntade 500/JS errors inträffar vid standardklick

UI får korrekt feedback vid fel (t.ex. 401 → redirect till login)

Viktigt praktiskt avgränsningskrav:

Selenium ska inte testa alla permutationer. Den ska testa “happy path + några kritiska felvägar” och fungera som regressionsskydd.

Rekommenderad struktur för E2E-svit (för att minimera underhåll):

Ett litet antal “smoke tests” som körs ofta.

En större E2E-svit som körs på merge/nightly.

Definition of Done (DoD) – testkrav per iteration

För varje iteration/feature anses arbetet färdigt först när:

Unit tests är uppdaterade och gröna.

Minst ett integrationstest finns för relevant backend-flöde (om ändringen påverkar API eller DB).

Minst ett E2E-test täcker det nya eller ändrade användarflödet om det är ett centralt UI-flöde.

CI kör igenom:

backend unit + integration

frontend unit/component tests

E2E smoke suite

Det finns inga nya fel i loggar som tyder på:

500 errors

authentication/authorization-brott

okontrollerade exceptions

Konkreta E2E “Smoke tests” som ger maximal nytta

Det här är de tester som oftast ger bäst ROI för din typ av system:

Login → Dashboard laddar

Verifiera att access token sätts och att användaren kommer in.

Navigation sanity

Klicka igenom huvudmenyn (Users, End Customers, Vehicles, Work Orders, Calendar).

Verifiera att varje sida laddar utan JS errors och utan 500.

CRUD mini-flöde (t.ex. End Customer)

Skapa → sök → öppna → uppdatera → (arkivera) → verifiera listan.

Work Order: skapa + statusbyte

Skapa work order, byt status enligt statusmaskinen och verifiera att UI uppdateras.

Booking: dra work order till kalender

Skapa eller välj oschemalagd order, dra till kalendern.

Verifiera att collision-warning fungerar (om relevant).

---

## Password Reset Feature (Implementerad)

### Översikt
Systemet har en komplett password reset-funktionalitet som låter användare återställa sina lösenord via en säker token-baserad mekanism.

### Användning
- **Username är email**: Användare registreras med email som username (t.ex. `admin@melta-studios.se`)
- **Self-service**: Användare kan själva begära lösenordsåterställning
- **Manuell distribution**: Systemet genererar en återställningslänk som visas i gränssnittet (ingen automatisk e-post)

### Teknisk Implementation

#### Backend (Spring Boot)

**Databas:**
- Tabell: `password_reset_tokens`
- Fält: id, user_id, token_hash (BCrypt), created_at, expires_at, used
- Tokens hashas med BCrypt innan lagring (säkerhet)
- 1 timmes giltighet
- Single-use enforcement (används-flagga)

**Entiteter och Services:**
- `PasswordResetToken` - JPA entity
- `PasswordResetTokenRepository` - Dataåtkomst
- `PasswordResetService` - Affärslogik med säkerhetsfunktioner
- DTOs: `PasswordResetRequest`, `PasswordResetResponse`, `NewPasswordRequest`

**API Endpoints:**
```
POST /api/auth/forgot-password
Request: { "username": "email@example.com" }
Response: { "resetLink": "http://...", "message": "..." }

POST /api/auth/reset-password
Request: { "token": "uuid", "newPassword": "..." }
Response: { "message": "Password has been reset successfully" }
```

**Säkerhetsfunktioner:**
- Tokens hashas med BCrypt (samma som lösenord)
- UUID v4 tokens (kryptografiskt säkra)
- Gamla tokens invalideras vid ny begäran
- Generiska felmeddelanden (förhindrar user enumeration)
- Lösenordvalidering (min 8 tecken)
- Single-use tokens
- Automatisk radering vid användning

#### Frontend (React)

**Sidor:**
- `/forgot-password` - ForgotPasswordPage.jsx
  - Email-formulär
  - Visar genererad återställningslänk
  - Kopiera till urklipp-funktion
  - Material-UI design

- `/reset-password/:token` - ResetPasswordPage.jsx
  - Nytt lösenord + bekräftelse
  - Client-side validering
  - Auto-redirect till login vid framgång
  - Inline felmeddelanden

**API Integration:**
- `auth.js` - API-funktioner för password reset
- Axios-baserade anrop till backend

### Säkerhet

**Token Security:**
- BCrypt-hashade tokens i databas
- 1 timmes expiration
- Single-use enforcement
- Cascade delete vid user deletion

**Attack Mitigation:**
- User enumeration prevention (generiska felmeddelanden)
- Brute force protection (implicit via BCrypt)
- Token invalidation vid nya requests
- Ingen token persistence i browser (endast i URL)

**Multi-tenant Isolation:**
- Tokens kopplade till User via foreign key
- User kopplad till Company
- Ingen ytterligare company_id behövs

### Testing

**Selenium Test Suite:**
`src/test/java/se/meltastudio/cms/selenium/PasswordResetTest.java`

**6 automatiserade tester:**
1. Forgot password page loads
2. Generate reset link
3. Complete password reset flow (end-to-end)
4. Password mismatch validation
5. Password length validation
6. Restore original password

**Test Coverage:**
- Full end-to-end flow i riktig webbläsare
- Validering av UI-komponenter
- API-integration
- Felhantering
- Säkerhetsaspekter (token reuse prevention)

### Användningsexempel

1. **Användare glömmer lösenord:**
   - Klickar "Glömt lösenord?" på login-sidan
   - Anger sin email (`admin@melta-studios.se`)
   - System genererar återställningslänk
   - Admin kopierar och skickar länken manuellt till användaren

2. **Användare återställer lösenord:**
   - Öppnar återställningslänken
   - Anger nytt lösenord (min 8 tecken)
   - Bekräftar lösenord
   - Redirectas till login
   - Loggar in med nytt lösenord

### Framtida Förbättringar

**Möjliga utökningar:**
- Automatisk e-postsändning (SMTP integration)
- Rate limiting (max 3 requests/timme)
- Password strength meter
- 2FA integration
- Audit logging av reset-försök
- IP-tracking för säkerhet
- Notifieringsemail när lösenord ändras

### Test Credentials

**Default admin user:**
- Username: `admin@melta-studios.se`
- Password: `admin123`
- Role: `SUPER_ADMIN`
- Company: Test Bilverkstad AB