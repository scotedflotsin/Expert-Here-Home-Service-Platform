package www.experthere.in.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClientDeeplink {
    private static final String BASE_URL = "https://experthere.in/";

    private static Retrofit retrofit = null;
    private static final int MAX_RETRY_ATTEMPTS = 5;

    public static Retrofit getClient() {

//
//        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(5, TimeUnit.MINUTES)
//                .readTimeout(5, TimeUnit.MINUTES)
//                .writeTimeout(5, TimeUnit.MINUTES)
//                .build();
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.MINUTES)
                .readTimeout(5, TimeUnit.MINUTES)
                .writeTimeout(5, TimeUnit.MINUTES)
                .addInterceptor(chain -> {
                    Request request = chain.request();
                    Response response = chain.proceed(request);

                    for (int attemptCount = 0; attemptCount < MAX_RETRY_ATTEMPTS; attemptCount++) {
                        if (response.isSuccessful()) {
                            break;
                        }

                        // Log retry attempt or perform other actions if needed

                        // Wait for a while before making the next attempt (adjust as needed)
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }

                        // Retry the request
                        response = chain.proceed(request);
                    }

                    return response;
                })
                .build();

        Gson gson = new GsonBuilder()
                .setLenient() // Set lenient mode here
                .create();
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }

}
