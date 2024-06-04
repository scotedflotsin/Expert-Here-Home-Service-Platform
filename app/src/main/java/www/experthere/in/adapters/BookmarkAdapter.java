package www.experthere.in.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

import www.experthere.in.R;
import www.experthere.in.api.ApiClient;
import www.experthere.in.api.ApiInterface;
import www.experthere.in.helper.Bookmark;
import www.experthere.in.helper.CustomToastPositive;
import www.experthere.in.helper.ProcessingDialog;
import www.experthere.in.model.SuccessMessageResponse;
import www.experthere.in.serviceProvider.ServiceProviderProfileDetailsActivity;

public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.ViewHolder>  {

    private List<Bookmark> bookmarks;

    private final Activity activity;

    ProcessingDialog processingDialog;

    public BookmarkAdapter(List<Bookmark> bookmarks, Activity activity) {
        this.bookmarks = bookmarks;
        this.activity = activity;

        processingDialog = new ProcessingDialog(activity);
    }

    public void setData(List<Bookmark> reviews) {
        this.bookmarks = reviews;
        notifyDataSetChanged();
    }

    public void addData(List<Bookmark> newreviews) {
        if (newreviews != null) {
            bookmarks.addAll(newreviews);
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public BookmarkAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bookmark_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookmarkAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        Glide.with(activity).load(bookmarks.get(position).getLogoImage()).placeholder(R.drawable.app_icon).into(holder.logo);

        holder.provider_name.setText(bookmarks.get(position).getName());
        holder.company_name_Txt.setText(bookmarks.get(position).getCompanyName());
        holder.visitingChargesTxt.setText((int) bookmarks.get(position).getVisitingCharges() + " Rs");


        float rating = Math.round(bookmarks.get(position).getAverage_rating());

        holder.ratingTxt.setText(rating +"("+ bookmarks.get(position).getTotal_reviews() +")");



        holder.bookmarkClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Bundle bundle = new Bundle();
                 bundle.putString("provider_id", String.valueOf(bookmarks.get(position).getProviderId()));
                 bundle.putString("provider_banner", bookmarks.get(position).getBannerImage());
                 bundle.putString("provider_name", bookmarks.get(position).getName());
                 bundle.putString("provider_logo", bookmarks.get(position).getLogoImage());
                bundle.putString("provider_company_name", bookmarks.get(position).getCompanyName());



                Intent intent = new Intent(activity, ServiceProviderProfileDetailsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtras(bundle);
                activity.startActivity(intent);

            }
        });

        holder.bookmarkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {






                Dialog dialog = new Dialog(activity);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.custom_delete_dialog);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                // Initialize views in the custom dialog
                TextView dialogTitle = dialog.findViewById(R.id.dialogTitle);
                TextView dialogMessage = dialog.findViewById(R.id.dialogMessage);
                Button btnCancel = dialog.findViewById(R.id.btnCancel);
                Button btnDelete = dialog.findViewById(R.id.btnDelete);
                btnDelete.setText("Remove");

                // Set dialog title and message
                dialogTitle.setText("Remove Bookmark ?");
                dialogMessage.setText("Are you sure you want to Remove Bookmark of '' "+bookmarks.get(position).getCompanyName()+" '' ?");


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
                            new DeleteBookmark(position,String.valueOf(bookmarks.get(position).getBookmark_id())).execute();

                        } catch (Exception e) {
//                            Toast.makeText(activity, "Error "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            throw new RuntimeException(e);
                        }




                        dialog.dismiss();
                    }
                });

                dialog.show();

            }
        });

    }


    @Override
    public int getItemCount() {
        return bookmarks != null ? bookmarks.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView logo,bookmarkBtn;
        TextView provider_name,company_name_Txt,visitingChargesTxt,ratingTxt;


        CardView bookmarkClick;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            logo = itemView.findViewById(R.id.serviceImage);
            provider_name = itemView.findViewById(R.id.provider_name_Txt);
            company_name_Txt = itemView.findViewById(R.id.company_name_Txt);
            ratingTxt = itemView.findViewById(R.id.ratingTxt);
            visitingChargesTxt = itemView.findViewById(R.id.visitingChargesTxt);
            bookmarkBtn = itemView.findViewById(R.id.bookmarkBtn);
            bookmarkClick = itemView.findViewById(R.id.bookmarkClick);


        }
    }


    private class DeleteBookmark extends AsyncTask<Void, Void, SuccessMessageResponse> {


        int  position;
        String  ID;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        public DeleteBookmark(int position, String ID) {
            this.position = position;
            this.ID = ID;
        }

        @Override
        protected SuccessMessageResponse doInBackground(Void... voids) {
            // Create Retrofit instance
            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);


            // Make API call
            Call<SuccessMessageResponse> call = apiInterface.deleteBookmark(ID);

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

                if (apiResponse.isSuccess() && apiResponse.getMessage().equals("Bookmark deleted successfully.")) {


                    bookmarks.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, bookmarks.size());


                    String CUSTOM_ACTION = "www.experthere.in.users.BOOKMARK_UPDATE_USER";


                    Intent intent = new Intent(CUSTOM_ACTION);

                    // Send the broadcast
                    if (activity != null) {
                        activity.sendBroadcast(intent);
                    }





//                    Toast.makeText(activity, "Bookmark Removed!", Toast.LENGTH_SHORT).show();



                    CustomToastPositive.create(activity,"Bookmark Removed!");


                } else {

//                    Toast.makeText(activity, "API Error " + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }

            } else {
//                Toast.makeText(activity, "SERVER ERROR", Toast.LENGTH_SHORT).show();
            }


        }
    }

}
