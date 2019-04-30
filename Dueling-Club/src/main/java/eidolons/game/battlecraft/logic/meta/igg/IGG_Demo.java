package eidolons.game.battlecraft.logic.meta.igg;

import eidolons.content.PARAMS;
import main.content.values.parameters.PARAMETER;
import main.system.auxiliary.StringMaster;

public class IGG_Demo {

    public static final String HERO_GORR = "Gorr IGG";
    public static final String HERO_DARK_ELF = "Gwyn IGG";
    public static final PARAMETER[] HERO_NEW_MASTERY_DARK_ELF = {
            PARAMS.ATHLETICS_MASTERY,
            PARAMS.TWO_HANDED_MASTERY,
            PARAMS.MARKSMANSHIP_MASTERY,
            PARAMS.POLEARM_MASTERY,
            PARAMS.AXE_MASTERY,
            PARAMS.BLUNT_MASTERY,
            PARAMS.ARMORER_MASTERY,
            PARAMS.MEDITATION_MASTERY,
    };
    public static final PARAMETER[] HERO_NEW_MASTERY_GORR = {
            PARAMS.TWO_HANDED_MASTERY,
            PARAMS.DUAL_WIELDING_MASTERY,
            PARAMS.POLEARM_MASTERY,
            PARAMS.BLADE_MASTERY,
            PARAMS.MARKSMANSHIP_MASTERY,
            PARAMS.ARMORER_MASTERY,
            PARAMS.DEFENSE_MASTERY,
            PARAMS.MOBILITY_MASTERY,
            PARAMS.DISCIPLINE_MASTERY,
    };

    public static IGG_MISSION getMissionByName(String missionName) {
        return IGG_MISSION.valueOf(StringMaster.getEnumFormat(missionName));
        //better store in this form, what if real name changes in updates!
    }

    public enum IGG_MISSION{
        ACT_I_MISSION_I("Gates of Nyrn", 1,1),
        ACT_I_MISSION_II("Fel Tunnels", 1,2),
        ACT_I_MISSION_III("Blackfathom", 1,3),
        ;
        String missionName;
        int act;
        int missionIndex;

        IGG_MISSION(String missionName, int act, int mission) {
            this.missionName = missionName;
            this.act = act;
            this.missionIndex = mission;
        }

        public int getAct() {
            return act;
        }

        public int getMissionIndex() {
            return missionIndex;
        }

        public String getMissionName() {
            return missionName;
        }
    }
}
