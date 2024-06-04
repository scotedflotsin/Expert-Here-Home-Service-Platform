package www.experthere.in.serviceProvider.profile_frgments;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import www.experthere.in.Common.GetShimmerStrings;
import www.experthere.in.R;
import www.experthere.in.adapters.serviceListUser.ServiceProviderAdapter;
import www.experthere.in.adapters.shimmer.MyShimmerAdapter;
import www.experthere.in.adapters.shimmer.NonScrollableLayoutManager;
import www.experthere.in.api.ApiClient;
import www.experthere.in.api.ApiInterface;
import www.experthere.in.model.Services;
import www.experthere.in.model.service_user_cat.ApiResponse;
import www.experthere.in.model.service_user_cat.Service;
import www.experthere.in.users.ServicesByCategoryActivity;

public class ServiciesProviderFragment extends Fragment {


    Activity activity;
    String providerID;

    RecyclerView recyclerView;
    ProgressBar progressService;
    List<Service> services = new ArrayList<>();
    ServiceProviderAdapter adapter;
    private int currentPage = 1;
    private final int itemsPerPage = 10;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private final String TAG = "SERVICES";

    TextView noDataText;

    ShimmerFrameLayout shimmerFrameLayout;
    private void inflateShimmerRecyclerView(View view ,String type) {


        RecyclerView shimmerRecycler = view.findViewById(R.id.recyclerShimmer);

        MyShimmerAdapter shimmerAdapter = new MyShimmerAdapter(type);
        NonScrollableLayoutManager layoutManager = new NonScrollableLayoutManager(activity);
        shimmerRecycler.setLayoutManager(layoutManager);
        shimmerRecycler.setAdapter(shimmerAdapter);


    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = requireActivity();
        initBundle(activity);

    }

    private void initBundle(Activity activity) {

        Bundle bundle = activity.getIntent().getExtras();

        if (bundle != null) {
            providerID = bundle.getString("provider_id", "0");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View view = inflater.inflate(R.layout.fragment_servicies_provider, container, false);


        shimmerFrameLayout = view.findViewById(R.id.shimmer_service);
        inflateShimmerRecyclerView(view, GetShimmerStrings.USER_SERVICE);
        shimmerFrameLayout.startShimmer();


        recyclerView = view.findViewById(R.id.recycler_Service);
        progressService = view.findViewById(R.id.progressService);
        noDataText = view.findViewById(R.id.noDataText);
        adapter = new ServiceProviderAdapter(services, activity, getParentFragmentManager());

        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);



        try {


            new ApiServiceTask(providerID, currentPage, itemsPerPage).execute();

        } catch (Exception e) {
            Log.d(TAG, "Error 1  "+e.getMessage());

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
                            && totalItemCount >= itemsPerPage) {
                        // Load more data when reaching the end

                        isLoading = true;
                        currentPage++;
                        progressService.setVisibility(View.VISIBLE);

                        try {



                            new ApiServiceTask(providerID, currentPage, itemsPerPage).execute();

                        } catch (Exception e) {
                            Log.d(TAG, "Error 2  "+e.getMessage());

                        }
                    }
                }
            }
        });


        return view;


    }


    public class ApiServiceTask extends AsyncTask<Void, Void, ApiResponse> {


        String providerId;
        private final int page;
        private final int itemsPerPage;

        public ApiServiceTask(String providerId, int page, int itemsPerPage) {
            this.providerId = providerId;
            this.page = page;
            this.itemsPerPage = itemsPerPage;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected ApiResponse doInBackground(Void... voids) {
            try {
                // Create the Retrofit instance
                ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

                // Make the API call
                Call<ApiResponse> call = apiInterface.getallserviciesforprovider(
                        providerId,
                        page, // page
                        itemsPerPage // itemsPerPage
                );


                Log.d("DATASENDTTOSERVER", "Pro id : " + providerId);

                Log.d("DATASENDTTOSERVER", "page : " + page);
                Log.d("DATASENDTTOSERVER", "item per page : " + itemsPerPage);

                Response<ApiResponse> response = call.execute();

                if (response.isSuccessful()) {
                    return response.body();
                } else {
                    // Handle error
                    Log.e("ApiServiceTask", "API call failed. Code: " + response.code());
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ApiResponse apiResponse) {
            super.onPostExecute(apiResponse);

            progressService.setVisibility(View.GONE);

            shimmerFrameLayout.setVisibility(View.GONE);
            shimmerFrameLayout.stopShimmer();


            if (apiResponse != null) {
                // Handle the API response

                isLoading = false;

                List<Service> newServices = apiResponse.getServices();



                if (newServices != null && !newServices.isEmpty()) {




                    if (currentPage == 1) {
                        // If it's the first page, clear the old list
                        services.clear();
                        adapter.notifyDataSetChanged();
                    }

                    services.addAll(newServices);
                    adapter.setData(services);
                    // Move notifyDataSetChanged outside the loop
                    adapter.notifyDataSetChanged();

                    recyclerView.setVisibility(View.VISIBLE);

                    if (newServices.size() < itemsPerPage) {
                        isLastPage = true;
                    }
                } else if (currentPage > 1) {
                    // If no new data on a subsequent page, consider it the last page
                    isLastPage = true;
                }else {

                    noDataText.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    progressService.setVisibility(View.GONE);

                }

                // Notify the adapter outside the if condition
//                adapter.notifyDataSetChanged();
            } else {
                // Handle error
            }
        }

    }


}