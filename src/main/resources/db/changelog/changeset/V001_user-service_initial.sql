create table users
(
    id       uuid         not null,
    username varchar(255) not null,
    password varchar(255) not null,
    email    varchar(255) not null,
    constraint pk_users primary key (id)
);

alter table users
    add constraint uc_users_email unique (email);

alter table users
    add constraint uc_users_username unique (username);