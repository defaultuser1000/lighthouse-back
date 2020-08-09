--liquibase formatted sql
--changeset zakrzhevskiy-as:20200723-08
--preconditions onFail:MARK_RAN onError:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM order_statuses WHERE display_name in ('Ready', 'Uploaded', 'Processed', 'Scanned', 'Developed', 'Arrived', 'Approved', 'New')
insert into order_statuses (id, display_name, next_status_id, creation_date, modification_date)
values (
           1,
           'Ready',
           NULL,
           current_timestamp,
           current_timestamp
       );
insert into order_statuses (id, display_name, next_status_id, creation_date, modification_date)
values (
           2,
           'Uploaded',
           (select id from order_statuses where display_name = 'Ready'),
           current_timestamp,
           current_timestamp
       );
insert into order_statuses (id, display_name, next_status_id, creation_date, modification_date)
values (
           3,
           'Processed',
           (select id from order_statuses where display_name = 'Uploaded'),
           current_timestamp,
           current_timestamp
       );
insert into order_statuses (id, display_name, next_status_id, creation_date, modification_date)
values (
           4,
           'Scanned',
           (select id from order_statuses where display_name = 'Processed'),
           current_timestamp,
           current_timestamp
       );
insert into order_statuses (id, display_name, next_status_id, creation_date, modification_date)
values (
           5,
           'Developed',
           (select id from order_statuses where display_name = 'Scanned'),
           current_timestamp,
           current_timestamp
       );
insert into order_statuses (id, display_name, next_status_id, creation_date, modification_date)
values (
           6,
           'Arrived',
           (select id from order_statuses where display_name = 'Developed'),
           current_timestamp,
           current_timestamp
       );
insert into order_statuses (id, display_name, next_status_id, creation_date, modification_date)
values (
           7,
           'Approved',
           (select id from order_statuses where display_name = 'Arrived'),
           current_timestamp,
           current_timestamp
       );
insert into order_statuses (id, display_name, next_status_id, creation_date, modification_date)
values (
           8,
           'New',
           (select id from order_statuses where display_name = 'Approved'),
           current_timestamp,
           current_timestamp
       );