--liquibase formatted sql
--changeset zakrzhevskiy-as:20200723-06
--preconditions onFail:MARK_RAN onError:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM scan_sizes WHERE size in ('L', 'XL', 'TIFF')
insert into scan_sizes (id, creation_date, modification_date, description, size)
values (1, current_timestamp, current_timestamp, '35mm 2000*2900\n120type 2000*2600', 'L');
insert into scan_sizes (id, creation_date, modification_date, description, size)
values (2, current_timestamp, current_timestamp, '35mm 3600*5400\n120type 3600*4800', 'XL');
insert into scan_sizes (id, creation_date, modification_date, description, size)
values (3, current_timestamp, current_timestamp, '35mm 3600*5400\n120type 3600*4800', 'TIFF');