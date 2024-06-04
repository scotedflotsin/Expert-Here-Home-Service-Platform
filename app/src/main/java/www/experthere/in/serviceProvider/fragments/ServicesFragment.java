package www.experthere.in.serviceProvider.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import www.experthere.in.Common.GetShimmerStrings;
import www.experthere.in.R;
import www.experthere.in.adapters.ServiceAdapter;
import www.experthere.in.adapters.shimmer.MyShimmerAdapter;
import www.experthere.in.adapters.shimmer.NonScrollableLayoutManager;
import www.experthere.in.api.ApiClient;
import www.experthere.in.api.ApiInterface;
import www.experthere.in.helper.CustomToastNegative;
import www.experthere.in.model.ServiceResponse;
import www.experthere.in.model.Services;
import www.experthere.in.serviceProvider.NewServiceActivity;

public class ServicesFragment extends Fragment {

    private Activity activity;
    private RecyclerView recyclerView;
    private ServiceAdapter serviceAdapter;
    private List<Services> serviceList;
    private int currentPage = 1;
    private final int itemsPerPage = 10;
    private ProgressBar progressBar;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private String id;
    SwipeRefreshLayout swipeRefreshLayout;
    TextView noServiceTxt;
    SearchView searchView;
    private AlertDialog alertDialog;


    ShimmerFrameLayout shimmerFrameLayout;

    String globalSearchString = "";

    private final Handler handler = new Handler();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = requireActivity();
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
        View view = inflater.inflate(R.layout.fragment_servicies, container, false);

        swipeRefreshLayout = view.findViewById(R.id.swipeLayout);
        shimmerFrameLayout = view.findViewById(R.id.shimmerServices);
        inflateShimmerRecyclerView(view, GetShimmerStrings.PROVIDER_SERVICES);


        shimmerFrameLayout.startShimmer();



        SharedPreferences sharedPreferences = activity.getSharedPreferences("provider", Context.MODE_PRIVATE);
        id = sharedPreferences.getString("id", "none");

        view.findViewById(R.id.add_btn_newService).setOnClickListener(v -> {


            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                Dexter.withContext(activity).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE).withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {

                        if (multiplePermissionsReport.areAllPermissionsGranted()) {


                            Intent intent = new Intent(activity, NewServiceActivity.class);

                            SharedPreferences preferences = activity.getSharedPreferences("first_service",Context.MODE_PRIVATE);
                            SharedPreferences.Editor e = preferences.edit();
                            e.putBoolean("done",true);
                            e.apply();


                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);

                        }


                        if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()){

                            CustomToastNegative.create(activity,"Please Grant Storage Permission!");
                        }

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        CustomToastNegative.create(activity, "Permission Required!");

                        permissionToken.continuePermissionRequest();

                    }
                }
                ).check();

            } else {
                Dexter.withContext(activity).withPermissions(Manifest.permission.READ_MEDIA_IMAGES).withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {

                        if (multiplePermissionsReport.areAllPermissionsGranted()) {


                            Intent intent = new Intent(activity, NewServiceActivity.class);

                            SharedPreferences preferences = activity.getSharedPreferences("first_service",Context.MODE_PRIVATE);
                            SharedPreferences.Editor e = preferences.edit();
                            e.putBoolean("done",true);
                            e.apply();


                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);

                        }


                        if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()){

                            CustomToastNegative.create(activity,"Please Grant Storage Permission!");
                        }


                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {

                        permissionToken.continuePermissionRequest();
                    }
                }).check();

            }




        });

        noServiceTxt = view.findViewById(R.id.noServiceTxt);
        searchView = view.findViewById(R.id.searchView);

        recyclerView = view.findViewById(R.id.recyclerServices);
        progressBar = view.findViewById(R.id.progressBar);

        serviceList = new ArrayList<>();
        serviceAdapter = new ServiceAdapter(serviceList, requireActivity());
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(serviceAdapter);
        recyclerView.setHasFixedSize(true);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                        isLoading=true;
                        currentPage++;

                        progressBar.setVisibility(View.VISIBLE);

                        new FetchServicesTask(ServicesFragment.this, id, currentPage, itemsPerPage, globalSearchString,activity).execute();



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
                serviceList.clear();
                serviceAdapter.notifyDataSetChanged();


                shimmerFrameLayout.startShimmer();
                shimmerFrameLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);

                new FetchServicesTask(ServicesFragment.this, id, currentPage, itemsPerPage, globalSearchString,activity).execute();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                globalSearchString = newText;
                serviceAdapter.filter(newText);



                // Fetch data when search query is empty
                currentPage = 1;
                isLoading = false;
                isLastPage = false;
                serviceList.clear();
                serviceAdapter.notifyDataSetChanged();

                shimmerFrameLayout.startShimmer();
                shimmerFrameLayout.setVisibility(View.VISIBLE);
//                recyclerView.setVisibility(View.GONE);


                new FetchServicesTask(ServicesFragment.this, id, currentPage, itemsPerPage, globalSearchString,activity).execute();

                return false;
            }
        });



        try {
            new FetchServicesTask(ServicesFragment.this, id, currentPage, itemsPerPage, globalSearchString,activity).execute();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        return view;
    }

    private class FetchServicesTask extends AsyncTask<Void, Void, ServiceResponse> {
        private final WeakReference<ServicesFragment> fragmentRef;
        private final String id;
        private final int page;
        private final int itemsPerPage;
        private final String searchQuery;
        Activity activity;

        public FetchServicesTask(ServicesFragment fragment, String id, int page, int itemsPerPage, String searchQuery,Activity activity) {
            this.fragmentRef = new WeakReference<>(fragment);
            this.id = id;
            this.page = page;
            this.itemsPerPage = itemsPerPage;
            this.searchQuery = searchQuery;
            this.activity = activity;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected ServiceResponse doInBackground(Void... voids) {
            ServicesFragment fragment = fragmentRef.get();
            if (fragment == null || fragment.isDetached()) {
                return null;
            }

            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
            Call<ServiceResponse> call = apiInterface.getServices(id, page, itemsPerPage, searchQuery);

            try {
                Response<ServiceResponse> response = call.execute();
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        return response.body();
                    }
                }else {

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
                }
            } catch (IOException e) {
                e.printStackTrace();

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("TAGsss", "run: "+e.getMessage());
//                        Toast.makeText(fragment.activity, "error " + e.getMessage(), Toast.LENGTH_SHORT).show();

                        shimmerFrameLayout.setVisibility(View.GONE);
                        shimmerFrameLayout.stopShimmer();


                        if (swipeRefreshLayout!=null) {
                            swipeRefreshLayout.setRefreshing(false);
                        }

                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(ServiceResponse serviceResponse) {
            super.onPostExecute(serviceResponse);
            ServicesFragment fragment = fragmentRef.get();
            if (fragment != null) {
                fragment.handleServiceResponse(serviceResponse);
            }
        }
    }
    private void handleServiceResponse(ServiceResponse serviceResponse) {
        isLoading = false;
        progressBar.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);
        shimmerFrameLayout.setVisibility(View.GONE);
        shimmerFrameLayout.stopShimmer();


        if (serviceResponse != null && serviceResponse.isSuccess()) {
            List<Services> newServices = serviceResponse.getServices();

            if (newServices != null && !newServices.isEmpty()) {
                recyclerView.setVisibility(View.VISIBLE);
                noServiceTxt.setVisibility(View.GONE);



                if (currentPage == 1) {
                    serviceList.clear();
                    serviceAdapter.notifyDataSetChanged();

                }

                serviceList.addAll(newServices);
                serviceAdapter.setData(serviceList);
                serviceAdapter.notifyDataSetChanged();

                if (newServices.size() < itemsPerPage) {
                    isLastPage = true;
                }
            } else if (currentPage > 1) {
                // If no new data on a subsequent page, consider it the last page
                isLastPage = true;
//                showNoServiceMessage();
            } else {
                showNoServiceMessage();
            }
        } else {
            showNoServiceMessage();
//            Toast.makeText(activity, "Null response body or unsuccessful", Toast.LENGTH_SHORT).show();
        }

    }

    private void showNoServiceMessage() {
        noServiceTxt.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        shimmerFrameLayout.setVisibility(View.GONE);
        shimmerFrameLayout.stopShimmer();
    }



    @Override
    public void onResume() {
        super.onResume();
        currentPage = 1;
        isLoading = false;
        isLastPage = false;

        if (serviceList!=null) {

            serviceList.clear();

        }
        serviceAdapter.notifyDataSetChanged();

        new FetchServicesTask(this, id, currentPage, itemsPerPage, "",activity).execute();
        serviceAdapter.notifyDataSetChanged();
    }
}
