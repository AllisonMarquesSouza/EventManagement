create table registration(
    id uuid primary key,
    eventId uuid not null,
    participantId uuid not null,
    created_at timestamp not null,
    foreign key (eventId) references event(id) on delete cascade,
    foreign key (participantId) references users(id) on delete cascade
)