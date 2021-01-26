package com.app.wingmate.login;

import android.content.Context;
import android.text.TextUtils;

import com.app.wingmate.utils.Utilities;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.Objects;

import static com.app.wingmate.utils.AppConstants.VALID_PASSWORD_MIN_LENGTH;

public class LoginInteractor {

    interface OnLoginFinishedListener {
        void onInternetError();
        void onEmailError();
        void onInvalidEmailError();
        void onPasswordError();
        void onInvalidPasswordError();
        void onEmailVerificationError(ParseException e);
        void onLoginSuccess(ParseUser parseUser);
        void onResponseError(ParseException e);
    }

    public void loginMeViaParse(Context context, final String email, final String password, final OnLoginFinishedListener listener) {
        if (TextUtils.isEmpty(email)) listener.onEmailError();
        else if (!Utilities.isValidEmail(email)) listener.onInvalidEmailError();
        else if (TextUtils.isEmpty(password)) listener.onPasswordError();
        else if (password.length() < VALID_PASSWORD_MIN_LENGTH) listener.onInvalidPasswordError();
        else if (!Utilities.isInternetAvailable(context)) listener.onInternetError();
        else {
            ParseUser.logInInBackground(email, password, (parseUser, e) -> {
                if (parseUser != null) listener.onLoginSuccess(parseUser);
                else if (Objects.requireNonNull(e.getMessage()).equalsIgnoreCase("User email is not verified."))
                    listener.onEmailVerificationError(e);
                else listener.onResponseError(e);

            });
        }
    }
}