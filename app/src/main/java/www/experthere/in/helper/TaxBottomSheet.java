package www.experthere.in.helper;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

import www.experthere.in.R;
import www.experthere.in.adapters.CategoryAdapter;
import www.experthere.in.adapters.TaxAdapter;
import www.experthere.in.model.Category;
import www.experthere.in.model.Taxes;

public class TaxBottomSheet extends BottomSheetDialogFragment {

    Activity activity;
    ArrayList<Taxes> tax;

    public TaxBottomSheet(Activity activity, ArrayList<Taxes> tax) {
        this.activity = activity;
        this.tax = tax;
    }

    public interface TaxBottomSheetListener {
        void onItemSelected(String id,String percentage );
    }

    private TaxBottomSheetListener mListener;

    public void setTaxBottomSheetListener(TaxBottomSheetListener listener) {
        this.mListener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tax_value_select_dilog, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerTax);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        TaxAdapter taxAdapter = new TaxAdapter(tax, activity, new TaxAdapter.OnTaxSelectedListener() {
            @Override
            public void onTaxSelectedListener(String id, String percentage) {


                if (mListener != null) {
                    mListener.onItemSelected(id,percentage);
                }

                dismiss();

            }
        });

        recyclerView.setAdapter(taxAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);

        return view;
    }
}
