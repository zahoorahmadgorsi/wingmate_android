package com.app.wingmate.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.Date;

@ParseClassName("Quotes")
public class Quotes extends ParseObject {
    private String quote;

    public Quotes(){

    }


    public String getQuote() {
        return getString("quote");
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
}
