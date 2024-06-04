package www.experthere.in.helper;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import www.experthere.in.R;
import www.experthere.in.adapters.serviceListUser.ReviewsAdapterImagesWithView;
import www.experthere.in.api.ApiClient;
import www.experthere.in.api.ApiInterface;
import www.experthere.in.model.ReviewResponse;
import www.experthere.in.model.Reviews;

// ImageListDialogFragment.java
public class ImageListDialogFragment extends DialogFragment {


    private List<Reviews> reviews;
    private Activity activity;

    private ReviewsAdapterImagesWithView adapter;

    ProgressBar progressBar;
    private boolean isLoadingImage = false;
    private boolean isLastPageImage = false;
    private int currentPageImage = 1;
    private final int itemsPerPageImage = 10;
    RecyclerView recyclerViewImages;
    List<Reviews> reviewsImages = new ArrayList<>();

    ImageView showImg;
    int serviceId;
    int initialPosition;

    public ImageListDialogFragment(  int initialPosition,int serviceId) {
        this.serviceId = serviceId;
        this.initialPosition = initialPosition;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_fullscreen_images, container, false);
        activity = requireActivity();

        progressBar = rootView.findViewById(R.id.progress_images_view);
        showImg = rootView.findViewById(R.id.mainImageViewFullScreen);

        recyclerViewImages = rootView.findViewById(R.id.miniImageRecyclerViewFullScreen);
        adapter = new ReviewsAdapterImagesWithView(reviewsImages, activity, initialPosition,new ReviewsAdapterImagesWithView.ImageDataListener() {
            @Override
            public void getImageData(Reviews imageReviewsData) {

                Glide.with(activity).load(imageReviewsData.getReview_image_url()).into(showImg);


            }
        });
        // Set up your RecyclerView adapter and layout manager here
        recyclerViewImages.setAdapter(adapter);
        recyclerViewImages.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        recyclerViewImages.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if (!isLoadingImage && !isLastPageImage) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0
                            && totalItemCount >= itemsPerPageImage) {
                        // Load more data when reaching the end

                        isLoadingImage = true;
                        currentPageImage++;
                        progressBar.setVisibility(View.VISIBLE);


                        try {

                            new GetReviewsImagesTask(currentPageImage, itemsPerPageImage, serviceId).execute();

                        } catch (Exception e) {
                            CustomToastNegative.create(activity,"Error: "+e.getMessage() );

                        }
                    }
                }
            }
        });



        try {

            new GetReviewsImagesTask(currentPageImage, itemsPerPageImage, serviceId).execute();

        } catch (Exception e) {

            CustomToastNegative.create(activity,"Error: "+e.getMessage() );

        }



        rootView.findViewById(R.id.cancelBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();

            }
        });




        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {
            // Set dialog window attributes for transparency
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    public class GetReviewsImagesTask extends AsyncTask<Void, Void, ReviewResponse> {

        private final int page;
        private final int itemsPerPage;
        int serviceId;

        public GetReviewsImagesTask(int page, int itemsPerPage, int serviceId) {
            this.page = page;
            this.itemsPerPage = itemsPerPage;
            this.serviceId = serviceId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);

        }

        @Override
        protected ReviewResponse doInBackground(Void... voids) {
            try {
                // Create the Retrofit instance
                ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

                // Make the API call
                Call<ReviewResponse> call = apiInterface.getReviews(
                        serviceId,
                        page, // page
                        itemsPerPage // itemsPerPage
                        , Integer.parseInt(activity.getSharedPreferences("user",MODE_PRIVATE).getString("id","0"))

                );


                Log.d("ksxln", "page : " + page);
                Log.d("ksxln", "item per page : " + itemsPerPage);
                Log.d("ksxln", "rService id : " + serviceId);

                Response<ReviewResponse> response = call.execute();

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
        protected void onPostExecute(ReviewResponse apiResponse) {
            super.onPostExecute(apiResponse);

            progressBar.setVisibility(View.GONE);
            recyclerViewImages.setVisibility(View.VISIBLE);

            if (apiResponse != null) {
                // Handle the API response

                isLoadingImage = false;


                if (currentPageImage == 1) {
                    // If it's the first page, clear the old list
                    reviewsImages.clear();
                    adapter.notifyDataSetChanged();
                }


                List<Reviews> newList = apiResponse.getReviews();


                if (newList != null && !newList.isEmpty()) {

                    


                    reviewsImages.addAll(newList);
                    adapter.setData(reviewsImages);
                    // Move notifyDataSetChanged outside the loop
                    adapter.notifyDataSetChanged();

                    if (newList.size() < itemsPerPage) {
                        isLastPageImage = true;
                    }
                } else if (currentPageImage > 1) {
                    // If no new data on a subsequent page, consider it the last page
                    isLastPageImage = true;
                }


            }


            // Notify the adapter outside the if condition
//                adapter.notifyDataSetChanged();
        }

    }


}
