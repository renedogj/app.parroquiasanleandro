package es.parroquiasanleandro.activitys;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import es.parroquiasanleandro.R;

public class ActivityWebView extends AppCompatActivity {
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        String url = getIntent().getStringExtra("url");

        webView = findViewById(R.id.webview);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(url);
        webView.setSoundEffectsEnabled(true);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
    }
}