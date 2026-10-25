package com.dayandnight.dayandnightserver.pojo;

import java.time.LocalDate;

public class Anniversary {

    private Long id;
    private String title;
    private LocalDate date;
    private Boolean repeatYearly;
    private String note;
    private String visibility;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Boolean getRepeatYearly() {
        return repeatYearly;
    }

    public void setRepeatYearly(Boolean repeatYearly) {
        this.repeatYearly = repeatYearly;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }
}
