package com.example.simu;

public class UserModel {
    private String userId;
    private String userName;
    private String userEmail;
    private String userProfile;
    private String userCover;

    public UserModel(String userId, String userName, String userEmail, String userProfile, String userCover) {
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userProfile = userProfile;
        this.userCover = userCover;
    }

    public UserModel() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(String userProfile) {
        this.userProfile = userProfile;
    }

    public String getUserCover() {
        return userCover;
    }

    public void setUserCover(String userCover) {
        this.userCover = userCover;
    }
}
