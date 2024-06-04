package www.experthere.in.helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;

import www.experthere.in.R;

public class InternetReceiver extends BroadcastReceiver {

    Context mContext;
    Dialog dialog;


    @Override
    public void onReceive(Context context, Intent intent) {

        mContext = context;

        if (isConnected(context)) {

            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        } else {
               showDialog();
        }



    }

    public boolean isConnected(Context context) {

        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            return (networkInfo != null && networkInfo.isConnectedOrConnecting() );

        } catch (NullPointerException e) {

            e.printStackTrace();
            return false;

        }

    }

    public void showDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.internet_dialog, null);
        builder.setCancelable(false);
        Button retryBtn = view.findViewById(R.id.retryBtn);
        builder.setView(view);



        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        view.findViewById(R.id.exitInternet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Send a broadcast to finish the application
                Intent finishAppIntent = new Intent("www.experthere.in.action.FINISH_APP");
                mContext.sendBroadcast(finishAppIntent);

                dialog.dismiss();

            }
        });

        retryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isConnected(mContext)) {
                    dialog.dismiss();

                } else {
                    CustomToastNegative.create(mContext,"No Internet! ");

                }

            }
        });

        dialog.show();

    }



}
