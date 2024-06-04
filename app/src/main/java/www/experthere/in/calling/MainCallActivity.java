package www.experthere.in.calling;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Callback;
import www.experthere.in.FCM.FcmApiClient;
import www.experthere.in.FCM.FcmBearerTokenGenerator;
import www.experthere.in.R;
import www.experthere.in.SplashScreen;
import www.experthere.in.api.ApiClient;
import www.experthere.in.api.ApiInterface;
import www.experthere.in.helper.CustomToastNegative;
import www.experthere.in.helper.ProcessingDialog;
import www.experthere.in.model.FCMData;
import www.experthere.in.model.TokenResponse;
import www.experthere.in.serviceProvider.ProviderHome;
import www.experthere.in.users.HomeActivity;

public class MainCallActivity extends AppCompatActivity implements SensorEventListener {

    private static final String LOG_TAG = "AGORAVOICETESTINGMAIN";

    String FINISH_MAIN = "www.experthere.in.calling.FINISH_MAIN";

    private TextView timerTextView;

    String user_id, provider_id, call_by, dp, displayName;

    SharedPreferences userPref, providerPref;
    ProcessingDialog processingDialog;

    private SensorManager sensorManager;
    private Sensor proximitySensor;
    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;


    boolean isFinished;


    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_call);
        processingDialog = new ProcessingDialog(this);

        setUpProximatySensor();

        Log.d("knlcnclnldsn", "onCreate: ");
        userPref = getSharedPreferences("user", MODE_PRIVATE);
        providerPref = getSharedPreferences("provider", MODE_PRIVATE);

        boolean userPrefContainsValue = userPref.contains("id");
        boolean providerPrefContainsValue = providerPref.contains("id");


        IntentFilter filter1 = new IntentFilter();
        filter1.addAction("timerReceiver");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(timerReceiver, filter1, Context.RECEIVER_NOT_EXPORTED);
        }else {
            registerReceiver(timerReceiver, filter1);

        }


        IntentFilter filter2 = new IntentFilter();
        filter2.addAction(FINISH_MAIN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(finishReceiver, filter2, Context.RECEIVER_NOT_EXPORTED);
        }else {
            registerReceiver(finishReceiver, filter2);

        }


        Bundle bundle = getIntent().getExtras();

        Log.d("CALLMAINSCREENDATATEST", "BUNDLE MAIN SCREEN - " + bundle);

        timerTextView = findViewById(R.id.timerTxt);


        if (bundle != null) {
            user_id = bundle.getString("user_id");
            provider_id = bundle.getString("provider_id");
            call_by = bundle.getString("call_by");
            dp = bundle.getString("dp");

            TextView userNameTxt = findViewById(R.id.textView5);
            TextView callByTxt = findViewById(R.id.textView3);

            ImageView imageView = findViewById(R.id.imageView8);


            if (providerPrefContainsValue) {
                Log.d("akmaknooa", "Provider Pref Not Null");

            } else {
                Log.d("akmaknooa", "Provider Null");

            }

            if (userPrefContainsValue) {
                Log.d("akmaknooa", "User Pref Not Null");

            } else {
                Log.d("akmaknooa", "User Null");

            }

            Log.d("akmaknooa", "Ans by " + bundle.getString("answered_by"));

            Log.d("akmaknooa", "name " + bundle.getString("user_name"));
            Log.d("akmaknooa", "d name " + bundle.getString("display_name"));
            Log.d("akmaknooa", "p name " + bundle.getString("provider_name"));

            Log.d("akmaknooa", "d p " + bundle.getString("dp"));
            Log.d("akmaknooa", "d p 2 " + bundle.getString("user_dp"));


            if (userPrefContainsValue) {

                if (call_by.equals("user")) {
                    // i am user and call from provider
                    displayName = bundle.getString("provider_name", "Expert Here Provider");
                    callByTxt.setText("Provider");
                    Glide.with(MainCallActivity.this).load(bundle.getString("provider_dp")).circleCrop().into(imageView);
                    userNameTxt.setText(displayName);


                }
                if (call_by.equals("provider")) {

                    // i am user and call from provider
                    displayName = bundle.getString("provider_name", "Expert Here Provider");
                    callByTxt.setText("Provider");
                    Glide.with(MainCallActivity.this).load(bundle.getString("user_dp")).circleCrop().into(imageView);
                    userNameTxt.setText(displayName);

                }


            } else if (providerPrefContainsValue) {


                if (call_by.equals("user")) {
                    //i am provider and call came from user
                    displayName = bundle.getString("user_name", "Expert Here User");
                    callByTxt.setText("User");
                    Glide.with(MainCallActivity.this).load(bundle.getString("user_dp")).circleCrop().into(imageView);
                    userNameTxt.setText(displayName);
                }

                if (call_by.equals("provider")) {
                    //i am provider and call came from user
                    displayName = bundle.getString("display_name", "Expert Here User");
                    callByTxt.setText("User");
                    Glide.with(MainCallActivity.this).load(bundle.getString("dp")).circleCrop().into(imageView);
                    userNameTxt.setText(displayName);
                }


            }


            Intent serviceIntent = new Intent(this, MainCallService.class);
            serviceIntent.putExtras(bundle);  // Pass the necessary data to the service
            startService(serviceIntent);


            AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);


            ImageView speakerBtn = findViewById(R.id.speakerBtn);
            ImageView muteBtn = findViewById(R.id.muteBtn);


            findViewById(R.id.muteBtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if (audioManager.isMicrophoneMute()) {
                        audioManager.setMicrophoneMute(false);
                        Glide.with(MainCallActivity.this).load(R.drawable.mute_icon_call).into(muteBtn);


                    } else {

                        Glide.with(MainCallActivity.this).load(R.drawable.selected_mute).into(muteBtn);
                        audioManager.setMicrophoneMute(true);

                    }


                }
            });

            findViewById(R.id.speakerBtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (audioManager.isSpeakerphoneOn()) {
                        Glide.with(MainCallActivity.this).load(R.drawable.spk_icon_call).into(speakerBtn);
                        audioManager.setSpeakerphoneOn(false);

                    } else {
                        Glide.with(MainCallActivity.this).load(R.drawable.selected_speaker).into(speakerBtn);
                        audioManager.setSpeakerphoneOn(true);

                    }


                }
            });
            findViewById(R.id.imageView9).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent serviceIntent = new Intent(MainCallActivity.this, MainCallService.class);
                    serviceIntent.setAction("END_CALL");
                    startService(serviceIntent);
                }
            });
        }
    }


    private final BroadcastReceiver timerReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getExtras() != null && timerTextView != null) {

                timerTextView.setText(intent.getExtras().getString("time"));
            }
        }
    };

    private final BroadcastReceiver finishReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction() != null && intent.getAction().equals(FINISH_MAIN)) {


                if (userPref.contains("id")) {
                    // this is user


                    try {

                        new FetchProviderToken2(provider_id).execute();

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                }

                if (providerPref.contains("id")) {
                    // this is provider


                    try {


                        new FetchUserToken(user_id).execute();

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                }


            }
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(timerReceiver);
        unregisterReceiver(finishReceiver);

        if (processingDialog != null && processingDialog.isShowing()) {
            processingDialog.dismiss();
        }


    }

    @Override
    protected void onRestart() {
        super.onRestart();

        Log.d("knlcnclnldsn", "restart: ");

    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            float distance = event.values[0];
            // If the proximity sensor detects that the object is close (e.g., near the ear)
            if (distance < proximitySensor.getMaximumRange()) {
                Log.d("TAG", "Object is close");
                // Acquire the wake lock to turn off the screen
                wakeLock.acquire();
            } else {
                Log.d("TAG", "Object is far");
                // Release the wake lock to turn on the screen
                if (wakeLock.isHeld()) {
                    wakeLock.release();
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onStop() {
        super.onStop();

        if (!isServiceRunning(MainCallService.class)) {

            if (providerPref.contains("id")) {

                startActivity(new Intent(MainCallActivity.this, ProviderHome.class));

            } else {

                startActivity(new Intent(MainCallActivity.this, HomeActivity.class));

            }

        }
        Log.d("knlcnclnldsn", "onstop: ");


    }

    @Override
    protected void onResume() {
        super.onResume();


        if (proximitySensor != null) {
            sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        }


    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {

        Intent serviceIntent = new Intent(MainCallActivity.this, MainCallService.class);
        serviceIntent.setAction("END_CALL");
        startService(serviceIntent);

    }

    private class FetchUserToken extends AsyncTask<Void, Void, TokenResponse> {


        String userID;

        public FetchUserToken(String userID) {
            this.userID = userID;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (!processingDialog.isShowing()) {

                processingDialog.show();
            }


        }

        @Override
        protected TokenResponse doInBackground(Void... voids) {

            Log.d("APISEARCHING", "Do IN BG  runs");


            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
//            Call<ApiResponse> call = apiInterface.getServiceUserWithSearchAndFilter(latitude, longitude, page, itemsPerPage, radius, 5, searchQuery);

            retrofit2.Call<TokenResponse> call = apiInterface.getFcmTokenDetailsUser(userID);


            try {
                retrofit2.Response<TokenResponse> response = call.execute();
                if (response.isSuccessful() && response.body() != null) {


                    if (response.body().getData() != null) {
                        return response.body();
                    }
                } else {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            processingDialog.dismiss();

                        }
                    });

                }
            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        processingDialog.dismiss();

                    }
                });
                Log.d("APISEARCHING", "Error " + e.getMessage());


            }

            return null;
        }

        @Override
        protected void onPostExecute(TokenResponse serviceResponse) {
            super.onPostExecute(serviceResponse);


            if (serviceResponse != null) {


                if (serviceResponse.isSuccess() && serviceResponse.getMessage().equals("User data retrieved successfully")) {

                    FCMData fcmData = serviceResponse.getData();


                    FcmBearerTokenGenerator tokenGenerator = new FcmBearerTokenGenerator(MainCallActivity.this);
                    tokenGenerator.generateAccessTokenAsync(new FcmBearerTokenGenerator.TokenGenerationListener() {
                        @Override
                        public void onTokenGenerated(String token) {

                            startSendingFcmNotificationToUser(fcmData, token);

                        }

                        @Override
                        public void onTokenGenerationFailed(Exception e) {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            CustomToastNegative.create(getApplicationContext(),"Error: "+e.getMessage());


                                        }
                                    });
                                }
                            });

                        }
                    });


                } else {

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {

                            CustomToastNegative.create(getApplicationContext(),"Data Not Found!");
                            processingDialog.dismiss();
                        }
                    });


                }


            } else {

                processingDialog.dismiss();

            }


        }


    }

    private void startSendingFcmNotificationToUser(FCMData fcmData, String token) {


        Map<String, String> EXTRA_DATA = new HashMap<>();


        EXTRA_DATA.put("user_name", displayName);
        EXTRA_DATA.put("user_id", user_id);
        EXTRA_DATA.put("user_dp", dp);
        EXTRA_DATA.put("provider_id", provider_id);
        EXTRA_DATA.put("call_by", call_by);

//
        String CALL_REJECTED_BY_USER = "www.experthere.in.calling.CALL_DECLINE_BY_USER";

        EXTRA_DATA.put("event", FINISH_MAIN);
        EXTRA_DATA.put("rejected_by", "provider");
        EXTRA_DATA.put("timestamp", String.valueOf(System.currentTimeMillis()));

        //

        FcmApiClient.sendFcmMessageToDevice(getString(R.string.FcmSendUrl), token, fcmData.getFcm_token(), "Call Decline", "decline by user", EXTRA_DATA, "rejecte", new Callback() {
            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {


                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {

                        CustomToastNegative.create(getApplicationContext(),"Error: "+e.getMessage());
                        processingDialog.dismiss();


                        finish();

                    }
                });


            }

            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) throws IOException {


                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {

                        processingDialog.dismiss();

                    }
                });


                if (response.isSuccessful()) {


                    finish();

                    if (isServiceRunning(MainCallService.class)) {

                        Intent intent = new Intent(getApplicationContext(), MainCallService.class);
                        stopService(intent);


                    } else {
                        finish();
                    }

                } else {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {

                            CustomToastNegative.create(getApplicationContext(),"User is not active from many days!");

                        }
                    });
                }


            }
        });


    }

    private class FetchProviderToken2 extends AsyncTask<Void, Void, TokenResponse> {


        String providerID;

        public FetchProviderToken2(String providerID) {
            this.providerID = providerID;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (!processingDialog.isShowing()) {

                processingDialog.show();
            }


        }

        @Override
        protected TokenResponse doInBackground(Void... voids) {

            Log.d("APISEARCHING", "Do IN BG  runs");


            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
//            Call<ApiResponse> call = apiInterface.getServiceUserWithSearchAndFilter(latitude, longitude, page, itemsPerPage, radius, 5, searchQuery);

            retrofit2.Call<TokenResponse> call = apiInterface.getFcmTokenDetailsProvider(providerID);


            try {
                retrofit2.Response<TokenResponse> response = call.execute();
                if (response.isSuccessful() && response.body() != null) {


                    if (response.body().getData() != null) {
                        return response.body();
                    }
                } else {
                    processingDialog.dismiss();

                }
            } catch (IOException e) {
                e.printStackTrace();
                processingDialog.dismiss();

                Log.d("APISEARCHING", "Error " + e.getMessage());


            }

            return null;
        }

        @Override
        protected void onPostExecute(TokenResponse serviceResponse) {
            super.onPostExecute(serviceResponse);


            if (serviceResponse != null) {


                if (serviceResponse.isSuccess() && serviceResponse.getMessage().equals("Provider data retrieved successfully")) {

                    FCMData fcmData = serviceResponse.getData();


                    FcmBearerTokenGenerator tokenGenerator = new FcmBearerTokenGenerator(MainCallActivity.this);
                    tokenGenerator.generateAccessTokenAsync(new FcmBearerTokenGenerator.TokenGenerationListener() {
                        @Override
                        public void onTokenGenerated(String token) {

                            startSendingFcmNotificationToProvider(fcmData, token);

                        }

                        @Override
                        public void onTokenGenerationFailed(Exception e) {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    CustomToastNegative.create(getApplicationContext(),"Error: "+e.getMessage());
                                }
                            });

                        }
                    });


                } else {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            CustomToastNegative.create(getApplicationContext(),"Data Not Found!");

                            processingDialog.dismiss();

                        }
                    });
                }
            } else {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {

                        processingDialog.dismiss();

                    }
                });
            }


        }


    }


    private void startSendingFcmNotificationToProvider(FCMData fcmData, String authToken) {


        Map<String, String> EXTRA_DATA = new HashMap<>();


        EXTRA_DATA.put("user_name", displayName);
        EXTRA_DATA.put("user_id", user_id);
        EXTRA_DATA.put("user_dp", dp);
        EXTRA_DATA.put("provider_id", provider_id);
        EXTRA_DATA.put("call_by", call_by);

//
        String CALL_REJECTED_BY_USER = "www.experthere.in.calling.CALL_DECLINE_BY_USER";

        EXTRA_DATA.put("event", FINISH_MAIN);
        EXTRA_DATA.put("rejected_by", "user");
        //
        EXTRA_DATA.put("timestamp", String.valueOf(System.currentTimeMillis()));


        FcmApiClient.sendFcmMessageToDevice(getString(R.string.FcmSendUrl), authToken, fcmData.getFcm_token(), "Call Decline", "decline by user", EXTRA_DATA, "rejecte", new Callback() {
            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {


                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {

                        CustomToastNegative.create(getApplicationContext(),"Error: "+e.getMessage());

                        processingDialog.dismiss();

                    }
                });


            }

            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) throws IOException {

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {


                        processingDialog.dismiss();

                    }
                });


                if (response.isSuccessful()) {


                    if (isServiceRunning(MainCallService.class)) {

                        Intent intent = new Intent(getApplicationContext(), MainCallService.class);
                        stopService(intent);

                        finish();

                    } else {
                        finish();
                    }


                } else {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {


                            finish();


                            CustomToastNegative.create(getApplicationContext(),"User Not Active From Many Days!");



                        }
                    });


                }


            }
        });

    }


    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @SuppressLint("InvalidWakeLockTag")
    private void setUpProximatySensor() {


        // Initialize the SensorManager
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        // Get the proximity sensor
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        // Initialize the PowerManager
        powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        // Acquire a wake lock to keep the screen on
        wakeLock = powerManager.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, "ss2wsw");

    }


    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(MainCallActivity.this);
            Log.d("knlcnclnldsn", "onpause: ");
        }
    }
}