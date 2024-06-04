package www.experthere.in.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.util.List;

import www.experthere.in.R;
import www.experthere.in.model.ProviderList;
import www.experthere.in.model.service_user_cat.Service;
import www.experthere.in.serviceProvider.ServiceProviderProfileDetailsActivity;

public class ProviderListAdapter extends RecyclerView.Adapter<ProviderListAdapter.ViewHolder> {

    List<ProviderList> providerLists;
    Activity activity;

    public ProviderListAdapter(List<ProviderList> providerLists, Activity activity) {
        this.providerLists = providerLists;
        this.activity = activity;
    }



    public void setData(List<ProviderList> service) {
        this.providerLists = service;
        notifyDataSetChanged();
    }

    public void addData(List<ProviderList> newServiceList) {
        if (newServiceList != null) {
            newServiceList.addAll(newServiceList);
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public ProviderListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.provider_list_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProviderListAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {


        Glide.with(activity)
                .load(providerLists.get(position)
                .getLogoImage())
                .placeholder(R.drawable.app_icon)
                .circleCrop()
                .into(holder.providerLogoImg);


        holder.companyName.setText(providerLists.get(position).getCompanyName());

        holder.servicesCount.setText(providerLists.get(position).getTotalServices()+" "+activity.getString(R.string.services));

        holder.ratingTxt.setText(Math.round(providerLists.get(position).getAverage_rating()) +" ("+providerLists.get(position).getTotal_reviews()+")");


      holder.clickItem.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {



              Intent intent = new Intent(activity.getApplicationContext(), ServiceProviderProfileDetailsActivity.class);


//              intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

              Bundle bundle  = new Bundle();


              bundle.putString("provider_id", String.valueOf(providerLists.get(position).getId()));
              bundle.putString("provider_name", String.valueOf(providerLists.get(position).getName()));
              bundle.putString("provider_banner", String.valueOf(providerLists.get(position).getBannerImage()));
              bundle.putString("provider_logo", String.valueOf(providerLists.get(position).getLogoImage()));

              bundle.putString("provider_company_name", String.valueOf(providerLists.get(position).getCompanyName()));


              intent.putExtras(bundle);
              activity.startActivity(intent);



          }
      });




    }

    @Override
    public int getItemCount() {
        return providerLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CardView clickItem;
        ImageView providerLogoImg;
        TextView companyName, servicesCount, ratingTxt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            providerLogoImg = itemView.findViewById(R.id.serviceImage);
            companyName = itemView.findViewById(R.id.titleTxt);
            servicesCount = itemView.findViewById(R.id.displayPriceTxt);
            ratingTxt = itemView.findViewById(R.id.reviewTxt);
            clickItem = itemView.findViewById(R.id.itemClickService);


        }
    }
}
