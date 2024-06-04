package www.experthere.in.adapters;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;

import java.util.List;

import www.experthere.in.R;
import www.experthere.in.model.SliderData;
import www.experthere.in.users.ServiceForCategoryActivity;
import www.experthere.in.users.ServicesByCategoryActivity;

public class ImageSliderAdapter extends PagerAdapter {

    private final Context context;
    private final List<SliderData> imageUrls;
    private final LayoutInflater inflater;
    private final LinearLayout dotsLayout;
    private final ViewPager viewPager;
    private final Handler autoScrollHandler;
    private final int AUTO_SCROLL_DELAY = 3000; // Adjust the delay as needed

    public ImageSliderAdapter(Context context, List<SliderData> imageUrls, LinearLayout dotsLayout, ViewPager viewPager) {
        this.context = context;
        this.imageUrls = imageUrls;
        this.dotsLayout = dotsLayout;
        this.viewPager = viewPager;

        inflater = LayoutInflater.from(context);
        autoScrollHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public int getCount() {
        // Return a large value to enable infinite scrolling
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = inflater.inflate(R.layout.slider_item, container, false);
        ImageView imageView = view.findViewById(R.id.imageView);

        // Calculate the actual position based on the list size
        int actualPosition = position % imageUrls.size();

        // Load image using Glide
        Glide.with(context)
                .load(imageUrls.get(actualPosition).getImage_url())
                .placeholder(R.drawable.app_icon)
                .into(imageView);

        container.addView(view);





        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



//                Toast.makeText(context, "Image "+imageUrls.get(actualPosition).getData()+" Selected!", Toast.LENGTH_SHORT).show();

                if (imageUrls.get(actualPosition).getData().matches("External Link")){

                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(imageUrls.get(actualPosition).getUrl()));
                    context.startActivity(intent);


                }
                if (imageUrls.get(actualPosition).getData().matches("Category")){

                    String catID = imageUrls.get(actualPosition).getCatId();


                    Intent intent = new Intent(context, ServiceForCategoryActivity.class);

                    Bundle bundle = new Bundle();
                    bundle.putString("cat_name","Expert Here Services");
                    bundle.putString("cat_id",catID);

                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtras(bundle);
                    context.startActivity(intent);

                }
                if (imageUrls.get(actualPosition).getData().matches("Subcategory")){

                    String catID = imageUrls.get(actualPosition).getCatId();
                    String subCatID = imageUrls.get(actualPosition).getSubCatId();




                    Bundle bundle = new Bundle();
                    bundle.putString("catId",catID);
                    bundle.putString("subCatId",subCatID);
                    bundle.putString("title","Expert Here Services");


                    Intent intent = new Intent(context, ServicesByCategoryActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtras(bundle);
                    context.startActivity(intent);

                }


                Log.d("TESTSLIDEDATA", "data "+imageUrls.get(actualPosition).getId());
                Log.d("TESTSLIDEDATA", "data "+imageUrls.get(actualPosition).getData());
                Log.d("TESTSLIDEDATA", "data "+imageUrls.get(actualPosition).getUrl());
                Log.d("TESTSLIDEDATA", "data "+imageUrls.get(actualPosition).getImage_url());
                Log.d("TESTSLIDEDATA", "data "+imageUrls.get(actualPosition).getCatId());
                Log.d("TESTSLIDEDATA", "data "+imageUrls.get(actualPosition).getSubCatId());

            }
        });



        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    // Method to add dots dynamically
    public void updateDots(int currentPage) {
        if (dotsLayout != null) {
            int actualPosition = currentPage % imageUrls.size();
            ImageView[] dots = new ImageView[imageUrls.size()];

            dotsLayout.removeAllViews(); // Clear existing dots before adding new ones

            for (int i = 0; i < dots.length; i++) {
                dots[i] = new ImageView(context);
                dots[i].setImageResource(i == actualPosition ? R.drawable.dot_active : R.drawable.dot_grey);
                dots[i].setPadding(8, 0, 8, 0); // Adjust padding as needed

                dotsLayout.addView(dots[i]);
            }
        }
    }

    // Start auto-scrolling
    public void startAutoScroll() {
        autoScrollHandler.postDelayed(autoScrollRunnable, AUTO_SCROLL_DELAY);
    }

    // Stop auto-scrolling
    public void stopAutoScroll() {
        autoScrollHandler.removeCallbacks(autoScrollRunnable);
    }

    // Runnable for auto-scrolling
    private final Runnable autoScrollRunnable = new Runnable() {
        @Override
        public void run() {
            int currentItem = viewPager.getCurrentItem();
            viewPager.setCurrentItem(currentItem + 1, true);
            autoScrollHandler.postDelayed(this, AUTO_SCROLL_DELAY);
        }
    };
}
