package www.experthere.in.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import www.experthere.in.R;
import www.experthere.in.api.ApiClient;
import www.experthere.in.api.ApiInterface;
import www.experthere.in.helper.CustomToastNegative;
import www.experthere.in.helper.TextMaskUtil;
import www.experthere.in.model.Order;
import www.experthere.in.model.OrderListResponse;
import www.experthere.in.model.SuccessMessageResponse;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    Activity activity;
    List<Order> orderList;

    private final DataUpdated dataUpdated;

    public interface DataUpdated{

         boolean isDataUpdated();

    }

    public OrderAdapter(Activity activity, List<Order> orderList,DataUpdated dataUpdated) {
        this.activity = activity;
        this.orderList = orderList;
        this.dataUpdated=dataUpdated;
    }


    public void setData(List<Order> orders) {
        this.orderList = orders;
        notifyDataSetChanged();
    }

    public void addData(List<Order> newOrders) {
        if (newOrders != null) {
            orderList.addAll(newOrders);
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public OrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_row, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        TextMaskUtil.maskText(holder.titleTxt, orderList.get(position).getServiceName(), 30, "..");
        holder.nameTxt.setText(orderList.get(position).getCustomerName());
        holder.dateTxt.setText(orderList.get(position).getTime());
        holder.addressTxt.setText(orderList.get(position).getAddress());



        holder.clickLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                showDialog(
                        orderList.get(position).getCustomerName(),
                        orderList.get(position).getTime(),
                        orderList.get(position).getServiceName(),
                        orderList.get(position).getAddress());


            }
        });

        holder.delBtn.setOnClickListener(new View.OnClickListener() {
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
                dialogTitle.setText("Delete Order");
                dialogMessage.setText("Are you sure you want to delete this Order Permanently?");


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

                            new DeleteOrder(orderList.get(position).getId(), position).execute();
                        } catch (Exception e) {

                            CustomToastNegative.create(activity,"Error: "+e.getMessage());

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

        return orderList.size();

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView titleTxt, addressTxt, nameTxt, dateTxt;
        ImageView delBtn;
        LinearLayout clickLay;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            titleTxt = itemView.findViewById(R.id.titleTxt);
            addressTxt = itemView.findViewById(R.id.locationTxtCat);
            nameTxt = itemView.findViewById(R.id.nameTxt);
            dateTxt = itemView.findViewById(R.id.dateTxt);
            delBtn = itemView.findViewById(R.id.delBtn);
            clickLay = itemView.findViewById(R.id.clickLay);


        }
    }


    private class DeleteOrder extends AsyncTask<Void, Void, SuccessMessageResponse> {


        int orderID, position;

        public DeleteOrder(int orderID, int position) {
            this.orderID = orderID;
            this.position = position;
        }

        @Override
        protected SuccessMessageResponse doInBackground(Void... voids) {
            // Create Retrofit instance
            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);


            // Make API call
            Call<SuccessMessageResponse> call = apiInterface.deleteOrder(orderID);

            try {
                // Execute the call synchronously
                Response<SuccessMessageResponse> response = call.execute();

                if (response.isSuccessful()) {
                    return response.body();
                } else {
                    // Handle unsuccessful response
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(SuccessMessageResponse apiResponse) {
            super.onPostExecute(apiResponse);

            if (apiResponse != null) {
                // Handle the API response

                if (apiResponse.isSuccess() && apiResponse.getMessage().equals("Order item deleted successfully.")) {


                    orderList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, orderList.size());
                    dataUpdated.isDataUpdated();


                } else {

                    CustomToastNegative.create(activity,"Error: "+apiResponse.getMessage());


//                    Toast.makeText(activity, "API Error " + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }

            } else {
//                Toast.makeText(activity, "SERVER ERROR", Toast.LENGTH_SHORT).show();
            }


        }
    }


    public void showDialog(String customerName,String date, String service,String address) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.order_list_dialog, null);
//        builder.setCancelable(false);

        TextView cName = view.findViewById(R.id.customerName);
        TextView dateTime = view.findViewById(R.id.dateTime);
        TextView serviceName = view.findViewById(R.id.serviceName);
        TextView addressName = view.findViewById(R.id.addressName);


        cName.setText(customerName);
        dateTime.setText(date);
        serviceName.setText(service);
        addressName.setText(address);


        ImageView cancelBtn = view.findViewById(R.id.cancelBtn);


        builder.setView(view);



        Dialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));



        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }


}
