package www.experthere.in.users;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.nativead.NativeAd;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import www.experthere.in.Common.GetShimmerStrings;
import www.experthere.in.LocationSelectActivity;
import www.experthere.in.R;
import www.experthere.in.adapters.ServiceAdapterUserSearch;
import www.experthere.in.adapters.SearchSuggestionAdapter;
import www.experthere.in.adapters.shimmer.MyShimmerAdapter;
import www.experthere.in.adapters.shimmer.NonScrollableLayoutManager;
import www.experthere.in.api.ApiClient;
import www.experthere.in.api.ApiInterface;
import www.experthere.in.helper.AdManager;
import www.experthere.in.helper.CustomToastNegative;
import www.experthere.in.helper.CustomToastPositive;
import www.experthere.in.helper.FilterSekectorBottomSheet;
import www.experthere.in.helper.TextMaskUtil;
import www.experthere.in.model.SavedFilterList;
import www.experthere.in.model.SearchServiceResponse;
import www.experthere.in.model.service_user_cat.Service;
import www.experthere.in.serviceProvider.ServiceProviderProfileDetailsActivity;

public class UserServiceSerachActivity extends AppCompatActivity {


    SearchView searchView;
    String latitude, longitude, address;
    String rangeFrom, rangeTo = null;
    int radius = 50;


    private RecyclerView recyclerView;
    private ServiceAdapterUserSearch serviceAdapter;
    private List<Service> serviceList;
    private int currentPage = 1;
    private final int itemsPerPage = 10;


    private int currentPageSearch = 1;
    private final int itemsPerPageSearch = 10;
    private ProgressBar progressBar;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private boolean isLoadingSearch = false;
    private boolean isLastPageSearch = false;
    String stars = null;
    String queryGlobal, searchQueryGlobal;
    TextView locationTxtCat, filterNumber;


    private ListView listView;
    private ArrayAdapter<String> suggestionsAdapter;

    SavedFilterList savedFilterList = new SavedFilterList();

    ShimmerFrameLayout shimmerFrameLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.white, this.getTheme()));

        setContentView(R.layout.activity_user_service_serach);



        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });






        locationTxtCat = findViewById(R.id.locationTxtCat);
        filterNumber = findViewById(R.id.filterNumber);

        initData();


        shimmerFrameLayout = findViewById(R.id.shimmer_lay);
        inflateShimmerRecyclerView(GetShimmerStrings.USER_SERVICE);


        serviceList = new ArrayList<>();
        serviceAdapter = new ServiceAdapterUserSearch(serviceList, UserServiceSerachActivity.this, getSupportFragmentManager());

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        recyclerView = findViewById(R.id.recyclerUserSearch);
        progressBar = findViewById(R.id.prograssBar);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(serviceAdapter);
        recyclerView.setHasFixedSize(true);

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

                        new FetchServicesTask(Double.parseDouble(latitude), Double.parseDouble(longitude), currentPage, radius, itemsPerPage, queryGlobal, stars, rangeFrom, rangeTo).execute();

                    }
                }
            }
        });



//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
//                        isLoading = true;
//                        currentPage++;
//                        new FetchServicesTask(Double.parseDouble(latitude), Double.parseDouble(longitude), currentPage, radius, itemsPerPage, queryGlobal, stars, rangeFrom, rangeTo).execute();
//                    }
//                }
//            }
//        });


        searchView = findViewById(R.id.searchView);
//        searchView.setOnSearchClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (!searchView.getQuery().toString().isEmpty()) {
//                    initiateSearchProcess(searchView.getQuery().toString());
//                } else {
//                }
//            }
//        });


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                serviceAdapter.filter(query);
                queryGlobal = query;
                initiateSearchProcess(queryGlobal);



                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                queryGlobal = newText;


                if (queryGlobal.isEmpty()) {

                    serviceList.clear();
                    serviceAdapter.notifyDataSetChanged();

                }

                return false;
            }
        });


        findViewById(R.id.searchClick).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {




                if (!searchView.getQuery().toString().isEmpty()) {
                    initiateSearchProcess(queryGlobal);

                    shimmerFrameLayout.startShimmer();
                    shimmerFrameLayout.setVisibility(View.VISIBLE);

                    showAds();

                } else {
                    CustomToastNegative.create(getApplicationContext(),"Type Something To Search !");

                }

            }
        });


        findViewById(R.id.backBtn).setOnClickListener(view -> finish());

        findViewById(R.id.locationBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(UserServiceSerachActivity.this, LocationSelectActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString("key", "RegisterProvider");
                intent.putExtras(bundle);
                startActivityForResult(intent, 882);

            }
        });


        findViewById(R.id.filterBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchView.clearFocus();
                hideKeyboard();



                FilterSekectorBottomSheet bottomSheet = new FilterSekectorBottomSheet(savedFilterList, searchQueryGlobal, queryGlobal, UserServiceSerachActivity.this, new FilterSekectorBottomSheet.KmListener() {
                    @Override
                    public void SelectedKmValue(Object km) {


                        if (km != null) {

                            radius = (int) km;
                            savedFilterList.setRange(String.valueOf(radius));
//
//                            if (listView.getVisibility()==View.VISIBLE){
//
//                                isLoadingSearch = false;
//                                isLastPageSearch = false;
//                                currentPageSearch = 1;
//
//
//                                // Clear old data and notify the adapter
//                                suggestionsAdapter.clear();
//                                suggestionsAdapter.notifyDataSetChanged();
//
//
//
//                                if (searchQueryGlobal!=null){
//
//
//                                    try {
//
//                                        new FetchSearchSuggestions(Double.parseDouble(latitude), Double.parseDouble(longitude), currentPageSearch, radius, itemsPerPageSearch, searchQueryGlobal, stars, rangeFrom, rangeTo).execute();
//
//
//                                    } catch (NumberFormatException e) {
//                                        throw new RuntimeException(e);
//                                    }
//                                }
//
//
//                            }else {

                            if (queryGlobal!=null){

                                if (!queryGlobal.isEmpty()) {

                                    startApiAgain();

                                }
                            }


//                            }


                        }


                    }

                    @Override
                    public void SelectedStars(Object star) {
                        hideKeyboard();

                        savedFilterList.setStar(String.valueOf(star));

                        if (star != null) {



                            if (star.equals(0)) {
                                stars = null;

                            } else {
                                stars = String.valueOf(star);
                            }


                        }


//                        if (listView.getVisibility()==View.VISIBLE){
//
//                            isLoadingSearch = false;
//                            isLastPageSearch = false;
//                            currentPageSearch = 1;
//
//
//                            // Clear old data and notify the adapter
//                            suggestionsAdapter.clear();
//                            suggestionsAdapter.notifyDataSetChanged();
//
//
//
//                            if (searchQueryGlobal!=null){
//
//
//                                try {
//
//                                    new FetchSearchSuggestions(Double.parseDouble(latitude), Double.parseDouble(longitude), currentPageSearch, radius, itemsPerPageSearch, searchQueryGlobal, stars, rangeFrom, rangeTo).execute();
//
//
//                                } catch (NumberFormatException e) {
//                                    throw new RuntimeException(e);
//                                }
//                            }
//
//
//                        }else {


//                        }

                        if (queryGlobal!=null){
                            if (!queryGlobal.isEmpty()) {

                                startApiAgain();

                            }

                        }



                    }


                    @Override
                    public void SelectedPriceRange(Object from, Object to) {
                        hideKeyboard();

                        if (from != null && to != null) {

                            rangeFrom = String.valueOf(from);
                            rangeTo = String.valueOf(to);

                            savedFilterList.setFrom(String.valueOf(rangeFrom));
                            savedFilterList.setTo(String.valueOf(rangeTo));

//                            if (listView.getVisibility()==View.VISIBLE){
//
//                                isLoadingSearch = false;
//                                isLastPageSearch = false;
//                                currentPageSearch = 1;
//
//
//                                // Clear old data and notify the adapter
//                                suggestionsAdapter.clear();
//                                suggestionsAdapter.notifyDataSetChanged();
//
//
//
//                                if (searchQueryGlobal!=null){
//
//
//                                    try {
//
//                                        new FetchSearchSuggestions(Double.parseDouble(latitude), Double.parseDouble(longitude), currentPageSearch, radius, itemsPerPageSearch, searchQueryGlobal, stars, rangeFrom, rangeTo).execute();
//
//
//                                    } catch (NumberFormatException e) {
//                                        throw new RuntimeException(e);
//                                    }
//                                }
//
//
//                            }else {


                            if (queryGlobal!=null){
                                if (!queryGlobal.isEmpty()) {

                                    startApiAgain();

                                }
                            }



//                            }
                        }


                    }

                    @Override
                    public void FilterCounts(int count) {
                        hideKeyboard();


                        filterNumber.setText(String.valueOf(count));


                        if (count == 0) {

                            CustomToastNegative.create(getApplicationContext(),"No Filter Applied!");
                        } else {


                            if (queryGlobal!=null) {

                                if (!queryGlobal.isEmpty()) {

                                    shimmerFrameLayout.startShimmer();
                                    shimmerFrameLayout.setVisibility(View.VISIBLE);




                                    CustomToastPositive.create(getApplicationContext(),"Filter Applied Successfully");

                                } else {
                                    CustomToastNegative.create(getApplicationContext(),"Enter Text in Search box !");


                                }
                            }



                        }


                    }

                });
                bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());


            }
        });


        listView = findViewById(R.id.listViewSuggestions);

        // Create an ArrayAdapter for suggestions
//        suggestionsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);

        suggestionsAdapter = new SearchSuggestionAdapter(getBaseContext(), new ArrayList<>());


        listView.setAdapter(suggestionsAdapter);
        listView.setDividerHeight(0);
        // Set up SearchView and handle query changes
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        if (searchManager != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Handle search query submission
                performSearch(query);


                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Handle search query changes
                // Update suggestions based on the newText
//




                suggestionsAdapter.clear();
                suggestionsAdapter.notifyDataSetChanged();

                searchQueryGlobal = newText;






                if (newText.isEmpty() ) {

                    listView.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);

                    serviceList.clear();
                    serviceAdapter.notifyDataSetChanged();

                    queryGlobal = "";

//                    Log.d("CHECKIHWBWB", "onQueryTextChange:  Null "+listView.getVisibility());

                }else {
                    listView.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    updateSuggestions(newText);


                }



                return true;
            }
        });

        // Handle item click in the suggestions list
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Handle click on a suggestion item
                String suggestion = suggestionsAdapter.getItem(position);
                searchView.setQuery(suggestion, true); // Set the query and submit
            }
        });


        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // Not used in this example
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int lastVisibleItem = firstVisibleItem + visibleItemCount;

                if (!isLoadingSearch && !isLastPageSearch) {
                    if (lastVisibleItem == totalItemCount && totalItemCount != 0) {
                        // Load more data when reaching the end
                        isLoadingSearch = true;
                        currentPageSearch++;
                        try {

                            new FetchSearchSuggestions(Double.parseDouble(latitude), Double.parseDouble(longitude), currentPageSearch, radius, itemsPerPageSearch, searchQueryGlobal, stars, rangeFrom, rangeTo).execute();


                        } catch (NumberFormatException e) {
                            throw new RuntimeException(e);
                        }

                    }
                }
            }
        });


        searchView.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(searchView, InputMethodManager.SHOW_IMPLICIT);



        showNativeAds();



    }


    AdManager adManager;
    SharedPreferences adsPref;

    boolean adsStatus;
    String bannerId, interstitialID, appOpenId, nativeId;

    private void showNativeAds() {


        SharedPreferences adsPref = getSharedPreferences("ads",MODE_PRIVATE);

        String nativeId = adsPref.getString("native", "ca-app-pub-3940256099942544/2247696110");


        if (adsPref.getBoolean("status", false)){




            int backgroundColor = ContextCompat.getColor(UserServiceSerachActivity.this, R.color.main_screen_bg_color);
            ColorDrawable background = new ColorDrawable(backgroundColor);


            AdLoader adLoader = new AdLoader.Builder(UserServiceSerachActivity.this, nativeId)
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


    private void showAds() {
        adsPref = getSharedPreferences("ads", MODE_PRIVATE);

        interstitialID = adsPref.getString("interstitial", "ca-app-pub-3940256099942544/1033173712");
        bannerId = adsPref.getString("banner", "ca-app-pub-3940256099942544/9214589741");
        nativeId = adsPref.getString("native", "ca-app-pub-3940256099942544/2247696110");
        appOpenId = adsPref.getString("app_open", "ca-app-pub-3940256099942544/9257395921");

        adsStatus = adsPref.getBoolean("status", false);


        if (adsStatus) {

            adManager = new AdManager(interstitialID);

            adManager.loadAd(UserServiceSerachActivity.this);
            // Schedule the ad to be shown after a delay
            new Handler().postDelayed(() -> {
                if (adManager.isAdLoaded()) {
                    adManager.showAd(UserServiceSerachActivity.this);
                    Log.d("slklxnl", "Showing Ads.");


                } else {
                    Log.d("slklxnl", "The interstitial ad wasn't ready yet.");
                }
            }, 3000); // Adjust the delay as needed

        }


    }

    private void inflateShimmerRecyclerView(String types) {
        RecyclerView shimmerRecycler = findViewById(R.id.recycler_shimmer);

        MyShimmerAdapter shimmerAdapter = new MyShimmerAdapter(types);
        NonScrollableLayoutManager layoutManager = new NonScrollableLayoutManager(UserServiceSerachActivity.this);
        shimmerRecycler.setLayoutManager(layoutManager);
        shimmerRecycler.setAdapter(shimmerAdapter);
    }


    private void updateSuggestions(String query) {
        // Implement your logic to get suggestions based on the query
        getSuggestions(query);

        // Update the suggestionsAdapter with the new suggestions

    }

    private void getSuggestions(String query) {

        searchQueryGlobal = query;

        try {

            Log.d("JHVWHVJJJWDW", "Finding Search Term" + query);

            new FetchSearchSuggestions(Double.parseDouble(latitude), Double.parseDouble(longitude), currentPageSearch, radius, itemsPerPageSearch, searchQueryGlobal, stars, rangeFrom, rangeTo).execute();

        } catch (NumberFormatException e) {
            throw new RuntimeException(e);
        }


    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    private void performSearch(String query) {
        // Implement your search logic here
        // You can use this method to handle the search query when submitted
        // For example, you can launch a new activity with search results

        listView.setVisibility(View.GONE);

        shimmerFrameLayout.startShimmer();
        shimmerFrameLayout.setVisibility(View.VISIBLE);







        hideKeyboard();
        queryGlobal = query;



        startApiAgain();


    }


    private void startApiAgain() {

        initiateSearchProcess(queryGlobal);

    }

    private void initData() {


        SharedPreferences preferences = getSharedPreferences("settings", MODE_PRIVATE);

        Log.d("KJBBSJBJ", "initData: " + preferences.getString("latitude", "0"));
        Log.d("KJBBSJBJ", "initData: " + preferences.getString("longitude", "0"));
        Log.d("KJBBSJBJ", "initData: " + preferences.getInt("range", 50));


        latitude = preferences.getString("latitude", "0");
        longitude = preferences.getString("longitude", "0");
        address = preferences.getString("address", "Select Address");
        radius = preferences.getInt("range", 50);

        TextMaskUtil.maskText(locationTxtCat, address, 30, "..");


    }

    private void initiateSearchProcess(String query) {


        try {


            // Fetch data when search query is empty
            currentPage = 1;
            isLoading = false;
            isLastPage = false;
            serviceList.clear();
            serviceAdapter.notifyDataSetChanged();


            new FetchServicesTask(Double.parseDouble(latitude), Double.parseDouble(longitude), currentPage, radius, itemsPerPage, query, stars, rangeFrom, rangeTo).execute();

        } catch (NumberFormatException e) {
            throw new RuntimeException(e);
        }

    }


    private class FetchSearchSuggestions extends AsyncTask<Void, Void, SearchServiceResponse> {
        private final Double latitude;
        private final Double longitude;
        private final int page;
        private final int radius;
        private final int itemsPerPage;
        private final String searchQuery;
        private final String stars;
        String rangeFrom, rangeTo;

        public FetchSearchSuggestions(Double latitude, Double longitude,
                                      int page, int radius, int itemsPerPage,
                                      String searchQuery, String stars,
                                      String rangeFrom, String rangeTo) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.page = page;
            this.radius = radius;
            this.itemsPerPage = itemsPerPage;
            this.searchQuery = searchQuery;
            this.stars = stars;
            this.rangeFrom = rangeFrom;
            this.rangeTo = rangeTo;
        }

        @Override
        protected void onPreExecute() {

            listView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);

            super.onPreExecute();

        }

        @Override
        protected SearchServiceResponse doInBackground(Void... voids) {

            Log.d("JHVWHVJJJWDW", "Do in BG runing");


            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
//            Call<ApiResponse> call = apiInterface.getServiceUserWithSearchAndFilter(latitude, longitude, page, itemsPerPage, radius, 5, searchQuery);

            Call<SearchServiceResponse> call = apiInterface.getServiceSuggestionSearch
                    (
                            page,
                            itemsPerPage,
                            searchQuery,
                            stars,
                            rangeFrom,
                            rangeTo,
                            String.valueOf(latitude),
                            String.valueOf(longitude),
                            radius);

            Log.d("JHVWHVJJJWDW", "send DATA" + latitude + "\n" +
                    longitude + "\n"
                    + page + "\n"
                    + itemsPerPage +
                    "\n" + radius +
                    "\n" + stars +
                    "\n" + searchQuery + "\n"
                    + rangeFrom + "\n"
                    + rangeTo);


            try {
                Response<SearchServiceResponse> response = call.execute();
                if (response.isSuccessful() && response.body() != null) {


                    if (response.body().getServices() != null) {
                        return response.body();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("APISEARCHING", "Error " + e.getMessage());


            }

            return null;
        }

        @Override
        protected void onPostExecute(SearchServiceResponse serviceResponse) {
            super.onPostExecute(serviceResponse);




            if (serviceResponse != null) {
                if (serviceResponse.isSuccess() && serviceResponse.getMessage().equals("Services found success.")) {
                    isLoadingSearch = false;

                    List<Service> newServices = serviceResponse.getServices();

                    if (newServices != null && !newServices.isEmpty()) {
                        List<String> enabledServices = new ArrayList<>();

                        for (Service service : newServices) {
                                enabledServices.add(service.getServiceTitle());
                        }


                        if (currentPageSearch == 1) {
                            // If it's the first page, clear the old list
                            suggestionsAdapter.clear();
                            suggestionsAdapter.notifyDataSetChanged();
                        }

                        suggestionsAdapter.clear();
                        suggestionsAdapter.addAll(enabledServices);

                        suggestionsAdapter.notifyDataSetChanged();

                        if (enabledServices.size() < itemsPerPageSearch) {
                            isLastPageSearch = true;
                        }
                    } else if (currentPageSearch > 1) {
                        // If no new data on a subsequent page, consider it the last page
                        isLastPageSearch = true;
                    }
                } else {

                    CustomToastNegative.create(getApplicationContext(),"No Service Found !");

                }
            } else {
                // Handle the case when there is no responsesw
            }
        }

    }

//    private class FetchServicesTask extends AsyncTask<Void, Void, SearchServiceResponse> {
//        private Double latitude, longitude;
//        private int page, radius;
//        private int itemsPerPage;
//        private String searchQuery, stars;
//        String rangeFrom, rangeTo;
//
//        public FetchServicesTask(Double latitude, Double longitude,
//                                 int page, int radius, int itemsPerPage,
//                                 String searchQuery, String stars,
//                                 String rangeFrom, String rangeTo) {
//            this.latitude = latitude;
//            this.longitude = longitude;
//            this.page = page;
//            this.radius = radius;
//            this.itemsPerPage = itemsPerPage;
//            this.searchQuery = searchQuery;
//            this.stars = stars;
//            this.rangeFrom = rangeFrom;
//            this.rangeTo = rangeTo;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//
//
//            isLoading = true;
//
//        }
//
//        @Override
//        protected SearchServiceResponse doInBackground(Void... voids) {
//
//            Log.d("APISEARCHING", "Do IN BG  runs");
//
//
//            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
////            Call<ApiResponse> call = apiInterface.getServiceUserWithSearchAndFilter(latitude, longitude, page, itemsPerPage, radius, 5, searchQuery);
//
//            Call<SearchServiceResponse> call = apiInterface.getServiceUserWithSearchAndFilter
//                    (
//                            page,
//                            itemsPerPage,
//                            searchQuery,
//                            stars,
//                            rangeFrom,
//                            rangeTo,
//                            String.valueOf(latitude),
//                            String.valueOf(longitude),
//                            radius);
//
//            Log.d("DATATAT", "send DATA" + latitude + "\n" +
//                    longitude + "\n"
//                    + page + "\n"
//                    + itemsPerPage +
//                    "\n" + radius +
//                    "\n" + stars +
//                    "\n" + searchQuery + "\n"
//                    + rangeFrom + "\n"
//                    + rangeTo);
//
//
//            try {
//                Response<SearchServiceResponse> response = call.execute();
//                if (response.isSuccessful() && response.body() != null) {
//
//
//                    if (response.body().getServices() != null) {
//                        return response.body();
//                    }
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//                Log.d("APISEARCHING", "Error " + e.getMessage());
//
//
//            }
//
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(SearchServiceResponse serviceResponse) {
//            super.onPostExecute(serviceResponse);
//
//
//            progressBar.setVisibility(View.GONE);
//            recyclerView.setVisibility(View.VISIBLE);
//            listView.setVisibility(View.GONE);
//
//            shimmerFrameLayout.stopShimmer();
//            shimmerFrameLayout.setVisibility(View.GONE);
//
//
//            if (serviceResponse != null) {
//
//
//                if (serviceResponse.isSuccess() && serviceResponse.getMessage().equals("Services found success.")) {
//
//
//                    isLoading = false;
//
//                    List<Service> newServices = serviceResponse.getServices();
//
//                    if (newServices != null && !newServices.isEmpty()) {
//
//
//
//                        if (currentPage == 1) {
//                            // If it's the first page, clear the old list
//                            serviceList.clear();
//                            serviceAdapter.notifyDataSetChanged();
//                        }
//
//                        serviceList.addAll(newServices);
//                        serviceAdapter.setData(serviceList);
//                        // Move notifyDataSetChanged outside the loop
//                        serviceAdapter.notifyDataSetChanged();
//
//
//                        findViewById(R.id.noDataText).setVisibility(View.GONE);
//                        recyclerView.setVisibility(View.VISIBLE);
//
//
//                        if (newServices.size() < itemsPerPage) {
//                            isLastPage = true;
//                        }
//                    } else if (currentPage > 1) {
//                        // If no new data on a subsequent page, consider it the last page
//                        isLastPage = true;
//                    }
//
//
//                } else {
//
//                    CustomToastNegative.create(getApplicationContext(),"No Service Found !");
//
//                }
//
//
//            } else {
//
//
//
//                Log.d("SGHHUSBSBH", "NULL RES - PAGE: "+page);
//                Log.d("SGHHUSBSBH", "NULL RES - ITEM PER PAGE : "+itemsPerPage);
//                Log.d("SGHHUSBSBH", "NULL RES  SEARCH QUERY: "+searchQuery);
//                Log.d("SGHHUSBSBH", "NULL RES STARS: "+  stars);
//                Log.d("SGHHUSBSBH", "NULL RES RANGE FROM: "+rangeFrom);
//                Log.d("SGHHUSBSBH", "NULL RES RANGE TO: "+rangeTo);
//                Log.d("SGHHUSBSBH", "NULL RES LAT: "+latitude);
//                Log.d("SGHHUSBSBH", "NULL RES LONG: "+longitude);
//                Log.d("SGHHUSBSBH", "NULL RES RADIUS: "+radius);
//
//                findViewById(R.id.noDataText).setVisibility(View.VISIBLE);
//                recyclerView.setVisibility(View.GONE);
//
//            }
//
//
//        }
//    }


    private class FetchServicesTask extends AsyncTask<Void, Void, SearchServiceResponse> {
        private final Double latitude;
        private final Double longitude;
        private final int page;
        private final int radius;
        private final int itemsPerPage;
        private final String searchQuery;
        private final String stars;
        String rangeFrom, rangeTo;

        public FetchServicesTask(Double latitude, Double longitude,
                                 int page, int radius, int itemsPerPage,
                                 String searchQuery, String stars,
                                 String rangeFrom, String rangeTo) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.page = page;
            this.radius = radius;
            this.itemsPerPage = itemsPerPage;
            this.searchQuery = searchQuery;
            this.stars = stars;
            this.rangeFrom = rangeFrom;
            this.rangeTo = rangeTo;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isLoading = true;
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected SearchServiceResponse doInBackground(Void... voids) {
            Log.d("APISEARCHING", "Do IN BG runs");

            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
            Call<SearchServiceResponse> call = apiInterface.getServiceUserWithSearchAndFilter(
                    page, itemsPerPage, searchQuery, stars, rangeFrom, rangeTo,
                    String.valueOf(latitude), String.valueOf(longitude), radius);

            Log.d("DATATAT", "send DATA: " + latitude + "\n" + longitude + "\n" + page + "\n" + itemsPerPage + "\n" + radius + "\n" + stars + "\n" + searchQuery + "\n" + rangeFrom + "\n" + rangeTo);

            try {
                Response<SearchServiceResponse> response = call.execute();
                if (response.isSuccessful() && response.body() != null) {
                    return response.body();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("APISEARCHING", "Error: " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(SearchServiceResponse serviceResponse) {
            super.onPostExecute(serviceResponse);

//            progressBar.setVisibility(View.GONE);
            isLoading = false;


            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);

            shimmerFrameLayout.stopShimmer();
            shimmerFrameLayout.setVisibility(View.GONE);

            if (serviceResponse != null) {
                if (serviceResponse.isSuccess() && serviceResponse.getMessage().equals("Services found success.")) {
                    List<Service> newServices = serviceResponse.getServices();
                    if (newServices != null && !newServices.isEmpty()) {
                        if (page == 1) {
                            serviceList.clear();
                        }
                        serviceList.addAll(newServices);
                        serviceAdapter.setData(serviceList);
                        serviceAdapter.notifyDataSetChanged();
                        findViewById(R.id.noDataText).setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);

                        if (newServices.size() < itemsPerPage) {
                            isLastPage = true;
                        }
                    } else {
                        isLastPage = true;
                    }
                } else {
                    if (page == 1) {
                        findViewById(R.id.noDataText).setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }
                    isLastPage = true;
                }
            } else {
                Log.d("SGHHUSBSBH", "NULL RES - PAGE: " + page);
                Log.d("SGHHUSBSBH", "NULL RES - ITEM PER PAGE : " + itemsPerPage);
                Log.d("SGHHUSBSBH", "NULL RES SEARCH QUERY: " + searchQuery);
                Log.d("SGHHUSBSBH", "NULL RES STARS: " + stars);
                Log.d("SGHHUSBSBH", "NULL RES RANGE FROM: " + rangeFrom);
                Log.d("SGHHUSBSBH", "NULL RES RANGE TO: " + rangeTo);
                Log.d("SGHHUSBSBH", "NULL RES LAT: " + latitude);
                Log.d("SGHHUSBSBH", "NULL RES LONG: " + longitude);
                Log.d("SGHHUSBSBH", "NULL RES RADIUS: " + radius);

                if (page == 1) {
                    findViewById(R.id.noDataText).setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
                isLastPage = true;
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 882 && resultCode == RESULT_OK) {

            if (data != null) {

                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    latitude = bundle.getString("lat");
                    longitude = bundle.getString("long");
                    address = bundle.getString("address");

                    assert address != null;
                    TextMaskUtil.maskText(locationTxtCat, address, 30, "..");


                }


                isLoading = false;
                isLastPage = false;
                currentPage = 1;
                progressBar.setVisibility(View.VISIBLE);


                // Clear old data and notify the adapter
                serviceList.clear();
                serviceAdapter.notifyDataSetChanged();

                isLoadingSearch = false;
                isLastPageSearch = false;
                currentPageSearch = 1;


                // Clear old data and notify the adapter
                suggestionsAdapter.clear();
                suggestionsAdapter.notifyDataSetChanged();


                if (searchQueryGlobal != null) {


                    try {



                        new FetchSearchSuggestions(Double.parseDouble(latitude), Double.parseDouble(longitude), currentPageSearch, radius, itemsPerPageSearch, searchQueryGlobal, stars, rangeFrom, rangeTo).execute();


                    } catch (NumberFormatException e) {
                        throw new RuntimeException(e);
                    }
                }


                if (queryGlobal != null) {


                    shimmerFrameLayout.startShimmer();
                    shimmerFrameLayout.setVisibility(View.VISIBLE);



                    new FetchServicesTask(Double.parseDouble(latitude), Double.parseDouble(longitude), currentPage, radius, itemsPerPage, queryGlobal, stars, rangeFrom, rangeTo).execute();

                } else {
                    progressBar.setVisibility(View.GONE);


                }


            }


        }


    }


    private double calculateFinalPrice(double cutPrice, double taxValue) {
        return cutPrice + (cutPrice * (taxValue / 100));
    }


    @Override
    protected void onResume() {
        super.onResume();


        hideKeyboard();


    }
}