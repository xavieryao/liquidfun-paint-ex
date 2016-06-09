package org.papdt.liquidfunpaint;

import com.google.fpl.liquidfun.World;
import com.google.fpl.liquidfun.Fixture;
import com.google.fpl.liquidfun.Body;
import com.google.fpl.liquidfun.Transform;
import com.google.fpl.liquidfun.Color;


public class ExtendedRendererHelper {
    private static ExtendedRendererHelper INSTANCE = null;

    private ExtendedRendererHelper() {
        // left empty
    }

    public static ExtendedRendererHelper getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ExtendedRendererHelper();
        }
        return INSTANCE;
    }

    public void drawIntoBuffer() {
        World world = Renderer.getInstance().acquireWorld();
        try {
            Body body = world.getBodyList();
            while (body != null) {
                Fixture fixture = body.getFixtureList();
                Transform transform = body.getTransform();
                while (fixture != null) {
                    int color = fixture.getColor();
                    short b = (short) (color >> 16 & 0xFF);
                    short g = (short) (color >> 8 & 0xFF);
                    short r = (short) (color & 0xFF);
                    Color c = new Color(r,g,b);
                    world.drawShape(fixture, transform, c);
                    // do things
                    Fixture nextFixture = fixture.getNext();
                    fixture.delete();
                    fixture = nextFixture;
                }
                // perhaps delete c?
                transform.delete();
                Body nextBody = body.getNext();
                body.delete();
                body = nextBody;
            }
        } finally {
            Renderer.getInstance().releaseWorld();
        }
    }
}
