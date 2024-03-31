package com.example.simu;

public class PostModel {
    private String postId, userId, postText, postImage, postLikes, postDislikes ,postComments, locationName, locationImageUrl;
    private long postingTime;
    private boolean isLiked;
    private boolean isDisliked;
    public PostModel(String postId, String userId, String postText, String postImage, String postLikes, String postDislikes, String postComments, long postingTime) {
        this.postId = postId;
        this.userId = userId;
        this.postText = postText;
        this.postImage = postImage;
        this.postLikes = postLikes;
        this.postDislikes = postDislikes;
        this.postComments = postComments;
        this.postingTime = postingTime;
    }


    public PostModel() {
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getLocationImageUrl() {
        return locationImageUrl;
    }

    public void setLocationImageUrl(String locationImageUrl) {
        this.locationImageUrl = locationImageUrl;
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
}