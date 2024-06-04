package www.experthere.in.helper.app;

import android.annotation.SuppressLint;
import android.app.MissingForegroundServiceTypeException;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import www.experthere.in.R;
import www.experthere.in.helper.NotificationHelper;

public class OrderNotifcationService extends Service {


    String ACTION_DISMISS_NOTIFICATION = "www.experthere.in.helper.app.ACTION_FINISH_ACTIVITY";
    private NotificationHelper notificationHelper;


    private final BroadcastReceiver stopServiceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null && intent.getAction().equals(ACTION_DISMISS_NOTIFICATION)) {

                stopForeground(true);

                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                notificationManager.cancel(9919);

                stopSelf();

            }
        }
    };

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    public void onCreate() {
        super.onCreate();

        IntentFilter intentFilter = new IntentFilter(ACTION_DISMISS_NOTIFICATION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(stopServiceReceiver, intentFilter, Context.RECEIVER_NOT_EXPORTED);
        }else {
            registerReceiver(stopServiceReceiver, intentFilter);

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Initialize the notification helper
        if (intent.getExtras()!=null) {

            String title = intent.getExtras().getString("title");
            String content = intent.getExtras().getString("content");

            notificationHelper = new NotificationHelper(
                    getString(R.string.app_name) + "orders",
                    getString(R.string.app_name) + "orders",
                    getString(R.string.app_name) + "orders",
                    true
            );

            // Create and show the notification
//            notificationHelper.createNotification(this, title, content);

            // Perform your service tasks here

            // Return START_STICKY to ensure the service restarts if it's killed by the system


            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForeground(9919, notificationHelper.getNotification(this, title, content));
                }

            }catch (MissingForegroundServiceTypeException ex){
                ex.printStackTrace();
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }

        }

            return START_STICKY;


    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(stopServiceReceiver);

    }
}
