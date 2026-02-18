DELETE FROM "order";
DELETE FROM "dish_order";


INSERT INTO "order" (id ,reference , type, status)
VALUES
    (1,'ORD100', 'TAKE_AWAY'::order_type, 'DELIVERED'::order_status),
    (2,'ORD102', 'EAT_IN'::order_type, 'CREATED'::order_status);

