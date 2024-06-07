package www.experthere.in;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import www.experthere.in.api.ApiClient;
import www.experthere.in.api.ApiInterface;
import www.experthere.in.helper.CustomToastNegative;
import www.experthere.in.helper.CustomToastPositive;
import www.experthere.in.model.ApiResponse;
import www.experthere.in.users.HomeActivity;

public class LocationSelectActivity extends AppCompatActivity implements OnMapReadyCallback {

    private EditText editTextSearch;
    private ListView listViewSuggestions;
    private ConstraintLayout mapContainer;
    private Button buttonSubmit;
    private GoogleMap googleMap;
    private PlacesClient placesClient;
    private ImageView customImageView;
    TextView selectedPlaceName;
    private List<AutocompletePrediction> predictionList = new ArrayList<>();

    String developerName="Harsh Verma";
    SharedPreferences preferences;
    static SharedPreferences.Editor editor;
    String dataFromFirstActivity = "null";


    private static final int GPS_ENABLE_REQUEST_CODE = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_select);


        SharedPreferences sharedPref = getSharedPreferences("keys", Context.MODE_PRIVATE);

        String apiKey = sharedPref.getString("map_api_key", "null");
        initializeMapsSdk(apiKey);


//        checkIfGpsIsEnabled();
//check gps enabled or not
        checkIfGpsIsEnabled();


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            dataFromFirstActivity = bundle.getString("key", "null");

        }


        editTextSearch = findViewById(R.id.editTextSearch);
        listViewSuggestions = findViewById(R.id.listViewSuggestions);
        mapContainer = findViewById(R.id.mapContainer);
        buttonSubmit = findViewById(R.id.buttonSubmit);
        customImageView = findViewById(R.id.customImageView);
        selectedPlaceName = findViewById(R.id.selectedPlaceName);
        // Initialize Google Maps
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        preferences = getSharedPreferences("user", MODE_PRIVATE);
        editor = preferences.edit();

        // Initialize Places API
        Places.initialize(getApplicationContext(), "AIzaSyBOD5-j2ElNi1GuIbPEZntT1iNLHKassW4");
        placesClient = Places.createClient(this);

        // Set up text change listener for the search EditText
        editTextSearch.addTextChangedListener(new TextWatcher() {

            private final Handler handler = new Handler();
            private final Runnable hideListViewRunnable = new Runnable() {
                @Override
                public void run() {
                    listViewSuggestions.setVisibility(View.GONE);
                }
            };

            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Update suggestions based on the entered text


                // Check if the entered text is empty and hide the ListView after a slight delay
                if (charSequence.length() == 0 || charSequence.toString().isEmpty() || charSequence.toString().trim().length() == 0) {
                    handler.removeCallbacks(hideListViewRunnable); // Remove any previously posted callbacks
                    handler.postDelayed(hideListViewRunnable, 300); // Delay for 300 milliseconds (adjust as needed)
                } else {
                    // If new text is entered, remove any pending callbacks to keep the ListView visible
                    handler.removeCallbacks(hideListViewRunnable);
                }

                updateSuggestions(charSequence.toString());


            }

            @Override
            public void afterTextChanged(Editable editable) {


            }
        });


        // Set up item click listener for the suggestion ListView
        listViewSuggestions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Handle suggestion item click
                AutocompletePrediction item = predictionList.get(position);
                if (item != null) {
                    // Show the selected place on the map
                    showPlaceOnMap(item.getPlaceId());


                    String address = item.getFullText(null).toString();
                    selectedPlaceName.setVisibility(View.VISIBLE);
                    selectedPlaceName.setText(address);


                    hideKeyboard(LocationSelectActivity.this);
                }
            }
        });

        // Set up button click listener
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle the submit button click
                // You can add your logic here


                if (checkIfGpsIsEnabled()) {


                    Dexter.withContext(getApplicationContext())
                            .withPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                            .withListener(new MultiplePermissionsListener() {
                                @Override
                                public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {

                                    if (multiplePermissionsReport.areAllPermissionsGranted()) {

                                        getLocationFromCustomCenterMarker();

                                    } else {

                                        CustomToastNegative.create(getApplicationContext(), "Permissions Required!");


                                        // Inside your activity or fragment
                                        LocationSettingFragment bottomSheetFragment = new LocationSettingFragment(LocationSelectActivity.this);
                                        bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());

//                                    checkIfGpsIsEnabled();

                                    }

//                                if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
//
//                                    CustomToastNegative.create(getApplicationContext(),"Allow Location Permissions From Settings!");
//
//                                    LocationSettingFragment bottomSheetFragment = new LocationSettingFragment(LocationSelectActivity.this);
//                                    bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
//
//                                }

                                }

                                @Override
                                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {

                                    permissionToken.continuePermissionRequest();


                                }
                            }).check();
                }
            }
        });


    }


    // Method to hide the keyboard


    private void getLocationFromCustomCenterMarker() {

        Log.d("UPDATINHHH", "updateUserLocationOnServer: 00");


        if (customImageView != null) {
            float x = customImageView.getX() + customImageView.getWidth() / 2;
            float y = customImageView.getY() + customImageView.getHeight() / 2;

            LatLng imageViewLocation = googleMap.getProjection().fromScreenLocation(new Point((int) x, (int) y));

            if (imageViewLocation != null) {
//                String locationInfo = "ImageView Location\nLatitude: " + imageViewLocation.latitude +
//                        "\nLongitude: " + imageViewLocation.longitude;
//
//                Log.d("LOCATIONTESTT", "getLocationFromCustomCenterMarker: " + locationInfo);

                if (googleMap != null) {
                    // Get the current camera position
                    CameraPosition cameraPosition = googleMap.getCameraPosition();

                    // Get the current zoom level
                    float currentZoom = cameraPosition.zoom;

                    // Show the zoom level in a Toast
                } else {

                    CustomToastNegative.create(getApplicationContext(), "No Map Available!");

                }

                if (!selectedPlaceName.getText().toString().isEmpty()) {
                    if (!selectedPlaceName.getText().toString().matches("Your Address Will Show Here!")) {

                        if (!dataFromFirstActivity.isEmpty() && dataFromFirstActivity.matches("RegisterProvider")) {
                            // Inside SecondActivity
                            Intent resultIntent = new Intent();
                            Bundle b = new Bundle();
                            b.putString("lat", String.valueOf(imageViewLocation.latitude));
                            b.putString("long", String.valueOf(imageViewLocation.longitude));
                            b.putString("address", selectedPlaceName.getText().toString());


                            String city = extractCityFromAddress(selectedPlaceName.getText().toString());
                            String subCity = extractSubCityFromAddress(selectedPlaceName.getText().toString());


                            String citySubcity = city;


                            if (city != null && subCity != null) {

                                citySubcity = city + " " + subCity;


                            }

                            if (city != null && subCity == null) {

                                citySubcity = city;
                            }
                            if (subCity != null && citySubcity == null) {

                                citySubcity = subCity;
                            }


                            b.putString("city", citySubcity.trim());


                            resultIntent.putExtras(b);

//
//                            resultIntent.putExtra("lat", imageViewLocation.longitude);
//                            resultIntent.putExtra("long" ,imageViewLocation.longitude );
//                            resultIntent.putExtra("address",selectedPlaceName.getText().toString());
//                            resultIntent.putExtra("city",extractCityFromAddress(selectedPlaceName.getText().toString()));
                            Log.d("UPDATINHHHhhh", "updateUserLocationOnServer: DONE");
                            setResult(RESULT_OK, resultIntent);


                            finish();

                        } else {

                            Log.d("UPDATINHHHhhh", "updateUserLocationOnServer: 1");


                            updateUserLocationOnServer(imageViewLocation.latitude, imageViewLocation.longitude, selectedPlaceName.getText().toString());
                        }
                    } else {


                        CustomToastNegative.create(getApplicationContext(), "Please Select Your Location !");


                    }

                } else {
                    CustomToastNegative.create(getApplicationContext(), "Please Select Your Location !");
                }


            } else {

                CustomToastNegative.create(getApplicationContext(), "Permission Error !");
            }
        }
    }

    // Add this method to extract the city from the address
    private String extractCityFromAddress(String address) {
        Geocoder geocoder = new Geocoder(LocationSelectActivity.this, Locale.getDefault());

        try {
            List<Address> addresses = geocoder.getFromLocationName(address, 1);
            if (addresses != null && addresses.size() > 0) {
                return addresses.get(0).getLocality();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private String extractSubCityFromAddress(String address) {
        Geocoder geocoder = new Geocoder(LocationSelectActivity.this, Locale.getDefault());

        try {
            List<Address> addresses = geocoder.getFromLocationName(address, 1);
            if (addresses != null && addresses.size() > 0) {
                return addresses.get(0).getSubLocality();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void updateUserLocationOnServer(double latitude, double longitude, String address) {

        Log.d("UPDATINHHH", "updateUserLocationOnServer: ");

        new UpdateUserDetailsTask(getApplicationContext(), this, preferences.getString("email", ""), latitude, longitude, address).execute();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, update the map
                if (googleMap != null) {
                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    googleMap.setMyLocationEnabled(true);
                }
            } else {
                // Permission denied, handle accordingly

                Toast.makeText(LocationSelectActivity.this, "Running", Toast.LENGTH_SHORT).show();


            }
        }
    }

    private void updateSuggestions(String query) {
        // Use the Autocomplete API to get suggestions based on the entered text
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();

        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setSessionToken(token)
                .setQuery(query)
                .build();

        placesClient.findAutocompletePredictions(request).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                predictionList = task.getResult().getAutocompletePredictions();
                // Update the ListView with the suggestions
                updateSuggestionListView();

            } else {
                // Handle error
                Exception exception = task.getException();
                if (exception != null) {
                    Log.e("AutocompleteError", "Error: " + exception.getMessage());
                    CustomToastNegative.create(getApplicationContext(), "Error: " + exception.getMessage());
                } else {
                    Log.e("AutocompleteError", "Unknown error");
                }

                listViewSuggestions.setVisibility(View.GONE);

            }
        });
    }

    private void updateSuggestionListView() {
        CustomAdapter adapter = new CustomAdapter(this, predictionList);
        listViewSuggestions.setAdapter(adapter);
        listViewSuggestions.setVisibility(View.VISIBLE);
    }

    // Inside the showPlaceOnMap method
    private void showPlaceOnMap(String placeId) {
        // Clear existing markers
        googleMap.clear();

        // Use the Place ID to fetch place details
        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG, Place.Field.NAME);
        FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields).build();

        placesClient.fetchPlace(request).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FetchPlaceResponse placeResponse = task.getResult();
                Place place = placeResponse.getPlace();

                // Show the selected place on the map
                LatLng location = place.getLatLng();
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(location));
                listViewSuggestions.setVisibility(View.GONE);
            } else {
                // Handle error
                CustomToastNegative.create(getApplicationContext(), "Error Fetching Places !");

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        // Enable the My Location layer if the permission has been granted.
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);


            // Move the camera to the user's location
            FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            LatLng userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 18.634048f));

                        } else {
                                // Get the SharedPreferences object
                                SharedPreferences userPref = getSharedPreferences("user", MODE_PRIVATE);
                            SharedPreferences providerPref = getSharedPreferences("provider", MODE_PRIVATE);

                            if(userPref.getBoolean("isUser",false)){

                                // Retrieve the latitude and longitude values
                                String latitude = userPref.getString("latitude", null);
                                String longitude = userPref.getString("longitude", null);
                                mapLocatorWithOutGps(latitude,longitude);

                            }else if(providerPref.getBoolean("isProvider",false)){

                                // Retrieve the latitude and longitude values
                                String latitude = providerPref.getString("latitude", null);
                                String longitude = providerPref.getString("longitude", null);
                                mapLocatorWithOutGps(latitude,longitude);


                            }else{
//Set default New Delhi location.
                                mapLocatorWithOutGps("28.6129","77.229446");


                            }








                        }

                    });
        } else {
            // Request the permission.
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }


        // Disable zoom controls
        googleMap.getUiSettings().setZoomControlsEnabled(false);
        googleMap.getUiSettings().setZoomGesturesEnabled(false);

        googleMap.setOnCameraIdleListener(() -> {
            LatLng centerLatLng = googleMap.getCameraPosition().target;

            // Reverse geocode the centerLatLng to get the address
            new ReverseGeocodeTask().execute(centerLatLng);
        });
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    private class ReverseGeocodeTask extends AsyncTask<LatLng, Void, String> {

        @Override
        protected String doInBackground(LatLng... params) {
            LatLng latLng = params[0];
            Geocoder geocoder = new Geocoder(LocationSelectActivity.this, Locale.getDefault());

            try {
                List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                if (addresses != null && addresses.size() > 0) {
                    Address address = addresses.get(0);
                    return address.getAddressLine(0);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                selectedPlaceName.setText(result);
                selectedPlaceName.setVisibility(View.VISIBLE);
            }
        }
    }


    private static class UpdateUserDetailsTask extends AsyncTask<Void, Void, Void> {
        private final Context context;
        private final Activity activity;
        private final String email;
        private final double latitude;
        private final double longitude;
        private final String address;


        public UpdateUserDetailsTask(Context context, Activity activity, String email, double latitude, double longitude, String address) {
            this.context = context;
            this.activity = activity;
            this.email = email;
            this.latitude = latitude;
            this.longitude = longitude;
            this.address = address;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

            apiInterface.updateUserLocation(email, latitude, longitude, address).enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

                    if (response.isSuccessful()) {
                        ApiResponse apiResponse = response.body();

                        if (apiResponse != null && !apiResponse.isError()) {


                            CustomToastPositive.create(context, " " + apiResponse.getMessage());

                            // Handle success, if needed

                            editor.putBoolean("isFullLogin", true);
                            editor.putString("address", address.trim());
                            editor.putString("latitude", String.valueOf(latitude));
                            editor.putString("longitude", String.valueOf(longitude));

                            editor.apply();


                            SharedPreferences preferences1 = activity.getSharedPreferences("settings", MODE_PRIVATE);
                            SharedPreferences.Editor ed = preferences1.edit();

                            ed.putString("address", address.trim());
                            ed.putString("latitude", String.valueOf(latitude));
                            ed.putString("longitude", String.valueOf(longitude));

                            ed.apply();

                            activity.runOnUiThread(() ->
                                    activity.startActivity(new Intent(activity, HomeActivity.class))
                            );
                            activity.runOnUiThread(() ->
                                    activity.finish()
                            );


                        } else {
                            if (apiResponse != null) {
                                CustomToastNegative.create(context, "Error: " + apiResponse.getMessage());
                            }
                        }
                    } else {
                        CustomToastNegative.create(context, "Error Getting Response!");
                    }

                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {


                    CustomToastNegative.create(context, "Error: " + t.getMessage());
                }
            });

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // This method runs on the UI thread and can be used to update the UI after background task completion
            // You can perform any UI updates or navigate to another activity if needed
        }

    }

    private void initializeMapsSdk(String apiKey) {
        try {
            ApplicationInfo ai = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            bundle.putString("com.google.android.geo.API_KEY", apiKey);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }


    private LocationRequest locationRequest;
    private static final int REQUEST_CHECK_SETTINGS = 10001;


    private boolean checkIfGpsIsEnabled() {

        boolean check = false;

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!isGpsEnabled) {
            createLocationRequest();

            check = false;

        } else {
            // GPS is already enabled

            check = true;
        }

        return check;
    }


    protected void createLocationRequest() {
        LocationRequest locationRequest = new LocationRequest.Builder(LocationRequest.PRIORITY_HIGH_ACCURACY, 1000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .build();

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.


                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(LocationSelectActivity.this,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });


    }
public void mapLocatorWithOutGps(String latitude,String longitude){
    // Check if latitude and longitude are not null or empty
    if (latitude != null && !latitude.isEmpty() && longitude != null && !longitude.isEmpty()) {
        try {
            // Convert the latitude and longitude to double
            double lat = Double.parseDouble(latitude);
            double lon = Double.parseDouble(longitude);
            // Create a LatLng object
            LatLng userLatLng = new LatLng(lat, lon);

            // Show a toast with the LatLng value
           // Toast.makeText(getApplicationContext(), userLatLng.toString(), Toast.LENGTH_SHORT).show();

            // Move the camera to the user's location on the map
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 18.634048f));
            checkIfGpsIsEnabled();
        } catch (NumberFormatException e) {
            // Handle the exception if parsing fails
            Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            checkIfGpsIsEnabled();

        }
    } else {
        // Handle the case where latitude or longitude is null or empty
        //IGNORE Impossible error.
        checkIfGpsIsEnabled();

    }
}

}