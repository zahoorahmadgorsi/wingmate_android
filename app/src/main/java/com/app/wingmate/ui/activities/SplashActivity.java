package com.app.wingmate.ui.activities;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.app.wingmate.R;
import com.app.wingmate.utils.ActivityUtility;
import com.app.wingmate.utils.AppConstants;
import com.parse.ParseUser;

import static com.app.wingmate.utils.AppConstants.MANDATORY;
import static com.app.wingmate.utils.AppConstants.OPTIONAL;
import static com.app.wingmate.utils.AppConstants.PARAM_EMAIL_VERIFIED;
import static com.app.wingmate.utils.AppConstants.PARAM_MANDATORY_QUESTIONNAIRE_FILLED;
import static com.app.wingmate.utils.AppConstants.PARAM_OPTIONAL_QUESTIONNAIRE_FILLED;
import static com.app.wingmate.utils.AppConstants.PARAM_PROFILE_PIC;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_DASHBOARD;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_PRE_LOGIN;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_QUESTIONNAIRE;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_UPLOAD_PHOTO_VIDEO_PROFILE;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (ParseUser.getCurrentUser() != null) {
                    if (!ParseUser.getCurrentUser().getBoolean(PARAM_EMAIL_VERIFIED)) {
                        ParseUser.logOut();
                        ActivityUtility.startActivity(SplashActivity.this, KEY_FRAGMENT_PRE_LOGIN);
//                        ActivityUtility.startEmailVerifyActivity(SplashActivity.this, KEY_FRAGMENT_EMAIL_VERIFY, ParseUser.getCurrentUser().getEmail(), ParseUser.getCurrentUser().getString(PARAM_NICK));
                    }

//                    else {
//                        ActivityUtility.startActivity(SplashActivity.this, KEY_FRAGMENT_DASHBOARD);
//                    }
                    else if (!ParseUser.getCurrentUser().getBoolean(PARAM_MANDATORY_QUESTIONNAIRE_FILLED)) {
                        ActivityUtility.startQuestionnaireActivity(SplashActivity.this, KEY_FRAGMENT_QUESTIONNAIRE, MANDATORY);
                    } else if (!ParseUser.getCurrentUser().getBoolean(PARAM_OPTIONAL_QUESTIONNAIRE_FILLED)) {
                        ActivityUtility.startQuestionnaireActivity(SplashActivity.this, KEY_FRAGMENT_QUESTIONNAIRE, OPTIONAL);
                    } else if (ParseUser.getCurrentUser().getString(PARAM_PROFILE_PIC) == null
                            || TextUtils.isEmpty(ParseUser.getCurrentUser().getString(PARAM_PROFILE_PIC))) {
                        ActivityUtility.startProfileMediaActivity(SplashActivity.this, KEY_FRAGMENT_UPLOAD_PHOTO_VIDEO_PROFILE);
                    } else {
                        ActivityUtility.startActivity(SplashActivity.this, KEY_FRAGMENT_DASHBOARD);
                    }
                } else {
                    ActivityUtility.startActivity(SplashActivity.this, KEY_FRAGMENT_PRE_LOGIN);
                }
                SplashActivity.this.finish();
            }
        }, AppConstants.SPLASH_DISPLAY_LENGTH);
    }
}