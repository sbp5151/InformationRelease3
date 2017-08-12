package com.jld.InformationRelease.view.web_view;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jld.InformationRelease.R;
import com.jld.InformationRelease.base.BaseActivity;
import com.jld.InformationRelease.util.LogUtil;
import com.jld.InformationRelease.util.ToastUtil;

public class WebViewActivity extends BaseActivity implements View.OnClickListener {

    public static final String TAG = "WebViewActivity";
    private WebView mWebView;
    private ProgressBar mProgressBar;
    public static final String LOAD_URL = "load_url";
    public static final String WEB_TITLE = "web_title";
    private String mLoadUrl;
    private String mWebTitle;
    private TextView mTitle_center;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        Intent intent = getIntent();
        mLoadUrl = intent.getStringExtra(LOAD_URL);
        mWebTitle = intent.getStringExtra(WEB_TITLE);
        LogUtil.d(TAG, "onCreate:" + "mLoadUrl:"+mLoadUrl+"\n\r"+"mWebTitle:"+mWebTitle);
        if (TextUtils.isEmpty(mLoadUrl)) {
            ToastUtil.showToast(this, getString(R.string.web_load_null), 3000);
            return;
        }
        initView();
        initWebView();
    }

    private void initView() {
        mWebView = (WebView) findViewById(R.id.web_view);
        mProgressBar = (ProgressBar) findViewById(R.id.web_progress);

        //title
        View title_view = findViewById(R.id.web_title_bar);
        title_view.findViewById(R.id.title_back).setOnClickListener(this);
        mTitle_center = (TextView) title_view.findViewById(R.id.title_center);
        mTitle_center.setText(mWebTitle);

        title_view.findViewById(R.id.title_right).setVisibility(View.GONE);
        title_view.findViewById(R.id.title_tool).setVisibility(View.GONE);
    }

    private void initWebView() {
        mWebView.loadUrl(mLoadUrl);
        //WebSettings
        WebSettings webSettings = mWebView.getSettings();
        //设置自适应屏幕，两者合用
        webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式
        // 设置WebView属性，能够执行Javascript脚本
        webSettings.setJavaScriptEnabled(true);
        //WebViewClient
        mWebView.setWebViewClient(new WebViewClient() {
            //复写shouldOverrideUrlLoading()方法，使得打开网页时不调用系统浏览器， 而是在本WebView中显示
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                LogUtil.d(TAG, "onProgressChanged:" + newProgress);
                if (newProgress < 100) {
                    mProgressBar.setProgress(newProgress);
                } else if (newProgress == 100)
                    mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (TextUtils.isEmpty(mWebTitle))
                    mTitle_center.setText(title);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        //如果web view可以返回，返回web view
        if (keyCode == KeyEvent.KEYCODE_BACK && mWebView != null && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                finish();
                break;
        }
    }
}
