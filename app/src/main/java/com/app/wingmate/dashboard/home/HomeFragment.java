package com.app.wingmate.dashboard.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.app.wingmate.R;
import com.app.wingmate.base.BaseFragment;
import com.app.wingmate.dashboard.DashboardFragment;
import com.app.wingmate.utils.ActivityUtility;
import com.parse.ParseUser;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.app.wingmate.utils.AppConstants.ERROR;
import static com.app.wingmate.utils.AppConstants.INFO;
import static com.app.wingmate.utils.AppConstants.MANDATORY;
import static com.app.wingmate.utils.AppConstants.OPTIONAL;
import static com.app.wingmate.utils.AppConstants.PARAM_IS_PAID_USER;
import static com.app.wingmate.utils.AppConstants.PARAM_MANDATORY_QUESTIONNAIRE_FILLED;
import static com.app.wingmate.utils.AppConstants.SUCCESS;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_PRE_LOGIN;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_PROFILE;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_QUESTIONNAIRE;
import static com.app.wingmate.utils.Utilities.showToast;

public class HomeFragment extends BaseFragment {

    public static final String TAG = HomeFragment.class.getName();

    Unbinder unbinder;

    private DashboardFragment dashboardInstance;

    public HomeFragment() {

    }

    public static HomeFragment newInstance(DashboardFragment dashboardInstance) {
        HomeFragment contentFragment = new HomeFragment();
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
        View view = inflater.inflate(R.layout.fragment_home, container, false);
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
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.buy_btn, R.id.profile_photo_video, R.id.questionnaire_mandatory, R.id.questionnaire_optional, R.id.logout})
    public void onViewClicked(View v) {
        if (v.getId() == R.id.buy_btn) {
            ParseUser.getCurrentUser().put(PARAM_IS_PAID_USER, true);
            showToast(getActivity(), getContext(), "Processing...", INFO);
            ParseUser.getCurrentUser().saveInBackground(e -> showToast(getActivity(), getContext(), "Congrats on becoming a paid user!", SUCCESS));
        } else if (v.getId() == R.id.profile_photo_video) {
            if (!ParseUser.getCurrentUser().getBoolean(PARAM_IS_PAID_USER)) {
                showToast(getActivity(), getContext(), "You are not a paid user!", ERROR);
            } else if (!ParseUser.getCurrentUser().getBoolean(PARAM_MANDATORY_QUESTIONNAIRE_FILLED)) {
                showToast(getActivity(), getContext(), "Please fill mandatory questionnaire!", ERROR);
            } else {
                ActivityUtility.startProfileActivity(getActivity(), KEY_FRAGMENT_PROFILE, true, ParseUser.getCurrentUser());
            }
        }  else if (v.getId() == R.id.questionnaire_mandatory) {
            if (ParseUser.getCurrentUser().getBoolean(PARAM_IS_PAID_USER)) {
                ActivityUtility.startQuestionnaireActivity(getActivity(), KEY_FRAGMENT_QUESTIONNAIRE, MANDATORY);
            } else {
                showToast(getActivity(), getContext(), "You are not a paid user!", ERROR);
            }
        } else if (v.getId() == R.id.questionnaire_optional) {
            if (ParseUser.getCurrentUser().getBoolean(PARAM_IS_PAID_USER)) {
                ActivityUtility.startQuestionnaireActivity(getActivity(), KEY_FRAGMENT_QUESTIONNAIRE, OPTIONAL);
            } else {
                showToast(getActivity(), getContext(), "You are not a paid user!", ERROR);
            }
        } else if (v.getId() == R.id.logout) {
            showToast(getActivity(), getContext(), "Logging out...", ERROR);
            ParseUser.logOutInBackground(e -> {
                ActivityUtility.startActivity(requireActivity(), KEY_FRAGMENT_PRE_LOGIN);
            });
        }
    }
}