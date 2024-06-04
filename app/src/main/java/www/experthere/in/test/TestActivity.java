package www.experthere.in.test;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Response;
import www.experthere.in.adapters.CatWiseUserHomeAdapter;

import java.io.IOException;

import www.experthere.in.R;
import www.experthere.in.api.ApiClient;
import www.experthere.in.api.ApiInterface;
import www.experthere.in.model.TestResponse;


public class TestActivity extends AppCompatActivity {
//    Activity activity;
//    RecyclerView catWiseRecycler;
//
//    CatWiseUserHomeAdapter catWiseUserHomeAdapter;
//


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_test);
//        activity = TestActivity.this;
//
//        catWiseRecycler = findViewById(R.id.recycler_Test);
//
//        SharedPreferences preferences = activity.getSharedPreferences("settings", Context.MODE_PRIVATE);
//
//
//        int radiusKm = preferences.getInt("range", 5);
//
//
//       String address = preferences.getString("address", "Select Address");
//       String latitude = preferences.getString("latitude", "0");
//       String longitude = preferences.getString("longitude", "0");
//
//
//
//
//
//        new GetCatList(Double.parseDouble(latitude),Double.parseDouble(longitude),radiusKm).execute();
//
//
//
//
//    }
//    public class GetCatList extends AsyncTask<Void, Void, TestResponse> {
//
//        private final double currentLatitude;
//        private final double currentLongitude;
//
//
//        private final int radius;
//
//
//        int page,itemPerPage;
//
//
//
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//
//
//
//
//        }
//
//        @Override
//        protected TestResponse doInBackground(Void... voids) {
//            try {
//                // Create the Retrofit instance
//                ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
//
//                // Make the API call
////                Call<TestResponse> call = apiInterface.getCategoryServices(
////
////                        currentLatitude, currentLongitude
////                        , radius);
////
//                // Make the API call
//                Call<TestResponse> call = apiInterface.getCategoryServices(
//
//                        currentLatitude, currentLongitude
//                        , radius,,5);
//
//
//                Response<TestResponse> response = call.execute();
//
//                if (response.isSuccessful()) {
//                    return response.body();
//                } else {
//                    // Handle error
//                    Log.e("ApiServiceTask", "API call failed. Code: " + response.code());
//                    return null;
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//                return null;
//            }
//        }
//
//        @Override
//        protected void onPostExecute(TestResponse apiResponse) {
//            super.onPostExecute(apiResponse);
//
//            if (apiResponse != null) {
//                // Handle the API response
//
//
//
//
//                catWiseUserHomeAdapter = new CatWiseUserHomeAdapter(TestActivity.this,apiResponse.getCategories(),getSupportFragmentManager());
//
//                catWiseRecycler.setAdapter(catWiseUserHomeAdapter);
//                catWiseRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
//
//
//
//
//
//            } else {
//                // Handle error
//                Toast.makeText(activity, "Error 1", Toast.LENGTH_SHORT).show();
//            }
//        }

    }

}