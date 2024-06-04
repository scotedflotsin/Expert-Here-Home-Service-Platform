package www.experthere.in.users;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.firebase.messaging.FirebaseMessaging;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import www.experthere.in.MaintenanceActivity;
import www.experthere.in.R;
import www.experthere.in.SplashScreen;
import www.experthere.in.api.ApiClient;
import www.experthere.in.api.ApiInterface;
import www.experthere.in.calling.MainCallActivity;
import www.experthere.in.helper.AdManager;
import www.experthere.in.model.AdmobData;
import www.experthere.in.model.AdsResponse;
import www.experthere.in.model.BlockRes;
import www.experthere.in.helper.BookmarkCountResponse;
import www.experthere.in.helper.CustomToastNegative;
import www.experthere.in.helper.CustomToastPositive;
import www.experthere.in.helper.ExitBottomSheet;
import www.experthere.in.helper.InternetReceiver;
import www.experthere.in.model.MaintenanceData;
import www.experthere.in.model.MaintenanceRes;
import www.experthere.in.model.SuccessMessageResponse;
import www.experthere.in.users.fragments.UserBookmarkFragment;
import www.experthere.in.users.fragments.UserCallFragment;
import www.experthere.in.users.fragments.UserCatFragment;
import www.experthere.in.users.fragments.UserHomeFragment;
import www.experthere.in.users.fragments.UserProfileFragment;


public class HomeActivity extends AppCompatActivity {



    BroadcastReceiver receiver;
    BottomNavigationView bottomNavigationView;
    private static final String LOCK_SCREEN_PERMISSION_GRANTED = "lock_screen_allowed";
    private static final String PREFS_SETTINGS = "settings";
    private static final String LANG_PREF_KEY = "language";

    private static final String CUSTOM_ACTION = "www.experthere.in.users.GOTO.CAT.FRAGMENT";
    private static final String UPDATE_BOOKMARK_BADGE = "www.experthere.in.users.BOOKMARK_UPDATE_USER";


    boolean isChecked;
    SharedPreferences prefs;

    Fragment fragment = null;
    String tag = null;
    private GoogleSignInClient mGoogleSignInClient;


    boolean adsStatus;
    String bannerId,interstitialID,appOpenId,nativeId;


    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Handle the broadcast here
            if (intent.getAction() != null && intent.getAction().equals(CUSTOM_ACTION)) {
                // Get the data from the intent


                fragment = new UserCatFragment();
                tag = "CAT";
                if (fragment != null && !isFragmentActive(tag)) {
                    loadFragment(fragment, tag);
                }
                bottomNavigationView.setSelectedItemId(R.id.catUserMenuBtn);


            }
        }
    };
    private final BroadcastReceiver bookmarkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Handle the broadcast here
            if (intent.getAction() != null && intent.getAction().equals(UPDATE_BOOKMARK_BADGE)) {
                // Get the data from the intent

                getBookmarkCount();


            }
        }
    };


    private static final int MY_REQUEST_CODE = 1110;
    private AppUpdateManager appUpdateManager;
    private InstallStateUpdatedListener listener; // Listener declared as a class-level variable




    SharedPreferences adsPref;

    SharedPreferences.Editor adsEditor;

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.white, this.getTheme()));

        setContentView(R.layout.activity_home);


        adsPref = getSharedPreferences("ads",MODE_PRIVATE);
        adsEditor = adsPref.edit();




//        InterstitialAd mInterstitialAd;
//
//
//
//        AdRequest adRequest = new AdRequest.Builder().build();
//        InterstitialAd.load(this,"ca-app-pub-3940256099942544/1033173712", adRequest,
//                new InterstitialAdLoadCallback() {
//                    @Override
//                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
//                        // The mInterstitialAd reference will be null until
//                        // an ad is loaded.
//                        mInterstitialAd = interstitialAd;
//                        Log.i("JSHISHIS", "onAdLoaded");
//
//                        if (mInterstitialAd != null) {
//                            mInterstitialAd.show(HomeActivity.this);
//                        } else {
//                            Log.d("TAG", "The interstitial ad wasn't ready yet.");
//                        }
//                    }
//
//                    @Override
//                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
//                        // Handle the error
//                        Log.d("JSHISHIS", loadAdError.toString());
//                        mInterstitialAd = null;
//                    }
//                });
//
//








        mGoogleSignInClient = GoogleSignIn.getClient(HomeActivity.this, GoogleSignInOptions.DEFAULT_SIGN_IN);


        appUpdateManager = AppUpdateManagerFactory.create(this);

        // Check for updates
        checkForAppUpdate();
        checkBlockStatus();
        askNotificationPermission();

        getAdmobDetails();



        // Subscribe to topic
        FirebaseMessaging.getInstance().subscribeToTopic("users")
                .addOnCompleteListener(task -> {
                    String msg = "Subscribed to users topic";
                    if (!task.isSuccessful()) {
                        msg = "Subscription to users topic failed";
                    }
                    Log.d("FCMTOPIC", msg);
                    // Optionally show a toast
                    // Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                });


        prefs = getSharedPreferences(PREFS_SETTINGS, MODE_PRIVATE);
        setLanguage(getLanguage());

        IntentFilter filter = new IntentFilter();
        filter.addAction(CUSTOM_ACTION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(broadcastReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(broadcastReceiver, filter);

        }


        IntentFilter filter2 = new IntentFilter();
        filter2.addAction(UPDATE_BOOKMARK_BADGE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(bookmarkReceiver, filter2, Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(bookmarkReceiver, filter2);

        }
        receiver = new InternetReceiver();
        internetStatus();
        requestAudioPermissions();


        bottomNavigationView = findViewById(R.id.bottom_navigation_user);
        bottomNavigationView.setSelectedItemId(R.id.homeUserMenuBtn);


        boolean overlayPermissionGranted = prefs.getBoolean(LOCK_SCREEN_PERMISSION_GRANTED, false);


        if (!overlayPermissionGranted) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = LayoutInflater.from(this);
            View dialogView = inflater.inflate(R.layout.dialog_custom_layout, null);
            builder.setView(dialogView);

            TextView messageTextView = dialogView.findViewById(R.id.messageTextView);
            CheckBox permissionCheckBox = dialogView.findViewById(R.id.permissionCheckBox);

            permissionCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean checked) {

                    isChecked = checked;

                }
            });

            builder.setTitle("Permission Required")
                    .setCancelable(false)
                    .setPositiveButton("Go to Settings", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            openAppSettings();

                            if (isChecked) {

                                CustomToastPositive.create(getApplicationContext(), "Setting Saved!");
                                saveOverlayPermission(true);
                            }

                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            if (isChecked) {
                                CustomToastPositive.create(getApplicationContext(), "Setting Saved!");
                                saveOverlayPermission(true);
                            }
                            dialog.dismiss();
                        }
                    });

            builder.create().show();

        }


        Bundle misCallbundle = getIntent().getExtras();


        if (misCallbundle != null) {

            if (misCallbundle.getBoolean("incomming_call")) {


                bottomNavigationView.setSelectedItemId(R.id.callUserMenuBtn);

                fragment = new UserCallFragment();
                tag = "CALL";
                if (fragment != null && !isFragmentActive(tag)) {
                    loadFragment(fragment, tag);
                }


            } else {

                fragment = new UserHomeFragment();
                tag = "HOME";
                if (fragment != null && !isFragmentActive(tag)) {
                    loadFragment(fragment, tag);
                }
                bottomNavigationView.setSelectedItemId(R.id.homeUserMenuBtn);

            }

        } else {


            fragment = new UserHomeFragment();
            tag = "HOME";
            if (fragment != null && !isFragmentActive(tag)) {
                loadFragment(fragment, tag);
            }


            bottomNavigationView.setSelectedItemId(R.id.homeUserMenuBtn);

        }


        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {


                if (item.getItemId() == R.id.callUserMenuBtn) {

                    fragment = new UserCallFragment();
                    tag = "CALL";

                }

                if (item.getItemId() == R.id.catUserMenuBtn) {
                    fragment = new UserCatFragment();
                    tag = "CAT";
                }

                if (item.getItemId() == R.id.homeUserMenuBtn) {


                    fragment = new UserHomeFragment();
                    tag = "HOME";


                }

                if (item.getItemId() == R.id.bookmarkUserMenuBtn) {


                    fragment = new UserBookmarkFragment();
                    tag = "BOOK";
                }

                if (item.getItemId() == R.id.profileUserMenuBtn) {
                    fragment = new UserProfileFragment();
                    tag = "PROFILE";
                }

                if (fragment != null && !isFragmentActive(tag)) {
                    loadFragment(fragment, tag);
                }


                return true;
            }
        });


        getBookmarkCount();


    }

    private void getAdmobDetails() {

        new GetAdmobData().execute();


    }


    public class GetAdmobData extends AsyncTask<Void, Void, AdsResponse> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected AdsResponse doInBackground(Void... voids) {
            try {
                // Create the Retrofit instance
                ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

                // Make the API call
                Call<AdsResponse> call = apiInterface.getAdmob(1);


                Response<AdsResponse> response = call.execute();

                if (response.isSuccessful()) {
                    return response.body();
                } else {
                    // Handle error
                    Log.e("ApiServiceTask", "API call failed. Code: " + response.code());
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(AdsResponse apiResponse) {
            super.onPostExecute(apiResponse);


            if (apiResponse != null) {

                if (apiResponse.isSuccess()) {

                    AdmobData admobData = apiResponse.getAdmob_data();

                    if (admobData!=null){


                        bannerId = admobData.getBanner();
                        interstitialID = admobData.getInterstitial();
                        nativeId = admobData.getNativeAds();
                        appOpenId = admobData.getOpen_app();


                        adsStatus= admobData.getStatus().equals("enable");



                        adsEditor.putString("banner",bannerId);
                        adsEditor.putString("interstitial",interstitialID);
                        adsEditor.putString("native",nativeId);
                        adsEditor.putString("app_open",appOpenId);
                        adsEditor.putBoolean("status",adsStatus);

                        adsEditor.apply();


                    }

                } else {


                }

            } else {


            }

        }

    }

    private void askNotificationPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Dexter.withContext(HomeActivity.this).withPermission(Manifest.permission.POST_NOTIFICATIONS).withListener(new PermissionListener() {
                @Override
                public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {



                }

                @Override
                public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {


                    if (permissionDeniedResponse.isPermanentlyDenied()){

                        CustomToastNegative.create(getApplicationContext(),"Please allow notification permission in Settings!!");


                    }else {

                        CustomToastNegative.create(getApplicationContext(),"Permission Required To Send You Notification!!");
                    }


                }

                @Override
                public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                    permissionToken.continuePermissionRequest();

                }
            }).check();
        }


    }

    private void checkBlockStatus() {

        SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);


        new GetBlockStatus(sharedPreferences.getString("id", "0")).execute();

    }

    private class GetBlockStatus extends AsyncTask<Void, Void, Void> {

        String id;

        public GetBlockStatus(String id) {
            this.id = id;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            // Perform background operation, e.g., user

            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

            Call<BlockRes> call = apiInterface.getStatusBlockedUSer(id);


            call.enqueue(new Callback<BlockRes>() {
                @Override
                public void onResponse(Call<BlockRes> call, Response<BlockRes> response) {

                    if (response.body() != null) {

                        BlockRes categoryResponse = response.body();

                        if (categoryResponse.isSuccess()) {

                            if (categoryResponse.getIs_blocked() == 1) {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {


                                        showBlockDialog(HomeActivity.this);

                                    }
                                });

                            } else {

                            }


                        } else {

                        }


                    }

                }

                @Override
                public void onFailure(Call<BlockRes> call, Throwable t) {


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

    public void showBlockDialog(Activity activity) {

        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(activity);
        alertDialogBuilder.setTitle(R.string.blocked_by_admin);
        alertDialogBuilder.setMessage(R.string.you_are_blocked_by_admin_due_to_some_invalid_activity_contact_admin_for_more_info);
        alertDialogBuilder.setCancelable(false);

        // Create a custom drawable with white background
        Drawable drawable = new ColorDrawable(Color.WHITE);
//        drawable.setAlpha(200); // Set transparency if needed

// Set the custom background drawable for the dialog window
        alertDialogBuilder.setBackground(drawable);


        alertDialogBuilder.setNegativeButton("Logout", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences p = getSharedPreferences("user", MODE_PRIVATE);
//
                new FcmServiceTaskUserGoogle(p.getString("id", "0"), "0", "user", "0").execute();

            }
        });
        alertDialogBuilder.setPositiveButton("Appeal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

//                SharedPreferences p = getSharedPreferences("provider", MODE_PRIVATE);
//
//                new LogoutTokenTaskProvider("0", p.getString("id", "0"), "provider", "0").execute();

                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://experthere.in/user_appeal")));
                finish();


            }
        });
        alertDialogBuilder.show();

    }

    private class FcmServiceTaskUserGoogle extends AsyncTask<Void, Void, SuccessMessageResponse> {
        String userIDForFcm, providerIdForFcm, typeFcm, fcmTOKEN;

        public FcmServiceTaskUserGoogle(String userIDForFcm, String providerIdForFcm, String typeFcm, String fcmTOKEN) {
            this.userIDForFcm = userIDForFcm;
            this.providerIdForFcm = providerIdForFcm;
            this.typeFcm = typeFcm;
            this.fcmTOKEN = fcmTOKEN;
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


            signOut();

            if (serviceResponse != null) {
                if (serviceResponse.isSuccess()) {

                    if (serviceResponse.getMessage().equals("Token saved successfully!") || serviceResponse.getMessage().equals("Token updated successfully!")) {


                        CustomToastPositive.create(HomeActivity.this, "Sign Out Success!");


                    } else {

                    }


                } else {

                }
            } else {
                // Handle the case when there is no responsesw
            }
        }

    }

    private void getBookmarkCount() {

        try {

            SharedPreferences userPr = getSharedPreferences("user", MODE_PRIVATE);

            new GetBookmarkCount(userPr.getString("id", "0")).execute();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


    public class GetBookmarkCount extends AsyncTask<Void, Void, BookmarkCountResponse> {

        String user_id;

        public GetBookmarkCount(String user_id) {
            this.user_id = user_id;
        }

        @Override
        protected BookmarkCountResponse doInBackground(Void... voids) {
            try {
                // Create the Retrofit instance
                ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

                // Make the API call
                Call<BookmarkCountResponse> call = apiInterface.getBookmarkCount(
                        user_id
                );

                Response<BookmarkCountResponse> response = call.execute();

                if (response.isSuccessful()) {
                    return response.body();
                } else {
                    // Handle error

                    Log.e("ksxln", "API call failed. Code: " + response.code());
                    return null;
                }
            } catch (IOException e) {

                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(BookmarkCountResponse apiResponse) {
            super.onPostExecute(apiResponse);


            if (apiResponse != null) {
                // Handle the API response

                if (apiResponse.isSuccess() && apiResponse.getMessage().equals("success")) {


                    BadgeDrawable badge = bottomNavigationView.getOrCreateBadge(R.id.bookmarkUserMenuBtn);
                    badge.setVisible(true);

                    badge.setBackgroundColor(getColor(R.color.redBtnTxtColor));
                    badge.setBadgeTextColor(getColor(R.color.badge_color));

                    badge.setBadgeGravity(BadgeDrawable.TOP_END);
                    badge.setHorizontalOffset(-20);
                    badge.setVerticalOffset(20);


                    if (apiResponse.getTotal_bookmarks() > 0) {
                        badge.setText(String.valueOf(apiResponse.getTotal_bookmarks()));
                    } else {
                        bottomNavigationView.removeBadge(R.id.bookmarkUserMenuBtn);
                    }

                }


            }
        }
    }

    boolean isGranted;

    private boolean requestAudioPermissions() {


        Dexter.withContext(getApplicationContext()).withPermission(Manifest.permission.RECORD_AUDIO).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

                isGranted = true;

            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                CustomToastNegative.create(getApplicationContext(), "Audio Permission Required!");

                isGranted = false;


                if (permissionDeniedResponse.isPermanentlyDenied()) {

                    showPermissionDeniedDialog();
                }


            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

            }
        }).check();


        return isGranted;


    }

    private void showPermissionDeniedDialog() {


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permission Denied")
                .setMessage("You denied audio record permission permanently. Please go to app settings and enable audio record permission manually.")
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

    private void saveOverlayPermission(boolean granted) {
        SharedPreferences prefs = getSharedPreferences(PREFS_SETTINGS, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(LOCK_SCREEN_PERMISSION_GRANTED, granted);
        editor.apply();
    }


    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {


        ExitBottomSheet bottomSheet = new ExitBottomSheet(true);
        bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());


    }


    private void internetStatus() {

        registerReceiver(receiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(receiver);
        unregisterReceiver(broadcastReceiver);
        unregisterReceiver(bookmarkReceiver);
        if (listener != null) {
            appUpdateManager.unregisterListener(listener);
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == MY_REQUEST_CODE) {
            if (resultCode != Activity.RESULT_OK) {
                CustomToastNegative.create(getApplicationContext(), "Update Fail!");
            }
        }


        if (requestCode == 882 && resultCode == RESULT_OK) {
            Log.d("ACTSJBHJBHJ", "onActivityResult: RESULT OK");

            if (data != null) {
                Log.d("ACTSJBHJBHJ", "onActivityResult: DATA OK");

                Bundle bundle = data.getExtras();
                if (bundle != null) {


                    Log.d("ACTSJBHJBHJ", "onActivityResult: DATA OK" + bundle.getString("address"));

                    SharedPreferences preferences = getSharedPreferences("settings", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();


                    editor.putString("address", bundle.getString("address"));
                    editor.putString("latitude", bundle.getString("lat"));
                    editor.putString("longitude", bundle.getString("long"));
                    editor.apply();


                    CustomToastPositive.create(getApplicationContext(), "Address Updated!");


                }


            }


        }

    }


    private String getLanguage() {

        return prefs.getString(LANG_PREF_KEY, "en");
    }


    private void setLanguage(String languageCode) {

        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        getResources().getConfiguration().setLocale(locale);


        getBaseContext().getResources().updateConfiguration(getResources().getConfiguration(), getBaseContext().getResources().getDisplayMetrics());


    }


    @Override
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int selectedItemID = savedInstanceState.getInt("item", R.id.homeUserMenuBtn);
        bottomNavigationView.setSelectedItemId(selectedItemID);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("item", bottomNavigationView.getSelectedItemId());
    }

    @Override
    protected void onResume() {
        super.onResume();

        // If the update is downloaded but not installed, notify the user to complete the update.
        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                popupSnackbarForCompleteUpdate();
            }
        });
        getBookmarkCount();

        new GetMaintenanceStatus("1").execute();


    }


    private boolean isFragmentActive(String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(tag);
        return fragment != null && fragment.isVisible();
    }

    private void loadFragment(Fragment fragment, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        // Check if the fragment with the given tag exists
        Fragment existingFragment = fragmentManager.findFragmentByTag(tag);

        if (existingFragment != null) {
            // If the fragment exists, show it
            transaction.show(existingFragment);
        } else {
            // If the fragment does not exist, add it
            transaction.add(R.id.fragment_container, fragment, tag);
        }

        // Hide other fragments
        List<Fragment> fragments = fragmentManager.getFragments();
        if (fragments != null) {
            for (Fragment frag : fragments) {
                if (frag != null && !frag.getTag().equals(tag)) {
                    transaction.hide(frag);
                }
            }
        }

        transaction.commit();
    }


    private void checkForAppUpdate() {
        // Create a listener to track request state updates.
        listener = state -> {
            if (state.installStatus() == InstallStatus.DOWNLOADED) {
                // After the update is downloaded, show a notification and request user confirmation to restart the app.
                popupSnackbarForCompleteUpdate();
            }
        };

        // Before starting an update, register a listener for updates.
        appUpdateManager.registerListener(listener);

        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                // Request the update.
                startAppUpdate(appUpdateInfo);
            }
        }).addOnFailureListener(e ->
                Log.d("UPDATETAG", "checkForAppUpdate: " + e.getMessage())
        );
    }

    private void startAppUpdate(AppUpdateInfo appUpdateInfo) {
        try {
            appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    AppUpdateType.IMMEDIATE,
                    HomeActivity.this,
                    MY_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
            CustomToastNegative.create(getApplicationContext(), "Update Fail " + e.getMessage());
        }
    }

    private void popupSnackbarForCompleteUpdate() {
        // Show a dialog or notification to the user to complete the update.
        CustomToastPositive.create(getApplicationContext(), "Update has been Downloaded!");
        appUpdateManager.completeUpdate();
    }


    private void signOut() {
        mGoogleSignInClient.signOut().addOnCompleteListener(HomeActivity.this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                // Sign-out was successful
                // You can now redirect the user to the sign-in screen or perform other actions

                // For example, you might want to return to the sign-in activity
                SharedPreferences preferences = getSharedPreferences("user", MODE_PRIVATE);
                SharedPreferences preferencesSettings = getSharedPreferences("settings", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                SharedPreferences.Editor editor2 = preferencesSettings.edit();
                editor.clear().apply();
                editor2.clear().apply();

                startActivity(new Intent(HomeActivity.this, SplashScreen.class));
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                CustomToastNegative.create(HomeActivity.this, "Error: " + e.getMessage());

            }
        });
    }


    private class GetMaintenanceStatus extends AsyncTask<Void, Void, Void> {

        String id;

        public GetMaintenanceStatus(String id) {
            this.id = id;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            // Perform background operation, e.g., user

            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

            Call<MaintenanceRes> call = apiInterface.get_maintenance(id);


            call.enqueue(new Callback<MaintenanceRes>() {
                @Override
                public void onResponse(Call<MaintenanceRes> call, Response<MaintenanceRes> response) {

                    if (response.body() != null) {

                        MaintenanceRes categoryResponse = response.body();

                        if (categoryResponse.isSuccess()) {

                            if (categoryResponse.getData() != null) {

                                if (categoryResponse.getData().getStatus().equals("enable")) {

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {


                                            showMaintenanceDialog(HomeActivity.this, categoryResponse.getData());

                                        }
                                    });

                                }

                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<MaintenanceRes> call, Throwable t) {


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

    private void showMaintenanceDialog(Activity activity, MaintenanceData data) {


        Intent intent = new Intent(HomeActivity.this, MaintenanceActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("txt",data.getMessage());

        intent.putExtras(bundle);
        startActivity(intent);
        finish();


    }


}