package com.app.wingmate.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.wingmate.R;
import com.app.wingmate.base.BaseFragment;
import com.app.wingmate.events.RefreshFans;
import com.app.wingmate.models.Fans;
import com.app.wingmate.ui.activities.MainActivity;
import com.app.wingmate.ui.adapters.MyFansAdapter;
import com.parse.ParseUser;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.app.wingmate.utils.AppConstants.FAN_TYPE_CRUSH;
import static com.app.wingmate.utils.AppConstants.FAN_TYPE_LIKE;
import static com.app.wingmate.utils.AppConstants.FAN_TYPE_MAY_BE;
import static com.app.wingmate.utils.AppConstants.PARAM_PROFILE_PIC;

public class MyFansFragment extends BaseFragment {

    public static final String TAG = MyFansFragment.class.getName();

    Unbinder unbinder;

    @BindView(R.id.pullToRefresh)
    SwipeRefreshLayout pullToRefresh;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.empty_view)
    LinearLayout emptyView;

    private MyFansAdapter adapter;
    private GridLayoutManager gridLayoutManager;

    private DashboardFragment dashboardInstance;

    private List<Fans> toList = new ArrayList<>();
    private List<Fans> fromList = new ArrayList<>();
    private List<Fans> likesList = new ArrayList<>();
    private List<Fans> crushList = new ArrayList<>();
    private List<Fans> maybeList = new ArrayList<>();

    public MyFansFragment() {

    }

    public static MyFansFragment newInstance(DashboardFragment dashboardInstance) {
        MyFansFragment contentFragment = new MyFansFragment();
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
        View view = inflater.inflate(R.layout.fragment_my_fans, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        setCountViewsClickListeners();
    }

    private void setCountViewsClickListeners() {
        ((MainActivity) getActivity()).getBtnLikesFilter().setOnClickListener(v -> {
            pullToRefresh.setRefreshing(false);
            dashboardInstance.myFansProgress = true;
            showProgress();
            dashboardInstance.myFansList = new ArrayList<>();
            dashboardInstance.presenter.queryAllMyFans(getContext());
            setLikesFilterView();
        });

        ((MainActivity) getActivity()).getBtnCrushFilter().setOnClickListener(v -> {
            pullToRefresh.setRefreshing(false);
            dashboardInstance.myFansProgress = true;
            showProgress();
            dashboardInstance.myFansList = new ArrayList<>();
            dashboardInstance.presenter.queryAllMyFans(getContext());
            setCrushFilterView();
        });

        ((MainActivity) getActivity()).getBtnMaybeFilter().setOnClickListener(v -> {
            pullToRefresh.setRefreshing(false);
            dashboardInstance.myFansProgress = true;
            showProgress();
            dashboardInstance.myFansList = new ArrayList<>();
            dashboardInstance.presenter.queryAllMyFans(getContext());
            setMaybeFilterView();
        });
    }

    private void setLikesFilterView() {
        ((MainActivity) getActivity()).getLikesCount().setText(likesList.size() + "");
        ((MainActivity) getActivity()).getCrushCount().setText(crushList.size() + "");
        ((MainActivity) getActivity()).getMaybeCount().setText(maybeList.size() + "");

        //((MainActivity) getActivity()).getLikesCountTV().setSelected(true);
        //((MainActivity) getActivity()).getCrushCountTV().setSelected(false);
        //((MainActivity) getActivity()).getMaybeCountTV().setSelected(false);

        if (likesList == null || likesList.size() == 0) {
            emptyView.setVisibility(View.VISIBLE);
        } else {
            emptyView.setVisibility(View.GONE);
        }

        adapter.setData(likesList);
        adapter.notifyDataSetChanged();
    }

    private void setCrushFilterView() {
        ((MainActivity) getActivity()).getLikesCount().setText(likesList.size() + "");
        ((MainActivity) getActivity()).getCrushCount().setText(crushList.size() + "");
        ((MainActivity) getActivity()).getMaybeCount().setText(maybeList.size() + "");

        //((MainActivity) getActivity()).getLikesCountTV().setSelected(false);
        //((MainActivity) getActivity()).getCrushCountTV().setSelected(true);
        //((MainActivity) getActivity()).getMaybeCountTV().setSelected(false);

        if (crushList == null || crushList.size() == 0) {
            emptyView.setVisibility(View.VISIBLE);
        } else {
            emptyView.setVisibility(View.GONE);
        }

        adapter.setData(crushList);
        adapter.notifyDataSetChanged();
    }

    private void setMaybeFilterView() {
        ((MainActivity) getActivity()).getLikesCount().setText(likesList.size() + "");
        ((MainActivity) getActivity()).getCrushCount().setText(crushList.size() + "");
        ((MainActivity) getActivity()).getMaybeCount().setText(maybeList.size() + "");

        //((MainActivity) getActivity()).getLikesCountTV().setSelected(false);
        //((MainActivity) getActivity()).getCrushCountTV().setSelected(false);
        //((MainActivity) getActivity()).getMaybeCountTV().setSelected(true);

        if (maybeList == null || maybeList.size() == 0) {
            emptyView.setVisibility(View.VISIBLE);
        } else {
            emptyView.setVisibility(View.GONE);
        }

        adapter.setData(maybeList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
//        if (dashboardInstance.myFansProgress) showProgress();
//        else dismissProgress();
//        pullToRefresh.setRefreshing(false);
//        if (dashboardInstance.myFansList != null) {
//            adapter.setData(dashboardInstance.myFansList);
//            adapter.notifyDataSetChanged();
//        }
//        if (dashboardInstance.myFansList == null || dashboardInstance.myFansList.size() == 0) {
//            emptyView.setVisibility(View.VISIBLE);
//        } else {
//            emptyView.setVisibility(View.GONE);
//        }

        ((MainActivity) getActivity()).setProfileImage(ParseUser.getCurrentUser().getString(PARAM_PROFILE_PIC));
        setDataInGridView();

//        dashboardInstance.performUserUpdateAction();
    }

    private void setDataInGridView() {
        if (dashboardInstance.myFansProgress) showProgress();
        else dismissProgress();

        toList = new ArrayList<>();
        fromList = new ArrayList<>();
        likesList = new ArrayList<>();
        crushList = new ArrayList<>();
        maybeList = new ArrayList<>();

        if (dashboardInstance.myFansList != null && dashboardInstance.myFansList.size() > 0) {
            for (int i = 0; i < dashboardInstance.myFansList.size(); i++) {
                if (dashboardInstance.myFansList.get(i).getToUser().getObjectId().equals(ParseUser.getCurrentUser().getObjectId())
                        && !dashboardInstance.myFansList.get(i).getFromUser().getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
                    toList.add(dashboardInstance.myFansList.get(i));
                }
                if (!dashboardInstance.myFansList.get(i).getToUser().getObjectId().equals(ParseUser.getCurrentUser().getObjectId())
                        && dashboardInstance.myFansList.get(i).getFromUser().getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
                    fromList.add(dashboardInstance.myFansList.get(i));
                }

                if (dashboardInstance.myFansList.get(i).getToUser().getObjectId().equals(ParseUser.getCurrentUser().getObjectId())
                        && dashboardInstance.myFansList.get(i).getFanType().equals(FAN_TYPE_LIKE)) {
                    likesList.add(dashboardInstance.myFansList.get(i));
                }

                if (dashboardInstance.myFansList.get(i).getToUser().getObjectId().equals(ParseUser.getCurrentUser().getObjectId())
                        && dashboardInstance.myFansList.get(i).getFanType().equals(FAN_TYPE_CRUSH)) {
                    crushList.add(dashboardInstance.myFansList.get(i));
                }

                if (dashboardInstance.myFansList.get(i).getToUser().getObjectId().equals(ParseUser.getCurrentUser().getObjectId())
                        && dashboardInstance.myFansList.get(i).getFanType().equals(FAN_TYPE_MAY_BE)) {
                    maybeList.add(dashboardInstance.myFansList.get(i));
                }
            }
            if (fromList != null && fromList.size() > 0) {
                for (int i = 0; i < fromList.size(); i++) {
                    for (int j = 0; j < toList.size(); j++) {
                        if (fromList.get(i).getToUser().getObjectId().equals(toList.get(j).getFromUser().getObjectId())) {
                            toList.get(j).setMySelectedType(fromList.get(i).getFanType());
                        }
                    }
                }
            }
        }

       /* System.out.println("===toList==" + toList.size());
        System.out.println("===fromList==" + fromList.size());
        System.out.println("===likesList==" + likesList.size());
        System.out.println("===crushList==" + crushList.size());
        System.out.println("===maybeList=" + maybeList.size());*/

        if (dashboardInstance.myFansList == null || dashboardInstance.myFansList.size() == 0) {
            emptyView.setVisibility(View.VISIBLE);
        } else {
            emptyView.setVisibility(View.GONE);
        }

        if (((MainActivity) getActivity()).getCrushCountTV().isSelected()) {
            setCrushFilterView();
        } else if (((MainActivity) getActivity()).getMaybeCountTV().isSelected()) {
            setMaybeFilterView();
        } else {
            setLikesFilterView();
        }
        pullToRefresh.setRefreshing(false);
    }

    @Subscribe
    public void onFansRefresh(RefreshFans refresh) {
        setDataInGridView();
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

    @OnClick({})
    public void onViewClicked(View v) {
        switch (v.getId()) {

        }
    }

    private void initView() {
        gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        adapter = new MyFansAdapter(getActivity(), likesList);
        adapter.setEmpty(emptyView);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter);

        pullToRefresh.setOnRefreshListener(() -> {
            pullToRefresh.setRefreshing(false);
            dashboardInstance.myFansProgress = true;
            showProgress();
            dashboardInstance.myFansList = new ArrayList<>();
            dashboardInstance.presenter.queryAllMyFans(getContext());
        });

        setLikesFilterView();
    }
}