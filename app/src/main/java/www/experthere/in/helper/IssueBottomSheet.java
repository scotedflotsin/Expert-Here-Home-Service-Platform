package www.experthere.in.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;

import www.experthere.in.Common.BrowserActivity;
import www.experthere.in.Common.SendEmailTask;
import www.experthere.in.R;
import www.experthere.in.api.ApiClient;
import www.experthere.in.api.ApiInterface;

public class IssueBottomSheet extends BottomSheetDialogFragment {

    Activity activity;

    ProgressBar progressEmail;
    Button submitTicketBtn;


    int availableCount = 500;


    public IssueBottomSheet(Activity activity) {
        this.activity = activity;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.issue_bottom_sheet, container, false);


        TextInputEditText issueET = view.findViewById(R.id.etAddress);
        submitTicketBtn = view.findViewById(R.id.submitTicketBtn);
        TextView learnMoreTxt = view.findViewById(R.id.learnMoreTxt);
        TextView countTxt = view.findViewById(R.id.countTxt);
        progressEmail = view.findViewById(R.id.progressEmail);



        issueET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                countTxt.setText(s.length() + "/500");


                if (s.length() >= availableCount) {
                    countTxt.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.shake));

                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        learnMoreTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                SharedPreferences sharedPref = activity.getSharedPreferences("keys", Context.MODE_PRIVATE);

                String websiteUrl = sharedPref.getString("privacy_url", "null");



                Intent intent = new Intent(activity, BrowserActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString("title", "Expert Here");
                bundle.putString("url", websiteUrl);

                intent.putExtras(bundle);
                activity.startActivity(intent);


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
                        CustomToastNegative.create(activity,"Enter Message To Submit! " );

                    }
                }

            }
        });


        return view;
    }

    private void sendEmailToAdmin(String message) {



        ProcessingDialog processingDialog = new ProcessingDialog(activity);
        processingDialog.show();




        SharedPreferences preferences = activity.getSharedPreferences("user", Context.MODE_PRIVATE);


        String email = preferences.getString("email", "0");
        String name = preferences.getString("name", "0");
        String to = "issues@experthere.in";
        String subject = "User : Rate Issue";

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        SendEmailTask sendEmailTask = new SendEmailTask(apiInterface, new SendEmailTask.SendOtpListener() {
            @Override
            public void onSendOtpSuccess(String response) {

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        processingDialog.dismiss();

                        CustomToastPositive.create(activity,"Ticket Submitted!");
                    }
                });


                dismiss();
            }

            @Override
            public void onSendOtpFailure(String errorMessage) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        processingDialog.dismiss();
                        CustomToastNegative.create(activity,"Fail To Submit Ticket!" );

                    }
                });

            }
        }, email, name, message, subject, to);


        sendEmailTask.execute();


    }



    private void hideKeyboard() {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


}
