insert into transport_companies(id, code)
values (1, 'cdek');
insert into transport_companies(id, code)
values (((select max(id) from transport_companies) + 1), 'russian-post');