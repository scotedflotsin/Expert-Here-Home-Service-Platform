package www.experthere.in.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import www.experthere.in.R;
import www.experthere.in.model.CAT_TEST;
import www.experthere.in.model.service_user_cat.Service;
import www.experthere.in.users.ServiceForCategoryActivity;

public class CatWiseUserHomeAdapter extends RecyclerView.Adapter<CatWiseUserHomeAdapter.ViewHolder> {

    Activity activity;

    List<CAT_TEST> catTests;

    FragmentManager fragmentManager;

    public CatWiseUserHomeAdapter(Activity activity, List<CAT_TEST> catTests, FragmentManager fragmentManager) {
        this.activity = activity;
        this.catTests = catTests;
        this.fragmentManager = fragmentManager;
    }


    public void setData(List<CAT_TEST> service) {
        this.catTests = service;
        notifyDataSetChanged();
    }

    public void addData(List<CAT_TEST> newServiceList) {
        if (catTests != null) {
            catTests.addAll(newServiceList);
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public CatWiseUserHomeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cat_user_lay,parent,false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull CatWiseUserHomeAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {


        holder.title.setText(catTests.get(position).getCategory_name());
        TopServiceAdapter adapter = new TopServiceAdapter(catTests.get(position).getServices(),activity,fragmentManager);


//        Log.d("SNSKJN", "DATA "+catTests.get(position).getCategory_name());
//        Log.d("SNSKJN", "DATA "+catTests.get(position).getServices().size());


//        Log.d("MYAPPTESTsjsbj", "DATA  WORKING name "+catTests.get(position).getCategory_name());
//        Log.d("MYAPPTESTsjsbj", "DATA  WORKING size "+catTests.get(position).getServices().size());


        holder.recyclerView.setAdapter(adapter);
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(activity,RecyclerView.HORIZONTAL,false));

        if (catTests.get(position).getServices().size() == 0){

            holder.nodatatxt.setVisibility(View.VISIBLE);
            holder.recyclerView.setVisibility(View.GONE);

//            holder.mainLay.setVisibility(View.GONE);


        }else {

            holder.nodatatxt.setVisibility(View.GONE);
            holder.recyclerView.setVisibility(View.VISIBLE);

//            holder.mainLay.setVisibility(View.VISIBLE);


        }


        holder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(activity, ServiceForCategoryActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString("cat_name",catTests.get(position).getCategory_name());
                bundle.putString("cat_id",catTests.get(position).getCategory_id());

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtras(bundle);
                activity.startActivity(intent);



            }
        });


    }

    @Override
    public int getItemCount() {
        return catTests.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RecyclerView recyclerView;
        TextView title,moreBtn,nodatatxt;

        LinearLayout mainLay;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            recyclerView = itemView.findViewById(R.id.recycler_home_services);
            title = itemView.findViewById(R.id.catTitle);
            moreBtn = itemView.findViewById(R.id.moreBtn);
            nodatatxt = itemView.findViewById(R.id.nodataTxt);
            mainLay = itemView.findViewById(R.id.homeServiceLay);

        }
    }



}
