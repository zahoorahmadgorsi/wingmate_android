package com.app.wingmate.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.app.wingmate.R;
import com.app.wingmate.base.BaseFragment;
import com.app.wingmate.dashboard.home.HomeFragment;
import com.app.wingmate.dashboard.search.SearchFragment;
import com.app.wingmate.models.Question;
import com.app.wingmate.ui.activities.MainActivity;
import com.app.wingmate.ui.fragments.DummyFragment;
import com.app.wingmate.widgets.NonSwappableViewPager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.app.wingmate.utils.AppConstants.PARAM_NICK;

public class DashboardFragment extends BaseFragment implements ViewPager.OnPageChangeListener {

    public static final String TAG = DashboardFragment.class.getName();

    Unbinder unbinder;

    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigationView;
    @BindView(R.id.view_pager)
    public NonSwappableViewPager viewPager;

    private HomeFragment homeFragment;
    private DummyFragment searchFragment;
    private DummyFragment likesFragment;
    private DummyFragment messagesFragment;
    private DummyFragment settingsFragment;

    private MyPagerAdapter viewPagerAdapter;

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
                        return true;
                    case R.id.navigation_search:
                        viewPager.setCurrentItem(1, true);
                        return true;
                    case R.id.navigation_likes:
                        viewPager.setCurrentItem(2, true);
                        return true;
                    case R.id.navigation_messages:
                        viewPager.setCurrentItem(3, true);
                        return true;
                    case R.id.navigation_settings:
                        viewPager.setCurrentItem(4, true);
                        return true;
                }
                return false;
            };

    private void initViews() {

        homeFragment = HomeFragment.newInstance(this);
        searchFragment = DummyFragment.newInstance(this);
        likesFragment = DummyFragment.newInstance(this);
        messagesFragment = DummyFragment.newInstance(this);
        settingsFragment = DummyFragment.newInstance(this);

        viewPagerAdapter = new MyPagerAdapter(requireActivity().getSupportFragmentManager());
        viewPagerAdapter.addFragment(homeFragment);
//        viewPagerAdapter.addFragment(searchFragment);
//        viewPagerAdapter.addFragment(likesFragment);
//        viewPagerAdapter.addFragment(messagesFragment);
//        viewPagerAdapter.addFragment(settingsFragment);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setPagingEnabled(false);
//        viewPager.setOffscreenPageLimit(4);
        viewPager.setCurrentItem(0, true);
        viewPager.addOnPageChangeListener(this);

        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        bottomNavigationView.setVisibility(View.INVISIBLE);

        ((MainActivity) getActivity()).setScreenTitle("Hi, " + ParseUser.getCurrentUser().getString(PARAM_NICK));
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
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({})
    public void onViewClicked(View v) {

    }
}