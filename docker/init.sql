-- CarCMS Database Initialization Script for MariaDB
-- This script creates all tables from scratch

-- Drop existing tables in correct order (respecting foreign key constraints)
DROP TABLE IF EXISTS work_task_article;
DROP TABLE IF EXISTS work_task;
DROP TABLE IF EXISTS required_part_template;
DROP TABLE IF EXISTS part_mapping;
DROP TABLE IF EXISTS part_order;
DROP TABLE IF EXISTS vehicle_service;
DROP TABLE IF EXISTS work_task_template;
DROP TABLE IF EXISTS work_order;
DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS invoice;
DROP TABLE IF EXISTS article;
DROP TABLE IF EXISTS supplier;
DROP TABLE IF EXISTS vehicle;
DROP TABLE IF EXISTS end_customer;
DROP TABLE IF EXISTS service_variation;
DROP TABLE IF EXISTS vehicle_service_type;
DROP TABLE IF EXISTS engine_type;
DROP TABLE IF EXISTS vehicle_model;
DROP TABLE IF EXISTS user_roles;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS workplace;
DROP TABLE IF EXISTS work_order_category;
DROP TABLE IF EXISTS repair_category;
DROP TABLE IF EXISTS work_order_status;
DROP TABLE IF EXISTS log_manual_changes;
DROP TABLE IF EXISTS company;

-- Create tables

-- Company table
CREATE TABLE company (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    org_number VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(255),
    email VARCHAR(255),
    address VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Workplace table
CREATE TABLE workplace (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    company_id BIGINT,
    FOREIGN KEY (company_id) REFERENCES company(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Roles table
CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Users table
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    company_id BIGINT NOT NULL,
    FOREIGN KEY (company_id) REFERENCES company(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- User-Roles join table
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- End Customer table
CREATE TABLE end_customer (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    email VARCHAR(255),
    phone VARCHAR(255),
    billing_street VARCHAR(255),
    billing_city VARCHAR(255),
    billing_zip VARCHAR(255),
    billing_country VARCHAR(255),
    company_id BIGINT NOT NULL,
    FOREIGN KEY (company_id) REFERENCES company(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Vehicle Model table
CREATE TABLE vehicle_model (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    brand VARCHAR(255),
    model VARCHAR(255),
    year INT,
    fuel_type VARCHAR(255),
    engine_code VARCHAR(255),
    transmission VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Engine Type table
CREATE TABLE engine_type (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    fuel_type VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Vehicle table
CREATE TABLE vehicle (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    registration_number VARCHAR(255),
    last_known_service INT,
    mileage INT,
    transmission VARCHAR(255),
    last_known_service_date DATE,
    vehicle_model_id BIGINT NOT NULL,
    engine_type_id BIGINT,
    workplace_id BIGINT,
    end_customer_id BIGINT NOT NULL,
    company_id BIGINT,
    FOREIGN KEY (vehicle_model_id) REFERENCES vehicle_model(id),
    FOREIGN KEY (engine_type_id) REFERENCES engine_type(id),
    FOREIGN KEY (workplace_id) REFERENCES workplace(id),
    FOREIGN KEY (end_customer_id) REFERENCES end_customer(id) ON DELETE CASCADE,
    FOREIGN KEY (company_id) REFERENCES company(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Work Order Status table
CREATE TABLE work_order_status (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Work Order Category table
CREATE TABLE work_order_category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Repair Category table
CREATE TABLE repair_category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Work Task Template table
CREATE TABLE work_task_template (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    description VARCHAR(255) NOT NULL,
    estimated_time INT NOT NULL,
    category_id BIGINT NOT NULL,
    repair_category_id BIGINT,
    FOREIGN KEY (category_id) REFERENCES work_order_category(id),
    FOREIGN KEY (repair_category_id) REFERENCES repair_category(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Work Order table
CREATE TABLE work_order (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    description VARCHAR(255),
    created_date DATE,
    vehicle_id BIGINT NOT NULL,
    status_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    mechanic_id BIGINT,
    FOREIGN KEY (vehicle_id) REFERENCES vehicle(id) ON DELETE CASCADE,
    FOREIGN KEY (status_id) REFERENCES work_order_status(id),
    FOREIGN KEY (category_id) REFERENCES work_order_category(id),
    FOREIGN KEY (mechanic_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Supplier table
CREATE TABLE supplier (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    contact_email VARCHAR(255),
    contact_phone VARCHAR(255),
    address VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Article table
CREATE TABLE article (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    description VARCHAR(255) NOT NULL,
    part_number VARCHAR(255) NOT NULL UNIQUE,
    stock_quantity INT NOT NULL,
    purchase_price DOUBLE,
    selling_price DOUBLE,
    supplier_id BIGINT NOT NULL,
    FOREIGN KEY (supplier_id) REFERENCES supplier(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Work Task table
CREATE TABLE work_task (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    description VARCHAR(255) NOT NULL,
    work_order_id BIGINT NOT NULL,
    work_task_template_id BIGINT,
    status_id BIGINT NOT NULL,
    FOREIGN KEY (work_order_id) REFERENCES work_order(id) ON DELETE CASCADE,
    FOREIGN KEY (work_task_template_id) REFERENCES work_task_template(id),
    FOREIGN KEY (status_id) REFERENCES work_order_status(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Work Task Article table (junction table)
CREATE TABLE work_task_article (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    work_task_id BIGINT NOT NULL,
    article_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    FOREIGN KEY (work_task_id) REFERENCES work_task(id) ON DELETE CASCADE,
    FOREIGN KEY (article_id) REFERENCES article(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Required Part Template table
CREATE TABLE required_part_template (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_template_id BIGINT NOT NULL,
    article_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    FOREIGN KEY (task_template_id) REFERENCES work_task_template(id) ON DELETE CASCADE,
    FOREIGN KEY (article_id) REFERENCES article(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Part Order table
CREATE TABLE part_order (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    article_id BIGINT NOT NULL,
    work_order_id BIGINT NOT NULL,
    supplier_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    order_date DATETIME,
    expected_arrival_date DATETIME,
    received BOOLEAN NOT NULL DEFAULT FALSE,
    purchase_price DOUBLE,
    selling_price DOUBLE,
    FOREIGN KEY (article_id) REFERENCES article(id),
    FOREIGN KEY (work_order_id) REFERENCES work_order(id) ON DELETE CASCADE,
    FOREIGN KEY (supplier_id) REFERENCES supplier(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Part Mapping table
CREATE TABLE part_mapping (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    vehicle_model_id BIGINT NOT NULL,
    task_template_id BIGINT NOT NULL,
    article_id BIGINT NOT NULL,
    quantity INT,
    FOREIGN KEY (vehicle_model_id) REFERENCES vehicle_model(id),
    FOREIGN KEY (task_template_id) REFERENCES work_task_template(id),
    FOREIGN KEY (article_id) REFERENCES article(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Invoice table
CREATE TABLE invoice (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    invoice_number VARCHAR(255),
    issue_date DATE,
    due_date DATE,
    amount DECIMAL(19,2),
    paid BOOLEAN NOT NULL DEFAULT FALSE,
    end_customer_id BIGINT NOT NULL,
    FOREIGN KEY (end_customer_id) REFERENCES end_customer(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bookings table
CREATE TABLE bookings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    mechanic_id BIGINT NOT NULL,
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    category_color VARCHAR(255) NOT NULL,
    FOREIGN KEY (mechanic_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Vehicle Service Type table
CREATE TABLE vehicle_service_type (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    service_name VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Service Variation table
CREATE TABLE service_variation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    variation VARCHAR(255),
    service_type_id BIGINT,
    FOREIGN KEY (service_type_id) REFERENCES vehicle_service_type(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Vehicle Service table
CREATE TABLE vehicle_service (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    vehicle_model_id BIGINT NOT NULL,
    service_type_id BIGINT,
    variation_id BIGINT,
    start_interval_km INT,
    start_interval_time_months INT,
    interval_km INT,
    interval_time_months INT,
    FOREIGN KEY (vehicle_model_id) REFERENCES vehicle_model(id),
    FOREIGN KEY (service_type_id) REFERENCES vehicle_service_type(id),
    FOREIGN KEY (variation_id) REFERENCES service_variation(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Log Manual Changes table (for GDPR compliance)
CREATE TABLE log_manual_changes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    entity_type VARCHAR(255),
    entity_id BIGINT,
    field_name VARCHAR(255),
    old_value VARCHAR(1000),
    new_value VARCHAR(1000),
    changed_at DATETIME,
    changed_by VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Password reset tokens table
CREATE TABLE password_reset_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token_hash VARCHAR(255) NOT NULL UNIQUE,
    created_at DATETIME NOT NULL,
    expires_at DATETIME NOT NULL,
    used BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_token_hash (token_hash),
    INDEX idx_expires_at (expires_at),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insert default roles
INSERT INTO roles (name) VALUES
    ('SUPER_ADMIN'),
    ('CUSTOMER_ADMIN'),
    ('WORKPLACE_ADMIN'),
    ('MECHANIC'),
    ('OFFICE');

-- Insert default work order statuses
INSERT INTO work_order_status (name) VALUES
    ('PENDING'),
    ('IN_PROGRESS'),
    ('AWAITING_PARTS'),
    ('COMPLETED'),
    ('CANCELLED');

-- Insert default work order categories (examples)
INSERT INTO work_order_category (name) VALUES
    ('SERVICE'),
    ('REPAIR'),
    ('INSPECTION'),
    ('TIRE_CHANGE'),
    ('OTHER');

-- Insert default repair categories (examples)
INSERT INTO repair_category (name) VALUES
    ('ENGINE'),
    ('TRANSMISSION'),
    ('BRAKES'),
    ('SUSPENSION'),
    ('ELECTRICAL'),
    ('BODY'),
    ('INTERIOR');

-- Create indexes for better performance
CREATE INDEX idx_users_company ON users(company_id);
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_vehicle_registration ON vehicle(registration_number);
CREATE INDEX idx_vehicle_end_customer ON vehicle(end_customer_id);
CREATE INDEX idx_work_order_vehicle ON work_order(vehicle_id);
CREATE INDEX idx_work_order_status ON work_order(status_id);
CREATE INDEX idx_work_order_mechanic ON work_order(mechanic_id);
CREATE INDEX idx_end_customer_company ON end_customer(company_id);
CREATE INDEX idx_workplace_company ON workplace(company_id);
CREATE INDEX idx_article_part_number ON article(part_number);
