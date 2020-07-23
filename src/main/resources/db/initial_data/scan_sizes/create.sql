--liquibase formatted sql
--changeset zakrzhevskiy-as:20200723-06
insert into scan_sizes (creation_date, modification_date, description, size)
values (current_timestamp, current_timestamp, '35mm 2000*2900\n120type 2000*2600', 'L');
insert into scan_sizes (creation_date, modification_date, description, size)
values (current_timestamp, current_timestamp, '35mm 3600*5400\n120type 3600*4800', 'XL');
insert into scan_sizes (creation_date, modification_date, description, size)
values (current_timestamp, current_timestamp, '35mm 3600*5400\n120type 3600*4800', 'TIFF');