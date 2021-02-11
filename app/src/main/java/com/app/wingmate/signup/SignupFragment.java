package com.app.wingmate.signup;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.app.wingmate.R;
import com.app.wingmate.base.BaseInteractor;
import com.app.wingmate.base.BasePresenter;
import com.app.wingmate.base.BaseView;
import com.app.wingmate.login.LoginFragment;
import com.app.wingmate.ui.activities.MainActivity;
import com.app.wingmate.base.BaseFragment;
import com.app.wingmate.utils.ActivityUtility;
import com.app.wingmate.utils.Utilities;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.potyvideo.library.AndExoPlayerView;
import com.rd.PageIndicatorView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.app.wingmate.utils.AppConstants.ERROR;
import static com.app.wingmate.utils.AppConstants.FEMALE;
import static com.app.wingmate.utils.AppConstants.MALE;
import static com.app.wingmate.utils.AppConstants.PARAM_VIDEO_LINK;
import static com.app.wingmate.utils.AppConstants.REQUEST_CODE_TERMS;
import static com.app.wingmate.utils.AppConstants.WARNING;
import static com.app.wingmate.utils.CommonKeys.KEY_ACTIVITY_TAG;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_EMAIL_VERIFY;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_TERMS_AND_CONDITIONS;
import static com.app.wingmate.utils.Utilities.showToast;

public class SignupFragment extends BaseFragment implements BaseView, ViewPager.OnPageChangeListener{

    public static final String TAG = SignupFragment.class.getName();

    Unbinder unbinder;

    @BindView(R.id.btn_continue)
    Button continueBtn;

    @BindView(R.id.layout_tutorial)
    LinearLayout layoutTutorial;

    @BindView(R.id.layout_video)
    LinearLayout layoutVideo;
    @BindView(R.id.details_tv)
    TextView detailsTV;

    @BindView(R.id.video_view)
    AndExoPlayerView videoView;
    @BindView(R.id.img_placeholder)
    ImageView videoImgPlaceholder;
    @BindView(R.id.play_btn)
    ImageView playBtn;

    @BindView(R.id.layout_gender)
    LinearLayout layoutGender;
    @BindView(R.id.gender_male)
    LinearLayout genderMaleBtn;
    @BindView(R.id.gender_female)
    LinearLayout genderFemaleBtn;
    @BindView(R.id.et_nick)
    EditText nickET;
    @BindView(R.id.error_nick)
    LinearLayout errorNickLayout;
    @BindView(R.id.error_nick_tv)
    TextView errorNickTV;

    @BindView(R.id.layout_info)
    LinearLayout layoutInfo;
    @BindView(R.id.et_email)
    EditText emailET;
    @BindView(R.id.error_email)
    LinearLayout errorEmailLayout;
    @BindView(R.id.error_email_tv)
    TextView errorEmailTV;
    @BindView(R.id.et_password)
    EditText passwordET;
    @BindView(R.id.error_password)
    LinearLayout errorPasswordLayout;
    @BindView(R.id.error_password_tv)
    TextView errorPasswordTV;
    @BindView(R.id.show_password)
    ImageView showPasswordIcon;
    @BindView(R.id.above21_checkbox)
    AppCompatCheckBox above21Checkbox;

    private String videoUrl = "";
    private boolean showPassword = false;
    private String gender = "";
    public int step = 1;

    private BasePresenter signupPresenter;
    private MediaController mediacontroller;

    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.pageIndicatorView)
    PageIndicatorView indicator;

    private int[] data = {
            R.drawable.app_heart, R.drawable.app_heart, R.drawable.app_heart, R.drawable.app_heart, R.drawable.app_heart
    };
//
//    private int[] tutorialLayouts = {
//            R.layout.layout_tut1, R.layout.layout_tut2, R.layout.layout_tut3, R.layout.layout_tut4, R.layout.layout_tut5
//    };

    private TextView skip;


    public SignupFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        signupPresenter = new BasePresenter(this, new BaseInteractor());

        mediacontroller = new MediaController(getContext());

//        fetchVideoLinkFormParse();

        videoImgPlaceholder.setVisibility(View.VISIBLE);
        playBtn.setVisibility(View.VISIBLE);
        videoView.setVisibility(View.INVISIBLE);

        layoutTutorial.setVisibility(View.VISIBLE);
        layoutVideo.setVisibility(View.GONE);
        layoutGender.setVisibility(View.GONE);
        layoutInfo.setVisibility(View.GONE);
        continueBtn.setSelected(true);
        continueBtn.setEnabled(true);
        continueBtn.setClickable(true);
        continueBtn.setAlpha(1.0f);

        viewPager.setAdapter(new CustomAdapter());
        viewPager.setOffscreenPageLimit(4);
        viewPager.setPageTransformer(false, new FadePageTransformer());
        viewPager.addOnPageChangeListener(this);
        indicator.setViewPager(viewPager);

        genderMaleBtn.setBackgroundResource(R.drawable.bg_disabled);
        genderFemaleBtn.setBackgroundResource(R.drawable.bg_disabled);

        step = 1;

        nickET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                errorNickLayout.setVisibility(View.INVISIBLE);
                nickET.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.field_bg_selected, null));

                if (!gender.isEmpty() && !nickET.getText().toString().isEmpty()) {
                    continueBtn.setSelected(true);
                    continueBtn.setEnabled(true);
                    continueBtn.setClickable(true);
                    continueBtn.setAlpha(1.0f);
                } else {
                    continueBtn.setSelected(false);
                    continueBtn.setEnabled(false);
                    continueBtn.setClickable(false);
                    continueBtn.setAlpha(0.5f);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        errorNickLayout.setVisibility(View.INVISIBLE);
        errorEmailLayout.setVisibility(View.INVISIBLE);
        errorPasswordLayout.setVisibility(View.INVISIBLE);

        emailET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                errorEmailLayout.setVisibility(View.INVISIBLE);
                emailET.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.field_bg_selected, null));

                if (!emailET.getText().toString().isEmpty() && !passwordET.getText().toString().isEmpty()) {
                    continueBtn.setSelected(true);
                    continueBtn.setEnabled(true);
                    continueBtn.setClickable(true);
                    continueBtn.setAlpha(1.0f);
                } else {
                    continueBtn.setSelected(false);
                    continueBtn.setEnabled(false);
                    continueBtn.setClickable(false);
                    continueBtn.setAlpha(0.5f);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        passwordET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                errorPasswordLayout.setVisibility(View.INVISIBLE);
                passwordET.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.field_bg_selected, null));

                if (!emailET.getText().toString().isEmpty() && !passwordET.getText().toString().isEmpty()) {
                    continueBtn.setSelected(true);
                    continueBtn.setEnabled(true);
                    continueBtn.setClickable(true);
                    continueBtn.setAlpha(1.0f);
                } else {
                    continueBtn.setSelected(false);
                    continueBtn.setEnabled(false);
                    continueBtn.setClickable(false);
                    continueBtn.setAlpha(0.5f);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        nickET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View arg0, boolean isFocus) {
                if (SignupFragment.this.getActivity() == null || nickET == null) return;
                if (isFocus) {
                    nickET.setBackground(ResourcesCompat.getDrawable(SignupFragment.this.getResources(), R.drawable.field_bg_selected, null));
                } else {
                    nickET.setBackground(ResourcesCompat.getDrawable(SignupFragment.this.getResources(), R.drawable.field_bg_unselected, null));
                }
            }
        });

        emailET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View arg0, boolean isFocus) {
                if (SignupFragment.this.getActivity() == null || emailET == null) return;
                if (isFocus) {
                    emailET.setBackground(ResourcesCompat.getDrawable(SignupFragment.this.getResources(), R.drawable.field_bg_selected, null));
                } else {
                    emailET.setBackground(ResourcesCompat.getDrawable(SignupFragment.this.getResources(), R.drawable.field_bg_unselected, null));
                }
            }
        });

        passwordET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View arg0, boolean isFocus) {
                if (SignupFragment.this.getActivity() == null || passwordET == null) return;
                if (isFocus) {
                    passwordET.setBackground(ResourcesCompat.getDrawable(SignupFragment.this.getResources(), R.drawable.field_bg_selected, null));
                } else {
                    passwordET.setBackground(ResourcesCompat.getDrawable(SignupFragment.this.getResources(), R.drawable.field_bg_unselected, null));
                }
            }
        });
    }

    private void setupVideoView() {
        RequestOptions requestOptions = new RequestOptions();
        Glide.with(getContext())
                .load(videoUrl)
                .apply(requestOptions)
                .thumbnail(Glide.with(getContext()).load(videoUrl))
                .placeholder(R.drawable.video_loading)
                .into(videoImgPlaceholder);
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

    @OnClick({R.id.above21, R.id.play_btn, R.id.gender_male, R.id.gender_female, R.id.btn_continue, R.id.btn_back, R.id.show_password, R.id.terms})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.above21:
                above21Checkbox.toggle();
                break;
            case R.id.show_password:
                showPassword = !showPassword;
                if (showPassword) {
                    passwordET.setTransformationMethod(null);
                    showPasswordIcon.setImageResource(R.drawable.password_show);
//                    showPasswordIcon.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorPrimary), android.graphics.PorterDuff.Mode.MULTIPLY);
                } else {
                    passwordET.setTransformationMethod(new PasswordTransformationMethod());
                    showPasswordIcon.setImageResource(R.drawable.password_hide);
//                    showPasswordIcon.setColorFilter(ContextCompat.getColor(getContext(), R.color.grey), android.graphics.PorterDuff.Mode.MULTIPLY);
                }
                if (passwordET.isFocused())
                    passwordET.setSelection(passwordET.getText().length());
                else
                    passwordET.setBackground(ResourcesCompat.getDrawable(SignupFragment.this.getResources(), R.drawable.field_bg_unselected, null));

                break;
            case R.id.terms:
                break;
            case R.id.play_btn:
                if (videoUrl != null && !TextUtils.isEmpty(videoUrl)) {
                    videoImgPlaceholder.setVisibility(View.GONE);
                    playBtn.setVisibility(View.GONE);
                    videoView.setVisibility(View.VISIBLE);
                    videoView.setSource(videoUrl);
                } else {
                    showToast(getActivity(), getContext(), "Unable to load video!", ERROR);
                }
                break;
            case R.id.gender_male:
                gender = MALE;
                genderMaleBtn.setBackgroundResource(R.drawable.bg_enabled);
                genderFemaleBtn.setBackgroundResource(R.drawable.bg_disabled);
                if (!gender.isEmpty() && !nickET.getText().toString().isEmpty()) {
                    continueBtn.setSelected(true);
                    continueBtn.setEnabled(true);
                    continueBtn.setClickable(true);
                    continueBtn.setAlpha(1.0f);
                } else {
                    continueBtn.setSelected(false);
                    continueBtn.setEnabled(false);
                    continueBtn.setClickable(false);
                    continueBtn.setAlpha(0.5f);
                }
                break;
            case R.id.gender_female:
                gender = FEMALE;
                genderMaleBtn.setBackgroundResource(R.drawable.bg_disabled);
                genderFemaleBtn.setBackgroundResource(R.drawable.bg_enabled);
                if (!gender.isEmpty() && !nickET.getText().toString().isEmpty()) {
                    continueBtn.setSelected(true);
                    continueBtn.setEnabled(true);
                    continueBtn.setClickable(true);
                    continueBtn.setAlpha(1.0f);
                } else {
                    continueBtn.setSelected(false);
                    continueBtn.setEnabled(false);
                    continueBtn.setClickable(false);
                    continueBtn.setAlpha(0.5f);
                }
                break;
            case R.id.btn_continue:
                if (videoView != null) {
                    videoView.pausePlayer();
                }
                switch (step) {
                    case 1:
                        step = 2;
                        ((MainActivity) getActivity()).showTopView();
                        ((MainActivity) getActivity()).showScreenTitle();
                        if (nickET.getText().toString().isEmpty()) ((MainActivity) getActivity()).setScreenTitle("Hi, Wingmate");
                        else ((MainActivity) getActivity()).setScreenTitle("Hi, " + nickET.getText().toString());
                        ((MainActivity) getActivity()).showStep();
                        ((MainActivity) getActivity()).setStep(step);
                        layoutTutorial.setVisibility(View.GONE);
                        layoutVideo.setVisibility(View.GONE);
                        layoutGender.setVisibility(View.VISIBLE);
                        layoutInfo.setVisibility(View.GONE);
                        if (!gender.isEmpty() && !nickET.getText().toString().isEmpty()) {
                            continueBtn.setSelected(true);
                            continueBtn.setEnabled(true);
                            continueBtn.setClickable(true);
                            continueBtn.setAlpha(1.0f);
                        } else {
                            continueBtn.setSelected(false);
                            continueBtn.setEnabled(false);
                            continueBtn.setClickable(false);
                            continueBtn.setAlpha(0.5f);
                        }
                        break;
                    case 2:
                        step = 3;
                        ((MainActivity) getActivity()).showTopView();
                        ((MainActivity) getActivity()).showScreenTitle();
                        ((MainActivity) getActivity()).setScreenTitle("Hi, " + nickET.getText().toString());
                        ((MainActivity) getActivity()).showStep();
                        ((MainActivity) getActivity()).setStep(step);
                        layoutTutorial.setVisibility(View.GONE);
                        layoutVideo.setVisibility(View.GONE);
                        layoutGender.setVisibility(View.GONE);
                        layoutInfo.setVisibility(View.VISIBLE);

                        if (!emailET.getText().toString().isEmpty() && !passwordET.getText().toString().isEmpty()) {
                            continueBtn.setSelected(true);
                            continueBtn.setEnabled(true);
                            continueBtn.setClickable(true);
                            continueBtn.setAlpha(1.0f);
                        } else {
                            continueBtn.setSelected(false);
                            continueBtn.setEnabled(false);
                            continueBtn.setClickable(false);
                            continueBtn.setAlpha(0.5f);
                        }
                        break;
                    case 3:
                        if (above21Checkbox.isChecked())
                            signupPresenter.signUpFormValidate(getContext(), nickET.getText().toString(), gender, emailET.getText().toString().trim(), passwordET.getText().toString().trim());
                        else
                            showToast(requireActivity(), requireContext(), "You must be 21 or above", WARNING);
                        break;
                }
                break;
            case R.id.btn_back:
                onBackPressed();
                break;
            default:
                break;
        }
    }

    public int getStep() {
        return step;
    }

    public void onBackPressed() {
        if (ParseUser.getCurrentUser() != null) {
            getActivity().onBackPressed();
        } else {
            switch (step) {
                case 1:
                    getActivity().onBackPressed();
                    break;
                case 2:
                    step = 1;
                    ((MainActivity) getActivity()).showTopView();
                    ((MainActivity) getActivity()).hideScreenTitle();
                    ((MainActivity) getActivity()).showStep();
                    ((MainActivity) getActivity()).setStep(step);
                    layoutTutorial.setVisibility(View.VISIBLE);
                    layoutVideo.setVisibility(View.GONE);
                    layoutGender.setVisibility(View.GONE);
                    layoutInfo.setVisibility(View.GONE);
                    continueBtn.setSelected(true);
                    continueBtn.setEnabled(true);
                    continueBtn.setClickable(true);
                    continueBtn.setAlpha(1.0f);
                    break;
                case 3:
                    step = 2;
                    ((MainActivity) getActivity()).showTopView();
                    ((MainActivity) getActivity()).showScreenTitle();
                    if (nickET.getText().toString().isEmpty()) ((MainActivity) getActivity()).setScreenTitle("Hi, Wingmate");
                    else ((MainActivity) getActivity()).setScreenTitle("Hi, " + nickET.getText().toString());
                    ((MainActivity) getActivity()).showStep();
                    ((MainActivity) getActivity()).setStep(step);
                    layoutTutorial.setVisibility(View.GONE);
                    layoutVideo.setVisibility(View.GONE);
                    layoutGender.setVisibility(View.VISIBLE);
                    layoutInfo.setVisibility(View.GONE);
                    if (!gender.isEmpty() && !nickET.getText().toString().isEmpty()) {
                        continueBtn.setSelected(true);
                        continueBtn.setEnabled(true);
                        continueBtn.setClickable(true);
                        continueBtn.setAlpha(1.0f);
                    } else {
                        continueBtn.setSelected(false);
                        continueBtn.setEnabled(false);
                        continueBtn.setClickable(false);
                        continueBtn.setAlpha(0.5f);
                    }
                    break;
            }
        }
    }

    @Override
    public void showProgress() {
        Utilities.hideKeyboard(getView());
//        dialog.show(getActivity().getSupportFragmentManager(), "progress_dialog");
        dialog.show();
    }

    @Override
    public void setVideoLinkSuccess(ParseObject parseObject) {
        dismissProgress();
        videoUrl = parseObject.getString(PARAM_VIDEO_LINK);
        setupVideoView();
    }

    @Override
    public void setNickError() {
        dismissProgress();
        errorNickLayout.setVisibility(View.VISIBLE);
        errorNickTV.setText(R.string.nick_req);
        nickET.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.field_bg_error, null));
    }

    @Override
    public void setGenderError() {
        dialog.dismiss();
        showToast(getActivity(), getContext(), "Please select gender!", ERROR);
    }

    @Override
    public void setEmailError() {
        dismissProgress();
        errorEmailLayout.setVisibility(View.VISIBLE);
        errorEmailTV.setText(R.string.email_req);
        emailET.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.field_bg_error, null));
    }

    @Override
    public void setInvalidEmailError() {
        dismissProgress();
        errorEmailLayout.setVisibility(View.VISIBLE);
        errorEmailTV.setText(R.string.invalid_email);
        emailET.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.field_bg_error, null));
    }

    @Override
    public void setPasswordError() {
        dismissProgress();
        errorPasswordLayout.setVisibility(View.VISIBLE);
        errorPasswordTV.setText(R.string.pass_req);
        passwordET.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.field_bg_error, null));
    }

    @Override
    public void setInvalidPasswordError() {
        dismissProgress();
        errorPasswordLayout.setVisibility(View.VISIBLE);
        errorPasswordTV.setText(R.string.pass_invalid);
        passwordET.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.field_bg_error, null));
    }

    @Override
    public void setResponseSuccess() {
        dismissProgress();
        System.out.println("=============" + ParseUser.getCurrentUser().getEmail());
        System.out.println("=============" + ParseUser.getCurrentUser().getUsername());
        ActivityUtility.startEmailVerifyActivity(getActivity(), KEY_FRAGMENT_EMAIL_VERIFY, emailET.getText().toString(), nickET.getText().toString());
    }

    @Override
    public void setFormValidateSuccess() {
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.putExtra(KEY_ACTIVITY_TAG, KEY_FRAGMENT_TERMS_AND_CONDITIONS);
        startActivityForResult(intent, REQUEST_CODE_TERMS);
    }


    private void fetchVideoLinkFormParse() {
        showProgress();
        signupPresenter.getVideoLinkParse(getContext());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_TERMS && resultCode == Activity.RESULT_OK) {
            showProgress();
            if (ParseUser.getCurrentUser() != null) {
                signupPresenter.updateEmailViaParse(getContext(), nickET.getText().toString(), gender, emailET.getText().toString().trim(), passwordET.getText().toString().trim());
            } else {
                signupPresenter.signUpMeViaParse(getContext(), nickET.getText().toString(), gender, emailET.getText().toString().trim(), passwordET.getText().toString().trim());
            }
        }
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
//        if (position == (tutorialLayouts.length - 1)) {
//            skip.setText("START");
//        } else {
//            skip.setText("SKIP");
//        }
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
            TextView textView = new TextView(container.getContext());
            textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            textView.setText("App Tutorials");
            textView.setTextSize(getContext().getResources().getDimension(R.dimen.extra_small_txt_size));
            textView.setTextColor(getContext().getResources().getColor(R.color.text_black_color));
            Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/baloo_regular.ttf");
            textView.setGravity(Gravity.CENTER);
            textView.setTypeface(typeface);
            container.addView(textView);
//            ImageView imageView = new ImageView(container.getContext());
//            imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                    ViewGroup.LayoutParams.MATCH_PARENT));
//            imageView.setAdjustViewBounds(true);
//            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
//            container.addView(imageView);
//            Glide.with(container.getContext())
//                    .load(data[position])
////                    .override(600, 600)
//                    .into(imageView);
//            return imageView;
            return textView;


//            View v = LayoutInflater.from(container.getContext()).inflate(tutorialLayouts[position], container, false);
//            FontManager.applyBoldFont(container.getContext(), v.findViewById(R.id.root));
//            container.addView(v);
//            return v;

//            LayoutInflater inflater = (LayoutInflater) container.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//
//            int resId = -1;
//
//            switch (position) {
//                case 0:
//                    resId = R.layout.layout_tut1;
//                    break;
//                case 1:
//                    resId = R.layout.layout_tut2;
//                    break;
//                case 2:
//                    resId = R.layout.layout_tut3;
//                    break;
//                case 3:
//                    resId = R.layout.layout_tut4;
//                    break;
//                case 4:
//                    resId = R.layout.layout_tut5;
//                    break;
//
//            }
//
//            View view = inflater.inflate(resId, container, false);
//            ((ViewPager) container).addView(view, 0);
//            return view;

        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return data.length;
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

    public class FadePageTransformer implements ViewPager.PageTransformer {
        public void transformPage(View view, float position) {
            view.setTranslationX(view.getWidth() * -position);

            if (position <= -1.0F || position >= 1.0F) {
                view.setAlpha(0.0F);
            } else if (position == 0.0F) {
                view.setAlpha(1.0F);
            } else {
                view.setAlpha(1.0F - Math.abs(position));
            }
        }
    }

    public static class MyPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 5;

        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
//                case 0:
//                    return FragmentTut1.newInstance();
//                case 1:
//                    return FragmentTut2.newInstance();
//                case 2:
//                    return FragmentTut3.newInstance();
//                case 3:
//                    return FragmentTut4.newInstance();
//                case 4:
//                    return FragmentTut5.newInstance();
                default:
                    return null;
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            return "Page " + position;
        }

    }

}
