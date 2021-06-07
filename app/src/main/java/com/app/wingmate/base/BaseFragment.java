package com.app.wingmate.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.app.wingmate.models.Fans;
import com.app.wingmate.models.Question;
import com.app.wingmate.models.QuestionOption;
import com.app.wingmate.models.TermsConditions;
import com.app.wingmate.models.UserAnswer;
import com.app.wingmate.models.UserProfilePhotoVideo;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.HashMap;
import java.util.List;

import pl.aprilapps.easyphotopicker.ChooserType;
import pl.aprilapps.easyphotopicker.EasyImage;

import static com.app.wingmate.utils.APIsUtility.PARSE_CLOUD_FUNCTION_EMAIL_TO_USER;
import static com.app.wingmate.utils.APIsUtility.PARSE_CLOUD_FUNCTION_PUSH_TO_ADMIN;
import static com.app.wingmate.utils.APIsUtility.PARSE_CLOUD_FUNCTION_PUSH_TO_USER;
import static com.app.wingmate.utils.AppConstants.ERROR;
import static com.app.wingmate.utils.AppConstants.PARAM_ALERT_TEXT;
import static com.app.wingmate.utils.AppConstants.PARAM_ALERT_TITLE;
import static com.app.wingmate.utils.AppConstants.PARAM_USER_ID;
import static com.app.wingmate.utils.AppConstants.SUCCESS;
import static com.app.wingmate.utils.AppConstants.WARNING;
import static com.app.wingmate.utils.Utilities.showToast;

public class BaseFragment extends Fragment implements BaseView {

    //    public ProgressDialog dialog;
    public KProgressHUD dialog;
    public EasyImage easyImage;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        dialog = ProgressDialog.newInstance(getActivity());
//        dialog.setCancelable(false);

        dialog = KProgressHUD.create(getActivity())
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
//                .setLabel("Please wait")
//                .setDetailsLabel("Loading data...")
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);

        easyImage = new EasyImage.Builder(requireContext())
                .setChooserTitle("Select Image")
                .setChooserType(ChooserType.CAMERA_AND_GALLERY)
                .setCopyImagesToPublicGalleryFolder(false)
                .setFolderName("Wing Mate")
                .allowMultiple(false)
                .build();
    }

    @Override
    public void showProgress() {
        dialog.show();
    }

    @Override
    public void dismissProgress() {
        dialog.dismiss();
    }

    @Override
    public void setInternetError() {
        dismissProgress();
        showToast(getActivity(), getContext(), "Couldn't connect to internet!", WARNING);
    }

    @Override
    public void setResponseSuccess() {
        dismissProgress();
    }

    @Override
    public void setResponseError(ParseException e) {
        dismissProgress();
        showToast(getActivity(), getContext(), e.getMessage(), ERROR);
    }

    @Override
    public void setResponseGeneralError(String error) {
        dismissProgress();
        showToast(getActivity(), getContext(), error, ERROR);
    }

    @Override
    public void setTrialEnded(String msg) {

    }

    @Override
    public void setVideoLinkSuccess(ParseObject parseObject) {

    }

    @Override
    public void setNickError() {

    }

    @Override
    public void setGenderError() {

    }

    @Override
    public void setEmailError() {

    }

    @Override
    public void setInvalidEmailError() {

    }

    @Override
    public void setPasswordError() {

    }

    @Override
    public void setInvalidPasswordError() {

    }

    @Override
    public void setFormValidateSuccess() {

    }

    @Override
    public void setEmailVerificationError(ParseException e) {

    }

    @Override
    public void setLoginSuccess(ParseUser parseUser) {

    }

    @Override
    public void setQuestionResponseSuccess(List<Question> questions) {

    }

    @Override
    public void setOptionsResponseSuccess(List<QuestionOption> questionOptions) {

    }

    @Override
    public void setUserAnswersResponseSuccess(UserAnswer userAnswer) {

    }

    @Override
    public void setUserAnswersResponseError(ParseException e) {

    }

    @Override
    public void setTermsResponseSuccess(List<TermsConditions> termsConditions) {

    }

    @Override
    public void setQuestionsResponseSuccess(List<Question> questions) {

    }

    @Override
    public void setUserAnswersResponseSuccess(List<UserAnswer> userAnswers) {

    }

    @Override
    public void setUserPhotosVideoResponseSuccess(List<UserProfilePhotoVideo> userProfilePhotoVideos) {

    }

    @Override
    public void setUserFanStatusSuccess(List<Fans> fansList) {

    }

    @Override
    public void setSpecificQuestionUserAnswersSuccess(List<UserAnswer> userAnswers) {

    }

    @Override
    public void setWithInKMUsersSuccess(List<ParseUser> parseUsers) {

    }

    @Override
    public void setAllUsersSuccess(List<ParseUser> parseUsers) {

    }

    @Override
    public void setMyFansSuccess(List<Fans> fansList) {

    }

    @Override
    public void setFanAddedSuccess(Fans fan) {

    }

    public void setPushToUser(Activity activity, Context context, String userId, String alertTitle, String alertText) {
        final HashMap<String, String> params = new HashMap<>();
        params.put(PARAM_USER_ID, userId);
        params.put(PARAM_ALERT_TITLE, alertTitle);
        params.put(PARAM_ALERT_TEXT, alertText);
        ParseCloud.callFunctionInBackground(PARSE_CLOUD_FUNCTION_PUSH_TO_USER, params, (response, exc) -> {
            if (exc != null) {
//                showToast(activity, context, exc.getMessage(), ERROR);
            } else {
                showToast(activity, context, "Push notification has been sent to user", SUCCESS);
            }
        });
    }

    public void setPushToAdmin(Activity activity, Context context, String alertTitle, String alertText) {
        final HashMap<String, String> params = new HashMap<>();
        params.put(PARAM_ALERT_TITLE, alertTitle);
        params.put(PARAM_ALERT_TEXT, alertText);
        ParseCloud.callFunctionInBackground(PARSE_CLOUD_FUNCTION_PUSH_TO_ADMIN, params, (response, exc) -> {
//            if (exc != null) {
//                showToast(activity, context, exc.getMessage(), ERROR);
//            } else {
////                showToast(activity, context, "Push notification has been sent to user", SUCCESS);
//            }
        });
    }

    public void sendEmailToUser(Activity activity, Context context, String emailId, String subject, String body) {
        final HashMap<String, String> params = new HashMap<>();
        params.put("emailId", emailId);
        params.put("subject", subject);
        params.put("body", body);
        ParseCloud.callFunctionInBackground(PARSE_CLOUD_FUNCTION_EMAIL_TO_USER, params, (response, exc) -> {
//            if (exc != null) {
//                showToast(activity, context, exc.getMessage(), ERROR);
//            } else {
//                System.out.println("====email sent====");
//            }
        });
    }

}