package www.experthere.in.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OrderListResponse {

    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("orders")
    private List<Order> order;


    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public List<Order> getOrder() {
        return order;
    }
}
