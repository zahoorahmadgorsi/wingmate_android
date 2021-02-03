package com.app.wingmate.dashboard;

import android.content.Context;

import com.app.wingmate.models.Question;
import com.app.wingmate.utils.Utilities;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import static com.app.wingmate.utils.AppConstants.CLASS_NAME_QUESTION;
import static com.app.wingmate.utils.AppConstants.PARAM_OPTIONS_OBJ_ARRAY;
import static com.app.wingmate.utils.AppConstants.PARAM_PROFILE_DISPLAY_ORDER;

public class DashboardInteractor {

    interface OnFinishedListener {
        void onInternetError();

        void onQuestionSuccess(List<Question> questions);

        void onResponseError(ParseException e);
    }

    public void fetchQuestionsFormParse(final Context context, final OnFinishedListener listener) {
        if (!Utilities.isInternetAvailable(context)) listener.onInternetError();
        else {
            ParseQuery query = ParseQuery.getQuery(CLASS_NAME_QUESTION);
            query.orderByAscending(PARAM_PROFILE_DISPLAY_ORDER);
            query.include(PARAM_OPTIONS_OBJ_ARRAY);
            query.setLimit(1000);
            query.findInBackground((FindCallback<Question>) (objects, e) -> {
                if (e == null) {
                    if (objects == null) objects = new ArrayList<>();
                    listener.onQuestionSuccess(objects);
                } else {
                    listener.onResponseError(e);
                }
            });
        }
    }


}
