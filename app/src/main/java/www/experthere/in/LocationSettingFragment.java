package www.experthere.in;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.CompositeMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.jetbrains.annotations.Contract;

import java.util.List;

import www.experthere.in.helper.LocationUtils;

public class LocationSettingFragment extends BottomSheetDialogFragment {

    Activity activity;

    public LocationSettingFragment(Activity activity) {
        this.activity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gif_lay, container, false);

        // Initialize your views and set any data if needed
        LocationUtils locationUtils = new LocationUtils();


        if (!LocationUtils.isLocationEnabled(activity)) {
            // Location services are not enabled, prompt the user to enable them

            view.findViewById(R.id.locationTunLay).setVisibility(View.VISIBLE);

        }else {

            view.findViewById(R.id.locationTunLay).setVisibility(View.GONE);

        }

        view.findViewById(R.id.settingOpenBtn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                // Check if location services are enabled
                if (!LocationUtils.isLocationEnabled(activity)) {
                    // Location services are not enabled, prompt the user to enable them
                    LocationUtils.enableLocationSettings(activity);

                }else {

                    view.findViewById(R.id.locationTunLay).setVisibility(View.GONE);

                }
            }
        });

        view.findViewById(R.id.settingOpenBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {





                    checkUserPermissions();





            }
        });

        return view;
    }


    private void checkUserPermissions() {

        // Check for s using Dexter
        Dexter.withContext(activity.getApplicationContext())
                .withPermissions(android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                .withListener(createPermissionListener())
                .check();

    }

    @NonNull
    @Contract(value = " -> new", pure = true)
    private MultiplePermissionsListener createPermissionListener() {
        return new CompositeMultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                // Check if all permissions are granted
                if (report.areAllPermissionsGranted()) {
                    // Permissions are granted, proceed with your logic


                    dismiss();


                } else {
                    // Permissions are not granted, handle denial
                    handlePermissionDenial(report.getDeniedPermissionResponses());
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                // Show a rationale for the requested permissions
                // You can show a dialog explaining why the permissions are needed
                // and then call token.continuePermissionRequest() if the user agrees

                // For simplicity, we'll just continue the permission request without showing a rationale
                token.continuePermissionRequest();
            }


        };
    }

    private void handlePermissionDenial(List<PermissionDeniedResponse> deniedResponses) {
        for (PermissionDeniedResponse response : deniedResponses) {
            // Check if "Don't ask again" is selected
            if (response.isPermanentlyDenied()) {
                // Permission permanently denied, guide the user to app settings
                openAppSettings();
                return;
            }
            // Permission denied but not permanently, show a rationale or retry
            // You can customize this part based on your app's requirements
        }
    }

    private void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

}
