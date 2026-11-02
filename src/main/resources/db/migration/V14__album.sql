create table album_photo (
    id bigint not null auto_increment,
    user_id bigint not null,
    url varchar(255) not null,
    width int null,
    height int null,
    created_at datetime(6) not null default current_timestamp(6),
    primary key (id),
    key idx_album_photo_created (created_at, id),
    constraint fk_album_photo_user foreign key (user_id) references app_user (id)
) engine = InnoDB default charset = utf8mb4 collate = utf8mb4_unicode_ci;
