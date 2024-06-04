package www.experthere.in.serviceProvider;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.Toast;

import www.experthere.in.Common.BrowserActivity;
import www.experthere.in.helper.CustomToastNegative;
import www.experthere.in.users.IntroActivity;
import www.experthere.in.R;

public class ProviderIntroActivity extends AppCompatActivity {

    CheckBox checkPrivacy;
    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_intro);

        checkPrivacy = findViewById(R.id.checkPrivacy);
         bundle = getIntent().getExtras();


        findViewById(R.id.registerBtnProvider).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checkPrivacy.isChecked()) {


                    findViewById(R.id.providerIntroProg).setVisibility(View.VISIBLE);
                    findViewById(R.id.registerBtnProvider).setVisibility(View.GONE);

                    if (bundle!=null) {
                        if (bundle.getBoolean("isUser", false)) {
                            Intent intent = new Intent(ProviderIntroActivity.this, ProviderDetailsActivity.class);
                            Bundle bundle1 = new Bundle();
                            bundle1.putBoolean("isUser", true);
                            intent.putExtras(bundle1);
                            startActivity(intent);
                            finish();

                        }
                    }else {

                        Intent intent = new Intent(ProviderIntroActivity.this, ProviderDetailsActivity.class);
                        Bundle bundle1 = new Bundle();
                        bundle1.putBoolean("isUser", false);
                        intent.putExtras(bundle1);
                        startActivity(intent);
                        finish();
                    }




                } else {

                    CustomToastNegative.create(getApplicationContext(),"Please Accept Privacy Policy!");
                }

            }
        });


        SharedPreferences sharedPref = getSharedPreferences("keys", Context.MODE_PRIVATE);

        String websiteUrl = sharedPref.getString("providers_url", "null");


        findViewById(R.id.linkText).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ProviderIntroActivity.this, BrowserActivity.class);
                Bundle bundle1 = new Bundle();

                bundle1.putString("url",websiteUrl);
                bundle1.putString("title","Expert Here Providers");

                intent.putExtras(bundle1);
                startActivity(intent);


            }
        });





    }


    @Override
    public void onBackPressed() {


        if (bundle!=null){

            if (bundle.getBoolean("isUser",false)){

                finish();

            }

        }else {
            startActivity(new Intent(ProviderIntroActivity.this, IntroActivity.class));
            super.onBackPressed();
        }


    }
}