package www.experthere.in.helper;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import www.experthere.in.R;

public class CustomToastPositive {

    public static void create(Context context, String message) {
        // Inflate the custom layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.custom_toast_layout_positive, null);

        // Set the text for the custom toast
        TextView text = layout.findViewById(R.id.message_text_view);
        text.setText(message);

        // Set layout parameters with margin
        int margin = (int) (context.getResources().getDisplayMetrics().density * 16); // 16dp margin
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(margin, 0, margin, 0);
        layout.setLayoutParams(params);

        // Create and show the custom toast
        Toast toast = new Toast(context.getApplicationContext());
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);

        // Set the toast to occupy almost the full width of the screen with margins
        toast.setGravity(Gravity.BOTTOM , 0, 100);
        toast.show();
    }
}
