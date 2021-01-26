package com.app.wingmate.models;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.Date;
import java.util.List;

import static com.app.wingmate.utils.AppConstants.PARAM_OPTIONS_IDS;
import static com.app.wingmate.utils.AppConstants.PARAM_OPTIONS_OBJ_ARRAY;
import static com.app.wingmate.utils.AppConstants.PARAM_OPTIONS_RELATION;
import static com.app.wingmate.utils.AppConstants.PARAM_QUESTION_ID;
import static com.app.wingmate.utils.AppConstants.PARAM_USER_ID;

@ParseClassName("UserAnswer")
public class UserAnswer extends ParseObject {

    private Question questionId;
    private ParseUser userId;
    ParseRelation questionOptionsRelation;
    private List<String> selectedOptionIds;
    private List<QuestionOption> optionsObjArray;
    private ParseUser dummyUser;

    public UserAnswer() {

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

    public Question getQuestionId() {
        return (Question) getParseObject(PARAM_QUESTION_ID);
    }

    public ParseUser getUserId() {
        return getParseUser(PARAM_USER_ID);
    }

    public List<String> getSelectedOptionIds() {
        return getList(PARAM_OPTIONS_IDS);
    }

    public List<QuestionOption> getOptionsObjArray() {
//        if (getList(PARAM_OPTIONS_OBJ_ARRAY) != null && getList(PARAM_OPTIONS_OBJ_ARRAY).size() > 0) {
//            List<QuestionOption> arr = getList(PARAM_OPTIONS_OBJ_ARRAY);
////            try {
//                System.out.println("====getOptionsObjArray1====" + arr.get(0).getOptionTitle());
////                System.out.println("====getOptionsObjArray2====" + arr.get(0).fetchIfNeeded().getString("title"));
////            } catch (ParseException e) {
////                e.printStackTrace();
////            }
//            System.out.println("====getOptionsObjArray3====" + arr.size());
//        }
        return getList(PARAM_OPTIONS_OBJ_ARRAY);
    }

    public ParseRelation getQuestionOptionsRelation() {
        return getRelation(PARAM_OPTIONS_RELATION);
    }

    public ParseUser getDummyUser() {
        return dummyUser;
    }

    public void setDummyUser(ParseUser dummyUser) {
        this.dummyUser = dummyUser;
    }
}