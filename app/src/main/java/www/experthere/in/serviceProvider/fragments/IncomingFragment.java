package www.experthere.in.serviceProvider.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import www.experthere.in.Common.GetShimmerStrings;
import www.experthere.in.R;
import www.experthere.in.adapters.ProviderCallHistoryAdapter;
import www.experthere.in.adapters.shimmer.MyShimmerAdapter;
import www.experthere.in.adapters.shimmer.NonScrollableLayoutManager;
import www.experthere.in.api.ApiClient;
import www.experthere.in.api.ApiInterface;
import www.experthere.in.model.CallHistoryModel;
import www.experthere.in.model.CallHistoryResponse;


public class IncomingFragment extends Fragment {
    private static final String TAG = "INCOMING_FRAGMENT";
    List<CallHistoryModel> reviews;
    String providerID;

    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int currentPage = 1;
    private final int itemsPerPage = 10;
    RecyclerView recyclerView;
    ProgressBar progressBar;

    Activity activity;
    LinearLayout txtNoData;
    SearchView searchView;
    ProviderCallHistoryAdapter adapter;
    SwipeRefreshLayout swipeRefreshLayout;

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
        View view = inflater.inflate(R.layout.fragment_incoming, container, false);
        swipeRefreshLayout = view.findViewById(R.id.swipeLayout);
        shimmerFrameLayout = view.findViewById(R.id.shimmer_call);

        inflateShimmerRecyclerView(view, GetShimmerStrings.PROVIDER_CALL);

        shimmerFrameLayout.startShimmer();



        SharedPreferences preferences = activity.getSharedPreferences("provider", Context.MODE_PRIVATE);

        if (preferences != null) {
            providerID = preferences.getString("id", "0");
        }

        searchView = view.findViewById(R.id.searchView);

        txtNoData = view.findViewById(R.id.txtNoData);

        recyclerView = view.findViewById(R.id.recycler_call);
        adapter = new ProviderCallHistoryAdapter(reviews, activity,false);
        progressBar = view.findViewById(R.id.progressBar);

        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);


        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);


        try {

            new GetCallHistory(currentPage, itemsPerPage, Integer.parseInt(providerID), "").execute();

        } catch (Exception e) {

            Log.d(TAG, "SERVER ERROR 1 "+e.getMessage());

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
                            && totalItemCount >= itemsPerPage
                            ) { // Ensure that scrolling is in the positive direction
                        // Load more data when reaching the end
                        isLoading = true;
                        currentPage++;
                        progressBar.setVisibility(View.VISIBLE);

                        try {
                            new GetCallHistory(currentPage, itemsPerPage, Integer.parseInt(providerID), "").execute();
                        } catch (Exception e) {

                            Log.d(TAG, "SERVER ERROR 2 "+e.getMessage());

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

                new GetCallHistory(currentPage, itemsPerPage, Integer.parseInt(providerID), "").execute();
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
                new GetCallHistory(currentPage, itemsPerPage, Integer.parseInt(providerID), newText).execute();

                return false;
            }
        });


        return view;

    }

    private void inflateShimmerRecyclerView(View view ,String type) {


        RecyclerView shimmerRecycler = view.findViewById(R.id.recyclerShimmer);

        MyShimmerAdapter shimmerAdapter = new MyShimmerAdapter(type);
        NonScrollableLayoutManager layoutManager = new NonScrollableLayoutManager(activity);
        shimmerRecycler.setLayoutManager(layoutManager);
        shimmerRecycler.setAdapter(shimmerAdapter);


    }

    public class GetCallHistory extends AsyncTask<Void, Void, CallHistoryResponse> {

        private final int page;
        private final int itemsPerPage;

        int provider_id;
        String query;

        public GetCallHistory(int page, int itemsPerPage, int provider_id, String query) {
            this.page = page;
            this.itemsPerPage = itemsPerPage;
            this.provider_id = provider_id;
            this.query = query;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);

        }

        @Override
        protected CallHistoryResponse doInBackground(Void... voids) {
            try {
                // Create the Retrofit instance
                ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

                // Make the API call
                Call<CallHistoryResponse> call = apiInterface.getCallHistoryProviderIncoming(
                        String.valueOf(provider_id),
                        page, // page
                        itemsPerPage // itemsPerPage
                        , query

                );


                Log.d("ksxln", "page : " + page);
                Log.d("ksxln", "item per page : " + itemsPerPage);
                Log.d("ksxln", "rService id : " + provider_id);

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


                            if (swipeRefreshLayout!=null) {
                                swipeRefreshLayout.setRefreshing(false);
                            }

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


                        if (swipeRefreshLayout!=null) {
                            swipeRefreshLayout.setRefreshing(false);
                        }

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

                Log.d(TAG, "SERVER ERROR 3 ");

            }
        }

    }


}