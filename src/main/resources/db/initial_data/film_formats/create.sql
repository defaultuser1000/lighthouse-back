--liquibase formatted sql
--changeset zakrzhevskiy-as:20200723-03
insert into film_formats (creation_date, modification_date, value)
values (current_timestamp, current_timestamp, '35');
insert into film_formats (creation_date, modification_date, value)
values (current_timestamp, current_timestamp, '120');
insert into film_formats (creation_date, modification_date, value)
values (current_timestamp, current_timestamp, '220');