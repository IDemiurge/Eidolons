package main.rules.action;

import main.ability.effects.oneshot.buff.RemoveBuffEffect;
import main.ability.effects.common.SpectrumEffect;
import main.ability.effects.oneshot.rule.WatchActionEffect;
import main.ability.effects.oneshot.rule.WatchBuffEffect;
import main.content.PARAMS;
import main.content.enums.entity.ActionEnums;
import main.content.enums.entity.UnitEnums;
import main.content.enums.rules.VisionEnums.VISIBILITY_LEVEL;
import main.content.mode.STD_MODES;
import main.elements.conditions.standard.OwnershipCondition;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.active.DC_ActiveObj;
import main.entity.active.DC_SpellObj;
import main.entity.obj.ActiveObj;
import main.entity.obj.BfObj;
import main.entity.obj.BuffObj;
import main.entity.obj.DC_Obj;
import main.entity.obj.unit.Unit;
import main.game.battlefield.FacingMaster;
import main.game.battlefield.vision.VisionManager;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.ListMaster;
import main.system.math.MathMaster;
import main.system.math.PositionMaster;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WatchRule implements ActionRule {
    public static final int ATTACK_MOD = 60;
    public static final int DEFENSE_MOD = 60;
    public static final int ATTACK_MOD_OTHERS = -20;
    public static final int DEFENSE_MOD_OTHERS = -30;
    public static final Integer INSTANT_ATTACK_MOD = 35;
    private static final int AP_PENALTY = 50;
    private static final int DEFENSE_MOD_OTHERS_PER_ADDITIONAL = 50;
    private static final int ATTACK_MOD_OTHERS_PER_ADDITIONAL = 50;
    private static final int AP_PENALTY_PER_ADDITIONAL = 50;
    // TODO watch *CELLS*?
    static String paramModString = "REFLEX_BEAT_BONUS(25);";
    /*
    :: Break conditions/prompt
    :: spectrum
    :: AoO - move from/onto cell, special actions
    >> No AoO's if not watched, by default?
    >> attack bonus for AoOs in spectrum?
         */
    private static Map<Unit, List<DC_Obj>> watchersMap = new HashMap<>();

    private static void removeWatcher(Unit watcher) {
        getWatchersMap().remove(watcher);
    }

    public static void breakWatch(Unit watcher, DC_Obj watched) {
        List<DC_Obj> list = getWatchersMap().get(watcher);
        if (list != null) {
            list.remove(watched);
        }
    }

    private static boolean checkValidWatchPairTarget(Unit watcher, DC_Obj watched) {
        if (FacingMaster.getSingleFacing(watcher, (BfObj) watched) != UnitEnums.FACING_SINGLE.IN_FRONT) {
            // some may have side-vision enough?
            return false;
        }
        if (PositionMaster.getDistance(watched.getCoordinates(), watcher.getCoordinates()) > watcher
                .getIntParam(PARAMS.SIGHT_RANGE)) {
            return false;
        }

        return true;
    }

    public static boolean checkActionWatched(DC_ActiveObj action, Unit watcher) {
        // AoO and Defense
        if (FacingMaster.getSingleFacing(watcher, action.getOwnerObj()) == UnitEnums.FACING_SINGLE.IN_FRONT) {
            return true;
        }
        return false;
    }

    private static boolean checkValidWatchTarget(DC_Obj watched) {
        if (!VisionManager.checkVisible(watched, true)) // TODO 'to watcher' !..
        {
            return false;
        }
        // clear shot?
        if (watched.isDead()) {
            return false;
        }
        if (watched.getVisibilityLevel() == VISIBILITY_LEVEL.CONCEALED) {
            return false;
        }
        return true;
        // if (target.getOwner() != unit.getOwner())
        // if (!target.checkInSightForUnit(unit))
        // return true;
    }

    private static void checkWatcherModeReducesApOnTurnEnd(Unit watcher) {

    }

    private static boolean checkValidWatcher(Unit watcher) {
        if (watcher.getBehaviorMode() != null) {
            return false;
        }
        if (watcher.checkStatus(UnitEnums.STATUS.CHARMED)) {
            return false;
        }
        if (watcher.checkStatusPreventsActions()) {
            return false;
        }
        if (!watcher.getMode().isWatchSupported()) {
            return false;
        }
        // if (watcher.checkModeDisablesActions() &&
        // !watcher.getMode().equals(STD_MODES.ALERT)) {
        // return false;

        return true;
    }

    // FOR PROMPT/INFO ONLY!
    public static boolean checkActionMayBreakWatch(DC_ActiveObj action, DC_Obj watched) {
        if (checkActionBreaksWatch(action, watched)) {
            return true;
        }
        if (action.getActionGroup() == ActionEnums.ACTION_TYPE_GROUPS.TURN) {
            return true; // TODO detailed preCheck!
        }
        if (action.getActionGroup() == ActionEnums.ACTION_TYPE_GROUPS.MODE) {
            return true; //
        }
        if (action.getActionGroup() == ActionEnums.ACTION_TYPE_GROUPS.MOVE) {
            // getDestination() -> preCheck relative facing to watch
        }
        if (action.getActionGroup() == ActionEnums.ACTION_TYPE_GROUPS.SPELL) {
            // getDestination() -> preCheck relative facing to watch
            if (action instanceof DC_SpellObj) {
                DC_SpellObj spellObj = (DC_SpellObj) action;
                // teleport
                return spellObj.isChanneling();
            }
        }
        if (action.getActionGroup() == ActionEnums.ACTION_TYPE_GROUPS.SPECIAL) {
            // special property like aoo
        }

        return false;
        // facing, channeling, targeting another unit(unless special skill),
        // mode, move beyond
    }

    public static boolean checkActionBreaksWatch(DC_ActiveObj action, DC_Obj watched) {
        // only no-state actions, others will break on checkValid()
        if (action.getActionGroup() == ActionEnums.ACTION_TYPE_GROUPS.ATTACK) {
            // other target ; special skill may allow retain watch
            if (action.getRef().getTargetObj() != watched) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkWatched(Unit watcher, DC_Obj watched) {
        if (isTestMode())
            return true;
        List<DC_Obj> list = getWatchersMap().get(watcher);
        if (list != null) {
            return list.contains(watched);
        }
        return false;
    }

    private static boolean isTestMode() {
   return false;
    }

    private static WatchBuffEffect getWatchBuffEffect(Unit watcher, List<DC_Obj> list) {
        return new WatchBuffEffect(watcher, list);
    }

    public static Map<Unit, List<DC_Obj>> getWatchersMap() {
        return watchersMap;
    }

    public static String getDefenseModVsOthers(Unit watcher, List<DC_Obj> list) {
        String mod = StringMaster.wrapInParenthesis(""
                + MathMaster.applyModIfNotZero(DEFENSE_MOD_OTHERS, watcher
                .getIntParam(PARAMS.WATCH_DEFENSE_OTHERS_MOD)));
        if (list.size() > 1) {
            mod = mod + mod + "*" + DEFENSE_MOD_OTHERS_PER_ADDITIONAL + "/100*" + (list.size() - 1);
        }
        return mod;
    }

    public static String getApPenaltyMod(Unit watcher, List<DC_Obj> list) {
        String mod = StringMaster.wrapInParenthesis(""
                + MathMaster.applyModIfNotZero(AP_PENALTY, watcher
                .getIntParam(PARAMS.WATCH_AP_PENALTY_MOD)));
        if (list.size() > 1) {
            mod = mod + mod + "*" + AP_PENALTY_PER_ADDITIONAL + "/100*" + (list.size() - 1);
        }
        return mod;
    }

    public static String getDefenseModVsWatched(Unit watcher, List<DC_Obj> list) {
        String mod = StringMaster.wrapInParenthesis(""
                + MathMaster.applyModIfNotZero(DEFENSE_MOD, watcher
                .getIntParam(PARAMS.WATCH_DEFENSE_MOD)));
        // TODO from WATCHED
        return mod;
    }

    public static String getAttackModVsWatched(Unit watcher, List<DC_Obj> list) {
        String mod = StringMaster.wrapInParenthesis(""
                + MathMaster.applyModIfNotZero(ATTACK_MOD, watcher
                .getIntParam(PARAMS.WATCH_ATTACK_MOD)));
        // TODO reduce?
        return mod;
    }

    public static String getAttackModVsOthers(Unit watcher, List<DC_Obj> list) {
        String mod = StringMaster.wrapInParenthesis(""
                + MathMaster.applyModIfNotZero(ATTACK_MOD_OTHERS, watcher
                .getIntParam(PARAMS.WATCH_ATTACK_OTHERS_MOD)));

        if (list.size() > 1) {
            mod = mod + mod + "*" + ATTACK_MOD_OTHERS_PER_ADDITIONAL + "/100*" + (list.size() - 1);
        }
        return mod;
    }

    @Override
    public void actionComplete(ActiveObj activeObj) {
        // updateWatchStatuses();
    }

    // what about Alert?
    public void updateWatchStatuses() {
        for (Unit watcher : getWatchersMap().keySet()) {
            updateWatchStatus(watcher);
        }
    }

    public void updateWatchStatus(Unit watcher) {
        List<DC_Obj> list = getWatchersMap().get(watcher);
        boolean invalid = false;
        if (list != null) {
            if (!checkValidWatcher(watcher)) {
                invalid = true;
                removeWatcher(watcher);
            } else {
                for (DC_Obj watched : list) {
                    if (!checkValidWatchTarget(watched)) {
                        breakWatch(watcher, watched);
                        break;
                    } else if (!checkValidWatchPairTarget(watcher, watched)) {
                        breakWatch(watcher, watched);
                        break;
                    }
                }
            }
        }

        BuffObj buff = watcher.getBuff("Watching", false);
        if (buff != null) {
            watcher.getGame().getManager().buffRemoved(buff);
        }
        if (!invalid) {
            if (watcher.getMode().equals(STD_MODES.ALERT)) {
                Ref ref = watcher.getRef().getCopy();
                try {
                    SpectrumEffect spectrumEffect = new SpectrumEffect(new OwnershipCondition(true,
                            "match", "source"), new WatchActionEffect(true));
                    spectrumEffect.setRangeFormula(StringMaster.getValueRef(KEYS.SOURCE,
                            PARAMS.SIGHT_RANGE));
                    spectrumEffect.setApplyThrough(false);// for now...
                    spectrumEffect.apply(ref);
                    watcher.getBuff(STD_MODES.ALERT.getBuffName(), false).setOnDispelEffects(
                            new RemoveBuffEffect("Watching ", false));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        list = getWatchersMap().get(watcher);
        if (ListMaster.isNotEmpty(list))
        // TODO alert???
        {
            getWatchBuffEffect(watcher, list).apply(Ref.getSelfTargetingRefCopy(watcher));
        }
    }

    @Override
    public boolean unitBecomesActive(Unit unit) {
        // TODO ??
        // applyWatcherBuffs();
        return true;
    }

}
