package com.app.wingmate.admin.ui.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.wingmate.R;
import com.app.wingmate.admin.events.AdminRefreshDashboard;
import com.app.wingmate.admin.models.RejectionReason;
import com.app.wingmate.admin.ui.dialogs.RejectionReasonsDialog;
import com.app.wingmate.base.BaseFragment;
import com.app.wingmate.base.BaseInteractor;
import com.app.wingmate.base.BasePresenter;
import com.app.wingmate.base.BaseView;
import com.app.wingmate.admin.events.AdminRefreshProfile;
import com.app.wingmate.models.Question;
import com.app.wingmate.models.TermsConditions;
import com.app.wingmate.models.UserAnswer;
import com.app.wingmate.models.UserProfilePhotoVideo;
import com.app.wingmate.ui.adapters.ProfileOptionsListAdapter;
import com.app.wingmate.utils.ActivityUtility;
import com.app.wingmate.utils.AppConstants;
import com.app.wingmate.utils.Utilities;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.app.wingmate.utils.AlertMessages.PROFILE_ACCEPTED;
import static com.app.wingmate.utils.AppConstants.ACTIVE;
import static com.app.wingmate.utils.AppConstants.ERROR;
import static com.app.wingmate.utils.AppConstants.GROUP_A;
import static com.app.wingmate.utils.AppConstants.GROUP_B;
import static com.app.wingmate.utils.AppConstants.NEW;
import static com.app.wingmate.utils.AppConstants.PARAM_ACCOUNT_STATUS;
import static com.app.wingmate.utils.AppConstants.PARAM_FILE_STATUS;
import static com.app.wingmate.utils.AppConstants.PARAM_GROUP_CATEGORY;
import static com.app.wingmate.utils.AppConstants.PARAM_IS_MEDIA_APPROVED;
import static com.app.wingmate.utils.AppConstants.PARAM_IS_PHOTO_SUBMITTED;
import static com.app.wingmate.utils.AppConstants.PARAM_IS_VIDEO_SUBMITTED;
import static com.app.wingmate.utils.AppConstants.PARAM_MEDIA_PENDING;
import static com.app.wingmate.utils.AppConstants.PARAM_PROFILE_PIC;
import static com.app.wingmate.utils.AppConstants.PARAM_REJECT_COMMENT;
import static com.app.wingmate.utils.AppConstants.PARAM_REJECT_REASON;
import static com.app.wingmate.utils.AppConstants.PENDING;
import static com.app.wingmate.utils.AppConstants.PHOTO_NOT_APPROVED;
import static com.app.wingmate.utils.AppConstants.PHOTO_NOT_UPLOADED;
import static com.app.wingmate.utils.AppConstants.PHOTO_VIDEO_NOT_APPROVED;
import static com.app.wingmate.utils.AppConstants.PHOTO_VIDEO_NOT_UPLOADED;
import static com.app.wingmate.utils.AppConstants.REJECT;
import static com.app.wingmate.utils.AppConstants.SUCCESS;
import static com.app.wingmate.utils.AppConstants.TYPE_PROFILE;
import static com.app.wingmate.utils.AppConstants.VIDEO_NOT_APPROVED;
import static com.app.wingmate.utils.AppConstants.VIDEO_NOT_UPLOADED;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_ADMIN_PHOTO_VIEW;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_ADMIN_VIDEO_VIEW;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_VIDEO_VIEW;
import static com.app.wingmate.utils.CommonKeys.KEY_PARSE_USER;
import static com.app.wingmate.utils.Utilities.showToast;

public class AdminProfileFragment extends BaseFragment implements BaseView, RejectionReasonsDialog.RejectionReasonsDialogClickListener {

    public static final String TAG = AdminProfileFragment.class.getName();

    Unbinder unbinder;

    @BindView(R.id.video_card)
    CardView videoCard;
    @BindView(R.id.pic2_card)
    CardView pic2Card;
    @BindView(R.id.pic3_card)
    CardView pic3Card;
    @BindView(R.id.pic_1)
    ImageView pic1;
    @BindView(R.id.pic_2)
    ImageView pic2;
    @BindView(R.id.pic_3)
    ImageView pic3;
    @BindView(R.id.video_1)
    ImageView video1;
    @BindView(R.id.name_tv)
    TextView nameTV;
    @BindView(R.id.distance_tv)
    TextView distanceTV;
    @BindView(R.id.tag)
    TextView tagTV;
    @BindView(R.id.status)
    TextView statusTV;

    @BindView(R.id.profile_options_rv)
    RecyclerView recyclerView;
    //    @BindView(R.id.about_tv)
//    TextView aboutTV;

    private BasePresenter presenter;

    private List<UserAnswer> userAnswers;
    private List<UserProfilePhotoVideo> userProfilePhotoVideos;
    private List<UserProfilePhotoVideo> userProfilePhotoOnly;
    private List<UserProfilePhotoVideo> userProfileVideoOnly;

    private List<RejectionReason> rejectionReasons = new ArrayList<>();

    private ProfileOptionsListAdapter adapter;
    private GridLayoutManager gridLayoutManager;

    private ParseUser parseUser;

    private LocationManager locationManager;

    private boolean isMediaTask = false;
    private String title = "";
    private String msg = "";

    boolean hasApprovedPhoto = false;
    boolean hasApprovedVideo = false;
    boolean hasUploadedPhoto = false;
    boolean hasUploadedVideo = false;

    boolean sendNotiAndEmail = false;

    public double currentLocationLatitude = AppConstants.DEFAULT_LATITUDE;
    public double currentLocationLongitude = AppConstants.DEFAULT_LONGITUDE;

    public AdminProfileFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_admin, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        presenter = new BasePresenter(this, new BaseInteractor());

        locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);

        parseUser = getActivity().getIntent().getParcelableExtra(KEY_PARSE_USER);

        distanceTV.setVisibility(View.VISIBLE);

        showProgress();
        presenter.queryRejectReasons(getContext());
        presenter.queryUserAnswers(getContext(), parseUser);
        presenter.adminQueryUserPhotosVideo(getContext(), parseUser);

        pic2Card.setVisibility(View.GONE);
        pic3Card.setVisibility(View.GONE);
        videoCard.setVisibility(View.GONE);

        userAnswers = new ArrayList<>();
        gridLayoutManager = new GridLayoutManager(getActivity(), 1);
        adapter = new ProfileOptionsListAdapter(getActivity(), userAnswers);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Subscribe
    public void refreshProfile(AdminRefreshProfile refreshProfile) {
        showProgress();
        presenter.querySpecificUser(getContext(), parseUser);
    }

    @Override
    public void setSpecificUserSuccess(ParseUser pu) {
        this.parseUser = pu;
        presenter.queryUserAnswers(getContext(), pu);
        presenter.adminQueryUserPhotosVideo(getContext(), pu);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.back_btn, R.id.pic_1, R.id.pic2_card, R.id.pic3_card, R.id.video_card,
            R.id.btn_group_a, R.id.btn_group_b, R.id.btn_reject})
    public void onViewClicked(View v) {
        if (v.getId() == R.id.back_btn) {
            getActivity().onBackPressed();
        } else if (v.getId() == R.id.pic_1) {
            ArrayList<String> arrayList = new ArrayList<>();
            if (userProfilePhotoOnly != null && userProfilePhotoOnly.size() > 0) {
                for (int i = 0; i < userProfilePhotoOnly.size(); i++) {
                    arrayList.add(userProfilePhotoOnly.get(i).getFile().getUrl());
                }
                ActivityUtility.startPhotoViewActivityAdmin(requireActivity(),
                        KEY_FRAGMENT_ADMIN_PHOTO_VIEW,
                        (ArrayList<UserProfilePhotoVideo>) userProfilePhotoOnly,
                        0,
                        (ArrayList<RejectionReason>) rejectionReasons,
                        parseUser.getUsername(),
                        parseUser.getString(PARAM_PROFILE_PIC));
            }
        } else if (v.getId() == R.id.pic2_card) {
            ArrayList<String> arrayList = new ArrayList<>();
            if (userProfilePhotoOnly != null && userProfilePhotoOnly.size() > 0) {
                for (int i = 0; i < userProfilePhotoOnly.size(); i++) {
                    arrayList.add(userProfilePhotoOnly.get(i).getFile().getUrl());
                }
                ActivityUtility.startPhotoViewActivityAdmin(requireActivity(), KEY_FRAGMENT_ADMIN_PHOTO_VIEW, (ArrayList<UserProfilePhotoVideo>) userProfilePhotoOnly,
                        1, (ArrayList<RejectionReason>) rejectionReasons, parseUser.getUsername(),
                        parseUser.getString(PARAM_PROFILE_PIC));
            }
        } else if (v.getId() == R.id.pic3_card) {
            ArrayList<String> arrayList = new ArrayList<>();
            if (userProfilePhotoOnly != null && userProfilePhotoOnly.size() > 0) {
                for (int i = 0; i < userProfilePhotoOnly.size(); i++) {
                    arrayList.add(userProfilePhotoOnly.get(i).getFile().getUrl());
                }
                ActivityUtility.startPhotoViewActivityAdmin(requireActivity(), KEY_FRAGMENT_ADMIN_PHOTO_VIEW,
                        (ArrayList<UserProfilePhotoVideo>) userProfilePhotoOnly, 2,
                        (ArrayList<RejectionReason>) rejectionReasons, parseUser.getUsername(),
                        parseUser.getString(PARAM_PROFILE_PIC));
            }
        } else if (v.getId() == R.id.video_card) {
            if (userProfileVideoOnly != null && userProfileVideoOnly.size() > 0)
                ActivityUtility.startVideoViewActivityAdmin(requireActivity(), KEY_FRAGMENT_ADMIN_VIDEO_VIEW, userProfileVideoOnly.get(0), (ArrayList<RejectionReason>) rejectionReasons, parseUser.getUsername());
//            ActivityUtility.startVideoViewActivity(requireActivity(), KEY_FRAGMENT_VIDEO_VIEW, userProfileVideoOnly.get(0).getFile().getUrl());
        } else if (v.getId() == R.id.btn_group_a) {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            if (parseUser != null) {
                if (parseUser.getBoolean(PARAM_IS_MEDIA_APPROVED)) {
                    builder.setMessage("Are you sure to approve this user for group A?");
                    builder.setPositiveButton("Yes", (dialog, which) -> {
                        dialog.cancel();

                        String currentCat = parseUser.getString(PARAM_GROUP_CATEGORY);
                        if (currentCat.equalsIgnoreCase("A") || currentCat.equalsIgnoreCase("B"))
                            sendNotiAndEmail = false;
                        else
                            sendNotiAndEmail = true;

                        showProgress();
                        isMediaTask = false;
                        title = "Profile Approved";
                        msg = PROFILE_ACCEPTED;
                        presenter.updateUserViaCloudCode(getContext(),
                                parseUser.getObjectId(),
                                "A",
                                1,
                                "",
                                "",
                                parseUser.getBoolean(PARAM_IS_MEDIA_APPROVED),
                                isMediaPending());
                    });
                    builder.setNegativeButton("Cancel", (dialog, which) -> {
                        dialog.cancel();
                    });
                    builder.show();
                } else if (!parseUser.getBoolean(PARAM_IS_PHOTO_SUBMITTED) && !parseUser.getBoolean(PARAM_IS_VIDEO_SUBMITTED)) {
                    builder.setMessage(PHOTO_VIDEO_NOT_UPLOADED);
                    builder.setPositiveButton("OK", (dialog, which) -> {
                        dialog.cancel();
                    });
                    builder.show();
                } else if (!parseUser.getBoolean(PARAM_IS_PHOTO_SUBMITTED) || !hasUploadedPhoto) {
                    builder.setMessage(PHOTO_NOT_UPLOADED);
                    builder.setPositiveButton("OK", (dialog, which) -> {
                        dialog.cancel();
                    });
                    builder.show();
                } else if (!parseUser.getBoolean(PARAM_IS_VIDEO_SUBMITTED) || !hasUploadedVideo) {
                    builder.setMessage(VIDEO_NOT_UPLOADED);
                    builder.setPositiveButton("OK", (dialog, which) -> {
                        dialog.cancel();
                    });
                    builder.show();
                } else if (!hasApprovedPhoto && !hasApprovedVideo) {
                    builder.setMessage(PHOTO_VIDEO_NOT_APPROVED);
                    builder.setPositiveButton("OK", (dialog, which) -> {
                        dialog.cancel();
                    });
                    builder.show();
                } else if (!hasApprovedPhoto) {
                    builder.setMessage(PHOTO_NOT_APPROVED);
                    builder.setPositiveButton("OK", (dialog, which) -> {
                        dialog.cancel();
                    });
                    builder.show();
                } else if (!hasApprovedVideo) {
                    builder.setMessage(VIDEO_NOT_APPROVED);
                    builder.setPositiveButton("OK", (dialog, which) -> {
                        dialog.cancel();
                    });
                    builder.show();
                } else {
                    builder.setMessage(PHOTO_VIDEO_NOT_UPLOADED);
                    builder.setPositiveButton("OK", (dialog, which) -> {
                        dialog.cancel();
                    });
                    builder.show();
                }
            }
        } else if (v.getId() == R.id.btn_group_b) {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            if (parseUser != null) {
                if (parseUser.getBoolean(PARAM_IS_MEDIA_APPROVED)) {
                    builder.setMessage("Are you sure to approve this user for group B?");
                    builder.setPositiveButton("Yes", (dialog, which) -> {
                        dialog.cancel();
                        String currentCat = parseUser.getString(PARAM_GROUP_CATEGORY);
                        if (currentCat.equalsIgnoreCase("A") || currentCat.equalsIgnoreCase("B"))
                            sendNotiAndEmail = false;
                        else
                            sendNotiAndEmail = true;
                        showProgress();
                        isMediaTask = false;
                        title = "Profile Approved";
                        msg = PROFILE_ACCEPTED;
                        presenter.updateUserViaCloudCode(getContext(),
                                parseUser.getObjectId(),
                                "B",
                                1,
                                "",
                                "",
                                parseUser.getBoolean(PARAM_IS_MEDIA_APPROVED),
                                isMediaPending());

                    });
                    builder.setNegativeButton("Cancel", (dialog, which) -> {
                        dialog.cancel();
                    });
                    builder.show();
                } else if ((!parseUser.getBoolean(PARAM_IS_PHOTO_SUBMITTED) && !parseUser.getBoolean(PARAM_IS_VIDEO_SUBMITTED))
                        || (!hasUploadedPhoto && !hasUploadedVideo)) {
                    builder.setMessage(PHOTO_VIDEO_NOT_UPLOADED);
                    builder.setPositiveButton("OK", (dialog, which) -> {
                        dialog.cancel();
                    });
                    builder.show();
                } else if (!parseUser.getBoolean(PARAM_IS_PHOTO_SUBMITTED) || !hasUploadedPhoto) {
                    builder.setMessage(PHOTO_NOT_UPLOADED);
                    builder.setPositiveButton("OK", (dialog, which) -> {
                        dialog.cancel();
                    });
                    builder.show();
                } else if (!parseUser.getBoolean(PARAM_IS_VIDEO_SUBMITTED) || !hasUploadedVideo) {
                    builder.setMessage(VIDEO_NOT_UPLOADED);
                    builder.setPositiveButton("OK", (dialog, which) -> {
                        dialog.cancel();
                    });
                    builder.show();
                } else if (!hasApprovedPhoto && !hasApprovedVideo) {
                    builder.setMessage(PHOTO_VIDEO_NOT_APPROVED);
                    builder.setPositiveButton("OK", (dialog, which) -> {
                        dialog.cancel();
                    });
                    builder.show();
                } else if (!hasApprovedPhoto) {
                    builder.setMessage(PHOTO_NOT_APPROVED);
                    builder.setPositiveButton("OK", (dialog, which) -> {
                        dialog.cancel();
                    });
                    builder.show();
                } else if (!hasApprovedVideo) {
                    builder.setMessage(VIDEO_NOT_APPROVED);
                    builder.setPositiveButton("OK", (dialog, which) -> {
                        dialog.cancel();
                    });
                    builder.show();
                } else {
                    builder.setMessage(PHOTO_VIDEO_NOT_UPLOADED);
                    builder.setPositiveButton("OK", (dialog, which) -> {
                        dialog.cancel();
                    });
                    builder.show();
                }
            }
        } else if (v.getId() == R.id.btn_reject) {
            showRejectionReasonsDialog();
        }
    }

    @Override
    public void setRejectReasonResponseSuccess(List<RejectionReason> rejectReasons) {
        if (rejectReasons == null) rejectReasons = new ArrayList<>();
        rejectionReasons = rejectReasons;
        System.out.println("==rejectReasons==" + rejectReasons.size());
    }

    @Override
    public void setResponseSuccess() {
        dismissProgress();
        if (!isMediaTask) {
            showToast(getActivity(), getContext(), "Updated successfully.", SUCCESS);
            if (msg.length() > 0 && title.length() > 0) {
                if (sendNotiAndEmail) {
                    setPushToUser(requireActivity(), requireContext(), parseUser.getObjectId(), title, msg);
                    sendEmailToUser(requireActivity(), requireContext(), parseUser.getUsername(), title, msg);
//                    new senEmailTask().execute();
                }
                title = "";
                msg = "";
            }
//            EventBus.getDefault().post(new AdminRefreshProfile());
            EventBus.getDefault().post(new AdminRefreshDashboard());
            getActivity().onBackPressed();
        } else {
            EventBus.getDefault().post(new AdminRefreshProfile());
        }
    }

    private final class senEmailTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
//                sendEmailToUser(parseUser.getEmail(), title, msg);
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {

        }
    }

    @Override
    public void setResponseError(ParseException e) {
        dismissProgress();
        showToast(getActivity(), getContext(), e.getMessage(), ERROR);
    }

    @Override
    public void setTermsResponseSuccess(List<TermsConditions> termsConditions) {

    }

    @Override
    public void setQuestionsResponseSuccess(List<Question> questions) {

    }

    @Override
    public void setUserAnswersResponseSuccess(List<UserAnswer> userAnswers) {
        dismissProgress();
        this.userAnswers = new ArrayList<>();
        if (userAnswers != null && userAnswers.size() > 0) {
            for (int i = 0; i < userAnswers.size(); i++) {
                if (userAnswers.get(i).getOptionsObjArray() != null && userAnswers.get(i).getOptionsObjArray().size() > 0) {
                    this.userAnswers.add(userAnswers.get(i));
                }
            }
        }
        setAnswersInViews();
        setViews();
    }

    @Override
    public void setUserPhotosVideoResponseSuccess(List<UserProfilePhotoVideo> userProfilePhotoVideos) {
        this.userProfilePhotoVideos = new ArrayList<>();
        if (userProfilePhotoVideos != null && userProfilePhotoVideos.size() > 0)
            this.userProfilePhotoVideos = userProfilePhotoVideos;
        if (requireActivity() != null) setMediaInViews();
    }


    private void setViews() {
        ParseGeoPoint myGeoPoint = new ParseGeoPoint();
        myGeoPoint.setLatitude(getLastBestLocation().getLatitude());
        myGeoPoint.setLongitude(getLastBestLocation().getLongitude());
        distanceTV.setVisibility(View.VISIBLE);
//        distanceTV.setText(Utilities.getDistanceBetweenUser(parseUser, myGeoPoint));
        distanceTV.setText(Utilities.getDistanceBetweenUser(parseUser));
        if (parseUser != null) {
            nameTV.setText(parseUser.getString(AppConstants.PARAM_NICK));
//            String val = parseUser.getString(AppConstants.PARAM_ABOUT_ME);
//            String sourceString = "<b>" + "About Me: " + "</b> " + val;
//            aboutTV.setText(Html.fromHtml(sourceString));
        }

        String groupCat = parseUser.getString(PARAM_GROUP_CATEGORY);
        int accStatus = parseUser.getInt(PARAM_ACCOUNT_STATUS);
        boolean isMediaPending = parseUser.getBoolean(PARAM_MEDIA_PENDING);

        if (groupCat != null && groupCat.equalsIgnoreCase(GROUP_A)) {
            tagTV.setText("Group A");
            tagTV.setBackgroundResource(R.drawable.bg_group_a);
        } else if (groupCat != null && groupCat.equalsIgnoreCase(GROUP_B)) {
            tagTV.setText("Group B");
            tagTV.setBackgroundResource(R.drawable.bg_group_b);
        } else if (groupCat == null || groupCat.isEmpty() || groupCat.equalsIgnoreCase(NEW)) {
            statusTV.setVisibility(View.VISIBLE);
            tagTV.setText("New User");
            tagTV.setBackgroundResource(R.drawable.bg_group_n);
        }

        if (accStatus == PENDING) {
            statusTV.setText("Pending");
            statusTV.setBackgroundResource(R.drawable.bg_status_pending);
            statusTV.setVisibility(View.VISIBLE);
        } else if (accStatus == ACTIVE) {
            statusTV.setText("Active");
            statusTV.setBackgroundResource(R.drawable.bg_status_active);
            statusTV.setVisibility(View.GONE);
            if (isMediaPending) {
                statusTV.setText("Media Pending");
                statusTV.setBackgroundResource(R.drawable.bg_status_pending);
                statusTV.setVisibility(View.VISIBLE);
            } else {
                statusTV.setVisibility(View.GONE);
            }
        } else if (accStatus == REJECT) {
            statusTV.setText("Rejected");
            statusTV.setBackgroundResource(R.drawable.bg_status_rejected);
            statusTV.setVisibility(View.VISIBLE);
        }
    }

    private void setAnswersInViews() {
        if (userAnswers != null && userAnswers.size() > 0) {
            Collections.sort(userAnswers, (lhs, rhs) -> Integer.compare(lhs.getQuestionId().getProfileDisplayOrder(), rhs.getQuestionId().getProfileDisplayOrder()));
            UserAnswer aboutAnswer = new UserAnswer();
            aboutAnswer.setDummyUser(parseUser);
            userAnswers.add(aboutAnswer);
            adapter.setData(userAnswers);
            adapter.notifyDataSetChanged();
        }
    }

    private boolean isMediaPending() {
        boolean isPending = false;
        if (userProfilePhotoOnly != null && userProfilePhotoOnly.size() > 0) {
            for (UserProfilePhotoVideo userProfilePhotoVideo : userProfilePhotoOnly) {
                if (userProfilePhotoVideo.getInt(PARAM_FILE_STATUS) == PENDING) {
                    isPending = true;
                    break;
                }
            }
        }
        if (userProfileVideoOnly != null && userProfileVideoOnly.size() > 0) {
            for (UserProfilePhotoVideo userProfilePhotoVideo : userProfileVideoOnly) {
                if (userProfilePhotoVideo.getInt(PARAM_FILE_STATUS) == PENDING) {
                    isPending = true;
                    break;
                }
            }
        }
        return isPending;
    }

    private void setMediaInViews() {
        pic2Card.setVisibility(View.GONE);
        pic3Card.setVisibility(View.GONE);
        videoCard.setVisibility(View.GONE);
        pic1.setImageResource(android.R.color.transparent);

        boolean hasPhotosSubmitted = false;
        boolean hasVideoSubmitted = false;

        hasApprovedPhoto = false;
        hasApprovedVideo = false;
        hasUploadedPhoto = false;
        hasUploadedVideo = false;

        if (userProfilePhotoVideos != null && userProfilePhotoVideos.size() > 0) {

            userProfilePhotoOnly = new ArrayList<>();
            userProfileVideoOnly = new ArrayList<>();

            for (UserProfilePhotoVideo userProfilePhotoVideo : userProfilePhotoVideos) {
                if (userProfilePhotoVideo.isPhoto())
                    userProfilePhotoOnly.add(userProfilePhotoVideo);
                else userProfileVideoOnly.add(userProfilePhotoVideo);
            }

//            if (userProfilePhotoOnly != null && userProfilePhotoOnly.size() > 0) {
//                for (int i = 0; i < userProfilePhotoOnly.size(); i++) {
//                    String profilePicUrl = parseUser.getString(PARAM_PROFILE_PIC);
//                    if (profilePicUrl != null && profilePicUrl.equals(userProfilePhotoOnly.get(i).getFile().getUrl())) {
//                        Collections.swap(userProfilePhotoOnly, i, 0);
//                        break;
//                    }
//                }
//            }

            if (userProfilePhotoOnly.size() > 0) {
                if (userProfilePhotoOnly.get(0).getInt(PARAM_FILE_STATUS) == 1)
                    hasApprovedPhoto = true;
                Picasso.get().load(userProfilePhotoOnly.get(0).getFile().getUrl()).placeholder(R.drawable.image_placeholder).into(pic1);
                hasPhotosSubmitted = true;
                hasUploadedPhoto = true;
            }
            if (userProfilePhotoOnly.size() > 1) {
                if (userProfilePhotoOnly.get(1).getInt(PARAM_FILE_STATUS) == 1)
                    hasApprovedPhoto = true;
                pic2Card.setVisibility(View.VISIBLE);
                Picasso.get().load(userProfilePhotoOnly.get(1).getFile().getUrl()).placeholder(R.drawable.image_placeholder).into(pic2);
                hasPhotosSubmitted = true;
                hasUploadedPhoto = true;
            }
            if (userProfilePhotoOnly.size() > 2) {
                if (userProfilePhotoOnly.get(2).getInt(PARAM_FILE_STATUS) == 1)
                    hasApprovedPhoto = true;
                pic3Card.setVisibility(View.VISIBLE);
                Picasso.get().load(userProfilePhotoOnly.get(2).getFile().getUrl()).placeholder(R.drawable.image_placeholder).into(pic3);
                hasPhotosSubmitted = true;
                hasUploadedPhoto = true;
            }
            if (userProfileVideoOnly.size() > 0) {
                if (userProfileVideoOnly.get(0).getInt(PARAM_FILE_STATUS) == 1)
                    hasApprovedVideo = true;
                hasUploadedVideo = true;
                videoCard.setVisibility(View.VISIBLE);
                Glide.with(requireContext())
                        .load(userProfileVideoOnly.get(0).getFile().getUrl())
                        .thumbnail(Glide.with(requireContext()).load(userProfileVideoOnly.get(0).getFile().getUrl()).placeholder(R.drawable.video_placeholder1).apply(new RequestOptions().override(200, 200)))
                        .apply(new RequestOptions().override(200, 200))
                        .placeholder(R.drawable.video_placeholder1)
                        .into(video1);

                hasVideoSubmitted = true;

            }

            System.out.println("====is photo app==" + hasApprovedPhoto);
            System.out.println("====is video app==" + hasApprovedVideo);

            if (hasApprovedPhoto && hasApprovedVideo && !parseUser.getBoolean(PARAM_IS_MEDIA_APPROVED)) {
                isMediaTask = true;
                presenter.updateUserViaCloudCode(getContext(),
                        parseUser.getObjectId(),
                        parseUser.getString(PARAM_GROUP_CATEGORY),
                        parseUser.getInt(PARAM_ACCOUNT_STATUS),
                        parseUser.getString(PARAM_REJECT_REASON),
                        parseUser.getString(PARAM_REJECT_COMMENT),
                        true,
                        isMediaPending());
            } else if ((!hasApprovedPhoto || !hasApprovedVideo) && parseUser.getBoolean(PARAM_IS_MEDIA_APPROVED)) {
                isMediaTask = true;
//                title = "Profile Pending";
//                msg = "Your profile is waiting for approval. You can not interact with other users right now";
                presenter.updateUserViaCloudCode(getContext(),
                        parseUser.getObjectId(),
                        parseUser.getString(PARAM_GROUP_CATEGORY),
                        0,
                        parseUser.getString(PARAM_REJECT_REASON),
                        parseUser.getString(PARAM_REJECT_COMMENT),
                        false,
                        isMediaPending());
            }
        }
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
            if (locationGPS == null) locationGPS = defaultLoc;
            return locationGPS;
        } else {
            if (locationNet == null) locationNet = defaultLoc;
            return locationNet;
        }
    }

    public Location getLastBestLocation2() {
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

    public void showRejectionReasonsDialog() {
        List<RejectionReason> list = new ArrayList<>();
        for (int i = 0; i < rejectionReasons.size(); i++) {
            if (rejectionReasons.get(i).getType().equals(TYPE_PROFILE))
                list.add(rejectionReasons.get(i));
        }
        RejectionReasonsDialog dialog = RejectionReasonsDialog.newInstance(this, getActivity(), list, TYPE_PROFILE);
        dialog.show(getActivity().getSupportFragmentManager(), "rejection_dialog");
    }

    @Override
    public void onRejectionSelectorBackButtonClick(DialogFragment dialog) {
        dialog.dismiss();
    }

    @Override
    public void onRejectionSelectorSaveClick(DialogFragment dialog, String reason, String comment) {
        dialog.dismiss();
        if (parseUser != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setMessage("Are you sure to reject this user?");
            builder.setPositiveButton("Yes", (dd, which) -> {
                dd.cancel();
                showProgress();
                isMediaTask = false;
                title = "Profile Rejected";
                msg = "Your profile has been rejected by the Wingmate.";
                msg = msg + "<br><br>" + "Reason: " + reason;
                if (!comment.isEmpty())
                    msg = msg + "<br><br>" + "Comment: " + comment;
//                msg = "Your profile has been rejected by the admin.";
                presenter.updateUserViaCloudCode(getContext(),
                        parseUser.getObjectId(),
                        parseUser.getString(PARAM_GROUP_CATEGORY),
                        2,
                        reason,
                        comment,
                        parseUser.getBoolean(PARAM_IS_MEDIA_APPROVED),
                        isMediaPending());
            });
            builder.setNegativeButton("Cancel", (dd, which) -> {
                dd.cancel();
            });
            builder.show();
        }
    }
}