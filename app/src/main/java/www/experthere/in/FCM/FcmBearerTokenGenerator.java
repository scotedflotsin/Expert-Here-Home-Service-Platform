package www.experthere.in.FCM;

import android.annotation.SuppressLint;
import android.content.Context;

import com.google.auth.oauth2.GoogleCredentials;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FcmBearerTokenGenerator {

    private static final String[] SCOPES = {"https://www.googleapis.com/auth/firebase.messaging"};
    private static final String SERVICE_ACCOUNT_KEY_PATH = "raw/service";

    private final Context context;

    public FcmBearerTokenGenerator(Context context) {


        if (context != null) {
            this.context = context.getApplicationContext();
        } else {
            throw new IllegalArgumentException("Context cannot be null");
        }

    }

    public interface TokenGenerationListener {
        void onTokenGenerated(String token);
        void onTokenGenerationFailed(Exception e);
    }

    public void generateAccessTokenAsync(TokenGenerationListener listener) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                String token = getAccessToken();
                listener.onTokenGenerated(token);
            } catch (IOException e) {
                listener.onTokenGenerationFailed(e);
            }
        });
    }

    private String getAccessToken() throws IOException {
        @SuppressLint("DiscouragedApi") InputStream serviceAccountFile = context.getResources().openRawResource(
                context.getResources().getIdentifier(SERVICE_ACCOUNT_KEY_PATH,
                        "raw", context.getPackageName()));
        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(serviceAccountFile)
                .createScoped(Arrays.asList(SCOPES));
        googleCredentials.refresh();
        serviceAccountFile.close();
        return googleCredentials.getAccessToken().getTokenValue();
    }
}
