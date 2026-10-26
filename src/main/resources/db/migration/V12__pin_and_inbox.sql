alter table post
    add column pinned tinyint not null default 0 after view_count,
    add column pinned_at datetime(6) null after pinned,
    add key idx_post_pin (pinned, pinned_at);

alter table post_like
    add key idx_post_like_user (user_id);

alter table comment_like
    add key idx_comment_like_user (user_id);
