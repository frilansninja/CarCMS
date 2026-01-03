-- Create test user for login
-- Username: admin@melta-studios.se
-- Password: admin (BCrypt hashed below)

-- First, create a test company
INSERT INTO company (name, org_number, address, phone, email)
VALUES ('Test Bilverkstad AB', '556677-8899', 'Testgatan 1, 12345 Stockholm', '08-123456', 'info@test.se');

-- Then create the admin user (company_id will be 1 as it's the first company)
INSERT INTO users (username, password, company_id)
VALUES ('admin@melta-studios.se', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 1);

-- Assign SUPER_ADMIN role to user
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.username = 'admin' AND r.name = 'SUPER_ADMIN';
