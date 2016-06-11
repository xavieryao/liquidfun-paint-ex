
package org.papdt.liquidfunpaint.tool;

import org.papdt.liquidfunpaint.util.Vector2f;
import org.papdt.liquidfunpaint.Renderer;
import org.papdt.liquidfunpaint.tool.Tool;

import com.google.fpl.liquidfun.World;
import com.google.fpl.liquidfun.Body;
import com.google.fpl.liquidfun.BodyDef;
import com.google.fpl.liquidfun.BodyType;
import com.google.fpl.liquidfun.Vec2;
import com.google.fpl.liquidfun.FixtureDef;
import com.google.fpl.liquidfun.CircleShape;


import android.util.Log;
import android.view.MotionEvent;
import android.view.View;


public class BallTool extends Tool {
    private final static String TAG = "BallTool";

    // private float mH = 0.05f; // height
    // private float mW = 0.15f; // width
    private float mFriction = 0.5f;
    private float mRestitution = 0.5f;

    public BallTool() {
        super(ToolType.BALL);
    }

    @Override
    public void onTouch(View v, MotionEvent e) {
        switch (e.getActionMasked()) {
          case MotionEvent.ACTION_UP:
          case MotionEvent.ACTION_POINTER_UP:
              float screenX = e.getX();
              float screenY = e.getY();
              // convert point on screen to world point.
              Vector2f worldPoint = new Vector2f(
                      Renderer.getInstance().sRenderWorldWidth
                          * screenX / v.getWidth(),
                      Renderer.getInstance().sRenderWorldHeight *
                          (v.getHeight() - screenY)
                      / v.getHeight());
              addCircle(worldPoint,convSize(mRadius));
              break;
          default:
              break;
        }
    }

    public void addCircle(Vector2f worldPoint, float radius) {
        Log.d(TAG, "addShape, at" + worldPoint + " r:" + radius);
        try {
            World world = Renderer.getInstance().acquireWorld();
            Body body = null;
            BodyDef bodyDef = new BodyDef();
            bodyDef.setType(BodyType.dynamicBody);
            // bodyDef.setFixedRotation(false);
            CircleShape circle = new CircleShape();
            body = world.createBody(bodyDef);
            Vec2 localPoint = body.getLocalPoint(new Vec2(worldPoint.x, worldPoint.y));
            circle.setRadius(radius);
            circle.setPosition(localPoint.getX(), localPoint.getY());
            FixtureDef fd = new FixtureDef();
            fd.setShape(circle);
            fd.setFriction(mFriction);
            fd.setRestitution(mRestitution);
            fd.setDensity(mDensity);
            fd.setColor(mFillColor);
            body.createFixture(fd);

            fd.delete();
            localPoint.delete();
            body.delete();
            circle.delete();
            bodyDef.delete();
        } finally {
            Renderer.getInstance().releaseWorld();
        }
    }

    private float convSize(float size) {
        return Renderer.getInstance().sRenderWorldHeight * size;
    }
}
