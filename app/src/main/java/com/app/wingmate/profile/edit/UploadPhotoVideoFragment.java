package com.app.wingmate.profile.edit;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.wingmate.R;
import com.app.wingmate.base.BaseFragment;
import com.app.wingmate.models.PhotoFile;
import com.app.wingmate.models.Question;
import com.app.wingmate.models.TermsConditions;
import com.app.wingmate.models.UserAnswer;
import com.app.wingmate.models.UserProfilePhotoVideo;
import com.app.wingmate.profile.ProfileInteractor;
import com.app.wingmate.profile.ProfilePresenter;
import com.app.wingmate.profile.ProfileView;
import com.app.wingmate.ui.activities.MainActivity;
import com.app.wingmate.ui.adapters.DosDontsListAdapter;
import com.app.wingmate.ui.adapters.DosDontsPhotosGridAdapter;
import com.app.wingmate.ui.adapters.PhotosHorizontalListAdapter;
import com.app.wingmate.ui.adapters.ProfileOptionsListAdapter;
import com.app.wingmate.ui.fragments.CropFragment;
import com.app.wingmate.utils.ActivityUtility;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import java.io.File;
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
import static com.app.wingmate.utils.AppConstants.MODE_PHOTOS;
import static com.app.wingmate.utils.AppConstants.MODE_VIDEO;
import static com.app.wingmate.utils.AppConstants.ERROR;
import static com.app.wingmate.utils.AppConstants.KEY_PHOTO;
import static com.app.wingmate.utils.AppConstants.KEY_PHOTO_TEXT;
import static com.app.wingmate.utils.AppConstants.KEY_VIDEO;
import static com.app.wingmate.utils.AppConstants.KEY_VIDEO_TEXT;
import static com.app.wingmate.utils.AppConstants.PARAM_FILE;
import static com.app.wingmate.utils.AppConstants.PARAM_IS_PHOTO;
import static com.app.wingmate.utils.AppConstants.PARAM_PROFILE_PIC;
import static com.app.wingmate.utils.AppConstants.PARAM_USER_ID;
import static com.app.wingmate.utils.CommonKeys.KEY_ACTIVITY_TAG;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_CROP;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_UPLOAD_PHOTO_VIDEO_PROFILE;
import static com.app.wingmate.utils.Utilities.showToast;

public class UploadPhotoVideoFragment extends BaseFragment implements ProfileView {

    public static final String TAG = UploadPhotoVideoFragment.class.getName();

    Unbinder unbinder;

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

    private ProfilePresenter presenter;

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

        presenter = new ProfilePresenter(this, new ProfileInteractor());

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

    @OnClick({R.id.take_photo_video_btn, R.id.btn_continue, R.id.btn_back})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.take_photo_video_btn:
                if (CURRENT_MODE == MODE_PHOTOS) {
                    CLICK_TAG = TAG_IMAGE;
                    easyImage.openChooser(this);
                } else if (CURRENT_MODE == MODE_VIDEO) {
                    CLICK_TAG = TAG_VIDEO;
                    easyImage.openCameraForVideo(this);
                }
                break;
            case R.id.btn_continue:
                if (CURRENT_MODE == MODE_PHOTOS) {
                    if (hasPhotos) {
                        CURRENT_MODE = MODE_VIDEO;
                        setVideoView();
                    } else {
                        showToast(getActivity(), getContext(), "Minimum 1 image is required!", ERROR);
                    }
                } else if (CURRENT_MODE == MODE_VIDEO) {
                    if (hasVideo) {
                        CURRENT_MODE = MODE_PHOTOS;
                        getActivity().onBackPressed();
                    } else {
                        showToast(getActivity(), getContext(), "At least 1 video is required!", ERROR);
                    }
                }
                break;
            case R.id.btn_back:
                getActivity().onBackPressed();
                break;
            default:
                break;
        }
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
                for (int i = 0; i < userProfilePhotoOnly.size(); i++) {
                    String profilePicUrl = ParseUser.getCurrentUser().getString(PARAM_PROFILE_PIC);
                    System.out.println(profilePicUrl + "==match==" + userProfilePhotoOnly.get(i).getFile().getUrl());
                    if (profilePicUrl != null && profilePicUrl.equals(userProfilePhotoOnly.get(i).getFile().getUrl())) {
                        user1stPhotoFile = userProfilePhotoOnly.get(i);
                        userProfilePhotoOnly.remove(i);
                        break;
                    }
                }
                if (user1stPhotoFile != null) {
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

    public void setPhotosView() {
        CURRENT_MODE = MODE_PHOTOS;
        placeholderTV.setText("Minimum 1 photo required");
        photosRV.setVisibility(View.VISIBLE);
        modeTagTV.setText("Photos");
        playIcon.setVisibility(View.GONE);
        dosDontsListAdapter.setData(dosDontsPhotoTextsList);
        dosDontsPhotosGridAdapter.setData(dosDontsPhotoFilesList);
        if (user1stPhotoFile != null) {
            Picasso.get().load(user1stPhotoFile.getFile().getUrl()).placeholder(R.drawable.image_placeholder).into(photoVideoPic1);
            addView.setVisibility(View.GONE);
            hasPhotos = true;
        } else {
            addView.setVisibility(View.VISIBLE);
            photoVideoPic1.setImageResource(android.R.color.transparent);
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
    }

    public void setVideoView() {
        CURRENT_MODE = MODE_VIDEO;
        placeholderTV.setText("Atleast 1 video required");
        modeTagTV.setText("Video");
        playIcon.setVisibility(View.GONE);
        dosDontsListAdapter.setData(dosDontsVideoTextsList);
        dosDontsPhotosGridAdapter.setData(dosDontsVideoFilesList);
        if (user1stVideoFile != null) {
            try {
                setVideoImage(user1stVideoFile.getFile().getFile().getPath());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            addView.setVisibility(View.GONE);
            playIcon.setVisibility(View.VISIBLE);
            hasVideo = true;
        } else {
            addView.setVisibility(View.VISIBLE);
            photoVideoPic1.setImageResource(android.R.color.transparent);
            hasVideo = false;
        }
        photosRV.setVisibility(View.GONE);
//        if (userProfileVideoOnly != null && userProfileVideoOnly.size() > 0) {
//            photosRV.setVisibility(View.VISIBLE);
//        } else {
//            photosRV.setVisibility(View.GONE);
//            userProfileVideoOnly = new ArrayList<>();
//        }
    }

    private void setVideoImage(String videoFilePath) {
        Bitmap thumb = ThumbnailUtils.createVideoThumbnail(videoFilePath, MediaStore.Images.Thumbnails.MINI_KIND);
        Matrix matrix = new Matrix();
        Bitmap bmThumbnail = Bitmap.createBitmap(thumb, 0, 0, thumb.getWidth(), thumb.getHeight(), matrix, true);
        photoVideoPic1.setImageBitmap(bmThumbnail);
    }

    public void addImage() {
        if (CURRENT_MODE == MODE_PHOTOS) {
            CLICK_TAG = TAG_LIST;
            easyImage.openChooser(this);
        }
    }

    public void deleteImage(int index) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(requireContext());
        dialog.setTitle("Alert")
                .setIcon(R.drawable.big_red_cross)
                .setMessage("Are you sure to delete this image?")
                .setNegativeButton("Cancel", (dialoginterface, i) -> dialoginterface.cancel())
                .setPositiveButton("Yes, Delete", (dialoginterface, i) -> {
                    if (CURRENT_MODE == MODE_PHOTOS) {
                        CLICK_TAG = TAG_LIST;
                        userProfilePhotoOnly.get(index).deleteEventually();
                        userProfilePhotoOnly.remove(index);
                        userPhotosListAdapter.setData(userProfilePhotoOnly);
                        userPhotosListAdapter.notifyDataSetChanged();
                    }
                    hasChange = true;
                }).show();
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
                    showProgress();
                    userProfilePhotoVideo.saveInBackground(e -> {
                        if (e == null) {
                            if (user1stVideoFile != null) {
                                user1stVideoFile.deleteEventually();
                            }
                            user1stVideoFile = userProfilePhotoVideo;
                            try {
                                setVideoImage(user1stVideoFile.getFile().getFile().getPath());
                            } catch (ParseException exception) {
                                exception.printStackTrace();
                            }
                            hasVideo = true;
                            addView.setVisibility(View.GONE);
                            playIcon.setVisibility(View.VISIBLE);
                            hasChange = true;
                        } else {
                            showToast(requireActivity(), getContext(), e.getMessage(), ERROR);
                        }
                        dismissProgress();
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
                showProgress();
                userProfilePhotoVideo.saveInBackground(e -> {
                    if (e == null) {
                        switch (CLICK_TAG) {
                            case TAG_IMAGE:
                                if (user1stPhotoFile != null) {
                                    user1stPhotoFile.deleteEventually();
                                }
                                user1stPhotoFile = userProfilePhotoVideo;
                                System.out.println("===url====" + user1stPhotoFile.getFile().getUrl());
                                ParseUser.getCurrentUser().put(PARAM_PROFILE_PIC, user1stPhotoFile.getFile().getUrl());
                                ParseUser.getCurrentUser().saveInBackground(e1 -> {
                                });
                                if (userProfilePhotoOnly == null || userProfilePhotoOnly.size() == 0) {
                                    UserProfilePhotoVideo dummyObj = new UserProfilePhotoVideo();
                                    dummyObj.setDummyFile(true);
                                    userProfilePhotoOnly = new ArrayList<>();
                                    userProfilePhotoOnly.add(dummyObj);
                                }
                                Picasso.get().load(user1stPhotoFile.getFile().getUrl()).placeholder(R.drawable.image_placeholder).into(photoVideoPic1);
                                addView.setVisibility(View.GONE);
                                break;
                            case TAG_LIST:
                                userProfilePhotoOnly.add(0, userProfilePhotoVideo);
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
}