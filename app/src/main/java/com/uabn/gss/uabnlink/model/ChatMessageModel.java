package com.uabn.gss.uabnlink.model;

public class ChatMessageModel {

    String ChatId,  FromUserId, ToUserId, MessageId, Message, ProfilePicture, MessageDate, UserName, MessageType;
    String Attatchment1, Attatchment2, Attatchment3, Attatchment4, Attatchment5, ChatAudio, ChatVideo, ChatDoc;

    public String getChatId() {
        return ChatId;
    }
    public void setChatId(String id) {
        ChatId = id;
    }

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

    public String getMessageType() {
        return MessageType;
    }
    public void setMessageType(String type) {
        MessageType = type;
    }

    public String getAttatchment1() {
        return Attatchment1;
    }
    public void setAttatchment1(String type) {
        Attatchment1 = type;
    }

    public String getAttatchment2() {
        return Attatchment2;
    }
    public void setAttatchment2(String type) {
        Attatchment2 = type;
    }

    public String getAttatchment3() {
        return Attatchment3;
    }
    public void setAttatchment3(String type) {
        Attatchment3 = type;
    }

    public String getAttatchment4() {
        return Attatchment4;
    }
    public void setAttatchment4(String type) {
        Attatchment4 = type;
    }

    public String getAttatchment5() {
        return Attatchment5;
    }
    public void setAttatchment5(String type) {
        Attatchment5 = type;
    }

    public String getChatAudio() {
        return ChatAudio;
    }
    public void setChatAudio(String audio) {
        ChatAudio = audio;
    }

    public String getChatVideo() {
        return ChatVideo;
    }
    public void setChatVideo(String video) {
        ChatVideo = video;
    }

    public String getChatDoc() {
        return ChatDoc;
    }
    public void setChatDoc(String doc) {
        ChatDoc = doc;
    }
}
