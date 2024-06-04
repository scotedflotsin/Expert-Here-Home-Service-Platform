package www.experthere.in.serviceProvider;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;
import www.experthere.in.R;
import www.experthere.in.api.ApiClient;
import www.experthere.in.api.ApiInterface;
import www.experthere.in.helper.CustomToastNegative;
import www.experthere.in.helper.CustomToastPositive;
import www.experthere.in.helper.app.DateTimePickerHelper;
import www.experthere.in.helper.app.FullScreenListDialog;
import www.experthere.in.model.Order;
import www.experthere.in.model.OrderResponse;
import www.experthere.in.model.service_user_cat.Service;

public class NewOrderActivity extends AppCompatActivity {


    TextView dateTimeTxtView, selectedServiceTxt;
    String selectedDateTime;
    boolean isDateTimeSelected, isServiceSelected;
    Service services;

    String selectedProviderId, selectedServiceId, selectedServiceName;

    TextInputEditText etName, etAddress;

    ProgressBar progressBar;
    private final String TAG = "NEW_ORDER";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_order);

        dateTimeTxtView = findViewById(R.id.selectedDateTimeTxt);
        progressBar = findViewById(R.id.progressBar);
        selectedServiceTxt = findViewById(R.id.selectedCatTxt);
        etName = findViewById(R.id.etName);
        etAddress = findViewById(R.id.etAddress);

        final DateTimePickerHelper dateTimePickerHelper = new DateTimePickerHelper(this);


        findViewById(R.id.selectServiceBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                FullScreenListDialog dialog = new FullScreenListDialog(NewOrderActivity.this, new FullScreenListDialog.getService() {
                    @Override
                    public void getService(Service service) {

                        services = service;


                        if (service != null) {

                            selectedProviderId = String.valueOf(service.getProviderId());
                            selectedServiceId = String.valueOf(service.getId());
                            selectedServiceName = String.valueOf(service.getServiceTitle());

                            isServiceSelected = true;


                            selectedServiceTxt.setText(selectedServiceName.trim());

                        }

                    }
                });
                dialog.show();


            }
        });

        findViewById(R.id.menuBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        findViewById(R.id.selectDateTimeBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                dateTimePickerHelper.showDateTimePicker(new DateTimePickerHelper.DateTimePickerCallback() {
                    @Override
                    public void onDateTimeSelected(String formattedDateTime) {



                        CustomToastPositive.create(getApplicationContext(), formattedDateTime);


                        selectedDateTime = formattedDateTime;
                        isDateTimeSelected = true;
                        dateTimeTxtView.setText(selectedDateTime);


                    }
                });

            }
        });


        findViewById(R.id.createOrderBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = etName.getText().toString().trim();
                String address = etAddress.getText().toString().trim();

                if (!name.isEmpty() && !address.isEmpty()) {

                    if (isDateTimeSelected) {



                        if (isServiceSelected) {




                            try {
                                new PlaceOrderTask(Integer.parseInt(selectedProviderId),Integer.parseInt(selectedServiceId),selectedServiceName,name,address,selectedDateTime).execute();
                            } catch (NumberFormatException e) {

                                Log.d(TAG, "Error 1 "+e.getMessage());

                                throw new RuntimeException(e);

                            }


                        } else {

                            CustomToastNegative.create(getApplicationContext(), "Select Service!");

                        }


                    } else {
                        CustomToastNegative.create(getApplicationContext(), "Select Date And Time!");
                    }


                } else {

                    CustomToastNegative.create(getApplicationContext(), "Fill Name And Address!");
                }


            }
        });

    }

        private class PlaceOrderTask extends AsyncTask<Void, Void, OrderResponse> {


            int providerId, serviceId;
            String serviceName, customerName, address, time;


            public PlaceOrderTask(int providerId, int serviceId, String serviceName, String customerName, String address, String time) {
                this.providerId = providerId;
                this.serviceId = serviceId;
                this.serviceName = serviceName;
                this.customerName = customerName;
                this.address = address;
                this.time = time;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                findViewById(R.id.createOrderBtn).setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);



            }

            @Override
            protected OrderResponse doInBackground(Void... voids) {
                // Create Retrofit instance
                ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);


                // Make API call
                Call<OrderResponse> call = apiInterface.placeOrder(providerId, serviceId, serviceName, customerName, address, time);

                try {
                    // Execute the call synchronously
                    Response<OrderResponse> response = call.execute();

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
            protected void onPostExecute(OrderResponse apiResponse) {
                super.onPostExecute(apiResponse);


                findViewById(R.id.createOrderBtn).setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);


                if (apiResponse != null) {
                    // Handle the API response
                    if (apiResponse.isSuccess()) {
                        // Order placed successfully
                        Order order = apiResponse.getOrder();

                        if (order!=null) {

//                            Toast.makeText(NewOrderActivity.this, "Order placed successfully. Order ID: " + order.getId(), Toast.LENGTH_LONG).show();

                            CustomToastPositive.create(getApplicationContext(), "Order Placed Successfully!");



                            finish();


                        }else {
                            CustomToastNegative.create(getApplicationContext(), "Can't Save: "+apiResponse.getMessage());
                        }



                    } else {
                        // Order placement failed
                        CustomToastNegative.create(getApplicationContext(), "Fail: "+apiResponse.getMessage());                    }
                } else {
                    // Handle error

                    Log.d(TAG, "Error 2 Error in API call");

                }
            }
        }


    }


