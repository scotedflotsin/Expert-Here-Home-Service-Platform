package www.experthere.in.helper.app;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import www.experthere.in.R;
import www.experthere.in.api.ApiClient;
import www.experthere.in.api.ApiInterface;
import www.experthere.in.helper.CustomToastNegative;
import www.experthere.in.model.service_user_cat.ApiResponse;
import www.experthere.in.model.service_user_cat.Service;
import www.experthere.in.serviceProvider.profile_frgments.ServiciesProviderFragment;

public class FullScreenListDialog extends Dialog {

    private final List<Service> itemList;

    private final getService getService;

    SelectServiceAdapter adapter;
    String providerID;

    private int currentPage = 1;
    private final int itemsPerPage = 10;
    private boolean isLoading = false;
    private boolean isLastPage = false;


    RecyclerView recyclerView;
    ProgressBar progressService;
    Activity activity;

    public interface getService {

        void getService(Service service);

    }


    public FullScreenListDialog(Activity activity, getService getService) {
        super(activity);
        this.activity = activity;
        this.getService = getService;

        itemList = new ArrayList<>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.dialog_fullscreen_list);

        initData(activity);


        recyclerView = findViewById(R.id.recyclerView);
        progressService = findViewById(R.id.progressBar);


        adapter = new SelectServiceAdapter(itemList, activity, new SelectServiceAdapter.GetService() {
            @Override
            public void getService(Service service) {


                getService.getService(service);
                dismiss();


            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setAdapter(adapter);


        try {


            new ApiServiceTask(providerID, currentPage, itemsPerPage).execute();

        } catch (Exception e) {

            CustomToastNegative.create(activity,"Error: "+e.getMessage());
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

                            CustomToastNegative.create(activity,"Error: "+e.getMessage());
                        }
                    }
                }
            }
        });


    }


    private void initData(Activity activity) {

        SharedPreferences preferences = activity.getSharedPreferences("provider", Context.MODE_PRIVATE);

        if (preferences != null) {
            providerID = preferences.getString("id", "0");
        }

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
            recyclerView.setVisibility(View.VISIBLE);

            if (apiResponse != null) {
                // Handle the API response

                isLoading = false;

                List<Service> newServices = apiResponse.getServices();

                Log.d("hcjhjhcvdj", "main list : " + newServices.size());


                if (newServices != null && !newServices.isEmpty()) {


                    List<Service> enabledServices = new ArrayList<>();

                    for (Service service : newServices) {
                        if (service.getServiceStatus().contains("Enable")) {
                            enabledServices.add(service);


                            Log.d("hcjhjhcvdj", "onPostExecute: " + service.getServiceTitle());

                        }
                    }

                    if (currentPage == 1) {
                        // If it's the first page, clear the old list
                        itemList.clear();
                        adapter.notifyDataSetChanged();
                    }

                    itemList.addAll(enabledServices);
                    adapter.setData(itemList);
                    // Move notifyDataSetChanged outside the loop
                    adapter.notifyDataSetChanged();

                    if (enabledServices.size() < itemsPerPage) {
                        isLastPage = true;
                    }
                } else if (currentPage > 1) {
                    // If no new data on a subsequent page, consider it the last page
                    isLastPage = true;
                }

                // Notify the adapter outside the if condition
//                adapter.notifyDataSetChanged();
            } else {
                // Handle error
                Toast.makeText(activity, "Error 1", Toast.LENGTH_SHORT).show();
            }
        }

    }

}
