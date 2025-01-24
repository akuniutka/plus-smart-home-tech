INSERT INTO cart.shopping_carts (shopping_cart_id,
                                 username,
                                 shopping_cart_state)
VALUES
(
  '801b5a89-c5f1-435c-a54e-d06cd6662a6a',
  'alice',
  'ACTIVE'
),
(
  '08e18b95-5916-484c-a239-70154e941b8a',
  'charlie',
  'ACTIVE'
);

INSERT INTO cart.shopping_cart_items (shopping_cart_id,
                                      product_id,
                                      quantity)
VALUES
(
  '801b5a89-c5f1-435c-a54e-d06cd6662a6a',
  '25182563-067b-441c-b11d-9ad1fb249e25',
  1
),
(
  '801b5a89-c5f1-435c-a54e-d06cd6662a6a',
  '0112f4d1-4940-4cd5-84ed-e7d44f683808',
  2
),
(
  '801b5a89-c5f1-435c-a54e-d06cd6662a6a',
  '0a53f38d-dd00-4f80-9b2e-c9d17ee46385',
  4
);
