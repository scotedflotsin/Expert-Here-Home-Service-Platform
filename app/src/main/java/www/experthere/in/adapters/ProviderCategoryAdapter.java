package www.experthere.in.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import www.experthere.in.R;
import www.experthere.in.model.Category;

public class ProviderCategoryAdapter extends RecyclerView.Adapter<ProviderCategoryAdapter.ViewHolder> {

    private final ArrayList<Category> categories;
    private final Activity activity;


    public ProviderCategoryAdapter(ArrayList<Category> categories, Activity activity) {
        this.categories = categories;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ProviderCategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cat_list_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProviderCategoryAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {


        holder.titleTxt.setText(categories.get(position).getCategoryName());
        Glide.with(activity).load(categories.get(position).getCategoryImage()).placeholder(R.drawable.app_icon).into(holder.catImg);


    }


    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView catImg;
        TextView titleTxt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            catImg = itemView.findViewById(R.id.serviceImage);
            titleTxt = itemView.findViewById(R.id.titleTxt);

        }
    }
}
