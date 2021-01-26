package com.app.wingmate.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.app.wingmate.R;
import com.app.wingmate.countries.Country;
import com.app.wingmate.ui.activities.SplashActivity;
import com.app.wingmate.utils.ActivityUtility;
import com.app.wingmate.utils.Utilities;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.app.wingmate.utils.APIsUtility.PARSE_CLOUD_FUNCTION_RESEND_EMAIL;
import static com.app.wingmate.utils.AppConstants.CLASS_NAME_COUNTRIES;
import static com.app.wingmate.utils.AppConstants.CLASS_NAME_USER;
import static com.app.wingmate.utils.AppConstants.ERROR;
import static com.app.wingmate.utils.AppConstants.PARAM_COUNTRY_FLAG;
import static com.app.wingmate.utils.AppConstants.PARAM_COUNTRY_NAME;
import static com.app.wingmate.utils.AppConstants.PARAM_EMAIL_VERIFIED;
import static com.app.wingmate.utils.AppConstants.PARAM_QUESTIONNAIRE_FILLED;
import static com.app.wingmate.utils.AppConstants.SUCCESS;
import static com.app.wingmate.utils.AppConstants.WARNING;
import static com.app.wingmate.utils.CommonKeys.KEY_EMAIL_TAG;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_DUMMY;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_LOGIN;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_PRE_LOGIN;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_QUESTIONNAIRE;
import static com.app.wingmate.utils.CommonKeys.KEY_NICK_TAG;
import static com.app.wingmate.utils.Utilities.showToast;

public class EmailVerificationFragment extends BaseFragment {

    public static final String TAG = EmailVerificationFragment.class.getName();

    Unbinder unbinder;

    @BindView(R.id.email_id_tv)
    TextView emailIdTV;
    @BindView(R.id.msg_tv)
    TextView msgTV;

    public EmailVerificationFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_email_verify, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        emailIdTV.setText(getActivity().getIntent().getExtras().getString(KEY_EMAIL_TAG, "adam@mail.com"));
        String msg = "Hi "
                + getActivity().getIntent().getExtras().getString(KEY_NICK_TAG, "")
                + ", you are almost ready to start enjoying Wing Mate! Simply click on button to verify your email address.";
        msgTV.setText(msg);
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

    @OnClick({R.id.btn_cont, R.id.btn_send_again, R.id.btn_wrong_email})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.btn_cont:
                ParseUser.logOut();
                ActivityUtility.startActivity(getActivity(), KEY_FRAGMENT_LOGIN);
                break;
            case R.id.btn_wrong_email:
                getActivity().finish();
                break;
            case R.id.btn_send_again:
                resendEmail();
                break;
            default:
                break;
        }
    }

    private void resendEmail() {
        if (Utilities.isInternetAvailable(getContext())) {
            Utilities.hideKeyboard(getView());
//            dialog.show(getActivity().getSupportFragmentManager(), "progress_dialog");
            dialog.show();
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("email", emailIdTV.getText().toString());
            ParseCloud.callFunctionInBackground(PARSE_CLOUD_FUNCTION_RESEND_EMAIL, params, (FunctionCallback<String>) (object, e) -> {
                if (e == null) {
                    showToast(getActivity(), getContext(), "Verification email has been resent successfully on " + emailIdTV.getText().toString(), SUCCESS);
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