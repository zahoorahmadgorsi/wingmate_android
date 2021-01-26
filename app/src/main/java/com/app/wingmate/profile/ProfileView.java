package com.app.wingmate.profile;

import com.app.wingmate.base.BaseView;
import com.app.wingmate.models.Question;
import com.app.wingmate.models.TermsConditions;
import com.app.wingmate.models.UserAnswer;
import com.app.wingmate.models.UserProfilePhotoVideo;

import java.util.List;

public interface ProfileView extends BaseView {
    void setTermsResponseSuccess(List<TermsConditions> termsConditions);
    void setQuestionsResponseSuccess(List<Question> questions);
    void setUserAnswersResponseSuccess(List<UserAnswer> userAnswers);
    void setUserPhotosVideoResponseSuccess(List<UserProfilePhotoVideo> userProfilePhotoVideos);
}