package www.experthere.in.users;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.hbb20.CCPCountry;
import com.hbb20.CountryCodePicker;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import www.experthere.in.LocationSelectActivity;
import www.experthere.in.R;
import www.experthere.in.api.ApiClient;
import www.experthere.in.api.ApiInterface;
import www.experthere.in.helper.CustomToastNegative;
import www.experthere.in.helper.CustomToastPositive;
import www.experthere.in.helper.ImageCompressActivity;
import www.experthere.in.helper.ImagePickerBottomSheet;
import www.experthere.in.helper.PhoneNumberUtilHelper;
import www.experthere.in.model.LoginResponse;
import www.experthere.in.serviceProvider.NewServiceActivity;

public class EditUserActivity extends AppCompatActivity {


    String userID, dpImage, name, email, number, countryCode;
    SharedPreferences preferences;
    String dpUri;

    TextInputEditText etName, etProfession, etPhone, etEmail;
    CountryCodePicker ccpCountry;
    boolean isEdited;

    ProgressBar btnProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.white, this.getTheme()));

        setContentView(R.layout.activity_edit_user);

        initPreferences();

        btnProgressBar = findViewById(R.id.btnProgressBar);

        findViewById(R.id.forgotPass).setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {


                // Create custom dialog
                Dialog dialog = new Dialog(EditUserActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.custom_delete_dialog);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                // Initialize views in the custom dialog
                TextView dialogTitle = dialog.findViewById(R.id.dialogTitle);
                TextView dialogMessage = dialog.findViewById(R.id.dialogMessage);
                Button btnCancel = dialog.findViewById(R.id.btnCancel);
                Button btnDelete = dialog.findViewById(R.id.btnDelete);

                btnDelete.setText(getString(R.string.reset));

                // Set dialog title and message
                dialogTitle.setText(getString(R.string.confirm_reset_password));
                dialogMessage.setText(getString(R.string.are_you_sure_to_reset_your_password_we_will_send_you_otp_in_your_email));


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

                        Bundle bundle = new Bundle();
                        bundle.putString("email", etEmail.getText().toString());
                        bundle.putBoolean("userEdit",true );

                        Intent intent = new Intent(EditUserActivity.this, ForgotPassOtpVerifyActivity.class);
                        intent.putExtras(bundle);
                        startActivity(intent);


                        dialog.dismiss();
                    }
                });

                dialog.show();







            }
        });

        findViewById(R.id.backEdit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isEdited){


                    showExitDialog();
                }else {
                    finish();
                }


            }
        });
        findViewById(R.id.selectDocBtn).setOnClickListener(view -> {

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

                    }
                }).check();

            } else {
                Dexter.withContext(getApplicationContext()).withPermissions(Manifest.permission.READ_MEDIA_IMAGES).withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {

                        if (multiplePermissionsReport.areAllPermissionsGranted()) {

                            selectDoc();

                        }

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {

                        CustomToastNegative.create(getApplicationContext(),"Permission Required!");
                    }
                }).check();

            }


        });

        ConstraintLayout submitBtn = findViewById(R.id.progressBtnLay);

        TextView btnTxt = findViewById(R.id.btnTxt);

        btnTxt.setText("Save Changes");

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (isEdited) {


                    new LoginTask(etEmail.getText().toString().trim()
                            , etName.getText().toString().trim(), etProfession.getText().toString()
                            , ccpCountry.getFullNumberWithPlus(), null, null, null, null, null, dpUri).execute();


                } else {
                    CustomToastNegative.create(getApplicationContext(),"No Changes Made!");

                }


            }
        });


    }

    private void initPreferences() {

        preferences = getSharedPreferences("user", MODE_PRIVATE);
        ImageView user_dp = findViewById(R.id.user_dp);

        userID = preferences.getString("id", "null");
        Glide.with(this).load(preferences.getString("dp", "null")).circleCrop().into(user_dp);


        etName = findViewById(R.id.etName);
        etProfession = findViewById(R.id.etProfession);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        ccpCountry = findViewById(R.id.ccp);


        etName.setText(preferences.getString("name", "null"));
        etEmail.setText(preferences.getString("email", "null"));
        etProfession.setText(preferences.getString("profession", "null"));


        String countryCode = PhoneNumberUtilHelper.extractCountryCode(preferences.getString("phone", "null"));


        //        String countryCode = "+1";

        if (countryCode != null) {
            ccpCountry.setCountryForPhoneCode(Integer.parseInt(countryCode));
            etPhone.setText(preferences.getString("phone", "null").replace(countryCode, ""));

        }
        ccpCountry.registerCarrierNumberEditText(etPhone);


        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                isEdited = true;

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        etProfession.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                isEdited = true;

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                isEdited = true;

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                isEdited = true;

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


    }

    private void selectDoc() {




        ImagePickerBottomSheet bottomSheet = new ImagePickerBottomSheet(1010, 1016);
        bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
        bottomSheet.setImageSelectionListener(new ImagePickerBottomSheet.ImageSelectionListener() {
            @Override
            public void onImageSelected(Uri imageUri) {

                Intent intent = new Intent(EditUserActivity.this, ImageCompressActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString("image", "doc");
                bundle.putString("originalUri", String.valueOf(imageUri));
                bundle.putInt("length", 1);
                bundle.putInt("height", 1);
                intent.putExtras(bundle);
                startActivityForResult(intent, 10001);


            }

            @Override
            public void onImagePath(Uri imageUri) {


                Intent intent = new Intent(EditUserActivity.this, ImageCompressActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString("image", "doc");
                bundle.putString("originalUri", String.valueOf(imageUri));
                bundle.putInt("length", 1);
                bundle.putInt("height", 1);
                intent.putExtras(bundle);
                startActivityForResult(intent, 10001);


            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 10001) {
            if (resultCode == RESULT_OK && data != null) {

                Bundle bundle = data.getExtras();

                if (bundle != null) {

                    String croppedImageUri = bundle.getString("croppedImageUri");

                    if (bundle.getString("image", "none").matches("doc")) {

                        ImageView imageView = findViewById(R.id.user_dp);
                        Glide.with(getApplicationContext()).load(croppedImageUri).circleCrop().into(imageView);
                        dpUri = getImageRealPathFromURI(Uri.parse(croppedImageUri));

                        isEdited = true;


                    }


                } else {
                }
            }

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


    private class LoginTask extends AsyncTask<Void, Void, Void> {

        String email, name, profession, phone, password, latitude, longitude, fullAddress, isBlocked, imagePath;

        public LoginTask(String email, String name, String profession, String phone,
                         String password, String latitude, String longitude, String fullAddress,
                         String isBlocked, String imagePath) {
            this.email = email;
            this.name = name;
            this.profession = profession;
            this.phone = phone;
            this.password = password;
            this.latitude = latitude;
            this.longitude = longitude;
            this.fullAddress = fullAddress;
            this.isBlocked = isBlocked;
            this.imagePath = imagePath;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

            // Create RequestBody instances from parameters


            RequestBody emailBody = createRequestBody(email);
            RequestBody nameBody = createRequestBody(name);
            RequestBody professionBody = createRequestBody(profession);
            RequestBody phoneBody = createRequestBody(phone);
            RequestBody passwordBody = createRequestBody(password);
            RequestBody latitudeBody = createRequestBody(latitude);
            RequestBody longitudeBody = createRequestBody(longitude);
            RequestBody fullAddressBody = createRequestBody(fullAddress);
            RequestBody isBlockedBody = createRequestBody(isBlocked);

            Log.d("TESTDETAISL", "doInBackground: " + email);
            Log.d("TESTDETAISL", "doInBackground: " + name);
            Log.d("TESTDETAISL", "doInBackground: " + profession);
            Log.d("TESTDETAISL", "doInBackground: " + phone);
            Log.d("TESTDETAISL", "doInBackground: " + password);
            Log.d("TESTDETAISL", "doInBackground: " + latitude);
            Log.d("TESTDETAISL", "doInBackground: " + longitude);
            Log.d("TESTDETAISL", "doInBackground: " + fullAddress);
            Log.d("TESTDETAISL", "doInBackground: " + isBlocked);

            MultipartBody.Part profilePicturePart = null;
            if (imagePath != null) {

                profilePicturePart = createImagePart(imagePath);


            }


            Log.d("TESTDETAISL", "doInBackground: " + profilePicturePart);

//            apiInterface.updateUserWithImage(
//                    emailBody, nameBody, professionBody, phoneBody, passwordBody,
//                    latitudeBody, longitudeBody, fullAddressBody, isBlockedBody, profilePicturePart
//            ).enqueue(new Callback<LoginResponse>() {
//                @Override
//                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
//                    Log.d("TESTDETAISL", "ON RES ");
//
//                    if (response.isSuccessful()) {
//
//                        Log.d("TESTDETAISL", "RES SUCCESS ");
//
//                    }
//
//                }
//
//                @Override
//                public void onFailure(Call<LoginResponse> call, Throwable t) {
//                    Log.d("TESTDETAISL", "ON RES FAIL  " + t.getMessage());
//
//                }
//            });

            apiInterface.updateUserWithImage(emailBody, nameBody, professionBody, phoneBody, passwordBody, latitudeBody, longitudeBody
                    , fullAddressBody, isBlockedBody, profilePicturePart).enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {


                    if (response.isSuccessful()) {

                        LoginResponse loginResponse = response.body();

                        if (loginResponse != null && !loginResponse.isError()) {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    CustomToastPositive.create(getApplicationContext()," "+loginResponse.getMessage());

                                }
                            });

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


                            editor.apply();


                            finish();


                        } else {
                            if (loginResponse != null) {


                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        CustomToastNegative.create(getApplicationContext()," "+loginResponse.getMessage());

                                    }
                                });
                            }
                        }


                    } else {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {


                            }
                        });
                    }


                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                        }
                    });

                }
            });


            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            btnProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // This method runs on the UI thread and can be used to update the UI after background task completion
            btnProgressBar.setVisibility(View.GONE);


        }


    }

    private RequestBody createRequestBody(String value) {
        // Check for null and return appropriate RequestBody
        if (value != null) {
            return RequestBody.create(MediaType.parse("text/plain"), value);
        } else {
            return null; // or handle it in a way that makes sense for your API
        }
    }

    private MultipartBody.Part createImagePart(String imagePath) {
        // Check if imagePath is not null
        if (imagePath != null) {
            File imageFile = new File(imagePath);
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), imageFile);
            return MultipartBody.Part.createFormData("profile_picture", imageFile.getName(), requestFile);
        } else {
            // Return a default MultipartBody.Part or handle it as per your API requirements
            // For example, return an empty MultipartBody.Part
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), "");
            return MultipartBody.Part.createFormData("profile_picture", "", requestFile);
        }
    }


    @Override
    public void onBackPressed() {

        if (isEdited) {

          showExitDialog();


        } else {

            super.onBackPressed();

        }

    }

    private void showExitDialog() {


        Dialog dialog = new Dialog(EditUserActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_delete_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Initialize views in the custom dialog
        TextView dialogTitle = dialog.findViewById(R.id.dialogTitle);
        TextView dialogMessage = dialog.findViewById(R.id.dialogMessage);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        Button btnDelete = dialog.findViewById(R.id.btnDelete);

        btnDelete.setText(getString(R.string.exit));

        // Set dialog title and message
        dialogTitle.setText(getString(R.string.exit_edit_page));
        dialogMessage.setText(getString(R.string.exitpagemessage));


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
                finish();


                dialog.dismiss();
            }
        });

        dialog.show();



    }
}