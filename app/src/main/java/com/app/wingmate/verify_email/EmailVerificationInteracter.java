package com.app.wingmate.verify_email;

import android.content.Context;

import com.app.wingmate.utils.Utilities;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;

import java.util.HashMap;

import static com.app.wingmate.utils.APIsUtility.PARSE_CLOUD_FUNCTION_RESEND_EMAIL;

public class EmailVerificationInteracter {

    interface OnEmailVerificationFinishedListener {
        void onInternetError();
        void onResponseSuccess();
        void onResponseError(ParseException e);
    }

    public void resendEmailViaCloudCode(Context context, final String email, final OnEmailVerificationFinishedListener listener) {
        if (!Utilities.isInternetAvailable(context)) listener.onInternetError();
        else {
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("email", email);
            ParseCloud.callFunctionInBackground(PARSE_CLOUD_FUNCTION_RESEND_EMAIL, params, new FunctionCallback<String>() {
                @Override
                public void done(String object, ParseException e) {
                    if (e == null) {
                        listener.onResponseSuccess();
                    } else {
                        listener.onResponseError(e);
                    }
                }
            });
        }
    }
}
