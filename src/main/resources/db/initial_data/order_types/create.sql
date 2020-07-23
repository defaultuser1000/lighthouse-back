--liquibase formatted sql
--changeset zakrzhevskiy-as:20200723-04
insert into order_types (creation_date, modification_date, name)
values (current_timestamp, current_timestamp, 'Basic');
insert into order_types (creation_date, modification_date, name)
values (current_timestamp, current_timestamp, 'Premium');