package www.experthere.in.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.DecimalFormat;
import java.util.List;

import www.experthere.in.R;
import www.experthere.in.helper.ServiceDetailsBottomSheet;
import www.experthere.in.model.service_user_cat.Service;

public class HomeSpecificCatServiceAdapter extends RecyclerView.Adapter<HomeSpecificCatServiceAdapter.ViewHolder> {

    private List<Service> serviceList;
    private final Activity activity;
    private final FragmentManager fragmentManager;

    public void setData(List<Service> service) {
        this.serviceList = service;
        notifyDataSetChanged();
    }

    public void addData(List<Service> newServiceList) {
        if (serviceList != null) {
            serviceList.addAll(newServiceList);
            notifyDataSetChanged();
        }
    }

    public HomeSpecificCatServiceAdapter(List<Service> serviceList, Activity activity, FragmentManager fragmentManager) {
        this.serviceList = serviceList;
        this.activity = activity;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_service_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {


            DecimalFormat decimalFormat = new DecimalFormat("#.#");
            holder.reviewTxt.setText(decimalFormat.format(serviceList.get(position).getAvgRating()) + "(" + serviceList.get(position).getTotalStars() + ")");


//            holder.itemClickBtn.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View v) {
//
//                    Toast.makeText(activity, " stars "+serviceList.get(position).getTotalStars(), Toast.LENGTH_SHORT).show();
//
//                    return true;
//
//                }
//            });

            holder.itemClickBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Bundle bundle = new Bundle();

                    bundle.putString("providerId", String.valueOf(serviceList.get(position).getProvider().getId()));
                    bundle.putString("bannerImage", String.valueOf(serviceList.get(position).getProvider().getBannerImage()));
                    bundle.putString("logoImage", String.valueOf(serviceList.get(position).getProvider().getLogoImage()));


                    ServiceDetailsBottomSheet bottomSheet = new ServiceDetailsBottomSheet(serviceList.get(position), activity, true);
                    bottomSheet.show(fragmentManager, bottomSheet.getTag());


                }
            });


            holder.titleTxt.setText(serviceList.get(position).getServiceTitle());


            Glide.with(activity).load(serviceList.get(position).getServiceImageUrl()).placeholder(R.drawable.app_icon).into(holder.serviceImage);


//        Log.d("SOINOSNSNJK", "onBindViewHolder: "+serviceList.get(position).getServiceImageUrl());

            holder.personTxt.setText(serviceList.get(position).getMembers() + " Person");
            if (serviceList.get(position).getServiceDuration().contains("Hourly")) {
                holder.durationTxt.setText(serviceList.get(position).getServiceDuration().replace("Hourly", "Hour"));
            } else {

                holder.durationTxt.setText(serviceList.get(position).getServiceDuration());
            }

            if (serviceList.get(position).getTaxType().contains("Tax Excluded")) {

                double discountPrice = Double.parseDouble(serviceList.get(position).getDiscountedPrice());
                double finalPrice = Double.parseDouble(serviceList.get(position).getFinalPrice());

//            double discountPercentage = (1 - (discountPrice / finalPrice)) * 100;


//            holder.discountTxt.setText((int) Math.round(discountPercentage) + "% OFF");

                holder.displayPriceTxt.setText(serviceList.get(position).getCurrency() + " " + (int) Math.round(discountPrice));

                holder.cutPriceTxt.setText(serviceList.get(position).getCurrency() + " " + (int) Math.round(finalPrice));

            } else {


                double discountPrice = Double.parseDouble(serviceList.get(position).getDiscountedPrice());
                double cutPrice = Double.parseDouble(serviceList.get(position).getFinalPrice());


                double tax = Double.parseDouble(serviceList.get(position).getTaxValue());

//            double discountPercentage = calculateDiscountPercentage(discountPrice, cutPrice);
                double finalCutPrice = calculateFinalCutPrice(cutPrice, tax);
                double finalPrice = calculateFinalPrice(discountPrice, tax);

//            holder.discountTxt.setText(Math.round(discountPercentage) + "% OFF");
                holder.displayPriceTxt.setText(serviceList.get(position).getCurrency() + " " + Math.round(finalPrice));
                holder.cutPriceTxt.setText(serviceList.get(position).getCurrency() + " " + Math.round(finalCutPrice));


            }



    }


    private double calculateDiscountPercentage(double discountedPrice, double cutPrice) {
        return (1 - (discountedPrice / cutPrice)) * 100;
    }

    private double calculateFinalCutPrice(double cutPrice, double taxValue) {
        return cutPrice + (cutPrice * (taxValue / 100));
    }

    private double calculateFinalPrice(double cutPrice, double taxValue) {
        return cutPrice + (cutPrice * (taxValue / 100));
    }


    @Override
    public int getItemCount() {


            return serviceList.size();

    }

    @Override
    public int getItemViewType(int position) {
        return serviceList == null ? 0 : serviceList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTxt, personTxt, durationTxt, displayPriceTxt, cutPriceTxt, reviewTxt;
        ImageView serviceImage;
        CardView itemClickBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            serviceImage = itemView.findViewById(R.id.serviceImage);
            titleTxt = itemView.findViewById(R.id.titleTxt);
            personTxt = itemView.findViewById(R.id.personTxt);
            durationTxt = itemView.findViewById(R.id.durationTxt);
            displayPriceTxt = itemView.findViewById(R.id.displayPriceTxt);
            cutPriceTxt = itemView.findViewById(R.id.cutPriceTxt);
            reviewTxt = itemView.findViewById(R.id.reviewTxt);
            itemClickBtn = itemView.findViewById(R.id.itemClickService);


        }
    }


}
