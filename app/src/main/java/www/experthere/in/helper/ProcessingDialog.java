package www.experthere.in.helper;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import www.experthere.in.R;

public class ProcessingDialog extends ProgressDialog {

    public ProcessingDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_processing); // Custom layout for the progress dialog
        setCancelable(false); // Prevent dialog from being canceled by touching outside
        setMessage("Processing..."); // Set message to display "Processing..."
    }



}
