package www.experthere.in;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MaintenanceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
        );


        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);


        getWindow().setStatusBarColor(getResources().getColor(R.color.white, this.getTheme()));



        setContentView(R.layout.activity_maintenance);


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            String txt = bundle.getString("txt", "App Is Under Maintenance Mode You Can Open App After Some Time!");

            TextView textView = findViewById(R.id.textView8);
            TextView headingTxt = findViewById(R.id.textView7);


            headingTxt.setText(bundle.getString("title",getString(R.string.app_is_under_maintenance)));


            textView.setText(txt);


        }


        findViewById(R.id.appCompatButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });

        findViewById(R.id.learnMoretxt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://experthere.in/QA")));

            }
        });


    }
}