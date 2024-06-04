package www.experthere.in.model;

import com.google.gson.annotations.SerializedName;

public class ProviderList {
    @SerializedName("id")
    private int id;

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

    @SerializedName("currency")
    private String currency;

    @SerializedName("advance_booking_days")
    private int advanceBookingDays;

    @SerializedName("account_type")
    private String accountType;

    @SerializedName("members")
    private int members;

    @SerializedName("description")
    private String description;

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


    @SerializedName("total_reviews")
    private int total_reviews;
    @SerializedName("average_rating")
    private float average_rating;
    @SerializedName("total_services")
    private int totalServices;

    @SerializedName("distance")
    private double distance;

    // Add getters and setters for all fields
    // You can also add any additional methods you may need


    public int getId() {
        return id;
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

    public String getCurrency() {
        return currency;
    }

    public int getAdvanceBookingDays() {
        return advanceBookingDays;
    }

    public String getAccountType() {
        return accountType;
    }

    public int getMembers() {
        return members;
    }

    public String getDescription() {
        return description;
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

    public int getTotal_reviews() {
        return total_reviews;
    }

    public float getAverage_rating() {
        return average_rating;
    }

    public int getTotalServices() {
        return totalServices;
    }

    public double getDistance() {
        return distance;
    }
}

