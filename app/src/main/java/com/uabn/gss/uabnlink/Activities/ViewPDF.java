package com.uabn.gss.uabnlink.Activities;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

//import com.github.barteksc.pdfviewer.PDFView;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.uabn.gss.uabnlink.R;

public class ViewPDF extends AppCompatActivity {

    WebView web;
    ProgressBar progressBar;
    String docPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pdf);

        docPath = getIntent().getStringExtra("doc_path");

        web = (WebView) findViewById(R.id.webview01);
        progressBar = (ProgressBar) findViewById(R.id.progressBar1);

        String url = docPath;

        web.setWebViewClient(new myWebClient());
        web.getSettings().setJavaScriptEnabled(true);
//        http://demo1.geniesoftsystem.com/newweb/icomuabn/app/webroot/files/user/upload_document/159/7395_2019-08-30_16:29:09.pdf
        web.loadUrl("http://drive.google.com/viewerng/viewer?embedded=true&url="+ docPath);

        web.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                progressBar.setProgress(progress);
                if (progress == 100) {
                    progressBar.setVisibility(View.GONE);
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    public class myWebClient extends WebViewClient
    {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub
            view.loadUrl(url);
            return true;

        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub
            super.onPageFinished(view, url);

        }
    }

    // To handle "Back" key press event for WebView to go back to previous screen.
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && web.canGoBack()) {
            web.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
