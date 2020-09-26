package com.uabn.gss.uabnlink.model;

public class ChatListModel {
    String ChatId, FromUserId, ToUserId, FromUserName, ToUserName, Message, Date, FromUserImage, UnreadMessages;


    public String getChatId() {
        return ChatId;
    }
    public void setChatId(String id) { ChatId = id; }

    public String getFromUserId() {
        return FromUserId;
    }
    public void setFromUserId(String id) { FromUserId = id; }

    public String getToUserId() {
        return ToUserId;
    }
    public void setToUserId(String id) {
        ToUserId = id;
    }

    public String getFromUserName() {
        return FromUserName;
    }
    public void setFromUserName(String name) {
        FromUserName = name;
    }

    public String getToUserName() {
        return ToUserName;
    }
    public void setToUserName(String name) {
        ToUserName = name;
    }

    public String getMessage() { return Message;}
    public void setMessage(String msg) {
        Message = msg;
    }

    public String getDate() {
        return Date;
    }
    public void setDate(String date) {
        Date = date;
    }

    public String getFromUserImage() {
        return FromUserImage;
    }
    public void setFromUserImage(String image) {
        FromUserImage = image;
    }

    public String getUnreadMessages() {
        return UnreadMessages;
    }
    public void setUnreadMessages(String message) {
        UnreadMessages = message;
    }
}
