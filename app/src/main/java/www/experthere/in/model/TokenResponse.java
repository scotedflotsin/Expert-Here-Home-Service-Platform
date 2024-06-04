package www.experthere.in.model;

public class TokenResponse {

    boolean success;
    String message;
    FCMData data;

    public String getMessage() {
        return message;
    }


    public boolean isSuccess() {
        return success;
    }

    public FCMData getData() {
        return data;
    }


}

