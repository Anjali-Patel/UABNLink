package com.uabn.gss.uabnlink.Activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.uabn.gss.uabnlink.R;

public class VideoWebViewIframe extends AppCompatActivity {

    WebView videoWeb;
    String url;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_web_view_iframe);
        url = getIntent().getStringExtra("url").replace(" ","%20");
        videoWeb = (WebView) findViewById(R.id.iframeweb);
        videoWeb.loadUrl(url);
        videoWeb.setWebChromeClient(new WebChromeClient());
        videoWeb.setWebViewClient(new WebViewClient());
        videoWeb.getSettings().setJavaScriptEnabled(true);

        videoWeb.getSettings().setPluginState(WebSettings.PluginState.ON);


    }
    public class Browser extends WebViewClient
    {
        Browser() {}

        public boolean shouldOverrideUrlLoading(WebView paramWebView, String paramString)
        {
            paramWebView.loadUrl(paramString);
            return true;
        }
    }

    public class MyWebClient extends WebChromeClient
    {
        private View mCustomView;
        private CustomViewCallback mCustomViewCallback;
        protected FrameLayout mFullscreenContainer;
        private int mOriginalOrientation;
        private int mOriginalSystemUiVisibility;

        public MyWebClient() {}

        public Bitmap getDefaultVideoPoster()
        {
            if (VideoWebViewIframe.this == null) {
                return null;
            }
            return BitmapFactory.decodeResource(VideoWebViewIframe.this.getApplicationContext().getResources(), 2130837573);
        }

        public void onHideCustomView()
        {
            ((FrameLayout)VideoWebViewIframe.this.getWindow().getDecorView()).removeView(this.mCustomView);
            this.mCustomView = null;
            VideoWebViewIframe.this.getWindow().getDecorView().setSystemUiVisibility(this.mOriginalSystemUiVisibility);
            VideoWebViewIframe.this.setRequestedOrientation(this.mOriginalOrientation);
            this.mCustomViewCallback.onCustomViewHidden();
            this.mCustomViewCallback = null;
        }

        public void onShowCustomView(View paramView, CustomViewCallback paramCustomViewCallback)
        {
            if (this.mCustomView != null)
            {
                onHideCustomView();
                return;
            }
            this.mCustomView = paramView;
            this.mOriginalSystemUiVisibility = VideoWebViewIframe.this.getWindow().getDecorView().getSystemUiVisibility();
            this.mOriginalOrientation = VideoWebViewIframe.this.getRequestedOrientation();
            this.mCustomViewCallback = paramCustomViewCallback;
            ((FrameLayout)VideoWebViewIframe.this.getWindow().getDecorView()).addView(this.mCustomView, new FrameLayout.LayoutParams(-1, -1));
            VideoWebViewIframe.this.getWindow().getDecorView().setSystemUiVisibility(3846);
        }
    }
    @Override
    public void onBackPressed() {
        finish();


    }
}
