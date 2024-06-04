package www.experthere.in.helper.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.DecimalFormat;
import java.util.List;

import www.experthere.in.R;
import www.experthere.in.helper.ServiceDetailsBottomSheet;
import www.experthere.in.model.service_user_cat.Service;

public class SelectServiceAdapter extends RecyclerView.Adapter<SelectServiceAdapter.ViewHolder> {

    List<Service> services;
    Activity activity;

    private final GetService getService;


    public interface GetService {


        void getService(Service service);


    }

    public SelectServiceAdapter(List<Service> services, Activity activity, GetService getService) {
        this.services = services;
        this.activity = activity;
        this.getService = getService;
    }

    public void setData(List<Service> services) {
        this.services = services;
        notifyDataSetChanged();
    }

    public void addData(List<Service> newServiceList) {
        if (services != null) {
            services.addAll(newServiceList);
            notifyDataSetChanged();
        }
    }


    @NonNull
    @Override
    public SelectServiceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.string_row, parent, false);
        return new ViewHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull SelectServiceAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {


        holder.serviceTitle.setText(services.get(position).getServiceTitle());
        holder.serviceTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getService.getService(services.get(position));

            }
        });

        }



    @Override
    public int getItemCount() {
        return services.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView serviceTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);


          serviceTitle = itemView.findViewById(R.id.title_service);


        }


    }
}
