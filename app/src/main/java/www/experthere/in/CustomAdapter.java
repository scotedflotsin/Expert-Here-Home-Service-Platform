package www.experthere.in;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.libraries.places.api.model.AutocompletePrediction;

import java.util.List;

public class CustomAdapter extends ArrayAdapter<AutocompletePrediction> {

    public CustomAdapter(Context context, List<AutocompletePrediction> items) {
        super(context, 0, items);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Get the data item for this position
        AutocompletePrediction item = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_list_item, parent, false);
        }

        // Lookup view for data population
        ImageView iconImageView = convertView.findViewById(R.id.iconImageView);
        TextView addressTextView = convertView.findViewById(R.id.addressTextView);

        // Populate the data into the template view using the data object
        if (item != null) {
            // Set the icon (you can replace this with your own logic)
            iconImageView.setImageResource(R.drawable.baseline_location_on_24);

            // Set the address
            addressTextView.setText(item.getFullText(null).toString());

            // Set a dummy distance (you can replace this with your own logic)

        }

        // Return the completed view to render on screen
        return convertView;
    }
}
