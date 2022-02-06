package main.level_editor.backend.sim;

import eidolons.content.PARAMS;
import eidolons.entity.obj.GridCell;
import eidolons.entity.obj.DC_Obj;
import eidolons.entity.unit.Unit;
import eidolons.game.battlecraft.logic.battlefield.vision.GammaMaster;
import eidolons.game.battlecraft.logic.battlefield.vision.VisionMaster;
import main.game.bf.Coordinates;

import static main.system.auxiliary.log.LogMaster.log;

public class LE_VisionMaster extends VisionMaster {
    public LE_VisionMaster(LE_GameSim sim) {
        super(sim);

        gammaMaster = new GammaMaster(this) {
            @Override
            public int getGamma(Unit source, DC_Obj target) {
                if (target instanceof GridCell) {
                    return (int) getGammaForCell((GridCell) target);
                }
                return super.getGamma(source, target);
            }

            @Override
            public float getGammaForCell(GridCell cell) {
                return cell.getIntParam(PARAMS.ILLUMINATION);
            }

            @Override
            protected float getLightAlpha(float gamma, GridCell cell, Coordinates observer) {
                if (gamma <= 0)
                    return 0;
                float alpha = (float) Math.min(Math.sqrt(gamma/120), gamma / 400);
                alpha = Math.min(alpha, 0.33f);
                if (observer.getX() == cell.getX() && observer.getY() == cell.getY()) {
                    alpha = alpha / 3 * 2;
                }
                log(1, cell + " has " + alpha + " alpha with" +
                        gamma + " gamma ");
                return alpha;
            }
        };
    }

}
