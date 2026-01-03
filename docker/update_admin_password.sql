-- Update admin user password with BCrypt hash for "admin"
-- Note: Username should be in email format: admin@melta-studios.se
UPDATE users SET password = '$2a$10$nwSURppTEHPHZU5x.GaCPeIZXxlvv2mWE7jLIyVfEHpYsXqfh9rV2' WHERE username = 'admin@melta-studios.se';
