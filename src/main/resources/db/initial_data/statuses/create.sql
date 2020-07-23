--liquibase formatted sql
--changeset zakrzhevskiy-as:20200723-08
insert into order_statuses (id, display_name, next_status_id, creation_date, modification_date)
values (
           default,
           'Ready',
           NULL,
           current_timestamp,
           current_timestamp
       );
insert into order_statuses (id, display_name, next_status_id, creation_date, modification_date)
values (
           default,
           'Uploaded',
           (select id from order_statuses where display_name = 'Ready'),
           current_timestamp,
           current_timestamp
       );
insert into order_statuses (id, display_name, next_status_id, creation_date, modification_date)
values (
           default,
           'Processed',
           (select id from order_statuses where display_name = 'Uploaded'),
           current_timestamp,
           current_timestamp
       );
insert into order_statuses (id, display_name, next_status_id, creation_date, modification_date)
values (
           default,
           'Scanned',
           (select id from order_statuses where display_name = 'Processed'),
           current_timestamp,
           current_timestamp
       );
insert into order_statuses (id, display_name, next_status_id, creation_date, modification_date)
values (
           default,
           'Developed',
           (select id from order_statuses where display_name = 'Scanned'),
           current_timestamp,
           current_timestamp
       );
insert into order_statuses (id, display_name, next_status_id, creation_date, modification_date)
values (
           default,
           'Arrived',
           (select id from order_statuses where display_name = 'Developed'),
           current_timestamp,
           current_timestamp
       );
insert into order_statuses (id, display_name, next_status_id, creation_date, modification_date)
values (
           default,
           'Approved',
           (select id from order_statuses where display_name = 'Arrived'),
           current_timestamp,
           current_timestamp
       );
insert into order_statuses (id, display_name, next_status_id, creation_date, modification_date)
values (
           default,
           'New',
           (select id from order_statuses where display_name = 'Approved'),
           current_timestamp,
           current_timestamp
       );