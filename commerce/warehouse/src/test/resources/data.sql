INSERT INTO warehouse.products (product_id,
                                fragile,
                                width,
                                height,
                                depth,
                                weight)
VALUES
(
  '25182563-067b-441c-b11d-9ad1fb249e25',
  true,
  1.000,
  2.000,
  3.000,
  4.000
),
(
  '0112f4d1-4940-4cd5-84ed-e7d44f683808',
  false,
  2.000,
  3.000,
  4.000,
  5.000
);

INSERT INTO warehouse.stocks (product_id,
                              total_quantity,
                              booked_quantity)
VALUES
(
  '25182563-067b-441c-b11d-9ad1fb249e25',
  10,
  5
),
(
  '0112f4d1-4940-4cd5-84ed-e7d44f683808',
  20,
  15
);
