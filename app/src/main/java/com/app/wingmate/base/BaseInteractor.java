package com.app.wingmate.base;

import android.content.Context;
import android.text.TextUtils;

import androidx.appcompat.app.AlertDialog;

import com.app.wingmate.R;
import com.app.wingmate.models.Fans;
import com.app.wingmate.models.Question;
import com.app.wingmate.models.QuestionOption;
import com.app.wingmate.models.TermsConditions;
import com.app.wingmate.models.UserAnswer;
import com.app.wingmate.models.UserProfilePhotoVideo;
import com.app.wingmate.utils.DateUtils;
import com.app.wingmate.utils.Utilities;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static com.app.wingmate.utils.APIsUtility.PARSE_CLOUD_FUNCTION_GET_SERVER_TIME;
import static com.app.wingmate.utils.APIsUtility.PARSE_CLOUD_FUNCTION_RESEND_EMAIL;
import static com.app.wingmate.utils.APIsUtility.PARSE_CLOUD_FUNCTION_UPDATE_WRONG_EMAIL;
import static com.app.wingmate.utils.AppConstants.ACTIVE;
import static com.app.wingmate.utils.AppConstants.BOTH;
import static com.app.wingmate.utils.AppConstants.CLASS_NAME_FANS;
import static com.app.wingmate.utils.AppConstants.CLASS_NAME_QUESTION;
import static com.app.wingmate.utils.AppConstants.CLASS_NAME_QUESTION_OPTION;
import static com.app.wingmate.utils.AppConstants.CLASS_NAME_TERMS;
import static com.app.wingmate.utils.AppConstants.CLASS_NAME_USER;
import static com.app.wingmate.utils.AppConstants.CLASS_NAME_USER_ANSWER;
import static com.app.wingmate.utils.AppConstants.CLASS_NAME_USER_PROFILE_PHOTOS_VIDEO;
import static com.app.wingmate.utils.AppConstants.CLASS_NAME_VIDEO_LINK;
import static com.app.wingmate.utils.AppConstants.ERROR;
import static com.app.wingmate.utils.AppConstants.GROUP_NEW;
import static com.app.wingmate.utils.AppConstants.INFO;
import static com.app.wingmate.utils.AppConstants.PARAM_ACCOUNT_STATUS;
import static com.app.wingmate.utils.AppConstants.PARAM_CURRENT_LOCATION;
import static com.app.wingmate.utils.AppConstants.PARAM_DISPLAY_ORDER;
import static com.app.wingmate.utils.AppConstants.PARAM_EMAIL_NEW;
import static com.app.wingmate.utils.AppConstants.PARAM_EMAIL_WRONG;
import static com.app.wingmate.utils.AppConstants.PARAM_FAN_TYPE;
import static com.app.wingmate.utils.AppConstants.PARAM_FILE_STATUS;
import static com.app.wingmate.utils.AppConstants.PARAM_FROM_USER;
import static com.app.wingmate.utils.AppConstants.PARAM_GENDER;
import static com.app.wingmate.utils.AppConstants.PARAM_GROUP_CATEGORY;
import static com.app.wingmate.utils.AppConstants.PARAM_IS_MEDIA_APPROVED;
import static com.app.wingmate.utils.AppConstants.PARAM_IS_PAID_USER;
import static com.app.wingmate.utils.AppConstants.PARAM_IS_PHOTO_SUBMITTED;
import static com.app.wingmate.utils.AppConstants.PARAM_IS_VIDEO_SUBMITTED;
import static com.app.wingmate.utils.AppConstants.PARAM_MANDATORY_QUESTIONNAIRE_FILLED;
import static com.app.wingmate.utils.AppConstants.PARAM_NICK;
import static com.app.wingmate.utils.AppConstants.PARAM_OBJECT_ID;
import static com.app.wingmate.utils.AppConstants.PARAM_OPTIONAL_QUESTIONNAIRE_FILLED;
import static com.app.wingmate.utils.AppConstants.PARAM_OPTIONS_OBJ_ARRAY;
import static com.app.wingmate.utils.AppConstants.PARAM_OPTION_ID;
import static com.app.wingmate.utils.AppConstants.PARAM_PROFILE_DISPLAY_ORDER;
import static com.app.wingmate.utils.AppConstants.PARAM_QUESTION_ID;
import static com.app.wingmate.utils.AppConstants.PARAM_QUESTION_TYPE;
import static com.app.wingmate.utils.AppConstants.PARAM_TO_USER;
import static com.app.wingmate.utils.AppConstants.PARAM_USER_ID;
import static com.app.wingmate.utils.AppConstants.PARAM_USER_MANDATORY_ARRAY;
import static com.app.wingmate.utils.AppConstants.PARAM_USER_OPTIONAL_ARRAY;
import static com.app.wingmate.utils.AppConstants.PARAM_USER_USER_MANDATORY_ARRAY;
import static com.app.wingmate.utils.AppConstants.PARAM_USER_USER_OPTIONAL_ARRAY;
import static com.app.wingmate.utils.AppConstants.PENDING;
import static com.app.wingmate.utils.AppConstants.REJECTED;
import static com.app.wingmate.utils.AppConstants.SUCCESS;
import static com.app.wingmate.utils.AppConstants.TRIAL_PERIOD;
import static com.app.wingmate.utils.AppConstants.VALID_PASSWORD_MIN_LENGTH;
import static com.app.wingmate.utils.Utilities.showToast;

public class BaseInteractor {

    private int skip = 0;
    private List<UserAnswer> results = new ArrayList<>();
    private List<UserProfilePhotoVideo> photosResults = new ArrayList<>();

    interface OnFinishedListener {
        void onInternetError();

        void onNickError();

        void onGenderError();

        void onEmailError();

        void onInvalidEmailError();

        void onPasswordError();

        void onInvalidPasswordError();

        void onResponseSuccess();

        void onFormValidateSuccess();

        void onVideoLinkSuccess(ParseObject parseObject);

        void onResponseError(ParseException e);

        void onEmailVerificationError(ParseException e);

        void onLoginSuccess(ParseUser parseUser);

        void onQuestionResponseSuccess(List<Question> questions);

        void onOptionsResponseSuccess(List<QuestionOption> questionOptions);

        void onUserAnswersResponseSuccess(UserAnswer userAnswer);

        void onUserAnswersResponseError(ParseException e);

        void onResponseGeneralError(String error);

        void onTermsSuccess(List<TermsConditions> termsConditions);

        void onUserAnswerSuccess(List<UserAnswer> userAnswers);

        void onQuestionSuccess(List<Question> questions);

        void onUserProfileSuccess(List<UserProfilePhotoVideo> userProfilePhotoVideos);

        void onTrialEnded(String msg);

        void onSpecificQuestionUserAnswersSuccess(List<UserAnswer> userAnswers);

        void onAllUsersSuccess(List<ParseUser> parseUsers);

        void onWithInKMUsersSuccess(List<ParseUser> parseUsers);

        void onMyFansSuccess(List<Fans> fans);

        void onUserFanStatusSuccess(List<Fans> fans);

        void onFanAddingSuccess(Fans fan);
    }

    public void signUpFormValidate(Context context, final String nick, final String gender, final String email, final String password, final OnFinishedListener listener) {
        if (TextUtils.isEmpty(nick)) listener.onNickError();
        else if (TextUtils.isEmpty(gender)) listener.onGenderError();
        else if (TextUtils.isEmpty(email)) listener.onEmailError();
        else if (!Utilities.isValidEmail(email)) listener.onInvalidEmailError();
        else if (TextUtils.isEmpty(password)) listener.onPasswordError();
        else if (password.length() < VALID_PASSWORD_MIN_LENGTH) listener.onInvalidPasswordError();
        else if (!Utilities.isInternetAvailable(context)) listener.onInternetError();
        else listener.onFormValidateSuccess();
    }

    public void signUpMeViaParse(Context context, final String nick, final String gender, final String email, final String password, final OnFinishedListener listener) {
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
            user.put(PARAM_IS_MEDIA_APPROVED, false);
            user.put(PARAM_ACCOUNT_STATUS, PENDING);
            user.put(PARAM_GROUP_CATEGORY, GROUP_NEW);
            user.put(PARAM_MANDATORY_QUESTIONNAIRE_FILLED, false);
            user.put(PARAM_OPTIONAL_QUESTIONNAIRE_FILLED, false);
            user.put(PARAM_IS_PHOTO_SUBMITTED, false);
            user.put(PARAM_IS_VIDEO_SUBMITTED, false);
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

    public void updateEmailViaParse(Context context, final String nick, final String gender, final String email, final String password, final OnFinishedListener listener) {
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

    public void getVideoLinkParse(Context context, final OnFinishedListener listener) {
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

    public void loginMeViaParse(Context context, final String email, final String password, final OnFinishedListener listener) {
        if (TextUtils.isEmpty(email)) listener.onEmailError();
        else if (!Utilities.isValidEmail(email)) listener.onInvalidEmailError();
        else if (TextUtils.isEmpty(password)) listener.onPasswordError();
        else if (password.length() < VALID_PASSWORD_MIN_LENGTH) listener.onInvalidPasswordError();
        else if (!Utilities.isInternetAvailable(context)) listener.onInternetError();
        else {
            ParseUser.logInInBackground(email, password, (parseUser, e) -> {
                if (parseUser != null) listener.onLoginSuccess(parseUser);
                else if (Objects.requireNonNull(e.getMessage()).equalsIgnoreCase("User email is not verified."))
                    listener.onEmailVerificationError(e);
                else listener.onResponseError(e);

            });
        }
    }

    public void resendEmailViaCloudCode(Context context, final String email, final OnFinishedListener listener) {
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

    public void recoverPasswordViaParse(Context context, final String email, final OnFinishedListener listener) {
        if (TextUtils.isEmpty(email)) listener.onEmailError();
        else if (!Utilities.isValidEmail(email)) listener.onInvalidEmailError();
        else if (!Utilities.isInternetAvailable(context)) listener.onInternetError();
        else ParseUser.requestPasswordResetInBackground(email,
                    new RequestPasswordResetCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) listener.onResponseSuccess();
                            else listener.onResponseError(e);
                        }
                    });
    }

    public void fetchQuestionsFormParse(final Context context, String questionType, final OnFinishedListener listener) {
        if (!Utilities.isInternetAvailable(context)) listener.onInternetError();
        else {
            ParseQuery query = ParseQuery.getQuery(CLASS_NAME_QUESTION);
            query.orderByAscending(PARAM_DISPLAY_ORDER);
            query.include(PARAM_OPTIONS_OBJ_ARRAY);
            if (!questionType.equals(BOTH)) query.whereEqualTo(PARAM_QUESTION_TYPE, questionType);
            query.findInBackground((FindCallback<Question>) (objects, e) -> {
                if (e == null) {
                    if (objects != null && objects.size() > 0)
                        listener.onQuestionResponseSuccess(objects);
                    else
                        listener.onResponseGeneralError(context.getResources().getString(R.string.no_que_found));
                } else {
                    listener.onResponseError(e);
                }
            });
        }
    }

    public void fetchQuestionOptionsFormParse(final Context context, Question questionObject, final OnFinishedListener listener) {
        if (!Utilities.isInternetAvailable(context)) listener.onInternetError();
        else {
            ParseQuery query = ParseQuery.getQuery(CLASS_NAME_QUESTION_OPTION);
            query.whereEqualTo(PARAM_QUESTION_ID, questionObject);
            query.orderByAscending(PARAM_OPTION_ID);
            query.findInBackground((FindCallback<QuestionOption>) (objects, e) -> {
                if (e == null) {
                    if (objects != null && objects.size() > 0)
                        listener.onOptionsResponseSuccess(objects);
                    else
                        listener.onResponseGeneralError(context.getString(R.string.no_option_found));
                } else {
                    listener.onResponseError(e);
                }
            });

        }
    }

    public void fetchUserAnswersFormParse(Context context, Question questionObject, final OnFinishedListener listener) {
        if (!Utilities.isInternetAvailable(context)) listener.onInternetError();
        else {
            ParseQuery query = ParseQuery.getQuery(CLASS_NAME_USER_ANSWER);
            query.whereEqualTo(PARAM_QUESTION_ID, questionObject);
            query.whereEqualTo(PARAM_USER_ID, ParseUser.getCurrentUser());
            query.include(PARAM_USER_ID);
            query.include(PARAM_QUESTION_ID);
            query.include(PARAM_OPTIONS_OBJ_ARRAY);
            query.getFirstInBackground((GetCallback<UserAnswer>) (object, e) -> {
                if (e == null) {
                    listener.onUserAnswersResponseSuccess(object);
                } else {
                    listener.onUserAnswersResponseError(e);
                }
            });

        }
    }

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
                    if (objects == null || objects.size() == 0) objects = new ArrayList<>();
                    listener.onUserAnswerSuccess(objects);
                } else {
                    listener.onResponseError(e);
                }
            });
        }
    }

    private FindCallback getAllRemainingRecords(final Context context, ParseUser parseUser, final OnFinishedListener listener) {
        return (FindCallback<UserAnswer>) (objects, e) -> {
            if (e == null) {
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
            if (parseUser.getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
                query.whereNotEqualTo(PARAM_FILE_STATUS, REJECTED);
            } else {
                query.whereEqualTo(PARAM_FILE_STATUS, ACTIVE);
            }
//            query.findInBackground(getAllRemainingProfilePhotosRecords(context, parseUser, listener));
            query.findInBackground((FindCallback<UserProfilePhotoVideo>) (objects, e) -> {
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
                    query.whereNotEqualTo(PARAM_FILE_STATUS, REJECTED);
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
                    if (objects == null || objects.size() == 0) objects = new ArrayList<>();
                    listener.onQuestionSuccess(objects);
                } else {
                    listener.onResponseError(e);
                }
            });
        }
    }

    public void fetchServerDateFormParse(final Context context, final OnFinishedListener listener) {
        HashMap<String, Object> params = new HashMap<>();
        ParseCloud.callFunctionInBackground(PARSE_CLOUD_FUNCTION_GET_SERVER_TIME, params, (FunctionCallback<String>) (result, e) -> {
            if (e == null) {
                String serverDate = "";
                serverDate = result;
                SimpleDateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                try {
                    Date SERVER_DATE = isoDateFormat.parse(serverDate);
                    if (DateUtils.daysBetween(ParseUser.getCurrentUser().getCreatedAt(), SERVER_DATE) >= TRIAL_PERIOD) {
                        listener.onTrialEnded("");
                    }
                } catch (java.text.ParseException parseException) {
                    parseException.printStackTrace();
                }
            }
        });

    }

    public void fetchTermsConditionsFormParse(final Context context, final OnFinishedListener listener) {
        if (!Utilities.isInternetAvailable(context)) listener.onInternetError();
        else {
            ParseQuery query = ParseQuery.getQuery(CLASS_NAME_TERMS);
            query.orderByAscending(PARAM_DISPLAY_ORDER);
            query.findInBackground((FindCallback<TermsConditions>) (objects, e) -> {
                if (e == null) {
                    if (objects == null || objects.size() == 0) objects = new ArrayList<>();
                    listener.onTermsSuccess(objects);
                } else {
                    listener.onResponseError(e);
                }
            });
        }
    }

    public void fetchQuestionsFormParse(final Context context, final OnFinishedListener listener, String questionType) {
        if (!Utilities.isInternetAvailable(context)) listener.onInternetError();
        else {
            ParseQuery query = ParseQuery.getQuery(CLASS_NAME_QUESTION);
            query.orderByAscending(PARAM_PROFILE_DISPLAY_ORDER);
            query.include(PARAM_OPTIONS_OBJ_ARRAY);
            if (!questionType.equals(BOTH)) query.whereEqualTo(PARAM_QUESTION_TYPE, questionType);
            query.setLimit(1000);
            query.findInBackground((FindCallback<Question>) (objects, e) -> {
                if (e == null) {
                    if (objects == null) objects = new ArrayList<>();
                    listener.onQuestionSuccess(objects);
                } else {
                    listener.onResponseError(e);
                }
            });
        }
    }

    public void fetchSpecificQuestionUserAnswersFormParse(final Context context, List<QuestionOption> optionsObjArray, final OnFinishedListener listener) {
        if (!Utilities.isInternetAvailable(context)) listener.onInternetError();
        else {
            ParseQuery query = ParseQuery.getQuery(CLASS_NAME_USER_ANSWER);
            query.include(PARAM_USER_ID);
            query.include(PARAM_QUESTION_ID);
            query.include(PARAM_OPTIONS_OBJ_ARRAY);

            query.include(PARAM_USER_ID + "." + PARAM_USER_MANDATORY_ARRAY);
            query.include(PARAM_USER_ID + "." + PARAM_USER_OPTIONAL_ARRAY);

            query.include(PARAM_USER_ID + "." + PARAM_USER_MANDATORY_ARRAY + "." + PARAM_QUESTION_ID);
            query.include(PARAM_USER_ID + "." + PARAM_USER_MANDATORY_ARRAY + "." + PARAM_OPTIONS_OBJ_ARRAY);
            query.include(PARAM_USER_ID + "." + PARAM_USER_OPTIONAL_ARRAY + "." + PARAM_QUESTION_ID);
            query.include(PARAM_USER_ID + "." + PARAM_USER_OPTIONAL_ARRAY + "." + PARAM_OPTIONS_OBJ_ARRAY);

            query.whereContainedIn(PARAM_OPTIONS_OBJ_ARRAY, optionsObjArray);
            query.setLimit(1000);
            query.findInBackground((FindCallback<UserAnswer>) (objects, e) -> {
                if (e == null) {
                    if (objects == null) objects = new ArrayList<>();
                    listener.onSpecificQuestionUserAnswersSuccess(objects);
                } else {
                    listener.onResponseError(e);
                }
            });
        }
    }

    public void fetchUsersWithInKMFormParse(final Context context, ParseGeoPoint myGeoPoint, double distance, final OnFinishedListener listener) {
        if (!Utilities.isInternetAvailable(context)) listener.onInternetError();
        else {
            ParseQuery<ParseUser> query = ParseUser.getQuery();
            query.whereEqualTo(PARAM_ACCOUNT_STATUS, ACTIVE);
            query.include(PARAM_USER_MANDATORY_ARRAY);
            query.include(PARAM_USER_OPTIONAL_ARRAY);
            query.include(PARAM_USER_MANDATORY_ARRAY + "." + PARAM_QUESTION_ID);
            query.include(PARAM_USER_MANDATORY_ARRAY + "." + PARAM_OPTIONS_OBJ_ARRAY);
            query.include(PARAM_USER_OPTIONAL_ARRAY + "." + PARAM_QUESTION_ID);
            query.include(PARAM_USER_OPTIONAL_ARRAY + "." + PARAM_OPTIONS_OBJ_ARRAY);
            query.whereNotEqualTo(PARAM_OBJECT_ID, ParseUser.getCurrentUser().getObjectId());
            query.whereNotEqualTo(PARAM_GENDER, ParseUser.getCurrentUser().getString(PARAM_GENDER));
            query.whereWithinKilometers(PARAM_CURRENT_LOCATION, myGeoPoint, distance);
            query.setLimit(1000);
            query.findInBackground((objects, e) -> {
                if (e == null) {
                    if (objects == null || objects.size() == 0) {
                        objects = new ArrayList<>();
                    }
                    listener.onWithInKMUsersSuccess(objects);
                } else {
                    listener.onResponseError(e);
                }
            });
        }
    }

    public void fetchAllUsersFormParse(final Context context, final OnFinishedListener listener) {
        if (!Utilities.isInternetAvailable(context)) listener.onInternetError();
        else {
            ParseQuery<ParseUser> query = ParseUser.getQuery();
            query.include(PARAM_USER_MANDATORY_ARRAY);
            query.include(PARAM_USER_OPTIONAL_ARRAY);
            query.include(PARAM_USER_MANDATORY_ARRAY + "." + PARAM_QUESTION_ID);
            query.include(PARAM_USER_MANDATORY_ARRAY + "." + PARAM_OPTIONS_OBJ_ARRAY);
            query.include(PARAM_USER_OPTIONAL_ARRAY + "." + PARAM_QUESTION_ID);
            query.include(PARAM_USER_OPTIONAL_ARRAY + "." + PARAM_OPTIONS_OBJ_ARRAY);
            query.whereEqualTo(PARAM_ACCOUNT_STATUS, ACTIVE);
            query.whereNotEqualTo(PARAM_OBJECT_ID, ParseUser.getCurrentUser().getObjectId());
            query.setLimit(1000);
            query.findInBackground((objects, e) -> {
                if (e == null) {
                    if (objects == null || objects.size() == 0) {
                        objects = new ArrayList<>();
                    }
                    listener.onAllUsersSuccess(objects);
                } else {
                    listener.onResponseError(e);
                }
            });
        }
    }

    public void fetchMyFansFormParse(final Context context, final OnFinishedListener listener) {
        if (!Utilities.isInternetAvailable(context)) listener.onInternetError();
        else {
            ParseQuery queryUserToMe = ParseQuery.getQuery(CLASS_NAME_FANS);
            queryUserToMe.whereEqualTo(PARAM_TO_USER, ParseUser.getCurrentUser());

            ParseQuery queryMeToUser = ParseQuery.getQuery(CLASS_NAME_FANS);
            queryMeToUser.whereEqualTo(PARAM_FROM_USER, ParseUser.getCurrentUser());

            List<ParseQuery<Fans>> queries = new ArrayList<>();
            queries.add(queryUserToMe);
            queries.add(queryMeToUser);

            ParseQuery<Fans> mainQuery = ParseQuery.or(queries);
            mainQuery.include(PARAM_FROM_USER);
            mainQuery.include(PARAM_FROM_USER + "." + PARAM_USER_MANDATORY_ARRAY);
            mainQuery.include(PARAM_FROM_USER + "." + PARAM_USER_MANDATORY_ARRAY + "." + PARAM_QUESTION_ID);
            mainQuery.include(PARAM_FROM_USER + "." + PARAM_USER_MANDATORY_ARRAY + "." + PARAM_OPTIONS_OBJ_ARRAY);
            mainQuery.include(PARAM_FROM_USER + "." + PARAM_USER_OPTIONAL_ARRAY);
            mainQuery.include(PARAM_FROM_USER + "." + PARAM_USER_OPTIONAL_ARRAY + "." + PARAM_QUESTION_ID);
            mainQuery.include(PARAM_FROM_USER + "." + PARAM_USER_OPTIONAL_ARRAY + "." + PARAM_OPTIONS_OBJ_ARRAY);
            mainQuery.include(PARAM_TO_USER);
            mainQuery.include(PARAM_TO_USER + "." + PARAM_USER_MANDATORY_ARRAY);
            mainQuery.include(PARAM_TO_USER + "." + PARAM_USER_MANDATORY_ARRAY + "." + PARAM_QUESTION_ID);
            mainQuery.include(PARAM_TO_USER + "." + PARAM_USER_MANDATORY_ARRAY + "." + PARAM_OPTIONS_OBJ_ARRAY);
            mainQuery.include(PARAM_TO_USER + "." + PARAM_USER_OPTIONAL_ARRAY);
            mainQuery.include(PARAM_TO_USER + "." + PARAM_USER_OPTIONAL_ARRAY + "." + PARAM_QUESTION_ID);
            mainQuery.include(PARAM_TO_USER + "." + PARAM_USER_OPTIONAL_ARRAY + "." + PARAM_OPTIONS_OBJ_ARRAY);
            mainQuery.findInBackground((objects, e) -> {
                if (e == null) {
                    if (objects == null) objects = new ArrayList<>();
                    listener.onMyFansSuccess(objects);
                } else {
                    listener.onResponseError(e);
                }
            });

//            ParseQuery query = ParseQuery.getQuery(CLASS_NAME_FANS);
//            query.include(PARAM_FROM_USER);
//            query.include(PARAM_TO_USER);
//            query.include(PARAM_FROM_USER + "." + PARAM_USER_MANDATORY_ARRAY);
//            query.include(PARAM_TO_USER + "." + PARAM_USER_OPTIONAL_ARRAY);
//            query.include(PARAM_FROM_USER + "." + PARAM_USER_MANDATORY_ARRAY + "." + PARAM_QUESTION_ID);
//            query.include(PARAM_FROM_USER + "." + PARAM_USER_MANDATORY_ARRAY + "." + PARAM_OPTIONS_OBJ_ARRAY);
//            query.include(PARAM_TO_USER + "." + PARAM_USER_OPTIONAL_ARRAY + "." + PARAM_QUESTION_ID);
//            query.include(PARAM_TO_USER + "." + PARAM_USER_OPTIONAL_ARRAY + "." + PARAM_OPTIONS_OBJ_ARRAY);
//            query.whereEqualTo(PARAM_TO_USER, ParseUser.getCurrentUser());
//            query.setLimit(1000);
//            query.findInBackground((FindCallback<Fans>) (objects, e) -> {
//                if (e == null) {
//                    if (objects == null) objects = new ArrayList<>();
//                    listener.onMyFansSuccess(objects);
//                    System.out.println("====fans==="+objects.size());
//                } else {
//                    System.out.println("====fans==="+e.getMessage());
//                    listener.onResponseError(e);
//                }
//            });
        }
    }

    public void fetchSpecificTypeFansFormParse(final Context context, String type, final OnFinishedListener listener) {
        if (!Utilities.isInternetAvailable(context)) listener.onInternetError();
        else {
            ParseQuery queryUserToMe = ParseQuery.getQuery(CLASS_NAME_FANS);
            queryUserToMe.whereEqualTo(PARAM_TO_USER, ParseUser.getCurrentUser());
            queryUserToMe.whereEqualTo(PARAM_FAN_TYPE, type);

            ParseQuery queryMeToUser = ParseQuery.getQuery(CLASS_NAME_FANS);
            queryMeToUser.whereEqualTo(PARAM_FROM_USER, ParseUser.getCurrentUser());
            queryMeToUser.whereEqualTo(PARAM_FAN_TYPE, type);

            List<ParseQuery<Fans>> queries = new ArrayList<>();
            queries.add(queryUserToMe);
            queries.add(queryMeToUser);

            ParseQuery<Fans> mainQuery = ParseQuery.or(queries);
            mainQuery.include(PARAM_FROM_USER);
            mainQuery.include(PARAM_FROM_USER + "." + PARAM_USER_MANDATORY_ARRAY);
            mainQuery.include(PARAM_FROM_USER + "." + PARAM_USER_MANDATORY_ARRAY + "." + PARAM_QUESTION_ID);
            mainQuery.include(PARAM_FROM_USER + "." + PARAM_USER_MANDATORY_ARRAY + "." + PARAM_OPTIONS_OBJ_ARRAY);
            mainQuery.include(PARAM_FROM_USER + "." + PARAM_USER_OPTIONAL_ARRAY);
            mainQuery.include(PARAM_FROM_USER + "." + PARAM_USER_OPTIONAL_ARRAY + "." + PARAM_QUESTION_ID);
            mainQuery.include(PARAM_FROM_USER + "." + PARAM_USER_OPTIONAL_ARRAY + "." + PARAM_OPTIONS_OBJ_ARRAY);
            mainQuery.include(PARAM_TO_USER);
            mainQuery.include(PARAM_TO_USER + "." + PARAM_USER_MANDATORY_ARRAY);
            mainQuery.include(PARAM_TO_USER + "." + PARAM_USER_MANDATORY_ARRAY + "." + PARAM_QUESTION_ID);
            mainQuery.include(PARAM_TO_USER + "." + PARAM_USER_MANDATORY_ARRAY + "." + PARAM_OPTIONS_OBJ_ARRAY);
            mainQuery.include(PARAM_TO_USER + "." + PARAM_USER_OPTIONAL_ARRAY);
            mainQuery.include(PARAM_TO_USER + "." + PARAM_USER_OPTIONAL_ARRAY + "." + PARAM_QUESTION_ID);
            mainQuery.include(PARAM_TO_USER + "." + PARAM_USER_OPTIONAL_ARRAY + "." + PARAM_OPTIONS_OBJ_ARRAY);
            mainQuery.findInBackground((objects, e) -> {
                if (e == null) {
                    if (objects == null) objects = new ArrayList<>();
                    listener.onMyFansSuccess(objects);
                } else {
                    listener.onResponseError(e);
                }
            });

//            ParseQuery query = ParseQuery.getQuery(CLASS_NAME_FANS);
//            query.include(PARAM_FROM_USER);
//            query.include(PARAM_TO_USER);
//            query.include(PARAM_FROM_USER + "." + PARAM_USER_MANDATORY_ARRAY);
//            query.include(PARAM_TO_USER + "." + PARAM_USER_OPTIONAL_ARRAY);
//            query.include(PARAM_FROM_USER + "." + PARAM_USER_MANDATORY_ARRAY + "." + PARAM_QUESTION_ID);
//            query.include(PARAM_FROM_USER + "." + PARAM_USER_MANDATORY_ARRAY + "." + PARAM_OPTIONS_OBJ_ARRAY);
//            query.include(PARAM_TO_USER + "." + PARAM_USER_OPTIONAL_ARRAY + "." + PARAM_QUESTION_ID);
//            query.include(PARAM_TO_USER + "." + PARAM_USER_OPTIONAL_ARRAY + "." + PARAM_OPTIONS_OBJ_ARRAY);
//            query.whereEqualTo(PARAM_TO_USER, ParseUser.getCurrentUser());
//            query.setLimit(1000);
//            query.findInBackground((FindCallback<Fans>) (objects, e) -> {
//                if (e == null) {
//                    if (objects == null) objects = new ArrayList<>();
//                    listener.onMyFansSuccess(objects);
//                    System.out.println("====fans==="+objects.size());
//                } else {
//                    System.out.println("====fans==="+e.getMessage());
//                    listener.onResponseError(e);
//                }
//            });
        }
    }

    public void fetchUserFanStatusFormParse(final Context context, ParseUser parseUser, final OnFinishedListener listener) {
        if (!Utilities.isInternetAvailable(context)) listener.onInternetError();
        else {
            ParseQuery query = ParseQuery.getQuery(CLASS_NAME_FANS);
            query.include(PARAM_FROM_USER);
            query.include(PARAM_TO_USER);
            query.include(PARAM_FROM_USER + "." + PARAM_USER_MANDATORY_ARRAY);
            query.include(PARAM_TO_USER + "." + PARAM_USER_OPTIONAL_ARRAY);
            query.include(PARAM_FROM_USER + "." + PARAM_USER_MANDATORY_ARRAY + "." + PARAM_QUESTION_ID);
            query.include(PARAM_FROM_USER + "." + PARAM_USER_MANDATORY_ARRAY + "." + PARAM_OPTIONS_OBJ_ARRAY);
            query.include(PARAM_TO_USER + "." + PARAM_USER_OPTIONAL_ARRAY + "." + PARAM_QUESTION_ID);
            query.include(PARAM_TO_USER + "." + PARAM_USER_OPTIONAL_ARRAY + "." + PARAM_OPTIONS_OBJ_ARRAY);
            query.whereEqualTo(PARAM_FROM_USER, ParseUser.getCurrentUser());
            query.whereEqualTo(PARAM_TO_USER, parseUser);
            query.setLimit(1000);
            query.findInBackground((FindCallback<Fans>) (objects, e) -> {
                if (e == null) {
                    if (objects == null) objects = new ArrayList<>();
                    listener.onUserFanStatusSuccess(objects);
                } else {
                    listener.onResponseError(e);
                }
            });
        }
    }

    public void setUserAsFan(final Context context, ParseUser toUser, String fanType, final OnFinishedListener listener) {
        if (!Utilities.isInternetAvailable(context)) listener.onInternetError();
        else {
            Fans fan = new Fans();
            fan.put(PARAM_FROM_USER, ParseUser.getCurrentUser());
            fan.put(PARAM_TO_USER, toUser);
            fan.put(PARAM_FAN_TYPE, fanType);
            fan.saveInBackground(e -> {
                if (e == null) {
                    listener.onFanAddingSuccess(fan);
                } else {
                    listener.onResponseError(e);
                }
            });
        }
    }
}
