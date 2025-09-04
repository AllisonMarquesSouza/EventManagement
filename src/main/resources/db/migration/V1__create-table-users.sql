create table users(
   id uuid primary key,
   username varchar(100) unique not null ,
   email varchar(255) unique not null,
   password varchar(100) not null,
   role varchar(15) not null,
   created_at timestamp not null
);