package www.experthere.in.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TimeAgoConverter {

    public static String getTimeAgo(String timestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        try {
            Date pastDate = dateFormat.parse(timestamp);
            Date currentDate = new Date();

            long timeDifference = currentDate.getTime() - pastDate.getTime();
            long seconds = timeDifference / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            long days = hours / 24;
            long weeks = days / 7;
            long months = days / 30;
            long years = days / 365;

            if (seconds < 60) {
                return seconds + " seconds ago";
            } else if (minutes < 60) {
                return minutes + " minutes ago";
            } else if (hours < 24) {
                return hours + " hours ago";
            } else if (days < 7) {
                return days + " days ago";
            } else if (weeks < 4) {
                return weeks + " weeks ago";
            } else if (months < 12) {
                return months + " months ago";
            } else {
                return years + " years ago";
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timestamp; // Return the original timestamp if parsing fails
    }
}
