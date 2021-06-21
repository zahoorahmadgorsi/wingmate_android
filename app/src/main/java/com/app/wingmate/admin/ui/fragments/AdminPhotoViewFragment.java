package com.app.wingmate.admin.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.app.wingmate.R;
import com.app.wingmate.base.BaseFragment;
import com.app.wingmate.admin.events.AdminRefreshDashboard;
import com.app.wingmate.admin.events.AdminRefreshProfile;
import com.app.wingmate.admin.models.RejectionReason;
import com.app.wingmate.models.UserProfilePhotoVideo;
import com.app.wingmate.admin.ui.dialogs.RejectionReasonsDialog;
import com.jsibbold.zoomage.ZoomageView;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.rd.PageIndicatorView;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.app.wingmate.utils.APIsUtility.PARSE_CLOUD_FUNCTION_EMAIL_TO_USER;
import static com.app.wingmate.utils.APIsUtility.PARSE_CLOUD_FUNCTION_UPDATE_USER_PHOTOS_STATUS;
import static com.app.wingmate.utils.APIsUtility.PARSE_CLOUD_FUNCTION_UPDATE_USER_PIC;
import static com.app.wingmate.utils.AppConstants.ACTIVE;
import static com.app.wingmate.utils.AppConstants.ERROR;
import static com.app.wingmate.utils.AppConstants.PARAM_FILE_STATUS;
import static com.app.wingmate.utils.AppConstants.PARAM_IS_PHOTO_SUBMITTED;
import static com.app.wingmate.utils.AppConstants.PARAM_REJECT_COMMENT;
import static com.app.wingmate.utils.AppConstants.PARAM_REJECT_REASON;
import static com.app.wingmate.utils.AppConstants.PARAM_USER_ID;
import static com.app.wingmate.utils.AppConstants.PENDING;
import static com.app.wingmate.utils.AppConstants.REJECT;
import static com.app.wingmate.utils.AppConstants.SUCCESS;
import static com.app.wingmate.utils.AppConstants.TYPE_MEDIA;
import static com.app.wingmate.utils.CommonKeys.KEY_IMAGES_ARRAY;
import static com.app.wingmate.utils.CommonKeys.KEY_IMAGE_INDEX;
import static com.app.wingmate.utils.CommonKeys.KEY_PROFILE_PIC;
import static com.app.wingmate.utils.CommonKeys.KEY_REASONS_ARRAY;
import static com.app.wingmate.utils.CommonKeys.KEY_USER_EMAIL;
import static com.app.wingmate.utils.Utilities.showToast;

public class AdminPhotoViewFragment extends BaseFragment implements ViewPager.OnPageChangeListener, RejectionReasonsDialog.RejectionReasonsDialogClickListener {

    public static final String TAG = AdminPhotoViewFragment.class.getName();

    Unbinder unbinder;

    @BindView(R.id.zoom_image_view)
    ZoomageView zoomageView;

    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.pageIndicatorView)
    PageIndicatorView indicator;
    @BindView(R.id.btn_reject)
    Button btnReject;
    @BindView(R.id.btn_approve)
    Button btnApprove;

    boolean isChange = false;

    String imagePath = "";
    int index = 0;

    private int IMAGE_1 = PENDING;
    private int IMAGE_2 = PENDING;
    private int IMAGE_3 = PENDING;

    private String REASON_1 = "";
    private String COMMENT_1 = "";
    private String REASON_2 = "";
    private String COMMENT_2 = "";
    private String REASON_3 = "";
    private String COMMENT_3 = "";

    private String userEmailId = "";
    private String userProfilePic = "";

    private List<UserProfilePhotoVideo> imagesPath = new ArrayList<>();

    private List<RejectionReason> rejectionReasons = new ArrayList<>();

    public AdminPhotoViewFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo_view_admin, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imagesPath = requireActivity().getIntent().getParcelableArrayListExtra(KEY_IMAGES_ARRAY);
        rejectionReasons = requireActivity().getIntent().getParcelableArrayListExtra(KEY_REASONS_ARRAY);
        index = requireActivity().getIntent().getExtras().getInt(KEY_IMAGE_INDEX);
        userEmailId = requireActivity().getIntent().getExtras().getString(KEY_USER_EMAIL, "");
        userProfilePic = requireActivity().getIntent().getExtras().getString(KEY_PROFILE_PIC, "");

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

    private void setAndSendEmailToUser() {
        showToast(requireActivity(), requireContext(), "Processing...", ERROR);

//        showProgress();
        String subject = "Profile Media Status Updated";
        String body = "";

//        if (IMAGE_1 == ACTIVE || IMAGE_2 == ACTIVE || IMAGE_3 == ACTIVE) {
//            body = "Congrats, your below images have been approved by the admin.";
//            if (IMAGE_1 == ACTIVE)
//                body = body + "<br><br>" + imagesPath.get(0).getFile().getUrl();
//            if (IMAGE_2 == ACTIVE)
//                body = body + "<br><br>" + imagesPath.get(1).getFile().getUrl();
//            if (IMAGE_3 == ACTIVE)
//                body = body + "<br><br>" + imagesPath.get(2).getFile().getUrl();
//        }

        boolean allRejected = true;
        if (imagesPath != null && imagesPath.size() > 0) {
            for (int i = 0; i < imagesPath.size(); i++) {
                if (imagesPath.get(i).getInt(PARAM_FILE_STATUS) != REJECT) {
                    allRejected = false;
                }
            }
        }
        if (allRejected) {
//            HashMap<String, Object> params = new HashMap<String, Object>();
//            params.put(PARAM_USER_ID, imagesPath.get(0).getUserId());
//            ParseCloud.callFunctionInBackground(PARSE_CLOUD_FUNCTION_UPDATE_USER_PHOTOS_STATUS, params, (FunctionCallback<String>) (object, e) -> {
//                if (e == null) {
//                    EventBus.getDefault().post(new AdminRefreshDashboard());
//                    EventBus.getDefault().post(new AdminRefreshProfile());
//                }
//            });
        }

        if (IMAGE_1 == REJECT || IMAGE_2 == REJECT || IMAGE_3 == REJECT) {
            body = body + "<br><br>" + "Unfortunately, below of images are rejected by the admin. Please update your profile accordingly.";
            if (IMAGE_1 == REJECT) {
                body = body + "<br><br>" + imagesPath.get(0).getFile().getUrl();
                body = body + "<br><br>" + "Reason: " + REASON_1;
                if (!COMMENT_1.isEmpty())
                    body = body + "<br><br>" + "Comment: " + COMMENT_1;

            }
            if (IMAGE_2 == REJECT) {
                body = body + "<br><br>" + imagesPath.get(1).getFile().getUrl();
                body = body + "<br><br>" + "Reason: " + REASON_2;
                if (!COMMENT_2.isEmpty())
                    body = body + "<br><br>" + "Comment: " + COMMENT_2;
            }
            if (IMAGE_3 == REJECT) {
                body = body + "<br><br>" + imagesPath.get(2).getFile().getUrl();
                body = body + "<br><br>" + "Reason: " + REASON_3;
                if (!COMMENT_3.isEmpty())
                    body = body + "<br><br>" + "Comment: " + COMMENT_3;
            }

            body = body + "<br><br><br>" + "Note: If you will delete this photo/video from the application then this link will not be accessible again.";

            final HashMap<String, String> params = new HashMap<>();
            params.put("emailId", userEmailId);
            params.put("subject", subject);
            params.put("body", body);
            ParseCloud.callFunctionInBackground(PARSE_CLOUD_FUNCTION_EMAIL_TO_USER, params, (response, exc) -> {
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
//            builder.setMessage("Are you done with the approval/rejection of photos?");
//            builder.setPositiveButton("Yes", (dialog, which) -> {
//                dialog.cancel();
//                if (IMAGE_1 == REJECT || IMAGE_2 == REJECT || IMAGE_3 == REJECT) {
//                    setAndSendEmailToUser();
//                } else {
//                    EventBus.getDefault().post(new AdminRefreshDashboard());
//                    EventBus.getDefault().post(new AdminRefreshProfile());
//                    requireActivity().finish();
//                }
//            });
//            builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());
//            builder.show();

            if (IMAGE_1 == REJECT || IMAGE_2 == REJECT || IMAGE_3 == REJECT) {
                setAndSendEmailToUser();
            } else {
                EventBus.getDefault().post(new AdminRefreshDashboard());
                EventBus.getDefault().post(new AdminRefreshProfile());
                requireActivity().finish();
            }

        } else
            requireActivity().finish();
    }

    @OnClick({R.id.btn_cross, R.id.btn_reject, R.id.btn_approve})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.btn_cross:
                backBtnPress();
//                if (isChange) {
//                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
//                    builder.setTitle("Confirmation");
//                    builder.setMessage("Are you done with approval/rejection of pictures? Please note that rejection of pictures can't be undone.");
//                    builder.setPositiveButton("Yes", (dialog, which) -> {
//                        dialog.cancel();
//                        setAndSendEmailToUser();
//                    });
//                    builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());
//                    builder.show();
//                } else
//                    requireActivity().onBackPressed();
                break;
            case R.id.btn_reject:
                showRejectionReasonsDialog();
                break;
            case R.id.btn_approve:
                System.out.println("==curr index===" + index);
                showProgress();
                isChange = true;
                imagesPath.get(index).put(PARAM_FILE_STATUS, 1);
                imagesPath.get(index).saveInBackground(e -> {
                    dismissProgress();
                    if (e == null) {
                        showToast(getActivity(), getContext(), "Updated successfully.", SUCCESS);
                        String msg = "Your photo has been approved by the admin.";

                        setPushToUser(requireActivity(), requireContext(), imagesPath.get(index).getUserId(), "Media Approved", msg);

                        HashMap<String, Object> params = new HashMap<String, Object>();
                        params.put("userId", imagesPath.get(index).getUserId());
                        params.put("profilePic", imagesPath.get(index).getFile().getUrl());
                        ParseCloud.callFunctionInBackground(PARSE_CLOUD_FUNCTION_UPDATE_USER_PIC, params, new FunctionCallback<String>() {
                            @Override
                            public void done(String object, ParseException e) {
                                if (e == null) {

                                } else {

                                }
                            }
                        });

//                        EventBus.getDefault().post(new AdminRefreshDashboard());
//                        EventBus.getDefault().post(new AdminRefreshProfile());
                        switch (index) {
                            case 0:
                                IMAGE_1 = ACTIVE;
                                break;
                            case 1:
                                IMAGE_2 = ACTIVE;
                                break;
                            case 2:
                                IMAGE_3 = ACTIVE;
                                break;
                        }
                        if (index < (imagesPath.size() - 1)) {
                            index++;
                        } else {
                            index = 0;
                        }
                        viewPager.setCurrentItem(index);
                    } else {
                        showToast(getActivity(), getContext(), e.getMessage(), ERROR);
                    }
                });
                break;
            default:
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (imagesPath.get(position).getInt(PARAM_FILE_STATUS) == REJECT) {
            disableRejectBtn();
            enableApproveBtn();
        } else if (imagesPath.get(position).getInt(PARAM_FILE_STATUS) == ACTIVE) {
            disableApproveBtn();
            enableRejectBtn();
        } else {
            enableApproveBtn();
            enableRejectBtn();
        }
    }

    @Override
    public void onPageSelected(int position) {
        index = position;
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
            zoomageView1.setScaleType(ImageView.ScaleType.CENTER_CROP);
            container.addView(zoomageView1);
            imagePath = imagesPath.get(position).getFile().getUrl();
            if (imagePath != null && imagePath.length() > 0) {
                Picasso.get().load(imagePath).placeholder(R.drawable.image_placeholder).into(zoomageView1);
            } else {
                showToast(getActivity(), getContext(), "Unable to load photo!", ERROR);
            }

            if (imagesPath.get(position).getInt(PARAM_FILE_STATUS) == REJECT) {
                disableRejectBtn();
                enableApproveBtn();
            } else if (imagesPath.get(position).getInt(PARAM_FILE_STATUS) == ACTIVE) {
                disableApproveBtn();
                enableRejectBtn();
            } else {
                enableApproveBtn();
                enableRejectBtn();
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
        imagesPath.get(index).put(PARAM_FILE_STATUS, 2);
        imagesPath.get(index).put(PARAM_REJECT_REASON, reason);
        imagesPath.get(index).put(PARAM_REJECT_COMMENT, comment);
        imagesPath.get(index).saveInBackground(e -> {
            dismissProgress();
            if (e == null) {
                showToast(getActivity(), getContext(), "Updated successfully.", SUCCESS);
//                String msg = "One of your photo has been rejected by the admin due to " + reason + ". Please check your email for details.";
                String msg = "One of your photo has been rejected by the admin. Please check your email for details.";
                setPushToUser(requireActivity(), requireContext(), imagesPath.get(index).getUserId(), "Media Rejected", msg);
                if (userProfilePic.equals(imagesPath.get(index).getFile().getUrl())) {
                    HashMap<String, Object> params = new HashMap<String, Object>();
                    params.put("userId", imagesPath.get(index).getUserId());
                    params.put("profilePic", "");
                    ParseCloud.callFunctionInBackground(PARSE_CLOUD_FUNCTION_UPDATE_USER_PIC, params, new FunctionCallback<String>() {
                        @Override
                        public void done(String object, ParseException e) {
                            if (e == null) {

                            } else {

                            }
                        }
                    });
                }

                switch (index) {
                    case 0:
                        IMAGE_1 = REJECT;
                        REASON_1 = reason;
                        COMMENT_1 = comment;
                        break;
                    case 1:
                        IMAGE_2 = REJECT;
                        REASON_2 = reason;
                        COMMENT_2 = comment;
                        break;
                    case 2:
                        IMAGE_3 = REJECT;
                        REASON_3 = reason;
                        COMMENT_3 = comment;
                        break;
                }

                if (index < (imagesPath.size() - 1)) {
                    index++;
                } else {
                    index = 0;
                }
                viewPager.setCurrentItem(index);
            } else {
                showToast(getActivity(), getContext(), e.getMessage(), ERROR);
            }
        });
    }

}
