package www.experthere.in.model;

import com.google.gson.annotations.SerializedName;

public class UpdateServiceResponse {

    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    // Add other fields as needed

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

}
