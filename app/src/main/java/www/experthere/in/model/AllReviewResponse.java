package www.experthere.in.model;

import java.util.List;

public class AllReviewResponse {

    boolean success;
    List<AllReviews> reviews;

    String message;

    public boolean isSuccess() {
        return success;
    }

    public List<AllReviews> getReviews() {
        return reviews;
    }

    public String getMessage() {
        return message;
    }



}
