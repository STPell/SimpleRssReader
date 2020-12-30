package com.example.hellodroid;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.CycleInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.fragment.app.DialogFragment;

import java.util.List;

public class NewFeedDialog extends DialogFragment {

    private Boolean valid_url = false;
    private Boolean back_button = false;
    private List<String> returnLocation;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.dialog_add_feed, null))
                .setPositiveButton("Add Feed", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        return;
                    }
                })
                .setNegativeButton("Back", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        NewFeedDialog.this.getDialog().cancel();
                        back_button = true;
                    }
                });

        Dialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Button positiveButton = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE);

                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        EditText entryBox = (EditText) getDialog().findViewById(R.id.new_feed_input);
                        TextView warningText = (TextView) getDialog().findViewById(R.id.new_feed_warning);
                        String newFeed = entryBox.getText().toString();

                        if (Patterns.WEB_URL.matcher(newFeed).matches()) {
                            returnLocation.add(newFeed);
                            onDismiss(dialog);
                        } else {
                            valid_url = false;

                            //Shake the dialog box and display a warning.
                            ((AlertDialog) dialog).getWindow()
                                                  .getDecorView()
                                                  .animate()
                                                  .translationX(16f)
                                                  .setInterpolator(new CycleInterpolator(2.5f));

                            warningText.setText("Please enter a valid URL");
                            warningText.setTextColor(Color.rgb(255,0,0));
                        }
                    }
                });
            }
        });
        return dialog;
    }

    void setReturnLocation(List<String> returnLocation_) {
        returnLocation = returnLocation_;
    }
}
