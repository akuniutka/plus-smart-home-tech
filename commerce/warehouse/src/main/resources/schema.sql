CREATE SCHEMA IF NOT EXISTS warehouse;

CREATE TABLE IF NOT EXISTS warehouse.products
(
  product_id UUID PRIMARY KEY,
  fragile BOOLEAN,
  width NUMERIC(19, 3),
  height NUMERIC(19, 3),
  depth NUMERIC(19, 3),
  weight NUMERIC(19, 3),
  total_quantity BIGINT,
  booked_quantity BIGINT
);
