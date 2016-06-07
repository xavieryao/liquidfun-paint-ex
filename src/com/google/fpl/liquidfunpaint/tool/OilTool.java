
package com.google.fpl.liquidfunpaint.tool;

import com.google.fpl.liquidfun.ParticleColor;
import com.google.fpl.liquidfun.ParticleFlag;
import com.google.fpl.liquidfun.ParticleGroup;
import com.google.fpl.liquidfun.ParticleGroupFlag;

/**
 * Pencil tool
 * These are wall + barrier particles. To prevent leaking, they are all
 * contained in one ParticleGroup. The ParticleGroup can be empty as a result.
 */
public class OilTool extends Tool {
    private static final int ALPHA_DECREMENT = 40; // TODO change appearance and feel
    private static final int ALPHA_THRESHOLD = 10;
    private ParticleGroup mParticleGroup = null;

    public OilTool() {
        super(ToolType.OIL);
        mParticleFlags =
                ParticleFlag.viscousParticle |
                ParticleFlag.tensileParticle |
                ParticleFlag.waterParticle;
        mParticleGroupFlags =
                ParticleGroupFlag.particleGroupCanBeEmpty;
    }

    /**
      * @param pInfo The pointer info containing the previous group info
      */
    @Override
    protected void applyTool(PointerInfo pInfo) {
        // If we have a ParticleGroup saved already, assign it to pInfo.
        // If not, we take the first ParticleGroup created for wall particles,
        // which will be contained in pInfo.
        if (mParticleGroup != null) {
            pInfo.setParticleGroup(mParticleGroup);
        } else if (pInfo.getParticleGroup() != null) {
            mParticleGroup = pInfo.getParticleGroup();
        }

        super.applyTool(pInfo);
    }

    @Override
    protected void reset() {
        mParticleGroup = null;
    }
}
