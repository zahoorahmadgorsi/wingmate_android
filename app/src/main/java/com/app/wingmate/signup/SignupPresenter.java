package com.app.wingmate.signup;

import android.content.Context;

import com.parse.ParseException;
import com.parse.ParseObject;

public class SignupPresenter implements SignupInteractor.OnSignupFinishedListener {

    private SignupView signUpView;
    private SignupInteractor signupInteractor;

    public SignupPresenter(SignupView signUpView, SignupInteractor signupInteractor) {
        this.signupInteractor = signupInteractor;
        this.signUpView = signUpView;
    }

    public void signUpFormValidate(Context context, String nick, String gender, String email, String password) {
        if (signUpView != null) {
            signupInteractor.signUpFormValidate(context, nick, gender, email, password, this);
        }
    }

    public void signUpMeViaParse(Context context, String nick, String gender, String email, String password) {
        if (signUpView != null) {
            signupInteractor.signUpMeViaParse(context, nick, gender, email, password, this);
        }
    }

    public void updateEmailViaParse(Context context, String nick, String gender, String email, String password) {
        if (signUpView != null) {
            signupInteractor.updateEmailViaParse(context, nick, gender, email, password, this);
        }
    }

    public void getVideoLinkParse(Context context) {
        if (signUpView != null) {
            signupInteractor.getVideoLinkParse(context, this);
        }
    }

    @Override
    public void onVideoLinkSuccess(ParseObject parseObject) {
        if (signUpView != null) {
            signUpView.setVideoLinkSuccess(parseObject);
        }
    }

    @Override
    public void onInternetError() {
        if (signUpView != null) {
            signUpView.setInternetError();
        }
    }

    @Override
    public void onNickError() {
        if (signUpView != null) {
            signUpView.setNickError();
        }
    }

    @Override
    public void onGenderError() {
        if (signUpView != null) {
            signUpView.setGenderError();
        }
    }

    @Override
    public void onEmailError() {
        if (signUpView != null) {
            signUpView.setEmailError();
        }
    }

    @Override
    public void onInvalidEmailError() {
        if (signUpView != null) {
            signUpView.setInvalidEmailError();
        }
    }

    @Override
    public void onPasswordError() {
        if (signUpView != null) {
            signUpView.setPasswordError();
        }
    }

    @Override
    public void onInvalidPasswordError() {
        if (signUpView != null) {
            signUpView.setInvalidPasswordError();
        }
    }

    @Override
    public void onResponseSuccess() {
        if (signUpView != null) {
            signUpView.setResponseSuccess();
        }
    }

    @Override
    public void onFormValidateSuccess() {
        if (signUpView != null) {
            signUpView.setFormValidateSuccess();
        }
    }

    @Override
    public void onResponseError(ParseException e) {
        if (signUpView != null) {
            signUpView.setResponseError(e);
        }
    }
}
