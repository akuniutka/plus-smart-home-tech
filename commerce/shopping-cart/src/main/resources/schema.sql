CREATE SCHEMA IF NOT EXISTS cart;

CREATE TABLE IF NOT EXISTS cart.shopping_carts
(
  shopping_cart_id UUID PRIMARY KEY,
  username VARCHAR,
  shopping_cart_state VARCHAR
);

CREATE TABLE IF NOT EXISTS cart.shopping_cart_items
(
  shopping_cart_id UUID,
  product_id UUID,
  quantity BIGINT
);
