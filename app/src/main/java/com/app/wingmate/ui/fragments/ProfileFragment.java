package com.app.wingmate.ui.fragments;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.wingmate.R;
import com.app.wingmate.base.BaseFragment;
import com.app.wingmate.base.BaseInteractor;
import com.app.wingmate.base.BasePresenter;
import com.app.wingmate.base.BaseView;
import com.app.wingmate.events.RefreshDashboardView;
import com.app.wingmate.events.RefreshFanList;
import com.app.wingmate.events.RefreshFans;
import com.app.wingmate.events.RefreshProfile;
import com.app.wingmate.models.Fans;
import com.app.wingmate.models.Question;
import com.app.wingmate.models.TermsConditions;
import com.app.wingmate.models.UserAnswer;
import com.app.wingmate.models.UserProfilePhotoVideo;
import com.app.wingmate.ui.adapters.ProfileOptionsListAdapter;
import com.app.wingmate.utils.ActivityUtility;
import com.app.wingmate.utils.AppConstants;
import com.app.wingmate.utils.SharedPrefers;
import com.app.wingmate.utils.Utilities;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.parse.DeleteCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
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
import me.tankery.lib.circularseekbar.CircularSeekBar;

import static com.app.wingmate.utils.AlertMessages.GO_TO_ACC_PENDING_SCREEN;
import static com.app.wingmate.utils.AlertMessages.GO_TO_PAYMENT_SCREEN;
import static com.app.wingmate.utils.AlertMessages.GO_TO_PAYMENT_SCREEN_AFTER_EXPIRED;
import static com.app.wingmate.utils.AlertMessages.GO_TO_QUESTIONNAIRE_SCREEN;
import static com.app.wingmate.utils.AlertMessages.GO_TO_UPLOAD_SCREEN;
import static com.app.wingmate.utils.AlertMessages.GO_TO_UPLOAD_SCREEN_AFTER_EXPIRED;
import static com.app.wingmate.utils.AppConstants.ACTIVE;
import static com.app.wingmate.utils.AppConstants.ERROR;
import static com.app.wingmate.utils.AppConstants.FAN_TYPE_CRUSH;
import static com.app.wingmate.utils.AppConstants.FAN_TYPE_LIKE;
import static com.app.wingmate.utils.AppConstants.FAN_TYPE_MAY_BE;
import static com.app.wingmate.utils.AppConstants.MANDATORY;
import static com.app.wingmate.utils.AppConstants.NOTI_MSG_CRUSH;
import static com.app.wingmate.utils.AppConstants.NOTI_MSG_LIKED;
import static com.app.wingmate.utils.AppConstants.NOTI_MSG_MAYBE;
import static com.app.wingmate.utils.AppConstants.NOTI_MSG_UN_CRUSH;
import static com.app.wingmate.utils.AppConstants.NOTI_MSG_UN_LIKED;
import static com.app.wingmate.utils.AppConstants.NOTI_MSG_UN_MAYBE;
import static com.app.wingmate.utils.AppConstants.OPTIONAL;
import static com.app.wingmate.utils.AppConstants.PARAM_ACCOUNT_STATUS;
import static com.app.wingmate.utils.AppConstants.PARAM_IS_PAID_USER;
import static com.app.wingmate.utils.AppConstants.PARAM_IS_PHOTO_SUBMITTED;
import static com.app.wingmate.utils.AppConstants.PARAM_IS_VIDEO_SUBMITTED;
import static com.app.wingmate.utils.AppConstants.PARAM_MANDATORY_QUESTIONNAIRE_FILLED;
import static com.app.wingmate.utils.AppConstants.PARAM_NICK;
import static com.app.wingmate.utils.AppConstants.PARAM_PROFILE_PIC;
import static com.app.wingmate.utils.AppConstants.PENDING;
import static com.app.wingmate.utils.AppConstants.REJECTED;
import static com.app.wingmate.utils.AppConstants.SUCCESS;
import static com.app.wingmate.utils.AppConstants.TRIAL_PERIOD;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_ACCOUNT_PENDING;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_CHAT;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_DASHBOARD;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_EDIT_PROFILE;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_LAUNCH_CAMPAIGN;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_MEMBERSHIP;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_PAYMENT;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_PHOTO_VIEW;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_PRE_LOGIN;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_QUESTIONNAIRE;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_UPLOAD_PHOTO_VIDEO_PROFILE;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_VIDEO_VIEW;
import static com.app.wingmate.utils.CommonKeys.KEY_IS_CURRENT_USER;
import static com.app.wingmate.utils.CommonKeys.KEY_PARSE_USER;
import static com.app.wingmate.utils.CommonKeys.PREF_LAST_UPDATE_TIME;
import static com.app.wingmate.utils.Utilities.print;
import static com.app.wingmate.utils.Utilities.showToast;

public class ProfileFragment extends BaseFragment implements BaseView {

    public static final String TAG = ProfileFragment.class.getName();

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
    @BindView(R.id.match_view)
    RelativeLayout matchView;
    @BindView(R.id.match_progress)
    CircularSeekBar matchProgress;
    @BindView(R.id.match_percent_tv)
    TextView matchPercentTV;
    @BindView(R.id.profile_options_rv)
    RecyclerView recyclerView;
    //    @BindView(R.id.about_tv)
//    TextView aboutTV;
    @BindView(R.id.btn_edit)
    Button editBtn;
    @BindView(R.id.btn_edit_media)
    Button editMediaBtn;
    @BindView(R.id.edit_bottom_view)
    LinearLayout editProfileView;
    @BindView(R.id.others_buttons_view)
    LinearLayout othersBottomView;
    @BindView(R.id.btn_may_be)
    ImageView btnMaybe;
    @BindView(R.id.btn_like)
    ImageView btnLike;
    @BindView(R.id.btn_crush)
    ImageView btnCrush;
    @BindView(R.id.btn_msg)
    ImageView btnMsg;

    private BasePresenter presenter;

    private List<UserAnswer> userAnswers;
    private List<UserProfilePhotoVideo> userProfilePhotoVideos;
    private List<UserProfilePhotoVideo> userProfilePhotoOnly;
    private List<UserProfilePhotoVideo> userProfileVideoOnly;

    private ProfileOptionsListAdapter adapter;
    private GridLayoutManager gridLayoutManager;

    private boolean isCurrentUser;
    private ParseUser parseUser;

    private Fans maybeObject;
    private Fans crushObject;
    private Fans likeObject;

    public boolean isExpired = false;
    public int remainingDays = TRIAL_PERIOD;
    private boolean isLaunchCampaignStatus = false;

    public ProfileFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        presenter = new BasePresenter(this, new BaseInteractor());

        parseUser = getActivity().getIntent().getParcelableExtra(KEY_PARSE_USER);
        isCurrentUser = getActivity().getIntent().getBooleanExtra(KEY_IS_CURRENT_USER, true);

        if (isCurrentUser) {
            matchView.setVisibility(View.INVISIBLE);
            distanceTV.setVisibility(View.GONE);
            editProfileView.setVisibility(View.VISIBLE);
            othersBottomView.setVisibility(View.GONE);
        } else {
            distanceTV.setVisibility(View.VISIBLE);
            matchView.setVisibility(View.VISIBLE);
            editProfileView.setVisibility(View.GONE);
            othersBottomView.setVisibility(View.VISIBLE);
        }

        showProgress();
        presenter.getLaunchCampaignStatus(getContext());
        presenter.queryUserAnswers(getContext(), parseUser);
        presenter.queryUserPhotosVideo(getContext(), parseUser);
        if (!isCurrentUser) {
            maybeObject = null;
            crushObject = null;
            likeObject = null;
            presenter.queryUserFansStatus(requireContext(), parseUser);
        }

        pic2Card.setVisibility(View.GONE);
        pic3Card.setVisibility(View.GONE);
        videoCard.setVisibility(View.GONE);

        userAnswers = new ArrayList<>();
        gridLayoutManager = new GridLayoutManager(getActivity(), 1);
        adapter = new ProfileOptionsListAdapter(getActivity(), userAnswers);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter);

        fetchUpdatedCurrentUser();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Subscribe
    public void refreshProfile(RefreshProfile refreshProfile) {
        if (isCurrentUser) {
            parseUser = ParseUser.getCurrentUser();
        }
        showProgress();
        presenter.queryUserAnswers(getContext(), parseUser);
        presenter.queryUserPhotosVideo(getContext(), parseUser);

        if (!isCurrentUser) {
            presenter.queryUserFansStatus(requireContext(), parseUser);
        }
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

    private void showPaymentAlert(String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(requireContext());
        dialog
//                .setTitle(getString(R.string.app_name))
//                .setIcon(R.drawable.app_heart)
                .setMessage(msg)
                .setNegativeButton("No", (dialoginterface, i) -> {
                    dialoginterface.cancel();
                })
                .setPositiveButton("Yes", (dialoginterface, i) -> {
                    dialoginterface.cancel();
                    ActivityUtility.startPaymentActivity(requireActivity(), KEY_FRAGMENT_PAYMENT, false);
                }).show();
    }

    private boolean canInteractWithUser() {

        int accountStatus = ParseUser.getCurrentUser().getInt(PARAM_ACCOUNT_STATUS);
        boolean isPaid = ParseUser.getCurrentUser().getBoolean(PARAM_IS_PAID_USER);
        boolean isPhotoSubmitted = ParseUser.getCurrentUser().getBoolean(PARAM_IS_PHOTO_SUBMITTED);
        boolean isVideoSubmitted = ParseUser.getCurrentUser().getBoolean(PARAM_IS_VIDEO_SUBMITTED);
        boolean isMandatoryQuestionnaireFilled = ParseUser.getCurrentUser().getBoolean(PARAM_MANDATORY_QUESTIONNAIRE_FILLED);

        AlertDialog.Builder dialog = new AlertDialog.Builder(requireContext());

        if (accountStatus == PENDING && (!isPhotoSubmitted || !isVideoSubmitted)) {
            dialog
//                    .setTitle(getString(R.string.app_name))
//                    .setIcon(R.drawable.app_heart)
                    .setMessage("Please upload your photos and video and become a paid user to avail this feature. Do you want to upload photos and video?")
                    .setNegativeButton("Upload Later", (dialoginterface, i) -> {
                        dialoginterface.cancel();
                    })
                    .setPositiveButton("Upload Now", (dialoginterface, i) -> {
                        dialoginterface.cancel();
                        ActivityUtility.startProfileMediaActivity(requireActivity(), KEY_FRAGMENT_UPLOAD_PHOTO_VIDEO_PROFILE, false, isExpired);
                    }).show();
            return false;
        } else if (accountStatus == PENDING) {
            dialog
//                    .setTitle(getString(R.string.app_name))
//                    .setIcon(R.drawable.app_heart)
                    .setMessage("Your account is under review. Only approved and paid accounts can avail this feature.")
                    .setPositiveButton("OK", (dialoginterface, i) -> {
                        dialoginterface.cancel();
                    }).show();
            return false;
        } else if (!isPaid && accountStatus == ACTIVE) {
            dialog
//                    .setTitle(getString(R.string.app_name))
//                    .setIcon(R.drawable.app_heart)
                    .setMessage("Please become a paid user to avail this feature. Do you want to pay?")
                    .setNegativeButton("Pay Later", (dialoginterface, i) -> {
                        dialoginterface.cancel();
                    })
                    .setPositiveButton("Pay Now", (dialoginterface, i) -> {
                        dialoginterface.cancel();
                        if (isLaunchCampaignStatus){
                            ActivityUtility.startLaunchActivity(getActivity(),KEY_FRAGMENT_LAUNCH_CAMPAIGN,false);
                        }else{
                            // ActivityUtility.startPaymentActivity(getActivity(), KEY_FRAGMENT_PAYMENT, true);
                            ActivityUtility.startPaymentActivity(getActivity(), KEY_FRAGMENT_MEMBERSHIP, false);
                        }
                    }).show();
            return false;
        } else if (isPaid && accountStatus == ACTIVE && !isMandatoryQuestionnaireFilled) {
            dialog
//                    .setTitle(getString(R.string.app_name))
//                    .setIcon(R.drawable.app_heart)
                    .setMessage("Please fill-up your mandatory questionnaire to avail this feature.")
                    .setNegativeButton("Fill Later", (dialoginterface, i) -> {
                        dialoginterface.cancel();
                    })
                    .setPositiveButton("Fill Now", (dialoginterface, i) -> {
                        dialoginterface.cancel();
                        ActivityUtility.startQuestionnaireActivity(getActivity(), KEY_FRAGMENT_QUESTIONNAIRE, MANDATORY, false);
                    }).show();
            return false;
        } else return true;
    }

    @OnClick({R.id.back_btn, R.id.btn_edit, R.id.btn_edit_media, R.id.pic_1, R.id.pic2_card, R.id.pic3_card, R.id.video_card,
            R.id.btn_may_be, R.id.btn_like, R.id.btn_crush, R.id.btn_msg, R.id.btn_refresh})
    public void onViewClicked(View v) {
        if (v.getId() == R.id.back_btn) {
            getActivity().onBackPressed();
        } else if (v.getId() == R.id.btn_edit) {
//            if (!ParseUser.getCurrentUser().getBoolean(PARAM_IS_PAID_USER)) {
//                showPaymentAlert("You need to pay first to edit your profile. Do you want to pay now?");
//            } else {
//                ActivityUtility.startEditProfileActivity(requireActivity(), KEY_FRAGMENT_EDIT_PROFILE, (ArrayList<UserAnswer>) userAnswers);
//            }
            if (canInteractWithUser()) {
                ActivityUtility.startEditProfileActivity(requireActivity(), KEY_FRAGMENT_EDIT_PROFILE, (ArrayList<UserAnswer>) userAnswers);
            }
        } else if (v.getId() == R.id.btn_edit_media) {
//             if (!ParseUser.getCurrentUser().getBoolean(PARAM_IS_PAID_USER)) {
//                 showPaymentAlert("You need to pay first to edit your profile. Do you want to pay now?");
//            } else {
            ActivityUtility.startActivity(requireActivity(), KEY_FRAGMENT_UPLOAD_PHOTO_VIDEO_PROFILE);
//             }
        } else if (v.getId() == R.id.pic_1) {
            ArrayList<String> arrayList = new ArrayList<>();
            if (userProfilePhotoOnly != null && userProfilePhotoOnly.size() > 0) {
                for (int i = 0; i < userProfilePhotoOnly.size(); i++) {
                    arrayList.add(userProfilePhotoOnly.get(i).getFile().getUrl());
                }
                ActivityUtility.startPhotoViewActivity(requireActivity(),
                        KEY_FRAGMENT_PHOTO_VIEW,
                        arrayList,
                        0);
//                ActivityUtility.startPhotoViewActivity(requireActivity(),
//                        KEY_FRAGMENT_PHOTO_VIEW,
//                        (ArrayList<UserProfilePhotoVideo>) userProfilePhotoOnly,
//                        0);

            }
        } else if (v.getId() == R.id.pic2_card) {
            ArrayList<String> arrayList = new ArrayList<>();
            if (userProfilePhotoOnly != null && userProfilePhotoOnly.size() > 0) {
                for (int i = 0; i < userProfilePhotoOnly.size(); i++) {
                    arrayList.add(userProfilePhotoOnly.get(i).getFile().getUrl());
                }
                ActivityUtility.startPhotoViewActivity(requireActivity(), KEY_FRAGMENT_PHOTO_VIEW, arrayList, 1);
//                ActivityUtility.startPhotoViewActivity(requireActivity(), KEY_FRAGMENT_PHOTO_VIEW, (ArrayList<UserProfilePhotoVideo>) userProfilePhotoOnly, 1);
            }
        } else if (v.getId() == R.id.pic3_card) {
            ArrayList<String> arrayList = new ArrayList<>();
            if (userProfilePhotoOnly != null && userProfilePhotoOnly.size() > 0) {
                for (int i = 0; i < userProfilePhotoOnly.size(); i++) {
                    arrayList.add(userProfilePhotoOnly.get(i).getFile().getUrl());
                }
                ActivityUtility.startPhotoViewActivity(requireActivity(), KEY_FRAGMENT_PHOTO_VIEW, arrayList, 2);
//                ActivityUtility.startPhotoViewActivity(requireActivity(), KEY_FRAGMENT_PHOTO_VIEW, (ArrayList<UserProfilePhotoVideo>) userProfilePhotoOnly, 2);
            }
        } else if (v.getId() == R.id.video_card) {
            if (userProfileVideoOnly != null && userProfileVideoOnly.size() > 0) {
                ActivityUtility.startVideoViewActivity(requireActivity(), KEY_FRAGMENT_VIDEO_VIEW, userProfileVideoOnly.get(0).getFile().getUrl());
            }
        } else if (v.getId() == R.id.btn_may_be) {
//            if (ParseUser.getCurrentUser().getBoolean(PARAM_IS_PAID_USER) && ParseUser.getCurrentUser().getInt(PARAM_ACCOUNT_STATUS) == ACTIVE) {
//                showProgress();
//                if (maybeObject != null) {
//                    maybeObject.deleteInBackground(e -> {
//                        dismissProgress();
////                    String msg = parseUser.getString(PARAM_NICK) + " has been un-marked as your Maybe";
//                        String msg = "Updated successfully";
//                        showToast(getActivity(), getContext(), msg, SUCCESS);
//                        EventBus.getDefault().post(new RefreshFanList());
//                        maybeObject = null;
//                        setBottomButtons();
//                        setPushToUser(requireActivity(), requireContext(), parseUser.getObjectId(), "Wing Mate", NOTI_MSG_UN_MAYBE + ParseUser.getCurrentUser().getString(PARAM_NICK));
//                    });
//                } else {
//                    presenter.setFan(requireContext(), parseUser, FAN_TYPE_MAY_BE);
//                }
//            } else {
//                if (!ParseUser.getCurrentUser().getBoolean(PARAM_IS_PAID_USER)) {
//                    showPaymentAlert("You need to pay first to perform this action. Do you want to pay now?");
////                    showToast(getActivity(), getContext(), "You need to buy subscription first", ERROR);
//                } else
//                    showToast(getActivity(), getContext(), "Couldn't perform this action as your account is not active.", ERROR);
//            }

            if (canInteractWithUser()) {
                showProgress();
                if (maybeObject != null) {
                    maybeObject.deleteInBackground(e -> {
                        dismissProgress();
//                    String msg = parseUser.getString(PARAM_NICK) + " has been un-marked as your Maybe";
                        String msg = "Updated successfully";
//                        showToast(getActivity(), getContext(), msg, SUCCESS);
                        EventBus.getDefault().post(new RefreshFanList());
                        maybeObject = null;
                        setBottomButtons();
                        setPushToUser(requireActivity(), requireContext(), parseUser.getObjectId(), "Wing Mate", NOTI_MSG_UN_MAYBE + ParseUser.getCurrentUser().getString(PARAM_NICK));
                    });
                } else {
                    presenter.setFan(requireContext(), parseUser, FAN_TYPE_MAY_BE);
                }
            }
        } else if (v.getId() == R.id.btn_like) {
//            if (ParseUser.getCurrentUser().getBoolean(PARAM_IS_PAID_USER) && ParseUser.getCurrentUser().getInt(PARAM_ACCOUNT_STATUS) == ACTIVE) {
//                showProgress();
//                if (likeObject != null) {
//                    likeObject.deleteInBackground(e -> {
//                        dismissProgress();
//                        String msg = "Updated successfully";
//                        showToast(getActivity(), getContext(), msg, SUCCESS);
//                        EventBus.getDefault().post(new RefreshFanList());
//                        likeObject = null;
//                        setBottomButtons();
//                        setPushToUser(requireActivity(), requireContext(), parseUser.getObjectId(), "Wing Mate", ParseUser.getCurrentUser().getString(PARAM_NICK) + NOTI_MSG_UN_LIKED);
//
//                    });
//                } else {
//                    presenter.setFan(requireContext(), parseUser, FAN_TYPE_LIKE);
//                }
//            } else {
//                if (!ParseUser.getCurrentUser().getBoolean(PARAM_IS_PAID_USER)) {
//                    showPaymentAlert("You need to pay first to perform this action. Do you want to pay now?");
////                    showToast(getActivity(), getContext(), "You need to buy subscription first", ERROR);
//                } else
//                    showToast(getActivity(), getContext(), "Couldn't perform this action as your account is not active.", ERROR);
//            }

            if (canInteractWithUser()) {
                showProgress();
                if (likeObject != null) {
                    likeObject.deleteInBackground(e -> {
                        dismissProgress();
                        String msg = "Updated successfully";
//                        showToast(getActivity(), getContext(), msg, SUCCESS);
                        EventBus.getDefault().post(new RefreshFanList());
                        likeObject = null;
                        setBottomButtons();
                        setPushToUser(requireActivity(), requireContext(), parseUser.getObjectId(), "Wing Mate", ParseUser.getCurrentUser().getString(PARAM_NICK) + NOTI_MSG_UN_LIKED);

                    });
                } else {
                    presenter.setFan(requireContext(), parseUser, FAN_TYPE_LIKE);
                }
            }
        } else if (v.getId() == R.id.btn_crush) {
//            if (ParseUser.getCurrentUser().getBoolean(PARAM_IS_PAID_USER) && ParseUser.getCurrentUser().getInt(PARAM_ACCOUNT_STATUS) == ACTIVE) {
//                showProgress();
//                if (crushObject != null) {
//                    crushObject.deleteInBackground(e -> {
//                        dismissProgress();
////                    String msg = parseUser.getString(PARAM_NICK) + " has been un-marked as your Crush";
//                        String msg = "Updated successfully";
//                        showToast(getActivity(), getContext(), msg, SUCCESS);
//                        EventBus.getDefault().post(new RefreshFanList());
//                        crushObject = null;
//                        setBottomButtons();
//                        setPushToUser(requireActivity(), requireContext(), parseUser.getObjectId(), "Wing Mate", NOTI_MSG_UN_CRUSH + ParseUser.getCurrentUser().getString(PARAM_NICK));
//                    });
//                } else {
//                    presenter.setFan(requireContext(), parseUser, FAN_TYPE_CRUSH);
//                }
//            } else {
//                if (!ParseUser.getCurrentUser().getBoolean(PARAM_IS_PAID_USER)) {
//                    showPaymentAlert("You need to pay first to perform this action. Do you want to pay now?");
////                    showToast(getActivity(), getContext(), "You need to buy subscription first", ERROR);
//                } else
//                    showToast(getActivity(), getContext(), "Couldn't perform this action as your account is not active.", ERROR);
//            }

            if (canInteractWithUser()) {
                showProgress();
                if (crushObject != null) {
                    crushObject.deleteInBackground(e -> {
                        dismissProgress();
//                    String msg = parseUser.getString(PARAM_NICK) + " has been un-marked as your Crush";
                        String msg = "Updated successfully";
//                        showToast(getActivity(), getContext(), msg, SUCCESS);
                        EventBus.getDefault().post(new RefreshFanList());
                        crushObject = null;
                        setBottomButtons();
                        setPushToUser(requireActivity(), requireContext(), parseUser.getObjectId(), "Wing Mate", NOTI_MSG_UN_CRUSH + ParseUser.getCurrentUser().getString(PARAM_NICK));
                    });
                } else {
                    presenter.setFan(requireContext(), parseUser, FAN_TYPE_CRUSH);
                }
            }
        } else if (v.getId() == R.id.btn_msg) {
            if (canInteractWithUser()){
                String userId = parseUser.getObjectId();
                String name = parseUser.getString("nick");
                ActivityUtility.startChatActivity(requireActivity(),KEY_FRAGMENT_CHAT,userId,name);
            }
        } else if (v.getId() == R.id.btn_refresh) {
            showProgress();
            presenter.queryUserAnswers(getContext(), parseUser);
            presenter.queryUserPhotosVideo(getContext(), parseUser);
            if (!isCurrentUser) {
                maybeObject = null;
                crushObject = null;
                likeObject = null;
                presenter.queryUserFansStatus(requireContext(), parseUser);
            }
        }
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
//            this.userAnswers = userAnswers;
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

    @Override
    public void setUserFanStatusSuccess(List<Fans> fansList) {
        if (fansList != null && fansList.size() > 0) {
            for (int i = 0; i < fansList.size(); i++) {
                if (fansList.get(i).getFanType().equals(FAN_TYPE_CRUSH))
                    crushObject = fansList.get(i);
                else if (fansList.get(i).getFanType().equals(FAN_TYPE_LIKE))
                    likeObject = fansList.get(i);
                else if (fansList.get(i).getFanType().equals(FAN_TYPE_MAY_BE))
                    maybeObject = fansList.get(i);
            }
        }
        setBottomButtons();
    }

    @Override
    public void setFanAddedSuccess(Fans fan) {
        dismissProgress();
        switch (fan.getFanType()) {
            case FAN_TYPE_CRUSH:
                crushObject = fan;
                setPushToUser(requireActivity(), requireContext(), parseUser.getObjectId(), "Wing Mate", NOTI_MSG_CRUSH + ParseUser.getCurrentUser().getString(PARAM_NICK));
                break;
            case FAN_TYPE_LIKE:
                likeObject = fan;
                setPushToUser(requireActivity(), requireContext(), parseUser.getObjectId(), "Wing Mate", ParseUser.getCurrentUser().getString(PARAM_NICK) + NOTI_MSG_LIKED);
                break;
            case FAN_TYPE_MAY_BE:
                maybeObject = fan;
                setPushToUser(requireActivity(), requireContext(), parseUser.getObjectId(), "Wing Mate", NOTI_MSG_MAYBE + ParseUser.getCurrentUser().getString(PARAM_NICK));
                break;
        }
        String msg = "Updated successfully";
//        showToast(getActivity(), getContext(), msg, SUCCESS);
        EventBus.getDefault().post(new RefreshFanList());

        setBottomButtons();
    }

    private void setBottomButtons() {
        if (crushObject != null) {
            btnCrush.setAlpha(1.0f);
            btnCrush.setImageResource(R.drawable.profile_crush_filled);
        } else {
            btnCrush.setAlpha(1.0f);
            btnCrush.setImageResource(R.drawable.profile_crush);
        }

        if (likeObject != null) {
            btnLike.setAlpha(1.0f);
            btnLike.setImageResource(R.drawable.profile_like_filled);
        } else {
            btnLike.setAlpha(1.0f);
            btnLike.setImageResource(R.drawable.profile_like);
        }

        if (maybeObject != null) {
            btnMaybe.setAlpha(1.0f);
            btnMaybe.setImageResource(R.drawable.profile_maybe_filled);
        } else {
            btnMaybe.setAlpha(1.0f);
            btnMaybe.setImageResource(R.drawable.profile_maybe);
        }
    }

    private void setViews() {
        if (isCurrentUser) {
            matchView.setVisibility(View.INVISIBLE);
            distanceTV.setVisibility(View.GONE);
            editProfileView.setVisibility(View.VISIBLE);
        } else {
            distanceTV.setVisibility(View.VISIBLE);
            matchView.setVisibility(View.VISIBLE);
            editProfileView.setVisibility(View.GONE);
            int percent = Utilities.getMatchPercentage(parseUser);
            matchPercentTV.setText(percent + "%");
            matchProgress.setMax(100);
            matchProgress.setProgress(percent);
            distanceTV.setText(Utilities.getDistanceBetweenUser(parseUser));
        }
        if (parseUser != null) {
            nameTV.setText(parseUser.getString(AppConstants.PARAM_NICK));
//            String val = parseUser.getString(AppConstants.PARAM_ABOUT_ME);
//            String sourceString = "<b>" + "About Me: " + "</b> " + val;
//            aboutTV.setText(Html.fromHtml(sourceString));
        }
    }

    private void setAnswersInViews() {
        if (userAnswers != null && userAnswers.size() > 0) {
            System.out.println("==userAnswers==" + userAnswers.size());
            Collections.sort(userAnswers, (lhs, rhs) -> Integer.compare(lhs.getQuestionId().getProfileDisplayOrder(), rhs.getQuestionId().getProfileDisplayOrder()));

            UserAnswer aboutAnswer = new UserAnswer();
            aboutAnswer.setDummyUser(parseUser);
            userAnswers.add(aboutAnswer);

            adapter.setData(userAnswers);
            adapter.notifyDataSetChanged();
        }
    }

    private void setMediaInViews() {

        print("====in media===");

        pic2Card.setVisibility(View.GONE);
        pic3Card.setVisibility(View.GONE);
        videoCard.setVisibility(View.GONE);
        pic1.setImageResource(android.R.color.transparent);
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
//                    String profilePicUrl = "";
//                    if (isCurrentUser) {
//                        profilePicUrl = ParseUser.getCurrentUser().getString(PARAM_PROFILE_PIC);
//                    } else {
//                        profilePicUrl = parseUser.getString(PARAM_PROFILE_PIC);
//                    }
//                    if (profilePicUrl != null && profilePicUrl.equals(userProfilePhotoOnly.get(i).getFile().getUrl())) {
//                        Collections.swap(userProfilePhotoOnly, i, 0);
//                        break;
//                    }
//                }
//            }

            if (userProfilePhotoOnly.size() > 0) {
                Picasso.get().load(userProfilePhotoOnly.get(0).getFile().getUrl()).placeholder(R.drawable.image_placeholder).into(pic1);
            }
            if (userProfilePhotoOnly.size() > 1) {
                pic2Card.setVisibility(View.VISIBLE);
                Picasso.get().load(userProfilePhotoOnly.get(1).getFile().getUrl()).placeholder(R.drawable.image_placeholder).into(pic2);
            }
            if (userProfilePhotoOnly.size() > 2) {
                pic3Card.setVisibility(View.VISIBLE);
                Picasso.get().load(userProfilePhotoOnly.get(2).getFile().getUrl()).placeholder(R.drawable.image_placeholder).into(pic3);
            }
            if (userProfileVideoOnly.size() > 0) {
                videoCard.setVisibility(View.VISIBLE);
//                try {
//                    Bitmap thumb = null;
//                    try {
//                        int THUMBSIZE = 300;
//                        CancellationSignal ca = new CancellationSignal();
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                            System.out.println("===hereeee-====");
//                            thumb = ThumbnailUtils.createVideoThumbnail(userProfileVideoOnly.get(0).getFile().getFile(),
//                                    new Size(THUMBSIZE, THUMBSIZE), ca);
//                        } else {
//                            System.out.println("===hereeee2222-====");
//                            thumb = ThumbnailUtils.createVideoThumbnail(
//                                    userProfileVideoOnly.get(0).getFile().getFile().getPath(),
//                                    MediaStore.Images.Thumbnails.MINI_KIND);
//                        }
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//
//                    Matrix matrix = new Matrix();
//                    if (thumb != null) {
//                        System.out.println("===hereeee-33333====");
//                        Bitmap bmThumbnail = Bitmap.createBitmap(thumb, 0, 0, thumb.getWidth(), thumb.getHeight(), matrix, true);
//                        video1.setImageBitmap(bmThumbnail);
//                    }
//                } catch (ParseException exception) {
//                    exception.printStackTrace();
//                }


                System.out.println("====url===" + userProfileVideoOnly.get(0).getFile().getUrl());
                Glide.with(requireContext())
                        .load(userProfileVideoOnly.get(0).getFile().getUrl())
                        .thumbnail(Glide.with(requireContext()).load(userProfileVideoOnly.get(0).getFile().getUrl()).placeholder(R.drawable.video_placeholder1).apply(new RequestOptions().override(200, 200)))
                        .apply(new RequestOptions().override(200, 200))
//                        .placeholder(android.R.drawable.progress_indeterminate_horizontal)
                        .placeholder(R.drawable.video_placeholder1)
//                        .error(android.R.drawable.stat_notify_error)
                        .into(video1);

            }
        }
    }


    public class ThumbnailExtract extends AsyncTask<String, long[], Bitmap> {

        private final String videoUrl;
        private final ImageView mThumbnail;
        private final boolean mIsVideo;
        private MediaMetadataRetriever mmr;

        public ThumbnailExtract(String videoLocalUrl, ImageView thumbnail, boolean isVideo) {
            this.videoUrl = videoLocalUrl;
            mThumbnail = thumbnail;
            mIsVideo = isVideo;
            if (!isVideo) {
                mmr = new MediaMetadataRetriever();
            }
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            if (!mIsVideo) {
                return getBitmap(videoUrl);
            } else {
                return ThumbnailUtils.createVideoThumbnail(videoUrl,
                        MediaStore.Images.Thumbnails.MINI_KIND);
            }
        }

        @Override
        protected void onPostExecute(Bitmap thumb) {
            if (thumb != null) {
                mThumbnail.setImageBitmap(thumb);
            }
        }

        private Bitmap getBitmap(String fileUrl) {
            mmr.setDataSource(fileUrl);
            byte[] data = mmr.getEmbeddedPicture();
            Bitmap bitmap = null;
            // convert the byte array to a bitmap
            if (data != null) {
                bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

            }
            return bitmap;
//            return bitmap != null ? ScalingUtilities.createScaledBitmap(bitmap, 40, 40, ScalingUtilities.ScalingLogic.FIT) : bitmap;
        }
    }

    private void fetchUpdatedCurrentUser() {
        ParseUser.getCurrentUser().fetchInBackground((GetCallback<ParseUser>) (parseUser, e) -> {
            presenter.checkServerDate(getContext(), false, true);
        });
    }

    @Override
    public void setHasTrial(int days, boolean showLoader, boolean isJustRefresh) {

    }

    @Override
    public void setTrialEnded(String msg, boolean showLoader, boolean isJustRefresh) {
//        EventBus.getDefault().post(new RefreshDashboardView());
//        requireActivity().onBackPressed();

        isExpired = true;
        remainingDays = 0;

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
                    .setMessage(GO_TO_UPLOAD_SCREEN_AFTER_EXPIRED)
                    .setPositiveButton("OK", (dialoginterface, i) -> {
                        dialoginterface.cancel();
                        ActivityUtility.startProfileMediaActivity(requireActivity(), KEY_FRAGMENT_UPLOAD_PHOTO_VIDEO_PROFILE, true, isExpired);
                    })
                    .show();
        } else if (accountStatus == PENDING) {
//            EventBus.getDefault().post(new RefreshDashboardView());
//            requireActivity().onBackPressed();

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
                    .setMessage(GO_TO_PAYMENT_SCREEN_AFTER_EXPIRED)
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

    public void setLaunchCampaignStatus(boolean launchCampaignStatus) {
        isLaunchCampaignStatus = launchCampaignStatus;
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
}