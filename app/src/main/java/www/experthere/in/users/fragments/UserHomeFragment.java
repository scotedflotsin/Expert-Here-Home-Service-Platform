package www.experthere.in.users.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import www.experthere.in.Common.GetShimmerStrings;
import www.experthere.in.FCM.FcmBearerTokenGenerator;
import www.experthere.in.LocationSelectActivity;
import www.experthere.in.R;
import www.experthere.in.adapters.CatWiseUserHomeAdapter;
import www.experthere.in.adapters.HomeCategoryAdapter;
import www.experthere.in.adapters.HomeSpecificCatServiceAdapter;
import www.experthere.in.adapters.ImageSliderAdapter;
import www.experthere.in.adapters.ProviderListAdapter;
import www.experthere.in.adapters.TopServiceAdapter;
import www.experthere.in.adapters.shimmer.MyShimmerAdapter;
import www.experthere.in.adapters.shimmer.NonScrollableLayoutManager;
import www.experthere.in.api.ApiClient;
import www.experthere.in.api.ApiInterface;
import www.experthere.in.helper.CustomToastNegative;
import www.experthere.in.helper.ProviderListResponse;
import www.experthere.in.helper.RangeSekectorBottomSheet;
import www.experthere.in.helper.TextMaskUtil;
import www.experthere.in.model.CAT_TEST;
import www.experthere.in.model.Category;
import www.experthere.in.model.CategoryResponse;
import www.experthere.in.model.LoginResponse;
import www.experthere.in.model.ProviderList;
import www.experthere.in.model.SliderData;
import www.experthere.in.model.SliderResponse;
import www.experthere.in.model.SuccessMessageResponse;
import www.experthere.in.model.service_user_cat.ApiResponse;
import www.experthere.in.model.service_user_cat.Service;
import www.experthere.in.model.TestResponse;
import www.experthere.in.users.HomeActivity;
import www.experthere.in.users.LoginActivity;
import www.experthere.in.users.UserServiceSerachActivity;


public class UserHomeFragment extends Fragment {

    SwipeRefreshLayout swipeRefreshLayout;
    String specificCatName = "Home Services";

    boolean isLocationUpdated;
    Activity activity;
    private LinearLayout dotsLayout;
    ViewPager viewPager;
    ImageSliderAdapter sliderAdapter;
    RecyclerView recyclerView;

    ProgressBar progressBar;
    ArrayList<Category> categories;

    HomeCategoryAdapter adapter;
    List<SliderData> imageUrls = new ArrayList<>();
    TextView noDataTxt, kmTxt, locationTxtCat;

    String latitude, longitude, address;
    int radiusKm;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;


    RecyclerView topServiceRecyclerView;
    TopServiceAdapter topServiceAdapter;

    ProgressBar topServiceProgressBar;

    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int currentPage = 1;
    private final int itemsPerPage = 10;

    List<Service> serviceList;


    RecyclerView homeServiceRecyclerView;
    HomeSpecificCatServiceAdapter homeServiceAdapter;
    ProgressBar homeServiceProgressBar;
    private boolean isLoadinghome = false;
    private boolean isLastPagehome = false;
    private int currentPagehome = 1;
    private final int itemsPerPagehome = 10;

    List<Service> serviceListhome;

    RecyclerView providersRecyclerView;
    ProviderListAdapter providersAdapter;
    ProgressBar providersProgressBar;
    private boolean isLoadingproviders = false;
    private boolean isLastPageproviders = false;
    private int currentPageproviders = 1;
    private final int itemsPerPageproviders = 4;

    List<ProviderList> serviceListproviders;



    private boolean isLoadingCatWise = false;
    private boolean isLastPageCatWise = false;
    private int currentPageCatWise = 1;
    private final int itemsPerPageCatWise = 4;

    //
    RecyclerView catWiseRecycler;
    CatWiseUserHomeAdapter catWiseUserHomeAdapter;


    ShimmerFrameLayout shimmerFrameLayoutTopServices, shimmerFrameLayoutCat, shimmerFrameLayoutHomeService, shimmerProviders, shimmerCatServices;


    TextView noData_top_services, noData_home_services, noData_home_providers, noData_cat_services;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = requireActivity();
        categories = new ArrayList<>();
        serviceListproviders = new ArrayList<>();
        serviceListhome = new ArrayList<>();
        serviceList = new ArrayList<>();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_home, container, false);
        dotsLayout = view.findViewById(R.id.dotsLayout);
        noDataTxt = view.findViewById(R.id.noSliderDatatxt);
        kmTxt = view.findViewById(R.id.kmTxt);
        locationTxtCat = view.findViewById(R.id.locationTxtCat);
        shimmerFrameLayoutTopServices = view.findViewById(R.id.shimmerTop);
        shimmerFrameLayoutCat = view.findViewById(R.id.shimmerCat);
        shimmerFrameLayoutHomeService = view.findViewById(R.id.shimmerHomeServices);
        shimmerProviders = view.findViewById(R.id.shimmerProviders);
        shimmerCatServices = view.findViewById(R.id.shimmerCatServices);

        noData_top_services = view.findViewById(R.id.noData_top_services);
        noData_home_services = view.findViewById(R.id.noData_home_services);
        noData_home_providers = view.findViewById(R.id.noData_home_providers);
        noData_cat_services = view.findViewById(R.id.noData_cat_services);

        inflateShimmerRecyclerView(view, GetShimmerStrings.USER_HOME_SERVICE);
        shimmerFrameLayoutTopServices.startShimmer();

        inflateShimmerRecyclerViewCat(view, GetShimmerStrings.USER_HOME_CAT);
        shimmerFrameLayoutCat.startShimmer();

        inflateShimmerRecyclerViewHomeService(view, GetShimmerStrings.USER_HOME_SERVICES_2);
        shimmerFrameLayoutHomeService.startShimmer();


        inflateShimmerRecyclerViewProviders(view, GetShimmerStrings.USER_HOME_Providers);
        shimmerProviders.startShimmer();


        inflateShimmerRecyclerViewCatServices(view, GetShimmerStrings.USER_HOME_SERVICE);
        shimmerCatServices.startShimmer();


        catWiseRecycler = view.findViewById(R.id.recycler_cat_user);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        // top service data
        topServiceRecyclerView = view.findViewById(R.id.recycler_top_services);
        topServiceProgressBar = view.findViewById(R.id.progTopServices);

        topServiceProgressBar.setVisibility(View.GONE);

        LinearLayoutManager layoutManager = new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
        topServiceAdapter = new TopServiceAdapter(serviceList, activity, getParentFragmentManager());
        topServiceRecyclerView.setAdapter(topServiceAdapter);
        topServiceRecyclerView.setLayoutManager(layoutManager);

        // homeservice data

        homeServiceRecyclerView = view.findViewById(R.id.recycler_home_services);
        homeServiceProgressBar = view.findViewById(R.id.progHomeServices);


        LinearLayoutManager layoutManagerHome = new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
        homeServiceAdapter = new HomeSpecificCatServiceAdapter(serviceListhome, activity, getParentFragmentManager());
        homeServiceRecyclerView.setAdapter(homeServiceAdapter);
        homeServiceRecyclerView.setLayoutManager(layoutManagerHome);

// providers data
        providersRecyclerView = view.findViewById(R.id.recycler_home_providers);
        providersProgressBar = view.findViewById(R.id.progHomeProviders);


        LinearLayoutManager layoutManagerProvider = new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
        providersAdapter = new ProviderListAdapter(serviceListproviders, activity);
        providersRecyclerView.setAdapter(providersAdapter);
        providersRecyclerView.setLayoutManager(layoutManagerProvider);


        preferences = activity.getSharedPreferences("settings", MODE_PRIVATE);
        editor = preferences.edit();


        radiusKm = preferences.getInt("range", 5);


        address = preferences.getString("address", "Select Address");
        latitude = preferences.getString("latitude", "0");
        longitude = preferences.getString("longitude", "0");


        kmTxt.setText(radiusKm + " KM");


        view.findViewById(R.id.locationLay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(activity, LocationSelectActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString("key", "RegisterProvider");
                intent.putExtras(bundle);
                activity.startActivityForResult(intent, 882);

                isLocationUpdated = true;

            }
        });


        view.findViewById(R.id.cardView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(activity, UserServiceSerachActivity.class);
                activity.startActivity(intent);


            }
        });


        view.findViewById(R.id.clickRangeBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                RangeSekectorBottomSheet bottomSheet = new RangeSekectorBottomSheet(activity, new RangeSekectorBottomSheet.KmListener() {
                    @Override
                    public void SelectedKmValue(String km) {




                        SharedPreferences.Editor ee = preferences.edit();
                        ee.putInt("range", Integer.parseInt(km));
                        ee.apply();

                        kmTxt.setText(km + " KM");



                        refresh();


                    }
                });
                bottomSheet.show(getParentFragmentManager(), bottomSheet.getTag());


            }
        });


        // Add more URLs as needed

        viewPager = view.findViewById(R.id.viewPager);

        recyclerView = view.findViewById(R.id.recycler_cat);
        progressBar = view.findViewById(R.id.progCat);


        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);


        adapter = new HomeCategoryAdapter(categories, activity);


        topServiceRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if (!isLoading && !isLastPage) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0 && totalItemCount >= itemsPerPage) {
                        // Load more data when reaching the end

                        isLoading = true;
                        currentPage++;
                        topServiceProgressBar.setVisibility(View.VISIBLE);


                        try {

                            new ApiServiceTask(Double.parseDouble(latitude), Double.parseDouble(longitude), currentPage, itemsPerPage, radiusKm).execute();

                        } catch (Exception e) {

                        }
                    }
                }
            }
        });

        homeServiceRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if (!isLoadinghome && !isLastPagehome) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0 && totalItemCount >= itemsPerPagehome) {
                        // Load more data when reaching the end

                        isLoadinghome = true;
                        currentPagehome++;
                        homeServiceProgressBar.setVisibility(View.VISIBLE);


                        try {

                            new HomeServiceTask(Double.parseDouble(latitude), Double.parseDouble(longitude), currentPagehome, itemsPerPagehome, radiusKm, specificCatName).execute();

                        } catch (Exception e) {

                        }
                    }
                }
            }
        });





        catWiseRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if (!isLoadingCatWise && !isLastPageCatWise) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0 && totalItemCount >= itemsPerPage) {
                        // Load more data when reaching the end

                        isLoadingCatWise = true;
                        currentPageCatWise++;
                        topServiceProgressBar.setVisibility(View.VISIBLE);


                        try {


                            new GetCatList(Double.parseDouble(latitude), Double.parseDouble(longitude), radiusKm).execute();


                        } catch (Exception e) {

                        }
                    }
                }
            }
        });






//        providersRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//
//
//                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
//                int visibleItemCount = layoutManager.getChildCount();
//                int totalItemCount = layoutManager.getItemCount();
//                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
//
//                if (!isLoadingproviders && !isLastPageproviders) {
//                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0 && totalItemCount >= itemsPerPagehome) {
//                        // Load more data when reaching the end
//
//                        isLoadingproviders = true;
//                        currentPageproviders++;
//                        providersProgressBar.setVisibility(View.VISIBLE);
//
//
//                        try {
//                            new GetProvidersList(Double.parseDouble(latitude), Double.parseDouble(longitude), currentPageproviders, itemsPerPageproviders, radiusKm).execute();
//                        } catch (Exception e) {
//
//                        }
//                    }
//                }
//            }
//        });


        providersRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if (!isLoadingproviders && !isLastPageproviders) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0
                            && totalItemCount >= itemsPerPageproviders) {
                        // Load more data when reaching the end

                        isLoadingproviders = true;
                        currentPageproviders++;
                        providersProgressBar.setVisibility(View.VISIBLE);

                        try {
                            new GetProvidersList(Double.parseDouble(latitude), Double.parseDouble(longitude), currentPageproviders, itemsPerPageproviders, radiusKm).execute();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });



        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                refresh();
            }
        });


        try {
            new GetSliderData().execute();
            new GetCategories(activity).execute();
            new ApiServiceTask(Double.parseDouble(latitude), Double.parseDouble(longitude), currentPage, itemsPerPage, radiusKm).execute();
            new HomeServiceTask(Double.parseDouble(latitude), Double.parseDouble(longitude), currentPagehome, itemsPerPagehome, radiusKm, specificCatName).execute();
            new GetProvidersList(Double.parseDouble(latitude), Double.parseDouble(longitude), currentPageproviders, itemsPerPageproviders, radiusKm).execute();
            new GetCatList(Double.parseDouble(latitude), Double.parseDouble(longitude), radiusKm).execute();


            saveTokenDetails();


        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        return view;

    }

    private void inflateShimmerRecyclerViewCatServices(View view, String types) {


        RecyclerView shimmerRecycler = view.findViewById(R.id.recyclerShimmerCatServices);

        MyShimmerAdapter shimmerAdapter = new MyShimmerAdapter(types);
        NonScrollableLayoutManager layoutManager = new NonScrollableLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
        shimmerRecycler.setLayoutManager(layoutManager);
        shimmerRecycler.setAdapter(shimmerAdapter);

    }

    private void inflateShimmerRecyclerViewProviders(View view, String types) {


        RecyclerView shimmerRecycler = view.findViewById(R.id.recyclerShimmerProviders);

        MyShimmerAdapter shimmerAdapter = new MyShimmerAdapter(types);
        NonScrollableLayoutManager layoutManager = new NonScrollableLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
        shimmerRecycler.setLayoutManager(layoutManager);
        shimmerRecycler.setAdapter(shimmerAdapter);

    }

    private void inflateShimmerRecyclerViewHomeService(View view, String types) {


        RecyclerView shimmerRecycler = view.findViewById(R.id.recyclerShimmerHomeService);

        MyShimmerAdapter shimmerAdapter = new MyShimmerAdapter(types);
        NonScrollableLayoutManager layoutManager = new NonScrollableLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
        shimmerRecycler.setLayoutManager(layoutManager);
        shimmerRecycler.setAdapter(shimmerAdapter);
    }

    private void inflateShimmerRecyclerViewCat(View view, String types) {


        RecyclerView shimmerRecycler = view.findViewById(R.id.recyclerShimmerCat);

        MyShimmerAdapter shimmerAdapter = new MyShimmerAdapter(types);
        NonScrollableLayoutManager layoutManager = new NonScrollableLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
        shimmerRecycler.setLayoutManager(layoutManager);
        shimmerRecycler.setAdapter(shimmerAdapter);

    }

    private void inflateShimmerRecyclerView(View view, String types) {

        RecyclerView shimmerRecycler = view.findViewById(R.id.recyclerShimmerTop);

        MyShimmerAdapter shimmerAdapter = new MyShimmerAdapter(types);
        NonScrollableLayoutManager layoutManager = new NonScrollableLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
        shimmerRecycler.setLayoutManager(layoutManager);
        shimmerRecycler.setAdapter(shimmerAdapter);

    }

    private void saveTokenDetails() {


        FcmBearerTokenGenerator tokenGenerator = new FcmBearerTokenGenerator(activity);
        tokenGenerator.generateAccessTokenAsync(new FcmBearerTokenGenerator.TokenGenerationListener() {
            @Override
            public void onTokenGenerated(final String token) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Use the generated token for FCM requests


                        //fcm with auth
                        SharedPreferences sharedPreferences = activity.getSharedPreferences("fcm", MODE_PRIVATE);
                        SharedPreferences.Editor ed = sharedPreferences.edit();

                        Log.d("MESS", "token- " + token);
                        ed.putString("TOKEN", token);
                        ed.apply();

                        String fcmDeviceToken = sharedPreferences.getString("device_token", "0");
                        SharedPreferences pp = activity.getSharedPreferences("user", MODE_PRIVATE);


                        try {
                            new FcmServiceTaskUserGoogle(pp.getString("id", "0"), "0", "user", fcmDeviceToken).execute();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }


                    }
                });
            }

            @Override
            public void onTokenGenerationFailed(final Exception e) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Handle token generation failure

                        CustomToastNegative.create(activity,"Token Generation Fail! ");

                        activity.findViewById(R.id.btnProgressBar).setVisibility(View.GONE);

                    }
                });
            }
        });


    }

    @SuppressLint("NotifyDataSetChanged")
    private void refresh() {


        radiusKm = preferences.getInt("range", 5);


        address = preferences.getString("address", "Select Address");
        latitude = preferences.getString("latitude", "0");
        longitude = preferences.getString("longitude", "0");


        currentPage = 1;
        isLoading = false;
        isLastPage = false;
        serviceList.clear();
        topServiceAdapter.notifyDataSetChanged();


        currentPagehome = 1;
        isLoadinghome = false;
        isLastPagehome = false;
        serviceListhome.clear();
        homeServiceAdapter.notifyDataSetChanged();


        currentPageproviders = 1;
        isLoadingproviders = false;
        isLastPageproviders = false;
        serviceListproviders.clear();
        providersAdapter.notifyDataSetChanged();


        categories.clear();
        adapter.notifyDataSetChanged();


        if (sliderAdapter != null) {
            sliderAdapter.stopAutoScroll();
        }


        topServiceRecyclerView.setVisibility(View.GONE);
        shimmerFrameLayoutTopServices.setVisibility(View.VISIBLE);
        shimmerFrameLayoutTopServices.startShimmer();

        homeServiceRecyclerView.setVisibility(View.GONE);
        shimmerFrameLayoutHomeService.setVisibility(View.VISIBLE);
        shimmerFrameLayoutHomeService.startShimmer();


        providersRecyclerView.setVisibility(View.GONE);
        shimmerProviders.setVisibility(View.VISIBLE);
        shimmerProviders.startShimmer();


        recyclerView.setVisibility(View.GONE);
        shimmerFrameLayoutCat.setVisibility(View.VISIBLE);
        shimmerFrameLayoutCat.startShimmer();

        catWiseRecycler.setVisibility(View.GONE);
        shimmerCatServices.setVisibility(View.VISIBLE);
        shimmerCatServices.startShimmer();


        noDataTxt.setVisibility(View.GONE);
        noData_cat_services.setVisibility(View.GONE);
        noData_home_providers.setVisibility(View.GONE);
        noData_home_services.setVisibility(View.GONE);
        noData_top_services.setVisibility(View.GONE);


        try {

            new GetCategories(activity).execute();
            new GetSliderData().execute();
            new HomeServiceTask(Double.parseDouble(latitude), Double.parseDouble(longitude), currentPagehome, itemsPerPagehome, radiusKm, specificCatName).execute();
            new ApiServiceTask(Double.parseDouble(latitude), Double.parseDouble(longitude), currentPage, itemsPerPage, radiusKm).execute();
            new GetProvidersList(Double.parseDouble(latitude), Double.parseDouble(longitude), currentPageproviders, itemsPerPageproviders, radiusKm).execute();
            new GetCatList(Double.parseDouble(latitude), Double.parseDouble(longitude), radiusKm).execute();


        } catch (NumberFormatException e) {
            throw new RuntimeException(e);
        }


    }

    private void updateDots(int currentPage) {
        if (dotsLayout != null && sliderAdapter != null) {
            sliderAdapter.updateDots(currentPage);
        }
    }

    public class GetSliderData extends AsyncTask<Void, Void, SliderResponse> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected SliderResponse doInBackground(Void... voids) {
            try {
                // Create the Retrofit instance
                ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

                // Make the API call
                Call<SliderResponse> call = apiInterface.getImageSlider();


                Response<SliderResponse> response = call.execute();

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
        protected void onPostExecute(SliderResponse apiResponse) {
            super.onPostExecute(apiResponse);


            if (apiResponse != null) {
                // Handle the API response

                Log.d("SJBSBJHB", "trying");


                if (apiResponse.isSuccess() && apiResponse.getMessage().equals("Image slider data retrieved successfully.")) {


                    imageUrls = apiResponse.getImage_slider_data();


                    if (imageUrls != null) {

                        Log.d("SJBSBJHB", "onPostExecute: " + imageUrls.size());


                        sliderAdapter = new ImageSliderAdapter(activity, imageUrls, dotsLayout, viewPager);

                        viewPager.setAdapter(sliderAdapter);


                        viewPager.setOffscreenPageLimit(imageUrls.size() * 2);


                        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {


                            @Override
                            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                            }

                            @Override
                            public void onPageSelected(int position) {


                                // Use modulo to determine the actual position for dots
                                updateDots(position);


                            }

                            @Override
                            public void onPageScrollStateChanged(int state) {

                            }

                        });


                        // Add initial dots
                        updateDots(0);
                        if (sliderAdapter != null) {

                            sliderAdapter.startAutoScroll();

                        }


                    } else {
                        Log.d("SJBSBJHB", "N Data: " + imageUrls.size());

                    }


                } else {
                    // if no images are in server


                    viewPager.setVisibility(View.INVISIBLE);
                    noDataTxt.setVisibility(View.VISIBLE);

                }

            } else {

            }


        }

    }

    private class GetCategories extends AsyncTask<Void, Void, Void> {

        Activity activity;

        public GetCategories(Activity activity) {
            this.activity = activity;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            // Perform background operation, e.g., user

            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

            Call<CategoryResponse> call = apiInterface.getCategories();


            call.enqueue(new Callback<CategoryResponse>() {
                @Override
                public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {


                    if (response.isSuccessful() && response.body() != null) {

                        CategoryResponse categoryResponse = response.body();

                        if (categoryResponse.isSuccess()) {

                            categories.addAll(categoryResponse.getCategories());
                            recyclerView.setAdapter(adapter);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
                            recyclerView.setLayoutManager(linearLayoutManager);
                            recyclerView.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);


                        } else {
                            activity.runOnUiThread(() -> {

                                CustomToastNegative.create(activity,"Error Getting Categories ");

                                progressBar.setVisibility(View.GONE);

                            });


                        }


                    } else {

                        activity.runOnUiThread(() -> {

                            CustomToastNegative.create(activity," Category Response Fail!");

                            progressBar.setVisibility(View.GONE);

                        });

                    }

                }

                @Override
                public void onFailure(Call<CategoryResponse> call, Throwable t) {

                    activity.runOnUiThread(() -> {
                        CustomToastNegative.create(activity,"Error: "+t.getMessage());

                        progressBar.setVisibility(View.GONE);


                    });

                }
            });


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // This method runs on the UI thread and can be used to update the UI after background task completion

            swipeRefreshLayout.setRefreshing(false);

            shimmerFrameLayoutCat.setVisibility(View.GONE);
            shimmerFrameLayoutCat.stopShimmer();


        }
    }


    @Override
    public void onResume() {

        super.onResume();


        if (preferences != null && locationTxtCat != null) {

            TextMaskUtil.maskText(locationTxtCat, preferences.getString("address", "Select Address"), 30, "..");


            address = preferences.getString("address", "Select Address");
            latitude = preferences.getString("latitude", "0");
            longitude = preferences.getString("longitude", "0");


            Log.d("LOCAAJBJHBJA", "onResume: "

                    + address + "\n" + latitude + "\n" + longitude + "\n");


            radiusKm = preferences.getInt("range", 5);
            kmTxt.setText(radiusKm + " KM");


            if (isLocationUpdated) {

                refresh();

                isLocationUpdated = false;

            }


        }


        if (sliderAdapter != null) {

            sliderAdapter.startAutoScroll();

        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (sliderAdapter != null) {

            sliderAdapter.stopAutoScroll();

        }
    }

    public class ApiServiceTask extends AsyncTask<Void, Void, ApiResponse> {

        private final double currentLatitude;
        private final double currentLongitude;

        private final int page;
        private final int itemsPerPage;
        private final int radius;

        public ApiServiceTask(double currentLatitude, double currentLongitude, int page, int itemsPerPage, int radius) {
            this.currentLatitude = currentLatitude;
            this.currentLongitude = currentLongitude;
            this.page = page;
            this.itemsPerPage = itemsPerPage;
            this.radius = radius;
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
                Call<ApiResponse> call = apiInterface.getTopService(

                        currentLatitude, currentLongitude, page, // page
                        itemsPerPage // itemsPerPage
                        , radius);


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
            swipeRefreshLayout.setRefreshing(false);
            topServiceProgressBar.setVisibility(View.GONE);
            topServiceRecyclerView.setVisibility(View.VISIBLE);

            noData_top_services.setVisibility(View.GONE);


            shimmerFrameLayoutTopServices.setVisibility(View.GONE);
            shimmerFrameLayoutTopServices.stopShimmer();

            if (apiResponse != null) {
                // Handle the API response

                isLoading = false;

                List<Service> newServices = apiResponse.getServices();


                if (newServices != null && !newServices.isEmpty()) {


                    if (currentPage == 1) {
                        // If it's the first page, clear the old list
                        serviceList.clear();
                        topServiceAdapter.notifyDataSetChanged();
                    }

                    serviceList.addAll(newServices);
                    topServiceAdapter.setData(serviceList);
                    // Move notifyDataSetChanged outside the loop
                    topServiceAdapter.notifyDataSetChanged();

                    if (newServices.size() < itemsPerPage) {
                        isLastPage = true;
                    }
                } else if (currentPage > 1) {
                    // If no new data on a subsequent page, consider it the last page
                    isLastPage = true;
                } else {
                    noData_top_services.setVisibility(View.VISIBLE);
                    topServiceProgressBar.setVisibility(View.GONE);
                    topServiceRecyclerView.setVisibility(View.GONE);

                }

                // Notify the adapter outside the if condition
//                adapter.notifyDataSetChanged();
            } else {
                // Handle error
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


            Log.d("laknckjckjdb", "DATA "+currentLatitude);
            Log.d("laknckjckjdb", "DATA "+currentLongitude);
            Log.d("laknckjckjdb", "DATA "+page);
            Log.d("laknckjckjdb", "DATA "+itemsPerPage);
            Log.d("laknckjckjdb", "DATA "+radius);
            Log.d("laknckjckjdb", "DATA "+categoryName);



            swipeRefreshLayout.setRefreshing(false);

            homeServiceProgressBar.setVisibility(View.GONE);
            homeServiceRecyclerView.setVisibility(View.VISIBLE);


            shimmerFrameLayoutHomeService.setVisibility(View.GONE);
            shimmerFrameLayoutHomeService.stopShimmer();

            if (apiResponse != null) {
                // Handle the API response

                isLoadinghome = false;

                List<Service> newServices = apiResponse.getServices();




                if (newServices != null && !newServices.isEmpty()) {



                    if (currentPagehome == 1) {
                        // If it's the first page, clear the old list
                        serviceListhome.clear();
                        homeServiceAdapter.notifyDataSetChanged();
                    }

                    serviceListhome.addAll(newServices);
                    homeServiceAdapter.setData(serviceListhome);


                    // Move notifyDataSetChanged outside the loop
                    homeServiceAdapter.notifyDataSetChanged();

                    if (newServices.size() < itemsPerPagehome) {
                        isLastPagehome = true;
                    }
                } else if (currentPagehome > 1) {
                    // If no new data on a subsequent page, consider it the last page
                    isLastPagehome = true;
                } else {

                    homeServiceProgressBar.setVisibility(View.GONE);
                    homeServiceRecyclerView.setVisibility(View.GONE);

                    noData_home_services.setVisibility(View.VISIBLE);
                }

                // Notify the adapter outside the if condition
//                adapter.notifyDataSetChanged();
            } else {
                // Handle error
            }
        }

    }


    public class GetProvidersList extends AsyncTask<Void, Void, ProviderListResponse> {

        private final double currentLatitude;
        private final double currentLongitude;

        private final int page;
        private final int itemsPerPage;
        private final int radius;

        public GetProvidersList(double currentLatitude, double currentLongitude, int page, int itemsPerPage, int radius) {
            this.currentLatitude = currentLatitude;
            this.currentLongitude = currentLongitude;
            this.page = page;
            this.itemsPerPage = itemsPerPage;
            this.radius = radius;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected ProviderListResponse doInBackground(Void... voids) {
            try {
                // Create the Retrofit instance
                ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

                // Make the API call
                Call<ProviderListResponse> call = apiInterface.getProvidersList(

                        currentLatitude, currentLongitude, page, // page
                        itemsPerPage // itemsPerPage
                        , radius);


                Response<ProviderListResponse> response = call.execute();

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
        protected void onPostExecute(ProviderListResponse apiResponse) {
            super.onPostExecute(apiResponse);

            swipeRefreshLayout.setRefreshing(false);

            providersProgressBar.setVisibility(View.GONE);
            providersRecyclerView.setVisibility(View.VISIBLE);


            shimmerProviders.setVisibility(View.GONE);
            shimmerProviders.stopShimmer();


            if (apiResponse != null) {
                // Handle the API response

                isLoadingproviders = false;

                List<ProviderList> newServices = apiResponse.getProviders();


                if (newServices != null && !newServices.isEmpty()) {


                    if (currentPageproviders == 1) {
                        // If it's the first page, clear the old list
                        serviceListproviders.clear();
                        providersAdapter.notifyDataSetChanged();
                    }

                    serviceListproviders.addAll(newServices);
                    providersAdapter.setData(serviceListproviders);
                    // Move notifyDataSetChanged outside the loop
                    providersAdapter.notifyDataSetChanged();

                    if (newServices.size() < itemsPerPageproviders) {
                        isLastPageproviders = true;
                    }
                } else if (currentPageproviders > 1) {
                    // If no new data on a subsequent page, consider it the last page
                    isLastPageproviders = true;
                } else {


                    providersProgressBar.setVisibility(View.GONE);
                    providersRecyclerView.setVisibility(View.GONE);
                    noData_home_providers.setVisibility(View.VISIBLE);


                }

                // Notify the adapter outside the if condition
//                adapter.notifyDataSetChanged();
            } else {
                // Handle error
            }
        }

    }


    public class GetCatList extends AsyncTask<Void, Void, TestResponse> {

        private final double currentLatitude;
        private final double currentLongitude;


        private final int radius;

        int pageG,itemperpageG;

//        public GetCatList(double currentLatitude, double currentLongitude, int radius, int pageG, int itemperpageG) {
//            this.currentLatitude = currentLatitude;
//            this.currentLongitude = currentLongitude;
//            this.radius = radius;
//            this.pageG = pageG;
//            this.itemperpageG = itemperpageG;
//        }

        public GetCatList(double currentLatitude, double currentLongitude, int radius) {
            this.currentLatitude = currentLatitude;
            this.currentLongitude = currentLongitude;
            this.radius = radius;



        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected TestResponse doInBackground(Void... voids) {
            try {
                // Create the Retrofit instance
                ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

                // Make the API call
//                Call<TestResponse> call = apiInterface.getCategoryServices(
//
//                        currentLatitude, currentLongitude
//                        , radius);

         Call<TestResponse> call = apiInterface.getCategoryServices(

                        currentLatitude, currentLongitude
                        , radius);




                Response<TestResponse> response = call.execute();

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
        protected void onPostExecute(TestResponse apiResponse) {
            super.onPostExecute(apiResponse);


            Log.d("MYAPPTESTsjsbj", "DATA  WORKING Radius  "+radius);
            Log.d("MYAPPTESTsjsbj", "DATA  WORKING LAT  "+latitude);
            Log.d("MYAPPTESTsjsbj", "DATA  WORKING LONGI  "+longitude);



            shimmerCatServices.setVisibility(View.GONE);
            shimmerCatServices.stopShimmer();


            if (apiResponse != null) {
                // Handle the API response


                if (apiResponse.isSuccess()) {


                    List<CAT_TEST> list = apiResponse.getCategories();


                    Log.d("SNSKJN", "radius "+radius);
                    Log.d("SNSKJN", "DATA "+currentLatitude);
                    Log.d("SNSKJN", "DATA "+currentLongitude);
                    Log.d("SNSKJN", "DATA "+list.get(1).getCategory_name());
                    Log.d("SNSKJN", "DATA "+list.get(1).getServices().size());




                    if (apiResponse.getCategories()!=null){





                        catWiseUserHomeAdapter = new CatWiseUserHomeAdapter(activity, apiResponse.getCategories(), getParentFragmentManager());
                        catWiseRecycler.setAdapter(catWiseUserHomeAdapter);
                        catWiseRecycler.setLayoutManager(new LinearLayoutManager(activity));

                        catWiseRecycler.setVisibility(View.VISIBLE);

                    }else {


                        noData_cat_services.setVisibility(View.VISIBLE);
                        catWiseRecycler.setVisibility(View.GONE);


                    }


                }


            } else {
                // Handle error
            }
        }

    }


    private class FcmServiceTaskUserGoogle extends AsyncTask<Void, Void, SuccessMessageResponse> {
        String userIDForFcm, providerIdForFcm, typeFcm, fcmTOKEN;

        public FcmServiceTaskUserGoogle(String userIDForFcm, String providerIdForFcm, String typeFcm, String fcmTOKEN) {
            this.userIDForFcm = userIDForFcm;
            this.providerIdForFcm = providerIdForFcm;
            this.typeFcm = typeFcm;
            this.fcmTOKEN = fcmTOKEN;
        }

        @Override
        protected void onPreExecute() {


            super.onPreExecute();

        }

        @Override
        protected SuccessMessageResponse doInBackground(Void... voids) {

            Log.d("JHVWHVJJJWDW", "Do in BG runing");


            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
//            Call<ApiResponse> call = apiInterface.getServiceUserWithSearchAndFilter(latitude, longitude, page, itemsPerPage, radius, 5, searchQuery);

            Call<SuccessMessageResponse> call = apiInterface.fcmUserCodeSaver(userIDForFcm, providerIdForFcm, fcmTOKEN, typeFcm);


            try {
                Response<SuccessMessageResponse> response = call.execute();
                if (response.isSuccessful() && response.body() != null) {


                    return response.body();

                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("APISEARCHING", "Error " + e.getMessage());


            }

            return null;
        }

        @Override
        protected void onPostExecute(SuccessMessageResponse serviceResponse) {
            super.onPostExecute(serviceResponse);


            if (serviceResponse != null) {
                if (serviceResponse.isSuccess()) {

                    if (serviceResponse.getMessage().equals("Token saved successfully!") || serviceResponse.getMessage().equals("Token updated successfully!")) {


                        Log.d("klnllklknl", "user ID " + userIDForFcm);
                        Log.d("klnllklknl", "fcm token " + fcmTOKEN);


                    }


                } else {

                }
            } else {
                // Handle the case when there is no responsesw
            }
        }

    }


}