package www.experthere.in;

import static androidx.core.app.ActivityCompat.recreate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;
import java.util.HashSet;
import java.util.Locale;

import io.agora.rtc2.IRtcEngineEventHandler;
import io.agora.rtc2.RtcEngine;
import io.agora.rtc2.internal.RtcEngineEvent;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import www.experthere.in.FCM.FcmBearerTokenGenerator;
import www.experthere.in.adapters.UserCallHistoryAdapter;
import www.experthere.in.api.ApiClient;
import www.experthere.in.api.ApiClientDeeplink;
import www.experthere.in.api.ApiInterface;
import www.experthere.in.api.ApiInterfaceDeeplink;
import www.experthere.in.helper.CustomToastNegative;
import www.experthere.in.helper.NightModeHelper;
import www.experthere.in.helper.app.Base64Util;
import www.experthere.in.model.BlockRes;
import www.experthere.in.model.MaintenanceData;
import www.experthere.in.model.MaintenanceRes;
import www.experthere.in.model.ProviderData;
import www.experthere.in.model.ProviderResponse;
import www.experthere.in.model.SettingsRes;
import www.experthere.in.serviceProvider.ProviderHome;
import www.experthere.in.serviceProvider.ServiceProviderProfileDetailsActivity;
import www.experthere.in.users.HomeActivity;
import www.experthere.in.users.IntroActivity;

@SuppressLint("CustomSplashScreen")

public class SplashScreen extends AppCompatActivity {


    private static final String PREFS_SETTINGS = "settings";
    private static final String LANG_PREF_KEY = "language";
    Uri appLinkData;
    private SharedPreferences preferences;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //to hide the navigation buttons and
        //to make splash screen a full screen activity

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
        );

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });



        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        setContentView(R.layout.activity_splash);




        preferences = getSharedPreferences(PREFS_SETTINGS, Context.MODE_PRIVATE);

        setLanguage(getLanguage());
        // Apply night mode setting
        applyNightModeSetting();

        startNextActivityAfterTimer();


        FirebaseMessaging.getInstance().subscribeToTopic("all")
                .addOnCompleteListener(task -> {
                    String msg = "Subscribed to all topic";
                    if (!task.isSuccessful()) {
                        msg = "Subscription to all topic failed";
                    }
                    Log.d("FCMTOPIC", msg);
                    // Optionally show a toast
                    // Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                });


        // ATTENTION: This was auto-generated to handle app links.
        Intent appLinkIntent = getIntent();
        appLinkData = appLinkIntent.getData();


        if (appLinkData != null) {

            String data = appLinkData.getQueryParameter("EHpd");

            Log.d("DATADEEP", "PATH : " + data);
            Log.d("DATADEEP", "PATH : " + Base64Util.decode(data));

            SharedPreferences pref2 = getSharedPreferences("user", MODE_PRIVATE);

            if (pref2.contains("id")) {

                fetchProviderDetails(Base64Util.decode(data));
            } else {

                animateText_Image();

            }


        } else {
            animateText_Image();

        }


    }

    private void fetchProviderDetails(String pID) {


        new GetProviderDetails(pID).execute();


    }


    private class GetProviderDetails extends AsyncTask<Void, Void, ProviderResponse> {
        private final String providerID;

        public GetProviderDetails(String providerID) {
            this.providerID = providerID;
        }

        @Override
        protected ProviderResponse doInBackground(Void... params) {
            try {
                ApiInterfaceDeeplink apiInterface = ApiClientDeeplink.getClient().create(ApiInterfaceDeeplink.class);
                Call<ProviderResponse> call = apiInterface.getProviderProfile(providerID, "API_v1_EXPERTHERE");

//                Log.d("SNSSSS", " "+call.request().url().toString());

                Response<ProviderResponse> response = call.execute();

                if (response.isSuccessful() && response.body() != null) {
                    return response.body();
                } else {
                    return null;
                }
            } catch (IOException e) {
                Log.e("SubmitReviewTask", "Error making API call", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(ProviderResponse apiResponse) {


            if (apiResponse != null && apiResponse.isSuccess()) {
                ProviderData providerData = apiResponse.getData();

                if (providerData != null) {

                    // Now that you have the latitude and longitude, you can update the map

                    Intent intent = new Intent(SplashScreen.this, ServiceProviderProfileDetailsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    Bundle bundle = new Bundle();


                    bundle.putString("provider_id", String.valueOf(providerData.getId()));
                    bundle.putString("provider_name", String.valueOf(providerData.getName()));
                    bundle.putString("provider_banner", String.valueOf(providerData.getBannerImage()));
                    bundle.putString("provider_logo", String.valueOf(providerData.getLogoImage()));

                    bundle.putString("provider_company_name", String.valueOf(providerData.getCompanyName()));
                    bundle.putString("deeplink", "1");


                    intent.putExtras(bundle);
                    startActivity(intent);

                    finish();


                } else {
                    Log.d("onzonal", "Error 2  ");

                }
            } else {

                Log.d("onzonal", "Error 3  ");

            }
        }
    }


    private void applyNightModeSetting() {
        boolean isNightModeEnabled = NightModeHelper.isNightModeEnabled(this);
        if (isNightModeEnabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    private void startNextActivityAfterTimer() {

//        ImageView imageView = findViewById(R.id.imageView);
//        TextView textView = findViewById(R.id.textView);
//
//
//        // Start fade-out animation for ImageView
//        ObjectAnimator imageFadeOut = ObjectAnimator.ofFloat(imageView, View.ALPHA, 1.0f, 0.0f);
//        imageFadeOut.setDuration(2000); // Set the duration in milliseconds
//        imageFadeOut.start();
//
//        // Start fade-out animation for TextView
//        ObjectAnimator textFadeOut = ObjectAnimator.ofFloat(textView, View.ALPHA, 1.0f, 0.0f);
//        textFadeOut.setDuration(2000); // Set the duration in milliseconds
//        textFadeOut.start();
    }

    private void animateText_Image() {


        Handler mHandler = new Handler();


        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                //start your activity here


                new GetMaintenanceStatus("1").execute();


            }


        }, 1000L);


    }


    private String getLanguage() {

        return preferences.getString(LANG_PREF_KEY, "en");
    }


    private void setLanguage(String languageCode) {

        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        getResources().getConfiguration().setLocale(locale);

        getBaseContext().getResources().updateConfiguration(getResources().getConfiguration(), getBaseContext().getResources().getDisplayMetrics());


    }


    private class GetMaintenanceStatus extends AsyncTask<Void, Void, Void> {

        String id;

        public GetMaintenanceStatus(String id) {
            this.id = id;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            // Perform background operation, e.g., user

            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

            Call<MaintenanceRes> call = apiInterface.get_maintenance(id);


            call.enqueue(new Callback<MaintenanceRes>() {
                @Override
                public void onResponse(Call<MaintenanceRes> call, Response<MaintenanceRes> response) {

                    if (response.body() != null) {

                        MaintenanceRes categoryResponse = response.body();

                        if (categoryResponse.isSuccess()) {

                            if (categoryResponse.getData() != null) {

                                if (categoryResponse.getData().getStatus().equals("enable")) {

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {


                                            showMaintenanceDialog(SplashScreen.this, categoryResponse.getData());

                                        }
                                    });

                                } else {

                                    new GetSettings().execute();


                                }


                            } else {
                                new GetSettings().execute();

                            }
                        } else {

                            new GetSettings().execute();


                        }


                    } else {
                        new GetSettings().execute();

                    }

                }

                @Override
                public void onFailure(Call<MaintenanceRes> call, Throwable t) {

                    new GetSettings().execute();


                }
            });


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // This method runs on the UI thread and can be used to update the UI after background task completion
        }
    }

    private void showMaintenanceDialog(Activity activity, MaintenanceData data) {


        Intent intent = new Intent(SplashScreen.this, MaintenanceActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("txt", data.getMessage());

        intent.putExtras(bundle);
        startActivity(intent);
        finish();

    }

    private class GetSettings extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... voids) {
            // Perform background operation, e.g., user

            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

            Call<SettingsRes> call = apiInterface.getSettings();

            SharedPreferences sharedPreferences = getSharedPreferences("keys", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();


            call.enqueue(new Callback<SettingsRes>() {
                @Override
                public void onResponse(Call<SettingsRes> call, Response<SettingsRes> response) {

                    if (response.body() != null) {

                        SettingsRes categoryResponse = response.body();

                        if (categoryResponse.isSuccess()) {


                            for (int i = 0; i <= categoryResponse.getSettings().size() - 1; i++) {

                                Log.d("KEUTEST", "onResponse: " + categoryResponse.getSettings().get(i).getKey_name());

                                editor.putString(categoryResponse.getSettings().get(i).getKey_name(), categoryResponse.getSettings().get(i).getValue());
                                editor.apply();


                            }


                            SharedPreferences sharedPref = getSharedPreferences("keys", Context.MODE_PRIVATE);

                            String apiKey = sharedPref.getString("map_api_key", "null");

                            initializeMapsSdk(apiKey);

                            Log.d("KEYPREFRENCE", "map api key : " + apiKey);


                            SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
                            boolean isLogIn = sharedPreferences.getBoolean("isLogIn", false);
                            boolean isFullLogin = sharedPreferences.getBoolean("isFullLogin", false);
                            boolean isUser = sharedPreferences.getBoolean("isUser", false);
                            SharedPreferences providerPref = getSharedPreferences("provider", MODE_PRIVATE);

                            boolean isProvider = providerPref.getBoolean("isProvider", false);


                            if (isProvider) {

                                startActivity(new Intent(SplashScreen.this, ProviderHome.class));
                                finish();

                            } else if (isUser) {
                                if (isLogIn && isFullLogin) {

                                    startActivity(new Intent(SplashScreen.this, HomeActivity.class));
                                    finish();
                                } else {


                                    if (!isFullLogin) {

                                        startActivity(new Intent(SplashScreen.this, IntroActivity.class));
                                        finish();
                                    }
                                }
                            } else {


                                startActivity(new Intent(SplashScreen.this, IntroActivity.class));
                                finish();
                            }


                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    startMantainanceActivity();
                                }
                            });
                        }


                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                startMantainanceActivity();
                            }
                        });
                    }

                }

                @Override
                public void onFailure(Call<SettingsRes> call, Throwable t) {

                  runOnUiThread(new Runnable() {
                      @Override
                      public void run() {
                          startMantainanceActivity();
                      }
                  });

                }
            });


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // This method runs on the UI thread and can be used to update the UI after background task completion
        }
    }

    private void startMantainanceActivity() {



        Intent intent  = new Intent(SplashScreen.this,MaintenanceActivity.class);

        Bundle bundle = new Bundle();
        bundle.putString("txt","Oops! It looks like our server is currently down or experiencing technical difficulties. We apologize for the inconvenience. Please try again later. Thank you for your patience!");
        bundle.putString("title","Server Down or facing technical issue.!");

        intent.putExtras(bundle);
        startActivity(intent);
        finish();


    }


    private void initializeMapsSdk(String apiKey) {
        try {
            ApplicationInfo ai = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            bundle.putString("com.google.android.geo.API_KEY", apiKey);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}