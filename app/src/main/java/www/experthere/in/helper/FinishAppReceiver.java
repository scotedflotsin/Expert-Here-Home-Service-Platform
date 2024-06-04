package www.experthere.in.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import www.experthere.in.MyApplication;

public class FinishAppReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals("www.experthere.in.action.FINISH_APP")) {
            // Finish all activities and exit the application
            MyApplication.getInstance().onTerminate();
            System.exit(0);
        }
    }
}
