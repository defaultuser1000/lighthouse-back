insert into transport_company_names(id, name, locale, company_id)
VALUES (1,
        'СДЭК',
        'ru_RU',
        (select id from transport_companies where code = 'cdek'));
insert into transport_company_names(id, name, locale, company_id)
VALUES (((select max(id) from transport_company_names) + 1),
        'Почта России',
        'ru_RU',
        (select id from transport_companies where code = 'rus_post'));
insert into transport_company_names(id, name, locale, company_id)
VALUES (((select max(id) from transport_company_names) + 1),
        'Russian post',
        'en',
        (select id from transport_companies where code = 'rus_post'));