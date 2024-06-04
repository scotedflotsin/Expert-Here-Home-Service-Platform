package www.experthere.in.serviceProvider;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
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
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

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
import com.google.android.material.navigation.NavigationView;
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

import io.github.vejei.cupertinoswitch.CupertinoSwitch;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import www.experthere.in.Common.AboutUsActivity;
import www.experthere.in.Common.ContactUsActivity;
import www.experthere.in.Common.PrivacyPolicyActivity;
import www.experthere.in.Common.TermsActivity;
import www.experthere.in.FCM.FcmBearerTokenGenerator;
import www.experthere.in.MaintenanceActivity;
import www.experthere.in.R;
import www.experthere.in.SplashScreen;
import www.experthere.in.api.ApiClient;
import www.experthere.in.api.ApiInterface;
import www.experthere.in.model.BlockRes;
import www.experthere.in.helper.CustomToastNegative;
import www.experthere.in.helper.CustomToastPositive;
import www.experthere.in.helper.EmailMaskUtil;
import www.experthere.in.helper.ExitBottomSheet;
import www.experthere.in.helper.InternetReceiver;
import www.experthere.in.helper.NightModeHelper;
import www.experthere.in.helper.ProcessingDialog;
import www.experthere.in.helper.TextMaskUtil;
import www.experthere.in.helper.app.LanguageBottomSheet;
import www.experthere.in.model.MaintenanceData;
import www.experthere.in.model.MaintenanceRes;
import www.experthere.in.model.SuccessMessageResponse;
import www.experthere.in.serviceProvider.fragments.HomeFragment;
import www.experthere.in.serviceProvider.fragments.IncomingFragment;
import www.experthere.in.serviceProvider.fragments.OutgoingFragment;
import www.experthere.in.serviceProvider.fragments.ReviewsFragment;
import www.experthere.in.serviceProvider.fragments.ServicesFragment;
import www.experthere.in.users.HomeActivity;
import www.experthere.in.users.PasswordResetActivity;

public class ProviderHome extends AppCompatActivity {
    private static final String PREFS_SETTINGS = "settings";
    private static final String LANG_PREF_KEY = "language";

    private static final int REQUEST_OVERLAY_PERMISSION = 1001;

    private static final String LOCK_SCREEN_PERMISSION_GRANTED = "lock_screen_allowed";
    private GoogleSignInClient mGoogleSignInClient;

    ImageView incomingIcon, outgoingIcon, homeIcon, serviceIcon, reviewIcon;
    TextView incomingText, outgoingText, homeText, serviceText, reviewText;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    View headerView;

    String companyName, email, dpUrl;
    TextView emailTxtView, companyNameTxtView;
    ImageView dpImage;
    ImageButton editBtn;

    BroadcastReceiver receiver;
    BottomNavigationView bottomNavigationView;
    boolean isChecked;

    SharedPreferences badgePref;

    String latestCount = null;
    SharedPreferences settingsPrefs;
    CupertinoSwitch darkModeSwitch;

    boolean isUserChangedMode;


    Fragment fragment = null;
    String tag = null;


    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    ProcessingDialog processingDialog;


    private static final int MY_REQUEST_CODE = 1110;
    private AppUpdateManager appUpdateManager;
    private InstallStateUpdatedListener listener; // Listener declared as a class-level variable


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(getResources().getColor(R.color.white, this.getTheme()));

        setContentView(R.layout.activity_provider_home);
        // Initialize AppUpdateManager
        appUpdateManager = AppUpdateManagerFactory.create(this);

        // Check for updates
        checkForAppUpdate();
        askNotificationPermission();


        FirebaseMessaging.getInstance().subscribeToTopic("providers")
                .addOnCompleteListener(task -> {
                    String msg = "Subscribed to providers topic";
                    if (!task.isSuccessful()) {
                        msg = "Subscription to providers topic failed";
                    }
                    Log.d("FCMTOPIC", msg);
                    // Optionally show a toast
                    // Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                });



        processingDialog = new ProcessingDialog(this);
        //fcm with auth
        sharedPreferences = getSharedPreferences("fcm", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        mGoogleSignInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN);

        badgePref = getSharedPreferences("badge", MODE_PRIVATE);

        settingsPrefs = getSharedPreferences(PREFS_SETTINGS, MODE_PRIVATE);

        receiver = new InternetReceiver();
        darkModeSwitch = findViewById(R.id.darkModeSwitch);

        setLanguage(getLanguage());


        internetStatus();

        initializeViews();
        initSharedPref();
        requestAudioPermissions();
        getCallHistoryCount();


        boolean overlayPermissionGranted = settingsPrefs.getBoolean(LOCK_SCREEN_PERMISSION_GRANTED, false);


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


                                CustomToastPositive.create(getApplicationContext(), "Setting Saved");

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

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.homeMenuBtn);


        fragment = new HomeFragment();
        tag = "HOME";

        if (fragment != null && !isFragmentActive(tag)) {
            loadFragment(fragment, tag);
        }



        boolean isNightModeEnabled = NightModeHelper.isNightModeEnabled(this);

        darkModeSwitch.setChecked(isNightModeEnabled);

        darkModeSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isUserChangedMode = true;

            }
        });


        darkModeSwitch.setOnStateChangeListener(new CupertinoSwitch.OnStateChangeListener() {
            @Override
            public void onChanged(CupertinoSwitch view, boolean checked) {


            }

            @Override
            public void onSwitchOn(CupertinoSwitch view) {

                NightModeHelper.saveNightModeSetting(ProviderHome.this, true);
                setMode();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        drawerLayout.closeDrawer(GravityCompat.START);

                    }
                }, 200);
            }

            @Override
            public void onSwitchOff(CupertinoSwitch view) {

                NightModeHelper.saveNightModeSetting(ProviderHome.this, false);
                setMode();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        drawerLayout.closeDrawer(GravityCompat.START);

                    }
                }, 200);


            }
        });


        findViewById(R.id.languageBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LanguageBottomSheet bottomSheet = new LanguageBottomSheet(ProviderHome.this);
                bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        drawerLayout.closeDrawer(GravityCompat.START);

                    }
                }, 300);


            }
        });


        findViewById(R.id.contactUs).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(ProviderHome.this, ContactUsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        drawerLayout.closeDrawer(GravityCompat.START);

                    }
                }, 300);


            }
        });


        findViewById(R.id.aboutUs).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(ProviderHome.this, AboutUsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        drawerLayout.closeDrawer(GravityCompat.START);

                    }
                }, 300);

            }
        });

        findViewById(R.id.privacyPolicy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(ProviderHome.this, PrivacyPolicyActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        drawerLayout.closeDrawer(GravityCompat.START);

                    }
                }, 300);

            }
        });
        findViewById(R.id.termsAndConditions).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(ProviderHome.this, TermsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);


                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        drawerLayout.closeDrawer(GravityCompat.START);

                    }
                }, 300);

            }
        });


        findViewById(R.id.catBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(ProviderHome.this, CategoriesListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        drawerLayout.closeDrawer(GravityCompat.START);

                    }
                }, 300);

            }
        });


        findViewById(R.id.changePassBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        drawerLayout.closeDrawer(GravityCompat.START);

                    }
                }, 300);


                // Create custom dialog
                Dialog dialog = new Dialog(ProviderHome.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.custom_delete_dialog);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                // Initialize views in the custom dialog
                TextView dialogTitle = dialog.findViewById(R.id.dialogTitle);
                TextView dialogMessage = dialog.findViewById(R.id.dialogMessage);
                Button btnCancel = dialog.findViewById(R.id.btnCancel);
                Button btnDelete = dialog.findViewById(R.id.btnDelete);
                btnDelete.setText("Change Password");

                // Set dialog title and message
                dialogTitle.setText("Change Password?");
                dialogMessage.setText("Are you sure you want to Go To Change Password Screen ?");


                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // User clicked the Cancel button
                        dialog.dismiss();


                    }
                });

                btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // User clicked the Delete button

                        Intent i = new Intent(ProviderHome.this, PasswordResetActivity.class);
                        Bundle b = new Bundle();
                        b.putString("email", email.trim());
                        b.putBoolean("isProvider", true);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.putExtras(b);
                        startActivity(i);


                        dialog.dismiss();
                    }
                });

                dialog.show();


            }
        });


        findViewById(R.id.logoutBtnlay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                // Create custom dialog
                Dialog dialog = new Dialog(ProviderHome.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.custom_delete_dialog);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                // Initialize views in the custom dialog
                TextView dialogTitle = dialog.findViewById(R.id.dialogTitle);
                TextView dialogMessage = dialog.findViewById(R.id.dialogMessage);
                Button btnCancel = dialog.findViewById(R.id.btnCancel);
                Button btnDelete = dialog.findViewById(R.id.btnDelete);
                btnDelete.setText("Logout");

                // Set dialog title and message
                dialogTitle.setText("Logout?");
                dialogMessage.setText("Are you sure you want to Logout ?");


                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // User clicked the Cancel button
                        dialog.dismiss();
                    }
                });

                btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // User clicked the Delete button


                        try {

                            SharedPreferences p = getSharedPreferences("provider", MODE_PRIVATE);

                            new LogoutTokenTaskProvider("0", p.getString("id", "0"), "provider", "0").execute();


                            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                            manager.cancel(9919);


                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }


                        dialog.dismiss();
                    }
                });

                dialog.show();


            }


        });


        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {


                if (item.getItemId() == R.id.incomingMenuBtn) {


                    if (latestCount != null) {

                        SharedPreferences.Editor editor = badgePref.edit();
                        editor.putInt("count", Integer.parseInt(latestCount));
                        editor.apply();

                        bottomNavigationView.removeBadge(R.id.incomingMenuBtn);

                    }


                    fragment = new IncomingFragment();
                    tag = "INCOMING";
                } else if (item.getItemId() == R.id.outgoingMenuBtn) {
                    fragment = new OutgoingFragment();
                    tag = "OUTGOING";
                } else if (item.getItemId() == R.id.homeMenuBtn) {
                    fragment = new HomeFragment();
                    tag = "HOME";
                } else if (item.getItemId() == R.id.servicesMenuBtn) {
                    fragment = new ServicesFragment();
                    tag = "SERVICE";
                } else if (item.getItemId() == R.id.reviewsMenuBtn) {
                    fragment = new ReviewsFragment();
                    tag = "REVIEWS";
                }

                if (fragment != null && !isFragmentActive(tag)) {
                    loadFragment(fragment, tag);
                }

                return true;
            }
        });


//
//        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//
//
//                if (item.getItemId() == R.id.incomingMenuBtn) {
//                    loadFragment(new IncomingFragment(), "INCOMING");
//
//
//                    if (latestCount != null) {
//
//                        SharedPreferences.Editor editor = badgePref.edit();
//                        editor.putInt("count", Integer.parseInt(latestCount));
//                        editor.apply();
//
//                        bottomNavigationView.removeBadge(R.id.incomingMenuBtn);
//
//                    }
//
//                }
//
//                if (item.getItemId() == R.id.outgoingMenuBtn) {
//                    loadFragment(new OutgoingFragment(), "OUTGOING");
//
//                }
//
//                if (item.getItemId() == R.id.homeMenuBtn) {
//
//                    loadFragment(new HomeFragment(), "HOME");
//
//                }
//
//                if (item.getItemId() == R.id.servicesMenuBtn) {
//                    loadFragment(new ServicesFragment(), "SERVICE");
//
//
//                }
//
//                if (item.getItemId() == R.id.reviewsMenuBtn) {
//
//                    loadFragment(new ReviewsFragment(), "REVIEWS");
//
//
//                }
//
//
//                return true;
//            }
//        });


        findViewById(R.id.sidebarMenuBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                hideKeyboard();


                if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {

                    drawerLayout.openDrawer(GravityCompat.START);


                }

            }
        });


        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                findViewById(R.id.editProg).setVisibility(View.VISIBLE);
                findViewById(R.id.editBtnProvider).setVisibility(View.GONE);


                Bundle bundle = new Bundle();
                bundle.putBoolean("edit", true);
                Intent intent = new Intent(ProviderHome.this, ProviderDetailsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtras(bundle);
                startActivity(intent);


                Handler handler = new Handler();
                // Delayed dismissal of the progress bar after 2 seconds
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Dismiss the progress bar


                        findViewById(R.id.editProg).setVisibility(View.GONE);
                        findViewById(R.id.editBtnProvider).setVisibility(View.VISIBLE);
                        drawerLayout.closeDrawer(GravityCompat.START);


                    }
                }, 2000); // 2000 milliseconds = 2 seconds


            }
        });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Dexter.withContext(ProviderHome.this).withPermission(Manifest.permission.POST_NOTIFICATIONS).withListener(new PermissionListener() {
                @Override
                public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {


                }

                @Override
                public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                }

                @Override
                public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

                }
            }).check();
        }


        UPDATE_TOKEN();

    }


    private void setMode() {

        if (isUserChangedMode) {


            if (!NightModeHelper.isNightModeEnabled(ProviderHome.this)) {

                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);


            } else if (NightModeHelper.isNightModeEnabled(ProviderHome.this)) {

                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

            }
        }

    }


    private void getCallHistoryCount() {

        SharedPreferences p = getSharedPreferences("provider", MODE_PRIVATE);

        new GetCallHistoryCount(p.getString("id", "0")).execute();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MY_REQUEST_CODE) {
            if (resultCode != Activity.RESULT_OK) {

                CustomToastNegative.create(getApplicationContext(), "Update Fail!");


            }
        }

    }


    private boolean hasOverlayPermission() {
        return Settings.canDrawOverlays(this);
    }

    private void saveOverlayPermission(boolean granted) {
        SharedPreferences prefs = getSharedPreferences(PREFS_SETTINGS, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(LOCK_SCREEN_PERMISSION_GRANTED, granted);
        editor.apply();
    }

    private void initSharedPref() {

        SharedPreferences preferences = getSharedPreferences("provider", MODE_PRIVATE);
        companyName = preferences.getString("company_name", "Null");
        email = preferences.getString("email", "Null");
        dpUrl = preferences.getString("logo_image", "Null");

        boolean isBlocked = preferences.getBoolean("isBlocked", false);


        new GetBlockStatus(preferences.getString("id","0")).execute();





        EmailMaskUtil.maskEmailAndSetToTextView(email, emailTxtView);
        TextMaskUtil.maskText(companyNameTxtView, companyName, 20, "..");
        Glide.with(getApplicationContext()).load(dpUrl).circleCrop().into(dpImage);


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


                                            showMaintenanceDialog(ProviderHome.this, categoryResponse.getData());

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


        Intent intent = new Intent(ProviderHome.this, MaintenanceActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("txt",data.getMessage());

        intent.putExtras(bundle);
        startActivity(intent);
        finish();
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

            Call<BlockRes> call = apiInterface.getStatusBlockedProvider(id);



            call.enqueue(new Callback<BlockRes>() {
                @Override
                public void onResponse(Call<BlockRes> call, Response<BlockRes> response) {

                    if (response.body() != null) {

                        BlockRes categoryResponse = response.body();

                        if (categoryResponse.isSuccess()) {


                            if (categoryResponse.getIs_blocked()==1){

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {


                                        showBlockDialog(ProviderHome.this);

                                    }
                                });

                            }else {


                            }



                        }else {

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

                SharedPreferences p = getSharedPreferences("provider", MODE_PRIVATE);

                new LogoutTokenTaskProvider("0", p.getString("id", "0"), "provider", "0").execute();



            }
        });

        alertDialogBuilder.setPositiveButton("Appeal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

//                SharedPreferences p = getSharedPreferences("provider", MODE_PRIVATE);
//
//                new LogoutTokenTaskProvider("0", p.getString("id", "0"), "provider", "0").execute();

                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://experthere.in/provider_appeal")));
                finish();



            }
        });
        alertDialogBuilder.show();

    }

    private void initializeViews() {

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
//        headerView = navigationView.getHeaderView(0); // 0 represents the first header in the navigation view

        companyNameTxtView = findViewById(R.id.companyNameTxt);
        emailTxtView = findViewById(R.id.companyEmailTxt);
        dpImage = findViewById(R.id.imageView3);
        editBtn = findViewById(R.id.editBtnProvider);


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

//    private void loadFragment(Fragment fragment, String tag) {
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction transaction = fragmentManager.beginTransaction();
//
//
//        // Check if the fragment with the given tag exists
//        Fragment existingFragment = fragmentManager.findFragmentByTag(tag);
//
//        if (existingFragment != null) {
//            // If the fragment exists, reuse it
//            transaction.replace(R.id.fragment_container, existingFragment, tag);
//        } else {
//            // If the fragment does not exist, create a new instance
//            transaction.replace(R.id.fragment_container, fragment, tag);
//        }
//
//
//        transaction.commit();
//    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {


        ExitBottomSheet bottomSheet = new ExitBottomSheet(true);
        bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());


    }


    private void signOutGoogle() {
        mGoogleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                // Sign-out was successful
                // You can now redirect the user to the sign-in screen or perform other actions

                // For example, you might want to return to the sign-in activity
                SharedPreferences preferences = getSharedPreferences("provider", MODE_PRIVATE);

                SharedPreferences.Editor editor = preferences.edit();
                SharedPreferences.Editor editor2 = settingsPrefs.edit();
                SharedPreferences.Editor editor3 = badgePref.edit();
                editor.clear().apply();
                editor2.clear().apply();
                editor3.clear().apply();


                String ACTION_DISMISS_NOTIFICATION = "www.experthere.in.helper.app.ACTION_FINISH_ACTIVITY";

                Intent intent = new Intent();
                intent.setAction(ACTION_DISMISS_NOTIFICATION);
                sendBroadcast(intent);

                FirebaseMessaging.getInstance().unsubscribeFromTopic("providers");



                startActivity(new Intent(ProviderHome.this, SplashScreen.class));
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        CustomToastNegative.create(getApplicationContext(), "Fail To SignOut: " + e.getMessage());

                    }
                });

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.d("AJKBKSBSBJHBS", "onPause: ");
        new GetMaintenanceStatus("1").execute();

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

//        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
//            drawerLayout.closeDrawer(GravityCompat.START);
//        }


//        bottomNavigationView.setSelectedItemId(R.id.homeMenuBtn);


        Bundle misCallbundle = getIntent().getExtras();


        if (misCallbundle != null) {

            if (misCallbundle.getBoolean("incomming_call")) {

                bottomNavigationView.setSelectedItemId(R.id.incomingMenuBtn);
                loadFragment(new IncomingFragment(), "INCOMING");


                fragment = new IncomingFragment();
                tag = "INCOMING";

                if (fragment != null && !isFragmentActive(tag)) {
                    loadFragment(fragment, tag);
                }


                if (latestCount != null) {

                    SharedPreferences.Editor editor = badgePref.edit();
                    editor.putInt("count", Integer.parseInt(latestCount));
                    editor.apply();

                    bottomNavigationView.removeBadge(R.id.incomingMenuBtn);

                }

            } else if (misCallbundle.getBoolean("review")) {


                fragment = new ReviewsFragment();
                tag = "REVIEWS";

                if (fragment != null && !isFragmentActive(tag)) {
                    loadFragment(fragment, tag);
                }
                bottomNavigationView.setSelectedItemId(R.id.reviewsMenuBtn);


            } else {
                fragment = new HomeFragment();
                tag = "HOME";

                if (fragment != null && !isFragmentActive(tag)) {
                    loadFragment(fragment, tag);
                }
                bottomNavigationView.setSelectedItemId(R.id.homeMenuBtn);

            }

        } else {


//            fragment = new HomeFragment();
//            tag = "HOME";
//
//            if (fragment != null && !isFragmentActive(tag)) {
//                loadFragment(fragment, tag);
//            }
//            bottomNavigationView.setSelectedItemId(R.id.homeMenuBtn);


            Log.d("NSNSL", " HIT SUCCESS ");

        }

    }


    @Override
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int selectedItemID = savedInstanceState.getInt("item", R.id.homeMenuBtn);
        bottomNavigationView.setSelectedItemId(selectedItemID);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("item", bottomNavigationView.getSelectedItemId());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (listener != null) {
            appUpdateManager.unregisterListener(listener);
        }

        appUpdateManager.unregisterListener(listener);

    }

    private void internetStatus() {

        registerReceiver(receiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
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


                CustomToastNegative.create(getApplicationContext(), "Audio Recording Permission Required For Calling!");


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


    private class GetCallHistoryCount extends AsyncTask<Void, Void, SuccessMessageResponse> {


        String providerId;

        public GetCallHistoryCount(String providerId) {
            this.providerId = providerId;
        }

        @Override
        protected SuccessMessageResponse doInBackground(Void... voids) {
            // Create Retrofit instance
            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);


            // Make API call
            Call<SuccessMessageResponse> call = apiInterface.getCallHistoryCount(String.valueOf(providerId));

            try {
                // Execute the call synchronously
                Response<SuccessMessageResponse> response = call.execute();

                if (response.isSuccessful()) {
                    return response.body();
                } else {
                    // Handle unsuccessful response
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(SuccessMessageResponse apiResponse) {
            super.onPostExecute(apiResponse);

            Log.d("KJSGJSGSFJFS", "RESPONSE Geting");

            if (apiResponse != null) {
                // Handle the API response
                Log.d("KJSGJSGSFJFS", "RESPONSE Not Null" + apiResponse.isSuccess());
                Log.d("KJSGJSGSFJFS", "RESPONSE Not Null" + apiResponse.getMessage());


                if (apiResponse.isSuccess()) {
                    Log.d("vhkjhvj", "count " + apiResponse.getMessage());
                    Log.d("vhkjhvj", "count " + badgePref.getInt("count", 0));

                    int countFromServer = Integer.parseInt(apiResponse.getMessage());
                    int savedBadge = badgePref.getInt("count", -1);

                    if (savedBadge != -1) {
                        if (countFromServer > savedBadge) {
                            // create badge

                            BadgeDrawable badge = bottomNavigationView.getOrCreateBadge(R.id.incomingMenuBtn);
                            badge.setVisible(true);

                            badge.setBackgroundColor(getColor(R.color.redBtnTxtColor));
                            badge.setBadgeTextColor(getColor(R.color.badge_color));

                            badge.setBadgeGravity(BadgeDrawable.TOP_END);
                            badge.setHorizontalOffset(-20);
                            badge.setVerticalOffset(20);

                            int num = countFromServer - badgePref.getInt("count", 0);

                            if (num >= 100) {
                                badge.setText("99+");

                            } else {

                                badge.setText(String.valueOf(num));
                            }
                            latestCount = String.valueOf(countFromServer);


                        }
                    }
                }

            } else {

                Log.d("PROHOME", "Error 1 SERVER ERROE");

            }


        }
    }


    private String getLanguage() {


        TextView textView = findViewById(R.id.languageTxt);

        String languageString = settingsPrefs.getString(LANG_PREF_KEY, "en");

        if (languageString.equals("en")) {
            textView.setText("English");
        } else if (languageString.equals("hi")) {
            textView.setText("हिन्दी");
        } else if (languageString.equals("bn")) {
            textView.setText("বাংলা");
        } else if (languageString.equals("te")) {
            textView.setText("తెలుగు");
        } else if (languageString.equals("mr")) {
            textView.setText("मराठी");
        } else if (languageString.equals("ta")) {
            textView.setText("தமிழ்");
        } else if (languageString.equals("gu")) {
            textView.setText("ગુજરાતી");
        } else if (languageString.equals("kn")) {
            textView.setText("ಕನ್ನಡ");
        } else if (languageString.equals("ml")) {
            textView.setText("മലയാളം");
        } else {
            // Default to English if the language preference is not recognized
            textView.setText("English");
        }

        return languageString;
    }


    private void setLanguage(String languageCode) {

        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        getResources().getConfiguration().setLocale(locale);
        getBaseContext().getResources().updateConfiguration(getResources().getConfiguration(), getBaseContext().getResources().getDisplayMetrics());


    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    private void UPDATE_TOKEN() {

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

                        String fcmDeviceToken = sharedPreferences.getString("device_token", "0");

                        try {

                            SharedPreferences p = getSharedPreferences("provider", MODE_PRIVATE);

                            new FcmServiceTaskProviderGoogle("0", p.getString("id", "0"), "provider", fcmDeviceToken).execute();


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
                        CustomToastNegative.create(getApplicationContext(), "Token Generation Fail!");

                    }
                });
            }
        });


    }


    private class FcmServiceTaskProviderGoogle extends AsyncTask<Void, Void, SuccessMessageResponse> {
        String userIDForFcm, providerIdForFcm, typeFcm, fcmTOKEN;

        public FcmServiceTaskProviderGoogle(String userIDForFcm, String providerIdForFcm, String typeFcm, String fcmTOKEN) {
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


            if (serviceResponse != null) {
                if (serviceResponse.isSuccess()) {

                    if (serviceResponse.getMessage().equals("Token saved successfully!") || serviceResponse.getMessage().equals("Token updated successfully!")) {


                        Log.d("WLNLWKJBW", "TOKEN UPDATED PROVIDER HOME");


                    } else {
                        Log.d("PROHOME", "Error 2 " + serviceResponse.getMessage());

                    }


                } else {
                    Log.d("PROHOME", "Error 3 " + serviceResponse.getMessage());

                }
            } else {
                // Handle the case when there is no responsesw
            }
        }

    }


    private class LogoutTokenTaskProvider extends AsyncTask<Void, Void, SuccessMessageResponse> {
        String userIDForFcm, providerIdForFcm, typeFcm, fcmTOKEN;

        public LogoutTokenTaskProvider(String userIDForFcm, String providerIdForFcm, String typeFcm, String fcmTOKEN) {
            this.userIDForFcm = userIDForFcm;
            this.providerIdForFcm = providerIdForFcm;
            this.typeFcm = typeFcm;
            this.fcmTOKEN = fcmTOKEN;
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();


            processingDialog.show();

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

            processingDialog.dismiss();


            if (serviceResponse != null) {
                if (serviceResponse.isSuccess()) {

                    if (serviceResponse.getMessage().equals("Token saved successfully!") || serviceResponse.getMessage().equals("Token updated successfully!")) {


                        signOutGoogle();


                    } else {

                        CustomToastNegative.create(getApplicationContext(), "Error: " + serviceResponse.getMessage());


                        signOutGoogle();

                    }


                } else {

                    signOutGoogle();

                    CustomToastNegative.create(getApplicationContext(), "  " + serviceResponse.getMessage());
                }
            } else {
                // Handle the case when there is no responsesw
                signOutGoogle();

            }
        }

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
                    ProviderHome.this,
                    MY_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();

            CustomToastNegative.create(getApplicationContext(), "Fail: " + e.getMessage());


        }
    }

    private void popupSnackbarForCompleteUpdate() {
        // Show a dialog or notification to the user to complete the update.
        CustomToastPositive.create(getApplicationContext(), "An update has just been downloaded.");

        appUpdateManager.completeUpdate();
    }

    private void askNotificationPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Dexter.withContext(ProviderHome.this).withPermission(Manifest.permission.POST_NOTIFICATIONS).withListener(new PermissionListener() {
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
}