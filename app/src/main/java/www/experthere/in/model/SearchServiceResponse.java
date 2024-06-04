package www.experthere.in.model;

import java.util.List;

import www.experthere.in.model.service_user_cat.Service;

public class SearchServiceResponse {


    private boolean success;
    private String message;
    private List<Service> services;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public List<Service> getServices() {
        return services;
    }
}
