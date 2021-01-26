package com.app.wingmate.verify_email;

import android.content.Context;

import com.parse.ParseException;

public class EmailVerificationPresenter implements EmailVerificationInteracter.OnEmailVerificationFinishedListener {

    private final EmailVerificationView emailVerificationView;
    private final EmailVerificationInteracter emailVerificationInteracter;

    public EmailVerificationPresenter(EmailVerificationView emailVerificationView, EmailVerificationInteracter emailVerificationInteracter) {
        this.emailVerificationInteracter = emailVerificationInteracter;
        this.emailVerificationView = emailVerificationView;
    }

    public void resendEmailViaCloudCode(Context context, String email) {
        if (emailVerificationView != null) {
            emailVerificationInteracter.resendEmailViaCloudCode(context, email, this);
        }
    }

    @Override
    public void onInternetError() {
        if (emailVerificationView != null) {
            emailVerificationView.setInternetError();
        }
    }

    @Override
    public void onResponseSuccess() {
        if (emailVerificationView != null) {
            emailVerificationView.setResponseSuccess();
        }
    }

    @Override
    public void onResponseError(ParseException e) {
        if (emailVerificationView != null) {
            emailVerificationView.setResponseError(e);
        }
    }
}