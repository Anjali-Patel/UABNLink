package com.uabn.gss.uabnlink.model;

public class MemberDataModel {

    String MemberId, MemberImage, MemberName, MemberAddress, MemberGender, MemberLocation, MemberJoined, MemberStatus, RequestStatus;
    String InterestType;

    public String getMemberId() {
        return MemberId;
    }
    public void setMemberId(String id) {
        MemberId = id;
    }

    public String getMemberImage() {
        return MemberImage;
    }
    public void setMemberImage(String wing) {
        MemberImage = wing;
    }

    public String getMemberName() {
        return MemberName;
    }
    public void setMemberName(String wing) {
        MemberName = wing;
    }

    public String getMemberAddress() {
        return MemberAddress;
    }
    public void setMemberAddress(String wing) {
        MemberAddress = wing;
    }


    public String getMemberGender() {
        return MemberGender;
    }
    public void setMemberGender(String wing) {
        MemberGender = wing;
    }

    public String getMemberLocation() {
        return MemberLocation;
    }
    public void setMemberLocation(String wing) {
        MemberLocation = wing;
    }

    public String getMemberJoined() {
        return MemberJoined;
    }
    public void setMemberJoined(String wing) {
        MemberJoined = wing;
    }

    public String getMemberStatus() {
        return MemberStatus;
    }
    public void setMemberStatus(String wing) {
        MemberStatus = wing;
    }

    public String getRequestStatus() {
        return RequestStatus;
    }
    public void setRequestStatus(String status) {
        RequestStatus = status;
    }

    public String getInterestType() {
        return InterestType;
    }
    public void setInterestType(String type) {
        InterestType = type;
    }
}
