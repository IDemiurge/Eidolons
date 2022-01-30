package eidolons.game.battlecraft.ai.elements.atomic;

import eidolons.content.PARAMS;
import eidolons.entity.active.DC_UnitAction;
import eidolons.entity.item.QuickItem;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.unit.Unit;
import eidolons.game.battlecraft.ai.UnitAI;
import eidolons.game.battlecraft.ai.advanced.machine.AiConst;
import eidolons.game.battlecraft.ai.elements.actions.AiAction;
import eidolons.game.battlecraft.ai.elements.actions.AiActionFactory;
import eidolons.game.battlecraft.ai.elements.generic.AiHandler;
import eidolons.game.battlecraft.ai.elements.generic.AiMaster;
import eidolons.game.battlecraft.ai.tools.Analyzer;
import eidolons.game.battlecraft.ai.tools.ParamAnalyzer;
import eidolons.game.battlecraft.ai.tools.priority.DC_PriorityManager;
import eidolons.game.battlecraft.logic.battlefield.DC_MovementManager;
import eidolons.game.battlecraft.logic.dungeon.universal.Positioner;
import eidolons.game.battlecraft.rules.action.StackingRule;
import eidolons.game.core.Core;
import eidolons.game.exploration.dungeon.objects.Door;
import eidolons.game.exploration.dungeon.objects.DoorMaster.DOOR_ACTION;
import eidolons.game.exploration.dungeon.objects.DoorMaster.DOOR_STATE;
import eidolons.game.exploration.dungeon.objects.DungeonObj.DUNGEON_OBJ_TYPE;
import main.content.enums.rules.VisionEnums;
import main.content.enums.system.AiEnums;
import main.content.enums.system.AiEnums.AI_TYPE;
import main.content.enums.system.AiEnums.ORDER_PRIORITY_MODS;
import main.entity.Ref;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.log.FileLogger.SPECIAL_LOG;
import main.system.auxiliary.log.SpecialLogger;
import main.system.auxiliary.secondary.Bools;
import main.system.math.FuncMaster;
import main.system.math.PositionMaster;

import java.util.Collection;
import java.util.List;

import static main.content.enums.entity.ActionEnums.DEFAULT_ACTION.*;

public class AtomicAi extends AiHandler {

    private static final double MAX_DST_TURN = 1;
    private static final double MAX_DST_TURN_BEHIND = 1;
    private boolean hotzoneMode;
    private boolean on = true;

    public AtomicAi(AiMaster master) {
        super(master);
    }

    public AiAction getAtomicAction(UnitAI ai, AiEnums.AI_LOGIC_CASE atomic) {
        AiAction aiAction = null;
        switch (atomic) {
            case FAR_UNSEEN:
                if (ai.getType() == AI_TYPE.BRUTE ||
                        ai.getGroup().getBehavior()
                                == UnitAI.AI_BEHAVIOR_MODE.AGGRO) {
                    aiAction = getAtomicActionApproach(ai);
                    break;
                }
            case RELOAD:
            case RESTORE:
                aiAction = getAtomicActionPrepare(ai);
                break;
            case APPROACH:
                aiAction = getAtomicActionApproach(ai);
                break;
        }
        // if (checkAtomicActionTurn(ai)) {
        //     action = getAtomicActionTurn(ai);
        //     if (action != null) {
        //         action.setTaskDescription("Facing Adjustment");
        //         return action;
        //     }
        // }
        // action = getAtomicActionPrepare(ai);
        // if (action != null) {
        //     action.setTaskDescription(TASK_DESCRIPTION.RESTORATION.toString());
        //     return action;
        // }
        // if (checkAtomicActionApproach(ai))
        //     action = getAtomicActionApproach(ai);
        if (aiAction != null) {
            String message = getUnit() + " chooses atomic action: " + aiAction;
            SpecialLogger.getInstance().appendAnalyticsLog(SPECIAL_LOG.AI, message);
        } else {
            SpecialLogger.getInstance().appendAnalyticsLog(SPECIAL_LOG.AI, getUnit() + " finds no atomic action!");
            return null;
        }
        return aiAction;
    }

    public AiAction getAtomicActionForced(UnitAI ai) {
        AiAction aiAction;
        aiAction = getAtomicActionDoor(ai);
        if (aiAction != null)
            aiAction.setTaskDescription("Door");

        return aiAction;
    }

    public AiAction getAtomicActionDoor(UnitAI ai) {

        Coordinates c = getDoorCoordinates(ai);
        if (c == null)
            return null;
        Door door = (Door) game.getObjectByCoordinate(c);
        DC_UnitAction action = game.getDungeonMaster().getDungeonObjMaster(DUNGEON_OBJ_TYPE.DOOR).createAction(DOOR_ACTION.OPEN,
                ai.getUnit(), door);
        return AiActionFactory.newAction(action, ai.getUnit().getRef().getTargetingRef(door));

    }

    public boolean checkAtomicActionDoor(UnitAI ai) {
        return getDoorCoordinates(ai) != null;
    }

    private Coordinates getDoorCoordinates(UnitAI ai) {
        Coordinates coordinates = ai.getUnit().getCoordinates();
        for (Coordinates c : coordinates.getAdjacentCoordinates()) {
            if (c == null) {
                continue;
            }
            DOOR_STATE state = game.getBattleFieldManager().getDoorMap().get(c);
            if (state == DOOR_STATE.CLOSED) {
                return c;
            }
        }
        return null;
    }

    public AiAction getAtomicActionPrepare(UnitAI ai) {
        //ammo
        //meditate
        // if (!checkAtomicActionCase(ATOMIC_LOGIC_CASE.PREPARE, ai)) {
        //     return null;
        // }
        if (ParamAnalyzer.isFatigued(getUnit())) {
            return AiActionFactory.newAction(Rest.name(),
                    getUnit().getAI());
        }
        if (ParamAnalyzer.isHazed(getUnit())) {
            return AiActionFactory.newAction(Concentrate.name(),
                    getUnit().getAI());
        }

        if (ai.getType() == AI_TYPE.ARCHER)
            if (ai.getUnit().getRangedWeapon() != null)
                if (ai.getUnit().getRangedWeapon().getAmmo() == null) {
                    AiAction aiAction = getReloadAction(ai);
                    if (aiAction != null) {
                        aiAction.setTaskDescription("Ammo Reload");
                        return aiAction;
                    }
                }

        return AiActionFactory.newAction(
                RandomWizard.random() ? RandomWizard.random() ?
                        Wait.toString() : RandomWizard.random() ?
                        Wait.toString() :
                        Defend.toString() : RandomWizard.random() ?
                        Wait.toString() :
                        On_Alert.toString(), ai);
    }

    public AiAction getAtomicWait(Unit unit) {
        return AiActionFactory.newAction(unit.getAction(
                Wait.toString()), Ref.getSelfTargetingRefCopy(unit));
    }

    private AiAction getReloadAction(UnitAI ai) {
        QuickItem ammo = null;
        Integer maxCost = 0;
        for (QuickItem a : ai.getUnit().getQuickItems()) {
            if (a.isAmmo()) {
                Integer cost = a.getWrappedWeapon().getIntParam(PARAMS.GOLD_COST);
                if (cost > maxCost) {
                    ammo = a;
                }
            }
        }
        if (ammo != null) {
            return AiActionFactory.newAction(ammo.getActive(), new Ref(ai.getUnit()));
        }

        return null;
    }

    public AiAction getAtomicActionApproach(UnitAI ai) {
        return getAtomicActionMove(ai, true);
    }

    public AiAction getAtomicActionAttack(UnitAI ai) {
        return null;
    }

    private Coordinates getApproachCoordinate(UnitAI ai) {
        Collection<Unit> units = getAnalyzer().getVisibleEnemies(ai);
        if (units.isEmpty())
            return null;
        //TODO AI Review - LC 2.0
        DIRECTION d=DIRECTION.NONE;
        Coordinates c = getUnit().getCoordinates().getAdjacentCoordinate(d);
        if (new StackingRule(game).canBeMovedOnto(getUnit(), c)) {
            return c;
        }
        Coordinates target = units.iterator().next().getCoordinates();
        double dst = target.dst_(getUnit().getCoordinates());
        for (BattleFieldObject object : getGame().getObjectsOnCoordinateNoOverlaying(c)) {
            if (object.getOwner() == (getUnit().getOwner())) {
                return Positioner.adjustCoordinate(ai.getUnit(), c, d,
                        c1 -> c1.dst_(target) <= dst);
            }
        }
        // return Positioner.adjustCoordinate(ai.getUnit(), c, d);
        return c;
    }

    //TODO NF AI Revamp
    private Integer getThreat(UnitAI ai, Unit t) {
        return t.getLevel() * 5;
    }

    public AiAction getAtomicActionMove(UnitAI ai, Boolean approach_retreat_search) {
        Coordinates pick;
        boolean b = isHotzoneMode();
        if (b) {
            pick = getHotZoneCell(ai, approach_retreat_search);
        } else {
            pick = getApproachCoordinate(ai);
        }
        if (pick == null)
            pick = b ? getApproachCoordinate(ai)
                    : getHotZoneCell(ai, approach_retreat_search);
        if (pick == null)
            return null;

        return getAtomicMove(pick, ai.getUnit());
    }

    private Coordinates getHotZoneCell(UnitAI ai, Boolean approach_retreat_search) {
        ATOMIC_LOGIC logic = getAtomicLogic(ai);
        List<Unit> enemies = Analyzer.getVisibleEnemies(ai);
        List<Unit> allies = Analyzer.getAllies(ai);
        float greatest = 0;

        Coordinates pick = null;
        for (Coordinates c : ai.getUnit().getCoordinates().getAdjacentCoordinates()) {
            float i = 0;
            i += getCellPriority(c, ai);
            if (Bools.isFalse(approach_retreat_search)) {
                for (Unit a : allies) {
                    i += getAllyPriority(c, a, ai, logic) + RandomWizard.getRandomInt(10);
                }
            }
            for (Unit e : enemies) {
                i = i + ((approach_retreat_search ? 1 : -1)
                        * getEnemyPriority(c, e, ai, logic))
                        + RandomWizard.getRandomInt(10)
                ;

            }
            i = i * getCellPriorityMod(c, ai) / 100;
            if (i > greatest) {
                greatest = i;
                pick = c;
            }
        }
        return pick;
    }

    public AiAction getAtomicMove(Coordinates pick, Unit unit) {
        AiAction aiAction = null;
        if (aiAction == null) {
            try {
                aiAction = DC_MovementManager.getMoveAction(unit, pick);
                //                main.system.auxiliary.log.LogMaster.log(1, " ATOMIC ACTION " + action +
                //                 "  CHOSEN TO GET TO " + pick);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
                //TODO what to return???
            }
        }
        return aiAction;
    }

    private int getCellPriority(Coordinates c, UnitAI ai) {
        return 0;
    }

    private int getCellPriorityMod(Coordinates c, UnitAI ai) {
        //        if (FacingMaster
        //                .getSingleFacing(ai.getUnit().getFacing(), ai.getUnit().getCoordinates(), c) == UnitEnums.FACING_SINGLE.IN_FRONT) {
        //            mod += 35;
        //        }
        //        if (FacingMaster
        //                .getSingleFacing(ai.getUnit().getFacing(), ai.getUnit().getCoordinates(), c) == UnitEnums.FACING_SINGLE.BEHIND) {
        //            mod += -50;
        //        }
        //        if (PositionMaster.inLine(ai.getUnit().getCoordinates(), c)) {
        //            if (ai.getType() == AiEnums.AI_TYPE.BRUTE) {
        //                mod += 25;
        //            }
        //            mod += 35;
        //        }
        return 100;
    }

    private float getEnemyPriority(Coordinates c, Unit e, UnitAI ai, ATOMIC_LOGIC logic) {
        if (logic == ATOMIC_LOGIC.GEN_AGGRO || logic == ATOMIC_LOGIC.GROUP_AGGRO) {
            return (float) DC_PriorityManager.getUnitPriority(ai, e, null)
                    / (1 + PositionMaster.getDistance(e.getCoordinates(), c));
        }
        return 0;
    }

    private float getAllyPriority(Coordinates c, Unit a, UnitAI ai, ATOMIC_LOGIC logic) {
        if (logic == ATOMIC_LOGIC.PROTECT || logic == ATOMIC_LOGIC.FORMATION) {
            return (float) DC_PriorityManager.getUnitPriority(ai, a, null)
                    / (1 + PositionMaster.getDistance(a.getCoordinates(), c));
        }
        return 0;
    }

    private ATOMIC_LOGIC getAtomicLogic(UnitAI ai) {
        return ATOMIC_LOGIC.GEN_AGGRO;
    }

    public AiEnums.AI_LOGIC_CASE checkAtomicActionRequired(UnitAI ai) {

        return checkAtomicActionCaseAny(ai); //also check power, danger, distance
    }

    public AiEnums.AI_LOGIC_CASE checkAtomicActionCaseAny(UnitAI ai) {
        boolean canAttack = getSituationAnalyzer().canAttackNow(ai);
        AiEnums.AI_LOGIC_CASE _case;
        for (ATOMIC_LOGIC_CASE C : ATOMIC_LOGIC_CASE.values()) {
            if (canAttack)
                if (C != ATOMIC_LOGIC_CASE.PREPARE)
                    //if can attack immediately, ATOMIC can only be used to force REST/..
                    continue;
            try {
                if ((_case = checkAtomicActionCase(C, ai)) != null) {
                    return _case;
                }
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        }
        return null;
    }

    public AiEnums.AI_LOGIC_CASE checkAtomicActionCase(ATOMIC_LOGIC_CASE CASE, UnitAI ai) {

        switch (CASE) {
            case APPROACH:
                return checkAtomicActionApproach(ai);
            case RETREAT:
                return checkAtomicActionRetreat(ai);
            case SEARCH:
                break;
            case ATTACK:
                return checkAtomicActionAttack(ai);
            case PREPARE:
                return checkAtomicActionPrepare(ai);
        }
        return null;
    }

    //TODO ai Review
    private AiEnums.AI_LOGIC_CASE checkAtomicActionAttack(UnitAI ai) {
        return null;
    }


    private AiEnums.AI_LOGIC_CASE checkAtomicActionRetreat(UnitAI ai) {
        // if (ai.getType().isRanged()) {
        //     int threat = getSituationAnalyzer().getMeleeDangerFactor(ai.getUnit(), true, true);
        //     if (ai.checkMod(AI_MODIFIERS.COWARD)) {
        //         threat *= 2;
        //     }
        //     return threat > ai.getUnit().getPower();
        //
        // }
        return null;
    }

    private AiEnums.AI_LOGIC_CASE checkAtomicActionPrepare(UnitAI ai) {
        if (ai.getType() == AI_TYPE.ARCHER) {
            if (ai.getUnit().getRangedWeapon() != null) {
                if (ai.getUnit().getRangedWeapon().getAmmo() == null)
                    return AiEnums.AI_LOGIC_CASE.RELOAD;
            }
        }
        //        if (ai.isMinion())
        //            return false;

        if (!Analyzer.getAdjacentEnemies(getUnit(), false).isEmpty())
            return null;
        VisionEnums.UNIT_VISION v = ai.getUnit().getUnitVisionStatus(Core.getMainHero());
        float dst = v == VisionEnums.UNIT_VISION.BLOCKED ? 3.5f : 6 +
                ai.getUnit().getSightRange();
        if (ai.getType().isCaster())
            dst *= 1.5f;
        if (ai.getType().isRanged())
            dst *= 1.25f;
        if (v != VisionEnums.UNIT_VISION.IN_PLAIN_SIGHT) if (v != VisionEnums.UNIT_VISION.IN_SIGHT) {
            if (PositionMaster.getExactDistance(ai.getUnit(), Core.getMainHero()) > dst)
                return AiEnums.AI_LOGIC_CASE.FAR_UNSEEN;
        }

        boolean criticalOnly = ai.getType() == AI_TYPE.BRUTE;

        List<PARAMS> params = getParamAnalyzer().getRelevantParams(ai.getUnit());
        for (PARAMS p : params) { //only if critical
            if (getParamAnalyzer().checkStatus(
                    criticalOnly
                    , ai.getUnit(), p)) {
                if (getSituationAnalyzer().getDangerFactor(ai.getUnit()) < 50)
                    return AiEnums.AI_LOGIC_CASE.RESTORE;
            }
        }
        return null;
    }

    private int getDistanceForAtomicApproach(UnitAI ai) {
        if (ai.getType().isCaster())
            return new FuncMaster().getGreatestValueEntity(ai.getUnit().getSpells(),
                    PARAMS.RANGE).getIntParam(PARAMS.RANGE);
        if (ai.getType().isRanged()) {
            if (ai.getType() == AI_TYPE.ARCHER)
                if (ai.getUnit().getRangedWeapon() != null)
                    return new FuncMaster().getGreatestValueEntity(ai.getUnit().getRangedWeapon()
                            .getOrCreateAttackActions(), PARAMS.RANGE).getIntParam(PARAMS.RANGE);
        }

        return getConstInt(AiConst.ATOMIC_APPROACH_DEFAULT_DISTANCE);
    }

    //Check if the unit is close enough to JUST GO IN
    public AiEnums.AI_LOGIC_CASE checkAtomicActionApproach(UnitAI ai) {
        if (ai.getType() == AI_TYPE.SNEAK)
            return null; //AI Review
        if (ai.getCurrentOrder() != null)
            if (ai.getCurrentOrder().getStrictPriority() != ORDER_PRIORITY_MODS.ATTACK
                    && ai.getCurrentOrder().getStrictPriority() != ORDER_PRIORITY_MODS.APPROACH)
                return null;
        double maxDistance = getDistanceForAtomicApproach(ai);
        double minDistance = -1;
        for (Unit sub : Analyzer.getVisibleEnemies(ai)) {
            if (game.getVisionMaster().getSightMaster().getClearShotCondition().check(getUnit(), sub)) {
                double distance = PositionMaster.getExactDistance(getUnit(), sub);
                if (distance < maxDistance)
                    minDistance = distance;
            }
        }
        if (minDistance < 0) {
            return null;
        }
        // minDistance -= new Float(Analyzer.getVisibleEnemies(ai).size()) / 2;
        // minDistance += new Float(ai.getGroup().getMembers().size()) / 2;
        // Double average = ai.getGroup().getMembers().stream().collect(
        //         Collectors.averagingInt((t) -> t.getIntParam(PARAMS.POWER)));
        // Integer p = ai.getUnit().getIntParam(PARAMS.POWER);
        // minDistance -= p / average;
        // minDistance += average / p;

        return minDistance > maxDistance ? AiEnums.AI_LOGIC_CASE.APPROACH : null;
    }


    public boolean isHotzoneMode() {
        return hotzoneMode;
    }

    public void setHotzoneMode(boolean hotzoneMode) {
        this.hotzoneMode = hotzoneMode;
    }

    public boolean isOn() {

        if (getUnitAi().getType() == AI_TYPE.ARCHER) {
            return true;
        }
        return on;
    }

    public void setOn(boolean on) {
        this.on = on;
    }


    public enum ATOMIC_LOGIC {
        GEN_AGGRO, GROUP_AGGRO, RETREAT, PROTECT, FORMATION,
    }

    public enum ATOMIC_LOGIC_CASE {
         APPROACH, RETREAT, SEARCH, ATTACK, PREPARE, DOOR(true),
        ;

        boolean forced;

        ATOMIC_LOGIC_CASE() {

        }

        ATOMIC_LOGIC_CASE(boolean forced) {
            this.forced = forced;
        }
    }
}
