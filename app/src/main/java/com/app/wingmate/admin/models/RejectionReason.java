package com.app.wingmate.admin.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.Date;

import static com.app.wingmate.utils.AppConstants.PARAM_DISPLAY_ORDER;
import static com.app.wingmate.utils.AppConstants.PARAM_REASON;
import static com.app.wingmate.utils.AppConstants.PARAM_TYPE;

@ParseClassName("RejectionReason")
public class RejectionReason extends ParseObject {
//public class RejectionReason {

    private boolean isSelected;

    public RejectionReason() {

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

    public String getReason() {
        return getString(PARAM_REASON);
    }

    public String getType() {
        return getString(PARAM_TYPE);
    }

    public int getReasonDisplayOrder() {
        return getInt(PARAM_DISPLAY_ORDER);
    }

//    public String getReason() {
//        return "getString(PARAM_REASON)";
//    }
//
//    public int getReasonDisplayOrder() {
//        return 1;
//    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
