-- Seed data for MySQL (>=8.0). Generates 200+ sample rows across core tables.
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE cart_item;
TRUNCATE TABLE cart;
TRUNCATE TABLE order_items;
TRUNCATE TABLE orders;
TRUNCATE TABLE payments;
TRUNCATE TABLE menu;
TRUNCATE TABLE menu_category;
TRUNCATE TABLE store;
TRUNCATE TABLE service_type;
TRUNCATE TABLE notifications;
TRUNCATE TABLE coupon;
TRUNCATE TABLE users;
SET FOREIGN_KEY_CHECKS = 1;

-- Service types
INSERT INTO service_type(name) VALUES ('Campus'), ('Cafe'), ('FoodCourt');

-- Admin
INSERT INTO users(password, email, username, name, phone, role, created_at)
VALUES ('adminpass', 'admin@example.com', 'admin', 'Admin', '010-9999-0000', 'ADMIN', NOW());

-- 50 users
WITH RECURSIVE seq AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM seq WHERE n < 50
)
INSERT INTO users(password, email, username, name, phone, role, created_at)
SELECT 'password',
       CONCAT('user', n, '@example.com'),
       CONCAT('user', n),
       CONCAT('User ', n),
       CONCAT('010-0000-', LPAD(n, 4, '0')),
       'USER',
       NOW()
FROM seq;

-- 10 stores
WITH RECURSIVE seq AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM seq WHERE n < 10
)
INSERT INTO store(name, service_type_id, created_at)
SELECT CONCAT('Store ', n),
       ((n - 1) % 3) + 1,
       NOW()
FROM seq;

-- 10 menu categories
WITH RECURSIVE seq AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM seq WHERE n < 10
)
INSERT INTO menu_category(name, service_type_id)
SELECT CONCAT('Category ', n),
       ((n - 1) % 3) + 1
FROM seq;

-- 60 menus
WITH RECURSIVE seq AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM seq WHERE n < 60
)
INSERT INTO menu(name, price, description, store_id, category_id)
SELECT CONCAT('Menu ', n),
       1000 + (n * 10),
       CONCAT('Description ', n),
       ((n - 1) % 10) + 1,
       ((n - 1) % 10) + 1
FROM seq;

-- 50 carts
WITH RECURSIVE seq AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM seq WHERE n < 50
)
INSERT INTO cart(user_id, created_at)
SELECT n, NOW() FROM seq;

-- 100 cart items (unique cart/menu pairs)
WITH RECURSIVE seq AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM seq WHERE n < 100
)
INSERT INTO cart_item(cart_id, menu_id, quantity, created_at)
SELECT ((n - 1) % 50) + 1,
       ((n - 1) % 60) + 1,
       ((n - 1) % 3) + 1,
       NOW()
FROM seq;

-- 40 orders
WITH RECURSIVE seq AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM seq WHERE n < 40
)
INSERT INTO orders(user_id, store_id, status, total_price, created_at)
SELECT ((n - 1) % 50) + 1,
       ((n - 1) % 10) + 1,
       'PAYMENT_COMPLETE',
       5000 + n,
       NOW()
FROM seq;

-- 80 order items
WITH RECURSIVE seq AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM seq WHERE n < 80
)
INSERT INTO order_items(order_id, menu_id, quantity, unit_price)
SELECT ((n - 1) % 40) + 1,
       ((n - 1) % 60) + 1,
       ((n - 1) % 3) + 1,
       1000 + (n * 5)
FROM seq;

-- 40 payments
WITH RECURSIVE seq AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM seq WHERE n < 40
)
INSERT INTO payments(user_id, order_id, method, amount, created_at)
SELECT ((n - 1) % 50) + 1,
       ((n - 1) % 40) + 1,
       1,
       5000 + n,
       NOW()
FROM seq;

-- 40 notifications
WITH RECURSIVE seq AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM seq WHERE n < 40
)
INSERT INTO notifications(user_id, title, body, created_at)
SELECT ((n - 1) % 50) + 1,
       CONCAT('Notice ', n),
       CONCAT('Hello user ', n),
       NOW()
FROM seq;

-- 5 coupons
INSERT INTO coupon(name, discount_type, discount_value, valid_from, valid_to)
VALUES ('WELCOME10', 'PERCENT', 10, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY)),
       ('SALE20', 'PERCENT', 20, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY)),
       ('FIX1000', 'AMOUNT', 1000, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY)),
       ('VIP15', 'PERCENT', 15, NOW(), DATE_ADD(NOW(), INTERVAL 60 DAY)),
       ('HOT500', 'AMOUNT', 500, NOW(), DATE_ADD(NOW(), INTERVAL 15 DAY));
