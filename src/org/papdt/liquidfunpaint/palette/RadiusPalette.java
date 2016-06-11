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

public class RadiusPalette extends Palette {
    private SeekBar mSbRadius, mSbDensity;
    private ColorPickerView mColorPicker;
    private static final float MAX = 2*1000f;
    private static final float MAX_DENSITY = 20f;

    private int mRadiusValue = 200, mDensityValue = 1000;

    public RadiusPalette(Controller c, int initialColor) {
        super(c,initialColor);
    }

    @Override
    protected View inflateView(LayoutInflater inflater) {
        View v = inflater.inflate(R.layout.radius_palette, null);
        mSbRadius = (SeekBar) v.findViewById(R.id.sb_r);
        mSbDensity = (SeekBar) v.findViewById(R.id.sb_density);
        mSbRadius.setProgress(mRadiusValue);
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
        float r,density;
        mRadiusValue = mSbRadius.getProgress();
        mDensityValue = mSbDensity.getProgress();
        r = mSbRadius.getProgress()/MAX;
        density = mSbDensity.getProgress()/MAX*MAX_DENSITY;
        int color = mColorPicker.getSelectedColor();
        mController.setColor(color);
        mController.setRadius(r);
        mController.setDensity(density);
    }

}
