package www.experthere.in.users.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
import com.google.android.gms.ads.nativead.NativeAd;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import www.experthere.in.Common.GetShimmerStrings;
import www.experthere.in.R;
import www.experthere.in.adapters.BookmarkAdapter;
import www.experthere.in.adapters.shimmer.MyShimmerAdapter;
import www.experthere.in.adapters.shimmer.NonScrollableLayoutManager;
import www.experthere.in.api.ApiClient;
import www.experthere.in.api.ApiInterface;
import www.experthere.in.helper.Bookmark;
import www.experthere.in.helper.BookmarkCountResponse;
import www.experthere.in.helper.BookmarkResponse;
import www.experthere.in.helper.CustomToastNegative;
import www.experthere.in.model.CallHistoryModel;
import www.experthere.in.model.CallHistoryResponse;
import www.experthere.in.model.service_user_cat.Service;
import www.experthere.in.serviceProvider.fragments.IncomingFragment;


public class UserBookmarkFragment extends Fragment {


    Activity activity;

    RecyclerView recyclerView;

    List<Bookmark> bookmarks;

    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int currentPage = 1;
    private final int itemsPerPage = 10;

    LinearLayout txtNoData;

    BookmarkAdapter adapter;

    ProgressBar progressBar;

    TextView bookmarkCountTxt;

    SwipeRefreshLayout swipeRefreshLayout;

    ShimmerFrameLayout shimmerFrameLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = requireActivity();

        bookmarks = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_bookmark, container, false);

        recyclerView = view.findViewById(R.id.recycler_bookmark);
        progressBar = view.findViewById(R.id.progress);
        txtNoData = view.findViewById(R.id.noDataLayout);
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(layoutManager);
        bookmarkCountTxt = view.findViewById(R.id.bookmarkCountTxt);
        adapter = new BookmarkAdapter(bookmarks, activity);
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout = view.findViewById(R.id.swipeLayoutBookmark);
        shimmerFrameLayout = view.findViewById(R.id.shimmer_bookmark);

        inflateShimmerRecyclerView(view, GetShimmerStrings.USER_BOOKMARK);
        shimmerFrameLayout.startShimmer();

        SharedPreferences preferences = activity.getSharedPreferences("user", Context.MODE_PRIVATE);

        try {
            new GetUserBookmarks(currentPage, itemsPerPage, preferences.getString("id", "0")).execute();
            new GetBookmarkCount(preferences.getString("id", "0")).execute();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPage = 1;
                isLoading = false;
                isLastPage = false;
                bookmarks.clear();
                adapter.notifyDataSetChanged();
//
                shimmerFrameLayout.startShimmer();
                shimmerFrameLayout.setVisibility(View.VISIBLE);

                new GetUserBookmarks(currentPage, itemsPerPage, preferences.getString("id", "0")).execute();
                new GetBookmarkCount(preferences.getString("id", "0")).execute();
                recyclerView.setVisibility(View.GONE);

            }
        });
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

                            new GetUserBookmarks(currentPage, itemsPerPage, preferences.getString("id", "0")).execute();

                        } catch (Exception e) {

                            Log.d("ajbsba", "SERVER ERROR 2 " + e.getMessage());

                        }
                    }
                }
            }
        });


        view.findViewById(R.id.addNowBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String CUSTOM_ACTION = "www.experthere.in.users.GOTO.CAT.FRAGMENT";


                Intent intent = new Intent(CUSTOM_ACTION);

                // Send the broadcast
                Context context = getContext();
                if (context != null) {
                    context.sendBroadcast(intent);
                }


            }
        });





        showNativeAds(view);

        return view;
    }


    private void showNativeAds(View view) {


        SharedPreferences adsPref = activity.getSharedPreferences("ads", MODE_PRIVATE);
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

    public class GetUserBookmarks extends AsyncTask<Void, Void, BookmarkResponse> {

        private final int page;
        private final int itemsPerPage;

        String user_id;


        public GetUserBookmarks(int page, int itemsPerPage, String user_id) {
            this.page = page;
            this.itemsPerPage = itemsPerPage;
            this.user_id = user_id;
        }

        @Override
        protected BookmarkResponse doInBackground(Void... voids) {
            try {
                // Create the Retrofit instance
                ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

                // Make the API call
                Call<BookmarkResponse> call = apiInterface.getBookmark(
                        user_id, page, itemsPerPage
                );


                Log.d("ksxln", "page : " + page);
                Log.d("ksxln", "item per page : " + itemsPerPage);
                Log.d("ksxln", "rService id : " + user_id);

                Response<BookmarkResponse> response = call.execute();

                if (response.isSuccessful()) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            swipeRefreshLayout.setRefreshing(false);
                            shimmerFrameLayout.setVisibility(View.GONE);
                            shimmerFrameLayout.stopShimmer();

                        }
                    });


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
        protected void onPostExecute(BookmarkResponse apiResponse) {
            super.onPostExecute(apiResponse);


            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(false);
                    progressBar.setVisibility(View.GONE);

                    shimmerFrameLayout.setVisibility(View.GONE);
                    shimmerFrameLayout.stopShimmer();

                }
            });
            if (apiResponse != null) {
                // Handle the API response

                isLoading = false;

                if (currentPage == 1) {
                    // If it's the first page, clear the old list
                    bookmarks.clear();
                    adapter.notifyDataSetChanged();
                }

                List<Bookmark> newList = apiResponse.getBookmarks();

                if (newList != null && !newList.isEmpty()) {
                    // Add the incomingList to the reviews list after the loop


                    recyclerView.setVisibility(View.VISIBLE);
                    txtNoData.setVisibility(View.GONE);

                    bookmarks.addAll(newList);
                    adapter.setData(bookmarks);
                    adapter.notifyDataSetChanged();

                    if (newList.size() < itemsPerPage) {
                        isLastPage = true;
                    }
                } else if (currentPage > 1) {
                    // If no new data on a subsequent page, consider it the last page
                    isLastPage = true;
                } else {
                    recyclerView.setVisibility(View.GONE);
                    txtNoData.setVisibility(View.VISIBLE);

                }
            } else {

                Log.d("nalla", "SERVER ERROR 3 ");

            }
        }

    }

    public class GetBookmarkCount extends AsyncTask<Void, Void, BookmarkCountResponse> {

        String user_id;

        public GetBookmarkCount(String user_id) {
            this.user_id = user_id;
        }

        @Override
        protected BookmarkCountResponse doInBackground(Void... voids) {
            try {
                // Create the Retrofit instance
                ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

                // Make the API call
                Call<BookmarkCountResponse> call = apiInterface.getBookmarkCount(
                        user_id
                );

                Response<BookmarkCountResponse> response = call.execute();

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
        protected void onPostExecute(BookmarkCountResponse apiResponse) {
            super.onPostExecute(apiResponse);

            progressBar.setVisibility(View.GONE);


            if (apiResponse != null) {
                // Handle the API response

                if (apiResponse.isSuccess() && apiResponse.getMessage().equals("success")) {


                    bookmarkCountTxt.setText(String.valueOf(apiResponse.getTotal_bookmarks()));

                } else {

                    CustomToastNegative.create(activity," "+apiResponse.getMessage());

                }


            }
        }
    }

}