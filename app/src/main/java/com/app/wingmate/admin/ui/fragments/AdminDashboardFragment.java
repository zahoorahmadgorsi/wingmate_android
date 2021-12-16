package com.app.wingmate.admin.ui.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.app.wingmate.R;
import com.app.wingmate.admin.events.AdminRefreshDashboard;
import com.app.wingmate.base.BaseInteractor;
import com.app.wingmate.base.BasePresenter;
import com.app.wingmate.base.BaseView;
import com.app.wingmate.admin.events.AdminRefreshHome;
import com.app.wingmate.admin.events.AdminRefreshSearch;
import com.app.wingmate.models.MyCustomUser;
import com.app.wingmate.base.BaseFragment;
import com.app.wingmate.models.Question;
import com.app.wingmate.models.QuestionOption;
import com.app.wingmate.models.UserAnswer;
import com.app.wingmate.ui.activities.MainActivity;
import com.app.wingmate.utils.AppConstants;
import com.app.wingmate.utils.SharedPrefers;
import com.app.wingmate.widgets.NonSwappableViewPager;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.app.wingmate.utils.AppConstants.MANDATORY;
import static com.app.wingmate.utils.CommonKeys.PREF_EMAIL;
import static com.app.wingmate.utils.CommonKeys.PREF_IMAGE_URL;

public class AdminDashboardFragment extends BaseFragment implements BaseView, ViewPager.OnPageChangeListener, com.google.android.gms.location.LocationListener {

    public static final String TAG = AdminDashboardFragment.class.getName();

    Unbinder unbinder;

    @BindView(R.id.view_pager)
    public NonSwappableViewPager viewPager;
    @BindView(R.id.btn_home)
    LinearLayout btnHome;
    @BindView(R.id.btn_search)
    LinearLayout btnSearch;
    @BindView(R.id.ic_home)
    ImageView icHome;
    @BindView(R.id.ic_search)
    ImageView icSearch;

    @BindView(R.id.tv_home)
    TextView tvHome;
    @BindView(R.id.tv_search)
    TextView tvSearch;

    private AdminHomeFragment homeFragment;
    private AdminSearchFragment searchFragment;

    private MyPagerAdapter viewPagerAdapter;

    public List<Question> questions = new ArrayList<>();
    public List<MyCustomUser> searchedUsers;
    public List<MyCustomUser> allUsers;
    public List<MyCustomUser> filteredUsers;
    public boolean homeProgress = false;
    public boolean searchProgress = false;

    private boolean isHomeView = true;

    public BasePresenter presenter;

    private LocationManager locationManager;

    private ExecutorService executor;
    private Handler handler;

    public double currentLocationLatitude = AppConstants.DEFAULT_LATITUDE;
    public double currentLocationLongitude = AppConstants.DEFAULT_LONGITUDE;

    public AdminDashboardFragment() {

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
        View view = inflater.inflate(R.layout.fragment_main_admin, container, false);
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
        filteredUsers = new ArrayList<>();
        presenter.queryAdminAllUsers(getContext());

        searchProgress = true;
        questions = new ArrayList<>();
        presenter.querySearchQuestions(getContext(), MANDATORY);
    }

    private void initViews() {
        homeFragment = AdminHomeFragment.newInstance(this);
        searchFragment = AdminSearchFragment.newInstance(this);
        ((MainActivity) getActivity()).hideTopView();
        ((MainActivity) getActivity()).hideScreenTitle();
        ((MainActivity) getActivity()).hideProfileImage();
        btnSearch.setVisibility(View.VISIBLE);
        isHomeView = true;
        viewPagerAdapter = new MyPagerAdapter(requireActivity().getSupportFragmentManager());
        viewPagerAdapter.addFragment(homeFragment);
        viewPagerAdapter.addFragment(searchFragment);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setPagingEnabled(false);
        viewPager.setCurrentItem(0, false);
        viewPager.addOnPageChangeListener(this);

        resetAllBottomButtons();
        icHome.setColorFilter(ContextCompat.getColor(requireContext(), R.color.purple_theme), android.graphics.PorterDuff.Mode.MULTIPLY);
        tvHome.setTextColor(requireContext().getResources().getColor(R.color.purple_theme));
        btnHome.setAlpha(1.0f);
    }

    public void setHomeView() {
        ((MainActivity) getActivity()).hideTopView();
        ((MainActivity) getActivity()).hideScreenTitle();
        ((MainActivity) getActivity()).hideProfileImage();
        //((MainActivity) getActivity()).hideCountsView();
        ((MainActivity) getActivity()).hideFansCountView();
        btnSearch.setVisibility(View.VISIBLE);
        resetAllBottomButtons();
        icHome.setColorFilter(ContextCompat.getColor(requireContext(), R.color.purple_theme), android.graphics.PorterDuff.Mode.MULTIPLY);
        tvHome.setTextColor(requireContext().getResources().getColor(R.color.purple_theme));
//        viewPager.setCurrentItem(0, true);
//        ((MainActivity) getActivity()).setScreenTitle("Hi, " + ParseUser.getCurrentUser().getString(PARAM_NICK));
        btnHome.setAlpha(1.0f);
    }

    public void setAllInView() {
        btnSearch.setVisibility(View.VISIBLE);
        isHomeView = false;
        viewPagerAdapter = new MyPagerAdapter(requireActivity().getSupportFragmentManager());
        viewPagerAdapter.addFragment(homeFragment);
        viewPagerAdapter.addFragment(searchFragment);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setPagingEnabled(false);
        viewPager.addOnPageChangeListener(this);
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
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

    }

    public Location getLastBestLocation() {
        Location defaultLoc = new Location("default");
        defaultLoc.setLatitude(currentLocationLatitude);
        defaultLoc.setLongitude(currentLocationLongitude);
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return defaultLoc;
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
            if (locationGPS==null) locationGPS = defaultLoc;
            return locationGPS;
        } else {
            if (locationNet==null) locationNet = defaultLoc;
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

    @OnClick({R.id.btn_home, R.id.btn_search})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.btn_home:
                isHomeView = true;
                ((MainActivity) getActivity()).hideTopView();
                ((MainActivity) getActivity()).hideScreenTitle();
                //((MainActivity) getActivity()).hideCountsView();
                ((MainActivity) getActivity()).hideFansCountView();
                ((MainActivity) getActivity()).hideProfileImage();
                btnSearch.setVisibility(View.VISIBLE);
                resetAllBottomButtons();
                icHome.setColorFilter(ContextCompat.getColor(requireContext(), R.color.purple_theme), android.graphics.PorterDuff.Mode.MULTIPLY);
                tvHome.setTextColor(requireContext().getResources().getColor(R.color.purple_theme));
                viewPager.setCurrentItem(0, false);
                ((MainActivity) getActivity()).setScreenTitle("Hi, " + SharedPrefers.getString(requireContext(), PREF_EMAIL, ""));
                btnHome.setAlpha(1.0f);
                break;
            case R.id.btn_search:
                ((MainActivity) getActivity()).showTopView();
                ((MainActivity) getActivity()).showScreenTitle();
                //((MainActivity) getActivity()).hideCountsView();
                ((MainActivity) getActivity()).hideFansCountView();
                ((MainActivity) getActivity()).setProfileImage(SharedPrefers.getString(requireContext(), PREF_IMAGE_URL, ""));
                ((MainActivity) getActivity()).hideProfileImage();
                resetAllBottomButtons();
                icSearch.setColorFilter(ContextCompat.getColor(requireContext(), R.color.purple_theme), android.graphics.PorterDuff.Mode.MULTIPLY);
                tvSearch.setTextColor(requireContext().getResources().getColor(R.color.purple_theme));
                viewPager.setCurrentItem(1, false);
                ((MainActivity) getActivity()).setScreenTitle("Search");
                btnSearch.setAlpha(1.0f);
                break;
        }
    }

    private void resetAllBottomButtons() {
        icHome.setColorFilter(ContextCompat.getColor(requireContext(), R.color.purple_theme), android.graphics.PorterDuff.Mode.MULTIPLY);
        icSearch.setColorFilter(ContextCompat.getColor(requireContext(), R.color.purple_theme), android.graphics.PorterDuff.Mode.MULTIPLY);
        tvHome.setTextColor(requireContext().getResources().getColor(R.color.purple_theme));
        tvSearch.setTextColor(requireContext().getResources().getColor(R.color.purple_theme));
        btnHome.setAlpha(0.4f);
        btnSearch.setAlpha(0.4f);
    }

    @Override
    public void setQuestionsResponseSuccess(List<Question> questions) {
        searchProgress = false;
        this.questions = questions;
        if (this.questions == null) this.questions = new ArrayList<>();
        EventBus.getDefault().post(new AdminRefreshSearch());
    }

    public void searchSpecificQuestion(List<QuestionOption> optionsObjArray) {
        showProgress();
        searchedUsers = new ArrayList<>();
        presenter.getSpecificQuestionUserAnswers(getContext(), optionsObjArray);
    }

    public void searchUsersWithInKM(ParseGeoPoint myGeoPoint, double distance) {
        showProgress();
        searchedUsers = new ArrayList<>();
        presenter.adminGetUsersWithInKM(getContext(), myGeoPoint, distance);
    }

    @Override
    public void setSpecificQuestionUserAnswersSuccess(List<UserAnswer> userAnswers) {
        dismissProgress();
        searchFragment.setSpecificQuestionUserAnswers(userAnswers);
    }

    @Override
    public void setWithInKMUsersSuccess(List<ParseUser> users) {
        dismissProgress();
        searchFragment.setSearchResultsFromWithInKm(users);
    }

    @Override
    public void setAllUsersSuccess(List<ParseUser> parseUsers) {
        executor.execute(() -> {
            allUsers = new ArrayList<>();
            filteredUsers = new ArrayList<>();
            if (parseUsers != null && parseUsers.size() > 0) {
                for (int i = 0; i < parseUsers.size(); i++) {
                    MyCustomUser myCustomUser = new MyCustomUser();
                    myCustomUser.setParseUser(parseUsers.get(i));
                    allUsers.add(myCustomUser);
                }
            }
            handler.post(() -> {
                homeProgress = false;
                filteredUsers = allUsers;
                EventBus.getDefault().post(new AdminRefreshHome());
            });
        });
    }

    @Subscribe
    public void refreshDashboardUsersList(AdminRefreshDashboard refreshDashboard) {
        homeProgress = true;
        allUsers = new ArrayList<>();
        filteredUsers = new ArrayList<>();
        presenter.queryAdminAllUsers(getContext());
    }
}