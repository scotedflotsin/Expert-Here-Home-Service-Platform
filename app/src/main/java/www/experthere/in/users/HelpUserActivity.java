package www.experthere.in.users;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import www.experthere.in.Common.BrowserActivity;
import www.experthere.in.Common.SendEmailTask;
import www.experthere.in.R;
import www.experthere.in.api.ApiClient;
import www.experthere.in.api.ApiInterface;
import www.experthere.in.helper.CustomToastNegative;
import www.experthere.in.helper.ProcessingDialog;

public class HelpUserActivity extends AppCompatActivity {


    ProgressBar progressEmail;
    Button submitTicketBtn;

    TextInputEditText issueET;
    int availableCount = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(getResources().getColor(R.color.white, this.getTheme()));

        setContentView(R.layout.activity_help_user);


        findViewById(R.id.backEdit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

         issueET = findViewById(R.id.etAddress);
        submitTicketBtn = findViewById(R.id.submitTicketBtn);
        TextView learnMoreTxt = findViewById(R.id.learnMoreTxt);
        TextView countTxt = findViewById(R.id.countTxt);
        progressEmail = findViewById(R.id.progressEmail);



        issueET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                countTxt.setText(s.length() + "/500");


                if (s.length() >= availableCount) {
                    countTxt.startAnimation(AnimationUtils.loadAnimation(HelpUserActivity.this, R.anim.shake));

                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        SharedPreferences sharedPref = getSharedPreferences("keys", Context.MODE_PRIVATE);

        String websiteUrl = sharedPref.getString("privacy_url", "null");



        learnMoreTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(HelpUserActivity.this, BrowserActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString("title", "Expert Here");
                bundle.putString("url", websiteUrl);

                intent.putExtras(bundle);
                startActivity(intent);


            }
        });


        submitTicketBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hideKeyboard();


                if (issueET.getText() != null) {

                    if (!issueET.getText().toString().trim().isEmpty()) {

                        sendEmailToAdmin(issueET.getText().toString().trim());

                    } else {
                        CustomToastNegative.create(getApplicationContext(),"Enter Message To Submit!");
                    }
                }

            }
        });



    }


    private void sendEmailToAdmin(String message) {



        ProcessingDialog processingDialog = new ProcessingDialog(HelpUserActivity.this);
        processingDialog.show();




        SharedPreferences preferences = getSharedPreferences("user", Context.MODE_PRIVATE);


        String email = preferences.getString("email", "0");
        String name = preferences.getString("name", "0");
        String to = "issues@experthere.in";
        String subject = "User : General Help";

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        SendEmailTask sendEmailTask = new SendEmailTask(apiInterface, new SendEmailTask.SendOtpListener() {
            @Override
            public void onSendOtpSuccess(String response) {

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        processingDialog.dismiss();

                        issueET.setText("");
                        Toast.makeText(HelpUserActivity.this, "Ticket Submitted!", Toast.LENGTH_SHORT).show();
                    }
                });


            }

            @Override
            public void onSendOtpFailure(String errorMessage) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        processingDialog.dismiss();
                        Toast.makeText(HelpUserActivity.this, "Fail To Submit Ticket!", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }, email, name, message, subject, to);


        sendEmailTask.execute();


    }



    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}