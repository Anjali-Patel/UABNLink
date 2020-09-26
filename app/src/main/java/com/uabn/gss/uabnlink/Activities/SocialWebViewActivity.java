package com.uabn.gss.uabnlink.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.uabn.gss.uabnlink.R;

public class SocialWebViewActivity extends AppCompatActivity {
    private WebView webView;
    String social_web_url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_web_view);

        webView = (WebView) findViewById(R.id.webView);

            webView.getSettings().setJavaScriptEnabled(true);
        social_web_url=getIntent().getStringExtra("url");

            webView.loadUrl(social_web_url);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);

        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
    }
}
