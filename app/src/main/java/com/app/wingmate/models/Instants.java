package com.app.wingmate.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Date;

@ParseClassName("Instants")
public class Instants extends ParseObject {

    private ParseUser sender;
    private ParseUser receiver;
    private String instantID;
    private String lastMessage;
    private boolean isUnread;
    private String msgSentBy;
    private Date msgCreateAt;

    public Instants() {
    }

    @Override
    public String getObjectId() {
        return super.getObjectId();
    }

    @Override
    public Date getUpdatedAt() {
        return super.getUpdatedAt();
    }

    @Override
    public Date getCreatedAt() {
        return super.getCreatedAt();
    }

    public ParseUser getSender() {
        return getParseUser("sender");
    }

    public ParseUser getReceiver() {
        return getParseUser("receiver");
    }

    public String getInstantID() {
        return instantID;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public boolean isUnread() {
        return isUnread;
    }

    public String getMsgSentBy() {
        return msgSentBy;
    }

    public Date getMsgCreateAt() {
        return msgCreateAt;
    }
}
