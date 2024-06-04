package www.experthere.in.users;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import www.experthere.in.helper.CustomToastNegative;
import www.experthere.in.helper.EmailValidator;
import www.experthere.in.R;

public class BottomSheetFragment extends BottomSheetDialogFragment {

    private OnSubmitListener onSubmitListener;

    public static BottomSheetFragment newInstance() {
        return new BottomSheetFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Ensure the hosting activity implements the interface
        if (context instanceof OnSubmitListener) {
            onSubmitListener = (OnSubmitListener) context;
        } else {
            throw new ClassCastException(context + " must implement OnSubmitListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bottom_sheet, container, false);

        EditText editTextEmail = view.findViewById(R.id.editTextEmail);
        Button buttonSubmit = view.findViewById(R.id.buttonSubmit);

        buttonSubmit.setOnClickListener(v -> {
            // Get the entered email
            String enteredEmail = editTextEmail.getText().toString().trim();

            // Check if the email is not empty
            if (!enteredEmail.isEmpty()) {
                // Pass the entered email to the MainActivity

                if (EmailValidator.isValidEmail(enteredEmail)){

                    onSubmitListener.onSubmit(enteredEmail);
                    dismiss();

                }else {
                    CustomToastNegative.create(getActivity(),"Wrong Email!");

                }


                // Dismiss the bottom sheet
            } else {
                // Show a toast if the email is empty
                CustomToastNegative.create(getActivity(),"Enter  Email!");
            }
        });

        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        dialog.setOnShowListener(dialogInterface -> {
            // Customize entrance animation here
        });
        return dialog;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        // Customize exit animation here
    }
}
