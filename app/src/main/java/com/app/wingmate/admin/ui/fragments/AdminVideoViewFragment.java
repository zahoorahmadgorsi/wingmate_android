package com.app.wingmate.admin.ui.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.app.wingmate.R;
import com.app.wingmate.base.BaseFragment;
import com.app.wingmate.admin.events.AdminRefreshDashboard;
import com.app.wingmate.admin.events.AdminRefreshProfile;
import com.app.wingmate.admin.models.RejectionReason;
import com.app.wingmate.models.UserProfilePhotoVideo;
import com.app.wingmate.admin.ui.dialogs.RejectionReasonsDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.potyvideo.library.AndExoPlayerView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.app.wingmate.utils.APIsUtility.PARSE_CLOUD_FUNCTION_EMAIL_TO_USER;
import static com.app.wingmate.utils.APIsUtility.PARSE_CLOUD_FUNCTION_UPDATE_USER_VIDEO_STATUS;
import static com.app.wingmate.utils.AppConstants.ACTIVE;
import static com.app.wingmate.utils.AppConstants.ERROR;
import static com.app.wingmate.utils.AppConstants.PARAM_FILE_STATUS;
import static com.app.wingmate.utils.AppConstants.PARAM_REJECT_COMMENT;
import static com.app.wingmate.utils.AppConstants.PARAM_REJECT_REASON;
import static com.app.wingmate.utils.AppConstants.PARAM_USER_ID;
import static com.app.wingmate.utils.AppConstants.PENDING;
import static com.app.wingmate.utils.AppConstants.REJECT;
import static com.app.wingmate.utils.AppConstants.SUCCESS;
import static com.app.wingmate.utils.AppConstants.TYPE_MEDIA;
import static com.app.wingmate.utils.CommonKeys.KEY_REASONS_ARRAY;
import static com.app.wingmate.utils.CommonKeys.KEY_USER_EMAIL;
import static com.app.wingmate.utils.CommonKeys.KEY_VIDEO_LINK;
import static com.app.wingmate.utils.Utilities.showToast;

public class AdminVideoViewFragment extends BaseFragment implements RejectionReasonsDialog.RejectionReasonsDialogClickListener {

    public static final String TAG = AdminVideoViewFragment.class.getName();

    Unbinder unbinder;

    @BindView(R.id.video_view)
    AndExoPlayerView videoView;
    @BindView(R.id.img_placeholder)
    ImageView videoImgPlaceholder;
    @BindView(R.id.play_btn)
    ImageView playBtn;
    @BindView(R.id.btn_reject)
    Button btnReject;
    @BindView(R.id.btn_approve)
    Button btnApprove;

    boolean isChange = false;

    private String videoUrl = "";
    private UserProfilePhotoVideo userProfilePhotoVideo;

    private List<RejectionReason> rejectionReasons = new ArrayList<>();

    private String userEmailId = "";

    private int VIDEO_STATUS = PENDING;
    private String REASON = "";
    private String COMMENT = "";

    public AdminVideoViewFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_view_admin, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userProfilePhotoVideo = getActivity().getIntent().getParcelableExtra(KEY_VIDEO_LINK);
        rejectionReasons = requireActivity().getIntent().getParcelableArrayListExtra(KEY_REASONS_ARRAY);
        userEmailId = requireActivity().getIntent().getExtras().getString(KEY_USER_EMAIL, "");
        videoUrl = userProfilePhotoVideo.getFile().getUrl();
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

        if (userProfilePhotoVideo != null) {
            if (userProfilePhotoVideo.getInt(PARAM_FILE_STATUS) == REJECT) {
                enableApproveBtn();
                disableRejectBtn();
            } else if (userProfilePhotoVideo.getInt(PARAM_FILE_STATUS) == ACTIVE) {
                disableApproveBtn();
                enableRejectBtn();
            } else {
                enableApproveBtn();
                enableRejectBtn();
            }
        } else {
            disableApproveBtn();
            disableRejectBtn();
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

    private void setAndSendEmailToUser() {
        showToast(requireActivity(), requireContext(), "Processing...", ERROR);

//        showProgress();
        String subject = "Profile Media Status Updated";
        String body = "";

//        if (VIDEO_STATUS == ACTIVE) {
//            body = "Congrats, your video has been approved by admin.";
//            body = body + "<br><br>" + userProfilePhotoVideo.getFile().getUrl();
//        }

        if (VIDEO_STATUS == REJECT) {
            body = body + "<br><br>" + "Unfortunately, your video has been rejected by admin. Please update your profile accordingly.";
            body = body + "<br><br>" + userProfilePhotoVideo.getFile().getUrl();
            body = body + "<br><br>" + "Reason: " + REASON;
            if (!COMMENT.isEmpty()) body = body + "<br><br>" + "Comment: " + COMMENT;

//            HashMap<String, Object> params = new HashMap<String, Object>();
//            params.put(PARAM_USER_ID, userProfilePhotoVideo.getUserId());
//            ParseCloud.callFunctionInBackground(PARSE_CLOUD_FUNCTION_UPDATE_USER_VIDEO_STATUS, params, (FunctionCallback<String>) (object, e) -> {
//                if (e == null) {
//
//                } else {
//
//                }
//            });

            body = body + "<br><br><br>" + "Note: If you will delete this photo/video from the application then this link will not be accessible again.";

            final HashMap<String, String> params2 = new HashMap<>();
            params2.put("emailId", userEmailId);
            params2.put("subject", subject);
            params2.put("body", body);
            ParseCloud.callFunctionInBackground(PARSE_CLOUD_FUNCTION_EMAIL_TO_USER, params2, (response, exc) -> {
                if (exc != null) {
                    showToast(requireActivity(), requireContext(), exc.getMessage(), ERROR);
                } else {
                    showToast(requireActivity(), requireContext(), "Email has been sent to user.", ERROR);
                    EventBus.getDefault().post(new AdminRefreshDashboard());
                    EventBus.getDefault().post(new AdminRefreshProfile());
                    requireActivity().finish();
                }
//            dismissProgress();
            });
        }
    }

    public void backBtnPress() {
        if (isChange) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
//            builder.setTitle("Confirmation");
//            builder.setMessage("Are you done with the approval/rejection of video?");
//            builder.setPositiveButton("Yes", (dialog, which) -> {
//                dialog.cancel();
//                if (VIDEO_STATUS == REJECT) {
//                    setAndSendEmailToUser();
//                } else {
//                    EventBus.getDefault().post(new AdminRefreshDashboard());
//                    EventBus.getDefault().post(new AdminRefreshProfile());
//                    requireActivity().finish();
//                }
//            });
//            builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());
//            builder.show();
            if (VIDEO_STATUS == REJECT) {
                setAndSendEmailToUser();
            } else {
                EventBus.getDefault().post(new AdminRefreshDashboard());
                EventBus.getDefault().post(new AdminRefreshProfile());
                requireActivity().finish();
            }
        } else {
            requireActivity().finish();
        }
    }

    @OnClick({R.id.btn_back, R.id.btn_cross, R.id.btn_reject, R.id.btn_approve})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
//                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
//                builder.setTitle("Confirmation");
//                builder.setMessage("Are you done with approval/rejection of video? Please note that rejection of video can't be undone.");
//                builder.setPositiveButton("Yes", (dialog, which) -> {
//                    dialog.cancel();
//                    EventBus.getDefault().post(new AdminRefreshDashboard());
//                    EventBus.getDefault().post(new AdminRefreshProfile());
//                    requireActivity().onBackPressed();
//                });
//                builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());
//                builder.show();
                break;
            case R.id.btn_cross:
                backBtnPress();
//                if (isChange) {
//                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
//                    builder.setTitle("Confirmation");
//                    builder.setMessage("Are you done with approval/rejection of video? Please note that rejection of video can't be undone.");
//                    builder.setPositiveButton("Yes", (dialog, which) -> {
//                        dialog.cancel();
//                        setAndSendEmailToUser();
//                    });
//                    builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());
//                    builder.show();
//                } else {
//                    requireActivity().onBackPressed();
//                }
                break;
            case R.id.btn_reject:
                showRejectionReasonsDialog();
                break;
            case R.id.btn_approve:
                showProgress();
                isChange = true;
                userProfilePhotoVideo.put(PARAM_FILE_STATUS, 1);
                userProfilePhotoVideo.saveInBackground(e -> {
                    dismissProgress();
                    if (e == null) {
                        showToast(getActivity(), getContext(), "Updated successfully.", SUCCESS);
                        String msg = "Your video has been approved by the admin.";
                        setPushToUser(requireActivity(), requireContext(), userProfilePhotoVideo.getUserId(), "Media Approved", msg);
                        VIDEO_STATUS = ACTIVE;
//                        EventBus.getDefault().post(new AdminRefreshDashboard());
//                        EventBus.getDefault().post(new AdminRefreshProfile());
                    } else {
                        showToast(getActivity(), getContext(), e.getMessage(), ERROR);
                    }
                });
                break;
            default:
                break;
        }
    }

    public void showRejectionReasonsDialog() {
        List<RejectionReason> list = new ArrayList<>();
        for (int i = 0; i < rejectionReasons.size(); i++) {
            if (rejectionReasons.get(i).getType().equals(TYPE_MEDIA))
                list.add(rejectionReasons.get(i));
        }
        RejectionReasonsDialog dialog = RejectionReasonsDialog.newInstance(this, getActivity(), list, TYPE_MEDIA);
        dialog.show(getActivity().getSupportFragmentManager(), "rejection_dialog");
    }

    @Override
    public void onRejectionSelectorBackButtonClick(DialogFragment dialog) {
        dialog.dismiss();
    }

    @Override
    public void onRejectionSelectorSaveClick(DialogFragment dialog, String reason, String comment) {
        dialog.dismiss();
        showProgress();
        isChange = true;
        userProfilePhotoVideo.put(PARAM_FILE_STATUS, 2);
        userProfilePhotoVideo.put(PARAM_REJECT_REASON, reason);
        userProfilePhotoVideo.put(PARAM_REJECT_COMMENT, comment);
        userProfilePhotoVideo.saveInBackground(e -> {
            dismissProgress();
            if (e == null) {
                showToast(getActivity(), getContext(), "Updated successfully.", SUCCESS);
//                String msg = "Your video has been rejected by the admin due to " + reason;
                String msg = "Your video has been rejected by the admin. Please check your email for details.";
                setPushToUser(requireActivity(), requireContext(), userProfilePhotoVideo.getUserId(), "Media Rejected", msg);
                VIDEO_STATUS = REJECT;
                REASON = reason;
                COMMENT = comment;
//                EventBus.getDefault().post(new AdminRefreshDashboard());
//                EventBus.getDefault().post(new AdminRefreshProfile());
            } else {
                showToast(getActivity(), getContext(), e.getMessage(), ERROR);
            }
        });
    }

    private void enableApproveBtn() {
        btnApprove.setClickable(true);
        btnApprove.setEnabled(true);
        btnApprove.setSelected(true);
        btnApprove.setActivated(true);
        btnApprove.setFocusable(true);
        btnApprove.setAlpha(1.0f);
    }

    private void disableApproveBtn() {
        btnApprove.setClickable(false);
        btnApprove.setEnabled(false);
        btnApprove.setSelected(false);
        btnApprove.setActivated(false);
        btnApprove.setFocusable(false);
        btnApprove.setAlpha(0.5f);
    }

    private void enableRejectBtn() {
        btnReject.setClickable(true);
        btnReject.setEnabled(true);
        btnReject.setSelected(true);
        btnReject.setActivated(true);
        btnReject.setFocusable(true);
        btnReject.setAlpha(1.0f);
    }

    private void disableRejectBtn() {
        btnReject.setClickable(false);
        btnReject.setEnabled(false);
        btnReject.setSelected(false);
        btnReject.setActivated(false);
        btnReject.setFocusable(false);
        btnReject.setAlpha(0.5f);
    }
}