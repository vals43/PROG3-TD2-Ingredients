SELECT
    id_ingredient,
    unit,
    SUM(
            CASE
                WHEN type = 'OUT' THEN quantity * -1
                ELSE quantity
                END
    ) AS actual_quantity
FROM stockmovement
WHERE id_ingredient = 1
  AND creation_datetime <= '2026-02-01 00:00:00'
GROUP BY id_ingredient, unit;



SELECT
    SUM(i.price * di.quantity_required) AS dish_cost
FROM dishingredient as di
         JOIN ingredient i ON di.id_ingredient = i.id
WHERE di.id_dish = 1 ;

