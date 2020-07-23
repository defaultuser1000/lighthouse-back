--liquibase formatted sql
--changeset zakrzhevskiy-as:20200723-05
insert into processes (id, creation_date, modification_date, name)
values (default, current_timestamp, current_timestamp, 'C41');
insert into processes (id, creation_date, modification_date, name)
values (default, current_timestamp, current_timestamp, 'BW');
insert into processes (id, creation_date, modification_date, name)
values (default, current_timestamp, current_timestamp, 'E6');