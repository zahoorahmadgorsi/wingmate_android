package com.app.wingmate.base;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.app.wingmate.R;
import com.app.wingmate.utils.ActivityUtility;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import me.tankery.lib.circularseekbar.CircularSeekBar;

import static com.app.wingmate.utils.AppConstants.PARAM_PROFILE_PIC;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_PHOTO_VIEW;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_PROFILE;

//public class BaseActivity extends AppCompatActivity implements View.OnClickListener {
public class BaseActivity extends AppCompatActivity {

    RelativeLayout cropToolbar;
    LinearLayout cropOptionsLayout;
    TextView title;
    ImageView backBtn;
    ImageView rotateLeftBtn;
    ImageView rotateRightBtn;
    TextView doneBtn;

    RelativeLayout topView;
    TextView screenTitle;
    TextView screenSubTitle;
    ImageView icStep;

    RelativeLayout stepsView;
    CircularSeekBar stepsProgress;
    TextView stepsCountTV;
    TextView skipTV;

    CircleImageView profileImg;

    LinearLayout fans_count_view;
    LinearLayout btn_likes_filter;
    LinearLayout btn_crush_filter;
    LinearLayout btn_may_be_filter;
    TextView likes_count;
    TextView likes_count_tv;
    TextView crush_count;
    TextView crush_count_tv;
    TextView may_be_count;
    TextView may_be_count_tv;
    TextView backLabel;

    RelativeLayout crush;
    RelativeLayout like;
    RelativeLayout maybe;
    TextView crushCount, likeCount, maybeCount;
    LinearLayout fansCountView;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);

        cropToolbar = (RelativeLayout) findViewById(R.id.crop_toolbar);
        cropOptionsLayout = (LinearLayout) findViewById(R.id.crop_options_layout);
        title = (TextView) findViewById(R.id.title);
        backBtn = (ImageView) findViewById(R.id.back_btn);
        rotateLeftBtn = (ImageView) findViewById(R.id.btn_rotate_left);
        rotateRightBtn = (ImageView) findViewById(R.id.btn_rotate_right);
        doneBtn = (TextView) findViewById(R.id.done);

        topView = (RelativeLayout) findViewById(R.id.top_view);
        screenTitle = (TextView) findViewById(R.id.screen_title);
        screenSubTitle = (TextView) findViewById(R.id.screen_sub_title);
        icStep = (ImageView) findViewById(R.id.ic_step);

        stepsView = (RelativeLayout) findViewById(R.id.steps_view);
        stepsProgress = (CircularSeekBar) findViewById(R.id.steps_progress);
        stepsCountTV = (TextView) findViewById(R.id.steps_count_tv);
        skipTV = (TextView) findViewById(R.id.skip);

        fans_count_view = findViewById(R.id.fans_count_view);
        btn_likes_filter = findViewById(R.id.btn_likes_filter);
        btn_crush_filter = findViewById(R.id.btn_crush_filter);
        btn_may_be_filter = findViewById(R.id.btn_may_be_filter);
        likes_count = findViewById(R.id.likes_count);
        likes_count_tv = findViewById(R.id.likes_count_tv);
        crush_count = findViewById(R.id.crush_count);
        crush_count_tv = findViewById(R.id.crush_count_tv);
        may_be_count = findViewById(R.id.may_be_count);
        may_be_count_tv = findViewById(R.id.may_be_count_tv);

        profileImg = (CircleImageView) findViewById(R.id.profile_img);
        backLabel = findViewById(R.id.backLabel);

        fansCountView = findViewById(R.id.fansCountView);
        crush = findViewById(R.id.crush);
        like = findViewById(R.id.like);
        maybe = findViewById(R.id.maybe);
        crushCount = findViewById(R.id.crushCount);
        likeCount = findViewById(R.id.likesCount);
        maybeCount = findViewById(R.id.maybeCount);

        profileImg.setOnClickListener(v -> {
            ActivityUtility.startProfileActivity(BaseActivity.this, KEY_FRAGMENT_PROFILE, true, ParseUser.getCurrentUser());
//            ActivityUtility.startPhotoViewActivity(BaseActivity.this, KEY_FRAGMENT_PHOTO_VIEW, ParseUser.getCurrentUser().getString(PARAM_PROFILE_PIC));

        });

        hideTopView();
        hideStepView();
        hideSkip();
        hideStep();
        hideCropToolbar();
        hideBackBtn();
        hideCropOptions();
        hideProfileImage();
        hideCountsView();
        hideBackLabel();
        hideFansCountView();
    }

    private void hideBackLabel() {
        backLabel.setVisibility(View.GONE);
    }

//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.back_btn:
//                onBackPressed();
//                break;
//            default:
//                break;
//        }
//    }

    public void showBackLabel(){
        backLabel.setVisibility(View.VISIBLE);
    }
    public void showTopView() {
        topView.setVisibility(View.VISIBLE);
    }

    public void hideTopView() {
        topView.setVisibility(View.GONE);
    }

    public void setScreenTitle(String title) {
        this.screenTitle.setText(title);
    }

    public void setScreenSubTitle(String title) {
        if (title.isEmpty())
            this.screenSubTitle.setVisibility(View.GONE);
        else
            this.screenSubTitle.setVisibility(View.VISIBLE);
        this.screenSubTitle.setText(title);
    }

    public void hideCropToolbar() {
        cropToolbar.setVisibility(View.GONE);
    }

    public void showCropToolbar() {
        cropToolbar.setVisibility(View.VISIBLE);
    }

    public void setActivityTitle(String title) {
        this.title.setText(title);
    }

    public void showScreenTitle() {
        screenTitle.setVisibility(View.VISIBLE);
    }

    public void hideScreenTitle() {
        screenTitle.setVisibility(View.GONE);
        screenSubTitle.setVisibility(View.GONE);
    }

    public void showStepView() {
        stepsView.setVisibility(View.VISIBLE);
    }

    public void hideStepView() {
        stepsView.setVisibility(View.GONE);
    }

    public void setStepViewStepTVAndProgress(int step, int limit) {
        stepsCountTV.setText(step + "/" + limit);
        stepsProgress.setMax(limit);
        stepsProgress.setProgress(step);
        stepsProgress.clearAnimation();
    }

    public void showSkip() {
        skipTV.setVisibility(View.VISIBLE);
    }

    public void hideSkip() {
        skipTV.setVisibility(View.GONE);
    }

    public TextView getSkipTV() {
        return skipTV;
    }

    public void showStep() {
        icStep.setVisibility(View.VISIBLE);
    }

    public void hideStep() {
        icStep.setVisibility(View.GONE);
    }

    public void setStep(int step) {
        switch (step) {
            case 1:
                icStep.setImageResource(R.drawable.ic_step1);
                break;
            case 2:
                icStep.setImageResource(R.drawable.ic_step2);
                break;
            case 3:
                icStep.setImageResource(R.drawable.ic_step3);
                break;
        }
    }

    public ImageView getBackBtn() {
        return backBtn;
    }

    public void showBackBtn() {
        backBtn.setVisibility(View.VISIBLE);
    }

    public void hideBackBtn() {
        backBtn.setVisibility(View.GONE);
    }

    public void hideCropOptions() {
        cropOptionsLayout.setVisibility(View.GONE);
    }

    public void showCropOptions() {
        cropOptionsLayout.setVisibility(View.VISIBLE);
    }

    public ImageView getRotateLeftButton() {
        return rotateLeftBtn;
    }

    public ImageView getRotateRightButton() {
        return rotateRightBtn;
    }

    public TextView getDoneButton() {
        return doneBtn;
    }

    public void showProfileImage() {
        profileImg.setVisibility(View.VISIBLE);
    }

    public void setProfileImage(String url) {
        profileImg.setVisibility(View.VISIBLE);
        if (ParseUser.getCurrentUser().getString(PARAM_PROFILE_PIC) != null && ParseUser.getCurrentUser().getString(PARAM_PROFILE_PIC).length() > 0)
            Picasso.get()
                    .load(ParseUser.getCurrentUser().getString(PARAM_PROFILE_PIC))
                    .centerCrop()
                    .resize(500, 500)
                    .placeholder(R.drawable.image_placeholder)
                    .into(profileImg);
        else
            Picasso.get()
                    .load(R.drawable.image_placeholder)
                    .centerCrop()
                    .resize(500, 500)
                    .placeholder(R.drawable.image_placeholder)
                    .into(profileImg);
    }

    public void hideProfileImage() {
        profileImg.setVisibility(View.GONE);
    }

    public void hideFansCountView(){
        fansCountView.setVisibility(View.GONE);
    }
    public void showFansCountView(){
        fansCountView.setVisibility(View.VISIBLE);
    }
    public void hideCountsView() {
        fans_count_view.setVisibility(View.GONE);
    }

    public void showCountsView() {
        fans_count_view.setVisibility(View.VISIBLE);
    }

    public LinearLayout getFansCountView() {
        return fans_count_view;
    }

    public RelativeLayout getBtnLikesFilter() {
        return like;
    }

    public RelativeLayout getBtnCrushFilter() {
        return crush;
    }

    public RelativeLayout getBtnMaybeFilter() {
        return maybe;
    }

    public TextView getLikesCount() {
        return likeCount;
    }

    public TextView getLikesCountTV() {
        return likes_count_tv;
    }

    public TextView getCrushCount() {
        return crushCount;
    }

    public TextView getCrushCountTV() {
        return crush_count_tv;
    }

    public TextView getMaybeCount() {
        return maybeCount;
    }

    public TextView getMaybeCountTV() {
        return may_be_count_tv;
    }
}
