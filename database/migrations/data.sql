SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE `payment`;
TRUNCATE TABLE `transaction`;
TRUNCATE TABLE `user_read_notification`;
TRUNCATE TABLE `notifications`;
TRUNCATE TABLE `review`;
TRUNCATE TABLE `food_item_option`;
TRUNCATE TABLE `food_item`;
TRUNCATE TABLE `food_order`;
TRUNCATE TABLE `restaurant_table_session`;
TRUNCATE TABLE `booking_table`;
TRUNCATE TABLE `booking`;
TRUNCATE TABLE `booking_time`;
TRUNCATE TABLE `operation_time`;
TRUNCATE TABLE `time`;
TRUNCATE TABLE `payment_source`;
TRUNCATE TABLE `food_description_option_group`;
TRUNCATE TABLE `food_option`;
TRUNCATE TABLE `food_option_group`;
TRUNCATE TABLE `food_description`;
TRUNCATE TABLE `food_group`;
TRUNCATE TABLE `menu`;
TRUNCATE TABLE `restaurant_table`;
TRUNCATE TABLE `table_area`;
TRUNCATE TABLE `legal_doc`;
TRUNCATE TABLE `restaurant_cuisine`;
TRUNCATE TABLE `cuisine`;
TRUNCATE TABLE `user_role`;
TRUNCATE TABLE `restaurant`;
TRUNCATE TABLE `user`;
TRUNCATE TABLE `role`;

-- 1) Roles
INSERT INTO `role` (`id`, `created_at`, `updated_at`, `name`) VALUES
(1, NOW(), NOW(), 'ADMIN'),
(2, NOW(), NOW(), 'MANAGER'),
(3, NOW(), NOW(), 'CUSTOMER'),
(4, NOW(), NOW(), 'RECEPTIONIST'),
(5, NOW(), NOW(), 'WAITER'),
(6, NOW(), NOW(), 'CASHIER');

-- Password: 123456 (BCrypt)
INSERT INTO `user` (`id`, `created_at`, `updated_at`, `username`, `password`, `full_name`, `avatar`, `email`, `phone`, `address`, `status`, `workplace_restaurant_id`) VALUES
(1, NOW(), NOW(), 'admin_1', '$2a$10$tBtDcA/Z72z6/tuO04GhWOC3GGZo7rEEqtKFWLfPLPU.8NaOennoS', 'System Admin', 'admin.png', 'admin@gmail.com', '0900000001', 'Ho Chi Minh City', 'ACTIVE', NULL),
(2, NOW(), NOW(), 'manager_1', '$2a$10$tBtDcA/Z72z6/tuO04GhWOC3GGZo7rEEqtKFWLfPLPU.8NaOennoS', 'Manager R1', 'manager1.png', 'manager_1@gmail.com', '0900000002', 'Ho Chi Minh City', 'ACTIVE', 1),
(3, NOW(), NOW(), 'customer_1', '$2a$10$tBtDcA/Z72z6/tuO04GhWOC3GGZo7rEEqtKFWLfPLPU.8NaOennoS', 'Customer One', 'customer1.png', 'customer_1@gmail.com', '0900000003', 'Ho Chi Minh City', 'ACTIVE', NULL),
(4, NOW(), NOW(), 'reception_1', '$2a$10$tBtDcA/Z72z6/tuO04GhWOC3GGZo7rEEqtKFWLfPLPU.8NaOennoS', 'Reception R1', 'reception1.png', 'reception_1@gmail.com', '0900000004', 'Ho Chi Minh City', 'ACTIVE', 1),
(5, NOW(), NOW(), 'waiter_1', '$2a$10$tBtDcA/Z72z6/tuO04GhWOC3GGZo7rEEqtKFWLfPLPU.8NaOennoS', 'Waiter R1', 'waiter1.png', 'waiter_1@gmail.com', '0900000005', 'Ho Chi Minh City', 'ACTIVE', 1),
(6, NOW(), NOW(), 'cashier_1', '$2a$10$tBtDcA/Z72z6/tuO04GhWOC3GGZo7rEEqtKFWLfPLPU.8NaOennoS', 'Cashier R1', 'cashier1.png', 'cashier_1@gmail.com', '0900000006', 'Ho Chi Minh City', 'ACTIVE', 1),
(7, NOW(), NOW(), 'manager_2', '$2a$10$tBtDcA/Z72z6/tuO04GhWOC3GGZo7rEEqtKFWLfPLPU.8NaOennoS', 'Manager R2', 'manager2.png', 'manager_2@gmail.com', '0900000007', 'Ho Chi Minh City', 'ACTIVE', 2),
(8, NOW(), NOW(), 'reception_2', '$2a$10$tBtDcA/Z72z6/tuO04GhWOC3GGZo7rEEqtKFWLfPLPU.8NaOennoS', 'Reception R2', 'reception2.png', 'reception_2@gmail.com', '0900000008', 'Ho Chi Minh City', 'ACTIVE', 2),
(9, NOW(), NOW(), 'waiter_2', '$2a$10$tBtDcA/Z72z6/tuO04GhWOC3GGZo7rEEqtKFWLfPLPU.8NaOennoS', 'Waiter R2', 'waiter2.png', 'waiter_2@gmail.com', '0900000009', 'Ho Chi Minh City', 'ACTIVE', 2),
(11, NOW(), NOW(), 'manager_3', '$2a$10$tBtDcA/Z72z6/tuO04GhWOC3GGZo7rEEqtKFWLfPLPU.8NaOennoS', 'Manager R3', 'manager3.png', 'manager_3@gmail.com', '0900000011', 'Da Nang', 'ACTIVE', 3),
(12, NOW(), NOW(), 'reception_3', '$2a$10$tBtDcA/Z72z6/tuO04GhWOC3GGZo7rEEqtKFWLfPLPU.8NaOennoS', 'Reception R3', 'reception3.png', 'reception_3@gmail.com', '0900000012', 'Da Nang', 'ACTIVE', 3),
(13, NOW(), NOW(), 'waiter_3', '$2a$10$tBtDcA/Z72z6/tuO04GhWOC3GGZo7rEEqtKFWLfPLPU.8NaOennoS', 'Waiter R3', 'waiter3.png', 'waiter_3@gmail.com', '0900000013', 'Da Nang', 'ACTIVE', 3),
(14, NOW(), NOW(), 'cashier_3', '$2a$10$tBtDcA/Z72z6/tuO04GhWOC3GGZo7rEEqtKFWLfPLPU.8NaOennoS', 'Cashier R3', 'cashier3.png', 'cashier_3@gmail.com', '0900000014', 'Da Nang', 'ACTIVE', 3),
(15, NOW(), NOW(), 'manager_4', '$2a$10$tBtDcA/Z72z6/tuO04GhWOC3GGZo7rEEqtKFWLfPLPU.8NaOennoS', 'Manager R4', 'manager4.png', 'manager_4@gmail.com', '0900000015', 'Ha Noi', 'ACTIVE', 4),
(16, NOW(), NOW(), 'reception_4', '$2a$10$tBtDcA/Z72z6/tuO04GhWOC3GGZo7rEEqtKFWLfPLPU.8NaOennoS', 'Reception R4', 'reception4.png', 'reception_4@gmail.com', '0900000016', 'Ha Noi', 'ACTIVE', 4),
(17, NOW(), NOW(), 'waiter_4', '$2a$10$tBtDcA/Z72z6/tuO04GhWOC3GGZo7rEEqtKFWLfPLPU.8NaOennoS', 'Waiter R4', 'waiter4.png', 'waiter_4@gmail.com', '0900000017', 'Ha Noi', 'ACTIVE', 4),
(18, NOW(), NOW(), 'cashier_4', '$2a$10$tBtDcA/Z72z6/tuO04GhWOC3GGZo7rEEqtKFWLfPLPU.8NaOennoS', 'Cashier R4', 'cashier4.png', 'cashier_4@gmail.com', '0900000018', 'Ha Noi', 'ACTIVE', 4),
(19, NOW(), NOW(), 'manager_5', '$2a$10$tBtDcA/Z72z6/tuO04GhWOC3GGZo7rEEqtKFWLfPLPU.8NaOennoS', 'Manager R5', 'manager5.png', 'manager_5@gmail.com', '0900000019', 'Can Tho', 'ACTIVE', 5),
(20, NOW(), NOW(), 'reception_5', '$2a$10$tBtDcA/Z72z6/tuO04GhWOC3GGZo7rEEqtKFWLfPLPU.8NaOennoS', 'Reception R5', 'reception5.png', 'reception_5@gmail.com', '0900000020', 'Can Tho', 'ACTIVE', 5),
(21, NOW(), NOW(), 'waiter_5', '$2a$10$tBtDcA/Z72z6/tuO04GhWOC3GGZo7rEEqtKFWLfPLPU.8NaOennoS', 'Waiter R5', 'waiter5.png', 'waiter_5@gmail.com', '0900000021', 'Can Tho', 'ACTIVE', 5),
(22, NOW(), NOW(), 'cashier_5', '$2a$10$tBtDcA/Z72z6/tuO04GhWOC3GGZo7rEEqtKFWLfPLPU.8NaOennoS', 'Cashier R5', 'cashier5.png', 'cashier_5@gmail.com', '0900000022', 'Can Tho', 'ACTIVE', 5),
(23, NOW(), NOW(), 'manager_6', '$2a$10$tBtDcA/Z72z6/tuO04GhWOC3GGZo7rEEqtKFWLfPLPU.8NaOennoS', 'Manager R6', 'manager6.png', 'manager_6@gmail.com', '0900000023', 'Nha Trang', 'ACTIVE', 6),
(24, NOW(), NOW(), 'reception_6', '$2a$10$tBtDcA/Z72z6/tuO04GhWOC3GGZo7rEEqtKFWLfPLPU.8NaOennoS', 'Reception R6', 'reception6.png', 'reception_6@gmail.com', '0900000024', 'Nha Trang', 'ACTIVE', 6),
(25, NOW(), NOW(), 'waiter_6', '$2a$10$tBtDcA/Z72z6/tuO04GhWOC3GGZo7rEEqtKFWLfPLPU.8NaOennoS', 'Waiter R6', 'waiter6.png', 'waiter_6@gmail.com', '0900000025', 'Nha Trang', 'ACTIVE', 6),
(26, NOW(), NOW(), 'cashier_6', '$2a$10$tBtDcA/Z72z6/tuO04GhWOC3GGZo7rEEqtKFWLfPLPU.8NaOennoS', 'Cashier R6', 'cashier6.png', 'cashier_6@gmail.com', '0900000026', 'Nha Trang', 'ACTIVE', 6),
(27, NOW(), NOW(), 'manager_7', '$2a$10$tBtDcA/Z72z6/tuO04GhWOC3GGZo7rEEqtKFWLfPLPU.8NaOennoS', 'Manager R7', 'manager7.png', 'manager_7@gmail.com', '0900000027', 'Hue', 'ACTIVE', 7),
(28, NOW(), NOW(), 'reception_7', '$2a$10$tBtDcA/Z72z6/tuO04GhWOC3GGZo7rEEqtKFWLfPLPU.8NaOennoS', 'Reception R7', 'reception7.png', 'reception_7@gmail.com', '0900000028', 'Hue', 'ACTIVE', 7),
(29, NOW(), NOW(), 'waiter_7', '$2a$10$tBtDcA/Z72z6/tuO04GhWOC3GGZo7rEEqtKFWLfPLPU.8NaOennoS', 'Waiter R7', 'waiter7.png', 'waiter_7@gmail.com', '0900000029', 'Hue', 'ACTIVE', 7),
(30, NOW(), NOW(), 'cashier_7', '$2a$10$tBtDcA/Z72z6/tuO04GhWOC3GGZo7rEEqtKFWLfPLPU.8NaOennoS', 'Cashier R7', 'cashier7.png', 'cashier_7@gmail.com', '0900000030', 'Hue', 'ACTIVE', 7),
(31, NOW(), NOW(), 'manager_8', '$2a$10$tBtDcA/Z72z6/tuO04GhWOC3GGZo7rEEqtKFWLfPLPU.8NaOennoS', 'Manager R8', 'manager8.png', 'manager_8@gmail.com', '0900000031', 'Vung Tau', 'ACTIVE', 8),
(32, NOW(), NOW(), 'reception_8', '$2a$10$tBtDcA/Z72z6/tuO04GhWOC3GGZo7rEEqtKFWLfPLPU.8NaOennoS', 'Reception R8', 'reception8.png', 'reception_8@gmail.com', '0900000032', 'Vung Tau', 'ACTIVE', 8),
(33, NOW(), NOW(), 'waiter_8', '$2a$10$tBtDcA/Z72z6/tuO04GhWOC3GGZo7rEEqtKFWLfPLPU.8NaOennoS', 'Waiter R8', 'waiter8.png', 'waiter_8@gmail.com', '0900000033', 'Vung Tau', 'ACTIVE', 8),
(34, NOW(), NOW(), 'cashier_8', '$2a$10$tBtDcA/Z72z6/tuO04GhWOC3GGZo7rEEqtKFWLfPLPU.8NaOennoS', 'Cashier R8', 'cashier8.png', 'cashier_8@gmail.com', '0900000034', 'Vung Tau', 'ACTIVE', 8),
(35, NOW(), NOW(), 'waiter_banned_1', '$2a$10$tBtDcA/Z72z6/tuO04GhWOC3GGZo7rEEqtKFWLfPLPU.8NaOennoS', 'Waiter Banned', 'waiter_banned.png', 'waiter_banned_1@gmail.com', '0900000035', 'Ho Chi Minh City', 'BANNED', 1),
(36, NOW(), NOW(), 'cashier_2', '$2a$10$tBtDcA/Z72z6/tuO04GhWOC3GGZo7rEEqtKFWLfPLPU.8NaOennoS', 'Cashier R2', 'cashier2.png', 'cashier_2@gmail.com', '0900000036', 'Ho Chi Minh City', 'ACTIVE', 2);

-- 30 customers for stress scenarios
INSERT INTO `user` (`id`, `created_at`, `updated_at`, `username`, `password`, `full_name`, `avatar`, `email`, `phone`, `address`, `status`, `workplace_restaurant_id`)
SELECT
    100 + s.n,
    NOW(),
    NOW(),
    CONCAT('customer_', 1 + s.n),
    '$2a$10$tBtDcA/Z72z6/tuO04GhWOC3GGZo7rEEqtKFWLfPLPU.8NaOennoS',
    CONCAT('Customer ', 1 + s.n),
    CONCAT('customer_', 1 + s.n, '.png'),
    CONCAT('customer_', 1 + s.n, '@gmail.com'),
    CONCAT('091', LPAD(1 + s.n, 7, '0')),
    'Viet Nam',
    CASE WHEN s.n IN (18, 26) THEN 'BANNED' ELSE 'ACTIVE' END,
    NULL
FROM (
    SELECT 1 AS n UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL
    SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9 UNION ALL SELECT 10 UNION ALL
    SELECT 11 UNION ALL SELECT 12 UNION ALL SELECT 13 UNION ALL SELECT 14 UNION ALL SELECT 15 UNION ALL
    SELECT 16 UNION ALL SELECT 17 UNION ALL SELECT 18 UNION ALL SELECT 19 UNION ALL SELECT 20 UNION ALL
    SELECT 21 UNION ALL SELECT 22 UNION ALL SELECT 23 UNION ALL SELECT 24 UNION ALL SELECT 25 UNION ALL
    SELECT 26 UNION ALL SELECT 27 UNION ALL SELECT 28 UNION ALL SELECT 29 UNION ALL SELECT 30
) AS s;

INSERT INTO `user_role` (`user_id`, `role_id`) VALUES
(1, 1),
(2, 2), (4, 4), (5, 5), (6, 6), (35, 5),
(7, 2), (8, 4), (9, 5), (36, 6),
(11, 2), (12, 4), (13, 5), (14, 6),
(15, 2), (16, 4), (17, 5), (18, 6),
(19, 2), (20, 4), (21, 5), (22, 6),
(23, 2), (24, 4), (25, 5), (26, 6),
(27, 2), (28, 4), (29, 5), (30, 6),
(31, 2), (32, 4), (33, 5), (34, 6),
(3, 3);

INSERT INTO `user_role` (`user_id`, `role_id`)
SELECT id, 3 FROM `user` WHERE id BETWEEN 101 AND 130;

-- 2) Cuisines, restaurants, legal docs
INSERT INTO `cuisine` (`id`, `created_at`, `updated_at`, `name`) VALUES
(1, NOW(), NOW(), 'Vietnamese'),
(2, NOW(), NOW(), 'Chinese'),
(3, NOW(), NOW(), 'Japanese'),
(4, NOW(), NOW(), 'Korean'),
(5, NOW(), NOW(), 'Thai'),
(6, NOW(), NOW(), 'Hotpot'),
(7, NOW(), NOW(), 'Seafood'),
(8, NOW(), NOW(), 'BBQ');

INSERT INTO `restaurant` (`id`, `created_at`, `updated_at`, `name`, `logo`, `description`, `status`, `location`, `avg_rating`, `day_of_week`, `base_deposit_value`, `deposit_type`, `address`, `commission_type`, `base_commission_value`, `manager_id`) VALUES
(1, NOW(), NOW(), 'R1 Haidilao Prime', 'r1.png', 'Restaurant 1', 'OPENING', ST_GeomFromText('POINT(10.776889 106.700981)', 4326), 4.80, 127, 200000, 'PER_GUEST', 'District 1, HCM', 'PERCENTAGE', 10, 2),
(2, NOW(), NOW(), 'R2 Kichi Hub', 'r2.png', 'Restaurant 2', 'OPENING', ST_GeomFromText('POINT(10.777222 106.701111)', 4326), 4.40, 127, 150000, 'FIXED', 'District 3, HCM', 'PERCENTAGE', 8, 7),
(3, NOW(), NOW(), 'R3 Sushi Garden', 'r3.png', 'Restaurant 3', 'PENDING', ST_GeomFromText('POINT(16.050000 108.220000)', 4326), 0.00, 127, 100000, 'FIXED', 'Da Nang', 'PERCENTAGE', 7, 11),
(4, NOW(), NOW(), 'R4 Pho Heritage', 'r4.png', 'Restaurant 4', 'REJECTED', ST_GeomFromText('POINT(21.028000 105.840000)', 4326), 0.00, 127, 0, 'NONE', 'Ha Noi', 'FIXED', 25000, 15),
(5, NOW(), NOW(), 'R5 BBQ Street', 'r5.png', 'Restaurant 5', 'SUSPENDED', ST_GeomFromText('POINT(10.045000 105.760000)', 4326), 3.80, 127, 120000, 'PER_GUEST', 'Can Tho', 'PERCENTAGE', 12, 19),
(6, NOW(), NOW(), 'R6 Seafood Pier', 'r6.png', 'Restaurant 6', 'CLOSED', ST_GeomFromText('POINT(12.238000 109.190000)', 4326), 4.10, 127, 90000, 'FIXED', 'Nha Trang', 'FIXED', 30000, 23),
(7, NOW(), NOW(), 'R7 Imperial Cuisine', 'r7.png', 'Restaurant 7', 'OPENING', ST_GeomFromText('POINT(16.463000 107.580000)', 4326), 4.20, 127, 180000, 'PER_GUEST', 'Hue', 'PERCENTAGE', 9, 27),
(8, NOW(), NOW(), 'R8 Ocean View', 'r8.png', 'Restaurant 8', 'OPENING', ST_GeomFromText('POINT(10.350000 107.090000)', 4326), 4.00, 127, 140000, 'FIXED', 'Vung Tau', 'PERCENTAGE', 11, 31);

INSERT INTO `restaurant_cuisine` (`restaurant_id`, `cuisine_id`) VALUES
(1, 2), (1, 6),
(2, 1), (2, 6),
(3, 3), (3, 7),
(4, 1), (4, 5),
(5, 8), (5, 4),
(6, 7), (6, 1),
(7, 2), (7, 1),
(8, 7), (8, 8);

INSERT INTO `legal_doc` (`id`, `created_at`, `updated_at`, `file`, `name`, `type`, `status`, `expire_date`, `restaurant_id`) VALUES
(1, NOW(), NOW(), 'r1_business.pdf', 'R1 Business Registration', 'BUSINESS_REGISTRATION', 'VALID', DATE_ADD(NOW(), INTERVAL 1 YEAR), 1),
(2, NOW(), NOW(), 'r1_food_safety.pdf', 'R1 Food Safety License', 'FOOD_SAFETY_LICENSE', 'VALID', DATE_ADD(NOW(), INTERVAL 10 MONTH), 1),
(3, NOW(), NOW(), 'r2_business.pdf', 'R2 Business Registration', 'BUSINESS_REGISTRATION', 'VALID', DATE_ADD(NOW(), INTERVAL 1 YEAR), 2),
(4, NOW(), NOW(), 'r2_fire.pdf', 'R2 Fire Safety License', 'FIRE_SAFETY_LICENSE', 'VALID', DATE_ADD(NOW(), INTERVAL 7 MONTH), 2),
(5, NOW(), NOW(), 'r3_business.pdf', 'R3 Business Registration', 'BUSINESS_REGISTRATION', 'VALID', DATE_ADD(NOW(), INTERVAL 1 YEAR), 3),
(6, NOW(), NOW(), 'r3_alcohol.pdf', 'R3 Alcohol License', 'ALCOHOL_LICENSE', 'EXPIRED', DATE_SUB(NOW(), INTERVAL 2 MONTH), 3),
(7, NOW(), NOW(), 'r4_business.pdf', 'R4 Business Registration', 'BUSINESS_REGISTRATION', 'EXPIRED', DATE_SUB(NOW(), INTERVAL 1 MONTH), 4),
(8, NOW(), NOW(), 'r4_id.pdf', 'R4 Owner ID', 'IDENTITY_CARD', 'VALID', DATE_ADD(NOW(), INTERVAL 6 MONTH), 4),
(9, NOW(), NOW(), 'r5_business.pdf', 'R5 Business Registration', 'BUSINESS_REGISTRATION', 'VALID', DATE_ADD(NOW(), INTERVAL 8 MONTH), 5),
(10, NOW(), NOW(), 'r5_food_safety.pdf', 'R5 Food Safety License', 'FOOD_SAFETY_LICENSE', 'EXPIRED', DATE_SUB(NOW(), INTERVAL 4 MONTH), 5),
(11, NOW(), NOW(), 'r6_business.pdf', 'R6 Business Registration', 'BUSINESS_REGISTRATION', 'VALID', DATE_ADD(NOW(), INTERVAL 1 YEAR), 6),
(12, NOW(), NOW(), 'r6_other.pdf', 'R6 Other Permit', 'OTHER', 'VALID', DATE_ADD(NOW(), INTERVAL 1 YEAR), 6),
(13, NOW(), NOW(), 'r7_business.pdf', 'R7 Business Registration', 'BUSINESS_REGISTRATION', 'VALID', DATE_ADD(NOW(), INTERVAL 1 YEAR), 7),
(14, NOW(), NOW(), 'r7_food_safety.pdf', 'R7 Food Safety License', 'FOOD_SAFETY_LICENSE', 'VALID', DATE_ADD(NOW(), INTERVAL 9 MONTH), 7),
(15, NOW(), NOW(), 'r8_business.pdf', 'R8 Business Registration', 'BUSINESS_REGISTRATION', 'VALID', DATE_ADD(NOW(), INTERVAL 1 YEAR), 8),
(16, NOW(), NOW(), 'r8_fire.pdf', 'R8 Fire Safety License', 'FIRE_SAFETY_LICENSE', 'VALID', DATE_ADD(NOW(), INTERVAL 8 MONTH), 8);

-- 3) Operation time, table area, tables, menu and foods
INSERT INTO `time` (`id`, `created_at`, `updated_at`, `start_time`, `end_time`, `status`, `type`) VALUES
(1, NOW(), NOW(), CONCAT(CURDATE(), ' 09:00:00'), CONCAT(CURDATE(), ' 22:00:00'), 'OPEN', 'OPERATION_TIME'),
(2, NOW(), NOW(), CONCAT(CURDATE(), ' 10:00:00'), CONCAT(CURDATE(), ' 22:30:00'), 'OPEN', 'OPERATION_TIME'),
(3, NOW(), NOW(), CONCAT(CURDATE(), ' 10:00:00'), CONCAT(CURDATE(), ' 21:30:00'), 'OPEN', 'OPERATION_TIME'),
(4, NOW(), NOW(), CONCAT(CURDATE(), ' 08:30:00'), CONCAT(CURDATE(), ' 21:00:00'), 'OPEN', 'OPERATION_TIME'),
(5, NOW(), NOW(), CONCAT(CURDATE(), ' 09:30:00'), CONCAT(CURDATE(), ' 23:00:00'), 'OPEN', 'OPERATION_TIME'),
(6, NOW(), NOW(), CONCAT(CURDATE(), ' 10:30:00'), CONCAT(CURDATE(), ' 22:30:00'), 'CLOSED', 'OPERATION_TIME'),
(7, NOW(), NOW(), CONCAT(CURDATE(), ' 09:00:00'), CONCAT(CURDATE(), ' 22:00:00'), 'OPEN', 'OPERATION_TIME'),
(8, NOW(), NOW(), CONCAT(CURDATE(), ' 09:00:00'), CONCAT(CURDATE(), ' 22:00:00'), 'OPEN', 'OPERATION_TIME');

INSERT INTO `operation_time` (`operation_time_id`, `day`, `restaurant_id`) VALUES
(1, 1, 1), (2, 2, 2), (3, 3, 3), (4, 4, 4),
(5, 5, 5), (6, 6, 6), (7, 7, 7), (8, 1, 8);

INSERT INTO `table_area` (`id`, `created_at`, `updated_at`, `name`, `status`, `restaurant_id`) VALUES
(1, NOW(), NOW(), 'R1_Main', 'ACTIVE', 1), (2, NOW(), NOW(), 'R1_VIP', 'ACTIVE', 1),
(3, NOW(), NOW(), 'R2_Main', 'ACTIVE', 2), (4, NOW(), NOW(), 'R2_VIP', 'PRIVATE_EVENT', 2),
(5, NOW(), NOW(), 'R3_Main', 'ACTIVE', 3), (6, NOW(), NOW(), 'R3_VIP', 'ACTIVE', 3),
(7, NOW(), NOW(), 'R4_Main', 'MAINTENANCE', 4), (8, NOW(), NOW(), 'R4_VIP', 'CLOSED', 4),
(9, NOW(), NOW(), 'R5_Main', 'ACTIVE', 5), (10, NOW(), NOW(), 'R5_VIP', 'ACTIVE', 5),
(11, NOW(), NOW(), 'R6_Main', 'CLOSED', 6), (12, NOW(), NOW(), 'R6_VIP', 'CLOSED', 6),
(13, NOW(), NOW(), 'R7_Main', 'ACTIVE', 7), (14, NOW(), NOW(), 'R7_VIP', 'ACTIVE', 7),
(15, NOW(), NOW(), 'R8_Main', 'ACTIVE', 8), (16, NOW(), NOW(), 'R8_VIP', 'ACTIVE', 8);

INSERT INTO `restaurant_table` (`id`, `created_at`, `updated_at`, `name`, `capacity`, `status`, `table_area_id`) VALUES
(1, NOW(), NOW(), 'R1_T1', 4, 'AVAILABLE', 1), (2, NOW(), NOW(), 'R1_T2', 6, 'OCCUPIED', 1), (3, NOW(), NOW(), 'R1_T3', 8, 'OCCUPIED', 2), (4, NOW(), NOW(), 'R1_T4', 10, 'AVAILABLE', 2),
(5, NOW(), NOW(), 'R2_T1', 4, 'AVAILABLE', 3), (6, NOW(), NOW(), 'R2_T2', 6, 'AVAILABLE', 3), (7, NOW(), NOW(), 'R2_T3', 8, 'MAINTENANCE', 4), (8, NOW(), NOW(), 'R2_T4', 10, 'AVAILABLE', 4),
(9, NOW(), NOW(), 'R3_T1', 4, 'AVAILABLE', 5), (10, NOW(), NOW(), 'R3_T2', 6, 'AVAILABLE', 5), (11, NOW(), NOW(), 'R3_T3', 8, 'AVAILABLE', 6), (12, NOW(), NOW(), 'R3_T4', 10, 'AVAILABLE', 6),
(13, NOW(), NOW(), 'R4_T1', 4, 'MAINTENANCE', 7), (14, NOW(), NOW(), 'R4_T2', 6, 'AVAILABLE', 7), (15, NOW(), NOW(), 'R4_T3', 8, 'AVAILABLE', 8), (16, NOW(), NOW(), 'R4_T4', 10, 'AVAILABLE', 8),
(17, NOW(), NOW(), 'R5_T1', 4, 'AVAILABLE', 9), (18, NOW(), NOW(), 'R5_T2', 6, 'OCCUPIED', 9), (19, NOW(), NOW(), 'R5_T3', 8, 'AVAILABLE', 10), (20, NOW(), NOW(), 'R5_T4', 10, 'AVAILABLE', 10),
(21, NOW(), NOW(), 'R6_T1', 4, 'AVAILABLE', 11), (22, NOW(), NOW(), 'R6_T2', 6, 'AVAILABLE', 11), (23, NOW(), NOW(), 'R6_T3', 8, 'AVAILABLE', 12), (24, NOW(), NOW(), 'R6_T4', 10, 'AVAILABLE', 12),
(25, NOW(), NOW(), 'R7_T1', 4, 'AVAILABLE', 13), (26, NOW(), NOW(), 'R7_T2', 6, 'AVAILABLE', 13), (27, NOW(), NOW(), 'R7_T3', 8, 'AVAILABLE', 14), (28, NOW(), NOW(), 'R7_T4', 10, 'AVAILABLE', 14),
(29, NOW(), NOW(), 'R8_T1', 4, 'AVAILABLE', 15), (30, NOW(), NOW(), 'R8_T2', 6, 'AVAILABLE', 15), (31, NOW(), NOW(), 'R8_T3', 8, 'AVAILABLE', 16), (32, NOW(), NOW(), 'R8_T4', 10, 'AVAILABLE', 16);

INSERT INTO `menu` (`id`, `created_at`, `updated_at`, `name`, `description`, `restaurant_id`) VALUES
(1, NOW(), NOW(), 'R1_Menu', 'Menu for R1', 1), (2, NOW(), NOW(), 'R2_Menu', 'Menu for R2', 2),
(3, NOW(), NOW(), 'R3_Menu', 'Menu for R3', 3), (4, NOW(), NOW(), 'R4_Menu', 'Menu for R4', 4),
(5, NOW(), NOW(), 'R5_Menu', 'Menu for R5', 5), (6, NOW(), NOW(), 'R6_Menu', 'Menu for R6', 6),
(7, NOW(), NOW(), 'R7_Menu', 'Menu for R7', 7), (8, NOW(), NOW(), 'R8_Menu', 'Menu for R8', 8);

INSERT INTO `food_group` (`id`, `created_at`, `updated_at`, `name`, `description`, `menu_id`) VALUES
(1, NOW(), NOW(), 'R1_Group_Broth', 'Broth group', 1), (2, NOW(), NOW(), 'R1_Group_Meat', 'Meat group', 1),
(3, NOW(), NOW(), 'R2_Group_Main', 'Main dishes', 2), (4, NOW(), NOW(), 'R2_Group_Drink', 'Drinks', 2),
(5, NOW(), NOW(), 'R3_Group_Sushi', 'Sushi', 3), (6, NOW(), NOW(), 'R3_Group_Ramen', 'Ramen', 3),
(7, NOW(), NOW(), 'R4_Group_Pho', 'Pho', 4), (8, NOW(), NOW(), 'R4_Group_Side', 'Side dishes', 4),
(9, NOW(), NOW(), 'R5_Group_BBQ', 'BBQ', 5), (10, NOW(), NOW(), 'R5_Group_Drink', 'Drinks', 5),
(11, NOW(), NOW(), 'R6_Group_Seafood', 'Seafood', 6), (12, NOW(), NOW(), 'R6_Group_Soup', 'Soup', 6),
(13, NOW(), NOW(), 'R7_Group_Hue', 'Hue dishes', 7), (14, NOW(), NOW(), 'R7_Group_Drink', 'Drinks', 7),
(15, NOW(), NOW(), 'R8_Group_Grill', 'Grill', 8), (16, NOW(), NOW(), 'R8_Group_Dessert', 'Dessert', 8);

INSERT INTO `food_option_group` (`id`, `created_at`, `updated_at`, `name`, `description`, `status`, `restaurant_id`) VALUES
(1, NOW(), NOW(), 'R1_SpiceLevel', 'Spice options', 'OPENING', 1),
(2, NOW(), NOW(), 'R2_Size', 'Size options', 'OPENING', 2),
(3, NOW(), NOW(), 'R3_Topping', 'Topping options', 'OPENING', 3),
(4, NOW(), NOW(), 'R4_Extra', 'Extra options', 'OPENING', 4),
(5, NOW(), NOW(), 'R5_Sauce', 'Sauce options', 'OPENING', 5),
(6, NOW(), NOW(), 'R6_SeaLevel', 'Freshness options', 'OPENING', 6),
(7, NOW(), NOW(), 'R7_Spice', 'Hue spice', 'OPENING', 7),
(8, NOW(), NOW(), 'R8_Cook', 'Cook level', 'OPENING', 8);

INSERT INTO `food_option` (`id`, `created_at`, `updated_at`, `name`, `description`, `status`, `price`, `option_group_id`) VALUES
(1, NOW(), NOW(), 'R1_NoSpice', 'No spice', 'OPENING', 0, 1), (2, NOW(), NOW(), 'R1_MediumSpice', 'Medium spice', 'OPENING', 15000, 1),
(3, NOW(), NOW(), 'R2_Small', 'Small', 'OPENING', 0, 2), (4, NOW(), NOW(), 'R2_Large', 'Large', 'OPENING', 12000, 2),
(5, NOW(), NOW(), 'R3_Normal', 'Normal topping', 'OPENING', 0, 3), (6, NOW(), NOW(), 'R3_ExtraEgg', 'Extra egg', 'OPENING', 10000, 3),
(7, NOW(), NOW(), 'R4_ExtraMeat', 'Extra meat', 'OPENING', 20000, 4), (8, NOW(), NOW(), 'R4_ExtraNoodle', 'Extra noodle', 'OPENING', 10000, 4),
(9, NOW(), NOW(), 'R5_Kimchi', 'Kimchi sauce', 'OPENING', 9000, 5), (10, NOW(), NOW(), 'R5_Cheese', 'Cheese sauce', 'OPENING', 15000, 5),
(11, NOW(), NOW(), 'R6_Live', 'Live seafood', 'OPENING', 25000, 6), (12, NOW(), NOW(), 'R6_Regular', 'Regular', 'OPENING', 0, 6),
(13, NOW(), NOW(), 'R7_LessSpice', 'Less spice', 'OPENING', 0, 7), (14, NOW(), NOW(), 'R7_HotSpice', 'Hot spice', 'OPENING', 7000, 7),
(15, NOW(), NOW(), 'R8_MediumRare', 'Medium rare', 'OPENING', 0, 8), (16, NOW(), NOW(), 'R8_WellDone', 'Well done', 'OPENING', 0, 8);

INSERT INTO `food_description` (`id`, `created_at`, `updated_at`, `name`, `description`, `image`, `status`, `price`, `food_group_id`) VALUES
(1, NOW(), NOW(), 'R1_SichuanHotpot', 'Signature hotpot', 'r1_hotpot.jpg', 'OPENING', 280000, 1),
(2, NOW(), NOW(), 'R1_USBeef', 'Premium beef', 'r1_beef.jpg', 'OPENING', 250000, 2),
(3, NOW(), NOW(), 'R2_HotpotSet', 'Combo set', 'r2_hotpot.jpg', 'OPENING', 220000, 3),
(4, NOW(), NOW(), 'R2_IceTea', 'Ice tea', 'r2_tea.jpg', 'OPENING', 35000, 4),
(5, NOW(), NOW(), 'R3_SalmonSushi', 'Fresh salmon', 'r3_sushi.jpg', 'OPENING', 160000, 5),
(6, NOW(), NOW(), 'R3_MisoRamen', 'Miso ramen', 'r3_ramen.jpg', 'OPENING', 140000, 6),
(7, NOW(), NOW(), 'R4_BeefPho', 'Pho bo', 'r4_pho.jpg', 'OPENING', 85000, 7),
(8, NOW(), NOW(), 'R4_SpringRoll', 'Spring roll', 'r4_roll.jpg', 'OPENING', 60000, 8),
(9, NOW(), NOW(), 'R5_BBQSet', 'BBQ combo', 'r5_bbq.jpg', 'OPENING', 190000, 9),
(10, NOW(), NOW(), 'R5_Soda', 'Soft drink', 'r5_soda.jpg', 'OPENING', 30000, 10),
(11, NOW(), NOW(), 'R6_Lobster', 'Lobster', 'r6_lobster.jpg', 'OPENING', 450000, 11),
(12, NOW(), NOW(), 'R6_SeaSoup', 'Sea soup', 'r6_soup.jpg', 'OPENING', 120000, 12),
(13, NOW(), NOW(), 'R7_BunBoHue', 'Hue noodle', 'r7_bunbo.jpg', 'OPENING', 95000, 13),
(14, NOW(), NOW(), 'R7_LemonTea', 'Lemon tea', 'r7_tea.jpg', 'OPENING', 28000, 14),
(15, NOW(), NOW(), 'R8_GrilledFish', 'Grilled fish', 'r8_fish.jpg', 'OPENING', 210000, 15),
(16, NOW(), NOW(), 'R8_Che', 'Vietnamese dessert', 'r8_che.jpg', 'OPENING', 50000, 16);

INSERT INTO `food_description_option_group` (`food_description_id`, `food_option_group_id`) VALUES
(1, 1), (2, 1), (3, 2), (4, 2), (5, 3), (6, 3), (7, 4), (8, 4),
(9, 5), (10, 5), (11, 6), (12, 6), (13, 7), (14, 7), (15, 8), (16, 8);

-- 4) Booking times and payment source
INSERT INTO `time` (`id`, `created_at`, `updated_at`, `start_time`, `end_time`, `status`, `type`) VALUES
(1001, NOW(), NOW(), DATE_ADD(NOW(), INTERVAL 1 DAY), DATE_ADD(DATE_ADD(NOW(), INTERVAL 1 DAY), INTERVAL 2 HOUR), 'OPEN', 'BOOKING_TIME'),
(1002, NOW(), NOW(), DATE_ADD(NOW(), INTERVAL 2 DAY), DATE_ADD(DATE_ADD(NOW(), INTERVAL 2 DAY), INTERVAL 2 HOUR), 'OPEN', 'BOOKING_TIME'),
(1003, NOW(), NOW(), DATE_SUB(NOW(), INTERVAL 48 HOUR), DATE_SUB(NOW(), INTERVAL 46 HOUR), 'CLOSED', 'BOOKING_TIME'),
(1004, NOW(), NOW(), DATE_SUB(NOW(), INTERVAL 26 HOUR), DATE_SUB(NOW(), INTERVAL 24 HOUR), 'CLOSED', 'BOOKING_TIME'),
(1005, NOW(), NOW(), DATE_ADD(NOW(), INTERVAL 3 DAY), DATE_ADD(DATE_ADD(NOW(), INTERVAL 3 DAY), INTERVAL 2 HOUR), 'OPEN', 'BOOKING_TIME'),
(1006, NOW(), NOW(), DATE_ADD(NOW(), INTERVAL 4 DAY), DATE_ADD(DATE_ADD(NOW(), INTERVAL 4 DAY), INTERVAL 2 HOUR), 'OPEN', 'BOOKING_TIME'),
(1007, NOW(), NOW(), DATE_ADD(NOW(), INTERVAL 5 DAY), DATE_ADD(DATE_ADD(NOW(), INTERVAL 5 DAY), INTERVAL 2 HOUR), 'OPEN', 'BOOKING_TIME'),
(1008, NOW(), NOW(), DATE_ADD(NOW(), INTERVAL 6 DAY), DATE_ADD(DATE_ADD(NOW(), INTERVAL 6 DAY), INTERVAL 2 HOUR), 'OPEN', 'BOOKING_TIME'),
(1009, NOW(), NOW(), DATE_SUB(NOW(), INTERVAL 120 HOUR), DATE_SUB(NOW(), INTERVAL 118 HOUR), 'CLOSED', 'BOOKING_TIME'),
(1010, NOW(), NOW(), DATE_SUB(NOW(), INTERVAL 168 HOUR), DATE_SUB(NOW(), INTERVAL 166 HOUR), 'CLOSED', 'BOOKING_TIME'),
(1011, NOW(), NOW(), DATE_ADD(NOW(), INTERVAL 1 DAY), DATE_ADD(DATE_ADD(NOW(), INTERVAL 1 DAY), INTERVAL 2 HOUR), 'OPEN', 'BOOKING_TIME'),
(1012, NOW(), NOW(), DATE_ADD(NOW(), INTERVAL 2 DAY), DATE_ADD(DATE_ADD(NOW(), INTERVAL 2 DAY), INTERVAL 2 HOUR), 'OPEN', 'BOOKING_TIME'),
(1013, NOW(), NOW(), DATE_ADD(NOW(), INTERVAL 1 DAY), DATE_ADD(DATE_ADD(NOW(), INTERVAL 1 DAY), INTERVAL 2 HOUR), 'OPEN', 'BOOKING_TIME'),
(1014, NOW(), NOW(), DATE_ADD(NOW(), INTERVAL 2 DAY), DATE_ADD(DATE_ADD(NOW(), INTERVAL 2 DAY), INTERVAL 2 HOUR), 'OPEN', 'BOOKING_TIME'),
(1015, NOW(), NOW(), DATE_ADD(NOW(), INTERVAL 1 DAY), DATE_ADD(DATE_ADD(NOW(), INTERVAL 1 DAY), INTERVAL 2 HOUR), 'OPEN', 'BOOKING_TIME'),
(1016, NOW(), NOW(), DATE_ADD(NOW(), INTERVAL 2 DAY), DATE_ADD(DATE_ADD(NOW(), INTERVAL 2 DAY), INTERVAL 2 HOUR), 'OPEN', 'BOOKING_TIME'),
(1017, NOW(), NOW(), DATE_ADD(NOW(), INTERVAL 1 DAY), DATE_ADD(DATE_ADD(NOW(), INTERVAL 1 DAY), INTERVAL 2 HOUR), 'OPEN', 'BOOKING_TIME'),
(1018, NOW(), NOW(), DATE_ADD(NOW(), INTERVAL 2 DAY), DATE_ADD(DATE_ADD(NOW(), INTERVAL 2 DAY), INTERVAL 2 HOUR), 'OPEN', 'BOOKING_TIME'),
(1019, NOW(), NOW(), DATE_ADD(NOW(), INTERVAL 3 DAY), DATE_ADD(DATE_ADD(NOW(), INTERVAL 3 DAY), INTERVAL 2 HOUR), 'OPEN', 'BOOKING_TIME'),
(1020, NOW(), NOW(), DATE_ADD(NOW(), INTERVAL 4 DAY), DATE_ADD(DATE_ADD(NOW(), INTERVAL 4 DAY), INTERVAL 2 HOUR), 'OPEN', 'BOOKING_TIME'),
(1021, NOW(), NOW(), DATE_SUB(NOW(), INTERVAL 72 HOUR), DATE_SUB(NOW(), INTERVAL 70 HOUR), 'CLOSED', 'BOOKING_TIME'),
(1022, NOW(), NOW(), DATE_ADD(NOW(), INTERVAL 5 DAY), DATE_ADD(DATE_ADD(NOW(), INTERVAL 5 DAY), INTERVAL 2 HOUR), 'OPEN', 'BOOKING_TIME'),
(1023, NOW(), NOW(), DATE_SUB(NOW(), INTERVAL 90 HOUR), DATE_SUB(NOW(), INTERVAL 88 HOUR), 'CLOSED', 'BOOKING_TIME'),
(1024, NOW(), NOW(), DATE_ADD(NOW(), INTERVAL 6 DAY), DATE_ADD(DATE_ADD(NOW(), INTERVAL 6 DAY), INTERVAL 2 HOUR), 'OPEN', 'BOOKING_TIME'),
(1025, NOW(), NOW(), DATE_ADD(NOW(), INTERVAL 7 DAY), DATE_ADD(DATE_ADD(NOW(), INTERVAL 7 DAY), INTERVAL 2 HOUR), 'OPEN', 'BOOKING_TIME'),
(1026, NOW(), NOW(), DATE_ADD(NOW(), INTERVAL 8 DAY), DATE_ADD(DATE_ADD(NOW(), INTERVAL 8 DAY), INTERVAL 2 HOUR), 'OPEN', 'BOOKING_TIME');

INSERT INTO `booking_time` (`booking_time_id`, `restaurant_id`) VALUES
(1001, 1), (1002, 1), (1003, 1), (1004, 1), (1005, 1), (1006, 1), (1007, 1), (1008, 1), (1009, 1), (1010, 1),
(1011, 2), (1012, 2), (1013, 3), (1014, 4), (1015, 5), (1016, 6), (1017, 7), (1018, 8), (1019, 2), (1020, 3),
(1021, 1), (1022, 1), (1023, 2), (1024, 2), (1025, 7), (1026, 5);

INSERT INTO `payment_source` (`id`, `created_at`, `updated_at`, `type`) VALUES
(1, NOW(), NOW(), 'BOOKING'), (2, NOW(), NOW(), 'BOOKING'), (3, NOW(), NOW(), 'BOOKING'), (4, NOW(), NOW(), 'BOOKING'),
(5, NOW(), NOW(), 'BOOKING'), (6, NOW(), NOW(), 'BOOKING'), (7, NOW(), NOW(), 'BOOKING'), (8, NOW(), NOW(), 'BOOKING'),
(9, NOW(), NOW(), 'BOOKING'), (20, NOW(), NOW(), 'BOOKING'), (21, NOW(), NOW(), 'BOOKING'), (22, NOW(), NOW(), 'BOOKING'),
(23, NOW(), NOW(), 'BOOKING'), (24, NOW(), NOW(), 'BOOKING'), (25, NOW(), NOW(), 'BOOKING'), (26, NOW(), NOW(), 'BOOKING'),
(27, NOW(), NOW(), 'BOOKING'), (28, NOW(), NOW(), 'BOOKING'), (29, NOW(), NOW(), 'BOOKING'), (30, NOW(), NOW(), 'BOOKING'),
(31, NOW(), NOW(), 'BOOKING'), (32, NOW(), NOW(), 'BOOKING'), (33, NOW(), NOW(), 'BOOKING'), (34, NOW(), NOW(), 'BOOKING'),
(35, NOW(), NOW(), 'BOOKING'), (36, NOW(), NOW(), 'BOOKING'),
(10, NOW(), NOW(), 'TABLE_SESSION'), (11, NOW(), NOW(), 'TABLE_SESSION'), (12, NOW(), NOW(), 'TABLE_SESSION'),
(13, NOW(), NOW(), 'TABLE_SESSION'), (14, NOW(), NOW(), 'TABLE_SESSION'), (15, NOW(), NOW(), 'TABLE_SESSION'),
(16, NOW(), NOW(), 'TABLE_SESSION'), (17, NOW(), NOW(), 'TABLE_SESSION'),
(40, NOW(), NOW(), 'TABLE_SESSION');

INSERT INTO `restaurant_table_session` (`table_session_id`, `status`, `total`, `waiter_id`) VALUES
(10, 'PAYING', 780000, 5),
(11, 'SERVING', 0, 5),
(12, 'COMPLETED', 630000, 5),
(13, 'SERVED', 560000, 5),
(14, 'ACTIVE', 0, 5),
(15, 'SERVED', 410000, 9),
(16, 'PAYING', 300000, 9),
(17, 'COMPLETED', 520000, 13),
(40, 'COMPLETED', 630000, 5);

-- 5) Bookings and table links
INSERT INTO `booking` (`booking_id`, `number_of_people`, `note`, `deposit_amount`, `status`, `booking_user_id`, `restaurant_id`, `table_session_id`, `booking_time_id`) VALUES
(1, 4, 'Need window seat', 800000, 'AWAITING_CONFIRMATION', 3, 1, NULL, 1001),
(2, 2, 'Birthday event', 400000, 'PENDING_PAYMENT', 101, 1, NULL, 1002),
(3, 3, 'E2E waiter open session', 600000, 'CONFIRMED', 102, 1, NULL, 1005),
(4, 2, 'Completed flow customer_1', 400000, 'COMPLETED', 3, 1, 12, 1003),
(5, 5, 'No spicy', 1000000, 'REJECTED', 103, 1, NULL, 1006),
(6, 6, 'Expired unpaid', 1200000, 'EXPIRED', 104, 1, NULL, 1007),
(7, 2, 'Cancelled by customer', 400000, 'CANCELLED', 105, 1, NULL, 1008),
(8, 4, 'Payment failed sample', 800000, 'FAILED', 106, 1, NULL, 1009),
(9, 3, 'Serving now', 600000, 'SERVING', 107, 1, 11, 1004),
(20, 4, 'Cashier paying case', 800000, 'SERVED', 108, 1, 10, 1010),
(21, 3, 'Completed with review', 600000, 'COMPLETED', 109, 1, 40, 1021),
(22, 4, 'Arrived but no session', 800000, 'CUSTOMER_ARRIVED', 110, 2, NULL, 1011),
(23, 2, 'Restaurant 2 awaiting', 300000, 'AWAITING_CONFIRMATION', 111, 2, NULL, 1012),
(24, 3, 'Restaurant 2 served', 450000, 'SERVED', 112, 2, 15, 1019),
(25, 2, 'Restaurant 2 paying', 300000, 'SERVED', 113, 2, 16, 1024),
(26, 5, 'Restaurant 3 completed', 500000, 'COMPLETED', 114, 3, 17, 1013),
(27, 2, 'Restaurant 4 rejected', 0, 'REJECTED', 115, 4, NULL, 1014),
(28, 3, 'Restaurant 5 cancelled', 360000, 'CANCELLED', 116, 5, NULL, 1015),
(29, 2, 'Restaurant 6 expired', 180000, 'EXPIRED', 117, 6, NULL, 1016),
(30, 4, 'Restaurant 7 pending payment', 720000, 'PENDING_PAYMENT', 118, 7, NULL, 1017),
(31, 3, 'Restaurant 8 confirmed', 420000, 'CONFIRMED', 119, 8, NULL, 1018),
(32, 2, 'Restaurant 2 completed', 300000, 'COMPLETED', 120, 2, NULL, 1023),
(33, 2, 'Restaurant 3 failed', 200000, 'FAILED', 121, 3, NULL, 1020),
(34, 6, 'Large group pending', 1200000, 'PENDING_PAYMENT', 122, 1, NULL, 1022),
(35, 4, 'Customer arrived quickly', 800000, 'CUSTOMER_ARRIVED', 123, 7, NULL, 1025),
(36, 2, 'Suspended restaurant booking', 240000, 'AWAITING_CONFIRMATION', 124, 5, NULL, 1026);

INSERT INTO `booking_table` (`booking_id`, `table_id`) VALUES
(3, 1), (4, 2), (9, 3), (20, 2), (21, 3),
(22, 5), (24, 6), (25, 8), (26, 9), (30, 25),
(31, 29), (32, 5), (35, 27), (36, 18);

-- 6) Orders and items
INSERT INTO `food_order` (`id`, `created_at`, `updated_at`, `total_price`, `status`, `table_session_id`) VALUES
(1, NOW(), NOW(), 780000, 'COMPLETED', 10),
(2, NOW(), NOW(), 0, 'TAKING_ORDER', 11),
(3, NOW(), NOW(), 630000, 'COMPLETED', 12),
(4, NOW(), NOW(), 560000, 'COMPLETED', 13),
(5, NOW(), NOW(), 410000, 'COMPLETED', 15),
(6, NOW(), NOW(), 300000, 'COMPLETED', 16),
(7, NOW(), NOW(), 520000, 'COMPLETED', 17);

INSERT INTO `food_item` (`id`, `created_at`, `updated_at`, `quantity`, `status`, `food_order_id`, `food_description_id`, `waiter_id`) VALUES
(1, NOW(), NOW(), 1, 'SERVED', 1, 1, 5),
(2, NOW(), NOW(), 2, 'SERVED', 1, 2, 5),
(3, NOW(), NOW(), 1, 'PENDING', 2, 1, 5),
(4, NOW(), NOW(), 1, 'SERVED', 3, 1, 5),
(5, NOW(), NOW(), 1, 'SERVED', 3, 2, 5),
(6, NOW(), NOW(), 1, 'SERVED', 4, 1, 5),
(7, NOW(), NOW(), 1, 'SERVED', 5, 3, 9),
(8, NOW(), NOW(), 2, 'SERVED', 5, 4, 9),
(9, NOW(), NOW(), 1, 'SERVED', 6, 3, 9),
(10, NOW(), NOW(), 1, 'SERVED', 7, 5, 13),
(11, NOW(), NOW(), 1, 'CANCELLED', 7, 6, 13);

INSERT INTO `food_item_option` (`food_item_id`, `food_option_id`) VALUES
(1, 2), (2, 2), (4, 1), (6, 1), (7, 4), (8, 3), (10, 6);

-- 7) Transactions and payments
INSERT INTO `transaction` (`id`, `created_at`, `updated_at`, `amount`, `url_payment`, `transaction_type`, `transaction_status`, `cashier_id`, `payment_source_id`) VALUES
(1, DATE_SUB(NOW(), INTERVAL 7 DAY), DATE_SUB(NOW(), INTERVAL 7 DAY), 400000, NULL, 'DEPOSIT', 'CAPTURED', NULL, 4),
(2, DATE_SUB(NOW(), INTERVAL 7 DAY), DATE_SUB(NOW(), INTERVAL 7 DAY), 230000, NULL, 'FINAL_PAYMENT', 'CAPTURED', 6, 4),
(3, DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY), 600000, NULL, 'DEPOSIT', 'CAPTURED', NULL, 9),
(4, DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY), 0, NULL, 'FINAL_PAYMENT', 'CAPTURED', 6, 9),
(5, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), 800000, NULL, 'DEPOSIT', 'CAPTURED', NULL, 20),
(6, DATE_SUB(NOW(), INTERVAL 2 HOUR), DATE_SUB(NOW(), INTERVAL 2 HOUR), 0, NULL, 'FINAL_PAYMENT', 'PENDING', NULL, 20),
(7, DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY), 600000, NULL, 'DEPOSIT', 'CAPTURED', NULL, 21),
(8, DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY), 30000, NULL, 'FINAL_PAYMENT', 'CAPTURED', 6, 21),
(9, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY), 300000, NULL, 'DEPOSIT', 'AUTHORIZED', NULL, 24),
(10, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY), 110000, NULL, 'FINAL_PAYMENT', 'CAPTURED', 36, 24),
(11, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY), 300000, NULL, 'DEPOSIT', 'CAPTURED', NULL, 25),
(12, DATE_SUB(NOW(), INTERVAL 4 HOUR), DATE_SUB(NOW(), INTERVAL 4 HOUR), 50000, NULL, 'FINAL_PAYMENT', 'PENDING', NULL, 25),
(13, DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY), 500000, NULL, 'DEPOSIT', 'CAPTURED', NULL, 26),
(14, DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY), 20000, NULL, 'FINAL_PAYMENT', 'CAPTURED', 14, 26),
(15, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), 300000, NULL, 'DEPOSIT', 'FAILED', NULL, 2),
(16, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), 720000, NULL, 'DEPOSIT', 'EXPIRED', NULL, 30),
(17, DATE_SUB(NOW(), INTERVAL 6 HOUR), DATE_SUB(NOW(), INTERVAL 6 HOUR), 1200000, NULL, 'DEPOSIT', 'PENDING', NULL, 34),
(18, DATE_SUB(NOW(), INTERVAL 8 DAY), DATE_SUB(NOW(), INTERVAL 8 DAY), 300000, NULL, 'DEPOSIT', 'CAPTURED', NULL, 32),
(19, DATE_SUB(NOW(), INTERVAL 8 DAY), DATE_SUB(NOW(), INTERVAL 8 DAY), 90000, NULL, 'FINAL_PAYMENT', 'CAPTURED', 36, 32),
(20, DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 10 DAY), 200000, NULL, 'DEPOSIT', 'CANCELLED', NULL, 33);

INSERT INTO `payment` (`id`, `created_at`, `updated_at`, `price`, `payment_type`, `payment_status`, `payment_method`, `transaction_id`) VALUES
(1, DATE_SUB(NOW(), INTERVAL 7 DAY), DATE_SUB(NOW(), INTERVAL 7 DAY), 400000, 'PAYMENT', 'SUCCESS', 'CREDIT_CARD', 1),
(2, DATE_SUB(NOW(), INTERVAL 7 DAY), DATE_SUB(NOW(), INTERVAL 7 DAY), 230000, 'PAYMENT', 'SUCCESS', 'CASH', 2),
(3, DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY), 600000, 'PAYMENT', 'SUCCESS', 'CREDIT_CARD', 3),
(4, DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY), 0, 'PAYMENT', 'SUCCESS', 'CASH', 4),
(5, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), 800000, 'PAYMENT', 'SUCCESS', 'CREDIT_CARD', 5),
(6, DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY), 600000, 'PAYMENT', 'SUCCESS', 'CREDIT_CARD', 7),
(7, DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY), 30000, 'PAYMENT', 'SUCCESS', 'CASH', 8),
(8, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY), 110000, 'PAYMENT', 'SUCCESS', 'CASH', 10),
(9, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY), 300000, 'PAYMENT', 'SUCCESS', 'CREDIT_CARD', 11),
(10, DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY), 500000, 'PAYMENT', 'SUCCESS', 'CREDIT_CARD', 13),
(11, DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY), 20000, 'PAYMENT', 'SUCCESS', 'CASH', 14),
(12, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), 300000, 'PAYMENT', 'FAILED', 'CREDIT_CARD', 15),
(13, DATE_SUB(NOW(), INTERVAL 8 DAY), DATE_SUB(NOW(), INTERVAL 8 DAY), 300000, 'PAYMENT', 'SUCCESS', 'CREDIT_CARD', 18),
(14, DATE_SUB(NOW(), INTERVAL 8 DAY), DATE_SUB(NOW(), INTERVAL 8 DAY), 90000, 'PAYMENT', 'SUCCESS', 'CASH', 19),
(15, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), 150000, 'REFUND', 'SUCCESS', 'CASH', 10);

-- 8) Reviews, notifications, read receipts
INSERT INTO `review` (`id`, `created_at`, `updated_at`, `rating`, `comment`, `restaurant_id`, `user_id`, `booking_id`) VALUES
(1, NOW(), NOW(), 5, 'R1 excellent service', 1, 3, 4),
(2, NOW(), NOW(), 4, 'R1 good hotpot', 1, 109, 21),
(3, NOW(), NOW(), 4, 'R2 stable quality', 2, 120, 32),
(4, NOW(), NOW(), 5, 'R3 great sushi', 3, 114, 26);

INSERT INTO `notifications` (`id`, `user_id`, `type`, `title`, `content`, `is_read`, `metadata`, `created_at`) VALUES
(1, 3, 'BOOKING_CONFIRMED', 'Booking confirmed', 'Booking #3 has been confirmed.', 0, '{"bookingId":3}', NOW()),
(2, 3, 'PAYMENT_REMINDER', 'Pending payment', 'Please complete payment for booking #2.', 0, '{"bookingId":2}', NOW()),
(3, 108, 'PAYMENT_SUCCESS', 'Deposit success', 'Deposit for booking #20 is successful.', 1, '{"bookingId":20}', NOW()),
(4, NULL, 'SYSTEM_ALERT', 'Maintenance notice', 'System maintenance at 02:00 AM.', 0, '{"scope":"all"}', NOW()),
(5, 1, 'NEW_BOOKING_REQUEST', 'Pending approvals', 'There are pending restaurants and bookings.', 0, '{"restaurantIds":[3,4,5]}', NOW());

INSERT INTO `user_read_notification` (`id`, `user_id`, `notification_id`, `read_at`) VALUES
(1, 3, 3, NOW()),
(2, 1, 5, NOW()),
(3, 108, 4, NOW());

SET FOREIGN_KEY_CHECKS = 1;
