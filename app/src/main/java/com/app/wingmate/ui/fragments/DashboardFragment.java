package com.app.wingmate.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.app.wingmate.R;
import com.app.wingmate.base.BaseFragment;
import com.app.wingmate.ui.activities.MainActivity;
import com.app.wingmate.utils.DateUtils;
import com.app.wingmate.widgets.NonSwappableViewPager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseUser;

import java.text.ParseException;
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
import static com.app.wingmate.utils.AppConstants.PARAM_IS_PAID_USER;
import static com.app.wingmate.utils.AppConstants.PARAM_NICK;
import static com.app.wingmate.utils.AppConstants.SUCCESS;
import static com.app.wingmate.utils.AppConstants.TRIAL_PERIOD;
import static com.app.wingmate.utils.Utilities.showToast;

public class DashboardFragment extends BaseFragment implements ViewPager.OnPageChangeListener {

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

    private String serverDate = "";

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

        initViews();
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
        if (!ParseUser.getCurrentUser().getBoolean(PARAM_IS_PAID_USER))
            checkServerDate();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({})
    public void onViewClicked(View v) {

    }

    private void checkServerDate() {
        HashMap<String, Object> params = new HashMap<>();
        ParseCloud.callFunctionInBackground(PARSE_CLOUD_FUNCTION_GET_SERVER_TIME, params, (FunctionCallback<String>) (result, e) -> {
            if (e == null) {
                serverDate = result;
                SimpleDateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                try {
                    Date SERVER_DATE = isoDateFormat.parse(serverDate);
                    if (DateUtils.daysBetween(ParseUser.getCurrentUser().getCreatedAt(), SERVER_DATE) >= TRIAL_PERIOD) {
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
                } catch (ParseException parseException) {
                    parseException.printStackTrace();
                }
            }
        });
    }
}