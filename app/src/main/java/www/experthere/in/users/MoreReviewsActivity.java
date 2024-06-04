package www.experthere.in.users;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.nativead.NativeAd;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
import www.experthere.in.helper.ServiceDetailsBottomSheet;
import www.experthere.in.model.ReviewResponse;
import www.experthere.in.model.Reviews;

public class MoreReviewsActivity extends AppCompatActivity {

    String id,title;
    RecyclerView recyclerView;
    ReviewsAdapter adapter;
    ProgressBar progReview;
    List<Reviews> reviews = new ArrayList<>();
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int currentPage = 1;
    private final int itemsPerPage = 10;

    ShimmerFrameLayout shimmerFrameLayout;

    private void inflateShimmerRecyclerView(String type) {


        RecyclerView shimmerRecycler = findViewById(R.id.recycler_shimmer);

        MyShimmerAdapter shimmerAdapter = new MyShimmerAdapter(type);
        NonScrollableLayoutManager layoutManager = new NonScrollableLayoutManager(MoreReviewsActivity.this);
        shimmerRecycler.setLayoutManager(layoutManager);
        shimmerRecycler.setAdapter(shimmerAdapter);


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(getResources().getColor(R.color.white, this.getTheme()));

        setContentView(R.layout.activity_more_reviews);

        initBundleId();

        recyclerView = findViewById(R.id.recycler_review);
        progReview = findViewById(R.id.progressReview);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        adapter = new ReviewsAdapter(reviews, this,title);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);


        shimmerFrameLayout = findViewById(R.id.shimmer_review);
        inflateShimmerRecyclerView(GetShimmerStrings.PROVIDER_REVIEWS);
        shimmerFrameLayout.startShimmer();


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if (!isLoading && !isLastPage) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0
                            && totalItemCount >= itemsPerPage) {
                        // Load more data when reaching the end

                        isLoading = true;
                        currentPage++;
                        progReview.setVisibility(View.VISIBLE);


                        try {

                            new GetReviewsImagesTask(currentPage, itemsPerPage, Integer.parseInt(id)).execute();

                        } catch (Exception e) {

                        }
                    }
                }
            }
        });


        try {

            Log.d("lknldsn", "HIt "+id);

            new GetReviewsImagesTask(currentPage, itemsPerPage, Integer.parseInt(id)).execute();
        } catch (Exception e) {

        }


        findViewById(R.id.backReview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();

            }
        });



        showNativeAds();



    }

    private void showNativeAds() {


        SharedPreferences adsPref = getSharedPreferences("ads",MODE_PRIVATE);

        String nativeId = adsPref.getString("native", "ca-app-pub-3940256099942544/2247696110");


        if (adsPref.getBoolean("status", false)){




            int backgroundColor = ContextCompat.getColor(MoreReviewsActivity.this, R.color.main_screen_bg_color);
            ColorDrawable background = new ColorDrawable(backgroundColor);


            AdLoader adLoader = new AdLoader.Builder(MoreReviewsActivity.this, nativeId)
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
                            TemplateView template = findViewById(R.id.my_template);
                            template.setStyles(styles);
                            template.setNativeAd(nativeAd);

                            template.setVisibility(View.VISIBLE);

                        }
                    })
                    .build();

            adLoader.loadAd(new AdRequest.Builder().build());



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
            progReview.setVisibility(View.VISIBLE);

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
                        , Integer.parseInt(getSharedPreferences("user",MODE_PRIVATE).getString("id","0"))

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
            recyclerView.setVisibility(View.VISIBLE);
            shimmerFrameLayout.setVisibility(View.GONE);
            shimmerFrameLayout.stopShimmer();

            if (apiResponse != null) {
                // Handle the API response

                if ( apiResponse.getReviews()==null){

                   findViewById(R.id.noReviewTxt).setVisibility(View.VISIBLE);
                   recyclerView.setVisibility(View.GONE);
                   progReview.setVisibility(View.GONE);
                }


                    isLoading = false;


                if (currentPage == 1) {
                    // If it's the first page, clear the old list
                    reviews.clear();
                    adapter.notifyDataSetChanged();
                }


                List<Reviews> newList = apiResponse.getReviews();


                if (newList != null && !newList.isEmpty()) {

                    Log.d("ksxln", "main list : " + newList.size());
                    Log.d("ksxln", "main list : " + newList.get(0).getReview());





                    reviews.addAll(newList);
                    adapter.setData(reviews);
                    // Move notifyDataSetChanged outside the loop
                    adapter.notifyDataSetChanged();

                    if (newList.size() < itemsPerPage) {
                        isLastPage = true;
                    }
                } else if (currentPage > 1) {
                    // If no new data on a subsequent page, consider it the last page
                    isLastPage = true;
                }


            }else {
                
                
            }


            // Notify the adapter outside the if condition
//      
//                adapter.notifyDataSetChanged();
        
        
            
           
        }
        

    }


    private void initBundleId() {

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            id = bundle.getString("id", "0");
            title = bundle.getString("title", " ");
        }

    }
}