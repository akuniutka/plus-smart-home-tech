CREATE SCHEMA IF NOT EXISTS payment;

CREATE TABLE IF NOT EXISTS payment.payments
(
  payment_id     UUID PRIMARY KEY,
  order_id       UUID,
  total_payment  NUMERIC(19, 2),
  product_total  NUMERIC(19, 2),
  delivery_total NUMERIC(19, 2),
  fee_total      NUMERIC(19, 2),
  state          VARCHAR
);
