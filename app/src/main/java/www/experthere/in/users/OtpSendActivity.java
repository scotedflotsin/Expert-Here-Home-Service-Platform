package www.experthere.in.users;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;


import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import www.experthere.in.FCM.FcmBearerTokenGenerator;
import www.experthere.in.LocationSelectActivity;
import www.experthere.in.R;
import www.experthere.in.api.ApiClient;
import www.experthere.in.api.ApiInterface;
import www.experthere.in.helper.CustomToastNegative;
import www.experthere.in.helper.CustomToastPositive;
import www.experthere.in.helper.EmailMaskUtil;
import www.experthere.in.helper.ExitBottomSheet;
import www.experthere.in.model.LoginResponse;
import www.experthere.in.model.SuccessMessageResponse;

public class OtpSendActivity extends AppCompatActivity implements OnSubmitListener {

    String name, profession, phone, email, password;
    long duration = 90000;
    long interval = 1000;
    TextView otpTimerTxt, resendTxt, emailForOtp;

    private CountDownTimer countDownTimer;

    String sentOtp;


    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    String fcmDeviceToken="0";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.white, this.getTheme()));

        setContentView(R.layout.activity_otp_send);

        //fcm with auth
        sharedPreferences = getSharedPreferences("fcm", MODE_PRIVATE);
        editor = sharedPreferences.edit();
         fcmDeviceToken = sharedPreferences.getString("device_token","0");


        emailForOtp = findViewById(R.id.emailForOtp);
        otpTimerTxt = findViewById(R.id.otpTimerTxt);
        resendTxt = findViewById(R.id.resendTxt);
        TextInputEditText otpEt = findViewById(R.id.otpEt);

        resendTxt.setEnabled(false);
        resendTxt.setClickable(false);


        TextView textView = findViewById(R.id.btnTxt);
        textView.setText("Complete verification");


        getDataFromIntentBundle();

        if (!email.isEmpty()) {

            EmailMaskUtil.maskEmailAndSetToTextView(email, emailForOtp);

            sendOtp(email);
            startCountdownTimer(duration, interval);

        }

        resendTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendOtp(email);
                startCountdownTimer(duration, interval);
                otpTimerTxt.setVisibility(View.VISIBLE);
                resendTxt.setEnabled(false);
                resendTxt.setClickable(false);
                resendTxt.setText("Resend in ");

            }
        });

        findViewById(R.id.changeEmailTxt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                // Show the BottomSheetFragment when needed
                BottomSheetFragment bottomSheetFragment = BottomSheetFragment.newInstance();
                bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());

            }
        });

        findViewById(R.id.progressBtnLay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!otpEt.getText().toString().isEmpty()) {
                    if (sentOtp.matches(otpEt.getText().toString().trim())) {


                        registerUser();


                    } else {

                        CustomToastNegative.create(getApplicationContext(),"Wrong OTP");

                    }
                } else {
                    otpEt.setError("Enter Otp");
                }
            }
        });

    }

    private void registerUser() {

        findViewById(R.id.btnProgressBar).setVisibility(View.VISIBLE);
        new RegisterUserTask(OtpSendActivity.this).execute();

    }

    private void sendOtp(String mail) {

        String OTP = OTPGenerator.generateOTP();
        sentOtp = OTP;
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);


        SendOtpTask sendOtpTask = new SendOtpTask(apiInterface, new SendOtpTask.SendOtpListener() {
            @Override
            public void onSendOtpSuccess(String res) {
                // Handle success in the UI thread
                runOnUiThread(() -> {
                    // Update UI or perform any success-related tasks
                    CustomToastPositive.create(getApplicationContext(),"Code Sent Successfully!");
                });
            }

            @Override
            public void onSendOtpFailure(String errorMessage) {
                // Handle failure in the UI thread
                runOnUiThread(() -> {
                    // Update UI or perform any failure-related tasks
                    System.out.println(errorMessage);
                    CustomToastNegative.create(getApplicationContext(),"Error: "+errorMessage);
                });
            }
        });

// Execute the task with parameters
        sendOtpTask.execute(mail, name, OTP, "OTP Verification : Expert Here");


    }

    private void getDataFromIntentBundle() {

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            name = bundle.getString("name");
            profession = bundle.getString("profession");
            phone = bundle.getString("phone");
            email = bundle.getString("email");
            password = bundle.getString("pass");


        }
    }


    // Declare a global variable to hold the reference to the CountDownTimer

    private void startCountdownTimer(long duration, long interval) {
        // Cancel the existing timer if it's running
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        // Create a new countdown timer
        countDownTimer = new CountDownTimer(duration, interval) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Calculate minutes and seconds
                long minutes = (millisUntilFinished / 1000) / 60;
                long seconds = (millisUntilFinished / 1000) % 60;

                // Update the EditText with the remaining time in "minute:seconds" format
                otpTimerTxt.setText(String.format("%02d:%02d", minutes, seconds));
            }

            @Override
            public void onFinish() {
                // Do work when the timer finishes

                // Optionally, reset the EditText to an initial state
                otpTimerTxt.setText("");
                otpTimerTxt.setVisibility(View.GONE);
                resendTxt.setText("Resend Now!");
                resendTxt.setEnabled(true);
                resendTxt.setClickable(true);
            }
        };

        // Start the new countdown timer
        countDownTimer.start();
    }


    @Override
    public void onSubmit(String emails) {


        email = emails;
        sendOtp(email);


        EmailMaskUtil.maskEmailAndSetToTextView(email, emailForOtp);
        otpTimerTxt.setText("");
        resendTxt.setText("Resend in ");

        otpTimerTxt.setVisibility(View.VISIBLE);
        resendTxt.setEnabled(false);
        resendTxt.setClickable(false);
        startCountdownTimer(duration, interval);


    }


    // AsyncTask to handle the API call in the background
    private class RegisterUserTask extends AsyncTask<Void, Void, Void> {

        Activity activity;

        public RegisterUserTask(Activity activity) {
            this.activity = activity;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            // Perform background operation, e.g., user

            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);


            apiInterface.registerUser(   // Pass the context
                    name,
                    profession,
                    phone,
                    email.toLowerCase(),
                    password,
                    null,
                    0,  // Latitude
                    0,  // Longitude
                    "0",
                    false).enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {


                    if (response.isSuccessful()) {

                        LoginResponse apiResponse = response.body();

                        if (apiResponse.isError() && apiResponse.getMessage().matches("User with this email already exists.")) {



                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    CustomToastNegative.create(getApplicationContext(),"Email Already Registered Login now!");

                                    Bundle bundle = new Bundle();
                                    bundle.putString("email", email);
                                    activity.startActivity(new Intent(OtpSendActivity.this, LoginActivity.class).putExtras(bundle));
                                    activity.finish();

                                }
                            });


                        }

                        if (apiResponse != null && !apiResponse.isError()) {

                            Log.d("OTPCHECK", "error " + apiResponse.isError());
                            Log.d("OTPCHECK", "Message " + apiResponse.getMessage());


                            signInAnonymouslyUser(apiResponse, activity);


                        }else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    findViewById(R.id.btnProgressBar).setVisibility(View.GONE);

                                }
                            });
                        }


                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                findViewById(R.id.btnProgressBar).setVisibility(View.GONE);


                            }
                        });
                    }


                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {

                    findViewById(R.id.btnProgressBar).setVisibility(View.GONE);
                    CustomToastNegative.create(getApplicationContext(),"Error: "+t.getMessage());
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

    private void signInAnonymouslyUser(LoginResponse apiResponse, Activity activity) {

                            // User is already signed in, handle accordingly
                            // For anonymous auth, you might want to upgrade the user's auth method


                            FcmBearerTokenGenerator tokenGenerator = new FcmBearerTokenGenerator(this);
                            tokenGenerator.generateAccessTokenAsync(new FcmBearerTokenGenerator.TokenGenerationListener() {
                                @Override
                                public void onTokenGenerated(final String token) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            // Use the generated token for FCM requests

                                            Log.d("MESS", "token- " + token);
                                            editor.putString("TOKEN", token);
                                            editor.apply();


                                            try {
                                                new FcmServiceTaskUser(apiResponse.getUser().getId(),
                                                        "0", "user",
                                                        fcmDeviceToken, apiResponse).execute();


                                            } catch (Exception e) {
                                                throw new RuntimeException(e);
                                            }


                                        }
                                    });
                                }

                                @Override
                                public void onTokenGenerationFailed(final Exception e) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            // Handle token generation failure
                                            CustomToastNegative.create(getApplicationContext(),"Token Generation Fail");
                                        }
                                    });
                                }
                            });


    }


    private class FcmServiceTaskUser extends AsyncTask<Void, Void, SuccessMessageResponse> {
        String userIDForFcm, providerIdForFcm, typeFcm, fcmTOKEN;
        LoginResponse apiResponse;

        public FcmServiceTaskUser(String userIDForFcm, String providerIdForFcm, String typeFcm, String fcmTOKEN, LoginResponse loginResponse) {
            this.userIDForFcm = userIDForFcm;
            this.providerIdForFcm = providerIdForFcm;
            this.typeFcm = typeFcm;
            this.fcmTOKEN = fcmTOKEN;
            this.apiResponse = loginResponse;
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

            Call<SuccessMessageResponse> call = apiInterface.fcmUserCodeSaver(userIDForFcm, providerIdForFcm, fcmTOKEN, typeFcm);


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
            findViewById(R.id.btnProgressBar).setVisibility(View.GONE);

            if (serviceResponse != null) {
                if (serviceResponse.isSuccess()) {

                    if (serviceResponse.getMessage().equals("Token saved successfully!") || serviceResponse.getMessage().equals("Token updated successfully!")) {


                        SharedPreferences preferences = getSharedPreferences("user", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();

                        editor.putString("id", apiResponse.getUser().getId());
                        editor.putString("dp", apiResponse.getUser().getProfile_picture());
                        editor.putString("name", apiResponse.getUser().getName());
                        editor.putString("profession", apiResponse.getUser().getProfession());
                        editor.putString("email", apiResponse.getUser().getEmail());
                        editor.putString("phone", apiResponse.getUser().getPhone());
                        editor.putString("latitude", apiResponse.getUser().getLatitude());
                        editor.putString("longitude", apiResponse.getUser().getLongitude());

                        boolean bool = (apiResponse.getUser().isIs_blocked() == 1);
                        editor.putBoolean("blocked", bool);

                        editor.putString("address", apiResponse.getUser().getAddress());

                        editor.putBoolean("isUser", true);

                        editor.putBoolean("isLogIn", true);


                        editor.apply();


                        SharedPreferences preferences2 = getSharedPreferences("settings", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor2 = preferences2.edit();


                        editor2.putString("address", apiResponse.getUser().getAddress());
                        editor2.putString("latitude", apiResponse.getUser().getLatitude());
                        editor2.putString("longitude", apiResponse.getUser().getLongitude());
                        editor2.apply();

                        startActivity(new Intent(OtpSendActivity.this, LocationSelectActivity.class));
                        finish();



                        CustomToastPositive.create(getApplicationContext(),"User Register Success!");


                    } else {
                        CustomToastNegative.create(getApplicationContext(),"Error: "+serviceResponse.getMessage());
                    }


                } else {

                    CustomToastNegative.create(getApplicationContext(),"Error FCM: "+serviceResponse.getMessage());
                }
            } else {
                // Handle the case when there is no responsesw
            }
        }

    }


    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {

        ExitBottomSheet exitBottomSheet = new ExitBottomSheet(true);
        exitBottomSheet.show(getSupportFragmentManager(), exitBottomSheet.getTag());


    }
}