package www.experthere.in.users;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;


import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import www.experthere.in.FCM.FcmBearerTokenGenerator;
import www.experthere.in.helper.CustomToastNegative;
import www.experthere.in.helper.CustomToastPositive;
import www.experthere.in.helper.EmailValidator;
import www.experthere.in.LocationSelectActivity;
import www.experthere.in.R;
import www.experthere.in.api.ApiClient;
import www.experthere.in.api.ApiInterface;
import www.experthere.in.helper.InternetReceiver;
import www.experthere.in.model.ApiResponse;
import www.experthere.in.model.EmailValidationResponse;
import www.experthere.in.model.LoginResponse;
import www.experthere.in.model.ResponseProvider;
import www.experthere.in.model.SuccessMessageResponse;
import www.experthere.in.serviceProvider.ProviderHome;
import www.experthere.in.serviceProvider.ProviderIntroActivity;

public class LoginActivity extends AppCompatActivity {
    boolean googleClicked = false;

    EditText etEmail;
    TextInputEditText etPass;
    private static final int RC_GOOGLE_SIGN_IN = 1001;

    private GoogleApiClient mGoogleApiClient;
    private GoogleSignInClient mGoogleSignInClient;
    boolean isProvider;

    BroadcastReceiver receiver = null;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    boolean providerCanceled;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.white, this.getTheme()));

        setContentView(R.layout.activity_login);
        receiver = new InternetReceiver();
        internetStatus();


        //fcm with auth
        sharedPreferences = getSharedPreferences("fcm", MODE_PRIVATE);
        editor = sharedPreferences.edit();


        Bundle bundle1 = getIntent().getExtras();
        if (bundle1 != null) {

            isProvider = bundle1.getBoolean("isProvider", false);

        }

        etEmail = findViewById(R.id.etEmail);
        etPass = findViewById(R.id.editTextPassword);
        mGoogleSignInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN);

//        Bundle bundle = getIntent().getExtras();
//        if (bundle != null) {
//d
//d
//            if (!bundle.getString("email").isEmpty()) {
//
//                etEmail.setText(bundle.getString("email"));
//
//            }
//
//        }

        TextView textView = findViewById(R.id.registerBtnLoginActivity);


        if (isProvider) {

            textView.setText("Join As Partner");
        }


        findViewById(R.id.registerBtnLoginActivity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isProvider) {


                    startActivity(new Intent(LoginActivity.this, ProviderIntroActivity.class));
                    finish();


                } else {

                    startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                    finish();
                }

            }
        });





        findViewById(R.id.googleSignInBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Configure Google Sign-In


                if (!googleClicked) {

                    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestEmail()
                            .build();

                    mGoogleApiClient = new GoogleApiClient.Builder(LoginActivity.this)
                            .enableAutoManage(LoginActivity.this, connectionResult -> Log.e("GoogleSignIn", "Connection failed"))
                            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                            .build();


                    signIn();


                }

            }
        });

        findViewById(R.id.progressBtnLay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (EmailValidator.isValidEmail(etEmail.getText().toString().trim())) {

                    if (!etPass.getText().toString().isEmpty()) {


                        findViewById(R.id.btnProgressBar).setVisibility(View.VISIBLE);


                        if (isProvider) {
                            new LoginTaskProvider(getApplicationContext(), LoginActivity.this, etEmail.getText().toString().trim().toLowerCase(), etPass.getText().toString().trim()).execute();

                            Log.d("Projbjhvjv", "provider login start");


                        } else {

                            new LoginTask(getApplicationContext(), LoginActivity.this, etEmail.getText().toString().trim().toLowerCase(), etPass.getText().toString().trim()).execute();

                        }

                    } else {
                        etPass.setError("Enter Password!");
                    }

                } else {
                    etEmail.setError("Enter Valid Email!");
                }


            }
        });

        // forgot password logic
        findViewById(R.id.forgotBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (EmailValidator.isValidEmail(etEmail.getText().toString().trim())) {

                    Bundle bundle = new Bundle();
                    bundle.putString("email", etEmail.getText().toString());

                    if (isProvider) {
                        bundle.putBoolean("isProvider", true);
                    }
                    findViewById(R.id.passwordTextInputLayout1).setVisibility(View.VISIBLE);
                    findViewById(R.id.progressBtnLay).setVisibility(View.VISIBLE);

                    Intent intent = new Intent(LoginActivity.this, ForgotPassOtpVerifyActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);


                } else {

                    findViewById(R.id.passwordTextInputLayout1).setVisibility(View.GONE);
                    findViewById(R.id.progressBtnLay).setVisibility(View.GONE);

                    CustomToastNegative.create(getApplicationContext(),"Enter Email To Reset Password");


                }
            }
        });


    }

    private void internetStatus() {

        registerReceiver(receiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(receiver);
    }


    private void signIn() {

        googleClicked = true;
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_GOOGLE_SIGN_IN) {
            GoogleSignInAccount account = Auth.GoogleSignInApi.getSignInResultFromIntent(data).getSignInAccount();
            handleSignInResult(account);
        }
    }


    private void handleSignInResult(GoogleSignInAccount account) {
        if (account != null) {
            String email = account.getEmail().trim();
            String displayName = account.getDisplayName();

            // Use the email and display name as needed


            findViewById(R.id.btnProgressBar).setVisibility(View.VISIBLE);
            if (!email.isEmpty()) {


                if (isProvider) {


                    new CheckProviderEmailTask(getApplicationContext(), LoginActivity.this, email).execute();


                } else {

                    new CheckEmailExists(getApplicationContext(), LoginActivity.this, email).execute();
                }

            } else {

                CustomToastNegative.create(getApplicationContext(),"Problem With Google Sign In!");

            }


        } else {
            // Google Sign-In failed
            Log.e("GoogleSignIn", "Failed to sign in with Google");
            CustomToastNegative.create(getApplicationContext(),"Google Sign-In failed");

            providerCanceled = true;
            signOut();
        }
    }


    private class LoginByGoogleTask extends AsyncTask<Void, Void, Void> {
        private final Context context;
        private final Activity activity;

        private final String email;

        LoginByGoogleTask(Context context, Activity activity, String email) {
            this.context = context;
            this.email = email;
            this.activity = activity;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            String apiKEY = getString(R.string.apikey);

            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
            apiInterface.loginUserByGoogle(email, apiKEY).enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {


                    if (response.isSuccessful()) {

                        LoginResponse loginResponse = response.body();

                        if (loginResponse != null && !loginResponse.isError()) {


                            signInAnonymouslyUserGoogle(loginResponse, activity);


                        } else {
                            activity.findViewById(R.id.btnProgressBar).setVisibility(View.GONE);


                            if (loginResponse.isError()) {
                                signOut();

                                CustomToastNegative.create(getApplicationContext()," "+loginResponse.getMessage());

                            }
                        }


                    } else {

                        signOut();
                        activity.findViewById(R.id.btnProgressBar).setVisibility(View.GONE);
                        CustomToastNegative.create(getApplicationContext(),"Error Getting Response!");

                    }


                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {

                    activity.findViewById(R.id.btnProgressBar).setVisibility(View.GONE);


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

    private void signInAnonymouslyProviderGoogle(ResponseProvider loginResponse, Activity activity) {

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

                        String fcmDeviceToken = sharedPreferences.getString("device_token","0");

                        try {

                            new FcmServiceTaskProviderGoogle("0", loginResponse.getProvider().getId(), "provider", fcmDeviceToken, loginResponse).execute();
                        } catch (Exception e) {

                            activity.findViewById(R.id.btnProgressBar).setVisibility(View.GONE);

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

                        CustomToastNegative.create(getApplicationContext(),"Token generation failed");

                    }
                });
            }
        });
    }


    private void signInAnonymouslyProvider(ResponseProvider loginResponse, Activity activity) {

        Log.d("Projbjhvjv", "provider  anonymus method run ");


        FcmBearerTokenGenerator tokenGenerator = new FcmBearerTokenGenerator(this);
        tokenGenerator.generateAccessTokenAsync(new FcmBearerTokenGenerator.TokenGenerationListener() {
            @Override
            public void onTokenGenerated(final String token) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Use the generated token for FCM requests

                        Log.d("Projbjhvjv", " got token- " + token);


                        editor.putString("TOKEN", token);

                        editor.apply();

                        String fcmDeviceToken = sharedPreferences.getString("device_token","0");


                        try {
                            Log.d("Projbjhvjv", " trying.... - ");



                            new FcmServiceTaskProvider("0", loginResponse.getProvider().getId(), "provider", fcmDeviceToken, loginResponse).execute();


                        } catch (Exception e) {

                            Log.d("Projbjhvjv", " error - " + e.getMessage());

                            activity.findViewById(R.id.btnProgressBar).setVisibility(View.GONE);


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
                        CustomToastNegative.create(getApplicationContext(),"Token generation failed");

                    }
                });
            }
        });


    }

    private void signInAnonymouslyUserGoogle(LoginResponse loginResponse, Activity activity) {


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

                        String fcmDeviceToken = sharedPreferences.getString("device_token","0");


                        try {
                            new FcmServiceTaskUserGoogle(loginResponse.getUser().getId(), "0", "user", fcmDeviceToken, loginResponse).execute();
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
                        CustomToastNegative.create(getApplicationContext(),"Token generation failed");

                        activity.findViewById(R.id.btnProgressBar).setVisibility(View.GONE);

                    }
                });
            }
        });


    }


    private void signInAnonymouslyUser(LoginResponse loginResponse, Activity activity) {


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

                        String fcmDeviceToken = sharedPreferences.getString("device_token","0");

                        try {
                            new FcmServiceTaskUser(loginResponse.getUser().getId(), "0", "user", fcmDeviceToken, loginResponse).execute();
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
                        CustomToastNegative.create(getApplicationContext(),"Token generation failed");

                        activity.findViewById(R.id.btnProgressBar).setVisibility(View.GONE);

                    }
                });
            }
        });


    }


    private class FcmServiceTaskProviderGoogle extends AsyncTask<Void, Void, SuccessMessageResponse> {
        String userIDForFcm, providerIdForFcm, typeFcm, fcmTOKEN;
        ResponseProvider loginResponse;

        public FcmServiceTaskProviderGoogle(String userIDForFcm, String providerIdForFcm, String typeFcm, String fcmTOKEN, ResponseProvider loginResponse) {
            this.userIDForFcm = userIDForFcm;
            this.providerIdForFcm = providerIdForFcm;
            this.typeFcm = typeFcm;
            this.fcmTOKEN = fcmTOKEN;
            this.loginResponse = loginResponse;
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

            Call<SuccessMessageResponse> call = apiInterface.fcmProviderCodeSaver(userIDForFcm, providerIdForFcm, fcmTOKEN, typeFcm);


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



                        Log.d("LOGINCHECK", "onResponse: " + loginResponse.getProvider().getId());
                        Log.d("LOGINCHECK", "onResponse: " + loginResponse.getProvider().getName());
                        Log.d("LOGINCHECK", "onResponse: " + loginResponse.getProvider().getEmail());
                        Log.d("LOGINCHECK", "onResponse: " + loginResponse.getProvider().getLatitude());
                        Log.d("LOGINCHECK", "onResponse: " + loginResponse.getProvider().getLongitude());
                        Log.d("LOGINCHECK", "onResponse: " + loginResponse.getProvider().getAddress());




                        SharedPreferences preferences = getSharedPreferences("provider", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();


                        editor.putString("id", loginResponse.getProvider().getId());
                        editor.putString("name", loginResponse.getProvider().getName());

                        editor.putString("email", loginResponse.getProvider().getEmail());
                        editor.putString("password", loginResponse.getProvider().getPassword());
                        editor.putString("country_code", loginResponse.getProvider().getCountry_code());
                        editor.putString("phone", loginResponse.getProvider().getPhone());
                        editor.putString("id_type", loginResponse.getProvider().getIdType());
                        editor.putString("company_name", loginResponse.getProvider().getCompanyName());
                        editor.putString("visiting_charges", loginResponse.getProvider().getVisitingCharges());
                        editor.putString("currency", loginResponse.getProvider().getCurrency());
                        editor.putString("advance_booking_days", loginResponse.getProvider().getAdvanceBookingDays());
                        editor.putString("account_type", loginResponse.getProvider().getAccountType());
                        editor.putString("members", loginResponse.getProvider().getMembers());
                        editor.putString("description", loginResponse.getProvider().getDescription());
                        editor.putString("city", loginResponse.getProvider().getCity());
                        editor.putString("latitude", loginResponse.getProvider().getLatitude());
                        editor.putString("longitude", loginResponse.getProvider().getLongitude());
                        editor.putString("address", loginResponse.getProvider().getAddress());

                        editor.putString("document_image", loginResponse.getProvider().getDocumentImage());
                        editor.putString("logo_image", loginResponse.getProvider().getLogoImage());
                        editor.putString("banner_image", loginResponse.getProvider().getBannerImage());

                        editor.putBoolean("isProvider", true);

                        boolean booleanValue = (loginResponse.getProvider().getIs_blocked() == 1);
                        editor.putBoolean("isBlocked", booleanValue);

                        editor.apply();




                        SharedPreferences preferences2 = getSharedPreferences("settings", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor2 = preferences2.edit();


                        editor2.putString("address", loginResponse.getProvider().getAddress());
                        editor2.putString("latitude", loginResponse.getProvider().getLatitude());
                        editor2.putString("longitude", loginResponse.getProvider().getLongitude());
                        editor2.apply();


                        startActivity(new Intent(getApplicationContext(), ProviderHome.class));
                        finish();


                    } else {
                        CustomToastNegative.create(getApplicationContext(),"Error: "+serviceResponse.getMessage());
                    }


                } else {

                    CustomToastNegative.create(getApplicationContext(),"  "+serviceResponse.getMessage());
                }
            } else {
                // Handle the case when there is no responsesw
            }
        }

    }

    private class FcmServiceTaskProvider extends AsyncTask<Void, Void, SuccessMessageResponse> {
        String userIDForFcm, providerIdForFcm, typeFcm, fcmTOKEN;
        ResponseProvider loginResponse;

        public FcmServiceTaskProvider(String userIDForFcm, String providerIdForFcm, String typeFcm, String fcmTOKEN, ResponseProvider loginResponse) {
            this.userIDForFcm = userIDForFcm;
            this.providerIdForFcm = providerIdForFcm;
            this.typeFcm = typeFcm;
            this.fcmTOKEN = fcmTOKEN;
            this.loginResponse = loginResponse;
        }

        @Override
        protected void onPreExecute() {


            super.onPreExecute();

        }

        @Override
        protected SuccessMessageResponse doInBackground(Void... voids) {

            Log.d("Projbjhvjv", "provider  fcm task  method run ");


            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
//            Call<ApiResponse> call = apiInterface.getServiceUserWithSearchAndFilter(latitude, longitude, page, itemsPerPage, radius, 5, searchQuery);

            Call<SuccessMessageResponse> call = apiInterface.fcmProviderCodeSaver(userIDForFcm, providerIdForFcm, fcmTOKEN, typeFcm);


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
                    Log.d("Projbjhvjv", "message  " + serviceResponse.getMessage());

                    if (serviceResponse.getMessage().equals("Token saved successfully!") || serviceResponse.getMessage().equals("Token updated successfully!")) {




                        SharedPreferences preferences = getSharedPreferences("provider", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();


                        editor.putString("id", loginResponse.getProvider().getId());
                        editor.putString("name", loginResponse.getProvider().getName());

                        editor.putString("email", loginResponse.getProvider().getEmail());
                        editor.putString("password", loginResponse.getProvider().getPassword());
                        editor.putString("country_code", loginResponse.getProvider().getCountry_code());
                        editor.putString("phone", loginResponse.getProvider().getPhone());
                        editor.putString("id_type", loginResponse.getProvider().getIdType());
                        editor.putString("company_name", loginResponse.getProvider().getCompanyName());
                        editor.putString("visiting_charges", loginResponse.getProvider().getVisitingCharges());
                        editor.putString("currency", loginResponse.getProvider().getCurrency());
                        editor.putString("advance_booking_days", loginResponse.getProvider().getAdvanceBookingDays());
                        editor.putString("account_type", loginResponse.getProvider().getAccountType());
                        editor.putString("members", loginResponse.getProvider().getMembers());
                        editor.putString("description", loginResponse.getProvider().getDescription());
                        editor.putString("city", loginResponse.getProvider().getCity());
                        editor.putString("latitude", loginResponse.getProvider().getLatitude());
                        editor.putString("longitude", loginResponse.getProvider().getLongitude());
                        editor.putString("address", loginResponse.getProvider().getAddress());
                        editor.putString("document_image", loginResponse.getProvider().getDocumentImage());
                        editor.putString("logo_image", loginResponse.getProvider().getLogoImage());
                        editor.putString("banner_image", loginResponse.getProvider().getBannerImage());

                        boolean booleanValue = (loginResponse.getProvider().getIs_blocked() == 1);
                        editor.putBoolean("isBlocked", booleanValue);

                        editor.putBoolean("isProvider", true);


                        editor.apply();


                        Log.d("LOGINCHECK", "block: " + loginResponse.getProvider().getIs_blocked());


                        Log.d("LOGINCHECK", "block: " + booleanValue);


                        SharedPreferences preferences2 = getSharedPreferences("settings", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor2 = preferences2.edit();


                        editor2.putString("address", loginResponse.getProvider().getAddress());
                        editor2.putString("latitude", loginResponse.getProvider().getLatitude());
                        editor2.putString("longitude", loginResponse.getProvider().getLongitude());
                        editor2.apply();

                        startActivity(new Intent(getApplicationContext(), ProviderHome.class));
                        finish();

                    } else {
                        CustomToastNegative.create(getApplicationContext(),"Error: "+serviceResponse.getMessage());

                    }


                } else {

                    CustomToastNegative.create(getApplicationContext()," "+serviceResponse.getMessage());
                }
            } else {
                // Handle the case when there is no responsesw
            }
        }

    }

    private class FcmServiceTaskUserGoogle extends AsyncTask<Void, Void, SuccessMessageResponse> {
        String userIDForFcm, providerIdForFcm, typeFcm, fcmTOKEN;
        LoginResponse loginResponse;

        public FcmServiceTaskUserGoogle(String userIDForFcm, String providerIdForFcm, String typeFcm, String fcmTOKEN, LoginResponse loginResponse) {
            this.userIDForFcm = userIDForFcm;
            this.providerIdForFcm = providerIdForFcm;
            this.typeFcm = typeFcm;
            this.fcmTOKEN = fcmTOKEN;
            this.loginResponse = loginResponse;
        }

        @Override
        protected void onPreExecute() {

            findViewById(R.id.btnProgressBar).setVisibility(View.VISIBLE);

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



                        CustomToastPositive.create(getApplicationContext()," "+loginResponse.getMessage());



                        Log.d("LOGINCHECK", "onResponse: " + loginResponse.getUser().getId());
                        Log.d("LOGINCHECK", "onResponse: " + loginResponse.getUser().getName());
                        Log.d("LOGINCHECK", "onResponse: " + loginResponse.getUser().getProfession());
                        Log.d("LOGINCHECK", "onResponse: " + loginResponse.getUser().getEmail());
                        Log.d("LOGINCHECK", "onResponse: " + loginResponse.getUser().getLatitude());
                        Log.d("LOGINCHECK", "onResponse: " + loginResponse.getUser().getLongitude());
                        Log.d("LOGINCHECK", "onResponse: " + loginResponse.getUser().isIs_blocked());
                        Log.d("LOGINCHECK", "onResponse: " + loginResponse.getUser().getAddress());


                        SharedPreferences preferences = getSharedPreferences("user", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();

                        editor.putString("id", loginResponse.getUser().getId());
                        editor.putString("name", loginResponse.getUser().getName());
                        editor.putString("dp", loginResponse.getUser().getProfile_picture());
                        editor.putString("profession", loginResponse.getUser().getProfession());
                        editor.putString("email", loginResponse.getUser().getEmail());
                        editor.putString("phone", loginResponse.getUser().getPhone());
                        editor.putString("latitude", loginResponse.getUser().getLatitude());
                        editor.putString("longitude", loginResponse.getUser().getLongitude());

                        boolean bool = (loginResponse.getUser().isIs_blocked() == 1);

                        editor.putBoolean("blocked", bool);


                        editor.putString("address", loginResponse.getUser().getAddress());
                        editor.putBoolean("isLogIn", true);

                        editor.putBoolean("isUser", true);


                        if (!loginResponse.getUser().getLatitude().matches("0")) {
                            editor.putBoolean("isFullLogin", true);
                        }

                        editor.apply();

                        SharedPreferences preferences2 = getSharedPreferences("settings", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor2 = preferences2.edit();


                        editor2.putString("address", loginResponse.getUser().getAddress());
                        editor2.putString("latitude", loginResponse.getUser().getLatitude());
                        editor2.putString("longitude", loginResponse.getUser().getLongitude());
                        editor2.apply();

                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        finish();


                    } else {
                        CustomToastNegative.create(getApplicationContext(),"Error: "+serviceResponse.getMessage());
                    }


                } else {

                    CustomToastNegative.create(getApplicationContext(),"  "+serviceResponse.getMessage());
                }
            } else {
                // Handle the case when there is no responsesw
            }
        }

    }

    private class FcmServiceTaskUser extends AsyncTask<Void, Void, SuccessMessageResponse> {
        String userIDForFcm, providerIdForFcm, typeFcm, fcmTOKEN;
        LoginResponse loginResponse;

        public FcmServiceTaskUser(String userIDForFcm, String providerIdForFcm, String typeFcm, String fcmTOKEN, LoginResponse loginResponse) {
            this.userIDForFcm = userIDForFcm;
            this.providerIdForFcm = providerIdForFcm;
            this.typeFcm = typeFcm;
            this.fcmTOKEN = fcmTOKEN;
            this.loginResponse = loginResponse;
        }

        @Override
        protected void onPreExecute() {

            findViewById(R.id.btnProgressBar).setVisibility(View.VISIBLE);

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


                        CustomToastPositive.create(getApplicationContext()," "+loginResponse.getMessage());

                        Log.d("LOGINCHECK", "onResponse: " + loginResponse.getUser().getId());
                        Log.d("LOGINCHECK", "onResponse: " + loginResponse.getUser().getName());
                        Log.d("LOGINCHECK", "onResponse: " + loginResponse.getUser().getProfession());
                        Log.d("LOGINCHECK", "onResponse: " + loginResponse.getUser().getEmail());
                        Log.d("LOGINCHECK", "onResponse: " + loginResponse.getUser().getLatitude());
                        Log.d("LOGINCHECK", "onResponse: " + loginResponse.getUser().getLongitude());
                        Log.d("LOGINCHECK", "onResponse: " + loginResponse.getUser().isIs_blocked());
                        Log.d("LOGINCHECK", "onResponse: " + loginResponse.getUser().getAddress());


                        SharedPreferences preferences = getSharedPreferences("user", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();

                        editor.putString("id", loginResponse.getUser().getId());
                        editor.putString("name", loginResponse.getUser().getName());
                        editor.putString("dp", loginResponse.getUser().getProfile_picture());
                        editor.putString("profession", loginResponse.getUser().getProfession());
                        editor.putString("email", loginResponse.getUser().getEmail());
                        editor.putString("phone", loginResponse.getUser().getPhone());
                        editor.putString("latitude", loginResponse.getUser().getLatitude());
                        editor.putString("longitude", loginResponse.getUser().getLongitude());


                        SharedPreferences preferences2 = getSharedPreferences("settings", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor2 = preferences2.edit();


                        editor2.putString("address", loginResponse.getUser().getAddress());
                        editor2.putString("latitude", loginResponse.getUser().getLatitude());
                        editor2.putString("longitude", loginResponse.getUser().getLongitude());
                        editor2.apply();


                        boolean bool = (loginResponse.getUser().isIs_blocked() == 1);

                        editor.putBoolean("blocked", bool);


                        editor.putString("address", loginResponse.getUser().getAddress());

                        editor.putBoolean("isUser", true);


                        editor.putBoolean("isLogIn", true);
                        if (!loginResponse.getUser().getLatitude().matches("0")) {
                            editor.putBoolean("isFullLogin", true);
                        }
                        editor.apply();

                        if (preferences.getBoolean("isFullLogin", false)) {
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                            finish();
                        } else {
                            startActivity(new Intent(getApplicationContext(), LocationSelectActivity.class));
                            finish();
                        }


                    } else {
                        CustomToastNegative.create(getApplicationContext(),"Error: "+serviceResponse.getMessage());
                    }


                } else {

                    CustomToastNegative.create(getApplicationContext(),"  "+serviceResponse.getMessage());
                }
            } else {
                // Handle the case when there is no responsesw
            }
        }

    }


    private class LoginByGoogleTaskProviders extends AsyncTask<Void, Void, Void> {
        private final Context context;
        private final Activity activity;

        private final String email;

        LoginByGoogleTaskProviders(Context context, Activity activity, String email) {
            this.context = context;
            this.email = email;
            this.activity = activity;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
            apiInterface.loginProvidersByGoogle(email).enqueue(new Callback<ResponseProvider>() {
                @Override
                public void onResponse(Call<ResponseProvider> call, Response<ResponseProvider> response) {



                    ResponseProvider loginResponse = response.body();

                    if (response.isSuccessful() && loginResponse != null) {


                        if (!loginResponse.isError()) {

                            signInAnonymouslyProviderGoogle(loginResponse, activity);


                        } else {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    activity.findViewById(R.id.btnProgressBar).setVisibility(View.GONE);


                                    CustomToastNegative.create(getApplicationContext(),"Got Error: "+loginResponse.getMessage());

                                    CustomToastNegative.create(getApplicationContext(),"Got Error: "+loginResponse.getMessage());

                                }
                            });

                        }


                    } else {


                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                CustomToastNegative.create(getApplicationContext(),"Error Getting Response!");

                                activity.findViewById(R.id.btnProgressBar).setVisibility(View.GONE);


                            }
                        });


                        signOut();
                    }


                }

                @Override
                public void onFailure(Call<ResponseProvider> call, Throwable t) {

                    activity.findViewById(R.id.btnProgressBar).setVisibility(View.GONE);


                }
            });


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // This method runs on the UI thread and can be used to update the UI after background task completion

            findViewById(R.id.btnProgressBar).setVisibility(View.GONE);

        }

        @Override
        protected void onPreExecute() {

            findViewById(R.id.btnProgressBar).setVisibility(View.VISIBLE);

            super.onPreExecute();
        }
    }

    private class CheckProviderEmailTask extends AsyncTask<String, Void, Boolean> {


        private final Context context;
        private final Activity activity;

        private final String email;

        public CheckProviderEmailTask(Context context, Activity activity, String email) {
            this.context = context;
            this.activity = activity;
            this.email = email;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {

                // Create Retrofit service
                ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

                // Make the API call to check if the email exists

                apiInterface.checkEmailProvider(email).enqueue(new Callback<EmailValidationResponse>() {
                    @Override
                    public void onResponse(Call<EmailValidationResponse> call, Response<EmailValidationResponse> response) {

                        if (response.isSuccessful()) {


                            EmailValidationResponse emailValidationResponse = response.body();


                            if (emailValidationResponse != null && emailValidationResponse.isExists()) {


                                new LoginByGoogleTaskProviders(getApplicationContext(), LoginActivity.this, email).execute();


                            } else {



                                CustomToastNegative.create(getApplicationContext(),"Email Not Register Create New Account!");



                                signOut();
                                activity.findViewById(R.id.btnProgressBar).setVisibility(View.GONE);


                            }


                        } else {
                            CustomToastNegative.create(getApplicationContext(),"Fail ");
                        }

                    }

                    @Override
                    public void onFailure(Call<EmailValidationResponse> call, Throwable t) {

                        CustomToastNegative.create(getApplicationContext(),"Got Error: "+t.getMessage());

                    }
                });


                return false;

            } catch (Exception e) {
                e.printStackTrace();

                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean emailExists) {


//            if (emailExists) {
//
//
//                new LoginByGoogleTaskProviders(getApplicationContext(), LoginActivity.this, etEmail.getText().toString()).execute();
//
//
//
//
//            } else {
//
//
//
//                signOut();
//                activity.findViewById(R.id.btnProgressBar).setVisibility(View.GONE);
//
//
//
//            }
        }
    }

    private class CheckEmailExists extends AsyncTask<Void, Void, Void> {
        private final Context context;
        private final Activity activity;

        private final String email;

        CheckEmailExists(Context context, Activity activity, String email) {
            this.context = context;
            this.email = email;
            this.activity = activity;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
            apiInterface.checkEmailExists(email).enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {


                    if (response.isSuccessful()) {

                        ApiResponse loginResponse = response.body();

                        if (loginResponse != null && !loginResponse.isError()) {


                            new LoginByGoogleTask(getApplicationContext(), LoginActivity.this, email).execute();


                        } else {

                            if (loginResponse != null) {
                                signOut();

                                CustomToastPositive.create(getApplicationContext()," "+loginResponse.getMessage());


                                activity.findViewById(R.id.btnProgressBar).setVisibility(View.GONE);


                            }
                        }


                    } else {
                        activity.findViewById(R.id.btnProgressBar).setVisibility(View.GONE);
                        signOut();

                    }


                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {

                    activity.findViewById(R.id.btnProgressBar).setVisibility(View.GONE);
                    signOut();

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

    private class LoginTaskProvider extends AsyncTask<Void, Void, Void> {
        private final Context context;
        private final Activity activity;

        private final String email;
        private final String password;

        LoginTaskProvider(Context context, Activity activity, String email, String password) {
            this.context = context;
            this.email = email;
            this.password = password;
            this.activity = activity;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
            apiInterface.loginProvider(email, password).enqueue(new Callback<ResponseProvider>() {
                @Override
                public void onResponse(Call<ResponseProvider> call, Response<ResponseProvider> response) {


                    if (response.isSuccessful()) {

                        ResponseProvider loginResponse = response.body();

                        if (loginResponse != null && !loginResponse.isError()) {

                            CustomToastPositive.create(getApplicationContext()," "+loginResponse.getMessage());

                            Log.d("Projbjhvjv", "provider login res - " + loginResponse.getMessage());


                            signInAnonymouslyProvider(loginResponse, activity);


                        } else {


                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    activity.findViewById(R.id.btnProgressBar).setVisibility(View.GONE);


                                }
                            });


                            if (loginResponse != null) {

                                CustomToastNegative.create(getApplicationContext()," "+loginResponse.getMessage());
                            }
                        }


                    } else {


                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                activity.findViewById(R.id.btnProgressBar).setVisibility(View.GONE);


                            }
                        });

                    }


                }

                @Override
                public void onFailure(Call<ResponseProvider> call, Throwable t) {

                    activity.findViewById(R.id.btnProgressBar).setVisibility(View.GONE);
                    CustomToastNegative.create(getApplicationContext(),"Got Error: "+t.getMessage());

                    Log.d("SERVERERRR", "onFailure: " + t.getMessage());

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

    private class LoginTask extends AsyncTask<Void, Void, Void> {
        private final Context context;
        private final Activity activity;

        private final String email;
        private final String password;

        LoginTask(Context context, Activity activity, String email, String password) {
            this.context = context;
            this.email = email;
            this.password = password;
            this.activity = activity;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
            apiInterface.loginUser(email, password).enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {


                    if (response.isSuccessful()) {

                        LoginResponse loginResponse = response.body();

                        if (loginResponse != null && !loginResponse.isError()) {


                            signInAnonymouslyUser(loginResponse, activity);

                        } else {

                            activity.findViewById(R.id.btnProgressBar).setVisibility(View.GONE);

                            if (loginResponse != null) {

                                CustomToastNegative.create(getApplicationContext()," "+loginResponse.getMessage());
                            }
                        }


                    } else {
                        activity.findViewById(R.id.btnProgressBar).setVisibility(View.GONE);

                    }


                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {

                    activity.findViewById(R.id.btnProgressBar).setVisibility(View.GONE);

                    CustomToastNegative.create(getApplicationContext(),"Got Error: "+t.getMessage());

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

    private void signOut() {

        mGoogleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                // Sign-out was successful
                // You can now redirect the user to the sign-in screen or perform other actions

                // For example, you might want to return to the sign-in activity

                if (isProvider) {

                    if (providerCanceled){
                        startActivity(new Intent(LoginActivity.this, IntroActivity.class));
                        finish();
                    }else {
                        startActivity(new Intent(LoginActivity.this, ProviderIntroActivity.class));
                        finish();
                    }



                } else {

                    startActivity(new Intent(LoginActivity.this, IntroActivity.class));
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                CustomToastNegative.create(getApplicationContext(),"Signout Fail: "+e.getMessage());

            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(LoginActivity.this, IntroActivity.class));
        super.onBackPressed();
    }

}