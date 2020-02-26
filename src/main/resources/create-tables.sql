drop table uah_account_attribute;
create table uah_account_attribute (
    id              bigint      not null AUTO_INCREMENT,
    uuid            varchar(36) not null unique,
    account_number  varchar(30),
    iban            varchar(34),
    closed_at       datetime,
    data            text,

    primary key (id)
) comment "Accounts attributes";

drop table uah_current_page;
create table uah_current_page (
    id              bigint      not null,
    data            text,

    primary key (id)
) comment "Accounts current pages";

drop table uah_historical_page;
create table uah_historical_page (
    id              bigint      not null AUTO_INCREMENT,
    account_id      bigint      not null,
    started_at      datetime    not null,
    finished_at     datetime    not null,
    rep_started_on  date        not null,
    rep_finished_on date        not null,
    data            text,

    primary key (id)
) comment "Accounts historical pages";

drop table uah_reservation;
create table uah_reservation (
    id              bigint      not null AUTO_INCREMENT,
    uuid            varchar(36) not null,
    expire_at       datetime    not null,
    closed_at       datetime,
    reason          varchar(10),
    data            text,

    primary key (id)
) comment "Reservations";

/*
USD
*/
drop table usd_account_attribute;
create table usd_account_attribute (
id              bigint      not null AUTO_INCREMENT,
uuid            varchar(36) not null unique,
account_number  varchar(30),
iban            varchar(34),
closed_at       datetime,
data            text,

primary key (id)
) comment "Accounts attributes";

drop table usd_current_page;
create table usd_current_page (
id              bigint      not null,
data            text,

primary key (id)
) comment "Accounts current pages";

drop table usd_historical_page;
create table usd_historical_page (
id              bigint      not null AUTO_INCREMENT,
account_id      bigint      not null,
started_at      datetime    not null,
finished_at     datetime    not null,
rep_started_on  date        not null,
rep_finished_on date        not null,
data            text,

primary key (id)
) comment "Accounts historical pages";

drop table usd_reservation;
create table usd_reservation (
id              bigint      not null AUTO_INCREMENT,
uuid            varchar(36) not null,
expire_at       datetime    not null,
closed_at       datetime,
reason          varchar(10),
data            text,

primary key (id)
) comment "Reservations";

/*
EUR
*/
drop table eur_account_attribute;
create table eur_account_attribute (
id              bigint      not null AUTO_INCREMENT,
uuid            varchar(36) not null unique,
account_number  varchar(30),
iban            varchar(34),
closed_at       datetime,
data            text,

primary key (id)
) comment "Accounts attributes";

drop table eur_current_page;
create table eur_current_page (
id              bigint      not null,
data            text,

primary key (id)
) comment "Accounts current pages";

drop table eur_historical_page;
create table eur_historical_page (
id              bigint      not null AUTO_INCREMENT,
account_id      bigint      not null,
started_at      datetime    not null,
finished_at     datetime    not null,
rep_started_on  date        not null,
rep_finished_on date        not null,
data            text,

primary key (id)
) comment "Accounts historical pages";

drop table eur_reservation;
create table eur_reservation (
id              bigint      not null AUTO_INCREMENT,
uuid            varchar(36) not null,
expire_at       datetime    not null,
closed_at       datetime,
reason          varchar(10),
data            text,

primary key (id)
) comment "Reservations";
