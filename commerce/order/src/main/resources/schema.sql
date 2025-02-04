CREATE SCHEMA IF NOT EXISTS order_;

CREATE TABLE IF NOT EXISTS order_.orders
(
  order_id         UUID PRIMARY KEY,
  username         VARCHAR,
  shopping_cart_id UUID,
  payment_id       UUID,
  delivery_id      UUID,
  state            VARCHAR,
  country          VARCHAR,
  city             VARCHAR,
  street           VARCHAR,
  house            VARCHAR,
  flat             VARCHAR,
  delivery_weight  NUMERIC(19, 3),
  delivery_volume  NUMERIC(19, 3),
  fragile          BOOLEAN,
  total_price      NUMERIC(19, 2),
  delivery_price   NUMERIC(19, 2),
  product_price    NUMERIC(19, 2)
);

CREATE TABLE IF NOT EXISTS order_.order_items
(
  order_id   UUID,
  product_id UUID,
  quantity   BIGINT
);
