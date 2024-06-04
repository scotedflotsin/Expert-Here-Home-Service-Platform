package www.experthere.in;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.google.firebase.messaging.FirebaseMessagingService;

import www.experthere.in.helper.FinishAppReceiver;

public class MyApplication extends Application implements Application.ActivityLifecycleCallbacks {

    private static MyApplication instance;
    private int foregroundActivities = 0;

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        registerActivityLifecycleCallbacks(this);

        ComponentName componentName = new ComponentName(this, FirebaseMessagingService.class);
        getPackageManager().setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

        // Register the FinishAppReceiver in the onCreate method
        FinishAppReceiver finishAppReceiver = new FinishAppReceiver();
        registerReceiver(finishAppReceiver, new IntentFilter("www.experthere.in.action.FINISH_APP"), Context.RECEIVER_NOT_EXPORTED);
    }

    public static MyApplication getInstance() {
        return instance;
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        foregroundActivities++;

    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        foregroundActivities--;

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }

    public boolean isAppInForeground() {
        return foregroundActivities > 0;
    }
}
