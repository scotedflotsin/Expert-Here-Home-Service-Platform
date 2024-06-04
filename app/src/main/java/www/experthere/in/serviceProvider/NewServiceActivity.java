package www.experthere.in.serviceProvider;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.dhaval2404.colorpicker.MaterialColorPickerDialog;
import com.github.dhaval2404.colorpicker.listener.ColorListener;
import com.github.dhaval2404.colorpicker.model.ColorShape;
import com.github.dhaval2404.colorpicker.model.ColorSwatch;
import com.google.android.material.textfield.TextInputEditText;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.github.vejei.cupertinoswitch.CupertinoSwitch;
import jp.wasabeef.richeditor.RichEditor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import www.experthere.in.R;
import www.experthere.in.adapters.TagAdapter;
import www.experthere.in.api.ApiClient;
import www.experthere.in.api.ApiInterface;
import www.experthere.in.helper.CatBottomSheet;
import www.experthere.in.helper.CustomToastNegative;
import www.experthere.in.helper.CustomToastPositive;
import www.experthere.in.helper.ExitBottomSheet;
import www.experthere.in.helper.ImageCompressActivity;
import www.experthere.in.helper.ImagePickerBottomSheet;
import www.experthere.in.helper.InternetReceiver;
import www.experthere.in.helper.SubCatBottomSheet;
import www.experthere.in.helper.TaxBottomSheet;
import www.experthere.in.model.Category;
import www.experthere.in.model.CategoryResponse;
import www.experthere.in.model.CreateServiceResponse;
import www.experthere.in.model.EditServiceRes;
import www.experthere.in.model.Subcategory;
import www.experthere.in.model.TagModel;
import www.experthere.in.model.TaxApiResponse;
import www.experthere.in.model.Taxes;
import www.experthere.in.model.UpdateServiceResponse;

public class NewServiceActivity extends AppCompatActivity {


    TextInputEditText serviceTitleET, finalPriceEt, discountPriceET, membersEt, durationEt, tagsEt;
    RichEditor htmlEditor;
    boolean isCatSelected, isSubCatSelected, isIdImageSelected,
            isTaxSelected, isCurrencySelected, isDurationSelected, isTaxValueSelected;
    String selectedCat, selectedSubCat = "";

    ArrayList<Category> categories = new ArrayList<>();
    ArrayList<Taxes> taxes = new ArrayList<>();
    List<Subcategory> subCategoriesList = new ArrayList<>();

    TextView selectedCatTxt, selectedSubCatTxt;
    CupertinoSwitch statusSwitch;
    boolean isStatusChecked;
    TextView stepsCountText;
    private RecyclerView recyclerView;
    private ArrayList<TagModel> dataList;
    TagAdapter adapter;
    String docUri;
    String status = "Disabled";

    String selectedTax, selectedCurrency, selectedDuration, selectedTaxValue = "";
    private ImageView boldBtn, colorBtn;
    String HTML_CONTENT, providerId, selectedCatID, selectedSubCatID;

    boolean isEdit, changesMade;
    String proId, serId;

    ProgressDialog progressDialog;

    BroadcastReceiver receiver;
    TextView durationTxt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_service);
        Bundle bundle = getIntent().getExtras();

        receiver = new InternetReceiver();
        internetStatus();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Processing");

        if (bundle != null) {

            isEdit = bundle.getBoolean("isEdit");
            proId = bundle.getString("provider_id", "none");
            serId = bundle.getString("service_id", "none");
        }

        if (isEdit) {

            progressDialog.show();
            new FetchEditService(NewServiceActivity.this).execute();

        }

        initializeViews();

        // Initialize dataList if needed
        setTagsMethod();
        setUpHtmlEditor();

        SharedPreferences p = getSharedPreferences("provider", MODE_PRIVATE);
        if (p != null) {

            providerId = p.getString("id", "none");

            isCurrencySelected = true;
            selectedCurrency = p.getString("currency", "INR");

            TextView currencyTxt = findViewById(R.id.currencyTxt);
            currencyTxt.setText(selectedCurrency);

        }


        new GetCategories(NewServiceActivity.this).execute();
        new GetTax(NewServiceActivity.this).execute();


        statusSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                changesMade = true;


            }
        });


        if (!isEdit){

            statusSwitch.setChecked(true);
            status = "Enable";
            isStatusChecked=true;

        }



        statusSwitch.setOnStateChangeListener(new CupertinoSwitch.OnStateChangeListener() {
            @Override
            public void onChanged(CupertinoSwitch view, boolean checked) {

                isStatusChecked = checked;
                if (checked) {
                    status = "Enable";

                } else {

                    status = "Disabled";

                }


            }

            @Override
            public void onSwitchOn(CupertinoSwitch view) {

                status = "Enable";


            }

            @Override
            public void onSwitchOff(CupertinoSwitch view) {
                status = "Disabled";

            }
        });
        stepsCountText.setText("Step 1 Of 3");

        findViewById(R.id.lay1).setVisibility(View.VISIBLE);
        findViewById(R.id.next1Btn).setVisibility(View.VISIBLE);

        findViewById(R.id.lay2).setVisibility(View.GONE);
        findViewById(R.id.next2Layout).setVisibility(View.GONE);


        findViewById(R.id.lay3).setVisibility(View.GONE);
        findViewById(R.id.next3Layout).setVisibility(View.GONE);

        findViewById(R.id.prev3Btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                stepsCountText.setText("Step 2 Of 3");

                findViewById(R.id.lay1).setVisibility(View.GONE);
                findViewById(R.id.next1Btn).setVisibility(View.GONE);

                findViewById(R.id.lay2).setVisibility(View.VISIBLE);
                findViewById(R.id.next2Layout).setVisibility(View.VISIBLE);


                findViewById(R.id.lay3).setVisibility(View.GONE);
                findViewById(R.id.next3Layout).setVisibility(View.GONE);
            }
        });
        findViewById(R.id.prev2Btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stepsCountText.setText("Step 1 Of 3");

                findViewById(R.id.lay1).setVisibility(View.VISIBLE);
                findViewById(R.id.next1Btn).setVisibility(View.VISIBLE);

                findViewById(R.id.lay2).setVisibility(View.GONE);
                findViewById(R.id.next2Layout).setVisibility(View.GONE);


                findViewById(R.id.lay3).setVisibility(View.GONE);
                findViewById(R.id.next3Layout).setVisibility(View.GONE);


            }
        });

        findViewById(R.id.next1Btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!serviceTitleET.getText().toString().trim().isEmpty() && dataList.size() > 0) {

                    if (isCatSelected && !selectedCat.isEmpty()) {

                        if (isSubCatSelected && selectedSubCat!=null) {

                            stepsCountText.setText("Step 2 Of 3");

                            findViewById(R.id.lay2).setVisibility(View.VISIBLE);
                            findViewById(R.id.next2Layout).setVisibility(View.VISIBLE);
                            findViewById(R.id.lay1).setVisibility(View.GONE);
                            findViewById(R.id.next1Btn).setVisibility(View.GONE);

                            findViewById(R.id.lay3).setVisibility(View.GONE);
                            findViewById(R.id.next3Layout).setVisibility(View.GONE);


                        } else {

                            CustomToastNegative.create(getApplicationContext(), "Please Select Sub Category! ");
                        }


                    } else {

                        CustomToastNegative.create(getApplicationContext(), "Please Select  Category! ");
                    }


                } else {
                    CustomToastNegative.create(getApplicationContext(), "Fill All Details! ");
                }
            }
        });

        findViewById(R.id.next2Btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (isIdImageSelected && isTaxSelected && isTaxValueSelected && isCurrencySelected
                        && !finalPriceEt.getText().toString().isEmpty() && !discountPriceET.getText().toString().isEmpty()
                        && !membersEt.getText().toString().isEmpty() && isDurationSelected && !durationEt.getText().toString().isEmpty()) {

                    if (!selectedDuration.matches("When service finish") && Integer.parseInt(durationEt.getText().toString().trim()) == 0) {


                        CustomToastNegative.create(getApplicationContext(), "Duration Can't be 0 !");


                    } else {


                        Log.d("STRINGDUR", "doInBackground: " + selectedDuration);
                        Log.d("STRINGDUR", "doInBackground: " + durationEt.getText().toString());

                        if (Integer.parseInt(finalPriceEt.getText().toString().trim()) == 0 || Integer.parseInt(discountPriceET.getText().toString().trim()) == 0) {

                            CustomToastNegative.create(getApplicationContext(), "Price Can't be 0 !");

                        } else {

                            if (Integer.parseInt(discountPriceET.getText().toString().trim()) == Integer.parseInt(finalPriceEt.getText().toString().trim())) {


                                CustomToastNegative.create(getApplicationContext(), "Discount Price & Final Price Cant be same!");


                            } else {

                                if (Integer.parseInt(discountPriceET.getText().toString().trim()) > Integer.parseInt(finalPriceEt.getText().toString().trim())) {

                                    CustomToastNegative.create(getApplicationContext(), "Discount Price Must be less than final price!!");


                                } else {


                                    if (Integer.parseInt(finalPriceEt.getText().toString().trim()) <= 10000000) {
                                        stepsCountText.setText("Step 3 Of 3");

                                        findViewById(R.id.lay2).setVisibility(View.GONE);
                                        findViewById(R.id.next2Layout).setVisibility(View.GONE);


                                        findViewById(R.id.lay1).setVisibility(View.GONE);
                                        findViewById(R.id.next1Btn).setVisibility(View.GONE);

                                        findViewById(R.id.lay3).setVisibility(View.VISIBLE);
                                        findViewById(R.id.next3Layout).setVisibility(View.VISIBLE);

                                    } else {

                                        CustomToastNegative.create(getApplicationContext(), "Maximum Final Price Can be 1 Crore!");

                                    }


                                }

                            }
                        }


                    }

                } else {

                    CustomToastNegative.create(getApplicationContext(), "Fill All Details!");
                }


            }
        });
        findViewById(R.id.complete3Btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                HTML_CONTENT = htmlEditor.getHtml();
                String plainText = android.text.Html.fromHtml(HTML_CONTENT != null ? HTML_CONTENT : "").toString().trim();


                try {
                    if (!TextUtils.isEmpty(HTML_CONTENT)) {

                        // Check if the content contains only whitespace characters
                        if (!plainText.matches("\\s*")) {

                            if (isEdit) {

                                if (changesMade) {

                                    updateService();

                                } else {

                                    CustomToastNegative.create(getApplicationContext(), "No Changes Made!");

                                }


                            } else {
                                saveNewService();
                            }

                        } else {
                            CustomToastNegative.create(getApplicationContext(), "Please Write Description!");
                        }

                    } else {
                        CustomToastNegative.create(getApplicationContext(), "Please Write Description!");
                    }
                } catch (Exception e) {


                    Log.d("NEWSERVICE", "Error 1 ");

                 }


            }
        });


        findViewById(R.id.selectCatBtn).setOnClickListener(view -> showCatDialog());
        findViewById(R.id.selectSubCatBtn).setOnClickListener(view -> showSubCatDialog());

        findViewById(R.id.backNewServiceBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                ExitBottomSheet exitBottomSheet = new ExitBottomSheet(true);
                exitBottomSheet.show(getSupportFragmentManager(),exitBottomSheet.getTag());


            }
        });


        findViewById(R.id.uploadLogo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                    Dexter.withContext(getApplicationContext()).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE).withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {

                            if (multiplePermissionsReport.areAllPermissionsGranted()) {

                                selectDoc();

                            }

                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                            CustomToastNegative.create(getApplicationContext(), "Permission Required!");

                        }
                    }).check();

                } else {
                    Dexter.withContext(getApplicationContext()).withPermissions(Manifest.permission.READ_MEDIA_IMAGES).withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {

                            if (multiplePermissionsReport.areAllPermissionsGranted()) {

                                selectDoc();

                            }

                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {

                            CustomToastNegative.create(getApplicationContext(), "Permission Required!");
                        }
                    }).check();

                }

            }
        });

        findViewById(R.id.selectTaxExcludeBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTaxDialog();
            }
        });
        findViewById(R.id.selectTaxValueBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                TextView taxValueTxt = findViewById(R.id.taxValueTxt);


                TaxBottomSheet bottomSheet = new TaxBottomSheet(NewServiceActivity.this, taxes);
                bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
                bottomSheet.setTaxBottomSheetListener(new TaxBottomSheet.TaxBottomSheetListener() {
                    @Override
                    public void onItemSelected(String id, String percentage) {

                        changesMade = true;


                        taxValueTxt.setText("GST (" + percentage + ") %");
                        selectedTaxValue = percentage;
                        isTaxValueSelected = true;


                    }
                });


            }
        });

        findViewById(R.id.selectCurrencyBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


//                showCurrencyDialog();


            }
        });

        findViewById(R.id.selectDurationBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showDurationDialog();

            }
        });


        serviceTitleET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                changesMade = true;

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        finalPriceEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                changesMade = true;

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        discountPriceET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                changesMade = true;


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        membersEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                changesMade = true;

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        durationEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                changesMade = true;

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        htmlEditor.setOnTextChangeListener(new RichEditor.OnTextChangeListener() {
            @Override
            public void onTextChange(String text) {

                changesMade = true;


            }
        });

    }

    private void saveNewService() {
        new SaveNewService(this).execute();

    }

    private void updateService() {
        new UpdateService(this).execute();

    }

    private void showDurationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Inflate the custom layout
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.duration_select_dilog, null);

        builder.setView(dialogView);

        // Show the dialog
        AlertDialog dialog = builder.create();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        // Set up the radio group
        // RadioGroup radioGroup = dialogView.findViewById(R.id.radioGroup);

        // Set up the radio buttons
        final RadioButton min = dialogView.findViewById(R.id.radioMin);
        final RadioButton hour = dialogView.findViewById(R.id.radioHourly);
        final RadioButton day = dialogView.findViewById(R.id.radioDay);
        final RadioButton serviceFinish = dialogView.findViewById(R.id.radioServiceFinish);


        TextView durationTxt = findViewById(R.id.durationTxt);


        min.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                changesMade = true;

                isDurationSelected = true;
                selectedDuration = min.getText().toString();
                durationTxt.setText(min.getText().toString());
                durationEt.setEnabled(true);
                durationEt.setText("");

                // Dismiss the dialog after a delay of 200ms
                new Handler().postDelayed(new Runnable() {
                    @Override

                    public void run() {
                        dialog.dismiss();
                    }
                }, 200);
            }
        });
        hour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                changesMade = true;

                isDurationSelected = true;

                selectedDuration = hour.getText().toString();
                durationTxt.setText(hour.getText().toString());
                durationEt.setEnabled(true);
                durationEt.setText("");

                // Dismiss the dialog after a delay of 200ms
                new Handler().postDelayed(new Runnable() {
                    @Override

                    public void run() {
                        dialog.dismiss();
                    }
                }, 200);
            }
        });
        day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changesMade = true;

                isDurationSelected = true;
                selectedDuration = day.getText().toString();
                durationTxt.setText(day.getText().toString());
                durationEt.setEnabled(true);
                durationEt.setText("");

                // Dismiss the dialog after a delay of 200ms
                new Handler().postDelayed(new Runnable() {
                    @Override

                    public void run() {
                        dialog.dismiss();
                    }
                }, 200);
            }
        });
        serviceFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changesMade = true;

                isDurationSelected = true;
                selectedDuration = serviceFinish.getText().toString();
                durationTxt.setText(serviceFinish.getText().toString());
                durationEt.setText("0");
                durationEt.setEnabled(false);

                // Dismiss the dialog after a delay of 200ms
                new Handler().postDelayed(new Runnable() {
                    @Override

                    public void run() {
                        dialog.dismiss();
                    }
                }, 200);
            }
        });


    }

    private void showCurrencyDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Inflate the custom layout
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.currency_select_dilog, null);

        builder.setView(dialogView);

        // Show the dialog
        AlertDialog dialog = builder.create();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        // Set up the radio group
        // RadioGroup radioGroup = dialogView.findViewById(R.id.radioGroup);

        // Set up the radio buttons
        final RadioButton radioInr = dialogView.findViewById(R.id.radioInr);
        final RadioButton radioUsd = dialogView.findViewById(R.id.radioUSD);


        TextView currencyTxt = findViewById(R.id.currencyTxt);


        radioInr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changesMade = true;

                isCurrencySelected = true;
                selectedCurrency = radioInr.getText().toString();
                currencyTxt.setText(radioInr.getText().toString());
                // Dismiss the dialog after a delay of 200ms
                new Handler().postDelayed(new Runnable() {
                    @Override

                    public void run() {
                        dialog.dismiss();
                    }
                }, 200);
            }
        });

        radioUsd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                changesMade = true;

                currencyTxt.setText(radioUsd.getText().toString());
                isCurrencySelected = true;
                selectedCurrency = radioUsd.getText().toString();
                // Dismiss the dialog after a delay of 200ms
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                    }
                }, 200);
            }
        });


    }


    private void showTaxDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Inflate the custom layout
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.tax_select_dilog, null);

        builder.setView(dialogView);

        // Show the dialog
        AlertDialog dialog = builder.create();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        // Set up the radio group
        // RadioGroup radioGroup = dialogView.findViewById(R.id.radioGroup);

        // Set up the radio buttons
        final RadioButton radioTaxInclude = dialogView.findViewById(R.id.radioTaxInclude);
        final RadioButton radioTaxExclude = dialogView.findViewById(R.id.radioTaxExclude);


        TextView taxExcludeTxt = findViewById(R.id.taxExcludeTxt);
        radioTaxInclude.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                changesMade = true;
                selectedTax = "Tax Included";
                taxExcludeTxt.setText("Tax Included");
                isTaxSelected = true;

                findViewById(R.id.selectTaxValueBtn).setVisibility(View.VISIBLE);

                selectedTaxValue = "Tax Value";
                TextView taxValue = findViewById(R.id.taxValueTxt);
                taxValue.setText(selectedTaxValue);
                isTaxValueSelected = false;


                // Dismiss the dialog after a delay of 200ms
                new Handler().postDelayed(new Runnable() {
                    @Override

                    public void run() {
                        dialog.dismiss();
                    }
                }, 200);
            }
        });

        radioTaxExclude.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changesMade = true;

                selectedTax = "Tax Excluded";
                taxExcludeTxt.setText("Tax Excluded");
                isTaxSelected = true;


                // for tax value if tax excluded
                selectedTaxValue = "1";
                isTaxValueSelected = true;
                TextView taxValue = findViewById(R.id.taxValueTxt);
                taxValue.setText(selectedTaxValue);
                findViewById(R.id.selectTaxValueBtn).setVisibility(View.GONE);


                // Dismiss the dialog after a delay of 200ms
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                    }
                }, 200);
            }
        });


    }

    private void selectDoc() {




        ImagePickerBottomSheet bottomSheet = new ImagePickerBottomSheet(1010, 1016);
        bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
        bottomSheet.setImageSelectionListener(new ImagePickerBottomSheet.ImageSelectionListener() {
            @Override
            public void onImageSelected(Uri imageUri) {

                Intent intent = new Intent(NewServiceActivity.this, ImageCompressActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString("image", "doc");
                bundle.putString("originalUri", String.valueOf(imageUri));
                bundle.putInt("length", 3);
                bundle.putInt("height", 4);
                intent.putExtras(bundle);
                startActivityForResult(intent, 10001);


            }

            @Override
            public void onImagePath(Uri imageUri) {


                Intent intent = new Intent(NewServiceActivity.this, ImageCompressActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString("image", "doc");
                bundle.putString("originalUri", String.valueOf(imageUri));
                bundle.putInt("length", 3);
                bundle.putInt("height", 4);
                intent.putExtras(bundle);
                startActivityForResult(intent, 10001);


            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 10001) {
            if (resultCode == RESULT_OK && data != null) {

                Bundle bundle = data.getExtras();

                if (bundle != null) {

                    String croppedImageUri = bundle.getString("croppedImageUri");

                    if (bundle.getString("image", "none").matches("doc")) {

                        ImageView imageView = findViewById(R.id.imgShowLogo);
                        imageView.setVisibility(View.VISIBLE);
                        findViewById(R.id.uploadViewLayLogo).setVisibility(View.GONE);
                        Glide.with(getApplicationContext()).load(croppedImageUri).into(imageView);

                        isIdImageSelected = true;
                        docUri = getImageRealPathFromURI(Uri.parse(croppedImageUri));

                        changesMade = true;


                    }


                } else {

                    Log.d("NEWSERVICE", "Error 2 Null Bundle ");

                }
            }

        }


    }


    public String getImageRealPathFromURI(Uri contentUri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String filePath = cursor.getString(column_index);
            cursor.close();
            return filePath;
        }
        return contentUri.getPath(); // Fallback to the URI itself
    }

    private void setTagsMethod() {
        dataList = new ArrayList<>();
        // Set adapter layout
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        // Set input type to make the "Enter" button visible
        tagsEt.setInputType(EditorInfo.TYPE_CLASS_TEXT);
        tagsEt.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                // Perform your action here

                changesMade = true;

                if (dataList.size() == 5) {    //for tags limit

                    findViewById(R.id.maxTxt).startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake));


                } else {
                    dataList.add(new TagModel(tagsEt.getText().toString().trim()));
                    setAdapterLayout();
                    tagsEt.setText("");
                }

                return true;
            }
            return false;
        });


    }

    private void showSubCatDialog() {

        SubCatBottomSheet bottomSheet = new SubCatBottomSheet(NewServiceActivity.this, subCategoriesList);
        bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());

        bottomSheet.setSubCatBottomSheetListener(new SubCatBottomSheet.SubCatBottomSheetListener() {
            @Override
            public void onItemSelected(String id, String subCatName) {


                selectedSubCatTxt.setText(subCatName);
                isSubCatSelected = true;
                selectedSubCat = subCatName;
                selectedSubCatID = id;

                changesMade = true;


            }
        });
    }

    private void showCatDialog() {

        CatBottomSheet bottomSheet = new CatBottomSheet(NewServiceActivity.this, categories);
        bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());

        bottomSheet.setCatBottomSheetListener(new CatBottomSheet.CatBottomSheetListener() {
            @Override
            public void onItemSelected(String id, String categoryName, List<Subcategory> subCatName) {

                changesMade = true;

                selectedCatTxt.setText(categoryName);
                selectedCat = categoryName;
                selectedCatID = id;
                isCatSelected = true;

                subCategoriesList.clear();
                subCategoriesList.addAll(subCatName);

                if (isSubCatSelected) {

                    selectedSubCatTxt.setText("Select sub category");
                    selectedSubCat = "";
                    isSubCatSelected = false;
                }

                findViewById(R.id.selectSubCatBtn).setVisibility(View.VISIBLE);


            }
        });
    }


    private void initializeViews() {

        durationTxt = findViewById(R.id.durationTxt);

        serviceTitleET = findViewById(R.id.etServiceTitle);
        tagsEt = findViewById(R.id.tagsET);
        finalPriceEt = findViewById(R.id.finalPriceEt);
        discountPriceET = findViewById(R.id.discountPriceEt);
        membersEt = findViewById(R.id.membersETService);
        durationEt = findViewById(R.id.durationEt);

        htmlEditor = findViewById(R.id.richEditor);
        selectedCatTxt = findViewById(R.id.selectedCatTxt);
        selectedSubCatTxt = findViewById(R.id.selectedSubCatTxt);

        statusSwitch = findViewById(R.id.switchStatus);
        stepsCountText = findViewById(R.id.stepsCountText);
        recyclerView = findViewById(R.id.recylerview);

    }

    private void setAdapterLayout() {
        adapter = new TagAdapter(dataList, position -> {
            // Handle item click here
            ((TagAdapter) recyclerView.getAdapter()).removeItem(position);
        });
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }


    private void setUpHtmlEditor() {


        boldBtn = findViewById(R.id.boldButton);
        colorBtn = findViewById(R.id.colorButton);

        TextView selectedFontSizeTxt = findViewById(R.id.selectedFontSizeTxt);


        findViewById(R.id.insertBullets).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                htmlEditor.setBullets();

            }
        });
        findViewById(R.id.insertNumbers).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                htmlEditor.setNumbers();

            }
        });

        findViewById(R.id.alignLeftButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                htmlEditor.setAlignLeft();
            }
        });
        findViewById(R.id.alignCenterButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                htmlEditor.setAlignCenter();
            }
        });
        findViewById(R.id.alignRightButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                htmlEditor.setAlignRight();
            }
        });

        findViewById(R.id.linksButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showAddUrlDialog(htmlEditor);

            }
        });

        findViewById(R.id.fontSizeButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 1-7

                showNumberPopupMenu(view, htmlEditor, selectedFontSizeTxt);


            }
        });

        boldBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                htmlEditor.setBold();
            }
        });






        colorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
// Java Code
                new MaterialColorPickerDialog
                        .Builder(NewServiceActivity.this)
                        .setTitle("Pick Color")
                        .setColorShape(ColorShape.CIRCLE)
                        .setColorSwatch(ColorSwatch._900)
                        .setColors(getcolorsList())
                        .setColorListener(new ColorListener() {
                            @Override
                            public void onColorSelected(int color, @NotNull String colorHex) {
                                // Handle Color Selection

                                htmlEditor.setTextColor(color);
                                colorBtn.setColorFilter(color);


                            }
                        })
                        .show();

            }
        });


    }

    private List<String> getcolorsList() {


        List<String> colorsList = new ArrayList<>();

        // Add commonly used hex color codes to the list
        colorsList.add("#000000"); // Black
        colorsList.add("#FFFFFF"); // White
        colorsList.add("#FF0000"); // Red
        colorsList.add("#00FF00"); // Lime
        colorsList.add("#0000FF"); // Blue
        colorsList.add("#FFFF00"); // Yellow
        colorsList.add("#FFA500"); // Orange
        colorsList.add("#800080"); // Purple
        colorsList.add("#FFC0CB"); // Pink
        colorsList.add("#00FFFF"); // Cyan
        colorsList.add("#FFD700"); // Gold
        colorsList.add("#008080"); // Teal
        colorsList.add("#FF69B4"); // HotPink
        colorsList.add("#FF6347"); // Tomato
        colorsList.add("#DC143C"); // Crimson
        colorsList.add("#FA8072"); // Salmon
        colorsList.add("#4682B4"); // SteelBlue
        colorsList.add("#8A2BE2"); // BlueViolet
        colorsList.add("#808080"); // Gray
        colorsList.add("#FF4500"); // OrangeRed


        return colorsList;


    }


    private void showAddUrlDialog(RichEditor richEditor) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Inflate the custom layout
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.link_add_dilog, null);

        builder.setView(dialogView);

        // Show the dialog
        AlertDialog dialog = builder.create();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        // Set up the radio group
        // RadioGroup radioGroup = dialogView.findViewById(R.id.radioGroup);

        // Set up the radio buttons
        final Button addLinkBtn = dialogView.findViewById(R.id.addLinkBtn);
        final TextInputEditText etLinks = dialogView.findViewById(R.id.etLink);


        addLinkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!etLinks.getText().toString().isEmpty()) {

                    String url = etLinks.getText().toString().trim();
                    richEditor.insertLink(url, url);
                    // Dismiss the dialog after a delay of 200ms
                    new Handler().postDelayed(new Runnable() {
                        @Override

                        public void run() {
                            dialog.dismiss();
                        }
                    }, 200);
                } else {

                    CustomToastNegative.create(getApplicationContext(), "Enter Url!");
                }
            }
        });


    }

    private void showNumberPopupMenu(View anchorView, RichEditor richEditor, TextView selectedFontSizeTxt) {
        PopupMenu popupMenu = new PopupMenu(this, anchorView);

        // Inflate the menu resource
        popupMenu.getMenuInflater().inflate(R.menu.number_menu, popupMenu.getMenu());

        // Set the item click listener
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // Handle the item click (show a Toast with the selected number)
                int selectedNumber = Integer.parseInt(item.getTitle().toString());


                richEditor.setFontSize(selectedNumber);

                selectedFontSizeTxt.setText(String.valueOf(selectedNumber));


                return true;
            }
        });

        // Show the PopupMenu
        popupMenu.show();
    }

    private class UpdateService extends AsyncTask<Void, Void, Void> {

        Activity activity;

        public UpdateService(Activity activity) {
            this.activity = activity;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            // Perform background operation, e.g., user


            // Assuming you have fields like these
            String id = providerId;
            String serviceTitle = serviceTitleET.getText().toString().trim();

            StringBuilder tagsBuilder = new StringBuilder();
            for (TagModel tagModel : dataList) {
                String tag = tagModel.getTag();
                tagsBuilder.append(tag).append(","); // You can use a separator of your choice
            }

// Remove the trailing separator if needed
            if (tagsBuilder.length() > 0) {
                tagsBuilder.setLength(tagsBuilder.length() - 1);
            }

// Convert the StringBuilder to String
            String serviceTags = tagsBuilder.toString();


            String serviceStatus = status;
            String taxType = selectedTax;
            double taxValue = Double.parseDouble(selectedTaxValue.trim()); // Replace with your actual tax value
            String currency = selectedCurrency.trim();
            double finalPrice = Double.parseDouble(finalPriceEt.getText().toString().trim()); // Replace with your actual final price
            double discountedPrice = Double.parseDouble(discountPriceET.getText().toString().trim()); // Replace with your actual discounted price
            int members = Integer.parseInt(membersEt.getText().toString().trim()); // Replace with your actual number of members

            String durString = "";
            if (durationEt.getText().toString().matches("0")) {

                selectedDuration = "When service finish";
                durString = selectedDuration;

            } else {

                selectedDuration = durationEt.getText().toString() + " " + durationTxt.getText().toString();

                durString = selectedDuration;

            }


            Log.d("STRINGDUR", "bg: " + durString);

            String description = HTML_CONTENT;

// Create RequestBody for each text field
            RequestBody providerIdBody = RequestBody.create(MediaType.parse("text/plain"), proId);
            RequestBody serviceIdBody = RequestBody.create(MediaType.parse("text/plain"), serId);
            RequestBody serviceTitleBody = RequestBody.create(MediaType.parse("text/plain"), serviceTitle);
            RequestBody serviceTagsBody = RequestBody.create(MediaType.parse("text/plain"), serviceTags);
            RequestBody serviceCategoryBody = RequestBody.create(MediaType.parse("text/plain"), selectedCatID);
            RequestBody serviceSubCategoryBody = RequestBody.create(MediaType.parse("text/plain"), selectedSubCatID);
            RequestBody serviceStatusBody = RequestBody.create(MediaType.parse("text/plain"), serviceStatus);
            RequestBody taxTypeBody = RequestBody.create(MediaType.parse("text/plain"), taxType);
            RequestBody taxValueBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(taxValue));
            RequestBody currencyBody = RequestBody.create(MediaType.parse("text/plain"), currency);
            RequestBody finalPriceBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(finalPrice));
            RequestBody discountedPriceBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(discountedPrice));
            RequestBody membersBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(members));
            RequestBody durationBody = RequestBody.create(MediaType.parse("text/plain"), durString);


            RequestBody descriptionBody = RequestBody.create(MediaType.parse("text/plain"), description);


            MultipartBody.Part imagePart = null;
            if (docUri != null) {


                File imageFile = new File(docUri);
                // Create RequestBody for image file
                RequestBody imageBody = RequestBody.create(MediaType.parse("image/*"), imageFile);
                // Create MultipartBody.Part for the image
                imagePart = MultipartBody.Part.createFormData("service_image", imageFile.getName(), imageBody);

            }


            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

            Call<UpdateServiceResponse> call = apiInterface.updateService(providerIdBody, serviceIdBody, serviceTitleBody
                    , serviceTagsBody, serviceCategoryBody
                    , serviceSubCategoryBody, serviceStatusBody, taxTypeBody, taxValueBody, currencyBody,
                    finalPriceBody, discountedPriceBody, membersBody, durationBody, descriptionBody, imagePart);

            call.enqueue(new Callback<UpdateServiceResponse>() {
                @Override
                public void onResponse(Call<UpdateServiceResponse> call, Response<UpdateServiceResponse> response) {

                    if (response.isSuccessful() && response.body() != null) {

                        UpdateServiceResponse serviceResponse = response.body();

                        if (serviceResponse.isSuccess()) {


                            CustomToastPositive.create(getApplicationContext(), " "+serviceResponse.getMessage());


                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();

                                }
                            });
                            finish();


                        }


                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.d("ERROROROR", "run: " + response.message());

                                progressDialog.dismiss();

                            }
                        });
                    }
                }

                @Override
                public void onFailure(Call<UpdateServiceResponse> call, Throwable t) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();

                            Log.d("ERROROROR", "run: " + t.getMessage());

                        }
                    });
                }
            });

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // This method runs on the UI thread and can be used to update the UI after background task completion

        }
    }

    private class SaveNewService extends AsyncTask<Void, Void, Void> {

        Activity activity;

        public SaveNewService(Activity activity) {
            this.activity = activity;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            // Perform background operation, e.g., user

            // Assuming you have fields like these
            String id = providerId;
            String serviceTitle = serviceTitleET.getText().toString().trim();

            StringBuilder tagsBuilder = new StringBuilder();
            for (TagModel tagModel : dataList) {
                String tag = tagModel.getTag();
                tagsBuilder.append(tag).append(","); // You can use a separator of your choice
            }

// Remove the trailing separator if needed
            if (tagsBuilder.length() > 0) {
                tagsBuilder.setLength(tagsBuilder.length() - 1);
            }

// Convert the StringBuilder to String
            String serviceTags = tagsBuilder.toString();


            String serviceCategory = selectedCat.trim();
            String serviceSubCategory = selectedSubCat.trim();


            String serviceStatus = status;
            String taxType = selectedTax;
            double taxValue = Double.parseDouble(selectedTaxValue.trim()); // Replace with your actual tax value
            String currency = selectedCurrency.trim();
            double finalPrice = Double.parseDouble(finalPriceEt.getText().toString().trim()); // Replace with your actual final price
            double discountedPrice = Double.parseDouble(discountPriceET.getText().toString().trim()); // Replace with your actual discounted price
            int members = Integer.parseInt(membersEt.getText().toString().trim()); // Replace with your actual number of members

            String durString;
            if (durationEt.getText().toString().matches("0")) {

                durString = selectedDuration;

            } else {
                durString = durationEt.getText().toString().trim() + " " + selectedDuration;

            }


            String description = HTML_CONTENT;

// Create RequestBody for each text field
            RequestBody providerIdBody = RequestBody.create(MediaType.parse("text/plain"), id);
            RequestBody serviceTitleBody = RequestBody.create(MediaType.parse("text/plain"), serviceTitle);
            RequestBody serviceTagsBody = RequestBody.create(MediaType.parse("text/plain"), serviceTags);
            RequestBody serviceCategoryBody = RequestBody.create(MediaType.parse("text/plain"), selectedCatID);
            RequestBody serviceSubCategoryBody = RequestBody.create(MediaType.parse("text/plain"), selectedSubCatID);
            RequestBody serviceStatusBody = RequestBody.create(MediaType.parse("text/plain"), serviceStatus);
            RequestBody taxTypeBody = RequestBody.create(MediaType.parse("text/plain"), taxType);
            RequestBody taxValueBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(taxValue));
            RequestBody currencyBody = RequestBody.create(MediaType.parse("text/plain"), currency);
            RequestBody finalPriceBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(finalPrice));
            RequestBody discountedPriceBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(discountedPrice));
            RequestBody membersBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(members));
            RequestBody durationBody = RequestBody.create(MediaType.parse("text/plain"), durString);
            RequestBody descriptionBody = RequestBody.create(MediaType.parse("text/plain"), description);
            File imageFile = new File(docUri);
            // Create RequestBody for image file
            RequestBody imageBody = RequestBody.create(MediaType.parse("image/*"), imageFile);

            // Create MultipartBody.Part for the image
            MultipartBody.Part imagePart = MultipartBody.Part.createFormData("service_image", imageFile.getName(), imageBody);


            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

            Call<CreateServiceResponse> call = apiInterface.addService(providerIdBody, serviceTitleBody
                    , serviceTagsBody, serviceCategoryBody
                    , serviceSubCategoryBody, serviceStatusBody, taxTypeBody, taxValueBody, currencyBody,
                    finalPriceBody, discountedPriceBody, membersBody, durationBody, descriptionBody, imagePart);

            call.enqueue(new Callback<CreateServiceResponse>() {
                @Override
                public void onResponse(Call<CreateServiceResponse> call, Response<CreateServiceResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {

                        CreateServiceResponse serviceResponse = response.body();


                        if (serviceResponse.isSuccess()) {



                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();

                                }
                            });


                            if (serviceResponse.getMessage().equals("Service added successfully.")){

                                CustomToastPositive.create(getApplicationContext(), " "+serviceResponse.getMessage());

                                finish();




                            }else {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        CustomToastNegative.create(activity,"Service Already exists With Same Name!");

                                        progressDialog.dismiss();

                                    }
                                });

                            }



                        }else {


                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    CustomToastNegative.create(activity,"Service Already exists With Same Name!");

                                    progressDialog.dismiss();

                                }
                            });

                        }


                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                Log.d("ERROEE", "run: " + response.message());
                                progressDialog.dismiss();

                            }
                        });
                    }
                }

                @Override
                public void onFailure(Call<CreateServiceResponse> call, Throwable t) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            Log.d("ERROEE", "run: " + t.getMessage());
                            progressDialog.dismiss();

                        }
                    });
                }
            });

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog.show();

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // This method runs on the UI thread and can be used to update the UI after background task completion


        }
    }

    private class GetTax extends AsyncTask<Void, Void, Void> {

        Activity activity;

        public GetTax(Activity activity) {
            this.activity = activity;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            // Perform background operation, e.g., user

            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

            Call<TaxApiResponse> call = apiInterface.getAllTaxes();

            call.enqueue(new Callback<TaxApiResponse>() {
                @Override
                public void onResponse(Call<TaxApiResponse> call, Response<TaxApiResponse> response) {

                    if (response.isSuccessful() && response.body() != null) {

                        TaxApiResponse taxApiResponse = response.body();

                        if (taxApiResponse.isSuccess()) {

                            taxes.addAll(taxApiResponse.getTaxes());

                            changesMade = false;


                        }


                    } else {

                        Log.d("NEWSERVICE", "Error 2 RES FAIL ");


                    }

                }

                @Override
                public void onFailure(Call<TaxApiResponse> call, Throwable t) {
                    activity.runOnUiThread(() -> {
                        Log.d("NEWSERVICE", "Error 3 "+t.getMessage());


                    });
                }
            });

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // This method runs on the UI thread and can be used to update the UI after background task completion
        }
    }

    private class GetCategories extends AsyncTask<Void, Void, Void> {

        Activity activity;

        public GetCategories(Activity activity) {
            this.activity = activity;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            // Perform background operation, e.g., user

            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

            Call<CategoryResponse> call = apiInterface.getCategories();


            call.enqueue(new Callback<CategoryResponse>() {
                @Override
                public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {

                    if (response.isSuccessful() && response.body() != null) {

                        CategoryResponse categoryResponse = response.body();

                        if (categoryResponse.isSuccess()) {

                            categories.addAll(categoryResponse.getCategories());

                            changesMade = false;


                        }


                    } else {

                        activity.runOnUiThread(() -> {
                            Log.d("NEWSERVICE", "Error 4 Res Fail");


                        });

                    }

                }

                @Override
                public void onFailure(Call<CategoryResponse> call, Throwable t) {

                    activity.runOnUiThread(() -> {
                        Log.d("NEWSERVICE", "Error 5 "+t.getMessage());


                    });

                }
            });


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // This method runs on the UI thread and can be used to update the UI after background task completion
        }
    }

    private class FetchEditService extends AsyncTask<Void, Void, Void> {

        Activity activity;

        public FetchEditService(Activity activity) {
            this.activity = activity;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            // Perform background operation, e.g., user

            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);


            Call<EditServiceRes> call = apiInterface.getServiceDetails(proId, serId);
            call.enqueue(new Callback<EditServiceRes>() {
                @Override
                public void onResponse(Call<EditServiceRes> call, Response<EditServiceRes> response) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();

                        }
                    });

                    if (response.isSuccessful() && response.body() != null) {

                        EditServiceRes editServiceRes = response.body();

                        if (editServiceRes.isSuccess()) {


                            runOnUiThread(() -> {


                                serviceTitleET.setText(editServiceRes.getService().getServiceTitle());
                                processTags(editServiceRes.getService().getServiceTags());

                                selectedCat = editServiceRes.getService().getServiceCategory();
                                selectedSubCat = editServiceRes.getService().getServiceSubCategory();
                                selectedCatID = editServiceRes.getService().getServiceCategoryId();
                                selectedSubCatID = editServiceRes.getService().getServiceSubcategoryId();

                                selectedCatTxt.setText(editServiceRes.getService().getServiceCategory());
                                selectedSubCatTxt.setText(editServiceRes.getService().getServiceSubCategory());
                                isCatSelected = true;
                                isSubCatSelected = true;
                                findViewById(R.id.selectSubCatBtn).setVisibility(View.VISIBLE);

                                if (editServiceRes.getService().getServiceStatus().matches("Enable")) {

                                    statusSwitch.setChecked(true);
                                    isStatusChecked = true;
                                    status = "Enable";

                                    changesMade = false;

                                } else {

                                    statusSwitch.setChecked(false);
                                    isStatusChecked = false;

                                    status = "Disabled";

                                    changesMade = false;
                                }
                                ImageView imgShowLogo = findViewById(R.id.imgShowLogo);

                                TextView taxExcludeTxt = findViewById(R.id.taxExcludeTxt);
                                TextView taxValueTxt = findViewById(R.id.taxValueTxt);
                                TextView currencyTxt = findViewById(R.id.currencyTxt);
                                TextView durationTxt = findViewById(R.id.durationTxt);

                                Glide.with(activity).load(editServiceRes.getService().getServiceImageUrl()).placeholder(R.drawable.app_icon).into(imgShowLogo);
                                isIdImageSelected = true;


                                imgShowLogo.setVisibility(View.VISIBLE);
                                findViewById(R.id.uploadViewLayLogo).setVisibility(View.GONE);

                                selectedTax = editServiceRes.getService().getTaxType();
                                isTaxSelected = true;
                                taxExcludeTxt.setText(selectedTax);

                                selectedTaxValue = editServiceRes.getService().getTaxValue();
                                isTaxValueSelected = true;
                                taxValueTxt.setText("GST (" + editServiceRes.getService().getTaxValue() + ") %");

                                selectedCurrency = editServiceRes.getService().getCurrency();
                                isCurrencySelected = true;
                                currencyTxt.setText(selectedCurrency);

                                finalPriceEt.setText(String.valueOf(editServiceRes.getService().getFinalPrice()));
                                discountPriceET.setText(String.valueOf(editServiceRes.getService().getDiscountedPrice()));
                                membersEt.setText(editServiceRes.getService().getMembers());

                                isDurationSelected = true;

                                if (editServiceRes.getService().getServiceDuration().contains("Minute")) {
                                    durationEt.setText(editServiceRes.getService().getServiceDuration().replace(" Minute", ""));
                                    durationTxt.setText("Minute");
                                    selectedDuration = durationEt.getText().toString() + " " + durationTxt.getText().toString();


                                    Log.d("SIISSU", "onResponse: " + selectedDuration);

                                }

                                if (editServiceRes.getService().getServiceDuration().contains("Hourly")) {
                                    durationEt.setText(editServiceRes.getService().getServiceDuration().replace(" Hourly", ""));

                                    durationTxt.setText("Hourly");
                                    selectedDuration = durationEt.getText().toString() + " " + durationTxt.getText().toString();

                                }

                                if (editServiceRes.getService().getServiceDuration().contains("Day")) {
                                    durationTxt.setText("Day");
                                    durationEt.setText(editServiceRes.getService().getServiceDuration().replace(" Day", ""));
                                    selectedDuration = durationEt.getText().toString() + " " + durationTxt.getText().toString();
                                }

                                if (editServiceRes.getService().getServiceDuration().matches("When service finish")) {

                                    durationEt.setText("0");
                                    durationTxt.setText("When service finish");
                                    durationEt.setEnabled(false);
                                    selectedDuration = "When service finish";

                                }


                                htmlEditor.setHtml(editServiceRes.getService().getDescription());


                                changesMade = false;

                            });


                        } else {
                            runOnUiThread(() -> {
                                Log.d("NEWSERVICE", "Error 6 "+editServiceRes.getMessage());

                            });

                        }


                    } else {
                        runOnUiThread(() -> {
                            Log.d("NEWSERVICE", "Error 7 ");

                        });
                    }
                }

                @Override
                public void onFailure(Call<EditServiceRes> call, Throwable t) {

                    Log.d("NEWSERVICE", "Error 8 "+t.getMessage());

                }
            });


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // This method runs on the UI thread and can be used to update the UI after background task completion
        }
    }


    private void processTags(String tagsString) {
        // Clear existing data or initialize the list if needed
        dataList = new ArrayList<>();

        // Split the tags string using ","
        String[] tagsArray = tagsString.split(",");

        // Iterate through the tags and create TagModel objects
        for (String tag : tagsArray) {
            // Trim any leading or trailing whitespaces from each tag
            String trimmedTag = tag.trim();

            // Create a TagModel object and add it to the list
            TagModel tagModel = new TagModel(trimmedTag);
            dataList.add(tagModel);
            setAdapterLayout();
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }

        // Now, dataList contains TagModel objects for each tag
    }

    private void internetStatus() {

        registerReceiver(receiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(receiver);
    }


    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        ExitBottomSheet exitBottomSheet = new ExitBottomSheet(true);
        exitBottomSheet.show(getSupportFragmentManager(), exitBottomSheet.getTag());
    }
}