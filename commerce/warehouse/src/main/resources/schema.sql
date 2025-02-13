CREATE SCHEMA IF NOT EXISTS warehouse;

CREATE TABLE IF NOT EXISTS warehouse.products
(
  product_id UUID PRIMARY KEY,
  fragile    BOOLEAN,
  width      NUMERIC(19, 3),
  height     NUMERIC(19, 3),
  depth      NUMERIC(19, 3),
  weight     NUMERIC(19, 3),
  quantity   BIGINT
);

CREATE TABLE IF NOT EXISTS warehouse.order_bookings
(
  order_booking_id UUID PRIMARY KEY,
  order_id         UUID,
  delivery_id      UUID
);

CREATE TABLE IF NOT EXISTS warehouse.order_booking_items
(
  order_booking_id UUID,
  product_id       UUID,
  quantity         BIGINT
);
