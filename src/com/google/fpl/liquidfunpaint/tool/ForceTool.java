
package com.google.fpl.liquidfunpaint.tool;

import com.google.fpl.liquidfunpaint.util.Vector2f;
import com.google.fpl.liquidfunpaint.Renderer;
import com.google.fpl.liquidfunpaint.tool.Tool;

import com.google.fpl.liquidfun.World;
import com.google.fpl.liquidfun.Body;
import com.google.fpl.liquidfun.BodyDef;
import com.google.fpl.liquidfun.BodyType;
import com.google.fpl.liquidfun.PolygonShape;
import com.google.fpl.liquidfun.QueryCallback;
import com.google.fpl.liquidfun.Fixture;
import com.google.fpl.liquidfun.Shape;
import com.google.fpl.liquidfun.Transform;
import com.google.fpl.liquidfun.Vec2;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Pencil tool
 * These are wall + barrier particles. To prevent leaking, they are all
 * contained in one ParticleGroup. The ParticleGroup can be empty as a result.
 */
public class ForceTool extends Tool {
    private final static String TAG = "ForceTool";

    private float mH = 0.05f; // height
    private float mW = 0.05f; // width

    public ForceTool() {
        super(ToolType.FORCE);
    }

    @Override
    public void onTouch(View v, MotionEvent e) {
        switch (e.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                float screenX = e.getX();
                float screenY = e.getY();
                float x = Renderer.getInstance().sRenderWorldWidth * screenX / v.getWidth();
                float y = Renderer.getInstance().sRenderWorldHeight * (v.getHeight() - screenY) / v.getHeight();
                boolean overlap = testPoint(x,y);
                Log.d(TAG,"overlap:" + overlap);
                break;
            /*
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                Log.d(TAG, "onUp");
                float screenX = e.getX();
                float screenY = e.getY();
                // convert point on screen to world point.
                Vector2f worldPoint = new Vector2f(
                    Renderer.getInstance().sRenderWorldWidth
                        * screenX / v.getWidth(),
                    Renderer.getInstance().sRenderWorldHeight *
                        (v.getHeight() - screenY)
                        / v.getHeight());
                    addPolygon(worldPoint);
                break;
            */
          default:
              break;
        }
    }

    private float convCordX(float x) {
        return Renderer.getInstance().sRenderWorldWidth * x;
    }

    private float convCordY(float y) {
        return Renderer.getInstance().sRenderWorldHeight * y;
    }

    private boolean testPoint(float x, float y) {
        Log.d(TAG, "test " + x + ", " + y);
        try {
            World w = Renderer.getInstance().acquireWorld();
            Vec2 vec = new Vec2(x,y);
            Body body = w.getBodyList();
            Transform t = new Transform();
            t.setIdentity();
            while(body != null) {
                Fixture f = body.getFixtureList();
                while (f != null) {
                    Shape s = f.getShape();
                    if (s.testPoint(t,vec)) {
                        return true;
                    }
                    f = f.getNext();
                }
                body = body.getNext();
            }
        } finally {
            Renderer.getInstance().releaseWorld();
        }
        return false;
    }
}
