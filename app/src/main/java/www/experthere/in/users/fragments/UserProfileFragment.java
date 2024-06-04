package www.experthere.in.users.fragments;

import static android.content.Context.MODE_PRIVATE;
import static androidx.core.content.ContextCompat.getSystemService;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;
import java.util.Locale;

import io.github.vejei.cupertinoswitch.CupertinoSwitch;
import retrofit2.Call;
import retrofit2.Response;
import www.experthere.in.Common.AboutUsActivity;
import www.experthere.in.Common.BrowserActivity;
import www.experthere.in.Common.ContactUsActivity;
import www.experthere.in.Common.PrivacyPolicyActivity;
import www.experthere.in.Common.TermsActivity;
import www.experthere.in.R;
import www.experthere.in.SplashScreen;
import www.experthere.in.api.ApiClient;
import www.experthere.in.api.ApiInterface;
import www.experthere.in.helper.CatBottomSheet;
import www.experthere.in.helper.CustomToastNegative;
import www.experthere.in.helper.CustomToastPositive;
import www.experthere.in.helper.IssueBottomSheet;
import www.experthere.in.helper.NightModeHelper;
import www.experthere.in.helper.ProcessingDialog;
import www.experthere.in.helper.app.LanguageBottomSheet;
import www.experthere.in.model.SuccessMessageResponse;
import www.experthere.in.serviceProvider.NewServiceActivity;
import www.experthere.in.serviceProvider.ProviderHome;
import www.experthere.in.serviceProvider.ProviderIntroActivity;
import www.experthere.in.users.EditUserActivity;
import www.experthere.in.users.HelpUserActivity;
import www.experthere.in.users.HomeActivity;


public class UserProfileFragment extends Fragment {


    Activity activity;
    SharedPreferences preferences;

    ImageView userDp;
    ImageButton editBtn;
    TextView uName, uEmail, userProfileProfession, userProfileNumber, userProfileAddress;
    private GoogleSignInClient mGoogleSignInClient;

    private static final String PREFS_SETTINGS = "settings";
    SharedPreferences settingsPrefs;

    CupertinoSwitch darkModeSwitch;
    private boolean isUserChangedMode;

    ProcessingDialog processingDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        activity = requireActivity();
        mGoogleSignInClient = GoogleSignIn.getClient(activity, GoogleSignInOptions.DEFAULT_SIGN_IN);

        preferences = activity.getSharedPreferences("user", MODE_PRIVATE);
        settingsPrefs = activity.getSharedPreferences(PREFS_SETTINGS, MODE_PRIVATE);


        processingDialog = new ProcessingDialog(activity);


    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        darkModeSwitch = view.findViewById(R.id.themeSwitch);

        initializeViews(view);
        setDataFromPreference();


        TextView textView = view.findViewById(R.id.langTxt);

        String languageString = settingsPrefs.getString("language", "en");

        setLanguage(languageString);


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


        boolean isNightModeEnabled = NightModeHelper.isNightModeEnabled(activity);

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

                NightModeHelper.saveNightModeSetting(activity, true);
                setMode();


            }

            @Override
            public void onSwitchOff(CupertinoSwitch view) {

                NightModeHelper.saveNightModeSetting(activity, false);
                setMode();


            }
        });



        ImageView imageView = view.findViewById(R.id.logouBtn);


        view.findViewById(R.id.menuProfileBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



//                showPopUp(v);

                imageView.setVisibility(View.VISIBLE);
                imageView.setAlpha(0f);
                imageView.animate().alpha(1f).setDuration(1000).start();

                // Delay hiding the ImageView after 5 seconds
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Hide the ImageView with animation after 5 seconds
                        imageView.animate().alpha(0f).setDuration(1000).withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                imageView.setVisibility(View.INVISIBLE);
                            }
                        }).start();
                    }
                }, 5000); // 5000 milliseconds = 5 seconds



            }
        });



        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Create custom dialog
                Dialog dialog = new Dialog(activity);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.custom_delete_dialog);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                // Initialize views in the custom dialog
                TextView dialogTitle = dialog.findViewById(R.id.dialogTitle);
                TextView dialogMessage = dialog.findViewById(R.id.dialogMessage);
                Button btnCancel = dialog.findViewById(R.id.btnCancel);
                Button btnDelete = dialog.findViewById(R.id.btnDelete);

                btnDelete.setText(getString(R.string.logout));

                // Set dialog title and message
                dialogTitle.setText(getString(R.string.logout)+"?");
                dialogMessage.setText(getString(R.string.are_you_sure_you_want_to_logout));


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

                            processingDialog.show();
                            new FcmServiceTaskUserGoogle(preferences.getString("id","0"),"0","user","0").execute();


                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }


                        dialog.dismiss();
                    }
                });

                dialog.show();

            }
        });


        view.findViewById(R.id.urlTxtProvider).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(activity, BrowserActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("url","https://www.experthere.in/provider");
                bundle.putString("title","Expert Here Provider");
                intent.putExtras(bundle);

                activity.startActivity(intent);


            }
        });

        view.findViewById(R.id.selectDocBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle bundle = new Bundle();
                bundle.putBoolean("isUser", true);

                Intent intent = new Intent(activity, ProviderIntroActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtras(bundle);
                activity.startActivity(intent);


            }
        });


        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(activity, EditUserActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(intent);


            }
        });


        view.findViewById(R.id.languageBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LanguageBottomSheet bottomSheet = new LanguageBottomSheet(activity);
                bottomSheet.show(getParentFragmentManager(), bottomSheet.getTag());



            }
        });
        view.findViewById(R.id.termsBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(activity, TermsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }
        });

        view.findViewById(R.id.privacyBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(activity, PrivacyPolicyActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }
        });

        view.findViewById(R.id.shareBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //

                shareApp(activity);

            }
        });

        view.findViewById(R.id.rateBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //



                showRatingDialog();



            }
        });

        view.findViewById(R.id.helpBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //


                Intent intent = new Intent(activity, HelpUserActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);


            }
        });
        view.findViewById(R.id.contactBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //

                Intent intent = new Intent(activity, ContactUsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);


            }
        });
        view.findViewById(R.id.aboutBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //

                Intent intent = new Intent(activity, AboutUsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);


            }
        });


        return view;

    }

    private void showRatingDialog() {


            AlertDialog.Builder builder = new AlertDialog.Builder(activity);

            // Inflate the custom layout
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.rating_dialog, null);

            builder.setView(dialogView);

            // Show the dialog
            AlertDialog dialog = builder.create();

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();


        RatingBar ratingBar = dialogView.findViewById(R.id.ratingBarNew);
        LinearLayout laterBtn = dialogView.findViewById(R.id.laterBtn);
        LinearLayout submitBtn = dialogView.findViewById(R.id.submitBtn);



        laterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                dialog.dismiss();


            }
        });
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                dialog.dismiss();


                int progress = (int) ratingBar.getRating();


                if (progress>=3){


                    openPlayStore(activity);

                }else {
                    showIssueDialog();



                }


            }
        });


    }

    private void showIssueDialog() {


        IssueBottomSheet bottomSheet = new IssueBottomSheet(activity);
        bottomSheet.show(getParentFragmentManager(), bottomSheet.getTag());



    }

    public void shareApp(Activity context) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Check out this app!");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Hey! I found this amazing app. You should check it out:" + " https://play.google.com/store/apps/details?id=" + context.getPackageName());
        context.startActivity(Intent.createChooser(shareIntent, "Share App"));
    }

//    private void showPopUp(View anchorView) {
//        // Inflate the custom layout
//        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View customView = inflater.inflate(R.layout.custom_popup_layout, null);
//
//        // Create a PopupWindow with your custom layout
//        PopupWindow popupWindow = new PopupWindow(
//                customView,
//                ViewGroup.LayoutParams.WRAP_CONTENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT
//        );
//
//        // Set focusable to true so that the popup can handle touch events
//        popupWindow.setFocusable(true);
//
//        // Example: Handle button click inside the custom layout
//        LinearLayout logoutBtn = customView.findViewById(R.id.logoutBtn);
//
//        logoutBtn.setOnClickListener(new View.OnClickListener() {
//            @SuppressLint("SetTextI18n")
//            @Override
//            public void onClick(View v) {
//                // Close the popup window
//
//
//                // Create custom dialog
//                Dialog dialog = new Dialog(activity);
//                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                dialog.setContentView(R.layout.custom_delete_dialog);
//                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//
//                // Initialize views in the custom dialog
//                TextView dialogTitle = dialog.findViewById(R.id.dialogTitle);
//                TextView dialogMessage = dialog.findViewById(R.id.dialogMessage);
//                Button btnCancel = dialog.findViewById(R.id.btnCancel);
//                Button btnDelete = dialog.findViewById(R.id.btnDelete);
//
//                btnDelete.setText(getString(R.string.logout));
//
//                // Set dialog title and message
//                dialogTitle.setText(getString(R.string.logout)+"?");
//                dialogMessage.setText(getString(R.string.are_you_sure_you_want_to_logout));
//
//
//                btnCancel.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        // User clicked the Cancel button
//                        dialog.dismiss();
//                    }
//                });
//
//                btnDelete.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        // User clicked the Delete button
//
//
//                        try {
//
//                            signOut();
//
//                            popupWindow.dismiss();
//
//
//                        } catch (Exception e) {
//                            throw new RuntimeException(e);
//                        }
//
//
//                        dialog.dismiss();
//                    }
//                });
//
//                dialog.show();
//
//
//            }
//        });
//
//
//
//        // Show the popup menu anchored to the anchor view (e.g., your button with three dots)
//        popupWindow.showAsDropDown(anchorView);
//    }


    private void setDataFromPreference() {

        Glide.with(activity).load(preferences.getString("dp", "null").trim()).circleCrop().into(userDp);

        uName.setText(preferences.getString("name", "null"));
        uEmail.setText(preferences.getString("email", "null"));
        userProfileProfession.setText(preferences.getString("profession", "null"));
        userProfileNumber.setText(preferences.getString("phone", "null"));
        userProfileAddress.setText(preferences.getString("address", "null"));


        Log.d("USERDATA", "setDataFromPreference: " + preferences.getString("address", "not got").trim());


    }

    @Override
    public void onResume() {
        super.onResume();
        setDataFromPreference();
    }

    private void initializeViews(View view) {

        uEmail = view.findViewById(R.id.userEmailTxt);
        uName = view.findViewById(R.id.userNameTxt);
        userDp = view.findViewById(R.id.user_dp);
        userProfileProfession = view.findViewById(R.id.userProfileProfession);
        userProfileAddress = view.findViewById(R.id.userProfileAddress);
        userProfileNumber = view.findViewById(R.id.userProfileNumber);
        editBtn = view.findViewById(R.id.editBtnProvider);

    }


    private void signOut() {
        mGoogleSignInClient.signOut().addOnCompleteListener(activity, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                // Sign-out was successful
                // You can now redirect the user to the sign-in screen or perform other actions

                // For example, you might want to return to the sign-in activity
                SharedPreferences preferences = activity.getSharedPreferences("user", MODE_PRIVATE);
                SharedPreferences preferencesSettings = activity.getSharedPreferences("settings", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                SharedPreferences.Editor editor2 = preferencesSettings.edit();
                editor.clear().apply();
                editor2.clear().apply();


                FirebaseMessaging.getInstance().unsubscribeFromTopic("users");


                startActivity(new Intent(activity, SplashScreen.class));
                activity.finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                CustomToastNegative.create(activity,"Error: "+e.getMessage());

            }
        });
    }


    public void openPlayStore(Activity context) {


            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + context.getPackageName()));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);

    }


    private void setMode() {

        if (isUserChangedMode) {


            if (!NightModeHelper.isNightModeEnabled(activity)) {

                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);


            } else if (NightModeHelper.isNightModeEnabled(activity)) {

                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

            }
        }

    }


    private void setLanguage(String languageCode) {

        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        getResources().getConfiguration().setLocale(locale);
        activity.getBaseContext().getResources().updateConfiguration(getResources().getConfiguration(), activity.getBaseContext().getResources().getDisplayMetrics());



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

            processingDialog.dismiss();

            signOut();

            if (serviceResponse != null) {
                if (serviceResponse.isSuccess()) {

                    if (serviceResponse.getMessage().equals("Token saved successfully!") || serviceResponse.getMessage().equals("Token updated successfully!")) {


                        CustomToastPositive.create(activity,"Sign Out Success!");



                    }else {

                    }




                } else {

                }
            } else {
                // Handle the case when there is no responsesw
            }
        }

    }



}