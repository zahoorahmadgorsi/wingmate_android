package com.app.wingmate.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.wingmate.R;
import com.app.wingmate.base.BaseFragment;
import com.app.wingmate.events.RefreshHome;
import com.app.wingmate.events.RefreshSearch;
import com.app.wingmate.ui.adapters.QuestionOptionsListAdapter;
import com.app.wingmate.ui.adapters.UserViewAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class HomeFragment extends BaseFragment {

    public static final String TAG = HomeFragment.class.getName();

    Unbinder unbinder;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.empty_view)
    LinearLayout emptyView;

    private UserViewAdapter userViewAdapter;
    private GridLayoutManager gridLayoutManager;

    private int selectedIndex = -1;
    private boolean onCrate = true;

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
        EventBus.getDefault().register(this);
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
        initView();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (dashboardInstance.homeProgress) showProgress();
        else dismissProgress();

        if (dashboardInstance.allUsers != null) {
            userViewAdapter.setData(dashboardInstance.allUsers);
            userViewAdapter.notifyDataSetChanged();
        }
    }

    @Subscribe
    public void onHomeRefresh(RefreshHome refreshHome) {
        if (dashboardInstance.homeProgress) showProgress();
        else dismissProgress();
        userViewAdapter.setData(dashboardInstance.allUsers);
        userViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        EventBus.getDefault().unregister(this);

    }

    @OnClick({})
    public void onViewClicked(View v) {

    }

    private void initView() {
        gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        userViewAdapter = new UserViewAdapter(getActivity(), dashboardInstance.allUsers);
        userViewAdapter.setEmpty(emptyView);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(userViewAdapter);
    }
}