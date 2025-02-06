CREATE SCHEMA IF NOT EXISTS delivery;

CREATE TABLE IF NOT EXISTS delivery.deliveries
(
  delivery_id    UUID PRIMARY KEY,
  country_from   VARCHAR,
  city_from      VARCHAR,
  street_from    VARCHAR,
  house_from     VARCHAR,
  flat_from      VARCHAR,
  country_to     VARCHAR,
  city_to        VARCHAR,
  street_to      VARCHAR,
  house_to       VARCHAR,
  flat_to        VARCHAR,
  order_id       UUID,
  delivery_state VARCHAR
);
