create table users
(
    id             uuid         not null,
    username       varchar(255) not null,
    steam_id       varchar(255) not null,
    hours_played   int          not null,
    rating_elo     int          not null,
    faceit_winrate int          not null,
    created_at     timestamp    not null,
    constraint pk_users primary key (id)
);

alter table users
    add constraint uc_users_username unique (username);