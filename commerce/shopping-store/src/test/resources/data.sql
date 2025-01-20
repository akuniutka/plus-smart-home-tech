INSERT INTO store.products (
                            product_id,
                            product_name,
                            description,
                            image_src,
                            quantity_state,
                            product_state,
                            rating,
                            product_category,
                            price)
VALUES
(
  '25182563-067b-441c-b11d-9ad1fb249e25',
  'light sensor v.1',
  'A modern energy efficient light sensor',
  '/sensors/light/light-sensor-v1.jpg',
  'ENOUGH',
  'ACTIVE',
  5.0,
  'SENSORS',
  9.99
),
(
 '0112f4d1-4940-4cd5-84ed-e7d44f683808',
 'lighting device v.2',
 'A new version of smart lighting device',
 '/lighting/lighting-device-v2.jpg',
 'FEW',
 'DEACTIVATE',
 3.0,
 'LIGHTING',
 4.99
),
(
  '0a53f38d-dd00-4f80-9b2e-c9d17ee46385',
  'light sensor (alpha)',
  'A prototype of light sensor',
  '/sensors/light/light-sensor-alpha.jpg',
  'FEW',
  'ACTIVE',
  3.0,
  'SENSORS',
  3.99
),
(
  '1fe4c399-4fb9-4893-a0d4-536d255adc3c',
  'light sensor (beta)',
  'A prototype of energy efficient light sensor',
  '/sensors/light/light-sensor-beta.jpg',
  'FEW',
  'ACTIVE',
  4.0,
  'SENSORS',
  5.99
);