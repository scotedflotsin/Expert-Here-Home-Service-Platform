package www.experthere.in.model;

import com.google.gson.annotations.SerializedName;

public class ResponseProvider {

    @SerializedName("error")
    private boolean error;

    @SerializedName("message")
    private String message;

    @SerializedName("provider")
    private Provider provider;

    public boolean isError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public Provider getProvider() {
        return provider;
    }

}
