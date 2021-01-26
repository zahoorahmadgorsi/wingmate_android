package com.app.wingmate.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

import java.util.Date;

import static com.app.wingmate.utils.AppConstants.PARAM_DISPLAY_ORDER;
import static com.app.wingmate.utils.AppConstants.PARAM_FILE;
import static com.app.wingmate.utils.AppConstants.PARAM_IS_DO;
import static com.app.wingmate.utils.AppConstants.PARAM_TERMS_TYPE;
import static com.app.wingmate.utils.AppConstants.PARAM_TEXT;

@ParseClassName("TermsConditions")
public class TermsConditions extends ParseObject {

    public TermsConditions() {

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

    public String getTermsType() {
        return getString(PARAM_TERMS_TYPE);
    }

    public String getText() {
        return getString(PARAM_TEXT);
    }

    public boolean isDo() {
        return getBoolean(PARAM_IS_DO);
    }

    public int getDisplayOrder() {
        return getInt(PARAM_DISPLAY_ORDER);
    }

    public ParseFile getFile() {
        return getParseFile(PARAM_FILE);
    }
}