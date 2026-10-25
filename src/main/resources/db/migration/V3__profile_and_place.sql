alter table app_user
    add column cover_url varchar(255) null,
    add column signature varchar(120) null;

alter table post
    add column latitude decimal(9, 6) null,
    add column longitude decimal(9, 6) null;

alter table comment
    add column user_id bigint null,
    add key idx_comment_user (user_id),
    add constraint fk_comment_user foreign key (user_id) references app_user (id);

update comment c
    join app_user u on u.display_name = c.nickname
set c.user_id = u.id
where c.user_id is null;
