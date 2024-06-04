package www.experthere.in.model;

import com.google.gson.annotations.SerializedName;

public class ProviderData {


    @SerializedName("id")
    private String id;

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
    private String visitingCharges;

    @SerializedName("currency")
    private String currency;

    @SerializedName("advance_booking_days")
    private String advanceBookingDays;

    @SerializedName("account_type")
    private String accountType;

    @SerializedName("members")
    private String members;

    @SerializedName("description")
    private String description;

    @SerializedName("city")
    private String city;

    @SerializedName("latitude")
    private String latitude;

    @SerializedName("longitude")
    private String longitude;

    @SerializedName("address")
    private String address;

    @SerializedName("is_blocked")
    private String isBlocked;

    @SerializedName("document_image")
    private String documentImage;

    @SerializedName("logo_image")
    private String logoImage;

    @SerializedName("banner_image")
    private String bannerImage;


    public String getId() {
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

    public String getVisitingCharges() {
        return visitingCharges;
    }

    public String getCurrency() {
        return currency;
    }

    public String getAdvanceBookingDays() {
        return advanceBookingDays;
    }

    public String getAccountType() {
        return accountType;
    }

    public String getMembers() {
        return members;
    }

    public String getDescription() {
        return description;
    }

    public String getCity() {
        return city;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getAddress() {
        return address;
    }

    public String getIsBlocked() {
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
