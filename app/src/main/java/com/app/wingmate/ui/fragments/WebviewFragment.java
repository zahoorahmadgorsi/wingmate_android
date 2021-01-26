package com.app.wingmate.ui.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.app.wingmate.R;
import com.app.wingmate.base.BaseFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.app.wingmate.utils.CommonKeys.KEY_TITLE;
import static com.app.wingmate.utils.CommonKeys.KEY_WEB_LINK;

public class WebviewFragment extends BaseFragment implements View.OnClickListener {

    public static final String TAG = WebviewFragment.class.getName();

    Unbinder unbinder;
    @BindView(R.id.webview)
    WebView webData;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    private String pageUrl;
    private String title;

    boolean loadingFinished = true;
    boolean redirect = false;
    private boolean setFwd = false;

    public WebviewFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_webview, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pageUrl = getActivity().getIntent().getExtras().getString(KEY_WEB_LINK, "");
        title = getActivity().getIntent().getExtras().getString(KEY_TITLE, "");

        progressBar.setVisibility(View.VISIBLE);

        webData.loadUrl(pageUrl);
        webData.setWebViewClient(new webCont());
        webData.getSettings().setJavaScriptEnabled(true);
        webData.getSettings().setDomStorageEnabled(true);
        webData.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        webData.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webData.getSettings().setAllowFileAccessFromFileURLs(true);
        webData.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

//        //Enable zoom in/out to webview
//        webData.getSettings().setSupportZoom(true);
//        webData.getSettings().setBuiltInZoomControls(true);
//        webData.getSettings().setDisplayZoomControls(false);

        webData.requestFocus(View.FOCUS_DOWN);
        webData.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_UP:
                        if (!v.hasFocus()) {
                            v.requestFocus();
                        }
                        break;
                }
                return false;
            }
        });
        webData.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                if (getActivity() == null) return;
                getActivity().setProgress(progress * 100);
            }
        });
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
                getActivity().onBackPressed();
                break;
            default:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private class webCont extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            if (!loadingFinished) {
                redirect = true;
            }
            loadingFinished = false;
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap facIcon) {
            loadingFinished = false;
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if (!redirect) {
                loadingFinished = true;
            }
            if (loadingFinished && !redirect) {
                progressBar.setVisibility(View.GONE);
            } else {
                redirect = false;
            }
        }
    }
}