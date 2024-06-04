package www.experthere.in.helper;

import android.content.Context;
import android.content.SharedPreferences;

public class NightModeHelper {
    private static final String PREFS_NAME = "settings";
    private static final String NIGHT_MODE_KEY = "night_mode_enabled";

    // Save night mode setting
    public static void saveNightModeSetting(Context context, boolean isEnabled) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putBoolean(NIGHT_MODE_KEY, isEnabled);
        editor.apply();
    }

    // Retrieve night mode setting
    public static boolean isNightModeEnabled(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(NIGHT_MODE_KEY, false); // Default value is false (night mode disabled)
    }
}
