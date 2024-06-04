package www.experthere.in.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import www.experthere.in.MyApplication;

public class BootCompletedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Log.d("BootCompletedReceiver", "Boot completed!");

            // Access MyApplication instance
            MyApplication myApplication = (MyApplication) context.getApplicationContext();
            if (myApplication != null) {
                // Call any methods or access any fields of MyApplication here
                boolean appInForeground = myApplication.isAppInForeground();
                Log.d("BootCompletedReceiver", "Is app in foreground? " + appInForeground);
            }
        }
    }
}