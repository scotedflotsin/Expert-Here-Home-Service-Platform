package www.experthere.in.adapters.shimmer;

import android.content.Context;
import android.util.AttributeSet;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class NonScrollableLayoutManager extends LinearLayoutManager {

    public NonScrollableLayoutManager(Context context) {
        super(context);
    }

    public NonScrollableLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public NonScrollableLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean canScrollVertically() {
        return false; // Disable vertical scrolling
    }

    @Override
    public boolean canScrollHorizontally() {
        return false; // Disable horizontal scrolling
    }
}
