package com.xinmei365.emojsdk.view;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.xinmei365.emojsdk.R;

/**
 * Created by xinmei on 15/12/14.
 */
public class WebViewActivity extends Activity {

    private WebView mWebView;

    private ProgressBar mProgressBar;

    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Uri uri = intent.getData(); // get url by intent
        if (intent == null || uri == null) {
            return;
        }

        initializeView(uri);

    }

    private void initializeView(Uri uri) {

        setContentView(R.layout.activity_webview);

        mWebView = (WebView) findViewById(R.id.mWebView);

        mProgressBar = (ProgressBar) findViewById(R.id.mLoadPB);
        mProgressBar.setVisibility(View.VISIBLE);
        mProgressBar.setProgress(0);

        setupWebSetting();

        mWebView.setWebChromeClient(new WebChrome());

        mWebView.setWebViewClient(new WebClient());

        mWebView.loadUrl(uri.toString());
    }

    private void setupWebSetting() {
        WebSettings webSetting = mWebView.getSettings();
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSetting.setUseWideViewPort(true);
        webSetting.setSavePassword(false);
        webSetting.setSaveFormData(false);
        webSetting.setJavaScriptEnabled(true);
    }

    class WebChrome extends WebChromeClient {


        @Override

        public void onReceivedTitle(WebView view, String title) {
        }


        @Override

        public void onProgressChanged(WebView view, int newProgress) {

            mProgressBar.setProgress(newProgress);
            if (newProgress == 100) {
                mProgressBar.setVisibility(View.GONE);
            }
        }

    }

    class WebClient extends WebViewClient {

        @Override

        public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
            return super.shouldOverrideKeyEvent(view, event);
        }

        @Override

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

    }

    @Override

    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        }else {
            finish();
        }
    }
}
