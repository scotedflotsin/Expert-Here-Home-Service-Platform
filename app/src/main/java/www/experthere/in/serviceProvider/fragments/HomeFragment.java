package www.experthere.in.serviceProvider.fragments;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.github.vejei.cupertinoswitch.CupertinoSwitch;
import retrofit2.Call;
import retrofit2.Response;
import www.experthere.in.Common.GetShimmerStrings;
import www.experthere.in.R;
import www.experthere.in.adapters.OrderAdapter;
import www.experthere.in.adapters.shimmer.MyShimmerAdapter;
import www.experthere.in.adapters.shimmer.NonScrollableLayoutManager;
import www.experthere.in.api.ApiClient;
import www.experthere.in.api.ApiInterface;
import www.experthere.in.helper.AddServiceFirstTimeBottomSheet;
import www.experthere.in.helper.CustomToastPositive;
import www.experthere.in.helper.ProcessingDialog;
import www.experthere.in.helper.app.OrderNotifcationService;
import www.experthere.in.model.Order;
import www.experthere.in.model.OrderCount;
import www.experthere.in.model.OrderListResponse;
import www.experthere.in.model.ProviderStatusResponse;
import www.experthere.in.model.SuccessMessageResponse;
import www.experthere.in.model.YourServicesCount;
import www.experthere.in.serviceProvider.NewOrderActivity;
import www.experthere.in.serviceProvider.ProviderHome;

public class HomeFragment extends Fragment {
    private static final String TAG = "HOME_FRAGMENT";
    String providerID;

    Activity activity;
    RecyclerView recyclerOrders;
    ProgressBar progressBar;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int currentPage = 1;
    private final int itemsPerPage = 10;
    List<Order> orderResponses;

    OrderAdapter adapter;

    TextView totalTxt, activeTxt, notActiveTxt;

    SwitchCompat statusSwitchProvider;

    boolean switchChangedByUser;

    LinearLayout activeLay, deactiveLay;

    SwipeRefreshLayout swipeRefreshLayout;

    ShimmerFrameLayout shimmerFrameLayout;

    TextView orderCountTxt;


    String ACTION_DISMISS_NOTIFICATION = "www.experthere.in.helper.app.ACTION_FINISH_ACTIVITY";

    TextView noDataText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = requireActivity();
        initBundle(activity);


    }

    private void inflateShimmerRecyclerView(View view ,String type) {


        RecyclerView shimmerRecycler = view.findViewById(R.id.recyclerShimmer);

        MyShimmerAdapter shimmerAdapter = new MyShimmerAdapter(type);
        NonScrollableLayoutManager layoutManager = new NonScrollableLayoutManager(activity);
        shimmerRecycler.setLayoutManager(layoutManager);
        shimmerRecycler.setAdapter(shimmerAdapter);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        orderCountTxt=view.findViewById(R.id.orderCountTxt);
        noDataText=view.findViewById(R.id.noDataText);

        swipeRefreshLayout = view.findViewById(R.id.swipeLayout);
        shimmerFrameLayout = view.findViewById(R.id.shimmer_orders);
        inflateShimmerRecyclerView(view, GetShimmerStrings.PROVIDER_ORDER);

        shimmerFrameLayout.startShimmer();


        swipeRefreshLayout = view.findViewById(R.id.swipeLayout);

        orderResponses = new ArrayList<>();


        activeLay = view.findViewById(R.id.activeLay);
        deactiveLay = view.findViewById(R.id.deActiveLay);
        statusSwitchProvider = view.findViewById(R.id.statusSwitchProvider);




        totalTxt = view.findViewById(R.id.totalTxt);
        activeTxt = view.findViewById(R.id.activeTxt);
        notActiveTxt = view.findViewById(R.id.notActiveTxt);
        progressBar = view.findViewById(R.id.progressBar);
        recyclerOrders = view.findViewById(R.id.recyclerOrders);
        adapter = new OrderAdapter(activity, orderResponses, new OrderAdapter.DataUpdated() {
            @Override
            public boolean isDataUpdated() {

                new GetOrderCount(Integer.parseInt(providerID)).execute();

                return false;
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        recyclerOrders.setAdapter(adapter);
        recyclerOrders.setLayoutManager(layoutManager);

        recyclerOrders.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if (!isLoading && !isLastPage) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0
                            && totalItemCount >= itemsPerPage) {
                        // Load more data when reaching the end

                        isLoading = true;
                        currentPage++;
                        progressBar.setVisibility(View.VISIBLE);


                        try {

                            new GetOrdersTask(Integer.parseInt(providerID), currentPage, itemsPerPage).execute();

                        } catch (Exception e) {
                            Log.d(TAG, " ERROR "+e.getMessage());

                        }
                    }
                }
            }
        });


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPage = 1;
                isLoading = false;
                isLastPage = false;
                orderResponses.clear();
                adapter.notifyDataSetChanged();

                switchChangedByUser=false;
                activeLay.setVisibility(View.GONE);
                deactiveLay.setVisibility(View.VISIBLE);



                try {

                    new GetOrdersTask(Integer.parseInt(providerID), currentPage, itemsPerPage).execute();
                    new GetOrderCount(Integer.parseInt(providerID)).execute();
                    new GetServicesCount(Integer.parseInt(providerID)).execute();
                    new GetStatus(providerID).execute();

                    shimmerFrameLayout.startShimmer();
                    shimmerFrameLayout.setVisibility(View.VISIBLE);
                    recyclerOrders.setVisibility(View.GONE);

                    adapter.notifyDataSetChanged();


                } catch (Exception e) {

                    Log.d(TAG, " ERROR 22"+e.getMessage());


                }

            }
        });

        view.findViewById(R.id.createOrderBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(activity, NewOrderActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }
        });


        try {

            new GetOrdersTask(Integer.parseInt(providerID), currentPage, itemsPerPage).execute();
            new GetServicesCount(Integer.parseInt(providerID)).execute();
            new GetOrderCount(Integer.parseInt(providerID)).execute();
            new GetStatus(providerID).execute();




        } catch (Exception e) {

            Log.d(TAG, "2 ERROR "+e.getMessage());

        }


// Apply color filter to the slider drawable

        statusSwitchProvider.setEnabled(false);

        statusSwitchProvider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switchChangedByUser = true;

            }
        });



        statusSwitchProvider.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {




                    if (isChecked){




                        try {
                            new SetStatus(providerID, "active").execute();
                            statusSwitchProvider.getThumbDrawable().setColorFilter(ContextCompat.getColor(activity, R.color.slider_green), PorterDuff.Mode.SRC_IN);
                            statusSwitchProvider.getTrackDrawable().setColorFilter(ContextCompat.getColor(activity, R.color.green_on), PorterDuff.Mode.SRC_IN);

                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }




                    }
                    else {





                        try {
                            new SetStatus(providerID, "deactive").execute();

                            statusSwitchProvider.getThumbDrawable().setColorFilter(ContextCompat.getColor(activity, R.color.slider_red), PorterDuff.Mode.SRC_IN);
                            statusSwitchProvider.getTrackDrawable().setColorFilter(ContextCompat.getColor(activity, R.color.slider_off_color_red), PorterDuff.Mode.SRC_IN);


                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }



                    }




            }
        });



        return view;
    }


    private class GetStatus extends AsyncTask<Void, Void, ProviderStatusResponse> {


        String providerId;

        public GetStatus(String providerId) {
            this.providerId = providerId;
        }


        @Override
        protected ProviderStatusResponse doInBackground(Void... voids) {
            // Create Retrofit instance
            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);


            // Make API call
            Call<ProviderStatusResponse> call = apiInterface.getProviderStatus(providerId);

            try {
                // Execute the call synchronously
                Response<ProviderStatusResponse> response = call.execute();

                if (response.isSuccessful()) {
                    return response.body();
                } else {
                    // Handle unsuccessful response


                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            swipeRefreshLayout.setRefreshing(false);
                            statusSwitchProvider.setEnabled(true);

                        }
                    });

                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        statusSwitchProvider.setEnabled(true);

                    }
                });
                return null;
            }
        }

        @Override
        protected void onPostExecute(ProviderStatusResponse apiResponse) {
            super.onPostExecute(apiResponse);

            swipeRefreshLayout.setRefreshing(false);
            statusSwitchProvider.setEnabled(true);


            if (apiResponse != null) {
                // Handle the API response

                if (apiResponse.isSuccess()) {


                    if (apiResponse.getMessage().equals("Data retrieved successfully")) {


                        String status = apiResponse.getData().getStatus();

                        if (status.equals("active")) {
//                            statusSwitchProvider.setSliderOffColor(getResources().getColor(R.color.slider_red));



                            activeLay.setVisibility(View.VISIBLE);
                            deactiveLay.setVisibility(View.GONE);
                              statusSwitchProvider.getThumbDrawable().setColorFilter(ContextCompat.getColor(activity, R.color.slider_green), PorterDuff.Mode.SRC_IN);
                            statusSwitchProvider.getTrackDrawable().setColorFilter(ContextCompat.getColor(activity, R.color.green_on), PorterDuff.Mode.SRC_IN);

                            statusSwitchProvider.setChecked(true);

//
                        }

                        if (status.equals("deactive")) {




                            activeLay.setVisibility(View.GONE);
                            deactiveLay.setVisibility(View.VISIBLE);
//
                            statusSwitchProvider.getThumbDrawable().setColorFilter(ContextCompat.getColor(activity, R.color.slider_red), PorterDuff.Mode.SRC_IN);
                            statusSwitchProvider.getTrackDrawable().setColorFilter(ContextCompat.getColor(activity, R.color.slider_off_color_red), PorterDuff.Mode.SRC_IN);

                            statusSwitchProvider.setChecked(false);



                        }


                    }else {

//                        statusSwitchProvider.setThumbResource(R.drawable.slider_red);
//                        statusSwitchProvider.setTrackResource(R.drawable.slider_off_color_red);

                        statusSwitchProvider.getThumbDrawable().setColorFilter(ContextCompat.getColor(activity, R.color.slider_red), PorterDuff.Mode.SRC_IN);
                        statusSwitchProvider.getTrackDrawable().setColorFilter(ContextCompat.getColor(activity, R.color.slider_off_color_red), PorterDuff.Mode.SRC_IN);

                        statusSwitchProvider.setChecked(false);

//                        statusSwitchProvider.setSliderOnColor(getResources().getColor(R.color.slider_green));

                    }


                } else {


                    Log.d(TAG, "onPostExecute: "+apiResponse.getMessage());
//                    statusSwitchProvider.setThumbResource(R.drawable.slider_red);



                    statusSwitchProvider.setChecked(false);
                    statusSwitchProvider.getThumbDrawable().setColorFilter(ContextCompat.getColor(activity, R.color.slider_red), PorterDuff.Mode.SRC_IN);
                    statusSwitchProvider.getTrackDrawable().setColorFilter(ContextCompat.getColor(activity, R.color.slider_off_color_red), PorterDuff.Mode.SRC_IN);

//                    statusSwitchProvider.setTrackResource(R.drawable.slider_off_color_red);
                }

            } else {



//                statusSwitchProvider.setThumbResource(R.drawable.slider_red);
                statusSwitchProvider.setChecked(false);
                statusSwitchProvider.getThumbDrawable().setColorFilter(ContextCompat.getColor(activity, R.color.slider_red), PorterDuff.Mode.SRC_IN);
                statusSwitchProvider.getTrackDrawable().setColorFilter(ContextCompat.getColor(activity, R.color.slider_off_color_red), PorterDuff.Mode.SRC_IN);


//                statusSwitchProvider.setTrackResource(R.drawable.slider_off_color_red);
            }


        }
    }

    private class SetStatus extends AsyncTask<Void, Void, SuccessMessageResponse> {


        String providerId;
        String status;

        public SetStatus(String providerId, String status) {
            this.providerId = providerId;
            this.status = status;
        }

        @Override
        protected SuccessMessageResponse doInBackground(Void... voids) {
            // Create Retrofit instance
            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);


            // Make API call
            Call<SuccessMessageResponse> call = apiInterface.setProviderStatus(providerId, status);

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

                        }
                    });

                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
                return null;
            }
        }

        @Override
        protected void onPostExecute(SuccessMessageResponse apiResponse) {
            super.onPostExecute(apiResponse);


            if (apiResponse != null) {
                // Handle the API response

                if (apiResponse.isSuccess()) {


                    if (apiResponse.getMessage().equals("Row updated successfully") || apiResponse.getMessage().equals("New row inserted successfully")) {



                        if (switchChangedByUser){
                            CustomToastPositive.create(activity,"Status Updated!");

                        }


                        if (status.equals("active")) {


                            activeLay.setVisibility(View.VISIBLE);
                            deactiveLay.setVisibility(View.GONE);

//                            statusSwitchProvider.setChecked(true);
//                            statusSwitchProvider.setSliderOnColor(getResources().getColor(R.color.slider_green));
                            statusSwitchProvider.getThumbDrawable().setColorFilter(ContextCompat.getColor(activity, R.color.slider_green), PorterDuff.Mode.SRC_IN);
                            statusSwitchProvider.getTrackDrawable().setColorFilter(ContextCompat.getColor(activity, R.color.green_on), PorterDuff.Mode.SRC_IN);

                        }

                        if (status.equals("deactive")) {


                            activeLay.setVisibility(View.GONE);
                            deactiveLay.setVisibility(View.VISIBLE);
                            statusSwitchProvider.getThumbDrawable().setColorFilter(ContextCompat.getColor(activity, R.color.slider_red), PorterDuff.Mode.SRC_IN);
                            statusSwitchProvider.getTrackDrawable().setColorFilter(ContextCompat.getColor(activity, R.color.slider_off_color_red), PorterDuff.Mode.SRC_IN);

//                            statusSwitchProvider.setChecked(false);
//                            statusSwitchProvider.setSliderOffColor(getResources().getColor(R.color.slider_red));

                        }

                    }


                } else {

                    Log.d(TAG, "SERVER ERROR 1"+apiResponse.getMessage());

                }

            } else {

                Log.d(TAG, "SERVER ERROR 2");

            }


        }
    }


    private class GetServicesCount extends AsyncTask<Void, Void, YourServicesCount> {


        int providerId;

        public GetServicesCount(int providerId) {
            this.providerId = providerId;
        }

        @Override
        protected YourServicesCount doInBackground(Void... voids) {
            // Create Retrofit instance
            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);


            // Make API call
            Call<YourServicesCount> call = apiInterface.getMyServicesCount(String.valueOf(providerId));

            try {
                // Execute the call synchronously
                Response<YourServicesCount> response = call.execute();

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
        protected void onPostExecute(YourServicesCount apiResponse) {
            super.onPostExecute(apiResponse);

            Log.d("KJSGJSGSFJFS", "RESPONSE Geting");

            if (apiResponse != null) {
                // Handle the API response
                Log.d("KJSGJSGSFJFS", "RESPONSE Not Null" + apiResponse.isSuccess());
                Log.d("KJSGJSGSFJFS", "RESPONSE Not Null" + apiResponse.getMessage());


                if (apiResponse.isSuccess() && apiResponse.getMessage().equals("Service counts retrieved successfully.")) {
                    Log.d("KJSGJSGSFJFS", "RESPONSE Success");

                    String totalServices = apiResponse.getTotal_services();
                    String enabledServices = apiResponse.getEnabled_services();
                    String disabledServices = apiResponse.getDisabled_services();


                    totalTxt.setText(totalServices);
                    activeTxt.setText(enabledServices);
                    notActiveTxt.setText(disabledServices);


                    if (Integer.parseInt(totalServices)<=0){

                        AddServiceFirstTimeBottomSheet bottomSheet = new AddServiceFirstTimeBottomSheet(activity);
                        bottomSheet.show(getParentFragmentManager(), bottomSheet.getTag());

                    }


                }

            } else {

                Log.d(TAG, "SERVER ERROR 3");

            }


        }
    }

    private class GetOrderCount extends AsyncTask<Void, Void, OrderCount> {


        int providerId;

        public GetOrderCount(int providerId) {
            this.providerId = providerId;
        }

        @Override
        protected OrderCount doInBackground(Void... voids) {
            // Create Retrofit instance
            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);


            // Make API call
            Call<OrderCount> call = apiInterface.getOrderCount(String.valueOf(providerId));

            try {
                // Execute the call synchronously
                Response<OrderCount> response = call.execute();

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
        protected void onPostExecute(OrderCount apiResponse) {
            super.onPostExecute(apiResponse);

            Log.d("KJSGJSGSFJFS", "RESPONSE Geting");

            if (apiResponse != null) {
                // Handle the API response
                Log.d("KJSGJSGSFJFS", "RESPONSE Not Null" + apiResponse.isSuccess());
                Log.d("KJSGJSGSFJFS", "RESPONSE Not Null" + apiResponse.getMessage());


                if (apiResponse.isSuccess() && apiResponse.getMessage().equals("Order count retrieved successfully.")) {
                    Log.d("KJSGJSGSFJFS", "RESPONSE Success");

                    int orderCount = apiResponse.getOrder_count();

                    if (orderCount > 0) {



                        recyclerOrders.setVisibility(View.VISIBLE);
                        noDataText.setVisibility(View.GONE);

//                        helper.createNotification(activity, "Pending Orders..", "You Have Total '' " + orderCount + " '' Orders Pending.! Complete Them Or Delete To Dismiss Notification!");


                        Intent serviceIntent = new Intent(activity, OrderNotifcationService.class);

                        Bundle gg = new Bundle();
                        gg.putString("title","Pending Orders..");
                        gg.putString("content","You Have Total '' " + orderCount + " '' Orders Pending.! Complete Them Or Delete To Dismiss Notification!");
                        serviceIntent.putExtras(gg);

                        if (!isServiceRunning(OrderNotifcationService.class)) {
                            activity.startService(serviceIntent);
                        }

                        orderCountTxt.setText(": "+ orderCount);

                    } else {

//                        helper.dismissNotification(activity);

                        //dismiss notification


                        Intent intent = new Intent();
                        intent.setAction(ACTION_DISMISS_NOTIFICATION);
                        activity.sendBroadcast(intent);


                        recyclerOrders.setVisibility(View.GONE);
                        noDataText.setVisibility(View.VISIBLE);




                    }


                }

            } else {
                Log.d(TAG, "SERVER ERROR 4");

            }


        }
    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;

    }


    private class GetOrdersTask extends AsyncTask<Void, Void, OrderListResponse> {


        int providerId, page, itemPerPage;

        public GetOrdersTask(int providerId, int page, int itemPerPage) {
            this.providerId = providerId;
            this.page = page;
            this.itemPerPage = itemPerPage;
        }

        @Override
        protected OrderListResponse doInBackground(Void... voids) {
            // Create Retrofit instance
            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);


            // Make API call
            Call<OrderListResponse> call = apiInterface.getOrders(providerId, page, itemPerPage);

            try {
                // Execute the call synchronously
                Response<OrderListResponse> response = call.execute();

                if (response.isSuccessful()) {
                    return response.body();
                } else {
                    // Handle unsuccessful response
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {

                            shimmerFrameLayout.setVisibility(View.GONE);
                            shimmerFrameLayout.stopShimmer();
                        }
                    });
                    return null;
                }
            } catch (IOException e) {

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {

                        shimmerFrameLayout.setVisibility(View.GONE);
                        shimmerFrameLayout.stopShimmer();
                    }
                });
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(OrderListResponse apiResponse) {
            super.onPostExecute(apiResponse);

            progressBar.setVisibility(View.GONE);
            recyclerOrders.setVisibility(View.VISIBLE);

            shimmerFrameLayout.setVisibility(View.GONE);
            shimmerFrameLayout.stopShimmer();


            Log.d("ksxln", "RESPONSE Geting");

            if (apiResponse != null) {
                // Handle the API response
                Log.d("ksxln", "RESPONSE GOT" + apiResponse.isSuccess());

                isLoading = false;


                if (currentPage == 1) {
                    // If it's the first page, clear the old list
                    orderResponses.clear();
                    adapter.notifyDataSetChanged();
                }

                List<Order> newList = apiResponse.getOrder();


                if (newList != null && !newList.isEmpty()) {

                    Log.d("ksxln", "main list : " + newList.size());
                    Log.d("ksxln", "main list : " + newList.get(0).getServiceName());


                    orderResponses.addAll(newList);
                    adapter.setData(orderResponses);
                    // Move notifyDataSetChanged outside the loop
                    adapter.notifyDataSetChanged();

                    if (newList.size() < itemsPerPage) {
                        isLastPage = true;
                    }
                } else if (currentPage > 1) {
                    // If no new data on a subsequent page, consider it the last page
                    isLastPage = true;
                }


            } else {
                Log.d(TAG, "SERVER ERROR 5");

            }


        }
    }

    private void initBundle(Activity activity) {

        SharedPreferences preferences = activity.getSharedPreferences("provider", Context.MODE_PRIVATE);

        if (preferences != null) {
            providerID = preferences.getString("id", "0");
        }

    }


    @Override
    public void onResume() {
        super.onResume();
        currentPage = 1;
        isLoading = false;
        isLastPage = false;
        orderResponses.clear();
        adapter.notifyDataSetChanged();

        try {

            new GetOrdersTask(Integer.parseInt(providerID), currentPage, itemsPerPage).execute();
            new GetOrderCount(Integer.parseInt(providerID)).execute();


            adapter.notifyDataSetChanged();


        } catch (Exception e) {

            Log.d(TAG, " ERROR 5");

        }

    }


}
