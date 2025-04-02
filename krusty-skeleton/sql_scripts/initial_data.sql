CREATE DATABASE IF NOT EXISTS krusty;
USE krusty;

SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE Request;
TRUNCATE TABLE Orders;
TRUNCATE TABLE Pallets_QualityCheck;
TRUNCATE TABLE Pallets;
TRUNCATE TABLE Ingredients;
TRUNCATE TABLE Products;
TRUNCATE TABLE Customers;
TRUNCATE TABLE QualityCheck;
TRUNCATE TABLE Truck;
TRUNCATE TABLE RawIngredients;

SET FOREIGN_KEY_CHECKS = 1;

-- Insert into Customers
INSERT INTO Customers (customer_name, address) VALUES
('Bjudkakor AB', 'Ystad'),
('Finkakor AB', 'Helsingborg'),
('Gästkakor AB', 'Hässleholm'),
('Kaffebröd AB', 'Landskrona'),
('Kalaskakor AB', 'Trelleborg'),
('Partykakor AB', 'Kristianstad'),
('Skånekakor AB', 'Perstorp'),
('Småbröd AB', 'Malmö');

-- Insert into Cookies (Products table)
INSERT INTO Products (productName) VALUES
('Almond delight'),
('Amneris'),
('Berliner'),
('Nut cookie'),
('Nut ring'),
('Tango');

-- Insert into Ingredients
INSERT INTO RawIngredients (name, quantityInStock, unit) VALUES
('Bread crumbs', 500000, 'g'),
('Butter', 500000, 'g'),
('Chocolate', 500000, 'g'),
('Chopped almonds', 500000, 'g'),
('Cinnamon', 500000, 'g'),
('Egg whites', 500000, 'ml'),
('Eggs', 500000, 'g'),
('Fine-ground nuts', 500000, 'g'),
('Flour', 500000,'g'),
('Ground, roasted nuts', 500000, 'g'),
('Icing sugar', 500000, 'g'),
('Marzipan', 500000, 'g'),
('Potato starch', 500000, 'g'),
('Roasted, chopped nuts', 500000, 'g'),
('Sodium bicarbonate', 500000, 'g'),
('Sugar', 500000, 'g'),
('Vanilla sugar', 500000, 'g'),
('Vanilla', 500000, 'g'),
('Wheat flour', 500000, 'g');

-- Insert into Recipes (Ingredients for each product)
-- Almond delight
INSERT INTO Ingredients (productName, ingredients_id, quantityInStock) VALUES
('Almond delight', (SELECT ingredients_id FROM RawIngredients WHERE name = 'Butter'), 400),
('Almond delight', (SELECT ingredients_id FROM RawIngredients WHERE name = 'Chopped almonds'), 279),
('Almond delight', (SELECT ingredients_id FROM RawIngredients WHERE name = 'Cinnamon'), 10),
('Almond delight', (SELECT ingredients_id FROM RawIngredients WHERE name = 'Flour'), 400),
('Almond delight', (SELECT ingredients_id FROM RawIngredients WHERE name = 'Sugar'), 270);

-- Amneris
INSERT INTO Ingredients (productName, ingredients_id, quantityInStock) VALUES
('Amneris', (SELECT ingredients_id FROM RawIngredients WHERE name = 'Butter'), 250),
('Amneris', (SELECT ingredients_id FROM RawIngredients WHERE name = 'Eggs'), 250),
('Amneris', (SELECT ingredients_id FROM RawIngredients WHERE name = 'Marzipan'), 750),
('Amneris', (SELECT ingredients_id FROM RawIngredients WHERE name = 'Potato starch'), 25),
('Amneris', (SELECT ingredients_id FROM RawIngredients WHERE name = 'Wheat flour'), 25);

-- Berliner
INSERT INTO Ingredients (productName, ingredients_id, quantityInStock) VALUES
('Berliner', (SELECT ingredients_id FROM RawIngredients WHERE name = 'Butter'), 250),
('Berliner', (SELECT ingredients_id FROM RawIngredients WHERE name = 'Chocolate'), 50),
('Berliner', (SELECT ingredients_id FROM RawIngredients WHERE name = 'Eggs'), 50),
('Berliner', (SELECT ingredients_id FROM RawIngredients WHERE name = 'Flour'), 350),
('Berliner', (SELECT ingredients_id FROM RawIngredients WHERE name = 'Icing sugar'), 100),
('Berliner', (SELECT ingredients_id FROM RawIngredients WHERE name = 'Vanilla sugar'), 5);

-- Nut cookie
INSERT INTO Ingredients (productName, ingredients_id, quantityInStock) VALUES
('Nut cookie', (SELECT ingredients_id FROM RawIngredients WHERE name = 'Bread crumbs'), 125),
('Nut cookie', (SELECT ingredients_id FROM RawIngredients WHERE name = 'Chocolate'), 50),
('Nut cookie', (SELECT ingredients_id FROM RawIngredients WHERE name = 'Egg whites'), 350),
('Nut cookie', (SELECT ingredients_id FROM RawIngredients WHERE name = 'Fine-ground nuts'), 750),
('Nut cookie', (SELECT ingredients_id FROM RawIngredients WHERE name = 'Ground, roasted nuts'), 625),
('Nut cookie', (SELECT ingredients_id FROM RawIngredients WHERE name = 'Sugar'), 375);

-- Nut ring
INSERT INTO Ingredients (productName, ingredients_id, quantityInStock) VALUES
('Nut ring', (SELECT ingredients_id FROM RawIngredients WHERE name = 'Butter'), 450),
('Nut ring', (SELECT ingredients_id FROM RawIngredients WHERE name = 'Flour'), 450),
('Nut ring', (SELECT ingredients_id FROM RawIngredients WHERE name = 'Icing sugar'), 190),
('Nut ring', (SELECT ingredients_id FROM RawIngredients WHERE name = 'Roasted, chopped nuts'), 225);

-- Tango
INSERT INTO Ingredients (productName, ingredients_id, quantityInStock) VALUES
('Tango', (SELECT ingredients_id FROM RawIngredients WHERE name = 'Butter'), 200),
('Tango', (SELECT ingredients_id FROM RawIngredients WHERE name = 'Flour'), 300),
('Tango', (SELECT ingredients_id FROM RawIngredients WHERE name = 'Sodium bicarbonate'), 4),
('Tango', (SELECT ingredients_id FROM RawIngredients WHERE name = 'Sugar'), 250),
('Tango', (SELECT ingredients_id FROM RawIngredients WHERE name = 'Vanilla'), 2);

-- Clear Pallets (If you want to clear all values)

-- Disable foreign key checks
SET FOREIGN_KEY_CHECKS = 0;

-- Truncate the Pallets and Pallets_QualityCheck tables
TRUNCATE TABLE Pallets_QualityCheck;
TRUNCATE TABLE Pallets;


SET FOREIGN_KEY_CHECKS = 1;

