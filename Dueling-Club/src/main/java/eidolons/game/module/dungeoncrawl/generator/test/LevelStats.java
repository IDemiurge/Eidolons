package eidolons.game.module.dungeoncrawl.generator.test;

/**
 * Created by JustMe on 8/2/2018.
 */
public class LevelStats {

    public enum LEVEL_GEN_FLAG {
        isRandomRotation,
        isMergeLinksAllowed,
        isBuildFromExitAllowed,
        isRandomizedSizeSort,
        isAdjustEvenRoomX,
        isAdjustEvenRoomY,
//        isAltExitsAllowed,
        isShearDisplacedOnly,
        isJoinAllowed,

        }

        public enum LEVEL_STAT{
ROOMS,
            FILL_PERCENTAGE,
            GRAPH_ADHERENCE,

    }
}
