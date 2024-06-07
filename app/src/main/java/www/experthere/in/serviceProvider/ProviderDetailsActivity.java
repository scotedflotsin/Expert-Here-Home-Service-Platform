package www.experthere.in.serviceProvider;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.dhaval2404.colorpicker.MaterialColorPickerDialog;
import com.github.dhaval2404.colorpicker.listener.ColorListener;
import com.github.dhaval2404.colorpicker.model.ColorShape;
import com.github.dhaval2404.colorpicker.model.ColorSwatch;
import com.google.android.material.textfield.TextInputEditText;

import com.hbb20.CountryCodePicker;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.richeditor.RichEditor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
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
import www.experthere.in.helper.EmailValidator;
import www.experthere.in.helper.ExitBottomSheet;
import www.experthere.in.helper.ImageCompressActivity;
import www.experthere.in.helper.ImagePickerBottomSheet;
import www.experthere.in.helper.InternetReceiver;
import www.experthere.in.model.EmailValidationResponse;
import www.experthere.in.model.ResponseProvider;
import www.experthere.in.model.SuccessMessageResponse;
import www.experthere.in.users.BottomSheetFragment;
import www.experthere.in.users.OTPGenerator;
import www.experthere.in.users.OnSubmitListener;
import www.experthere.in.users.SendOtpTask;

public class ProviderDetailsActivity extends AppCompatActivity implements OnSubmitListener {

    String selectedID = "none";
    String selectedAccount = "none";
    TextView selectedIdTxt, accountTypeTxt;
    boolean isIdSelected, isAccountSelected, isIdImageSelected, isLogoSelected, isBannerSelected = false;

    TextView stepsCountText;
    TextView titleTxt;
    boolean emailVerified = false;

    TextInputEditText nameEt, emailEt, phoneEt, companyNameEt,
            visitingChargesEt, advanceBookingDaysEt, membersEt,
            cityEt, latEt, longEt, addressEt, otpEt, passEt, confifrmPassEt;


    private RichEditor richEditor;
    private ImageView boldBtn, colorBtn;

    String tempMail = "null";

    String email, name;
    long duration = 90000;
    long interval = 1000;
    TextView otpTimerTxt, resendTxt, emailForOtp;

    private CountDownTimer countDownTimer;

    String sentOtp;

    String lat, longitude, address, city = "";
    String HTML_CONTENT;

    private ProgressDialog progressDialog;


    String docUri, logoUri, bannerUri;

    TextView currencyTxt;
    CountryCodePicker ccp;

    boolean isEdit, isUser = false;
    String id_;

    boolean isUpdate;

    BroadcastReceiver receiver;


    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    String fcmDeviceToken = "0";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_details);

        //fcm with auth
        sharedPreferences = getSharedPreferences("fcm", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        fcmDeviceToken = sharedPreferences.getString("device_token", "0");


        initializeVariables();
        setUpHtmlEditor();
        ccp.registerCarrierNumberEditText(phoneEt);

        initBundle();

        receiver = new InternetReceiver();
        internetStatus();

        findViewById(R.id.currencyTxt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showCurrencyDialog();

            }
        });

        findViewById(R.id.backProvider).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isEdit) {


                    ExitBottomSheet bottomSheet = new ExitBottomSheet(true);
                    bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());

                    Log.d("CHECKECIT", "onClick: " + " IS EDIT");


                } else {

                    Log.d("CHECKECIT", "onClick: " + " IS EDIT 1");


                    if (isUser) {
                        Log.d("CHECKECIT", "onClick: " + " IS EDIT 2");

                        ExitBottomSheet bottomSheet = new ExitBottomSheet(true);
                        bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
                    } else {

                        Log.d("CHECKECIT", "onClick: " + " IS EDIT 3");

                        ExitBottomSheet bottomSheet = new ExitBottomSheet(false);
                        bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
                    }


                }
            }
        });
        findViewById(R.id.selectIdBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showCustomDialog();
            }
        });

        findViewById(R.id.selectAccountTypeBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAccountTypeDialog();
            }
        });

        findViewById(R.id.selectDocBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                    Dexter.withContext(getApplicationContext()).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE).withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {

                            if (multiplePermissionsReport.areAllPermissionsGranted()) {

                                selectDoc();

                            }

                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {


                            CustomToastNegative.create(getApplicationContext(),"Permission Required!");
                            permissionToken.continuePermissionRequest();
                        }
                    }).check();

                } else {
                    Dexter.withContext(getApplicationContext()).withPermissions(
                            Manifest.permission.READ_MEDIA_IMAGES).withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {

                            if (multiplePermissionsReport.areAllPermissionsGranted()) {

                                selectDoc();

                            }

                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {

                            CustomToastNegative.create(getApplicationContext(),"Permission Required!");
                            permissionToken.continuePermissionRequest();

                        }
                    }).check();

                }

            }
        });


        if (isEdit) {
            titleTxt.setText("Your Personal details");
            stepsCountText.setText("Step 1 Of 5");


        } else {

            stepsCountText.setText("Step 1 Of 6");

            titleTxt.setText("Fill required details");

        }

        findViewById(R.id.next1Btn).setVisibility(View.VISIBLE);
        findViewById(R.id.lay1).setVisibility(View.VISIBLE);
        findViewById(R.id.lay3).setVisibility(View.GONE);
        findViewById(R.id.next3Layout).setVisibility(View.GONE);
        findViewById(R.id.lay2).setVisibility(View.GONE);
        findViewById(R.id.next2Btn).setVisibility(View.GONE);
        findViewById(R.id.lay4).setVisibility(View.GONE);
        findViewById(R.id.next4Layout).setVisibility(View.GONE);
        findViewById(R.id.next5Layout).setVisibility(View.GONE);
        findViewById(R.id.lay5).setVisibility(View.GONE);

        findViewById(R.id.lay6).setVisibility(View.GONE);
        findViewById(R.id.next6Layout).setVisibility(View.GONE);


        findViewById(R.id.next1Btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (isEdit) {

                    emailVerified = true;


                    stepsCountText.setText("Step 2 Of 5");
                    titleTxt.setText("Your Company details");


                    findViewById(R.id.lay3).setVisibility(View.VISIBLE);
                    findViewById(R.id.next3Layout).setVisibility(View.VISIBLE);

                    findViewById(R.id.lay2).setVisibility(View.GONE);
                    findViewById(R.id.next2Btn).setVisibility(View.GONE);
                    findViewById(R.id.next1Btn).setVisibility(View.GONE);
                    findViewById(R.id.lay1).setVisibility(View.GONE);
                    findViewById(R.id.next4Layout).setVisibility(View.GONE);
                    findViewById(R.id.lay4).setVisibility(View.GONE);
                    findViewById(R.id.next5Layout).setVisibility(View.GONE);
                    findViewById(R.id.lay5).setVisibility(View.GONE);
                    findViewById(R.id.lay6).setVisibility(View.GONE);
                    findViewById(R.id.next6Layout).setVisibility(View.GONE);


                } else {


                    if (!tempMail.matches(emailEt.getText().toString().toLowerCase())) {

                        emailVerified = false;
                        otpEt.setText("");
                    }

                    if (!nameEt.getText().toString().isEmpty() && !emailEt.getText().toString().isEmpty()
                            && !phoneEt.getText().toString().isEmpty() &&
                            EmailValidator.isValidEmail(emailEt.getText().toString()) &&
                            isIdSelected && isIdImageSelected) {


                        // all good
                        if (emailVerified) {


                            if (isEdit) {
                                stepsCountText.setText("Step 2 Of 5");
                                titleTxt.setText("Your Company details");


                            } else {
                                stepsCountText.setText("Step 3 Of 6");

                                titleTxt.setText("Fill required details");

                            }

                            findViewById(R.id.lay3).setVisibility(View.VISIBLE);
                            findViewById(R.id.next3Layout).setVisibility(View.VISIBLE);

                            findViewById(R.id.lay2).setVisibility(View.GONE);
                            findViewById(R.id.next2Btn).setVisibility(View.GONE);
                            findViewById(R.id.next1Btn).setVisibility(View.GONE);
                            findViewById(R.id.lay1).setVisibility(View.GONE);
                            findViewById(R.id.next4Layout).setVisibility(View.GONE);
                            findViewById(R.id.lay4).setVisibility(View.GONE);
                            findViewById(R.id.next5Layout).setVisibility(View.GONE);
                            findViewById(R.id.lay5).setVisibility(View.GONE);
                            findViewById(R.id.lay6).setVisibility(View.GONE);
                            findViewById(R.id.next6Layout).setVisibility(View.GONE);

                        } else {


                            if (!emailEt.getText().toString().isEmpty()) {
                                progressDialog = new ProgressDialog(ProviderDetailsActivity.this);
                                progressDialog.setMessage("Processing...");
                                progressDialog.setCancelable(false);
                                progressDialog.show();

                                new CheckEmailTask().execute(emailEt.getText().toString().toLowerCase());


                            } else {
                                CustomToastNegative.create(getApplicationContext(),"Enter Email!");
                            }


                        }

                    } else {

                        if (!isIdSelected) {
                            CustomToastNegative.create(getApplicationContext(),"Select ID!");

                        }
                        if (!isIdImageSelected) {

                            CustomToastNegative.create(getApplicationContext(),"Upload Id Image!");

                        }

                        if (nameEt.getText().toString().isEmpty() || emailEt.getText().toString().isEmpty()
                                || phoneEt.getText().toString().isEmpty()) {

                            CustomToastNegative.create(getApplicationContext(),"Fill All Details!");
                        }

                        if (!EmailValidator.isValidEmail(emailEt.getText().toString())) {

                            CustomToastNegative.create(getApplicationContext(),"Email Not Valid!");
                        }
                    }


                }
            }
        });


        findViewById(R.id.next2Btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!otpEt.getText().toString().isEmpty()) {
                    if (sentOtp.matches(otpEt.getText().toString().trim())) {


                        CustomToastPositive.create(getApplicationContext(),"Verification Complete!");

                        emailVerified = true;


                        stepsCountText.setText("Step 3 Of 6");


                        if (isEdit) {
                            titleTxt.setText("Other details");


                        } else {

                            titleTxt.setText("Fill required details");

                        }

                        findViewById(R.id.lay3).setVisibility(View.VISIBLE);
                        findViewById(R.id.next3Layout).setVisibility(View.VISIBLE);

                        findViewById(R.id.lay2).setVisibility(View.GONE);
                        findViewById(R.id.next2Btn).setVisibility(View.GONE);
                        findViewById(R.id.next1Btn).setVisibility(View.GONE);
                        findViewById(R.id.lay1).setVisibility(View.GONE);
                        findViewById(R.id.next4Layout).setVisibility(View.GONE);
                        findViewById(R.id.lay4).setVisibility(View.GONE);
                        findViewById(R.id.next5Layout).setVisibility(View.GONE);
                        findViewById(R.id.lay5).setVisibility(View.GONE);
                        findViewById(R.id.lay6).setVisibility(View.GONE);
                        findViewById(R.id.next6Layout).setVisibility(View.GONE);

                    } else {

                        CustomToastNegative.create(getApplicationContext(),"Wrong Otp!");
                    }
                } else {
                    otpEt.setError("Enter Otp");
                }
            }


        });


        findViewById(R.id.next3Btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!companyNameEt.getText().toString().isEmpty() && !visitingChargesEt.getText().toString().isEmpty()
                        && !advanceBookingDaysEt.getText().toString().isEmpty() && !membersEt.getText().toString().isEmpty()
                        && isAccountSelected && isLogoSelected && isBannerSelected) {


                    if (isEdit) {

                        stepsCountText.setText("Step 3 Of 5");
                        titleTxt.setText("About your company");

                    } else {

                        stepsCountText.setText("Step 4 Of 6");
                        titleTxt.setText("About your company");
                    }

                    findViewById(R.id.next4Layout).setVisibility(View.VISIBLE);
                    findViewById(R.id.lay4).setVisibility(View.VISIBLE);


                    findViewById(R.id.lay3).setVisibility(View.GONE);
                    findViewById(R.id.next3Layout).setVisibility(View.GONE);

                    findViewById(R.id.lay2).setVisibility(View.GONE);
                    findViewById(R.id.next2Btn).setVisibility(View.GONE);
                    findViewById(R.id.next1Btn).setVisibility(View.GONE);
                    findViewById(R.id.lay1).setVisibility(View.GONE);
                    findViewById(R.id.next5Layout).setVisibility(View.GONE);
                    findViewById(R.id.lay5).setVisibility(View.GONE);
                    findViewById(R.id.lay6).setVisibility(View.GONE);
                    findViewById(R.id.next6Layout).setVisibility(View.GONE);

                } else {
                    CustomToastNegative.create(getApplicationContext(),"Fill All Details!");

                }
            }
        });


        findViewById(R.id.next4Btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                HTML_CONTENT = richEditor.getHtml();
                String plainText = android.text.Html.fromHtml(HTML_CONTENT != null ? HTML_CONTENT : "").toString().trim();

                try {
                    if (!TextUtils.isEmpty(HTML_CONTENT)) {

                        // Check if the content contains only whitespace characters
                        if (!plainText.matches("\\s*")) {


                            if (isEdit) {
                                titleTxt.setText("Your Service location");
                                stepsCountText.setText("Step 4 Of 5");


                            } else {

                                titleTxt.setText("Set service location");
                                stepsCountText.setText("Step 5 Of 6");

                            }


                            findViewById(R.id.next5Layout).setVisibility(View.VISIBLE);
                            findViewById(R.id.lay5).setVisibility(View.VISIBLE);

                            findViewById(R.id.next4Layout).setVisibility(View.GONE);
                            findViewById(R.id.lay4).setVisibility(View.GONE);

                            findViewById(R.id.lay3).setVisibility(View.GONE);
                            findViewById(R.id.next3Layout).setVisibility(View.GONE);

                            findViewById(R.id.lay2).setVisibility(View.GONE);
                            findViewById(R.id.next2Btn).setVisibility(View.GONE);
                            findViewById(R.id.next1Btn).setVisibility(View.GONE);
                            findViewById(R.id.lay1).setVisibility(View.GONE);
                            findViewById(R.id.lay6).setVisibility(View.GONE);
                            findViewById(R.id.next6Layout).setVisibility(View.GONE);
                        } else {
                            CustomToastNegative.create(getApplicationContext(),"Please Write Description!");
                        }

                    } else {
                        CustomToastNegative.create(getApplicationContext(),"Please Write Description!");
                    }
                } catch (Exception e) {
                    Log.d("ProDETAILSACTI", "Error 1 " + e.getMessage());


                }
            }
        });


        findViewById(R.id.prev5Btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isEdit) {

                    stepsCountText.setText("Step 3 Of 5");
                    titleTxt.setText("About your company ");
                } else {
                    stepsCountText.setText("Step 4 Of 6");
                    titleTxt.setText("About your company ");
                }

                findViewById(R.id.next4Layout).setVisibility(View.VISIBLE);
                findViewById(R.id.lay4).setVisibility(View.VISIBLE);


                findViewById(R.id.lay3).setVisibility(View.GONE);
                findViewById(R.id.next3Layout).setVisibility(View.GONE);

                findViewById(R.id.lay2).setVisibility(View.GONE);
                findViewById(R.id.next2Btn).setVisibility(View.GONE);
                findViewById(R.id.next1Btn).setVisibility(View.GONE);
                findViewById(R.id.lay1).setVisibility(View.GONE);
                findViewById(R.id.next5Layout).setVisibility(View.GONE);
                findViewById(R.id.lay5).setVisibility(View.GONE);
                findViewById(R.id.lay6).setVisibility(View.GONE);
                findViewById(R.id.next6Layout).setVisibility(View.GONE);


            }
        });

        findViewById(R.id.prev4Btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (isEdit) {

                    stepsCountText.setText("Step 2 Of 5");
                    titleTxt.setText("Your Company details");
                } else {


                    stepsCountText.setText("Step 3 Of 6");
                    titleTxt.setText("Fill required details");
                }


                findViewById(R.id.lay3).setVisibility(View.VISIBLE);
                findViewById(R.id.next3Layout).setVisibility(View.VISIBLE);

                findViewById(R.id.lay2).setVisibility(View.GONE);
                findViewById(R.id.next2Btn).setVisibility(View.GONE);
                findViewById(R.id.next1Btn).setVisibility(View.GONE);
                findViewById(R.id.lay1).setVisibility(View.GONE);
                findViewById(R.id.next4Layout).setVisibility(View.GONE);
                findViewById(R.id.lay4).setVisibility(View.GONE);
                findViewById(R.id.next5Layout).setVisibility(View.GONE);
                findViewById(R.id.lay5).setVisibility(View.GONE);
                findViewById(R.id.lay6).setVisibility(View.GONE);
                findViewById(R.id.next6Layout).setVisibility(View.GONE);


            }
        });

        findViewById(R.id.prev3Btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (emailVerified) {

                    if (isEdit) {

                        stepsCountText.setText("Step 1 Of 5");
                        titleTxt.setText("Your Personal details");

                    } else {

                        stepsCountText.setText("Step 1 Of 6");
                        titleTxt.setText("Fill required details");
                    }


                    findViewById(R.id.next1Btn).setVisibility(View.VISIBLE);
                    findViewById(R.id.lay1).setVisibility(View.VISIBLE);


                    findViewById(R.id.lay3).setVisibility(View.GONE);
                    findViewById(R.id.next3Layout).setVisibility(View.GONE);

                    findViewById(R.id.lay2).setVisibility(View.GONE);
                    findViewById(R.id.next2Btn).setVisibility(View.GONE);

                    findViewById(R.id.next4Layout).setVisibility(View.GONE);
                    findViewById(R.id.lay4).setVisibility(View.GONE);

                    findViewById(R.id.next5Layout).setVisibility(View.GONE);
                    findViewById(R.id.lay5).setVisibility(View.GONE);
                    findViewById(R.id.lay6).setVisibility(View.GONE);
                    findViewById(R.id.next6Layout).setVisibility(View.GONE);


                } else {

                    stepsCountText.setText("Step 2 Of 6");
                    titleTxt.setText("Provider Email Verification");


                    findViewById(R.id.lay2).setVisibility(View.VISIBLE);
                    findViewById(R.id.next2Btn).setVisibility(View.VISIBLE);

                    findViewById(R.id.next1Btn).setVisibility(View.GONE);
                    findViewById(R.id.lay1).setVisibility(View.GONE);


                    findViewById(R.id.lay3).setVisibility(View.GONE);
                    findViewById(R.id.next3Layout).setVisibility(View.GONE);


                    findViewById(R.id.next4Layout).setVisibility(View.GONE);
                    findViewById(R.id.lay4).setVisibility(View.GONE);

                    findViewById(R.id.next5Layout).setVisibility(View.GONE);
                    findViewById(R.id.lay5).setVisibility(View.GONE);
                    findViewById(R.id.lay6).setVisibility(View.GONE);
                    findViewById(R.id.next6Layout).setVisibility(View.GONE);
                }
            }
        });


        findViewById(R.id.uploadLogo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    Dexter.withContext(getApplicationContext()).withPermissions(
                            Manifest.permission.READ_MEDIA_IMAGES).withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {

                            if (multiplePermissionsReport.areAllPermissionsGranted()) {

                                selectLogo();

                            }

                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {

                            CustomToastNegative.create(getApplicationContext(),"Permission Required!");
                        }
                    }).check();
                } else {

                    Dexter.withContext(getApplicationContext()).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE).withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {

                            if (multiplePermissionsReport.areAllPermissionsGranted()) {

                                selectLogo();

                            }

                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                            CustomToastNegative.create(getApplicationContext(),"Permission Required!");

                        }
                    }).check();

                }
            }
        });

        findViewById(R.id.uploadBanner).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    Dexter.withContext(getApplicationContext()).withPermissions(
                            Manifest.permission.READ_MEDIA_IMAGES).withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {

                            if (multiplePermissionsReport.areAllPermissionsGranted()) {

                                selectBanner();

                            }

                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {

                            CustomToastNegative.create(getApplicationContext(),"Permission Required!");
                        }
                    }).check();
                } else {

                    Dexter.withContext(getApplicationContext()).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE).withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {

                            if (multiplePermissionsReport.areAllPermissionsGranted()) {

                                selectBanner();

                            }

                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                            CustomToastNegative.create(getApplicationContext(),"Permission Required!");

                        }
                    }).check();

                }
            }
        });


        findViewById(R.id.complete5Btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (isEdit) {

                    if (!emailEt.getText().toString().isEmpty() && EmailValidator.isValidEmail(emailEt.getText().toString())) {


                        if (isUpdate) {

                            progressDialog = new ProgressDialog(ProviderDetailsActivity.this);
                            progressDialog.setMessage("Updating Account...");
                            progressDialog.setCancelable(false);
                            progressDialog.show();
                            new UpdateDataTask().execute();
                        } else {


                        }
                    }


                } else {

                    stepsCountText.setText("Step 6 Of 6");
                    titleTxt.setText("Enter New Password");

                    findViewById(R.id.lay6).setVisibility(View.VISIBLE);
                    findViewById(R.id.next6Layout).setVisibility(View.VISIBLE);


                    findViewById(R.id.lay2).setVisibility(View.GONE);
                    findViewById(R.id.next2Btn).setVisibility(View.GONE);

                    findViewById(R.id.next1Btn).setVisibility(View.GONE);
                    findViewById(R.id.lay1).setVisibility(View.GONE);


                    findViewById(R.id.lay3).setVisibility(View.GONE);
                    findViewById(R.id.next3Layout).setVisibility(View.GONE);


                    findViewById(R.id.next4Layout).setVisibility(View.GONE);
                    findViewById(R.id.lay4).setVisibility(View.GONE);

                    findViewById(R.id.next5Layout).setVisibility(View.GONE);
                    findViewById(R.id.lay5).setVisibility(View.GONE);


                    // check email response....
                }

            }
        });

        findViewById(R.id.prev6Btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                stepsCountText.setText("Step 5 Of 6");
                titleTxt.setText("Set service location");

                findViewById(R.id.lay6).setVisibility(View.GONE);
                findViewById(R.id.next6Layout).setVisibility(View.GONE);


                findViewById(R.id.lay2).setVisibility(View.GONE);
                findViewById(R.id.next2Btn).setVisibility(View.GONE);

                findViewById(R.id.next1Btn).setVisibility(View.GONE);
                findViewById(R.id.lay1).setVisibility(View.GONE);


                findViewById(R.id.lay3).setVisibility(View.GONE);
                findViewById(R.id.next3Layout).setVisibility(View.GONE);


                findViewById(R.id.next4Layout).setVisibility(View.GONE);
                findViewById(R.id.lay4).setVisibility(View.GONE);

                findViewById(R.id.next5Layout).setVisibility(View.VISIBLE);
                findViewById(R.id.lay5).setVisibility(View.VISIBLE);


            }
        });
        findViewById(R.id.complete6Btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                // final
                finishSetUp();


            }
        });


        findViewById(R.id.selectLocationBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(ProviderDetailsActivity.this, LocationSelectActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString("key", "RegisterProvider");
                intent.putExtras(bundle);
                startActivityForResult(intent, 882);

            }
        });

    }

    private void initBundle() {

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {


            if (bundle.getBoolean("isUser", false)) {

                isUser = true;
            }


            isEdit = bundle.getBoolean("edit", false);

            if (isEdit) {

                SharedPreferences sharedPreferences = getSharedPreferences("provider", MODE_PRIVATE);


                id_ = sharedPreferences.getString("id", " ");
                String name_ = sharedPreferences.getString("name", "");
                String email1_ = sharedPreferences.getString("email", "");
                String currency_ = sharedPreferences.getString("currency", "");
//                String password_ = sharedPreferences.getString("password", "");
                String country_code_ = sharedPreferences.getString("country_code", "");
                String phone_ = sharedPreferences.getString("phone", "");
                String id_type_ = sharedPreferences.getString("id_type", "");
                String company_name_ = sharedPreferences.getString("company_name", "");
                String visiting_charges_ = sharedPreferences.getString("visiting_charges", "");
                String advance_booking_days_ = sharedPreferences.getString("advance_booking_days", "");
                String account_type_ = sharedPreferences.getString("account_type", "");
                String members_ = sharedPreferences.getString("members", "");
                String description_ = sharedPreferences.getString("description", "");
                String city_ = sharedPreferences.getString("city", "");
                String latitude_ = sharedPreferences.getString("latitude", "");
                String longitude_ = sharedPreferences.getString("longitude", "");
                String address_ = sharedPreferences.getString("address", "");
                String logoImage_ = sharedPreferences.getString("logo_image", "");
                String bannerImage_ = sharedPreferences.getString("banner_image", "");

                boolean isBlocked = sharedPreferences.getBoolean("isBlocked", false);


                if (!isBlocked) {

                    Button docUploadBtn = findViewById(R.id.selectDocBtn);
                    docUploadBtn.setEnabled(false);
                    docUploadBtn.setVisibility(View.GONE);


                    findViewById(R.id.docVerifyTxt).setVisibility(View.VISIBLE);

                    selectedIdTxt.setText(id_type_);
                    findViewById(R.id.selectIdBtn).setEnabled(false);

                    setSelection(id_type_.trim());
                    isIdImageSelected = true;

                    nameEt.setText(name_);
                    emailEt.setText(email1_);
                    emailEt.setEnabled(false);
                    phoneEt.setText(phone_);
                    companyNameEt.setText(company_name_);
                    visitingChargesEt.setText(visiting_charges_);
                    advanceBookingDaysEt.setText(advance_booking_days_);

                    Log.d("ACCOUNTTYPE", "type: " + account_type_);


                    setAccountSelection(account_type_);

                    Log.d("ACCOUNTTYPE", "type:1 " + selectedAccount);


                    if (account_type_.trim().matches("Individual")) {

                        findViewById(R.id.etNoOfMembers).setEnabled(false);
                    }

                    membersEt.setText(members_);


//                    String ccode = country_code_.trim().replace("+","");

                    ccp.setCountryForPhoneCode(Integer.parseInt(country_code_));

                    ImageView logoImg = findViewById(R.id.imgShowLogo);
                    ImageView bannerImg = findViewById(R.id.imgShowBanner);
                    Glide.with(getApplicationContext()).load(logoImage_).into(logoImg);
                    Glide.with(getApplicationContext()).load(bannerImage_).into(bannerImg);

                    logoImg.setVisibility(View.VISIBLE);
                    bannerImg.setVisibility(View.VISIBLE);

                    findViewById(R.id.uploadViewLayLogo).setVisibility(View.GONE);
                    findViewById(R.id.uploadViewLayBanner).setVisibility(View.GONE);


                    isLogoSelected = true;
                    isBannerSelected = true;

                    richEditor.setHtml(description_.trim());

                    isAccountSelected = true;

                    accountTypeTxt.setText(account_type_);


                    cityEt.setText(city_);
                    latEt.setText(latitude_);
                    longEt.setText(longitude_);
                    addressEt.setText(address_);

                    currencyTxt.setText(currency_);


                    nameEt.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            isUpdate = true;


                        }

                        @Override
                        public void afterTextChanged(Editable editable) {

                        }
                    });


                    phoneEt.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            isUpdate = true;

                        }

                        @Override
                        public void afterTextChanged(Editable editable) {

                        }
                    });

                    ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
                        @Override
                        public void onCountrySelected() {

                            isUpdate = true;

                        }
                    });


                    companyNameEt.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            isUpdate = true;

                        }

                        @Override
                        public void afterTextChanged(Editable editable) {

                        }
                    });

                    visitingChargesEt.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            isUpdate = true;

                        }

                        @Override
                        public void afterTextChanged(Editable editable) {

                        }
                    });

                    advanceBookingDaysEt.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            isUpdate = true;

                        }

                        @Override
                        public void afterTextChanged(Editable editable) {

                        }
                    });

                    membersEt.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            isUpdate = true;

                        }

                        @Override
                        public void afterTextChanged(Editable editable) {

                        }
                    });
                    cityEt.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            isUpdate = true;

                        }

                        @Override
                        public void afterTextChanged(Editable editable) {

                        }
                    });
                    latEt.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            isUpdate = true;

                        }

                        @Override
                        public void afterTextChanged(Editable editable) {

                        }
                    });
                    longEt.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            isUpdate = true;

                        }

                        @Override
                        public void afterTextChanged(Editable editable) {

                        }
                    });
                    addressEt.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            isUpdate = true;

                        }

                        @Override
                        public void afterTextChanged(Editable editable) {

                        }
                    });

                    richEditor.setOnTextChangeListener(new RichEditor.OnTextChangeListener() {
                        @Override
                        public void onTextChange(String text) {

                            isUpdate = true;

                        }
                    });


                    Button buttonUpdate = findViewById(R.id.complete5Btn);

                    buttonUpdate.setText("Update Now");

                } else {
                    findViewById(R.id.docVerifyTxt).setVisibility(View.VISIBLE);
                    showBlockDialog(getApplicationContext());
                }


            } else {
//                findViewById(R.id.selectDocBtn).setEnabled(true);
            }

        }


    }

    public static void showBlockDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("You Are Blocked By Admin!!")
                .setCancelable(false)
                .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Handle exit action
                        // For example, you can call finish() to exit the activity
                        ((Activity) context).finish();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private class CheckEmailTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                String email = params[0];

                // Create Retrofit service
                ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

                // Make the API call to check if the email exists
                Call<EmailValidationResponse> call = apiInterface.checkEmailProvider(email);


                Response<EmailValidationResponse> response = call.execute();

                if (response.isSuccessful()) {
                    EmailValidationResponse emailValidationResponse = response.body();
                    return emailValidationResponse != null && emailValidationResponse.isExists();
                } else {
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();

                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean emailExists) {

            progressDialog.dismiss();

            if (emailExists) {


                if (isEdit) {

                    stepsCountText.setText("Step 2 Of 6");
                    titleTxt.setText("Provider Email Verification");
                    findViewById(R.id.lay2).setVisibility(View.VISIBLE);
                    findViewById(R.id.next2Btn).setVisibility(View.VISIBLE);


                    findViewById(R.id.next1Btn).setVisibility(View.GONE);
                    findViewById(R.id.lay1).setVisibility(View.GONE);
                    findViewById(R.id.lay3).setVisibility(View.GONE);
                    findViewById(R.id.next3Layout).setVisibility(View.GONE);
                    findViewById(R.id.next4Layout).setVisibility(View.GONE);
                    findViewById(R.id.lay4).setVisibility(View.GONE);

                    findViewById(R.id.next5Layout).setVisibility(View.GONE);
                    findViewById(R.id.lay5).setVisibility(View.GONE);
                    findViewById(R.id.lay6).setVisibility(View.GONE);
                    findViewById(R.id.next6Layout).setVisibility(View.GONE);
                    initializeEmailVerification();

                } else {

                    CustomToastNegative.create(getApplicationContext(),"Email Already Exists!");

                }


            } else {

                stepsCountText.setText("Step 2 Of 6");
                titleTxt.setText("Provider Email Verification");
                findViewById(R.id.lay2).setVisibility(View.VISIBLE);
                findViewById(R.id.next2Btn).setVisibility(View.VISIBLE);


                findViewById(R.id.next1Btn).setVisibility(View.GONE);
                findViewById(R.id.lay1).setVisibility(View.GONE);
                findViewById(R.id.lay3).setVisibility(View.GONE);
                findViewById(R.id.next3Layout).setVisibility(View.GONE);
                findViewById(R.id.next4Layout).setVisibility(View.GONE);
                findViewById(R.id.lay4).setVisibility(View.GONE);

                findViewById(R.id.next5Layout).setVisibility(View.GONE);
                findViewById(R.id.lay5).setVisibility(View.GONE);
                findViewById(R.id.lay6).setVisibility(View.GONE);
                findViewById(R.id.next6Layout).setVisibility(View.GONE);
                initializeEmailVerification();


            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 882 && resultCode == RESULT_OK) {

//
//


            if (data != null) {

                Bundle bundle = data.getExtras();
                lat = bundle.getString("lat");
                longitude = bundle.getString("long");
                address = bundle.getString("address");
                city = bundle.getString("city");


                latEt.setText(lat);
                longEt.setText(longitude);
                addressEt.setText(address);
                cityEt.setText(city);
            }


        }


        if (requestCode == 10001) {
            if (resultCode == RESULT_OK && data != null) {

                Bundle bundle = data.getExtras();

                if (bundle != null) {

                    String croppedImageUri = bundle.getString("croppedImageUri");

                    if (bundle.getString("image", "none").matches("doc")) {

                        ImageView imageView = findViewById(R.id.imgShow);
                        imageView.setVisibility(View.VISIBLE);
                        Glide.with(getApplicationContext()).load(croppedImageUri).into(imageView);

                        isIdImageSelected = true;
                        docUri = getImageRealPathFromURI(Uri.parse(croppedImageUri));

                    }
                    if (bundle.getString("image", "none").matches("logo")) {

                        ImageView imageView = findViewById(R.id.imgShowLogo);
                        isLogoSelected = true;
                        imageView.setVisibility(View.VISIBLE);
                        findViewById(R.id.uploadViewLayLogo).setVisibility(View.GONE);
                        Glide.with(getApplicationContext()).load(croppedImageUri).into(imageView);
                        logoUri = getImageRealPathFromURI(Uri.parse(croppedImageUri));
                        isUpdate = true;

                    }
                    if (bundle.getString("image", "none").matches("banner")) {


                        ImageView imageView = findViewById(R.id.imgShowBanner);
                        isBannerSelected = true;
                        imageView.setVisibility(View.VISIBLE);
                        findViewById(R.id.uploadViewLayBanner).setVisibility(View.GONE);
                        Glide.with(getApplicationContext()).load(croppedImageUri).into(imageView);
                        bannerUri = getImageRealPathFromURI(Uri.parse(croppedImageUri));

                        isUpdate = true;
                    }


                } else {

                    Log.d("ProDETAILSACTI", "Error 2 Null Bundle ");

                }
            }

        }

    }

    private void selectLogo() {


        ImagePickerBottomSheet bottomSheet = new ImagePickerBottomSheet(1010, 1016);
        bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
        bottomSheet.setImageSelectionListener(new ImagePickerBottomSheet.ImageSelectionListener() {
            @Override
            public void onImageSelected(Uri imageUri) {

                Intent intent = new Intent(ProviderDetailsActivity.this, ImageCompressActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString("image", "logo");
                bundle.putString("originalUri", String.valueOf(imageUri));
                bundle.putInt("length", 1);
                bundle.putInt("height", 1);
                intent.putExtras(bundle);

                startActivityForResult(intent, 10001);


            }

            @Override
            public void onImagePath(Uri imageUri) {


                Intent intent = new Intent(ProviderDetailsActivity.this, ImageCompressActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString("image", "logo");
                bundle.putString("originalUri", String.valueOf(imageUri));
                bundle.putInt("length", 1);
                bundle.putInt("height", 1);

                intent.putExtras(bundle);
                startActivityForResult(intent, 10001);


            }
        });


//        ImageView imageView = findViewById(R.id.imgShowLogo);
//        ImagePickerBottomSheet bottomSheet = new ImagePickerBottomSheet(11, 12);
//        bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
//        bottomSheet.setImageSelectionListener(new ImagePickerBottomSheet.ImageSelectionListener() {
//            @Override
//            public void onImageSelected(Uri imageUri) {
//
//                isLogoSelected = true;
//                imageView.setVisibility(View.VISIBLE);
//                findViewById(R.id.uploadViewLayLogo).setVisibility(View.GONE);
//
//                Glide.with(getApplicationContext()).load(imageUri).into(imageView);
//
//                logoUri = getImageRealPathFromURI(imageUri);
//
//
//            }
//
//            @Override
//            public void onImagePath(Uri path) {
//
//                isLogoSelected = true;
//                imageView.setVisibility(View.VISIBLE);
//                findViewById(R.id.uploadViewLayLogo).setVisibility(View.GONE);
//
//                Glide.with(getApplicationContext()).load(path).into(imageView);
//                logoUri = String.valueOf(path);
//
//            }
//        });


    }

    private void selectBanner() {


        ImagePickerBottomSheet bottomSheet = new ImagePickerBottomSheet(1010, 1016);
        bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
        bottomSheet.setImageSelectionListener(new ImagePickerBottomSheet.ImageSelectionListener() {
            @Override
            public void onImageSelected(Uri imageUri) {

                Intent intent = new Intent(ProviderDetailsActivity.this, ImageCompressActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString("image", "banner");
                bundle.putString("originalUri", String.valueOf(imageUri));

                bundle.putInt("length", 16);
                bundle.putInt("height", 9);

                intent.putExtras(bundle);
                startActivityForResult(intent, 10001);


            }

            @Override
            public void onImagePath(Uri imageUri) {

                Intent intent = new Intent(ProviderDetailsActivity.this, ImageCompressActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString("image", "banner");
                bundle.putString("originalUri", String.valueOf(imageUri));
                bundle.putInt("length", 16);
                bundle.putInt("height", 9);
                intent.putExtras(bundle);
                startActivityForResult(intent, 10001);


            }
        });


//        ImagePickerBottomSheet bottomSheet = new ImagePickerBottomSheet(13, 14);
//        bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
//        bottomSheet.setImageSelectionListener(new ImagePickerBottomSheet.ImageSelectionListener() {
//            @Override
//            public void onImageSelected(Uri imageUri) {
//
        //        ImageView imageView = findViewById(R.id.imgShowBanner);

//                isBannerSelected = true;
//                imageView.setVisibility(View.VISIBLE);
//
//                findViewById(R.id.uploadViewLayBanner).setVisibility(View.GONE);
//                Glide.with(getApplicationContext()).load(imageUri).into(imageView);
//                bannerUri = getImageRealPathFromURI(imageUri);
//
//
//            }
//
//            @Override
//            public void onImagePath(Uri path) {
//
//                isBannerSelected = true;
//                imageView.setVisibility(View.VISIBLE);
//                findViewById(R.id.uploadViewLayBanner).setVisibility(View.GONE);
//
//                Glide.with(getApplicationContext()).load(path).into(imageView);
//                logoUri = String.valueOf(path);
//
//            }
//        });

    }

    private void finishSetUp() {


        if (!email.isEmpty() && !nameEt.getText().toString().isEmpty() &&
                !phoneEt.getText().toString().isEmpty() && isIdSelected
                && isIdImageSelected && !companyNameEt.getText().toString().isEmpty()
                && !visitingChargesEt.getText().toString().isEmpty()
                && !advanceBookingDaysEt.getText().toString().isEmpty()
                && isAccountSelected && !membersEt.getText().toString().isEmpty()
                && isBannerSelected && isLogoSelected && !richEditor.getHtml().isEmpty()
                && !cityEt.getText().toString().isEmpty() && !latEt.getText().toString().isEmpty()
                && !longEt.getText().toString().isEmpty() && !addressEt.getText().toString().isEmpty()
                && !latEt.getText().toString().isEmpty() && !longEt.getText().toString().isEmpty()
                && !addressEt.getText().toString().isEmpty()
                && !passEt.getText().toString().isEmpty()
                && !confifrmPassEt.getText().toString().isEmpty()
        ) {


            if (passEt.getText().toString().matches(confifrmPassEt.getText().toString())) {


                progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("Creating Account...");
                progressDialog.setCancelable(false);
                progressDialog.show();

                // Start AsyncTask to perform background upload
                new UploadDataTask().execute();

            } else {


                CustomToastNegative.create(getApplicationContext(),"Password Not Matching!");
            }


        } else {

            if (cityEt.getText().toString().isEmpty()) {


                CustomToastNegative.create(getApplicationContext(),"Fill City!");

            }
            if (addressEt.getText().toString().isEmpty()) {


                CustomToastNegative.create(getApplicationContext(),"Fill Email!");

            }
            if (latEt.getText().toString().isEmpty() || longEt.getText().toString().isEmpty()) {


                CustomToastNegative.create(getApplicationContext(),"Select Location!");

            }

        }


    }

    private class UploadDataTask extends AsyncTask<Void, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                // Your Retrofit service setup
                ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);


                // Your existing Retrofit code for data upload
                // Create RequestBody for text fields
                RequestBody finalName = RequestBody.create(MediaType.parse("text/plain"), nameEt.getText().toString().trim());
                RequestBody emails = RequestBody.create(MediaType.parse("text/plain"), email.trim());
                RequestBody country_code = RequestBody.create(MediaType.parse("text/plain"), ccp.getSelectedCountryCodeWithPlus());
                RequestBody phone = RequestBody.create(MediaType.parse("text/plain"), phoneEt.getText().toString().trim());
                RequestBody idype = RequestBody.create(MediaType.parse("text/plain"), selectedID.trim());
                RequestBody company_name = RequestBody.create(MediaType.parse("text/plain"), companyNameEt.getText().toString().trim());
                RequestBody visiting_charges = RequestBody.create(MediaType.parse("text/plain"), visitingChargesEt.getText().toString().trim());
                RequestBody currency = RequestBody.create(MediaType.parse("text/plain"), currencyTxt.getText().toString().trim());


                RequestBody advance_booking_days = RequestBody.create(MediaType.parse("text/plain"), advanceBookingDaysEt.getText().toString().trim());
                RequestBody account_type = RequestBody.create(MediaType.parse("text/plain"), selectedAccount.trim());
                RequestBody members = RequestBody.create(MediaType.parse("text/plain"), membersEt.getText().toString().trim());
                RequestBody description = RequestBody.create(MediaType.parse("text/plain"), HTML_CONTENT);
                RequestBody city = RequestBody.create(MediaType.parse("text/plain"), cityEt.getText().toString().trim());
                RequestBody latitude = RequestBody.create(MediaType.parse("text/plain"), latEt.getText().toString().trim());
                RequestBody longitude = RequestBody.create(MediaType.parse("text/plain"), longEt.getText().toString().trim());
                RequestBody address = RequestBody.create(MediaType.parse("text/plain"), addressEt.getText().toString().trim());
                RequestBody isblock = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(false));

                RequestBody pass = RequestBody.create(MediaType.parse("text/plain"), passEt.getText().toString().trim());


                // Create RequestBody for image files
                MultipartBody.Part documentImagePart = prepareImagePart("document_image", docUri);
                MultipartBody.Part logoImagePart = prepareImagePart("logo_image", logoUri);
                MultipartBody.Part bannerImagePart = prepareImagePart("banner_image", bannerUri);

                Call<ResponseProvider> call = apiInterface.uploadProvider(finalName, emails, pass, country_code, phone, idype, company_name, visiting_charges, currency, advance_booking_days
                        , account_type, members, description, city, latitude, longitude, address, isblock,
                        documentImagePart, logoImagePart, bannerImagePart
                );


                // Execute the API call

                call.enqueue(new Callback<ResponseProvider>() {
                    @Override
                    public void onResponse(Call<ResponseProvider> call, Response<ResponseProvider> response) {


                        progressDialog.dismiss();
                        if (response.isSuccessful() && response != null) {

                            ResponseProvider responseProvider = response.body();

                            if (!responseProvider.isError()) {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        CustomToastPositive.create(getApplicationContext(),"Account Created Successfully!");

                                    }
                                });

                                // save all data and open home activity for provider


                                SharedPreferences sharedPreferences = getSharedPreferences("provider", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();


                                editor.putString("id", responseProvider.getProvider().getId());
                                editor.putString("name", responseProvider.getProvider().getName());

                                editor.putString("email", responseProvider.getProvider().getEmail());
                                editor.putString("password", responseProvider.getProvider().getEmail());
                                editor.putString("phone", responseProvider.getProvider().getPhone());
                                editor.putString("country_code", responseProvider.getProvider().getCountry_code());
                                editor.putString("currency", responseProvider.getProvider().getCurrency());
                                editor.putString("id_type", responseProvider.getProvider().getIdType());
                                editor.putString("company_name", responseProvider.getProvider().getCompanyName());
                                editor.putString("visiting_charges", responseProvider.getProvider().getVisitingCharges());
                                editor.putString("advance_booking_days", responseProvider.getProvider().getAdvanceBookingDays());
                                editor.putString("account_type", responseProvider.getProvider().getAccountType());
                                editor.putString("members", responseProvider.getProvider().getMembers());
                                editor.putString("description", responseProvider.getProvider().getDescription());
                                editor.putString("city", responseProvider.getProvider().getCity());
                                editor.putString("latitude", responseProvider.getProvider().getLatitude());
                                editor.putString("longitude", responseProvider.getProvider().getLongitude());
                                editor.putString("address", responseProvider.getProvider().getAddress());
                                editor.putString("document_image", responseProvider.getProvider().getDocumentImage());
                                editor.putString("logo_image", responseProvider.getProvider().getLogoImage());
                                editor.putString("banner_image", responseProvider.getProvider().getBannerImage());

                                boolean booleanValue = (responseProvider.getProvider().getIs_blocked() == 1);
                                editor.putBoolean("isBlocked", booleanValue);

                                editor.putBoolean("isProvider", true);

                                editor.apply();


                                SharedPreferences preferences2 = getSharedPreferences("settings", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor2 = preferences2.edit();


                                editor2.putString("address", responseProvider.getProvider().getAddress());
                                editor2.putString("latitude", responseProvider.getProvider().getLatitude());
                                editor2.putString("longitude", responseProvider.getProvider().getLongitude());
                                editor2.apply();


                                startActivity(new Intent(ProviderDetailsActivity.this, ProviderHome.class));
                                finish();


                            } else {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        Log.d("ProDETAILSACTI", "Error 33 " + responseProvider.getMessage());


                                    }
                                });

                            }


                        } else {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    Log.d("ProDETAILSACTI", "Error 4 Res Not Success");


                                }
                            });

                        }

                    }

                    @Override
                    public void onFailure(Call<ResponseProvider> call, Throwable t) {
                        progressDialog.dismiss();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.d("ProDETAILSACTI", "Error 6 " + t.getMessage());


                            }
                        });
                    }
                });

                return true;

            } catch (Exception e) {
                e.printStackTrace();

                Log.d("CHECKRES", "doInBackground: " + e.getMessage());
                progressDialog.dismiss();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("ProDETAILSACTI", "Error 7 " + e.getMessage());

                    }
                });

                return false;
            }
        }


        @Override
        protected void onPostExecute(Boolean success) {

        }
    }

    private class UpdateDataTask extends AsyncTask<Void, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                // Your Retrofit service setup
                ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);


                // Your existing Retrofit code for data upload
                // Create RequestBody for text fields
                RequestBody userID = RequestBody.create(MediaType.parse("text/plain"), id_);
                RequestBody finalName = RequestBody.create(MediaType.parse("text/plain"), nameEt.getText().toString().trim());
                RequestBody emails = RequestBody.create(MediaType.parse("text/plain"), emailEt.getText().toString().trim().toLowerCase());
                RequestBody country_code = RequestBody.create(MediaType.parse("text/plain"), ccp.getSelectedCountryCodeWithPlus());
                RequestBody phone = RequestBody.create(MediaType.parse("text/plain"), phoneEt.getText().toString().trim());
                RequestBody idype = RequestBody.create(MediaType.parse("text/plain"), selectedID.trim());
                RequestBody company_name = RequestBody.create(MediaType.parse("text/plain"), companyNameEt.getText().toString().trim());
                RequestBody visiting_charges = RequestBody.create(MediaType.parse("text/plain"), visitingChargesEt.getText().toString().trim());
                RequestBody currency = RequestBody.create(MediaType.parse("text/plain"), currencyTxt.getText().toString().trim());


                RequestBody advance_booking_days = RequestBody.create(MediaType.parse("text/plain"), advanceBookingDaysEt.getText().toString().trim());
                RequestBody account_type = RequestBody.create(MediaType.parse("text/plain"), selectedAccount.trim());
                RequestBody members = RequestBody.create(MediaType.parse("text/plain"), membersEt.getText().toString().trim());
                RequestBody description = RequestBody.create(MediaType.parse("text/plain"), HTML_CONTENT);
                RequestBody city = RequestBody.create(MediaType.parse("text/plain"), cityEt.getText().toString().trim());
                RequestBody latitude = RequestBody.create(MediaType.parse("text/plain"), latEt.getText().toString().trim());
                RequestBody longitude = RequestBody.create(MediaType.parse("text/plain"), longEt.getText().toString().trim());
                RequestBody address = RequestBody.create(MediaType.parse("text/plain"), addressEt.getText().toString().trim());
                RequestBody isblock = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(false));

                RequestBody pass = RequestBody.create(MediaType.parse("text/plain"), passEt.getText().toString().trim());


                // Create RequestBody for image files


                MultipartBody.Part documentImagePart;
                MultipartBody.Part logoImagePart;
                MultipartBody.Part bannerImagePart;


                if (logoUri == null) {


                    logoImagePart = null;
                } else {

                    logoImagePart = prepareImagePart("logo_image", logoUri);
                }

                if (bannerUri == null) {

                    bannerImagePart = null;
                } else {

                    bannerImagePart = prepareImagePart("banner_image", bannerUri);
                }

                if (docUri == null) {

                    documentImagePart = null;
                } else {

                    documentImagePart = prepareImagePart("document_image", docUri);
                }


                Call<ResponseProvider> call = apiInterface.updateProvider(userID, finalName, emails, pass, country_code, phone, idype, company_name, visiting_charges, currency, advance_booking_days
                        , account_type, members, description, city, latitude, longitude, address, isblock,
                        documentImagePart, logoImagePart, bannerImagePart
                );


                // Execute the API call

                call.enqueue(new Callback<ResponseProvider>() {
                    @Override
                    public void onResponse(Call<ResponseProvider> call, Response<ResponseProvider> response) {

//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//
//                            }
//                        });

                        progressDialog.dismiss();

                        if (response.isSuccessful() && response != null) {

                            ResponseProvider responseProvider = response.body();

                            if (!responseProvider.isError()) {


                                signInAnonymouslyProvider(responseProvider, ProviderDetailsActivity.this);


                            } else {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.dismiss();
                                        Log.d("ProDETAILSACTI", "Error 9 ");


                                    }
                                });

                            }


                        } else {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.d("ProDETAILSACTI", "Error 10 ");
                                    progressDialog.dismiss();

                                }
                            });

                        }

                    }

                    @Override
                    public void onFailure(Call<ResponseProvider> call, Throwable t) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                                Log.d("ProDETAILSACTI", "Error 11 " + t.getMessage());


                            }
                        });
                    }
                });

                return true;

            } catch (Exception e) {
                e.printStackTrace();

                Log.d("CHECKRES", "doInBackground: " + e.getMessage());
                progressDialog.dismiss();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("ProDETAILSACTI", "Error 12 " + e.getMessage());

                    }
                });

                return false;
            }
        }


        @Override
        protected void onPostExecute(Boolean success) {

        }
    }

    public String getImageRealPathFromURI(Uri contentUri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String filePath = cursor.getString(column_index);
            cursor.close();
            return filePath;
        }
        return contentUri.getPath(); // Fallback to the URI itself
    }


    // Helper method to create MultipartBody.Part for image files
    private MultipartBody.Part prepareImagePart(String partName, String imagePath) {
        File file = new File(imagePath);
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }

    private void setUpHtmlEditor() {


        richEditor = findViewById(R.id.richEditor);
        boldBtn = findViewById(R.id.boldButton);
        colorBtn = findViewById(R.id.colorButton);

        TextView selectedFontSizeTxt = findViewById(R.id.selectedFontSizeTxt);


        findViewById(R.id.insertBullets).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                richEditor.setBullets();

            }
        });
        findViewById(R.id.insertNumbers).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                richEditor.setNumbers();

            }
        });

        findViewById(R.id.alignLeftButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                richEditor.setAlignLeft();
            }
        });
        findViewById(R.id.alignCenterButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                richEditor.setAlignCenter();
            }
        });
        findViewById(R.id.alignRightButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                richEditor.setAlignRight();
            }
        });

        findViewById(R.id.linksButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showAddUrlDialog(richEditor);

            }
        });

        findViewById(R.id.fontSizeButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 1-7

                showNumberPopupMenu(view, richEditor, selectedFontSizeTxt);


            }
        });

        boldBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                richEditor.setBold();
            }
        });

        colorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
// Java Code
                new MaterialColorPickerDialog
                        .Builder(ProviderDetailsActivity.this)
                        .setTitle("Pick Color")
                        .setColorShape(ColorShape.CIRCLE)
                        .setColors(getcolorsList())
                        .setColorSwatch(ColorSwatch._900)
                        .setColorListener(new ColorListener() {
                            @Override
                            public void onColorSelected(int color, @NotNull String colorHex) {
                                // Handle Color Selection

                                richEditor.setTextColor(color);
                                colorBtn.setColorFilter(color);


                            }
                        })
                        .show();

            }
        });


    }


    private void showNumberPopupMenu(View anchorView, RichEditor richEditor, TextView selectedFontSizeTxt) {
        PopupMenu popupMenu = new PopupMenu(this, anchorView);

        // Inflate the menu resource
        popupMenu.getMenuInflater().inflate(R.menu.number_menu, popupMenu.getMenu());

        // Set the item click listener
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // Handle the item click (show a Toast with the selected number)
                int selectedNumber = Integer.parseInt(item.getTitle().toString());


                richEditor.setFontSize(selectedNumber);

                selectedFontSizeTxt.setText(String.valueOf(selectedNumber));


                return true;
            }
        });

        // Show the PopupMenu
        popupMenu.show();
    }




    private void initializeEmailVerification() {

        email = emailEt.getText().toString().trim().toLowerCase();
        name = nameEt.getText().toString().trim();

        emailForOtp = findViewById(R.id.emailForOtp);
        otpTimerTxt = findViewById(R.id.otpTimerTxt);
        resendTxt = findViewById(R.id.resendTxt);
        TextInputEditText otpEt = findViewById(R.id.otpEt);
        resendTxt.setEnabled(false);
        resendTxt.setClickable(false);

        if (!email.isEmpty()) {

            EmailMaskUtil.maskEmailAndSetToTextView(email, emailForOtp);

//            emailForOtp.setText(email);
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


    }

    private void initializeVariables() {
        ccp = findViewById(R.id.ccp);

        currencyTxt = findViewById(R.id.currencyTxt);
        selectedIdTxt = findViewById(R.id.selectedIdTxt);
        accountTypeTxt = findViewById(R.id.accountTypeTxt);
        stepsCountText = findViewById(R.id.stepsCountText);
        titleTxt = findViewById(R.id.stepsTitleTxt);

        nameEt = findViewById(R.id.etName);
        emailEt = findViewById(R.id.etEmail);
        phoneEt = findViewById(R.id.etPhone);

        otpEt = findViewById(R.id.otpEt);

        companyNameEt = findViewById(R.id.etCompanyName);
        visitingChargesEt = findViewById(R.id.etVisitingCharges);
        advanceBookingDaysEt = findViewById(R.id.etAdvanceBookingdays);
        membersEt = findViewById(R.id.etNoOfMembers);

        cityEt = findViewById(R.id.etCity);
        latEt = findViewById(R.id.etLatitude);
        longEt = findViewById(R.id.etLongitude);
        addressEt = findViewById(R.id.etAddress);
        passEt = findViewById(R.id.passEt);
        confifrmPassEt = findViewById(R.id.confirmPassEt);


//        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1) { // API level 24
//            setAutofillHighlightColor(nameEt, getColor(R.color.white));
//            setAutofillHighlightColor(emailEt, getColor(R.color.white));
//            setAutofillHighlightColor(phoneEt, getColor(R.color.white));
//            setAutofillHighlightColor(otpEt, getColor(R.color.white));
//            setAutofillHighlightColor(companyNameEt, getColor(R.color.white));
//            setAutofillHighlightColor(visitingChargesEt, getColor(R.color.white));
//            setAutofillHighlightColor(advanceBookingDaysEt, getColor(R.color.white));
//            setAutofillHighlightColor(membersEt, getColor(R.color.white));
//            setAutofillHighlightColor(cityEt, getColor(R.color.white));
//            setAutofillHighlightColor(latEt, getColor(R.color.white));
//            setAutofillHighlightColor(longEt, getColor(R.color.white));
//            setAutofillHighlightColor(addressEt, getColor(R.color.white));
//            setAutofillHighlightColor(passEt, getColor(R.color.white));
//            setAutofillHighlightColor(confifrmPassEt, getColor(R.color.white));
//        }


    }

//    private void setAutofillHighlightColor(EditText editText, int color) {
//        try {
//            // Use reflection to access the internal property
//            Paint paint = (Paint) EditText.class.getDeclaredField("mHighlightPaint").get(editText);
//            paint.setColor(color);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    private void sendOtp(String mail) {

        tempMail = mail;

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
                    Log.d("ProDETAILSACTI", "Error 13 " + errorMessage);

                });
            }
        });

// Execute the task with parameters
        sendOtpTask.execute(mail, name, OTP, "OTP Verification : Expert Here");


    }

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

    private void selectDoc() {


        ImagePickerBottomSheet bottomSheet = new ImagePickerBottomSheet(1010, 1016);
        bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
        bottomSheet.setImageSelectionListener(new ImagePickerBottomSheet.ImageSelectionListener() {
            @Override
            public void onImageSelected(Uri imageUri) {

                Intent intent = new Intent(ProviderDetailsActivity.this, ImageCompressActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString("image", "doc");
                bundle.putString("originalUri", String.valueOf(imageUri));
                bundle.putInt("length", 16);
                bundle.putInt("height", 9);
                intent.putExtras(bundle);
                startActivityForResult(intent, 10001);


            }

            @Override
            public void onImagePath(Uri imageUri) {


                Intent intent = new Intent(ProviderDetailsActivity.this, ImageCompressActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString("image", "doc");
                bundle.putString("originalUri", String.valueOf(imageUri));
                bundle.putInt("length", 16);
                bundle.putInt("height", 9);
                intent.putExtras(bundle);
                startActivityForResult(intent, 10001);


            }
        });


//        ImageView imageView = findViewById(R.id.imgShow);
//        ImagePickerBottomSheet bottomSheet = new ImagePickerBottomSheet(15, 16);
//        bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
//        bottomSheet.setImageSelectionListener(new ImagePickerBottomSheet.ImageSelectionListener() {
//            @Override
//            public void onImageSelected(Uri imageUri) {
//
//                isIdImageSelected = true;
//                Glide.with(getApplicationContext()).load(imageUri).into(imageView);
//                docUri = getImageRealPathFromURI(imageUri);
//                cropAndCompressImage(String.valueOf(imageUri));
//
//
//            }
//
//            @Override
//            public void onImagePath(Uri path) {
//
//                isIdImageSelected = true;
//                Glide.with(getApplicationContext()).load(path).into(imageView);
//                docUri = String.valueOf(path);
//
//
//                Log.d("DOCURITEST", "uri path" + path);
//
//
//            }
//        });


    }


    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {

        if (isEdit) {


            ExitBottomSheet bottomSheet = new ExitBottomSheet(true);
            bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());

            Log.d("CHECKECIT", "onClick: " + " IS EDIT");


        } else {

            Log.d("CHECKECIT", "onClick: " + " IS EDIT 1");


            if (isUser) {
                Log.d("CHECKECIT", "onClick: " + " IS EDIT 2");

                ExitBottomSheet bottomSheet = new ExitBottomSheet(true);
                bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
            } else {

                Log.d("CHECKECIT", "onClick: " + " IS EDIT 3");

                ExitBottomSheet bottomSheet = new ExitBottomSheet(false);
                bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
            }


        }


    }


    private void showCustomDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Inflate the custom layout
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.id_select_dilog, null);

        builder.setView(dialogView);

        // Show the dialog
        AlertDialog dialog = builder.create();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        // Set up the radio group
        // RadioGroup radioGroup = dialogView.findViewById(R.id.radioGroup);

        // Set up the radio buttons
        final RadioButton radioPanCard = dialogView.findViewById(R.id.radioPanCard);
        final RadioButton radioDrivingLicense = dialogView.findViewById(R.id.radioDrivingLicense);
        final RadioButton radioOtherID = dialogView.findViewById(R.id.radioOtherID);


        radioPanCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setSelection("Pan Card");

                // Dismiss the dialog after a delay of 200ms
                new Handler().postDelayed(new Runnable() {
                    @Override

                    public void run() {
                        dialog.dismiss();
                    }
                }, 200);
            }
        });

        radioDrivingLicense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setSelection("Driving License");

                // Dismiss the dialog after a delay of 200ms
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                    }
                }, 200);
            }
        });
        radioOtherID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setSelection("Other valid government ID");

                // Dismiss the dialog after a delay of 200ms
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                    }
                }, 200);
            }
        });


    }

    private void showCurrencyDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Inflate the custom layout
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.currency_select_dilog, null);

        builder.setView(dialogView);

        // Show the dialog
        AlertDialog dialog = builder.create();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        // Set up the radio group
        // RadioGroup radioGroup = dialogView.findViewById(R.id.radioGroup);

        // Set up the radio buttons
        final RadioButton radioInr = dialogView.findViewById(R.id.radioInr);
        final RadioButton radioUsd = dialogView.findViewById(R.id.radioUSD);


        radioInr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                currencyTxt.setText(radioInr.getText().toString());
                isUpdate = true;
                // Dismiss the dialog after a delay of 200ms
                new Handler().postDelayed(new Runnable() {
                    @Override

                    public void run() {
                        dialog.dismiss();
                    }
                }, 200);
            }
        });

        radioUsd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isUpdate = true;
                currencyTxt.setText(radioUsd.getText().toString());
                // Dismiss the dialog after a delay of 200ms
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                    }
                }, 200);
            }
        });


    }

    private void showAccountTypeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Inflate the custom layout
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.account_select_dilog, null);

        builder.setView(dialogView);

        // Show the dialog
        AlertDialog dialog = builder.create();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        // Set up the radio group
        // RadioGroup radioGroup = dialogView.findViewById(R.id.radioGroup);

        // Set up the radio buttons
        final RadioButton radioIndividual = dialogView.findViewById(R.id.radioIndividual);
        final RadioButton radioOrg = dialogView.findViewById(R.id.radioOrganization);


        radioIndividual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setAccountSelection("Individual");
                membersEt.setText("1");
                membersEt.setEnabled(false);
                isUpdate = true;

                // Dismiss the dialog after a delay of 200ms
                new Handler().postDelayed(new Runnable() {
                    @Override

                    public void run() {
                        dialog.dismiss();
                    }
                }, 200);
            }
        });

        radioOrg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setAccountSelection("Organisation");
                membersEt.setText("");
                membersEt.setEnabled(true);
                isUpdate = true;

                // Dismiss the dialog after a delay of 200ms
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                    }
                }, 200);
            }
        });


    }


    private void showAddUrlDialog(RichEditor richEditor) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Inflate the custom layout
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.link_add_dilog, null);

        builder.setView(dialogView);

        // Show the dialog
        AlertDialog dialog = builder.create();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        // Set up the radio group
        // RadioGroup radioGroup = dialogView.findViewById(R.id.radioGroup);

        // Set up the radio buttons
        final Button addLinkBtn = dialogView.findViewById(R.id.addLinkBtn);
        final TextInputEditText etLinks = dialogView.findViewById(R.id.etLink);


        addLinkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!etLinks.getText().toString().isEmpty()) {

                    String url = etLinks.getText().toString().trim();
                    richEditor.insertLink(url, url);
                    // Dismiss the dialog after a delay of 200ms
                    new Handler().postDelayed(new Runnable() {
                        @Override

                        public void run() {
                            dialog.dismiss();
                        }
                    }, 200);
                } else {

                    CustomToastNegative.create(getApplicationContext(),"Enter URL!");
                }
            }
        });


    }

    private void setAccountSelection(String selection) {

        if (selection.matches("Individual")) {
            selectedAccount = "Individual";
        }
        if (selection.matches("Organisation")) {

            selectedAccount = "Organisation";
        }


        accountTypeTxt.setText(selectedAccount);
        isAccountSelected = true;

    }

    private void setSelection(String selection) {

        if (selection.matches("Pan Card")) {
            selectedID = "Pan Card";
        }
        if (selection.matches("Driving License")) {

            selectedID = "Driving License";
        }
        if (selection.matches("Other valid government ID")) {

            selectedID = "Other valid government ID";
        }


        selectedIdTxt.setText(selectedID);
        isIdSelected = true;

    }


    @Override
    public void onSubmit(String emails) {


        email = emails;


        emailEt.setText(emails);
        sendOtp(email);
        EmailMaskUtil.maskEmailAndSetToTextView(email, emailForOtp);

//        emailForOtp.setText(emails);
        otpTimerTxt.setText("");
        resendTxt.setText("Resend in ");

        otpTimerTxt.setVisibility(View.VISIBLE);
        resendTxt.setEnabled(false);
        resendTxt.setClickable(false);
        startCountdownTimer(duration, interval);

        tempMail = emails;
    }

    private void internetStatus() {

        registerReceiver(receiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(receiver);
    }


    private void signInAnonymouslyProvider(ResponseProvider responseProvider, Activity activity) {


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

                            new FcmServiceTaskProvider("0", responseProvider.getProvider().getId(), "provider", fcmDeviceToken, responseProvider).execute();

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
                        progressDialog.dismiss();
                        Log.d("ProDETAILSACTI", "Error 14 Token GEN FAIL");
                    }
                });

            }
        });
    }

    private class FcmServiceTaskProvider extends AsyncTask<Void, Void, SuccessMessageResponse> {
        String userIDForFcm, providerIdForFcm, typeFcm, fcmTOKEN;
        ResponseProvider responseProvider;

        public FcmServiceTaskProvider(String userIDForFcm, String providerIdForFcm, String typeFcm, String fcmTOKEN, ResponseProvider loginResponse) {
            this.userIDForFcm = userIDForFcm;
            this.providerIdForFcm = providerIdForFcm;
            this.typeFcm = typeFcm;
            this.fcmTOKEN = fcmTOKEN;
            this.responseProvider = loginResponse;
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
            progressDialog.dismiss();

            if (serviceResponse != null) {
                if (serviceResponse.isSuccess()) {

                    if (serviceResponse.getMessage().equals("Token saved successfully!") || serviceResponse.getMessage().equals("Token updated successfully!")) {


                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                CustomToastPositive.create(getApplicationContext(),"Account Updated Success!");

                            }
                        });

                        // save all data and open home activity for provider


                        SharedPreferences sharedPreferences = getSharedPreferences("provider", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();


                        editor.putString("id", responseProvider.getProvider().getId());
                        editor.putString("name", responseProvider.getProvider().getName());

                        editor.putString("email", responseProvider.getProvider().getEmail());
                        editor.putString("password", responseProvider.getProvider().getPassword());
                        editor.putString("country_code", responseProvider.getProvider().getCountry_code());
                        editor.putString("currency", responseProvider.getProvider().getCurrency());
                        editor.putString("phone", responseProvider.getProvider().getPhone());
                        editor.putString("id_type", responseProvider.getProvider().getIdType());
                        editor.putString("company_name", responseProvider.getProvider().getCompanyName());
                        editor.putString("visiting_charges", responseProvider.getProvider().getVisitingCharges());
                        editor.putString("advance_booking_days", responseProvider.getProvider().getAdvanceBookingDays());
                        editor.putString("account_type", responseProvider.getProvider().getAccountType());
                        editor.putString("members", responseProvider.getProvider().getMembers());
                        editor.putString("description", responseProvider.getProvider().getDescription());
                        editor.putString("city", responseProvider.getProvider().getCity());
                        editor.putString("latitude", responseProvider.getProvider().getLatitude());
                        editor.putString("longitude", responseProvider.getProvider().getLongitude());
                        editor.putString("address", responseProvider.getProvider().getAddress());
                        editor.putString("document_image", responseProvider.getProvider().getDocumentImage());
                        editor.putString("logo_image", responseProvider.getProvider().getLogoImage());
                        editor.putString("banner_image", responseProvider.getProvider().getBannerImage());

                        boolean booleanValue = (responseProvider.getProvider().getIs_blocked() == 1);
                        editor.putBoolean("isBlocked", booleanValue);

                        editor.putBoolean("isProvider", true);


                        SharedPreferences preferences2 = getSharedPreferences("settings", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor2 = preferences2.edit();


                        editor2.putString("address", responseProvider.getProvider().getAddress());
                        editor2.putString("latitude", responseProvider.getProvider().getLatitude());
                        editor2.putString("longitude", responseProvider.getProvider().getLongitude());
                        editor2.apply();


                        editor.apply();


                        startActivity(new Intent(ProviderDetailsActivity.this, ProviderHome.class));
                        finish();


                    } else {
                        Log.d("ProDETAILSACTI", "Error 15 " + serviceResponse.getMessage());

                    }


                } else {

                    Log.d("ProDETAILSACTI", "Error 16 ");
                }
            } else {
                // Handle the case when there is no responsesw
            }
        }

    }


    private List<String> getcolorsList() {


        List<String> colorsList = new ArrayList<>();

        // Add commonly used hex color codes to the list
        colorsList.add("#000000"); // Black
        colorsList.add("#FFFFFF"); // White
        colorsList.add("#FF0000"); // Red
        colorsList.add("#00FF00"); // Lime
        colorsList.add("#0000FF"); // Blue
        colorsList.add("#FFFF00"); // Yellow
        colorsList.add("#FFA500"); // Orange
        colorsList.add("#800080"); // Purple
        colorsList.add("#FFC0CB"); // Pink
        colorsList.add("#00FFFF"); // Cyan
        colorsList.add("#FFD700"); // Gold
        colorsList.add("#008080"); // Teal
        colorsList.add("#FF69B4"); // HotPink
        colorsList.add("#FF6347"); // Tomato
        colorsList.add("#DC143C"); // Crimson
        colorsList.add("#FA8072"); // Salmon
        colorsList.add("#4682B4"); // SteelBlue
        colorsList.add("#8A2BE2"); // BlueViolet
        colorsList.add("#808080"); // Gray
        colorsList.add("#FF4500"); // OrangeRed


        return colorsList;


    }


}