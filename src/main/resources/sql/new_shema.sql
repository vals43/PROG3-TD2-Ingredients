create type dish_type as enum ('STARTER', 'MAIN', 'DESSERT');
create type unit_type as enum ('PCS', 'KG', 'L');
create type ingredient_category as enum ('VEGETABLE', 'ANIMAL', 'MARINE', 'DAIRY', 'OTHER');
CREATE TABLE Dish (
                      id SERIAL PRIMARY KEY,
                      name VARCHAR(100) NOT NULL,
                      dish_type dish_type,
                      price NUMERIC NULL
);

CREATE TABLE Ingredient (
                            id SERIAL PRIMARY KEY,
                            name VARCHAR(100) NOT NULL UNIQUE,
                            price NUMERIC NOT NULL,
                            category ingredient_category
);

CREATE TABLE DishIngredient (
                                id SERIAL NOT NULL ,
                                id_dish INT NOT NULL,
                                id_ingredient INT NOT NULL,
                                quantity_required DOUBLE PRECISION NOT NULL,
                                unit unit_type,
                                PRIMARY KEY (id_dish, id_ingredient),
                                CONSTRAINT fk_dish
                                    FOREIGN KEY (id_dish) REFERENCES Dish(id),
                                CONSTRAINT fk_ingredient
                                    FOREIGN KEY (id_ingredient) REFERENCES Ingredient(id)
);
