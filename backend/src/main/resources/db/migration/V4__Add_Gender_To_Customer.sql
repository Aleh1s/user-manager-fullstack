delete from customer;
alter table customer add column gender varchar(6) not null check (gender in ('MALE', 'FEMALE'));