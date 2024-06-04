package www.experthere.in.users;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
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
import www.experthere.in.LocationSelectActivity;
import www.experthere.in.R;
import www.experthere.in.adapters.serviceListUser.ServiceByCatUserAdapter;
import www.experthere.in.adapters.shimmer.MyShimmerAdapter;
import www.experthere.in.adapters.shimmer.NonScrollableLayoutManager;
import www.experthere.in.api.ApiClient;
import www.experthere.in.api.ApiInterface;
import www.experthere.in.helper.CustomToastPositive;
import www.experthere.in.helper.RangeSekectorBottomSheet;
import www.experthere.in.helper.TextMaskUtil;
import www.experthere.in.model.service_user_cat.ApiResponse;
import www.experthere.in.model.service_user_cat.Service;

public class ServiceForCategoryActivity extends AppCompatActivity {


    TextView locationTxtView;
    RecyclerView recyclerByCat;
    ProgressBar progressBar;
    LinearLayout selectLocationBtn;
    String lat, longitude, address, city;

    SharedPreferences preferences;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int currentPage = 1;
    private final int itemsPerPage = 10;
    String catName, catId;
    ServiceByCatUserAdapter adapter;

    List<Service> services = new ArrayList<>();
    int radius = 50;

    TextView kmTxt, titleCatUser;

    SharedPreferences preferencesSettings;
    SharedPreferences.Editor editor;


    ShimmerFrameLayout shimmerFrameLayout;


    SwipeRefreshLayout swipeRefreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.white, this.getTheme()));
        setContentView(R.layout.activity_service_by_category);
        initViews();
        initBundle();
        initPrefrence();
        preferences = getSharedPreferences("user", MODE_PRIVATE);
        setUserLoginDefaultLocation();




        inflateShimmerRecyclerView( GetShimmerStrings.USER_SERVICE);
        shimmerFrameLayout.startShimmer();



        preferencesSettings = getSharedPreferences("settings", MODE_PRIVATE);
        kmTxt.setText(preferencesSettings.getInt("range", 5) + " Km");


        findViewById(R.id.clickRangeBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                RangeSekectorBottomSheet bottomSheet = new RangeSekectorBottomSheet(ServiceForCategoryActivity.this, new RangeSekectorBottomSheet.KmListener() {
                    @Override
                    public void SelectedKmValue(String km) {


                        CustomToastPositive.create(getApplicationContext(),"Applied Successfully!");
                        editor.putInt("range", Integer.parseInt(km));
                        editor.apply();

                        kmTxt.setText(km + " Km");



                        try {


                            currentPage = 1;
                            isLoading = false;
                            isLastPage = false;
                            services.clear();
                            adapter.notifyDataSetChanged();


                            new HomeServiceTask(Double.parseDouble(lat), Double.parseDouble(longitude), currentPage, itemsPerPage, Integer.parseInt(km), catName).execute();


                        } catch (Exception e) {

                        }


                    }
                });
                bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());


            }
        });


        selectLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(ServiceForCategoryActivity.this, LocationSelectActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString("key", "RegisterProvider");
                intent.putExtras(bundle);
                startActivityForResult(intent, 882);


            }
        });


        findViewById(R.id.backEdit).setOnClickListener(view -> finish());


        adapter = new ServiceByCatUserAdapter(services, this, getSupportFragmentManager());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerByCat.setLayoutManager(layoutManager);
        recyclerByCat.setHasFixedSize(true);
        recyclerByCat.setAdapter(adapter);

        try {


            Log.d("lnsdccsdcnksn", "range: " + radius);
            Log.d("lnsdccsdcnksn", "lat: " + lat);
            Log.d("lnsdccsdcnksn", "long: " + longitude);
            Log.d("lnsdccsdcnksn", "page: " + currentPage);
            Log.d("lnsdccsdcnksn", "item page: " + itemsPerPage);


            new HomeServiceTask(Double.parseDouble(lat), Double.parseDouble(longitude), currentPage, itemsPerPage, radius, catName).execute();

        } catch (Exception e) {

        }

        recyclerByCat.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

                        new HomeServiceTask(Double.parseDouble(lat), Double.parseDouble(longitude), currentPage, itemsPerPage, radius, catName).execute();

                    }
                }
            }
        });


//        recyclerByCat.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//
//                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
//                int visibleItemCount = layoutManager.getChildCount();
//                int totalItemCount = layoutManager.getItemCount();
//                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
//
//                if (!isLoading && !isLastPage) {
//                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
//                            && firstVisibleItemPosition >= 0
//                            && totalItemCount >= itemsPerPage) {
//                        // Load more data when reaching the end
//
//                        isLoading = true;
//                        currentPage++;
//                        progressBar.setVisibility(View.VISIBLE);
//
//                        try {
//
//
//                            new HomeServiceTask(Double.parseDouble(lat), Double.parseDouble(longitude), currentPage, itemsPerPage, radius, catName).execute();
//
//
//                        } catch (Exception e) {
//
//                        }
//                    }
//                }
//            }
//        });


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPage = 1;
                isLoading = false;
                isLastPage = false;
                services.clear();
                adapter.notifyDataSetChanged();

                shimmerFrameLayout.startShimmer();
                shimmerFrameLayout.setVisibility(View.VISIBLE);
                recyclerByCat.setVisibility(View.GONE);


                initPrefrence();

                new HomeServiceTask(Double.parseDouble(lat), Double.parseDouble(longitude), currentPage, itemsPerPage, radius, catName).execute();


                int rg = preferencesSettings.getInt("range",5);

                kmTxt.setText(rg+" Km");

            }
        });


    }

    private void inflateShimmerRecyclerView(String types) {
        RecyclerView shimmerRecycler = findViewById(R.id.recyclerShimmer);

        MyShimmerAdapter shimmerAdapter = new MyShimmerAdapter(types);
        NonScrollableLayoutManager layoutManager = new NonScrollableLayoutManager(ServiceForCategoryActivity.this);
        shimmerRecycler.setLayoutManager(layoutManager);
        shimmerRecycler.setAdapter(shimmerAdapter);
    }


    private void initPrefrence() {

        SharedPreferences settingsPref = getSharedPreferences("settings", MODE_PRIVATE);

        editor = settingsPref.edit();

        if (!settingsPref.getString("latitude", "0").equals("0")) {
            lat = settingsPref.getString("latitude", "0");
        }

        if (!settingsPref.getString("longitude", "0").equals("0")) {
            longitude = settingsPref.getString("longitude", "0");
        }

        if (!settingsPref.getString("address", "0").equals("0")) {

            address = settingsPref.getString("address", "0");

            TextMaskUtil.maskText(locationTxtView, address, 30, "..");


        }


        radius = settingsPref.getInt("range", 5);

        kmTxt.setText(radius + " Km");


        city = " ";




    }

    private void initBundle() {

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            catName = bundle.getString("cat_name", "0");
            catId = bundle.getString("cat_id", "0");

            titleCatUser = findViewById(R.id.titleCatUser);
            titleCatUser.setText(catName);


        }
    }

    private void setUserLoginDefaultLocation() {

        if (lat.equals("0") && longitude.equals("0") && address.equals("0")) {


            String defAddress = preferences.getString("address", "Select Location");
            locationTxtView.setText(defAddress);

            lat = preferences.getString("latitude", "0");
            longitude = preferences.getString("longitude", "0");
        }

    }

    private void initViews() {
        locationTxtView = findViewById(R.id.locationTxtCat);
        selectLocationBtn = findViewById(R.id.locationLay);
        recyclerByCat = findViewById(R.id.recyclerByCat);
        progressBar = findViewById(R.id.progressBar);
        kmTxt = findViewById(R.id.kmTxt);
        shimmerFrameLayout = findViewById(R.id.shimmer);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 882 && resultCode == RESULT_OK) {

            if (data != null) {

                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    lat = bundle.getString("lat");
                    longitude = bundle.getString("long");
                    address = bundle.getString("address");
                    city = bundle.getString("city");


                    SharedPreferences preferences = getSharedPreferences("settings", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();


                    editor.putString("address", bundle.getString("address"));
                    editor.putString("latitude", bundle.getString("lat"));
                    editor.putString("longitude", bundle.getString("long"));
                    editor.apply();


                }

                TextMaskUtil.maskText(locationTxtView, address, 30, "..");


                Log.d("LOCATION", "lat - " + lat);
                Log.d("LOCATION", "LONG - " + longitude);
                Log.d("LOCATION", "ADDRESS - " + address);
                Log.d("LOCATION", "CITY - " + city);


                isLoading = false;
                isLastPage = false;
                currentPage = 1;

                shimmerFrameLayout.setVisibility(View.VISIBLE);
                shimmerFrameLayout.startShimmer();



                // Clear old data and notify the adapter
                services.clear();
//                adapter.notifyDataSetChanged();

                Log.d("LOCATION", "CITY - " + services.size());


                new HomeServiceTask(Double.parseDouble(lat), Double.parseDouble(longitude), currentPage, itemsPerPage, radius, catName).execute();

            }


        }


    }


    public class HomeServiceTask extends AsyncTask<Void, Void, ApiResponse> {

        private final double currentLatitude;
        private final double currentLongitude;

        private final int page;
        private final int itemsPerPage;
        private final int radius;
        private final String categoryName;

        public HomeServiceTask(double currentLatitude, double currentLongitude, int page, int itemsPerPage, int radius, String categoryName) {
            this.currentLatitude = currentLatitude;
            this.currentLongitude = currentLongitude;
            this.page = page;
            this.itemsPerPage = itemsPerPage;
            this.radius = radius;
            this.categoryName = categoryName;
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
                Call<ApiResponse> call = apiInterface.getServiceForSpecificCategoryUserHome(

                        currentLatitude, currentLongitude, page, // page
                        itemsPerPage // itemsPerPage
                        , radius, categoryName);


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

            isLoading = false;


            progressBar.setVisibility(View.GONE);
            shimmerFrameLayout.setVisibility(View.GONE);
            shimmerFrameLayout.stopShimmer();
            swipeRefreshLayout.setRefreshing(false);


            if (apiResponse != null) {
                // Handle the API response


                List<Service> newServices = apiResponse.getServices();



                if (newServices != null && !newServices.isEmpty()) {


                    if (currentPage == 1) {
                        // If it's the first page, clear the old list
                        services.clear();
                        adapter.notifyDataSetChanged();
                    }

                    findViewById(R.id.noDataTxt).setVisibility(View.GONE);
                    recyclerByCat.setVisibility(View.VISIBLE);


                    services.addAll(newServices);
                    adapter.setData(services);
                    // Move notifyDataSetChanged outside the loop
                    adapter.notifyDataSetChanged();

                    if (newServices.size() < itemsPerPage) {
                        isLastPage = true;
                    }
                } else if (currentPage > 1) {
                    // If no new data on a subsequent page, consider it the last page
                    isLastPage = true;
                } else {

                    findViewById(R.id.noDataTxt).setVisibility(View.VISIBLE);

                    recyclerByCat.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                }

                // Notify the adapter outside the if condition
//                adapter.notifyDataSetChanged();
            } else {
                // Handle error
            }
        }

    }
}