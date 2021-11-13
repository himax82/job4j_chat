create table role (
    id        serial primary key,
    name varchar(200) not null unique
);

create table room (
    id   serial primary key,
    name varchar(2000)
);

create table person (
    id       serial primary key,
    name     varchar(200) not null unique,
    password varchar(200) not null,
    role_id  int          not null references role (id)
);

create table message (
    id        serial primary key,
    text      text                        not null,
    created   timestamp without time zone not null default now(),
    person_id int                         not null references person (id),
    room_id   int                         not null references room (id)
);

insert into role (name)
values ('ROLE_USER');
insert into role (name)
values ('ROLE_ADMIN');

insert into room (name)
values ('Общая');
insert into room (name)
values ('Групповая');
insert into room (name)
values ('Приватная');

insert into person (name, password, role_id)
values ('admin', 'qwerty', (select id from role where name = 'ROLE_ADMIN'));

insert into person (name, password, role_id)
values ('user', '12345', (select id from role where name = 'ROLE_USER'));

insert into message (text, person_id, room_id)
values ('Добро пожаловать в общую комнату', 1, 1);

insert into message (text, person_id, room_id)
values ('Добро пожаловать в групповой чат', 1, 2);

insert into message (text, person_id, room_id)
values ('Добро пожаловать в приватный чат', 1, 3);