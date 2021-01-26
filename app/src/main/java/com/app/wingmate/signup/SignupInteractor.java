package com.app.wingmate.signup;

import android.content.Context;
import android.text.TextUtils;

import com.app.wingmate.utils.Utilities;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.HashMap;
import java.util.List;

import static com.app.wingmate.utils.APIsUtility.PARSE_CLOUD_FUNCTION_UPDATE_WRONG_EMAIL;
import static com.app.wingmate.utils.AppConstants.CLASS_NAME_VIDEO_LINK;
import static com.app.wingmate.utils.AppConstants.PARAM_EMAIL_NEW;
import static com.app.wingmate.utils.AppConstants.PARAM_EMAIL_WRONG;
import static com.app.wingmate.utils.AppConstants.PARAM_GENDER;
import static com.app.wingmate.utils.AppConstants.PARAM_IS_PAID_USER;
import static com.app.wingmate.utils.AppConstants.PARAM_MANDATORY_QUESTIONNAIRE_FILLED;
import static com.app.wingmate.utils.AppConstants.PARAM_NICK;
import static com.app.wingmate.utils.AppConstants.PARAM_OPTIONAL_QUESTIONNAIRE_FILLED;
import static com.app.wingmate.utils.AppConstants.VALID_PASSWORD_MIN_LENGTH;

public class SignupInteractor {

    interface OnSignupFinishedListener {
        void onVideoLinkSuccess(ParseObject parseObject);
        void onInternetError();
        void onNickError();
        void onGenderError();
        void onEmailError();
        void onInvalidEmailError();
        void onPasswordError();
        void onInvalidPasswordError();
        void onResponseSuccess();
        void onFormValidateSuccess();
        void onResponseError(ParseException e);
    }

    public void signUpFormValidate(Context context, final String nick, final String gender, final String email, final String password, final OnSignupFinishedListener listener) {
        if (TextUtils.isEmpty(nick)) listener.onNickError();
        else if (TextUtils.isEmpty(gender)) listener.onGenderError();
        else if (TextUtils.isEmpty(email)) listener.onEmailError();
        else if (!Utilities.isValidEmail(email)) listener.onInvalidEmailError();
        else if (TextUtils.isEmpty(password)) listener.onPasswordError();
        else if (password.length() < VALID_PASSWORD_MIN_LENGTH) listener.onInvalidPasswordError();
        else if (!Utilities.isInternetAvailable(context)) listener.onInternetError();
        else listener.onFormValidateSuccess();
    }

    public void signUpMeViaParse(Context context, final String nick, final String gender, final String email, final String password, final OnSignupFinishedListener listener) {
        if (TextUtils.isEmpty(nick)) listener.onNickError();
        else if (TextUtils.isEmpty(gender)) listener.onGenderError();
        else if (TextUtils.isEmpty(email)) listener.onEmailError();
        else if (!Utilities.isValidEmail(email)) listener.onInvalidEmailError();
        else if (TextUtils.isEmpty(password)) listener.onPasswordError();
        else if (password.length() < VALID_PASSWORD_MIN_LENGTH) listener.onInvalidPasswordError();
        else if (!Utilities.isInternetAvailable(context)) listener.onInternetError();
        else {
            ParseUser user = new ParseUser();
            user.setUsername(email);
            user.setPassword(password);
            user.setEmail(email);
            user.put(PARAM_GENDER, gender);
            user.put(PARAM_NICK, nick);
            user.put(PARAM_IS_PAID_USER, false);
            user.put(PARAM_MANDATORY_QUESTIONNAIRE_FILLED, false);
            user.put(PARAM_OPTIONAL_QUESTIONNAIRE_FILLED, false);
            user.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e1) {
                    if (e1 == null) {
                        listener.onResponseSuccess();
                    } else {
                        ParseUser.logOut();
                        listener.onResponseError(e1);
                    }
                }
            });
        }
    }

    public void updateEmailViaParse(Context context, final String nick, final String gender, final String email, final String password, final OnSignupFinishedListener listener) {
        if (TextUtils.isEmpty(nick)) listener.onNickError();
        else if (TextUtils.isEmpty(gender)) listener.onGenderError();
        else if (TextUtils.isEmpty(email)) listener.onEmailError();
        else if (!Utilities.isValidEmail(email)) listener.onInvalidEmailError();
        else if (TextUtils.isEmpty(password)) listener.onPasswordError();
        else if (password.length() < VALID_PASSWORD_MIN_LENGTH) listener.onInvalidPasswordError();
        else if (!Utilities.isInternetAvailable(context)) listener.onInternetError();
        else {
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put(PARAM_EMAIL_WRONG, ParseUser.getCurrentUser().getEmail());
            params.put(PARAM_EMAIL_NEW, email);
            ParseCloud.callFunctionInBackground(PARSE_CLOUD_FUNCTION_UPDATE_WRONG_EMAIL, params, new FunctionCallback<String>() {
                @Override
                public void done(String object, ParseException e) {
                    if (e == null) {
                        ParseUser.getCurrentUser().setEmail(email);
                        ParseUser.getCurrentUser().setUsername(email);
                        listener.onResponseSuccess();
                    } else {
                        listener.onResponseError(e);
                    }
                }
            });
        }
    }

    public void getVideoLinkParse(Context context, final OnSignupFinishedListener listener) {
        if (!Utilities.isInternetAvailable(context)) listener.onInternetError();
        else {
            ParseQuery query = ParseQuery.getQuery(CLASS_NAME_VIDEO_LINK);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null) listener.onVideoLinkSuccess(objects.get(0));
                    else listener.onResponseError(e);
                }
            });
        }
    }
}