--liquibase formatted sql
--changeset zakrzhevskiy-as:20200723-01
insert into roles (name) values ('USER');
insert into roles (name) values ('ADMIN');