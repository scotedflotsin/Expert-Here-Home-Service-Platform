package www.experthere.in.calling;


import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

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
import www.experthere.in.calling.OutGoingCallActivity;
import www.experthere.in.helper.CustomToastNegative;
import www.experthere.in.model.FCMData;
import www.experthere.in.model.TokenResponse;

public class OutGoingCallService extends Service {
    static int NOTIFICATION_ID = 0;
    private static final String CHANNEL_ID = "OutGoingCallChannel";
    private static final String CHANNEL_NAME = "OutGoingCallChannel";
    public static final String ACTION_STOP = "www.experthere.in.calling.STOP";

    private String dpUrl, displayName, userID, providerId, call_by;
    private static final String TAG = "CHECKOUTGOINGSERVICE";

    String FINISH_OUTGOING = "www.experthere.in.calling.FINISH_OUTGOING";

    private static final long TIMER_DURATION_MS = 60000; // 60 seconds in milliseconds
    private CountDownTimer countDownTimer;

    private boolean isTimerRunning = false;

    SharedPreferences providerPref, userPref;


    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();

        providerPref = getSharedPreferences("provider", MODE_PRIVATE);
        userPref = getSharedPreferences("user", MODE_PRIVATE);

        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        audioManager.setSpeakerphoneOn(false);


        Intent i = new Intent(getApplicationContext(), DialToneGeneratorService.class);
        startService(i);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        Log.d(TAG, "onStartCommand: IS Timer Running " + isTimerRunning);


        if (intent != null) {
            String action = intent.getAction();
            if (ACTION_STOP.equals(action)) {
                // User clicked on "Dismiss Call" action, stop the service

                Bundle bundle = new Bundle();
                bundle.putBoolean("finish", true);

                Intent broadcastIntent = new Intent(this, OutGoingNotificationActionReceiver.class);
                broadcastIntent.setAction(ACTION_STOP);
                broadcastIntent.putExtras(bundle);
                sendBroadcast(broadcastIntent);


                stopTimer();

                stopForeground(true);
                stopSelf();


                return START_NOT_STICKY;
            }

            Bundle extras = intent.getExtras();
            if (extras != null) {
                dpUrl = extras.getString("dp");
                call_by = extras.getString("call_by");
                displayName = extras.getString("display_name");
                userID = extras.getString("user_id");
                providerId = extras.getString("provider_id");

                // Start the foreground service with notification

//                Log.d(TAG, "DATA " + dpUrl + "\n" + call_by + "\n" + displayName + "\n" + userID + "\n" + providerId);

                NOTIFICATION_ID = Integer.parseInt(userID + providerId);


            }
        }


        if (!isTimerRunning) {
            startTimer();
        }

        isTimerRunning = true;

        startForeground(NOTIFICATION_ID, buildNotification(intent));

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Notification buildNotification(Intent intent) {


        Intent notificationIntent = new Intent(this, OutGoingCallActivity.class);
        notificationIntent.putExtras(getExtrasBundle(intent));

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        Intent stopIntent = new Intent(this, OutGoingCallService.class);
        stopIntent.setAction(ACTION_STOP);
        PendingIntent stopPendingIntent = PendingIntent.getService(this, 0,
                stopIntent, PendingIntent.FLAG_IMMUTABLE);


        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Outgoing Call")
                .setContentText("Call in progress with " + displayName)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentIntent(pendingIntent)
                .setSilent(true)
//                .addAction(android.R.drawable.ic_menu_close_clear_cancel,
//                        "Dismiss Call", stopPendingIntent)
                .setOngoing(true) // Notification cannot be dismissed by user
                .build();
    }

    private Bundle getExtrasBundle(Intent intent) {
        Bundle bundle = new Bundle();
        bundle.putString("call_by", call_by);
        bundle.putString("user_id", userID);
        bundle.putString("provider_id", providerId);
        bundle.putString("dp", dpUrl);
        bundle.putString("display_name", displayName);

        Log.d(TAG, "DATA 2 " + dpUrl + "\n" + call_by + "\n" + displayName + "\n" + userID + "\n" + providerId);


        return bundle;
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(TIMER_DURATION_MS, 1000) {
            public void onTick(long millisUntilFinished) {
                // Timer is counting down
                Log.d(TAG, "TIMER COUNTING " + millisUntilFinished / 60);
            }

            public void onFinish() {
                // Timer finished, close the service
                if (providerPref.contains("id")) {
                    try {
                        Log.d(TAG, "TIMER COMPLETE Hitting FetchUserToken3");
                        new FetchUserToken3(userID).execute();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                if (userPref.contains("id")) {
                    try {
                        Log.d(TAG, "TIMER COMPLETE Hitting FetchProviderToken3");
                        new FetchProviderToken3(providerId).execute();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }.start();


    }


    private class FetchProviderToken3 extends AsyncTask<Void, Void, TokenResponse> {


        String provider__ID;

        public FetchProviderToken3(String provider__ID) {
            this.provider__ID = provider__ID;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected TokenResponse doInBackground(Void... voids) {

            Log.d("APISEARCHING", "Do IN BG  runs");


            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
//            Call<ApiResponse> call = apiInterface.getServiceUserWithSearchAndFilter(latitude, longitude, page, itemsPerPage, radius, 5, searchQuery);

            retrofit2.Call<TokenResponse> call = apiInterface.getFcmTokenDetailsProvider(provider__ID);


            try {
                retrofit2.Response<TokenResponse> response = call.execute();
                if (response.isSuccessful() && response.body() != null) {


                    if (response.body().getData() != null) {
                        return response.body();
                    }
                } else {

                }
            } catch (IOException e) {
                e.printStackTrace();

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


                    FcmBearerTokenGenerator tokenGenerator = new FcmBearerTokenGenerator(OutGoingCallService.this);
                    tokenGenerator.generateAccessTokenAsync(new FcmBearerTokenGenerator.TokenGenerationListener() {
                        @Override
                        public void onTokenGenerated(String token) {

                            sendMissCallNotificationToProvider(fcmData, token);

                        }

                        @Override
                        public void onTokenGenerationFailed(Exception e) {

                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    CustomToastNegative.create(OutGoingCallService.this,"Error " + e.getMessage());

                                }
                            });
                            stopService();


                        }
                    });


                } else {


                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {

                            CustomToastNegative.create(OutGoingCallService.this,"Data Not Found!");

                        }
                    });
                    stopService();

                }


            } else {

                stopService();

            }


        }


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


                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        CustomToastNegative.create(OutGoingCallService.this,"Error " + e.getMessage());
                    }
                });

                stopService();


            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {


                if (response.isSuccessful()) {


                    stopService();


                } else {
                    stopService();


                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {

                            CustomToastNegative.create(OutGoingCallService.this,"Error Token Invalid User!");

                        }
                    });

                }


            }
        });

    }

    private class FetchUserToken3 extends AsyncTask<Void, Void, TokenResponse> {


        String userId;

        public FetchUserToken3(String userId) {
            this.userId = userId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


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

                }
            } catch (IOException e) {
                e.printStackTrace();

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


                    FcmBearerTokenGenerator tokenGenerator = new FcmBearerTokenGenerator(OutGoingCallService.this);
                    tokenGenerator.generateAccessTokenAsync(new FcmBearerTokenGenerator.TokenGenerationListener() {
                        @Override
                        public void onTokenGenerated(String token) {

                            sendMissCallNotificationToUser(fcmData, token);

                        }

                        @Override
                        public void onTokenGenerationFailed(Exception e) {

                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {

                                    CustomToastNegative.create(OutGoingCallService.this,"Error " + e.getMessage());

                                }
                            });
                            stopService();


                        }
                    });


                } else {


                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            CustomToastNegative.create(OutGoingCallService.this,"Data Not Found! ");
                        }
                    });

                    stopService();

                }


            } else {

                stopService();

            }


        }


    }

    private void stopTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }


        Intent i = new Intent(getApplicationContext(), DialToneGeneratorService.class);
        stopService(i);

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

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        CustomToastNegative.create(OutGoingCallService.this,"Error " + e.getMessage());
                    }
                });

                stopService();


            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {


                if (response.isSuccessful()) {


                    if (isServiceRunning(OutGoingCallService.class)) {

                        stopService();


                    }


                } else {
                    stopService();
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            CustomToastNegative.create(OutGoingCallService.this,"Error Token Invalid User" );

                        }
                    });
                }


            }
        });
    }


    private void stopService() {


        stopTimer();
        stopForeground(true);
        stopSelf();

        Intent filter = new Intent(FINISH_OUTGOING);
        sendBroadcast(filter);


        Intent broadcastIntent = new Intent(this, OutGoingNotificationActionReceiver.class);
        broadcastIntent.setAction(ACTION_STOP);
        sendBroadcast(broadcastIntent);


        Intent serviceIntent = new Intent(getApplicationContext(), OutGoingCallService.class);
        serviceIntent.setAction(ACTION_STOP);
        stopService(serviceIntent);


        Intent dialServiceIntent = new Intent(getApplicationContext(), DialToneGeneratorService.class);
        dialServiceIntent.setAction(ACTION_STOP);
        stopService(dialServiceIntent);


    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        stopService();

        Log.d(TAG, "SERVICE IS STOPED NOW!");
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


    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }


}
