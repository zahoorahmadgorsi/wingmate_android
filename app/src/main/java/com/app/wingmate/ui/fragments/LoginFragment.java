package com.app.wingmate.ui.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.core.content.res.ResourcesCompat;

import com.app.wingmate.R;
import com.app.wingmate.base.BaseFragment;
import com.app.wingmate.base.BaseInteractor;
import com.app.wingmate.base.BasePresenter;
import com.app.wingmate.base.BaseView;
import com.app.wingmate.ui.activities.SplashActivity;
import com.app.wingmate.utils.ActivityUtility;
import com.app.wingmate.utils.SharedPrefers;
import com.app.wingmate.utils.Utilities;
import com.parse.ParseException;
import com.parse.ParseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.app.wingmate.utils.AppConstants.ERROR;
import static com.app.wingmate.utils.AppConstants.MANDATORY;
import static com.app.wingmate.utils.AppConstants.PARAM_ACCOUNT_STATUS;
import static com.app.wingmate.utils.AppConstants.PARAM_IS_MEDIA_APPROVED;
import static com.app.wingmate.utils.AppConstants.PARAM_IS_PHOTO_SUBMITTED;
import static com.app.wingmate.utils.AppConstants.PARAM_IS_VIDEO_SUBMITTED;
import static com.app.wingmate.utils.AppConstants.PARAM_MANDATORY_QUESTIONNAIRE_FILLED;
import static com.app.wingmate.utils.AppConstants.PARAM_PROFILE_PIC;
import static com.app.wingmate.utils.AppConstants.PENDING;
import static com.app.wingmate.utils.AppConstants.REJECTED;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_FORGOT_PASSWORD;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_DASHBOARD;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_PRE_LOGIN;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_QUESTIONNAIRE;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_UPLOAD_PHOTO_VIDEO_PROFILE;
import static com.app.wingmate.utils.CommonKeys.PREF_EMAIL;
import static com.app.wingmate.utils.CommonKeys.PREF_PASSWORD;
import static com.app.wingmate.utils.Utilities.showToast;

public class LoginFragment extends BaseFragment implements BaseView {

    public static final String TAG = LoginFragment.class.getName();

    Unbinder unbinder;

    @BindView(R.id.layout)
    RelativeLayout layout;
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
    @BindView(R.id.remember_checkbox)
    AppCompatCheckBox rememberCheckbox;
    @BindView(R.id.btn_signin)
    Button signinBtn;

    private boolean showPassword = false;

    private BasePresenter loginPresenter;

    public LoginFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loginPresenter = new BasePresenter(this, new BaseInteractor());

        errorEmailLayout.setVisibility(View.INVISIBLE);
        errorPasswordLayout.setVisibility(View.INVISIBLE);
        signinBtn.setSelected(false);
        signinBtn.setEnabled(false);
        signinBtn.setClickable(false);
        signinBtn.setAlpha(0.5f);

        emailET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                errorEmailLayout.setVisibility(View.INVISIBLE);
                emailET.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.field_bg_selected, null));
                if (!emailET.getText().toString().isEmpty() && !passwordET.getText().toString().isEmpty()) {
                    signinBtn.setSelected(true);
                    signinBtn.setEnabled(true);
                    signinBtn.setClickable(true);
                    signinBtn.setAlpha(1.0f);
                } else {
                    signinBtn.setSelected(false);
                    signinBtn.setEnabled(false);
                    signinBtn.setClickable(false);
                    signinBtn.setAlpha(0.5f);
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
                    signinBtn.setSelected(true);
                    signinBtn.setEnabled(true);
                    signinBtn.setClickable(true);
                    signinBtn.setAlpha(1.0f);
                } else {
                    signinBtn.setSelected(false);
                    signinBtn.setEnabled(false);
                    signinBtn.setClickable(false);
                    signinBtn.setAlpha(0.5f);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        emailET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View arg0, boolean isFocus) {
                if (LoginFragment.this.getActivity() == null || emailET == null) return;
                if (isFocus) {
                    emailET.setBackground(ResourcesCompat.getDrawable(LoginFragment.this.getResources(), R.drawable.field_bg_selected, null));
                } else {
                    emailET.setBackground(ResourcesCompat.getDrawable(LoginFragment.this.getResources(), R.drawable.field_bg_unselected, null));
//                emailET.setBackground(getResources().getDrawable(R.drawable.field_bg_unselected));
                }
            }
        });

        passwordET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View arg0, boolean isFocus) {
                System.out.println("==========isFocus=======" + isFocus);
                if (LoginFragment.this.getActivity() == null || passwordET == null) return;
                if (isFocus) {
                    passwordET.setBackground(ResourcesCompat.getDrawable(LoginFragment.this.getResources(), R.drawable.field_bg_selected, null));
                } else {
                    passwordET.setBackground(ResourcesCompat.getDrawable(LoginFragment.this.getResources(), R.drawable.field_bg_unselected, null));
                }
            }
        });

//        rememberCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            if (isChecked) {
//                SharedPrefers.saveString(requireContext(), PREF_EMAIL, emailET.getText().toString());
//                SharedPrefers.saveString(requireContext(), PREF_PASSWORD, passwordET.getText().toString());
//            } else {
//                SharedPrefers.saveString(requireContext(), PREF_EMAIL, "");
//                SharedPrefers.saveString(requireContext(), PREF_PASSWORD, "");
//            }
//        });
    }

    @Override
    public void onResume() {
        super.onResume();
        String e = SharedPrefers.getString(requireContext(), PREF_EMAIL, "");
        String p = SharedPrefers.getString(requireContext(), PREF_PASSWORD, "");

        if (!e.isEmpty()) {
            emailET.setText("");
            emailET.append(e);
            rememberCheckbox.setChecked(true);
            emailET.setBackground(ResourcesCompat.getDrawable(LoginFragment.this.getResources(), R.drawable.field_bg_unselected, null));
        }
        if (!p.isEmpty()) {
            passwordET.setText("");
            passwordET.append(p);
            rememberCheckbox.setChecked(true);
            passwordET.setBackground(ResourcesCompat.getDrawable(LoginFragment.this.getResources(), R.drawable.field_bg_unselected, null));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.remember_me, R.id.btn_signin, R.id.forgot_password, R.id.btn_back, R.id.show_password})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.remember_me:
                rememberCheckbox.toggle();
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
                    passwordET.setBackground(ResourcesCompat.getDrawable(LoginFragment.this.getResources(), R.drawable.field_bg_unselected, null));

                break;
            case R.id.btn_signin:
                showProgress();
                loginPresenter.validateAndLogin(getContext(), emailET.getText().toString().trim(), passwordET.getText().toString().trim());
                break;
            case R.id.forgot_password:
                ActivityUtility.startActivity(getActivity(), KEY_FRAGMENT_FORGOT_PASSWORD);
                break;
            case R.id.btn_back:
                getActivity().onBackPressed();
                break;
            default:
                break;
        }
    }

    @Override
    public void showProgress() {
        Utilities.hideKeyboard(getView());
//        dialog.show(getActivity().getSupportFragmentManager(), "progress_dialog");
        dialog.show();
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
    public void setEmailVerificationError(ParseException e) {
        dismissProgress();
        ParseUser.logOut();
        showToast(getActivity(), getContext(), "User email is not verified", ERROR);
//        showToast(getActivity(), getContext(), "Email is not verified yet!", ERROR);
    }

    @Override
    public void setLoginSuccess(ParseUser parseUser) {
        dismissProgress();

        if (ParseUser.getCurrentUser().getInt(PARAM_ACCOUNT_STATUS) == REJECTED) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(requireContext());
            dialog
//                    .setTitle(getString(R.string.app_name))
//                    .setIcon(R.drawable.app_heart)
                    .setCancelable(false)
                    .setMessage("Your profile has been rejected by the admin!")
                    .setNegativeButton("OK", (dialoginterface, i) -> {
                        dialoginterface.cancel();
                        ParseUser.logOut();
                        ActivityUtility.startActivity(requireActivity(), KEY_FRAGMENT_PRE_LOGIN);
                    }).show();

        }
//        else if (!ParseUser.getCurrentUser().getBoolean(PARAM_IS_PHOTO_SUBMITTED) || !ParseUser.getCurrentUser().getBoolean(PARAM_IS_VIDEO_SUBMITTED)) {
//            ActivityUtility.startProfileMediaActivity(requireActivity(), KEY_FRAGMENT_UPLOAD_PHOTO_VIDEO_PROFILE);
//        }
//        else if (!ParseUser.getCurrentUser().getBoolean(PARAM_MANDATORY_QUESTIONNAIRE_FILLED)) {
//            ActivityUtility.startQuestionnaireActivity(getActivity(), KEY_FRAGMENT_QUESTIONNAIRE, MANDATORY);
//        }
        else {
            if (rememberCheckbox.isChecked()) {
                SharedPrefers.saveString(requireContext(), PREF_EMAIL, emailET.getText().toString());
                SharedPrefers.saveString(requireContext(), PREF_PASSWORD, passwordET.getText().toString());
            } else {
                SharedPrefers.saveString(requireContext(), PREF_EMAIL, "");
                SharedPrefers.saveString(requireContext(), PREF_PASSWORD, "");
            }
            ActivityUtility.startActivity(getActivity(), KEY_FRAGMENT_DASHBOARD);
        }
    }

    @Override
    public void setResponseError(ParseException e) {
        dismissProgress();
        ParseUser.logOut();
        showToast(getActivity(), getContext(), e.getMessage(), ERROR);
    }
}