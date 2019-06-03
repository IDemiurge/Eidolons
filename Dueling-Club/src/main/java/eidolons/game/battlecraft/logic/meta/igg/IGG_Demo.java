package eidolons.game.battlecraft.logic.meta.igg;

import com.badlogic.gdx.graphics.Color;
import eidolons.content.PARAMS;
import main.content.values.parameters.PARAMETER;
import main.data.xml.XML_Formatter;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IGG_Demo {

    public static final String HERO_GORR = "Gorr Eddar";
    public static final String HERO_DARK_ELF = "Gwynn";
    public static final String HERO_RAINA = "Raina Ardren";
    public static final String HERO_GRIMBART = "Grimbart";
    public static final PARAMETER[] HERO_NEW_MASTERY_HERO_RAINA = {
            PARAMS.DUAL_WIELDING_MASTERY,
            PARAMS.TWO_HANDED_MASTERY,
            PARAMS.BLADE_MASTERY,
            PARAMS.AXE_MASTERY,
            PARAMS.BLUNT_MASTERY,
            PARAMS.FIRE_MASTERY,
            PARAMS.DISCIPLINE_MASTERY,
            PARAMS.MEDITATION_MASTERY,
            PARAMS.FIRE_MASTERY,
//            PARAMS.ARMORER_MASTERY,
//            PARAMS.MEDITATION_MASTERY,
    };
    public static final PARAMETER[] HERO_NEW_MASTERY_HERO_GRIMBART = {
            PARAMS.ATHLETICS_MASTERY,
            PARAMS.TWO_HANDED_MASTERY,
            PARAMS.POLEARM_MASTERY,
            PARAMS.BLUNT_MASTERY,
            PARAMS.SHADOW_MASTERY,
            PARAMS.WITCHERY_MASTERY,
            PARAMS.STEALTH,
            PARAMS.ITEM_MASTERY,
            PARAMS.MEDITATION_MASTERY,
            PARAMS.DUAL_WIELDING_MASTERY
    };
    public static final PARAMETER[] HERO_NEW_MASTERY_DARK_ELF = {
            PARAMS.ATHLETICS_MASTERY,
            PARAMS.TWO_HANDED_MASTERY,
            PARAMS.MARKSMANSHIP_MASTERY,
            PARAMS.POLEARM_MASTERY,
            PARAMS.AXE_MASTERY,
            PARAMS.ITEM_MASTERY,
            PARAMS.DISCIPLINE_MASTERY,
            PARAMS.MEDITATION_MASTERY,
//            PARAMS.ARMORER_MASTERY,
//            PARAMS.MEDITATION_MASTERY,
    };
    public static final PARAMETER[] HERO_NEW_MASTERY_GORR = {
            PARAMS.TWO_HANDED_MASTERY,
            PARAMS.DUAL_WIELDING_MASTERY,
            PARAMS.POLEARM_MASTERY,
            PARAMS.BLADE_MASTERY,
            PARAMS.SAVAGE_MASTERY,
            PARAMS.DEFENSE_MASTERY,
            PARAMS.MOBILITY_MASTERY,
            PARAMS.ITEM_MASTERY,
            PARAMS.MEDITATION_MASTERY,
    };
    public static final String IMAGE_KESERIM = "demo/heroes/keserim.png";
    public static IGG_MISSION MISSION;

    public static IGG_MISSION getByXmlLevelName(String levelName) {
        levelName= (levelName).trim();
        for (IGG_MISSION value : IGG_MISSION.values()) {
            if (value.getXmlLevelName().toLowerCase().contains(levelName.toLowerCase())) {
                return value;
            }
        }
        return IGG_MISSION.valueOf(StringMaster.getEnumFormat(levelName));
    }

    public static IGG_MISSION getMissionByName(String missionName) {
        missionName=XML_Formatter.restoreXmlNodeName(missionName).trim();
        for (IGG_MISSION value : IGG_MISSION.values()) {
            if (value.getMissionName().toLowerCase().contains(missionName.toLowerCase())) {
                return value;
            }
        }
        return IGG_MISSION.valueOf(StringMaster.getEnumFormat(missionName));
        //better store in this form, what if real name changes in updates!
    }

    public static List<PARAMETER> getMasteriesForHero(String name) {
        switch (name) {
            case IGG_Demo.HERO_GORR:
                return new ArrayList<>(Arrays.asList(IGG_Demo.HERO_NEW_MASTERY_GORR));
            case IGG_Demo.HERO_DARK_ELF:
                return new ArrayList<>(Arrays.asList(IGG_Demo.HERO_NEW_MASTERY_DARK_ELF));
            case IGG_Demo.HERO_RAINA:
                return new ArrayList<>(Arrays.asList(IGG_Demo.HERO_NEW_MASTERY_HERO_RAINA));
            case IGG_Demo.HERO_GRIMBART:
                return new ArrayList<>(Arrays.asList(IGG_Demo.HERO_NEW_MASTERY_HERO_GRIMBART));
        }
        return     new ArrayList<>() ;
    }

    public static Color getHeroColor(String name) {
        switch (name) {
            case IGG_Demo.HERO_GORR:
                return Color.ORANGE;
            case IGG_Demo.HERO_DARK_ELF:
                return Color.PURPLE;
            case IGG_Demo.HERO_RAINA:
                return Color.YELLOW;
            case IGG_Demo.HERO_GRIMBART:
                return Color.CYAN;

        }
        return Color.CYAN;
    }

    public enum IGG_MISSION {
        ACT_I_MISSION_I("Gates.xml", "Gates of Nyrn", 1, 1){
            @Override
            public boolean isTutorial() {
                return true;
            }
        },
        ACT_I_MISSION_II("underworld.xml", "Fortress of Nyrn", 1, 2),
        ACT_I_BOSS("Vault.xml", "Rune Vault - boss", 1, 3){
            @Override
            public boolean isBossFight() {
                return true;
            }
        },

        ACT_II_MISSION_I("Bastion.xml", "Bastion", 1, 3),
        ACT_II_BOSS("Shadow Keep.xml", "Shadow Tower - Boss", 2, 1) {
            @Override
            public boolean isBossFight() {
                return true;
            }
        },

        FINALE("Gates.xml", "Nightmare - Boss", 4, 1) {
            @Override
            public boolean isBossFight() {
                return true;
            }

            @Override
            public IGG_MISSION getNext() {
                return null;
            }
        },
        ;
        String xmlLevelName;
        String missionName;
        int act;
        int missionIndex;

        public IGG_MISSION getNext() {
            return values()[EnumMaster.getEnumConstIndex(getClass(), this) + 1];
        }

        public String getScenarioName() {
            return StringMaster.getWellFormattedString(name().split("__")[0]);
        }
        IGG_MISSION(String xmlLevelName, String missionName, int act, int mission) {
            this.xmlLevelName = xmlLevelName;
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

        public boolean isBossFight() {
            return false;

        }

        public boolean isTutorial() {
            return false;
        }


        public String getXmlLevelName() {
            return xmlLevelName;
        }
    }
}
