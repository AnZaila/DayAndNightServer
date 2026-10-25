package com.dayandnight.dayandnightserver.pojo;

import java.time.LocalDate;

public class SiteSetting {

    private Integer id;
    private String title;
    private String subtitle;
    private LocalDate togetherSince;
    private String about;
    private String commentMode;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public LocalDate getTogetherSince() {
        return togetherSince;
    }

    public void setTogetherSince(LocalDate togetherSince) {
        this.togetherSince = togetherSince;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getCommentMode() {
        return commentMode;
    }

    public void setCommentMode(String commentMode) {
        this.commentMode = commentMode;
    }
}
