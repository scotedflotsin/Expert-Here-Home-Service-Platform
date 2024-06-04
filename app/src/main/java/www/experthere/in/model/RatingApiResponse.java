package www.experthere.in.model;

import com.google.gson.annotations.SerializedName;

public class RatingApiResponse {

    @SerializedName("success")
    private boolean success;

    @SerializedName("ratings_data")
    private RatingsData ratingsData;

    public boolean isSuccess() {
        return success;
    }

    public RatingsData getRatingsData() {
        return ratingsData;
    }
}
