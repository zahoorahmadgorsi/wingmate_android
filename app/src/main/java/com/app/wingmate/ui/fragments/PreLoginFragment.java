package com.app.wingmate.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.app.wingmate.R;
import com.app.wingmate.base.BaseFragment;
import com.app.wingmate.utils.ActivityUtility;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_LOGIN;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_SIGN_UP;

public class PreLoginFragment extends BaseFragment {

    public static final String TAG = PreLoginFragment.class.getName();

    Unbinder unbinder;

    public PreLoginFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pre_login, container, false);
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

    @OnClick({R.id.btn_signin, R.id.btn_sign_up})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.btn_signin:
                ActivityUtility.startActivity(getActivity(), KEY_FRAGMENT_LOGIN);
                break;
            case R.id.btn_sign_up:
                ActivityUtility.startActivity(getActivity(), KEY_FRAGMENT_SIGN_UP);
                break;
            default:
                break;
        }
    }
}