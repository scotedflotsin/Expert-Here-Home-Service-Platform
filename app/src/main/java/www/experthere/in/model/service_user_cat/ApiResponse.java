package www.experthere.in.model.service_user_cat;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ApiResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("services")
    private List<Service> services;


    // getters and setters


    public boolean isSuccess() {
        return success;
    }

    public List<Service> getServices() {
        return services;
    }
}