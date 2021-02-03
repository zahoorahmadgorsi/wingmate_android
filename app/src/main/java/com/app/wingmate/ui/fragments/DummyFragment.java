package com.app.wingmate.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.app.wingmate.R;
import com.app.wingmate.base.BaseFragment;
import com.app.wingmate.dashboard.DashboardFragment;
import com.app.wingmate.dashboard.home.HomeFragment;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.app.wingmate.utils.Utilities.showToast;

public class DummyFragment extends BaseFragment {

    public static final String TAG = DummyFragment.class.getName();

    private DashboardFragment dashboardInstance;

    Unbinder unbinder;

    public DummyFragment() {

    }

    public static DummyFragment newInstance() {
        DummyFragment contentFragment = new DummyFragment();
        return contentFragment;
    }

    public static DummyFragment newInstance(DashboardFragment dashboardInstance) {
        DummyFragment contentFragment = new DummyFragment();
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
        View view = inflater.inflate(R.layout.fragment_dummy, container, false);
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

    @OnClick({})
    public void onViewClicked(View v) {

    }
}