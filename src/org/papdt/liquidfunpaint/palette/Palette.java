package org.papdt.liquidfunpaint.palette;

import android.view.View;
import android.view.LayoutInflater;
import android.app.DialogFragment;
import android.app.Dialog;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;

public abstract class Palette extends DialogFragment implements DialogInterface.OnClickListener{
    protected static String TAG = "Palette";
    protected View mDialogView;

    protected abstract View inflateView(LayoutInflater inflater);

    public void openPalette(FragmentManager manager) {
        if (mDialogView == null) {
            mDialogView = inflateView(getActivity().getLayoutInflater());
        }
        this.show(manager, TAG);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(mDialogView)
            .setPositiveButton(android.R.string.ok, this)
            .setNegativeButton(android.R.string.cancel, this);
        return builder.create();
    }

    public void closePalette() {
        getDialog().cancel();
    }
}
