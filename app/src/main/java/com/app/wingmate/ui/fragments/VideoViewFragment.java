package com.app.wingmate.ui.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.app.wingmate.R;
import com.app.wingmate.base.BaseFragment;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.potyvideo.library.AndExoPlayerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.app.wingmate.utils.AppConstants.ERROR;
import static com.app.wingmate.utils.AppConstants.PARAM_VIDEO_LINK;
import static com.app.wingmate.utils.CommonKeys.KEY_VIDEO_LINK;
import static com.app.wingmate.utils.Utilities.showToast;

public class VideoViewFragment extends BaseFragment {

    public static final String TAG = VideoViewFragment.class.getName();

    Unbinder unbinder;

    @BindView(R.id.video_view)
    AndExoPlayerView videoView;
    @BindView(R.id.img_placeholder)
    ImageView videoImgPlaceholder;
    @BindView(R.id.play_btn)
    ImageView playBtn;

    private String videoUrl = "";

    public VideoViewFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_view, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        videoUrl = getActivity().getIntent().getStringExtra(KEY_VIDEO_LINK);
        setupVideoView();
    }

    private void setupVideoView() {
        RequestOptions requestOptions = new RequestOptions();
        Glide.with(getContext())
                .load(videoUrl)
                .apply(requestOptions)
                .thumbnail(Glide.with(getContext()).load(videoUrl))
                .placeholder(R.drawable.video_loading)
                .into(videoImgPlaceholder);
        if (videoUrl != null && !TextUtils.isEmpty(videoUrl)) {
            videoImgPlaceholder.setVisibility(View.GONE);
            playBtn.setVisibility(View.GONE);
            videoView.setVisibility(View.VISIBLE);
            videoView.setSource(videoUrl);
        } else {
            showToast(getActivity(), getContext(), "Unable to load video!", ERROR);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (videoView != null) {
            videoView.pausePlayer();
            videoView.stopPlayer();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.btn_back})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                getActivity().onBackPressed();
                break;
            default:
                break;
        }
    }
}