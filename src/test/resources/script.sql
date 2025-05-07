create table groupe (id integer not null, libelle varchar(255), primary key (id))
create table groupe_client (idClient varchar(255) not null, idGroupe integer not null, primary key (idClient, idGroupe))
alter table bkeve_message drop constraint if exists UK_4g4xror9nvbnv5kokk95ph30u
alter table bkeve_message add constraint UK_4g4xror9nvbnv5kokk95ph30u unique (messages_id)
alter table messageformat drop constraint if exists UK5y63lkv4hh73o2te51dj7b9p4
alter table messageformat add constraint UK5y63lkv4hh73o2te51dj7b9p4 unique (ope, langue)