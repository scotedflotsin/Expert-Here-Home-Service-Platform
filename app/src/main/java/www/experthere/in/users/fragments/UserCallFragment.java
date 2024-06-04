package www.experthere.in.users.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;

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
import www.experthere.in.adapters.UserCallHistoryAdapter;
import www.experthere.in.adapters.shimmer.MyShimmerAdapter;
import www.experthere.in.adapters.shimmer.NonScrollableLayoutManager;
import www.experthere.in.api.ApiClient;
import www.experthere.in.api.ApiInterface;
import www.experthere.in.model.CallHistoryModel;
import www.experthere.in.model.CallHistoryResponse;
import www.experthere.in.serviceProvider.fragments.IncomingFragment;

public class UserCallFragment extends Fragment {

    Activity activity;

    List<CallHistoryModel> reviews;
    UserCallHistoryAdapter adapter;

    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int currentPage = 1;
    private final int itemsPerPage = 10;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    SearchView searchView;
    LinearLayout txtNoData;
    SwipeRefreshLayout swipeRefreshLayout;

    SharedPreferences preferences;


    ShimmerFrameLayout shimmerFrameLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = requireActivity();

        reviews = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_call, container, false);



        preferences = activity.getSharedPreferences("user", MODE_PRIVATE);

        swipeRefreshLayout = view.findViewById(R.id.swipeLayoutBookmark);
        searchView = view.findViewById(R.id.searchView);
        txtNoData = view.findViewById(R.id.txtNoData);
        recyclerView = view.findViewById(R.id.recycler_call);
        progressBar = view.findViewById(R.id.progressBar);
        shimmerFrameLayout = view.findViewById(R.id.shimmer_call);

        inflateShimmerRecyclerView(view, GetShimmerStrings.USER_CALL);
        shimmerFrameLayout.startShimmer();


        adapter = new UserCallHistoryAdapter(reviews,activity);

        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);


        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);






        try {

            new GetCallHistory(currentPage,itemsPerPage,preferences.getString("id","0"),"").execute();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }




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
                            && totalItemCount >= itemsPerPage) { // Ensure that scrolling is in the positive direction
                        // Load more data when reaching the end
                        isLoading = true;
                        currentPage++;
                        progressBar.setVisibility(View.VISIBLE);

                        try {
                            new GetCallHistory(currentPage, itemsPerPage, preferences.getString("id","0"), "").execute();
                        } catch (Exception e) {

                            Log.d("qqndbqkkd", "SERVER ERROR 2 "+e.getMessage());

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

                shimmerFrameLayout.startShimmer();
                shimmerFrameLayout.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);

                new GetCallHistory(currentPage, itemsPerPage, preferences.getString("id","0"), "").execute();
            }
        });


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);

                // Fetch data when search query is empty
                currentPage = 1;
                isLoading = false;
                isLastPage = false;
                reviews.clear();
                adapter.notifyDataSetChanged();

                shimmerFrameLayout.startShimmer();
                shimmerFrameLayout.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                new GetCallHistory(currentPage, itemsPerPage, preferences.getString("id","0"), newText).execute();

                return false;
            }
        });





        showNativeAds(view);





        return view;
    }

    private void showNativeAds(View view) {


        SharedPreferences  adsPref = activity.getSharedPreferences("ads", MODE_PRIVATE);
        String nativeId = adsPref.getString("native", "ca-app-pub-3940256099942544/2247696110");

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

    private void inflateShimmerRecyclerView(View view, String types) {


        RecyclerView shimmerRecycler = view.findViewById(R.id.recyclerShimmer);

        MyShimmerAdapter shimmerAdapter = new MyShimmerAdapter(types);
        NonScrollableLayoutManager layoutManager = new NonScrollableLayoutManager(activity);
        shimmerRecycler.setLayoutManager(layoutManager);
        shimmerRecycler.setAdapter(shimmerAdapter);


    }


    public class GetCallHistory extends AsyncTask<Void, Void, CallHistoryResponse> {

        private final int page;
        private final int itemsPerPage;

        String user_id;
        String query;


        public GetCallHistory(int page, int itemsPerPage, String user_id, String query) {
            this.page = page;
            this.itemsPerPage = itemsPerPage;
            this.user_id = user_id;
            this.query = query;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected CallHistoryResponse doInBackground(Void... voids) {
            try {
                // Create the Retrofit instance
                ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

                // Make the API call
                Call<CallHistoryResponse> call = apiInterface.getCallHistoryUser(
                        user_id,
                        page, // page
                        itemsPerPage // itemsPerPage
                        , query

                );



                Response<CallHistoryResponse> response = call.execute();

                if (response.isSuccessful()) {
                    return response.body();
                } else {
                    // Handle error
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {

                            shimmerFrameLayout.setVisibility(View.GONE);
                            shimmerFrameLayout.stopShimmer();
//
//
//                            if (swipeRefreshLayout!=null) {
//                                swipeRefreshLayout.setRefreshing(false);
//                            }

                        }
                    });
                    Log.e("ksxln", "API call failed. Code: " + response.code());
                    return null;
                }
            } catch (IOException e) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {

                        shimmerFrameLayout.setVisibility(View.GONE);
                        shimmerFrameLayout.stopShimmer();
//
//
//                        if (swipeRefreshLayout!=null) {
//                            swipeRefreshLayout.setRefreshing(false);
//                        }

                    }
                });

                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(CallHistoryResponse apiResponse) {
            super.onPostExecute(apiResponse);


            if (swipeRefreshLayout!=null) {
                swipeRefreshLayout.setRefreshing(false);
            }

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

                List<CallHistoryModel> newList = apiResponse.getCall_history();

                if (newList != null && !newList.isEmpty()) {
                    // Add the incomingList to the reviews list after the loop

                    recyclerView.setVisibility(View.VISIBLE);
                    txtNoData.setVisibility(View.GONE);

                    reviews.addAll(newList);
                    adapter.setData(reviews);
                    adapter.notifyDataSetChanged();

                    if (newList.size() < itemsPerPage) {
                        isLastPage = true;
                    }
                } else if (currentPage > 1) {
                    // If no new data on a subsequent page, consider it the last page
                    isLastPage = true;
                } else {
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                    txtNoData.setVisibility(View.VISIBLE);
                }
            } else {

                Log.d("bkjb", "SERVER ERROR 3 ");

            }
        }

    }

}