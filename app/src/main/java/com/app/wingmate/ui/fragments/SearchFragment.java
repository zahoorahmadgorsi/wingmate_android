package com.app.wingmate.ui.fragments;

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
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.wingmate.R;
import com.app.wingmate.base.BaseFragment;
import com.app.wingmate.base.BaseInteractor;
import com.app.wingmate.base.BasePresenter;
import com.app.wingmate.base.BaseView;
import com.app.wingmate.events.RefreshSearch;
import com.app.wingmate.models.Fans;
import com.app.wingmate.models.MyCustomUser;
import com.app.wingmate.ui.activities.MainActivity;
import com.app.wingmate.ui.adapters.QuestionOptionsListAdapter;
import com.app.wingmate.models.Question;
import com.app.wingmate.models.UserAnswer;
import com.app.wingmate.ui.adapters.UserViewAdapter;
import com.app.wingmate.ui.dialogs.OptionsSelectorDialog;
import com.app.wingmate.utils.Utilities;
import com.parse.FindCallback;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.app.wingmate.utils.AppConstants.ACTIVE;
import static com.app.wingmate.utils.AppConstants.CLASS_NAME_USER;
import static com.app.wingmate.utils.AppConstants.CLASS_NAME_USER_ANSWER;
import static com.app.wingmate.utils.AppConstants.ERROR;
import static com.app.wingmate.utils.AppConstants.MANDATORY;
import static com.app.wingmate.utils.AppConstants.PARAM_ACCOUNT_STATUS;
import static com.app.wingmate.utils.AppConstants.PARAM_CURRENT_LOCATION;
import static com.app.wingmate.utils.AppConstants.PARAM_GENDER;
import static com.app.wingmate.utils.AppConstants.PARAM_GROUP_CATEGORY;
import static com.app.wingmate.utils.AppConstants.PARAM_OPTIONS_OBJ_ARRAY;
import static com.app.wingmate.utils.AppConstants.PARAM_PROFILE_PIC;
import static com.app.wingmate.utils.AppConstants.PARAM_QUESTION_ID;
import static com.app.wingmate.utils.AppConstants.PARAM_USER_ID;
import static com.app.wingmate.utils.AppConstants.PARAM_USER_MANDATORY_ARRAY;
import static com.app.wingmate.utils.AppConstants.PARAM_USER_OPTIONAL_ARRAY;
import static com.app.wingmate.utils.AppConstants.TAG_SEARCH;
import static com.app.wingmate.utils.Utilities.showToast;

public class SearchFragment extends BaseFragment implements BaseView, OptionsSelectorDialog.OptionsSelectorDialogClickListener {

    public static final String TAG = SearchFragment.class.getName();

    private DashboardFragment dashboardInstance;

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

    public SearchFragment() {

    }

    public static SearchFragment newInstance() {
        SearchFragment contentFragment = new SearchFragment();
        return contentFragment;
    }

    public static SearchFragment newInstance(DashboardFragment dashboardInstance) {
        SearchFragment contentFragment = new SearchFragment();
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
        userViewAdapter = new UserViewAdapter(getActivity(), dashboardInstance.searchedUsers);
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

        ((MainActivity) getActivity()).setProfileImage(ParseUser.getCurrentUser().getString(PARAM_PROFILE_PIC));

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

//        dashboardInstance.performUserUpdateAction();
    }

    @Subscribe
    public void onSearchQuestionsRefresh(RefreshSearch refreshSearch) {
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
            List<UserAnswer> filter = new ArrayList<>();
            for (int i = 0; i < userAnswers.size(); i++) {
                if (userAnswers.get(i).getUserId()!=null)
                if (!Objects.equals(ParseUser.getCurrentUser().getString(PARAM_GENDER), userAnswers.get(i).getUserId().getString(PARAM_GENDER))) {
                    filter.add(userAnswers.get(i));
                }
            }
//            dashboardInstance.questions.get(selectedIndex).setSearchedResults(userAnswers);
            dashboardInstance.questions.get(selectedIndex).setSearchedResults(filter);
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
                myCustomUser.setMatchPercent(Utilities.getMatchPercentage(users.get(i)));
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
        System.out.println("===>>>>>user_search");

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
            System.out.println(ParseUser.getCurrentUser().getObjectId() + "==2==" + userId1);
            if (!userId1.equals(ParseUser.getCurrentUser().getObjectId())) {
                for (int z = 0; z < allSearchedResults.size(); z++) {
                    String userId2 = allSearchedResults.get(z).getUserId().getObjectId();
                    if (userId1.equals(userId2)) {
                        count++;
                    }
                }
            }
            if (count == totalNoOfSelectedQuestions) {
                if (!userIds.contains(userId1)
                        && allSearchedResults.get(y).getUserId().getInt(PARAM_ACCOUNT_STATUS) == ACTIVE
                        && !allSearchedResults.get(y).getUserId().getBoolean("isUserUnsubscribed") //added by zaki
                ) {
                    MyCustomUser myCustomUser = new MyCustomUser();
                    myCustomUser.setParseUser(allSearchedResults.get(y).getUserId());
                    myCustomUser.setMatchPercent(Utilities.getMatchPercentage(allSearchedResults.get(y).getUserId()));
                    if (selectedDistanceInKM > 0) {
                        ParseGeoPoint userGepPoint = allSearchedResults.get(y).getUserId().getParseGeoPoint(PARAM_CURRENT_LOCATION);
                        if (myGeoPoint != null && myGeoPoint.distanceInKilometersTo(userGepPoint) <= selectedDistanceInKM) {
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
            List<MyCustomUser> searchedUsersTemp = new ArrayList<>();
            if (dashboardInstance.searchedUsers != null && dashboardInstance.searchedUsers.size() > 0) {
                for (int i = 0; i < dashboardInstance.searchedUsers.size(); i++) {
                    String currentUserCategory = ParseUser.getCurrentUser().getString(PARAM_GROUP_CATEGORY);
                    String otherUserCategory = dashboardInstance.searchedUsers.get(i).getParseUser().getString(PARAM_GROUP_CATEGORY);
                    if (currentUserCategory != null) {
                        if (currentUserCategory.equalsIgnoreCase(otherUserCategory)) {
                            searchedUsersTemp.add(dashboardInstance.searchedUsers.get(i));
                        }
                    }
                }
            }
            dashboardInstance.searchedUsers = searchedUsersTemp;

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

//    private void manualSearch() {
//        List<Question> filledQuestions = new ArrayList<>();
//        for (int k = 0; k < dashboardInstance.questions.size(); k++) {
//            if (dashboardInstance.questions.get(k).getUserAnswer() != null
//                    && dashboardInstance.questions.get(k).getUserAnswer().getOptionsObjArray() != null
//                    && dashboardInstance.questions.get(k).getUserAnswer().getOptionsObjArray().size() > 0) {
//                filledQuestions.add(dashboardInstance.questions.get(k));
//            }
//        }
//
//        ParseQuery<ParseUser> query = ParseUser.getQuery();
//        query.whereExists(PARAM_USER_MANDATORY_ARRAY);
//        query.include(PARAM_USER_MANDATORY_ARRAY);
//        query.include(PARAM_USER_OPTIONAL_ARRAY);
//        query.include(PARAM_QUESTION_ID);
//        query.include(PARAM_OPTIONS_OBJ_ARRAY);
//        query.setLimit(1000);
//        query.findInBackground((objects, e) -> {
//            if (e == null) {
//
//                List<ParseUser> filteredUsers = new ArrayList<>();
//
//                boolean queFound = false;
//                boolean hasAnyOption = false;
//                int matchCount = 0;
//
//                for (int i = 0; i < objects.size(); i++) {
//
//                    List<UserAnswer> userAnswers = objects.get(i).getList(PARAM_USER_MANDATORY_ARRAY);
//
//                    matchCount = 0;
//
//                    if (userAnswers != null && userAnswers.size() > 0) {
//
//                        for (int x = 0; x < filledQuestions.size(); x++) {
//
//                            queFound = false;
//                            hasAnyOption = false;
//
//                            for (int y = 0; y < userAnswers.size(); y++) {
//
//                                if (filledQuestions.get(x).getObjectId().equals(userAnswers.get(y).getQuestionId().getObjectId())) {
//
//                                    queFound = true;
//
//                                    for (int z = 0; z < filledQuestions.get(x).getUserAnswer().getOptionsObjArray().size(); z++) {
//
//                                        for (int a = 0; a < userAnswers.get(y).getOptionsObjArray().size(); a++) {
//
//                                            if (filledQuestions.get(x).getUserAnswer().getOptionsObjArray().get(z).getObjectId().equals(userAnswers.get(y).getOptionsObjArray().get(a).getObjectId())) {
//
//                                                hasAnyOption = true;
//
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//
//                            if (queFound && hasAnyOption) {
//                                matchCount++;
//                            }
//                        }
//                    }
//                    if (filledQuestions.size() > 0 && (matchCount == filledQuestions.size())) {
//                        filteredUsers.add(objects.get(i));
//                    }
//
//                }
//                AlertDialog.Builder dialog = new AlertDialog.Builder(requireContext());
//                dialog.setTitle("Alert")
//                        .setIcon(R.drawable.app_heart)
//                        .setMessage(filteredUsers.size() + " Results Found")
//                        .setNegativeButton("Ok", (dialoginterface, i) -> dialoginterface.cancel())
//                        .show();
//            } else {
//                System.out.println("====ee====" + e.getMessage());
//            }
//            dismissProgress();
//        });
//    }
}