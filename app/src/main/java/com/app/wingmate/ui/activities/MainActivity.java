package com.app.wingmate.ui.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.app.wingmate.R;
import com.app.wingmate.base.BaseActivity;
import com.app.wingmate.ui.fragments.DashboardFragment;
import com.app.wingmate.events.RefreshProfile;
import com.app.wingmate.ui.fragments.HomeFragment;
import com.app.wingmate.ui.fragments.ProfileFragment;
import com.app.wingmate.ui.fragments.EditProfileFragment;
import com.app.wingmate.ui.fragments.EditProfileTextFieldFragment;
import com.app.wingmate.ui.fragments.UploadPhotoVideoFragment;
import com.app.wingmate.ui.fragments.CropFragment;
import com.app.wingmate.ui.fragments.DummyFragment;
import com.app.wingmate.ui.fragments.PhotoViewFragment;
import com.app.wingmate.ui.fragments.VideoViewFragment;
import com.app.wingmate.ui.fragments.EmailVerificationFragment;
import com.app.wingmate.ui.fragments.PreLoginFragment;
import com.app.wingmate.ui.fragments.QuestionnaireFragment;
import com.app.wingmate.ui.fragments.TermsAndConditionsFragment;
import com.app.wingmate.ui.fragments.WebviewFragment;
import com.app.wingmate.ui.fragments.ForgotPasswordFragment;
import com.app.wingmate.ui.fragments.LoginFragment;
import com.app.wingmate.ui.fragments.SignupFragment;
import com.parse.ParseUser;

import org.greenrobot.eventbus.EventBus;

import static com.app.wingmate.utils.AppConstants.PARAM_ABOUT_ME;
import static com.app.wingmate.utils.AppConstants.PARAM_NICK;
import static com.app.wingmate.utils.CommonKeys.KEY_ACTIVITY_TAG;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_CROP;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_DUMMY;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_EDIT_PROFILE;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_EDIT_PROFILE_TEXT_FIELDS;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_EMAIL_VERIFY;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_FORGOT_PASSWORD;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_HOME;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_LOGIN;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_DASHBOARD;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_PHOTO_VIEW;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_PRE_LOGIN;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_PROFILE;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_QUESTIONNAIRE;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_SIGN_UP;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_TERMS_AND_CONDITIONS;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_UPLOAD_PHOTO_VIDEO_PROFILE;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_VIDEO_VIEW;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_WEB_VIEW;
import static com.app.wingmate.utils.CommonKeys.KEY_TITLE;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private static final int PERMISSION_CALLBACK_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    String[] permissionsRequired = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private SharedPreferences permissionStatus;
    private boolean sentToSettings = false;

    private FragmentManager supportFragmentManager;

    private Fragment preLoginFragment;
    private Fragment loginFragment;
    private Fragment signUpFragment;
    private Fragment forgotPasswordFragment;
    private Fragment emailVerifyFragment;
    private Fragment questionnaireFragment;
    private Fragment termsFragment;
    private Fragment webViewFragment;
    private Fragment cropFragment;
    private Fragment dashboardFragment;
    private Fragment homeFragment;
    private Fragment dummyFragment;
    private Fragment profileFragment;
    private Fragment editProfileFragment;
    private Fragment editProfileFieldsFragment;
    private Fragment uploadPhotosVideoFragment;
    private Fragment videoViewFragment;
    private Fragment photoViewFragment;

    private String fragmentTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        permissionStatus = getSharedPreferences("permissionStatus", MODE_PRIVATE);

        hideBackBtn();

        supportFragmentManager = getSupportFragmentManager();

        replaceFragment();

        getBackBtn().setOnClickListener(this);
        getDoneButton().setOnClickListener(this);
        getRotateLeftButton().setOnClickListener(this);
        getRotateRightButton().setOnClickListener(this);
    }

    private void replaceFragment() {
        fragmentTag = getIntent().getExtras().getString(KEY_ACTIVITY_TAG, "");
        switch (fragmentTag) {
            case KEY_FRAGMENT_PRE_LOGIN:
                beginPreLoginFragment();
                break;
            case KEY_FRAGMENT_LOGIN:
                beginLoginFragment();
                break;
            case KEY_FRAGMENT_QUESTIONNAIRE:
                beginQuestionnaireFragment();
                break;
            case KEY_FRAGMENT_TERMS_AND_CONDITIONS:
                beginTermsFragment();
                break;
            case KEY_FRAGMENT_SIGN_UP:
                beginSignupFragment();
                break;
            case KEY_FRAGMENT_FORGOT_PASSWORD:
                beginForgotPasswordFragment();
                break;
            case KEY_FRAGMENT_EMAIL_VERIFY:
                beginEmailVerificationFragment();
                break;
            case KEY_FRAGMENT_CROP:
                beginCropFragment();
                break;
            case KEY_FRAGMENT_WEB_VIEW:
                beginWebViewFragment();
                break;
            case KEY_FRAGMENT_DASHBOARD:
                checkPermissions();
                beginDashboardFragment();
                break;
            case KEY_FRAGMENT_HOME:
                beginHomeFragment();
                break;
            case KEY_FRAGMENT_DUMMY:
                beginDummyFragment();
                break;
            case KEY_FRAGMENT_PROFILE:
                beginProfileFragment();
                break;
            case KEY_FRAGMENT_EDIT_PROFILE:
                beginEditProfileFragment();
                break;
            case KEY_FRAGMENT_EDIT_PROFILE_TEXT_FIELDS:
                beginEditProfileFieldsFragment();
                break;
            case KEY_FRAGMENT_UPLOAD_PHOTO_VIDEO_PROFILE:
                checkPermissions();
                beginUploadPhotosVideoFragment();
                break;
            case KEY_FRAGMENT_VIDEO_VIEW:
                beginVideoViewFragment();
                break;
            case KEY_FRAGMENT_PHOTO_VIEW:
                beginPhotoViewFragment();
                break;
            default:
                onBackPressed();
                break;
        }
    }

    private void beginPreLoginFragment() {
        preLoginFragment = getSupportFragmentManager().findFragmentByTag(PreLoginFragment.TAG);
        if (preLoginFragment == null)
            preLoginFragment = supportFragmentManager.getFragmentFactory().instantiate(ClassLoader.getSystemClassLoader(), PreLoginFragment.TAG);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, preLoginFragment, PreLoginFragment.TAG).commit();
    }

    private void beginLoginFragment() {
        showTopView();
        setScreenTitle("Login");
        loginFragment = getSupportFragmentManager().findFragmentByTag(LoginFragment.TAG);
        if (loginFragment == null)
            loginFragment = getSupportFragmentManager().getFragmentFactory().instantiate(ClassLoader.getSystemClassLoader(), LoginFragment.TAG);
//        loginFragment = Fragment.instantiate(this, LoginFragment.TAG, getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().replace(R.id.container, loginFragment, LoginFragment.TAG).commit();
    }

    private void beginQuestionnaireFragment() {
        showTopView();
//        showStepView();
        questionnaireFragment = getSupportFragmentManager().findFragmentByTag(QuestionnaireFragment.TAG);
        if (questionnaireFragment == null)
            questionnaireFragment = getSupportFragmentManager().getFragmentFactory().instantiate(ClassLoader.getSystemClassLoader(), QuestionnaireFragment.TAG);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, questionnaireFragment, QuestionnaireFragment.TAG).commit();
    }

    private void beginTermsFragment() {
        showTopView();
        setScreenTitle("Terms & Conditions");
        termsFragment = getSupportFragmentManager().findFragmentByTag(TermsAndConditionsFragment.TAG);
        if (termsFragment == null)
            termsFragment = getSupportFragmentManager().getFragmentFactory().instantiate(ClassLoader.getSystemClassLoader(), TermsAndConditionsFragment.TAG);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, termsFragment, TermsAndConditionsFragment.TAG).commit();
    }

    private void beginEmailVerificationFragment() {
        showTopView();
        hideScreenTitle();
        emailVerifyFragment = getSupportFragmentManager().findFragmentByTag(EmailVerificationFragment.TAG);
        if (emailVerifyFragment == null)
            emailVerifyFragment = getSupportFragmentManager().getFragmentFactory().instantiate(ClassLoader.getSystemClassLoader(), EmailVerificationFragment.TAG);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, emailVerifyFragment, EmailVerificationFragment.TAG).commit();
    }

    private void beginSignupFragment() {
        showTopView();
        hideScreenTitle();
        showStep();
        setStep(1);
        signUpFragment = getSupportFragmentManager().findFragmentByTag(SignupFragment.TAG);
        if (signUpFragment == null)
            signUpFragment = getSupportFragmentManager().getFragmentFactory().instantiate(ClassLoader.getSystemClassLoader(), SignupFragment.TAG);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, signUpFragment, SignupFragment.TAG).commit();
    }

    private void beginForgotPasswordFragment() {
        showTopView();
        showScreenTitle();
        setScreenTitle("Forgot Password");
        forgotPasswordFragment = getSupportFragmentManager().findFragmentByTag(ForgotPasswordFragment.TAG);
        if (forgotPasswordFragment == null)
            forgotPasswordFragment = getSupportFragmentManager().getFragmentFactory().instantiate(ClassLoader.getSystemClassLoader(), ForgotPasswordFragment.TAG);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, forgotPasswordFragment, ForgotPasswordFragment.TAG).commit();
    }

    private void beginCropFragment() {
        showCropToolbar();
        setActivityTitle(getResources().getString(R.string.title_crop));
        showBackBtn();
        showCropOptions();
        cropFragment = getSupportFragmentManager().findFragmentByTag(CropFragment.TAG);
        if (cropFragment == null) {
            cropFragment = getSupportFragmentManager().getFragmentFactory().instantiate(ClassLoader.getSystemClassLoader(), CropFragment.TAG);
//            cropFragment = Fragment.instantiate(this, CropFragment.TAG, getIntent().getExtras());
        }
        cropFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().replace(R.id.container, cropFragment, CropFragment.TAG).commit();
    }

    private void beginDummyFragment() {
        showTopView();
        hideScreenTitle();
        dummyFragment = getSupportFragmentManager().findFragmentByTag(DummyFragment.TAG);
        if (dummyFragment == null)
            dummyFragment = getSupportFragmentManager().getFragmentFactory().instantiate(ClassLoader.getSystemClassLoader(), DummyFragment.TAG);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, dummyFragment, DummyFragment.TAG).commit();
    }

    private void beginHomeFragment() {
        showTopView();
        setScreenTitle("Home");
        homeFragment = getSupportFragmentManager().findFragmentByTag(HomeFragment.TAG);
        if (homeFragment == null)
            homeFragment = getSupportFragmentManager().getFragmentFactory().instantiate(ClassLoader.getSystemClassLoader(), HomeFragment.TAG);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragment, HomeFragment.TAG).commit();
    }

    private void beginDashboardFragment() {
        showTopView();
        setScreenTitle("Home");
        dashboardFragment = getSupportFragmentManager().findFragmentByTag(DashboardFragment.TAG);
        if (dashboardFragment == null)
            dashboardFragment = getSupportFragmentManager().getFragmentFactory().instantiate(ClassLoader.getSystemClassLoader(), DashboardFragment.TAG);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, dashboardFragment, DashboardFragment.TAG).commit();
    }

    private void beginProfileFragment() {
        hideTopView();
        hideScreenTitle();
        profileFragment = getSupportFragmentManager().findFragmentByTag(ProfileFragment.TAG);
        if (profileFragment == null)
            profileFragment = getSupportFragmentManager().getFragmentFactory().instantiate(ClassLoader.getSystemClassLoader(), ProfileFragment.TAG);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, profileFragment, ProfileFragment.TAG).commit();
    }

    private void beginEditProfileFragment() {
        showTopView();
        setScreenTitle("Edit Profile");
        editProfileFragment = getSupportFragmentManager().findFragmentByTag(EditProfileFragment.TAG);
        if (editProfileFragment == null)
            editProfileFragment = getSupportFragmentManager().getFragmentFactory().instantiate(ClassLoader.getSystemClassLoader(), EditProfileFragment.TAG);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, editProfileFragment, EditProfileFragment.TAG).commit();
    }

    private void beginEditProfileFieldsFragment() {
        showTopView();
        String title = getIntent().getStringExtra(KEY_TITLE);
        if (title != null && title.equalsIgnoreCase(PARAM_NICK))
            setScreenTitle("You can call me");
        else if (title != null && title.equalsIgnoreCase(PARAM_ABOUT_ME))
            setScreenTitle("Me, Myself & I");
        else
            setScreenTitle("Edit Profile");
        editProfileFieldsFragment = getSupportFragmentManager().findFragmentByTag(EditProfileTextFieldFragment.TAG);
        if (editProfileFieldsFragment == null)
            editProfileFieldsFragment = getSupportFragmentManager().getFragmentFactory().instantiate(ClassLoader.getSystemClassLoader(), EditProfileTextFieldFragment.TAG);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, editProfileFieldsFragment, EditProfileTextFieldFragment.TAG).commit();
    }

    private void beginUploadPhotosVideoFragment() {
        showTopView();
        setScreenTitle("Upload Photos");
        uploadPhotosVideoFragment = getSupportFragmentManager().findFragmentByTag(UploadPhotoVideoFragment.TAG);
        if (uploadPhotosVideoFragment == null)
            uploadPhotosVideoFragment = getSupportFragmentManager().getFragmentFactory().instantiate(ClassLoader.getSystemClassLoader(), UploadPhotoVideoFragment.TAG);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, uploadPhotosVideoFragment, UploadPhotoVideoFragment.TAG).commit();
    }

    private void beginVideoViewFragment() {
        showCropToolbar();
        setActivityTitle("Video");
        showBackBtn();
        hideCropOptions();
        videoViewFragment = getSupportFragmentManager().findFragmentByTag(VideoViewFragment.TAG);
        if (videoViewFragment == null)
            videoViewFragment = getSupportFragmentManager().getFragmentFactory().instantiate(ClassLoader.getSystemClassLoader(), VideoViewFragment.TAG);
        videoViewFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().replace(R.id.container, videoViewFragment, VideoViewFragment.TAG).commit();
    }

    private void beginPhotoViewFragment() {
        showCropToolbar();
        setActivityTitle("Photo");
        showBackBtn();
        hideCropOptions();
        photoViewFragment = getSupportFragmentManager().findFragmentByTag(PhotoViewFragment.TAG);
        if (photoViewFragment == null)
            photoViewFragment = getSupportFragmentManager().getFragmentFactory().instantiate(ClassLoader.getSystemClassLoader(), PhotoViewFragment.TAG);
        photoViewFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().replace(R.id.container, photoViewFragment, PhotoViewFragment.TAG).commit();
    }

    private void beginWebViewFragment() {
        setActivityTitle(getIntent().getExtras().getString(KEY_TITLE, getResources().getString(R.string.app_name)));
        showBackBtn();
        webViewFragment = getSupportFragmentManager().findFragmentByTag(WebviewFragment.TAG);
        if (webViewFragment == null)
            webViewFragment = Fragment.instantiate(this, WebviewFragment.TAG, getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().replace(R.id.container, webViewFragment, WebviewFragment.TAG).commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
                onBackPressed();
                break;
            case R.id.btn_rotate_left:
                if (cropFragment != null)
                    ((CropFragment) cropFragment).rotateLeft();
                break;
            case R.id.btn_rotate_right:
                if (cropFragment != null)
                    ((CropFragment) cropFragment).rotateRight();
                break;
            case R.id.done:
                if (cropFragment != null)
                    ((CropFragment) cropFragment).cropDone();
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (loginFragment != null) {
            Intent mainIntent = new Intent(this, MainActivity.class);
            mainIntent.putExtra(KEY_ACTIVITY_TAG, KEY_FRAGMENT_PRE_LOGIN);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(mainIntent);
            overridePendingTransition(R.anim.blank_anim, R.anim.blank_anim);
//            finish();
        } else if (signUpFragment != null) {
            if (ParseUser.getCurrentUser() != null || ((SignupFragment) signUpFragment).getStep() == 1) {
                ParseUser.logOut();
                super.onBackPressed();
                overridePendingTransition(R.anim.blank_anim, R.anim.left_to_right);
            } else {
                ((SignupFragment) signUpFragment).onBackPressed();
            }
        } else if (editProfileFragment != null) {
            if (((EditProfileFragment) editProfileFragment).isAnyUpdate) {
                EventBus.getDefault().post(new RefreshProfile());
            }
            super.onBackPressed();
            overridePendingTransition(R.anim.blank_anim, R.anim.left_to_right);
        } else if (uploadPhotosVideoFragment != null) {
//            if (((UploadPhotoVideoFragment) uploadPhotosVideoFragment).CURRENT_MODE == MODE_VIDEO) {
//                ((UploadPhotoVideoFragment) uploadPhotosVideoFragment).setPhotosView();
//            } else {
                if (((UploadPhotoVideoFragment) uploadPhotosVideoFragment).hasChange) {
                    EventBus.getDefault().post(new RefreshProfile());
                }
                super.onBackPressed();
                overridePendingTransition(R.anim.blank_anim, R.anim.left_to_right);
//            }
        } else if (dashboardFragment != null) {
            if (((DashboardFragment) dashboardFragment).viewPager.getCurrentItem() > 0) {
                ((DashboardFragment) dashboardFragment).viewPager.setCurrentItem(0, true);
                setScreenTitle("Hi, " + ParseUser.getCurrentUser().getString(PARAM_NICK));
            } else {
                super.onBackPressed();
                overridePendingTransition(R.anim.blank_anim, R.anim.left_to_right);
//                System.exit(0);
            }

        } else if (emailVerifyFragment == null && termsFragment == null) {
            super.onBackPressed();
            overridePendingTransition(R.anim.blank_anim, R.anim.left_to_right);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void checkPermissions() {
        if (ActivityCompat.checkSelfPermission(com.app.wingmate.ui.activities.MainActivity.this, permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(com.app.wingmate.ui.activities.MainActivity.this, permissionsRequired[1]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(com.app.wingmate.ui.activities.MainActivity.this, permissionsRequired[2]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(com.app.wingmate.ui.activities.MainActivity.this, permissionsRequired[3]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(com.app.wingmate.ui.activities.MainActivity.this, permissionsRequired[4]) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(com.app.wingmate.ui.activities.MainActivity.this, permissionsRequired[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale(com.app.wingmate.ui.activities.MainActivity.this, permissionsRequired[1])
                    || ActivityCompat.shouldShowRequestPermissionRationale(com.app.wingmate.ui.activities.MainActivity.this, permissionsRequired[2])
                    || ActivityCompat.shouldShowRequestPermissionRationale(com.app.wingmate.ui.activities.MainActivity.this, permissionsRequired[3])
                    || ActivityCompat.shouldShowRequestPermissionRationale(com.app.wingmate.ui.activities.MainActivity.this, permissionsRequired[4])
            ) {
                //Show Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(com.app.wingmate.ui.activities.MainActivity.this);
                builder.setTitle("Need Multiple Permissions");
                builder.setMessage("Wing Mate needs permissions.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(com.app.wingmate.ui.activities.MainActivity.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else if (permissionStatus.getBoolean(permissionsRequired[0], false)) {
                //Previously Permission Request was cancelled with 'Dont Ask Again',
                // Redirect to Settings after showing Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(com.app.wingmate.ui.activities.MainActivity.this);
                builder.setTitle("Need Multiple Permissions");
                builder.setMessage("Wing Mate needs permissions.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        sentToSettings = true;
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                        Toast.makeText(getBaseContext(), "Go to Permissions to Grant", Toast.LENGTH_LONG).show();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else {
                //just request the permission
                ActivityCompat.requestPermissions(com.app.wingmate.ui.activities.MainActivity.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
            }
//            txtPermissions.setText("Permissions Required");
            SharedPreferences.Editor editor = permissionStatus.edit();
            editor.putBoolean(permissionsRequired[0], true);
            editor.commit();
        } else {
            //You already have the permission, just go ahead.
            proceedAfterPermission();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CALLBACK_CONSTANT) {
            //check if all permissions are granted
            boolean allgranted = false;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    allgranted = true;
                } else {
                    allgranted = false;
                    break;
                }
            }

            if (allgranted) {
                proceedAfterPermission();
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(com.app.wingmate.ui.activities.MainActivity.this, permissionsRequired[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale(com.app.wingmate.ui.activities.MainActivity.this, permissionsRequired[1])
                    || ActivityCompat.shouldShowRequestPermissionRationale(com.app.wingmate.ui.activities.MainActivity.this, permissionsRequired[2])
                    || ActivityCompat.shouldShowRequestPermissionRationale(com.app.wingmate.ui.activities.MainActivity.this, permissionsRequired[3])
                    || ActivityCompat.shouldShowRequestPermissionRationale(com.app.wingmate.ui.activities.MainActivity.this, permissionsRequired[4])
            ) {
                AlertDialog.Builder builder = new AlertDialog.Builder(com.app.wingmate.ui.activities.MainActivity.this);
                builder.setTitle("Need Multiple Permissions");
                builder.setMessage("Wing Mate needs permissions.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(MainActivity.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else {
                Toast.makeText(getBaseContext(), "Unable to get Permission", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PERMISSION_SETTING) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(MainActivity.this, permissionsRequired[1]) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(MainActivity.this, permissionsRequired[2]) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(MainActivity.this, permissionsRequired[3]) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(MainActivity.this, permissionsRequired[4]) == PackageManager.PERMISSION_GRANTED
            ) {
                proceedAfterPermission();
            }
        }
    }

    private void proceedAfterPermission() {

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (sentToSettings) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(MainActivity.this, permissionsRequired[1]) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(MainActivity.this, permissionsRequired[2]) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(MainActivity.this, permissionsRequired[3]) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(MainActivity.this, permissionsRequired[4]) == PackageManager.PERMISSION_GRANTED
            ) {
                proceedAfterPermission();
            }
        }
    }
}