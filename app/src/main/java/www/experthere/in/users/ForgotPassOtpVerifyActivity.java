package www.experthere.in.users;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
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

public class ForgotPassOtpVerifyActivity extends AppCompatActivity implements OnSubmitListener {
    long duration = 90000;
    long interval = 1000;
    TextView otpTimerTxt, resendTxt, emailForOtp;

    private CountDownTimer countDownTimer;

    String sentOtp, email;
    boolean isProvider,userEdit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(getResources().getColor(R.color.white, this.getTheme()));

        setContentView(R.layout.activity_forgot_pass_otp_verify);


        getDataFromIntentBundle();

        emailForOtp = findViewById(R.id.emailForOtp);
        otpTimerTxt = findViewById(R.id.otpTimerTxt);
        resendTxt = findViewById(R.id.resendTxt);
        TextInputEditText otpEt = findViewById(R.id.otpEt);

        resendTxt.setEnabled(false);
        resendTxt.setClickable(false);


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

        findViewById(R.id.completeVerificationBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!otpEt.getText().toString().isEmpty()) {
                    if (sentOtp.matches(otpEt.getText().toString().trim())) {


                        CustomToastPositive.create(getApplicationContext(),"Verification Complete!");
                        openResetPassActivity();


                    } else {

                        CustomToastNegative.create(getApplicationContext(),"Wrong Otp!");
                    }
                } else {
                    otpEt.setError("Enter Otp");
                }
            }
        });

    }

    private void openResetPassActivity() {

        Bundle bundle = new Bundle();

        bundle.putString("email", email);

        if (isProvider) {
            bundle.putBoolean("isProvider", true);
        }

        Intent intent = new Intent(ForgotPassOtpVerifyActivity.this, PasswordResetActivity.class);

        intent.putExtras(bundle);
        startActivity(intent);
        finish();


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

                    CustomToastPositive.create(getApplicationContext(),"Code Sent!");
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
        sendOtpTask.execute(mail, " ", OTP, "OTP Verification : Expert Here");


    }

    private void getDataFromIntentBundle() {

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {

            email = bundle.getString("email");
            userEdit = bundle.getBoolean("userEdit",false);

            isProvider = bundle.getBoolean("isProvider", false);




            if (userEdit){

                findViewById(R.id.changeEmailTxt).setEnabled(false);
            }

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


    @Override
    public void onBackPressed() {

        super.onBackPressed();
        ExitBottomSheet exitBottomSheet = new ExitBottomSheet(true);
        exitBottomSheet.show(getSupportFragmentManager(), exitBottomSheet.getTag());


    }
}