package www.experthere.in.adapters.serviceListUser;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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

public class ServiceProviderAdapter extends RecyclerView.Adapter<ServiceProviderAdapter.ViewHolder> {

    List<Service> services;
    Activity activity;
    FragmentManager fragmentManager;

    public ServiceProviderAdapter(List<Service> services, Activity activity, FragmentManager fragmentManager) {
        this.services = services;
        this.activity = activity;
        this.fragmentManager=fragmentManager;
    }

    public void setData(List<Service> services) {
        this.services = services;
        notifyDataSetChanged();
    }

    public void addData(List<Service> newServiceList) {
        if (services != null) {
            services.addAll(newServiceList);
            notifyDataSetChanged();
        }
    }


    @NonNull
    @Override
    public ServiceProviderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.service_provider_profile_row, parent, false);
        return new ViewHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull ServiceProviderAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {


        DecimalFormat decimalFormat = new DecimalFormat("#.#");
        holder.reviewTxt.setText(decimalFormat.format(services.get(position).getAvgRating())+"("+ services.get(position).getTotalStars() +")");



        holder.itemClickBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle bundle = new Bundle();

                bundle.putString("providerId", String.valueOf(services.get(position).getProvider().getId()));
                bundle.putString("bannerImage", String.valueOf(services.get(position).getProvider().getBannerImage()));
                bundle.putString("logoImage", String.valueOf(services.get(position).getProvider().getLogoImage()));




                ServiceDetailsBottomSheet bottomSheet = new ServiceDetailsBottomSheet(services.get(position),activity,false);
                bottomSheet.show(fragmentManager, bottomSheet.getTag());



            }
        });



        holder.titleTxt.setText(services.get(position).getServiceTitle());
        Glide.with(activity).load(services.get(position).getServiceImageUrl()).into(holder.serviceImage);

        holder.personTxt.setText(services.get(position).getMembers() + " Person");
        if (services.get(position).getServiceDuration().contains("Hourly")) {
            holder.durationTxt.setText(services.get(position).getServiceDuration().replace("Hourly", "Hour"));
        } else {

            holder.durationTxt.setText(services.get(position).getServiceDuration());
        }

        if (services.get(position).getTaxType().contains("Tax Excluded")) {

            double discountPrice = Double.parseDouble(services.get(position).getDiscountedPrice());
            double finalPrice = Double.parseDouble(services.get(position).getFinalPrice());

            double discountPercentage = (1-(discountPrice / finalPrice)) * 100;


            holder.discountTxt.setText((int) Math.round(discountPercentage) + "% OFF");

            holder.displayPriceTxt.setText(services.get(position).getCurrency() + " " + (int) Math.round(discountPrice));

            holder.cutPriceTxt.setText(services.get(position).getCurrency() + " " + (int) Math.round(finalPrice));

        } else {


            double discountPrice = Double.parseDouble(services.get(position).getDiscountedPrice());
            double cutPrice = Double.parseDouble(services.get(position).getFinalPrice());


            double tax = Double.parseDouble(services.get(position).getTaxValue());

//            double finalDisplayPrice = (discountPrice / 100) * tax;
//
//            double showPrice= finalDisplayPrice+discountPrice;
//
//            double finalCut = (finalPrice / 100) * tax;
//            double finalCutShow = finalCut+finalPrice;
//
//            double finalPriceWithTax = finalPrice + (finalPrice * (tax / 100));
//            double discountPercentage = (1 - (discountPrice / finalPriceWithTax)) * 100;
//
            double discountPercentage = calculateDiscountPercentage(discountPrice, cutPrice);
            double finalCutPrice = calculateFinalCutPrice(cutPrice, tax);
            double finalPrice = calculateFinalPrice(discountPrice, tax);

            holder.discountTxt.setText(Math.round(discountPercentage) + "% OFF");
            holder.displayPriceTxt.setText(services.get(position).getCurrency() + " " + Math.round(finalPrice));
            holder.cutPriceTxt.setText(services.get(position).getCurrency() + " " + Math.round(finalCutPrice));








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
        return services.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView discountTxt, titleTxt, personTxt, durationTxt, displayPriceTxt, cutPriceTxt, reviewTxt;
        ImageView serviceImage,btnView;
        CardView itemClickBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            serviceImage = itemView.findViewById(R.id.serviceImage);
            discountTxt = itemView.findViewById(R.id.discountTxt);
            titleTxt = itemView.findViewById(R.id.titleTxt);
            personTxt = itemView.findViewById(R.id.personTxt);
            durationTxt = itemView.findViewById(R.id.durationTxt);
            displayPriceTxt = itemView.findViewById(R.id.displayPriceTxt);
            cutPriceTxt = itemView.findViewById(R.id.cutPriceTxt);
            reviewTxt = itemView.findViewById(R.id.reviewTxt);
            itemClickBtn = itemView.findViewById(R.id.itemClickService);
            btnView = itemView.findViewById(R.id.btnView);


        }


    }
}
