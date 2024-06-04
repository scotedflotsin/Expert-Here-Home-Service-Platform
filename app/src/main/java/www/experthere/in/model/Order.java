package www.experthere.in.model;
import com.google.gson.annotations.SerializedName;

public class Order {
    @SerializedName("id")
    private int id;

    @SerializedName("provider_id")
    private int providerId;

    @SerializedName("service_id")
    private int serviceId;

    @SerializedName("service_name")
    private String serviceName;

    @SerializedName("customer_name")
    private String customerName;

    @SerializedName("address")
    private String address;

    @SerializedName("time")
    private String time;

    public int getId() {
        return id;
    }

    public int getProviderId() {
        return providerId;
    }

    public int getServiceId() {
        return serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getAddress() {
        return address;
    }

    public String getTime() {
        return time;
    }
}

