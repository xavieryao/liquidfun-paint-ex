package org.papdt.liquidfunpaint.palette;

import android.view.LayoutInflater;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.ColorPickerView;
import android.os.Bundle;
import android.app.Dialog;
import android.view.View;
import android.content.DialogInterface;
import org.papdt.liquidfunpaint.Controller;

import org.papdt.liquidfunpaint.R;

public class ColorPalette extends Palette {
    public ColorPalette(Controller c, int initialColor) {
        super(c,initialColor);
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
            .initialColor(mColor)
            .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
            .density(12)
            .setOnColorSelectedListener(new OnColorSelectedListener(){
                @Override
                public void onColorSelected(int color) {
                  mColor = color;
                }
            })
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
