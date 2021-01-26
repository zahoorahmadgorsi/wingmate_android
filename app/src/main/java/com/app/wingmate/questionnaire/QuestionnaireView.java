package com.app.wingmate.questionnaire;

import com.app.wingmate.base.BaseView;
import com.app.wingmate.models.Question;
import com.app.wingmate.models.QuestionOption;
import com.app.wingmate.models.UserAnswer;
import com.parse.ParseException;
import com.parse.ParseObject;

import java.util.List;

public interface QuestionnaireView extends BaseView {
    void setQuestionResponseSuccess(List<Question> questions);
    void setOptionsResponseSuccess(List<QuestionOption> questionOptions);
    void setUserAnswersResponseSuccess(UserAnswer userAnswer);
    void setUserAnswersResponseError(ParseException e);
}
