 package com.app.wingmate.ui.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.app.wingmate.R;
import com.app.wingmate.base.BaseFragment;
import com.app.wingmate.base.BaseInteractor;
import com.app.wingmate.base.BasePresenter;
import com.app.wingmate.base.BaseView;
import com.app.wingmate.events.RefreshFanList;
import com.app.wingmate.events.RefreshFans;
import com.app.wingmate.events.RefreshHome;
import com.app.wingmate.events.RefreshSearch;
import com.app.wingmate.models.Fans;
import com.app.wingmate.models.MyCustomUser;
import com.app.wingmate.models.Question;
import com.app.wingmate.models.QuestionOption;
import com.app.wingmate.models.UserAnswer;
import com.app.wingmate.ui.activities.MainActivity;
import com.app.wingmate.ui.dialogs.OptionsSelectorDialog;
import com.app.wingmate.utils.DateUtils;
import com.app.wingmate.utils.Utilities;
import com.app.wingmate.widgets.FadePageTransformer;
import com.app.wingmate.widgets.NonSwappableViewPager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.app.wingmate.utils.APIsUtility.PARSE_CLOUD_FUNCTION_GET_SERVER_TIME;
import static com.app.wingmate.utils.AppConstants.ERROR;
import static com.app.wingmate.utils.AppConstants.INFO;
import static com.app.wingmate.utils.AppConstants.MANDATORY;
import static com.app.wingmate.utils.AppConstants.PARAM_CURRENT_LOCATION;
import static com.app.wingmate.utils.AppConstants.PARAM_IS_PAID_USER;
import static com.app.wingmate.utils.AppConstants.PARAM_NICK;
import static com.app.wingmate.utils.AppConstants.PARAM_OPTIONS_OBJ_ARRAY;
import static com.app.wingmate.utils.AppConstants.PARAM_PROFILE_PIC;
import static com.app.wingmate.utils.AppConstants.PARAM_USER_OPTIONAL_ARRAY;
import static com.app.wingmate.utils.AppConstants.SUCCESS;
import static com.app.wingmate.utils.AppConstants.TAG_SEARCH;
import static com.app.wingmate.utils.AppConstants.TRIAL_PERIOD;
import static com.app.wingmate.utils.Utilities.showToast;

public class DashboardFragment extends BaseFragment implements BaseView, ViewPager.OnPageChangeListener {

    public static final String TAG = DashboardFragment.class.getName();

    Unbinder unbinder;

    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigationView;
    @BindView(R.id.view_pager)
    public NonSwappableViewPager viewPager;
    @BindView(R.id.btn_home)
    LinearLayout btnHome;
    @BindView(R.id.btn_search)
    LinearLayout btnSearch;
    @BindView(R.id.btn_fan)
    LinearLayout btnFan;
    @BindView(R.id.btn_msg)
    LinearLayout btnMsg;
    @BindView(R.id.btn_settings)
    LinearLayout btnSettings;
    @BindView(R.id.ic_home)
    ImageView icHome;
    @BindView(R.id.ic_search)
    ImageView icSearch;
    @BindView(R.id.ic_fan)
    ImageView icFan;
    @BindView(R.id.ic_msg)
    ImageView icMsg;
    @BindView(R.id.ic_settings)
    ImageView icSettings;
    @BindView(R.id.tv_home)
    TextView tvHome;
    @BindView(R.id.tv_search)
    TextView tvSearch;
    @BindView(R.id.tv_fan)
    TextView tvFan;
    @BindView(R.id.tv_msg)
    TextView tvMsg;
    @BindView(R.id.tv_settings)
    TextView tvSettings;

    private HomeFragment homeFragment;
    private SearchFragment searchFragment;
    private MyFansFragment likesFragment;
    private MessagesFragment messagesFragment;
    private SettingsFragment settingsFragment;

    private MyPagerAdapter viewPagerAdapter;

    public List<Question> questions;
    public List<MyCustomUser> searchedUsers;
    public List<MyCustomUser> allUsers;
    public List<Fans> myFansList;
    public boolean homeProgress = false;
    public boolean searchProgress = false;
    public boolean myFansProgress = false;

    private boolean isHomeView = true;

    public BasePresenter presenter;

    private LocationManager locationManager;

    private ExecutorService executor ;
    private Handler handler;

    public DashboardFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
        executor = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        presenter = new BasePresenter(this, new BaseInteractor());

        locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);

        initViews();

        homeProgress = true;
        allUsers = new ArrayList<>();
        presenter.queryAllUsers(getContext());

        searchProgress = true;
        questions = new ArrayList<>();
        presenter.querySearchQuestions(getContext(), MANDATORY);

        myFansProgress = true;
        myFansList = new ArrayList<>();
        presenter.queryAllMyFans(getContext());
    }

    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            item -> {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        viewPager.setCurrentItem(0, false);
                        ((MainActivity) getActivity()).setScreenTitle("Hi, " + ParseUser.getCurrentUser().getString(PARAM_NICK));
                        return true;
                    case R.id.navigation_search:
                        viewPager.setCurrentItem(1, false);
                        ((MainActivity) getActivity()).setScreenTitle("Search");
                        return true;
                    case R.id.navigation_likes:
                        viewPager.setCurrentItem(2, false);
                        ((MainActivity) getActivity()).setScreenTitle("My Fans");
                        return true;
                    case R.id.navigation_messages:
                        viewPager.setCurrentItem(3, false);
                        ((MainActivity) getActivity()).setScreenTitle("Messages");
                        return true;
                    case R.id.navigation_settings:
                        viewPager.setCurrentItem(4, false);
                        ((MainActivity) getActivity()).setScreenTitle("Settings");
                        return true;
                }
                return false;
            };

    private void initViews() {
        homeFragment = HomeFragment.newInstance(this);
        searchFragment = SearchFragment.newInstance(this);
        likesFragment = MyFansFragment.newInstance(this);
        messagesFragment = MessagesFragment.newInstance(this);
        settingsFragment = SettingsFragment.newInstance(this);

        ((MainActivity) getActivity()).hideTopView();
        ((MainActivity) getActivity()).hideScreenTitle();
        ((MainActivity) getActivity()).hideProfileImage();
        btnSearch.setVisibility(View.GONE);
        btnFan.setVisibility(View.GONE);
        btnMsg.setVisibility(View.GONE);
        isHomeView = true;
        viewPagerAdapter = new MyPagerAdapter(requireActivity().getSupportFragmentManager());
        viewPagerAdapter.addFragment(homeFragment);
        viewPagerAdapter.addFragment(searchFragment);
        viewPagerAdapter.addFragment(likesFragment);
        viewPagerAdapter.addFragment(messagesFragment);
        viewPagerAdapter.addFragment(settingsFragment);
        viewPager.setAdapter(viewPagerAdapter);
//        viewPager.setPageTransformer(false, new FadePageTransformer());
        viewPager.setPagingEnabled(false);
//        viewPager.setOffscreenPageLimit(2);
        viewPager.setCurrentItem(0, false);
        viewPager.addOnPageChangeListener(this);

        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        bottomNavigationView.setVisibility(View.GONE);

        resetAllBottomButtons();
        icHome.setColorFilter(ContextCompat.getColor(requireContext(), R.color.purple_theme), android.graphics.PorterDuff.Mode.MULTIPLY);
        tvHome.setTextColor(requireContext().getResources().getColor(R.color.purple_theme));
        btnHome.setAlpha(1.0f);
    }

    public void setHomeView() {
        ((MainActivity) getActivity()).hideTopView();
        ((MainActivity) getActivity()).hideScreenTitle();
        ((MainActivity) getActivity()).hideProfileImage();
        ((MainActivity) getActivity()).hideCountsView();
        btnSearch.setVisibility(View.GONE);
        btnFan.setVisibility(View.GONE);
        btnMsg.setVisibility(View.GONE);
        resetAllBottomButtons();
        icHome.setColorFilter(ContextCompat.getColor(requireContext(), R.color.purple_theme), android.graphics.PorterDuff.Mode.MULTIPLY);
        tvHome.setTextColor(requireContext().getResources().getColor(R.color.purple_theme));
//        viewPager.setCurrentItem(0, true);
        ((MainActivity) getActivity()).setScreenTitle("Hi, " + ParseUser.getCurrentUser().getString(PARAM_NICK));
        btnHome.setAlpha(1.0f);
    }

    public void setAllInView() {
        btnSearch.setVisibility(View.VISIBLE);
        btnFan.setVisibility(View.VISIBLE);
        btnMsg.setVisibility(View.VISIBLE);
        isHomeView = false;
//        viewPagerAdapter.clearFragments();
        viewPagerAdapter = new MyPagerAdapter(requireActivity().getSupportFragmentManager());
        viewPagerAdapter.addFragment(homeFragment);
        viewPagerAdapter.addFragment(searchFragment);
        viewPagerAdapter.addFragment(likesFragment);
        viewPagerAdapter.addFragment(messagesFragment);
        viewPagerAdapter.addFragment(settingsFragment);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setPagingEnabled(false);
//        viewPager.setCurrentItem(0, true);
        viewPager.addOnPageChangeListener(this);

//        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
//        bottomNavigationView.setVisibility(View.GONE);

//        resetAllBottomButtons();
//        icHome.setColorFilter(ContextCompat.getColor(requireContext(), R.color.purple_theme), android.graphics.PorterDuff.Mode.MULTIPLY);
//        tvHome.setTextColor(requireContext().getResources().getColor(R.color.purple_theme));
    }

    public void setTab(int index) {
        switch (index) {
            case 1:
                ((MainActivity) getActivity()).showTopView();
                ((MainActivity) getActivity()).showScreenTitle();
                ((MainActivity) getActivity()).setScreenTitle("Search");
                ((MainActivity) getActivity()).hideCountsView();
                ((MainActivity) getActivity()).setProfileImage(ParseUser.getCurrentUser().getString(PARAM_PROFILE_PIC));
                resetAllBottomButtons();
                icSearch.setColorFilter(ContextCompat.getColor(requireContext(), R.color.purple_theme), android.graphics.PorterDuff.Mode.MULTIPLY);
                tvSearch.setTextColor(requireContext().getResources().getColor(R.color.purple_theme));
                viewPager.setCurrentItem(1, false);
                btnSearch.setVisibility(View.VISIBLE);
                btnFan.setVisibility(View.VISIBLE);
                btnMsg.setVisibility(View.VISIBLE);
                btnMsg.setAlpha(1.0f);
                break;
            case 2:
                ((MainActivity) getActivity()).showTopView();
                ((MainActivity) getActivity()).showScreenTitle();
                ((MainActivity) getActivity()).setScreenTitle("My Fans");
                ((MainActivity) getActivity()).showCountsView();
                ((MainActivity) getActivity()).setProfileImage(ParseUser.getCurrentUser().getString(PARAM_PROFILE_PIC));
                resetAllBottomButtons();
                icFan.setColorFilter(ContextCompat.getColor(requireContext(), R.color.purple_theme), android.graphics.PorterDuff.Mode.MULTIPLY);
                tvFan.setTextColor(requireContext().getResources().getColor(R.color.purple_theme));
                viewPager.setCurrentItem(2, false);
                btnSearch.setVisibility(View.VISIBLE);
                btnFan.setVisibility(View.VISIBLE);
                btnMsg.setVisibility(View.VISIBLE);
                btnFan.setAlpha(1.0f);
                break;
            case 3:
                ((MainActivity) getActivity()).showTopView();
                ((MainActivity) getActivity()).showScreenTitle();
                ((MainActivity) getActivity()).setScreenTitle("Messages");
                ((MainActivity) getActivity()).hideCountsView();
                ((MainActivity) getActivity()).setProfileImage(ParseUser.getCurrentUser().getString(PARAM_PROFILE_PIC));
                resetAllBottomButtons();
                icMsg.setColorFilter(ContextCompat.getColor(requireContext(), R.color.purple_theme), android.graphics.PorterDuff.Mode.MULTIPLY);
                tvMsg.setTextColor(requireContext().getResources().getColor(R.color.purple_theme));
                viewPager.setCurrentItem(3, false);
                btnSearch.setVisibility(View.VISIBLE);
                btnFan.setVisibility(View.VISIBLE);
                btnMsg.setVisibility(View.VISIBLE);
                btnMsg.setAlpha(1.0f);
                break;
        }
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {

    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    private class MyPagerAdapter extends FragmentPagerAdapter {
        private final List<BaseFragment> mFragmentList = new ArrayList<>();

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            return mFragmentList.get(pos);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(BaseFragment fragment) {
            mFragmentList.add(fragment);
        }

        public void clearFragments() {
            mFragmentList.clear();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
//        ((MainActivity) getActivity()).setScreenTitle("Hi, " + ParseUser.getCurrentUser().getString(PARAM_NICK));
        checkPaidUser();
        saveCurrentGeoPoint();
    }

    private void checkPaidUser() {
        if (!ParseUser.getCurrentUser().getBoolean(PARAM_IS_PAID_USER)) {
            presenter.checkServerDate(getContext());
        }
    }

    private void saveCurrentGeoPoint() {
        if (getLastBestLocation() != null) {
            ParseGeoPoint geoPoint = new ParseGeoPoint(getLastBestLocation().getLatitude(), getLastBestLocation().getLongitude());
            ParseUser.getCurrentUser().put(PARAM_CURRENT_LOCATION, geoPoint);
            ParseUser.getCurrentUser().saveInBackground(e -> {
            });
        }
    }

    private Location getLastBestLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }

        Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Location locationNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        long GPSLocationTime = 0;
        if (null != locationGPS) {
            GPSLocationTime = locationGPS.getTime();
        }

        long NetLocationTime = 0;

        if (null != locationNet) {
            NetLocationTime = locationNet.getTime();
        }

        if (0 < GPSLocationTime - NetLocationTime) {
            return locationGPS;
        } else {
            return locationNet;
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

    @OnClick({R.id.btn_home, R.id.btn_search, R.id.btn_fan, R.id.btn_msg, R.id.btn_settings})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.btn_home:
                isHomeView = true;
//                setHomeView();
                ((MainActivity) getActivity()).hideTopView();
                ((MainActivity) getActivity()).hideScreenTitle();
                ((MainActivity) getActivity()).hideCountsView();
                ((MainActivity) getActivity()).hideProfileImage();
                btnSearch.setVisibility(View.GONE);
                btnFan.setVisibility(View.GONE);
                btnMsg.setVisibility(View.GONE);
                resetAllBottomButtons();
                icHome.setColorFilter(ContextCompat.getColor(requireContext(), R.color.purple_theme), android.graphics.PorterDuff.Mode.MULTIPLY);
                tvHome.setTextColor(requireContext().getResources().getColor(R.color.purple_theme));
                viewPager.setCurrentItem(0, false);
                ((MainActivity) getActivity()).setScreenTitle("Hi, " + ParseUser.getCurrentUser().getString(PARAM_NICK));
                btnHome.setAlpha(1.0f);
                break;
            case R.id.btn_search:
                ((MainActivity) getActivity()).showTopView();
                ((MainActivity) getActivity()).showScreenTitle();
                ((MainActivity) getActivity()).hideCountsView();
                ((MainActivity) getActivity()).setProfileImage(ParseUser.getCurrentUser().getString(PARAM_PROFILE_PIC));
                resetAllBottomButtons();
                icSearch.setColorFilter(ContextCompat.getColor(requireContext(), R.color.purple_theme), android.graphics.PorterDuff.Mode.MULTIPLY);
                tvSearch.setTextColor(requireContext().getResources().getColor(R.color.purple_theme));
                viewPager.setCurrentItem(1, false);
                ((MainActivity) getActivity()).setScreenTitle("Search");
                btnSearch.setAlpha(1.0f);
                break;
            case R.id.btn_fan:
                ((MainActivity) getActivity()).showTopView();
                ((MainActivity) getActivity()).showScreenTitle();
                ((MainActivity) getActivity()).showCountsView();
                ((MainActivity) getActivity()).setProfileImage(ParseUser.getCurrentUser().getString(PARAM_PROFILE_PIC));
                resetAllBottomButtons();
                icFan.setColorFilter(ContextCompat.getColor(requireContext(), R.color.purple_theme), android.graphics.PorterDuff.Mode.MULTIPLY);
                tvFan.setTextColor(requireContext().getResources().getColor(R.color.purple_theme));
                viewPager.setCurrentItem(2, false);
                ((MainActivity) getActivity()).setScreenTitle("My Fans");
                btnFan.setAlpha(1.0f);
                break;
            case R.id.btn_msg:
                ((MainActivity) getActivity()).showTopView();
                ((MainActivity) getActivity()).showScreenTitle();
                ((MainActivity) getActivity()).hideCountsView();
                ((MainActivity) getActivity()).setProfileImage(ParseUser.getCurrentUser().getString(PARAM_PROFILE_PIC));
                resetAllBottomButtons();
                icMsg.setColorFilter(ContextCompat.getColor(requireContext(), R.color.purple_theme), android.graphics.PorterDuff.Mode.MULTIPLY);
                tvMsg.setTextColor(requireContext().getResources().getColor(R.color.purple_theme));
                viewPager.setCurrentItem(3, false);
                ((MainActivity) getActivity()).setScreenTitle("Messages");
                btnMsg.setAlpha(1.0f);
                break;
            case R.id.btn_settings:
                ((MainActivity) getActivity()).showTopView();
                ((MainActivity) getActivity()).showScreenTitle();
                ((MainActivity) getActivity()).hideCountsView();
                ((MainActivity) getActivity()).setScreenTitle("Settings");
                ((MainActivity) getActivity()).setProfileImage(ParseUser.getCurrentUser().getString(PARAM_PROFILE_PIC));
                resetAllBottomButtons();
                icSettings.setColorFilter(ContextCompat.getColor(requireContext(), R.color.purple_theme), android.graphics.PorterDuff.Mode.MULTIPLY);
                tvSettings.setTextColor(requireContext().getResources().getColor(R.color.purple_theme));
//                if (isHomeView)
//                    viewPager.setCurrentItem(1, true);
//                else
                viewPager.setCurrentItem(4, false);
                btnSearch.setVisibility(View.VISIBLE);
                btnFan.setVisibility(View.VISIBLE);
                btnMsg.setVisibility(View.VISIBLE);
                btnSettings.setAlpha(1.0f);
                break;
        }
    }

    private void resetAllBottomButtons() {
//        icHome.setColorFilter(ContextCompat.getColor(requireContext(), R.color.grey), android.graphics.PorterDuff.Mode.MULTIPLY);
//        icSearch.setColorFilter(ContextCompat.getColor(requireContext(), R.color.grey), android.graphics.PorterDuff.Mode.MULTIPLY);
//        icFan.setColorFilter(ContextCompat.getColor(requireContext(), R.color.grey), android.graphics.PorterDuff.Mode.MULTIPLY);
//        icMsg.setColorFilter(ContextCompat.getColor(requireContext(), R.color.grey), android.graphics.PorterDuff.Mode.MULTIPLY);
//        icSettings.setColorFilter(ContextCompat.getColor(requireContext(), R.color.grey), android.graphics.PorterDuff.Mode.MULTIPLY);
//        tvHome.setTextColor(requireContext().getResources().getColor(R.color.grey));
//        tvSearch.setTextColor(requireContext().getResources().getColor(R.color.grey));
//        tvFan.setTextColor(requireContext().getResources().getColor(R.color.grey));
//        tvMsg.setTextColor(requireContext().getResources().getColor(R.color.grey));
//        tvSettings.setTextColor(requireContext().getResources().getColor(R.color.grey));

        icHome.setColorFilter(ContextCompat.getColor(requireContext(), R.color.purple_theme), android.graphics.PorterDuff.Mode.MULTIPLY);
        icSearch.setColorFilter(ContextCompat.getColor(requireContext(), R.color.purple_theme), android.graphics.PorterDuff.Mode.MULTIPLY);
        icFan.setColorFilter(ContextCompat.getColor(requireContext(), R.color.purple_theme), android.graphics.PorterDuff.Mode.MULTIPLY);
        icMsg.setColorFilter(ContextCompat.getColor(requireContext(), R.color.purple_theme), android.graphics.PorterDuff.Mode.MULTIPLY);
        icSettings.setColorFilter(ContextCompat.getColor(requireContext(), R.color.purple_theme), android.graphics.PorterDuff.Mode.MULTIPLY);
        tvHome.setTextColor(requireContext().getResources().getColor(R.color.purple_theme));
        tvSearch.setTextColor(requireContext().getResources().getColor(R.color.purple_theme));
        tvFan.setTextColor(requireContext().getResources().getColor(R.color.purple_theme));
        tvMsg.setTextColor(requireContext().getResources().getColor(R.color.purple_theme));
        tvSettings.setTextColor(requireContext().getResources().getColor(R.color.purple_theme));

        btnHome.setAlpha(0.4f);
        btnSearch.setAlpha(0.4f);
        btnFan.setAlpha(0.4f);
        btnMsg.setAlpha(0.4f);
        btnSettings.setAlpha(0.4f);
    }

    @Override
    public void setQuestionsResponseSuccess(List<Question> questions) {
        searchProgress = false;
        this.questions = questions;
        EventBus.getDefault().post(new RefreshSearch());
    }

    @Override
    public void setTrialEnded(String msg) {
        showToast(getActivity(), getContext(), "Trial period ended!", ERROR);
        AlertDialog.Builder dialog = new AlertDialog.Builder(requireContext());
        dialog.setTitle(getString(R.string.app_name))
                .setIcon(R.drawable.app_heart)
                .setMessage("Your trial period has been ended!")
                .setNegativeButton("Pay Now", (dialoginterface, i) -> {
                    dialoginterface.cancel();
                    ParseUser.getCurrentUser().put(PARAM_IS_PAID_USER, true);
                    showToast(getActivity(), getContext(), "Processing...", INFO);
                    ParseUser.getCurrentUser().saveInBackground(en -> {
                        showToast(getActivity(), getContext(), "Congrats on becoming a paid user!", SUCCESS);
                    });
                })
                .setPositiveButton("Cancel", (dialoginterface, i) -> {
                    dialoginterface.cancel();
                    getActivity().onBackPressed();
                }).show();
    }

    public void searchSpecificQuestion(List<QuestionOption> optionsObjArray) {
        showProgress();
        searchedUsers = new ArrayList<>();
        presenter.getSpecificQuestionUserAnswers(getContext(), optionsObjArray);
    }

    @Override
    public void setSpecificQuestionUserAnswersSuccess(List<UserAnswer> userAnswers) {
        dismissProgress();
        searchFragment.setSpecificQuestionUserAnswers(userAnswers);
    }

    @Override
    public void setAllUsersSuccess(List<ParseUser> parseUsers) {
        executor.execute(() -> {
            allUsers = new ArrayList<>();
            if (parseUsers != null && parseUsers.size() > 0) {
                for (int i = 0; i < parseUsers.size(); i++) {
                    MyCustomUser myCustomUser = new MyCustomUser();
                    myCustomUser.setParseUser(parseUsers.get(i));
                    myCustomUser.setMatchPercent(Utilities.getMatchPercentage(parseUsers.get(i)));
                    allUsers.add(myCustomUser);
                }
            }
            handler.post(() -> {
                homeProgress = false;
                EventBus.getDefault().post(new RefreshHome());
            });
        });

//        homeProgress = false;
//        this.allUsers = parseUsers;
//        EventBus.getDefault().post(new RefreshHome());
    }

    @Override
    public void setMyFansSuccess(List<Fans> fansList) {
        myFansProgress = false;
        this.myFansList = fansList;
        EventBus.getDefault().post(new RefreshFans());
    }

    @Subscribe
    public void refreshFansList(RefreshFanList refreshFanList) {
        myFansProgress = true;
        myFansList = new ArrayList<>();
        presenter.queryAllMyFans(getContext());
    }
}