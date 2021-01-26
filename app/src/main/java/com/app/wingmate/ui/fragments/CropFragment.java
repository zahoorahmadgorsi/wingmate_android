package com.app.wingmate.ui.fragments;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.app.wingmate.R;
import com.app.wingmate.utils.FilenameUtils;
import com.isseiaoki.simplecropview.CropImageView;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A simple {@link Fragment} subclass.
 */
public class CropFragment extends Fragment {

    public static final String TAG = CropFragment.class.getName();

    public static final int REQUEST_CODE_CROP_IMAGE = 176;
    public static final String IMAGE_PATH = "image-path";
//    private static final String TAG = "CropImage";
    final int IMAGE_MAX_SIZE = 1024;
    private Uri mSaveUri = null;
    private String mImagePath;
    private ContentResolver mContentResolver;
    private CropImageView cropView;

    public CropFragment() {
        // Required empty public constructor
    }

    public static void closeSilently(Closeable c) {

        if (c == null) return;
        try {
            c.close();
        } catch (Throwable t) {
            // do nothing
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
        mImagePath = getArguments().getString(IMAGE_PATH);
        mSaveUri = getImageUri(mImagePath);
        mContentResolver = getActivity().getContentResolver();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_crop, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cropView = (CropImageView) view.findViewById(R.id.crop);
        cropView.setCropMode(CropImageView.CropMode.FREE);
        cropView.setImageBitmap(getBitmap(mImagePath));
    }

    private Uri getImageUri(String path) {

        return Uri.fromFile(new File(path));
    }

    private void saveOutput(Bitmap croppedImage) {
             File file = new
                     File(Environment.
                     getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                     , FilenameUtils.getName(mImagePath));

            OutputStream outputStream = null;
            try {
                outputStream = new FileOutputStream(file);
                if (outputStream != null) {
                    croppedImage.compress(Bitmap.CompressFormat.JPEG, 75, outputStream);
                }
            } catch (IOException ex) {

                Log.e(TAG, "Cannot open file: " + file.getPath(), ex);
                getActivity().setResult(Activity.RESULT_CANCELED);
                getActivity().finish();
                return;
            } finally {
                closeSilently(outputStream);
            }

            Bundle extras = new Bundle();
            Intent intent = new Intent(getImageUri(file.getAbsolutePath()).toString());
            intent.putExtras(extras);
            intent.putExtra(IMAGE_PATH, file.getAbsolutePath());
//        getTargetFragment().onActivityResult(Activity.RESULT_OK, SignupExtraFragment.REQUEST_CODE_CROP_IMAGE,intent );
            getActivity().setResult(Activity.RESULT_OK, intent);
        croppedImage.recycle();
        getActivity().finish();
    }

    private Bitmap getBitmap(String path) {

        Uri uri = getImageUri(path);
        InputStream in = null;
        try {
            in = mContentResolver.openInputStream(uri);

            //Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;

            BitmapFactory.decodeStream(in, null, o);
            in.close();

            int scale = 1;
            if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
                scale = (int) Math.pow(2, (int) Math.round(Math.log(IMAGE_MAX_SIZE / (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            in = mContentResolver.openInputStream(uri);
            Bitmap b = BitmapFactory.decodeStream(in, null, o2);
            in.close();

            return b;
        } catch (FileNotFoundException e) {
            Log.e(TAG, "file " + path + " not found");
        } catch (IOException e) {
            Log.e(TAG, "file " + path + " not found");
        }
        return null;
    }

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.crop_menu, menu);
//        super.onCreateOptionsMenu(menu, inflater);
//    }

    public void rotateLeft() {
        cropView.rotateImage(CropImageView.RotateDegrees.ROTATE_270D); // rotate counter-clockwise by 90 degrees
    }

    public void rotateRight() {
        cropView.rotateImage(CropImageView.RotateDegrees.ROTATE_90D); // rotate clockwise by 90 degrees
    }

    public void cropDone() {
        saveOutput(cropView.getCroppedBitmap());
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.crop:
//                saveOutput(cropView.getCroppedBitmap());
//                break;
//
//            case R.id.rotate_x:
//                cropView.rotateImage(CropImageView.RotateDegrees.ROTATE_270D); // rotate counter-clockwise by 90 degrees
//
//                break;
//            case R.id.rotate_y:
//                cropView.rotateImage(CropImageView.RotateDegrees.ROTATE_90D); // rotate clockwise by 90 degrees
//
//                break;
//            case R.id.home:
//                getActivity().finish();
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }
}
