--liquibase formatted sql
--changeset zakrzhevskiy-as:20200723-01
--preconditions onFail:MARK_RAN onError:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM roles WHERE name in ('USER', 'ADMIN')
insert into roles (id, name) values (1, 'USER');
insert into roles (id, name) values (2, 'ADMIN');