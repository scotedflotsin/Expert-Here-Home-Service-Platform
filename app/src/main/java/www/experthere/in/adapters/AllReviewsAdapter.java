package www.experthere.in.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import www.experthere.in.R;
import www.experthere.in.helper.TimeAgoConverter;
import www.experthere.in.model.AllReviews;
import www.experthere.in.model.Reviews;
import www.experthere.in.users.AddReviewActivity;

public class AllReviewsAdapter extends RecyclerView.Adapter<AllReviewsAdapter.ViewHolder> {

    private List<AllReviews> reviews;
    private final Activity activity;

    private final SharedPreferences preferences;



    public AllReviewsAdapter(List<AllReviews> reviews, Activity activity) {
        this.reviews = reviews;
        this.activity = activity;

        preferences = activity.getSharedPreferences("user", Context.MODE_PRIVATE);
    }


    public void setData(List<AllReviews> reviews) {
        this.reviews = reviews;
        notifyDataSetChanged();
    }

    public void addData(List<AllReviews> newreviews) {
        if (newreviews != null) {
            reviews.addAll(newreviews);
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public AllReviewsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_reviews_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AllReviewsAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        Glide.with(activity).load(reviews.get(position).getProfile_picture()).circleCrop().into(holder.userDp);

        holder.serviceTitle.setText(reviews.get(position).getService_title());
        holder.timeTxt.setText(reviews.get(position).getCreated_at());
        holder.reviewTxt.setText(reviews.get(position).getReview());
        holder.ratingTxt.setText(reviews.get(position).getStars());



    }







    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView serviceTitle, reviewTxt, timeTxt, ratingTxt;

        ImageView userDp;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            serviceTitle = itemView.findViewById(R.id.serviceTitle);
            timeTxt = itemView.findViewById(R.id.timeTxt);
            reviewTxt = itemView.findViewById(R.id.reviewTxt);
            ratingTxt = itemView.findViewById(R.id.ratingTxt);
            userDp = itemView.findViewById(R.id.imgUser);


        }
    }


}
