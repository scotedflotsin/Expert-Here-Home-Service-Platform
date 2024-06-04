package www.experthere.in.calling;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class OutGoingNotificationActionReceiver extends BroadcastReceiver {
    public static final String ACTION_STOP = "www.experthere.in.calling.STOP";
    private static final String TAG = "OUTGOINGRECEIVERCHECK";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals(ACTION_STOP)) {

            Log.d(TAG, "onReceive: Dismiss call action received");

            // Send a broadcast to finish the OutGoingCallActivity
            Intent finishIntent = new Intent();
            finishIntent.setAction(OutGoingCallActivity.ACTION_STOP);
            context.sendBroadcast(finishIntent);
//
//            // Send a broadcast to stop the OutGoingCallService
//            Intent stopServiceIntent = new Intent();
//            stopServiceIntent.setAction(OutGoingCallService.ACTION_STOP);
//            context.sendBroadcast(stopServiceIntent);
        }
    }
}
