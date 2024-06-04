package www.experthere.in.model;

import com.google.gson.annotations.SerializedName;

public class ApiResponse {
    @SerializedName("error")
    private boolean error;

    @SerializedName("message")
    private String message;

    public boolean isError() {
        return error;
    }

    public String getMessage() {
        return message;
    }
}
