package com.app.wingmate.models;

import com.parse.ParseObject;

public class InstantsList {

    String time;
    String lastMessage;
    String userName;
    String userAvatar;
    boolean unreadLabelShow;
    String userIdForChat;
    String userNameForChat;
    ParseObject parseObject;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public boolean isUnreadLabelShow() {
        return unreadLabelShow;
    }

    public void setUnreadLabelShow(boolean unreadLabelShow) {
        this.unreadLabelShow = unreadLabelShow;
    }

    public String getUserIdForChat() {
        return userIdForChat;
    }

    public void setUserIdForChat(String userIdForChat) {
        this.userIdForChat = userIdForChat;
    }

    public String getUserNameForChat() {
        return userNameForChat;
    }

    public void setUserNameForChat(String userNameForChat) {
        this.userNameForChat = userNameForChat;
    }

    public ParseObject getParseObject() {
        return parseObject;
    }

    public void setParseObject(ParseObject parseObject) {
        this.parseObject = parseObject;
    }
}
