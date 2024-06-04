package www.experthere.in.serviceProvider;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import www.experthere.in.R;
import www.experthere.in.adapters.HomeCategoryAdapter;
import www.experthere.in.adapters.ProviderCategoryAdapter;
import www.experthere.in.api.ApiClient;
import www.experthere.in.api.ApiInterface;
import www.experthere.in.model.Category;
import www.experthere.in.model.CategoryResponse;

public class CategoriesListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    LinearLayout progressBar;
    ArrayList<Category> categories;
    private final String TAG = "CAT_LIST_ACTIVITY";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories_list2);

        recyclerView = findViewById(R.id.categoryRecycler);
        progressBar = findViewById(R.id.progress_bar);
        categories = new ArrayList<>();
        findViewById(R.id.backReview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        try {
            new GetCategories(CategoriesListActivity.this).execute();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }


    private class GetCategories extends AsyncTask<Void, Void, Void> {

        Activity activity;

        public GetCategories(Activity activity) {
            this.activity = activity;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            // Perform background operation, e.g., user

            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

            Call<CategoryResponse> call = apiInterface.getCategories();


            call.enqueue(new Callback<CategoryResponse>() {
                @Override
                public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {

                    if (response.isSuccessful() && response.body() != null) {

                        CategoryResponse categoryResponse = response.body();

                        if (categoryResponse.isSuccess()) {

                            categories.addAll(categoryResponse.getCategories());
                            ProviderCategoryAdapter adapter = new ProviderCategoryAdapter(categories, activity);
                            recyclerView.setAdapter(adapter);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
                            recyclerView.setLayoutManager(linearLayoutManager);
                            recyclerView.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);

                        } else {
                            activity.runOnUiThread(() -> {
                                Log.d(TAG, "Error Getting Categories!");

                                progressBar.setVisibility(View.GONE);

                            });


                        }


                    } else {

                        activity.runOnUiThread(() -> {
                            Log.d(TAG, "Category Response Fail");

                            progressBar.setVisibility(View.GONE);

                        });

                    }

                }

                @Override
                public void onFailure(Call<CategoryResponse> call, Throwable t) {

                    activity.runOnUiThread(() -> {
                        Log.d(TAG, "Category Error Fail "+t.getMessage());

                        progressBar.setVisibility(View.GONE);

                    });

                }
            });


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // This method runs on the UI thread and can be used to update the UI after background task completion
        }
    }

}