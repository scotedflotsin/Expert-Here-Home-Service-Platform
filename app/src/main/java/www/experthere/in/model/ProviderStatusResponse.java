package www.experthere.in.model;

public class ProviderStatusResponse {

    boolean success;
    String message;

    ProviderStatusData data;

    public ProviderStatusData getData() {
        return data;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }



}
