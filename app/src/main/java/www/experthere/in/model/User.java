package www.experthere.in.model;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;
    @SerializedName("profile_picture")
    private String profile_picture;

    @SerializedName("profession")
    private String profession;

    @SerializedName("phone")
    private String phone;

    @SerializedName("email")
    private String email;

    @SerializedName("latitude")
    private String latitude;

    @SerializedName("longitude")
    private String longitude;

    @SerializedName("full_address")
    private String address;

    @SerializedName("is_blocked")
    private int is_blocked;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getProfession() {
        return profession;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
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

    public String getProfile_picture() {
        return profile_picture;
    }



    public int isIs_blocked() {
        return is_blocked;
    }
}
