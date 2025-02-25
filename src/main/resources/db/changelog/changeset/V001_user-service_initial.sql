CREATE TABLE users (
                       id                 UUID         NOT NULL,
                       steam_username     VARCHAR(255) NOT NULL,
                       steam_id           VARCHAR(255) NOT NULL,
                       rating_elo         INT          NOT NULL,
                       avatar_image_link  VARCHAR(255) NOT NULL,
                       steam_profile_link VARCHAR(255) NOT NULL,
                       created_at         TIMESTAMP    NOT NULL,
                       role               VARCHAR(255) NOT NULL,
                       CONSTRAINT pk_users PRIMARY KEY (id)
);