package www.experthere.in.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import www.experthere.in.R;

public class SearchSuggestionAdapter extends ArrayAdapter<String> {

    Context context;
        public SearchSuggestionAdapter(Context context, List<String> items) {
            super(context, 0, items);

            this.context = context;

        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            // Get the data item for this position
            String item = getItem(position);

            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_search_item, parent, false);
            }




            // Lookup view for data population
            TextView searchItemTxt = convertView.findViewById(R.id.searchTextView);

            // Populate the data into the template view using the data object
            if (item != null) {
                // Set the icon (you can replace this with your own logic)
                // Set the address
                searchItemTxt.setText(item);

                // Set a dummy distance (you can replace this with your own logic)

            }

            // Return the completed view to render on screen
            return convertView;
        }


}
