package www.experthere.in.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import www.experthere.in.R;
import www.experthere.in.model.TagModel;

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.ViewHolder> {

    private final ArrayList<TagModel> dataList;
    private final ItemClick itemClick;

    public TagAdapter(ArrayList<TagModel> dataList, ItemClick itemClick) {
        this.dataList = dataList;
        this.itemClick = itemClick;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.editTextText);
            textView.setOnClickListener(v -> itemClick.OnItemClick(getAdapterPosition()));
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.tags_lay, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TagModel currentTag = dataList.get(position);
        // Bind your data to the views in the ViewHolder
        holder.textView.setText(currentTag.getTag());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public interface ItemClick {
        void OnItemClick(int position);
    }

    // Function to remove an item from the dataset and notify the adapter
    public void removeItem(int position) {
        dataList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, dataList.size());
    }
}
