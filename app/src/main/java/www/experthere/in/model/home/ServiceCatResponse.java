package www.experthere.in.model.home;

import java.util.List;
import java.util.Map;

import www.experthere.in.model.service_user_cat.Service;

public class ServiceCatResponse {

    boolean success;
    String message;
    private Map<String, List<Service>> category_services;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Map<String, List<Service>> getCategory_services() {
        return category_services;
    }
}
