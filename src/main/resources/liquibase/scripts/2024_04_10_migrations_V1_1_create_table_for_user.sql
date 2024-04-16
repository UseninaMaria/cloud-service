create table users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(255) NOT NULL);

insert into users (id, username, password)
values (1, 'Ivan', '123456789qwerty');
insert into users (id, username, password)
values (2, 'Sergey', '987654321ytrewq');
insert into users (id, username, password)
values (2, 'Oly', '1029384756qywter');

