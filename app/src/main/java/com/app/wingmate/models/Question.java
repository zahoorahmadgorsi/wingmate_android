package com.app.wingmate.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.app.wingmate.utils.AppConstants.PARAM_DISPLAY_ORDER;
import static com.app.wingmate.utils.AppConstants.PARAM_OPTIONS_OBJ_ARRAY;
import static com.app.wingmate.utils.AppConstants.PARAM_PROFILE_DISPLAY_ORDER;
import static com.app.wingmate.utils.AppConstants.PARAM_QUESTION_TYPE;
import static com.app.wingmate.utils.AppConstants.PARAM_SHORT_TITLE;
import static com.app.wingmate.utils.AppConstants.PARAM_TITLE;

@ParseClassName("Question")
public class Question extends ParseObject {

    private List<QuestionOption> options;
    private UserAnswer userAnswer;

    public Question() {

    }

    @Override
    public String getObjectId() {
        return super.getObjectId();
    }

    @Override
    public Date getUpdatedAt() {
        return super.getUpdatedAt();
    }

    @Override
    public Date getCreatedAt() {
        return super.getCreatedAt();
    }

    public String getQuestionTitle() {
        return getString(PARAM_TITLE);
    }

    public String getQuestionType() {
        return getString(PARAM_QUESTION_TYPE);
    }

    public int getQuestionDisplayOrder() {
        return getInt(PARAM_DISPLAY_ORDER);
    }

    public int getProfileDisplayOrder() {
        return getInt(PARAM_PROFILE_DISPLAY_ORDER);
    }

    public List<QuestionOption> getOptions() {
        return options;
    }

    public UserAnswer getUserAnswer() {
        return userAnswer;
    }

    public void setOptions(List<QuestionOption> options) {
        this.options = options;
    }

    public void setUserAnswer(UserAnswer userAnswer) {
        this.userAnswer = userAnswer;
    }

    public String getShortTitle() {
        return getString(PARAM_SHORT_TITLE);
    }

    public List<QuestionOption> getOptionsObjArray() {
        return getList(PARAM_OPTIONS_OBJ_ARRAY);
    }
}