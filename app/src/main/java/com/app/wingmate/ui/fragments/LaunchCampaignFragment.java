package com.app.wingmate.ui.fragments;

import static com.app.wingmate.utils.AppConstants.INFO;
import static com.app.wingmate.utils.AppConstants.PARAM_IS_PAID_USER;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_NEXTSTEP_MEMBERSHIP;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_QUESTIONNAIRE;
import static com.app.wingmate.utils.Utilities.showToast;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.app.wingmate.R;
import com.app.wingmate.base.BaseFragment;
import com.app.wingmate.base.BaseView;
import com.app.wingmate.utils.ActivityUtility;
import com.parse.ParseUser;


public class LaunchCampaignFragment extends BaseFragment implements BaseView {

    public static final String TAG = LaunchCampaignFragment.class.getName();
    Button btnContinue;
    View view;

    public LaunchCampaignFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_launch_campaign, container, false);
        btnContinue = view.findViewById(R.id.btn_continue);
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseUser.getCurrentUser().put(PARAM_IS_PAID_USER, true);
                showProgress();
                ParseUser.getCurrentUser().saveInBackground(en -> {
                    dismissProgress();
                    ActivityUtility.startNextStepMembershipActivity(getActivity(),KEY_FRAGMENT_NEXTSTEP_MEMBERSHIP,true);
                });
            }
        });

        return view;
    }
}