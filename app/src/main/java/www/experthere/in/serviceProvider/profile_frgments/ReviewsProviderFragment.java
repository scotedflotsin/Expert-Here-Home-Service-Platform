package www.experthere.in.serviceProvider.profile_frgments;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.facebook.shimmer.ShimmerFrameLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Response;
import www.experthere.in.Common.GetShimmerStrings;
import www.experthere.in.R;
import www.experthere.in.adapters.AllReviewsAdapter;
import www.experthere.in.adapters.shimmer.MyShimmerAdapter;
import www.experthere.in.adapters.shimmer.NonScrollableLayoutManager;
import www.experthere.in.api.ApiClient;
import www.experthere.in.api.ApiInterface;
import www.experthere.in.helper.ServiceDetailsBottomSheet;
import www.experthere.in.model.AllReviewResponse;
import www.experthere.in.model.AllReviews;
import www.experthere.in.model.AllReviewsStats;
import www.experthere.in.model.ReviewResponse;
import www.experthere.in.model.Reviews;
import www.experthere.in.model.Stats;

public class ReviewsProviderFragment extends Fragment {
    private static final String TAG = "REVIEWS_FRAG_PRO";
    String providerID;

    Activity activity;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    List<AllReviews> reviews = new ArrayList<>() ;

    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int currentPage = 1;
    private final int itemsPerPage = 10;

    AllReviewsAdapter adapter;

    Stats stats;

//    RatingReviews ratingReviews;

    TextView avgRatingTxt,totalReviewsTxt;
    AppCompatRatingBar ratingBarAvg;
    ProgressBar star1Progress,star2Progress,star3Progress,star4Progress,star5Progress;


    ShimmerFrameLayout shimmerFrameLayout;
    TextView noDataReviews;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity =requireActivity();
        initBundle(activity);

    }

    private void inflateShimmerRecyclerView(View view ,String type) {


        RecyclerView shimmerRecycler = view.findViewById(R.id.recycler_shimmer);

        MyShimmerAdapter shimmerAdapter = new MyShimmerAdapter(type);
        NonScrollableLayoutManager layoutManager = new NonScrollableLayoutManager(activity);
        shimmerRecycler.setLayoutManager(layoutManager);
        shimmerRecycler.setAdapter(shimmerAdapter);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view =  inflater.inflate(R.layout.fragment_reviews_provider, container, false);


//         ratingReviews = view.findViewById(R.id.rating_reviews);

        star1Progress = view.findViewById(R.id.star1Progress);
        star2Progress = view.findViewById(R.id.star2Progress);
        star3Progress = view.findViewById(R.id.star3Progress);
        star4Progress = view.findViewById(R.id.star4Progress);
        star5Progress = view.findViewById(R.id.star5Progress);


        avgRatingTxt = view.findViewById(R.id.avgRatingTxt);
        totalReviewsTxt = view.findViewById(R.id.totalReviewsTxt);
        ratingBarAvg = view.findViewById(R.id.ratingBarNew);



        noDataReviews = view.findViewById(R.id.noDataReviews);
        shimmerFrameLayout = view.findViewById(R.id.shimmer_review);

        inflateShimmerRecyclerView(view, GetShimmerStrings.PROVIDER_REVIEWS);

        shimmerFrameLayout.startShimmer();


        recyclerView = view.findViewById(R.id.recycler_review);
        progressBar = view.findViewById(R.id.progressService);

        adapter = new AllReviewsAdapter(reviews,activity);

        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setHasFixedSize(true);
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

                            new GetReviewsTask(currentPage, itemsPerPage,Integer.parseInt(providerID)).execute();

                        } catch (Exception e) {
                            Log.d(TAG, "Error 1  "+e.getMessage());

                        }
                    }
                }
            }
        });


        try {

            Log.d("HGSHG", "HIt ");

            new GetReviewsTask(currentPage, itemsPerPage,Integer.parseInt(providerID)).execute();
            new GetReviewDetails(providerID).execute();


        } catch (Exception e) {

            Log.d(TAG, "Error 2 "+e.getMessage());

        }




        return view;

    }

    private class GetReviewDetails extends AsyncTask<Void, Void, AllReviewsStats> {


        private final String providerID;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();



        }

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
                    return null;
                }
            } catch (IOException e) {
                Log.e("SubmitReviewTask", "Error making API call", e);
                return null;
            }

        }

        @Override
        protected void onPostExecute(AllReviewsStats apiResponse) {
            // The onPostExecute method remains the same as in the previous example



            if (apiResponse != null && apiResponse.isSuccess()) {
                // API call successful

                stats = apiResponse.getStats();

                if (stats!=null) {

//                    String progress = apiResponse.getStats().getAverage_rating();

//                    ratingBar.setProgress(Integer.parseInt(progress));
//                    avgRatingsTxt.setText(String.valueOf(progress));
//                    reviewersTxt.setText("("+apiResponse.getStats().getTotal_reviews()+") Reviewers");


                    Log.d("STATSGDGS", "onPostExecute: "+stats.getAverage_rating());
                    Log.d("STATSGDGS", "onPostExecute: "+stats.getTotal_reviews());
                    Log.d("STATSGDGS", "onPostExecute: "+stats.getOne_star());
                    Log.d("STATSGDGS", "onPostExecute: "+stats.getTwo_star());
                    Log.d("STATSGDGS", "onPostExecute: "+stats.getThree_star());
                    Log.d("STATSGDGS", "onPostExecute: "+stats.getFour_star());
                    Log.d("STATSGDGS", "onPostExecute: "+stats.getFive_star());
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

//                    String labels[] = new String[]{
//                    "1 ","2 ","3 ","4 ","5 ",} ;
//
//
//                    int raters[] = new int[]{ oneStarCount,twoStarCount,threeStarCount,fourStarCount,fiveStarCount
//                    };


                    // Set the ratings percentages
//                    ratingReviews.createRatingBars(totalReviews,labels,colors,raters
//                    );

//                    ratingReviews.setLength(3.0);


                    String formattedValue = String.format("%.1f", stats.getAverage_rating());


                    avgRatingTxt.setText(formattedValue);
                    ratingBarAvg.setRating(stats.getAverage_rating());
                    totalReviewsTxt.setText("Total Reviewers ("+apiResponse.getStats().getTotal_reviews()+")");



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
            noDataReviews.setVisibility(View.GONE);


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
                    Log.e("ksxln", "API call failed. Code: " + response.code());
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(AllReviewResponse apiResponse) {
            super.onPostExecute(apiResponse);

            progressBar.setVisibility(View.GONE);
            shimmerFrameLayout.setVisibility(View.GONE);
            shimmerFrameLayout.stopShimmer();
            recyclerView.setVisibility(View.VISIBLE);

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
                    Log.d("ksxln", "main list : " + newList.get(0).getReview());


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
                }else {


                    noDataReviews.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                }


            }


            // Notify the adapter outside the if condition
//                adapter.notifyDataSetChanged();
        }

    }
    private void initBundle(Activity activity) {

        Bundle bundle = activity.getIntent().getExtras();

        if (bundle != null) {
            providerID = bundle.getString("provider_id", "0");
        }

    }



    private void animateProgressBar(ProgressBar progressBar, int progress) {


        ObjectAnimator progressAnimator = ObjectAnimator.ofInt(progressBar, "progress",0,progress);
        progressAnimator.setDuration(600);
        progressAnimator.setInterpolator(new LinearInterpolator());
        progressAnimator.start();
    }


}