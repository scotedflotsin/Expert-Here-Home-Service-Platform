package www.experthere.in.adapters.serviceListUser;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import www.experthere.in.R;
import www.experthere.in.model.Reviews;
import www.experthere.in.helper.ImageListDialogFragment;

public class ReviewsAdapterImages extends RecyclerView.Adapter<ReviewsAdapterImages.ViewHolder> {

    private List<Reviews> reviews;
    private final Activity activity;

    public ReviewsAdapterImages(List<Reviews> reviews, Activity activity) {
        this.reviews = reviews;
        this.activity = activity;
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
    public ReviewsAdapterImages.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_image_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewsAdapterImages.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        Glide.with(activity).load(reviews.get(position).getReview_image_url()).into(holder.reviewImage);

        holder.reviewImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageListDialogFragment dialogFragment = new ImageListDialogFragment(position,Integer.parseInt(reviews.get(position).getService_id()));
                dialogFragment.show(((AppCompatActivity) activity).getSupportFragmentManager()
                        , "ImageListDialogFragment");
            }
        });


    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView reviewImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            reviewImage = itemView.findViewById(R.id.reviewImage);


        }
    }

}
