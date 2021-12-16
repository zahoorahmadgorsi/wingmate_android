package com.app.wingmate.ui.activities;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.app.wingmate.R;
import com.app.wingmate.utils.ActivityUtility;
import com.app.wingmate.utils.AppConstants;
import com.parse.GetCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import static com.app.wingmate.utils.AppConstants.DEFAULT_TIMEZONE;
import static com.app.wingmate.utils.AppConstants.ERROR;
import static com.app.wingmate.utils.AppConstants.INFO;
import static com.app.wingmate.utils.AppConstants.MANDATORY;
import static com.app.wingmate.utils.AppConstants.OPTIONAL;
import static com.app.wingmate.utils.AppConstants.PARAM_ACCOUNT_STATUS;
import static com.app.wingmate.utils.AppConstants.PARAM_EMAIL_VERIFIED;
import static com.app.wingmate.utils.AppConstants.PARAM_IS_MEDIA_APPROVED;
import static com.app.wingmate.utils.AppConstants.PARAM_IS_PAID_USER;
import static com.app.wingmate.utils.AppConstants.PARAM_IS_PHOTO_SUBMITTED;
import static com.app.wingmate.utils.AppConstants.PARAM_IS_VIDEO_SUBMITTED;
import static com.app.wingmate.utils.AppConstants.PARAM_MANDATORY_QUESTIONNAIRE_FILLED;
import static com.app.wingmate.utils.AppConstants.PARAM_OPTIONAL_QUESTIONNAIRE_FILLED;
import static com.app.wingmate.utils.AppConstants.PARAM_PROFILE_PIC;
import static com.app.wingmate.utils.AppConstants.PENDING;
import static com.app.wingmate.utils.AppConstants.REJECTED;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_CHAT;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_DASHBOARD;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_LAUNCH_CAMPAIGN;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_MEMBERSHIP;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_NEXTSTEP_MEMBERSHIP;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_PAYMENT;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_PRE_LOGIN;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_QUESTIONNAIRE;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_UPLOAD_PHOTO_VIDEO_PROFILE;
import static com.app.wingmate.utils.Utilities.showToast;

import org.joda.time.LocalTime;

import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

public class SplashActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        setStatusBarColor(R.color.bg_color,true);
        String id = getIntent().getStringExtra("userId");
        String username = getIntent().getStringExtra("userName");
        String title = getIntent().getStringExtra("title");
        DEFAULT_TIMEZONE = TimeZone.getDefault().getID();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (ParseUser.getCurrentUser() != null) {
                    ParseUser.getCurrentUser().fetchInBackground((GetCallback<ParseUser>) (parseUser, e) -> {
                        if (!ParseUser.getCurrentUser().getBoolean(PARAM_EMAIL_VERIFIED)) {
                            ParseUser.logOut();
                            ActivityUtility.startActivity(SplashActivity.this, KEY_FRAGMENT_PRE_LOGIN);
                        } else if (ParseUser.getCurrentUser().getInt(PARAM_ACCOUNT_STATUS) == REJECTED) {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(SplashActivity.this);
                            dialog
//                                    .setTitle(getString(R.string.app_name))
//                                    .setIcon(R.drawable.app_heart)
                                    .setCancelable(false)
                                    .setMessage("Your profile has been rejected by the admin!")
                                    .setNegativeButton("OK", (dialoginterface, i) -> {
                                        dialoginterface.cancel();
                                        ParseUser.logOut();
                                        ActivityUtility.startActivity(SplashActivity.this, KEY_FRAGMENT_PRE_LOGIN);
                                    }).show();
                        }
//                        else if (!ParseUser.getCurrentUser().getBoolean(PARAM_IS_PHOTO_SUBMITTED) || !ParseUser.getCurrentUser().getBoolean(PARAM_IS_VIDEO_SUBMITTED)) {
//                            ActivityUtility.startProfileMediaActivity(SplashActivity.this, KEY_FRAGMENT_UPLOAD_PHOTO_VIDEO_PROFILE);
//                        }
//                        else if (!ParseUser.getCurrentUser().getBoolean(PARAM_MANDATORY_QUESTIONNAIRE_FILLED)) {
//                            ActivityUtility.startQuestionnaireActivity(SplashActivity.this, KEY_FRAGMENT_QUESTIONNAIRE, MANDATORY);
//                        }
                        else {
                            //ActivityUtility.startActivity(SplashActivity.this, KEY_FRAGMENT_DASHBOARD);
                            if ((id!=null && username!=null) || title!=null ){
                                //ActivityUtility.startChatActivityOnly(SplashActivity.this,KEY_FRAGMENT_CHAT,id,username);
                                ActivityUtility.startActivity(SplashActivity.this,KEY_FRAGMENT_DASHBOARD);
                            }else{
                                ActivityUtility.startActivity(SplashActivity.this, KEY_FRAGMENT_DASHBOARD);
                            }
                        }
                    });
                } else {
                    ActivityUtility.startActivity(SplashActivity.this, KEY_FRAGMENT_PRE_LOGIN);
                    //ActivityUtility.startActivity(SplashActivity.this, KEY_FRAGMENT_MEMBERSHIP);
                    //ActivityUtility.startActivity(SplashActivity.this, KEY_FRAGMENT_LAUNCH_CAMPAIGN);
                    //ActivityUtility.startActivity(SplashActivity.this, KEY_FRAGMENT_NEXTSTEP_MEMBERSHIP);
                    //ActivityUtility.startActivity(SplashActivity.this, KEY_FRAGMENT_CHAT);
                }
            }
        }, AppConstants.SPLASH_DISPLAY_LENGTH);
    }
    public void setStatusBarColor(int color, boolean isLightStatusBar){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            if (isLightStatusBar){
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                //getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                getWindow().setStatusBarColor(ContextCompat.getColor(SplashActivity.this,color));
            }else{
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                //getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                getWindow().setStatusBarColor(ContextCompat.getColor(SplashActivity.this,color));
            }
        }
    }
}