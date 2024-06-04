package www.experthere.in.model.service_user_cat;

import com.google.gson.annotations.SerializedName;

public class Provider {
    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;

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

    // getters and setters


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
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
}