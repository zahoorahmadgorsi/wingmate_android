package com.app.wingmate.ui.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.app.wingmate.R;
import com.app.wingmate.base.BaseFragment;
import com.app.wingmate.base.BaseInteractor;
import com.app.wingmate.base.BasePresenter;
import com.app.wingmate.base.BaseView;
import com.app.wingmate.events.RefreshHome;
import com.app.wingmate.events.RefreshSearch;
import com.app.wingmate.models.Question;
import com.app.wingmate.models.QuestionOption;
import com.app.wingmate.models.UserAnswer;
import com.app.wingmate.ui.activities.MainActivity;
import com.app.wingmate.ui.dialogs.OptionsSelectorDialog;
import com.app.wingmate.utils.DateUtils;
import com.app.wingmate.widgets.NonSwappableViewPager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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

    private HomeFragment homeFragment;
    private SearchFragment searchFragment;
    private DummyFragment likesFragment;
    private DummyFragment messagesFragment;
    private SettingsFragment settingsFragment;

    private MyPagerAdapter viewPagerAdapter;

    public List<Question> questions;
    public List<ParseUser> searchedUsers;
    public List<ParseUser> allUsers;
    public boolean searchProgress = false;
    public boolean homeProgress = false;

    public BasePresenter presenter;

    private LocationManager locationManager;

    public DashboardFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
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
    }

    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            item -> {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        viewPager.setCurrentItem(0, true);
                        ((MainActivity) getActivity()).setScreenTitle("Hi, " + ParseUser.getCurrentUser().getString(PARAM_NICK));
                        return true;
                    case R.id.navigation_search:
                        viewPager.setCurrentItem(1, true);
                        ((MainActivity) getActivity()).setScreenTitle("Search");
                        return true;
                    case R.id.navigation_likes:
                        viewPager.setCurrentItem(2, true);
                        ((MainActivity) getActivity()).setScreenTitle("My Likes");
                        return true;
                    case R.id.navigation_messages:
                        viewPager.setCurrentItem(3, true);
                        ((MainActivity) getActivity()).setScreenTitle("Messages");
                        return true;
                    case R.id.navigation_settings:
                        viewPager.setCurrentItem(4, true);
                        ((MainActivity) getActivity()).setScreenTitle("Settings");
                        return true;
                }
                return false;
            };

    private void initViews() {

        homeFragment = HomeFragment.newInstance(this);
        searchFragment = SearchFragment.newInstance(this);
        likesFragment = DummyFragment.newInstance(this);
        messagesFragment = DummyFragment.newInstance(this);
        settingsFragment = SettingsFragment.newInstance(this);

        viewPagerAdapter = new MyPagerAdapter(requireActivity().getSupportFragmentManager());
        viewPagerAdapter.addFragment(homeFragment);
        viewPagerAdapter.addFragment(searchFragment);
        viewPagerAdapter.addFragment(likesFragment);
        viewPagerAdapter.addFragment(messagesFragment);
        viewPagerAdapter.addFragment(settingsFragment);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setPagingEnabled(false);
//        viewPager.setOffscreenPageLimit(2);
        viewPager.setCurrentItem(0, true);
        viewPager.addOnPageChangeListener(this);

        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        bottomNavigationView.setVisibility(View.VISIBLE);
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
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).setScreenTitle("Hi, " + ParseUser.getCurrentUser().getString(PARAM_NICK));
       checkPaidUser();
       saveCurrentGeoPoint();
    }

    private void checkPaidUser() {
        if (!ParseUser.getCurrentUser().getBoolean(PARAM_IS_PAID_USER)) {
            presenter.checkServerDate(getContext());
        }
    }

    private void saveCurrentGeoPoint() {
        ParseGeoPoint geoPoint = new ParseGeoPoint(getLastBestLocation().getLatitude(), getLastBestLocation().getLongitude());
        ParseUser.getCurrentUser().put(PARAM_CURRENT_LOCATION, geoPoint);
        ParseUser.getCurrentUser().saveInBackground(e -> {
        });
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

    @OnClick({})
    public void onViewClicked(View v) {

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
        homeProgress = false;
        this.allUsers = parseUsers;
        EventBus.getDefault().post(new RefreshHome());
    }
}