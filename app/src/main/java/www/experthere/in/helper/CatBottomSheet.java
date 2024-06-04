package www.experthere.in.helper;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

import www.experthere.in.R;
import www.experthere.in.adapters.CategoryAdapter;
import www.experthere.in.model.Category;
import www.experthere.in.model.Subcategory;

public class CatBottomSheet extends BottomSheetDialogFragment {

    Activity activity;
    ArrayList<Category> categories;

    public CatBottomSheet(Activity activity, ArrayList<Category> categories) {
        this.activity = activity;
        this.categories = categories;
    }

    public interface CatBottomSheetListener {
        void onItemSelected(String id,String categoryName,List<Subcategory> subCatName );
    }

    private CatBottomSheetListener mListener;

    public void setCatBottomSheetListener(CatBottomSheetListener listener) {
        this.mListener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cat_select_dilog, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerCat);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        CategoryAdapter adapter = new CategoryAdapter(categories, activity, new CategoryAdapter.OnCategorySelectedListener() {
            @Override
            public void onCategorySelected(String id, String categoryName, List<Subcategory> subCatName) {

                if (mListener != null) {
                    mListener.onItemSelected(id,categoryName,subCatName);
                }

                dismiss();
            }
        });

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(linearLayoutManager);

        return view;
    }
}
