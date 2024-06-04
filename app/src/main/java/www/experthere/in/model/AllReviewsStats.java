package www.experthere.in.model;

import com.google.gson.annotations.SerializedName;

public class AllReviewsStats {

    boolean success;
    String message;
    @SerializedName("stats")

    Stats stats;


    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Stats getStats() {
        return stats;
    }
}
