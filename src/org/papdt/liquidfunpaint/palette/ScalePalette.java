package org.papdt.liquidfunpaint.palette;

import android.view.LayoutInflater;
import com.flask.colorpicker.OnColorSelectedListener;

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

    private int mWidthValue = 200, mHeightValue = 200, mDensityValue = 1000;

    public ScalePalette(Controller c, int initialColor) {
        super(c,initialColor);
    }

    @Override
    protected View inflateView(LayoutInflater inflater) {
        View v = inflater.inflate(R.layout.scale_palette, null);
        mSbHeight = (SeekBar) v.findViewById(R.id.sb_h);
        mSbWidth = (SeekBar) v.findViewById(R.id.sb_w);
        mSbDensity = (SeekBar) v.findViewById(R.id.sb_density);
        mSbHeight.setProgress(mHeightValue);
        mSbWidth.setProgress(mWidthValue);
        mSbDensity.setProgress(mDensityValue);
        mColorPicker = (ColorPickerView) v.findViewById(R.id.color_picker);
        mColorPicker.setInitialColor(mColor, false);
        mColorPicker.addOnColorSelectedListener(new OnColorSelectedListener(){
            @Override
            public void onColorSelected(int color) {
              mColor = color;
            }
        });
        return v;
    }

    @Override
    protected void onApply() {
        float w,h,density;
        mWidthValue = mSbWidth.getProgress();
        mHeightValue = mSbHeight.getProgress();
        mDensityValue = mSbDensity.getProgress();
        w = mSbWidth.getProgress()/MAX;
        h = mSbHeight.getProgress()/MAX;
        density = mSbDensity.getProgress()/MAX*MAX_DENSITY;
        int color = mColorPicker.getSelectedColor();
        mController.setColor(color);
        mController.setSize(w,h);
        mController.setDensity(density);
    }

}
