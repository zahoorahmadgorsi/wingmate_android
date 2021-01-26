package com.app.wingmate;

import android.app.Application;

import com.app.wingmate.models.Question;
import com.app.wingmate.models.QuestionOption;
import com.app.wingmate.models.TermsConditions;
import com.app.wingmate.models.UserAnswer;
import com.app.wingmate.models.UserProfilePhotoVideo;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseObject;

public class WingMateApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Question.class);
        ParseObject.registerSubclass(QuestionOption.class);
        ParseObject.registerSubclass(UserAnswer.class);
        ParseObject.registerSubclass(TermsConditions.class);
        ParseObject.registerSubclass(UserProfilePhotoVideo.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.back4app_app_id))
                .clientKey(getString(R.string.back4app_client_key))
                .server(getString(R.string.back4app_server_url))
                .build()
        );

        ParseInstallation.getCurrentInstallation().saveInBackground();
    }
}
