package eidolons.macro.entity.action;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.core.Eidolons;
import eidolons.macro.MacroGame;
import eidolons.macro.entity.MacroRef;
import eidolons.macro.entity.party.MacroParty;
import main.content.DC_TYPE;
import main.data.ConcurrentMap;
import main.data.filesys.PathFinder;
import main.entity.obj.Obj;
import main.entity.type.ObjType;
import main.system.PathUtils;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.StringMaster;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MacroActionManager {
    // "Explore Route" => still coming forward at 1/2 speed...
    // "Explore Outskirts" => not moving, just looking for routes/places

    private static Map<String, MacroAction> actionMap = new ConcurrentMap<>();

    private static ObjType type;

    private static boolean actionsBlocked;

    public static String getMacroPartyActionImagePathPrefix() {

        return
         StrPathBuilder.build(PathFinder.getMacroPath(), "actions", "party") + PathUtils.getPathSeparator();
    }

    public static String getMacroModeImagePathPrefix() {
        return
         StrPathBuilder.build(PathFinder.getMacroPath(), "actions", "Mode");
    }

    public static List<MacroAction> getMacroActions(MACRO_ACTION_GROUPS group,
                                                    MacroParty playerParty) {
        Unit hero = Eidolons.getMainHero();// MacroManager.getSelectedPartyMember();
        switch (group) {
            case CHARACTER:
                return getHeroActions(hero);
            case MODE:
                // should there be a way to order the entire party to enter X
                // mode?
                // and by the way, what of the non-Camp modes?!
                // e.g. Stealth/Explore modes
                return getModeActions(hero);
            case PARTY:
                return getPartyActions(playerParty);
        }
        return null;
    }


    private static List<MacroAction> getPartyActions(MacroParty playerParty) {
        List<MacroAction> list = new ArrayList<>();
        list.add(getAction(MACRO_PARTY_ACTIONS.EXPLORE.toString(), playerParty));
        list.add(getAction(MACRO_PARTY_ACTIONS.WAIT.toString(), playerParty));
        list.add(getAction(MACRO_PARTY_ACTIONS.HIDE.toString(), playerParty));
        list.add(getAction(MACRO_PARTY_ACTIONS.TRAVEL.toString(), playerParty));
        // TODO perhaps just block them
        // if (playerParty.getTown() == null)
        list.add(getAction(MACRO_PARTY_ACTIONS.CAMP.toString(), playerParty));

        // if (playerParty.getCurrentRoute() != null)
        list.add(getAction(MACRO_PARTY_ACTIONS.AMBUSH.toString(), playerParty));

        return list;
    }

    private static List<MacroAction> getModeActions(Unit hero) {
        List<MacroAction> modeActions = new ArrayList<>();
        // String actions = hero.getProp("macro actions");
        for (MACRO_MODES m : MACRO_MODES.values()) {
            // m.getRequirements() if (m.isSpecial())
            modeActions.add(actionMap.get(m.toString()));
        }
        return modeActions;
    }

    private static List<MacroAction> getHeroActions(Unit hero) {
        // TODO macro spells and tricks

        // init per hero

        return new ArrayList<>();
    }

    private static MacroAction getAction(String actionName, Obj obj) {
        MacroAction action = actionMap.get(actionName);
        if (action != null) {
            return action;
        }
        MacroRef ref = new MacroRef(obj);
        // objType.initType(); aut0
        ObjType objType = null;//TODO  DataManager.getType(actionName, MACRO_OBJ_TYPES.MAP_ACTIONS);
        if (objType == null)
            objType = getBaseType();
        action = new MacroAction(objType, ref);
        actionMap.put(actionName, action);
        return action;
    }

    private static ObjType getBaseType() {
        if (type == null) {
            type = new ObjType(MacroGame.getGame());
            type.setOBJ_TYPE_ENUM(DC_TYPE.ACTIONS);
            type.setName("Base Action Type");
            // type.setOBJ_TYPE_ENUM(MACRO_OBJ_TYPES.MACRO_ACTION);
        }
        return new ObjType(type);
    }

    public static void generateMacroActions() { // for hero? for party? or
        // perhaps not, just apply to
        // different entities
        for (MACRO_MODES m : MACRO_MODES.values()) {
            MacroAction action =
             new MacroAction(getBaseType(), new MacroRef(), m);
            actionMap.put((m.toString()), action);
        }
        for (MACRO_PARTY_ACTIONS m : MACRO_PARTY_ACTIONS.values()) {
            MacroAction action = new MacroAction(getBaseType(), new MacroRef(),
             m);
            actionMap.put((m.toString()), action);
        }
        // can this work for real setting with many parties active?
    }

    public static boolean isActionsBlocked() {
        return actionsBlocked;
    }

    public static void setActionsBlocked(boolean b) {
        actionsBlocked = b;
    }

    public enum MACRO_MODES {
        REST("sleep.jpg", "energy([hours remaining]*5);", "", true, true),
        PREPARE_FOR_BATTLE("guard.jpg", "combat readiness([hours remaining]*5);", "", true, false),
        STAND_WATCH("watch2.jpg", "", "", true, null),

        DIVINATION("divine.jpg", "", "", true, null),
        MEDITATION("meditation2.jpg", "oneness([hours remaining]*(5+{meditation}/10));", "", true, null),
        TRAIN_WITH_MAGIC("practice magic.jpg", "", "", true, null), // add
        // fatigue?
        TRAIN_WITH_WEAPONS("train weapons.jpg", "", "", true, null), // add
        // readiness?
        FORAGE("forage2.jpg", "provisions([hours remaining]*{hunting}*{area_forage_mod}/100);", "", true, false),
        SCOUT("forage.jpg", "", "", true, false),

        REPAIR("smith.jpg", "", ""),
        BREW("brew.jpg", "", ""),
        RECHARGE("", "", ""),
        ENTERTAIN("", "", ""), // BARD
        ;

        String continuousParamString;
        String paramString;
        Boolean countryPermitted = true;
        Boolean townPermitted = true;
        String imagePath;

        MACRO_MODES(String imagePath, String paramString,
                    String continuousParamString, Boolean countryPermitted,
                    Boolean townPermitted) {
            this(imagePath, paramString, continuousParamString);
            this.countryPermitted = countryPermitted; // null means 'depends'
            // (on room, zone, etc)
            this.townPermitted = townPermitted;

        }

        MACRO_MODES(String imagePath, String paramString,
                    String continuousParamString) {
            this.paramString = paramString;
            this.continuousParamString = continuousParamString;
            if (imagePath.isEmpty()) {
                imagePath = (toString());
            }
            this.imagePath = MacroActionManager.getMacroModeImagePathPrefix()
             + imagePath;
        }

        public String getContinuousParamString() {
            return continuousParamString;
        }

        public String getParamString() {
            return paramString;
        }

        public boolean isSpecEffect() {
            return false;
        }

        public String getImagePath() {
            return imagePath;
        }

        public String toString() {
            return StringMaster.getWellFormattedString(name());
        }

        public Boolean isCountryPermitted() {
            return countryPermitted;
        }

        public Boolean isTownPermitted() {
            return townPermitted;
        }

    }

    public enum MACRO_PARTY_ACTIONS {
        CAMP, WAIT, HIDE, EXPLORE,

        AMBUSH, TRAVEL;

        public String toString() {
            return StringMaster.getWellFormattedString(name());
        }

        public String getImagePath() {
            return MacroActionManager.getMacroPartyActionImagePathPrefix()
             + toString() + ".png";
        }

    }

}
