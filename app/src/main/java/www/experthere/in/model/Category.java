package www.experthere.in.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import www.experthere.in.model.Subcategory;


public class Category {
    @SerializedName("category_id")
    private String categoryId;

    @SerializedName("category_name")
    private String categoryName;
   @SerializedName("category_image")
    private String categoryImage;

    @SerializedName("subcategories")
    private List<Subcategory> subcategories;

    public String getCategoryImage() {
        return categoryImage;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public List<Subcategory> getSubcategories() {
        return subcategories;
    }

}
