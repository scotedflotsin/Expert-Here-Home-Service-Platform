package www.experthere.in.model;

public class ReviewDetailsModel {

    boolean success;
    String message;

    Review review;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Review getReview() {
        return review;
    }
}
