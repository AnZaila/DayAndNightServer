create table danmaku (
    id bigint not null auto_increment,
    post_id bigint null,
    user_id bigint null,
    nickname varchar(20) not null,
    content varchar(40) not null,
    status varchar(16) not null,
    ip_hash varchar(64) null,
    created_at datetime(6) not null default current_timestamp(6),
    primary key (id),
    key idx_danmaku_post_created (post_id, created_at),
    key idx_danmaku_created (created_at),
    constraint fk_danmaku_post foreign key (post_id) references post (id) on delete cascade,
    constraint fk_danmaku_user foreign key (user_id) references app_user (id)
) engine = InnoDB default charset = utf8mb4 collate = utf8mb4_unicode_ci;
