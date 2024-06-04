package www.experthere.in.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import www.experthere.in.R;
import www.experthere.in.api.ApiClient;
import www.experthere.in.api.ApiInterface;
import www.experthere.in.calling.OutGoingCallActivity;
import www.experthere.in.helper.CustomToastNegative;
import www.experthere.in.helper.DateTimeUtils;
import www.experthere.in.helper.ProcessingDialog;
import www.experthere.in.model.CallHistoryModel;
import www.experthere.in.model.SuccessMessageResponse;

public class ProviderCallHistoryAdapter extends RecyclerView.Adapter<ProviderCallHistoryAdapter.ViewHolder> implements Filterable {

    private List<CallHistoryModel> reviews;
    private final List<CallHistoryModel> filterServiceList;
    private final Activity activity;

    private final SharedPreferences preferences;

    private String searchQuery = "";

    boolean outgoingActivity;

    ProcessingDialog processingDialog;


    public ProviderCallHistoryAdapter(List<CallHistoryModel> reviews, Activity activity, boolean outgoingActivity) {
        this.reviews = reviews;
        this.activity = activity;
        this.filterServiceList = new ArrayList<>(reviews);
        this.outgoingActivity = outgoingActivity;
        preferences = activity.getSharedPreferences("user", Context.MODE_PRIVATE);
        processingDialog = new ProcessingDialog(activity);
    }


    public void setData(List<CallHistoryModel> reviews) {
        this.reviews = reviews;
        notifyDataSetChanged();
    }

    public void addData(List<CallHistoryModel> newreviews) {
        if (newreviews != null) {
            reviews.addAll(newreviews);
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public ProviderCallHistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.call_history_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProviderCallHistoryAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {


        holder.userName.setText(reviews.get(position).getUser_name());

        String time = DateTimeUtils.formatTimestamp(activity, Long.valueOf(reviews.get(position).getTimestamp()));

        holder.time.setText(time);



        if (outgoingActivity){
            holder.greenArrow.setRotation(180);
            holder.redArrow.setRotation(180);
        }


        if (reviews.get(position).getReceived().equals("answered")) {
            holder.receivedTxt.setText("Answered");
            holder.receivedTxt.setTextColor(activity.getColor(R.color.slider_green));
            holder.greenArrow.setVisibility(View.VISIBLE);
            holder.redArrow.setVisibility(View.GONE);


        } else {
            holder.receivedTxt.setText("Not Answered");
            holder.receivedTxt.setTextColor(activity.getColor(R.color.redBtnTxtColor));
            holder.greenArrow.setVisibility(View.GONE);
            holder.redArrow.setVisibility(View.VISIBLE);
        }


        Glide.with(activity).load(reviews.get(position).getUser_profile_picture()).circleCrop().into(holder.displayImage);


        holder.callHistroryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Bundle bundle = new Bundle();
                bundle.putString("call_by", "provider");

                bundle.putString("user_id", reviews.get(position).getUser_id());
                bundle.putString("provider_id", reviews.get(position).getProvider_id());
                bundle.putString("dp", reviews.get(position).getUser_profile_picture());
                bundle.putString("display_name", reviews.get(position).getUser_name());

                SharedPreferences preferences = activity.getSharedPreferences("provider", Context.MODE_PRIVATE);

                if (preferences.contains("id")) {
                    bundle.putString("provider_name", preferences.getString("name", "Expert Here Provider"));
                    bundle.putString("provider_dp", preferences.getString("logo_image", "0"));
                }

                bundle.putString("selected_service", "Expert Here");

                Log.d("AKBAKBKABK", "onClick: " + bundle);

                Intent intent = new Intent(activity, OutGoingCallActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtras(bundle);
                activity.startActivity(intent);


            }
        });

        holder.callHistroryLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {


                Dialog dialog = new Dialog(activity);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.custom_delete_dialog);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                // Initialize views in the custom dialog
                TextView dialogTitle = dialog.findViewById(R.id.dialogTitle);
                TextView dialogMessage = dialog.findViewById(R.id.dialogMessage);
                Button btnCancel = dialog.findViewById(R.id.btnCancel);
                Button btnDelete = dialog.findViewById(R.id.btnDelete);
                btnDelete.setText("Delete");

                // Set dialog title and message
                dialogTitle.setText("Delete Call History?");
                dialogMessage.setText("Are you sure you want to Delete Call History With '' "+reviews.get(position).getUser_name()+" '' ?");


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


                        try {

                            processingDialog.show();
                            new DeleteCallHistory(position,reviews.get(position).getId()).execute();
                        } catch (Exception e) {

                            CustomToastNegative.create(activity,"Error: "+e.getMessage());


                            throw new RuntimeException(e);
                        }




                        dialog.dismiss();
                    }
                });

                dialog.show();

                return false;
            }
        });

    }


    @Override
    public int getItemCount() {
        return reviews != null ? reviews.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView userName, receivedTxt, time;
        ImageView displayImage, redArrow, greenArrow;

        LinearLayout callHistroryLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.userName);
            time = itemView.findViewById(R.id.timeTxt);
            receivedTxt = itemView.findViewById(R.id.receivedTxt);
            displayImage = itemView.findViewById(R.id.displayImage);
            callHistroryLayout = itemView.findViewById(R.id.callHistroryLayout);
            redArrow = itemView.findViewById(R.id.redArrow);
            greenArrow = itemView.findViewById(R.id.greenArrow);


        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String query = searchQuery.toLowerCase(); // Use searchQuery instead of charSequence

                List<CallHistoryModel> filteredList = new ArrayList<>();

                for (CallHistoryModel item : filterServiceList) {
                    if (item.getUser_name().toLowerCase().contains(query)) {
                        filteredList.add(item);
                    }
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                reviews.clear();
                reviews.addAll((List<CallHistoryModel>) filterResults.values);
                notifyDataSetChanged();
            }
        };
    }

    // Method to filter data based on search query
    public void filter(String query) {
        searchQuery = query;
        getFilter().filter(query);
    }


    private class DeleteCallHistory extends AsyncTask<Void, Void, SuccessMessageResponse> {


        int  position;
        String  ID;


        public DeleteCallHistory(int position, String ID) {
            this.position = position;
            this.ID = ID;
        }

        @Override
        protected SuccessMessageResponse doInBackground(Void... voids) {
            // Create Retrofit instance
            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);


            // Make API call
            Call<SuccessMessageResponse> call = apiInterface.deleteCallHistoryForProvider(ID);

            try {
                // Execute the call synchronously
                Response<SuccessMessageResponse> response = call.execute();

                if (response.isSuccessful()) {
                    return response.body();
                } else {
                    // Handle unsuccessful response

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            processingDialog.dismiss();

                        }
                    });

                    return null;
                }
            } catch (IOException e) {

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        processingDialog.dismiss();

                    }
                });

                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(SuccessMessageResponse apiResponse) {
            super.onPostExecute(apiResponse);
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    processingDialog.dismiss();

                }
            });

            if (apiResponse != null) {
                // Handle the API response

                if (apiResponse.isSuccess() && apiResponse.getMessage().equals("Call history deleted successfully.")) {


                    reviews.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, reviews.size());


                } else {

                    CustomToastNegative.create(activity,"Error: "+apiResponse.getMessage());

                }

            } else {
            }


        }
    }

}
