insert into dish (id, name, dish_type)
values (1, 'Salaide fraîche', 'STARTER'),
       (2, 'Poulet grillé', 'MAIN'),
       (3, 'Riz aux légumes', 'MAIN'),
       (4, 'Gâteau au chocolat ', 'DESSERT'),
       (5, 'Salade de fruits', 'DESSERT');


insert into ingredient (id, name, category, price, id_dish)
values (1, 'Laitue', 'VEGETABLE', 800.0, 1),
       (2, 'Tomate', 'VEGETABLE', 600.0, 1),
       (3, 'Poulet', 'ANIMAL', 4500.0, 2),
       (4, 'Chocolat ', 'OTHER', 3000.0, 4),
       (5, 'Beurre', 'DAIRY', 2500.0, 4);



update dish
set price = 2000.0
where id = 1;

update dish
set price = 6000.0
where id = 2;



INSERT INTO DishIngredient (id, id_dish, id_ingredient, quantity_required, unit)
VALUES
    (1,1, 1, 0.20, 'KG'),
    (2,1, 2, 0.15, 'KG'),
    (3,2, 3, 1.0, 'KG'),
    (4,4, 4, 0.3, 'KG'),
    (5,4, 5, 0.2, 'KG');


UPDATE Dish SET selling_price = 3500.00 WHERE id = 1;
UPDATE Dish SET selling_price = 12000.00 WHERE id = 2;
UPDATE Dish SET selling_price = NULL    WHERE id = 3;
UPDATE Dish SET selling_price = 8000.00 WHERE id = 4;
UPDATE Dish SET selling_price = NULL    WHERE id = 5;
