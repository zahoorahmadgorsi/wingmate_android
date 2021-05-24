package com.app.wingmate.ui.activities;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.app.wingmate.R;
import com.app.wingmate.utils.ActivityUtility;
import com.app.wingmate.utils.AppConstants;
import com.parse.GetCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import static com.app.wingmate.utils.AppConstants.ERROR;
import static com.app.wingmate.utils.AppConstants.INFO;
import static com.app.wingmate.utils.AppConstants.MANDATORY;
import static com.app.wingmate.utils.AppConstants.OPTIONAL;
import static com.app.wingmate.utils.AppConstants.PARAM_ACCOUNT_STATUS;
import static com.app.wingmate.utils.AppConstants.PARAM_EMAIL_VERIFIED;
import static com.app.wingmate.utils.AppConstants.PARAM_IS_MEDIA_APPROVED;
import static com.app.wingmate.utils.AppConstants.PARAM_IS_PAID_USER;
import static com.app.wingmate.utils.AppConstants.PARAM_MANDATORY_QUESTIONNAIRE_FILLED;
import static com.app.wingmate.utils.AppConstants.PARAM_OPTIONAL_QUESTIONNAIRE_FILLED;
import static com.app.wingmate.utils.AppConstants.PARAM_PROFILE_PIC;
import static com.app.wingmate.utils.AppConstants.PENDING;
import static com.app.wingmate.utils.AppConstants.REJECTED;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_DASHBOARD;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_PAYMENT;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_PRE_LOGIN;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_QUESTIONNAIRE;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_UPLOAD_PHOTO_VIDEO_PROFILE;
import static com.app.wingmate.utils.Utilities.showToast;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (ParseUser.getCurrentUser() != null) {
                    System.out.println("====current user status1111==="+ParseUser.getCurrentUser().getInt(PARAM_ACCOUNT_STATUS));
                    ParseUser.getCurrentUser().fetchInBackground((GetCallback<ParseUser>) (parseUser, e) -> {
                        System.out.println("====current user status==="+parseUser.getInt(PARAM_ACCOUNT_STATUS));
                        System.out.println("====current user status222==="+ParseUser.getCurrentUser().getInt(PARAM_ACCOUNT_STATUS));

                        if (!ParseUser.getCurrentUser().getBoolean(PARAM_EMAIL_VERIFIED)) {
                            ParseUser.logOut();
                            ActivityUtility.startActivity(SplashActivity.this, KEY_FRAGMENT_PRE_LOGIN);
//                        ActivityUtility.startEmailVerifyActivity(SplashActivity.this, KEY_FRAGMENT_EMAIL_VERIFY, ParseUser.getCurrentUser().getEmail(), ParseUser.getCurrentUser().getString(PARAM_NICK));
                        } else if (ParseUser.getCurrentUser().getInt(PARAM_ACCOUNT_STATUS) == REJECTED) {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(SplashActivity.this);
                            dialog.setTitle(getString(R.string.app_name))
                                    .setIcon(R.drawable.app_heart)
                                    .setCancelable(false)
                                    .setMessage("Your profile has been rejected by the admin!")
                                    .setNegativeButton("OK", (dialoginterface, i) -> {
                                        dialoginterface.cancel();
                                        ParseUser.logOut();
                                        ActivityUtility.startActivity(SplashActivity.this, KEY_FRAGMENT_PRE_LOGIN);
                                    }).show();
                        } else if (!ParseUser.getCurrentUser().getBoolean(PARAM_IS_MEDIA_APPROVED)) {
                            ActivityUtility.startProfileMediaActivity(SplashActivity.this, KEY_FRAGMENT_UPLOAD_PHOTO_VIDEO_PROFILE);
//                            SplashActivity.this.finish();
                        }
//                    else if (ParseUser.getCurrentUser().getString(PARAM_PROFILE_PIC) == null
//                            || TextUtils.isEmpty(ParseUser.getCurrentUser().getString(PARAM_PROFILE_PIC))) {
//                        ActivityUtility.startProfileMediaActivity(SplashActivity.this, KEY_FRAGMENT_UPLOAD_PHOTO_VIDEO_PROFILE);
//                    }
//                    else if (!ParseUser.getCurrentUser().getBoolean(PARAM_MANDATORY_QUESTIONNAIRE_FILLED)) {
//                        ActivityUtility.startQuestionnaireActivity(SplashActivity.this, KEY_FRAGMENT_QUESTIONNAIRE, MANDATORY);
//                    }
////                    else if (!ParseUser.getCurrentUser().getBoolean(PARAM_OPTIONAL_QUESTIONNAIRE_FILLED)) {
////                        ActivityUtility.startQuestionnaireActivity(SplashActivity.this, KEY_FRAGMENT_QUESTIONNAIRE, OPTIONAL);
////                    }
                        else {
                            ActivityUtility.startActivity(SplashActivity.this, KEY_FRAGMENT_DASHBOARD);
                        }
                    });
                } else {
                    ActivityUtility.startActivity(SplashActivity.this, KEY_FRAGMENT_PRE_LOGIN);
                }
            }
        }, AppConstants.SPLASH_DISPLAY_LENGTH);
    }
}