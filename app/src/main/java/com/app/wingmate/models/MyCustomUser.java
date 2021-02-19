package com.app.wingmate.models;

import com.parse.ParseUser;

public class MyCustomUser {

    private ParseUser parseUser;
    private int matchPercent;

    public MyCustomUser() {

    }

    public ParseUser getParseUser() {
        return parseUser;
    }

    public void setParseUser(ParseUser parseUser) {
        this.parseUser = parseUser;
    }

    public int getMatchPercent() {
        return matchPercent;
    }

    public void setMatchPercent(int matchPercent) {
        this.matchPercent = matchPercent;
    }
}
