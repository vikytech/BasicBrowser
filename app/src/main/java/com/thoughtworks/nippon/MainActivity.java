package com.thoughtworks.nippon;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity {

    private WebView webView;


    Activity activity;
    private ProgressDialog progDailog;
    private JavaScriptInterface javaScriptInterface = new JavaScriptInterface(this);

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @SuppressLint({"JavascriptInterface", "AddJavascriptInterface"})
    @TargetApi(Build.VERSION_CODES.ECLAIR_MR1)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;
        progDailog = ProgressDialog.show(activity, "Loading", "Please wait...", true);
        progDailog.setCancelable(false);

        webView = (WebView) findViewById(R.id.webView);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setSaveFormData(true);
        webView.getSettings().setSavePassword(true);
        webView.addJavascriptInterface(javaScriptInterface, "Android");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                progDailog.show();
                view.loadUrl(url);
                return true;
            }
            @Override
            public void onPageFinished(WebView view, final String url) {
                progDailog.dismiss();
            }
        });
        webView.loadUrl("http://10.16.20.175:3000");
    }

    public class JavaScriptInterface extends WebChromeClient {
        Context mContext;
        public String barcode = null;

        JavaScriptInterface(Context c) {
            mContext = c;
        }

        public void closeMyActivity() {
            finish();
        }

        public void scanBarcode() {
            Intent intent = new Intent("com.google.zxing.client.android.SCAN");
            intent.setPackage("com.google.zxing.client.android");
            startActivityForResult(intent, 0);
        }

        public void onActivityResult(int requestCode, int resultCode, Intent intent) {
            if (requestCode == 0) {
                if (resultCode == RESULT_OK) {
                    //here is where you get your result
                    barcode = intent.getStringExtra("SCAN_RESULT");
                    webView.loadUrl("javascript:receivebarcode(" + barcode + ")");
                }
            }
        }
    }
}
