package com.app.wingmate.ui.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
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
import com.app.wingmate.base.BaseFragment;
import com.parse.ParseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.app.wingmate.utils.AppConstants.ERROR;
import static com.app.wingmate.utils.AppConstants.PARAM_ABOUT_ME;
import static com.app.wingmate.utils.AppConstants.PARAM_NICK;
import static com.app.wingmate.utils.AppConstants.SUCCESS;
import static com.app.wingmate.utils.CommonKeys.KEY_TITLE;
import static com.app.wingmate.utils.Utilities.showToast;

public class EditProfileTextFieldFragment extends BaseFragment {

    public static final String TAG = EditProfileTextFieldFragment.class.getName();

    Unbinder unbinder;

    @BindView(R.id.title)
    TextView titleTV;
    @BindView(R.id.et)
    EditText editText;
    @BindView(R.id.error_email)
    LinearLayout errorLayout;
    @BindView(R.id.error_email_tv)
    TextView errorTV;

    @BindView(R.id.btn_save)
    Button saveBtn;

    public EditProfileTextFieldFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile_fields, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        errorLayout.setVisibility(View.INVISIBLE);
        saveBtn.setSelected(false);
        saveBtn.setEnabled(false);
        saveBtn.setClickable(false);
        saveBtn.setAlpha(0.5f);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                errorLayout.setVisibility(View.INVISIBLE);
                editText.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.field_bg_selected, null));
                if (!editText.getText().toString().isEmpty()) {
                    saveBtn.setSelected(true);
                    saveBtn.setEnabled(true);
                    saveBtn.setClickable(true);
                    saveBtn.setAlpha(1.0f);
                } else {
                    saveBtn.setSelected(false);
                    saveBtn.setEnabled(false);
                    saveBtn.setClickable(false);
                    saveBtn.setAlpha(0.5f);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editText.setOnFocusChangeListener((arg0, isFocus) -> {
            if (EditProfileTextFieldFragment.this.getActivity() == null || editText == null) return;
            if (isFocus) {
                editText.setBackground(ResourcesCompat.getDrawable(EditProfileTextFieldFragment.this.getResources(), R.drawable.field_bg_selected, null));
            } else {
                editText.setBackground(ResourcesCompat.getDrawable(EditProfileTextFieldFragment.this.getResources(), R.drawable.field_bg_unselected, null));
            }
        });

        if (getActivity().getIntent().getStringExtra(KEY_TITLE).equalsIgnoreCase(PARAM_NICK)) {
            titleTV.setText("Name or Nick Name");
            editText.append(ParseUser.getCurrentUser().getString(PARAM_NICK));
        } else if (getActivity().getIntent().getStringExtra(KEY_TITLE).equalsIgnoreCase(PARAM_ABOUT_ME)) {
            titleTV.setText("About Me");
            editText.append(ParseUser.getCurrentUser().getString(PARAM_ABOUT_ME));
            ViewGroup.LayoutParams layoutParams = editText.getLayoutParams();
            int heightInPixels = (int) getResources().getDimension(R.dimen._100ssp);
            heightInPixels = (int) getResources().getDimension(R.dimen.dp200);
            layoutParams.height = heightInPixels;
            int paddingDp = (int) getResources().getDimension(R.dimen._8ssp);
//            float density = getActivity().getResources().getDisplayMetrics().density;
//            int paddingPixel = (int)(paddingDp * density);
            int paddingPixel = paddingDp;
            editText.setPadding(paddingPixel,paddingPixel,paddingPixel,paddingPixel);
            editText.setLayoutParams(layoutParams);
            editText.setGravity(Gravity.START);
        }
        editText.requestFocus();
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

    @OnClick({R.id.btn_back, R.id.btn_save})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.btn_save:
                if (getActivity().getIntent().getStringExtra(KEY_TITLE).equalsIgnoreCase(PARAM_NICK)) {
                    ParseUser.getCurrentUser().put(PARAM_NICK, editText.getText().toString());
                } else if (getActivity().getIntent().getStringExtra(KEY_TITLE).equalsIgnoreCase(PARAM_ABOUT_ME)) {
                    ParseUser.getCurrentUser().put(PARAM_ABOUT_ME, editText.getText().toString());
                }
                showProgress();
                ParseUser.getCurrentUser().saveInBackground(e -> {
                    if (e==null) {
                        showToast(getActivity(), getContext(), "Profile has been updated successfully", SUCCESS);
                        dismissProgress();
                        getActivity().onBackPressed();
                    } else {
                        showToast(getActivity(), getContext(), e.getMessage(), ERROR);
                    }
                });
                break;
            case R.id.btn_back:
                getActivity().onBackPressed();
                break;
        }

    }
}