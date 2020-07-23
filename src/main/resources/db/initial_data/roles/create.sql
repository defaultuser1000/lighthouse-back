--liquibase formatted sql
--changeset zakrzhevskiy-as:20200723-01
insert into roles (id, name) values (1, 'USER');
insert into roles (id, name) values (2, 'ADMIN');