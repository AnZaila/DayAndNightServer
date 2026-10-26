alter table app_user
    add column profile_visibility varchar(16) not null default 'PUBLIC',
    add column show_signature tinyint(1) not null default 1,
    add column show_cover tinyint(1) not null default 1,
    add column show_email tinyint(1) not null default 0,
    add column show_comments tinyint(1) not null default 0;
