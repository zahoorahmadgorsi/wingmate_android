package com.app.wingmate.ui.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.app.wingmate.R;
import com.app.wingmate.base.BaseFragment;
import com.app.wingmate.widgets.FadePageTransformer;
import com.jsibbold.zoomage.ZoomageView;
import com.rd.PageIndicatorView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.app.wingmate.utils.AppConstants.ERROR;
import static com.app.wingmate.utils.CommonKeys.KEY_IMAGES_ARRAY;
import static com.app.wingmate.utils.CommonKeys.KEY_IMAGE_INDEX;
import static com.app.wingmate.utils.CommonKeys.KEY_IMAGE_LINK;
import static com.app.wingmate.utils.Utilities.showToast;

public class PhotoViewFragment extends BaseFragment implements ViewPager.OnPageChangeListener {

    public static final String TAG = PhotoViewFragment.class.getName();

    Unbinder unbinder;

    @BindView(R.id.zoom_image_view)
    ZoomageView zoomageView;

    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.pageIndicatorView)
    PageIndicatorView indicator;

    String imagePath = "";
    int index = 0;

    private List<String> imagesPath = new ArrayList<>();

    public PhotoViewFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo_view, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imagesPath = requireActivity().getIntent().getStringArrayListExtra(KEY_IMAGES_ARRAY);
        index = requireActivity().getIntent().getExtras().getInt(KEY_IMAGE_INDEX);

        viewPager.setAdapter(new CustomAdapter());
        viewPager.setOffscreenPageLimit(imagesPath.size());
//        viewPager.setPageTransformer(false, new FadePageTransformer());
        viewPager.addOnPageChangeListener(this);
        indicator.setViewPager(viewPager);
        viewPager.setCurrentItem(index);

//        imagePath = getActivity().getIntent().getStringExtra(KEY_IMAGE_LINK);
//        if (imagePath != null && imagePath.length() > 0) {
//            Picasso.get().load(imagePath).placeholder(R.drawable.image_placeholder).into(zoomageView);
//        } else {
//            showToast(getActivity(), getContext(), "Unable to load photo!", ERROR);
//        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.btn_cross})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.btn_cross:
                requireActivity().onBackPressed();
                break;
            default:
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public class CustomAdapter extends PagerAdapter {
        public CustomAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            // TODO Auto-generated method stub
            ZoomageView zoomageView1 = new ZoomageView(container.getContext());
            zoomageView1.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            container.addView(zoomageView1);
            imagePath = imagesPath.get(position);
            if (imagePath != null && imagePath.length() > 0) {
                Picasso.get().load(imagePath).placeholder(R.drawable.image_placeholder).into(zoomageView1);
            } else {
                showToast(getActivity(), getContext(), "Unable to load photo!", ERROR);
            }
            return zoomageView1;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return imagesPath.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            // TODO Auto-generated method stub
            return view == ((View) object);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            // TODO Auto-generated method stub
            ((ViewPager) container).removeView((View) object);
        }


    }
}
