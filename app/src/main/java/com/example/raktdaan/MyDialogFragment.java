package com.example.raktdaan;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

public class MyDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("About:");
        builder.setMessage("Developed By:Shashwat Shukla(11514)\n\t2017-2021\n\nUnder the Guidance of:Dr. Vishal Passricha\n\nCentral University of Haryana");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // You don't have to do anything here if you just
                // want it dismissed when clicked
            }
        });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
