package www.experthere.in.model;

import com.google.gson.annotations.SerializedName;

public class Taxes {

    @SerializedName("id")
    private String id;

    @SerializedName("percentage")
    private String percentage;

    public String getId() {
        return id;
    }

    public String getPercentage() {
        return percentage;
    }
}
