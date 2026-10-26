alter table post
    add column view_count int not null default 0 after comments_open;

create table post_like (
    id bigint not null auto_increment,
    post_id bigint not null,
    ip_hash varchar(64) not null,
    user_id bigint null,
    created_at datetime(6) not null default current_timestamp(6),
    primary key (id),
    unique key uk_post_like_ip (post_id, ip_hash),
    constraint fk_post_like_post foreign key (post_id) references post (id) on delete cascade,
    constraint fk_post_like_user foreign key (user_id) references app_user (id)
) engine = InnoDB default charset = utf8mb4 collate = utf8mb4_unicode_ci;

create table post_view (
    id bigint not null auto_increment,
    post_id bigint not null,
    ip_hash varchar(64) not null,
    created_at datetime(6) not null default current_timestamp(6),
    primary key (id),
    unique key uk_post_view_ip (post_id, ip_hash),
    constraint fk_post_view_post foreign key (post_id) references post (id) on delete cascade
) engine = InnoDB default charset = utf8mb4 collate = utf8mb4_unicode_ci;
