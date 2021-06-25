package com.app.wingmate.ui.fragments;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.app.wingmate.R;
import com.app.wingmate.base.BaseFragment;
import com.app.wingmate.base.BaseView;
import com.app.wingmate.ui.activities.SplashActivity;
import com.app.wingmate.utils.ActivityUtility;
import com.app.wingmate.utils.SharedPrefers;
import com.parse.GetCallback;
import com.parse.ParseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.app.wingmate.utils.AppConstants.ACTIVE;
import static com.app.wingmate.utils.AppConstants.ERROR;
import static com.app.wingmate.utils.AppConstants.MANDATORY;
import static com.app.wingmate.utils.AppConstants.PARAM_ACCOUNT_STATUS;
import static com.app.wingmate.utils.AppConstants.PARAM_IS_ADMIN;
import static com.app.wingmate.utils.AppConstants.PARAM_IS_PAID_USER;
import static com.app.wingmate.utils.AppConstants.PARAM_IS_PHOTO_SUBMITTED;
import static com.app.wingmate.utils.AppConstants.PARAM_IS_VIDEO_SUBMITTED;
import static com.app.wingmate.utils.AppConstants.PARAM_MANDATORY_QUESTIONNAIRE_FILLED;
import static com.app.wingmate.utils.AppConstants.PENDING;
import static com.app.wingmate.utils.AppConstants.REJECTED;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_ADMIN_DASHBOARD;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_DASHBOARD;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_PAYMENT;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_PRE_LOGIN;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_QUESTIONNAIRE;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_UPLOAD_PHOTO_VIDEO_PROFILE;
import static com.app.wingmate.utils.CommonKeys.PREF_LAST_UPDATE_TIME;
import static com.app.wingmate.utils.Utilities.showToast;

public class AccountPendingFragment extends BaseFragment implements BaseView {

    public static final String TAG = AccountPendingFragment.class.getName();

    Unbinder unbinder;

    @BindView(R.id.admin_btn)
    Button adminBtn;

    public AccountPendingFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_pending, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (ParseUser.getCurrentUser().getBoolean(PARAM_IS_ADMIN)) {
            adminBtn.setVisibility(View.VISIBLE);
            showProgress();
            ParseUser.getCurrentUser().fetchInBackground((GetCallback<ParseUser>) (parseUser, e) -> {
                int accountStatus = ParseUser.getCurrentUser().getInt(PARAM_ACCOUNT_STATUS);
                dismissProgress();
                if (accountStatus == ACTIVE) {
                    ActivityUtility.startActivity(requireActivity(), KEY_FRAGMENT_DASHBOARD);
                }
            });
        } else {
            adminBtn.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.admin_btn, R.id.btn_logout})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.admin_btn:
                ActivityUtility.startActivity(getActivity(), KEY_FRAGMENT_ADMIN_DASHBOARD);
                break;
            case R.id.btn_logout:
//                showToast(requireActivity(), getContext(), "Logging out...", ERROR);
//                SharedPrefers.saveLong(requireContext(), PREF_LAST_UPDATE_TIME, 0);
//                ParseUser.logOutInBackground(e -> {
//                    ActivityUtility.startActivity(requireActivity(), KEY_FRAGMENT_PRE_LOGIN);
//                });

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
                break;
        }
    }
}