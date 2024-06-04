package www.experthere.in.model;

import com.google.gson.annotations.SerializedName;

public class SERVICE {

    @SerializedName("service_id")
    private int serviceId;

    @SerializedName("id")
    private int id;

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

    @SerializedName("name")
    private String name;

    @SerializedName("email")
    private String email;

    @SerializedName("country_code")
    private String countryCode;

    @SerializedName("phone")
    private String phone;

    @SerializedName("id_type")
    private String idType;

    @SerializedName("company_name")
    private String companyName;

    @SerializedName("visiting_charges")
    private int visitingCharges;

    @SerializedName("advance_booking_days")
    private int advanceBookingDays;

    @SerializedName("account_type")
    private String accountType;

    @SerializedName("city")
    private String city;

    @SerializedName("latitude")
    private double latitude;

    @SerializedName("longitude")
    private double longitude;

    @SerializedName("address")
    private String address;

    @SerializedName("is_blocked")
    private int isBlocked;

    @SerializedName("document_image")
    private String documentImage;

    @SerializedName("logo_image")
    private String logoImage;

    @SerializedName("banner_image")
    private String bannerImage;

    @SerializedName("service_description")
    private String serviceDescription;

    @SerializedName("total_stars")
    private int totalStars;

    @SerializedName("average_rating")
    private String averageRating;

    @SerializedName("provider")
    private Provider provider;


    public int getServiceId() {
        return serviceId;
    }

    public int getId() {
        return id;
    }

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

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getPhone() {
        return phone;
    }

    public String getIdType() {
        return idType;
    }

    public String getCompanyName() {
        return companyName;
    }

    public int getVisitingCharges() {
        return visitingCharges;
    }

    public int getAdvanceBookingDays() {
        return advanceBookingDays;
    }

    public String getAccountType() {
        return accountType;
    }

    public String getCity() {
        return city;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getAddress() {
        return address;
    }

    public int getIsBlocked() {
        return isBlocked;
    }

    public String getDocumentImage() {
        return documentImage;
    }

    public String getLogoImage() {
        return logoImage;
    }

    public String getBannerImage() {
        return bannerImage;
    }

    public String getServiceDescription() {
        return serviceDescription;
    }

    public int getTotalStars() {
        return totalStars;
    }

    public String getAverageRating() {
        return averageRating;
    }

    public Provider getProvider() {
        return provider;
    }
}
