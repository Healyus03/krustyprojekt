CREATE DATABASE IF NOT EXISTS krusty;
USE krusty;

SET foreign_key_checks = 0;

DROP TABLE IF EXISTS Pallets_QualityCheck;
DROP TABLE IF EXISTS Ingredients;
DROP TABLE IF EXISTS Pallets;
DROP TABLE IF EXISTS QualityCheck;
DROP TABLE IF EXISTS Truck;
DROP TABLE IF EXISTS Orders;
DROP TABLE IF EXISTS Customers;
DROP TABLE IF EXISTS Request;
DROP TABLE IF EXISTS Products;
DROP TABLE IF EXISTS RawIngredients;

SET foreign_key_checks = 1;

CREATE TABLE IF NOT EXISTS RawIngredients (
    ingredients_id INT AUTO_INCREMENT,
    name VARCHAR(30),
    quantityInStock INT, 
    unit VARCHAR(10),
    lastDeliveredDate DATE,
    lastDeliveredAmount INT, 
    PRIMARY KEY (ingredients_id)
);

CREATE TABLE IF NOT EXISTS Products (
    productName VARCHAR(30),
    PRIMARY KEY (productName)
);

CREATE TABLE IF NOT EXISTS Truck (
    truck_id INT,
    capacity INT,
    PRIMARY KEY (truck_id)
);

CREATE TABLE IF NOT EXISTS Pallets (
    pallet_id INT AUTO_INCREMENT,
    productName VARCHAR(30),
    productionDate DATE,
    isBlocked BOOLEAN,
    deliveryDate DATE,
    truck_id INT,  -- Added this line
    PRIMARY KEY (pallet_id),
    FOREIGN KEY (productName) REFERENCES Products(productName)
    ON DELETE CASCADE,
    FOREIGN KEY (truck_id) REFERENCES Truck(truck_id)
    ON DELETE CASCADE
);



CREATE TABLE IF NOT EXISTS QualityCheck (
    sample_id INT,
    status VARCHAR(30),
    testDate DATE,
    PRIMARY KEY (sample_id)
);

CREATE TABLE IF NOT EXISTS Pallets_QualityCheck (
    pallet_id INT,
    sample_id INT,
    FOREIGN KEY (pallet_id) REFERENCES Pallets(pallet_id)
    ON DELETE CASCADE,
    FOREIGN KEY (sample_id) REFERENCES QualityCheck(sample_id)
    ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Ingredients (
    ingredients_id INT,
    quantityInStock INT, 
    productName VARCHAR(30),
    FOREIGN KEY (ingredients_id) REFERENCES RawIngredients(ingredients_id)
    ON DELETE CASCADE,
    FOREIGN KEY (productName) REFERENCES Products(productName)
    ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Customers (
    customer_id INT AUTO_INCREMENT,
    customer_name VARCHAR(30),
    address VARCHAR(40),
    PRIMARY KEY(customer_id)
);

CREATE TABLE IF NOT EXISTS Orders (
    order_id INT,
    customer_id INT,
    orderDate DATE,
    deliveryDate DATE,
    PRIMARY KEY(order_id),
    FOREIGN KEY (customer_id) REFERENCES Customers(customer_id)
    ON DELETE CASCADE
);



CREATE TABLE IF NOT EXISTS Request (
    quantityInStock INT, 
    productName VARCHAR(30),
    order_id INT,
    FOREIGN KEY (productName) REFERENCES Products(productName)
    ON DELETE CASCADE,
    FOREIGN KEY (order_id) REFERENCES Orders(order_id)
    ON DELETE CASCADE
);
