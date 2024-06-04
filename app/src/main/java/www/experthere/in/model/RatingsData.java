package www.experthere.in.model;

import com.google.gson.annotations.SerializedName;

public class RatingsData {

    @SerializedName("total_ratings")
    private int totalRatings;

    @SerializedName("average_rating")
    private double averageRating;

    @SerializedName("one_star")
    private int oneStar;

    @SerializedName("two_star")
    private int twoStar;

    @SerializedName("three_star")
    private int threeStar;

    @SerializedName("four_star")
    private int fourStar;

    @SerializedName("five_star")
    private int fiveStar;

    public int getTotalRatings() {
        return totalRatings;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public int getOneStar() {
        return oneStar;
    }

    public int getTwoStar() {
        return twoStar;
    }

    public int getThreeStar() {
        return threeStar;
    }

    public int getFourStar() {
        return fourStar;
    }

    public int getFiveStar() {
        return fiveStar;
    }
}
