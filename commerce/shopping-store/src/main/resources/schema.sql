CREATE SCHEMA IF NOT EXISTS store;

CREATE TABLE IF NOT EXISTS store.products
(
  product_id UUID PRIMARY KEY,
  product_name VARCHAR,
  description VARCHAR,
  image_src VARCHAR,
  quantity_state VARCHAR,
  product_state VARCHAR,
  product_category VARCHAR,
  price NUMERIC(19, 2)
);