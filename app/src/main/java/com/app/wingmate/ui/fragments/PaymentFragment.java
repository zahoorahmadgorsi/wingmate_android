package com.app.wingmate.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.app.wingmate.R;
import com.app.wingmate.base.BaseFragment;
import com.app.wingmate.base.BaseInteractor;
import com.app.wingmate.base.BasePresenter;
import com.app.wingmate.base.BaseView;
import com.app.wingmate.ui.activities.SplashActivity;
import com.app.wingmate.utils.ActivityUtility;
import com.app.wingmate.utils.SharedPrefers;
import com.parse.ParseUser;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.app.wingmate.utils.AppConstants.ERROR;
import static com.app.wingmate.utils.AppConstants.INFO;
import static com.app.wingmate.utils.AppConstants.MANDATORY;
import static com.app.wingmate.utils.AppConstants.PARAM_IS_PAID_USER;
import static com.app.wingmate.utils.AppConstants.SUCCESS;
import static com.app.wingmate.utils.CommonKeys.KEY_BACK_TAG;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_DASHBOARD;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_PRE_LOGIN;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_QUESTIONNAIRE;
import static com.app.wingmate.utils.CommonKeys.PREF_LAST_UPDATE_TIME;
import static com.app.wingmate.utils.Utilities.showToast;

public class PaymentFragment extends BaseFragment implements BaseView {

    public static final String TAG = PaymentFragment.class.getName();

    private BasePresenter presenter;

    Unbinder unbinder;

    @BindView(R.id.payment_success_view)
    RelativeLayout paymentSuccessView;
    @BindView(R.id.btn_back)
    ImageView btnBack;

    public boolean isClear = false;

    public PaymentFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payment, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        presenter = new BasePresenter(this, new BaseInteractor());

        isClear = requireActivity().getIntent().getBooleanExtra(KEY_BACK_TAG, false);

        if (isClear) btnBack.setVisibility(View.INVISIBLE);
        else btnBack.setVisibility(View.VISIBLE);

        paymentSuccessView.setVisibility(View.GONE);
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

    @OnClick({R.id.btn_pay_now, R.id.logout, R.id.btn_back, R.id.btn_continue})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.btn_continue:
                ActivityUtility.startQuestionnaireActivity(getActivity(), KEY_FRAGMENT_QUESTIONNAIRE, MANDATORY, true);
                break;
            case R.id.btn_pay_now:
                ParseUser.getCurrentUser().put(PARAM_IS_PAID_USER, true);
                showToast(requireActivity(), getContext(), "Processing...", INFO);
                ParseUser.getCurrentUser().saveInBackground(en -> {
                    isClear = true;
                    paymentSuccessView.setVisibility(View.VISIBLE);
                });
                break;
            case R.id.logout:
                showToast(requireActivity(), getContext(), "Logging out...", ERROR);
                SharedPrefers.saveLong(requireContext(), PREF_LAST_UPDATE_TIME, 0);
                ParseUser.logOutInBackground(e -> {
                    ActivityUtility.startActivity(requireActivity(), KEY_FRAGMENT_PRE_LOGIN);
                });
                break;
            case R.id.btn_back:
                requireActivity().onBackPressed();
                break;
        }
    }

    public boolean canBack() {
        if (isClear) {
            return false;
        } else {
            return true;
        }
    }
}