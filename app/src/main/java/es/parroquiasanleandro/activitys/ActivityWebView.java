package es.parroquiasanleandro.activitys;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import es.parroquiasanleandro.R;
import es.parroquiasanleandro.Url;

public class ActivityWebView extends AppCompatActivity {
    private final Context context = ActivityWebView.this;

    private WebView webView;

    private String url;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        url = getIntent().getStringExtra("url");

        webView = findViewById(R.id.webview);

        WebSettings webSettings = webView.getSettings();
        webSettings.setAllowContentAccess(true);
        webSettings.setBlockNetworkLoads(false);
        webSettings.setJavaScriptEnabled(true);

        webView.loadUrl(url);
        webView.setWebViewClient(new WebViewClient() {
            @SuppressWarnings("deprecation")
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
        });
        webView.setSoundEffectsEnabled(true);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(!url.equals(Url.urlPoliticaPrivacidad)){
            onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(url.equals(Url.urlPoliticaPrivacidad)){
            startActivity(new Intent(context, ActivityRegistro.class));
        }
    }
}