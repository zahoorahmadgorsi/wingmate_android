package com.app.wingmate.base;

import com.parse.ParseException;
import com.parse.ParseObject;

import java.util.List;

public interface BaseView {
    void showProgress();
    void dismissProgress();
    void setInternetError();
    void setResponseSuccess();
    void setResponseError(ParseException e);
    void setResponseGeneralError(String error);
}
