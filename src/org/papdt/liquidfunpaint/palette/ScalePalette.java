package org.papdt.liquidfunpaint.palette;

import android.view.LayoutInflater;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.ColorPickerView;
import android.os.Bundle;
import android.app.Dialog;
import android.view.View;
import android.widget.SeekBar;
import android.content.DialogInterface;
import org.papdt.liquidfunpaint.Controller;
import com.flask.colorpicker.ColorPickerView;

import org.papdt.liquidfunpaint.R;

public class ScalePalette extends Palette {
    private SeekBar mSbHeight, mSbWidth;
    private ColorPickerView mColorPicker;
    private static final float MAX = 1000f;

    public ScalePalette(Controller c, int initialColor) {
        super(c,initialColor);
    }

    @Override
    protected View inflateView(LayoutInflater inflater) {
        View v = inflater.inflate(R.layout.scale_palette, null);
        mSbHeight = (SeekBar) v.findViewById(R.id.sb_h);
        mSbWidth = (SeekBar) v.findViewById(R.id.sb_w);
        mColorPicker = (ColorPickerView) v.findViewById(R.id.color_picker);
        mColorPicker.setInitialColor(mColor, false);
        return v;
    }

    @Override
    protected void onApply() {
        // useless, thereby do nothing.
    }

}
