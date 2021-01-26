package com.app.wingmate.ui.dialogs;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.app.wingmate.R;
import com.skyfishjy.library.RippleBackground;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProgressDialog extends DialogFragment {

    private Activity myActivity;
    private View fragmentView;

    @BindView(R.id.content)
    RippleBackground rippleBackground;
    @BindView(R.id.img)
    ImageView loaderImg;

    public static ProgressDialog newInstance(Activity myActivity) {
        ProgressDialog dialogFrag = new ProgressDialog();
        dialogFrag.myActivity = myActivity;
        return dialogFrag;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        myActivity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.PauseDialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.dialog_progress, container, false);
        ButterKnife.bind(this, fragmentView);
        getDialog().getWindow().setBackgroundDrawableResource(R.color.transparent);
        return fragmentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rippleBackground.startRippleAnimation();
//        Glide.with(myActivity)
//                .load(R.drawable.myle_loader)
//                .into(loaderImg);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        rippleBackground.stopRippleAnimation();
    }
}