-- Tables creation --
create table books (
    id bigint generated by default as identity,
    author varchar(255) not null,
    isbn varchar(255) not null,
    title varchar(255) not null,
    total_available integer not null,
    total_copies integer not null,
    version integer,
    primary key (id)
);

create table reservation_items (
    id bigint generated by default as identity,
    quantity integer not null,
    book_id bigint not null,
    reservation_id bigint not null,
    primary key (id)
);

create table reservations (
    id bigint generated by default as identity,
    created_at timestamp(6) not null,
    expires_at timestamp(6),
    picked_up_at timestamp(6),
    status varchar(50) not null,
    user_id bigint not null,
    primary key (id)
);

create table users (
    id bigint generated by default as identity,
    email varchar(255) not null,
    name varchar(255) not null,
    primary key (id)
);

-- Unique constraints --
alter table if exists books add constraint books_isbn_unique unique (isbn);
alter table if exists books add constraint books_title_unique unique (title);
alter table if exists users add constraint users_email_unique unique (email);

-- Foreign keys --
alter table if exists reservation_items add constraint reservation_items_books_fk foreign key (book_id) references books;
alter table if exists reservation_items add constraint reservation_items_reservations_fk foreign key (reservation_id) references reservations;
alter table if exists reservations add constraint reservations_users_fk foreign key (user_id) references users;

-- Indices --
create index reservations_created_at_expires_at_idx on reservations (created_at, expires_at);

-- Insertion of data --
insert into books (id, author, isbn, title, total_available, total_copies, version) values (1, 'Jose', 'a1b2', 'The empire', 10, 10, 1);
insert into books (id, author, isbn, title, total_available, total_copies, version) values (2, 'Maria', 'c3d4', 'Linux made easy', 10, 10, 1);
insert into users (id, email, name) values (1, 'email1@email.com', 'User one');
insert into users (id, email, name) values (2, 'email2@email.com', 'User two');