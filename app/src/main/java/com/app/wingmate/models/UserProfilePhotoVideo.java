package com.app.wingmate.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

import java.util.Date;

import static com.app.wingmate.utils.AppConstants.PARAM_DISPLAY_ORDER;
import static com.app.wingmate.utils.AppConstants.PARAM_FILE;
import static com.app.wingmate.utils.AppConstants.PARAM_IS_DO;
import static com.app.wingmate.utils.AppConstants.PARAM_IS_PHOTO;
import static com.app.wingmate.utils.AppConstants.PARAM_TERMS_TYPE;
import static com.app.wingmate.utils.AppConstants.PARAM_TEXT;
import static com.app.wingmate.utils.AppConstants.PARAM_USER_ID;

@ParseClassName("UserProfilePhotoVideo")
public class UserProfilePhotoVideo extends ParseObject {

    private boolean isDummyFile;

    public UserProfilePhotoVideo() {

    }

    public boolean isDummyFile() {
        return isDummyFile;
    }

    public void setDummyFile(boolean dummyFile) {
        isDummyFile = dummyFile;
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

    public String getUserId() {
        return getString(PARAM_USER_ID);
    }

    public boolean isPhoto() {
        return getBoolean(PARAM_IS_PHOTO);
    }

    public ParseFile getFile() {
        return getParseFile(PARAM_FILE);
    }
}
