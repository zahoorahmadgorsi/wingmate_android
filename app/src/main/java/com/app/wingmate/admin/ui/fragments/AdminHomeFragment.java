package com.app.wingmate.admin.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.wingmate.R;
import com.app.wingmate.base.BaseFragment;
import com.app.wingmate.admin.events.AdminRefreshHome;
import com.app.wingmate.admin.events.AdminRefreshHomeWithNewLocation;
import com.app.wingmate.admin.ui.adapters.UserViewAdapter;
import com.app.wingmate.utils.ActivityUtility;
import com.app.wingmate.utils.SharedPrefers;
import com.parse.GetCallback;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.app.wingmate.utils.AppConstants.ACTIVE;
import static com.app.wingmate.utils.AppConstants.GROUP_A;
import static com.app.wingmate.utils.AppConstants.GROUP_B;
import static com.app.wingmate.utils.AppConstants.NEW;
import static com.app.wingmate.utils.AppConstants.PARAM_ACCOUNT_STATUS;
import static com.app.wingmate.utils.AppConstants.PARAM_GROUP_CATEGORY;
import static com.app.wingmate.utils.AppConstants.PARAM_IS_MEDIA_APPROVED;
import static com.app.wingmate.utils.AppConstants.PARAM_NICK;
import static com.app.wingmate.utils.AppConstants.PARAM_PROFILE_PIC;
import static com.app.wingmate.utils.AppConstants.PENDING;
import static com.app.wingmate.utils.AppConstants.REJECT;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_PRE_LOGIN;
import static com.app.wingmate.utils.CommonKeys.PREF_EMAIL;
import static com.app.wingmate.utils.CommonKeys.PREF_IMAGE_URL;
import static com.app.wingmate.utils.CommonKeys.PREF_PASSWORD;

public class AdminHomeFragment extends BaseFragment {

    public static final String TAG = AdminHomeFragment.class.getName();

    Unbinder unbinder;

    @BindView(R.id.pullToRefresh)
    SwipeRefreshLayout pullToRefresh;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.empty_view)
    LinearLayout emptyView;
    @BindView(R.id.profile_img)
    CircleImageView profileImg;

    @BindView(R.id.all)
    TextView allView;
    @BindView(R.id.group_a)
    TextView groupAView;
    @BindView(R.id.group_b)
    TextView groupBView;
    @BindView(R.id.new_req)
    TextView newRequestsView;
    @BindView(R.id.rejected)
    TextView rejectedView;
    @BindView(R.id.user_name)
    TextView usernameTV;

    private UserViewAdapter userViewAdapter;
    private GridLayoutManager gridLayoutManager;

    private int selectedIndex = -1;
    private boolean onCrate = true;

    private AdminDashboardFragment dashboardInstance;

    public AdminHomeFragment() {

    }

    public static AdminHomeFragment newInstance(AdminDashboardFragment dashboardInstance) {
        AdminHomeFragment contentFragment = new AdminHomeFragment();
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
        View view = inflater.inflate(R.layout.fragment_admin_home, container, false);
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
        if (dashboardInstance.filteredUsers != null) {
            userViewAdapter.setData(dashboardInstance.filteredUsers);
            userViewAdapter.notifyDataSetChanged();
        }
        if (dashboardInstance.allUsers == null || dashboardInstance.allUsers.size() == 0) {
            emptyView.setVisibility(View.VISIBLE);
        } else {
            emptyView.setVisibility(View.GONE);
        }

        filterUsers();
//        Picasso.get()
//                .load(SharedPrefers.getString(requireContext(), PREF_IMAGE_URL, ""))
//                .centerCrop()
//                .resize(500, 500)
//                .placeholder(R.drawable.image_placeholder)
//                .error(R.drawable.image_placeholder)
//                .into(profileImg);

        ParseUser.getCurrentUser().fetchInBackground((GetCallback<ParseUser>) (parseUser, e) -> {
            if (ParseUser.getCurrentUser().getString(PARAM_PROFILE_PIC) != null && ParseUser.getCurrentUser().getString(PARAM_PROFILE_PIC).length() > 0)
                Picasso.get()
                        .load(ParseUser.getCurrentUser().getString(PARAM_PROFILE_PIC))
                        .centerCrop()
                        .resize(500, 500)
                        .placeholder(R.drawable.image_placeholder)
                        .into(profileImg);
            else
                Picasso.get()
                        .load(R.drawable.image_placeholder)
                        .centerCrop()
                        .resize(500, 500)
                        .placeholder(R.drawable.image_placeholder)
                        .into(profileImg);
        });

        usernameTV.setText("Hi, " + ParseUser.getCurrentUser().getString(PARAM_NICK));
//        usernameTV.setText("Hi, " + SharedPrefers.getString(requireContext(), PREF_EMAIL, "Admin"));
    }

    @Subscribe
    public void onHomeRefresh(AdminRefreshHome refreshHome) {
        if (dashboardInstance.homeProgress) showProgress();
        else dismissProgress();
        pullToRefresh.setRefreshing(false);
        userViewAdapter.setData(dashboardInstance.filteredUsers);
        userViewAdapter.notifyDataSetChanged();
        if (dashboardInstance.filteredUsers == null || dashboardInstance.filteredUsers.size() == 0) {
            emptyView.setVisibility(View.VISIBLE);
        } else {
            emptyView.setVisibility(View.GONE);
        }

        filterUsers();
    }


    @Subscribe
    public void onHomeRefreshWithNewLocation(AdminRefreshHomeWithNewLocation refreshHome) {
        pullToRefresh.setRefreshing(false);
        userViewAdapter.setData(dashboardInstance.filteredUsers);
        userViewAdapter.notifyDataSetChanged();

        if (dashboardInstance.filteredUsers == null || dashboardInstance.filteredUsers.size() == 0) {
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

    @OnClick({R.id.logout, R.id.all, R.id.group_a, R.id.group_b, R.id.new_req, R.id.rejected})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.logout:
//                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
//                builder.setMessage("Are you sure you want to logout?");
//                builder.setPositiveButton("Yes", (dialog, which) -> {
//                    dialog.cancel();
//                    SharedPrefers.saveString(requireContext(), PREF_EMAIL, "");
//                    SharedPrefers.saveString(requireContext(), PREF_PASSWORD, "");
//                    SharedPrefers.saveString(requireContext(), PREF_IMAGE_URL, "");
//                    ActivityUtility.startActivity(requireActivity(), KEY_FRAGMENT_PRE_LOGIN);
//                });
//                builder.setNegativeButton("No", (dialog, which) -> {
//                    dialog.cancel();
//                });
//                builder.show();
                break;
            case R.id.all:
                allView.setSelected(true);
                groupAView.setSelected(false);
                groupBView.setSelected(false);
                newRequestsView.setSelected(false);
                rejectedView.setSelected(false);
                filterUsers();
                break;
            case R.id.group_a:
                allView.setSelected(false);
                groupAView.setSelected(true);
                groupBView.setSelected(false);
                newRequestsView.setSelected(false);
                rejectedView.setSelected(false);
                filterUsers();
                break;
            case R.id.group_b:
                allView.setSelected(false);
                groupAView.setSelected(false);
                groupBView.setSelected(true);
                newRequestsView.setSelected(false);
                rejectedView.setSelected(false);
                filterUsers();
                break;
            case R.id.new_req:
                allView.setSelected(false);
                groupAView.setSelected(false);
                groupBView.setSelected(false);
                newRequestsView.setSelected(true);
                rejectedView.setSelected(false);
                filterUsers();
                break;
            case R.id.rejected:
                allView.setSelected(false);
                groupAView.setSelected(false);
                groupBView.setSelected(false);
                newRequestsView.setSelected(false);
                rejectedView.setSelected(true);
                filterUsers();
                break;
        }
    }

    private void initView() {
        allView.setSelected(true);
        groupAView.setSelected(false);
        groupBView.setSelected(false);
        newRequestsView.setSelected(false);
        gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        userViewAdapter = new UserViewAdapter(getActivity(), dashboardInstance, dashboardInstance.filteredUsers);
        userViewAdapter.setEmpty(emptyView);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(userViewAdapter);

//        Picasso.get()
//                .load(ParseUser.getCurrentUser().getString(PARAM_PROFILE_PIC))
//                .centerCrop()
//                .resize(500, 500)
//                .placeholder(R.drawable.image_placeholder)
//                .into(profileImg);

        pullToRefresh.setOnRefreshListener(() -> {
            pullToRefresh.setRefreshing(false);
            dashboardInstance.homeProgress = true;
            showProgress();
            dashboardInstance.filteredUsers = new ArrayList<>();
            dashboardInstance.presenter.queryAdminAllUsers(getContext());
        });
    }

    private void filterUsers() {
        dashboardInstance.filteredUsers = new ArrayList<>();
        if (dashboardInstance.allUsers != null && dashboardInstance.allUsers.size() > 0) {
            for (int i = 0; i < dashboardInstance.allUsers.size(); i++) {

                String groupCat = dashboardInstance.allUsers.get(i).getParseUser().getString(PARAM_GROUP_CATEGORY);
                int accStatus = dashboardInstance.allUsers.get(i).getParseUser().getInt(PARAM_ACCOUNT_STATUS);

                if (allView.isSelected()) {
                    dashboardInstance.filteredUsers.add(dashboardInstance.allUsers.get(i));
                } else if (groupAView.isSelected()) {
                    if (groupCat != null && groupCat.equalsIgnoreCase(GROUP_A) && accStatus == ACTIVE) {
                        dashboardInstance.filteredUsers.add(dashboardInstance.allUsers.get(i));
                    }
                } else if (groupBView.isSelected()) {
                    if (groupCat != null && groupCat.equalsIgnoreCase(GROUP_B) && accStatus == ACTIVE) {
                        dashboardInstance.filteredUsers.add(dashboardInstance.allUsers.get(i));
                    }
                } else if (rejectedView.isSelected()) {
                    if (accStatus == REJECT) {
                        dashboardInstance.filteredUsers.add(dashboardInstance.allUsers.get(i));
                    }
                } else if (newRequestsView.isSelected()) {
                    if ((groupCat == null || groupCat.isEmpty() || groupCat.equalsIgnoreCase(NEW) || accStatus == PENDING) && accStatus != REJECT) {
                        dashboardInstance.filteredUsers.add(dashboardInstance.allUsers.get(i));
                    }
                }
            }
        }
        userViewAdapter.setData(dashboardInstance.filteredUsers);
        userViewAdapter.notifyDataSetChanged();

        if (dashboardInstance.filteredUsers == null || dashboardInstance.filteredUsers.size() == 0) {
            emptyView.setVisibility(View.VISIBLE);
        } else {
            emptyView.setVisibility(View.GONE);
        }
    }
}