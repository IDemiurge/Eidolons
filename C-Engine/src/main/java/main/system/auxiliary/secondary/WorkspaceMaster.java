package main.system.auxiliary.secondary;

import main.content.CONTENT_CONSTS.MASTERY;
import main.content.CONTENT_CONSTS.WORKSPACE_GROUP;
import main.content.OBJ_TYPES;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.game.Game;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.data.ListMaster;
import main.system.images.ImageManager.BORDER;

import java.util.List;

public class WorkspaceMaster {

    public static final boolean FILTER_UNITS_LIST = false;
    private static final MASTERY[] SKILL_CONTENT_SCOPE = new MASTERY[]{MASTERY.DEFENSE_MASTERY,
            MASTERY.DISCIPLINE_MASTERY, MASTERY.ARMORER_MASTERY, MASTERY.STEALTH_MASTERY,
            MASTERY.DETECTION_MASTERY, MASTERY.TWO_HANDED_MASTERY, MASTERY.DUAL_WIELDING_MASTERY,
            MASTERY.MOBILITY_MASTERY, MASTERY.ATHLETICS_MASTERY, MASTERY.BLADE_MASTERY,
            MASTERY.AXE_MASTERY, MASTERY.BLUNT_MASTERY, MASTERY.POLEARM_MASTERY,
            MASTERY.MARKSMANSHIP_MASTERY, MASTERY.UNARMED_MASTERY,};
    private static boolean testAllowed;
    private static boolean polishAllowed;
    private static boolean implAllowed;
    private static boolean focusAllowed;
    private static boolean fixAllowed;
    private static List<String> completeSkillSubgroups = new ListMaster<String>()
            .getList("Axe", "Blade", "Polearm", "Blunt", "Two handed",
                    "Dual Wielding", "Armorer", "Defense", "Meditation", "Discipline",

                    "Marksmanship", "Mobility", "Athletics", "Spellcraft", "Wizardry", "");
    String[] DESIGN_GROUPS = {};
    String[] IMPL_GROUPS = {};
    String[] POLISH_GROUPS = {};
    String[] TEST_GROUPS = {};
    String[] FOCUS_GROUPS = {};

    public static void generateAutoWorkspace() {
        for (String f : completeSkillSubgroups) {
            DataManager.getTypesSubGroup(OBJ_TYPES.SKILLS, f);
        }
    }

    public static void autoTagTypesWithWsGroup() {
        /*
         * non-Complete items from [groups] will be given proper values
		 */
    }

    public static void init(Game game) {
        if (game.isDebugMode()) {
            testAllowed = true;
            polishAllowed = true;
            implAllowed = true;
            fixAllowed = true;
            focusAllowed = true;
        } else {
            polishAllowed = true;
        }
    }

    public static boolean checkTypeIsReadyForUse(ObjType type) {
        WORKSPACE_GROUP ws = new EnumMaster<WORKSPACE_GROUP>().retrieveEnumFromEntityProp(
                WORKSPACE_GROUP.class, type);
        switch ((OBJ_TYPES) type.getOBJ_TYPE_ENUM()) {
            case SKILLS:
                if (ws == WORKSPACE_GROUP.COMPLETE || ws == WORKSPACE_GROUP.POLISH) {
                    return true;
                }
                return completeSkillSubgroups.contains(type.getSubGroupingKey()
                        .replace(" Mastery", ""));
        }
        if (ws == null) {
            return true;
        }
        switch (ws) {
            case DESIGN:
                return false;
            case COMPLETE:
                return true;
            case FIX:
                return false;
            case FOCUS:
                return true;
            case IMPLEMENT:
                return false;
            case POLISH:
                return true;
            case TEST:
                return false;
            default:
                return true;

        }

    }

    public static BORDER getBorderForType(ObjType type) {
        // if (!checkTypeIsGenerallyReady(type))
        // return BORDER.HIGHLIGHTED_RED;
        WORKSPACE_GROUP ws = new EnumMaster<WORKSPACE_GROUP>().retrieveEnumFromEntityProp(
                WORKSPACE_GROUP.class, type);
        if (ws != null) {
            switch (ws) {
                case DESIGN:
                    return BORDER.SPEC_Q;
                case TEST:
                    return BORDER.SPEC_SEARCH;
                case FOCUS:
                    return BORDER.SPEC_DEAD2;
                case IMPLEMENT:
                    return BORDER.SPEC_LOCK;
                case FIX:
                    return BORDER.SPEC_DEAD;
            }
        }
        return null;

    }

    public static boolean checkTypeIsReadyToTest(ObjType type) {
        WORKSPACE_GROUP ws = new EnumMaster<WORKSPACE_GROUP>().retrieveEnumFromEntityProp(
                WORKSPACE_GROUP.class, type);
        if (ws != null) {
            switch (ws) {
                case DESIGN:
                    return false;
                case COMPLETE:
                    return false;
                case FIX:
                    return true;
                case FOCUS:
                    return true;
                case IMPLEMENT:
                    return false;
                case POLISH:
                    return true;
                case TEST:
                    return true;
                default:
                    return true;

            }
        }
        return false;
    }

    public static boolean checkTypeIsGenerallyReady(ObjType type) {
        WORKSPACE_GROUP ws = new EnumMaster<WORKSPACE_GROUP>().retrieveEnumFromEntityProp(
                WORKSPACE_GROUP.class, type);
        if (ws != null) {
            switch (ws) {
                case DESIGN:
                    return false;
                case FIX:
                    return isFixAllowed();
                case FOCUS:
                    return isFocusAllowed();
                case IMPLEMENT:
                    return isImplAllowed();
                case POLISH:
                    return isPolishAllowed();
                case TEST:
                    return isTestAllowed();

            }
        }
        return true;
    }

    private static boolean isTestAllowed() {
        return testAllowed;
    }

    private static boolean isPolishAllowed() {
        return polishAllowed;
    }

    private static boolean isImplAllowed() {
        return implAllowed;
    }

    private static boolean isFocusAllowed() {
        return focusAllowed;
    }

    private static boolean isFixAllowed() {
        return fixAllowed;
    }

}
