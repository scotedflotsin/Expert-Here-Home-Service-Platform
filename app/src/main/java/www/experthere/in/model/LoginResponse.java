package www.experthere.in.model;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("error")
    private boolean error;

    @SerializedName("message")
    private String message;

    @SerializedName("user")
    private User user; // Create a User class to model the user data

    public boolean isError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public User getUser() {
        return user;
    }
}
