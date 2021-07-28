package com.app.wingmate.ui.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.app.wingmate.events.RefreshDashboard;
import com.app.wingmate.events.RefreshDashboardView;
import com.app.wingmate.events.RefreshFanList;
import com.app.wingmate.events.RefreshFans;
import com.app.wingmate.events.RefreshHome;
import com.app.wingmate.events.RefreshHomeWithNewLocation;
import com.app.wingmate.events.RefreshSearch;
import com.app.wingmate.events.RefreshUserStatus;
import com.app.wingmate.models.Fans;
import com.app.wingmate.models.MyCustomUser;
import com.app.wingmate.models.Question;
import com.app.wingmate.models.QuestionOption;
import com.app.wingmate.models.UserAnswer;
import com.app.wingmate.ui.activities.MainActivity;
import com.app.wingmate.utils.ActivityUtility;
import com.app.wingmate.utils.AlertMessages;
import com.app.wingmate.utils.AppConstants;
import com.app.wingmate.utils.DateUtils;
import com.app.wingmate.utils.SharedPrefers;
import com.app.wingmate.utils.Utilities;
import com.app.wingmate.widgets.NonSwappableViewPager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.GetCallback;
import com.parse.ParseGeoPoint;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.app.wingmate.utils.AlertMessages.GO_TO_ACC_PENDING_SCREEN;
import static com.app.wingmate.utils.AlertMessages.GO_TO_PAYMENT_SCREEN;
import static com.app.wingmate.utils.AlertMessages.GO_TO_QUESTIONNAIRE_SCREEN;
import static com.app.wingmate.utils.AlertMessages.GO_TO_UPLOAD_SCREEN;
import static com.app.wingmate.utils.AppConstants.ACTIVE;
import static com.app.wingmate.utils.AppConstants.MANDATORY;
import static com.app.wingmate.utils.AppConstants.PARAM_ACCOUNT_STATUS;
import static com.app.wingmate.utils.AppConstants.PARAM_CURRENT_LOCATION;
import static com.app.wingmate.utils.AppConstants.PARAM_IS_ADMIN;
import static com.app.wingmate.utils.AppConstants.PARAM_IS_MEDIA_APPROVED;
import static com.app.wingmate.utils.AppConstants.PARAM_IS_PAID_USER;
import static com.app.wingmate.utils.AppConstants.PARAM_IS_PHOTO_SUBMITTED;
import static com.app.wingmate.utils.AppConstants.PARAM_IS_VIDEO_SUBMITTED;
import static com.app.wingmate.utils.AppConstants.PARAM_MANDATORY_QUESTIONNAIRE_FILLED;
import static com.app.wingmate.utils.AppConstants.PARAM_NICK;
import static com.app.wingmate.utils.AppConstants.PARAM_PROFILE_PIC;
import static com.app.wingmate.utils.AppConstants.PARAM_USER_ID;
import static com.app.wingmate.utils.AppConstants.PENDING;
import static com.app.wingmate.utils.AppConstants.REJECTED;
import static com.app.wingmate.utils.AppConstants.TRIAL_PERIOD;
import static com.app.wingmate.utils.AppConstants.UPDATE_INTERVAL_MINS;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_ACCOUNT_PENDING;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_PAYMENT;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_PRE_LOGIN;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_QUESTIONNAIRE;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_UPLOAD_PHOTO_VIDEO_PROFILE;
import static com.app.wingmate.utils.CommonKeys.PREF_LAST_UPDATE_TIME;

public class DashboardFragment extends BaseFragment implements BaseView, ViewPager.OnPageChangeListener, com.google.android.gms.location.LocationListener {

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

    @BindView(R.id.pending_view)
    RelativeLayout pendingView;

    private HomeFragment homeFragment;
    private SearchFragment searchFragment;
    private MyFansFragment likesFragment;
    private MessagesFragment messagesFragment;
    private SettingsFragment settingsFragment;

    private MyPagerAdapter viewPagerAdapter;

    public List<Question> questions = new ArrayList<>();
    public List<MyCustomUser> searchedUsers;
    public List<MyCustomUser> allUsers;
    public List<Fans> myFansList;
    public boolean homeProgress = false;
    public boolean searchProgress = false;
    public boolean myFansProgress = false;

    public double currentLocationLatitude = AppConstants.DEFAULT_LATITUDE;
    public double currentLocationLongitude = AppConstants.DEFAULT_LONGITUDE;

    private boolean isHomeView = true;

    public BasePresenter presenter;

    private LocationManager locationManager;

    private ExecutorService executor;
    private Handler handler;

    public boolean isExpired = false;
    public int remainingDays = TRIAL_PERIOD;

    public boolean isStart = true;

    public DashboardFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
        executor = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());
        EventBus.getDefault().register(this);

        isStart = true;

        updateParseInstallation();
    }

    private void updateParseInstallation() {
        if (ParseUser.getCurrentUser() != null) {
            ParseInstallation installation = ParseInstallation.getCurrentInstallation();
            installation.put(PARAM_USER_ID, ParseUser.getCurrentUser().getObjectId());
            if (ParseUser.getCurrentUser().getBoolean(PARAM_IS_ADMIN))
                installation.put(PARAM_IS_ADMIN, true);
            installation.saveInBackground();
        }
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

        pendingView.setVisibility(View.GONE);

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
//        ((MainActivity) getActivity()).setScreenTitle("Hi, " + ParseUser.getCurrentUser().getString(PARAM_NICK));
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
    }

    @Subscribe
    public void refreshUser(RefreshUserStatus refreshUserStatus) {
        performUserUpdateAction(false, true);
    }

    public void performUserUpdateAction(boolean showLoader, boolean isJustRefresh) {
        isStart = false;
        if (ParseUser.getCurrentUser() != null) {
            if (showLoader) showProgress();
            fetchUpdatedCurrentUser(showLoader, isJustRefresh);
//            long time1 = SharedPrefers.getLong(requireContext(), PREF_LAST_UPDATE_TIME, 0);
//            Date lastUpdateTime = new Date(time1);
//            Date currentTime = new Date();
//            if (DateUtils.greaterThanXMins(lastUpdateTime, currentTime, UPDATE_INTERVAL_MINS)) {
//                SharedPrefers.saveLong(requireContext(), PREF_LAST_UPDATE_TIME, new Date().getTime());
//                fetchUpdatedCurrentUser();
//            }
        }
    }

    private void fetchUpdatedCurrentUser(boolean showLoader, boolean isJustRefresh) {
        ParseUser.getCurrentUser().fetchInBackground((GetCallback<ParseUser>) (parseUser, e) -> {
            presenter.checkServerDate(getContext(), showLoader, isJustRefresh);
        });
    }

    @Override
    public void setHasTrial(int days, boolean showLoader, boolean isJustRefresh) {
        isExpired = false;
        remainingDays = days;

        int accountStatus = ParseUser.getCurrentUser().getInt(PARAM_ACCOUNT_STATUS);
        boolean isPaid = ParseUser.getCurrentUser().getBoolean(PARAM_IS_PAID_USER);
        boolean isPhotoSubmitted = ParseUser.getCurrentUser().getBoolean(PARAM_IS_PHOTO_SUBMITTED);
        boolean isVideoSubmitted = ParseUser.getCurrentUser().getBoolean(PARAM_IS_VIDEO_SUBMITTED);
        boolean isMandatoryQuestionnaireFilled = ParseUser.getCurrentUser().getBoolean(PARAM_MANDATORY_QUESTIONNAIRE_FILLED);

        if (showLoader) dismissProgress();

        if (accountStatus == REJECTED) {
            showRejectionPopupAndLogout();
        } else if (accountStatus == PENDING && (!isPhotoSubmitted || !isVideoSubmitted)) {
            homeFragment.setBannerTV("Kindly submit your photos/video");
            if (!isJustRefresh) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(requireContext());
                dialog.setCancelable(false)
                        .setMessage(GO_TO_UPLOAD_SCREEN)
                        .setPositiveButton("OK", (dialoginterface, i) -> {
                            dialoginterface.cancel();
                            ActivityUtility.startProfileMediaActivity(requireActivity(), KEY_FRAGMENT_UPLOAD_PHOTO_VIDEO_PROFILE, false, isExpired);
                        })
                        .show();
            }
        } else if (accountStatus == PENDING) {
            homeFragment.setBannerTV("Your profile is under screening process");
        } else if (!isPaid && accountStatus == ACTIVE) {
            String str = days + " days left for trial. <b>Buy Pro</b>";
            homeFragment.setBannerTV(Html.fromHtml(str).toString());
            if (!isJustRefresh) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(requireContext());
                dialog.setCancelable(false)
                        .setMessage(GO_TO_PAYMENT_SCREEN)
                        .setPositiveButton("OK", (dialoginterface, i) -> {
                            dialoginterface.cancel();
                            ActivityUtility.startPaymentActivity(getActivity(), KEY_FRAGMENT_PAYMENT, false);
                        })
                        .show();
            }
        } else if (isPaid && accountStatus == ACTIVE && !isMandatoryQuestionnaireFilled) {
            homeFragment.setBannerTV("Kindly fill-up your mandatory questionnaire");
            if (!isJustRefresh) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(requireContext());
                dialog.setCancelable(false)
                        .setMessage(GO_TO_QUESTIONNAIRE_SCREEN)
                        .setPositiveButton("OK", (dialoginterface, i) -> {
                            dialoginterface.cancel();
                            ActivityUtility.startQuestionnaireActivity(getActivity(), KEY_FRAGMENT_QUESTIONNAIRE, MANDATORY, false);
                        })
                        .show();
            }
        } else if (isPaid && accountStatus == ACTIVE) {
            homeFragment.hideBannerTV();
        }
    }

    @Override
    public void setTrialEnded(String msg, boolean showLoader, boolean isJustRefresh) {

        isExpired = true;
        remainingDays = 0;

        homeFragment.hideBannerTV();

        if (showLoader) dismissProgress();

        int accountStatus = ParseUser.getCurrentUser().getInt(PARAM_ACCOUNT_STATUS);
        boolean isPaid = ParseUser.getCurrentUser().getBoolean(PARAM_IS_PAID_USER);
        boolean isPhotoSubmitted = ParseUser.getCurrentUser().getBoolean(PARAM_IS_PHOTO_SUBMITTED);
        boolean isVideoSubmitted = ParseUser.getCurrentUser().getBoolean(PARAM_IS_VIDEO_SUBMITTED);
        boolean isMandatoryQuestionnaireFilled = ParseUser.getCurrentUser().getBoolean(PARAM_MANDATORY_QUESTIONNAIRE_FILLED);

        if (accountStatus == REJECTED) {
            showRejectionPopupAndLogout();
        } else if (accountStatus == PENDING && (!isPhotoSubmitted || !isVideoSubmitted)) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(requireContext());
            dialog.setCancelable(false)
                    .setMessage(GO_TO_UPLOAD_SCREEN)
                    .setPositiveButton("OK", (dialoginterface, i) -> {
                        dialoginterface.cancel();
                        ActivityUtility.startProfileMediaActivity(requireActivity(), KEY_FRAGMENT_UPLOAD_PHOTO_VIDEO_PROFILE, true, isExpired);
                    })
                    .show();
        } else if (accountStatus == PENDING) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(requireContext());
            dialog.setCancelable(false)
                    .setMessage(GO_TO_ACC_PENDING_SCREEN)
                    .setPositiveButton("OK", (dialoginterface, i) -> {
                        dialoginterface.cancel();
                        ActivityUtility.startActivity(requireActivity(), KEY_FRAGMENT_ACCOUNT_PENDING);
                    })
                    .show();
//            pendingView.setVisibility(View.VISIBLE);
        } else if (!isPaid && accountStatus == ACTIVE) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(requireContext());
            dialog.setCancelable(false)
                    .setMessage(GO_TO_PAYMENT_SCREEN)
                    .setPositiveButton("OK", (dialoginterface, i) -> {
                        dialoginterface.cancel();
                        ActivityUtility.startPaymentActivity(getActivity(), KEY_FRAGMENT_PAYMENT, true);
                    })
                    .show();
        } else if (isPaid && accountStatus == ACTIVE && !isMandatoryQuestionnaireFilled) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(requireContext());
            dialog.setCancelable(false)
                    .setMessage(GO_TO_QUESTIONNAIRE_SCREEN)
                    .setPositiveButton("OK", (dialoginterface, i) -> {
                        dialoginterface.cancel();
                        ActivityUtility.startQuestionnaireActivity(getActivity(), KEY_FRAGMENT_QUESTIONNAIRE, MANDATORY, true);
                    })
                    .show();
        }
    }

    private void showRejectionPopupAndLogout() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(requireContext());
        dialog
//                .setTitle(getString(R.string.app_name))
//                .setIcon(R.drawable.app_heart)
                .setCancelable(false)
                .setMessage("Your profile has been rejected by the admin!")
                .setNegativeButton("OK", (dialoginterface, i) -> {
                    dialoginterface.cancel();
                    SharedPrefers.saveLong(requireContext(), PREF_LAST_UPDATE_TIME, 0);
                    ParseUser.logOut();
                    ActivityUtility.startActivity(requireActivity(), KEY_FRAGMENT_PRE_LOGIN);
                })
                .show();
    }

    @Subscribe
    public void refreshDashboardViews(RefreshDashboardView refreshDashboardView) {
        isExpired = true;
        remainingDays = 0;

        int accountStatus = ParseUser.getCurrentUser().getInt(PARAM_ACCOUNT_STATUS);
        boolean isPaid = ParseUser.getCurrentUser().getBoolean(PARAM_IS_PAID_USER);
        boolean isPhotoSubmitted = ParseUser.getCurrentUser().getBoolean(PARAM_IS_PHOTO_SUBMITTED);
        boolean isVideoSubmitted = ParseUser.getCurrentUser().getBoolean(PARAM_IS_VIDEO_SUBMITTED);
        boolean isMandatoryQuestionnaireFilled = ParseUser.getCurrentUser().getBoolean(PARAM_MANDATORY_QUESTIONNAIRE_FILLED);

        homeFragment.hideBannerTV();

        if (accountStatus == REJECTED) {
            showRejectionPopupAndLogout();
        } else if (accountStatus == PENDING && (!isPhotoSubmitted || !isVideoSubmitted)) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(requireContext());
            dialog.setCancelable(false)
                    .setMessage(GO_TO_UPLOAD_SCREEN)
                    .setPositiveButton("OK", (dialoginterface, i) -> {
                        dialoginterface.cancel();
                        ActivityUtility.startProfileMediaActivity(requireActivity(), KEY_FRAGMENT_UPLOAD_PHOTO_VIDEO_PROFILE, true, isExpired);
                    })
                    .show();
        } else if (accountStatus == PENDING) {
//            pendingView.setVisibility(View.VISIBLE);
            AlertDialog.Builder dialog = new AlertDialog.Builder(requireContext());
            dialog.setCancelable(false)
                    .setMessage(GO_TO_ACC_PENDING_SCREEN)
                    .setPositiveButton("OK", (dialoginterface, i) -> {
                        dialoginterface.cancel();
                        ActivityUtility.startActivity(requireActivity(), KEY_FRAGMENT_ACCOUNT_PENDING);
                    })
                    .show();
        } else if (!isPaid && accountStatus == ACTIVE) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(requireContext());
            dialog.setCancelable(false)
                    .setMessage(GO_TO_PAYMENT_SCREEN)
                    .setPositiveButton("OK", (dialoginterface, i) -> {
                        dialoginterface.cancel();
                        ActivityUtility.startPaymentActivity(getActivity(), KEY_FRAGMENT_PAYMENT, true);
                    })
                    .show();
        } else if (isPaid && accountStatus == ACTIVE && !isMandatoryQuestionnaireFilled) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(requireContext());
            dialog.setCancelable(false)
                    .setMessage(GO_TO_QUESTIONNAIRE_SCREEN)
                    .setPositiveButton("OK", (dialoginterface, i) -> {
                        dialoginterface.cancel();
                        ActivityUtility.startQuestionnaireActivity(getActivity(), KEY_FRAGMENT_QUESTIONNAIRE, MANDATORY, true);
                    })
                    .show();
        }
    }

    private void checkPaidUser() {
        if (!ParseUser.getCurrentUser().getBoolean(PARAM_IS_PAID_USER)) {
            presenter.checkServerDate(getContext(), false, true);
        }
    }

    public void saveCurrentGeoPoint() {
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
//            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0f, location -> {
//                    System.out.println("GPS_PROVIDER_LocationUpdates:: " + location.getLatitude() + ", " + location.getLongitude());
//                    ParseGeoPoint geoPoint = new ParseGeoPoint(location.getLatitude(), location.getLongitude());
//                    ParseUser.getCurrentUser().put(PARAM_CURRENT_LOCATION, geoPoint);
//                    ParseUser.getCurrentUser().saveInBackground(e -> {
//                        handler.post(() -> {
//                            homeProgress = false;
//                            EventBus.getDefault().post(new RefreshHome());
//                        });
//                    });
//                });
//            } else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
//                System.out.println("====hereee4");
//
//                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 0f, location -> {
//                    System.out.println("NETWORK_PROVIDER_LocationUpdates:: " + location.getLatitude() + ", " + location.getLongitude());
//                    ParseGeoPoint geoPoint = new ParseGeoPoint(location.getLatitude(), location.getLongitude());
//                    ParseUser.getCurrentUser().put(PARAM_CURRENT_LOCATION, geoPoint);
//                    ParseUser.getCurrentUser().saveInBackground(e -> {
//                        handler.post(() -> {
//                            homeProgress = false;
//                            EventBus.getDefault().post(new RefreshHome());
//                        });
//                    });
//                });
//            }


            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                System.out.println("===GPS_PROVIDER==" + locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER));
                System.out.println("===NETWORK_PROVIDER==" + locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER));
                if (getLastBestLocation() != null) {
                    ParseGeoPoint geoPoint = new ParseGeoPoint(getLastBestLocation().getLatitude(), getLastBestLocation().getLongitude());
                    ParseUser.getCurrentUser().put(PARAM_CURRENT_LOCATION, geoPoint);
                    ParseUser.getCurrentUser().saveInBackground(e -> {
                        handler.post(() -> {
//                            homeProgress = false;
                            EventBus.getDefault().post(new RefreshHomeWithNewLocation());
                        });
                    });
                    System.out.println("getLastBestLocation:: " + geoPoint.getLatitude() + ", " + geoPoint.getLongitude());
                }
            } else {
                System.out.println("===GPS Connection Error!==");
                AlertDialog.Builder dialog = new AlertDialog.Builder(requireContext());
                dialog.setTitle("GPS Connection Error!")
                        .setIcon(R.drawable.location_icon)
                        .setCancelable(false)
                        .setMessage("GPS is not enabled on your device.")
//                        .setNegativeButton("No", (dialogInterface, i) -> {
//                            dialogInterface.cancel();
//                        })
                        .setPositiveButton("Enable GPS", (dialogInterface, i) -> {
                            dialogInterface.cancel();
                            dialogInterface.dismiss();
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(intent, 1234);
                        }).show();
            }
        }
//        if (getLastBestLocation() != null) {
//            ParseGeoPoint geoPoint = new ParseGeoPoint(getLastBestLocation().getLatitude(), getLastBestLocation().getLongitude());
//            ParseUser.getCurrentUser().put(PARAM_CURRENT_LOCATION, geoPoint);
//            ParseUser.getCurrentUser().saveInBackground(e -> {
//            });
//        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        saveCurrentGeoPoint();
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

    @OnClick({R.id.btn_logout, R.id.btn_home, R.id.btn_search, R.id.btn_fan, R.id.btn_msg, R.id.btn_settings})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.btn_logout:
                ParseUser.logOut();
                ActivityUtility.startActivity(requireActivity(), KEY_FRAGMENT_PRE_LOGIN);
                break;
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
        if (this.questions == null) this.questions = new ArrayList<>();
        EventBus.getDefault().post(new RefreshSearch());
    }

    public void searchSpecificQuestion(List<QuestionOption> optionsObjArray) {
        showProgress();
        searchedUsers = new ArrayList<>();
        presenter.getSpecificQuestionUserAnswers(getContext(), optionsObjArray);
    }

    public void searchUsersWithInKM(ParseGeoPoint myGeoPoint, double distance) {
        showProgress();
        searchedUsers = new ArrayList<>();
        presenter.getUsersWithInKM(getContext(), myGeoPoint, distance);
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
    public void refreshDashboardUsersList(RefreshDashboard refreshDashboard) {
        homeProgress = true;
        allUsers = new ArrayList<>();
        presenter.queryAllUsers(getContext());
    }

    @Subscribe
    public void refreshFansList(RefreshFanList refreshFanList) {
        myFansProgress = true;
        myFansList = new ArrayList<>();
        presenter.queryAllMyFans(getContext());
    }
}