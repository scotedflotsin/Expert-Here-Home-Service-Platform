package www.experthere.in.users;

import android.os.AsyncTask;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;
import www.experthere.in.api.ApiInterface;

public class SendOtpTask extends AsyncTask<String, Void, Void>
{
    private final ApiInterface apiService;
    private final SendOtpListener listener;

    public interface SendOtpListener {
        void onSendOtpSuccess(String response );

        void onSendOtpFailure(String errorMessage);
    }

    public SendOtpTask(ApiInterface apiService, SendOtpListener listener) {
        this.apiService = apiService;
        this.listener = listener;
    }

    @Override
    protected Void doInBackground(String... params) {
        String email = params[0];
        String name = params[1];
        String otp = params[2];
        String subject = params[3];

        Call<Void> call = apiService.sendOtp(email, name, otp, subject);

        try {
            Response<Void> response = call.execute();

            if (response.isSuccessful()) {
                // Callback to UI thread on success
                listener.onSendOtpSuccess("Response - "+response.message());
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
