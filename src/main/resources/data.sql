-- Rensa tabellen för att undvika dubbletter vid omstart (valfritt)
DELETE FROM work_order_category;

-- Lägg till standardkategorier
INSERT INTO work_order_category (name) VALUES ('Service');
INSERT INTO work_order_category (name) VALUES ('Reparation');
INSERT INTO work_order_category (name) VALUES ('Diagnostik');
INSERT INTO work_order_category (name) VALUES ('Besiktning');
