package com.uabn.gss.uabnlink.model;

public class CommentDataModel {

    String CommentId, CommentText, CommentDate, CommentetorImage, CommentatorName, CommentLikeCount, CommentISLiked, getCommentReplyCount, CommentorUserId;
    String CommentReply, setCommentReplyDate, CommentReplierImage, CommentReplierName, CommentReplyLikeCount;

    public String getCommentId() {
        return CommentId;
    }
    public void setCommentId(String id) {
        CommentId = id;
    }

    public String getCommentText() {
        return CommentText;
    }
    public void setCommentText(String comment) {
        CommentText = comment;
    }

    public String getCommentDate() {
        return CommentDate;
    }
    public void setCommentDate(String created_date) {
        CommentDate = created_date;
    }

    public String getCommentetorImage() {
        return CommentetorImage;
    }
    public void setCommentetorImage(String image) { CommentetorImage = image; }

    public String getCommentatorName() {
        return CommentatorName;
    }
    public void setCommentatorName(String name) {
        CommentatorName = name;
    }

    public String getCommentReplyCount() {
        return getCommentReplyCount;
    }
    public void setCommentReplyCount(String count) { getCommentReplyCount = count; }

    public String getCommentorUserId() {
        return CommentorUserId;
    }
    public void setCommentorUserId(String userid) { CommentorUserId = userid; }

    public String getCommentReply() {
        return CommentReply;
    }
    public void setCommentReply(String id) {
        CommentReply = id;
    }

    public String getCommentISLiked() {
        return CommentISLiked;
    }
    public void setCommentISLiked(String islike) {
        CommentISLiked = islike;
    }

    public String getsetCommentReplyDate() {
        return setCommentReplyDate;
    }
    public void setCommentReplyDate(String date) {
        setCommentReplyDate = date;
    }

    public String getCommentReplierImage() {
        return CommentReplierImage;
    }
    public void setCommentReplierImage(String image) {
        CommentReplierImage = image;
    }

    public String getCommentReplierName() {
        return CommentReplierName;
    }
    public void setCommentReplierName(String name) { CommentReplierName = name; }

    public String getCommentReplyLikeCount() {
        return CommentReplyLikeCount;
    }
    public void setCommentReplyLikeCount(String like) { CommentReplyLikeCount = like; }

    public String getCommentLikeCount() {
        return CommentLikeCount;
    }
    public void setCommentLikeCount(String like) { CommentLikeCount = like; }
}
