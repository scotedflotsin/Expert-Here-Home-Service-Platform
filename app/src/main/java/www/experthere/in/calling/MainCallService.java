package www.experthere.in.calling;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Locale;

import io.agora.rtc2.Constants;
import io.agora.rtc2.IRtcEngineEventHandler;
import io.agora.rtc2.RtcEngine;
import retrofit2.Call;
import retrofit2.Response;
import www.experthere.in.R;
import www.experthere.in.api.ApiClient;
import www.experthere.in.api.ApiInterface;
import www.experthere.in.helper.CustomToastNegative;
import www.experthere.in.helper.CustomToastPositive;
import www.experthere.in.model.SuccessMessageResponse;

public class MainCallService extends Service {
    private static final String LOG_TAG = "CALLSERVICEINCOMING";

    String FINISH_MAIN = "www.experthere.in.calling.FINISH_MAIN";

    private final int USER_ID = 0;
    private RtcEngine mRtcEngine; // Tutorial Step 1

    String channel_id;

    String accessToken;

    String user_id, provider_id, call_by;

    String NOTIFICATION_CHANNEL_ID;

    Bundle pendingbundle;
    NotificationCompat.Builder builder;
    NotificationManager notificationManager;
    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() { // Tutorial Step 1


        @Override
        public void onError(int err) {

            Log.d(LOG_TAG, "Error : " + err);

            super.onError(err);
        }

        @Override
        public void onUserJoined(int uid, int elapsed) {
            super.onUserJoined(uid, elapsed);
            Log.d(LOG_TAG, "onUserJoined: " + uid);

        }

        @Override
        public void onUserOffline(final int uid, final int reason) { // Tutorial Step 4

            Log.d(LOG_TAG, "User End: " + uid + "\n Offline reason - ");

            onRemoteUserLeft(uid, reason);

        }

        @Override
        public void onUserMuteAudio(final int uid, final boolean muted) { // Tutorial Step 6

            onRemoteUserVoiceMuted(uid, muted);


            Log.d(LOG_TAG, " Mute call ");

        }
    };

    private long startTime;
    private long totalTime; // Variable to hold the total time

    private final Handler handler = new Handler();
    private final Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;


            sendDataTimer(String.format("%02d:%02d", minutes, seconds));


            handler.postDelayed(this, 500); // Update every 0.5 second


            if (notificationManager != null) {

                builder.setContentTitle("Call in progress " + String.format("%02d:%02d", minutes, seconds));
                notificationManager.notify(Integer.parseInt(NOTIFICATION_CHANNEL_ID), builder.build());


            }

        }
    };

    private void sendDataTimer(String format) {

        Intent filter = new Intent("timerReceiver");
        Bundle bundle = new Bundle();

        bundle.putString("time", format);
        filter.putExtras(bundle);
        getApplicationContext().sendBroadcast(filter);


    }


    @Override
    public void onCreate() {
        super.onCreate();

        startTime = System.currentTimeMillis();
        handler.postDelayed(timerRunnable, 0);

    }

    private void createNotificationChannel() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Call Notifications";
            String description = "Channel for call notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance);
            channel.setDescription(description);
            notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
//        }

    }

    @SuppressLint("MissingPermission")
    private void showCallNotification(String duration) {
        builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle("Call in progress " + duration)
                .setSilent(true)
                .setContentIntent(createIntentForCallingActivity())
                .setAutoCancel(false)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(Integer.parseInt(NOTIFICATION_CHANNEL_ID), builder.build());


    }


    private PendingIntent createIntentForCallingActivity() {
        Intent intent = new Intent(this, MainCallActivity.class);

        if (pendingbundle != null) {
            intent.putExtras(pendingbundle);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    private void cancelCallNotification() {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.cancel(Integer.parseInt(NOTIFICATION_CHANNEL_ID));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle bundle = intent.getExtras();
        if (intent.getAction() != null) {

            if (intent.getAction().equals("END_CALL")) {
                onEncCallClicked();
            }
        }


        if (bundle != null) {

            pendingbundle = bundle;

            user_id = bundle.getString("user_id");
            provider_id = bundle.getString("provider_id");
            call_by = bundle.getString("call_by");

            Log.d("MAINCALLDATABUNDLE", "onCreate");
            Log.d("MAINCALLDATABUNDLE", "NAME : " + bundle.getString("user_name"));
            Log.d("MAINCALLDATABUNDLE", "USER ID : " + bundle.getString("user_id"));
            Log.d("MAINCALLDATABUNDLE", "Pro ID : " + bundle.getString("provider_id"));
            Log.d("MAINCALLDATABUNDLE", "AGORA TOKEN : " + bundle.getString("agora_token") + ":asdf");


            accessToken = bundle.getString("agora_token", "0");

            int uid = Integer.parseInt(bundle.getString("user_id", "0"));
            int pid = Integer.parseInt(bundle.getString("provider_id", "0"));

            if (uid + pid == 0 || accessToken.equals("0")) {


                CustomToastNegative.create(this, "Cant Join UID PID not available!");



            } else {

                int finalAgoraChannel = uid + pid;
                channel_id = String.valueOf(finalAgoraChannel);
                Log.d("MAINCALLDATABUNDLE", "CID : " + channel_id);


                Log.d(LOG_TAG, " USER ID  " + USER_ID);


                if (requestAudioPermissions()) {

                    Log.d(LOG_TAG, " INITIATING AGORA FROM user side ");

                    initAgoraEngineAndJoinChannel();
                    Log.d(LOG_TAG, " Channel ID " + channel_id);
                    Log.d(LOG_TAG, " USER ID " + USER_ID);


                    NOTIFICATION_CHANNEL_ID = channel_id;


                    createNotificationChannel();
                    showCallNotification("0:0");


                }


            }
        }


        return START_NOT_STICKY;

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;


    }

    // Tutorial Step 1
    private void initializeAgoraEngine() {

        SharedPreferences preferences = getSharedPreferences("keys",MODE_PRIVATE);
        String agoraAppId = preferences.getString("agora_app_id","0");

        Log.d("KEYPREFRENCE", "agora APP ID : "+agoraAppId);



        try {
            mRtcEngine = RtcEngine.create(getBaseContext(), agoraAppId, mRtcEventHandler);
        } catch (Exception e) {
            Log.e(LOG_TAG, Log.getStackTraceString(e));

            Log.d(LOG_TAG, "initializeAgoraEngine: " + e.getMessage());
            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }
    }

    // Tutorial Step 2
    private void joinChannel() {
//        String accessToken = getString(R.string.agoraTempKey);
//        if (TextUtils.equals(accessToken, "") || TextUtils.equals(accessToken, "#YOUR ACCESS TOKEN#")) {
//            accessToken = null; // default, no token
//        }

        // Sets the channel profile of the Agora RtcEngine.
        // CHANNEL_PROFILE_COMMUNICATION(0): (Default) The Communication profile. Use this profile in one-on-one calls or group calls, where all users can talk freely.
        // CHANNEL_PROFILE_LIVE_BROADCASTING(1): The Live-Broadcast profile. Users in a live-broadcast channel have a role as either broadcaster or audience. A broadcaster can both send and receive streams; an audience can only receive streams.
        mRtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_COMMUNICATION);
        // Allows a user to join a channel.
        mRtcEngine.joinChannel(accessToken, channel_id, "Extra Optional Data", USER_ID); // if you do not specify the uid, we will generate the uid for you


    }

    // Tutorial Step 3
    private void leaveChannel() {

        if (mRtcEngine != null) {

            mRtcEngine.leaveChannel();
        }


        int seconds = (int) (getTotalTime() / 1000);
        int minutes = seconds / 60;
        seconds = seconds % 60;
        String totTime = String.format("%02d:%02d", minutes, seconds);

        Log.d("TAG", "CALL DISCONNECT WITH timer MILLS " + getTotalTime());
        Log.d("TAG", "CALL DISCONNECT WITH timer Formated " + totTime);


    }

    // Tutorial Step 4
    private void onRemoteUserLeft(int uid, int reason) {
        showLongToast(String.format(Locale.US, "user %d left %d", (uid & 0xFFFFFFFFL), reason));
        finish();

    }

    private void finish() {


        Intent filter = new Intent(FINISH_MAIN);
        sendBroadcast(filter);
        if (mRtcEngine != null) {
            mRtcEngine.leaveChannel();
        }
        cancelCallNotification();
        stopSelf();
        stopForeground(true);
//        finish();


    }

    // Tutorial Step 6
    private void onRemoteUserVoiceMuted(int uid, boolean muted) {

        if (muted) {
            showLongToast("user  muted The Call");

        } else {

            showLongToast("user un muted The Call");

        }

    }

    boolean isGranted;

    private boolean requestAudioPermissions() {


        Dexter.withContext(getApplicationContext()).withPermissions(Manifest.permission.RECORD_AUDIO).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {

                if (multiplePermissionsReport.areAllPermissionsGranted()) {

                    isGranted = true;

                }

                if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {

                    showPermissionDeniedDialog();
                }


            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {

            }
        }).check();

        return isGranted;


    }

    private void showPermissionDeniedDialog() {


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permission Denied")
                .setMessage("You denied audio/camera permission permanently. Please go to app settings and enable permissions manually.")
                .setPositiveButton("Go to Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        openAppSettings();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builder.create().show();
    }

    private void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

//    private void sendFcmMessage(String url, String authToken, String device_token, String title,
//                                String body, String extraData, String clickAction) {
//
//        FcmApiClient.sendFcmMessageToDevice(
//                url,
//                authToken,
//                device_token,
//                title,
//                body,
//                extraData,
//                clickAction,
//                new Callback() {
//                    @Override
//                    public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) throws IOException {
//
//                        String responseBody = response.body().string();
//                        Log.d("TAG", "onResponse: " + responseBody);
//
//                    }
//
//                    @Override
//                    public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
//
//                        Log.d("TAG", "onFailure: " + e.getMessage());
//                    }
//
//
//                }
//        );
//    }


    // Optionally, you can expose a method to get the total time
    public long getTotalTime() {

        totalTime = System.currentTimeMillis() - startTime;
        return totalTime;
    }


    private class CallHistorySavedUser extends AsyncTask<Void, Void, SuccessMessageResponse> {

        int userId, providerId;
        BigInteger duration;
        String callBy, received;

        public CallHistorySavedUser(int userId, int providerId, BigInteger duration, String callBy, String received) {
            this.userId = userId;
            this.providerId = providerId;
            this.duration = duration;
            this.callBy = callBy;
            this.received = received;
        }

        @Override
        protected void onPreExecute() {


            super.onPreExecute();

        }

        @Override
        protected SuccessMessageResponse doInBackground(Void... voids) {

            Log.d("JHVWHVJJJWDW", "Do in BG runing");


            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
//            Call<ApiResponse> call = apiInterface.getServiceUserWithSearchAndFilter(latitude, longitude, page, itemsPerPage, radius, 5, searchQuery);
            BigInteger time = BigInteger.valueOf(System.currentTimeMillis());

            Call<SuccessMessageResponse> call = apiInterface.saveCallHistoryUser(userId, providerId, duration, callBy, received, time);


            try {
                Response<SuccessMessageResponse> response = call.execute();
                if (response.isSuccessful() && response.body() != null) {


                    return response.body();

                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("APISEARCHING", "Error " + e.getMessage());


            }

            return null;
        }

        @Override
        protected void onPostExecute(SuccessMessageResponse serviceResponse) {
            super.onPostExecute(serviceResponse);

            if (serviceResponse != null) {
                if (serviceResponse.isSuccess()) {

                    if (serviceResponse.getMessage().equals("Call history saved successfully.")) {


                    } else {
                        finish();

                        CustomToastNegative.create(getApplicationContext(), "Error: "+serviceResponse.getMessage());

                    }


                } else {
                    finish();

                    CustomToastNegative.create(getApplicationContext(), " "+serviceResponse.getMessage());
                }
            } else {
                finish();

                // Handle the case when there is no responsesw
            }
        }

    }

    private class CallHistorySavedProvider extends AsyncTask<Void, Void, SuccessMessageResponse> {

        int userId, providerId;
        BigInteger duration;
        String callBy, received;

        public CallHistorySavedProvider(int userId, int providerId, BigInteger duration, String callBy, String received) {
            this.userId = userId;
            this.providerId = providerId;
            this.duration = duration;
            this.callBy = callBy;
            this.received = received;
        }

        @Override
        protected void onPreExecute() {


            super.onPreExecute();

        }

        @Override
        protected SuccessMessageResponse doInBackground(Void... voids) {

            Log.d("JHVWHVJJJWDW", "Do in BG runing");


            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
//            Call<ApiResponse> call = apiInterface.getServiceUserWithSearchAndFilter(latitude, longitude, page, itemsPerPage, radius, 5, searchQuery);
            BigInteger time = BigInteger.valueOf(System.currentTimeMillis());

            Call<SuccessMessageResponse> call = apiInterface.saveCallHistoryProvider(userId, providerId, duration, callBy, received, time);


            try {
                Response<SuccessMessageResponse> response = call.execute();
                if (response.isSuccessful() && response.body() != null) {


                    return response.body();

                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("APISEARCHING", "Error " + e.getMessage());


            }

            return null;
        }

        @Override
        protected void onPostExecute(SuccessMessageResponse serviceResponse) {
            super.onPostExecute(serviceResponse);

            if (serviceResponse != null) {
                if (serviceResponse.isSuccess()) {

                    if (serviceResponse.getMessage().equals("Call history saved successfully.")) {


                        finish();


                    } else {
                        CustomToastNegative.create(getApplicationContext(), "Error: "+serviceResponse.getMessage());


                        finish();


                    }


                } else {
                    finish();

                    CustomToastNegative.create(getApplicationContext(), " "+serviceResponse.getMessage());

             }
            } else {
                // Handle the case when there is no responsesw
            }
        }

    }


    // Tutorial Step 3
    private void onEncCallClicked() {


        try {

            if (user_id!=null && provider_id!=null) {

                new CallHistorySavedUser(Integer.parseInt(user_id), Integer.parseInt(provider_id), BigInteger.valueOf(getTotalTime()), call_by, "answered").execute();
                new CallHistorySavedProvider(Integer.parseInt(user_id), Integer.parseInt(provider_id), BigInteger.valueOf(getTotalTime()), call_by, "answered").execute();
            }
        } catch (NumberFormatException e) {
            throw new RuntimeException(e);
        }


    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        handler.removeCallbacks(timerRunnable);
        leaveChannel();

        RtcEngine.destroy();
        mRtcEngine = null;
        cancelCallNotification();


        Log.d("KJSlMSKBSS", "onDestroy: ");


    }

    private void initAgoraEngineAndJoinChannel() {
        initializeAgoraEngine();     // Tutorial Step 1
        joinChannel();               // Tutorial Step 2
    }

    public final void showLongToast(final String msg) {


        CustomToastPositive.create(getApplicationContext(), msg);



    }

}
