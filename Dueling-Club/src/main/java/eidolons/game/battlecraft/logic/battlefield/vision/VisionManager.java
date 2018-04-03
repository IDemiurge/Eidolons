package eidolons.game.battlecraft.logic.battlefield.vision;

import eidolons.entity.obj.DC_Obj;
import eidolons.game.core.Eidolons;
import eidolons.game.core.game.DC_Game;

public class VisionManager {

    private static boolean visionHacked;

    public static boolean isVisionHacked() {
        return visionHacked || DC_Game.game.isDebugMode();
    }

    public static void setVisionHacked(boolean visionHacked) {
        VisionManager.visionHacked = visionHacked;
    }

    public static boolean checkVisible(DC_Obj obj) {
        return getMaster().checkVisible(obj, true);
    }

    public static boolean checkVisible(DC_Obj obj, boolean active) {
        return getMaster().checkVisible(obj, active);
    }

    public static void refresh() {
        getMaster().refresh();
    }


    public static VisionMaster getMaster() {
        return Eidolons.game.getVisionMaster();
    }

    public static boolean checkDetected(DC_Obj dc_obj) {
        return checkDetected(dc_obj, false);
    }

    public static boolean checkDetected(DC_Obj dc_obj, boolean enemy) {
        return getMaster().getDetectionMaster().checkDetected(dc_obj, enemy);
    }

    public static boolean checkKnown(DC_Obj dc_obj) {
        return getMaster().getDetectionMaster().checkKnown(dc_obj);
    }

    public static boolean checkKnownForPlayer(DC_Obj dc_obj) {
        return getMaster().getDetectionMaster().checkKnownForPlayer(dc_obj);
    }
}
