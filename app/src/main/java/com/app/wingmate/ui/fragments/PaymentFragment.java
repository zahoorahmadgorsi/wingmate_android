package com.app.wingmate.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.app.wingmate.R;
import com.app.wingmate.base.BaseFragment;
import com.app.wingmate.base.BaseInteractor;
import com.app.wingmate.base.BasePresenter;
import com.app.wingmate.base.BaseView;
import com.app.wingmate.ui.activities.SplashActivity;
import com.app.wingmate.utils.ActivityUtility;
import com.parse.ParseUser;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.app.wingmate.utils.AppConstants.INFO;
import static com.app.wingmate.utils.AppConstants.PARAM_IS_PAID_USER;
import static com.app.wingmate.utils.AppConstants.SUCCESS;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_DASHBOARD;
import static com.app.wingmate.utils.Utilities.showToast;

public class PaymentFragment extends BaseFragment implements BaseView {

    public static final String TAG = PaymentFragment.class.getName();

    private BasePresenter presenter;

    Unbinder unbinder;

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

    @OnClick({R.id.btn_pay_now})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.btn_pay_now:
                ParseUser.getCurrentUser().put(PARAM_IS_PAID_USER, true);
                showToast(getActivity(), getContext(), "Processing...", INFO);
                ParseUser.getCurrentUser().saveInBackground(en -> {
                    showToast(getActivity(), getContext(), "Congrats on becoming a paid user!", SUCCESS);
                    ActivityUtility.startActivity(requireActivity(), KEY_FRAGMENT_DASHBOARD);
                });
                break;
        }
    }
}