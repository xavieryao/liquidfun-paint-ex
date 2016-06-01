package com.google.fpl.liquidfunpaint.init;

import com.google.fpl.liquidfunpaint.Renderer;
import com.google.fpl.liquidfunpaint.util.Vector2f;
import com.google.fpl.liquidfunpaint.init.LineBuffer;

import com.google.fpl.liquidfun.Vec2;
import com.google.fpl.liquidfun.ParticleSystem;
import com.google.fpl.liquidfun.ParticleGroup;
import com.google.fpl.liquidfun.ParticleGroupDef;
import com.google.fpl.liquidfun.ParticleFlag;
import com.google.fpl.liquidfun.ParticleGroupFlag;
import com.google.fpl.liquidfun.ParticleColor;

import java.nio.ByteBuffer;

import android.util.Log;

public class Boundary {
    private static final String TAG = "Boundry";

    private static final float RADIUS = 0.05f;
    private static final int PARTICLE_FLAGS = ParticleFlag.wallParticle |
            ParticleFlag.barrierParticle |
            ParticleFlag.repulsiveParticle;
    private static final int PARTICLE_GROUP_FLAGS = ParticleGroupFlag.rigidParticleGroup | ParticleGroupFlag.solidParticleGroup;

    private ParticleGroup mGroup;
    private ParticleColor mColor = new ParticleColor();
    private Vec2 mVelocity = new Vec2(0, 0);

    public void drawLine(float fromX, float fromY, float toX, float toY) {
        Log.d(TAG, "drawLine, from" + fromX + "," + fromY + "to: " + toX + "," + toY);
        Vector2f startPoint = getWorldPoint(fromX, fromY);
        Vector2f endPoint = getWorldPoint(toX, toY);

        // clampToWorld(startPoint, RADIUS);
        // clampToWorld(endPoint, RADIUS);

        LineBuffer lineBuffer = new LineBuffer();

        int pointCount = (int) (Vector2f.length(startPoint, endPoint)/RADIUS);
        for (int i = 0; i < pointCount; i++) {
            Vector2f incr = Vector2f.lerpFixedInterval(startPoint, endPoint, i, pointCount);
            lineBuffer.putPoint(incr);
            if (lineBuffer.needsFlush()) {
                sprayParticles(lineBuffer);
                lineBuffer.reset();
            }
        }
        sprayParticles(lineBuffer);
        lineBuffer.reset();
    }

    private void sprayParticles(LineBuffer lBuffer) {
        Log.d(TAG, "sprayParticles");
        ByteBuffer buffer = lBuffer.getRawPointsBuffer();

        ParticleGroupDef pgd = new ParticleGroupDef();
        pgd.setFlags(PARTICLE_FLAGS);
        pgd.setGroupFlags(PARTICLE_GROUP_FLAGS);
        pgd.setLinearVelocity(mVelocity);
        pgd.setColor(mColor);
        buffer.position(lBuffer.getBufferStart());
        pgd.setCircleShapesFromVertexList(
            buffer.slice(), lBuffer.getNumPoints(), RADIUS);

            ParticleSystem ps = Renderer.getInstance().acquireParticleSystem();
        try {
            // Create ParticleGroup
            // Join to existing group if the group has the same flags
            ParticleGroup pGroup = ps.createParticleGroup(pgd);
            if ((mGroup == null) ||
                (mGroup.getGroupFlags() != pgd.getGroupFlags())) {
                mGroup = pGroup;
            } else {
                ps.joinParticleGroups(mGroup, pGroup);
            }

            // Clean up native objects
            pgd.delete();
        } finally {
            Renderer.getInstance().releaseParticleSystem();
        }
    }

    private float convCordX(float x) {
        return Renderer.getInstance().sRenderWorldWidth * x;
    }

    private float convCordY(float y) {
        return Renderer.getInstance().sRenderWorldHeight * y;
    }

    private Vector2f getWorldPoint(float x, float y) {
        return new Vector2f(convCordX(x), convCordY(y));
    }

    protected void clampToWorld(Vector2f worldPoint, float border) {
        worldPoint.x = Math.max(border,
                Math.min(
                    worldPoint.x,
                    Renderer.getInstance().sRenderWorldWidth - border));
        worldPoint.y = Math.max(border,
                Math.min(
                    worldPoint.y,
                    Renderer.getInstance().sRenderWorldHeight - border));
    }

    public void setColor (int color) {
        // Convert ABGR back into ParticleColor
        // Box2D doesn't have this functionality,
        // check why color is stored as an int to begin with.
        short a = (short) (color >> 24 & 0xFF);
        short b = (short) (color >> 16 & 0xFF);
        short g = (short) (color >> 8 & 0xFF);
        short r = (short) (color & 0xFF);
        mColor.set(r, g, b, a);
    }

    @Override
    protected void finalize() {
        // clean up native variables
        mColor.delete();
        mVelocity.delete();
    }
}
