package com.app.wingmate.ui.fragments;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.wingmate.BuildConfig;
import com.app.wingmate.R;
import com.app.wingmate.base.BaseFragment;
import com.app.wingmate.base.BaseInteractor;
import com.app.wingmate.base.BasePresenter;
import com.app.wingmate.ui.activities.MainActivity;
import com.app.wingmate.ui.adapters.SettingsAdapter;
import com.app.wingmate.utils.ActivityUtility;
import com.app.wingmate.utils.AlertMessages;
import com.app.wingmate.utils.SharedPrefers;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.app.wingmate.utils.AlertMessages.GO_TO_PAYMENT_SCREEN;
import static com.app.wingmate.utils.AlertMessages.GO_TO_PAYMENT_SCREEN_AFTER_EXPIRED;
import static com.app.wingmate.utils.AppConstants.ACTIVE;
import static com.app.wingmate.utils.AppConstants.CLASS_NAME_USER;
import static com.app.wingmate.utils.AppConstants.ERROR;
import static com.app.wingmate.utils.AppConstants.INFO;
import static com.app.wingmate.utils.AppConstants.MANDATORY;
import static com.app.wingmate.utils.AppConstants.OPTIONAL;
import static com.app.wingmate.utils.AppConstants.PARAM_ACCOUNT_STATUS;
import static com.app.wingmate.utils.AppConstants.PARAM_IS_ADMIN;
import static com.app.wingmate.utils.AppConstants.PARAM_IS_PAID_USER;
import static com.app.wingmate.utils.AppConstants.PARAM_IS_PHOTO_SUBMITTED;
import static com.app.wingmate.utils.AppConstants.PARAM_IS_VIDEO_SUBMITTED;
import static com.app.wingmate.utils.AppConstants.PARAM_MANDATORY_QUESTIONNAIRE_FILLED;
import static com.app.wingmate.utils.AppConstants.PARAM_PROFILE_PIC;
import static com.app.wingmate.utils.AppConstants.PENDING;
import static com.app.wingmate.utils.AppConstants.REJECTED;
import static com.app.wingmate.utils.AppConstants.SUCCESS;
import static com.app.wingmate.utils.AppConstants.TRIAL_PERIOD;
import static com.app.wingmate.utils.AppConstants.USER_CLASS_NAME;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_ADMIN_DASHBOARD;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_PAYMENT;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_PRE_LOGIN;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_PROFILE;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_QUESTIONNAIRE;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_UPLOAD_PHOTO_VIDEO_PROFILE;
import static com.app.wingmate.utils.CommonKeys.PREF_LAST_UPDATE_TIME;
import static com.app.wingmate.utils.Utilities.showToast;

import java.util.ArrayList;
import java.util.List;

public class SettingsFragment extends BaseFragment {

    public static final String TAG = SettingsFragment.class.getName();

    Unbinder unbinder;

    private DashboardFragment dashboardInstance;

    @BindView(R.id.buy_btn)
    Button buyBtn;
    @BindView(R.id.admin_btn)
    Button adminBtn;
    @BindView(R.id.version_tv)
    TextView versionTV;
    boolean isLikeDisable = false;
    boolean isMessageDisable = false;

    RecyclerView settingsItemsList;

    public BasePresenter presenter;

    public boolean isExpired = false;
    public int remainingDays = TRIAL_PERIOD;
    SettingsAdapter settingsAdapter;
    int listSize = 12;
    private boolean isUnSubscribed = false;

    public SettingsFragment() {

    }

    public static SettingsFragment newInstance(DashboardFragment dashboardInstance) {
        SettingsFragment contentFragment = new SettingsFragment();
        contentFragment.dashboardInstance = dashboardInstance;
        return contentFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        unbinder = ButterKnife.bind(this, view);
        settingsItemsList = view.findViewById(R.id.itemsList);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter = new BasePresenter(this, new BaseInteractor());
        String versionNo = "Version: " + BuildConfig.VERSION_NAME;
        versionTV.setText(versionNo);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).setProfileImage(ParseUser.getCurrentUser().getString(PARAM_PROFILE_PIC));
//        dashboardInstance.performUserUpdateAction();
        List<String> dataList = generateList();
        if (ParseUser.getCurrentUser().getBoolean(PARAM_IS_ADMIN)) {
            //adminBtn.setVisibility(View.VISIBLE);
            //listSize = listSize + 1;
            dataList.add("Go to Admin section");
        } else {
            //adminBtn.setVisibility(View.GONE);
            //listSize = listSize - 1;
        }

        if (ParseUser.getCurrentUser().getBoolean(PARAM_IS_PAID_USER)) {
            buyBtn.setVisibility(View.GONE);
            //listSize = listSize - 1;
        } else {
            buyBtn.setVisibility(View.GONE);
            //listSize = listSize + 1;
            //dataList.add("Pay Now");
        }
        dataList.add("Version: " + BuildConfig.VERSION_NAME);
        isMessageDisable = ParseUser.getCurrentUser().getBoolean("messageDisabled");
        isLikeDisable = ParseUser.getCurrentUser().getBoolean("likeDisabled");
        isUnSubscribed = ParseUser.getCurrentUser().getBoolean("isUserUnsubscribed");
        settingsAdapter = new SettingsAdapter(getActivity(),dataList,this,"Version: " + BuildConfig.VERSION_NAME,isMessageDisable,isLikeDisable,isUnSubscribed);
        settingsItemsList.setAdapter(settingsAdapter);
        settingsItemsList.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
    }

    private List<String> generateList() {
        List<String> list = new ArrayList<>();
        list.add("Change Password");
        list.add("Contact us");
        list.add("Help");
        list.add("About us");
        list.add("Terms of use");
        list.add("Privacy policy");
        list.add("Notifications");
        list.add("Disable messages");
        list.add("Disable likes");
        list.add("Change saved credit card");
        list.add("Unsubscribe");
        //list.add("Logout");
        return list;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.admin_btn, R.id.buy_btn, R.id.profile_photo_video, R.id.questionnaire_mandatory, R.id.questionnaire_optional, R.id.logout})
    public void onViewClicked(View v) {
        if (v.getId() == R.id.buy_btn) {
            performUserUpdateAction(true);
//            ActivityUtility.startPaymentActivity(requireActivity(), KEY_FRAGMENT_PAYMENT, false);
//            ParseUser.getCurrentUser().put(PARAM_IS_PAID_USER, true);
//            showToast(getActivity(), getContext(), "Processing...", INFO);
//            ParseUser.getCurrentUser().saveInBackground(e -> showToast(getActivity(), getContext(), "Congrats on becoming a paid user!", SUCCESS));
        } else if (v.getId() == R.id.admin_btn) {
            ActivityUtility.startActivity(getActivity(), KEY_FRAGMENT_ADMIN_DASHBOARD);
        } else if (v.getId() == R.id.profile_photo_video) {
            if (!ParseUser.getCurrentUser().getBoolean(PARAM_IS_PAID_USER)) {
                showToast(getActivity(), getContext(), "You are not a paid user!", ERROR);
            } else if (!ParseUser.getCurrentUser().getBoolean(PARAM_MANDATORY_QUESTIONNAIRE_FILLED)) {
                showToast(getActivity(), getContext(), "Please fill mandatory questionnaire!", ERROR);
            } else {
                ActivityUtility.startProfileActivity(getActivity(), KEY_FRAGMENT_PROFILE, true, ParseUser.getCurrentUser());
            }
        } else if (v.getId() == R.id.questionnaire_mandatory) {
            if (ParseUser.getCurrentUser().getBoolean(PARAM_IS_PAID_USER)) {
                ActivityUtility.startQuestionnaireActivity(getActivity(), KEY_FRAGMENT_QUESTIONNAIRE, MANDATORY, false);
            } else {
                showToast(getActivity(), getContext(), "You are not a paid user!", ERROR);
            }
        } else if (v.getId() == R.id.questionnaire_optional) {
            if (ParseUser.getCurrentUser().getBoolean(PARAM_IS_PAID_USER)) {
                ActivityUtility.startQuestionnaireActivity(getActivity(), KEY_FRAGMENT_QUESTIONNAIRE, OPTIONAL, false);
            } else {
                showToast(getActivity(), getContext(), "You are not a paid user!", ERROR);
            }
        } else if (v.getId() == R.id.logout) {

            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setMessage("Are you sure you want to logout?");
            builder.setPositiveButton("Yes", (dialog, which) -> {
                dialog.cancel();
                showToast(getActivity(), getContext(), "Logging out...", ERROR);
                SharedPrefers.saveLong(requireContext(), PREF_LAST_UPDATE_TIME, 0);
                ParseUser.logOutInBackground(e -> {
                    ActivityUtility.startActivity(requireActivity(), KEY_FRAGMENT_PRE_LOGIN);
                });
            });
            builder.setNegativeButton("No", (dialog, which) -> {
                dialog.cancel();
            });
            builder.show();
        }
    }

    public void performUserUpdateAction(boolean showLoader) {
        if (ParseUser.getCurrentUser() != null) {
            if (showLoader) showProgress();
            fetchUpdatedCurrentUser(showLoader);
        }
    }

    private void fetchUpdatedCurrentUser(boolean showLoader) {
        ParseUser.getCurrentUser().fetchInBackground((GetCallback<ParseUser>) (parseUser, e) -> {
            presenter.checkServerDate(getContext(), showLoader, false);
        });
    }

    @Override
    public void setHasTrial(int days, boolean showLoader, boolean isJustRefresh) {
        isExpired = false;
        remainingDays = days;

        int accountStatus = ParseUser.getCurrentUser().getInt(PARAM_ACCOUNT_STATUS);
        boolean isPaid = ParseUser.getCurrentUser().getBoolean(PARAM_IS_PAID_USER);
        boolean isPhotoSubmitted = ParseUser.getCurrentUser().getBoolean(PARAM_IS_PHOTO_SUBMITTED);
        boolean isVideoSubmitted = ParseUser.getCurrentUser().getBoolean(PARAM_IS_VIDEO_SUBMITTED);
        boolean isMandatoryQuestionnaireFilled = ParseUser.getCurrentUser().getBoolean(PARAM_MANDATORY_QUESTIONNAIRE_FILLED);

        AlertDialog.Builder dialog = new AlertDialog.Builder(requireContext());

        if (showLoader) dismissProgress();

        if (accountStatus == REJECTED) {
            showRejectionPopupAndLogout();
        } else if (accountStatus == PENDING && (!isPhotoSubmitted || !isVideoSubmitted)) {
            dialog
//                    .setTitle(getString(R.string.app_name))
//                    .setIcon(R.drawable.app_heart)
                    .setMessage("You need to upload photos & video first in order to proceed. Do you wish to continue?")
                    .setNegativeButton("Upload Later", (dialoginterface, i) -> {
                        dialoginterface.cancel();
                    })
                    .setPositiveButton("Upload Now", (dialoginterface, i) -> {
                        dialoginterface.cancel();
                        ActivityUtility.startProfileMediaActivity(requireActivity(), KEY_FRAGMENT_UPLOAD_PHOTO_VIDEO_PROFILE, false, isExpired);
                    }).show();
        } else if (accountStatus == PENDING) {
            dialog
//                    .setTitle(getString(R.string.app_name))
//                    .setIcon(R.drawable.app_heart)
                    .setMessage("Your account is under review.")
                    .setPositiveButton("OK", (dialoginterface, i) -> {
                        dialoginterface.cancel();
                    }).show();
        } else if (!isPaid && accountStatus == ACTIVE) {
            dialog
                    .setCancelable(false)
                    .setMessage(GO_TO_PAYMENT_SCREEN)
                    .setPositiveButton("OK", (dialoginterface, i) -> {
                        dialoginterface.cancel();
                        ActivityUtility.startPaymentActivity(getActivity(), KEY_FRAGMENT_PAYMENT, false);
                    }).show();
        } else if (isPaid) {
            buyBtn.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void setTrialEnded(String msg, boolean showLoader, boolean isJustRefresh) {

        isExpired = true;
        remainingDays = 0;

        if (showLoader) dismissProgress();

        int accountStatus = ParseUser.getCurrentUser().getInt(PARAM_ACCOUNT_STATUS);
        boolean isPaid = ParseUser.getCurrentUser().getBoolean(PARAM_IS_PAID_USER);
        boolean isPhotoSubmitted = ParseUser.getCurrentUser().getBoolean(PARAM_IS_PHOTO_SUBMITTED);
        boolean isVideoSubmitted = ParseUser.getCurrentUser().getBoolean(PARAM_IS_VIDEO_SUBMITTED);
        boolean isMandatoryQuestionnaireFilled = ParseUser.getCurrentUser().getBoolean(PARAM_MANDATORY_QUESTIONNAIRE_FILLED);

        AlertDialog.Builder dialog = new AlertDialog.Builder(requireContext());

        if (accountStatus == REJECTED) {
            showRejectionPopupAndLogout();
        } else if (accountStatus == PENDING && (!isPhotoSubmitted || !isVideoSubmitted)) {
            dialog
//                    .setTitle(getString(R.string.app_name))
//                    .setIcon(R.drawable.app_heart)
                    .setMessage("You need to upload photos & video first in order to proceed. Do you wish to continue?")
                    .setNegativeButton("Upload Later", (dialoginterface, i) -> {
                        dialoginterface.cancel();
                    })
                    .setPositiveButton("Upload Now", (dialoginterface, i) -> {
                        dialoginterface.cancel();
                        ActivityUtility.startProfileMediaActivity(requireActivity(), KEY_FRAGMENT_UPLOAD_PHOTO_VIDEO_PROFILE, false, isExpired);
                    }).show();
        } else if (accountStatus == PENDING) {
            dialog
//                    .setTitle(getString(R.string.app_name))
//                    .setIcon(R.drawable.app_heart)
                    .setMessage("Your account is under review.")
                    .setPositiveButton("OK", (dialoginterface, i) -> {
                        dialoginterface.cancel();
                    }).show();
        } else if (!isPaid && accountStatus == ACTIVE) {
            dialog
                    .setCancelable(false)
                    .setMessage(GO_TO_PAYMENT_SCREEN_AFTER_EXPIRED)
                    .setPositiveButton("OK", (dialoginterface, i) -> {
                        dialoginterface.cancel();
                        ActivityUtility.startPaymentActivity(getActivity(), KEY_FRAGMENT_PAYMENT, false);
                    }).show();
        } else if (isPaid) {
            buyBtn.setVisibility(View.INVISIBLE);
        }
    }

    private void showRejectionPopupAndLogout() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(requireContext());
        dialog
//                .setTitle(getString(R.string.app_name))
//                .setIcon(R.drawable.app_heart)
                .setCancelable(false)
                .setMessage("Your profile has been rejected by the admin!")
                .setNegativeButton("OK", (dialoginterface, i) -> {
                    dialoginterface.cancel();
                    SharedPrefers.saveLong(requireContext(), PREF_LAST_UPDATE_TIME, 0);
                    ParseUser.logOut();
                    ActivityUtility.startActivity(requireActivity(), KEY_FRAGMENT_PRE_LOGIN);
                })
                .show();
    }

    public void call(int action){
        if (action == 1){
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setMessage("Are you sure you want to logout?");
            builder.setPositiveButton("Yes", (dialog, which) -> {
                dialog.cancel();
                showToast(getActivity(), getContext(), "Logging out...", ERROR);
                SharedPrefers.saveLong(requireContext(), PREF_LAST_UPDATE_TIME, 0);
                ParseUser.logOutInBackground(e -> {
                    ActivityUtility.startActivity(requireActivity(), KEY_FRAGMENT_PRE_LOGIN);
                });
            });
            builder.setNegativeButton("No", (dialog, which) -> {
                dialog.cancel();
            });
            builder.show();
        }
        else if (action == 2){
            ActivityUtility.startActivity(getActivity(), KEY_FRAGMENT_ADMIN_DASHBOARD);
        }else if (action == 3){
            performUserUpdateAction(true);
        }else if (action == 4){

        }else if (action == 5){

        }
    }

    public void unSubscribe (boolean isUnsubscribe){
        showProgress();
        ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery(USER_CLASS_NAME);
        parseQuery.whereEqualTo("objectId", ParseUser.getCurrentUser().getObjectId());
        parseQuery.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                object.put("isUserUnsubscribed",isUnsubscribe);
                object.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e==null){
                            ParseUser.getCurrentUser().fetchInBackground(new GetCallback<ParseObject>() {
                                @Override
                                public void done(ParseObject object, ParseException e) {
                                    if (e==null){
                                        dismissProgress();
                                        if (isUnsubscribe){
                                            Toast.makeText(getContext(),"You have unSubscribed successfully",Toast.LENGTH_SHORT).show();
                                        }else{
                                            Toast.makeText(getContext(),"You have subscribed successfully",Toast.LENGTH_SHORT).show();
                                        }
                                    }else{
                                        dismissProgress();
                                        showToast(getActivity(), getContext(), e.getMessage(), ERROR);
                                    }
                                }
                            });
                        }else{
                            dismissProgress();
                            showToast(getActivity(), getContext(), e.getMessage(), ERROR);
                        }
                    }
                });
            }
        });
    }
    public void disableLikes (boolean isDisable){
        showProgress();
        ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery(USER_CLASS_NAME);
        parseQuery.whereEqualTo("objectId",ParseUser.getCurrentUser().getObjectId());
        parseQuery.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                object.put("likeDisabled",isDisable);
                object.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e==null){
                            //dismissProgress();
                            fetchUpdatedCurrentUser(true);
                        }else{
                            dismissProgress();
                            showToast(getActivity(), getContext(), e.getMessage(), ERROR);
                        }
                    }
                });
            }
        });
    }

    public void disableMessages (boolean isDisable){
        showProgress();
        ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery(USER_CLASS_NAME);
        parseQuery.whereEqualTo("objectId",ParseUser.getCurrentUser().getObjectId());
        parseQuery.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                object.put("messageDisabled",isDisable);
                object.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e==null){
                            //dismissProgress();
                            fetchUpdatedCurrentUser(true);
                        }else{
                            dismissProgress();
                            showToast(getActivity(), getContext(), e.getMessage(), ERROR);
                        }
                    }
                });
            }
        });
    }
}