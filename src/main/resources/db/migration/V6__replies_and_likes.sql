alter table comment
    add column parent_id bigint null after post_id,
    add column reply_to_nickname varchar(20) null after parent_id,
    add key idx_comment_parent (parent_id),
    add constraint fk_comment_parent foreign key (parent_id) references comment (id) on delete cascade;

alter table danmaku
    add column parent_id bigint null after post_id,
    add column reply_to_nickname varchar(20) null after parent_id,
    add key idx_danmaku_parent (parent_id),
    add constraint fk_danmaku_parent foreign key (parent_id) references danmaku (id) on delete cascade;

create table comment_like (
    id bigint not null auto_increment,
    comment_id bigint not null,
    ip_hash varchar(64) not null,
    user_id bigint null,
    created_at datetime(6) not null default current_timestamp(6),
    primary key (id),
    unique key uk_comment_like_ip (comment_id, ip_hash),
    constraint fk_comment_like_comment foreign key (comment_id) references comment (id) on delete cascade,
    constraint fk_comment_like_user foreign key (user_id) references app_user (id)
) engine = InnoDB default charset = utf8mb4 collate = utf8mb4_unicode_ci;

create table danmaku_like (
    id bigint not null auto_increment,
    danmaku_id bigint not null,
    ip_hash varchar(64) not null,
    user_id bigint null,
    created_at datetime(6) not null default current_timestamp(6),
    primary key (id),
    unique key uk_danmaku_like_ip (danmaku_id, ip_hash),
    constraint fk_danmaku_like_danmaku foreign key (danmaku_id) references danmaku (id) on delete cascade,
    constraint fk_danmaku_like_user foreign key (user_id) references app_user (id)
) engine = InnoDB default charset = utf8mb4 collate = utf8mb4_unicode_ci;
