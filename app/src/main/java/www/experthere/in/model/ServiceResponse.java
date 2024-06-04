package www.experthere.in.model;

import java.util.ArrayList;

public class ServiceResponse {

    boolean success;
    String message;
    ArrayList<Services> services;

    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return success;
    }

    public ArrayList<Services> getServices() {
        return services;
    }
}
