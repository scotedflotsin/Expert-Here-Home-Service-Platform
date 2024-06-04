package www.experthere.in.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
import www.experthere.in.model.DeleteServiceResponse;
import www.experthere.in.model.Services;
import www.experthere.in.serviceProvider.NewServiceActivity;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ViewHolder> implements Filterable {

    private List<Services> serviceList;
    private final List<Services> filterServiceList;
    private final Activity activity;
    private String searchQuery = "";

    public void setData(List<Services> service) {
        this.serviceList = service;
        notifyDataSetChanged();
    }

    public void addData(List<Services> newServiceList) {
        if (serviceList != null) {
            serviceList.addAll(newServiceList);
            notifyDataSetChanged();
        }
    }

    public ServiceAdapter(List<Services> serviceList, Activity activity) {
        this.serviceList = serviceList;
        this.activity = activity;
        this.filterServiceList = new ArrayList<>(serviceList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.service_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.titleTextView.setText(serviceList.get(position).getServiceTitle());

        holder.priceTxt.setText(serviceList.get(position).getCurrency()+" "+ serviceList.get(position).getFinalPrice());



        holder.durationTxt.setText(serviceList.get(position).getServiceDuration());
        holder.statusTxt.setText(serviceList.get(position).getServiceStatus());


        DecimalFormat decimalFormat = null;

        if (serviceList.get(position).getAverage_rating()==0){

            decimalFormat =  new DecimalFormat("#.#");


        }
        else {
            decimalFormat =  new DecimalFormat("#.0");

        }



        // Format the number using the DecimalFormat
        String avgRatings = decimalFormat.format(serviceList.get(position).getAverage_rating());



        holder.avgRatingTxt.setText(avgRatings+" ("+ serviceList.get(position).getTotal_stars() +")");

        if (serviceList.get(position).getServiceStatus().matches("Disabled")) {
            holder.statusTxt.setTextColor(activity.getColor(R.color.redBtnTxtColor));
        } else {
            holder.statusTxt.setTextColor(activity.getColor(R.color.enable_green));
        }

        holder.personTxt.setText(String.valueOf(serviceList.get(position).getMembers()));

        Glide.with(activity).load(serviceList.get(position).getServiceImageUrl()).placeholder(R.drawable.app_icon).into(holder.imageView);

//        holder.imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.d("DATATTAT", "onClick: " + serviceList.get(position).getServiceImageUrl());
//
//                Toast.makeText(activity, "Url " + serviceList.get(position).getServiceImageUrl(), Toast.LENGTH_SHORT).show();
//            }
//        });

        holder.edit_Service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("provider_id", serviceList.get(position).getProviderId());
                bundle.putString("service_id", serviceList.get(position).getId());
                bundle.putBoolean("isEdit", true);

                Intent intent = new Intent(activity, NewServiceActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtras(bundle);
                activity.startActivity(intent);
            }
        });

        holder.del_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Create custom dialog
                Dialog dialog = new Dialog(activity);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.custom_delete_dialog);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                // Initialize views in the custom dialog
                TextView dialogTitle = dialog.findViewById(R.id.dialogTitle);
                TextView dialogMessage = dialog.findViewById(R.id.dialogMessage);
                Button btnCancel = dialog.findViewById(R.id.btnCancel);
                Button btnDelete = dialog.findViewById(R.id.btnDelete);

                // Set dialog title and message
                dialogTitle.setText("Delete Service");
                dialogMessage.setText("Are you sure you want to delete this service Permanently?");


                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // User clicked the Cancel button
                        dialog.dismiss();
                    }
                });

                btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // User clicked the Delete button


                        new DeleteServiceTask(ServiceAdapter.this, serviceList.get(position).getId(), position, new ProgressDialog(activity)).execute();
                        dialog.dismiss();
                    }
                });

                dialog.show();




            }
        });
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
        private final TextView titleTextView;
        private final TextView priceTxt;
        private final TextView durationTxt;
        private final TextView statusTxt;
        private final TextView personTxt;
        private final TextView avgRatingTxt;
        ImageView imageView;
        LinearLayout del_service, edit_Service;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTxt);
            priceTxt = itemView.findViewById(R.id.priceTxt);
            durationTxt = itemView.findViewById(R.id.durationTxt);
            statusTxt = itemView.findViewById(R.id.statusTxt);
            personTxt = itemView.findViewById(R.id.personTxt);
            imageView = itemView.findViewById(R.id.serviceImage);
            del_service = itemView.findViewById(R.id.del_service);
            edit_Service = itemView.findViewById(R.id.editServiceBtn);
            avgRatingTxt = itemView.findViewById(R.id.avgRatingTxt);
        }
    }

    private static class DeleteServiceTask extends AsyncTask<String, Void, Void> {
        private final WeakReference<ServiceAdapter> adapterReference;
        private final String serviceId;
        private final ProgressDialog progressDialog;
        private final int deletedItemPosition;

        DeleteServiceTask(ServiceAdapter adapter, String serviceId, int deletedItemPosition, ProgressDialog progressDialog) {
            adapterReference = new WeakReference<>(adapter);
            this.serviceId = serviceId;
            this.deletedItemPosition = deletedItemPosition;
            this.progressDialog = progressDialog;
        }

        @Override
        protected Void doInBackground(String... params) {
            ServiceAdapter adapter = adapterReference.get();
            if (adapter != null) {
                ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
                Call<Void> call = apiInterface.deleteService(serviceId);

                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            // Handle successful deletion
                            // Refresh your RecyclerView data if needed

                            adapter.serviceList.remove(deletedItemPosition);
                            adapter.notifyItemRemoved(deletedItemPosition);
                            adapter.notifyItemRangeChanged(deletedItemPosition, adapter.serviceList.size());
                        } else {
                            // Handle API error
                            Log.e("DeleteService", "Error: " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        // Handle network error
                        Log.e("DeleteService", "Network error: " + t.getMessage());
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Show the ProgressDialog before the API call
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            ServiceAdapter adapter = adapterReference.get();
            if (adapter != null) {
                // Hide the ProgressDialog after the API call is complete
                progressDialog.dismiss();
            }
        }
    }

    // Method to filter data based on search query
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String query = searchQuery.toLowerCase(); // Use searchQuery instead of charSequence

                List<Services> filteredList = new ArrayList<>();

                for (Services item : filterServiceList) {
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
                serviceList.clear();
                serviceList.addAll((List<Services>) filterResults.values);
                notifyDataSetChanged();
            }
        };
    }

    // Method to filter data based on search query
    public void filter(String query) {
        searchQuery = query;
        getFilter().filter(query);    }
}
