package org.papdt.liquidfunpaint.palette;

import android.view.LayoutInflater;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.ColorPickerView;
import android.os.Bundle;
import android.app.Dialog;
import android.view.View;
import android.content.DialogInterface;
import org.papdt.liquidfunpaint.Controller;

import org.papdt.liquidfunpaint.R;

public class PencilPalette extends Palette {
    public PencilPalette(Controller c) {
        super(c);
    }

    @Override
    protected View inflateView(LayoutInflater inflater) {
        // useless, thereby do nothing.
        return null;
    }

    @Override
    protected void onApply() {
        // useless, thereby do nothing.
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return ColorPickerDialogBuilder
            .with(getActivity())
            .setTitle(getString(R.string.pencil))
            .initialColor(getResources().getColor(R.color.color_rigid_3))
            .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
            .density(12)
            .setPositiveButton(getString(android.R.string.ok), new ColorPickerClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                    mController.setColor(selectedColor);
                }
            })
            .setNegativeButton(getString(android.R.string.cancel), this)
            .build();
    }
}
