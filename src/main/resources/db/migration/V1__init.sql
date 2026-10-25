create table app_user (
    id bigint not null auto_increment,
    email varchar(255) not null,
    password_hash varchar(100) not null,
    display_name varchar(40) not null,
    role varchar(16) not null,
    created_at datetime(6) not null default current_timestamp(6),
    primary key (id),
    unique key uk_app_user_email (email)
) engine = InnoDB default charset = utf8mb4 collate = utf8mb4_unicode_ci;

create table site_setting (
    id int not null,
    title varchar(40) not null,
    subtitle varchar(80) not null,
    together_since date not null,
    about text not null,
    comment_mode varchar(16) not null,
    primary key (id)
) engine = InnoDB default charset = utf8mb4 collate = utf8mb4_unicode_ci;

create table post (
    id bigint not null auto_increment,
    author_id bigint not null,
    content text not null,
    happened_at date not null,
    mood varchar(32) null,
    location varchar(80) null,
    visibility varchar(16) not null,
    status varchar(16) not null,
    comments_open tinyint not null default 1,
    created_at datetime(6) not null default current_timestamp(6),
    updated_at datetime(6) not null default current_timestamp(6) on update current_timestamp(6),
    primary key (id),
    key idx_post_happened (happened_at),
    key idx_post_visibility_status (visibility, status),
    constraint fk_post_author foreign key (author_id) references app_user (id)
) engine = InnoDB default charset = utf8mb4 collate = utf8mb4_unicode_ci;

create table post_media (
    id bigint not null auto_increment,
    post_id bigint not null,
    url varchar(255) not null,
    sort_order int not null default 0,
    width int null,
    height int null,
    primary key (id),
    key idx_post_media_post (post_id, sort_order),
    constraint fk_post_media_post foreign key (post_id) references post (id) on delete cascade
) engine = InnoDB default charset = utf8mb4 collate = utf8mb4_unicode_ci;

create table comment (
    id bigint not null auto_increment,
    post_id bigint not null,
    nickname varchar(20) not null,
    content varchar(500) not null,
    status varchar(16) not null,
    ip_hash varchar(64) null,
    created_at datetime(6) not null default current_timestamp(6),
    primary key (id),
    key idx_comment_post_created (post_id, created_at),
    constraint fk_comment_post foreign key (post_id) references post (id) on delete cascade
) engine = InnoDB default charset = utf8mb4 collate = utf8mb4_unicode_ci;

create table anniversary (
    id bigint not null auto_increment,
    title varchar(40) not null,
    date date not null,
    repeat_yearly tinyint not null default 1,
    note varchar(200) null,
    visibility varchar(16) not null,
    primary key (id)
) engine = InnoDB default charset = utf8mb4 collate = utf8mb4_unicode_ci;
