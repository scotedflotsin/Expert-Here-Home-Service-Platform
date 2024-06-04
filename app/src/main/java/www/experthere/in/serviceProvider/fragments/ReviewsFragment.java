package www.experthere.in.serviceProvider.fragments;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import www.experthere.in.Common.GetShimmerStrings;
import www.experthere.in.R;
import www.experthere.in.adapters.AllReviewsAdapter;
import www.experthere.in.adapters.shimmer.MyShimmerAdapter;
import www.experthere.in.adapters.shimmer.NonScrollableLayoutManager;
import www.experthere.in.api.ApiClient;
import www.experthere.in.api.ApiInterface;
import www.experthere.in.model.AllReviewResponse;
import www.experthere.in.model.AllReviews;
import www.experthere.in.model.AllReviewsStats;
import www.experthere.in.model.Stats;


public class ReviewsFragment extends Fragment {
    private static final String TAG = "REVIEWS_FRAGMENT";
    String providerID;

    Activity activity;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    List<AllReviews> reviews;

    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int currentPage = 1;
    private final int itemsPerPage = 10;

    AllReviewsAdapter adapter;

    Stats stats;

//    RatingReviews ratingReviews;

    TextView avgRatingTxt, totalReviewsTxt;
    AppCompatRatingBar ratingBarAvg;

    SwipeRefreshLayout swipeRefreshLayout;

    ShimmerFrameLayout shimmerFrameLayout;

    ProgressBar star1Progress,star2Progress,star3Progress,star4Progress,star5Progress;

    LinearLayout txtNoData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = requireActivity();
        initBundle(activity);


        Log.d("progressSOlving", "onCreate");


    }
    private void inflateShimmerRecyclerView(View view ,String type) {


        RecyclerView shimmerRecycler = view.findViewById(R.id.recyclerShimmer);

        MyShimmerAdapter shimmerAdapter = new MyShimmerAdapter(type);
        NonScrollableLayoutManager layoutManager = new NonScrollableLayoutManager(activity);
        shimmerRecycler.setLayoutManager(layoutManager);
        shimmerRecycler.setAdapter(shimmerAdapter);


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_reviews, container, false);


        Log.d("progressSOlving", "onCreate views");

        txtNoData = view.findViewById(R.id.txtNoData);

         star1Progress = view.findViewById(R.id.star1Progress);
         star2Progress = view.findViewById(R.id.star2Progress);
         star3Progress = view.findViewById(R.id.star3Progress);
         star4Progress = view.findViewById(R.id.star4Progress);
         star5Progress = view.findViewById(R.id.star5Progress);




        swipeRefreshLayout = view.findViewById(R.id.swipeLayout);
        shimmerFrameLayout = view.findViewById(R.id.shimmerReview);
        inflateShimmerRecyclerView(view, GetShimmerStrings.PROVIDER_REVIEWS);

        shimmerFrameLayout.startShimmer();

        reviews = new ArrayList<>();

//        ratingReviews = view.findViewById(R.id.rating_reviews);

        avgRatingTxt = view.findViewById(R.id.avgRatingTxt);
        totalReviewsTxt = view.findViewById(R.id.totalReviewsTxt);
        ratingBarAvg = view.findViewById(R.id.ratingBarNew);


        recyclerView = view.findViewById(R.id.recycler_review);
        progressBar = view.findViewById(R.id.progressService);

        adapter = new AllReviewsAdapter(reviews, activity);

        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
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
                        progressBar.setVisibility(View.VISIBLE);


                        try {

                            new GetReviewsTask(currentPage, itemsPerPage, Integer.parseInt(providerID)).execute();

                        } catch (Exception e) {
                            Log.d(TAG, "Error 1  "+e.getMessage());

                        }
                    }
                }
            }
        });




        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPage = 1;
                isLoading = false;
                isLastPage = false;
                reviews.clear();
                adapter.notifyDataSetChanged();

//                ratingReviews.clearAll();

                shimmerFrameLayout.startShimmer();
                shimmerFrameLayout.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);

                try {


                    new GetReviewsTask(currentPage, itemsPerPage, Integer.parseInt(providerID)).execute();
                    new GetReviewDetails(providerID).execute();


                } catch (NumberFormatException e) {
                    throw new RuntimeException(e);
                }
//

            }
        });


        try {

            Log.d("HGSHG", "HIt ");

            new GetReviewsTask(currentPage, itemsPerPage, Integer.parseInt(providerID)).execute();
//            new GetReviewDetails(providerID).execute();


        } catch (Exception e) {

            Log.d(TAG, "Error 2  "+e.getMessage());

        }


        return view;
    }

    private class GetReviewDetails extends AsyncTask<Void, Void, AllReviewsStats> {


        private final String providerID;


        public GetReviewDetails(String providerID) {
            this.providerID = providerID;
        }

        @Override
        protected AllReviewsStats doInBackground(Void... params) {
            // The doInBackground method remains the same as in the previous example

            try {

                // Create ReviewService instance
                ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);


                // Make the API call
                Call<AllReviewsStats> call = apiInterface.getAllReviewStats(providerID);

                // Execute the call synchronously
                Response<AllReviewsStats> response = call.execute();

                // Check if the response is successful
                if (response.isSuccessful() && response.body() != null) {

                    return response.body();


                } else {

                    if (swipeRefreshLayout!=null) {
                        swipeRefreshLayout.setRefreshing(false);
                    }

                    return null;
                }
            } catch (IOException e) {


                if (swipeRefreshLayout!=null) {
                    swipeRefreshLayout.setRefreshing(false);
                }

                Log.e("SubmitReviewTask", "Error making API call", e);
                return null;
            }

        }

        @Override
        protected void onPostExecute(AllReviewsStats apiResponse) {
            // The onPostExecute method remains the same as in the previous example
            if (swipeRefreshLayout!=null) {
                swipeRefreshLayout.setRefreshing(false);
            }


            if (apiResponse != null && apiResponse.isSuccess()) {
                // API call successful

                stats = apiResponse.getStats();


                if (stats != null) {

//                    String progress = apiResponse.getStats().getAverage_rating();

//                    ratingBar.setProgress(Integer.parseInt(progress));
//                    avgRatingsTxt.setText(String.valueOf(progress));
//                    reviewersTxt.setText("("+apiResponse.getStats().getTotal_reviews()+") Reviewers");


                    Log.d("STATSGDGS", "onPostExecute: " + stats.getAverage_rating());
                    Log.d("STATSGDGS", "onPostExecute: " + stats.getTotal_reviews());
                    Log.d("STATSGDGS", "onPostExecute: " + stats.getOne_star());
                    Log.d("STATSGDGS", "onPostExecute: " + stats.getTwo_star());
                    Log.d("STATSGDGS", "onPostExecute: " + stats.getThree_star());
                    Log.d("STATSGDGS", "onPostExecute: " + stats.getFour_star());
                    Log.d("STATSGDGS", "onPostExecute: " + stats.getFive_star());
//
//                    ratingBar.setRating(stats.getAverage_rating());
//                    avgRatingsTxt.setText(String.valueOf(stats.getAverage_rating()));
//                    reviewersTxt.setText("("+apiResponse.getStats().getTotal_reviews()+") Reviewers");

//
                    int[] colors = new int[]{
                            Color.parseColor("#69A157"),
                            Color.parseColor("#69A157"),
                            Color.parseColor("#69A157"),
                            Color.parseColor("#69A157"),
                            Color.parseColor("#69A157")};
//
//                    int raters[] = new int[]{ ,
//                            new Random().nextInt(100),
//                            new Random().nextInt(100),
//                            new Random().nextInt(100),
//                            new Random().nextInt(100)
//                    };
//
//
//                    ratingReviews.createRatingBars(100, BarLabels.STYPE1, colors, raters);

                    // Assuming you have the ratings data from your API response
                    int totalReviews = stats.getTotal_reviews();  // Replace with actual value
                    int oneStarCount = Integer.parseInt(stats.getOne_star());  // Replace with actual value
                    int twoStarCount = Integer.parseInt(stats.getTwo_star());  // Replace with actual value
                    int threeStarCount = Integer.parseInt(stats.getThree_star());  // Replace with actual value
                    int fourStarCount = Integer.parseInt(stats.getFour_star());  // Replace with actual value
                    int fiveStarCount = Integer.parseInt(stats.getFive_star());  // Replace with actual value

                    // Set the total number of reviews
//                    ratingReviews.setMaxBarValue(totalReviews);

                    String[] labels = new String[]{
                            "1 ", "2 ", "3 ", "4 ", "5 ",};


                    int[] raters = new int[]{oneStarCount, twoStarCount, threeStarCount, fourStarCount, fiveStarCount
                    };




//                    ratingReviews.setLength(3.0);


                    String v = String.format("%.1f", stats.getAverage_rating());

                    avgRatingTxt.setText(v);
                    ratingBarAvg.setRating(stats.getAverage_rating());
                    totalReviewsTxt.setText("Total Reviewers (" + apiResponse.getStats().getTotal_reviews() + ")");

                    // Set the ratings percentages
//                    ratingReviews.createRatingBars(totalReviews, labels, colors, raters);



//                    Log.d("progressSOlving", "Creating Rating Bars "+ratingReviews.getChildCount());



                    // Calculate percentages for each star rating
                    float oneStarPercentage = (float) oneStarCount / totalReviews * 100;
                    float twoStarPercentage = (float) twoStarCount / totalReviews * 100;
                    float threeStarPercentage = (float) threeStarCount / totalReviews * 100;
                    float fourStarPercentage = (float) fourStarCount / totalReviews * 100;
                    float fiveStarPercentage = (float) fiveStarCount / totalReviews * 100;


// Set the progress for each star's ProgressBar
                    star1Progress.setProgress((int) oneStarPercentage);
                    star2Progress.setProgress((int) twoStarPercentage);
                    star3Progress.setProgress((int) threeStarPercentage);
                    star4Progress.setProgress((int) fourStarPercentage);
                    star5Progress.setProgress((int) fiveStarPercentage);



//                    ObjectAnimator progressAnimator = ObjectAnimator.ofInt(star1Progress, "progress", 100, 0);
//                    progressAnimator.setDuration(4000);
//                    progressAnimator.setInterpolator(new LinearInterpolator());
//                    progressAnimator.start();

                    animateProgressBar(star1Progress, (int) oneStarPercentage);
                    animateProgressBar(star2Progress, (int) twoStarPercentage);
                    animateProgressBar(star3Progress, (int) threeStarPercentage);
                    animateProgressBar(star4Progress, (int) fourStarPercentage);
                    animateProgressBar(star5Progress, (int) fiveStarPercentage);


                } else {
                    Log.d(TAG, "Error 3  ");


                }
            } else {

                // API call failed
                Log.d(TAG, "Error 4  ");


            }


        }
    }

    public class GetReviewsTask extends AsyncTask<Void, Void, AllReviewResponse> {

        private final int page;
        private final int itemsPerPage;

        int provider_id;

        public GetReviewsTask(int page, int itemsPerPage, int provider_id) {
            this.page = page;
            this.itemsPerPage = itemsPerPage;
            this.provider_id = provider_id;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);

        }

        @Override
        protected AllReviewResponse doInBackground(Void... voids) {
            try {
                // Create the Retrofit instance
                ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

                // Make the API call
                Call<AllReviewResponse> call = apiInterface.getAllReviewsOfProvider(
                        provider_id,
                        page, // page
                        itemsPerPage // itemsPerPage

                );


                Log.d("ksxln", "page : " + page);
                Log.d("ksxln", "item per page : " + itemsPerPage);
                Log.d("ksxln", "rService id : " + provider_id);

                Response<AllReviewResponse> response = call.execute();

                if (response.isSuccessful()) {
                    return response.body();
                } else {
                    // Handle error

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            swipeRefreshLayout.setRefreshing(false);
                            shimmerFrameLayout.setVisibility(View.GONE);
                            shimmerFrameLayout.stopShimmer();

                        }
                    });
                    Log.e("ksxln", "API call failed. Code: " + response.code());
                    return null;
                }
            } catch (IOException e) {

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        shimmerFrameLayout.setVisibility(View.GONE);
                        shimmerFrameLayout.stopShimmer();

                    }
                });
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(AllReviewResponse apiResponse) {
            super.onPostExecute(apiResponse);

            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            Log.d("RESJBSJBS", "1");



            swipeRefreshLayout.setRefreshing(false);
            shimmerFrameLayout.setVisibility(View.GONE);
            shimmerFrameLayout.stopShimmer();

            if (apiResponse != null) {
                // Handle the API response

                isLoading = false;


                if (currentPage == 1) {
                    // If it's the first page, clear the old list
                    reviews.clear();
                    adapter.notifyDataSetChanged();
                }


                List<AllReviews> newList = apiResponse.getReviews();



                if (newList != null && !newList.isEmpty()) {


                    Log.d("ksxln", "main list : " + newList.size());


                    txtNoData.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);

                    reviews.addAll(newList);
                    adapter.setData(reviews);
                    // Move notifyDataSetChanged outside the loop
                    adapter.notifyDataSetChanged();

                    if (newList.size() < itemsPerPage) {
                        isLastPage = true;
                    }



                }else if (currentPage > 1) {
                    // If no new data on a subsequent page, consider it the last page
                    isLastPage = true;


                    Log.d("RESJBSJBS", "2");


                }
                else {
                    Log.d("RESJBSJBS", "3");


                    txtNoData.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }


            }
            else {

                Log.d(TAG, "Error 5 ");

                txtNoData.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }


            // Notify the adapter outside the if condition
//                adapter.notifyDataSetChanged();
        }

    }

    private void initBundle(Activity activity) {

        SharedPreferences preferences = activity.getSharedPreferences("provider", Context.MODE_PRIVATE);

        if (preferences != null) {
            providerID = preferences.getString("id", "0");
        }

    }


    @Override
    public void onResume() {
        super.onResume();
        Log.d("progressSOlving", "onResume ");


//        ratingReviews.clearAll();
        try {
            new GetReviewDetails(providerID).execute();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("progressSOlving", "onpause");
    }

    @Override
    public void onStop() {
        super.onStop();

        Log.d("progressSOlving", "onstop");

    }
    private void animateProgressBar(ProgressBar progressBar, int progress) {


        ObjectAnimator progressAnimator = ObjectAnimator.ofInt(progressBar, "progress",0,progress);
                    progressAnimator.setDuration(600);
                    progressAnimator.setInterpolator(new LinearInterpolator());
                    progressAnimator.start();
    }


}