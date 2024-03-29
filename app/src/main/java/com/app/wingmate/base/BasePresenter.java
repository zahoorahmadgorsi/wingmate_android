package com.app.wingmate.base;

import android.content.Context;

import com.app.wingmate.admin.models.RejectionReason;
import com.app.wingmate.models.Fans;
import com.app.wingmate.models.Question;
import com.app.wingmate.models.QuestionOption;
import com.app.wingmate.models.TermsConditions;
import com.app.wingmate.models.UserAnswer;
import com.app.wingmate.models.UserProfilePhotoVideo;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
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

    public void adminQueryUserPhotosVideo(Context context, ParseUser parseUser) {
        if (baseView != null) {
            interactor.adminFetchUserProfilePhotosVideoFormParse(context, parseUser, this);
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

    public void queryAdminAllUsers(Context context) {
        if (baseView != null) {
            interactor.adminFetchAllUsersFormParse(context, this);
        }
    }

    public void queryAllMyFans(Context context) {
        if (baseView != null) {
            interactor.fetchMyFansFormParse(context, this);
        }
    }

    public void queryMySpecificTypeFans(Context context, String type) {
        if (baseView != null) {
            interactor.fetchSpecificTypeFansFormParse(context, type, this);
        }
    }

    public void queryUserFansStatus(Context context, ParseUser parseUser) {
        if (baseView != null) {
            interactor.fetchUserFanStatusFormParse(context, parseUser, this);
        }
    }

    public void checkServerDate(Context context, boolean showLoader, boolean isJustRefresh) {
        if (baseView != null) {
            interactor.fetchServerDateFormParse(context, showLoader, isJustRefresh, this);
        }
    }

    public void getSpecificQuestionUserAnswers(Context context, List<QuestionOption> options) {
        if (baseView != null) {
            interactor.fetchSpecificQuestionUserAnswersFormParse(context, options, this);
        }
    }

    public void getUsersWithInKM(Context context, ParseGeoPoint myGeoPoint, double distance) {
        if (baseView != null) {
            interactor.fetchUsersWithInKMFormParse(context, myGeoPoint, distance, this);
        }
    }

    public void adminGetUsersWithInKM(Context context, ParseGeoPoint myGeoPoint, double distance) {
        if (baseView != null) {
            interactor.adminFetchUsersWithInKMFormParse(context, myGeoPoint, distance, this);
        }
    }

    public void setFan(Context context, ParseUser parseUser, String fanType) {
        if (baseView != null) {
            interactor.setUserAsFan(context, parseUser, fanType, this);
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
    public void onTrialEnded(String msg, boolean showLoader, boolean isJustRefresh) {
        if (baseView != null) {
            baseView.setTrialEnded(msg, showLoader, isJustRefresh);
        }
    }

    @Override
    public void onHasTrial(int days, boolean showLoader, boolean isJustRefresh) {
        if (baseView != null) {
            baseView.setHasTrial(days, showLoader, isJustRefresh);
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
    public void onWithInKMUsersSuccess(List<ParseUser> parseUsers) {
        if (baseView != null) {
            baseView.setWithInKMUsersSuccess(parseUsers);
        }
    }

    @Override
    public void onMyFansSuccess(List<Fans> fans) {
        if (baseView != null) {
            baseView.setMyFansSuccess(fans);
        }
    }

    @Override
    public void onUserFanStatusSuccess(List<Fans> fans) {
        if (baseView != null) {
            baseView.setUserFanStatusSuccess(fans);
        }
    }

    @Override
    public void onFanAddingSuccess(Fans fan) {
        if (baseView != null) {
            baseView.setFanAddedSuccess(fan);
        }
    }

    @Override
    public void onRejectReasonsResponseSuccess(List<RejectionReason> rejectReasons) {
        if (baseView != null) {
            baseView.setRejectReasonResponseSuccess(rejectReasons);
        }
    }

    @Override
    public void onSpecificUsersSuccess(ParseUser parseUser) {
        if (baseView != null) {
            baseView.setSpecificUserSuccess(parseUser);
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

    public void queryRejectReasons(Context context) {
        if (baseView != null) {
            interactor.fetchRejectReasonsFormParse(context, this);
        }
    }

    public void querySpecificUser(Context context, ParseUser parseUser) {
        if (baseView != null) {
            interactor.fetchSpecificUserFormParse(context, parseUser, this);
        }
    }

    public void updateUserViaCloudCode(Context context,
                                       String userId,
                                       String category,
                                       int status,
                                       String reason,
                                       String comment,
                                       boolean isMediaApproved,
                                       boolean isMediaPending
    ) {
        if (baseView != null) {
            interactor.updateUserViaCloudCode(context, userId, category, status, reason, comment, isMediaApproved, isMediaPending, this);
        }
    }

}
