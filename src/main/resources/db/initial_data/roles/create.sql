--liquibase formatted sql
--changeset zakrzhevskiy-as:20200723-01
insert into roles (id, name) values (default, 'USER');
insert into roles (id, name) values (default, 'ADMIN');