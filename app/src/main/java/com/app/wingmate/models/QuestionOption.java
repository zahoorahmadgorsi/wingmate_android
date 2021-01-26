package com.app.wingmate.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

import java.util.Date;

import static com.app.wingmate.utils.AppConstants.PARAM_COUNTRY_FLAG_IMAGE;
import static com.app.wingmate.utils.AppConstants.PARAM_OPTION_ID;
import static com.app.wingmate.utils.AppConstants.PARAM_QUESTION_ID;
import static com.app.wingmate.utils.AppConstants.PARAM_TITLE;

@ParseClassName("QuestionOption")
public class QuestionOption extends ParseObject {

    private String title;
    private Question questionId;
    private int optionId;
    private ParseFile countryFlagImage;
    private boolean isSelected = false;

    public QuestionOption() {

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

    public String getOptionTitle() {
        return getString(PARAM_TITLE);
    }

    public Question getQuestionId() {
        return (Question) getParseObject(PARAM_QUESTION_ID);
    }

    public int getOptionId() {
        return getInt(PARAM_OPTION_ID);
    }

    public ParseFile getCountryFlagImage() {
        return getParseFile(PARAM_COUNTRY_FLAG_IMAGE);
    }

    public boolean isSelected() {
        return isSelected;
    }
}
