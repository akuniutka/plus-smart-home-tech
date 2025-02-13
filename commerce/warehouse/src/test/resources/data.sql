INSERT INTO warehouse.products (product_id,
                                fragile,
                                width,
                                height,
                                depth,
                                weight,
                                quantity)
VALUES ('25182563-067b-441c-b11d-9ad1fb249e25',
        true,
        1.000,
        2.000,
        3.000,
        4.000,
        10),
       ('0112f4d1-4940-4cd5-84ed-e7d44f683808',
        false,
        2.000,
        3.000,
        4.000,
        5.000,
        20);

INSERT INTO warehouse.order_bookings (order_booking_id, order_id, delivery_id)
VALUES ('3c05ad1d-6d62-4af5-b3b7-ab0aefadceb4',
        'f8991fc5-1c29-4395-b781-7717893cea92',
        '1c44948b-78ca-48a5-80e5-6a901af8c117');

INSERT INTO warehouse.order_booking_items(order_booking_id, product_id, quantity)
VALUES ('3c05ad1d-6d62-4af5-b3b7-ab0aefadceb4',
        '25182563-067b-441c-b11d-9ad1fb249e25',
        1),
       ('3c05ad1d-6d62-4af5-b3b7-ab0aefadceb4',
        '0112f4d1-4940-4cd5-84ed-e7d44f683808',
        2)
