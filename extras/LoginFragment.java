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
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.app.wingmate.R;
import com.app.wingmate.utils.ActivityUtility;
import com.app.wingmate.utils.Utilities;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.app.wingmate.utils.AppConstants.ERROR;
import static com.app.wingmate.utils.AppConstants.PARAM_NICK;
import static com.app.wingmate.utils.AppConstants.PARAM_QUESTIONNAIRE_FILLED;
import static com.app.wingmate.utils.AppConstants.SUCCESS;
import static com.app.wingmate.utils.AppConstants.WARNING;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_DUMMY;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_EMAIL_VERIFY;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_FORGOT_PASSWORD;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_QUESTIONNAIRE;
import static com.app.wingmate.utils.Utilities.showToast;

public class LoginFragment extends BaseFragment {

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

    @BindView(R.id.btn_signin)
    Button signinBtn;

    private boolean showPassword = false;

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

        emailET.setOnFocusChangeListener((arg0, isFocus) -> {
            if (getActivity() == null || emailET == null) return;
            if (isFocus) {
                emailET.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.field_bg_selected, null));
            } else {
                emailET.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.field_bg_unselected, null));
//                emailET.setBackground(getResources().getDrawable(R.drawable.field_bg_unselected));
            }
        });

        passwordET.setOnFocusChangeListener((arg0, isFocus) -> {
            if (getActivity() == null || passwordET == null) return;
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

    @OnClick({R.id.btn_signin, R.id.forgot_password, R.id.btn_back, R.id.show_password})
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
            case R.id.btn_signin:
                if (isValid()) {
                    loginUser();
                }
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

    private void loginUser() {
        if (Utilities.isInternetAvailable(getContext())) {
            Utilities.hideKeyboard(getView());
            dialog.show();
//            dialog.show(getActivity().getSupportFragmentManager(), "progress_dialog");
            ParseUser.logInInBackground(emailET.getText().toString(), passwordET.getText().toString(), new LogInCallback() {
                @Override
                public void done(ParseUser parseUser, ParseException e) {
                    if (parseUser != null) {
                        if (parseUser.getBoolean(PARAM_QUESTIONNAIRE_FILLED)) {
                            showToast(getActivity(), getContext(), "Welcome back" + emailET.getText().toString() + "!", SUCCESS);
                            ActivityUtility.startActivity(getActivity(), KEY_FRAGMENT_DUMMY);
                        } else {
                            ActivityUtility.startActivity(getActivity(), KEY_FRAGMENT_QUESTIONNAIRE);
                        }

                    } else {
                        if (e.getMessage().equalsIgnoreCase("User email is not verified.")) {
                            ActivityUtility.startEmailVerifyActivity(getActivity(), KEY_FRAGMENT_EMAIL_VERIFY, parseUser.getEmail(), parseUser.getString(PARAM_NICK));
//                            ActivityUtility.startEmailVerifyActivity(getActivity(), KEY_FRAGMENT_EMAIL_VERIFY, emailET.getText().toString(), "");
                        } else {
                            ParseUser.logOut();
                            showToast(getActivity(), getContext(), e.getMessage(), ERROR);
                        }
                    }
                    dialog.dismiss();
                }
            });
        } else {
            showToast(getActivity(), getContext(), "Couldn't connect to internet!", WARNING);
        }
    }
}
