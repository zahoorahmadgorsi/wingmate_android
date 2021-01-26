package com.app.wingmate.ui.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.app.wingmate.R;
import com.app.wingmate.utils.Utilities;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.app.wingmate.utils.AppConstants.ERROR;
import static com.app.wingmate.utils.AppConstants.SUCCESS;
import static com.app.wingmate.utils.AppConstants.WARNING;
import static com.app.wingmate.utils.Utilities.showToast;

public class ForgotPasswordFragment extends BaseFragment {

    public static final String TAG = ForgotPasswordFragment.class.getName();

    Unbinder unbinder;

    @BindView(R.id.et_email)
    EditText emailET;
    @BindView(R.id.error_email)
    LinearLayout errorEmailLayout;
    @BindView(R.id.error_email_tv)
    TextView errorEmailTV;

    @BindView(R.id.btn_continue)
    Button continueBtn;

    public ForgotPasswordFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forgot_password, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        errorEmailLayout.setVisibility(View.INVISIBLE);
        continueBtn.setSelected(false);
        continueBtn.setEnabled(false);
        continueBtn.setClickable(false);
        continueBtn.setAlpha(0.5f);

        emailET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                errorEmailLayout.setVisibility(View.INVISIBLE);
                emailET.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.field_bg_selected, null));
                if (!emailET.getText().toString().isEmpty()) {
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

        emailET.setOnFocusChangeListener((arg0, isFocus) -> {
            if (getActivity() == null || emailET == null) return;
            if (isFocus) {
                emailET.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.field_bg_selected, null));
            } else {
                emailET.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.field_bg_unselected, null));
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

    @OnClick({R.id.btn_continue, R.id.btn_back})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.btn_continue:
                if (isValid()) {
                    forgotPassword();
                }
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
        } else {
            return true;
        }
    }

    private void forgotPassword() {
        if (Utilities.isInternetAvailable(getContext())) {
            Utilities.hideKeyboard(getView());
            dialog.show(getActivity().getSupportFragmentManager(), "progress_dialog");
            ParseUser.requestPasswordResetInBackground(emailET.getText().toString(),
                    e -> {
                        if (e == null) {
                            showToast(getActivity(), getContext(), "An email has been successfully sent with reset instructions.", SUCCESS);
                            new Handler().postDelayed(() -> {
                                getActivity().onBackPressed();
                            }, 2000);
                        } else {
                            showToast(getActivity(), getContext(), e.getMessage(), ERROR);
                        }
                        dialog.dismiss();
                    });
        } else {
            showToast(getActivity(), getContext(), "Couldn't connect to internet!", WARNING);
        }
    }
}