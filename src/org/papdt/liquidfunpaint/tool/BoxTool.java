
package org.papdt.liquidfunpaint.tool;

import org.papdt.liquidfunpaint.util.Vector2f;
import org.papdt.liquidfunpaint.Renderer;
import org.papdt.liquidfunpaint.tool.Tool;

import com.google.fpl.liquidfun.*;

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
    private float mW = 0.15f; // width
    private float mFriction = 0.5f;
    private float mRestitution = 0.5f;
    private float mDensity = 10.0f;

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
              addPolygon(worldPoint,convSize(mH), convSize(mW));
              break;
          default:
              break;
        }
    }

    public void addPolygon(Vector2f worldPoint, float h, float w) {
        Log.d(TAG, "addPolygon, at" + worldPoint + " h:" + h + "w:" + w);
        try {
            World world = Renderer.getInstance().acquireWorld();
            Body body = null;
            BodyDef bodyDef = new BodyDef();
            bodyDef.setType(BodyType.dynamicBody);
            // bodyDef.setFixedRotation(false);
            PolygonShape boundaryPolygon = new PolygonShape();
            body = world.createBody(bodyDef);
            Vec2 localPoint = body.getLocalPoint(new Vec2(worldPoint.x, worldPoint.y));
            boundaryPolygon.setAsBox(
                    h,
                    w,
                    localPoint.getX(),
                    localPoint.getY(),
                    0f);
            FixtureDef fd = new FixtureDef();
            fd.setShape(boundaryPolygon);
            fd.setFriction(mFriction);
            fd.setRestitution(mRestitution);
            fd.setDensity(mDensity);
            fd.setColor(mFillColor);
            body.createFixture(fd);

            fd.delete();
            localPoint.delete();
            body.delete();
            boundaryPolygon.delete();
            bodyDef.delete();
        } finally {
            Renderer.getInstance().releaseWorld();
        }
    }

    private float convSize(float size) {
        return Renderer.getInstance().sRenderWorldHeight * size;
    }
}
