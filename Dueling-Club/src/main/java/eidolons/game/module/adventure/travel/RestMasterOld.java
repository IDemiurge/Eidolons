package eidolons.game.module.adventure.travel;

import eidolons.ability.effects.common.ModifyValueEffect;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.module.adventure.entity.MacroActionManager.MACRO_MODES;
import eidolons.game.module.adventure.entity.MacroParty;
import eidolons.game.module.adventure.global.TimeMaster;
import eidolons.game.module.adventure.town.Tavern;
import eidolons.game.module.adventure.town.Town;
import eidolons.game.module.herocreator.logic.spells.DivinationMaster;
import eidolons.swing.generic.services.dialog.DialogMaster;
import main.ability.effects.Effect.MOD;
import main.content.CONTENT_CONSTS2.MACRO_STATUS;
import main.content.values.parameters.MACRO_PARAMS;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.MACRO_PROPS;
import main.entity.Ref;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.RandomWizard;

import java.util.Map;

public class RestMasterOld {
    private static Integer mod;
    private static int one_mod;
    private static int vig_mod;
    private static int hp_mod;

    // setFinishDate / setRestTime / defaultTime (4/8/12 hours)
    // TODO endTurn => auto-rest? valid modes preCheck
    public static boolean rest(MacroParty party) {

        for (Unit hero : party.getMembers()) {
            MACRO_MODES mode = hero.getMacroMode();
            if (mode == null || !checkModeValid(party, mode)) {
                DialogMaster.inform("Choose a mode on the lowermost panel!");
                return false;
                // mode = promptMode(hero); // or auto-set for non-leaders?
            }
            hero.setMacroMode(mode);
        }
        // each hero gain as per Mode
        // non-cancelable perhaps? Or also an Order?
        //
        party.setStatus(MACRO_STATUS.CAMPING);
        return true;
        // party.setCurrentDestination(null);
        // party.setCurrentRoute(null);
    }

    private static boolean checkModeValid(MacroParty party, MACRO_MODES mode) {
        if (party.getTown() != null) {
            return (mode.isTownPermitted());
        } else {
            return mode.isCountryPermitted();
        }
    }

    private static MACRO_MODES promptMode(Unit hero) {
//        Component[] actionListItems = ((G_ListPanel) MacroManager.getMapView()
//                .getMacroActionPanel().getPanelMap()
//                .get(MACRO_ACTION_GROUPS.MODE).getCurrentComponent()).getList()
//                .getComponents();
//        int option = DialogMaster.optionChoice("Choose what to do",
//                actionListItems);
//        ListItem item = (ListItem) actionListItems[option];
//
//        hero.setProperty(MACRO_PROPS.MACRO_MODE, item.getObj().getName());
//        return hero.getMacroMode();
        return null;
    }

    public static void applyMacroMode(MacroParty party) {
        mod = 100;
        one_mod = 100;
        vig_mod = 100;
        hp_mod = 100; // TODO from area! or place...
        if (party.getTown() != null) {
            Town town = party.getTown();
            for (Tavern t : town.getTaverns()) {
                // t.getProperty(MACRO_PROPS.ROOM_TYPE);
                if (t.getIntParam(MACRO_PARAMS.RENT_DURATION) > 0) {
                    // apply mods from Room
                    mod = t.getIntParam(MACRO_PARAMS.ROOM_QUALITY_MOD);
                    one_mod = t.getIntParam(MACRO_PARAMS.ROOM_QUALITY_MOD);
                    vig_mod = t.getIntParam(MACRO_PARAMS.ROOM_QUALITY_MOD);
                    hp_mod = t.getIntParam(MACRO_PARAMS.ROOM_QUALITY_MOD);

                }
            }
        }

        for (Unit hero : party.getMembers()) {
            RestMasterOld.applyMacroMode(hero);
        }

    }

    public static void applyMacroMode(Unit hero) {
        MACRO_MODES mode = new EnumMaster<MACRO_MODES>().retrieveEnumConst(
         MACRO_MODES.class, hero.getProperty(MACRO_PROPS.MACRO_MODE));
        String paramString = mode.getParamString();
        Map<PARAMETER, String> map = new RandomWizard<PARAMETER>()
         .constructStringWeightMap(paramString, PARAMETER.class);
        for (PARAMETER p : map.keySet()) {
            Ref ref = hero.getRef().getCopy();
            setModeTargets(mode, ref); // TODO MAX
            String string = formatModeFormula(map.get(p));
            new ModifyValueEffect(p, MOD.MODIFY_BY_CONST, string)
             .apply(ref);
            // hero.modifyParameter(portrait, amount);
        }
        // applyCustomEffect
        if (mode.isSpecEffect()) {
            switch (mode) {
                // case STUDY:
                // // spell points? target-spell in library/scroll? minor xp
                // // bonus?
                // break;
                // case MEMORIZE:
                // // dynamic memory points produced
                // break;
                case BREW:
                    break;
                case DIVINATION:
                    DivinationMaster.divine(hero, TimeMaster.hoursLeft());
                    break;
                case RECHARGE:
                    break;
                case REPAIR:
                    break;
                case SCOUT:
                    break;
                case STAND_WATCH:
                    break;
                case TRAIN_WITH_MAGIC:
                    // choose mastery? minor xp bonus?
                    break;
                case TRAIN_WITH_WEAPONS:
                    break;
            }
        }
    }

    private static String formatModeFormula(String string) {
        return string.replace("[hours remaining]", TimeMaster.hoursLeft() + "");
    }

    private static void setModeTargets(MACRO_MODES mode, Ref ref) {
        ref.setTarget(ref.getSource());
        // TODO whole party

    }

    // upon activation
    public static void applyMacroModeContinuous(Unit hero) {
        MACRO_MODES mode = new EnumMaster<MACRO_MODES>().retrieveEnumConst(
         MACRO_MODES.class, hero.getProperty(MACRO_PROPS.MACRO_MODE));
        String paramString = mode.getContinuousParamString();

        Map<PARAMETER, Integer> map = new RandomWizard<PARAMETER>()
         .constructWeightMap(paramString, PARAMETER.class);

        for (PARAMETER param : map.keySet()) {
            hero.modifyParameter(param, map.get(param));
        }
    }

}
