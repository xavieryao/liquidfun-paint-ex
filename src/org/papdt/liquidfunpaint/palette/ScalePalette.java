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
    private SeekBar mSbHeight, mSbWidth, mSbDensity;
    private ColorPickerView mColorPicker;
    private static final float MAX = 2*1000f;
    private static final float MAX_DENSITY = 20f;

    public ScalePalette(Controller c, int initialColor) {
        super(c,initialColor);
    }

    @Override
    protected View inflateView(LayoutInflater inflater) {
        View v = inflater.inflate(R.layout.scale_palette, null);
        mSbHeight = (SeekBar) v.findViewById(R.id.sb_h);
        mSbWidth = (SeekBar) v.findViewById(R.id.sb_w);
        mSbDensity = (SeekBar) v.findViewById(R.id.sb_density);
        mSbHeight.setProgress(200);
        mSbWidth.setProgress(200);
        mSbDensity.setProgress(1000);
        mColorPicker = (ColorPickerView) v.findViewById(R.id.color_picker);
        mColorPicker.setInitialColor(mColor, false);
        return v;
    }

    @Override
    protected void onApply() {
        float w,h,density;
        w = mSbWidth.getProgress()/MAX;
        h = mSbHeight.getProgress()/MAX;
        density = mSbDensity.getProgress()/MAX*MAX_DENSITY;
        int color = mColorPicker.getSelectedColor();
        mController.setColor(color);
        mController.setSize(w,h);
    }

}
