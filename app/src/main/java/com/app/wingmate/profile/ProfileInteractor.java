package com.app.wingmate.profile;

import android.content.Context;

import com.app.wingmate.R;
import com.app.wingmate.models.Question;
import com.app.wingmate.models.TermsConditions;
import com.app.wingmate.models.UserAnswer;
import com.app.wingmate.models.UserProfilePhotoVideo;
import com.app.wingmate.questionnaire.QuestionnaireInteractor;
import com.app.wingmate.utils.Utilities;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import static com.app.wingmate.utils.AppConstants.CLASS_NAME_QUESTION;
import static com.app.wingmate.utils.AppConstants.CLASS_NAME_TERMS;
import static com.app.wingmate.utils.AppConstants.CLASS_NAME_USER_ANSWER;
import static com.app.wingmate.utils.AppConstants.CLASS_NAME_USER_PROFILE_PHOTOS_VIDEO;
import static com.app.wingmate.utils.AppConstants.PARAM_DISPLAY_ORDER;
import static com.app.wingmate.utils.AppConstants.PARAM_OPTIONS_OBJ_ARRAY;
import static com.app.wingmate.utils.AppConstants.PARAM_PROFILE_DISPLAY_ORDER;
import static com.app.wingmate.utils.AppConstants.PARAM_QUESTION_ID;
import static com.app.wingmate.utils.AppConstants.PARAM_QUESTION_TYPE;
import static com.app.wingmate.utils.AppConstants.PARAM_USER_ID;

public class ProfileInteractor {

    interface OnFinishedListener {
        void onInternetError();

        void onTermsSuccess(List<TermsConditions> termsConditions);

        void onUserAnswerSuccess(List<UserAnswer> userAnswers);

        void onQuestionSuccess(List<Question> questions);

        void onUserProfileSuccess(List<UserProfilePhotoVideo> userProfilePhotoVideos);

        void onResponseError(ParseException e);

        void onResponseGeneralError(String error);
    }

    private int skip = 0;
    private List<UserAnswer> results = new ArrayList<>();
    private List<UserProfilePhotoVideo> photosResults = new ArrayList<>();

    public void fetchUserAnswersFormParse(final Context context, ParseUser parseUser, final OnFinishedListener listener) {
        if (!Utilities.isInternetAvailable(context)) listener.onInternetError();
        else {
            skip = 0;
            results = new ArrayList<>();
            ParseQuery query = ParseQuery.getQuery(CLASS_NAME_USER_ANSWER);
            query.setLimit(1000);
            query.whereEqualTo(PARAM_USER_ID, parseUser);
            query.include(PARAM_USER_ID);
            query.include(PARAM_QUESTION_ID);
            query.include(PARAM_OPTIONS_OBJ_ARRAY);
//            query.findInBackground(getAllRemainingRecords(context, parseUser, listener));
            query.findInBackground((FindCallback<UserAnswer>) (objects, e) -> {
                if (e == null) {
                    if (objects != null && objects.size() > 0)
                        listener.onUserAnswerSuccess(objects);
//                    else
//                        listener.onResponseGeneralError(context.getResources().getString(R.string.no_record_found));
                } else {
                    listener.onResponseError(e);
                }
            });
        }
    }

    private FindCallback getAllRemainingRecords(final Context context, ParseUser parseUser, final OnFinishedListener listener) {
        return (FindCallback<UserAnswer>) (objects, e) -> {
            if (e == null) {
                System.out.println("===objects==" + objects.size());
                results.addAll(objects);
                int limit = 1000;
                if (objects.size() == limit) {
                    skip = skip + limit;
                    ParseQuery query = ParseQuery.getQuery(CLASS_NAME_USER_ANSWER);
                    query.setSkip(skip);
                    query.setLimit(limit);
                    query.whereEqualTo(PARAM_USER_ID, parseUser);
                    query.include(PARAM_USER_ID);
                    query.include(PARAM_QUESTION_ID);
                    query.include(PARAM_OPTIONS_OBJ_ARRAY);
                    query.findInBackground(getAllRemainingRecords(context, parseUser, listener));
                } else {
                    if (results == null) results = new ArrayList<>();
                    listener.onUserAnswerSuccess(results);
                }
            } else {
                listener.onResponseError(e);
            }
        };
    }

    public void fetchUserProfilePhotosVideoFormParse(final Context context, ParseUser parseUser, final OnFinishedListener listener) {
        if (!Utilities.isInternetAvailable(context)) listener.onInternetError();
        else {
            skip = 0;
            photosResults = new ArrayList<>();
            ParseQuery query = ParseQuery.getQuery(CLASS_NAME_USER_PROFILE_PHOTOS_VIDEO);
            query.setLimit(1000);
            query.whereEqualTo(PARAM_USER_ID, parseUser.getObjectId());
//            query.findInBackground(getAllRemainingProfilePhotosRecords(context, parseUser, listener));
            query.findInBackground((FindCallback<UserProfilePhotoVideo>) (objects, e) -> {
//                if (e == null) {
//                    if (objects != null && objects.size() > 0)
//                        listener.onUserProfileSuccess(objects);
//                } else {
//                    listener.onResponseError(e);
//                }
                if (e != null) {
                    listener.onResponseError(e);
                    objects = new ArrayList<>();
                }
                listener.onUserProfileSuccess(objects);
            });
        }
    }

    private FindCallback getAllRemainingProfilePhotosRecords(final Context context, ParseUser parseUser, final OnFinishedListener listener) {
        return (FindCallback<UserProfilePhotoVideo>) (objects, e) -> {
            if (e == null) {
                photosResults.addAll(objects);
                int limit = 1000;
                if (objects.size() == limit) {
                    skip = skip + limit;
                    ParseQuery query = ParseQuery.getQuery(CLASS_NAME_USER_PROFILE_PHOTOS_VIDEO);
                    query.setSkip(skip);
                    query.setLimit(limit);
                    query.whereEqualTo(PARAM_USER_ID, parseUser.getObjectId());
                    query.findInBackground(getAllRemainingProfilePhotosRecords(context, parseUser, listener));
                } else {
                    if (photosResults == null) photosResults = new ArrayList<>();
                    listener.onUserProfileSuccess(photosResults);
                }
            } else {
                listener.onResponseError(e);
            }
        };
    }

    public void fetchQuestionsFormParse(final Context context, final OnFinishedListener listener) {
        if (!Utilities.isInternetAvailable(context)) listener.onInternetError();
        else {
            ParseQuery query = ParseQuery.getQuery(CLASS_NAME_QUESTION);
            query.orderByAscending(PARAM_PROFILE_DISPLAY_ORDER);
            query.include(PARAM_OPTIONS_OBJ_ARRAY);
            query.setLimit(1000);
            query.findInBackground((FindCallback<Question>) (objects, e) -> {
                if (e == null) {
                    if (objects != null && objects.size() > 0)
                        listener.onQuestionSuccess(objects);
//                    else
//                        listener.onResponseGeneralError(context.getResources().getString(R.string.no_que_found));
                } else {
                    listener.onResponseError(e);
                }
            });
        }
    }

    public void fetchTermsConditionsFormParse(final Context context, final OnFinishedListener listener) {
        if (!Utilities.isInternetAvailable(context)) listener.onInternetError();
        else {
            ParseQuery query = ParseQuery.getQuery(CLASS_NAME_TERMS);
            query.orderByAscending(PARAM_DISPLAY_ORDER);
            query.findInBackground((FindCallback<TermsConditions>) (objects, e) -> {
                if (e == null) {
                    if (objects != null && objects.size() > 0)
                        listener.onTermsSuccess(objects);
                } else {
                    listener.onResponseError(e);
                }
            });
        }
    }


}
