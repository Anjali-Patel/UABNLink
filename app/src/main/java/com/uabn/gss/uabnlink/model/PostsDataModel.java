package com.uabn.gss.uabnlink.model;

public class PostsDataModel {

    String PostId, PostImage, PostDocument, PostMediaType, PostEmbeddedVideo, PostMessage, Postdate, PostLikeCount, PostCommentCount, PostIsLiked;
    String PostUserId;
    String PostUserImage;
    String Update_video;

    public String getUpdate_video() {
        return Update_video;
    }

    public void setUpdate_video(String update_video) {
        Update_video = update_video;
    }

    public String getVideo_type() {
        return video_type;
    }

    public void setVideo_type(String video_type) {
        this.video_type = video_type;
    }

    String PostUserName;
String video_type;
    public String getYou_tubeLink() {
        return You_tubeLink;
    }

    public void setYou_tubeLink(String you_tubeLink) {
        You_tubeLink = you_tubeLink;
    }

    String You_tubeLink;


    public String getYou_Tube_Tumbnail() {
        return You_Tube_Tumbnail;
    }

    public void setYou_Tube_Tumbnail(String you_Tube_Tumbnail) {
        You_Tube_Tumbnail = you_Tube_Tumbnail;
    }

    String You_Tube_Tumbnail;

    public String getGallery_camera_video() {
        return gallery_camera_video;
    }

    public void setGallery_camera_video(String gallery_camera_video) {
        this.gallery_camera_video = gallery_camera_video;
    }

    String PostImageBaseUrl, PostDocumentBaseUrl ,PostUserImageBaseUrl;
    String gallery_camera_video;


    public String getPostId() {
        return PostId;
    }
    public void setPostId(String postid) { PostId = postid; }

    public String getPostImage() {
        return PostImage;
    }
    public void setPostImage(String image) {
        PostImage = image;
    }

    public String getPostDocument() {
        return PostDocument;
    }
    public void setPostDocument(String doc) {
        PostDocument = doc;
    }

    public String getPostMediaType() {
        return PostMediaType;
    }
    public void setPostMediaType(String media) {
        PostMediaType = media;
    }

    public String getPostMessage() {
        return PostMessage;
    }
    public void setPostMessage(String message) {
        PostMessage = message;
    }

    public String getPostdate() {
        return Postdate;
    }
    public void setPostdate(String date) {
        Postdate = date;
    }

    public String getPostLikeCount() {
        return PostLikeCount;
    }
    public void setPostLikeCount(String likecount) {
        PostLikeCount = likecount;
    }

    public String getPostCommentCount() {
        return PostCommentCount;
    }
    public void setPostCommentCount(String commentcount) {
        PostCommentCount = commentcount;
    }

    public String getPostIsLiked() {
        return PostIsLiked;
    }
    public void setPostIsLiked(String islike) {
        PostIsLiked = islike;
    }

    public String getPostEmbeddedVideo() {
        return PostEmbeddedVideo;
    }
    public void setPostEmbeddedVideo(String video) {
        PostEmbeddedVideo = video;
    }

    public String getPostUserId() {
        return PostUserId;
    }
    public void setPostUserId(String id) {
        PostUserId = id;
    }

    public String getPostUserImage() {
        return PostUserImage;
    }
    public void setPostUserImage(String userimage) {
        PostUserImage = userimage;
    }

    public String getPostUserName() {
        return PostUserName;
    }
    public void setPostUserName(String username) {
        PostUserName = username;
    }


    public String getPostImageBaseUrl() {
        return PostImageBaseUrl;
    }
    public void setPostImageBaseUrl(String imagebase) {
        PostImageBaseUrl = imagebase;
    }

    public String getPostDocumentBaseUrl() {
        return PostDocumentBaseUrl;
    }
    public void setPostDocumentBaseUrl(String docbase) {
        PostDocumentBaseUrl = docbase;
    }

    public String getPostUserImageBaseUrl() {
        return PostUserImageBaseUrl;
    }
    public void setPostUserImageBaseUrl(String userimagebase) { PostUserImageBaseUrl = userimagebase; }

}
