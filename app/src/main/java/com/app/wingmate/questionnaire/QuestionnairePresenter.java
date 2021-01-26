package com.app.wingmate.questionnaire;

import android.content.Context;

import com.app.wingmate.base.BaseView;
import com.app.wingmate.models.Question;
import com.app.wingmate.models.QuestionOption;
import com.app.wingmate.models.UserAnswer;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.List;

public class QuestionnairePresenter implements QuestionnaireInteractor.OnQuestionnaireFinishedListener {

    private QuestionnaireView view;
    private QuestionnaireInteractor interactor;

    public QuestionnairePresenter(QuestionnaireView view, QuestionnaireInteractor interactor) {
        this.interactor = interactor;
        this.view = view;
    }

    public void queryQuestions(Context context, String questionType) {
        if (view != null) {
            interactor.fetchQuestionsFormParse(context, questionType, this);
        }
    }

    public void queryQuestionOptions(Context context, Question object) {
        if (view != null) {
            interactor.fetchQuestionOptionsFormParse(context, object, this);
        }
    }

    public void queryUserAnswers(Context context, Question questionObject) {
        if (view != null) {
            interactor.fetchUserAnswersFormParse(context, questionObject, this);
        }
    }

    @Override
    public void onInternetError() {
        if (view != null) {
            view.setInternetError();
        }
    }

    @Override
    public void onQuestionResponseSuccess(List<Question> objects) {
        if (view != null) {
            view.setQuestionResponseSuccess(objects);
        }
    }

    @Override
    public void onOptionsResponseSuccess(List<QuestionOption> objects) {
        if (view != null) {
            view.setOptionsResponseSuccess(objects);
        }
    }

    @Override
    public void onUserAnswersResponseSuccess(UserAnswer object) {
        if (view != null) {
            view.setUserAnswersResponseSuccess(object);
        }
    }

    @Override
    public void onUserAnswersResponseError(ParseException e) {
        if (view != null) {
            view.setUserAnswersResponseError(e);
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
