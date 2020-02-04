create user 'gl'@'localhost' identified by 'gl';
GRANT ALL PRIVILEGES ON * . * TO 'gl'@'localhost';
create database gl DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;
use gl;
set sql_mode = "ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION";

create table account_attribute (
    id              bigint      not null AUTO_INCREMENT,
    uuid            varchar(36) not null unique,
    account_number  varchar(30),
    iban            varchar(34),
    data            blob,

    primary key (id)
) comment "Accounts attributes";

