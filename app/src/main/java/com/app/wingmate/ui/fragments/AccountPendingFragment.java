package com.app.wingmate.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.app.wingmate.R;
import com.app.wingmate.base.BaseFragment;
import com.app.wingmate.base.BaseView;
import com.app.wingmate.utils.ActivityUtility;
import com.app.wingmate.utils.SharedPrefers;
import com.parse.ParseUser;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.app.wingmate.utils.AppConstants.ERROR;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_PRE_LOGIN;
import static com.app.wingmate.utils.CommonKeys.PREF_LAST_UPDATE_TIME;
import static com.app.wingmate.utils.Utilities.showToast;

public class AccountPendingFragment extends BaseFragment implements BaseView {

    public static final String TAG = AccountPendingFragment.class.getName();

    Unbinder unbinder;

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
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.btn_logout})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.btn_logout:
                showToast(requireActivity(), getContext(), "Logging out...", ERROR);
                SharedPrefers.saveLong(requireContext(), PREF_LAST_UPDATE_TIME, 0);
                ParseUser.logOutInBackground(e -> {
                    ActivityUtility.startActivity(requireActivity(), KEY_FRAGMENT_PRE_LOGIN);
                });
                break;
        }
    }
}