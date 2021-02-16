package com.app.wingmate.base;

import android.content.Context;

import com.app.wingmate.models.Question;
import com.app.wingmate.models.QuestionOption;
import com.app.wingmate.models.TermsConditions;
import com.app.wingmate.models.UserAnswer;
import com.app.wingmate.models.UserProfilePhotoVideo;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.List;

public class BasePresenter implements BaseInteractor.OnFinishedListener {

    private BaseView baseView;
    private BaseInteractor interactor;

    public BasePresenter(BaseView baseView, BaseInteractor interactor) {
        this.interactor = interactor;
        this.baseView = baseView;
    }

    public void signUpFormValidate(Context context, String nick, String gender, String email, String password) {
        if (baseView != null) {
            interactor.signUpFormValidate(context, nick, gender, email, password, this);
        }
    }

    public void signUpMeViaParse(Context context, String nick, String gender, String email, String password) {
        if (baseView != null) {
            interactor.signUpMeViaParse(context, nick, gender, email, password, this);
        }
    }

    public void updateEmailViaParse(Context context, String nick, String gender, String email, String password) {
        if (baseView != null) {
            interactor.updateEmailViaParse(context, nick, gender, email, password, this);
        }
    }

    public void getVideoLinkParse(Context context) {
        if (baseView != null) {
            interactor.getVideoLinkParse(context, this);
        }
    }

    public void validateAndLogin(Context context, String email, String password) {
        if (baseView != null) {
            interactor.loginMeViaParse(context, email, password, this);
        }
    }

    public void resendEmailViaCloudCode(Context context, String email) {
        if (baseView != null) {
            interactor.resendEmailViaCloudCode(context, email, this);
        }
    }

    public void validateAndRecover(Context context, String email) {
        if (baseView != null) {
            interactor.recoverPasswordViaParse(context, email, this);
        }
    }

    public void queryQuestions(Context context, String questionType) {
        if (baseView != null) {
            interactor.fetchQuestionsFormParse(context, questionType, this);
        }
    }

    public void queryQuestionOptions(Context context, Question object) {
        if (baseView != null) {
            interactor.fetchQuestionOptionsFormParse(context, object, this);
        }
    }

    public void queryUserAnswers(Context context, Question questionObject) {
        if (baseView != null) {
            interactor.fetchUserAnswersFormParse(context, questionObject, this);
        }
    }

    public void queryQuestions(Context context) {
        if (baseView != null) {
            interactor.fetchQuestionsFormParse(context, this);
        }
    }

    public void queryUserAnswers(Context context, ParseUser parseUser) {
        if (baseView != null) {
            interactor.fetchUserAnswersFormParse(context, parseUser, this);
        }
    }

    public void queryUserPhotosVideo(Context context, ParseUser parseUser) {
        if (baseView != null) {
            interactor.fetchUserProfilePhotosVideoFormParse(context, parseUser, this);
        }
    }

    public void queryTermsConditions(Context context) {
        if (baseView != null) {
            interactor.fetchTermsConditionsFormParse(context, this);
        }
    }

    public void querySearchQuestions(Context context, String questionType) {
        if (baseView != null) {
            interactor.fetchQuestionsFormParse(context, this, questionType);
        }
    }

    public void queryAllUsers(Context context) {
        if (baseView != null) {
            interactor.fetchAllUsersFormParse(context, this);
        }
    }

    public void checkServerDate(Context context) {
        if (baseView != null) {
            interactor.fetchServerDateFormParse(context, this);
        }
    }

    public void getSpecificQuestionUserAnswers(Context context, List<QuestionOption> options) {
        if (baseView != null) {
            interactor.fetchSpecificQuestionUserAnswersFormParse(context, options, this);
        }
    }

    @Override
    public void onQuestionSuccess(List<Question> objects) {
        if (baseView != null) {
            baseView.setQuestionsResponseSuccess(objects);
        }
    }

    @Override
    public void onUserAnswerSuccess(List<UserAnswer> objects) {
        if (baseView != null) {
            baseView.setUserAnswersResponseSuccess(objects);
        }
    }

    @Override
    public void onUserProfileSuccess(List<UserProfilePhotoVideo> objects) {
        if (baseView != null) {
            baseView.setUserPhotosVideoResponseSuccess(objects);
        }
    }

    @Override
    public void onTrialEnded(String msg) {
        if (baseView != null) {
            baseView.setTrialEnded(msg);
        }
    }

    @Override
    public void onSpecificQuestionUserAnswersSuccess(List<UserAnswer> userAnswers) {
        if (baseView != null) {
            baseView.setSpecificQuestionUserAnswersSuccess(userAnswers);
        }
    }

    @Override
    public void onAllUsersSuccess(List<ParseUser> parseUsers) {
        if (baseView != null) {
            baseView.setAllUsersSuccess(parseUsers);
        }
    }

    @Override
    public void onTermsSuccess(List<TermsConditions> objects) {
        if (baseView != null) {
            baseView.setTermsResponseSuccess(objects);
        }
    }

    @Override
    public void onQuestionResponseSuccess(List<Question> objects) {
        if (baseView != null) {
            baseView.setQuestionResponseSuccess(objects);
        }
    }

    @Override
    public void onOptionsResponseSuccess(List<QuestionOption> objects) {
        if (baseView != null) {
            baseView.setOptionsResponseSuccess(objects);
        }
    }

    @Override
    public void onUserAnswersResponseSuccess(UserAnswer object) {
        if (baseView != null) {
            baseView.setUserAnswersResponseSuccess(object);
        }
    }

    @Override
    public void onUserAnswersResponseError(ParseException e) {
        if (baseView != null) {
            baseView.setUserAnswersResponseError(e);
        }
    }

    @Override
    public void onResponseGeneralError(String error) {
        if (baseView != null) {
            baseView.setResponseGeneralError(error);
        }
    }

    @Override
    public void onVideoLinkSuccess(ParseObject parseObject) {
        if (baseView != null) {
            baseView.setVideoLinkSuccess(parseObject);
        }
    }

    @Override
    public void onInternetError() {
        if (baseView != null) {
            baseView.setInternetError();
        }
    }

    @Override
    public void onNickError() {
        if (baseView != null) {
            baseView.setNickError();
        }
    }

    @Override
    public void onGenderError() {
        if (baseView != null) {
            baseView.setGenderError();
        }
    }

    @Override
    public void onEmailError() {
        if (baseView != null) {
            baseView.setEmailError();
        }
    }

    @Override
    public void onInvalidEmailError() {
        if (baseView != null) {
            baseView.setInvalidEmailError();
        }
    }

    @Override
    public void onPasswordError() {
        if (baseView != null) {
            baseView.setPasswordError();
        }
    }

    @Override
    public void onInvalidPasswordError() {
        if (baseView != null) {
            baseView.setInvalidPasswordError();
        }
    }

    @Override
    public void onResponseSuccess() {
        if (baseView != null) {
            baseView.setResponseSuccess();
        }
    }

    @Override
    public void onFormValidateSuccess() {
        if (baseView != null) {
            baseView.setFormValidateSuccess();
        }
    }

    @Override
    public void onResponseError(ParseException e) {
        if (baseView != null) {
            baseView.setResponseError(e);
        }
    }

    @Override
    public void onEmailVerificationError(ParseException e) {
        if (baseView != null) {
            baseView.setEmailVerificationError(e);
        }
    }

    @Override
    public void onLoginSuccess(ParseUser parseUser) {
        if (baseView != null) {
            baseView.setLoginSuccess(parseUser);
        }
    }
}
