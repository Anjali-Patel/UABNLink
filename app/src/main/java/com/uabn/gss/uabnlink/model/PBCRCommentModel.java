package com.uabn.gss.uabnlink.model;

import java.util.ArrayList;

public class PBCRCommentModel {
    String name;
    String comment;
    String date;
    String image;
    String user_id;
    String comment_id;
    String path;
    String pbcr_id;
    String group_id, Event_Id;
    String Type;

    public String getReply_count() {
        return reply_count;
    }

    public void setReply_count(String reply_count) {
        this.reply_count = reply_count;
    }

    String reply_count;

    String GroupcommentEmddedVideo, GroupcommentLink, GroupcommentDocument, GroupcommentMediaType;
    ArrayList<String> GroupcommentImageList = new ArrayList<String>();



    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        this.Type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPbcr_id() {
        return pbcr_id;
    }

    public void setPbcr_id(String pbcr_id) {
        this.pbcr_id = pbcr_id;
    }

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public String getEvent_Id() {
        return Event_Id;
    }

    public void setEvent_Id(String event_Id) {
        this.Event_Id = event_Id;
    }


    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }




    public String getGroupcommentEmddedVideo() {
        return GroupcommentEmddedVideo;
    }
    public void setGroupcommentEmddedVideo(String embed) {
        this.GroupcommentEmddedVideo = embed;
    }

    public String getGroupcommentLink() {
        return GroupcommentLink;
    }
    public void setGroupcommentLink(String link) {
        this.GroupcommentLink = link;
    }

    public String getGroupcommentDocument() {
        return GroupcommentDocument;
    }
    public void setGroupcommentDocument(String doc) { this.GroupcommentDocument = doc; }

    public String getGroupcommentMediaType() {
        return GroupcommentMediaType;
    }
    public void setGroupcommentMediaType(String type) { this.GroupcommentMediaType = type; }

//    public ArrayList<String> getGroupcommentImageList() {
//        return GroupcommentImageList;
//    }
//    public void setGroupcommentImageList(ArrayList<String> type) { this.GroupcommentImageList = type; }

    public ArrayList<String> getGroupcommentImageList() { return GroupcommentImageList; }
    public void setGroupcommentImageList(ArrayList<String> groupcommentImageList) { GroupcommentImageList = groupcommentImageList; }
}
