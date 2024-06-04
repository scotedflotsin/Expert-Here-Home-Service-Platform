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
import android.widget.TextView;
import android.widget.Toast;

import www.experthere.in.R;

public class BrowserActivity extends AppCompatActivity {
    private WebView webView;
    ProgressBar progressBar;

    TextView titleTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.white, this.getTheme()));

        setContentView(R.layout.activity_broswer);




        titleTxt = findViewById(R.id.titleTxt);


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






        Bundle bundle = getIntent().getExtras();

        if (bundle!=null){

            // Load a website URL into the WebView
            String websiteUrl = bundle.getString("url","https://experthere.in");
            webView.loadUrl(websiteUrl);

            titleTxt.setText(bundle.getString("title","Expert Here"));





        }


        findViewById(R.id.backReview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}