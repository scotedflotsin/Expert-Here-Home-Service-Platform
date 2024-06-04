package www.experthere.in.FCM;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import www.experthere.in.R;
import www.experthere.in.api.ApiClient;
import www.experthere.in.api.ApiInterface;
import www.experthere.in.helper.CustomToastNegative;
import www.experthere.in.model.FCMData;
import www.experthere.in.model.TokenResponse;

public class RejectCallReceiver extends BroadcastReceiver {
    public static final String DECLINE_CALL_ACTION = "www.experthere.in.calling.CALL_DECLINE";
    String STOPS_RING = "www.experthere.in.calling.STOP_RING";



    @Override
    public void onReceive(Context context, Intent intent) {



        if (intent.getAction() != null) {


            if (intent.getAction().equals(DECLINE_CALL_ACTION)) {

                stopRing(context);



                if (intent.getExtras() != null) {

                    Log.d("sknkjsnksn", "intent not null ");

                    try {

                        Log.d("sknkjsnksn", "TRYING USER ID " + intent.getExtras().getString("user_id"));

                        if(intent.getExtras().getString("call_by").equals("user")){

                            new FetchProviderToken(intent.getExtras().getString("user_id"), context, intent).execute();
                            Log.d("lkaNKJB", "CALL BY USER " + intent.getExtras().getString("user_id"));


                        }else {

                            new FetchUserToken(intent.getExtras().getString("provider_id"), context, intent).execute();

                            Log.d("lkaNKJB", "CALL BY PROVIDER " + intent.getExtras().getString("provider_id"));

                        }


                        int userID = Integer.parseInt(intent.getExtras().getString("user_id"));
                        int providerID = Integer.parseInt(intent.getExtras().getString("provider_id"));
                        String call_by = intent.getExtras().getString("call_by");
                        BigInteger duration = BigInteger.valueOf(0);



                        Bundle bundle = intent.getExtras();

                        if (bundle != null) {

                            if (bundle.getBoolean("finish")) {

                                int NOTIFICATION_ID = bundle.getInt("notification_id");

                                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                                notificationManager.cancel(NOTIFICATION_ID);


                            }

                        }


                        Intent finishIntent = new Intent("www.experthere.in.calling.ACTION_FINISH_ACTIVITY");
                        context.sendBroadcast(finishIntent);


                    } catch (Exception e) {

                        throw new RuntimeException(e);
                    }

                }


            }

        }


    }


    private class FetchProviderToken extends AsyncTask<Void, Void, TokenResponse> {


        String userId;
        Context context;
        Intent intent;

        public FetchProviderToken(String userId, Context context, Intent intent) {
            this.userId = userId;
            this.context = context;
            this.intent = intent;
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
                Log.d("sknkjsnksn", "RES Not Null ");
                Log.d("sknkjsnksn", "MESSAGE " + serviceResponse.getMessage());


                if (serviceResponse.isSuccess() && serviceResponse.getMessage().equals("User data retrieved successfully")) {

                    FCMData fcmData = serviceResponse.getData();

                    startSendingFcmNotificationToProvider(fcmData, context, intent);

                } else {

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {

                            CustomToastNegative.create(context,"Data Not Found!");


                        }
                    });


                }


            } else {


            }


        }


    }
    private class FetchUserToken extends AsyncTask<Void, Void, TokenResponse> {


        String providerId;
        Context context;
        Intent intent;

        public FetchUserToken(String providerId, Context context, Intent intent) {
            this.providerId = providerId;
            this.context = context;
            this.intent = intent;
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

            retrofit2.Call<TokenResponse> call = apiInterface.getFcmTokenDetailsProvider(providerId);


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
                Log.d("sknkjsnksn", "RES Not Null ");
                Log.d("sknkjsnksn", "MESSAGE " + serviceResponse.getMessage());


                if (serviceResponse.isSuccess() && serviceResponse.getMessage().equals("Provider data retrieved successfully")) {

                    FCMData fcmData = serviceResponse.getData();

                    startSendingFcmNotificationToUser(fcmData, context, intent);

                } else {

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            CustomToastNegative.create(context,"Data Not Found!");

                        }
                    });


                }


            } else {


            }


        }


    }

    private void startSendingFcmNotificationToUser(FCMData fcmData, Context context, Intent intent) {


        FcmBearerTokenGenerator tokenGenerator = new FcmBearerTokenGenerator(context);
        tokenGenerator.generateAccessTokenAsync(new FcmBearerTokenGenerator.TokenGenerationListener() {
            @Override
            public void onTokenGenerated(String token) {

                Log.d("sknkjsnksn", " USER ID FCM : " + fcmData.getUser_id());
                Log.d("sknkjsnksn", "TOKEN USER : " + fcmData.getFcm_token());

                if (intent.getExtras() != null) {


                    Map<String, String> EXTRA_DATA = new HashMap<>();

                    Log.d("sknkjsnksn", "user_name : " + intent.getExtras().getString("user_name"));
                    Log.d("sknkjsnksn", "user_id : " + intent.getExtras().getString("user_id"));
                    Log.d("sknkjsnksn", "user_dp : " + intent.getExtras().getString("user_dp"));
                    Log.d("sknkjsnksn", "provider_id : " + intent.getExtras().getString("provider_id"));
                    Log.d("sknkjsnksn", "call_by : " + intent.getExtras().getString("call_by"));

                    EXTRA_DATA.put("user_name", intent.getExtras().getString("user_name"));
                    EXTRA_DATA.put("user_id", intent.getExtras().getString("user_id"));
                    EXTRA_DATA.put("user_dp", intent.getExtras().getString("user_dp"));
                    EXTRA_DATA.put("provider_id", intent.getExtras().getString("provider_id"));
                    EXTRA_DATA.put("call_by", intent.getExtras().getString("call_by"));

                    EXTRA_DATA.put("timestamp", String.valueOf(System.currentTimeMillis()));


                    EXTRA_DATA.put("event", DECLINE_CALL_ACTION);
                    EXTRA_DATA.put("rejected_by", "user");
                    //


                    FcmApiClient.sendFcmMessageToDevice(context.getString(R.string.FcmSendUrl), token, fcmData.getFcm_token(), "Incoming Call",
                            "Call From : " + intent.getExtras().getString("user_name"), EXTRA_DATA, "ACTION", new Callback() {
                                @Override
                                public void onFailure(@NonNull Call call, @NonNull IOException e) {


                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            CustomToastNegative.create(context,"Fail: "+e.getMessage());

                                        }
                                    });


                                }

                                @Override
                                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                                    if (response.isSuccessful()) {


                                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                                            @Override
                                            public void run() {

                                                CustomToastNegative.create(context,"Call Declined!");

                                            }
                                        });

                                    }

                                }
                            });

                }


            }

            @Override
            public void onTokenGenerationFailed(Exception e) {

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        CustomToastNegative.create(context,"Error: "+e.getMessage());

                    }
                });

            }
        });


    }
    private void startSendingFcmNotificationToProvider(FCMData fcmData, Context context, Intent intent) {


        FcmBearerTokenGenerator tokenGenerator = new FcmBearerTokenGenerator(context);
        tokenGenerator.generateAccessTokenAsync(new FcmBearerTokenGenerator.TokenGenerationListener() {
            @Override
            public void onTokenGenerated(String token) {

                Log.d("sknkjsnksn", " USER ID FCM : " + fcmData.getUser_id());
                Log.d("sknkjsnksn", "TOKEN USER : " + fcmData.getFcm_token());

                if (intent.getExtras() != null) {


                    Map<String, String> EXTRA_DATA = new HashMap<>();

                    Log.d("sknkjsnksn", "user_name : " + intent.getExtras().getString("user_name"));
                    Log.d("sknkjsnksn", "user_id : " + intent.getExtras().getString("user_id"));
                    Log.d("sknkjsnksn", "user_dp : " + intent.getExtras().getString("user_dp"));
                    Log.d("sknkjsnksn", "provider_id : " + intent.getExtras().getString("provider_id"));
                    Log.d("sknkjsnksn", "call_by : " + intent.getExtras().getString("call_by"));

                    EXTRA_DATA.put("user_name", intent.getExtras().getString("user_name"));
                    EXTRA_DATA.put("user_id", intent.getExtras().getString("user_id"));
                    EXTRA_DATA.put("user_dp", intent.getExtras().getString("user_dp"));
                    EXTRA_DATA.put("provider_id", intent.getExtras().getString("provider_id"));
                    EXTRA_DATA.put("call_by", intent.getExtras().getString("call_by"));

                    EXTRA_DATA.put("timestamp", String.valueOf(System.currentTimeMillis()));


                    EXTRA_DATA.put("event", DECLINE_CALL_ACTION);
                    EXTRA_DATA.put("rejected_by", "provider");
                    //


                    FcmApiClient.sendFcmMessageToDevice(context.getString(R.string.FcmSendUrl), token, fcmData.getFcm_token(), "Incoming Call",
                            "Call From : " + intent.getExtras().getString("user_name"), EXTRA_DATA, "ACTION", new Callback() {
                                @Override
                                public void onFailure(@NonNull Call call, @NonNull IOException e) {


                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            CustomToastNegative.create(context,"Fail: "+e.getMessage());

                                        }
                                    });


                                }

                                @Override
                                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                                }
                            });

                }


            }

            @Override
            public void onTokenGenerationFailed(Exception e) {

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        CustomToastNegative.create(context,"Error: "+e.getMessage());

                    }
                });

            }
        });


    }

    private void stopRing(Context context) {

        Intent intent = new Intent(STOPS_RING);
        context.sendBroadcast(intent);



    }



}
