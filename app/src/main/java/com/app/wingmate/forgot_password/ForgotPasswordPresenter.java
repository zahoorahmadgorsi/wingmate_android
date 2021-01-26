package com.app.wingmate.forgot_password;

import android.content.Context;

import com.parse.ParseException;

public class ForgotPasswordPresenter implements ForgotPasswordInteracter.OnForgotPasswordFinishedListener {

    private final ForgotPasswordView forgotPasswordView;
    private final ForgotPasswordInteracter forgotPasswordInteracter;

    public ForgotPasswordPresenter(ForgotPasswordView forgotPasswordView, ForgotPasswordInteracter forgotPasswordInteracter) {
        this.forgotPasswordInteracter = forgotPasswordInteracter;
        this.forgotPasswordView = forgotPasswordView;
    }

    public void validateAndRecover(Context context, String email) {
        if (forgotPasswordView != null) {
            forgotPasswordInteracter.recoverPasswordViaParse(context, email, this);
        }
    }

    @Override
    public void onInternetError() {
        if (forgotPasswordView != null) {
            forgotPasswordView.setInternetError();
        }
    }

    @Override
    public void onEmailError() {
        if (forgotPasswordView != null) {
            forgotPasswordView.setEmailError();
        }
    }

    @Override
    public void onInvalidEmailError() {
        if (forgotPasswordView != null) {
            forgotPasswordView.setInvalidEmailError();
        }
    }

    @Override
    public void onResponseSuccess() {
        if (forgotPasswordView != null) {
            forgotPasswordView.setResponseSuccess();
        }
    }

    @Override
    public void onResponseError(ParseException e) {
        if (forgotPasswordView != null) {
            forgotPasswordView.setResponseError(e);
        }
    }
}