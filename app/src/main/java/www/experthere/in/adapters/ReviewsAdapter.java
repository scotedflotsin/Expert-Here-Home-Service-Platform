package www.experthere.in.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;
import www.experthere.in.R;
import www.experthere.in.adapters.serviceListUser.ReviewsAdapterImages;
import www.experthere.in.api.ApiClient;
import www.experthere.in.api.ApiInterface;
import www.experthere.in.helper.ServiceDetailsBottomSheet;
import www.experthere.in.helper.TimeAgoConverter;
import www.experthere.in.model.Category;
import www.experthere.in.model.ReviewDetailsModel;
import www.experthere.in.model.Reviews;
import www.experthere.in.model.Subcategory;
import www.experthere.in.model.SuccessMessageResponse;
import www.experthere.in.model.service_user_cat.Service;
import www.experthere.in.users.AddReviewActivity;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {

    private List<Reviews> reviews;
    private final Activity activity;

    private final SharedPreferences preferences;
    String title;



    public ReviewsAdapter(List<Reviews> reviews, Activity activity,String title) {
        this.reviews = reviews;
        this.activity = activity;
        this.title=title;

        preferences = activity.getSharedPreferences("user", Context.MODE_PRIVATE);
    }


    public void setData(List<Reviews> reviews) {
        this.reviews = reviews;
        notifyDataSetChanged();
    }

    public void addData(List<Reviews> newreviews) {
        if (newreviews != null) {
            reviews.addAll(newreviews);
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public ReviewsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_row_full, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewsAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.userName.setText(reviews.get(position).getUser_name());
        holder.reviewTxt.setText(reviews.get(position).getReview());
        Glide.with(activity).load(reviews.get(position).getProfile_picture()).circleCrop().into(holder.userDp);
        holder.ratingBar.setRating(Float.parseFloat(reviews.get(position).getStars()));
        holder.ratingTxt.setText(reviews.get(position).getStars() + ".0");
        try {
            holder.time.setText(TimeAgoConverter.getTimeAgo(reviews.get(position).getCreated_at()));
        } catch (Exception e) {
            throw new RuntimeException(e);

        }

        if (reviews.get(position).getUser_id().equals(preferences.getString("id","0"))){

            holder.menuReviewBtn.setVisibility(View.VISIBLE);




        }else {
            holder.menuReviewBtn.setVisibility(View.GONE);

        }

        holder.menuReviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                showPopUp(view,position);

            }
        });

        // Get the layout manager from the RecyclerView





    }



    private void showPopUp(View anchorView,int position) {
        // Inflate the custom layout
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.review_popup_layout, null);

        // Create a PopupWindow with your custom layout
        PopupWindow popupWindow = new PopupWindow(
                customView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        // Set focusable to true so that the popup can handle touch events
        popupWindow.setFocusable(true);

        // Example: Handle button click inside the custom layout
        LinearLayout edit = customView.findViewById(R.id.editBtn);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close the popup window

                    Intent intent = new Intent(activity, AddReviewActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("provider_id", String.valueOf(reviews.get(position).getProvider_id()));
                    bundle.putString("service_id", String.valueOf(reviews.get(position).getService_id()));
                    bundle.putString("user_id", reviews.get(position).getUser_id());
                    bundle.putString("review_id", reviews.get(position).getReview_id());
                    bundle.putString("title", title);
                    bundle.putBoolean("isEdit", true);

                    intent.putExtras(bundle);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.startActivity(intent);




                popupWindow.dismiss();




            }
        });


        // Show the popup menu anchored to the anchor view (e.g., your button with three dots)
        popupWindow.showAsDropDown(anchorView);
    }


    public void moveReviewToTop(int position) {
        Reviews userReview = reviews.get(position);
        reviews.remove(position);
        reviews.add(0, userReview);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView userName, reviewTxt, time, ratingTxt;
        RatingBar ratingBar;
        ImageView userDp,menuReviewBtn;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ratingBar = itemView.findViewById(R.id.ratingBar);
            userName = itemView.findViewById(R.id.userName);
            reviewTxt = itemView.findViewById(R.id.reviewTxt);
            time = itemView.findViewById(R.id.timeTxt);
            ratingTxt = itemView.findViewById(R.id.ratingTxt);
            userDp = itemView.findViewById(R.id.companyLogo);
            menuReviewBtn = itemView.findViewById(R.id.menuReviewBtn);


        }
    }


}
