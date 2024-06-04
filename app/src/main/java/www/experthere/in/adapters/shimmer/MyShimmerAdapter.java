package www.experthere.in.adapters.shimmer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import www.experthere.in.Common.GetShimmerStrings;
import www.experthere.in.R;

public class MyShimmerAdapter extends RecyclerView.Adapter<MyShimmerAdapter.ViewHolder> {


    String type;

    public MyShimmerAdapter(String type) {
        this.type = type;
    }

    @NonNull
    @Override
    public MyShimmerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = null;

        if (type.equals(GetShimmerStrings.PROVIDER_CALL)) {

            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.provider_call_shimmer, parent, false);

        }


        if (type.equals(GetShimmerStrings.PROVIDER_ORDER)) {

            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_shimmer_layout, parent, false);

        }


        if (type.equals(GetShimmerStrings.PROVIDER_SERVICES)) {

            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.services_shimmer_provider, parent, false);

        }
        if (type.equals(GetShimmerStrings.PROVIDER_REVIEWS)) {

            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_review_shimmer_lay, parent, false);

        }
        if (type.equals(GetShimmerStrings.USER_CALL)) {

            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.call_shimmer_user, parent, false);

        }
        if (type.equals(GetShimmerStrings.USER_SERVICE)) {

            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.services_shimmer_user, parent, false);

        }
        if (type.equals(GetShimmerStrings.USER_BOOKMARK)) {

            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bookmark_shimmer, parent, false);

        }
        if (type.equals(GetShimmerStrings.USER_HOME_SERVICE)) {

            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_service_shimmer, parent, false);

        }
        if (type.equals(GetShimmerStrings.USER_HOME_CAT)) {

            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_shimmer_home, parent, false);

        }
        if (type.equals(GetShimmerStrings.USER_HOME_SERVICES_2)) {

            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_service_shimmer2, parent, false);

        }

        if (type.equals(GetShimmerStrings.USER_HOME_Providers)) {

            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.provider_list_shimmer, parent, false);

        }

        if (type.equals(GetShimmerStrings.USER_SUBCAT)) {

            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sub_category_shimmer, parent, false);

        }
  if (type.equals(GetShimmerStrings.PROVIDER_REVIEWS_Images)) {

            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shimmer_review_images, parent, false);

        }


        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MyShimmerAdapter.ViewHolder holder, int position) {


    }

    @Override
    public int getItemCount() {
        return 12;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
