package www.experthere.in.FCM;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.messaging.FirebaseMessagingService;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Random;

import www.experthere.in.MyApplication;
import www.experthere.in.R;
import www.experthere.in.SplashScreen;
import www.experthere.in.api.ApiClient;
import www.experthere.in.api.ApiInterface;
import www.experthere.in.calling.CallingScreen;
import www.experthere.in.calling.DialToneGeneratorService;
import www.experthere.in.calling.MainCallActivity;
import www.experthere.in.calling.MainCallService;
import www.experthere.in.calling.OutGoingCallService;
import www.experthere.in.calling.OutGoingNotificationActionReceiver;
import www.experthere.in.helper.CustomToastNegative;
import www.experthere.in.helper.DateTimeUtils;
import www.experthere.in.model.SuccessMessageResponse;
import www.experthere.in.serviceProvider.ProviderHome;
import www.experthere.in.users.HomeActivity;

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
public class MyFirebaseMessagingService extends FirebaseMessagingService {


    String CHANNEL_ID = "CALL_EXPERT_HERE";
    String CHANNEL_NAME = "CALL_EXPERT_HERE";

    String EVENT_INCOMING_CALL = "incoming_call";

    String ANSWER_CALL_ACTION = "www.experthere.in.calling.CALL_RECEIVED";
    String DECLINE_CALL_ACTION = "www.experthere.in.calling.CALL_DECLINE";
    String CALL_REJECTED_BY_USER = "www.experthere.in.calling.CALL_DECLINE_BY_USER";
    public static final String ACTION_STOP = "www.experthere.in.calling.STOP";
    String FINISH_MAIN = "www.experthere.in.calling.FINISH_MAIN";
    String STOPS_RING = "www.experthere.in.calling.STOP_RING";
    String MISS_CALL_NOTIFICATION_TO_USER = "www.experthere.in.calling.MISS_CALL_USER";

    String REVIEW_NOTIFICATION_TO_USER = "www.experthere.in.NEW_REVIEW_ADDED";

    int NOT_ID = 0;


    private Ringtone ringtone;
    Vibrator v;
    SharedPreferences providerPref, userPref;


    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("fcm", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.clear().apply();

        editor.putString("device_token", token);
        editor.apply();


    }


    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    public void handleIntent(Intent intent) {
        super.handleIntent(intent);


        Log.d("NOHIIEBEIB", "handleIntent: RUNS");

        providerPref = getSharedPreferences("provider", MODE_PRIVATE);
        userPref = getSharedPreferences("user", MODE_PRIVATE);


        IntentFilter ringFilter = new IntentFilter(STOPS_RING);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(stopCallTone, ringFilter, Context.RECEIVER_EXPORTED);
        } else {
            registerReceiver(stopCallTone, ringFilter);

        }
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);


        if (intent.getExtras() != null) {

            Log.d("MESSJHSJHVSJHV", "From: " + intent.getExtras().toString());

            String EVENT = intent.getExtras().getString("event");

            Log.d("NOHIIEBEIB", "handleIntent: EVENT " + EVENT);


            if (EVENT != null) {


                if (EVENT.equals(EVENT_INCOMING_CALL)) {


                    if (!isServiceRunning(MainCallService.class) && !isServiceRunning(OutGoingCallService.class)) {


                        long timeStamp = Long.parseLong(intent.getExtras().getString("timestamp", "101010"));

                        if (DateTimeUtils.isTimeDifferenceLessThanOneMinute(timeStamp)) {


                            playRingTone();

                            int Userid = Integer.parseInt(intent.getExtras().getString("user_id", "0"));
                            int ProviderId = Integer.parseInt(intent.getExtras().getString("provider_id", "0"));


                            Log.d("lnaskbska", "handleIntent: " + Userid + " - " + ProviderId);

                            NOT_ID = Userid + ProviderId;


                            CHANNEL_ID = CHANNEL_ID + intent.getExtras().getString("user_id") + intent.getExtras().getString("provider_id");


                            Intent intent1 = new Intent(getApplicationContext(), CallingScreen.class);

                            Bundle bundle = new Bundle();
                            bundle.putInt("notification_id", NOT_ID);
                            bundle.putBoolean("finish", true);


                            Log.d("ANAKKAKJANK", "handleIntent: " + intent.getExtras().getString("agora_token"));

                            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                            intent1.putExtras(intent);
                            intent1.putExtras(bundle);
                            getApplicationContext().startActivity(intent1);


                            createNotificationChannel(intent);
                            buildNotification(intent);


                        }


                    } else {


                    }
                }
                if (EVENT.equals(DECLINE_CALL_ACTION)) {


                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {

                            stopRingTone();


                            CustomToastNegative.create(getApplicationContext(), "Call Declined!");

//                            if (intent.getExtras().getString("rejected_by").equals("provider")){
//
////                                saveHistory(userid,providerid,calltime,calltype);
//
//                            }


                            if (intent.getExtras() != null) {
                                int userID = Integer.parseInt(intent.getExtras().getString("user_id"));
                                int providerID = Integer.parseInt(intent.getExtras().getString("provider_id"));
                                String call_by = intent.getExtras().getString("call_by");
                                BigInteger duration = BigInteger.valueOf(0);


                                Log.d("lakjbJHAV", "run:  " + intent.getExtras().getString("user_id"));
//                                Log.d("lakjbJHAV", "run:  "+providerID);
//                                Log.d("lakjbJHAV", "run:  "+call_by);
//                                try {
//                                    new CallHistorySaved(userID, providerID, duration, call_by, "not_answered", getApplicationContext()).execute();
//                                } catch (Exception e) {
//                                    throw new RuntimeException(e);
//                                }

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


                        }
                    });
                }
                if (EVENT.equals(ANSWER_CALL_ACTION)) {


                    Log.d("ANSWERCALLRECEIVER", "Got Receiver notification");

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
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


                            Intent intents = new Intent(MyFirebaseMessagingService.this, MainCallActivity.class);

                            intents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intents.putExtras(intent);
                            startActivity(intents);

                            stopRingTone();


                        }
                    });
                }

                if (EVENT.equals(CALL_REJECTED_BY_USER)) {

                    stopRingTone();


                    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    notificationManager.cancel(NOT_ID);

                    Intent filter = new Intent(CALL_REJECTED_BY_USER);
                    getApplicationContext().sendBroadcast(filter);

                }

                if (EVENT.equals(FINISH_MAIN)) {

                    Intent filter = new Intent(FINISH_MAIN);
                    getApplicationContext().sendBroadcast(filter);

                }
                if (EVENT.equals(MISS_CALL_NOTIFICATION_TO_USER)) {


                    createMissCallNotificationChannel(intent);
                    buildMissCallNotification(intent);


                }


                if (EVENT.equals(REVIEW_NOTIFICATION_TO_USER)) {

                    createReviewNotificationChannel(intent);
                    buildReviewNotification(intent);


                }


                if (EVENT.equals("APP_NOTIFICATION")) {


                    String title = intent.getExtras().getString("title");
                    String message = intent.getExtras().getString("message");
                    String image = intent.getExtras().getString("image");


                    Log.d("NOHIIEBEIB", " TITLE " + title);
                    Log.d("NOHIIEBEIB", " Message " + message);
                    Log.d("NOHIIEBEIB", " Image " + image);


                    createAppNotificationChannel(intent);
                    buildAppNotification(intent, title, message, image);


                }

            }


        }

    }

    private void buildAppNotification(Intent intent, String title, String body, String image) {

        if (intent.getExtras() != null) {


            Intent intent1 = new Intent(getApplicationContext(), SplashScreen.class);
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent1, PendingIntent.FLAG_IMMUTABLE);

            NotificationCompat.Builder builder;


            if (image != null && image.contains("null")) {
                // no image received

                builder = new NotificationCompat.Builder(getApplicationContext(), "APP_NOTIFICATION")
                        .setSmallIcon(R.drawable.ic_stat_name) // Replace with your own icon
                        .setContentTitle(title)
                        .setContentText(body)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);

            } else {

                // image received
                builder = new NotificationCompat.Builder(getApplicationContext(), "APP_NOTIFICATION")
                        .setSmallIcon(R.drawable.ic_stat_name) // Replace with your own icon
                        .setContentTitle(title)
                        .setContentText(body)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .setStyle(new NotificationCompat.BigPictureStyle()
                                .bigPicture(getBitmapFromUrlSquare(image)));

            }


            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

            // NotificationId is a unique int for each notification that you must define
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            notificationManager.notify(generateRandomNotificationId(), builder.build());
        }


    }

    private void createAppNotificationChannel(Intent intent) {

        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "APP_NOTIFICATION";
            String description = "APP_NOTIFICATION";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("APP_NOTIFICATION", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void playRingTone() {


        // Get the resource identifier of the custom ringtone in the raw folder
        Uri ringtoneUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.call);

        // Create a Ringtone instance using the custom ringtone URI
        ringtone = RingtoneManager.getRingtone(getApplicationContext(), ringtoneUri);

        // Set the audio stream type to ensure the ringtone plays over the correct channel
        ringtone.setStreamType(AudioManager.STREAM_RING);

        long[] pattern = {0, 1000, 1000, 1000, 1000}; // Example: Vibrate for 1 second, pause for 1 second, repeat

        // Start playing the ringtone
        try {
            ringtone.play();
// The '-1' here means to vibrate once, as '-1' is out of bounds in the pattern array
            v.vibrate(pattern, 1);
        } catch (Exception e) {
            Log.e("IOSHSSNK", "Error playing ringtone: " + e.getMessage());
        }


    }


    private void buildNotification(Intent intent) {

        if (intent.getExtras() != null) {


            Bundle bundle = new Bundle();
            bundle.putInt("notification_id", NOT_ID);
            bundle.putBoolean("finish", true);


            Context context = getApplicationContext();
            // Intent for answering the call
            Intent answerIntent = new Intent(context, AnswerCallReceiver.class);
            answerIntent.putExtras(intent);
            answerIntent.setAction(ANSWER_CALL_ACTION);
            PendingIntent answerPendingIntent = PendingIntent.getBroadcast(context, 0, answerIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
            // Intent for answering the call


            Intent declineIntent = new Intent(context, RejectCallReceiver.class);

            declineIntent.putExtras(intent);
            declineIntent.putExtras(bundle);

            declineIntent.setAction(DECLINE_CALL_ACTION);


            PendingIntent declinePendingIntent = PendingIntent.getBroadcast(context, 0, declineIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);


            RemoteViews collapseView = new RemoteViews(context.getPackageName(), R.layout.custom_call_notification_layout_big);
            RemoteViews collapseViewSmall = new RemoteViews(context.getPackageName(), R.layout.custom_call_notification_layout_small);

            collapseView.setTextViewText(R.id.username, intent.getExtras().getString("title"));
            collapseView.setTextViewText(R.id.serviceTitle, intent.getExtras().getString("body"));

            collapseViewSmall.setTextViewText(R.id.username, intent.getExtras().getString("title"));
            collapseViewSmall.setTextViewText(R.id.serviceTitle, intent.getExtras().getString("body"));


            Bitmap DP = getBitmapFromUrl(intent.getExtras().getString("user_dp"));

            collapseView.setImageViewBitmap(R.id.userImg, DP);
            collapseViewSmall.setImageViewBitmap(R.id.userImg, DP);
            // Intent to open SplashScreen.class when notification is clicked
            Intent mainCallScreenIntent = new Intent(context, CallingScreen.class);
            mainCallScreenIntent.putExtras(bundle);
            mainCallScreenIntent.putExtras(intent);


            PendingIntent pendingIntentOPENMAIN = PendingIntent.getActivity(context, 0, mainCallScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);


            collapseView.setOnClickPendingIntent(R.id.acceptCallBtb, pendingIntentOPENMAIN);
            collapseView.setOnClickPendingIntent(R.id.declineCallBtn, declinePendingIntent);

            collapseViewSmall.setOnClickPendingIntent(R.id.acceptCallBtb, pendingIntentOPENMAIN);
            collapseViewSmall.setOnClickPendingIntent(R.id.declineCallBtn, declinePendingIntent);


            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, mainCallScreenIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);


            // Create a notification builder
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);
            builder.setSmallIcon(R.drawable.ic_stat_name);
            builder.setLargeIcon(DP);
            builder.setPriority(NotificationCompat.PRIORITY_MAX);
            builder.setCategory(NotificationCompat.CATEGORY_CALL);
            builder.setFullScreenIntent(pendingIntent, true);
            builder.setContentTitle("Incoming Call");// Custom vibration pattern
            builder.setContentText(intent.getExtras().getString("user_name") + " Is Calling You!"); // Custom vibration pattern
            builder.setCustomBigContentView(collapseView);
            builder.setCustomContentView(collapseViewSmall);

//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
//
//            } else {
//
//            }


            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(NOT_ID, builder.build());

        }

    }

    private void buildMissCallNotification(Intent intent) {

        if (intent.getExtras() != null) {


            Bundle bundle = intent.getExtras();

            String missCallBy = bundle.getString("miss_call_name", "Expert Here User");
            String timestamp = bundle.getString("timestamp", "0");

            Context context = getApplicationContext();


            Intent intents = null;

            if (userPref.contains("id")) {
                // i am user

                intents = new Intent(context, HomeActivity.class);


            } else if (providerPref.contains("id")) {
                // i am provider

                intents = new Intent(context, ProviderHome.class);

            }

            Bitmap DP = getBitmapFromUrl(intent.getExtras().getString("user_icon"));


            Bundle miscallbundle = new Bundle();

            miscallbundle.putBoolean("incomming_call", true);
            intents.putExtras(miscallbundle);
            intents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intents, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);


            // Create a notification builder
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);
            builder.setSmallIcon(R.drawable.ic_stat_name);
            builder.setPriority(NotificationCompat.PRIORITY_HIGH);
            builder.setCategory(NotificationCompat.CATEGORY_CALL);
            builder.setFullScreenIntent(pendingIntent, true);
            builder.setLargeIcon(DP);
            builder.setContentTitle("Miss Call From " + missCallBy);// Custom vibration pattern
            builder.setContentText(DateTimeUtils.formatTimestamp(context, Long.parseLong(timestamp)) + " :Tap To View Miss Calls!"); // Custom vibration pattern
            builder.setAutoCancel(true);


            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(generateRandomNotificationId(), builder.build());

        }

    }

    private void buildReviewNotification(Intent intent) {

        if (intent.getExtras() != null) {


            Bundle bundle = intent.getExtras();

            String reviewerName = bundle.getString("reviewer_name", "Expert Here User");
            String timestamp = bundle.getString("timestamp", "0");

            String stars = bundle.getString("stars", "0");
            String review = bundle.getString("review", "0");


            String Added = "Added";

            if (bundle.getString("updated", "0").equals("true")) {
                Added = "Updated";
            }

            Context context = getApplicationContext();


            Intent intents = new Intent(context, ProviderHome.class);

            Bundle bundle1 = new Bundle();
            bundle1.putBoolean("review", true);
            intents.putExtras(bundle1);
            Bitmap DP = getBitmapFromUrl(intent.getExtras().getString("user_icon"));


            Log.d("NOHIIEBEIB", "handleIntent: " + reviewerName + "\n" + timestamp + "\n" + stars + "\n" + review + "\n" + Added + "\n" + intent.getExtras().getString("user_icon"));


            intents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intents, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);


            // Create a notification builder
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "REVIEW_NOTIFICATIONS");
            builder.setSmallIcon(R.drawable.ic_stat_name);
            builder.setPriority(NotificationCompat.PRIORITY_HIGH);
            builder.setCategory(NotificationCompat.CATEGORY_EVENT);
            builder.setFullScreenIntent(pendingIntent, true);
            builder.setLargeIcon(DP);
            builder.setContentTitle(reviewerName + " : " + Added + " A " + stars + " Star Review To Your Services!");// Custom vibration pattern
            builder.setContentText(
                    "Review - '' " +

                            review

                            + " ''" + "\n" + DateTimeUtils.formatTimestamp(context, Long.parseLong(timestamp))); // Custom vibration pattern

            builder.setAutoCancel(true);


            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(generateRandomNotificationId(), builder.build());

        }

    }

    private static int generateRandomNotificationId() {
        Random random = new Random();
        return random.nextInt(1000); // Generate a random integer between 0 and 999
    }

    private Bitmap getBitmapFromUrl(String url) {
        try {
            return Glide.with(this)
                    .asBitmap()
                    .load(url)
                    .circleCrop()
                    .submit()
                    .get(); // This is a blocking call, consider using callbacks for better performance
        } catch (Exception e) {

            Log.d("lkankabka", "getBitmapFromUrl: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private Bitmap getBitmapFromUrlSquare(String url) {
        try {
            return Glide.with(this)
                    .asBitmap()
                    .load(url)
                    .submit()
                    .get(); // This is a blocking call, consider using callbacks for better performance
        } catch (Exception e) {

            Log.d("lkankabka", "getBitmapFromUrl: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }


    private void createNotificationChannel(Intent intent) {
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

    private void createMissCallNotificationChannel(Intent intent) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "MISS_CALL",
                    "MISS_CALL",
                    NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private void createReviewNotificationChannel(Intent intent) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "REVIEW_NOTIFICATIONS",
                    "REVIEW_NOTIFICATIONS",
                    NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
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


//    private class CallHistorySaved extends AsyncTask<Void, Void, SuccessMessageResponse> {
//
//        int userId, providerId;
//        BigInteger duration;
//        String callBy, received;
//        Context context;
//
//        public CallHistorySaved(int userId, int providerId, BigInteger duration, String callBy, String received, Context context) {
//            this.userId = userId;
//            this.providerId = providerId;
//            this.duration = duration;
//            this.callBy = callBy;
//            this.received = received;
//            this.context = context;
//        }
//
//        @Override
//        protected void onPreExecute() {
//
//
//            super.onPreExecute();
//
//        }
//
//        @Override
//        protected SuccessMessageResponse doInBackground(Void... voids) {
//
//            Log.d("JHVWHVJJJWDW", "Do in BG runing");
//
//
//            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
////            Call<ApiResponse> call = apiInterface.getServiceUserWithSearchAndFilter(latitude, longitude, page, itemsPerPage, radius, 5, searchQuery);
//            BigInteger time = BigInteger.valueOf(System.currentTimeMillis());
//
//            retrofit2.Call<SuccessMessageResponse> call = apiInterface.saveCallHistory(userId, providerId, duration, callBy, received, time);
//
//
//            try {
//                retrofit2.Response<SuccessMessageResponse> response = call.execute();
//                if (response.isSuccessful() && response.body() != null) {
//
//
//                    return response.body();
//
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//                Log.d("APISEARCHING", "Error " + e.getMessage());
//
//
//            }
//
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(SuccessMessageResponse serviceResponse) {
//            super.onPostExecute(serviceResponse);
//
//            if (serviceResponse != null) {
//                if (serviceResponse.isSuccess()) {
//
//                    if (serviceResponse.getMessage().equals("Call history saved successfully.")) {
//
//
//                        if (isServiceRunning(OutGoingCallService.class)) {
//
//                            Intent intent = new Intent(getApplicationContext(), OutGoingCallService.class);
//                            Intent intent2 = new Intent(getApplicationContext(), DialToneGeneratorService.class);
//                            stopService(intent);
//                            stopService(intent2);
//
//                            Intent broadcastIntent = new Intent(getApplicationContext(), OutGoingNotificationActionReceiver.class);
//                            broadcastIntent.setAction(ACTION_STOP);
//                            sendBroadcast(broadcastIntent);
//
//
//                        }
//
//                    } else {
//
//
//                        new Handler(Looper.getMainLooper()).post(new Runnable() {
//                            @Override
//                            public void run() {
//
//                                Toast.makeText(context, "Error " + serviceResponse.getMessage(), Toast.LENGTH_SHORT).show();
//
//
//                            }
//                        });
//                    }
//
//
//                } else {
//                    new Handler(Looper.getMainLooper()).post(new Runnable() {
//                        @Override
//                        public void run() {
//
//                            Toast.makeText(context, " " + serviceResponse.getMessage(), Toast.LENGTH_SHORT).show();
//
//
//                        }
//                    });
//                }
//            } else {
//                // Handle the case when there is no responsesw
//            }
//        }
//
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopRingTone();

        unregisterReceiver(stopCallTone);


    }

    private void stopRingTone() {


        if (ringtone != null && ringtone.isPlaying()) {
            ringtone.stop();
        }

        if (v != null) {

            v.cancel();

        }
    }


    private final BroadcastReceiver stopCallTone = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction() != null && intent.getAction().equals(STOPS_RING)) {


                stopRingTone();
            }

        }
    };


}
