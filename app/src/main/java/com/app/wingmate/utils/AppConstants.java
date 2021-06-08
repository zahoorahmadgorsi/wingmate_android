package com.app.wingmate.utils;

public class AppConstants {

    public static final int SPLASH_DISPLAY_LENGTH = 1000;

    public static final int VALID_PASSWORD_MIN_LENGTH = 6;
    public static final int TRIAL_PERIOD = 7;

    public static final int UPDATE_INTERVAL_MINS = 1;

    public static final int REQUEST_CODE_TERMS = 100;

    public static final long CURRENT_TIME_MILLISECONDS = System.currentTimeMillis();
    public static final long TWO_DAYS_AGO_TIME_MILLISECONDS = CURRENT_TIME_MILLISECONDS - (2 * 24 * 3600 * 1000);

    public static final String NATIONALITY_QUESTION_OBJECT_ID = "C5dYcH5GNx";
    public static final String SHORT_TITLE_NATIONALITY = "Nationality";
    public static final String SHORT_TITLE_AGE = "Age";
    public static final String SHORT_TITLE_HEIGHT = "Height";
    public static final String SHORT_TITLE_LOOKING_FOR = "Looking for";

    public static final String SUCCESS = "SUCCESS";
    public static final String ERROR = "ERROR";
    public static final String WARNING = "WARNING";
    public static final String NORMAL = "NORMAL";
    public static final String INFO = "INFO";

    public static final String MANDATORY = "Mandatory";
    public static final String OPTIONAL = "Optional";
    public static final String BOTH = "Both";
    public static final String MALE = "Male";
    public static final String FEMALE = "Female";

    public static final String CLASS_NAME_USER = "User";
    public static final String PARAM_OBJECT_ID = "objectId";
    public static final String PARAM_GENDER = "gender";
    public static final String PARAM_NICK = "nick";
    public static final String PARAM_ABOUT_ME = "aboutMe";
    public static final String PARAM_MANDATORY_QUESTIONNAIRE_FILLED = "isMandatoryQuestionnairesFilled";
    public static final String PARAM_OPTIONAL_QUESTIONNAIRE_FILLED = "isOptionalQuestionnairesFilled";
    public static final String PARAM_IS_PAID_USER = "isPaidUser";
    public static final String PARAM_EMAIL_VERIFIED = "emailVerified";
    public static final String PARAM_PROFILE_PIC = "profilePic";
    public static final String PARAM_USER_MANDATORY_ARRAY = "mandatoryQuestionAnswersList";
    public static final String PARAM_USER_OPTIONAL_ARRAY = "optionalQuestionAnswersList";
    public static final String PARAM_USER_NATIONALITY = "nationality";
    public static final String PARAM_USER_AGE = "age";
    public static final String PARAM_CURRENT_LOCATION = "currentLocation";
    public static final String PARAM_ACCOUNT_STATUS = "accountStatus";
    public static final String PARAM_GROUP_CATEGORY = "groupCategory";
    public static final String PARAM_IS_MEDIA_APPROVED = "isMediaApproved";
    public static final String PARAM_IS_PHOTO_SUBMITTED = "isPhotosSubmitted";
    public static final String PARAM_IS_VIDEO_SUBMITTED = "isVideoSubmitted";

    public static final String PARAM_ALERT_TITLE = "alertTitle";
    public static final String PARAM_ALERT_TEXT = "alertText";

    public static final String PARAM_EMAIL_NEW = "emailNew";
    public static final String PARAM_EMAIL_WRONG = "emailWrong";

    public static final String CLASS_NAME_QUESTION = "Question";
    public static final String PARAM_TITLE = "title";
    public static final String PARAM_SHORT_TITLE = "shortTitle";
    public static final String PARAM_QUESTION_TYPE = "questionType";
    public static final String PARAM_DISPLAY_ORDER = "displayOrder";
    public static final String PARAM_PROFILE_DISPLAY_ORDER = "profileDisplayOrder";

    public static final String CLASS_NAME_QUESTION_OPTION = "QuestionOption";
    public static final String PARAM_QUESTION_ID = "questionId";
    public static final String PARAM_OPTION_ID = "optionId";
    public static final String PARAM_COUNTRY_FLAG_IMAGE = "countryFlagImage";

    public static final String CLASS_NAME_USER_ANSWER = "UserAnswer";
    public static final String PARAM_USER_ID = "userId";
    public static final String PARAM_OPTIONS_IDS = "selectedOptionIds";
    public static final String PARAM_OPTIONS_OBJ_ARRAY = "optionsObjArray";
    public static final String PARAM_OPTIONS_RELATION = "questionOptionsRelation";

    public static final String CLASS_NAME_TERMS = "TermsConditions";
    public static final String PARAM_TERMS_TYPE = "termsType";
    public static final String PARAM_TEXT = "text";
    public static final String PARAM_IS_DO = "isDo";
    public static final String PARAM_FILE = "file";
    public static final String PARAM_FILE_STATUS = "fileStatus";

    public static final String CLASS_NAME_FANS = "Fans";
    public static final String PARAM_FROM_USER = "fromUser";
    public static final String PARAM_TO_USER = "toUser";
    public static final String PARAM_FAN_TYPE = "fanType";

    public static final String FAN_TYPE_LIKE = "Like";
    public static final String FAN_TYPE_CRUSH = "Crush";
    public static final String FAN_TYPE_MAY_BE = "Maybe";

    public static final String PARAM_USER_USER_MANDATORY_ARRAY = PARAM_USER_ID + "." + PARAM_USER_MANDATORY_ARRAY;
    public static final String PARAM_USER_USER_OPTIONAL_ARRAY = PARAM_USER_ID + "." + PARAM_USER_OPTIONAL_ARRAY;

    public static final String CLASS_NAME_USER_PROFILE_PHOTOS_VIDEO = "UserProfilePhotoVideo";
    public static final String PARAM_IS_PHOTO = "isPhoto";

    public static final String KEY_PHOTO = "Photo";
    public static final String KEY_PHOTO_TEXT = "PhotoText";
    public static final String KEY_VIDEO = "Video";
    public static final String KEY_VIDEO_TEXT = "VideoText";

    public static final String CLASS_NAME_VIDEO_LINK = "VideoLink";
    public static final String PARAM_VIDEO_LINK = "videoLink";

    public static final int MODE_PHOTOS = 1;
    public static final int MODE_VIDEO = 2;

    public static final String TAG_PROFILE_EDIT = "tag_profile_edit";
    public static final String TAG_SEARCH = "tag_search";

    public static final String _5M = "5m";
    public static final String _10M = "10m";
    public static final String _50M = "50m";
    public static final String _100M = "100m";
    public static final String _250M = "250m";
    public static final String _1KM = "1km";
    public static final String _5KM = "5km";
    public static final String _10KM = "10km";
    public static final String _100KM = "100km";
    public static final String _1000KM = "1000km";
    public static final String _5000KM = "5000km";

    public static final int PENDING = 0;
    public static final int ACTIVE = 1;
    public static final int REJECTED = 2;

    public static final String GROUP_A = "A";
    public static final String GROUP_B = "B";
    public static final String GROUP_NEW = "N";

    public static final String NOTI_MSG_CRUSH = "You are marked as crush by ";
    public static final String NOTI_MSG_UN_CRUSH = "You are unmarked as crush by ";
    public static final String NOTI_MSG_LIKED = " liked you.";
    public static final String NOTI_MSG_UN_LIKED = " unliked you.";
    public static final String NOTI_MSG_MAYBE = "You are marked as maybe by ";
    public static final String NOTI_MSG_UN_MAYBE = "You are unmarked as maybe by ";
    public static final String NOTI_MSG_SIGNUP = "A new user has just registered to Wing Mate.";
}