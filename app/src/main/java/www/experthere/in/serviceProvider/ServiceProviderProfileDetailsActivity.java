package www.experthere.in.serviceProvider;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
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
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import www.experthere.in.R;
import www.experthere.in.SplashScreen;
import www.experthere.in.api.ApiClient;
import www.experthere.in.api.ApiInterface;
import www.experthere.in.helper.AdManager;
import www.experthere.in.helper.CustomToastNegative;
import www.experthere.in.helper.CustomToastPositive;
import www.experthere.in.helper.ProcessingDialog;
import www.experthere.in.helper.TextMaskUtil;
import www.experthere.in.helper.app.Base64Util;
import www.experthere.in.model.AllReviewsStats;
import www.experthere.in.model.ReviewDetailsModel;
import www.experthere.in.model.Stats;
import www.experthere.in.model.SuccessMessageResponse;
import www.experthere.in.serviceProvider.profile_frgments.AboutProviderFragment;
import www.experthere.in.serviceProvider.profile_frgments.ReviewsProviderFragment;
import www.experthere.in.serviceProvider.profile_frgments.ServiciesProviderFragment;
import www.experthere.in.users.AddReviewActivity;
import www.experthere.in.users.HomeActivity;
import www.experthere.in.users.MoreReviewsActivity;

public class ServiceProviderProfileDetailsActivity extends AppCompatActivity {
    TabLayout tabLayout;

    String providerID, providerName, providerBannerUrl, providerLogoUrl, providerCompanyName, deeplink;
    private AppBarLayout appBarLayout;
    private Toolbar toolbar;
    private TextView toolbarTitle;
    ImageView logo, banner;
    TextView companyNameTxt, reviewersTxt, avgRatingsTxt;
    RatingBar ratingBar;

    Stats stats;

//    ProcessingDialog processingDialog;

    SharedPreferences preferences;

    boolean isBookmarkSaved;
    AdManager adManager;
    SharedPreferences adsPref, adsCountPref;
    SharedPreferences.Editor adsCountEditor;

    boolean adsStatus;
    String bannerId, interstitialID, appOpenId, nativeId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(getResources().getColor(R.color.white, this.getTheme()));

        setContentView(R.layout.activity_service_provider_profile_details);
        adsPref = getSharedPreferences("ads", MODE_PRIVATE);

        getAdsDetails();


        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        showNativeAds();


        adsCountPref = getSharedPreferences("count", MODE_PRIVATE);
        adsCountEditor = adsCountPref.edit();


        int adsCount = adsCountPref.getInt("count", 0);

        Log.d("slklxnl", "count: " + adsCount);

        if (adsCount <= 1) {
            //show ads

            adsCount++;
            adsCountEditor.putInt("count", adsCount).apply();


            if (adsStatus) {

                adManager = new AdManager(interstitialID);

                adManager.loadAd(ServiceProviderProfileDetailsActivity.this);
                // Schedule the ad to be shown after a delay
                new Handler().postDelayed(() -> {
                    if (adManager.isAdLoaded()) {
                        adManager.showAd(ServiceProviderProfileDetailsActivity.this);
                        Log.d("slklxnl", "Showing Ads.");


                    } else {
                        Log.d("slklxnl", "The interstitial ad wasn't ready yet.");
                    }
                }, 3000); // Adjust the delay as needed


            }


        } else {

            //dont show ads

            Log.d("slklxnl", "Ads Not Showing!.");


            if (adsCount >= 3) {
                adsCount = 0;
                adsCountEditor.putInt("count", adsCount).apply();

            } else {


                adsCount++;

                adsCountEditor.putInt("count", adsCount).apply();
            }

        }


//        processingDialog = new ProcessingDialog(this);
        preferences = getSharedPreferences("user", MODE_PRIVATE);

        initBundle();

        logo = findViewById(R.id.imageView5);
        banner = findViewById(R.id.imageView4);
        companyNameTxt = findViewById(R.id.textView2);


        ratingBar = findViewById(R.id.ratingBar);
        avgRatingsTxt = findViewById(R.id.ratingTxt);
        reviewersTxt = findViewById(R.id.reviewersTxt);

        tabLayout = findViewById(R.id.tabLayout);
        ViewPager viewPager = findViewById(R.id.viewPager);

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);


        appBarLayout = findViewById(R.id.appBarLayout);
        toolbar = findViewById(R.id.toolBar);
        toolbarTitle = findViewById(R.id.toolbarTitle);

        setSupportActionBar(toolbar);


        try {
            new GetReviewDetails(providerID).execute();
            new CheckBookmark(providerID, preferences.getString("id", "0")).execute();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        // Add the OnOffsetChangedListener to the AppBarLayout
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }

                // Calculate the scroll percentage
                int percentage = (Math.abs(verticalOffset) * 100) / scrollRange;

                // Change the visibility of the title based on the scroll percentage
                if (percentage >= 80) {
                    if (!isShow) {
                        isShow = true;
                        toolbarTitle.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (isShow) {
                        isShow = false;
                        toolbarTitle.setVisibility(View.GONE);
                    }
                }
            }
        });

        toolbarTitle.setText("Service Provder Name");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (deeplink.matches("1")) {

                    Intent intent = new Intent(ServiceProviderProfileDetailsActivity.this, SplashScreen.class);
                    startActivity(intent);
                    finish();


                } else {
                    finish();
                }

            }
        });


        Glide.with(getApplicationContext()).load(providerBannerUrl).into(banner);
        Glide.with(getApplicationContext()).load(providerLogoUrl).circleCrop().into(logo);
        TextMaskUtil.maskText(toolbarTitle, providerCompanyName, 30, "..");
        TextMaskUtil.maskText(companyNameTxt, providerCompanyName, 30, "..");


    }

    private void getAdsDetails() {


        interstitialID = adsPref.getString("interstitial", "ca-app-pub-3940256099942544/1033173712");
        bannerId = adsPref.getString("banner", "ca-app-pub-3940256099942544/9214589741");
        nativeId = adsPref.getString("native", "ca-app-pub-3940256099942544/2247696110");
        appOpenId = adsPref.getString("app_open", "ca-app-pub-3940256099942544/9257395921");

        adsStatus = adsPref.getBoolean("status", false);


    }

    private void initBundle() {

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            providerID = bundle.getString("provider_id", "0");
            providerName = bundle.getString("provider_name", "0");
            providerBannerUrl = bundle.getString("provider_banner", "0");
            providerLogoUrl = bundle.getString("provider_logo", "0");
            providerCompanyName = bundle.getString("provider_company_name", "0");
            deeplink = bundle.getString("deeplink", "0");


        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.colleps_menu, menu);

        return true;

    }

    // Method to change the menu item icon
    private void changeMenuItemIcon(int menuItemId, Drawable newIcon) {
        MenuItem menuItem = toolbar.getMenu().findItem(menuItemId);
        if (menuItem != null) {
            menuItem.setIcon(newIcon);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.shareMenu) {


            String provider_id = Base64Util.encode(providerID);

            String sharingText = "''" + getString(R.string.app_name) + ": The Service Platform''\nCheck Out " + providerName + " !" +
                    "\nCompany Name: " + providerCompanyName + "\nRatings: " + avgRatingsTxt.getText().toString() + "★ .\nClick On Link: " + "https://experthere.in/provider_profile.php?EHpd=" + provider_id;


            shareImageFromUrl(ServiceProviderProfileDetailsActivity.this, providerLogoUrl, sharingText);


        }
        if (item.getItemId() == R.id.bookmarkMenu) {


            try {

//                processingDialog.show();


                String CUSTOM_ACTION = "www.experthere.in.users.BOOKMARK_UPDATE_USER";


                Intent intent = new Intent(CUSTOM_ACTION);

                // Send the broadcast
                Context context = getApplicationContext();
                if (context != null) {
                    context.sendBroadcast(intent);
                }


                if (isBookmarkSaved) {

                    new DeleteBookmark(preferences.getString("id", "0"), providerID).execute();

                } else {
                    new Add_New_Bookmark(providerID, preferences.getString("id", "0")).execute();
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }


        }


        return super.onOptionsItemSelected(item);


    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ServiciesProviderFragment(), "Services");
        adapter.addFragment(new ReviewsProviderFragment(), "Reviews");
        adapter.addFragment(new AboutProviderFragment(), "About");

        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(2);


    }

    private static class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> fragments = new ArrayList<>();
        private final List<String> fragmentTitles = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        public void addFragment(Fragment fragment, String title) {
            fragments.add(fragment);
            fragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // Return the title in lowercase
            return fragmentTitles.get(position);
        }
    }


    private class GetReviewDetails extends AsyncTask<Void, Void, AllReviewsStats> {


        private final String providerID;


        public GetReviewDetails(String providerID) {
            this.providerID = providerID;
        }

        @Override
        protected AllReviewsStats doInBackground(Void... params) {
            // The doInBackground method remains the same as in the previous example

            try {

                // Create ReviewService instance
                ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);


                // Make the API call
                Call<AllReviewsStats> call = apiInterface.getAllReviewStats(providerID);

                // Execute the call synchronously
                Response<AllReviewsStats> response = call.execute();

                // Check if the response is successful
                if (response.isSuccessful() && response.body() != null) {

                    return response.body();


                } else {
                    return null;
                }
            } catch (IOException e) {
                Log.e("SubmitReviewTask", "Error making API call", e);
                return null;
            }

        }

        @Override
        protected void onPostExecute(AllReviewsStats apiResponse) {
            // The onPostExecute method remains the same as in the previous example


            if (apiResponse != null && apiResponse.isSuccess()) {
                // API call successful

                stats = apiResponse.getStats();

                if (stats != null) {

//                    String progress = apiResponse.getStats().getAverage_rating();

//                    ratingBar.setProgress(Integer.parseInt(progress));
//                    avgRatingsTxt.setText(String.valueOf(progress));
//                    reviewersTxt.setText("("+apiResponse.getStats().getTotal_reviews()+") Reviewers");


                    Log.d("STATSGDGS", "onPostExecute: " + stats.getAverage_rating());
                    Log.d("STATSGDGS", "onPostExecute: " + stats.getTotal_reviews());
                    Log.d("STATSGDGS", "onPostExecute: " + stats.getOne_star());
                    Log.d("STATSGDGS", "onPostExecute: " + stats.getTwo_star());
                    Log.d("STATSGDGS", "onPostExecute: " + stats.getThree_star());
                    Log.d("STATSGDGS", "onPostExecute: " + stats.getFour_star());
                    Log.d("STATSGDGS", "onPostExecute: " + stats.getFive_star());


                    String v = String.format("%.1f", stats.getAverage_rating());


                    ratingBar.setRating(stats.getAverage_rating());
                    avgRatingsTxt.setText(v);
                    reviewersTxt.setText("(" + apiResponse.getStats().getTotal_reviews() + ") Reviewers");


                } else {

                    Log.d("SERPROPRODET", "Error 1 ");


                }
            } else {

                // API call failed
                Log.d("SERPROPRODET", "Error 2 ");

            }


        }
    }

    private class CheckBookmark extends AsyncTask<Void, Void, SuccessMessageResponse> {


        private final String providerID, userId;


        public CheckBookmark(String providerID, String userId) {
            this.providerID = providerID;
            this.userId = userId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            if (processingDialog!=null && !processingDialog.isShowing())
//            {
//                processingDialog.show();
//            }
        }

        @Override
        protected SuccessMessageResponse doInBackground(Void... params) {
            // The doInBackground method remains the same as in the previous example

            try {

                // Create ReviewService instance
                ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);


                // Make the API call
                Call<SuccessMessageResponse> call = apiInterface.checkBookmarkAvailable(userId, providerID);

                // Execute the call synchronously
                Response<SuccessMessageResponse> response = call.execute();

                // Check if the response is successful
                if (response.isSuccessful() && response.body() != null) {

                    return response.body();


                } else {
                    return null;
                }
            } catch (IOException e) {
                Log.e("SubmitReviewTask", "Error making API call", e);
                return null;
            }

        }

        @Override
        protected void onPostExecute(SuccessMessageResponse apiResponse) {
            // The onPostExecute method remains the same as in the previous example

//            processingDialog.dismiss();

            if (apiResponse != null && apiResponse.isSuccess()) {
                // API call successful

                if (apiResponse.getMessage().equals("available")) {


                    Drawable newIcon = ContextCompat.getDrawable(ServiceProviderProfileDetailsActivity.this, R.drawable.bookmark_colleps);
                    changeMenuItemIcon(R.id.bookmarkMenu, newIcon);


                    isBookmarkSaved = true;

                }
                if (apiResponse.getMessage().equals("not available")) {

                    Drawable newIcon = ContextCompat.getDrawable(ServiceProviderProfileDetailsActivity.this, R.drawable.bookmark_colleps_border);
                    changeMenuItemIcon(R.id.bookmarkMenu, newIcon);

                    isBookmarkSaved = false;
                }


            } else {

                // API call failed
                Log.d("SERPROPRODET", "Error 2 ");

            }


        }
    }

    private class Add_New_Bookmark extends AsyncTask<Void, Void, SuccessMessageResponse> {


        private final String providerID;
        private final String userID;

        public Add_New_Bookmark(String providerID, String userID) {
            this.providerID = providerID;
            this.userID = userID;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected SuccessMessageResponse doInBackground(Void... params) {
            // The doInBackground method remains the same as in the previous example

            try {

                // Create ReviewService instance
                ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);


                // Make the API call
                Call<SuccessMessageResponse> call = apiInterface.addBookmark(userID, providerID);

                // Execute the call synchronously
                Response<SuccessMessageResponse> response = call.execute();

                // Check if the response is successful
                if (response.body() != null && response.body().isSuccess()) {

                    return response.body();


                } else {
                    return null;
                }
            } catch (IOException e) {
                Log.e("SubmitReviewTask", "Error making API call", e);
                return null;
            }

        }

        @Override
        protected void onPostExecute(SuccessMessageResponse apiResponse) {
            // The onPostExecute method remains the same as in the previous example

//            processingDialog.dismiss();

            if (apiResponse != null && apiResponse.isSuccess()) {
                // API call successful

                CustomToastPositive.create(getApplicationContext(), " " + apiResponse.getMessage());


                if (apiResponse.getMessage().equals("Bookmark saved successfully")) {

                    Drawable newIcon = ContextCompat.getDrawable(ServiceProviderProfileDetailsActivity.this, R.drawable.bookmark_colleps);
                    changeMenuItemIcon(R.id.bookmarkMenu, newIcon);

                    isBookmarkSaved = true;
                }


            } else {

                CustomToastNegative.create(getApplicationContext(), "Error Saving Bookmark");

            }
        }
    }


    private class DeleteBookmark extends AsyncTask<Void, Void, SuccessMessageResponse> {


        String userID, providerID;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        public DeleteBookmark(String userID, String providerID) {
            this.userID = userID;
            this.providerID = providerID;
        }

        @Override
        protected SuccessMessageResponse doInBackground(Void... voids) {
            // Create Retrofit instance
            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);


            // Make API call
            Call<SuccessMessageResponse> call = apiInterface.deleteBookmarkWithUserIdProviderId(userID, providerID);

            try {
                // Execute the call synchronously
                Response<SuccessMessageResponse> response = call.execute();

                if (response.isSuccessful()) {
                    return response.body();
                } else {
                    // Handle unsuccessful response

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            processingDialog.dismiss();

                        }
                    });

                    return null;
                }
            } catch (IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        processingDialog.dismiss();

                    }
                });

                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(SuccessMessageResponse apiResponse) {
            super.onPostExecute(apiResponse);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    processingDialog.dismiss();

                }
            });

            if (apiResponse != null) {
                // Handle the API response

                if (apiResponse.isSuccess() && apiResponse.getMessage().equals("Bookmark deleted successfully.")) {


                    CustomToastNegative.create(getApplicationContext(), "Bookmark Removed Successfully!");


                    isBookmarkSaved = false;

                    Drawable newIcon = ContextCompat.getDrawable(ServiceProviderProfileDetailsActivity.this, R.drawable.bookmark_colleps_border);
                    changeMenuItemIcon(R.id.bookmarkMenu, newIcon);


                } else {

                    CustomToastNegative.create(getApplicationContext(), "Error: " + apiResponse.getMessage());

                }

            } else {
            }


        }
    }

    private void shareImageFromUrl(Context context, String imageUrl, String text) {
        Glide.with(context)
                .asBitmap()
                .load(imageUrl)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        try {
                            // Save the image to cache directory
                            File cachePath = new File(context.getExternalFilesDir("Pictures"), "shared_images");
                            cachePath.mkdirs(); // Create the directory if it doesn't exist
                            File imageFile = new File(cachePath, "image.png");
                            FileOutputStream stream = new FileOutputStream(imageFile);
                            resource.compress(Bitmap.CompressFormat.PNG, 100, stream);
                            stream.close();

                            // Get content URI using FileProvider
                            Uri contentUri = FileProvider.getUriForFile(context, "www.experthere.in.fileprovider", imageFile);

                            if (contentUri != null) {
                                Intent shareIntent = new Intent();
                                shareIntent.setAction(Intent.ACTION_SEND);
                                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                shareIntent.setType("image/png");
                                shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                                shareIntent.putExtra(Intent.EXTRA_TEXT, text);

                                context.startActivity(Intent.createChooser(shareIntent, "Share via"));
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        // This can be left empty
                    }
                });
    }


    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {

        if (deeplink.matches("1")) {

            Intent intent = new Intent(ServiceProviderProfileDetailsActivity.this, SplashScreen.class);
            startActivity(intent);
            finish();


        } else {
            finish();
        }

    }


    private void showNativeAds() {


//        SharedPreferences adsPref = getSharedPreferences("ads",MODE_PRIVATE);

        String nativeId = adsPref.getString("native", "ca-app-pub-3940256099942544/2247696110");


        if (adsPref.getBoolean("status", false)){




            int backgroundColor = ContextCompat.getColor(ServiceProviderProfileDetailsActivity.this, R.color.main_screen_bg_color);
            ColorDrawable background = new ColorDrawable(backgroundColor);


            AdLoader adLoader = new AdLoader.Builder(ServiceProviderProfileDetailsActivity.this, nativeId)
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



}