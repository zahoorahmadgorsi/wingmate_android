package com.app.wingmate.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.app.wingmate.R;
import com.app.wingmate.base.BaseFragment;
import com.app.wingmate.ui.activities.MainActivity;
import com.parse.ParseUser;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.app.wingmate.utils.AppConstants.PARAM_PROFILE_PIC;

public class MessagesFragment  extends BaseFragment {

    public static final String TAG = MessagesFragment.class.getName();

    private DashboardFragment dashboardInstance;

    Unbinder unbinder;

    public MessagesFragment() {

    }

    public static MessagesFragment newInstance() {
        MessagesFragment contentFragment = new MessagesFragment();
        return contentFragment;
    }

    public static MessagesFragment newInstance(DashboardFragment dashboardInstance) {
        MessagesFragment contentFragment = new MessagesFragment();
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
        View view = inflater.inflate(R.layout.fragment_messages, container, false);
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
        ((MainActivity) getActivity()).setProfileImage(ParseUser.getCurrentUser().getString(PARAM_PROFILE_PIC));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({})
    public void onViewClicked(View v) {

    }
}