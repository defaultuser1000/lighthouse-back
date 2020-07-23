--liquibase formatted sql
--changeset zakrzhevskiy-as:20200723-02
insert into currencies (id, creation_date, modification_date, code, symbol)
values (default, current_timestamp, current_timestamp, 'EUR', '€');
insert into currencies (id, creation_date, modification_date, code, symbol)
values (default, current_timestamp, current_timestamp, 'USD', '$');
insert into currencies (id, creation_date, modification_date, code, symbol)
values (default, current_timestamp, current_timestamp, 'RUB', '₽');