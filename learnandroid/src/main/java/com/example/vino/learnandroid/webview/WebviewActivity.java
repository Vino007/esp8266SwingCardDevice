package com.example.vino.learnandroid.webview;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.vino.learnandroid.R;

public class WebviewActivity extends ActionBarActivity {
    private WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        webView= (WebView) findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);//开启js
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);//载录url
                return true;//表示当前webview可以处理打开新网页的请求，不用借助系统浏览器
            }
        });

        webView.loadUrl("http://www.baidu.com");

    }



}
