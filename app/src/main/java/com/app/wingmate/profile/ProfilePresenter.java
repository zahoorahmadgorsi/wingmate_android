package com.app.wingmate.profile;

import android.content.Context;

import com.app.wingmate.models.Question;
import com.app.wingmate.models.TermsConditions;
import com.app.wingmate.models.UserAnswer;
import com.app.wingmate.models.UserProfilePhotoVideo;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.List;

public class ProfilePresenter implements ProfileInteractor.OnFinishedListener {

    private ProfileView view;
    private ProfileInteractor interactor;

    public ProfilePresenter(ProfileView view, ProfileInteractor interactor) {
        this.interactor = interactor;
        this.view = view;
    }

    public void queryQuestions(Context context) {
        if (view != null) {
            interactor.fetchQuestionsFormParse(context, this);
        }
    }

    public void queryUserAnswers(Context context, ParseUser parseUser) {
        if (view != null) {
            interactor.fetchUserAnswersFormParse(context, parseUser, this);
        }
    }

    public void queryUserPhotosVideo(Context context, ParseUser parseUser) {
        if (view != null) {
            interactor.fetchUserProfilePhotosVideoFormParse(context, parseUser, this);
        }
    }

    public void queryTermsConditions(Context context) {
        if (view != null) {
            interactor.fetchTermsConditionsFormParse(context, this);
        }
    }

    @Override
    public void onQuestionSuccess(List<Question> objects) {
        if (view != null) {
            view.setQuestionsResponseSuccess(objects);
        }
    }

    @Override
    public void onUserAnswerSuccess(List<UserAnswer> objects) {
        if (view != null) {
            view.setUserAnswersResponseSuccess(objects);
        }
    }

    @Override
    public void onUserProfileSuccess(List<UserProfilePhotoVideo> objects) {
        if (view != null) {
            view.setUserPhotosVideoResponseSuccess(objects);
        }
    }

    @Override
    public void onInternetError() {
        if (view != null) {
            view.setInternetError();
        }
    }

    @Override
    public void onTermsSuccess(List<TermsConditions> objects) {
        if (view != null) {
            view.setTermsResponseSuccess(objects);
        }
    }

    @Override
    public void onResponseError(ParseException e) {
        if (view != null) {
            view.setResponseError(e);
        }
    }

    @Override
    public void onResponseGeneralError(String error) {
        if (view != null) {
            view.setResponseGeneralError(error);
        }
    }
}