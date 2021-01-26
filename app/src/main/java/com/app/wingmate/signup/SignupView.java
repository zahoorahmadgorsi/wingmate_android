package com.app.wingmate.signup;

import com.app.wingmate.base.BaseView;
import com.parse.ParseException;
import com.parse.ParseObject;

public interface SignupView extends BaseView {
    void setVideoLinkSuccess(ParseObject parseObject);
    void setNickError();
    void setGenderError();
    void setEmailError();
    void setInvalidEmailError();
    void setPasswordError();
    void setInvalidPasswordError();
    void setFormValidateSuccess();
}
