package eidolons.game.module.dungeoncrawl.generator.test;

import eidolons.system.options.Options.OPTION;

/**
 * Created by JustMe on 8/2/2018.
 */
public class LevelStats {

    public enum LEVEL_GEN_FLAG implements OPTION {
        isRandomRotation,
        isMergeLinksAllowed,
        isBuildFromExitAllowed,
        isRandomizedSizeSort,
        isAdjustEvenRoomX,
        isAdjustEvenRoomY,
        //        isAltExitsAllowed,
        isShearDisplacedOnly,
        isJoinAllowed, isShearLinkWallsAllowed,;
        private final Boolean defaultValue;

        LEVEL_GEN_FLAG() {
            this(false);

        }

        LEVEL_GEN_FLAG(Boolean exclusive) {
            defaultValue = exclusive;
        }

        @Override
        public Integer getMin() {
            return null;
        }

        @Override
        public Integer getMax() {
            return null;
        }

        @Override
        public Object getDefaultValue() {
            return defaultValue;
        }

        @Override
        public Boolean isExclusive() {
            return defaultValue;
        }

        @Override
        public Object[] getOptions() {
            return new Object[0];
        }

    }

    public enum LEVEL_STAT {
        ROOMS,
        FILL_PERCENTAGE,
        GRAPH_ADHERENCE,

    }
}
