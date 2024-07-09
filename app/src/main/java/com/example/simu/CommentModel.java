package com.example.simu;

public class CommentModel {
    private String commentId;
    private String postId;
    private String userId;
    private String comment;
    private long commentTime;
    private String sentiment;
    private float positiveScore;
    private float negativeScore;

    public CommentModel(String commentId, String postId, String userId, String comment, long commentTime, String sentiment, float positiveScore, float negativeScore) {
        this.commentId = commentId;
        this.postId = postId;
        this.userId = userId;
        this.comment = comment;
        this.commentTime = commentTime;
        this.sentiment = sentiment;
        this.positiveScore = positiveScore;
        this.negativeScore = negativeScore;
    }

    public CommentModel() {
    }

    public long getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(long commentTime) {
        this.commentTime = commentTime;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getSentiment() {
        return sentiment;
    }

    public void setSentiment(String sentiment) {
        this.sentiment = sentiment;
    }

    public float getPositiveScore() {
        return positiveScore;
    }

    public void setPositiveScore(float positiveScore) {
        this.positiveScore = positiveScore;
    }

    public float getNegativeScore() {
        return negativeScore;
    }

    public void setNegativeScore(float negativeScore) {
        this.negativeScore = negativeScore;
    }
}
