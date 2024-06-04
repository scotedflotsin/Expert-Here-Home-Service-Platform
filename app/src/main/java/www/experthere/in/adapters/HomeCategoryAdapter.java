package www.experthere.in.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import www.experthere.in.R;
import www.experthere.in.model.Category;
import www.experthere.in.model.Subcategory;
import www.experthere.in.users.ServiceForCategoryActivity;

public class HomeCategoryAdapter extends RecyclerView.Adapter<HomeCategoryAdapter.ViewHolder> {

    private final ArrayList<Category> categories;
    private final Activity activity;

    private final int selectedPosition = RecyclerView.NO_POSITION;

    public HomeCategoryAdapter(ArrayList<Category> categories, Activity activity) {
        this.categories = categories;
        this.activity = activity;
    }

    @NonNull
    @Override
    public HomeCategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_row_home, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeCategoryAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.catTxt.setText(categories.get(position).getCategoryName());
        Glide.with(activity).load(categories.get(position).getCategoryImage()).placeholder(R.drawable.app_icon).into(holder.catImg);

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                Intent intent = new Intent(activity, ServiceForCategoryActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString("cat_name",categories.get(position).getCategoryName());
                bundle.putString("cat_id",categories.get(position).getCategoryId());

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtras(bundle);
                activity.startActivity(intent);


            }
        });




//        // Check if the current position is the selected position and change text color accordingly
//        if (position == selectedPosition) {
//            holder.catTxt.setTextColor(activity.getResources().getColor(R.color.theme_Blue));
//            holder.bottomBar.setVisibility(View.VISIBLE);
//        } else {
//            holder.catTxt.setTextColor(activity.getResources().getColor(R.color.text_color));
//            holder.bottomBar.setVisibility(View.GONE);
//        }






    }


    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView catImg;
        TextView catTxt;
        LinearLayout layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            catImg = itemView.findViewById(R.id.catImg);
            catTxt = itemView.findViewById(R.id.catTxt);
            layout = itemView.findViewById(R.id.cat_click_lay);
        }
    }
}
