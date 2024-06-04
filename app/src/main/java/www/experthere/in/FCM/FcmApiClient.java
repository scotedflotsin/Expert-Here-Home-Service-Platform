package www.experthere.in.FCM;

import android.util.Log;
import com.google.gson.Gson;
import java.util.Map;
import okhttp3.*;
import www.experthere.in.FCM.model.FCMNotificationPayload;

public class FcmApiClient {

    private static final OkHttpClient client = new OkHttpClient();
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static void sendFcmMessageToTopic(String url, String authToken, String topic, String title,
                                      String body, String extraData, String clickAction,
                                      Callback callback) {


        String jsonBody = "{"
                + "\"message\": {"
                + "\"topic\": \"" + topic + "\","
                + "\"notification\": {"
                + "\"title\": \"" + title + "\","
                + "\"body\": \"" + body + "\""
                + "},"
                + "\"data\": {"
                + "\"extra_data\": \"" + extraData + "\""
                + "},"
                + "\"android\": {"
                + "\"notification\": {"
                + "\"click_action\": \"" + clickAction + "\""
                + "}"
                + "}"
                + "}"
                + "}";

        RequestBody requestBody = RequestBody.create(jsonBody, JSON);
        Request request = new Request.Builder()
                .url(url)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + authToken)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(callback);
    }

    public static void sendFcmMessageToDevice(String url, String authToken, String DEVICE_TOKEN_HERE, String title,
                                      String body, Map<String,String> EXTRA_DATA, String clickAction,
                                      Callback callback) {

        FCMNotificationPayload.Notification notification = new FCMNotificationPayload.Notification(title,body);

        FCMNotificationPayload.Notification2 notification2 = new FCMNotificationPayload.Notification2(clickAction,"incomming_calls");

        FCMNotificationPayload.Android android = new FCMNotificationPayload.Android(notification2);


        FCMNotificationPayload.Message message = new FCMNotificationPayload.Message(DEVICE_TOKEN_HERE,EXTRA_DATA,null);

        FCMNotificationPayload payload = new FCMNotificationPayload(message);
//        String jsonBody = "{"
//                + "\"message\": {"
//                + "\"token\": \"" + DEVICE_TOKEN_HERE + "\","
//                + "\"notification\": {"
//                + "\"title\": \"" + title + "\","
//                + "\"body\": \"" + body + "\""
//                + "},"
//                + "\"data\": {"
//                + "\"extra_data\": \"" + extraData + "\""
//                + "},"
//                + "\"android\": {"
//                + "\"notification\": {"
//                + "\"click_action\": \"" + clickAction + "\""
//                + "}"
//                + "}"
//                + "}"
//                + "}";
        Gson gson = new Gson();
        String jsonBody = gson.toJson(payload);

        Log.d("JSONOAYLOIAD", "sendFcmMessageToDevice: "+ jsonBody);

        RequestBody requestBody = RequestBody.create(jsonBody, JSON);
        Request request = new Request.Builder()
                .url(url)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + authToken)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(callback);
    }
}
