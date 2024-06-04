package www.experthere.in.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import www.experthere.in.R;
import www.experthere.in.api.ApiClient;
import www.experthere.in.api.ApiInterface;
import www.experthere.in.helper.ServiceDetailsBottomSheet;
import www.experthere.in.model.service_user_cat.Service;

public class ServiceAdapterUserSearch extends RecyclerView.Adapter<ServiceAdapterUserSearch.ViewHolder> implements Filterable {

    private List<Service> serviceList;
    private final List<Service> filterServiceList;
    private final Activity activity;
    private String searchQuery = "";

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

    public ServiceAdapterUserSearch(List<Service> serviceList, Activity activity, FragmentManager fragmentManager) {
        this.serviceList = serviceList;
        this.activity = activity;
        this.filterServiceList = new ArrayList<>(serviceList);
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.service_by_cat_user_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        DecimalFormat decimalFormat = new DecimalFormat("#.#");
        holder.reviewTxt.setText(decimalFormat.format(serviceList.get(position).getAvgRating()) + "(" + serviceList.get(position).getTotalStars() + ")");


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

        holder.personTxt.setText(serviceList.get(position).getMembers() + " Person");
        if (serviceList.get(position).getServiceDuration().contains("Hourly")) {
            holder.durationTxt.setText(serviceList.get(position).getServiceDuration().replace("Hourly", "Hour"));
        } else {

            holder.durationTxt.setText(serviceList.get(position).getServiceDuration());
        }

        if (serviceList.get(position).getTaxType().contains("Tax Excluded")) {

            double discountPrice = Double.parseDouble(serviceList.get(position).getDiscountedPrice());
            double finalPrice = Double.parseDouble(serviceList.get(position).getFinalPrice());

            double discountPercentage = (1 - (discountPrice / finalPrice)) * 100;


            holder.discountTxt.setText((int) Math.round(discountPercentage) + "% OFF");

            holder.displayPriceTxt.setText(serviceList.get(position).getCurrency() + " " + (int) Math.round(discountPrice));

            holder.cutPriceTxt.setText(serviceList.get(position).getCurrency() + " " + (int) Math.round(finalPrice));

        } else {


            double discountPrice = Double.parseDouble(serviceList.get(position).getDiscountedPrice());
            double cutPrice = Double.parseDouble(serviceList.get(position).getFinalPrice());


            double tax = Double.parseDouble(serviceList.get(position).getTaxValue());

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
        TextView discountTxt, titleTxt, personTxt, durationTxt, displayPriceTxt, cutPriceTxt, reviewTxt;
        ImageView serviceImage;
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


        }
    }

//    private static class DeleteServiceTask extends AsyncTask<String, Void, Void> {
//        private WeakReference<ServiceAdapterUserSearch> adapterReference;
//        private String serviceId;
//        private ProgressDialog progressDialog;
//        private int deletedItemPosition;
//
//        DeleteServiceTask(ServiceAdapterUserSearch adapter, String serviceId, int deletedItemPosition, ProgressDialog progressDialog) {
//            adapterReference = new WeakReference<>(adapter);
//            this.serviceId = serviceId;
//            this.deletedItemPosition = deletedItemPosition;
//            this.progressDialog = progressDialog;
//        }
//
//        @Override
//        protected Void doInBackground(String... params) {
//            ServiceAdapterUserSearch adapter = adapterReference.get();
//            if (adapter != null) {
//                ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
//                Call<Void> call = apiInterface.deleteService(serviceId);
//
//                call.enqueue(new Callback<Void>() {
//                    @Override
//                    public void onResponse(Call<Void> call, Response<Void> response) {
//                        if (response.isSuccessful()) {
//                            // Handle successful deletion
//                            // Refresh your RecyclerView data if needed
//
//
//                            adapter.serviceList.remove(deletedItemPosition);
//                            adapter.notifyItemRemoved(deletedItemPosition);
//                            adapter.notifyItemRangeChanged(deletedItemPosition, adapter.serviceList.size());
//                        } else {
//                            // Handle API error
//                            Log.e("DeleteService", "Error: " + response.message());
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<Void> call, Throwable t) {
//                        // Handle network error
//                        Log.e("DeleteService", "Network error: " + t.getMessage());
//                    }
//                });
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            // Show the ProgressDialog before the API call
//            progressDialog.show();
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//            ServiceAdapterUserSearch adapter = adapterReference.get();
//            if (adapter != null) {
//                // Hide the ProgressDialog after the API call is complete
//                progressDialog.dismiss();
//            }
//        }
//    }

    // Method to filter data based on search query
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String query = searchQuery.toLowerCase(); // Use searchQuery instead of charSequence

                List<Service> filteredList = new ArrayList<>();

                for (Service item : filterServiceList) {
                    if (item.getServiceTitle().toLowerCase().contains(query)) {
                        filteredList.add(item);
                    }
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {


                if (serviceList != null) {

                    serviceList.clear();
                    serviceList.addAll((List<Service>) filterResults.values);
                    notifyDataSetChanged();

                }
            }
        };
    }

    // Method to filter data based on search query
    public void filter(String query) {
        searchQuery = query;
        getFilter().filter(query);
    }
}
