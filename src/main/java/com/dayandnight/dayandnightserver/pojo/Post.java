package com.dayandnight.dayandnightserver.pojo;

import com.dayandnight.dayandnightserver.dto.CreatePostDTO;
import com.dayandnight.dayandnightserver.enums.PostStatus;

import java.time.LocalDate;

public class Post {

    private Long id;
    private Long authorId;
    private String content;
    private LocalDate happenedAt;
    private String mood;
    private String location;
    private String visibility;
    private String status;
    private Boolean commentsOpen;

    public static Post forInsert(long authorId, CreatePostDTO request) {
        Post post = new Post();
        post.authorId = authorId;
        post.content = request.content().trim();
        post.happenedAt = request.happenedAt();
        post.mood = blankToNull(request.mood());
        post.location = blankToNull(request.location());
        post.visibility = request.visibility().name();
        post.status = PostStatus.PUBLISHED.name();
        post.commentsOpen = true;
        return post;
    }

    private static String blankToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDate getHappenedAt() {
        return happenedAt;
    }

    public void setHappenedAt(LocalDate happenedAt) {
        this.happenedAt = happenedAt;
    }

    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getCommentsOpen() {
        return commentsOpen;
    }

    public void setCommentsOpen(Boolean commentsOpen) {
        this.commentsOpen = commentsOpen;
    }
}
