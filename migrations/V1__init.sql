-- Initial schema snapshot (not auto-applied; for reference or manual Flyway use).
-- Tables match JPA entities.
CREATE TABLE IF NOT EXISTS service_type (
  service_type_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS users (
  user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  password VARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL UNIQUE,
  username VARCHAR(255) NOT NULL UNIQUE,
  name VARCHAR(255) NOT NULL,
  phone VARCHAR(255),
  role VARCHAR(50) NOT NULL,
  created_at TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6)
);

CREATE TABLE IF NOT EXISTS store (
  store_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  service_type_id BIGINT,
  name VARCHAR(255) NOT NULL,
  created_at TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6),
  INDEX idx_store_service_type_id(service_type_id),
  CONSTRAINT fk_store_service_type FOREIGN KEY (service_type_id) REFERENCES service_type(service_type_id)
);

CREATE TABLE IF NOT EXISTS menu_category (
  menu_category_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  service_type_id BIGINT,
  name VARCHAR(255) NOT NULL,
  INDEX idx_menu_category_service_type_id(service_type_id),
  CONSTRAINT fk_menu_category_service_type FOREIGN KEY (service_type_id) REFERENCES service_type(service_type_id)
);

CREATE TABLE IF NOT EXISTS menu (
  menu_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  store_id BIGINT,
  category_id BIGINT,
  name VARCHAR(255) NOT NULL,
  price INT NOT NULL,
  description TEXT,
  INDEX idx_menu_store_id(store_id),
  INDEX idx_menu_category_id(category_id),
  CONSTRAINT fk_menu_store FOREIGN KEY (store_id) REFERENCES store(store_id),
  CONSTRAINT fk_menu_category FOREIGN KEY (category_id) REFERENCES menu_category(menu_category_id)
);

CREATE TABLE IF NOT EXISTS cart (
  cart_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT,
  created_at TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6),
  CONSTRAINT fk_cart_user FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE IF NOT EXISTS cart_item (
  cart_item_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  cart_id BIGINT,
  menu_id BIGINT,
  quantity INT NOT NULL,
  created_at TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6),
  CONSTRAINT uk_cart_item_cart_menu UNIQUE(cart_id, menu_id),
  CONSTRAINT fk_cart_item_cart FOREIGN KEY (cart_id) REFERENCES cart(cart_id),
  CONSTRAINT fk_cart_item_menu FOREIGN KEY (menu_id) REFERENCES menu(menu_id)
);

CREATE TABLE IF NOT EXISTS orders (
  order_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT,
  store_id BIGINT,
  status VARCHAR(50),
  total_price INT NOT NULL,
  created_at TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6),
  INDEX idx_orders_user_id(user_id),
  INDEX idx_orders_store_id(store_id),
  CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES users(user_id),
  CONSTRAINT fk_orders_store FOREIGN KEY (store_id) REFERENCES store(store_id)
);

CREATE TABLE IF NOT EXISTS order_items (
  order_item_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  order_id BIGINT,
  menu_id BIGINT,
  quantity INT NOT NULL,
  unit_price INT NOT NULL,
  INDEX idx_order_items_order_id(order_id),
  INDEX idx_order_items_menu_id(menu_id),
  CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders(order_id),
  CONSTRAINT fk_order_items_menu FOREIGN KEY (menu_id) REFERENCES menu(menu_id)
);

CREATE TABLE IF NOT EXISTS payments (
  payments_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT,
  order_id BIGINT,
  method INT NOT NULL,
  amount INT NOT NULL,
  created_at TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6),
  INDEX idx_payments_user_id(user_id),
  INDEX idx_payments_order_id(order_id),
  CONSTRAINT fk_payments_user FOREIGN KEY (user_id) REFERENCES users(user_id),
  CONSTRAINT fk_payments_order FOREIGN KEY (order_id) REFERENCES orders(order_id)
);

CREATE TABLE IF NOT EXISTS notifications (
  noti_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT,
  title TEXT NOT NULL,
  body TEXT NOT NULL,
  created_at TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6),
  CONSTRAINT fk_notifications_user FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE IF NOT EXISTS coupon (
  coupon_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  discount_type ENUM('AMOUNT','PERCENT'),
  discount_value INT NOT NULL,
  valid_from TIMESTAMP(6),
  valid_to TIMESTAMP(6)
);
