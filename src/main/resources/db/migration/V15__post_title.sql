alter table post
    add column title varchar(80) null after author_id;

update post
set title = left(
    trim(regexp_replace(regexp_replace(content, '<[^>]+>', ' '), '[[:space:]]+', ' ')),
    80
);

update post
set title = '这一天'
where title is null
   or title = '';

alter table post
    modify column title varchar(80) not null;
