package www.experthere.in.api;

import java.math.BigInteger;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import www.experthere.in.helper.BookmarkCountResponse;
import www.experthere.in.helper.BookmarkResponse;
import www.experthere.in.helper.ProviderListResponse;
import www.experthere.in.model.AdsResponse;
import www.experthere.in.model.AllReviewResponse;
import www.experthere.in.model.AllReviewsStats;
import www.experthere.in.model.ApiResponse;
import www.experthere.in.model.BlockRes;
import www.experthere.in.model.CallHistoryResponse;
import www.experthere.in.model.CategoryResponse;
import www.experthere.in.model.CreateServiceResponse;
import www.experthere.in.model.EditServiceRes;
import www.experthere.in.model.EmailValidationResponse;
import www.experthere.in.model.LoginResponse;
import www.experthere.in.model.MaintenanceRes;
import www.experthere.in.model.OrderCount;
import www.experthere.in.model.OrderListResponse;
import www.experthere.in.model.OrderResponse;
import www.experthere.in.model.ProviderResponse;
import www.experthere.in.model.ProviderStatusResponse;
import www.experthere.in.model.RatingApiResponse;
import www.experthere.in.model.ResponseProvider;
import www.experthere.in.model.ReviewDetailsModel;
import www.experthere.in.model.ReviewResponse;
import www.experthere.in.model.SearchServiceResponse;
import www.experthere.in.model.ServiceResponse;
import www.experthere.in.model.SettingsRes;
import www.experthere.in.model.SliderResponse;
import www.experthere.in.model.SuccessMessageResponse;
import www.experthere.in.model.TaxApiResponse;
import www.experthere.in.model.TokenResponse;
import www.experthere.in.model.UpdateServiceResponse;
import www.experthere.in.model.YourServicesCount;
import www.experthere.in.model.TestResponse;

public interface ApiInterface {

    @FormUrlEncoded
    @POST("application/SendOtp/otp_sender.php")
        // Change this to the actual name of your PHP script
    Call<Void> sendOtp(
            @Field("email") String email,
            @Field("name") String name,
            @Field("otp") String otp,
            @Field("subject") String subject
    );


    @FormUrlEncoded
    @POST("users/register_user.php")
    Call<LoginResponse> registerUser(

            @Field("name") String name,
            @Field("profession") String profession,
            @Field("phone") String phone,
            @Field("email") String email,
            @Field("password") String password,
            @Field("profile_picture") String profile_picture,
            @Field("latitude") double latitude,
            @Field("longitude") double longitude,
            @Field("full_address") String fullAddress,
            @Field("is_blocked") boolean isBlocked
    );

    @FormUrlEncoded
    @POST("users/login_user.php")
        // Replace with the actual name of your login PHP script
    Call<LoginResponse> loginUser(
            @Field("email") String email,
            @Field("password") String password
    );


    @Multipart
    @POST("users/edit_user_details.php")
        // Replace with the actual endpoint
    Call<LoginResponse> updateUserWithImage(
            @Part("email") RequestBody email,
            @Part("name") RequestBody name,
            @Part("profession") RequestBody profession,
            @Part("phone") RequestBody phone,
            @Part("password") RequestBody password,
            @Part("latitude") RequestBody latitude,
            @Part("longitude") RequestBody longitude,
            @Part("full_address") RequestBody fullAddress,
            @Part("is_blocked") RequestBody isBlocked,
            @Part MultipartBody.Part profile_picture // This is for sending the image file
    );

    @FormUrlEncoded
    @POST("users/edit_user_details.php")
        // Replace with the actual endpoint
    Call<LoginResponse> updateUserWithoutImage(
            @Field("email") String email,
            @Field("name") String name,
            @Field("profession") String profession,
            @Field("phone") String phone,
            @Field("password") String password,
            @Field("latitude") String latitude,
            @Field("longitude") String longitude,
            @Field("full_address") String fullAddress,
            @Field("is_blocked") String isBlocked
    );

    @FormUrlEncoded
    @POST("providers/login_provider.php")
        // Replace with the actual name of your login PHP script
    Call<ResponseProvider> loginProvider(
            @Field("email") String email,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("users/login_google.php")
        // Replace with the actual name of your login PHP script
    Call<LoginResponse> loginUserByGoogle(
            @Field("email") String email
            , @Header("X-Api-Key") String apiKey

    );

    @FormUrlEncoded
    @POST("providers/login_google_provider.php")
        // Replace with the actual name of your login PHP script
    Call<ResponseProvider> loginProvidersByGoogle(
            @Field("email") String email
    );


    @FormUrlEncoded
    @POST("providers/edit_provider_details.php")
        // Replace with the actual name of your PHP script
    Call<ApiResponse> updateProviderDetails(
            @Field("email") String email,
            @Field("field_to_edit") String fieldToEdit,
            @Field("new_value") String newValue
    );

    @FormUrlEncoded
    @POST("users/update_location.php")
        // Replace with the actual name of your PHP script
    Call<ApiResponse> updateUserLocation(
            @Field("email") String email,
            @Field("latitude") double latitude,
            @Field("longitude") double longitude,
            @Field("full_address") String full_address
    );

    @FormUrlEncoded
    @POST("users/email_check.php")
    Call<ApiResponse> checkEmailExists(@Field("email") String email);


    // check email for provider
    @FormUrlEncoded
    @POST("providers/check_email.php")
    // Replace with the actual path
    Call<EmailValidationResponse> checkEmailProvider(@Field("email") String email);


    @Multipart
    @POST("providers/register_provider.php")
        // Replace with the actual path
    Call<ResponseProvider> uploadProvider(
            @Part("name") RequestBody name,
            @Part("email") RequestBody email,
            @Part("password") RequestBody pass,
            @Part("country_code") RequestBody country_code,
            @Part("phone") RequestBody phone,
            @Part("id_type") RequestBody idType,
            @Part("company_name") RequestBody companyName,
            @Part("visiting_charges") RequestBody visitingCharges,
            @Part("currency") RequestBody currency,
            @Part("advance_booking_days") RequestBody advanceBookingDays,
            @Part("account_type") RequestBody accountType,
            @Part("members") RequestBody members,
            @Part("description") RequestBody description,
            @Part("city") RequestBody city,
            @Part("latitude") RequestBody latitude,
            @Part("longitude") RequestBody longitude,
            @Part("address") RequestBody address,
            @Part("is_blocked") RequestBody is_blocked,
            @Part MultipartBody.Part documentImage,
            @Part MultipartBody.Part logoImage,
            @Part MultipartBody.Part bannerImage
    );

    @Multipart
    @POST("providers/update_provider.php")
        // Replace with the actual path
    Call<ResponseProvider> updateProvider(
            @Part("id") RequestBody id,
            @Part("name") RequestBody name,
            @Part("email") RequestBody email,
            @Part("password") RequestBody pass,
            @Part("country_code") RequestBody country_code,
            @Part("phone") RequestBody phone,
            @Part("id_type") RequestBody idType,
            @Part("company_name") RequestBody companyName,
            @Part("visiting_charges") RequestBody visitingCharges,
            @Part("currency") RequestBody currency,
            @Part("advance_booking_days") RequestBody advanceBookingDays,
            @Part("account_type") RequestBody accountType,
            @Part("members") RequestBody members,
            @Part("description") RequestBody description,
            @Part("city") RequestBody city,
            @Part("latitude") RequestBody latitude,
            @Part("longitude") RequestBody longitude,
            @Part("address") RequestBody address,
            @Part("is_blocked") RequestBody is_blocked,
            @Part MultipartBody.Part documentImage,
            @Part MultipartBody.Part logoImage,
            @Part MultipartBody.Part bannerImage
    );


    @GET("application/get_categories.php")
    Call<CategoryResponse> getCategories();

    @GET("application/get_tax.php")
        // Replace with the actual path to your API
    Call<TaxApiResponse> getAllTaxes();


    @GET("services/get_provider_services.php")
    Call<ServiceResponse> getServices(
            @Query("provider_id") String providerId,
            @Query("page") int page,
            @Query("itemsPerPage") int itemsPerPage,
            @Query("searchQuery") String searchQuery
    );

    @Multipart
    @POST("services/create_service.php")
        // Replace with the actual endpoint
    Call<CreateServiceResponse> addService(
            @Part("provider_id") RequestBody providerId,
            @Part("service_title") RequestBody serviceTitle,
            @Part("service_tags") RequestBody serviceTags,
            @Part("service_category") RequestBody serviceCategory,
            @Part("service_sub_category") RequestBody serviceSubCategory,
            @Part("service_status") RequestBody serviceStatus,
            @Part("tax_type") RequestBody taxType,
            @Part("tax_value") RequestBody taxValue,
            @Part("currency") RequestBody currency,
            @Part("final_price") RequestBody finalPrice,
            @Part("discounted_price") RequestBody discountedPrice,
            @Part("members") RequestBody members,
            @Part("duration") RequestBody duration,
            @Part("description") RequestBody description,
            @Part MultipartBody.Part image
    );


    @Multipart
    @POST("services/update_service.php")
        // Replace with the actual endpoint
    Call<UpdateServiceResponse> updateService(
            @Part("provider_id") RequestBody providerId,
            @Part("service_id") RequestBody serviceId,
            @Part("service_title") RequestBody serviceTitle,
            @Part("service_tags") RequestBody serviceTags,
            @Part("service_category") RequestBody serviceCategory,
            @Part("service_sub_category") RequestBody serviceSubCategory,
            @Part("service_status") RequestBody serviceStatus,
            @Part("tax_type") RequestBody taxType,
            @Part("tax_value") RequestBody taxValue,
            @Part("currency") RequestBody currency,
            @Part("final_price") RequestBody finalPrice,
            @Part("discounted_price") RequestBody discountedPrice,
            @Part("members") RequestBody members,
            @Part("service_duration") RequestBody duration,
            @Part("description") RequestBody description,
            @Part MultipartBody.Part image
    );


    // Define the DELETE endpoint for deleting a service by ID
//    @DELETE("services/delete_service.php/{serviceId}")
//    Call<DeleteServiceResponse> deleteService(@Path("serviceId") int serviceId);

    @DELETE("services/delete_service.php")
    Call<Void> deleteService(@Query("service_id") String serviceId);


    @GET("services/get_service.php")
        // Adjust the path to match your API endpoint
    Call<EditServiceRes> getServiceDetails(
            @Query("provider_id") String providerId,
            @Query("service_id") String serviceId
    );

    @FormUrlEncoded
    @POST("services/get_service_by_cat.php")
    Call<www.experthere.in.model.service_user_cat.ApiResponse> getServicesWithCatSubCat(
            @Field("category_id") String categoryId,
            @Field("subcategory_id") String subcategoryId,
            @Field("current_latitude") double currentLatitude,
            @Field("current_longitude") double currentLongitude,
            @Field("page") int page,
            @Field("itemsPerPage") int itemsPerPage,
            @Field("radius") int radius
    );

    @FormUrlEncoded
    @POST("services/get_all_services.php")
    Call<www.experthere.in.model.service_user_cat.ApiResponse> getallserviciesforprovider(
            @Field("provider_id") String categoryId,
            @Field("page") int page,
            @Field("itemsPerPage") int itemsPerPage
    );


    @FormUrlEncoded
    @POST("services/get_reviews.php")
    Call<ReviewResponse> getReviews(
            @Field("service_id") int serviceId,
            @Field("page") int page,
            @Field("itemsPerPage") int itemsPerPage,
            @Field("user_id") int user_id
    );


    @FormUrlEncoded
    @POST("services/get_rating_stars.php")
        // Replace with the actual path
    Call<RatingApiResponse> getRatingData(
            @Field("service_id") int serviceId
    );


    @Multipart
    @POST("services/add_review.php")
        // Change this to the actual path of your API file
    Call<SuccessMessageResponse> submitReview(
            @Part("user_id") RequestBody userId,
            @Part("service_id") RequestBody serviceId,
            @Part("provider_id") RequestBody providerId,
            @Part("review") RequestBody review,
            @Part("stars") RequestBody stars,
            @Part MultipartBody.Part reviewImage);


    @FormUrlEncoded
    @POST("services/check_user_review.php")
        // Replace with your actual API endpoint
    Call<ReviewDetailsModel> checkUserId(
            @Field("user_id") String userId,
            @Field("service_id") String serviceId
    );


    @FormUrlEncoded
    @POST("services/get_review_details.php")
    Call<ReviewDetailsModel> getReviewDetails(@Field("review_id") String reviewId);

    @FormUrlEncoded
    @POST("services/total_reviews_stats.php")
    Call<AllReviewsStats> getAllReviewStats(@Field("provider_id") String reviewId);


    @Multipart
    @POST("services/edit_review.php")
        // Change this to the actual path of your API file
    Call<SuccessMessageResponse> editReview(
            @Part("review_id") RequestBody reviewId,
            @Part("review") RequestBody review,
            @Part("stars") RequestBody stars,
            @Part MultipartBody.Part reviewImage);

    @FormUrlEncoded
    @POST("services/delete_review.php")
        // Replace with your actual API endpoint
    Call<SuccessMessageResponse> deleteReview(
            @Field("review_id") String reviewID
    );


    @FormUrlEncoded
    @POST("services/get_all_reviews_provider.php")
    Call<AllReviewResponse> getAllReviewsOfProvider(
            @Field("provider_id") int providerId,
            @Field("page") int page,
            @Field("itemsPerPage") int itemsPerPage
    );


    @GET("providers/get_provider_details.php")
    Call<ProviderResponse> getProviderData(@Query("id") String id);


    @FormUrlEncoded
    @POST("providers/create_order.php")
        // Change this to your PHP script name
    Call<OrderResponse> placeOrder(
            @Field("provider_id") int providerId,
            @Field("service_id") int serviceId,
            @Field("service_name") String serviceName,
            @Field("customer_name") String customerName,
            @Field("address") String address,
            @Field("time") String time
    );

    @FormUrlEncoded
    @POST("providers/get_orders.php")
        // Change this to your PHP script name
    Call<OrderListResponse> getOrders(
            @Field("provider_id") int providerId,
            @Field("page") int page,
            @Field("items_per_page") int itemsPerPage

    );

    @FormUrlEncoded
    @POST("providers/delete_order.php")
        // Change this to your PHP script name
    Call<SuccessMessageResponse> deleteOrder(
            @Field("order_id") int order_id

    );


    @FormUrlEncoded
    @POST("providers/your_services_count.php")
    Call<YourServicesCount> getMyServicesCount(@Field("provider_id") String provider_id);


    @FormUrlEncoded
    @POST("providers/order_count.php")
    Call<OrderCount> getOrderCount(@Field("provider_id") String provider_id);

    @POST("application/get_image_slider.php")
    Call<SliderResponse> getImageSlider();


//    @FormUrlEncoded
//    @POST("services/get_services_search_user.php")
//    Call<www.experthere.in.model.service_user_cat.ApiResponse> getServiceUserWithSearchAndFilter(
//
//            @Field("current_latitude") double currentLatitude,
//            @Field("current_longitude") double currentLongitude,
//            @Field("page") int page,
//            @Field("itemsPerPage") int itemsPerPage,
//            @Field("radius") int radius,
//            @Field("stars") int stars,
//            @Field("searchQuery") String searchQuery
//    );


    @GET("services/get_services_search_user.php")
    Call<SearchServiceResponse> getServiceUserWithSearchAndFilter(
            @Query("page") int page,
            @Query("itemsPerPage") int itemsPerPage,
            @Query("searchQuery") String searchQuery,
            @Query("stars") String stars,
            @Query("priceFrom") String priceFrom,
            @Query("priceTo") String priceTo,
            @Query("current_latitude") String currentLatitude,
            @Query("current_longitude") String currentLongitude,
            @Query("radius") int radius);


    @GET("services/get_service_data_serach_suggestion.php")
    Call<SearchServiceResponse> getServiceSuggestionSearch(
            @Query("page") int page,
            @Query("itemsPerPage") int itemsPerPage,
            @Query("searchQuery") String searchQuery,
            @Query("stars") String stars,
            @Query("priceFrom") String priceFrom,
            @Query("priceTo") String priceTo,
            @Query("current_latitude") String currentLatitude,
            @Query("current_longitude") String currentLongitude,
            @Query("radius") int radius);

    //    @FormUrlEncoded
//    @POST("fcm/fcm_code.php")
//        // Change this to your PHP script name
//    Call<SuccessMessageResponse> fcmCodeSaver(
//            @Field("user_id") String  userId,
//            @Field("provider_id") String providerId,
//            @Field("fcm_token") String token,
//            @Field("type") String type
//
//    );
    @FormUrlEncoded
    @POST("fcm/fcm_token_update_user.php")
    // Change this to your PHP script name
    Call<SuccessMessageResponse> fcmUserCodeSaver(
            @Field("user_id") String userId,
            @Field("provider_id") String providerId,
            @Field("fcm_token") String token,
            @Field("type") String type

    );

    @FormUrlEncoded
    @POST("fcm/fcm_token_update_provider.php")
        // Change this to your PHP script name
    Call<SuccessMessageResponse> fcmProviderCodeSaver(
            @Field("user_id") String userId,
            @Field("provider_id") String providerId,
            @Field("fcm_token") String token,
            @Field("type") String type

    );


    @FormUrlEncoded
    @POST("calling/set_provider_status.php")
        // Change this to your PHP script name
    Call<SuccessMessageResponse> setProviderStatus(
            @Field("provider_id") String providerId,
            @Field("status") String status


    );


    //    @FormUrlEncoded
//    @POST("calling/save_call_history.php")
//        // Change this to your PHP script name
//    Call<SuccessMessageResponse> saveCallHistory(
//
//            @Field("user_id") int user_id,
//            @Field("provider_id") int provider_id,
//            @Field("duration") BigInteger duration,
//            @Field("call_by") String call_by, //either user,provider
//            @Field("received") String received ,// either answered,not_answered
//            @Field("timestamp") BigInteger timestamp // either answered,not_answered
//    );
//
    @FormUrlEncoded
    @POST("calling/save_call_history_user.php")
    // Change this to your PHP script name
    Call<SuccessMessageResponse> saveCallHistoryUser(


            @Field("user_id") int user_id,
            @Field("provider_id") int provider_id,
            @Field("duration") BigInteger duration,
            @Field("call_by") String call_by, //either user,provider
            @Field("received") String received,// either answered,not_answered
            @Field("timestamp") BigInteger timestamp // either answered,not_answered


    );

    @FormUrlEncoded
    @POST("calling/save_call_history_provider.php")
        // Change this to your PHP script name
    Call<SuccessMessageResponse> saveCallHistoryProvider(

            @Field("user_id") int user_id,
            @Field("provider_id") int provider_id,
            @Field("duration") BigInteger duration,
            @Field("call_by") String call_by, //either user,provider
            @Field("received") String received,// either answered,not_answered
            @Field("timestamp") BigInteger timestamp // either answered,not_answered
    );


    @FormUrlEncoded
    @POST("calling/get_provider_status.php")
        // Change this to your PHP script name
    Call<ProviderStatusResponse> getProviderStatus(
            @Field("provider_id") String providerId

    );


    @GET("fcm/get_user_data.php")
    Call<TokenResponse> getFcmTokenDetailsUser(
            @Query("user_id") String userId);

    @GET("fcm/get_provider_data.php")
    Call<TokenResponse> getFcmTokenDetailsProvider(
            @Query("provider_id") String providerId);

    @FormUrlEncoded
    @POST("calling/get_provider_call_history_incoming.php")
    Call<CallHistoryResponse> getCallHistoryProviderIncoming(
            @Field("provider_id") String providerId,
            @Field("page") int page,
            @Field("itemsPerPage") int itemsPerPage,
            @Field("searchQuery") String searchQuery

    );

    @FormUrlEncoded
    @POST("calling/get_provider_call_history_outgoing.php")
    Call<CallHistoryResponse> getCallHistoryProviderOutGoing(
            @Field("provider_id") String providerId,
            @Field("page") int page,
            @Field("itemsPerPage") int itemsPerPage,
            @Field("searchQuery") String searchQuery

    );


    @FormUrlEncoded
    @POST("calling/get_user_call_history.php")
    Call<CallHistoryResponse> getCallHistoryUser(
            @Field("user_id") String userId,
            @Field("page") int page,
            @Field("itemsPerPage") int itemsPerPage,
            @Field("searchQuery") String searchQuery

    );


    @FormUrlEncoded
    @POST("calling/call_history_count_provider.php")
    Call<SuccessMessageResponse> getCallHistoryCount(@Field("provider_id") String provider_id);


    @FormUrlEncoded
    @POST("calling/delete_call_history_for_provider.php")
        // Replace with your actual API endpoint
    Call<SuccessMessageResponse> deleteCallHistoryForProvider(
            @Field("id") String id
    );

    @FormUrlEncoded
    @POST("calling/delete_call_history_for_user.php")
        // Replace with your actual API endpoint
    Call<SuccessMessageResponse> deleteCallHistoryForUser(
            @Field("id") String id
    );


    @FormUrlEncoded
    @POST("bookmark/add_bookmark.php")
        // Replace with your actual API endpoint
    Call<SuccessMessageResponse> addBookmark(
            @Field("user_id") String userId,
            @Field("provider_id") String providerId
    );

    @FormUrlEncoded
    @POST("bookmark/get_book_mark.php")
        // Replace with your actual API endpoint
    Call<BookmarkResponse> getBookmark(
            @Field("user_id") String userId,
            @Field("page") int page,
            @Field("itemsPerPage") int itemsPerPage

    );


    @FormUrlEncoded
    @POST("bookmark/delete_bookmark.php")
        // Replace with your actual API endpoint
    Call<SuccessMessageResponse> deleteBookmark(
            @Field("bookmark_id") String bookmarkId

    );

    @FormUrlEncoded
    @POST("bookmark/delete_bookmark_user_provider.php")
        // Replace with your actual API endpoint
    Call<SuccessMessageResponse> deleteBookmarkWithUserIdProviderId(
            @Field("user_id") String userID, @Field("provider_id") String providerID


    );

    @FormUrlEncoded
    @POST("bookmark/bookmark_count.php")
        // Replace with your actual API endpoint
    Call<BookmarkCountResponse> getBookmarkCount(
            @Field("user_id") String userId

    );


    @FormUrlEncoded
    @POST("bookmark/check_bookmark_available.php")
        // Replace with your actual API endpoint
    Call<SuccessMessageResponse> checkBookmarkAvailable(
            @Field("user_id") String userId,
            @Field("provider_id") String provider_id

    );


    @GET("services/get_top_services.php")
    Call<www.experthere.in.model.service_user_cat.ApiResponse> getTopService(
            @Query("current_latitude") double currentLatitude,
            @Query("current_longitude") double currentLongitude,
            @Query("page") int page,
            @Query("itemsPerPage") int itemsPerPage,
            @Query("radius") int radius
    );

    @FormUrlEncoded
    @POST("services/get_services_of_specific_category.php")
    Call<www.experthere.in.model.service_user_cat.ApiResponse> getServiceForSpecificCategoryUserHome(
            @Field("current_latitude") double currentLatitude,
            @Field("current_longitude") double currentLongitude,
            @Field("page") int page,
            @Field("itemsPerPage") int itemsPerPage,
            @Field("radius") int radius,
            @Field("category_name") String category_name

    );

    @FormUrlEncoded
    @POST("services/get_services_category_wise.php")
    Call<TestResponse> getCategoryServices(

            @Field("current_latitude") double latitude,
            @Field("current_longitude") double longitude,
            @Field("radius") double radius
//            @Field("page") int page,
//            @Field("itemsPerPage") int itemsPerPage

    );


    @FormUrlEncoded
    @POST("application/SendEmail/email_sender.php")
        // Change this to the actual name of your PHP script
    Call<Void> sendEmail(
            @Field("email") String email,
            @Field("name") String name,
            @Field("message") String message,
            @Field("subject") String subject,
            @Field("to") String to
    );


    @FormUrlEncoded
    @POST("providers/get_providers_list.php")
    Call<ProviderListResponse> getProvidersList(
            @Field("current_latitude") double currentLatitude,
            @Field("current_longitude") double currentLongitude,
            @Field("page") int page,
            @Field("itemsPerPage") int itemsPerPage,
            @Field("radius") int radius
    );


    @GET("admin/get_settings.php")
    Call<SettingsRes> getSettings();

    @GET("application/get_block_status_user.php")
    Call<BlockRes> getStatusBlockedUSer(
            @Query("id") String id
    );

    @GET("application/get_block_status_provider.php")
    Call<BlockRes> getStatusBlockedProvider(
            @Query("id") String id
    );




    @FormUrlEncoded
    @POST("admin/get_maintenance.php")
    Call<MaintenanceRes> get_maintenance(

            @Field("id") String id
    );




    @FormUrlEncoded
    @POST("admin/get_admob.php")
    Call<AdsResponse> getAdmob(

            @Field("id") int id
    );

}


