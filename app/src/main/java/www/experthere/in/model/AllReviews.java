package www.experthere.in.model;

public class AllReviews {

    String review_id, user_id, service_id, provider_id, review
            , stars, review_image_url, created_at,profile_picture
            ,user_name,service_title;

    public String getService_title() {
        return service_title;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getProfile_picture() {
        return profile_picture;
    }

    public String getReview_id() {
        return review_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getService_id() {
        return service_id;
    }

    public String getProvider_id() {
        return provider_id;
    }

    public String getReview() {
        return review;
    }

    public String getStars() {
        return stars;
    }

    public String getReview_image_url() {
        return review_image_url;
    }

    public String getCreated_at() {
        return created_at;
    }
}
