package com.app.wingmate.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.app.wingmate.R;
import com.app.wingmate.base.BaseFragment;
import com.jsibbold.zoomage.ZoomageView;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.app.wingmate.utils.AppConstants.ERROR;
import static com.app.wingmate.utils.CommonKeys.KEY_IMAGE_LINK;
import static com.app.wingmate.utils.Utilities.showToast;

public class PhotoViewFragment extends BaseFragment {

    public static final String TAG = PhotoViewFragment.class.getName();

    Unbinder unbinder;

    @BindView(R.id.zoom_image_view)
    ZoomageView zoomageView;

    String imagePath = "";

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

        imagePath = getActivity().getIntent().getStringExtra(KEY_IMAGE_LINK);
        if (imagePath != null && imagePath.length() > 0) {
            Picasso.get().load(imagePath).placeholder(R.drawable.image_placeholder).into(zoomageView);
        } else {
            showToast(getActivity(), getContext(), "Unable to load photo!", ERROR);
        }

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
}
