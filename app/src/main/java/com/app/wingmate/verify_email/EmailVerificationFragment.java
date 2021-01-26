package com.app.wingmate.verify_email;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.app.wingmate.R;
import com.app.wingmate.base.BaseFragment;
import com.app.wingmate.utils.ActivityUtility;
import com.parse.ParseException;
import com.parse.ParseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.app.wingmate.utils.AppConstants.ERROR;
import static com.app.wingmate.utils.AppConstants.SUCCESS;
import static com.app.wingmate.utils.AppConstants.WARNING;
import static com.app.wingmate.utils.CommonKeys.KEY_EMAIL_TAG;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_LOGIN;
import static com.app.wingmate.utils.CommonKeys.KEY_NICK_TAG;
import static com.app.wingmate.utils.Utilities.showToast;

public class EmailVerificationFragment  extends BaseFragment implements EmailVerificationView {

    public static final String TAG = EmailVerificationFragment.class.getName();

    Unbinder unbinder;

    @BindView(R.id.email_id_tv)
    TextView emailIdTV;
    @BindView(R.id.msg_tv)
    TextView msgTV;

    private EmailVerificationPresenter emailVerificationPresenter;

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

        emailVerificationPresenter = new EmailVerificationPresenter(this, new EmailVerificationInteracter());

        emailIdTV.setText(getActivity().getIntent().getExtras().getString(KEY_EMAIL_TAG, "adam@mail.com"));
        String nickName = getActivity().getIntent().getExtras().getString(KEY_NICK_TAG, "");
        if (!nickName.isEmpty()) nickName = nickName + "!";
        String msg = "Hi " + nickName
                + " You're almost ready to start enjoying Wingmate! Simply click on the link which has been sent to your email to verify your email address.";
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
                showProgress();
                emailVerificationPresenter.resendEmailViaCloudCode(getContext(), emailIdTV.getText().toString());
                break;
            default:
                break;
        }
    }

    @Override
    public void setResponseSuccess() {
        dismissProgress();
        showToast(getActivity(), getContext(), "Email resent successfully", SUCCESS);
//        showToast(getActivity(), getContext(), "Verification email has been resent successfully on " + emailIdTV.getText().toString(), SUCCESS);
    }
}