--liquibase formatted sql
--changeset zakrzhevskiy-as:20200723-08
insert into order_statuses (display_name, next_status_id, creation_date, modification_date)
values (
           'Ready',
           NULL,
           current_timestamp,
           current_timestamp
       );
insert into order_statuses (display_name, next_status_id, creation_date, modification_date)
values (
           'Uploaded',
           (select id from order_statuses where display_name = 'Ready'),
           current_timestamp,
           current_timestamp
       );
insert into order_statuses (display_name, next_status_id, creation_date, modification_date)
values (
           'Processed',
           (select id from order_statuses where display_name = 'Uploaded'),
           current_timestamp,
           current_timestamp
       );
insert into order_statuses (display_name, next_status_id, creation_date, modification_date)
values (
           'Scanned',
           (select id from order_statuses where display_name = 'Processed'),
           current_timestamp,
           current_timestamp
       );
insert into order_statuses (display_name, next_status_id, creation_date, modification_date)
values (
           'Developed',
           (select id from order_statuses where display_name = 'Scanned'),
           current_timestamp,
           current_timestamp
       );
insert into order_statuses (display_name, next_status_id, creation_date, modification_date)
values (
           'Arrived',
           (select id from order_statuses where display_name = 'Developed'),
           current_timestamp,
           current_timestamp
       );
insert into order_statuses (display_name, next_status_id, creation_date, modification_date)
values (
           'Approved',
           (select id from order_statuses where display_name = 'Arrived'),
           current_timestamp,
           current_timestamp
       );
insert into order_statuses (display_name, next_status_id, creation_date, modification_date)
values (
           'New',
           (select id from order_statuses where display_name = 'Approved'),
           current_timestamp,
           current_timestamp
       );