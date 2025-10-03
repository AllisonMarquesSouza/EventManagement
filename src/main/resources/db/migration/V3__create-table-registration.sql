create table registration(
    id uuid primary key,
    event_id uuid not null,
    user_id uuid not null,
    created_at timestamp not null,
    foreign key (event_id) references event(id) on delete cascade,
    foreign key (user_id) references users(id) on delete cascade
)