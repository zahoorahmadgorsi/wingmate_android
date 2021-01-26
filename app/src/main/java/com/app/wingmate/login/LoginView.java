package com.app.wingmate.login;

import com.app.wingmate.base.BaseView;
import com.parse.ParseException;
import com.parse.ParseUser;

public interface LoginView extends BaseView {
    void setEmailError();
    void setInvalidEmailError();
    void setPasswordError();
    void setInvalidPasswordError();
    void setEmailVerificationError(ParseException e);
    void setLoginSuccess(ParseUser parseUser);
}
