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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.wingmate.R;
import com.app.wingmate.base.BaseFragment;
import com.app.wingmate.events.RefreshHome;
import com.app.wingmate.events.RefreshSearch;
import com.app.wingmate.ui.adapters.QuestionOptionsListAdapter;
import com.app.wingmate.ui.adapters.UserViewAdapter;
import com.app.wingmate.utils.ActivityUtility;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.app.wingmate.utils.AppConstants.PARAM_PROFILE_PIC;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_PHOTO_VIEW;

public class HomeFragment extends BaseFragment {

    public static final String TAG = HomeFragment.class.getName();

    Unbinder unbinder;

    @BindView(R.id.pullToRefresh)
    SwipeRefreshLayout pullToRefresh;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.empty_view)
    LinearLayout emptyView;
    @BindView(R.id.profile_img)
    CircleImageView profileImg;

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
        pullToRefresh.setRefreshing(false);
        if (dashboardInstance.allUsers != null) {
            userViewAdapter.setData(dashboardInstance.allUsers);
            userViewAdapter.notifyDataSetChanged();
        }
    }

    @Subscribe
    public void onHomeRefresh(RefreshHome refreshHome) {
        System.out.println("====refresh here=======");
        if (dashboardInstance.homeProgress) showProgress();
        else dismissProgress();
        pullToRefresh.setRefreshing(false);
        userViewAdapter.setData(dashboardInstance.allUsers);
        userViewAdapter.notifyDataSetChanged();
        if (dashboardInstance.allUsers==null || dashboardInstance.allUsers.size()==0) {
            emptyView.setVisibility(View.VISIBLE);
        } else {
            emptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @OnClick({R.id.profile_img, R.id.btn_top_fans, R.id.btn_top_search , R.id.btn_top_msg , R.id.btn_top_compatibility})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.profile_img:
                ActivityUtility.startPhotoViewActivity(requireActivity(), KEY_FRAGMENT_PHOTO_VIEW, ParseUser.getCurrentUser().getString(PARAM_PROFILE_PIC));
                break;
            case R.id.btn_top_fans:
//                dashboardInstance.setAllInView();
                dashboardInstance.setTab(2);
                break;
            case R.id.btn_top_search:
//                dashboardInstance.setAllInView();
                dashboardInstance.setTab(1);
                break;
            case R.id.btn_top_msg:
//                dashboardInstance.setAllInView();
                dashboardInstance.setTab(3);
                break;
            case R.id.btn_top_compatibility:
                sortByCompatibility();
                break;
        }
    }

    public void sortByCompatibility() {
        Collections.sort(dashboardInstance.allUsers, (lhs, rhs) -> Integer.valueOf(rhs.getMatchPercent()).compareTo(lhs.getMatchPercent()));
        userViewAdapter.setData(dashboardInstance.allUsers);
        userViewAdapter.notifyDataSetChanged();
    }

    private void initView() {
        gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        userViewAdapter = new UserViewAdapter(getActivity(), dashboardInstance.allUsers);
        userViewAdapter.setEmpty(emptyView);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(userViewAdapter);

        Picasso.get()
                .load(ParseUser.getCurrentUser().getString(PARAM_PROFILE_PIC))
                .centerCrop()
                .resize(500, 500)
                .placeholder(R.drawable.image_placeholder)
                .into(profileImg);

        pullToRefresh.setOnRefreshListener(() -> {
            pullToRefresh.setRefreshing(false);
            dashboardInstance.homeProgress = true;
            showProgress();
            dashboardInstance.allUsers = new ArrayList<>();
            dashboardInstance.presenter.queryAllUsers(getContext());
        });
    }
}