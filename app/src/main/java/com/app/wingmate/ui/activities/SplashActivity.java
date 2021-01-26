package com.app.wingmate.ui.activities;

import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.app.wingmate.R;
import com.app.wingmate.utils.ActivityUtility;
import com.app.wingmate.utils.AppConstants;
import com.parse.ParseUser;

import static com.app.wingmate.utils.AppConstants.PARAM_EMAIL_VERIFIED;
import static com.app.wingmate.utils.AppConstants.PARAM_NICK;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_DUMMY;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_EMAIL_VERIFY;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_HOME;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_PRE_LOGIN;

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
//                else if (!ParseUser.getCurrentUser().getBoolean(PARAM_QUESTIONNAIRE_FILLED)) {
//                    ActivityUtility.startActivity(SplashActivity.this, KEY_FRAGMENT_QUESTIONNAIRE);
//                }
                    else {
                        ActivityUtility.startActivity(SplashActivity.this, KEY_FRAGMENT_HOME);
                    }
                } else {
                    ActivityUtility.startActivity(SplashActivity.this, KEY_FRAGMENT_PRE_LOGIN);
                }
                SplashActivity.this.finish();
            }
        }, AppConstants.SPLASH_DISPLAY_LENGTH);
    }
}