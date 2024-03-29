package com.app.wingmate.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.wingmate.R;
import com.app.wingmate.base.BaseFragment;
import com.app.wingmate.base.BaseInteractor;
import com.app.wingmate.base.BasePresenter;
import com.app.wingmate.base.BaseView;
import com.app.wingmate.models.Question;
import com.app.wingmate.models.TermsConditions;
import com.app.wingmate.models.UserAnswer;
import com.app.wingmate.models.UserProfilePhotoVideo;
import com.app.wingmate.ui.activities.MainActivity;
import com.app.wingmate.ui.adapters.DosDontsListAdapter;
import com.app.wingmate.ui.adapters.DosDontsPhotosGridAdapter;
import com.app.wingmate.ui.adapters.PhotosHorizontalListAdapter;
import com.app.wingmate.utils.ActivityUtility;
import com.app.wingmate.utils.FileUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.MediaFile;
import pl.aprilapps.easyphotopicker.MediaSource;

import static com.app.wingmate.ui.fragments.CropFragment.REQUEST_CODE_CROP_IMAGE;
import static com.app.wingmate.utils.AlertMessages.FIRST_PHOTO_UPLOADED;
import static com.app.wingmate.utils.AlertMessages.PHOTO_UPLOADED;
import static com.app.wingmate.utils.AlertMessages.VIDEO_UPLOADED;
import static com.app.wingmate.utils.AppConstants.ACTIVE;
import static com.app.wingmate.utils.AppConstants.ERROR;
import static com.app.wingmate.utils.AppConstants.KEY_PHOTO;
import static com.app.wingmate.utils.AppConstants.KEY_PHOTO_TEXT;
import static com.app.wingmate.utils.AppConstants.KEY_VIDEO;
import static com.app.wingmate.utils.AppConstants.KEY_VIDEO_TEXT;
import static com.app.wingmate.utils.AppConstants.MANDATORY;
import static com.app.wingmate.utils.AppConstants.MODE_PHOTOS;
import static com.app.wingmate.utils.AppConstants.MODE_VIDEO;
import static com.app.wingmate.utils.AppConstants.PARAM_ACCOUNT_STATUS;
import static com.app.wingmate.utils.AppConstants.PARAM_FILE;
import static com.app.wingmate.utils.AppConstants.PARAM_FILE_STATUS;
import static com.app.wingmate.utils.AppConstants.PARAM_IS_MEDIA_APPROVED;
import static com.app.wingmate.utils.AppConstants.PARAM_IS_PHOTO;
import static com.app.wingmate.utils.AppConstants.PARAM_IS_PHOTO_SUBMITTED;
import static com.app.wingmate.utils.AppConstants.PARAM_IS_VIDEO_SUBMITTED;
import static com.app.wingmate.utils.AppConstants.PARAM_MANDATORY_QUESTIONNAIRE_FILLED;
import static com.app.wingmate.utils.AppConstants.PARAM_MEDIA_PENDING;
import static com.app.wingmate.utils.AppConstants.PARAM_PROFILE_PIC;
import static com.app.wingmate.utils.AppConstants.PARAM_USER_ID;
import static com.app.wingmate.utils.AppConstants.PENDING;
import static com.app.wingmate.utils.AppConstants.REJECTED;
import static com.app.wingmate.utils.AppConstants.SUCCESS;
import static com.app.wingmate.utils.CommonKeys.KEY_ACTIVITY_TAG;
import static com.app.wingmate.utils.CommonKeys.KEY_BACK_TAG;
import static com.app.wingmate.utils.CommonKeys.KEY_EXPIRE_TAG;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_ACCOUNT_PENDING;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_CROP;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_DASHBOARD;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_PAYMENT;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_PHOTO_VIEW;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_QUESTIONNAIRE;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_VIDEO_VIEW;
import static com.app.wingmate.utils.Utilities.showOkAlertDialog;
import static com.app.wingmate.utils.Utilities.showToast;

public class UploadPhotoVideoFragment extends BaseFragment implements BaseView {

    public static final String TAG = UploadPhotoVideoFragment.class.getName();

    Unbinder unbinder;

    @BindView(R.id.scrollView)
    NestedScrollView scrollView;
    @BindView(R.id.placeholder_tv)
    TextView placeholderTV;
    @BindView(R.id.photo_video_pic1)
    ImageView photoVideoPic1;
    @BindView(R.id.add_view)
    LinearLayout addView;
    @BindView(R.id.play_icon)
    ImageView playIcon;
    @BindView(R.id.mode_tag)
    TextView modeTagTV;
    @BindView(R.id.photos_rv)
    RecyclerView photosRV;
    @BindView(R.id.dos_rv)
    RecyclerView dosRV;
    @BindView(R.id.dos_photos_rv)
    RecyclerView dosPhotosRV;
    @BindView(R.id.del_main_img_btn)
    ImageView delMainImgBtn;
    @BindView(R.id.congrats_view)
    RelativeLayout congratsView;
    @BindView(R.id.status)
    TextView statusTV;

    public boolean hasChange = false;
    public int CURRENT_MODE = MODE_PHOTOS;
    private final int TAG_IMAGE = 100;
    private final int TAG_LIST = 101;
    private final int TAG_VIDEO = 102;
    private int CLICK_TAG = TAG_IMAGE;

    private List<TermsConditions> termsConditions;
    private List<TermsConditions> dosDontsPhotoTextsList;
    private List<TermsConditions> dosDontsVideoTextsList;
    private List<TermsConditions> dosDontsPhotoFilesList;
    private List<TermsConditions> dosDontsVideoFilesList;

    private List<UserProfilePhotoVideo> userProfilePhotoVideos;
    private List<UserProfilePhotoVideo> userProfilePhotoOnly;
    private List<UserProfilePhotoVideo> userProfileVideoOnly;
    private UserProfilePhotoVideo user1stPhotoFile;
    private UserProfilePhotoVideo user1stVideoFile;

    private DosDontsListAdapter dosDontsListAdapter;
    private DosDontsPhotosGridAdapter dosDontsPhotosGridAdapter;
    private PhotosHorizontalListAdapter userPhotosListAdapter;

    private GridLayoutManager photosRVLayoutManager;
    private GridLayoutManager dosRVLayoutManager;
    private GridLayoutManager dosPhotosRVLayoutManager;

    private boolean hasPhotos = false;
    private boolean hasVideo = false;

    private BasePresenter presenter;

    public boolean isClear = false;
    public boolean isExpired = false;

    public UploadPhotoVideoFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upload_photos_video, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        presenter = new BasePresenter(this, new BaseInteractor());

        isClear = requireActivity().getIntent().getBooleanExtra(KEY_BACK_TAG, false);
        isExpired = requireActivity().getIntent().getBooleanExtra(KEY_EXPIRE_TAG, false);

        CURRENT_MODE = MODE_PHOTOS;

        termsConditions = new ArrayList<>();

        dosDontsPhotoTextsList = new ArrayList<>();
        dosDontsVideoTextsList = new ArrayList<>();
        dosRVLayoutManager = new GridLayoutManager(getActivity(), 1);
        dosDontsListAdapter = new DosDontsListAdapter(getActivity(), dosDontsPhotoTextsList);
        dosRV.setLayoutManager(dosRVLayoutManager);
        dosRV.setAdapter(dosDontsListAdapter);

        dosDontsVideoFilesList = new ArrayList<>();
        dosDontsPhotoFilesList = new ArrayList<>();
        dosPhotosRVLayoutManager = new GridLayoutManager(getActivity(), 2);
        dosDontsPhotosGridAdapter = new DosDontsPhotosGridAdapter(getActivity(), dosDontsPhotoFilesList);
        dosPhotosRV.setLayoutManager(dosPhotosRVLayoutManager);
        dosPhotosRV.setAdapter(dosDontsPhotosGridAdapter);

        userProfilePhotoVideos = new ArrayList<>();
        userProfilePhotoOnly = new ArrayList<>();
        userProfileVideoOnly = new ArrayList<>();
        photosRVLayoutManager = new GridLayoutManager(getActivity(), 1);
        userPhotosListAdapter = new PhotosHorizontalListAdapter(getActivity(), this, userProfilePhotoOnly);
        photosRV.setAdapter(userPhotosListAdapter);

        congratsView.setVisibility(View.GONE);

        showProgress();
        presenter.queryTermsConditions(getContext());
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

    @OnClick({R.id.btn_congrats, R.id.del_main_img_btn, R.id.take_photo_video_btn, R.id.btn_continue, R.id.btn_back})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.del_main_img_btn:
                if (CURRENT_MODE == MODE_VIDEO) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(requireContext());
                    dialog
//                            .setTitle(getString(R.string.app_name))
//                            .setIcon(R.drawable.app_heart)
//                            .setCancelable(false)
//                            .setMessage("Removing video will make account pending. Are you sure you want to continue?")
                            .setMessage("Are you sure you want to delete this video?")
                            .setNegativeButton("No", (dialoginterface, i) -> {
                                dialoginterface.cancel();
                            })
                            .setPositiveButton("Yes", (dialoginterface, i) -> {
                                dialoginterface.cancel();
                                deleteMainImageVideo(CURRENT_MODE, false);
                            }).show();
                } else if (CURRENT_MODE == MODE_PHOTOS && (userProfilePhotoOnly == null || userProfilePhotoOnly.size() == 0 || userProfilePhotoOnly.size() == 1)) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(requireContext());
                    dialog
//                            .setTitle(getString(R.string.app_name))
//                            .setIcon(R.drawable.app_heart)
//                            .setCancelable(false)
//                            .setMessage("Removing all photos will make account pending. Are you sure you want to continue?")
                            .setMessage("Are you sure you want to delete this photo?")
                            .setNegativeButton("No", (dialoginterface, i) -> {
                                dialoginterface.cancel();
                            })
                            .setPositiveButton("Yes", (dialoginterface, i) -> {
                                dialoginterface.cancel();
                                deleteMainImageVideo(CURRENT_MODE, true);
                            }).show();
                } else {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(requireContext());
                    dialog
//                            .setCancelable(false)
                            .setMessage("Are you sure you want to delete this photo?")
                            .setNegativeButton("No", (dialoginterface, i) -> {
                                dialoginterface.cancel();
                            })
                            .setPositiveButton("Yes", (dialoginterface, i) -> {
                                dialoginterface.cancel();
                                deleteMainImageVideo(CURRENT_MODE, false);
                            }).show();
                }
                break;
            case R.id.take_photo_video_btn:
                if (CURRENT_MODE == MODE_PHOTOS) {
                    if (user1stPhotoFile != null) {
                        ArrayList<String> arrayList = new ArrayList<>();
                        arrayList.add(user1stPhotoFile.getFile().getUrl());
                        ActivityUtility.startPhotoViewActivity(requireActivity(), KEY_FRAGMENT_PHOTO_VIEW, arrayList, 0);
//                        ActivityUtility.startPhotoViewActivity(requireActivity(), KEY_FRAGMENT_PHOTO_VIEW, user1stPhotoFile.getFile().getUrl());
                    } else {
                        CLICK_TAG = TAG_IMAGE;
                        easyImage.openChooser(this);
                    }
                } else if (CURRENT_MODE == MODE_VIDEO) {
                    if (user1stVideoFile != null) {
                        ActivityUtility.startVideoViewActivity(requireActivity(), KEY_FRAGMENT_VIDEO_VIEW, user1stVideoFile.getFile().getUrl());
                    } else {
                        CLICK_TAG = TAG_VIDEO;
                        openVideoGallery();
//                        easyImage.openCameraForVideo(this);
                    }
                }
                break;
            case R.id.btn_continue:
                if (CURRENT_MODE == MODE_PHOTOS) {
//                    if (hasPhotos) {
//                        CURRENT_MODE = MODE_VIDEO;
//                        setVideoView();
//                    } else {
//                        showToast(getActivity(), getContext(), "Minimum 1 photo is required!", ERROR);
//                    }

                    if (isExpired) {
                        if (hasPhotos) {
                            CURRENT_MODE = MODE_VIDEO;
                            setVideoView();
                        } else {
                            showToast(requireActivity(), getContext(), "Upload at least 1 photo", ERROR);
                        }
                    } else {
                        if (hasPhotos) {
                            CURRENT_MODE = MODE_VIDEO;
                            setVideoView();
                        } else {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(requireContext());
                            dialog
//                                    .setTitle(getString(R.string.app_name))
//                                    .setIcon(R.drawable.app_heart)
                                    .setMessage("Minimum 1 photo is required, are you sure you want to continue without uploading it?")
                                    .setNegativeButton("No", (dialoginterface, i) -> {
                                        dialoginterface.cancel();
                                    })
                                    .setPositiveButton("Yes", (dialoginterface, i) -> {
                                        dialoginterface.cancel();
                                        CURRENT_MODE = MODE_VIDEO;
                                        setVideoView();
                                    }).show();
                        }
                    }

                } else if (CURRENT_MODE == MODE_VIDEO) {
//                    if (hasVideo) {
//                        CURRENT_MODE = MODE_PHOTOS;
//                        if (isClear) {
//                            ActivityUtility.startActivity(requireActivity(), KEY_FRAGMENT_DASHBOARD);
//                        } else {
//                            getActivity().onBackPressed();
//                        }
//                    } else {
//                        showToast(getActivity(), getContext(), "Video is required!", ERROR);
//                    }


                    if (isExpired) {
                        if (hasVideo) {
                            if (hasPhotos && hasChange) {
                                ((MainActivity) requireActivity()).hideTopView();
                                congratsView.setVisibility(View.VISIBLE);
                            } else {
                                if (isClear) {
                                    ActivityUtility.startActivity(requireActivity(), KEY_FRAGMENT_DASHBOARD);
                                } else {
                                    getActivity().onBackPressed();
                                }
                            }
                        } else {
                            showToast(requireActivity(), getContext(), "Upload video to continue", ERROR);
                        }
                    } else {
                        if (hasVideo) {
                            if (hasPhotos && hasChange) {
                                ((MainActivity) requireActivity()).hideTopView();
                                congratsView.setVisibility(View.VISIBLE);
                            } else {
                                if (isClear) {
                                    ActivityUtility.startActivity(requireActivity(), KEY_FRAGMENT_DASHBOARD);
                                } else {
                                    getActivity().onBackPressed();
                                }
                            }
                        } else {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(requireContext());
                            dialog
//                                    .setTitle(getString(R.string.app_name))
//                                    .setIcon(R.drawable.app_heart)
                                    .setMessage("Video is required, are you sure you want to continue without uploading it?")
                                    .setNegativeButton("No", (dialoginterface, i) -> {
                                        dialoginterface.cancel();
                                    })
                                    .setPositiveButton("Yes", (dialoginterface, i) -> {
                                        dialoginterface.cancel();
                                        if (isClear) {
                                            ActivityUtility.startActivity(requireActivity(), KEY_FRAGMENT_DASHBOARD);
                                        } else {
                                            getActivity().onBackPressed();
                                        }
                                    }).show();
                        }
                    }

                }
                break;
            case R.id.btn_back:
                getActivity().onBackPressed();
                break;
            case R.id.btn_congrats:
//                if (isClear) {
                if (isExpired)
                    ActivityUtility.startActivity(requireActivity(), KEY_FRAGMENT_ACCOUNT_PENDING);
                else
                    ActivityUtility.startActivity(requireActivity(), KEY_FRAGMENT_DASHBOARD);
//                } else {
//                    getActivity().onBackPressed();
//                }
                break;
            default:
                break;
        }
    }

    public boolean canBack() {
        if (isClear) {
            return false;
        } else {
            return true;
        }
    }

    public void openVideoCamera() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, MAXIMUM_DURATION_VIDEO);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
        startActivityForResult(intent, VIDEO_CAMERA);
    }

    private int VIDEO_CAMERA = 2;
    private int VIDEO_GALLERY = 3;
    private File videoFile = null;
    private String videoPath = null;
    private Uri videoURI = null;
    public static int MAXIMUM_DURATION_VIDEO = 20;

    public void openVideoGallery() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, MAXIMUM_DURATION_VIDEO);
        startActivityForResult(Intent.createChooser(intent, "Select Video"), VIDEO_GALLERY);
    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    //this function returns null when using IO file manager
    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Video.Media.DATA};
        Cursor cursor = requireActivity().getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }

    private byte[] convertVideoToBytes(Uri uri) {
        byte[] videoBytes = null;
        if (uri == null) return null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            String realPathFromURI = getRealPathFromURI(uri);
            String realPathFromURI = getPath(uri);
            if (realPathFromURI == null) return null;
            FileInputStream fis = new FileInputStream(new File(realPathFromURI));
            byte[] buf = new byte[1024];
            int n;
            while (-1 != (n = fis.read(buf)))
                baos.write(buf, 0, n);
            videoBytes = baos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return videoBytes;
    }


    @Override
    public void setTermsResponseSuccess(List<TermsConditions> list) {
        termsConditions = new ArrayList<>();
        if (list != null && list.size() > 0) {
            termsConditions = list;
        }
        dosDontsPhotoTextsList = new ArrayList<>();
        dosDontsPhotoFilesList = new ArrayList<>();
        dosDontsVideoTextsList = new ArrayList<>();
        dosDontsVideoFilesList = new ArrayList<>();
        for (int i = 0; i < termsConditions.size(); i++) {
            String termType = termsConditions.get(i).getTermsType();
            switch (termType) {
                case KEY_PHOTO_TEXT:
                    dosDontsPhotoTextsList.add(termsConditions.get(i));
                    break;
                case KEY_VIDEO_TEXT:
                    dosDontsVideoTextsList.add(termsConditions.get(i));
                    break;
                case KEY_PHOTO:
                    dosDontsPhotoFilesList.add(termsConditions.get(i));
                    break;
                case KEY_VIDEO:
                    dosDontsVideoFilesList.add(termsConditions.get(i));
                    break;
                default:
                    break;
            }
        }

        switch (CURRENT_MODE) {
            case MODE_PHOTOS:
                setPhotosView();
                break;
            case MODE_VIDEO:
                setVideoView();
                break;
            default:
                break;
        }
        dosDontsListAdapter.notifyDataSetChanged();
        dosDontsPhotosGridAdapter.notifyDataSetChanged();

        presenter.queryUserPhotosVideo(getContext(), ParseUser.getCurrentUser());
    }

    @Override
    public void setQuestionsResponseSuccess(List<Question> questions) {

    }

    @Override
    public void setUserAnswersResponseSuccess(List<UserAnswer> userAnswers) {

    }

    @Override
    public void setUserPhotosVideoResponseSuccess(List<UserProfilePhotoVideo> userProfilePhotoVideos) {
        dismissProgress();
        this.userProfilePhotoVideos = new ArrayList<>();
        if (userProfilePhotoVideos != null && userProfilePhotoVideos.size() > 0)
            this.userProfilePhotoVideos = userProfilePhotoVideos;
        setMediaInViews();
    }

    private boolean isMediaPending() {
        boolean isPending = false;
        if (userProfilePhotoOnly != null && userProfilePhotoOnly.size() > 0) {
            for (UserProfilePhotoVideo userProfilePhotoVideo : userProfilePhotoOnly) {
                if (!userProfilePhotoVideo.isDummyFile() && userProfilePhotoVideo.getInt(PARAM_FILE_STATUS) == PENDING) {
                    System.out.println("====pending photo=");
                    isPending = true;
                    break;
                }
            }
        }
        if (userProfileVideoOnly != null && userProfileVideoOnly.size() > 0) {
            for (UserProfilePhotoVideo userProfilePhotoVideo : userProfileVideoOnly) {
                if (!userProfilePhotoVideo.isDummyFile() && userProfilePhotoVideo.getInt(PARAM_FILE_STATUS) == PENDING) {
                    System.out.println("====pending video=");
                    isPending = true;
                    break;
                }
            }
        }
        return isPending;
    }

    private void setMediaInViews() {
        userProfilePhotoOnly = new ArrayList<>();
        userProfileVideoOnly = new ArrayList<>();
        user1stPhotoFile = null;
        user1stVideoFile = null;
        if (userProfilePhotoVideos != null && userProfilePhotoVideos.size() > 0) {
            for (UserProfilePhotoVideo userProfilePhotoVideo : userProfilePhotoVideos) {
                if (userProfilePhotoVideo.isPhoto())
                    userProfilePhotoOnly.add(userProfilePhotoVideo);
                else userProfileVideoOnly.add(userProfilePhotoVideo);
            }
            if (userProfilePhotoOnly != null && userProfilePhotoOnly.size() > 0) {
//                for (int i = 0; i < userProfilePhotoOnly.size(); i++) {
//                    String profilePicUrl = ParseUser.getCurrentUser().getString(PARAM_PROFILE_PIC);
//                    System.out.println(profilePicUrl + "==match==" + userProfilePhotoOnly.get(i).getFile().getUrl());
//                    if (profilePicUrl != null && profilePicUrl.equals(userProfilePhotoOnly.get(i).getFile().getUrl())) {
//                        user1stPhotoFile = userProfilePhotoOnly.get(i);
//                        userProfilePhotoOnly.remove(i);
//                        break;
//                    }
//                }
                if (user1stPhotoFile == null) {
                    user1stPhotoFile = userProfilePhotoOnly.get(0);
                    userProfilePhotoOnly.remove(0);
                }
                if (user1stPhotoFile != null && userProfilePhotoOnly != null && userProfilePhotoOnly.size() < 2) {
                    UserProfilePhotoVideo dummyObj = new UserProfilePhotoVideo();
                    dummyObj.setDummyFile(true);
                    userProfilePhotoOnly.add(dummyObj);
                }
            }

            if (userProfileVideoOnly != null && userProfileVideoOnly.size() > 0) {
                user1stVideoFile = userProfileVideoOnly.get(0);
            }
        }
        switch (CURRENT_MODE) {
            case MODE_PHOTOS:
                setPhotosView();
                break;
            case MODE_VIDEO:
                setVideoView();
                break;
            default:
                break;
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void setPhotosView() {
        ((MainActivity) requireActivity()).setScreenTitle("Upload Photos");

        photoVideoPic1.setImageResource(android.R.color.transparent);
        CURRENT_MODE = MODE_PHOTOS;
        placeholderTV.setText("Minimum 1 photo is required");
        photosRV.setVisibility(View.VISIBLE);
        modeTagTV.setText("Photos");
        playIcon.setVisibility(View.GONE);
        dosDontsListAdapter.setData(dosDontsPhotoTextsList);
        dosDontsPhotosGridAdapter.setData(dosDontsPhotoFilesList);
        if (user1stPhotoFile != null) {
            Picasso.get().load(user1stPhotoFile.getFile().getUrl()).placeholder(R.drawable.image_placeholder).into(photoVideoPic1);
            addView.setVisibility(View.GONE);
            delMainImgBtn.setVisibility(View.VISIBLE);
            hasPhotos = true;

            System.out.println("====" + user1stPhotoFile.getInt(PARAM_FILE_STATUS));
            if (user1stPhotoFile.getInt(PARAM_FILE_STATUS) == ACTIVE) {
                statusTV.setVisibility(View.GONE);
            } else if (user1stPhotoFile.getInt(PARAM_FILE_STATUS) == PENDING) {
                statusTV.setVisibility(View.VISIBLE);
                statusTV.setText("Pending");
                statusTV.setBackground(requireActivity().getResources().getDrawable(R.drawable.bg_status_pending));
            } else if (user1stPhotoFile.getInt(PARAM_FILE_STATUS) == REJECTED) {
                statusTV.setVisibility(View.VISIBLE);
                statusTV.setText("Rejected");
                statusTV.setBackground(requireActivity().getResources().getDrawable(R.drawable.bg_status_rejected));
            }
        } else {
            addView.setVisibility(View.VISIBLE);
            delMainImgBtn.setVisibility(View.GONE);
            photoVideoPic1.setImageResource(android.R.color.transparent);
            statusTV.setVisibility(View.GONE);
        }
        if (userProfilePhotoOnly != null && userProfilePhotoOnly.size() > 0) {
            photosRV.setVisibility(View.VISIBLE);
            hasPhotos = true;
        } else {
            userProfilePhotoOnly = new ArrayList<>();
            photosRV.setVisibility(View.GONE);
        }
        userPhotosListAdapter.setData(userProfilePhotoOnly);
        userPhotosListAdapter.notifyDataSetChanged();

        ((MainActivity) getActivity()).showStepView();
        ((MainActivity) getActivity()).setStepViewStepTVAndProgress(1, 2);
        scrollView.post(() -> scrollView.fullScroll(ScrollView.FOCUS_UP));
    }

    public void setVideoView() {
        ((MainActivity) requireActivity()).setScreenTitle("Upload Video");

        photoVideoPic1.setImageResource(android.R.color.transparent);
        CURRENT_MODE = MODE_VIDEO;
        placeholderTV.setText("Video is required. Max. video length is 20 seconds");
        modeTagTV.setText("Video");
        playIcon.setVisibility(View.GONE);
        dosDontsListAdapter.setData(dosDontsVideoTextsList);
        dosDontsPhotosGridAdapter.setData(dosDontsVideoFilesList);
        if (user1stVideoFile != null) {
            try {
                setVideoImage(user1stVideoFile.getFile().getFile().getPath(), user1stVideoFile.getFile().getUrl());
            } catch (ParseException e) {
                e.printStackTrace();
                photoVideoPic1.setImageResource(android.R.color.transparent);
            }
            addView.setVisibility(View.GONE);
            playIcon.setVisibility(View.VISIBLE);
            delMainImgBtn.setVisibility(View.VISIBLE);
            hasVideo = true;
            if (user1stVideoFile.getInt(PARAM_FILE_STATUS) == ACTIVE) {
                statusTV.setVisibility(View.GONE);
            } else if (user1stVideoFile.getInt(PARAM_FILE_STATUS) == PENDING) {
                statusTV.setVisibility(View.VISIBLE);
                statusTV.setText("Pending");
                statusTV.setBackground(requireActivity().getResources().getDrawable(R.drawable.bg_status_pending));
            } else if (user1stVideoFile.getInt(PARAM_FILE_STATUS) == REJECTED) {
                statusTV.setVisibility(View.VISIBLE);
                statusTV.setText("Rejected");
                statusTV.setBackground(requireActivity().getResources().getDrawable(R.drawable.bg_status_rejected));
            }
        } else {
            addView.setVisibility(View.VISIBLE);
            delMainImgBtn.setVisibility(View.GONE);
            photoVideoPic1.setImageResource(android.R.color.transparent);
            hasVideo = false;
            statusTV.setVisibility(View.GONE);
        }
        photosRV.setVisibility(View.GONE);
//        if (userProfileVideoOnly != null && userProfileVideoOnly.size() > 0) {
//            photosRV.setVisibility(View.VISIBLE);
//        } else {
//            photosRV.setVisibility(View.GONE);
//            userProfileVideoOnly = new ArrayList<>();
//        }
        ((MainActivity) getActivity()).showStepView();
        ((MainActivity) getActivity()).setStepViewStepTVAndProgress(2, 2);
        scrollView.post(() -> scrollView.fullScroll(ScrollView.FOCUS_UP));
    }

    private void setVideoImage(String videoFilePath, String url) {
//        try {
//            Bitmap thumb = null;
//            try {
//                int THUMBSIZE = 200;
//                CancellationSignal ca = new CancellationSignal();
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                    System.out.println("===hereeee-====");
//                    thumb = ThumbnailUtils.createVideoThumbnail(userProfileVideoOnly.get(0).getFile().getFile(),
//                            new Size(THUMBSIZE, THUMBSIZE), ca);
//                } else {
//                    System.out.println("===hereeee222-====");
//
//                    thumb = ThumbnailUtils.createVideoThumbnail(
//                            userProfileVideoOnly.get(0).getFile().getFile().getPath(),
//                            MediaStore.Images.Thumbnails.MINI_KIND);
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            Matrix matrix = new Matrix();
//            if (thumb != null) {
//                System.out.println("===hereeee33333-====");
//                Bitmap bmThumbnail = Bitmap.createBitmap(thumb, 0, 0, thumb.getWidth(), thumb.getHeight(), matrix, true);
//                photoVideoPic1.setImageBitmap(bmThumbnail);
//            }
//        } catch (ParseException exception) {
//            exception.printStackTrace();
//        }

        Glide.with(requireContext())
                .load(url)
                .thumbnail(Glide.with(requireContext()).load(url).placeholder(R.drawable.video_placeholder1).apply(new RequestOptions().override(600, 600)))
                .apply(new RequestOptions().override(600, 600))
                .placeholder(R.drawable.video_placeholder1)
//                .placeholder(android.R.drawable.progress_indeterminate_horizontal)
//                .error(android.R.drawable.stat_notify_error)
                .into(photoVideoPic1);

    }

    public void addImage() {
        if (CURRENT_MODE == MODE_PHOTOS) {
            CLICK_TAG = TAG_LIST;
            easyImage.openChooser(this);
        }
    }

    public void deleteImage(int index) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(requireContext());
        dialog
                .setMessage("Are you sure you want to delete this photo?")
                .setNegativeButton("No", (dialoginterface, i) -> dialoginterface.cancel())
                .setPositiveButton("Yes", (dialoginterface, i) -> {
                    if (CURRENT_MODE == MODE_PHOTOS) {
                        CLICK_TAG = TAG_LIST;
//            userProfilePhotoOnly.get(index).deleteEventually();
                        showProgress();
                        userProfilePhotoOnly.get(index).deleteInBackground(e -> {
                            if (e == null) {
                                userProfilePhotoOnly.remove(index);
                                userPhotosListAdapter.setData(userProfilePhotoOnly);
                                userPhotosListAdapter.notifyDataSetChanged();

                                boolean hasDummy = false;
                                if (userProfilePhotoOnly == null || userProfilePhotoOnly.size() == 0)
                                    userProfilePhotoOnly = new ArrayList<>();
                                for (int x = 0; x < userProfilePhotoOnly.size(); x++) {
                                    if (userProfilePhotoOnly.get(x).isDummyFile())
                                        hasDummy = true;
                                }
                                if (!hasDummy) {
                                    UserProfilePhotoVideo dummyObj = new UserProfilePhotoVideo();
                                    dummyObj.setDummyFile(true);
                                    userProfilePhotoOnly.add(dummyObj);
                                }
                                hasChange = true;

                                System.out.println("====pending ="+isMediaPending());
                                ParseUser.getCurrentUser().put(PARAM_MEDIA_PENDING, isMediaPending());
                                ParseUser.getCurrentUser().saveInBackground(e12 -> {
                                });

                            } else {
                                showToast(requireActivity(), getContext(), e.getMessage(), ERROR);
                            }

                            dismissProgress();
                        });
                    }
                }).show();
    }

    public void deleteMainImageVideo(int mode, boolean isLastPhoto) {
//        AlertDialog.Builder dialog = new AlertDialog.Builder(requireContext());
//        dialog.setTitle("Alert")
//                .setIcon(R.drawable.small_red_cross)
//                .setMessage("Are you sure to delete?")
//                .setNegativeButton("Cancel", (dialoginterface, i) -> dialoginterface.cancel())
//                .setPositiveButton("Yes, Delete", (dialoginterface, i) -> {
        if (mode == MODE_PHOTOS) {
            if (user1stPhotoFile != null) {
//                user1stPhotoFile.deleteEventually();
                showProgress();
                user1stPhotoFile.deleteInBackground(e -> {
                    if (e == null) {
                        user1stPhotoFile = null;
//                        ParseUser.getCurrentUser().put(PARAM_PROFILE_PIC, "");
//                        ParseUser.getCurrentUser().saveInBackground(e1 -> {
//                        });
                        if (userProfilePhotoOnly != null && userProfilePhotoOnly.size() > 1) {
                            user1stPhotoFile = userProfilePhotoOnly.get(0);
                            userProfilePhotoOnly.remove(0);
//                            ParseUser.getCurrentUser().put(PARAM_PROFILE_PIC, user1stPhotoFile.getFile().getUrl());
//                            ParseUser.getCurrentUser().saveInBackground(e1 -> {
//                            });
                            boolean hasDummy = false;
                            for (int x = 0; x < userProfilePhotoOnly.size(); x++) {
                                if (userProfilePhotoOnly.get(x).isDummyFile())
                                    hasDummy = true;
                            }
                            if (!hasDummy) {
                                UserProfilePhotoVideo dummyObj = new UserProfilePhotoVideo();
                                dummyObj.setDummyFile(true);
                                userProfilePhotoOnly.add(dummyObj);
                            }
                        }
                        if (user1stPhotoFile != null) {
                            Picasso.get().load(user1stPhotoFile.getFile().getUrl()).placeholder(R.drawable.image_placeholder).into(photoVideoPic1);
                            addView.setVisibility(View.GONE);
                            delMainImgBtn.setVisibility(View.VISIBLE);
                            hasPhotos = true;
                            if (user1stPhotoFile.getInt(PARAM_FILE_STATUS) == ACTIVE) {
                                statusTV.setVisibility(View.GONE);
                            } else if (user1stPhotoFile.getInt(PARAM_FILE_STATUS) == PENDING) {
                                statusTV.setVisibility(View.VISIBLE);
                                statusTV.setText("Pending");
                                statusTV.setBackground(requireActivity().getResources().getDrawable(R.drawable.bg_status_pending));
                            } else if (user1stPhotoFile.getInt(PARAM_FILE_STATUS) == REJECTED) {
                                statusTV.setVisibility(View.VISIBLE);
                                statusTV.setText("Rejected");
                                statusTV.setBackground(requireActivity().getResources().getDrawable(R.drawable.bg_status_rejected));
                            }
                        } else {
                            userProfilePhotoOnly = new ArrayList<>();
                            addView.setVisibility(View.VISIBLE);
                            delMainImgBtn.setVisibility(View.GONE);
                            photoVideoPic1.setImageResource(android.R.color.transparent);
                            hasPhotos = false;
                            statusTV.setVisibility(View.GONE);
                        }
                        if (userProfilePhotoOnly != null && userProfilePhotoOnly.size() > 0) {
                            photosRV.setVisibility(View.VISIBLE);
                            hasPhotos = true;
                            photosRV.smoothScrollToPosition(userProfilePhotoOnly.size() - 1);
                        } else {
                            userProfilePhotoOnly = new ArrayList<>();
                            photosRV.setVisibility(View.GONE);
                            if (isLastPhoto) {
                                ParseUser.getCurrentUser().put(PARAM_ACCOUNT_STATUS, PENDING);
                                ParseUser.getCurrentUser().put(PARAM_IS_MEDIA_APPROVED, false);
                                ParseUser.getCurrentUser().put(PARAM_IS_PHOTO_SUBMITTED, false);
                                ParseUser.getCurrentUser().put(PARAM_MEDIA_PENDING, true);
                                ParseUser.getCurrentUser().saveInBackground(e12 -> {

                                });
                            }
                        }
                        userPhotosListAdapter.setData(userProfilePhotoOnly);
                        userPhotosListAdapter.notifyDataSetChanged();
                    } else {
                        showToast(requireActivity(), getContext(), e.getMessage(), ERROR);
                    }
                    dismissProgress();
                });

            }
        } else if (mode == MODE_VIDEO) {
            if (user1stVideoFile != null) {
//                user1stVideoFile.deleteEventually();
                showProgress();
                user1stVideoFile.deleteInBackground(new DeleteCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            user1stVideoFile = null;
                            photoVideoPic1.setImageResource(android.R.color.transparent);
                            hasVideo = false;
                            addView.setVisibility(View.VISIBLE);
                            playIcon.setVisibility(View.GONE);
                            delMainImgBtn.setVisibility(View.GONE);
                            statusTV.setVisibility(View.GONE);
                            hasChange = true;
                            ParseUser.getCurrentUser().put(PARAM_ACCOUNT_STATUS, PENDING);
                            ParseUser.getCurrentUser().put(PARAM_IS_MEDIA_APPROVED, false);
                            ParseUser.getCurrentUser().put(PARAM_IS_VIDEO_SUBMITTED, false);
                            ParseUser.getCurrentUser().put(PARAM_MEDIA_PENDING, true);
                            ParseUser.getCurrentUser().saveInBackground(e12 -> {
                            });
                        } else {
                            showToast(requireActivity(), getContext(), e.getMessage(), ERROR);
                        }
                        dismissProgress();
                    }
                });
            }

        }
        hasChange = true;
//        System.out.println("====pending ="+isMediaPending());
//        ParseUser.getCurrentUser().put(PARAM_MEDIA_PENDING, isMediaPending());
//        ParseUser.getCurrentUser().saveInBackground(e12 -> {
//        });
//                }).show();
    }

    private void cropImage(File file) {
        Intent intent = new Intent(requireContext(), MainActivity.class);
        intent.putExtra(KEY_ACTIVITY_TAG, KEY_FRAGMENT_CROP);
        intent.putExtra(CropFragment.IMAGE_PATH, file.getPath());
        startActivityForResult(intent, REQUEST_CODE_CROP_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == VIDEO_CAMERA || requestCode == VIDEO_GALLERY) {
            if (data != null) {
                videoURI = data.getData();
//                videoPath = getRealPathFromURI(videoURI);
                videoPath = FileUtils.getPath(requireContext(), videoURI);
                MediaPlayer mp = MediaPlayer.create(getContext(), videoURI);
                int videoDuration = mp.getDuration();
                mp.release();
                Log.i("log-", "VIDEO PATH: " + videoPath);
                Log.i("log-", "VIDEO URI: " + videoURI);
                Log.i("log-", "VIDEO DURATION: " + videoDuration);
                if (videoDuration <= MAXIMUM_DURATION_VIDEO * 1100) {
//                    Bitmap thumbnail = null;
                    videoFile = null;
                    if (videoPath != null) {
                        videoFile = new File(videoPath);
//                        thumbnail = ThumbnailUtils.createVideoThumbnail(videoPath, MediaStore.Images.Thumbnails.MINI_KIND);
                    } else if (videoURI != null) {
                        videoFile = new File(videoURI.getPath());
//                        thumbnail = ThumbnailUtils.createVideoThumbnail(videoPath, MediaStore.Images.Thumbnails.MINI_KIND);
                    }
                    if (videoFile != null) {
                        Log.i("log-", "VIDEO FILE: " + videoFile.getAbsolutePath());

                        showProgress();
                        ParseFile vidFile = new ParseFile(videoFile);
//                        ParseFile vidFile = new ParseFile("video.mp4", convertVideoToBytes(videoURI));
//                        vidFile.saveInBackground((SaveCallback) e -> {
//                            if(null == e) {
                        final UserProfilePhotoVideo userProfilePhotoVideo = new UserProfilePhotoVideo();
                        userProfilePhotoVideo.put(PARAM_USER_ID, ParseUser.getCurrentUser().getObjectId());
                        userProfilePhotoVideo.put(PARAM_FILE, vidFile);
                        userProfilePhotoVideo.put(PARAM_IS_PHOTO, false);
                        userProfilePhotoVideo.put(PARAM_FILE_STATUS, 0);
                        userProfilePhotoVideo.saveInBackground(ea -> {
                            if (ea == null) {
                                if (user1stVideoFile != null) {
                                    user1stVideoFile.deleteInBackground(e12 -> {
                                        if (e12 == null) {
                                            user1stVideoFile = userProfilePhotoVideo;
                                            try {
                                                setVideoImage(user1stVideoFile.getFile().getFile().getPath(), user1stVideoFile.getFile().getUrl());
                                            } catch (ParseException exception) {
                                                exception.printStackTrace();
                                            }
                                            hasVideo = true;
                                            addView.setVisibility(View.GONE);
                                            delMainImgBtn.setVisibility(View.VISIBLE);
                                            playIcon.setVisibility(View.VISIBLE);
                                            hasChange = true;
//                                            showToast(requireActivity(), getContext(), "Updated successfully", SUCCESS);
                                            showOkAlertDialog(requireContext(), VIDEO_UPLOADED);
                                            statusTV.setVisibility(View.VISIBLE);
                                            statusTV.setText("Pending");
                                            statusTV.setBackground(requireActivity().getResources().getDrawable(R.drawable.bg_status_pending));
                                        } else {
                                            showToast(requireActivity(), getContext(), e12.getMessage(), ERROR);
                                            System.out.println("====e12===" + e12.getMessage());
                                            statusTV.setVisibility(View.GONE);
                                        }
                                        dismissProgress();
                                    });
                                } else {
                                    user1stVideoFile = userProfilePhotoVideo;
                                    try {
                                        setVideoImage(user1stVideoFile.getFile().getFile().getPath(), user1stVideoFile.getFile().getUrl());
                                    } catch (ParseException exception) {
                                        exception.printStackTrace();
                                    }
                                    hasVideo = true;
                                    addView.setVisibility(View.GONE);
                                    delMainImgBtn.setVisibility(View.VISIBLE);
                                    playIcon.setVisibility(View.VISIBLE);
                                    hasChange = true;
//                                    showToast(requireActivity(), getContext(), "Updated successfully", SUCCESS);
                                    showOkAlertDialog(requireContext(), VIDEO_UPLOADED);
                                    dismissProgress();
                                    statusTV.setVisibility(View.VISIBLE);
                                    statusTV.setText("Pending");
                                    statusTV.setBackground(requireActivity().getResources().getDrawable(R.drawable.bg_status_pending));
                                }
                                ParseUser.getCurrentUser().put(PARAM_IS_VIDEO_SUBMITTED, true);
                                ParseUser.getCurrentUser().put(PARAM_MEDIA_PENDING, true);
                                ParseUser.getCurrentUser().saveInBackground(e12 -> {
                                });
                            } else {
                                showToast(requireActivity(), getContext(), ea.getMessage(), ERROR);
                                System.out.println("==ea=====" + ea.getMessage());
                                dismissProgress();
                            }
                        });
//                            } else {
//                                showToast(requireActivity(), getContext(), e.getMessage(), ERROR);
//                                System.out.println("==e====="+e.getMessage());
//                                dismissProgress();
//                            }
//                        });
                    }
                } else {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(requireContext());
                    dialog.setMessage("Your video is longer than " + MAXIMUM_DURATION_VIDEO + " seconds. Please choose or take a shorter video")
                            .setPositiveButton("OK", (dialoginterface, i) -> {
                                dialoginterface.cancel();
                            }).show();
//                    showToast(requireActivity(), getContext(), "Your video is longer than " + MAXIMUM_DURATION_VIDEO + " seconds. Please choose or take a shorter video", ERROR);
                    videoPath = null;
                    videoURI = null;
                    videoFile = null;
                }
            }
        }

        easyImage.handleActivityResult(requestCode, resultCode, data, getActivity(), new DefaultCallback() {
            @Override
            public void onMediaFilesPicked(MediaFile[] imageFiles, MediaSource source) {
                if (CURRENT_MODE == MODE_PHOTOS) {
                    cropImage(imageFiles[0].getFile());
                } else if (CURRENT_MODE == MODE_VIDEO) {
                    final UserProfilePhotoVideo userProfilePhotoVideo = new UserProfilePhotoVideo();
                    userProfilePhotoVideo.put(PARAM_USER_ID, ParseUser.getCurrentUser().getObjectId());
                    userProfilePhotoVideo.put(PARAM_FILE, new ParseFile(imageFiles[0].getFile()));
                    userProfilePhotoVideo.put(PARAM_IS_PHOTO, false);
                    userProfilePhotoVideo.put(PARAM_FILE_STATUS, 0);
                    showProgress();
                    userProfilePhotoVideo.saveInBackground(e -> {
                        if (e == null) {
                            if (user1stVideoFile != null) {
//                                user1stVideoFile.deleteEventually();
                                user1stVideoFile.deleteInBackground(e12 -> {
                                    if (e12 == null) {
                                        user1stVideoFile = userProfilePhotoVideo;
                                        try {
                                            setVideoImage(user1stVideoFile.getFile().getFile().getPath(), user1stVideoFile.getFile().getUrl());
                                        } catch (ParseException exception) {
                                            exception.printStackTrace();
                                        }
                                        hasVideo = true;
                                        addView.setVisibility(View.GONE);
                                        delMainImgBtn.setVisibility(View.VISIBLE);
                                        playIcon.setVisibility(View.VISIBLE);
                                        hasChange = true;
//                                        showToast(requireActivity(), getContext(), "Updated successfully", SUCCESS);
                                        showOkAlertDialog(requireContext(), VIDEO_UPLOADED);
                                        statusTV.setVisibility(View.VISIBLE);
                                        statusTV.setText("Pending");
                                        statusTV.setBackground(requireActivity().getResources().getDrawable(R.drawable.bg_status_pending));
                                    } else {
                                        showToast(requireActivity(), getContext(), e12.getMessage(), ERROR);
                                        statusTV.setVisibility(View.GONE);
                                    }
                                    dismissProgress();
                                });
                            } else {
                                user1stVideoFile = userProfilePhotoVideo;
                                try {
                                    setVideoImage(user1stVideoFile.getFile().getFile().getPath(), user1stVideoFile.getFile().getUrl());
                                } catch (ParseException exception) {
                                    exception.printStackTrace();
                                }
                                hasVideo = true;
                                addView.setVisibility(View.GONE);
                                delMainImgBtn.setVisibility(View.VISIBLE);
                                playIcon.setVisibility(View.VISIBLE);
                                hasChange = true;
//                                showToast(requireActivity(), getContext(), "Updated successfully", SUCCESS);
                                showOkAlertDialog(requireContext(), VIDEO_UPLOADED);
                                dismissProgress();
                                statusTV.setVisibility(View.VISIBLE);
                                statusTV.setText("Pending");
                                statusTV.setBackground(requireActivity().getResources().getDrawable(R.drawable.bg_status_pending));
                            }
                            ParseUser.getCurrentUser().put(PARAM_IS_VIDEO_SUBMITTED, true);
                            ParseUser.getCurrentUser().put(PARAM_MEDIA_PENDING, true);
                            ParseUser.getCurrentUser().saveInBackground(e12 -> {
                            });
                        } else {
                            showToast(requireActivity(), getContext(), e.getMessage(), ERROR);
                            dismissProgress();
                        }
                    });
                }
            }

            @Override
            public void onImagePickerError(@NonNull Throwable error, @NonNull MediaSource source) {
                //Some error handling
                error.printStackTrace();
                showToast(getActivity(), getContext(), error.getMessage(), ERROR);
            }

            @Override
            public void onCanceled(@NonNull MediaSource source) {
                //Not necessary to remove any files manually anymore
            }
        });

        if (data != null && requestCode == REQUEST_CODE_CROP_IMAGE) {
            String path = data.getStringExtra(CropFragment.IMAGE_PATH);
            if (path != null) {
                File file = new File(path);
                final UserProfilePhotoVideo userProfilePhotoVideo = new UserProfilePhotoVideo();
                userProfilePhotoVideo.put(PARAM_USER_ID, ParseUser.getCurrentUser().getObjectId());
                userProfilePhotoVideo.put(PARAM_FILE, new ParseFile(file));
                userProfilePhotoVideo.put(PARAM_IS_PHOTO, true);
                userProfilePhotoVideo.put(PARAM_FILE_STATUS, 0);
                showProgress();
                userProfilePhotoVideo.saveInBackground(e -> {
                    if (e == null) {
                        switch (CLICK_TAG) {
                            case TAG_IMAGE:
                                if (user1stPhotoFile != null) {
//                                    user1stPhotoFile.deleteEventually();
//                                    showProgress();
                                    user1stPhotoFile.deleteInBackground(e13 -> {
                                        if (e13 == null) {
                                            user1stPhotoFile = userProfilePhotoVideo;
//                                            ParseUser.getCurrentUser().put(PARAM_PROFILE_PIC, user1stPhotoFile.getFile().getUrl());
//                                            ParseUser.getCurrentUser().saveInBackground(e1 -> {
//                                            });
                                            if (userProfilePhotoOnly == null || userProfilePhotoOnly.size() == 0) {
                                                UserProfilePhotoVideo dummyObj = new UserProfilePhotoVideo();
                                                dummyObj.setDummyFile(true);
                                                userProfilePhotoOnly = new ArrayList<>();
                                                userProfilePhotoOnly.add(dummyObj);
                                            }
                                            Picasso.get().load(user1stPhotoFile.getFile().getUrl()).placeholder(R.drawable.image_placeholder).into(photoVideoPic1);
                                            addView.setVisibility(View.GONE);
                                            delMainImgBtn.setVisibility(View.VISIBLE);
                                            statusTV.setVisibility(View.VISIBLE);
                                            statusTV.setText("Pending");
                                            statusTV.setBackground(requireActivity().getResources().getDrawable(R.drawable.bg_status_pending));
                                        } else {
                                            showToast(requireActivity(), getContext(), e13.getMessage(), ERROR);
                                            statusTV.setVisibility(View.GONE);
                                        }
                                        dismissProgress();
                                    });
                                } else {
                                    user1stPhotoFile = userProfilePhotoVideo;
//                                    ParseUser.getCurrentUser().put(PARAM_PROFILE_PIC, user1stPhotoFile.getFile().getUrl());
//                                    ParseUser.getCurrentUser().saveInBackground(e1 -> {
//                                    });
                                    if (userProfilePhotoOnly == null || userProfilePhotoOnly.size() == 0) {
                                        UserProfilePhotoVideo dummyObj = new UserProfilePhotoVideo();
                                        dummyObj.setDummyFile(true);
                                        userProfilePhotoOnly = new ArrayList<>();
                                        userProfilePhotoOnly.add(dummyObj);
                                    }
                                    Picasso.get().load(user1stPhotoFile.getFile().getUrl()).placeholder(R.drawable.image_placeholder).into(photoVideoPic1);
                                    addView.setVisibility(View.GONE);
                                    delMainImgBtn.setVisibility(View.VISIBLE);
                                    statusTV.setVisibility(View.VISIBLE);
                                    statusTV.setText("Pending");
                                    statusTV.setBackground(requireActivity().getResources().getDrawable(R.drawable.bg_status_pending));
                                }
                                break;
                            case TAG_LIST:
                                if (userProfilePhotoOnly.size() == 1) {
                                    userProfilePhotoOnly.add(0, userProfilePhotoVideo);
                                } else {
                                    userProfilePhotoOnly.remove(userProfilePhotoOnly.size() - 1);
                                    userProfilePhotoOnly.add(userProfilePhotoVideo);
                                }
                                photosRV.smoothScrollToPosition(userProfilePhotoOnly.size() - 1);
                                break;
                            default:
                                break;
                        }
                        userPhotosListAdapter.setData(userProfilePhotoOnly);
                        userPhotosListAdapter.notifyDataSetChanged();
                        photosRV.setVisibility(View.VISIBLE);
                        hasPhotos = true;
                        hasChange = true;
//                        showToast(getActivity(), getContext(), "Updated successfully", SUCCESS);
                        if (userProfilePhotoOnly != null && userProfilePhotoOnly.size() == 1)
                            showOkAlertDialog(requireContext(), FIRST_PHOTO_UPLOADED);
                        else
                            showOkAlertDialog(requireContext(), PHOTO_UPLOADED);
                        ParseUser.getCurrentUser().put(PARAM_IS_PHOTO_SUBMITTED, true);
                        ParseUser.getCurrentUser().put(PARAM_MEDIA_PENDING, true);
                        ParseUser.getCurrentUser().saveInBackground(e12 -> {
                        });
                    } else {
                        showToast(requireActivity(), getContext(), e.getMessage(), ERROR);
                    }
                    dismissProgress();
                });
            } else {
                showToast(getActivity(), getContext(), "Error occurred, try another image!", ERROR);
            }
        }
    }

    private void setPhotoStatus() {
        if (user1stPhotoFile != null) {
            if (user1stPhotoFile.getInt(PARAM_FILE_STATUS) == ACTIVE) {
                statusTV.setVisibility(View.GONE);
            } else if (user1stPhotoFile.getInt(PARAM_FILE_STATUS) == PENDING) {
                statusTV.setVisibility(View.VISIBLE);
                statusTV.setText("Pending");
                statusTV.setBackground(requireActivity().getResources().getDrawable(R.drawable.bg_status_pending));
            } else if (user1stPhotoFile.getInt(PARAM_FILE_STATUS) == REJECTED) {
                statusTV.setVisibility(View.VISIBLE);
                statusTV.setText("Rejected");
                statusTV.setBackground(requireActivity().getResources().getDrawable(R.drawable.bg_status_rejected));
            }
        } else {
            statusTV.setVisibility(View.GONE);
        }
    }
}