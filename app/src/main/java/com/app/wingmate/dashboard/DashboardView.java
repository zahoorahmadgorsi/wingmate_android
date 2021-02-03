package com.app.wingmate.dashboard;

import com.app.wingmate.base.BaseView;
import com.app.wingmate.models.Question;

import java.util.List;

public interface DashboardView extends BaseView {
    void setQuestionsResponseSuccess(List<Question> questions);
}
