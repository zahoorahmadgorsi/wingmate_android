package com.app.wingmate.admin.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.wingmate.R;
import com.app.wingmate.admin.events.AdminRefreshSearch;
import com.app.wingmate.base.BaseFragment;
import com.app.wingmate.base.BaseView;
import com.app.wingmate.models.MyCustomUser;
import com.app.wingmate.models.Question;
import com.app.wingmate.models.UserAnswer;
import com.app.wingmate.ui.activities.MainActivity;
import com.app.wingmate.ui.adapters.QuestionOptionsListAdapter;
import com.app.wingmate.admin.ui.adapters.UserViewAdapter;
import com.app.wingmate.ui.dialogs.OptionsSelectorDialog;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.app.wingmate.utils.AppConstants.ERROR;
import static com.app.wingmate.utils.AppConstants.PARAM_CURRENT_LOCATION;
import static com.app.wingmate.utils.AppConstants.TAG_SEARCH;
import static com.app.wingmate.utils.Utilities.showToast;

public class AdminSearchFragment extends BaseFragment implements BaseView, OptionsSelectorDialog.OptionsSelectorDialogClickListener {

    public static final String TAG = AdminSearchFragment.class.getName();

    private AdminDashboardFragment dashboardInstance;

    Unbinder unbinder;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.search_view)
    RelativeLayout searchView;
    @BindView(R.id.search_recycler_view)
    RecyclerView searchRecyclerView;
    @BindView(R.id.empty_view)
    LinearLayout emptyView;

    @BindView(R.id.btn_search)
    Button searchBtn;
    @BindView(R.id.btn_reset_filter)
    TextView resetBtn;

    private QuestionOptionsListAdapter adapter;
    private GridLayoutManager gridLayoutManager;

    private UserViewAdapter userViewAdapter;
    private GridLayoutManager searchGridLayoutManager;

    private int selectedIndex = -1;
    private boolean onCrate = true;

    private double selectedDistanceInKM = 0;

    public AdminSearchFragment() {

    }

    public static AdminSearchFragment newInstance() {
        AdminSearchFragment contentFragment = new AdminSearchFragment();
        return contentFragment;
    }

    public static AdminSearchFragment newInstance(AdminDashboardFragment dashboardInstance) {
        AdminSearchFragment contentFragment = new AdminSearchFragment();
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
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initQuestionsView();
        initSearchView();
    }

    private void initQuestionsView() {
        if (dashboardInstance.questions == null) dashboardInstance.questions = new ArrayList<>();
        gridLayoutManager = new GridLayoutManager(getActivity(), 1);
        adapter = new QuestionOptionsListAdapter(getActivity(), this, dashboardInstance.questions);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void initSearchView() {
        if (dashboardInstance.searchedUsers == null)
            dashboardInstance.searchedUsers = new ArrayList<>();
        searchGridLayoutManager = new GridLayoutManager(getActivity(), 2);
        userViewAdapter = new UserViewAdapter(getActivity(), dashboardInstance, dashboardInstance.searchedUsers);
        userViewAdapter.setEmpty(emptyView);
        searchRecyclerView.setHasFixedSize(false);
        searchRecyclerView.setLayoutManager(searchGridLayoutManager);
        searchRecyclerView.setAdapter(userViewAdapter);
        emptyView.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (dashboardInstance.searchProgress) showProgress();
        else dismissProgress();

//        ((MainActivity) getActivity()).setProfileImage(SharedPrefers.getString(requireContext(), PREF_IMAGE_URL, ""));

        if (dashboardInstance.searchedUsers != null && dashboardInstance.searchedUsers.size() > 0) {
            searchView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            userViewAdapter.setData(dashboardInstance.searchedUsers);
            userViewAdapter.notifyDataSetChanged();
            searchBtn.setText(getString(R.string.search_again));
            resetBtn.setVisibility(View.VISIBLE);
        } else {
            adapter.setData(dashboardInstance.questions);
            adapter.notifyDataSetChanged();
            recyclerView.setVisibility(View.VISIBLE);
            searchView.setVisibility(View.GONE);
            searchBtn.setText(getString(R.string.search));
            resetBtn.setVisibility(View.GONE);
        }
    }

    @Subscribe
    public void onSearchQuestionsRefresh(AdminRefreshSearch refreshSearch) {
        if (dashboardInstance.searchProgress) showProgress();
        else dismissProgress();
        adapter.setData(dashboardInstance.questions);
        adapter.notifyDataSetChanged();
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

    @OnClick({R.id.btn_reset_filter, R.id.btn_search})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.btn_reset_filter:
                resetFilters();
                break;
            case R.id.btn_search:
                if (searchBtn.getText().toString().equals(getString(R.string.search_again))) {
                    searchAgain();
                } else {
                    searchUsers();
                }
                break;
        }
    }

    public void setDistanceValue(double distance) {
        selectedDistanceInKM = distance / 1000;
    }

    public void showOptionSelectionDialog(Question question, int index, String tag) {
        selectedIndex = index;
        OptionsSelectorDialog dialog = OptionsSelectorDialog.newInstance(this, getActivity(), question, tag, TAG_SEARCH);
        dialog.show(getActivity().getSupportFragmentManager(), "selection_dialog");
    }

    @Override
    public void onSelectorBackButtonClick(DialogFragment dialog) {
        dialog.dismiss();
        selectedIndex = -1;
    }

    @Override
    public void onSelectorFieldsUpdateClick(DialogFragment dialog) {
        dialog.dismiss();
    }

    @Override
    public void onSelectorGenderUpdateClick(DialogFragment dialog) {
        dialog.dismiss();
    }

    @Override
    public void onSelectorApplyClick(DialogFragment dialog, Question question) {
        dialog.dismiss();
        if (selectedIndex > -1) {
            dashboardInstance.questions.set(selectedIndex, question);
            dashboardInstance.questions.get(selectedIndex).setSearchedResults(new ArrayList<>());
            adapter.setData(dashboardInstance.questions);
            adapter.notifyDataSetChanged();

            if (dashboardInstance.questions.get(selectedIndex).getUserAnswer().getOptionsObjArray() != null
                    && dashboardInstance.questions.get(selectedIndex).getUserAnswer().getOptionsObjArray().size() > 0)
                dashboardInstance.searchSpecificQuestion(dashboardInstance.questions.get(selectedIndex).getUserAnswer().getOptionsObjArray());
        }
    }

    public void setSpecificQuestionUserAnswers(List<UserAnswer> userAnswers) {
        dismissProgress();
        if (selectedIndex > -1) {
            dashboardInstance.questions.get(selectedIndex).setSearchedResults(userAnswers);
            adapter.setData(dashboardInstance.questions);
            adapter.notifyDataSetChanged();
        }
        selectedIndex = -1;
    }

    public void setSearchResultsFromWithInKm(List<ParseUser> users) {
        dismissProgress();
        if (users != null & users.size() > 0) {
            for (int i = 0; i < users.size(); i++) {
                MyCustomUser myCustomUser = new MyCustomUser();
                myCustomUser.setParseUser(users.get(i));
                myCustomUser.setMatchPercent(0);
//                myCustomUser.setMatchPercent(Utilities.getMatchPercentage(users.get(i)));
                dashboardInstance.searchedUsers.add(myCustomUser);
            }
        }

        searchView.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        userViewAdapter.setEmpty(emptyView);
        userViewAdapter.setData(dashboardInstance.searchedUsers);
        userViewAdapter.notifyDataSetChanged();
        searchBtn.setText(getString(R.string.search_again));
        resetBtn.setVisibility(View.VISIBLE);

        if (dashboardInstance.searchedUsers == null || dashboardInstance.searchedUsers.size() == 0) {
            emptyView.setVisibility(View.VISIBLE);
        } else {
            emptyView.setVisibility(View.GONE);
        }
    }

    private void resetFilters() {
        for (int i = 0; i < dashboardInstance.questions.size(); i++) {
            dashboardInstance.questions.get(i).setUserAnswer(null);
        }
        dashboardInstance.searchedUsers = new ArrayList<>();
        adapter.setData(dashboardInstance.questions);
        adapter.setReset();
        adapter.notifyDataSetChanged();
        recyclerView.setVisibility(View.VISIBLE);
        searchView.setVisibility(View.GONE);
        searchBtn.setText(getString(R.string.search));
        resetBtn.setVisibility(View.GONE);
    }

    private void searchAgain() {
        adapter.setData(dashboardInstance.questions);
        adapter.notifyDataSetChanged();
        recyclerView.setVisibility(View.VISIBLE);
        searchView.setVisibility(View.GONE);
        searchBtn.setText(getString(R.string.search));
        resetBtn.setVisibility(View.GONE);
    }

    private void searchUsers() {

        System.out.println("===>>>>>admin_search");
        ParseGeoPoint myGeoPoint = null;
        if (selectedDistanceInKM > 0) {
            if (dashboardInstance.getLastBestLocation() != null) {
                myGeoPoint = new ParseGeoPoint(dashboardInstance.getLastBestLocation().getLatitude(), dashboardInstance.getLastBestLocation().getLongitude());
            } else {
                showToast(getActivity(), getContext(), "Location permissions are denied!", ERROR);
                ((MainActivity) getActivity()).checkPermissions();
                return;
            }
        }

        int totalNoOfSelectedQuestions = 0;
        List<UserAnswer> allSearchedResults = new ArrayList<>();
        for (int k = 0; k < dashboardInstance.questions.size(); k++) {
            if (dashboardInstance.questions.get(k).getUserAnswer() != null
                    && dashboardInstance.questions.get(k).getUserAnswer().getOptionsObjArray() != null
                    && dashboardInstance.questions.get(k).getUserAnswer().getOptionsObjArray().size() > 0) {
                totalNoOfSelectedQuestions++;
                allSearchedResults.addAll(dashboardInstance.questions.get(k).getSearchedResults());
            }
        }

        dashboardInstance.searchedUsers = new ArrayList<>();
        List<String> userIds = new ArrayList<>();

        for (int y = 0; y < allSearchedResults.size(); y++) {
            int count = 0;
            String userId1 = allSearchedResults.get(y).getUserId().getObjectId();
            for (int z = 0; z < allSearchedResults.size(); z++) {
                String userId2 = allSearchedResults.get(z).getUserId().getObjectId();
                if (userId1.equals(userId2)) {
                    count++;
                }
            }
            if (count == totalNoOfSelectedQuestions) {
                if (!userIds.contains(userId1) && !allSearchedResults.get(y).getUserId().getBoolean("isUserUnsubscribed") ) {
                    MyCustomUser myCustomUser = new MyCustomUser();
                    myCustomUser.setParseUser(allSearchedResults.get(y).getUserId());
                    myCustomUser.setMatchPercent(0);
//                    myCustomUser.setMatchPercent(Utilities.getMatchPercentage(allSearchedResults.get(y).getUserId()));
                    if (selectedDistanceInKM > 0) {
                        ParseGeoPoint userGepPoint = allSearchedResults.get(y).getUserId().getParseGeoPoint(PARAM_CURRENT_LOCATION);
                        if (myGeoPoint != null && userGepPoint!=null && myGeoPoint.distanceInKilometersTo(userGepPoint) <= selectedDistanceInKM) {
                            dashboardInstance.searchedUsers.add(myCustomUser);
                            userIds.add(userId1);
                        }
                    } else {
                        dashboardInstance.searchedUsers.add(myCustomUser);
                        userIds.add(userId1);
                    }
                }
            }
        }

        if (myGeoPoint != null && selectedDistanceInKM > 0 && allSearchedResults.size() == 0) {
            dashboardInstance.searchUsersWithInKM(myGeoPoint, selectedDistanceInKM);
        } else {
            searchView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            userViewAdapter.setEmpty(emptyView);
            userViewAdapter.setData(dashboardInstance.searchedUsers);
            userViewAdapter.notifyDataSetChanged();
            searchBtn.setText(getString(R.string.search_again));
            resetBtn.setVisibility(View.VISIBLE);

            if (dashboardInstance.searchedUsers == null || dashboardInstance.searchedUsers.size() == 0) {
                emptyView.setVisibility(View.VISIBLE);
            } else {
                emptyView.setVisibility(View.GONE);
            }
        }
    }
}