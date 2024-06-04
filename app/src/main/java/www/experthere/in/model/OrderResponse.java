package www.experthere.in.model;

import com.google.gson.annotations.SerializedName;

public class OrderResponse {

    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("order")
    private Order order;


    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Order getOrder() {
        return order;
    }
}
