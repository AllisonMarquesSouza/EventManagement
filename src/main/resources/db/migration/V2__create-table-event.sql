create table event(
    id uuid primary key,
    title varchar(100) not null,
    date timestamp not null,
    location text not null,
    max_participants integer not null,
    registered_participants integer
)