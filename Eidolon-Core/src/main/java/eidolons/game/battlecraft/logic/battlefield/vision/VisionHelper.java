package eidolons.game.battlecraft.logic.battlefield.vision;

import eidolons.entity.obj.DC_Obj;
import eidolons.game.core.Core;
import eidolons.game.core.game.DC_Game;

public class VisionHelper {

    private static boolean visionHacked;
    private static boolean cinematicVision;

    public static boolean isVisionHacked() {
        if (isCinematicVision())
            return false;
        return visionHacked || DC_Game.game.isDebugMode();
    }

    public static void setVisionHacked(boolean visionHacked) {
        VisionHelper.visionHacked = visionHacked;
    }

    public static boolean checkVisible(DC_Obj obj) {
        return !getMaster().checkInvisible(obj, true);
    }

    public static boolean checkVisible(DC_Obj obj, boolean active) {
        return !getMaster()
         .checkInvisible(obj, active);
    }

    public static void refresh() {
        getMaster().refresh();
    }


    public static VisionMaster getMaster() {
        return Core.game.getVisionMaster();
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

    public static void setCinematicVision(boolean cinematicVision) {
        VisionHelper.cinematicVision = cinematicVision;
    }

    public static boolean isCinematicVision() {
        return cinematicVision;
    }
}
