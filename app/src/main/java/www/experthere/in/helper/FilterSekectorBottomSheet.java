package www.experthere.in.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.slider.RangeSlider;
import com.google.android.material.slider.Slider;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import www.experthere.in.R;
import www.experthere.in.model.SavedFilterList;
import www.experthere.in.users.UserServiceSerachActivity;

public class FilterSekectorBottomSheet extends BottomSheetDialogFragment {

    Activity activity;
    private final KmListener kmListener;
    int kmValue = 0;
    int filtersCount = 0;

    int chipID = 0;

    boolean userChangedPriceRangeSlider, userChangedKM, userChangedChips,changesMade;

    float valueTo, valueFrom = 100;

    String query, searchQuery;


    SavedFilterList savedFilterList;

    HashSet<String> uniqueStrings = new HashSet<>();


    public interface KmListener {

        void SelectedKmValue(Object km);

        void SelectedStars(Object star);

        void SelectedPriceRange(Object from, Object to);

        void FilterCounts(int count);

    }


    public FilterSekectorBottomSheet(SavedFilterList savedFilterList, String searchQuery, String query, Activity activity, KmListener kmListener) {
        this.activity = activity;
        this.kmListener = kmListener;
        this.query = query;
        this.searchQuery = searchQuery;
        this.savedFilterList = savedFilterList;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.filter_selector_layout, container, false);


        SharedPreferences preferences = activity.getSharedPreferences("settings", Context.MODE_PRIVATE);

        int savedRange = preferences.getInt("range", 5);


        AppCompatButton noBtn = view.findViewById(R.id.cancelBtn);
        AppCompatButton yesBtn = view.findViewById(R.id.applyBtn);
        AppCompatButton resetBtn = view.findViewById(R.id.resetBtn);


        Slider rangeSlider = view.findViewById(R.id.rangeSlider);
        RangeSlider priceRangeSlider = view.findViewById(R.id.priceRangeSlider);
        TextView rangeTxt = view.findViewById(R.id.rangeTxt);

        TextView priceRangeTxt = view.findViewById(R.id.priceRangeTxt);


        ChipGroup chipGroup = view.findViewById(R.id.chipGroup);

        chipGroup.setOnCheckedStateChangeListener(new ChipGroup.OnCheckedStateChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull ChipGroup group, @NonNull List<Integer> checkedIds) {

                userChangedChips = true;
                Log.d("JKWDBKJBDWI", "Check chaneged by user ");


                chipID = group.getCheckedChipId();

                changesMade=true;

            }
        });


        // Set a listener to update the TextView when the slider value changes
        rangeSlider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(Slider slider, float value, boolean fromUser) {
                // Update the TextView with the current value
                rangeTxt.setText(String.format("%d KM", (int) value));

                if (fromUser) {
                    kmValue = (int) value;
                }
                userChangedKM = fromUser;
                changesMade=fromUser;


            }
        });


        // Set an initial value for the TextView
        rangeTxt.setText(savedRange + " KM");
        rangeSlider.setValue(savedRange);

        priceRangeTxt.setText("INR " + (int) valueFrom + " - INR " + (int) valueFrom);

        priceRangeSlider.addOnChangeListener(new RangeSlider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull RangeSlider slider, float value, boolean fromUser) {

                userChangedPriceRangeSlider = fromUser;
                changesMade=fromUser;
                valueFrom = slider.getValues().get(0);
                valueTo = slider.getValues().get(1);


                priceRangeTxt.setText("INR " + (int) valueFrom + " - INR " + (int) valueTo);


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


                if (query != null || searchQuery != null) {


                    if (kmValue != 0) {
                        kmListener.SelectedKmValue(kmValue);
                    }


                    if (userChangedPriceRangeSlider) {

                        if (valueFrom == valueTo) {


                            CustomToastNegative.create(activity,"Range Can't Be Same");

                        } else {

                            kmListener.SelectedPriceRange((int) valueFrom, (int) valueTo);

                            uniqueStrings.add("price");

                            Log.d("FILTERFEGVE", "Price FIlter ");


                        }


                    } else {
                        kmListener.SelectedPriceRange(null, null);
                    }


                    if (userChangedKM) {

                        Log.d("FILTERFEGVE", "KM FIlter ");

                        uniqueStrings.add("range");

                        filtersCount++;
                    }


                    if (userChangedChips) {
                        Log.d("FILTERFEGVE", "star FIlter ");

                        uniqueStrings.add("stars");

                        filtersCount++;

                    }


//                    kmListener.FilterCounts(filtersCount);


                    savedFilterList.setUniqueFilters(uniqueStrings);
//                    savedFilterList.getUniqueFilters().addAll(uniqueStrings);


                    kmListener.FilterCounts(savedFilterList.getUniqueFilters().size());
                    Log.d("FILTERFEGVE", " FIlter Applied - " + savedFilterList.getUniqueFilters().toString());


                    if (chipID != 0) {


                        if (chipID == R.id.allChip) {


                            kmListener.SelectedStars(0);

                        } else {
                            userChangedChips = true;

                        }
                        if (chipID == R.id.oneChip) {

                            kmListener.SelectedStars(1);


                        }
                        if (chipID == R.id.twoChip) {

                            kmListener.SelectedStars(2);

                        }
                        if (chipID == R.id.threeChip) {

                            kmListener.SelectedStars(3);

                        }
                        if (chipID == R.id.fousChip) {

                            kmListener.SelectedStars(4);

                        }
                        if (chipID == R.id.fiveChip) {

                            kmListener.SelectedStars(5);

                        }


                    }

                } else {

                    CustomToastNegative.create(activity,"No Search Query!");

                }
                dismissDialog();
            }
        });


        if (savedFilterList != null) {


            if (savedFilterList.getRange() != null) {

                rangeTxt.setText(String.format("%d KM", Integer.parseInt(savedFilterList.getRange())));
                kmValue = Integer.parseInt(savedFilterList.getRange());
                rangeSlider.setValue(kmValue);

                Log.d("ksjbjsvjhs", "oRange Not Nukk");

                uniqueStrings.add("range");


            }


            if (savedFilterList.getFrom() != null && savedFilterList.getTo() != null) {


                valueFrom = Float.parseFloat(savedFilterList.getFrom());
                valueTo = Float.parseFloat(savedFilterList.getTo());


                priceRangeTxt.setText("INR " + (int) valueFrom + " - INR " + (int) valueTo);


                List<Float> val = new ArrayList<>();
                val.add(0, valueFrom);
                val.add(1, valueTo);

                priceRangeSlider.setValues(val);

                uniqueStrings.add("price");

            }


            if (savedFilterList.getStar() != null) {


                uniqueStrings.add("stars");


                if (savedFilterList.getStar().equals("0")) {

                    chipID = R.id.allChip;
                    Chip chip = chipGroup.findViewById(chipID);
                    chip.setChecked(true);

                }
                if (savedFilterList.getStar().equals("1")) {

                    chipID = R.id.oneChip;

                    Chip chip = chipGroup.findViewById(chipID);
                    chip.setChecked(true);
                }
                if (savedFilterList.getStar().equals("2")) {

                    chipID = R.id.twoChip;

                    Chip chip = chipGroup.findViewById(chipID);
                    chip.setChecked(true);
                }
                if (savedFilterList.getStar().equals("3")) {

                    chipID = R.id.threeChip;

                    Chip chip = chipGroup.findViewById(chipID);
                    chip.setChecked(true);
                }
                if (savedFilterList.getStar().equals("4")) {

                    chipID = R.id.fousChip;

                    Chip chip = chipGroup.findViewById(chipID);
                    chip.setChecked(true);
                }
                if (savedFilterList.getStar().equals("5")) {

                    chipID = R.id.fiveChip;

                    Chip chip = chipGroup.findViewById(chipID);
                    chip.setChecked(true);
                }


            } else {


//                chipID = R.id.allChip;
//                Chip chip = chipGroup.findViewById(chipID);
//                chip.setChecked(true);
            }


        }


        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (savedFilterList.getStar() != null || savedFilterList.getRange() != null
                        || savedFilterList.getFrom() != null ||
                        savedFilterList.getTo() != null || savedFilterList.getUniqueFilters() != null || changesMade) {

                    Intent intent = new Intent(activity, UserServiceSerachActivity.class);
                    activity.startActivity(intent);
                    activity.finish();


                    CustomToastPositive.create(activity,"Filter Reset!");



                }


            }
        });


        return view;
    }


    private void dismissDialog() {
        dismiss();
    }


    private void hideKeyboard() {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


}
