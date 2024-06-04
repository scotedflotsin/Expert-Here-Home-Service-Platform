package www.experthere.in.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.slider.Slider;

import www.experthere.in.R;
import www.experthere.in.users.IntroActivity;

public class RangeSekectorBottomSheet extends BottomSheetDialogFragment {

    Activity activity;
    private final KmListener kmListener;
    int kmValue = 5;

    boolean fUser;
    public interface KmListener{

        void SelectedKmValue (String km);

    }


    public RangeSekectorBottomSheet(Activity activity, KmListener kmListener) {
        this.activity = activity;
        this.kmListener = kmListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.radius_selector_layout, container, false);

        SharedPreferences preferences = activity.getSharedPreferences("settings", Context.MODE_PRIVATE);

        int savedRange = preferences.getInt("range",5);




        AppCompatButton noBtn = view.findViewById(R.id.cancelBtn);
        AppCompatButton yesBtn = view.findViewById(R.id.applyBtn);


        Slider rangeSlider = view.findViewById(R.id.rangeSlider);
        TextView rangeTxt = view.findViewById(R.id.rangeTxt);


        // Set an initial value for the TextView
        rangeTxt.setText(savedRange+" KM");
        rangeSlider.setValue(savedRange);

        // Set a listener to update the TextView when the slider value changes
        rangeSlider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(Slider slider, float value, boolean fromUser) {
                // Update the TextView with the current value
                rangeTxt.setText(String.format("%d KM", (int) value));

                kmValue= (int)value;

                fUser = fromUser;


            }
        });



        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissDialog();
            }
        });

        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (fUser) {

                    kmListener.SelectedKmValue(String.valueOf(kmValue));

                }
                dismissDialog();
            }
        });


        return view;
    }



    private void dismissDialog() {
        dismiss();
    }


}
