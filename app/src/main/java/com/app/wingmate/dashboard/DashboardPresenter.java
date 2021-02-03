package com.app.wingmate.dashboard;

import android.content.Context;

import com.app.wingmate.models.Question;
import com.parse.ParseException;

import java.util.List;

public class DashboardPresenter implements DashboardInteractor.OnFinishedListener {

    private DashboardView view;
    private DashboardInteractor interactor;

    public DashboardPresenter(DashboardView view, DashboardInteractor interactor) {
        this.interactor = interactor;
        this.view = view;
    }

    public void queryQuestions(Context context) {
        if (view != null) {
            interactor.fetchQuestionsFormParse(context, this);
        }
    }

    @Override
    public void onQuestionSuccess(List<Question> objects) {
        if (view != null) {
            view.setQuestionsResponseSuccess(objects);
        }
    }

    @Override
    public void onInternetError() {
        if (view != null) {
            view.setInternetError();
        }
    }

    @Override
    public void onResponseError(ParseException e) {
        if (view != null) {
            view.setResponseError(e);
        }
    }
}