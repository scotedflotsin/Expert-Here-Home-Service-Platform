package www.experthere.in.users;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

import www.experthere.in.R;
import www.experthere.in.helper.CustomToastNegative;
import www.experthere.in.helper.InternetReceiver;
import www.experthere.in.serviceProvider.ProviderIntroActivity;


public class IntroActivity extends AppCompatActivity  {

    private TextView textView;

    BroadcastReceiver receiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.white, this.getTheme()));

        setContentView(R.layout.activity_intro);


        receiver = new InternetReceiver();
        internetStatus();

//        animateBottomLyout();

        checkPermissions();


        findViewById(R.id.partnerRegisterTxtBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                startActivity(new Intent(IntroActivity.this, ProviderIntroActivity.class));
                finish();

            }
        });

        findViewById(R.id.partnerLoginTxtBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(IntroActivity.this, LoginActivity.class);

                Bundle bundle = new Bundle();
                bundle.putBoolean("isProvider",true);

                intent.putExtras(bundle);

                startActivity(intent);
                finish();

            }
        });


        findViewById(R.id.createAccountBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(IntroActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();

            }
        });
        findViewById(R.id.haveAccountBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(IntroActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });




        findViewById(R.id.linkText).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.experthere.in")));

            }
        });


    }


    private void checkPermissions() {


//        Dexter.withContext(getApplicationContext())
//                .withPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
//                .withListener(new MultiplePermissionsListener() {
//                    @Override
//                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
//
//                        if (multiplePermissionsReport.areAllPermissionsGranted()) {
//
//                        }
//
//                    }
//
//                    @Override
//                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
//
//                    }
//                }).check();

            Dexter.withContext(this)
                    .withPermissions(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                    .withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                            if (multiplePermissionsReport.areAllPermissionsGranted()) {
                                // Permissions are granted
                            } else {
                                // Permissions are denied
                                checkPermissions(); // Re-check permissions
                            }
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                            // Show the rationale and continue asking for permissions
                            permissionToken.continuePermissionRequest();
                            CustomToastNegative.create(getApplicationContext(),"Permission Required!");

                        }
                    }).check();



    }

//    private void animateBottomLyout() {
//
//
//        LinearLayout linearLayout = findViewById(R.id.linearLayout2);
//
//        Animation animation = new TranslateAnimation(
//                Animation.RELATIVE_TO_PARENT, 0f, // fromXDelta
//                Animation.RELATIVE_TO_PARENT, 0f, // toXDelta
//                Animation.RELATIVE_TO_PARENT, 1f, // fromYDelta (start from the bottom)
//                Animation.RELATIVE_TO_PARENT, 0f  // toYDelta (go to the top)
//        );
//
//        // Set the animation duration in milliseconds
//        animation.setDuration(1000);
//        // Start the animation on the LinearLayout
//        linearLayout.startAnimation(animation);
//    }


    private void internetStatus() {

        registerReceiver(receiver,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(receiver);
    }
}