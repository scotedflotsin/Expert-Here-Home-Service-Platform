package www.experthere.in.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import www.experthere.in.R;
import www.experthere.in.model.Category;
import www.experthere.in.model.Subcategory;

public class UserCategoryAdapter extends RecyclerView.Adapter<UserCategoryAdapter.ViewHolder> {

    private final ArrayList<Category> categories;
    private final Activity activity;
    private final RecyclerView subRecyclerView;
    private final TextView subCatTitle;

    private int selectedPosition = RecyclerView.NO_POSITION;

    // Method to set the selected position
    public void setSelectedPosition(int position) {


        subCatTitle.setText(categories.get(position).getCategoryName());

        setSubCatList(position, categories.get(position).getCategoryId());

        selectedPosition = position;
        notifyDataSetChanged(); // Notify adapter about the change




    }

    public UserCategoryAdapter(ArrayList<Category> categories, Activity activity, RecyclerView subRecyclerView, TextView subCatTitle) {
        this.categories = categories;
        this.activity = activity;
        this.subRecyclerView = subRecyclerView;
        this.subCatTitle = subCatTitle;



    }

    @NonNull
    @Override
    public UserCategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserCategoryAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.catTxt.setText(categories.get(position).getCategoryName());
        Glide.with(activity).load(categories.get(position).getCategoryImage()).placeholder(R.drawable.app_icon).into(holder.catImg);




        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                subCatTitle.setText(categories.get(position).getCategoryName());

                setSubCatList(position, categories.get(position).getCategoryId());

                // Update the selected position
                selectedPosition = position;

                // Reset text color for all items
                for (int i = 0; i < getItemCount(); i++) {
                    notifyItemChanged(i, "textColor");
                }

                // Set the selected item text color
                holder.catTxt.setTextColor(activity.getResources().getColor(R.color.theme_Blue));

                // Show the bottom bar
                holder.bottomBar.setVisibility(View.VISIBLE);


            }
        });


        // Check if the current position is the selected position and change text color accordingly
        if (position == selectedPosition) {
            holder.catTxt.setTextColor(activity.getResources().getColor(R.color.theme_Blue));
            holder.bottomBar.setVisibility(View.VISIBLE);
        } else {
            holder.catTxt.setTextColor(activity.getResources().getColor(R.color.text_color));
            holder.bottomBar.setVisibility(View.INVISIBLE);
        }


    }



    private void setSubCatList(int position, String categoryID) {
        List<Subcategory> subCatName = categories.get(position).getSubcategories();

//        Log.d("salsnalns", "setSubCatList: "+categories.get(position).getSubcategories().get(0).getSubcategoryName());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        UserSubCategoryAdapter adapter = new UserSubCategoryAdapter(subCatName, activity, categoryID);

        subRecyclerView.setAdapter(adapter);
        subRecyclerView.setLayoutManager(linearLayoutManager);



    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView catImg;
        TextView catTxt;
        LinearLayout layout;
        LinearLayout bottomBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            catImg = itemView.findViewById(R.id.catImg);
            catTxt = itemView.findViewById(R.id.catTxt);
            layout = itemView.findViewById(R.id.cat_click_lay);
            bottomBar = itemView.findViewById(R.id.bottomBar);
        }
    }
}
