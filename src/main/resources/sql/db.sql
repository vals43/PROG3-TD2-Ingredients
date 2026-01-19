create database "mini_dish_db";

create user "mini_dish_db_manager" with password '123456';

-- Grant all privileges

GRANT ALL ON SCHEMA public TO mini_dish_db_manager;

GRANT ALL PRIVILEGES ON DATABASE mini_dish_db TO mini_dish_db_manager;

GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO mini_dish_db_manager;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO mini_dish_db_manager;

ALTER TABLE ingredient OWNER TO mini_dish_db_manager;
ALTER TABLE dish OWNER TO mini_dish_db_manager;

ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL PRIVILEGES ON TABLES TO mini_dish_db_manager;