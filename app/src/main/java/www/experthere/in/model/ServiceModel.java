package www.experthere.in.model;

import com.google.gson.annotations.SerializedName;

public class ServiceModel {
    @SerializedName("id")
    private String id;

    @SerializedName("provider_id")
    private String providerId;

    @SerializedName("service_title")
    private String serviceTitle;

    @SerializedName("service_tags")
    private String serviceTags;

    @SerializedName("service_category_id")
    private String serviceCategoryId; // Add this line

    @SerializedName("service_subcategory_id")
    private String serviceSubcategoryId; // Add this line

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
    private int finalPrice;

    @SerializedName("discounted_price")
    private int discountedPrice;

    @SerializedName("members")
    private String members;

    @SerializedName("service_duration")
    private String serviceDuration;

    @SerializedName("description")
    private String description;

    // Add other fields as needed

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getServiceTitle() {
        return serviceTitle;
    }

    public void setServiceTitle(String serviceTitle) {
        this.serviceTitle = serviceTitle;
    }

    public String getServiceTags() {
        return serviceTags;
    }

    public void setServiceTags(String serviceTags) {
        this.serviceTags = serviceTags;
    }

    public String getServiceCategoryId() {
        return serviceCategoryId;
    }

    public void setServiceCategoryId(String serviceCategoryId) {
        this.serviceCategoryId = serviceCategoryId;
    }

    public String getServiceSubcategoryId() {
        return serviceSubcategoryId;
    }

    public void setServiceSubcategoryId(String serviceSubcategoryId) {
        this.serviceSubcategoryId = serviceSubcategoryId;
    }

    public String getServiceCategory() {
        return serviceCategory;
    }

    public void setServiceCategory(String serviceCategory) {
        this.serviceCategory = serviceCategory;
    }

    public String getServiceSubCategory() {
        return serviceSubCategory;
    }

    public void setServiceSubCategory(String serviceSubCategory) {
        this.serviceSubCategory = serviceSubCategory;
    }

    public String getServiceStatus() {
        return serviceStatus;
    }

    public void setServiceStatus(String serviceStatus) {
        this.serviceStatus = serviceStatus;
    }

    public String getServiceImageUrl() {
        return serviceImageUrl;
    }

    public void setServiceImageUrl(String serviceImageUrl) {
        this.serviceImageUrl = serviceImageUrl;
    }

    public String getTaxType() {
        return taxType;
    }

    public void setTaxType(String taxType) {
        this.taxType = taxType;
    }

    public String getTaxValue() {
        return taxValue;
    }

    public void setTaxValue(String taxValue) {
        this.taxValue = taxValue;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public int getDiscountedPrice() {
        return discountedPrice;


    }


    public int getFinalPrice() {
        return finalPrice;
    }

    public String getMembers() {
        return members;
    }

    public void setMembers(String members) {
        this.members = members;
    }

    public String getServiceDuration() {
        return serviceDuration;
    }

    public void setServiceDuration(String serviceDuration) {
        this.serviceDuration = serviceDuration;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Add other getters and setters for additional fields
}
