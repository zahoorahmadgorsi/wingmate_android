package com.app.wingmate.ui.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.app.wingmate.R;
import com.app.wingmate.ui.activities.MainActivity;
import com.app.wingmate.utils.ActivityUtility;
import com.app.wingmate.utils.Utilities;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.app.wingmate.utils.AppConstants.ERROR;
import static com.app.wingmate.utils.AppConstants.PARAM_GENDER;
import static com.app.wingmate.utils.AppConstants.PARAM_NICK;
import static com.app.wingmate.utils.AppConstants.PARAM_QUESTIONNAIRE_FILLED;
import static com.app.wingmate.utils.AppConstants.SUCCESS;
import static com.app.wingmate.utils.AppConstants.WARNING;
import static com.app.wingmate.utils.CommonKeys.KEY_EMAIL_TAG;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_EMAIL_VERIFY;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_LOGIN;
import static com.app.wingmate.utils.Utilities.showToast;

public class SignupFragment extends BaseFragment {

    public static final String TAG = SignupFragment.class.getName();

    Unbinder unbinder;

    @BindView(R.id.btn_continue)
    Button continueBtn;

    @BindView(R.id.layout_video)
    LinearLayout layoutVideo;
    @BindView(R.id.details_tv)
    TextView detailsTV;

    @BindView(R.id.layout_gender)
    LinearLayout layoutGender;
    @BindView(R.id.gender_male)
    ImageView genderMaleBtn;
    @BindView(R.id.gender_female)
    ImageView genderFemaleBtn;
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

    private boolean showPassword = false;
    private String gender = "";
    private int step = 1;

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

        layoutVideo.setVisibility(View.VISIBLE);
        layoutGender.setVisibility(View.GONE);
        layoutInfo.setVisibility(View.GONE);
        continueBtn.setSelected(true);
        continueBtn.setEnabled(true);
        continueBtn.setClickable(true);
        continueBtn.setAlpha(1.0f);

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

        nickET.setOnFocusChangeListener((arg0, isFocus) -> {
            if (getActivity()==null || nickET==null) return;
            if (isFocus) {
                nickET.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.field_bg_selected, null));
            } else {
                nickET.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.field_bg_unselected, null));
            }
        });

        emailET.setOnFocusChangeListener((arg0, isFocus) -> {
            if (getActivity()==null || emailET==null) return;
            if (isFocus) {
                emailET.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.field_bg_selected, null));
            } else {
                emailET.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.field_bg_unselected, null));
            }
        });

        passwordET.setOnFocusChangeListener((arg0, isFocus) -> {
            if (getActivity()==null || passwordET==null) return;
            if (isFocus) {
                passwordET.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.field_bg_selected, null));
            } else {
                passwordET.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.field_bg_unselected, null));
            }
        });
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

    @OnClick({R.id.play_btn, R.id.gender_male, R.id.gender_female, R.id.btn_continue, R.id.btn_back, R.id.show_password, R.id.terms})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.show_password:
                showPassword = !showPassword;
                if (showPassword) {
                    passwordET.setTransformationMethod(null);
                    showPasswordIcon.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorPrimary), android.graphics.PorterDuff.Mode.MULTIPLY);
                } else {
                    passwordET.setTransformationMethod(new PasswordTransformationMethod());
                    showPasswordIcon.setColorFilter(ContextCompat.getColor(getContext(), R.color.grey), android.graphics.PorterDuff.Mode.MULTIPLY);
                }
                break;
            case R.id.terms:
                break;
            case R.id.play_btn:
                break;
            case R.id.gender_male:
                gender = "male";
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
                gender = "female";
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
                switch (step) {
                    case 1:
                        step = 2;
                        ((MainActivity) getActivity()).showTopView();
                        ((MainActivity) getActivity()).showScreenTitle();
                        ((MainActivity) getActivity()).setScreenTitle("Hi, Wing Mate");
                        ((MainActivity) getActivity()).showStep();
                        ((MainActivity) getActivity()).setStep(step);
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
                        ((MainActivity) getActivity()).setScreenTitle("Hi, Wing Mate");
                        ((MainActivity) getActivity()).showStep();
                        ((MainActivity) getActivity()).setStep(step);
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
                        if (isValid()) {
                            signUpUser();
                        }
                        break;
                }
                break;
            case R.id.btn_back:
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
                        layoutVideo.setVisibility(View.VISIBLE);
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
                        ((MainActivity) getActivity()).setScreenTitle("Hi, Wing Mate");
                        ((MainActivity) getActivity()).showStep();
                        ((MainActivity) getActivity()).setStep(step);
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
                break;
            default:
                break;
        }
    }

    private boolean isValid() {
        if (emailET.getText().toString().isEmpty()) {
            errorEmailLayout.setVisibility(View.VISIBLE);
            errorEmailTV.setText(R.string.email_req);
            emailET.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.field_bg_error, null));
            return false;
        } else if (!Utilities.isValidEmail(emailET.getText().toString())) {
            errorEmailLayout.setVisibility(View.VISIBLE);
            errorEmailTV.setText(R.string.invalid_email);
            emailET.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.field_bg_error, null));
            return false;
        } else if (passwordET.getText().toString().isEmpty()) {
            errorPasswordLayout.setVisibility(View.VISIBLE);
            errorPasswordTV.setText(R.string.pass_req);
            passwordET.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.field_bg_error, null));
            return false;
        } else if (passwordET.getText().toString().length()<8) {
            errorPasswordLayout.setVisibility(View.VISIBLE);
            errorPasswordTV.setText(R.string.pass_invalid);
            passwordET.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.field_bg_error, null));
            return false;
        } else {
            return true;
        }
    }

    private void signUpUser() {
        if (Utilities.isInternetAvailable(getContext())) {
            Utilities.hideKeyboard(getView());
            dialog.show();
//            dialog.show(getActivity().getSupportFragmentManager(), "progress_dialog");
            ParseUser user = new ParseUser();
            user.setUsername(emailET.getText().toString());
            user.setPassword(passwordET.getText().toString());
            user.setEmail(emailET.getText().toString());
            user.put(PARAM_GENDER, gender);
            user.put(PARAM_NICK, nickET.getText().toString());
            user.put(PARAM_QUESTIONNAIRE_FILLED, false);
            user.signUpInBackground(e -> {
                if (e == null) {
                    ActivityUtility.startEmailVerifyActivity(getActivity(), KEY_FRAGMENT_EMAIL_VERIFY, emailET.getText().toString(), nickET.getText().toString());
                } else {
                    ParseUser.logOut();
                    showToast(getActivity(), getContext(), e.getMessage(), ERROR);
                }
            });
        } else {
            showToast(getActivity(), getContext(), "Couldn't connect to internet!", WARNING);
        }
    }
}
