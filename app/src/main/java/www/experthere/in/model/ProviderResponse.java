package www.experthere.in.model;

import com.google.gson.annotations.SerializedName;

public class ProviderResponse {

    @SerializedName("message")
    private String message;

    @SerializedName("success")
    private boolean success;

    @SerializedName("data")
    private ProviderData data;

    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return success;
    }

    public ProviderData getData() {
        return data;
    }
}
