package com.moez.QKSMS.ui.dialog;

import android.app.DialogFragment;


public interface DialogFragmentListener {
    // Called when the DialogFragment button is pressed, the DialogFragment is dismissed, etc.
    public void onDialogFragmentResult(int resultCode, DialogFragment fragment);

    // Called when a list item within the dialog is pressed.
    public void onDialogFragmentListResult(int resultCode, DialogFragment fragment, int index);
}
