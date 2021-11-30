package com.app.wingmate;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

import com.app.wingmate.admin.models.AdminUsers;
import com.app.wingmate.admin.models.RejectionReason;
import com.app.wingmate.models.Fans;
import com.app.wingmate.models.Instants;
import com.app.wingmate.models.LaunchCampaign;
import com.app.wingmate.models.Question;
import com.app.wingmate.models.QuestionOption;
import com.app.wingmate.models.Quotes;
import com.app.wingmate.models.TermsConditions;
import com.app.wingmate.models.UserAnswer;
import com.app.wingmate.models.UserProfilePhotoVideo;
import com.app.wingmate.utils.AppConstants;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseObject;

import java.util.TimeZone;

public class WingMateApplication extends Application {

    private String GCM_SENDER_ID = "537652241854";

    @Override
    public void onCreate() {
        super.onCreate();
        //AppConstants.DEFAULT_TIMEZONE = TimeZone.getDefault().getID();
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        ParseObject.registerSubclass(Question.class);
        ParseObject.registerSubclass(QuestionOption.class);
        ParseObject.registerSubclass(UserAnswer.class);
        ParseObject.registerSubclass(TermsConditions.class);
        ParseObject.registerSubclass(UserProfilePhotoVideo.class);
        ParseObject.registerSubclass(Fans.class);
        ParseObject.registerSubclass(RejectionReason.class);
        ParseObject.registerSubclass(LaunchCampaign.class);
        ParseObject.registerSubclass(AdminUsers.class);
        ParseObject.registerSubclass(Quotes.class);
        ParseObject.registerSubclass(Instants.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.back4app_app_id))
                .clientKey(getString(R.string.back4app_client_key))
                .server(getString(R.string.back4app_server_url))
                .build()
        );

        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("GCMSenderId", GCM_SENDER_ID);
        installation.put("isAdmin", false);
        installation.saveInBackground();
    }
}
