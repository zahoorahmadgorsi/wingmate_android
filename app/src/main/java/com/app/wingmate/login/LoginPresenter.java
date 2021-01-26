package com.app.wingmate.login;

import android.content.Context;

import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginPresenter implements LoginInteractor.OnLoginFinishedListener {

    private LoginView loginView;
    private LoginInteractor loginInteractor;

    public LoginPresenter(LoginView loginView, LoginInteractor loginInteractor) {
        this.loginInteractor = loginInteractor;
        this.loginView = loginView;
    }

    public void validateAndLogin(Context context, String email, String password) {
        if (loginView != null) {
            loginInteractor.loginMeViaParse(context, email, password, this);
        }
    }

    @Override
    public void onInternetError() {
        if (loginView != null) {
            loginView.setInternetError();
        }

    }

    @Override
    public void onEmailError() {
        if (loginView != null) {
            loginView.setEmailError();
        }
    }

    @Override
    public void onInvalidEmailError() {
        if (loginView != null) {
            loginView.setInvalidEmailError();
        }
    }

    @Override
    public void onPasswordError() {
        if (loginView != null) {
            loginView.setPasswordError();
        }
    }

    @Override
    public void onInvalidPasswordError() {
        if (loginView != null) {
            loginView.setInvalidPasswordError();
        }
    }

    @Override
    public void onEmailVerificationError(ParseException e) {
        if (loginView != null) {
            loginView.setEmailVerificationError(e);
        }
    }

    @Override
    public void onLoginSuccess(ParseUser parseUser) {
        if (loginView != null) {
            loginView.setLoginSuccess(parseUser);
        }
    }

    @Override
    public void onResponseError(ParseException e) {
        if (loginView != null) {
            loginView.setResponseError(e);
        }
    }

}
