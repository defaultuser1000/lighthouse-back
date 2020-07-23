--liquibase formatted sql
--changeset zakrzhevskiy-as:20200723-05
insert into processes (creation_date, modification_date, name)
values (current_timestamp, current_timestamp, 'C41');
insert into processes (creation_date, modification_date, name)
values (current_timestamp, current_timestamp, 'BW');
insert into processes (creation_date, modification_date, name)
values (current_timestamp, current_timestamp, 'E6');