alter table app_user
    add column show_likes tinyint(1) not null default 0 after show_comments;
