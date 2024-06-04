package www.experthere.in.FCM.model;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

public class FCMNotificationPayload {
    @SerializedName("message")
    private final Message message;

    public FCMNotificationPayload(Message message) {
        this.message = message;
    }

    public static class Message {
        @SerializedName("token")
        private final String token;
//        @SerializedName("notification")
//        private Notification notification;
        @SerializedName("data")
        private final Map<String,String> data;
        @SerializedName("android")
        private final Android android;

        public Message(String token, Map<String, String> data, Android android) {
            this.token = token;
//            this.notification = notification;
            this.data = data;
            this.android = android;
        }
    }

    public static class Notification {
        @SerializedName("title")
        private final String title;
        @SerializedName("body")
        private final String body;




        public Notification(String title, String body) {
            this.title = title;
            this.body = body;
        }
    }


    public static class Android {
        @SerializedName("notification")
        private final Notification2 androidNotification;

        public Android(Notification2 androidNotification) {
            this.androidNotification = androidNotification;
        }

    }

    public static class Notification2 {

        @SerializedName("click_action")
        private String click_action;

        String icon;

        public Notification2(String click_action, String icon) {
            this.click_action = click_action;
            this.icon = icon;
        }


        public void setClick_action(String click_action) {
            this.click_action = click_action;
        }
    }
}
