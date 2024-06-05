package www.experthere.in.calling;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import www.experthere.in.FCM.FcmApiClient;
import www.experthere.in.FCM.FcmBearerTokenGenerator;
import www.experthere.in.R;
import www.experthere.in.api.ApiClient;
import www.experthere.in.api.ApiInterface;
import www.experthere.in.helper.CustomToastNegative;
import www.experthere.in.helper.CustomToastPositive;
import www.experthere.in.media.RtcTokenBuilder2;
import www.experthere.in.model.FCMData;
import www.experthere.in.model.TokenResponse;

public class OutGoingCallActivity extends AppCompatActivity implements SensorEventListener {

    String dpUrl, displayName, userID, providerId, call_by, selected_service, provider_name, provider_dp;
    TextView callByTxt;
    ImageView dpImg, muteBtn, spkBtn;
    String FINISH_OUTGOING = "www.experthere.in.calling.FINISH_OUTGOING";
    public static final String ACTION_STOP = "www.experthere.in.calling.STOP";
    private static final String TAG = "CHECKOUTGOINGACTIVITY";
    private SensorManager sensorManager;
    private Sensor proximitySensor;
    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;
    Dialog dialog;

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_out_going_call);

        dialog = new Dialog(OutGoingCallActivity.this);

        setUpProximatySensor();


        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_STOP);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(dismissBroadcastReceiver, filter, Context.RECEIVER_EXPORTED);
        }else {
            registerReceiver(dismissBroadcastReceiver, filter);
        }
        IntentFilter filter2 = new IntentFilter();
        filter2.addAction(FINISH_OUTGOING);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        registerReceiver(finishReceiver, filter2, Context.RECEIVER_EXPORTED);
        }else {
            registerReceiver(finishReceiver, filter2);
        }
        muteBtn = findViewById(R.id.muteBtn);
        spkBtn = findViewById(R.id.speakerBtn);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            dpUrl = bundle.getString("dp", "0");
            userID = bundle.getString("user_id", "0");
            providerId = bundle.getString("provider_id", "0");
            call_by = bundle.getString("call_by", "Expert Here User");
            displayName = bundle.getString("display_name", "Expert Here");
            selected_service = bundle.getString("selected_service", "Expert Here Service");
            provider_name = bundle.getString("provider_name", "Expert Here Provider");
            provider_dp = bundle.getString("provider_dp", "0");

            showProcessingDialog();

            // Update UI with data
            TextView displayNameTxt = findViewById(R.id.textView5);
            callByTxt = findViewById(R.id.textView3);
            displayNameTxt.setText(displayName);

            // Load profile image using Glide library
            dpImg = findViewById(R.id.imageView8);
            Glide.with(this).load(dpUrl).circleCrop().into(dpImg);

            if (call_by.equals("user")) {
                callByTxt.setText("Provider");
            } else {
                callByTxt.setText("User");
            }

            if (call_by.equals("user")) {
                getProviderDeviceToken();
            }

            if (call_by.equals("provider")) {
                getUserDeviceToken();
            }
        } else {
            Log.d(TAG, "onCreate: Activity created Bundle NULL");
        }

        findViewById(R.id.imageView9).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //end call
                try {
                    if (call_by.equals("user")) {
                        new FetchProviderToken2(providerId).execute();
                    } else {
                        new FetchUserToken2(userID).execute();
                    }
                } catch (Exception e) {
                    Log.d("NWBWJBWJBW", "Error : " + e.getMessage());
                    throw new RuntimeException(e);
                }
            }
        });
        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        if (audioManager.isSpeakerphoneOn()) {
            Glide.with(OutGoingCallActivity.this).load(R.drawable.selected_speaker).into(spkBtn);
        } else {
            Glide.with(OutGoingCallActivity.this).load(R.drawable.spk_icon_call).into(spkBtn);
        }

        if (audioManager.isMicrophoneMute()) {
            Glide.with(OutGoingCallActivity.this).load(R.drawable.selected_mute).into(muteBtn);
        } else {
            Glide.with(OutGoingCallActivity.this).load(R.drawable.mute_icon_call).into(muteBtn);
        }

        findViewById(R.id.speakerBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent broadcastIntent = new Intent("www.experthere.in.calling.SWITCH_AUDIO");
                sendBroadcast(broadcastIntent);
                if (audioManager.isSpeakerphoneOn()) {
                    Glide.with(OutGoingCallActivity.this).load(R.drawable.spk_icon_call).into(spkBtn);
                } else {
                    Glide.with(OutGoingCallActivity.this).load(R.drawable.selected_speaker).into(spkBtn);
                }
            }
        });
        findViewById(R.id.muteBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent broadcastIntent = new Intent("www.experthere.in.calling.SWITCH_MIC");
                sendBroadcast(broadcastIntent);
                if (audioManager.isMicrophoneMute()) {
                    Glide.with(OutGoingCallActivity.this).load(R.drawable.mute_icon_call).into(muteBtn);
                } else {
                    Glide.with(OutGoingCallActivity.this).load(R.drawable.selected_mute).into(muteBtn);
                }
            }
        });
    }

    private void showProcessingDialog() {
        // Create custom dialog
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.processing_dialog_call);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        TextView textView = dialog.findViewById(R.id.dialogMessage);
        textView.setText(selected_service);
        if (call_by.equals("provider")) {
            textView.setVisibility(View.GONE);
        }

        // Initialize views in the custom dialog


        dialog.show();


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
        wakeLock = powerManager.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, TAG);

    }

    private void getProviderDeviceToken() {


        try {


            new FetchProviderToken(providerId).execute();


        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    private void getUserDeviceToken() {


        try {


            new FetchUserToken(userID).execute();


        } catch (Exception e) {

            Log.d("kbaSKBsk", "E " + e.getMessage());

            throw new RuntimeException(e);
        }


    }

    private void startSendingFcmNotificationToUser(FCMData fcmData) {


        SharedPreferences preferences = getSharedPreferences("keys",MODE_PRIVATE);
        String agoraAppId = preferences.getString("agora_app_id","0");

        Log.d("KEYPREFRENCE", "agora APP ID : "+agoraAppId);





        Log.d("LKHLBKSJB", "startSendingFcmNotificationTo User:");


        SharedPreferences providerPref = getSharedPreferences("provider", MODE_PRIVATE);
        String providerName = providerPref.getString("name", "0");
        String providerId = providerPref.getString("id", "0");
        String providerDp = providerPref.getString("logo_image", "0");


        String deviceToken = fcmData.getFcm_token();



        SharedPreferences preferencesKeys = getSharedPreferences("keys",MODE_PRIVATE);
        String agora_certificate = preferencesKeys.getString("agora_certificate","0");

        Log.d("KEYPREFRENCE", "agora certificate  : "+agora_certificate);





        FcmBearerTokenGenerator tokenGenerator = new FcmBearerTokenGenerator(OutGoingCallActivity.this);
        tokenGenerator.generateAccessTokenAsync(new FcmBearerTokenGenerator.TokenGenerationListener() {
            @Override
            public void onTokenGenerated(String token) {

                int time = (int) (System.currentTimeMillis() / 1000) + 1800000;


                int CHANEL_ID = Integer.parseInt(userID) + Integer.parseInt(providerId);

                RtcTokenBuilder2 tokenBuilder2 = new RtcTokenBuilder2();
                String agora_token = tokenBuilder2.buildTokenWithUid(
                        agoraAppId,
                        agora_certificate,
                        String.valueOf(CHANEL_ID),
                        0,
                        RtcTokenBuilder2.Role.ROLE_PUBLISHER,
                        time,
                        time);


                Log.d("klankaknaknak", "onTokenGenerated: " + agora_token);
                Log.d("klankaknaknak", "Channel id : " + CHANEL_ID);


                Map<String, String> EXTRA_DATA = new HashMap<>();
                EXTRA_DATA.put("user_name", providerName);

                EXTRA_DATA.put("display_name", displayName);
                EXTRA_DATA.put("dp", dpUrl);

                EXTRA_DATA.put("user_id", userID);
                EXTRA_DATA.put("user_dp", providerDp);
                EXTRA_DATA.put("provider_id", providerId);
                EXTRA_DATA.put("call_by", call_by);
                EXTRA_DATA.put("selected_service", selected_service);


                Log.d("amknakjn", "onTokenGenerated: " + call_by);


                SharedPreferences preferences = getSharedPreferences("provider", Context.MODE_PRIVATE);
                if (preferences.contains("id")) {
                    EXTRA_DATA.put("provider_name", preferences.getString("name", "Expert Here Provider"));
                    EXTRA_DATA.put("provider_dp", preferences.getString("logo_image", "0"));

                }


                EXTRA_DATA.put("event", "incoming_call");
                EXTRA_DATA.put("agora_token", agora_token);


                EXTRA_DATA.put("title", "Incoming Call");
                EXTRA_DATA.put("body", providerName + " Is Calling You!");

                EXTRA_DATA.put("timestamp", String.valueOf(System.currentTimeMillis()));

                String ACTION = "CALL";


                Log.d("LKHLBKSJB", "USER ID");


                FcmApiClient.sendFcmMessageToDevice(getString(R.string.FcmSendUrl), token, deviceToken, "Incoming Call",
                        "Call From : " + providerName, EXTRA_DATA, ACTION, new Callback() {
                            @Override
                            public void onFailure(@NonNull Call call, @NonNull IOException e) {


                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        dialog.dismiss();


                                        Intent intent = new Intent(getApplicationContext(), OutGoingCallService.class);
                                        Intent intent2 = new Intent(getApplicationContext(), DialToneGeneratorService.class);
                                        stopService(intent);
                                        stopService(intent2);

                                        Intent broadcastIntent = new Intent(getApplicationContext(), OutGoingNotificationActionReceiver.class);
                                        broadcastIntent.setAction(ACTION_STOP);
                                        sendBroadcast(broadcastIntent);



                                        CustomToastNegative.create(getApplicationContext(), "User Not Active In App From Many Days!!");

                                        Log.d(TAG, "FAIL TOKEN: " + e.getMessage());

                                    }
                                });

                            }

                            @Override
                            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                                if (response.isSuccessful()) {

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {


                                            CustomToastPositive.create(OutGoingCallActivity.this, "calling..");

                                            startoutgingservice("provider", providerId, userID, providerDp, providerName);
                                            dialog.dismiss();


                                        }
                                    });


                                } else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                            dialog.dismiss();


                                            Intent intent = new Intent(getApplicationContext(), OutGoingCallService.class);
                                            Intent intent2 = new Intent(getApplicationContext(), DialToneGeneratorService.class);
                                            stopService(intent);
                                            stopService(intent2);

                                            Intent broadcastIntent = new Intent(getApplicationContext(), OutGoingNotificationActionReceiver.class);
                                            broadcastIntent.setAction(ACTION_STOP);
                                            sendBroadcast(broadcastIntent);


                                            CustomToastNegative.create(OutGoingCallActivity.this, "User Not Active In App From Many Days!!");


                                        }
                                    });

                                }

                            }
                        });


            }

            @Override
            public void onTokenGenerationFailed(Exception e) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();


                        CustomToastNegative.create(OutGoingCallActivity.this, "Error: "+e.getMessage());


                    }
                });


            }
        });


    }

    private void startSendingFcmNotificationToProvider(FCMData fcmData) {


        Log.d(TAG, "startSendingFcmNotificationToProvider:");



        SharedPreferences preferences = getSharedPreferences("keys",MODE_PRIVATE);
        String agoraAppId = preferences.getString("agora_app_id","0");

        Log.d("KEYPREFRENCE", "agora APP ID : "+agoraAppId);




        SharedPreferences preferencesKeys = getSharedPreferences("keys",MODE_PRIVATE);
        String agora_certificate = preferencesKeys.getString("agora_certificate","0");

        Log.d("KEYPREFRENCE", "agora certificate  : "+agora_certificate);





        SharedPreferences userPref = getSharedPreferences("user", MODE_PRIVATE);
        String userName = userPref.getString("name", "0");
        String userId = userPref.getString("id", "0");
        String userDp = userPref.getString("dp", "0");


        String deviceToken = fcmData.getFcm_token();


        FcmBearerTokenGenerator tokenGenerator = new FcmBearerTokenGenerator(OutGoingCallActivity.this);
        tokenGenerator.generateAccessTokenAsync(new FcmBearerTokenGenerator.TokenGenerationListener() {
            @Override
            public void onTokenGenerated(String token) {

                int time = (int) (System.currentTimeMillis() / 1000) + 1800000;


                int CHANEL_ID = Integer.parseInt(userID) + Integer.parseInt(providerId);

                RtcTokenBuilder2 tokenBuilder2 = new RtcTokenBuilder2();
                String agora_token = tokenBuilder2.buildTokenWithUid(
                        agoraAppId,
                        agora_certificate,
                        String.valueOf(CHANEL_ID),
                        0,
                        RtcTokenBuilder2.Role.ROLE_PUBLISHER,
                        time,
                        time);


                Log.d("klankaknaknak", "onTokenGenerated: " + agora_token);
                Log.d("klankaknaknak", "Channel id : " + CHANEL_ID);


                Map<String, String> EXTRA_DATA = new HashMap<>();
                EXTRA_DATA.put("user_name", userName);
                EXTRA_DATA.put("user_id", userId);
                EXTRA_DATA.put("user_dp", userDp);
                EXTRA_DATA.put("provider_id", providerId);
                EXTRA_DATA.put("call_by", call_by);


                EXTRA_DATA.put("event", "incoming_call");
                EXTRA_DATA.put("agora_token", agora_token);


                EXTRA_DATA.put("title", "Incoming Call");
                EXTRA_DATA.put("body", userName + " Is Calling You!");


                String ACTION = "CALL";
                EXTRA_DATA.put("timestamp", String.valueOf(System.currentTimeMillis()));


                FcmApiClient.sendFcmMessageToDevice(getString(R.string.FcmSendUrl), token, deviceToken, "Incoming Call",
                        "Call From : " + userName, EXTRA_DATA, ACTION, new Callback() {
                            @Override
                            public void onFailure(@NonNull Call call, @NonNull IOException e) {

                                dialog.dismiss();

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {


                                        CustomToastNegative.create(OutGoingCallActivity.this, "Fail: "+e.getMessage());


                                        Intent intent = new Intent(getApplicationContext(), OutGoingCallService.class);
                                        Intent intent2 = new Intent(getApplicationContext(), DialToneGeneratorService.class);
                                        stopService(intent);
                                        stopService(intent2);

                                        Intent broadcastIntent = new Intent(getApplicationContext(), OutGoingNotificationActionReceiver.class);
                                        broadcastIntent.setAction(ACTION_STOP);
                                        sendBroadcast(broadcastIntent);


                                    }
                                });

                            }

                            @Override
                            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                dialog.dismiss();

                                if (response.isSuccessful()) {

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {


                                            CustomToastPositive.create(OutGoingCallActivity.this, "Calling..");

                                            startoutgingservice(call_by, userId, providerId, dpUrl, displayName);


                                        }
                                    });


                                } else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                            Log.d("CALLTESTANKU", " Provider Token invalid " + deviceToken);


                                            CustomToastNegative.create(OutGoingCallActivity.this, "Provider No Longer Active on App!!");


                                            Intent intent = new Intent(getApplicationContext(), OutGoingCallService.class);
                                            Intent intent2 = new Intent(getApplicationContext(), DialToneGeneratorService.class);
                                            stopService(intent);
                                            stopService(intent2);

                                            Intent broadcastIntent = new Intent(getApplicationContext(), OutGoingNotificationActionReceiver.class);
                                            broadcastIntent.setAction(ACTION_STOP);
                                            sendBroadcast(broadcastIntent);

                                            finish();


                                        }
                                    });

                                }

                            }
                        });


            }

            @Override
            public void onTokenGenerationFailed(Exception e) {


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();

                        CustomToastNegative.create(OutGoingCallActivity.this, "Error: "+e.getMessage());


                    }
                });

            }
        });


    }

    private void startAgoraCall() {


    }

    @Override
    protected void onResume() {
        super.onResume();

        if (proximitySensor != null) {
            sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    private void startoutgingservice(String callBy, String userId, String providerId, String dp, String name) {

        Intent serviceIntent = new Intent(this, OutGoingCallService.class);

        Bundle bundle1 = new Bundle();


        bundle1.putString("call_by", callBy);
        bundle1.putString("user_id", userId);
        bundle1.putString("provider_id", providerId);
        bundle1.putString("dp", dp);
        bundle1.putString("display_name", name);
        bundle1.putString("provider_name", provider_name);

        serviceIntent.putExtras(bundle1);
        startService(serviceIntent);


    }


    @Override
    protected void onStart() {


        super.onStart();
    }


    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(OutGoingCallActivity.this);

    }

    @Override
    protected void onStop() {
        super.onStop();

//


    }

    private final BroadcastReceiver dismissBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null && intent.getAction().equals(ACTION_STOP)) {
                // Finish the activity

                finish();
//
                Intent serviceIntent = new Intent(OutGoingCallActivity.this, OutGoingCallService.class);
                serviceIntent.setAction(ACTION_STOP);
                stopService(serviceIntent);
                Log.d(TAG, "onDestroy: Activity destroyed");

            }
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister the broadcast receiver

        unregisterReceiver(finishReceiver);

        if (!isServiceRunning(OutGoingCallService.class)) {

            unregisterReceiver(dismissBroadcastReceiver);
            // Send a broadcast to finish the OutGoingCallActivity
            // Stop the service
            Intent serviceIntent = new Intent(OutGoingCallActivity.this, OutGoingCallService.class);
            stopService(serviceIntent);


        }

        if (isServiceRunning(OutGoingCallService.class)) {

            Intent intent = new Intent(getApplicationContext(), OutGoingCallService.class);
            Intent intent2 = new Intent(getApplicationContext(), DialToneGeneratorService.class);
            stopService(intent);
            stopService(intent2);

            Intent broadcastIntent = new Intent(getApplicationContext(), OutGoingNotificationActionReceiver.class);
            broadcastIntent.setAction(ACTION_STOP);
            sendBroadcast(broadcastIntent);

        }


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

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            float distance = event.values[0];
            // If the proximity sensor detects that the object is close (e.g., near the ear)
            if (distance < proximitySensor.getMaximumRange()) {
                Log.d(TAG, "Object is close");
                // Acquire the wake lock to turn off the screen
                wakeLock.acquire();
            } else {
                Log.d(TAG, "Object is far");
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


    private class FetchUserToken extends AsyncTask<Void, Void, TokenResponse> {


        String userId;

        public FetchUserToken(String userId) {
            this.userId = userId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            dialog.show();


        }

        @Override
        protected TokenResponse doInBackground(Void... voids) {
            Log.d("kbaSKBsk", "DO iN BG RUN");


            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
//            Call<ApiResponse> call = apiInterface.getServiceUserWithSearchAndFilter(latitude, longitude, page, itemsPerPage, radius, 5, searchQuery);

            retrofit2.Call<TokenResponse> call = apiInterface.getFcmTokenDetailsUser(userId);


            try {
                retrofit2.Response<TokenResponse> response = call.execute();
                if (response.isSuccessful() && response.body() != null) {


                    if (response.body().getData() != null) {
                        return response.body();
                    }
                } else {
                    dialog.dismiss();

                }
            } catch (IOException e) {
                e.printStackTrace();
                dialog.dismiss();

                Log.d("APISEARCHING", "Error " + e.getMessage());


            }

            return null;
        }

        @Override
        protected void onPostExecute(TokenResponse serviceResponse) {
            super.onPostExecute(serviceResponse);


            if (serviceResponse != null) {

                Log.d("kbaSKBsk", "RES " + serviceResponse.isSuccess() + "\n" + serviceResponse.getMessage());

                if (serviceResponse.isSuccess() && serviceResponse.getMessage().equals("User data retrieved successfully")) {


                    FCMData fcmData = serviceResponse.getData();

                    startSendingFcmNotificationToUser(fcmData);

                } else {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CustomToastNegative.create(OutGoingCallActivity.this, "Data Not Found!");

                            dialog.dismiss();

                        }
                    });

                }


            } else {

                dialog.dismiss();

            }


        }


    }

    private class FetchProviderToken extends AsyncTask<Void, Void, TokenResponse> {


        String providerID;

        public FetchProviderToken(String providerID) {
            this.providerID = providerID;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            dialog.show();


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
                    dialog.dismiss();

                }
            } catch (IOException e) {
                e.printStackTrace();
                dialog.dismiss();

                Log.d("APISEARCHING", "Error " + e.getMessage());


            }

            return null;
        }

        @Override
        protected void onPostExecute(TokenResponse serviceResponse) {
            super.onPostExecute(serviceResponse);


            Log.d("CALLTESTANKU", "Getting Provider Token ");


            if (serviceResponse != null) {


                if (serviceResponse.isSuccess() && serviceResponse.getMessage().equals("Provider data retrieved successfully")) {

                    FCMData fcmData = serviceResponse.getData();

                    startSendingFcmNotificationToProvider(fcmData);

                    Log.d("CALLTESTANKU", " Provider Token Retrived Success ");


                } else {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            Log.d("CALLTESTANKU", " Provider Token error 1");

                            CustomToastNegative.create(OutGoingCallActivity.this,"Data Not Found!");
                            dialog.dismiss();
                        }
                    });


                }


            } else {

                dialog.dismiss();

            }


        }


    }

    @SuppressLint("StaticFieldLeak")
    private class FetchProviderToken2 extends AsyncTask<Void, Void, TokenResponse> {


        String providerID;

        public FetchProviderToken2(String providerID) {
            this.providerID = providerID;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            dialog.show();


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
                    dialog.dismiss();

                }
            } catch (IOException e) {
                e.printStackTrace();
                dialog.dismiss();

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


                    FcmBearerTokenGenerator tokenGenerator = new FcmBearerTokenGenerator(OutGoingCallActivity.this);
                    tokenGenerator.generateAccessTokenAsync(new FcmBearerTokenGenerator.TokenGenerationListener() {
                        @Override
                        public void onTokenGenerated(String token) {

                            startSendingFcmNotificationToUser(fcmData, token);

                            sendMissCallNotificationToProvider(fcmData, token);
                        }

                        @Override
                        public void onTokenGenerationFailed(Exception e) {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    CustomToastNegative.create(OutGoingCallActivity.this,"Error: "+e.getMessage());

                                }
                            });

                        }
                    });


                } else {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            CustomToastNegative.create(OutGoingCallActivity.this,"Data Not Found!");

                            dialog.dismiss();

                        }
                    });


                }


            } else {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();

                    }
                });

            }


        }


    }

    private class FetchUserToken2 extends AsyncTask<Void, Void, TokenResponse> {


        String userId;

        public FetchUserToken2(String userId) {
            this.userId = userId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialog.show();

                }
            });


        }

        @Override
        protected TokenResponse doInBackground(Void... voids) {

            Log.d("APISEARCHING", "Do IN BG  runs");


            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
//            Call<ApiResponse> call = apiInterface.getServiceUserWithSearchAndFilter(latitude, longitude, page, itemsPerPage, radius, 5, searchQuery);

            retrofit2.Call<TokenResponse> call = apiInterface.getFcmTokenDetailsUser(userId);


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
                            dialog.dismiss();

                        }
                    });


                }
            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();

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


                    FcmBearerTokenGenerator tokenGenerator = new FcmBearerTokenGenerator(OutGoingCallActivity.this);
                    tokenGenerator.generateAccessTokenAsync(new FcmBearerTokenGenerator.TokenGenerationListener() {
                        @Override
                        public void onTokenGenerated(String token) {


                            startSendingFcmNotificationToProvider(fcmData, token);

                            sendMissCallNotificationToUser(fcmData, token);

                        }

                        @Override
                        public void onTokenGenerationFailed(Exception e) {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    CustomToastNegative.create(OutGoingCallActivity.this,"Error: "+e.getMessage());

                                }
                            });

                        }
                    });


                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                            CustomToastNegative.create(OutGoingCallActivity.this,"Data Not Found!");


                        }
                    });


                }


            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();

                    }
                });

            }


        }


    }

    private void sendMissCallNotificationToUser(FCMData fcmData, String token) {
        Map<String, String> EXTRA_DATA = new HashMap<>();


        EXTRA_DATA.put("user_name", displayName);
        EXTRA_DATA.put("user_id", userID);
        EXTRA_DATA.put("user_dp", dpUrl);
        EXTRA_DATA.put("provider_id", providerId);
        EXTRA_DATA.put("call_by", call_by);


        SharedPreferences preferencesUser = getSharedPreferences("user", MODE_PRIVATE);
        SharedPreferences preferencesPro = getSharedPreferences("provider", MODE_PRIVATE);


        if (preferencesPro.contains("id")) {

            EXTRA_DATA.put("miss_call_name", preferencesPro.getString("name", "Expert Here Provider"));
            EXTRA_DATA.put("user_icon", preferencesPro.getString("logo_image", "Expert Here Provider"));

        }

        if (preferencesUser.contains("id")) {

            EXTRA_DATA.put("miss_call_name", preferencesUser.getString("name", "Expert Here Provider"));
            EXTRA_DATA.put("user_icon", preferencesUser.getString("dp", "Expert Here Provider"));

        }


//
        String MISS_CALL_NOTIFICATION_TO_USER = "www.experthere.in.calling.MISS_CALL_USER";

        EXTRA_DATA.put("event", MISS_CALL_NOTIFICATION_TO_USER);
        EXTRA_DATA.put("rejected_by", "provider");
        EXTRA_DATA.put("timestamp", String.valueOf(System.currentTimeMillis()));


        //


        Log.d("snaocs", "PPPP ");


        FcmApiClient.sendFcmMessageToDevice(getString(R.string.FcmSendUrl), token, fcmData.getFcm_token(), "Call Decline", "decline by user", EXTRA_DATA, "rejecte", new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        CustomToastNegative.create(OutGoingCallActivity.this,"Error " + e.getMessage());

                        if (isServiceRunning(OutGoingCallService.class)) {

                            Intent intent = new Intent(getApplicationContext(), OutGoingCallService.class);
                            Intent intent2 = new Intent(getApplicationContext(), DialToneGeneratorService.class);
                            stopService(intent);
                            stopService(intent2);

                            Intent broadcastIntent = new Intent(getApplicationContext(), OutGoingNotificationActionReceiver.class);
                            broadcastIntent.setAction(ACTION_STOP);
                            sendBroadcast(broadcastIntent);

                        }
                        dialog.dismiss();

                    }
                });


            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();

                    }
                });


                if (response.isSuccessful()) {


                    if (isServiceRunning(OutGoingCallService.class)) {

                        Intent intent = new Intent(getApplicationContext(), OutGoingCallService.class);
                        Intent intent2 = new Intent(getApplicationContext(), DialToneGeneratorService.class);
                        stopService(intent);
                        stopService(intent2);

                        Intent broadcastIntent = new Intent(getApplicationContext(), OutGoingNotificationActionReceiver.class);
                        broadcastIntent.setAction(ACTION_STOP);
                        sendBroadcast(broadcastIntent);


                    }


                } else {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CustomToastNegative.create(OutGoingCallActivity.this,"Error Token Invalid User!");


                            if (isServiceRunning(OutGoingCallService.class)) {

                                Intent intent = new Intent(getApplicationContext(), OutGoingCallService.class);
                                Intent intent2 = new Intent(getApplicationContext(), DialToneGeneratorService.class);
                                stopService(intent);
                                stopService(intent2);

                                Intent broadcastIntent = new Intent(getApplicationContext(), OutGoingNotificationActionReceiver.class);
                                broadcastIntent.setAction(ACTION_STOP);
                                sendBroadcast(broadcastIntent);

                            }

                        }
                    });

                }


            }
        });
    }

    private void sendMissCallNotificationToProvider(FCMData fcmData, String token) {
        Map<String, String> EXTRA_DATA = new HashMap<>();


        EXTRA_DATA.put("user_name", displayName);
        EXTRA_DATA.put("user_id", userID);
        EXTRA_DATA.put("user_dp", dpUrl);
        EXTRA_DATA.put("provider_id", providerId);
        EXTRA_DATA.put("call_by", call_by);


        SharedPreferences preferencesUser = getSharedPreferences("user", MODE_PRIVATE);
        SharedPreferences preferencesPro = getSharedPreferences("provider", MODE_PRIVATE);


        if (preferencesPro.contains("id")) {

            EXTRA_DATA.put("miss_call_name", preferencesPro.getString("name", "Expert Here Provider"));
            EXTRA_DATA.put("user_icon", preferencesPro.getString("logo_image", "Expert Here Provider"));

        }

        if (preferencesUser.contains("id")) {

            EXTRA_DATA.put("miss_call_name", preferencesUser.getString("name", "Expert Here Provider"));
            EXTRA_DATA.put("user_icon", preferencesUser.getString("dp", "Expert Here Provider"));

        }


//
        String MISS_CALL_NOTIFICATION_TO_USER = "www.experthere.in.calling.MISS_CALL_USER";

        EXTRA_DATA.put("event", MISS_CALL_NOTIFICATION_TO_USER);
        EXTRA_DATA.put("rejected_by", "provider");
        EXTRA_DATA.put("timestamp", String.valueOf(System.currentTimeMillis()));


        //


        Log.d("snaocs", "PPPP ");


        FcmApiClient.sendFcmMessageToDevice(getString(R.string.FcmSendUrl), token, fcmData.getFcm_token(), "Call Decline", "decline by user", EXTRA_DATA, "rejecte", new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (isServiceRunning(OutGoingCallService.class)) {

                            Intent intent = new Intent(getApplicationContext(), OutGoingCallService.class);
                            Intent intent2 = new Intent(getApplicationContext(), DialToneGeneratorService.class);
                            stopService(intent);
                            stopService(intent2);

                            Intent broadcastIntent = new Intent(getApplicationContext(), OutGoingNotificationActionReceiver.class);
                            broadcastIntent.setAction(ACTION_STOP);
                            sendBroadcast(broadcastIntent);

                        }
                        CustomToastNegative.create(OutGoingCallActivity.this,"Error " + e.getMessage());

                        dialog.dismiss();

                    }
                });


            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();

                    }
                });


                if (response.isSuccessful()) {


                    if (isServiceRunning(OutGoingCallService.class)) {

                        Intent intent = new Intent(getApplicationContext(), OutGoingCallService.class);
                        Intent intent2 = new Intent(getApplicationContext(), DialToneGeneratorService.class);
                        stopService(intent);
                        stopService(intent2);

                        Intent broadcastIntent = new Intent(getApplicationContext(), OutGoingNotificationActionReceiver.class);
                        broadcastIntent.setAction(ACTION_STOP);
                        sendBroadcast(broadcastIntent);


                    }


                } else {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            CustomToastNegative.create(OutGoingCallActivity.this,"Error Token Invalid User!");


                            if (isServiceRunning(OutGoingCallService.class)) {

                                Intent intent = new Intent(getApplicationContext(), OutGoingCallService.class);
                                Intent intent2 = new Intent(getApplicationContext(), DialToneGeneratorService.class);
                                stopService(intent);
                                stopService(intent2);

                                Intent broadcastIntent = new Intent(getApplicationContext(), OutGoingNotificationActionReceiver.class);
                                broadcastIntent.setAction(ACTION_STOP);
                                sendBroadcast(broadcastIntent);

                            }


                            dialog.dismiss();

                        }
                    });


                }


            }
        });
    }


    private void startSendingFcmNotificationToProvider(FCMData fcmData, String authToken) {


        Map<String, String> EXTRA_DATA = new HashMap<>();


        EXTRA_DATA.put("user_name", displayName);
        EXTRA_DATA.put("user_id", userID);
        EXTRA_DATA.put("user_dp", dpUrl);
        EXTRA_DATA.put("provider_id", providerId);
        EXTRA_DATA.put("call_by", call_by);


//
        String CALL_REJECTED_BY_USER = "www.experthere.in.calling.CALL_DECLINE_BY_USER";

        EXTRA_DATA.put("event", CALL_REJECTED_BY_USER);
        EXTRA_DATA.put("rejected_by", "provider");
        EXTRA_DATA.put("timestamp", String.valueOf(System.currentTimeMillis()));


        //


        Log.d("snaocs", "PPPP ");


        FcmApiClient.sendFcmMessageToDevice(getString(R.string.FcmSendUrl), authToken, fcmData.getFcm_token(), "Call Decline", "decline by user", EXTRA_DATA, "rejecte", new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CustomToastNegative.create(OutGoingCallActivity.this,"Error " + e.getMessage());


                        if (isServiceRunning(OutGoingCallService.class)) {

                            Intent intent = new Intent(getApplicationContext(), OutGoingCallService.class);
                            Intent intent2 = new Intent(getApplicationContext(), DialToneGeneratorService.class);
                            stopService(intent);
                            stopService(intent2);

                            Intent broadcastIntent = new Intent(getApplicationContext(), OutGoingNotificationActionReceiver.class);
                            broadcastIntent.setAction(ACTION_STOP);
                            sendBroadcast(broadcastIntent);

                        }

                        dialog.dismiss();

                    }
                });


            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();

                    }
                });

                if (response.isSuccessful()) {


                    if (isServiceRunning(OutGoingCallService.class)) {

                        Intent intent = new Intent(getApplicationContext(), OutGoingCallService.class);
                        Intent intent2 = new Intent(getApplicationContext(), DialToneGeneratorService.class);
                        stopService(intent);
                        stopService(intent2);

                        Intent broadcastIntent = new Intent(getApplicationContext(), OutGoingNotificationActionReceiver.class);
                        broadcastIntent.setAction(ACTION_STOP);
                        sendBroadcast(broadcastIntent);

                    }


                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CustomToastNegative.create(OutGoingCallActivity.this,"Error Token Invalid User!");

                            if (isServiceRunning(OutGoingCallService.class)) {

                                Intent intent = new Intent(getApplicationContext(), OutGoingCallService.class);
                                Intent intent2 = new Intent(getApplicationContext(), DialToneGeneratorService.class);
                                stopService(intent);
                                stopService(intent2);

                                Intent broadcastIntent = new Intent(getApplicationContext(), OutGoingNotificationActionReceiver.class);
                                broadcastIntent.setAction(ACTION_STOP);
                                sendBroadcast(broadcastIntent);

                            }

                        }
                    });
                }


            }
        });

    }

    private void startSendingFcmNotificationToUser(FCMData fcmData, String authToken) {


        Map<String, String> EXTRA_DATA = new HashMap<>();


        EXTRA_DATA.put("user_name", displayName);
        EXTRA_DATA.put("user_id", userID);
        EXTRA_DATA.put("user_dp", dpUrl);
        EXTRA_DATA.put("provider_id", providerId);
        EXTRA_DATA.put("call_by", call_by);

//
        String CALL_REJECTED_BY_USER = "www.experthere.in.calling.CALL_DECLINE_BY_USER";

        EXTRA_DATA.put("event", CALL_REJECTED_BY_USER);
        EXTRA_DATA.put("rejected_by", "user");
        //
        EXTRA_DATA.put("timestamp", String.valueOf(System.currentTimeMillis()));


        FcmApiClient.sendFcmMessageToDevice(getString(R.string.FcmSendUrl), authToken, fcmData.getFcm_token(), "Call Decline", "decline by user", EXTRA_DATA, "rejecte", new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {


                        CustomToastNegative.create(OutGoingCallActivity.this,"Error " + e.getMessage());

                        if (isServiceRunning(OutGoingCallService.class)) {

                            Intent intent = new Intent(getApplicationContext(), OutGoingCallService.class);
                            Intent intent2 = new Intent(getApplicationContext(), DialToneGeneratorService.class);
                            stopService(intent);
                            stopService(intent2);

                            Intent broadcastIntent = new Intent(getApplicationContext(), OutGoingNotificationActionReceiver.class);
                            broadcastIntent.setAction(ACTION_STOP);
                            sendBroadcast(broadcastIntent);

                        }

                        dialog.dismiss();

                    }
                });

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();

                    }
                });


                if (response.isSuccessful()) {


                    if (isServiceRunning(OutGoingCallService.class)) {

                        Intent intent = new Intent(getApplicationContext(), OutGoingCallService.class);
                        Intent intent2 = new Intent(getApplicationContext(), DialToneGeneratorService.class);
                        stopService(intent);
                        stopService(intent2);

                        Intent broadcastIntent = new Intent(getApplicationContext(), OutGoingNotificationActionReceiver.class);
                        broadcastIntent.setAction(ACTION_STOP);
                        sendBroadcast(broadcastIntent);

                    }


                } else {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            CustomToastNegative.create(OutGoingCallActivity.this,"Error Token Invalid User!");


                            if (isServiceRunning(OutGoingCallService.class)) {

                                Intent intent = new Intent(getApplicationContext(), OutGoingCallService.class);
                                Intent intent2 = new Intent(getApplicationContext(), DialToneGeneratorService.class);
                                stopService(intent);
                                stopService(intent2);

                                Intent broadcastIntent = new Intent(getApplicationContext(), OutGoingNotificationActionReceiver.class);
                                broadcastIntent.setAction(ACTION_STOP);
                                sendBroadcast(broadcastIntent);

                            }

                        }
                    });

                }


            }
        });

    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {

        try {

            new FetchProviderToken2(providerId).execute();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }


    private final BroadcastReceiver finishReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction() != null) {

                if (intent.getAction().equals(FINISH_OUTGOING)) {

                    finish();

                }

            }


        }
    };


}