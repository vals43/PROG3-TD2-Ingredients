create table if not exists "order" (
    id                serial primary key,
    reference         varchar(255) not null,
    creation_datetime timestamp default now()
);

create table if not exists dish_order (
    id       serial primary key,
    id_order int,
    id_dish  int,
    quantity int,
    foreign key (id_order) references "order" (id),
    foreign key (id_dish) references dish (id)
);


--- Migration pour l'examen ---
create type order_type as enum ('EAT_IN', 'TAKE_AWAY');
create type order_status as enum ('CREATED', 'READY', 'DELIVERED');

alter table "order"
    add column type order_type default 'EAT_IN',
add column status order_status default 'CREATED';
