package com.dayandnight.dayandnightserver.pojo;

public class User {

    private Long id;
    private String email;
    private String passwordHash;
    private String displayName;
    private String avatarUrl;
    private String coverUrl;
    private String signature;
    private String profileVisibility;
    private boolean showSignature;
    private boolean showCover;
    private boolean showEmail;
    private boolean showComments;
    private boolean showLikes;
    private String role;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getProfileVisibility() {
        return profileVisibility;
    }

    public void setProfileVisibility(String profileVisibility) {
        this.profileVisibility = profileVisibility;
    }

    public boolean isShowSignature() {
        return showSignature;
    }

    public void setShowSignature(boolean showSignature) {
        this.showSignature = showSignature;
    }

    public boolean isShowCover() {
        return showCover;
    }

    public void setShowCover(boolean showCover) {
        this.showCover = showCover;
    }

    public boolean isShowEmail() {
        return showEmail;
    }

    public void setShowEmail(boolean showEmail) {
        this.showEmail = showEmail;
    }

    public boolean isShowComments() {
        return showComments;
    }

    public void setShowComments(boolean showComments) {
        this.showComments = showComments;
    }

    public boolean isShowLikes() {
        return showLikes;
    }

    public void setShowLikes(boolean showLikes) {
        this.showLikes = showLikes;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
