package www.experthere.in.serviceProvider.profile_frgments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;

import jp.wasabeef.richeditor.RichEditor;
import retrofit2.Call;
import retrofit2.Response;
import www.experthere.in.R;
import www.experthere.in.api.ApiClient;
import www.experthere.in.api.ApiInterface;
import www.experthere.in.model.ProviderData;
import www.experthere.in.model.ProviderResponse;

public class AboutProviderFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = "ABOUT_PROVIDER";
    Activity activity;
    ProviderData providerData;
    RichEditor htmlEditor;
    MapView mapView;
    private GoogleMap googleMap;
    String providerId;
    Double latitude, longitude; // Declare these variables globally
    TextView locationTxt;
    float zoomLevel = 16.634048f;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = requireActivity();
        initBundle(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about_provider, container, false);

        htmlEditor = view.findViewById(R.id.htmlEditor);
        mapView = view.findViewById(R.id.mapView);
        locationTxt = view.findViewById(R.id.locationTxtCat);
        htmlEditor.setEnabled(false);

        try {
            new GetProviderDetails(providerId).execute();
        } catch (Exception e) {
            Log.d(TAG, "Error 1  "+e.getMessage());

            throw new RuntimeException(e);
        }

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);


        mapView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String uri = "geo:" + latitude + "," + longitude;

                // Create an Intent with the ACTION_VIEW action and the URI
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));

                // Set the package to Google Maps to ensure it opens in the Maps app
                intent.setPackage("com.google.android.apps.maps");

                // Verify that the Google Maps app is installed before starting the activity
                if (intent.resolveActivity(activity.getPackageManager()) != null) {
                    startActivity(intent);
                }

            }
        });

        return view;
    }

    private class GetProviderDetails extends AsyncTask<Void, Void, ProviderResponse> {
        private final String providerID;

        public GetProviderDetails(String providerID) {
            this.providerID = providerID;
        }

        @Override
        protected ProviderResponse doInBackground(Void... params) {
            try {
                ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
                Call<ProviderResponse> call = apiInterface.getProviderData(providerID);
                Response<ProviderResponse> response = call.execute();

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
        protected void onPostExecute(ProviderResponse apiResponse) {
            if (apiResponse != null && apiResponse.isSuccess()) {
                providerData = apiResponse.getData();



                if (providerData != null) {

                    htmlEditor.setHtml(providerData.getDescription());
                    latitude = Double.valueOf(providerData.getLatitude());
                    longitude = Double.valueOf(providerData.getLongitude());
                    locationTxt.setText(providerData.getAddress());


                    // Now that you have the latitude and longitude, you can update the map
                    updateMap();


                    Log.d(TAG, "onPostExecute: "+latitude+"\n"+longitude);


                } else {
                    Log.d(TAG, "Error 2  ");

                }
            } else {

                Log.d(TAG, "Error 3  ");

            }
        }
    }

    private void updateMap() {
        if (googleMap != null && latitude != null && longitude != null) {
          
          
            LatLng location = new LatLng(latitude, longitude);
            googleMap.addMarker(new MarkerOptions().position(location).title("Marker Title"));

            // Set the camera to the specified location with the desired zoom level
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, zoomLevel));

            // Disable zoom gestures and scrolling
            googleMap.getUiSettings().setZoomGesturesEnabled(false);
            googleMap.getUiSettings().setScrollGesturesEnabled(false);
        }
    }


    private void initBundle(Activity activity) {
        Bundle bundle = activity.getIntent().getExtras();
        if (bundle != null) {
            providerId = bundle.getString("provider_id", "0");
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        updateMap(); // Update the map when it is ready
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
