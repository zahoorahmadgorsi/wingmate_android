package com.app.wingmate.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Date;

import static com.app.wingmate.utils.AppConstants.PARAM_FAN_TYPE;
import static com.app.wingmate.utils.AppConstants.PARAM_FROM_USER;
import static com.app.wingmate.utils.AppConstants.PARAM_TO_USER;

@ParseClassName("Fans")
public class Fans extends ParseObject {
    private ParseUser fromUser;
    private ParseUser toUser;
    private String fanType;
    private String mySelectedType;

    public Fans() {

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

    public String getFanType() {
        return getString(PARAM_FAN_TYPE);
    }

    public ParseUser getFromUser() {
        return getParseUser(PARAM_FROM_USER);
    }

    public ParseUser getToUser() {
        return getParseUser(PARAM_TO_USER);
    }

    public String getMySelectedType() {
        return mySelectedType;
    }

    public void setMySelectedType(String mySelectedType) {
        this.mySelectedType = mySelectedType;
    }
}
