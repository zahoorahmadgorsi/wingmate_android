package com.app.wingmate.base;

import com.app.wingmate.models.Fans;
import com.app.wingmate.models.Question;
import com.app.wingmate.models.QuestionOption;
import com.app.wingmate.models.TermsConditions;
import com.app.wingmate.models.UserAnswer;
import com.app.wingmate.models.UserProfilePhotoVideo;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.List;

public interface BaseView {

    // General Methods
    void showProgress();
    void dismissProgress();
    void setInternetError();
    void setResponseSuccess();
    void setResponseError(ParseException e);
    void setResponseGeneralError(String error);
    void setTrialEnded(String msg, boolean showLoader, boolean isJustRefresh);
    void setHasTrial(int days, boolean showLoader, boolean isJustRefresh);

    //Common Methods
    void setEmailError();
    void setInvalidEmailError();
    void setPasswordError();
    void setInvalidPasswordError();

    //Sign Up Methods
    void setVideoLinkSuccess(ParseObject parseObject);
    void setNickError();
    void setGenderError();
    void setFormValidateSuccess();

    //Login Methods
    void setEmailVerificationError(ParseException e);
    void setLoginSuccess(ParseUser parseUser);

    // Questionnaire Methods
    void setQuestionResponseSuccess(List<Question> questions);
    void setOptionsResponseSuccess(List<QuestionOption> questionOptions);
    void setUserAnswersResponseSuccess(UserAnswer userAnswer);
    void setUserAnswersResponseError(ParseException e);

    // Profile Methods
    void setTermsResponseSuccess(List<TermsConditions> termsConditions);
    void setQuestionsResponseSuccess(List<Question> questions);
    void setUserAnswersResponseSuccess(List<UserAnswer> userAnswers);
    void setUserPhotosVideoResponseSuccess(List<UserProfilePhotoVideo> userProfilePhotoVideos);
    void setUserFanStatusSuccess(List<Fans> fansList);

    // Search
    void setSpecificQuestionUserAnswersSuccess(List<UserAnswer> userAnswers);
    void setWithInKMUsersSuccess(List<ParseUser> parseUsers);

    //Home
    void setAllUsersSuccess(List<ParseUser> parseUsers);

    //Fans
    void setMyFansSuccess(List<Fans> fansList);
    void setFanAddedSuccess(Fans fan);
}
