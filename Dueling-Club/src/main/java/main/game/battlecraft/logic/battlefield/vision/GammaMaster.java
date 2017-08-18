package main.game.battlecraft.logic.battlefield.vision;

import main.entity.obj.DC_Obj;
import main.entity.obj.unit.Unit;
import main.game.bf.Coordinates;
import main.game.core.Eidolons;
import main.system.math.MathMaster;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by JustMe on 2/22/2017.
 */
public class GammaMaster {

    private static final Float CELL_GAMMA_MODIFIER = 5F;
    private static final Float UNIT_GAMMA_MODIFIER = 5F;
    public static final boolean DEBUG_MODE =true ;
    private VisionMaster master;
    private Map<DC_Obj, Integer> cache = new HashMap<>();

    public GammaMaster(VisionMaster visionManager) {
        master = visionManager;
    }

    public static int getGammaForThickDarkness() {
        return 15;
    }

    public static int getGammaForBlindingLight() {
        return 300;
    }


    public int getGamma(boolean minusForVagueLight, Unit source, DC_Obj target) {
        if (source == null) {
            source = target.getGame().getManager().getActiveObj();
        }
        Integer illumination = master.getIlluminationMaster().getIllumination(source, target);
        Integer concealment = master.getIlluminationMaster().getConcealment(source, target);


        Integer gamma = illumination - concealment;
        cache.put(target, gamma);

        if (source == target.getGame().getManager().getActiveObj()) {
            target.setGamma(gamma);
        }

//        if (i > 50 && c > 50) {
//            return Integer.MIN_VALUE; TODO twilight
//        }
        return gamma;
    }


    public void clearCache() {
        cache.clear();
    }

    public float getGammaForCell(int x, int y) {
        Unit unit= Eidolons.game.getManager().getMainHero();
        if (unit == null) {
          unit= Eidolons.game.getManager().getActiveObj();
        }
        return MathMaster.minMax(CELL_GAMMA_MODIFIER * (float)
         getGamma(false,unit,
         Eidolons.game.getCellByCoordinate(new Coordinates(x, y))) / 100, 0, 1);
//        return new Random().nextInt(50)/100 + 0.5f;
    }
}
