package com.app.wingmate.utils;

import android.app.Activity;
import android.content.Intent;

import com.app.wingmate.R;
import com.app.wingmate.models.UserAnswer;
import com.app.wingmate.ui.activities.MainActivity;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import static com.app.wingmate.utils.CommonKeys.KEY_ACTIVITY_TAG;
import static com.app.wingmate.utils.CommonKeys.KEY_BACK_TAG;
import static com.app.wingmate.utils.CommonKeys.KEY_EMAIL_TAG;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_CROP;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_DUMMY;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_EDIT_PROFILE;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_EDIT_PROFILE_TEXT_FIELDS;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_FORGOT_PASSWORD;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_HOME;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_LOGIN;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_DASHBOARD;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_PAYMENT;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_PRE_LOGIN;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_PROFILE;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_QUESTIONNAIRE;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_SIGN_UP;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_TERMS_AND_CONDITIONS;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_UPLOAD_PHOTO_VIDEO_PROFILE;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_VIDEO_VIEW;
import static com.app.wingmate.utils.CommonKeys.KEY_IMAGES_ARRAY;
import static com.app.wingmate.utils.CommonKeys.KEY_IMAGE_INDEX;
import static com.app.wingmate.utils.CommonKeys.KEY_IMAGE_LINK;
import static com.app.wingmate.utils.CommonKeys.KEY_IS_CURRENT_USER;
import static com.app.wingmate.utils.CommonKeys.KEY_NICK_TAG;
import static com.app.wingmate.utils.CommonKeys.KEY_PARSE_USER;
import static com.app.wingmate.utils.CommonKeys.KEY_QUESTION_TYPE;
import static com.app.wingmate.utils.CommonKeys.KEY_TITLE;
import static com.app.wingmate.utils.CommonKeys.KEY_USER_ANSWERS;
import static com.app.wingmate.utils.CommonKeys.KEY_VIDEO_LINK;

public class ActivityUtility {

    public static void startActivity(Activity activity, String tag) {
        Intent mainIntent = new Intent(activity, MainActivity.class);
        mainIntent.putExtra(KEY_ACTIVITY_TAG, tag);
        switch (tag) {
            case KEY_FRAGMENT_LOGIN:
            case KEY_FRAGMENT_DUMMY:
            case KEY_FRAGMENT_DASHBOARD:
            case KEY_FRAGMENT_HOME:
            case KEY_FRAGMENT_PRE_LOGIN:
            case KEY_FRAGMENT_QUESTIONNAIRE:
            case KEY_FRAGMENT_PAYMENT:
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            case KEY_FRAGMENT_CROP:
            case KEY_FRAGMENT_SIGN_UP:
            case KEY_FRAGMENT_FORGOT_PASSWORD:
            case KEY_FRAGMENT_TERMS_AND_CONDITIONS:
            case KEY_FRAGMENT_PROFILE:
            case KEY_FRAGMENT_EDIT_PROFILE:
            case KEY_FRAGMENT_EDIT_PROFILE_TEXT_FIELDS:
            case KEY_FRAGMENT_UPLOAD_PHOTO_VIDEO_PROFILE:
            case KEY_FRAGMENT_VIDEO_VIEW:
                activity.startActivity(mainIntent);
                activity.overridePendingTransition(R.anim.right_to_left, R.anim.blank_anim);
                break;
            default:
                break;
        }
    }

    public static void startVideoViewActivity(Activity context, String tag, String path) {
        Intent mainIntent = new Intent(context, MainActivity.class);
        mainIntent.putExtra(KEY_ACTIVITY_TAG, tag);
        mainIntent.putExtra(KEY_VIDEO_LINK, path);
        context.startActivity(mainIntent);
        context.overridePendingTransition(R.anim.right_to_left, R.anim.blank_anim);
    }

    public static void startUploadMediaActivity(Activity context, String tag) {
        Intent mainIntent = new Intent(context, MainActivity.class);
        mainIntent.putExtra(KEY_ACTIVITY_TAG, tag);
        context.startActivity(mainIntent);
        context.overridePendingTransition(R.anim.right_to_left, R.anim.blank_anim);
    }

    public static void startPhotoViewActivity(Activity context, String tag, ArrayList<String> paths, int index) {
        Intent mainIntent = new Intent(context, MainActivity.class);
        mainIntent.putExtra(KEY_ACTIVITY_TAG, tag);
        mainIntent.putExtra(KEY_IMAGE_LINK, paths.get(0));
        mainIntent.putStringArrayListExtra(KEY_IMAGES_ARRAY, paths);
        mainIntent.putExtra(KEY_IMAGE_INDEX, index);
        context.startActivity(mainIntent);
        context.overridePendingTransition(R.anim.right_to_left, R.anim.blank_anim);
    }

    public static void startEmailVerifyActivity(Activity context, String tag, String emailId, String nick) {
        Intent mainIntent = new Intent(context, MainActivity.class);
        mainIntent.putExtra(KEY_ACTIVITY_TAG, tag);
        mainIntent.putExtra(KEY_EMAIL_TAG, emailId);
        mainIntent.putExtra(KEY_NICK_TAG, nick);
        context.startActivity(mainIntent);
        context.overridePendingTransition(R.anim.right_to_left, R.anim.blank_anim);
    }

    public static void startProfileActivity(Activity context, String tag, boolean isCurrentUser, ParseUser parseUser) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(KEY_ACTIVITY_TAG, tag);
        intent.putExtra(KEY_IS_CURRENT_USER, isCurrentUser);
        intent.putExtra(KEY_PARSE_USER, parseUser);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.right_to_left, R.anim.blank_anim);
    }

    public static void startEditProfileActivity(Activity context, String tag, ArrayList<UserAnswer> userAnswerList) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(KEY_ACTIVITY_TAG, tag);
        intent.putParcelableArrayListExtra(KEY_USER_ANSWERS, userAnswerList);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.right_to_left, R.anim.blank_anim);
    }

    public static void startEditProfileFieldsActivity(Activity context, String tag, String title) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(KEY_ACTIVITY_TAG, tag);
        intent.putExtra(KEY_TITLE, title);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.right_to_left, R.anim.blank_anim);
    }

    public static void startQuestionnaireActivity(Activity context, String tag, String questionType, boolean isClear) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(KEY_ACTIVITY_TAG, tag);
        intent.putExtra(KEY_QUESTION_TYPE, questionType);
        intent.putExtra(KEY_BACK_TAG, isClear);
        if (isClear)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.right_to_left, R.anim.blank_anim);
    }

    public static void startProfileMediaActivity(Activity context, String tag, boolean isClear) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(KEY_ACTIVITY_TAG, tag);
        intent.putExtra(KEY_BACK_TAG, isClear);
        if (isClear)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.right_to_left, R.anim.blank_anim);
    }

    public static void startPaymentActivity(Activity context, String tag, boolean isClear) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(KEY_ACTIVITY_TAG, tag);
        intent.putExtra(KEY_BACK_TAG, isClear);
        if (isClear)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.right_to_left, R.anim.blank_anim);
    }
}