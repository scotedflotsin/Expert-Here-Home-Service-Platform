package www.experthere.in.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import www.experthere.in.R;
import www.experthere.in.model.Taxes;

public class TaxAdapter extends RecyclerView.Adapter<TaxAdapter.ViewHolder> {

    private final ArrayList<Taxes> taxes;
    private final Activity activity;
    private final OnTaxSelectedListener taxSelectedListener;

    public TaxAdapter(ArrayList<Taxes> taxes, Activity activity, OnTaxSelectedListener taxSelectedListener) {
        this.taxes = taxes;
        this.activity = activity;
        this.taxSelectedListener = taxSelectedListener;
    }

    @NonNull
    @Override
    public TaxAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tax_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaxAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.radioButton.setText("Including All Taxes ("+taxes.get(position).getPercentage()+") %");

        holder.radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (taxSelectedListener != null) {
                    taxSelectedListener.onTaxSelectedListener(taxes.get(position).getId(),taxes.get(position).getPercentage());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return taxes.size();


    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        RadioButton radioButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            radioButton = itemView.findViewById(R.id.radioItem);
        }
    }

    // Interface to define the callback method
    public interface OnTaxSelectedListener {
        void onTaxSelectedListener( String id, String percentage);
    }
}
