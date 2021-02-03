package com.app.wingmate.questionnaire;

import android.content.Context;

import com.app.wingmate.R;
import com.app.wingmate.models.Question;
import com.app.wingmate.models.QuestionOption;
import com.app.wingmate.models.UserAnswer;
import com.app.wingmate.utils.Utilities;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

import static com.app.wingmate.utils.AppConstants.BOTH;
import static com.app.wingmate.utils.AppConstants.CLASS_NAME_QUESTION;
import static com.app.wingmate.utils.AppConstants.CLASS_NAME_QUESTION_OPTION;
import static com.app.wingmate.utils.AppConstants.CLASS_NAME_USER_ANSWER;
import static com.app.wingmate.utils.AppConstants.PARAM_DISPLAY_ORDER;
import static com.app.wingmate.utils.AppConstants.PARAM_OPTIONS_OBJ_ARRAY;
import static com.app.wingmate.utils.AppConstants.PARAM_OPTION_ID;
import static com.app.wingmate.utils.AppConstants.PARAM_QUESTION_ID;
import static com.app.wingmate.utils.AppConstants.PARAM_QUESTION_TYPE;
import static com.app.wingmate.utils.AppConstants.PARAM_USER_ID;

public class QuestionnaireInteractor {

    interface OnQuestionnaireFinishedListener {
        void onInternetError();

        void onQuestionResponseSuccess(List<Question> questions);

        void onOptionsResponseSuccess(List<QuestionOption> questionOptions);

        void onUserAnswersResponseSuccess(UserAnswer userAnswer);

        void onUserAnswersResponseError(ParseException e);

        void onResponseError(ParseException e);

        void onResponseGeneralError(String error);
    }

    public void fetchQuestionsFormParse(final Context context, String questionType, final OnQuestionnaireFinishedListener listener) {
        if (!Utilities.isInternetAvailable(context)) listener.onInternetError();
        else {
            ParseQuery query = ParseQuery.getQuery(CLASS_NAME_QUESTION);
            query.orderByAscending(PARAM_DISPLAY_ORDER);
            if (!questionType.equals(BOTH)) query.whereEqualTo(PARAM_QUESTION_TYPE, questionType);
            query.findInBackground((FindCallback<Question>) (objects, e) -> {
                if (e == null) {
                    if (objects != null && objects.size() > 0)
                        listener.onQuestionResponseSuccess(objects);
                    else
                        listener.onResponseGeneralError(context.getResources().getString(R.string.no_que_found));
                } else {
                    System.out.println("===1==");
                    listener.onResponseError(e);
                }
            });
        }
    }

    public void fetchQuestionOptionsFormParse(final Context context, Question questionObject, final OnQuestionnaireFinishedListener listener) {
        if (!Utilities.isInternetAvailable(context)) listener.onInternetError();
        else {
            ParseQuery query = ParseQuery.getQuery(CLASS_NAME_QUESTION_OPTION);
            query.whereEqualTo(PARAM_QUESTION_ID, questionObject);
            query.orderByAscending(PARAM_OPTION_ID);
            query.findInBackground((FindCallback<QuestionOption>) (objects, e) -> {
                if (e == null) {
                    if (objects != null && objects.size() > 0)
                        listener.onOptionsResponseSuccess(objects);
                    else
                        listener.onResponseGeneralError(context.getString(R.string.no_option_found));
                } else {
                    System.out.println("===2==");
                    listener.onResponseError(e);
                }
            });

        }
    }

    public void fetchUserAnswersFormParse(Context context, Question questionObject, final OnQuestionnaireFinishedListener listener) {
        if (!Utilities.isInternetAvailable(context)) listener.onInternetError();
        else {
            ParseQuery query = ParseQuery.getQuery(CLASS_NAME_USER_ANSWER);
            query.whereEqualTo(PARAM_QUESTION_ID, questionObject);
            query.whereEqualTo(PARAM_USER_ID, ParseUser.getCurrentUser());
            query.include(PARAM_USER_ID);
            query.include(PARAM_QUESTION_ID);
            query.include(PARAM_OPTIONS_OBJ_ARRAY);
            query.getFirstInBackground((GetCallback<UserAnswer>) (object, e) -> {
                if (e == null) {
                    listener.onUserAnswersResponseSuccess(object);
                } else {
                    System.out.println("===3==");
                    listener.onUserAnswersResponseError(e);
                }
            });

        }
    }
}
