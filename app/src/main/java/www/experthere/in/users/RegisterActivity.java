package www.experthere.in.users;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.hbb20.CountryCodePicker;

import www.experthere.in.helper.CustomToastNegative;
import www.experthere.in.helper.EmailValidator;
import www.experthere.in.R;
import www.experthere.in.helper.InternetReceiver;

public class RegisterActivity extends AppCompatActivity {

    TextInputEditText passEt,nameEt, professionEt, phoneEt, emailEt;

    BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.white, this.getTheme()));
        setContentView(R.layout.activity_register);

        receiver = new InternetReceiver();

        internetStatus();


        nameEt = findViewById(R.id.etName);
        professionEt = findViewById(R.id.etProfession);
        phoneEt = findViewById(R.id.etPhone);
        emailEt = findViewById(R.id.etEmail);
        passEt = findViewById(R.id.editTextPassword);

        CountryCodePicker ccp = findViewById(R.id.ccp);
        ccp.registerCarrierNumberEditText(phoneEt);



        TextView textView = findViewById(R.id.btnTxt);
        textView.setText("Send OTP");


        findViewById(R.id.loginBtnRegisterActivity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();

            }
        });



        findViewById(R.id.progressBtnLay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!nameEt.getText().toString().isEmpty()){
                    if (!professionEt.getText().toString().isEmpty()){
                        if (!phoneEt.getText().toString().isEmpty()){
                            if (!emailEt.getText().toString().isEmpty()){
                                if (!passEt.getText().toString().isEmpty()){


                                    if (EmailValidator.isValidEmail(emailEt.getText().toString())){

                                        if (ccp.isValidFullNumber()){

                                            Bundle bundle = new Bundle();
                                            bundle.putString("name",nameEt.getText().toString().trim());
                                            bundle.putString("profession",professionEt.getText().toString().trim());
                                            bundle.putString("phone", ccp.getFullNumberWithPlus().trim());
                                            bundle.putString("email",emailEt.getText().toString().trim().toLowerCase());
                                            bundle.putString("pass",passEt.getText().toString().trim());

                                            Intent intent = new Intent(RegisterActivity.this,OtpSendActivity.class);
                                            intent.putExtras(bundle);
                                            startActivity(intent);
                                            finish();



                                        }else {

                                            CustomToastNegative.create(getApplicationContext(),"Wrong Number");

                                        }


                                    }else {
                                        CustomToastNegative.create(getApplicationContext(),"Email Is Invalid..!");

                                    }


                                }else {

                                    passEt.setError("Enter Password");
                                }

                            }else {

                                emailEt.setError("Enter Email");
                            }

                        }else {

                            phoneEt.setError("Enter Phone");
                        }

                    }else {

                        professionEt.setError("Enter Profession");
                    }
                }else {

                    nameEt.setError("Enter Name");
                }

            }
        });







    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(RegisterActivity.this, IntroActivity.class));
        super.onBackPressed();
    }

    private void internetStatus() {

        registerReceiver(receiver,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(receiver);
    }
}