package www.experthere.in.FCM;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import www.experthere.in.R;
import www.experthere.in.api.ApiClient;
import www.experthere.in.api.ApiInterface;
import www.experthere.in.calling.MainCallActivity;
import www.experthere.in.helper.CustomToastNegative;
import www.experthere.in.helper.CustomToastPositive;
import www.experthere.in.model.FCMData;
import www.experthere.in.model.TokenResponse;

public class AnswerCallReceiver extends BroadcastReceiver {
    public static String ANSWER_CALL_ACTION = "www.experthere.in.calling.CALL_RECEIVED";
    String STOPS_RING = "www.experthere.in.calling.STOP_RING";


    Bundle sendableBUndle = new Bundle();

    @Override
    public void onReceive(Context context, Intent intent) {


        if (intent.getAction() != null) {


            if (intent.getAction().equals(ANSWER_CALL_ACTION)) {

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {



                        CustomToastPositive.create(context,"Call Received!");


                    }
                });


                stopRing(context);

                if (intent.getExtras() != null) {

                    try {


                        if (intent.getExtras().getString("call_by").equals("user")) {
                            Log.d("ANSWERCALLRECEIVER", "Fetch Token User");

                            new FetchTokenUser(intent.getExtras().getString("user_id"), context, intent).execute();

                        } else {
                            Log.d("ANSWERCALLRECEIVER", "Fetch Token Provider");

                            new FetchTokenProvider(intent.getExtras().getString("provider_id"), context, intent).execute();


                        }


                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                }
            }

        }

    }

    private void stopRing(Context context) {

        Intent intent = new Intent(STOPS_RING);
        context.sendBroadcast(intent);


    }

    private class FetchTokenUser extends AsyncTask<Void, Void, TokenResponse> {


        String userId;
        Context context;
        Intent intent;

        public FetchTokenUser(String userId, Context context, Intent intent) {
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

                    startSendingFcmNotificationUser(fcmData, context, intent);

                } else {

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {

                            CustomToastNegative.create(context,"Data Not Found! ");


                        }
                    });


                }


            } else {


            }


        }


    }

    private class FetchTokenProvider extends AsyncTask<Void, Void, TokenResponse> {


        String provider_id;
        Context context;
        Intent intent;

        public FetchTokenProvider(String provider_id, Context context, Intent intent) {
            this.provider_id = provider_id;
            this.context = context;
            this.intent = intent;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected TokenResponse doInBackground(Void... voids) {



            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
//            Call<ApiResponse> call = apiInterface.getServiceUserWithSearchAndFilter(latitude, longitude, page, itemsPerPage, radius, 5, searchQuery);

            retrofit2.Call<TokenResponse> call = apiInterface.getFcmTokenDetailsProvider(provider_id);


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

                    Log.d("KSKNQBSJVSQJ", "Response sucess for provider  " + fcmData.getUser_id() + fcmData.getProvider_id());

                    startSendingFcmNotificationProvider(fcmData, context, intent);

                } else {

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            CustomToastNegative.create(context,"Data Not Found! ");

                        }
                    });


                }


            } else {


            }


        }


    }

    private void startSendingFcmNotificationProvider(FCMData fcmData, Context context, Intent intent) {


        FcmBearerTokenGenerator tokenGenerator = new FcmBearerTokenGenerator(context);
        tokenGenerator.generateAccessTokenAsync(new FcmBearerTokenGenerator.TokenGenerationListener() {
            @Override
            public void onTokenGenerated(String token) {

                Log.d("sknkjsnksn1", " USER ID FCM : " + fcmData.getUser_id());
                Log.d("sknkjsnksn1", "TOKEN USER : " + fcmData.getFcm_token());

                if (intent.getExtras() != null) {


                    Map<String, String> EXTRA_DATA = new HashMap<>();

                    Log.d("sknkjsnksn1", "user_name : " + intent.getExtras().getString("user_name"));
                    Log.d("sknkjsnksn1", "user_id : " + intent.getExtras().getString("user_id"));
                    Log.d("sknkjsnksn1", "user_dp : " + intent.getExtras().getString("user_dp"));
                    Log.d("sknkjsnksn1", "provider_id : " + intent.getExtras().getString("provider_id"));
                    Log.d("sknkjsnksn1", "call_by : " + intent.getExtras().getString("call_by"));

                    EXTRA_DATA.put("user_name", intent.getExtras().getString("user_name"));
                    EXTRA_DATA.put("user_id", intent.getExtras().getString("user_id"));
                    EXTRA_DATA.put("user_dp", intent.getExtras().getString("user_dp"));
                    EXTRA_DATA.put("provider_id", intent.getExtras().getString("provider_id"));
                    EXTRA_DATA.put("call_by", intent.getExtras().getString("call_by"));

                    EXTRA_DATA.put("agora_token", intent.getExtras().getString("agora_token"));
                    SharedPreferences preferences = context.getSharedPreferences("provider", Context.MODE_PRIVATE);
                    if (preferences.contains("id")) {
                        EXTRA_DATA.put("provider_name", preferences.getString("name", "Expert Here Provider"));
                        EXTRA_DATA.put("provider_dp", preferences.getString("logo_image", "0"));

                        sendableBUndle.putString("provider_name", preferences.getString("name", "Expert Here Provider"));
                        sendableBUndle.putString("provider_dp", preferences.getString("logo_image", "0"));

                    }

                    SharedPreferences preferencesUser = context.getSharedPreferences("user", Context.MODE_PRIVATE);
                    if (preferencesUser.contains("id")) {
                        EXTRA_DATA.put("display_name", preferencesUser.getString("name", "Expert Here Provider"));
                        EXTRA_DATA.put("dp", preferencesUser.getString("dp", "0"));

                        sendableBUndle.putString("display_name", preferencesUser.getString("name", "Expert Here Provider"));
                        sendableBUndle.putString("dp", preferencesUser.getString("dp", "0"));

                    }
//

                    EXTRA_DATA.put("event", ANSWER_CALL_ACTION);
                    EXTRA_DATA.put("answered_by", "user");
                    EXTRA_DATA.put("timestamp", String.valueOf(System.currentTimeMillis()));
                    //


                    sendableBUndle.putString("user_name", intent.getExtras().getString("user_name"));
                    sendableBUndle.putString("user_id", intent.getExtras().getString("user_id"));
                    sendableBUndle.putString("user_dp", intent.getExtras().getString("user_dp"));
                    sendableBUndle.putString("provider_id", intent.getExtras().getString("provider_id"));
                    sendableBUndle.putString("call_by", "provider");
                    sendableBUndle.putString("agora_token", intent.getExtras().getString("agora_token"));
                    sendableBUndle.putString("event", ANSWER_CALL_ACTION);
                    sendableBUndle.putString("answered_by", "user");
                    sendableBUndle.putString("timestamp", String.valueOf(System.currentTimeMillis()));



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


                                                Intent filter = new Intent("www.experthere.in.calling.ACTION_FINISH_ACTIVITY");
                                                context.sendBroadcast(filter);


                                                Intent intent1 = new Intent(context, MainCallActivity.class);
                                                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                intent1.putExtras(intent.getExtras());
//                                                intent1.putExtras(sendableBUndle);

                                                context.startActivity(intent1);


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

    private void startSendingFcmNotificationUser(FCMData fcmData, Context context, Intent intent) {


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


                    SharedPreferences preferences = context.getSharedPreferences("provider", Context.MODE_PRIVATE);

                    if (preferences.contains("id")) {
                        EXTRA_DATA.put("provider_name", preferences.getString("name", "Expert Here Provider"));
                        EXTRA_DATA.put("provider_dp", preferences.getString("logo_image", "0"));

                        sendableBUndle.putString("provider_name", preferences.getString("name", "Expert Here Provider"));
                        sendableBUndle.putString("provider_dp", preferences.getString("logo_image", "0"));
                    }


                    SharedPreferences preferencesUser = context.getSharedPreferences("user", Context.MODE_PRIVATE);
                    if (preferencesUser.contains("id")) {
                        EXTRA_DATA.put("display_name", preferencesUser.getString("name", "Expert Here Provider"));
                        EXTRA_DATA.put("dp", preferencesUser.getString("dp", "0"));

                        sendableBUndle.putString("display_name", preferencesUser.getString("name", "Expert Here Provider"));
                        sendableBUndle.putString("dp", preferencesUser.getString("dp", "0"));

                    }
//


                    EXTRA_DATA.put("call_by", intent.getExtras().getString("call_by"));
                    EXTRA_DATA.put("agora_token", intent.getExtras().getString("agora_token"));
//
//

                    EXTRA_DATA.put("event", ANSWER_CALL_ACTION);
                    EXTRA_DATA.put("answered_by", "provider");

                    EXTRA_DATA.put("timestamp", String.valueOf(System.currentTimeMillis()));


                    //

                    sendableBUndle.putString("user_name", intent.getExtras().getString("user_name"));
                    sendableBUndle.putString("user_id", intent.getExtras().getString("user_id"));
                    sendableBUndle.putString("user_dp", intent.getExtras().getString("user_dp"));
                    sendableBUndle.putString("provider_id", intent.getExtras().getString("provider_id"));
                    sendableBUndle.putString("call_by", "user");
                    sendableBUndle.putString("agora_token", intent.getExtras().getString("agora_token"));
                    sendableBUndle.putString("event", ANSWER_CALL_ACTION);
                    sendableBUndle.putString("answered_by", "provider");
                    sendableBUndle.putString("timestamp", String.valueOf(System.currentTimeMillis()));






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


                                        Log.d("ANSWERCALLRECEIVER", "onResponse: success sent notification");

                                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                                            @Override
                                            public void run() {


                                                Intent filter = new Intent("www.experthere.in.calling.ACTION_FINISH_ACTIVITY");
                                                context.sendBroadcast(filter);

                                                Intent intent1 = new Intent(context, MainCallActivity.class);
                                                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                                intent1.putExtras(intent.getExtras());
                                                intent1.putExtras(sendableBUndle);
                                                context.startActivity(intent1);


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

}
