package www.experthere.in.users;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import www.experthere.in.R;
import www.experthere.in.api.ApiClient;
import www.experthere.in.api.ApiInterface;
import www.experthere.in.helper.CustomToastNegative;
import www.experthere.in.helper.CustomToastPositive;
import www.experthere.in.helper.EmailMaskUtil;
import www.experthere.in.helper.ExitBottomSheet;
import www.experthere.in.model.ApiResponse;
import www.experthere.in.model.LoginResponse;
import www.experthere.in.serviceProvider.ProviderHome;

public class PasswordResetActivity extends AppCompatActivity {

    String email = " ";
    boolean isProvider = false;
    TextView emailTxt;


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ExitBottomSheet exitBottomSheet = new ExitBottomSheet(true);
        exitBottomSheet.show(getSupportFragmentManager(), exitBottomSheet.getTag());

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(getResources().getColor(R.color.white, this.getTheme()));

        setContentView(R.layout.activity_password_reset);

        emailTxt = findViewById(R.id.emailTxt);

        TextView textView = findViewById(R.id.btnTxt);
        textView.setText("update new password");


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {


            email = bundle.getString("email").toLowerCase();
            isProvider = bundle.getBoolean("isProvider", false);

            EmailMaskUtil.maskEmailAndSetToTextView(email, emailTxt);
        }

        EditText passEt = findViewById(R.id.passEt);
        TextInputEditText confirmPassEt = findViewById(R.id.confirmPassEt);


        findViewById(R.id.progressBtnLay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!passEt.getText().toString().isEmpty() && !confirmPassEt.getText().toString().isEmpty()) {

                    if (passEt.getText().toString().equals(confirmPassEt.getText().toString())) {


                        showConfirmDialog(isProvider, PasswordResetActivity.this, email, "password", passEt.getText().toString().trim());


                    } else {


                        Log.d("JGJGHJGJH", "pass: " + passEt.getText().toString());
                        Log.d("JGJGHJGJH", "confirm Pass: " + confirmPassEt.getText().toString());


                        CustomToastNegative.create(getApplicationContext(),"Password Don't Match!");

                    }


                } else {

                    CustomToastNegative.create(getApplicationContext(),"Enter Password And Confirm Password!");

                }


            }
        });


    }

    private void showConfirmDialog(boolean isProvider, PasswordResetActivity passwordResetActivity, String email, String password, String newPass) {


        // Create custom dialog
        Dialog dialog = new Dialog(PasswordResetActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_delete_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Initialize views in the custom dialog
        TextView dialogTitle = dialog.findViewById(R.id.dialogTitle);
        TextView dialogMessage = dialog.findViewById(R.id.dialogMessage);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        Button btnDelete = dialog.findViewById(R.id.btnDelete);
        btnDelete.setText("Reset");

        // Set dialog title and message
        dialogTitle.setText("Reset Password?");
        dialogMessage.setText("Are you sure you want to Reset Password ?");


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

                    if (isProvider) {


                        new UpdateProviderDetailsTask(getApplicationContext(), passwordResetActivity, email, password, newPass).execute();

                    } else
                        new UpdateUserDetailsTask(getApplicationContext(), passwordResetActivity, email, password, newPass).execute();


                } catch (Exception e) {
                    throw new RuntimeException(e);
                }


                dialog.dismiss();
            }
        });

        dialog.show();





}


private static class UpdateProviderDetailsTask extends AsyncTask<Void, Void, Void> {
    private final Context context;
    private final Activity activity;
    private final String email;
    private final String fieldToEdit;
    private final String newValue;


    UpdateProviderDetailsTask(Context context, Activity activity, String email, String fieldToEdit, String newValue) {
        this.context = context;
        this.email = email;
        this.fieldToEdit = fieldToEdit;
        this.newValue = newValue;
        this.activity = activity;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        apiInterface.updateProviderDetails(email, fieldToEdit, newValue).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful()) {
                    ApiResponse apiResponse = response.body();

                    if (apiResponse != null && !apiResponse.isError()) {

                        CustomToastPositive.create(context,"Password Reset Successful!");
                        // Handle success, if needed

                        activity.runOnUiThread(() ->
                                activity.finish()

                        );


                    } else {
                        if (apiResponse != null) {
                            CustomToastNegative.create(context,"error: "+apiResponse.getMessage());
                        }
                    }
                } else {
                    CustomToastNegative.create(context,"error Getting response");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                CustomToastNegative.create(context,"Error: "+t.getMessage());
            }
        });

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        // This method runs on the UI thread and can be used to update the UI after background task completion
        // You can perform any UI updates or navigate to another activity if needed
    }

}

private static class UpdateUserDetailsTask extends AsyncTask<Void, Void, Void> {
    private final Context context;
    private final Activity activity;
    private final String email;
    private final String fieldToEdit;
    private final String newValue;


    UpdateUserDetailsTask(Context context, Activity activity, String email, String fieldToEdit, String newValue) {
        this.context = context;
        this.email = email;
        this.fieldToEdit = fieldToEdit;
        this.newValue = newValue;
        this.activity = activity;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        apiInterface.updateUserWithoutImage(email, null, null, null, newValue, null, null, null, null).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    LoginResponse apiResponse = response.body();

                    if (apiResponse != null && !apiResponse.isError()) {

                        CustomToastPositive.create(context,"Password Reset Successful!");
                        // Handle success, if needed

                        activity.runOnUiThread(() ->
                                activity.finish()

                        );


                    } else {
                        if (apiResponse != null) {

                            CustomToastNegative.create(context,"error: "+apiResponse.getMessage());

                        }
                    }
                } else {
                    CustomToastNegative.create(context,"error getting Response ");
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                CustomToastNegative.create(context,"error: "+t.getMessage());
            }
        });

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        // This method runs on the UI thread and can be used to update the UI after background task completion
        // You can perform any UI updates or navigate to another activity if needed
    }

}

}