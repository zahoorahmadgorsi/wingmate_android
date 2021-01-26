package com.app.wingmate.forgot_password;

import android.content.Context;
import android.text.TextUtils;

import com.app.wingmate.utils.Utilities;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

public class ForgotPasswordInteracter {

    interface OnForgotPasswordFinishedListener {
        void onInternetError();
        void onEmailError();
        void onInvalidEmailError();
        void onResponseSuccess();
        void onResponseError(ParseException e);
    }

    public void recoverPasswordViaParse(Context context, final String email, final OnForgotPasswordFinishedListener listener) {
        if (TextUtils.isEmpty(email)) listener.onEmailError();
        else if (!Utilities.isValidEmail(email)) listener.onInvalidEmailError();
        else if (!Utilities.isInternetAvailable(context)) listener.onInternetError();
        else ParseUser.requestPasswordResetInBackground(email,
                    new RequestPasswordResetCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) listener.onResponseSuccess();
                            else listener.onResponseError(e);
                        }
                    });
    }
}