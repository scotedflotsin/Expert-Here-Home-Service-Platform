package www.experthere.in.users.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
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

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import www.experthere.in.Common.GetShimmerStrings;
import www.experthere.in.R;
import www.experthere.in.adapters.UserCategoryAdapter;
import www.experthere.in.adapters.shimmer.MyShimmerAdapter;
import www.experthere.in.adapters.shimmer.NonScrollableLayoutManager;
import www.experthere.in.api.ApiClient;
import www.experthere.in.api.ApiInterface;
import www.experthere.in.helper.CustomToastNegative;
import www.experthere.in.model.Category;
import www.experthere.in.model.CategoryResponse;


public class UserCatFragment extends Fragment {

    ArrayList<Category> categories;
    Activity activity;

    RecyclerView recyclerView, subRecycler;
    LinearLayout progressBar;

    TextView subCatTitle;

    SwipeRefreshLayout swipeRefreshLayout;

    UserCategoryAdapter adapter;
    ShimmerFrameLayout shimmerFrameLayout, shimmerFrameLayoutSubCat;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = requireActivity();
        categories = new ArrayList<>();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View view = inflater.inflate(R.layout.fragment_user_cat, container, false);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        recyclerView = view.findViewById(R.id.recycler_cat);
        subRecycler = view.findViewById(R.id.subCatRecycler);
        subCatTitle = view.findViewById(R.id.subCatTitle);
        progressBar = view.findViewById(R.id.progCat);
        shimmerFrameLayout = view.findViewById(R.id.shimmer_cat);
        shimmerFrameLayoutSubCat = view.findViewById(R.id.subCatShimmer);


        inflateShimmerRecyclerView(view, GetShimmerStrings.USER_HOME_CAT);
        shimmerFrameLayout.startShimmer();

        inflateShimmerRecyclerViewSubCat(view, GetShimmerStrings.USER_SUBCAT);
        shimmerFrameLayoutSubCat.startShimmer();

        try {
            new GetCategories(activity).execute();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {


                try {


                    shimmerFrameLayout.startShimmer();
                    shimmerFrameLayout.setVisibility(View.VISIBLE);

                    shimmerFrameLayoutSubCat.startShimmer();
                    shimmerFrameLayoutSubCat.setVisibility(View.VISIBLE);

                    recyclerView.setVisibility(View.GONE);
                    subRecycler.setVisibility(View.GONE);

                    new GetCategories(activity).execute();

                } catch (Exception e) {
                    throw new RuntimeException(e);
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


    private void inflateShimmerRecyclerViewSubCat(View view, String types) {

        RecyclerView shimmerRecycler = view.findViewById(R.id.shimmerRecyclerSub);

        MyShimmerAdapter shimmerAdapter = new MyShimmerAdapter(types);
        NonScrollableLayoutManager layoutManager = new NonScrollableLayoutManager(activity);
        shimmerRecycler.setLayoutManager(layoutManager);
        shimmerRecycler.setAdapter(shimmerAdapter);
    }

    private void inflateShimmerRecyclerView(View view, String types) {

        RecyclerView shimmerRecycler = view.findViewById(R.id.recyclerShimmer);

        MyShimmerAdapter shimmerAdapter = new MyShimmerAdapter(types);
        NonScrollableLayoutManager layoutManager = new NonScrollableLayoutManager(activity);
        shimmerRecycler.setLayoutManager(layoutManager);
        shimmerRecycler.setAdapter(shimmerAdapter);


    }


    private class GetCategories extends AsyncTask<Void, Void, Void> {

        Activity activity;

        public GetCategories(Activity activity) {
            this.activity = activity;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            // Perform background operation, e.g., user


            categories.clear();

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);

                    shimmerFrameLayoutSubCat.stopShimmer();
                    shimmerFrameLayoutSubCat.setVisibility(View.GONE);


                }
            });


            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

            Call<CategoryResponse> call = apiInterface.getCategories();


            call.enqueue(new Callback<CategoryResponse>() {
                @Override
                public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {

                    if (response.isSuccessful() && response.body() != null) {

                        CategoryResponse categoryResponse = response.body();

                        if (categoryResponse.isSuccess()) {

                            categories.addAll(categoryResponse.getCategories());
                            adapter = new UserCategoryAdapter(categories, activity, subRecycler, subCatTitle);
                            recyclerView.setAdapter(adapter);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);
                            recyclerView.setLayoutManager(linearLayoutManager);

                            adapter.setSelectedPosition(0);
                            swipeRefreshLayout.setRefreshing(false);


                            recyclerView.setVisibility(View.VISIBLE);
                            subRecycler.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);


                        } else {
                            activity.runOnUiThread(() -> {

                                CustomToastNegative.create(activity,"Error Getting Categories!");

                                progressBar.setVisibility(View.GONE);
                                swipeRefreshLayout.setRefreshing(false);
                            });


                        }


                    } else {

                        activity.runOnUiThread(() -> {

                            CustomToastNegative.create(activity," Category Response Fail");

                            progressBar.setVisibility(View.GONE);
                            swipeRefreshLayout.setRefreshing(false);


                        });

                    }

                }

                @Override
                public void onFailure(Call<CategoryResponse> call, Throwable t) {

                    activity.runOnUiThread(() -> {

                        CustomToastNegative.create(activity,"Category Error: "+t.getMessage());

                        progressBar.setVisibility(View.GONE);
                        swipeRefreshLayout.setRefreshing(false);


                    });

                }
            });


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // This method runs on the UI thread and can be used to update the UI after background task completion
        }
    }

}