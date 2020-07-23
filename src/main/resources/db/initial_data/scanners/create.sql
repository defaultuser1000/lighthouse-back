--liquibase formatted sql
--changeset zakrzhevskiy-as:20200723-07
insert into scanners (creation_date, modification_date, description, name, year_of_manufacture)
values (current_timestamp, current_timestamp, null, 'Frontier', null);
insert into scanners (creation_date, modification_date, description, name, year_of_manufacture)
values (current_timestamp, current_timestamp, null, 'Imacon', null);
insert into scanners (creation_date, modification_date, description, name, year_of_manufacture)
values (current_timestamp, current_timestamp, null, 'Noritsu', null);