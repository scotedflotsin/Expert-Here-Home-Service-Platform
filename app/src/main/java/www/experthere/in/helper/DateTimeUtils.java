package www.experthere.in.helper;

import android.annotation.SuppressLint;
import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateTimeUtils {

    // Method to convert milliseconds to desired format with Indian Standard Time
    @SuppressLint("SimpleDateFormat")
    public static String formatTimestamp(Context context, long milliseconds) {
        // Define output date format
        SimpleDateFormat outputFormat = new SimpleDateFormat("d MMMM yyyy, h:mm a", Locale.getDefault());

        // Set time zone to Indian Standard Time
        outputFormat.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));

        // Convert milliseconds to Date object
        Date date = new Date(milliseconds);

        // Format the Date object into the desired format
        return outputFormat.format(date);
    }

    public static boolean isTimeDifferenceLessThanOneMinute(long givenTimeMillis) {
        // Get the current time in milliseconds
        long currentTimeMillis = System.currentTimeMillis();

        // Calculate the time difference in milliseconds
        long timeDifferenceMillis = Math.abs(currentTimeMillis - givenTimeMillis);

        // Convert milliseconds to seconds
        long timeDifferenceSeconds = timeDifferenceMillis / 1000;

        // Check if the time difference is less than 1 minute (60 seconds)
        return timeDifferenceSeconds < 60;
    }


}
