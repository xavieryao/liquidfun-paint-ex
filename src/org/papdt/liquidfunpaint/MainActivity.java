/**
* Copyright (c) 2014 Google, Inc. All rights reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
*/

package org.papdt.liquidfunpaint;

import org.papdt.liquidfunpaint.tool.Tool;
import org.papdt.liquidfunpaint.tool.Tool.ToolType;
import org.papdt.liquidfunpaint.palette.ColorPalette;
import org.papdt.liquidfunpaint.palette.ScalePalette;
import org.papdt.liquidfunpaint.palette.RadiusPalette;
import org.papdt.liquidfunpaint.palette.Palette;

import com.google.fpl.liquidfun.World;
import com.google.fpl.liquidfun.Body;
import com.google.fpl.liquidfun.Fixture;
import com.google.fpl.liquidfun.Shape;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.EnumMap;

/**
 * MainActivity for Splashpaint
 * Implements the Android UI layout.
 */
public class MainActivity extends Activity implements OnTouchListener {
    private static String TAG = "MainActivity";

    static String sVersionName;
    private Controller mController;
    private GLSurfaceView mWorldView;

    private RelativeLayout mRootLayout;

    // The image view of the selected tool
    private ImageView mSelected;
    // The current open palette
    private Palette mOpenPalette = null;

    private boolean mUsingTool = false;
    private static final int ANIMATION_DURATION = 300;

    private Palette mPencilPalette;
    private Palette mRigidPalette;
    private Palette mOilPalette;
    private Palette mWaterPalette;
    private Palette mBoxPalette;
    private Palette mBallPalette;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Explicitly load all shared libraries for Android 4.1 (Jelly Bean)
        // Or we could get a crash from dependencies.
        System.loadLibrary("liquidfun");
        System.loadLibrary("liquidfun_jni");


        // Set the ToolBar layout
        // requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.tools_layout);
        mRootLayout = (RelativeLayout) findViewById(R.id.root);

        // Set the restart button's listener
        findViewById(R.id.button_restart).setOnTouchListener(this);

        Renderer renderer = Renderer.getInstance();
        Renderer.getInstance().init(this);
        mController = new Controller(this);

        // Set up the OpenGL WorldView
        mWorldView = (GLSurfaceView) findViewById(R.id.world);
        mWorldView.setEGLContextClientVersion(2);
        mWorldView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        mWorldView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        if (BuildConfig.DEBUG) {
            mWorldView.setDebugFlags(
                    GLSurfaceView.DEBUG_LOG_GL_CALLS |
                    GLSurfaceView.DEBUG_CHECK_GL_ERROR);
        }
        mWorldView.setOnTouchListener(this);
        // GLSurfaceView#setPreserveEGLContextOnPause() is added in API level 11
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setPreserveEGLContextOnPause();
        }

        mWorldView.setRenderer(renderer);
        renderer.startSimulation();

        // Set default tool colors
        Tool.getTool(ToolType.PENCIL).setColor(
                getABGRColor(getString(R.string.default_pencil_color), "color"));
        Tool.getTool(ToolType.RIGID).setColor(
                getABGRColor(getString(R.string.default_rigid_color), "color"));
        Tool.getTool(ToolType.WATER).setColor(
                getABGRColor(getString(R.string.default_water_color), "color"));
        Tool.getTool(ToolType.OIL).setColor(
                getABGRColor(getString(R.string.default_oil_color), "color"));
        Tool.getTool(ToolType.BOX).setColor(
                getABGRColor(getString(R.string.default_oil_color), "color"));
        Tool.getTool(ToolType.BALL).setColor(
                getABGRColor(getString(R.string.default_oil_color), "color"));

        initPalette();

        // Initialize the first selected tool
        mSelected = (ImageView) findViewById(R.id.water);

        // Show the title view for 3 seconds
        LayoutInflater inflater = getLayoutInflater();
        inflater.inflate(R.layout.title, mRootLayout);
        final View title = findViewById(R.id.title);
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setDuration(500);
        fadeOut.setStartOffset(3000);
        fadeOut.setAnimationListener(new AnimationListener() {
                @Override
            public void onAnimationStart(Animation animation) {
            }

                @Override
            public void onAnimationRepeat(Animation animation) {
            }

                @Override
            public void onAnimationEnd(Animation animation) {
                title.setVisibility(View.GONE);
            }
        });
        title.setVisibility(View.VISIBLE);
        title.startAnimation(fadeOut);

        if (BuildConfig.DEBUG) {
            View fps = findViewById(R.id.fps);
            fps.setVisibility(View.VISIBLE);
            TextView versionView = (TextView) findViewById(R.id.version);
            try {
                sVersionName = "Version "
                        + getPackageManager()
                            .getPackageInfo(getPackageName(), 0).versionName;
                versionView.setText(sVersionName);
            } catch (NameNotFoundException e) {
                // The name returned by getPackageName() must be found.
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setPreserveEGLContextOnPause() {
        mWorldView.setPreserveEGLContextOnPause(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mController.onResume();
        mWorldView.onResume();
        Renderer.getInstance().totalFrames = -10000;

    }

    @Override
    protected void onPause() {
        super.onPause();
        mController.onPause();
        mWorldView.onPause();
    }

    private void initPalette() {
        mPencilPalette = new ColorPalette(mController,getColor(getString(R.string.default_pencil_color), "color"));
        mRigidPalette = new ColorPalette(mController,getColor(getString(R.string.default_rigid_color), "color"));
        mWaterPalette = new ColorPalette(mController,getColor(getString(R.string.default_water_color), "color"));
        mOilPalette = new ColorPalette(mController,getColor(getString(R.string.default_oil_color), "color"));
        mBoxPalette = new ScalePalette(mController,getColor(getString(R.string.default_oil_color), "color"));
        mBallPalette = new RadiusPalette(mController,getColor(getString(R.string.default_oil_color), "color"));
    }

    private void togglePalette(View selectedTool, Palette palette) {
        // Save the previously opened palette as closePalette() will clear it.
        Palette prevOpenPalette = mOpenPalette;

        // Always close the palette.
        closePalette();

        // If we are not selecting the same tool with an open color palette,
        // open it.
        if (!(selectedTool.getId() == mSelected.getId() &&
              prevOpenPalette != null)) {
            openPalette(palette);
        }
    }

    private void openPalette(Palette palette) {
        palette.openPalette(getFragmentManager());
        mOpenPalette = palette;
    }

    private void closePalette() {
        if (mOpenPalette != null)
            mOpenPalette.closePalette();
        mOpenPalette = null;
    }

    private void select(View v, ToolType tool) {
        // Send the new tool over to the Controller
        mController.setTool(tool);
        // Keep track of the ImageView of the tool and highlight it
        mSelected = (ImageView) v;
        View selecting = findViewById(R.id.selecting);
        RelativeLayout.LayoutParams params =
                new RelativeLayout.LayoutParams(selecting.getLayoutParams());

        params.addRule(RelativeLayout.ALIGN_TOP, v.getId());
        selecting.setLayoutParams(params);
        selecting.setVisibility(View.VISIBLE);
    }

    private int getABGRColor(String name, String defType) {
        Resources r = getResources();
        int id = r.getIdentifier(name, defType, getPackageName());
        int color = r.getColor(id);
        // ARGB to ABGR
        int red = (color >> 16) & 0xFF;
        int blue = (color << 16) & 0xFF0000;
        return (color & 0xFF00FF00) | red | blue;
    }

    private int getColor(String name, String defType) {
        Resources r = getResources();
        int id = r.getIdentifier(name, defType, getPackageName());
        return r.getColor(id);
    }

    /**
     * OnTouch event handler.
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        boolean retValue = false;
        switch (v.getId()) {
            case R.id.button_restart:
                doBadThings();
                retValue = onTouchReset(v, event);
                break;
            case R.id.world:
                retValue = onTouchCanvas(v, event);
                break;
            default:
                break;
        }
        return retValue;
    }

    /**
     * OnTouch handler for OpenGL canvas.
     * Called from OnTouchListener event.
     */
    public boolean onTouchCanvas(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mUsingTool = true;
                if (mSelected.getId() == R.id.rigid) {
                    Renderer.getInstance().pauseSimulation();
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mUsingTool = false;
                if (mSelected.getId() == R.id.rigid) {
                    Renderer.getInstance().startSimulation();
                }
                break;
            default:
                break;
        }

        closePalette();
        return mController.onTouch(v, event);
    }

    /**
     * OnTouch handler for reset button.
     * Called from OnTouchListener event.
     */
    public boolean onTouchReset(View v, MotionEvent event) {

        if (!mUsingTool) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    closePalette();
                    select(v, null);
                    break;
                case MotionEvent.ACTION_UP:
                    Renderer.getInstance().reset();
                    mController.reset();
                    // Could refactor out to a deselect() function, but this is
                    // the only place that needs it now.
                    View selecting = findViewById(R.id.selecting);
                    selecting.setVisibility(View.INVISIBLE);
                    break;
                default:
                    break;
            }
        }

        return true;
    }

    /**
     * OnClick method for debug view.
     * Called from XML layout.
     */
    public void onClickDebug(View v) {
    }

    /**
     * OnClick method for the color palette.
     * Called from XML layout.
     */
    public void onClickPalette(View v) {
        if (mUsingTool) {
            return;
        }
        // int color = mColorMap.get(v.getId());
        // mController.setColor(color);
        // Close the palette on choosing a color
        closePalette();
    }

    /**
     * OnClick method for tools.
     * Called from XML layout.
     */
    public void onClickTool(View v) {
        if (mUsingTool) {
            return;
        }

        ToolType tool = null;

        switch (v.getId()) {
            case R.id.pencil:
                tool = ToolType.PENCIL;
                togglePalette(v, mPencilPalette);
                break;
            case R.id.rigid:
                tool = ToolType.RIGID;
                togglePalette(v, mRigidPalette);
                break;
            case R.id.water:
                tool = ToolType.WATER;
                togglePalette(v, mWaterPalette);
                break;
            case R.id.oil:
                tool = ToolType.OIL;
                togglePalette(v, mOilPalette);
                break;
            case R.id.eraser:
                tool = ToolType.ERASER;
                // Always close palettes for non-drawing tools
                break;
            case R.id.hand:
                tool = ToolType.MOVE;
                // Always close palettes for non-drawing tools
                break;
            case R.id.box:
                tool = ToolType.BOX;
                togglePalette(v, mBoxPalette);
                break;
            case R.id.ball:
                tool = ToolType.BALL;
                togglePalette(v, mBallPalette);
                break;
            case R.id.force:
                tool = ToolType.FORCE;
                break;
            default:
        }

        // Actually select the view
        select(v, tool);
    }

    private void doBadThings() {

        World w = Renderer.getInstance().acquireWorld();
        try {
            List<Fixture> fixtures = new ArrayList<>();
            List<Body> bodies = new ArrayList<>();
            List<Shape> shapes = new ArrayList<>();
            Body body = w.getBodyList();
            while(body != null) {
                bodies.add(body);
                body = body.getNext();
            }
            Log.d(TAG, "Bodies:" + bodies.size());
            for (Body b : bodies) {
                Fixture f = b.getFixtureList();
                while (f != null) {
                    shapes.add(f.getShape());
                    Log.d(TAG, "shape with type:" + f.getShape().getType());
                    f = f.getNext();
                }
            }
            Log.d(TAG, "Shapes:" + shapes.size());
        } finally {
            Renderer.getInstance().releaseWorld();
        }
    }
}
