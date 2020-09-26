package com.uabn.gss.uabnlink.model;

public class GroupDataModel {

    String GroupId, GroupCreatorUserId, GroupName, GroupBy, GroupType, GroupDate, GroupViews, GroupComments, GroupIcon, UserGroupType, IsGroupLiked, IsJoined;

    public String getGroupId() {
        return GroupId;
    }
    public void setGroupId(String id) { GroupId = id; }

    public String getGroupCreatorUserId() {
        return GroupCreatorUserId;
    }
    public void setGroupCreatorUserId(String id) { GroupCreatorUserId = id; }

    public String getGroupName() {
        return GroupName;
    }
    public void setGroupName(String wing) { GroupName = wing; }

    public String getGroupBy() { return GroupBy; }
    public void setGroupBy(String wing) { GroupBy = wing; }

    public String getGroupType() { return GroupType; }
    public void setGroupType(String wing) { GroupType = wing; }

    public String getGroupDate() { return GroupDate; }
    public void setGroupDate(String wing) { GroupDate = wing; }

    public String getGroupViews() { return GroupViews; }
    public void setGroupViews(String wing) { GroupViews = wing; }

    public String getGroupComments() { return GroupComments; }
    public void setGroupComments(String wing) { GroupComments = wing; }

    public String getGroupIcon() { return GroupIcon; }
    public void setGroupIcon(String wing) { GroupIcon = wing; }

    public String getUserGroupType() { return UserGroupType; }
    public void setUserGroupType(String type) { UserGroupType = type; }

    public String getIsGroupLiked() { return IsGroupLiked; }
    public void setIsGroupLiked(String like) { IsGroupLiked = like; }

    public String getIsJoined() { return IsJoined; }
    public void setIsJoined(String join) { IsJoined = join; }
}
