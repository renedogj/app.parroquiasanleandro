package es.parroquiasanleandro.activitys;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import es.parroquiasanleandro.R;
import es.parroquiasanleandro.Url;
import es.parroquiasanleandro.Usuario;

public class ActivityWebView extends AppCompatActivity {
    private final Context context = ActivityWebView.this;

    private WebView webView;
    private LinearLayout lnlytAceptarPoliticas;
    private TextView tvCancelar;
    private Button btnAceptar;

    private String url;
    private boolean soloVisualizarPoliticas;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        url = getIntent().getStringExtra("url");
        soloVisualizarPoliticas = getIntent().getBooleanExtra("soloVisualizarPoliticas", true);

        webView = findViewById(R.id.webview);
        lnlytAceptarPoliticas = findViewById(R.id.lnlytAceptarPoliticas);
        tvCancelar = findViewById(R.id.tvCancelar);
        btnAceptar = findViewById(R.id.btnAceptar);

        WebSettings webSettings = webView.getSettings();
        webSettings.setAllowContentAccess(true);
        webSettings.setBlockNetworkLoads(false);
        webSettings.setJavaScriptEnabled(true);

        webView.loadUrl(url);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
        });
        webView.setSoundEffectsEnabled(true);

        if (url.equals(Url.urlPoliticaPrivacidad) && !soloVisualizarPoliticas) {
            Usuario usuario = Usuario.recuperarUsuarioLocal(context);
            if (usuario.getId() != null) {
                lnlytAceptarPoliticas.setVisibility(View.VISIBLE);

                tvCancelar.setOnClickListener(v -> {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                    alertDialog.setTitle("No acepto las politicas de privacidad");
                    alertDialog.setMessage("Al no aceptar las politicas de privacidad se cerrará sesión de la aplicación pero no se elimininará la cuenta");
                    alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
                    alertDialog.setPositiveButton("De acuerdo", (dialog, whichButton) -> {
                        Usuario.cerrarSesion(context);
                        finish();
                    });
                    alertDialog.setNegativeButton(android.R.string.no, null).show();
                });

                btnAceptar.setOnClickListener(v -> {
                    usuario.aceptarPoliticaPrivacidad(context, this);
                });
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.web_view_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menuItemAbrirEnNavegador) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (!soloVisualizarPoliticas) {
            if (!url.equals(Url.urlPoliticaPrivacidad)) {
                onBackPressed();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (!soloVisualizarPoliticas) {
            if (url.equals(Url.urlPoliticaPrivacidad)) {
                startActivity(new Intent(context, ActivityRegistro.class));
            }
        }
    }
}