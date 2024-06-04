package www.experthere.in.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Query;
import www.experthere.in.model.ProviderResponse;
import www.experthere.in.model.ResponseProvider;

public interface ApiInterfaceDeeplink {


    @Headers("Content-Type: application/json")
    @GET("provider_profile.php")
    Call<ProviderResponse> getProviderProfile(
            @Query("EHpd") String id,
            @Header("x-api-key") String apiKey);


}
