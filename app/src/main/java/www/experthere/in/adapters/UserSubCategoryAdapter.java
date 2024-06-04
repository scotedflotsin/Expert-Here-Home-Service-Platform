package www.experthere.in.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import www.experthere.in.R;
import www.experthere.in.model.Subcategory;
import www.experthere.in.users.ServicesByCategoryActivity;

public class UserSubCategoryAdapter extends RecyclerView.Adapter<UserSubCategoryAdapter.ViewHolder> {

    private final List<Subcategory> subCategoryList;
    private final Activity activity;


    String categoryId;


    public UserSubCategoryAdapter(List<Subcategory> subCategoryList, Activity activity,
                                  String categoryId) {

        this.subCategoryList = subCategoryList;
        this.activity = activity;
        this.categoryId = categoryId;
    }

    @NonNull
    @Override
    public UserSubCategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sub_category_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserSubCategoryAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.subCatTxt.setText(subCategoryList.get(position).getSubcategoryName());



        holder.select_lay_subcat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {




                Bundle bundle = new Bundle();
                bundle.putString("catId",categoryId);
                bundle.putString("subCatId",subCategoryList.get(position).getSubcategoryId());
                bundle.putString("title",subCategoryList.get(position).getSubcategoryName());


                Intent intent = new Intent(activity, ServicesByCategoryActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtras(bundle);
                activity.startActivity(intent);




            }
        });
    }

    @Override
    public int getItemCount() {
        return subCategoryList.size();


    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView subCatTxt;
        LinearLayout select_lay_subcat;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            subCatTxt = itemView.findViewById(R.id.subCatTxt);
            select_lay_subcat = itemView.findViewById(R.id.select_lay_subcat);
        }
    }


}
