package www.experthere.in.model.service_user_cat;

import com.google.gson.annotations.SerializedName;

public class Service {
    @SerializedName("service_id")
    private int id;

    public int getId() {
        return id;
    }

    @SerializedName("provider_id")
    private int providerId;

    @SerializedName("service_title")
    private String serviceTitle;

    @SerializedName("service_tags")
    private String serviceTags;

    @SerializedName("service_category")
    private String serviceCategory;

    @SerializedName("service_sub_category")
    private String serviceSubCategory;

    @SerializedName("service_status")
    private String serviceStatus;

    @SerializedName("service_image_url")
    private String serviceImageUrl;

    @SerializedName("tax_type")
    private String taxType;

    @SerializedName("tax_value")
    private String taxValue;

    @SerializedName("currency")
    private String currency;

    @SerializedName("final_price")
    private String finalPrice;

    @SerializedName("discounted_price")
    private String discountedPrice;

    @SerializedName("members")
    private int members;

    @SerializedName("service_duration")
    private String serviceDuration;

    @SerializedName("service_description")
    private String description;
    @SerializedName("total_stars")
    private int totalStars;
    @SerializedName("average_rating")
    private double avgRating;

    public int getTotalStars() {
        return totalStars;
    }

    public double getAvgRating() {
        return avgRating;
    }

    @SerializedName("provider")
    private Provider provider;

    // getters and setters




    public int getProviderId() {
        return providerId;
    }

    public String getServiceTitle() {
        return serviceTitle;
    }

    public String getServiceTags() {
        return serviceTags;
    }

    public String getServiceCategory() {
        return serviceCategory;
    }

    public String getServiceSubCategory() {
        return serviceSubCategory;
    }

    public String getServiceStatus() {
        return serviceStatus;
    }

    public String getServiceImageUrl() {
        return serviceImageUrl;
    }

    public String getTaxType() {
        return taxType;
    }

    public String getTaxValue() {
        return taxValue;
    }

    public String getCurrency() {
        return currency;
    }

    public String getFinalPrice() {
        return finalPrice;
    }

    public String getDiscountedPrice() {
        return discountedPrice;
    }

    public int getMembers() {
        return members;
    }

    public String getServiceDuration() {
        return serviceDuration;
    }

    public String getDescription() {
        return description;
    }

    public Provider getProvider() {
        return provider;
    }
}