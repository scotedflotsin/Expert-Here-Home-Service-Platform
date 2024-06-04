package www.experthere.in.users;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRatingBar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;
import www.experthere.in.Common.BrowserActivity;
import www.experthere.in.FCM.FcmApiClient;
import www.experthere.in.FCM.FcmBearerTokenGenerator;
import www.experthere.in.R;
import www.experthere.in.api.ApiClient;
import www.experthere.in.api.ApiInterface;
import www.experthere.in.helper.CustomToastNegative;
import www.experthere.in.helper.CustomToastPositive;
import www.experthere.in.helper.ImageCompressActivity;
import www.experthere.in.helper.ImagePickerBottomSheet;
import www.experthere.in.helper.TextMaskUtil;
import www.experthere.in.model.FCMData;
import www.experthere.in.model.Review;
import www.experthere.in.model.ReviewDetailsModel;
import www.experthere.in.model.SuccessMessageResponse;
import www.experthere.in.model.TokenResponse;

public class AddReviewActivity extends AppCompatActivity {

    String userID, serviceID, providerID, reviewId = "0";
    String title = " ";

    AppCompatRatingBar ratingBar;
    TextInputEditText reviewEt;
    TextView etLengthTxt;
    LinearLayout uploadBtn;
    boolean isImageSelected;

    ImageView imgShow;

    String selectedImageUri;

    ProgressBar progressBarReview;

    boolean isEdit;

    static Review reviewDetails = null;
    boolean changesMade;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.white, this.getTheme()));

        setContentView(R.layout.activity_new_review);
        initDataBundle();

        TextView titleTxt = findViewById(R.id.titleUser);
        TextMaskUtil.maskText(titleTxt, "Rate To: " + title, 30, "..");

        ratingBar = findViewById(R.id.ratingBarNew);
        uploadBtn = findViewById(R.id.uploadLogo);
        reviewEt = findViewById(R.id.reviewEt);
        etLengthTxt = findViewById(R.id.etLengthDisplay);
        imgShow = findViewById(R.id.imgShowLogo);
        progressBarReview = findViewById(R.id.progressReview);



        findViewById(R.id.learnMoreBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                SharedPreferences sharedPref = getSharedPreferences("keys", Context.MODE_PRIVATE);

                String websiteUrl = sharedPref.getString("url_review_learn_more", "null");




                Intent intent = new Intent(AddReviewActivity.this, BrowserActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString("title","Expert Here Reviews Policy");


                bundle.putString("url",websiteUrl);

                intent.putExtras(bundle);
                startActivity(intent);



            }
        });


        findViewById(R.id.delBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                try {
                    new DeleteReview(Integer.parseInt(reviewId), new DeleteReview.DeleteReviewListener() {
                        @Override
                        public void onSuccess(SuccessMessageResponse apiResponse) {

                            if (apiResponse.getMessage().equals("Review deleted successfully.")) {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        CustomToastPositive.create(getApplicationContext(),"Deleted Successfully!");

                                        finish();
                                    }
                                });



                            } else {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        CustomToastNegative.create(getApplicationContext(),"Error Deleting Review Try Again Later! ");

                                    }
                                });


                            }

                        }

                        @Override
                        public void onError(String errorMessage) {

                        }
                    }).execute();
                } catch (Exception e) {
                    throw new RuntimeException(e);

                }

            }
        });


        reviewEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                etLengthTxt.setText(charSequence.length() + "/500");

                if (charSequence.length() >= 500) {
                    etLengthTxt.startAnimation(AnimationUtils.loadAnimation(AddReviewActivity.this, R.anim.shake));


                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        if (isEdit) {
            //get Review Details
            TextMaskUtil.maskText(titleTxt, "Edit Rating: " + title, 30, "..");

            try {
                new GetReviewDetails(reviewId).execute();

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            findViewById(R.id.delBtn).setVisibility(View.VISIBLE);

        } else {
            findViewById(R.id.delBtn).setVisibility(View.GONE);

        }


        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ImagePickerBottomSheet imagePickerBottomSheet = new ImagePickerBottomSheet(1010, 1016);
                imagePickerBottomSheet.show(getSupportFragmentManager(), imagePickerBottomSheet.getTag());

                imagePickerBottomSheet.setImageSelectionListener(new ImagePickerBottomSheet.ImageSelectionListener() {
                    @Override
                    public void onImageSelected(Uri imageUri) {


                        Intent intent = new Intent(AddReviewActivity.this, ImageCompressActivity.class);

                        Bundle bundle = new Bundle();
                        bundle.putString("image", "doc");
                        bundle.putString("originalUri", String.valueOf(imageUri));
                        bundle.putInt("length", 1);
                        bundle.putInt("height", 1);
                        intent.putExtras(bundle);
                        startActivityForResult(intent, 10001);


                    }

                    @Override
                    public void onImagePath(Uri imageUri) {

                        Intent intent = new Intent(AddReviewActivity.this, ImageCompressActivity.class);

                        Bundle bundle = new Bundle();
                        bundle.putString("image", "doc");
                        bundle.putString("originalUri", String.valueOf(imageUri));
                        bundle.putInt("length", 1);
                        bundle.putInt("height", 1);
                        intent.putExtras(bundle);
                        startActivityForResult(intent, 10001);


                    }
                });

            }
        });

        findViewById(R.id.backReview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();

            }
        });
        findViewById(R.id.submitReviewBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isEdit) {

                    if (changesMade) {


                        if (selectedImageUri != null) {
                            String uro = getImageRealPathFromURI(Uri.parse(selectedImageUri));

                            EditReviewTask submitReviewTask = new EditReviewTask(Integer.parseInt(reviewId), reviewEt.getText().toString().trim(), ratingBar.getProgress(), uro, new EditReviewTask.EditReviewListener() {
                                @Override
                                public void onSuccess(SuccessMessageResponse apiResponse) {


                                    if (apiResponse.isSuccess()) {


                                        if (apiResponse.getMessage().equals("Review updated successfully.")) {

                                            new FetchProviderToken2(providerID).execute();

                                        } else {



                                        }


                                    } else {
                                    }
                                }

                                @Override
                                public void onError(String errorMessage) {

                                }
                            });

                            submitReviewTask.execute();
                        } else {


                            EditReviewTask submitReviewTask = new EditReviewTask(Integer.parseInt(reviewId), reviewEt.getText().toString().trim(), ratingBar.getProgress(), null, new EditReviewTask.EditReviewListener() {
                                @Override
                                public void onSuccess(SuccessMessageResponse apiResponse) {


                                    if (apiResponse.isSuccess()) {


                                        if (apiResponse.getMessage().equals("Review updated successfully.")) {
                                            new FetchProviderToken2(providerID).execute();


                                        } else {


                                            CustomToastNegative.create(getApplicationContext(),"Error: "+apiResponse.getMessage());

                                        }


                                    } else {
                                    }
                                }

                                @Override
                                public void onError(String errorMessage) {

                                }
                            });

                            submitReviewTask.execute();

                        }


                    } else {

                        CustomToastNegative.create(getApplicationContext(),"No Changes Made!");

                    }


                } else {


                    if (ratingBar.getProgress() >= 1) {

                        if (!reviewEt.getText().toString().isEmpty()) {

                            if (isImageSelected) {


                                int progressStars = ratingBar.getProgress();
                                String writtenReview = reviewEt.getText().toString();

                                String realUri = getImageRealPathFromURI(Uri.parse(selectedImageUri));

                                File imageFile = new File(realUri);

                                if (imageFile.exists()) {


                                    try {


                                        progressBarReview.setVisibility(View.VISIBLE);
                                        findViewById(R.id.submitReviewBtn).setVisibility(View.GONE);


                                        SubmitReviewTask submitReviewTask = new SubmitReviewTask(Integer.parseInt(userID), Integer.parseInt(serviceID), Integer.parseInt(providerID), writtenReview, progressStars, imageFile
                                                , new SubmitReviewTask.SubmitReviewListener() {
                                            @Override
                                            public void onSuccess(SuccessMessageResponse apiResponse) {

                                                progressBarReview.setVisibility(View.GONE);
                                                findViewById(R.id.submitReviewBtn).setVisibility(View.VISIBLE);

                                                if (apiResponse.isSuccess()) {



                                                    if (apiResponse.getMessage().equals("Review added successfully.")) {

                                                        CustomToastPositive.create(getApplicationContext()," "+apiResponse.getMessage());


                                                        finish();
                                                    } else {
                                                        CustomToastNegative.create(getApplicationContext(),"Error: "+apiResponse.getMessage());

                                                        finish();
                                                    }


                                                } else {
                                                }

                                            }

                                            @Override
                                            public void onError(String errorMessage) {
                                                progressBarReview.setVisibility(View.GONE);
                                                findViewById(R.id.submitReviewBtn).setVisibility(View.VISIBLE);

                                                CustomToastNegative.create(getApplicationContext(),"Error: "+errorMessage);

                                            }
                                        });

                                        submitReviewTask.execute();


                                    } catch (NumberFormatException e) {
                                        throw new RuntimeException(e);

                                    }


                                } else {

                                    CustomToastNegative.create(getApplicationContext(),"Reselect Image File!");

                                }


                            } else {
                                CustomToastNegative.create(getApplicationContext(),"Select Image File!");
                            }


                        } else {

                            reviewEt.setError("Enter Your Experience!");

                        }


                    } else {

                        CustomToastNegative.create(getApplicationContext(),"Please Give Star Rating!");
                    }
                }

            }
        });


    }

    private void initDataBundle() {

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            userID = bundle.getString("user_id");
            serviceID = bundle.getString("service_id");
            providerID = bundle.getString("provider_id");
            reviewId = bundle.getString("review_id");
            title = bundle.getString("title");
            isEdit = bundle.getBoolean("isEdit", false);

        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 10001) {
            if (resultCode == RESULT_OK && data != null) {

                Bundle bundle = data.getExtras();

                if (bundle != null) {

                    String croppedImageUri = bundle.getString("croppedImageUri");

                    if (bundle.getString("image", "none").matches("doc")) {

                        isImageSelected = true;
                        changesMade = true;

                        selectedImageUri = croppedImageUri;
                        Glide.with(AddReviewActivity.this).load(croppedImageUri).into(imgShow);
                        imgShow.setVisibility(View.VISIBLE);
                        findViewById(R.id.uploadViewLayLogo).setVisibility(View.GONE);


                    }


                } else {
                }
            }

        }


    }


    boolean isUpdatedReview;


    private class GetReviewDetails extends AsyncTask<Void, Void, ReviewDetailsModel> {


        private final String reviewId;


        public GetReviewDetails(String reviewId) {
            this.reviewId = reviewId;
        }

        @Override
        protected ReviewDetailsModel doInBackground(Void... params) {
            // The doInBackground method remains the same as in the previous example

            try {

                // Create ReviewService instance
                ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);


                // Make the API call
                Call<ReviewDetailsModel> call = apiInterface.getReviewDetails(reviewId);

                // Execute the call synchronously
                Response<ReviewDetailsModel> response = call.execute();

                // Check if the response is successful
                if (response.isSuccessful() && response.body() != null) {

                    return response.body();


                } else {
                    return null;
                }
            } catch (IOException e) {
                Log.e("SubmitReviewTask", "Error making API call", e);
                return null;
            }

        }

        @Override
        protected void onPostExecute(ReviewDetailsModel apiResponse) {
            // The onPostExecute method remains the same as in the previous example


            if (apiResponse != null && apiResponse.isSuccess()) {
                // API call successful

                if (apiResponse.getReview() != null) {

                    reviewDetails = apiResponse.getReview();


                    Glide.with(getApplicationContext()).load(reviewDetails.getReview_image_url()).into(imgShow);
                    imgShow.setVisibility(View.VISIBLE);
                    findViewById(R.id.uploadViewLayLogo).setVisibility(View.GONE);
                    ratingBar.setProgress(Integer.parseInt(reviewDetails.getStars()));
                    reviewEt.setText(reviewDetails.getReview());

                    isUpdatedReview = true;

                    ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                        @Override
                        public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {

                            changesMade = true;

                        }
                    });
                    reviewEt.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {


                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            changesMade = true;
                        }

                        @Override
                        public void afterTextChanged(Editable editable) {

                        }
                    });


                } else {

                    CustomToastNegative.create(getApplicationContext(),"No Review Found On Server!");


                }
            } else {

                // API call failed

            }


        }
    }

    private static class SubmitReviewTask extends AsyncTask<Void, Void, SuccessMessageResponse> {


        private final int userId;
        private final int serviceId;
        private final int providerId;
        private final String reviewText;
        private final int stars;
        private final File imageFile;
        private final SubmitReviewListener listener;

        interface SubmitReviewListener {
            void onSuccess(SuccessMessageResponse apiResponse);

            void onError(String errorMessage);
        }

        public SubmitReviewTask(int userId, int serviceId, int providerId, String reviewText, int stars, File imageFile, SubmitReviewListener listener) {
            this.userId = userId;
            this.serviceId = serviceId;
            this.providerId = providerId;
            this.reviewText = reviewText;
            this.stars = stars;
            this.imageFile = imageFile;
            this.listener = listener;
        }

        @Override
        protected SuccessMessageResponse doInBackground(Void... params) {
            // The doInBackground method remains the same as in the previous example

            try {

                Log.e("SubmitReviewTask", "TRYING");

                // Prepare the image file for upload
                RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), imageFile);
                MultipartBody.Part reviewImage = MultipartBody.Part.createFormData("review_image", imageFile.getName(), requestBody);


                // Create ReviewService instance
                ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);


                Log.d("SUN<SSJJSJBJ", "data " + userId);
                Log.d("SUN<SSJJSJBJ", "data " + serviceId);
                Log.d("SUN<SSJJSJBJ", "data " + providerId);
                Log.d("SUN<SSJJSJBJ", "data " + reviewText);
                Log.d("SUN<SSJJSJBJ", "data " + stars);
                Log.d("SUN<SSJJSJBJ", "data " + imageFile.getPath());


                // Make the API call
                Call<SuccessMessageResponse> call = apiInterface.submitReview(
                        RequestBody.create(MediaType.parse("text/plain"), String.valueOf(userId)),
                        RequestBody.create(MediaType.parse("text/plain"), String.valueOf(serviceId)),
                        RequestBody.create(MediaType.parse("text/plain"), String.valueOf(providerId)),
                        RequestBody.create(MediaType.parse("text/plain"), reviewText),
                        RequestBody.create(MediaType.parse("text/plain"), String.valueOf(stars)),
                        reviewImage
                );

                // Execute the call synchronously
                Response<SuccessMessageResponse> response = call.execute();

                // Check if the response is successful
                if (response.isSuccessful() && response.body() != null) {

                    return response.body();


                } else {
                    return null;
                }
            } catch (IOException e) {
                Log.e("SubmitReviewTask", "Error making API call", e);
                return null;
            }

        }

        @Override
        protected void onPostExecute(SuccessMessageResponse apiResponse) {
            // The onPostExecute method remains the same as in the previous example


            if (apiResponse != null && apiResponse.isSuccess()) {
                // API call successful
                listener.onSuccess(apiResponse);


            } else {

                // API call failed
                listener.onError("Error submitting review");
            }


        }
    }

    private static class EditReviewTask extends AsyncTask<Void, Void, SuccessMessageResponse> {


        private final int reviewId;
        private final String reviewText;
        private final int stars;
        private final String imagePath;
        private final EditReviewListener listener;

        interface EditReviewListener {
            void onSuccess(SuccessMessageResponse apiResponse);

            void onError(String errorMessage);
        }

        public EditReviewTask(int reviewId, String reviewText, int stars, String imagePath, EditReviewListener listener) {
            this.reviewId = reviewId;
            this.reviewText = reviewText;
            this.stars = stars;
            this.imagePath = imagePath;
            this.listener = listener;
        }

        @Override
        protected SuccessMessageResponse doInBackground(Void... params) {
            // The doInBackground method remains the same as in the previous example

            try {

                Log.e("SubmitReviewTask", "TRYING");


                // Create ReviewService instance
                ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);


                Log.d("SUN<SSJJSJBJ", "data " + reviewId);
                Log.d("SUN<SSJJSJBJ", "data " + reviewText);
                Log.d("SUN<SSJJSJBJ", "data " + stars);
                Log.d("SUN<SSJJSJBJ", "data " + imagePath);


                RequestBody reviewIdBody = createRequestBody(String.valueOf(reviewId));
                RequestBody reviewBody = createRequestBody(String.valueOf(reviewText));
                RequestBody starsBody = createRequestBody(String.valueOf(stars));

                MultipartBody.Part imagePart = null;

                if (imagePath != null) {

                    imagePart = createImagePart(imagePath);


                }


                // Make the API call
                Call<SuccessMessageResponse> call = apiInterface.editReview(reviewIdBody, reviewBody, starsBody, imagePart);

                // Execute the call synchronously
                Response<SuccessMessageResponse> response = call.execute();

                // Check if the response is successful
                if (response.isSuccessful() && response.body() != null) {

                    return response.body();


                } else {
                    return null;
                }
            } catch (IOException e) {
                Log.e("SubmitReviewTask", "Error making API call", e);
                return null;
            }

        }

        @Override
        protected void onPostExecute(SuccessMessageResponse apiResponse) {
            // The onPostExecute method remains the same as in the previous example


            if (apiResponse != null && apiResponse.isSuccess()) {
                // API call successful
                listener.onSuccess(apiResponse);


            } else {

                // API call failed
                listener.onError("Error submitting review");
            }


        }
    }


    public String getImageRealPathFromURI(Uri contentUri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String filePath = cursor.getString(column_index);
            cursor.close();
            return filePath;
        }
        return contentUri.getPath(); // Fallback to the URI itself
    }


    static RequestBody createRequestBody(String value) {
        // Check for null and return appropriate RequestBody
        if (value != null) {
            return RequestBody.create(MediaType.parse("text/plain"), value);
        } else {
            return null; // or handle it in a way that makes sense for your API
        }
    }


    private static MultipartBody.Part createImagePart(String imagePath) {
        // Check if imagePath is not null
        if (imagePath != null) {
            File imageFile = new File(imagePath);
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), imageFile);
            return MultipartBody.Part.createFormData("review_image", imageFile.getName(), requestFile);
        } else {
            // Return a default MultipartBody.Part or handle it as per your API requirements
            // For example, return an empty MultipartBody.Part
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), "");
            return MultipartBody.Part.createFormData("review_image", "", requestFile);
        }
    }


    private static class DeleteReview extends AsyncTask<Void, Void, SuccessMessageResponse> {


        private final int reviewId;
        private String reviewText;
        private int stars;
        private String imagePath;
        private final DeleteReviewListener listener;

        interface DeleteReviewListener {
            void onSuccess(SuccessMessageResponse apiResponse);

            void onError(String errorMessage);
        }

        public DeleteReview(int reviewId, DeleteReviewListener listener) {
            this.reviewId = reviewId;
            this.listener = listener;
        }

        @Override
        protected SuccessMessageResponse doInBackground(Void... params) {
            // The doInBackground method remains the same as in the previous example

            try {

                Log.e("SubmitReviewTask", "TRYING");


                // Create ReviewService instance
                ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);


                Log.d("SUN<SSJJSJBJ", "data " + reviewId);


                // Make the API call
                Call<SuccessMessageResponse> call = apiInterface.deleteReview(String.valueOf(reviewId));

                // Execute the call synchronously
                Response<SuccessMessageResponse> response = call.execute();

                // Check if the response is successful
                if (response.isSuccessful() && response.body() != null) {

                    return response.body();


                } else {
                    return null;
                }
            } catch (IOException e) {
                Log.e("SubmitReviewTask", "Error making API call", e);
                return null;
            }

        }

        @Override
        protected void onPostExecute(SuccessMessageResponse apiResponse) {
            // The onPostExecute method remains the same as in the previous example


            if (apiResponse != null && apiResponse.isSuccess()) {
                // API call successful
                listener.onSuccess(apiResponse);


            } else {

                // API call failed
                listener.onError("Error submitting review");
            }


        }
    }


    private class FetchProviderToken2 extends AsyncTask<Void, Void, TokenResponse> {


        String providerID;

        public FetchProviderToken2(String providerID) {
            this.providerID = providerID;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected TokenResponse doInBackground(Void... voids) {

            Log.d("APISEARCHING", "Do IN BG  runs");


            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
//            Call<ApiResponse> call = apiInterface.getServiceUserWithSearchAndFilter(latitude, longitude, page, itemsPerPage, radius, 5, searchQuery);

            retrofit2.Call<TokenResponse> call = apiInterface.getFcmTokenDetailsProvider(providerID);


            try {
                retrofit2.Response<TokenResponse> response = call.execute();
                if (response.isSuccessful() && response.body() != null) {


                    if (response.body().getData() != null) {
                        return response.body();
                    }
                } else {

                }
            } catch (IOException e) {
                e.printStackTrace();

                Log.d("APISEARCHING", "Error " + e.getMessage());


            }

            return null;
        }

        @Override
        protected void onPostExecute(TokenResponse serviceResponse) {
            super.onPostExecute(serviceResponse);


            if (serviceResponse != null) {


                if (serviceResponse.isSuccess() && serviceResponse.getMessage().equals("Provider data retrieved successfully")) {

                    FCMData fcmData = serviceResponse.getData();


                    FcmBearerTokenGenerator tokenGenerator = new FcmBearerTokenGenerator(AddReviewActivity.this);
                    tokenGenerator.generateAccessTokenAsync(new FcmBearerTokenGenerator.TokenGenerationListener() {
                        @Override
                        public void onTokenGenerated(String token) {


                            if (isUpdatedReview) {

                                sendReviewNotificationToProviderUpdated(fcmData,token);

                            } else {
                                sendReviewNotificationToProvider(fcmData, token);
                            }
                        }

                        @Override
                        public void onTokenGenerationFailed(Exception e) {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    CustomToastNegative.create(getApplicationContext(),"Error: "+e.getMessage());

                                }
                            });

                        }
                    });


                } else {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CustomToastNegative.create(getApplicationContext(),"Data Not Found!");

                        }
                    });


                }


            }


        }


    }

    private void sendReviewNotificationToProvider(FCMData fcmData, String token) {
        Map<String, String> EXTRA_DATA = new HashMap<>();
        String REVIEW_NOTIFICATION_TO_USER = "www.experthere.in.NEW_REVIEW_ADDED";

        SharedPreferences preferencesUser = getSharedPreferences("user", MODE_PRIVATE);

        EXTRA_DATA.put("reviewer_name", preferencesUser.getString("name", "Expert Here Provider"));
        EXTRA_DATA.put("user_icon", preferencesUser.getString("dp", "Expert Here Provider"));
        EXTRA_DATA.put("event", REVIEW_NOTIFICATION_TO_USER);
        EXTRA_DATA.put("timestamp", String.valueOf(System.currentTimeMillis()));
        EXTRA_DATA.put("updated", "false");
        EXTRA_DATA.put("stars", String.valueOf(ratingBar.getProgress()));
        EXTRA_DATA.put("review", reviewEt.getText().toString());



        FcmApiClient.sendFcmMessageToDevice(getString(R.string.FcmSendUrl), token, fcmData.getFcm_token(), "Call Decline", "decline by user", EXTRA_DATA, "rejecte", new Callback() {
            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        CustomToastNegative.create(getApplicationContext(),"Error: "+e.getMessage());

                    }
                });


            }

            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) throws IOException {


                CustomToastPositive.create(getApplicationContext(),"Review Added Successfully!");
                finish();


            }
        });
    }

    private void sendReviewNotificationToProviderUpdated(FCMData fcmData, String token) {
        Map<String, String> EXTRA_DATA = new HashMap<>();
        String REVIEW_NOTIFICATION_TO_USER = "www.experthere.in.NEW_REVIEW_ADDED";

        SharedPreferences preferencesUser = getSharedPreferences("user", MODE_PRIVATE);

        EXTRA_DATA.put("reviewer_name", preferencesUser.getString("name", "Expert Here Provider"));
        EXTRA_DATA.put("user_icon", preferencesUser.getString("dp", "Expert Here Provider"));
        EXTRA_DATA.put("event", REVIEW_NOTIFICATION_TO_USER);
        EXTRA_DATA.put("timestamp", String.valueOf(System.currentTimeMillis()));
        EXTRA_DATA.put("updated", "true");
        EXTRA_DATA.put("stars", String.valueOf(ratingBar.getProgress()));
        EXTRA_DATA.put("review", reviewEt.getText().toString());


        FcmApiClient.sendFcmMessageToDevice(getString(R.string.FcmSendUrl), token, fcmData.getFcm_token(), "Call Decline", "decline by user", EXTRA_DATA, "rejecte", new Callback() {
            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {


                        CustomToastNegative.create(getApplicationContext(),"Error: "+e.getMessage());


                    }
                });


            }

            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) throws IOException {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CustomToastPositive.create(getApplicationContext(),"Review Updated Successfully!");

                        finish();


                    }
                });




            }
        });
    }




}
