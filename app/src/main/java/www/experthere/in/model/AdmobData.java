package www.experthere.in.model;

import com.google.gson.annotations.SerializedName;

import retrofit2.http.Field;

public class AdmobData
{


    @SerializedName("id")
    String id;
    @SerializedName("status")
    String status;
    @SerializedName("banner")

    String banner;
    @SerializedName("interstitial")

    String interstitial;

    @SerializedName("native")

    String nativeAds;
    @SerializedName("open_app")

    String open_app;


    public String getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public String getBanner() {
        return banner;
    }

    public String getInterstitial() {
        return interstitial;
    }

    public String getNativeAds() {
        return nativeAds;
    }

    public String getOpen_app() {
        return open_app;
    }
}
