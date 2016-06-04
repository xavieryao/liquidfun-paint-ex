
package com.google.fpl.liquidfunpaint.tool;

import com.google.fpl.liquidfunpaint.util.Vector2f;
import com.google.fpl.liquidfunpaint.Renderer;
import com.google.fpl.liquidfunpaint.tool.Tool;

import com.google.fpl.liquidfun.World;
import com.google.fpl.liquidfun.Body;
import com.google.fpl.liquidfun.BodyDef;
import com.google.fpl.liquidfun.BodyType;
import com.google.fpl.liquidfun.PolygonShape;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Pencil tool
 * These are wall + barrier particles. To prevent leaking, they are all
 * contained in one ParticleGroup. The ParticleGroup can be empty as a result.
 */
public class BoxTool extends Tool {
    private final static String TAG = "BoxTool";

    private float mH = 0.05f; // height
    private float mW = 0.05f; // width

    public BoxTool() {
        super(ToolType.BOX);
    }

    @Override
    public void onTouch(View v, MotionEvent e) {
        switch (e.getActionMasked()) {
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
          default:
              break;
        }
    }

    public void addPolygon(Vector2f worldPoint) {
        World world = Renderer.getInstance().acquireWorld();
        try {
            Body mBoundaryBody = null;
            BodyDef bodyDef = new BodyDef();
            bodyDef.setType(BodyType.dynamicBody);
            bodyDef.setFixedRotation(false);
            PolygonShape boundaryPolygon = new PolygonShape();
            Log.d(TAG, "create body");
            mBoundaryBody = world.createBody(bodyDef);

            boundaryPolygon.setAsBox(
                    convCordX(mH),
                    convCordX(mW),
                    worldPoint.x,
                    worldPoint.y,
                    0f); // TODO: rotate
            mBoundaryBody.createFixture(boundaryPolygon, 0.0f);
        } finally {
            Renderer.getInstance().releaseWorld();
        }
    }

    private float convCordX(float x) {
        return Renderer.getInstance().sRenderWorldWidth * x;
    }

    private float convCordY(float y) {
        return Renderer.getInstance().sRenderWorldHeight * y;
    }
}
