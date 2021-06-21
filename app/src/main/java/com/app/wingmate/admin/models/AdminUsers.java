package com.app.wingmate.admin.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.Date;

import static com.app.wingmate.utils.AppConstants.PARAM_IMAGE;
import static com.app.wingmate.utils.AppConstants.PARAM_IS_ACTIVE;
import static com.app.wingmate.utils.AppConstants.PARAM_USERNAME;

@ParseClassName("AdminUsers")
public class AdminUsers extends ParseObject {

    public AdminUsers() {

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

    public String getUsername() {
        return getString(PARAM_USERNAME);
    }

    public boolean isActive() {
        return getBoolean(PARAM_IS_ACTIVE);
    }

    public String getImageUrl() {
        return getParseFile(PARAM_IMAGE).getUrl();
    }

}
