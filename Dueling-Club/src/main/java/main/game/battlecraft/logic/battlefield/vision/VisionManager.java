package main.game.battlecraft.logic.battlefield.vision;

import main.entity.obj.DC_Obj;
import main.game.battlecraft.rules.RuleMaster;
import main.game.battlecraft.rules.RuleMaster.FEATURE;
import main.game.core.Eidolons;

public class VisionManager {

    private static boolean visionHacked;
    private static VisionMaster master;


    public static boolean isVisionHacked() {


        return visionHacked;
    }

    public static void setVisionHacked(boolean visionHacked) {
        VisionManager.visionHacked = visionHacked;
    }

    public static boolean checkVisible(DC_Obj obj) {
        return master.checkVisible(obj);
    }

    public static boolean checkVisible(DC_Obj obj, boolean active) {
        return master.checkVisible(obj, active);
    }

    public static void refresh() {
        master.refresh();
    }


    public static VisionMaster getMaster() {
        if (master == null) {
            master = new VisionMaster(Eidolons.game);
        }
        return master;
    }

    public static boolean checkDetected(DC_Obj dc_obj) {
        return master.getDetectionMaster().checkDetected(dc_obj);
    }

}
