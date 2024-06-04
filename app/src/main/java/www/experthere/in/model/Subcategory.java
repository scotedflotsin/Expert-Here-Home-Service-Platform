package www.experthere.in.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class Subcategory {
    @SerializedName("subcategory_id")
    private String subcategoryId;

    @SerializedName("subcategory_name")
    private String subcategoryName;

    public String getSubcategoryId() {
        return subcategoryId;
    }

    public String getSubcategoryName() {
        return subcategoryName;
    }
}
