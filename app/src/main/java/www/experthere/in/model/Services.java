package www.experthere.in.model;

import com.google.gson.annotations.SerializedName;

public class Services {
    @SerializedName("id")
    private String id;

    @SerializedName("provider_id")
    private String providerId;

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
    private double taxValue;

    @SerializedName("currency")
    private String currency;

    @SerializedName("final_price")
    private double finalPrice;

    @SerializedName("discounted_price")
    private double discountedPrice;

    @SerializedName("members")
    private int members;

    @SerializedName("service_duration")
    private String serviceDuration;

    @SerializedName("description")
    private String description;


    @SerializedName("total_stars")
    private int total_stars;


    @SerializedName("average_rating")
    private Double average_rating;

    public Double getAverage_rating() {
        return average_rating;

    }

    public int getTotal_stars() {
        return total_stars;
    }

    // Constructors, getters, and setters can be added as needed

    public String getId() {
        return id;
    }

    public String getProviderId() {
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

    public double getTaxValue() {
        return taxValue;
    }

    public String getCurrency() {
        return currency;
    }

    public double getFinalPrice() {
        return finalPrice;
    }

    public double getDiscountedPrice() {
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
}
