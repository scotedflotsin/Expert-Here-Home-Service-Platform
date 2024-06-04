package www.experthere.in.helper.app;

import static androidx.core.app.ActivityCompat.recreate;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Locale;

import www.experthere.in.R;
import www.experthere.in.helper.CustomToastPositive;

public class LanguageBottomSheet extends BottomSheetDialogFragment {

    Activity activity;

    private static final String PREFS_SETTINGS = "settings";
    private static final String LANG_PREF_KEY = "language";

    private final SharedPreferences preferences;
    private final SharedPreferences.Editor editor;

    public LanguageBottomSheet(Activity activity) {
        this.activity = activity;

        preferences = activity.getSharedPreferences(PREFS_SETTINGS, Context.MODE_PRIVATE);
        editor = preferences.edit();


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.language_dialog_lay, container, false);


        RadioButton englishBtn = view.findViewById(R.id.englishBtn);
        RadioButton hindiBtn = view.findViewById(R.id.hindiBtn);
        RadioButton bengaliBtn = view.findViewById(R.id.bengaliBtn);
        RadioButton telguBtn = view.findViewById(R.id.telguBtn);
        RadioButton marathiBtn = view.findViewById(R.id.marathiBtn);
        RadioButton tamilBtn = view.findViewById(R.id.tamilBtn);
        RadioButton gujratiBtn = view.findViewById(R.id.gujratiBtn);
        RadioButton kannadaBtn = view.findViewById(R.id.kannadaBtn);
        RadioButton malayalamBtn = view.findViewById(R.id.malyalamBtn);


        englishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setLanguage("en");

            }
        });
        hindiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                setLanguage("hi");


            }
        });

        bengaliBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                setLanguage("bn");


            }
        });
        telguBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                setLanguage("te");


            }
        });
        marathiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                setLanguage("mr");


            }
        });

        tamilBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                setLanguage("ta");


            }
        });
        gujratiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                setLanguage("gu");


            }
        });
        kannadaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                setLanguage("kn");


            }
        });

        malayalamBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                setLanguage("ml");


            }
        });


        String savedLanguage = getLanguage();


        if (savedLanguage.equals("en")) {

            englishBtn.setChecked(true);

        }


        if (savedLanguage.equals("hi")) {

            hindiBtn.setChecked(true);

        }





        if (savedLanguage.equals("bn")) {

            bengaliBtn.setChecked(true);

        }


        if (savedLanguage.equals("te")) {

            telguBtn.setChecked(true);

        }


        if (savedLanguage.equals("mr")) {

            marathiBtn.setChecked(true);

        }


        if (savedLanguage.equals("ta")) {

            tamilBtn.setChecked(true);

        }


        if (savedLanguage.equals("gu")) {

            gujratiBtn.setChecked(true);

        }


        if (savedLanguage.equals("kn")) {

            kannadaBtn.setChecked(true);

        }


        if (savedLanguage.equals("ml")) {

            malayalamBtn.setChecked(true);

        }


        return view;
    }

    private void setLanguage(String languageCode) {

        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        getResources().getConfiguration().setLocale(locale);


        activity.getBaseContext().getResources().updateConfiguration(getResources().getConfiguration(), activity.getBaseContext().getResources().getDisplayMetrics());

        // Save the selected language in SharedPreferences
        editor.putString(LANG_PREF_KEY, languageCode);
        editor.apply();

        // Show a toast to indicate language change

        CustomToastPositive.create(activity,getString(R.string.language_change));
        // Refresh the activity to reflect language change

        dismiss();



        recreate(activity);

    }

    private String getLanguage() {

        return preferences.getString(LANG_PREF_KEY, "en");
    }

}
