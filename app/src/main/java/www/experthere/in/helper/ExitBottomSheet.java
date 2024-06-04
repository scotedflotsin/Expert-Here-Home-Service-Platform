package www.experthere.in.helper;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import www.experthere.in.calling.DialToneGeneratorService;
import www.experthere.in.calling.OutGoingCallService;
import www.experthere.in.calling.OutGoingNotificationActionReceiver;
import www.experthere.in.users.IntroActivity;
import www.experthere.in.R;

public class ExitBottomSheet extends BottomSheetDialogFragment {


    boolean fullAppClose;

    public static final String ACTION_STOP = "www.experthere.in.calling.STOP";

    public ExitBottomSheet(boolean closeActivity) {
        this.fullAppClose = closeActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bottom_sheet_back, container, false);

        Button noBtn = view.findViewById(R.id.noBtn);
        Button yesBtn = view.findViewById(R.id.yesBtn);

        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissDialog();
            }
        });

        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exit();
            }
        });


        return view;
    }


    private void exit() {

        if (isServiceRunning(OutGoingCallService.class)) {

            Intent intent = new Intent(requireActivity(), OutGoingCallService.class);
            Intent intent2 = new Intent(requireActivity(), DialToneGeneratorService.class);
            requireActivity().stopService(intent);
            requireActivity(). stopService(intent2);

            Intent broadcastIntent = new Intent(requireActivity(), OutGoingNotificationActionReceiver.class);
            broadcastIntent.setAction(ACTION_STOP);
            requireActivity(). sendBroadcast(broadcastIntent);

        }




        if (fullAppClose){

            requireActivity().finish();

        }else {

            startActivity(new Intent(requireActivity(), IntroActivity.class));
            requireActivity().finish();
        }



    }

    private void dismissDialog() {
        dismiss();
    }


    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) requireActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
