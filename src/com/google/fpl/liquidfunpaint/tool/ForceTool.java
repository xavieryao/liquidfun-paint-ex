
package com.google.fpl.liquidfunpaint.tool;

import com.google.fpl.liquidfunpaint.util.Vector2f;
import com.google.fpl.liquidfunpaint.Renderer;
import com.google.fpl.liquidfunpaint.tool.Tool;
import com.google.fpl.liquidfunpaint.util.LogF;

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

import java.util.LinkedList;

/**
 * Pencil tool
 * These are wall + barrier particles. To prevent leaking, they are all
 * contained in one ParticleGroup. The ParticleGroup can be empty as a result.
 */
public class ForceTool extends Tool {
    private final static String TAG = "ForceTool";
    private final static float FACTOR = 10000f;

    private Body mTouchedBody;
    private Vec2 mApplyPoint;

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
            mTouchedBody = testPoint(x,y);
            boolean overlap = (mTouchedBody!=null);
            if (overlap) {
                mApplyPoint = new Vec2(x,y);
            }
            Log.d(TAG,"overlap:" + overlap);
            break;
        case MotionEvent.ACTION_MOVE:
            handleMoveEvent(v,e);
            break;
        case MotionEvent.ACTION_POINTER_UP:
        case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_CANCEL:
            if (mTouchedBody!=null) {
                mTouchedBody.delete();
                mTouchedBody = null;
            }
            if (mApplyPoint!=null) {
                mApplyPoint.delete();
                mApplyPoint = null;
            }
            break;
        default:
              break;
        }
    }

    private void handleMoveEvent(View v, MotionEvent ev) {
        final int historySize = ev.getHistorySize();
        final int pointerCount = ev.getPointerCount();
        if (historySize == 0) {
            return;
        }
        if (historySize == 1) {
            float prevX = ev.getHistoricalX(0,0);
            float prevY = ev.getHistoricalY(0,0);
            long prevTime = ev.getHistoricalEventTime(0);
            float curX = ev.getX(0);
            float curY = ev.getY(0);
            long curTime = ev.getEventTime();
            float dx = convCordX(curX - prevX, v);
            float dy = -convCordY(curY - prevY, v);
            float dt = curTime - prevTime;
            Vec2 force = new Vec2(dx/dt*FACTOR, dy/dt*FACTOR);
            LogF.d(TAG, "apply force:[%f, %f]", force.getX(), force.getY());
            if (mTouchedBody != null) {
                mTouchedBody.applyForce(force,mTouchedBody.getWorldPoint(mApplyPoint),true);
            }
            force.delete();
        } else {
            float prevX = ev.getHistoricalX(0, 0);
            float prevY = ev.getHistoricalY(0, 0);
            long prevTime = ev.getHistoricalEventTime(0);
            for (int h = 1; h < historySize; h++) {
                float curX = ev.getHistoricalX(0, h);
                float curY = ev.getHistoricalY(0, h);
                long curTime = ev.getHistoricalEventTime(h);
                float dx = convCordX(curX - prevX, v);
                float dy = -convCordY(curY - prevY, v);
                float dt = curTime - prevTime;
                Vec2 force = new Vec2(dx/dt*FACTOR, dy/dt*FACTOR);
                LogF.d(TAG, "apply force:[%f, %f]", force.getX(), force.getY());
                if (mTouchedBody != null) {
                    mTouchedBody.applyForce(force,mTouchedBody.getWorldPoint(mApplyPoint), true);
                }
                prevX = curX;
                prevY = curY;
                prevTime = curTime;
                force.delete();
            }
        }
    }

    private Body testPoint(float x, float y) {
        Log.d(TAG, "test " + x + ", " + y);
        try {
            World w = Renderer.getInstance().acquireWorld();
            Vec2 vec = new Vec2(x,y);
            Body body = w.getBodyList();
            Transform t;
            while(body != null) {
                t = body.getTransform();
                Fixture f = body.getFixtureList();
                while (f != null) {
                    Shape s = f.getShape();
                    if (s.testPoint(t,vec)) {
                        s.delete();
                        // body.setAngularVelocity(3f);
                        return body;
                    }
                    Fixture next = f.getNext();
                    s.delete();
                    f.delete();
                    f = next;
                }
                Body next = body.getNext();
                body.delete();
                body = next;
                t.delete();
            }
        } finally {
            Renderer.getInstance().releaseWorld();
        }
        return null;
    }
}
