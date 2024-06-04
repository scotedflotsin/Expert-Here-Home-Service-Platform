package www.experthere.in.model;

import com.google.gson.annotations.SerializedName;

public class IntrosliderData {
    @SerializedName("id")
    private String id;

    @SerializedName("url")
    private String imageUrl;

    // Constructor, getters, setters


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
