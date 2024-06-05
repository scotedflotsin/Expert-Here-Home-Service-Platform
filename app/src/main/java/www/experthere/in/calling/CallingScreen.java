package www.experthere.in.calling;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.math.BigInteger;

import www.experthere.in.FCM.AnswerCallReceiver;
import www.experthere.in.FCM.RejectCallReceiver;
import www.experthere.in.MyApplication;
import www.experthere.in.R;
import www.experthere.in.SplashScreen;
import www.experthere.in.api.ApiClient;
import www.experthere.in.api.ApiInterface;
import www.experthere.in.helper.CustomToastNegative;
import www.experthere.in.helper.ProcessingDialog;
import www.experthere.in.model.SuccessMessageResponse;

public class CallingScreen extends AppCompatActivity {

    String user_name, user_id, user_dp, provider_id, call_by, title, body;
    private static final String TAG = "CallingScreenTag";
    ImageView userImage;
    TextView callByTxt, displayNameTxt;

    int NOT_ID = 0;

    private CountDownTimer countDownTimer;

    private static final long TIMER_DURATION_MS = 60000; // 30 seconds in milliseconds
    String CALL_REJECTED_BY_USER = "www.experthere.in.calling.CALL_DECLINE_BY_USER";
    String FINISH_ACTION = "www.experthere.in.calling.ACTION_FINISH_ACTIVITY";

    ProcessingDialog processingDialog;

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        setContentView(R.layout.activity_callig_screen);


        processingDialog = new ProcessingDialog(CallingScreen.this);
//        // Check if the device is locked
//        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
//        boolean isDeviceLocked = keyguardManager.isDeviceLocked();
//
//        // Check if the screen is off
//        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
//        boolean isScreenOff = !powerManager.isInteractive();
//
//        // Show the activity as a window overlay if the device is locked or the screen is off
//        if (isDeviceLocked || isScreenOff) {
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
//                    | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
//                    | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        }


        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "YourApp:WakeLock");
        wakeLock.acquire();

        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("YourApp:KeyguardLock");
        keyguardLock.disableKeyguard();


        IntentFilter filter1 = new IntentFilter();
        filter1.addAction(CALL_REJECTED_BY_USER);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(dismissCallingScreenReceiver, filter1, Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(dismissCallingScreenReceiver, filter1);

        }


        startTimer();

        // Register the BroadcastReceiver to listen for the custom action
        IntentFilter filter = new IntentFilter();
        filter.addAction(FINISH_ACTION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(finishReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(finishReceiver, filter);

        }


        userImage = findViewById(R.id.imageView8);
        callByTxt = findViewById(R.id.textView3);
        displayNameTxt = findViewById(R.id.textView5);


        Bundle bundle = getIntent().getExtras();


        if (bundle != null) {

            body = bundle.getString("body", "0");
            title = bundle.getString("title", "0");
            call_by = bundle.getString("call_by", "0");
            provider_id = bundle.getString("provider_id", "0");
            user_dp = bundle.getString("user_dp", "0");
            user_id = bundle.getString("user_id", "0");
            user_name = bundle.getString("user_name", "0");
            NOT_ID = bundle.getInt("notification_id", 0);


        }

        Glide.with(this).load(user_dp).circleCrop().into(userImage);
        if (call_by.equalsIgnoreCase("user")) {
            callByTxt.setText("User");

        } else {
            callByTxt.setText("Provider");

        }
        displayNameTxt.setText(user_name);


        findViewById(R.id.declineBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent declineIntent = new Intent(CallingScreen.this, RejectCallReceiver.class);
                declineIntent.putExtras(getIntent());
                declineIntent.setAction(RejectCallReceiver.DECLINE_CALL_ACTION);


                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                if (bundle != null) {
                    notificationManager.cancel(bundle.getInt("notification_id", 0));
                    Log.d("DR3076818", "This is hiting.");
                }


                sendBroadcast(declineIntent);

         finish();


            }
        });


        findViewById(R.id.pickBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                processingDialog.show();


                    Log.e(TAG, "onClick: Pick  button");
                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                    if (bundle != null) {
                        notificationManager.cancel(bundle.getInt("notification_id", 0));
                    }

                    Intent acceptIntent = new Intent(CallingScreen.this, AnswerCallReceiver.class);
                    acceptIntent.putExtras(getIntent());
                    acceptIntent.setAction(AnswerCallReceiver.ANSWER_CALL_ACTION);
                    sendBroadcast(acceptIntent);

                    finish();


            }
        });


    }


    @Override
    protected void onPause() {
        super.onPause();


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

    private final BroadcastReceiver finishReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Finish the activity

            if (intent.getAction() != null && intent.getAction().matches(FINISH_ACTION)) {

                finish();
                countDownTimer.cancel();


                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                manager.cancel(NOT_ID);


            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(finishReceiver);
        unregisterReceiver(dismissCallingScreenReceiver);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.cancel(NOT_ID);


        if (processingDialog != null && processingDialog.isShowing()) {
            processingDialog.dismiss();
        }


    }


    private void startTimer() {
        countDownTimer = new CountDownTimer(TIMER_DURATION_MS, 1000) {
            public void onTick(long millisUntilFinished) {
                // Timer is counting down
            }

            public void onFinish() {
                // Timer finished, close the service


                try {


                    new CallHistorySaved(Integer.parseInt(user_id), Integer.parseInt(provider_id), BigInteger.valueOf(0), call_by, "not_answered", getApplicationContext()).execute();


                } catch (NumberFormatException e) {
                    throw new RuntimeException(e);
                }

            }
        }.start();


    }


    private class CallHistorySaved extends AsyncTask<Void, Void, SuccessMessageResponse> {

        int userId, providerId;
        BigInteger duration;
        String callBy, received;
        Context context;

        public CallHistorySaved(int userId, int providerId, BigInteger duration, String callBy, String received, Context context) {
            this.userId = userId;
            this.providerId = providerId;
            this.duration = duration;
            this.callBy = callBy;
            this.received = received;
            this.context = context;
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

            retrofit2.Call<SuccessMessageResponse> call = apiInterface.saveCallHistoryUser(userId, providerId, duration, callBy, received, time);


            try {
                retrofit2.Response<SuccessMessageResponse> response = call.execute();
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


                        try {
                            new CallHistorySavedPro(Integer.parseInt(user_id), Integer.parseInt(provider_id), BigInteger.valueOf(0), call_by, "not_answered", getApplicationContext()).execute();
                        } catch (NumberFormatException e) {
                            throw new RuntimeException(e);
                        }


                    } else {

                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {

                                CustomToastNegative.create(getApplicationContext(), "Error: " + serviceResponse.getMessage());

                                doneCallHistory();

                            }
                        });
                    }


                } else {

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            CustomToastNegative.create(getApplicationContext(), " " + serviceResponse.getMessage());
                            doneCallHistory();

                        }
                    });
                }
            } else {
                // Handle the case when there is no responsesw
                doneCallHistory();

            }
        }

    }

    private class CallHistorySavedPro extends AsyncTask<Void, Void, SuccessMessageResponse> {

        int userId, providerId;
        BigInteger duration;
        String callBy, received;
        Context context;

        public CallHistorySavedPro(int userId, int providerId, BigInteger duration, String callBy, String received, Context context) {
            this.userId = userId;
            this.providerId = providerId;
            this.duration = duration;
            this.callBy = callBy;
            this.received = received;
            this.context = context;
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

            retrofit2.Call<SuccessMessageResponse> call = apiInterface.saveCallHistoryProvider(userId, providerId, duration, callBy, received, time);


            try {
                retrofit2.Response<SuccessMessageResponse> response = call.execute();
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


                        doneCallHistory();


                    } else {

                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                CustomToastNegative.create(getApplicationContext(), "Error: " + serviceResponse.getMessage());
                                doneCallHistory();

                            }
                        });
                    }


                } else {

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            CustomToastNegative.create(getApplicationContext(), " " + serviceResponse.getMessage());
                            doneCallHistory();

                        }
                    });
                }
            } else {
                // Handle the case when there is no responsesw
                doneCallHistory();

            }
        }

    }

    private void doneCallHistory() {


        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.cancel(NOT_ID);


        Intent intent = new Intent();
        intent.setAction("www.experthere.in.calling.STOP_RING");
        sendBroadcast(intent);

        finish();


    }

    private final BroadcastReceiver dismissCallingScreenReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null && intent.getAction().equals(CALL_REJECTED_BY_USER)) {
                // Finish the activity


                closeActivity();

            }
        }
    };


    private void closeActivity() {

        finish();

    }


}