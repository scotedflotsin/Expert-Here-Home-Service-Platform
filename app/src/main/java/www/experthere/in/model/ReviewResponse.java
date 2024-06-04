package www.experthere.in.model;

import java.util.List;

public class ReviewResponse {

    boolean success;
    List<Reviews> reviews;

    String message;

    public boolean isSuccess() {
        return success;
    }

    public List<Reviews> getReviews() {
        return reviews;
    }

    public String getMessage() {
        return message;
    }
}
