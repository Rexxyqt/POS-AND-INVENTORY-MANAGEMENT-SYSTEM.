CREATE DATABASE IF NOT EXISTS POS_AND_INVENTORY_DB;
USE POS_AND_INVENTORY_DB;

CREATE TABLE `users` (
    `id` int NOT NULL AUTO_INCREMENT,
    `username` varchar(50) NOT NULL,
    `password` varchar(255) NOT NULL,
    `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `username` (`username`)
);

INSERT INTO `users` (`username`, `password`) VALUES ('admin', 'admin123');

CREATE TABLE `categories` (
    `id` int NOT NULL AUTO_INCREMENT,
    `name` varchar(100) NOT NULL,
    `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `name` (`name`)
);

CREATE TABLE `brands` (
    `id` int NOT NULL AUTO_INCREMENT,
    `name` varchar(100) NOT NULL,
    `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `name` (`name`)
);

CREATE TABLE `suppliers` (
    `id` int NOT NULL AUTO_INCREMENT,
    `name` varchar(100) NOT NULL,
    `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `name` (`name`)
);

CREATE TABLE `units` (
    `id` int NOT NULL AUTO_INCREMENT,
    `name` varchar(50) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `name` (`name`)
);

CREATE TABLE `products` (
    `id` int NOT NULL AUTO_INCREMENT,
    `name` varchar(200) NOT NULL,
    `category_id` int NOT NULL,
    `brand_id` int NOT NULL,
    `supplier_id` int NOT NULL,
    `unit` varchar(50) NOT NULL,
    `cost_price` decimal(10, 2) NOT NULL DEFAULT '0.00',
    `markup_percentage` decimal(5, 2) NOT NULL DEFAULT '0.00',
    `selling_price` decimal(10, 2) NOT NULL DEFAULT '0.00',
    `quantity` int NOT NULL DEFAULT '0',
    `stock_threshold` int NOT NULL DEFAULT '10',
    `date_added` date NOT NULL,
    `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_name` (`name`),
    KEY `idx_category` (`category_id`),
    KEY `idx_brand` (`brand_id`),
    KEY `idx_supplier` (`supplier_id`),
    CONSTRAINT `products_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`) ON DELETE RESTRICT,
    CONSTRAINT `products_ibfk_2` FOREIGN KEY (`brand_id`) REFERENCES `brands` (`id`) ON DELETE RESTRICT,
    CONSTRAINT `products_ibfk_3` FOREIGN KEY (`supplier_id`) REFERENCES `suppliers` (`id`) ON DELETE RESTRICT
);

CREATE TABLE `inventory_logs` (
    `id` int NOT NULL AUTO_INCREMENT,
    `product_id` int NOT NULL,
    `activity_type` varchar(20) NOT NULL,
    `quantity_change` int NOT NULL,
    `description` varchar(500) DEFAULT NULL,
    `activity_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_product` (`product_id`),
    KEY `idx_activity_date` (`activity_date`),
    KEY `idx_activity_type` (`activity_type`),
    CONSTRAINT `fk_inventory_logs_product_id` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE
);

CREATE TABLE `sales` (
    `id` int NOT NULL AUTO_INCREMENT,
    `sale_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `subtotal` decimal(10, 2) NOT NULL DEFAULT '0.00',
    `tax` decimal(10, 2) NOT NULL DEFAULT '0.00',
    `total` decimal(10, 2) NOT NULL DEFAULT '0.00',
    PRIMARY KEY (`id`),
    KEY `idx_sale_date` (`sale_date`)
);

CREATE TABLE `sale_items` (
    `id` int NOT NULL AUTO_INCREMENT,
    `sale_id` int NOT NULL,
    `product_id` int NOT NULL,
    `product_name` varchar(200) NOT NULL,
    `quantity` int NOT NULL,
    `unit_price` decimal(10, 2) NOT NULL,
    `subtotal` decimal(10, 2) NOT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_sale` (`sale_id`),
    KEY `idx_product` (`product_id`),
    CONSTRAINT `fk_sale_items_product_id` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE,
    CONSTRAINT `sale_items_ibfk_1` FOREIGN KEY (`sale_id`) REFERENCES `sales` (`id`) ON DELETE CASCADE
);
