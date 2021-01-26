package com.app.wingmate.base;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.app.wingmate.R;

import me.tankery.lib.circularseekbar.CircularSeekBar;

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
    ImageView icStep;

    RelativeLayout stepsView;
    CircularSeekBar stepsProgress;
    TextView stepsCountTV;
    TextView skipTV;

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
        icStep = (ImageView) findViewById(R.id.ic_step);

        stepsView = (RelativeLayout) findViewById(R.id.steps_view);
        stepsProgress = (CircularSeekBar) findViewById(R.id.steps_progress);
        stepsCountTV = (TextView) findViewById(R.id.steps_count_tv);
        skipTV = (TextView) findViewById(R.id.skip);

        hideTopView();
        hideStepView();
        hideSkip();
        hideStep();
        hideCropToolbar();
        hideBackBtn();
        hideCropOptions();
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

    public void showTopView() {
        topView.setVisibility(View.VISIBLE);
    }

    public void hideTopView() {
        topView.setVisibility(View.GONE);
    }

    public void setScreenTitle(String title) {
        this.screenTitle.setText(title);
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
}
