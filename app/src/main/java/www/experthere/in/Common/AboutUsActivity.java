package www.experthere.in.Common;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import www.experthere.in.R;

public class AboutUsActivity extends AppCompatActivity {

    private WebView webView;
    ProgressBar progressBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.white, this.getTheme()));

        setContentView(R.layout.activity_about_us);


        findViewById(R.id.backReview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        progressBar = findViewById(R.id.progressBar);

        // Initialize WebView from layout
        webView = findViewById(R.id.webView);

        // Enable JavaScript (optional)
        webView.getSettings().setJavaScriptEnabled(true);

        // Set up a WebViewClient to monitor loading events
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

                progressBar.setVisibility(View.VISIBLE);

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                progressBar.setVisibility(View.INVISIBLE);


            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return true;
            }
        });


        SharedPreferences sharedPref = getSharedPreferences("keys", Context.MODE_PRIVATE);

        String websiteUrl = sharedPref.getString("about_us_url", "null");

        // Load a website URL into the WebView
        webView.loadUrl(websiteUrl);

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}