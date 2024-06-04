package www.experthere.in.helper;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

import www.experthere.in.Common.BrowserActivity;
import www.experthere.in.Common.SendEmailTask;
import www.experthere.in.R;
import www.experthere.in.api.ApiClient;
import www.experthere.in.api.ApiInterface;
import www.experthere.in.serviceProvider.NewServiceActivity;

public class AddServiceFirstTimeBottomSheet extends BottomSheetDialogFragment {

    Activity activity;

    Button submitTicketBtn;


    public AddServiceFirstTimeBottomSheet(Activity activity) {
        this.activity = activity;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.first_time_service_bottom_sheet, container, false);


        submitTicketBtn = view.findViewById(R.id.submitTicketBtn);


        submitTicketBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                    Dexter.withContext(activity).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE
                            , Manifest.permission.WRITE_EXTERNAL_STORAGE).withListener(new MultiplePermissionsListener() {
                                                                                           @Override
                                                                                           public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {

                                                                                               if (multiplePermissionsReport.areAllPermissionsGranted()) {


                                                                                                   Intent intent = new Intent(activity, NewServiceActivity.class);

                                                                                                   SharedPreferences preferences = activity.getSharedPreferences("first_service", Context.MODE_PRIVATE);
                                                                                                   SharedPreferences.Editor e = preferences.edit();
                                                                                                   e.putBoolean("done", true);
                                                                                                   e.apply();


                                                                                                   intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                                                   startActivity(intent);
                                                                                                   dismiss();
                                                                                               }


                                                                                               if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {

                                                                                                   CustomToastNegative.create(activity, "Please Grant Storage Permission!");
                                                                                               }

                                                                                           }

                                                                                           @Override
                                                                                           public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                                                                                               CustomToastNegative.create(activity, "Permission Required!");

                                                                                               permissionToken.continuePermissionRequest();

                                                                                           }
                                                                                       }
                    ).check();

                } else {
                    Dexter.withContext(activity).withPermissions(Manifest.permission.READ_MEDIA_IMAGES).withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {

                            if (multiplePermissionsReport.areAllPermissionsGranted()) {


                                Intent intent = new Intent(activity, NewServiceActivity.class);

                                SharedPreferences preferences = activity.getSharedPreferences("first_service", Context.MODE_PRIVATE);
                                SharedPreferences.Editor e = preferences.edit();
                                e.putBoolean("done", true);
                                e.apply();


                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);

                                dismiss();

                            }


                            if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {

                                CustomToastNegative.create(activity, "Please Grant Storage Permission!");
                            }


                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {

                            permissionToken.continuePermissionRequest();
                        }
                    }).check();

                }


//                Intent intent = new Intent(activity, NewServiceActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
//
//                SharedPreferences preferences = activity.getSharedPreferences("first_service", Context.MODE_PRIVATE);
//                SharedPreferences.Editor e = preferences.edit();
//                e.putBoolean("done", true);
//                e.apply();
//
//                dismiss();


            }
        });


        return view;
    }


}
