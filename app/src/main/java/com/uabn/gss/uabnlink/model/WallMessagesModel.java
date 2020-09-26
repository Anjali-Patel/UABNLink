package com.uabn.gss.uabnlink.model;

public class WallMessagesModel {

    String FromUserId, ToUserId, MessageId, Message, ProfilePicture, MessageDate, UserName;

    public String getFromUserId() {
        return FromUserId;
    }
    public void setFromUserId(String from) {
        FromUserId = from;
    }

    public String getToUserId() {
        return ToUserId;
    }
    public void setToUserId(String to) {
        ToUserId = to;
    }

    public String getMessageId() {
        return MessageId;
    }
    public void setMessageId(String msgid) {
        MessageId = msgid;
    }

    public String getMessage() {
        return Message;
    }
    public void setMessage(String message) {
        Message = message;
    }

    public String getProfilePicture() {
        return ProfilePicture;
    }
    public void setProfilePicture(String pic) {
        ProfilePicture = pic;
    }

    public String getMessageDate() {
        return MessageDate;
    }
    public void setMessageDate(String date) {
        MessageDate = date;
    }

    public String getUserName() {
        return UserName;
    }
    public void setUserName(String name) {
        UserName = name;
    }


}
