package com.example.simu;

public class PostModel {
    private String postId, userId, postText, postImage, postLikes, postDislikes ,postComments, address, attendanceType, workstation, designation;
    private long postingTime;
    private boolean isLiked;
    private boolean isDisliked;
    private double latitude, longitude;
    public PostModel(String postId, String userId, String postText, String postImage, String postLikes, String postDislikes, String postComments, long postingTime, double latitude, double longitude, String address, String attendanceType, String workstation, String designation) {
        this.postId = postId;
        this.userId = userId;
        this.postText = postText;
        this.postImage = postImage;
        this.postLikes = postLikes;
        this.postDislikes = postDislikes;
        this.postComments = postComments;
        this.postingTime = postingTime;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.attendanceType = attendanceType;
        this.workstation = workstation;
        this.designation = designation;
    }

    public PostModel() {
    }

    public String getAttendanceType() {
        return attendanceType;
    }

    public void setAttendanceType(String attendanceType) {
        this.attendanceType = attendanceType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public PostModel(String postId) {
        this.postId = postId;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public boolean isDisliked() {
        return isDisliked;
    }

    public void setDisliked(boolean disliked) {
        isDisliked = disliked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPostText() {
        return postText;
    }

    public void setPostText(String postText) {
        this.postText = postText;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public String getPostLikes() {
        return postLikes;
    }

    public void setPostLikes(String postLikes) {
        this.postLikes = postLikes;
    }

    public String getPostDislikes() {
        return postDislikes;
    }

    public void setPostDislikes(String postDislikes) {
        this.postDislikes = postDislikes;
    }

    public String getPostComments() {
        return postComments;
    }

    public void setPostComments(String postComments) {
        this.postComments = postComments;
    }

    public long getPostingTime() {
        return postingTime;
    }

    public void setPostingTime(long postingTime) {
        this.postingTime = postingTime;
    }

    public String getWorkstation() {
        return workstation;
    }

    public void setWorkstation(String workstation) {
        this.workstation = workstation;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }
}