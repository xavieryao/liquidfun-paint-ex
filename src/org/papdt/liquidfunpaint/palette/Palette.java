package org.papdt.liquidfunpaint.palette;

import android.view.View;
import android.view.LayoutInflater;
import android.app.DialogFragment;
import android.app.Dialog;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;

import org.papdt.liquidfunpaint.Controller;

public abstract class Palette extends DialogFragment implements DialogInterface.OnClickListener{
    protected static String TAG = "Palette";
    protected View mDialogView;
    protected Controller mController;
    protected int mColor;

    protected abstract View inflateView(LayoutInflater inflater);
    protected abstract void onApply();

    public Palette(Controller c, int color) {
        mController = c;
        mColor = color;
    }

    public void openPalette(FragmentManager manager) {
        this.show(manager, TAG);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mDialogView = inflateView(getActivity().getLayoutInflater());
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(mDialogView)
            .setPositiveButton(android.R.string.ok, this)
            .setNegativeButton(android.R.string.cancel, this);
        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch(which) {
            case DialogInterface.BUTTON_POSITIVE:
                onApply();
                break;
            case DialogInterface.BUTTON_NEGATIVE:
                break;
        }
    }

    public void closePalette() {
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.cancel();
        }
    }
}
