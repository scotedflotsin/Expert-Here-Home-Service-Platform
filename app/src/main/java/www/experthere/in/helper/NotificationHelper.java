package www.experthere.in.helper;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import www.experthere.in.R;

public class NotificationHelper {

    private final String channelId;
    private final String channelName;
    private final String channelDescription;
    private final boolean isNonDismissible;
    private int notificationId;

    public NotificationHelper(String channelId, String channelName, String channelDescription, boolean isNonDismissible) {
        this.channelId = channelId;
        this.channelName = channelName;
        this.channelDescription = channelDescription;
        this.isNonDismissible = isNonDismissible;
    }

    public Notification getNotification(Context context, String title, String content) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        if (isNonDismissible) {
            builder.setOngoing(true); // Set notification as non-dismissible
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager);
        }

        notificationId = 9919; // You can set a unique ID for each notification
        return builder.build();
    }

    public void dismissNotification(Context context, int notificationId) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(notificationId);
    }

    private void createNotificationChannel(NotificationManager notificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription(channelDescription);
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.enableVibration(true);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
