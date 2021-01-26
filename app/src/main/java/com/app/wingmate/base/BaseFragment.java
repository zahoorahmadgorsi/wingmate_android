package com.app.wingmate.base;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.parse.ParseException;

import pl.aprilapps.easyphotopicker.ChooserType;
import pl.aprilapps.easyphotopicker.EasyImage;

import static com.app.wingmate.utils.AppConstants.ERROR;
import static com.app.wingmate.utils.AppConstants.WARNING;
import static com.app.wingmate.utils.Utilities.showToast;

public class BaseFragment extends Fragment implements BaseView {

    //    public ProgressDialog dialog;
    public KProgressHUD dialog;
    public EasyImage easyImage;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        dialog = ProgressDialog.newInstance(getActivity());
//        dialog.setCancelable(false);

        dialog = KProgressHUD.create(getActivity())
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
//                .setLabel("Please wait")
//                .setDetailsLabel("Loading data...")
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);

        easyImage = new EasyImage.Builder(requireContext())
                .setChooserTitle("Select Image")
                .setChooserType(ChooserType.CAMERA_AND_GALLERY)
                .setCopyImagesToPublicGalleryFolder(false)
                .setFolderName("Wing Mate")
                .allowMultiple(false)
                .build();
    }

    @Override
    public void showProgress() {
        dialog.show();
    }

    @Override
    public void dismissProgress() {
        dialog.dismiss();
    }

    @Override
    public void setInternetError() {
        dismissProgress();
        showToast(getActivity(), getContext(), "Couldn't connect to internet!", WARNING);
    }

    @Override
    public void setResponseSuccess() {
        dismissProgress();
    }

    @Override
    public void setResponseError(ParseException e) {
        dismissProgress();
        showToast(getActivity(), getContext(), e.getMessage(), ERROR);
    }

    @Override
    public void setResponseGeneralError(String error) {
        dismissProgress();
        showToast(getActivity(), getContext(), error, ERROR);
    }
}