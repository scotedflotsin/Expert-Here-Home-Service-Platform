package www.experthere.in.helper;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.richeditor.RichEditor;
import retrofit2.Call;
import retrofit2.Response;
import www.experthere.in.Common.GetShimmerStrings;
import www.experthere.in.R;
import www.experthere.in.adapters.ReviewsAdapter;
import www.experthere.in.adapters.serviceListUser.ReviewsAdapterImages;
import www.experthere.in.adapters.shimmer.MyShimmerAdapter;
import www.experthere.in.adapters.shimmer.NonScrollableLayoutManager;
import www.experthere.in.api.ApiClient;
import www.experthere.in.api.ApiInterface;
import www.experthere.in.calling.OutGoingCallActivity;
import www.experthere.in.model.ProviderStatusResponse;
import www.experthere.in.model.RatingApiResponse;
import www.experthere.in.model.RatingsData;
import www.experthere.in.model.Review;
import www.experthere.in.model.ReviewDetailsModel;
import www.experthere.in.model.ReviewResponse;
import www.experthere.in.model.Reviews;
import www.experthere.in.model.service_user_cat.Service;
import www.experthere.in.serviceProvider.ServiceProviderProfileDetailsActivity;
import www.experthere.in.users.AddReviewActivity;
import www.experthere.in.users.MoreReviewsActivity;

public class ServiceDetailsBottomSheet extends BottomSheetDialogFragment {


    Service services;
    Activity activity;
    RecyclerView recycler_review, recyclerViewImages;
    ReviewsAdapter adapter;
    ReviewsAdapterImages adapterImages;
    ProgressBar progReview, progImages;

    List<Reviews> reviews = new ArrayList<>();

    List<Reviews> reviewsImages = new ArrayList<>();
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int currentPage = 1;
    private int itemsPerPage = 10;


    private boolean isLoadingImage = false;
    private boolean isLastPageImage = false;
    private int currentPageImage = 1;
    private int itemsPerPageImage = 10;

    TextView reviewTxt;

    SharedPreferences preferences;
    String userID;

    AppCompatButton rateBtn, reviewEditBtn;


    ProgressBar progressDetailsLay;
    LinearLayout detailsLayout;

    Review myReviewDetails = null;
    boolean showViewBtn;


    ProcessingDialog processingDialog;

    TextView nodataTxtReviews, nodataTxtReviewImages;

    ShimmerFrameLayout shimmerFrameLayout, shimmerFrameLayoutImages;


    SharedPreferences adsPref;
     String nativeId ;





    public ServiceDetailsBottomSheet(Service services, Activity activity, boolean showViewBtn) {
        this.services = services;
        this.activity = activity;
        this.showViewBtn = showViewBtn;
    }


    private void inflateShimmerRecyclerView(View view, String type) {


        RecyclerView shimmerRecycler = view.findViewById(R.id.recycler_shimmer);

        MyShimmerAdapter shimmerAdapter = new MyShimmerAdapter(type);
        NonScrollableLayoutManager layoutManager = new NonScrollableLayoutManager(activity);
        shimmerRecycler.setLayoutManager(layoutManager);
        shimmerRecycler.setAdapter(shimmerAdapter);


    }

    private void inflateShimmerRecyclerViewImages(View view, String type) {


        RecyclerView shimmerRecycler = view.findViewById(R.id.recycler_shimmer_image);

        MyShimmerAdapter shimmerAdapter = new MyShimmerAdapter(type);
        NonScrollableLayoutManager layoutManager = new NonScrollableLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
        shimmerRecycler.setLayoutManager(layoutManager);
        shimmerRecycler.setAdapter(shimmerAdapter);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.service_bottom_sheet, container, false);

        processingDialog = new ProcessingDialog(activity);

        preferences = activity.getSharedPreferences("user", MODE_PRIVATE);
        userID = preferences.getString("id", "0");



         adsPref = activity.getSharedPreferences("ads", MODE_PRIVATE);
         nativeId = adsPref.getString("native", "ca-app-pub-3940256099942544/2247696110");




        shimmerFrameLayout = view.findViewById(R.id.shimmer_review);
        inflateShimmerRecyclerView(view, GetShimmerStrings.PROVIDER_REVIEWS);
        shimmerFrameLayout.startShimmer();


        shimmerFrameLayoutImages = view.findViewById(R.id.shimmer_review_image);
        inflateShimmerRecyclerViewImages(view, GetShimmerStrings.PROVIDER_REVIEWS_Images);
        shimmerFrameLayoutImages.startShimmer();


        ImageView logo = view.findViewById(R.id.companyLogo);
        ImageView serviceImage = view.findViewById(R.id.serviceImage);

        recycler_review = view.findViewById(R.id.recycler_review);
        recyclerViewImages = view.findViewById(R.id.recyclerReviewImages);
        progReview = view.findViewById(R.id.progReview);
        progImages = view.findViewById(R.id.progImages);

        reviewTxt = view.findViewById(R.id.reviewTxt);


        rateBtn = view.findViewById(R.id.rateBtn);
        reviewEditBtn = view.findViewById(R.id.editBtn);

        progressDetailsLay = view.findViewById(R.id.progDetailsLay);
        detailsLayout = view.findViewById(R.id.detailsLayout);

        nodataTxtReviewImages = view.findViewById(R.id.nodataTxtReviewImages);
        nodataTxtReviews = view.findViewById(R.id.nodataTxtReviews);


        TextView titleTxt = view.findViewById(R.id.titleTxt);
        TextView personTxt = view.findViewById(R.id.personTxt);
        TextView durationTxt = view.findViewById(R.id.durationTxt);

        TextView displayPriceTxt = view.findViewById(R.id.displayPriceTxt);
        TextView cutPriceTxt = view.findViewById(R.id.cutPriceTxt);
        TextView discountPercentageTxt = view.findViewById(R.id.discounPercentagetTxt);

        TextView companyName = view.findViewById(R.id.companyNameTxt);

        TextView serviceCost = view.findViewById(R.id.serviceCost);
        TextView discountCost = view.findViewById(R.id.discountCost);
        TextView taxValueTxt = view.findViewById(R.id.taxValueTxt);
        TextView visitingCharges = view.findViewById(R.id.visitingCharges);
        TextView totalCharge = view.findViewById(R.id.totalCharge);

        RichEditor htmlEditor = view.findViewById(R.id.htmlEditor);
        htmlEditor.setEnabled(false);


        if (showViewBtn) {

            view.findViewById(R.id.viewProviderBtn).setVisibility(View.VISIBLE);


        } else {

            view.findViewById(R.id.viewProviderBtn).setVisibility(View.GONE);
        }


        reviewEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (myReviewDetails != null) {
                    Intent intent = new Intent(activity, AddReviewActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("provider_id", String.valueOf(services.getProviderId()));
                    bundle.putString("service_id", String.valueOf(services.getId()));
                    bundle.putString("user_id", userID);
                    bundle.putString("review_id", myReviewDetails.getReview_id());
                    bundle.putString("title", services.getServiceTitle());
                    bundle.putBoolean("isEdit", true);

                    intent.putExtras(bundle);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.startActivity(intent);
                }


            }
        });
        view.findViewById(R.id.moreReviews).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(activity, MoreReviewsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("id", String.valueOf(services.getId()));
                bundle.putString("title", String.valueOf(services.getServiceTitle()));
                intent.putExtras(bundle);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(intent);


            }
        });
        view.findViewById(R.id.rateBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(activity, AddReviewActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("provider_id", String.valueOf(services.getProviderId()));
                bundle.putString("service_id", String.valueOf(services.getId()));
                bundle.putString("user_id", userID);
                bundle.putString("review_id", "0");
                bundle.putString("title", services.getServiceTitle());
                bundle.putBoolean("isEdit", false);
                intent.putExtras(bundle);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(intent);


            }
        });


        if (services.getTaxType().contains("Tax Excluded")) {

            double discountPrice = Double.parseDouble(services.getDiscountedPrice());
            double finalPrice = Double.parseDouble(services.getFinalPrice());

            double discountPercentage = (1 - (discountPrice / finalPrice)) * 100;

            discountPercentageTxt.setText((int) Math.round(discountPercentage) + "% OFF");

            displayPriceTxt.setText(services.getCurrency() + " " + (int) Math.round(discountPrice));

            cutPriceTxt.setText(services.getCurrency() + " " + (int) Math.round(finalPrice));


            double visitCharge = services.getProvider().getVisitingCharges();
            double grand_total = discountPrice + visitCharge;


            totalCharge.setText(services.getCurrency() + " " + Math.round(grand_total));

        } else {


            double discountPrice = Double.parseDouble(services.getDiscountedPrice());
            double cutPrice = Double.parseDouble(services.getFinalPrice());
            double tax = Double.parseDouble(services.getTaxValue());

            double discountPercentage = calculateDiscountPercentage(discountPrice, cutPrice);
            double finalCutPrice = calculateFinalCutPrice(cutPrice, tax);
            double finalPrice = calculateFinalPrice(discountPrice, tax);

            discountPercentageTxt.setText(Math.round(discountPercentage) + "% OFF");
            displayPriceTxt.setText(services.getCurrency() + " " + Math.round(finalPrice));
            cutPriceTxt.setText(services.getCurrency() + " " + Math.round(finalCutPrice));


            double visitCharge = services.getProvider().getVisitingCharges();
            double grand_total = finalPrice + visitCharge;


            totalCharge.setText(services.getCurrency() + " " + Math.round(grand_total));

        }


        titleTxt.setText(services.getServiceTitle());
        personTxt.setText(services.getMembers() + " Person(s)");


        if (services.getServiceDuration().contains("Hourly")) {
            durationTxt.setText(services.getServiceDuration().replace("Hourly", "Hour"));
        } else {

            durationTxt.setText(services.getServiceDuration());
        }


        Glide.with(activity).load(services.getProvider().getLogoImage()).circleCrop().into(logo);
        Glide.with(activity).load(services.getServiceImageUrl()).into(serviceImage);
        companyName.setText(services.getProvider().getCompanyName());
        htmlEditor.setHtml(services.getDescription());
        serviceCost.setText(services.getCurrency() + " " + services.getFinalPrice());
        discountCost.setText(services.getCurrency() + " " + services.getDiscountedPrice());

        Log.d("TAXTTYUEUEU", "onCreateView: " + services.getTaxType());

        if (services.getTaxType().contains("Tax Include")) {
            taxValueTxt.setText(services.getTaxValue() + " % Including All Taxes!");
        } else {
            taxValueTxt.setText("nill");
        }

        visitingCharges.setText(services.getProvider().getCurrency() + " " + services.getProvider().getVisitingCharges());


        view.findViewById(R.id.viewProviderBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(activity, ServiceProviderProfileDetailsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                Bundle bundle = new Bundle();
                bundle.putString("provider_id", String.valueOf(services.getProvider().getId()));
                bundle.putString("provider_name", String.valueOf(services.getProvider().getName()));
                bundle.putString("provider_banner", String.valueOf(services.getProvider().getBannerImage()));
                bundle.putString("provider_logo", String.valueOf(services.getProvider().getLogoImage()));

                bundle.putString("provider_company_name", String.valueOf(services.getProvider().getCompanyName()));


                intent.putExtras(bundle);
                activity.startActivity(intent);


            }
        });


        getReviews(view, activity);


        view.findViewById(R.id.bookBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                try {

                    processingDialog.show();
                    new GetStatus(String.valueOf(services.getProvider().getId())).execute();

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }


            }
        });



        showNativeAds1(view);
        showNativeAds2(view);
//        showNativeAdsBig(view);





        return view;


    }

    private void showNativeAds1(View view) {


        if (adsPref.getBoolean("status", false)){



            MobileAds.initialize(activity);

            int backgroundColor = ContextCompat.getColor(activity, R.color.main_screen_bg_color);
            ColorDrawable background = new ColorDrawable(backgroundColor);


            AdLoader adLoader = new AdLoader.Builder(activity, nativeId)
                    .withAdListener(new AdListener() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            super.onAdFailedToLoad(loadAdError);


                        }

                        @Override
                        public void onAdLoaded() {
                            super.onAdLoaded();
                        }
                    })
                    .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {

                        @Override
                        public void onNativeAdLoaded(NativeAd nativeAd) {
                            NativeTemplateStyle styles = new
                                    NativeTemplateStyle.Builder().withMainBackgroundColor(background).build();
                            TemplateView template = view.findViewById(R.id.my_template);
                            template.setStyles(styles);
                            template.setNativeAd(nativeAd);

                            template.setVisibility(View.VISIBLE);

                        }
                    })
                    .build();

            adLoader.loadAd(new AdRequest.Builder().build());



        }


    }

    private void showNativeAds2(View view) {


        if (adsPref.getBoolean("status", false)){



            MobileAds.initialize(activity);

            int backgroundColor = ContextCompat.getColor(activity, R.color.main_screen_bg_color);
            ColorDrawable background = new ColorDrawable(backgroundColor);


            AdLoader adLoader = new AdLoader.Builder(activity, nativeId)
                    .withAdListener(new AdListener() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            super.onAdFailedToLoad(loadAdError);


                        }

                        @Override
                        public void onAdLoaded() {
                            super.onAdLoaded();
                        }
                    })
                    .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {

                        @Override
                        public void onNativeAdLoaded(NativeAd nativeAd) {
                            NativeTemplateStyle styles = new
                                    NativeTemplateStyle.Builder().withMainBackgroundColor(background).build();
                            TemplateView template = view.findViewById(R.id.my_template2);
                            template.setStyles(styles);
                            template.setNativeAd(nativeAd);

                            template.setVisibility(View.VISIBLE);

                        }
                    })
                    .build();

            adLoader.loadAd(new AdRequest.Builder().build());



        }



    }

    private void showNativeAdsBig(View view) {

        if (adsPref.getBoolean("status", false)){



            MobileAds.initialize(activity);

            int backgroundColor = ContextCompat.getColor(activity, R.color.main_screen_bg_color);
            ColorDrawable background = new ColorDrawable(backgroundColor);


            AdLoader adLoader = new AdLoader.Builder(activity, nativeId)
                    .withAdListener(new AdListener() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            super.onAdFailedToLoad(loadAdError);


                        }

                        @Override
                        public void onAdLoaded() {
                            super.onAdLoaded();
                        }
                    })
                    .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {

                        @Override
                        public void onNativeAdLoaded(NativeAd nativeAd) {
                            NativeTemplateStyle styles = new
                                    NativeTemplateStyle.Builder().withMainBackgroundColor(background).build();
                            TemplateView template = view.findViewById(R.id.my_template_big);
                            template.setStyles(styles);
                            template.setNativeAd(nativeAd);

                            template.setVisibility(View.VISIBLE);

                        }
                    })
                    .build();

            adLoader.loadAd(new AdRequest.Builder().build());



        }


    }



    private void getReviews(View view, Activity activity) {

        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        LinearLayoutManager imagesLayoutManager = new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);

        adapter = new ReviewsAdapter(reviews, activity, services.getServiceTitle());
        adapterImages = new ReviewsAdapterImages(reviews, activity);


        recycler_review.setLayoutManager(layoutManager);
        recycler_review.setHasFixedSize(true);
        recycler_review.setAdapter(adapter);


        recyclerViewImages.setLayoutManager(imagesLayoutManager);
        recyclerViewImages.setHasFixedSize(true);
        recyclerViewImages.setAdapter(adapterImages);

        recyclerViewImages.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if (!isLoadingImage && !isLastPageImage) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0
                            && totalItemCount >= itemsPerPageImage) {
                        // Load more data when reaching the end

                        isLoadingImage = true;
                        currentPageImage++;
                        progImages.setVisibility(View.VISIBLE);


                        try {

                            new GetReviewsImagesTask(currentPageImage, itemsPerPageImage, services.getId()).execute();

                        } catch (Exception e) {


                            CustomToastNegative.create(activity, "Error " + e.getMessage());
                        }
                    }
                }
            }
        });


//        try {
//
//            Log.d("HGSHG", "HIt ");
//
//            new GetReviewsTask(currentPage, itemsPerPage, services.getId()).execute();
//            new GetReviewsImagesTask(currentPage, itemsPerPage, services.getId()).execute();
//            new GetRatingValues(services.getId(), activity).execute();
//            new checkUserReview(String.valueOf(services.getId()), userID).execute();
//
//        } catch (Exception e) {
//
//            Toast.makeText(activity, "Error " + e.getMessage(), Toast.LENGTH_SHORT).show();
//        }


    }


    private void dismissDialog() {
        dismiss();
    }

    private double calculateDiscountPercentage(double discountedPrice, double cutPrice) {
        return (1 - (discountedPrice / cutPrice)) * 100;
    }

    private double calculateFinalCutPrice(double cutPrice, double taxValue) {
        return cutPrice + (cutPrice * (taxValue / 100));
    }

    private double calculateFinalPrice(double cutPrice, double taxValue) {
        return cutPrice + (cutPrice * (taxValue / 100));
    }


    public class GetRatingValues extends AsyncTask<Void, Void, RatingApiResponse> {

        int serviceId;
        Activity activity;

        public GetRatingValues(int serviceId, Activity activity) {
            this.serviceId = serviceId;
            this.activity = activity;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected RatingApiResponse doInBackground(Void... voids) {
            try {
                // Create the Retrofit instance
                ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

                // Make the API call
                Call<RatingApiResponse> call = apiInterface.getRatingData(
                        serviceId

                );


                Response<RatingApiResponse> response = call.execute();

                if (response.isSuccessful()) {
                    return response.body();
                } else {
                    // Handle error
                    Log.e("ksxln", "API call failed. Code: " + response.code());
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(RatingApiResponse apiResponse) {
            super.onPostExecute(apiResponse);

            if (apiResponse != null) {
                // Handle the API response

                RatingsData response = apiResponse.getRatingsData();

                if (response != null) {

                    Log.d("ksxln", "AVG RATING : " + response.getAverageRating());
                    Log.d("ksxln", "TOTAL RATINGS : " + response.getTotalRatings());

                    Log.d("ksxln", "5 : " + response.getFiveStar());
                    Log.d("ksxln", "4: " + response.getFourStar());
                    Log.d("ksxln", "3: " + response.getThreeStar());
                    Log.d("ksxln", "2 : " + response.getTwoStar());
                    Log.d("ksxln", "main list : " + response.getOneStar());

                    DecimalFormat decimalFormat = new DecimalFormat("#.#");


                    reviewTxt.setText(decimalFormat.format(response.getAverageRating()) + "(" + response.getTotalRatings() + ")");


                }


            }

        }
    }

    public class GetReviewsTask extends AsyncTask<Void, Void, ReviewResponse> {

        private final int page;
        private final int itemsPerPage;

        int serviceId;

        public GetReviewsTask(int page, int itemsPerPage, int serviceId) {
            this.page = page;
            this.itemsPerPage = itemsPerPage;
            this.serviceId = serviceId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected ReviewResponse doInBackground(Void... voids) {
            try {
                // Create the Retrofit instance
                ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

                // Make the API call
                Call<ReviewResponse> call = apiInterface.getReviews(
                        serviceId,
                        page, // page
                        itemsPerPage // itemsPerPage
                        , Integer.parseInt(activity.getSharedPreferences("user", MODE_PRIVATE).getString("id", "0"))

                );


                Log.d("ksxln", "page : " + page);
                Log.d("ksxln", "item per page : " + itemsPerPage);
                Log.d("ksxln", "rService id : " + serviceId);

                Response<ReviewResponse> response = call.execute();

                if (response.isSuccessful()) {
                    return response.body();
                } else {
                    // Handle error
                    Log.e("ksxln", "API call failed. Code: " + response.code());
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ReviewResponse apiResponse) {
            super.onPostExecute(apiResponse);

            progReview.setVisibility(View.GONE);
            recycler_review.setVisibility(View.VISIBLE);

            shimmerFrameLayout.setVisibility(View.GONE);
            shimmerFrameLayout.stopShimmer();

            if (apiResponse != null) {
                // Handle the API response

                isLoading = false;


//                if (currentPage == 1) {
//                    // If it's the first page, clear the old list
//                    reviews.clear();
//                    adapter.notifyDataSetChanged();
//                }

                reviews = apiResponse.getReviews();


                if (reviews != null && !reviews.isEmpty()) {

                    Log.d("ksxln", "main list : " + reviews.size());
                    Log.d("ksxln", "main list : " + reviews.get(0).getReview());


                    adapter.setData(reviews);
                    // Move notifyDataSetChanged outside the loop
                    adapter.notifyDataSetChanged();


                    if (reviews.size() < itemsPerPage) {
                        isLastPage = true;
                    }
                } else if (currentPage > 1) {
                    // If no new data on a subsequent page, consider it the last page
                    isLastPage = true;
                } else {

                    progReview.setVisibility(View.GONE);
                    recycler_review.setVisibility(View.GONE);
                    nodataTxtReviews.setVisibility(View.VISIBLE);


                }


            }


            // Notify the adapter outside the if condition
//                adapter.notifyDataSetChanged();
        }

    }

    public class GetReviewsImagesTask extends AsyncTask<Void, Void, ReviewResponse> {

        private final int page;
        private final int itemsPerPage;
        int serviceId;

        public GetReviewsImagesTask(int page, int itemsPerPage, int serviceId) {
            this.page = page;
            this.itemsPerPage = itemsPerPage;
            this.serviceId = serviceId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progImages.setVisibility(View.VISIBLE);

        }

        @Override
        protected ReviewResponse doInBackground(Void... voids) {
            try {
                // Create the Retrofit instance
                ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

                // Make the API call
                Call<ReviewResponse> call = apiInterface.getReviews(
                        serviceId,
                        page, // page
                        itemsPerPage // itemsPerPage
                        , Integer.parseInt(activity.getSharedPreferences("user", MODE_PRIVATE).getString("id", "0"))

                );


                Log.d("ksxln", "page : " + page);
                Log.d("ksxln", "item per page : " + itemsPerPage);
                Log.d("ksxln", "rService id : " + serviceId);

                Response<ReviewResponse> response = call.execute();

                if (response.isSuccessful()) {
                    return response.body();
                } else {
                    // Handle error
                    Log.e("ksxln", "API call failed. Code: " + response.code());
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ReviewResponse apiResponse) {
            super.onPostExecute(apiResponse);

            progImages.setVisibility(View.GONE);
            recyclerViewImages.setVisibility(View.VISIBLE);
            shimmerFrameLayoutImages.setVisibility(View.GONE);
            shimmerFrameLayoutImages.stopShimmer();


            if (apiResponse != null) {
                // Handle the API response

                isLoadingImage = false;


                if (currentPageImage == 1) {
                    // If it's the first page, clear the old list
                    reviewsImages.clear();
                    adapter.notifyDataSetChanged();
                }


                List<Reviews> newList = apiResponse.getReviews();


                if (newList != null && !newList.isEmpty()) {

                    Log.d("ksxln", "main list : " + newList.size());
                    Log.d("ksxln", "main list : " + newList.get(0).getReview());


                    reviewsImages.addAll(newList);
                    adapterImages.setData(reviewsImages);
                    // Move notifyDataSetChanged outside the loop
                    adapterImages.notifyDataSetChanged();

                    if (newList.size() < itemsPerPage) {
                        isLastPageImage = true;
                    }
                } else if (currentPageImage > 1) {
                    // If no new data on a subsequent page, consider it the last page
                    isLastPageImage = true;
                } else {

                    nodataTxtReviewImages.setVisibility(View.VISIBLE);
                    progImages.setVisibility(View.GONE);
                    recyclerViewImages.setVisibility(View.GONE);


                }


            }


            // Notify the adapter outside the if condition
//                adapter.notifyDataSetChanged();
        }

    }

    public class checkUserReview extends AsyncTask<Void, Void, ReviewDetailsModel> {


        String serviceId, userId;

        public checkUserReview(String serviceId, String userId) {
            this.serviceId = serviceId;
            this.userId = userId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDetailsLay.setVisibility(View.VISIBLE);
            detailsLayout.setVisibility(View.GONE);

        }

        @Override
        protected ReviewDetailsModel doInBackground(Void... voids) {
            try {
                // Create the Retrofit instance
                ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

                // Make the API call
                Call<ReviewDetailsModel> call = apiInterface.checkUserId(
                        userId,
                        serviceId

                );


                Response<ReviewDetailsModel> response = call.execute();

                if (response.isSuccessful()) {
                    return response.body();
                } else {
                    // Handle error
                    Log.e("ksxln", "API call failed. Code: " + response.code());
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ReviewDetailsModel apiResponse) {
            super.onPostExecute(apiResponse);

            progressDetailsLay.setVisibility(View.GONE);
            detailsLayout.setVisibility(View.VISIBLE);

            if (apiResponse.isSuccess()) {

                if (apiResponse.getMessage().equals("already_submitted")) {


                    rateBtn.setVisibility(View.GONE);
                    reviewEditBtn.setVisibility(View.VISIBLE);

                    myReviewDetails = apiResponse.getReview();

                }
                if (apiResponse.getMessage().equals("not_submitted")) {

                    rateBtn.setVisibility(View.VISIBLE);
                    reviewEditBtn.setVisibility(View.GONE);
                }


            } else {


            }


        }

    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onResume() {
        super.onResume();


        currentPage = 1;
        itemsPerPage = 10;
        boolean isLoading = false;
        boolean isLastPage = false;
        isLoadingImage = false;
        isLastPageImage = false;
        currentPageImage = 1;
        itemsPerPageImage = 10;


        if (adapter != null && reviews != null) {

            reviews.clear();
            adapter.notifyDataSetChanged();
        }

        if (adapterImages != null && reviewsImages != null) {
            reviewsImages.clear();
            adapterImages.notifyDataSetChanged();
        }

        try {

            Log.d("HGSHG", "HIt ");

            new GetReviewsTask(currentPage, itemsPerPage, services.getId()).execute();
            new GetReviewsImagesTask(currentPage, itemsPerPage, services.getId()).execute();
            new GetRatingValues(services.getId(), activity).execute();
            new checkUserReview(String.valueOf(services.getId()), userID).execute();

        } catch (Exception e) {

            CustomToastNegative.create(activity, "Error " + e.getMessage());

        }


    }


    private class GetStatus extends AsyncTask<Void, Void, ProviderStatusResponse> {


        String providerId;

        public GetStatus(String providerId) {
            this.providerId = providerId;
        }


        @Override
        protected ProviderStatusResponse doInBackground(Void... voids) {
            // Create Retrofit instance
            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);


            // Make API call
            Call<ProviderStatusResponse> call = apiInterface.getProviderStatus(providerId);

            try {
                // Execute the call synchronously
                Response<ProviderStatusResponse> response = call.execute();

                if (response.isSuccessful()) {
                    return response.body();
                } else {
                    // Handle unsuccessful response


                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            processingDialog.dismiss();

                        }
                    });

                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        processingDialog.dismiss();

                    }
                });
                return null;
            }
        }

        @Override
        protected void onPostExecute(ProviderStatusResponse apiResponse) {
            super.onPostExecute(apiResponse);

            processingDialog.dismiss();

            if (apiResponse != null) {
                // Handle the API response

                if (apiResponse.isSuccess()) {


                    if (apiResponse.getMessage().equals("Data retrieved successfully")) {


                        String status = apiResponse.getData().getStatus();

                        if (status.equals("active")) {

                            Bundle bundle = new Bundle();
                            bundle.putString("call_by", "user");

                            bundle.putString("user_id", userID);
                            bundle.putString("provider_id", String.valueOf(services.getProvider().getId()));
                            bundle.putString("dp", services.getProvider().getLogoImage());
                            bundle.putString("display_name", services.getProvider().getName());
                            bundle.putString("selected_service", services.getServiceTitle());

                            Intent intent = new Intent(activity, OutGoingCallActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtras(bundle);
                            activity.startActivity(intent);

                            Log.d("CALLTESTANKU", "Provider is Active ");


                        }

                        if (status.equals("deactive")) {

                            CustomToastNegative.create(activity, "Provider Is Deactive Try Again Later!");

                            Log.d("CALLTESTANKU", "provider is Deactive ");

                        }


                    }


                } else {


                    CustomToastNegative.create(activity, " "+apiResponse.getMessage());

                }

            } else {
            }


        }
    }


}



