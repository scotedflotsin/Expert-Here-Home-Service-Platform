package www.experthere.in.Common;

import android.os.AsyncTask;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;
import www.experthere.in.api.ApiInterface;

public class SendEmailTask extends AsyncTask<String, Void, Void>
{
    private final ApiInterface apiService;
    private final SendOtpListener listener;


    String email,name,message,subject,to;


    public interface SendOtpListener {
        void onSendOtpSuccess(String response );

        void onSendOtpFailure(String errorMessage);
    }

    public SendEmailTask(ApiInterface apiService, SendOtpListener listener, String email, String name, String message, String subject, String to) {
        this.apiService = apiService;
        this.listener = listener;
        this.email = email;
        this.name = name;
        this.message = message;
        this.subject = subject;
        this.to = to;
    }

    @Override
    protected Void doInBackground(String... params) {


        Call<Void> call = apiService.sendEmail(email, name, message, subject,to);

        try {
            Response<Void> response = call.execute();

            if (response.isSuccessful()) {
                // Callback to UI thread on success
                listener.onSendOtpSuccess("Ticket Submitted!");

            } else {
                // Callback to UI thread on failure
                listener.onSendOtpFailure("Failed to send message. Error: " + response.message());
            }
        } catch (IOException e) {
            // Callback to UI thread on exception
            listener.onSendOtpFailure("Failed to send message. Exception: " + e.getMessage());
        }

        return null;
    }

    // Override onPostExecute if necessary
    @Override
    protected void onPostExecute(Void aVoid) {
        // Update UI or perform any post-execution tasks
    }
}
