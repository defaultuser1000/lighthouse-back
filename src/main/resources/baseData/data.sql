-- Default system roles
insert into roles (name) values ('USER');
insert into roles (name) values ('ADMIN');

-- Default order statuses
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

-- Default Currencies
insert into currencies (creation_date, modification_date, code, symbol)
values (current_timestamp, current_timestamp, 'EUR', '€');
insert into currencies (creation_date, modification_date, code, symbol)
values (current_timestamp, current_timestamp, 'USD', '$');
insert into currencies (creation_date, modification_date, code, symbol)
values (current_timestamp, current_timestamp, 'RUB', '₽');

-- Defaults Film Formats
insert into film_formats (creation_date, modification_date, value)
values (current_timestamp, current_timestamp, '35');
insert into film_formats (creation_date, modification_date, value)
values (current_timestamp, current_timestamp, '120');
insert into film_formats (creation_date, modification_date, value)
values (current_timestamp, current_timestamp, '220');

-- Defaults Processes
insert into processes (creation_date, modification_date, name)
values (current_timestamp, current_timestamp, 'C41');
insert into processes (creation_date, modification_date, name)
values (current_timestamp, current_timestamp, 'BW');
insert into processes (creation_date, modification_date, name)
values (current_timestamp, current_timestamp, 'E6');

-- Defaults Order Types
insert into order_types (creation_date, modification_date, name)
values (current_timestamp, current_timestamp, 'Basic');
insert into order_types (creation_date, modification_date, name)
values (current_timestamp, current_timestamp, 'Premium');

-- Default Scan Sizes
insert into scan_sizes (creation_date, modification_date, description, size)
values (current_timestamp, current_timestamp, '35mm 2000*2900\n120type 2000*2600', 'L');
insert into scan_sizes (creation_date, modification_date, description, size)
values (current_timestamp, current_timestamp, '35mm 3600*5400\n120type 3600*4800', 'XL');
insert into scan_sizes (creation_date, modification_date, description, size)
values (current_timestamp, current_timestamp, '35mm 3600*5400\n120type 3600*4800', 'TIFF');

-- Default Scanners
insert into scanners (creation_date, modification_date, description, name, year_of_manufacture)
values (current_timestamp, current_timestamp, null, 'Frontier', null);
insert into scanners (creation_date, modification_date, description, name, year_of_manufacture)
values (current_timestamp, current_timestamp, null, 'Imacon', null);
insert into scanners (creation_date, modification_date, description, name, year_of_manufacture)
values (current_timestamp, current_timestamp, null, 'Noritsu', null);