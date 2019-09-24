package eidolons.game.battlecraft.ai.logic.atomic;

import eidolons.content.PARAMS;
import eidolons.entity.active.DC_ActionManager.STD_MODE_ACTIONS;
import eidolons.entity.active.DC_ActionManager.STD_SPEC_ACTIONS;
import eidolons.entity.active.DC_UnitAction;
import eidolons.entity.item.DC_QuickItemObj;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.ai.UnitAI;
import eidolons.game.battlecraft.ai.advanced.machine.AiConst;
import eidolons.game.battlecraft.ai.elements.actions.Action;
import eidolons.game.battlecraft.ai.elements.actions.AiActionFactory;
import eidolons.game.battlecraft.ai.elements.generic.AiHandler;
import eidolons.game.battlecraft.ai.elements.generic.AiMaster;
import eidolons.game.battlecraft.ai.elements.task.Task.TASK_DESCRIPTION;
import eidolons.game.battlecraft.ai.tools.Analyzer;
import eidolons.game.battlecraft.ai.tools.ParamAnalyzer;
import eidolons.game.battlecraft.ai.tools.priority.DC_PriorityManager;
import eidolons.game.battlecraft.logic.battlefield.DC_MovementManager;
import eidolons.game.battlecraft.logic.battlefield.FacingMaster;
import eidolons.game.battlecraft.logic.dungeon.universal.Positioner;
import eidolons.game.battlecraft.rules.action.StackingRule;
import eidolons.game.core.Eidolons;
import eidolons.game.module.dungeoncrawl.objects.Door;
import eidolons.game.module.dungeoncrawl.objects.DoorMaster.DOOR_ACTION;
import eidolons.game.module.dungeoncrawl.objects.DoorMaster.DOOR_STATE;
import eidolons.game.module.dungeoncrawl.objects.DungeonObj.DUNGEON_OBJ_TYPE;
import main.content.CONTENT_CONSTS2.AI_MODIFIERS;
import main.content.enums.entity.UnitEnums.FACING_SINGLE;
import main.content.enums.entity.UnitEnums.STANDARD_PASSIVES;
import main.content.enums.rules.VisionEnums;
import main.content.enums.system.AiEnums.AI_TYPE;
import main.content.enums.system.AiEnums.ORDER_PRIORITY_MODS;
import main.entity.Ref;
import main.game.bf.Coordinates;
import main.game.bf.directions.FACING_DIRECTION;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.data.ListMaster;
import main.system.auxiliary.log.FileLogger.SPECIAL_LOG;
import main.system.auxiliary.log.SpecialLogger;
import main.system.auxiliary.secondary.Bools;
import main.system.math.FuncMaster;
import main.system.math.PositionMaster;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class AtomicAi extends AiHandler {

    private boolean hotzoneMode;
    private boolean on = true;

    public AtomicAi(AiMaster master) {
        super(master);
    }

    public Action getAtomicAction(UnitAI ai) {
        Action action = null;
        if (checkAtomicActionTurn(ai)) {
            action = getAtomicActionTurn(ai);
            if (action != null) {
                action.setTaskDescription("Facing Adjustment");
                return action;
            }
        }
        action = getAtomicActionPrepare(ai);
        if (action != null) {
            action.setTaskDescription(TASK_DESCRIPTION.RESTORATION.toString());
            return action;
        }
        if (checkAtomicActionApproach(ai))
            action = getAtomicActionApproach(ai);
        if (action != null)
            action.setTaskDescription("Approach");
        else {
            SpecialLogger.getInstance().appendSpecialLog(SPECIAL_LOG.AI, getUnit() + " finds no atomic action!");
            return null;
        }
        String message = getUnit() + " chooses atomic action: " + action;
        SpecialLogger.getInstance().appendSpecialLog(SPECIAL_LOG.AI, message);
        return action;
    }

    public Action getAtomicActionForced(UnitAI ai) {
        Action action = null;
        action = getAtomicActionDoor(ai);
        if (action != null)
            action.setTaskDescription("Door");

        return action;
    }

    public Action getAtomicActionDoor(UnitAI ai) {

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
            if (FacingMaster.getSingleFacing(ai.getUnit().getFacing(), coordinates, c) != FACING_SINGLE.IN_FRONT) {
                continue;
            }
            DOOR_STATE state = game.getBattleFieldManager().getDoorMap().get(c);
            if (state == DOOR_STATE.CLOSED) {
                return c;
            }
        }
        return null;
    }

    public Action getAtomicActionPrepare(UnitAI ai) {
        //ammo
        //meditate
        if (!checkAtomicActionCase(ATOMIC_LOGIC_CASE.PREPARE, ai)) {
            return null;
        }
        if (ParamAnalyzer.isFatigued(getUnit())) {
            return AiActionFactory.newAction(STD_MODE_ACTIONS.Rest.name(),
                    getUnit().getAI());
        }
        if (ParamAnalyzer.isHazed(getUnit())) {
            return AiActionFactory.newAction(STD_MODE_ACTIONS.Concentrate.name(),
                    getUnit().getAI());
        }

        if (ai.getType() == AI_TYPE.ARCHER)
            if (ai.getUnit().getRangedWeapon() != null)
                if (ai.getUnit().getRangedWeapon().getAmmo() == null) {
                    Action action = getReloadAction(ai);
                    if (action != null) {
                        action.setTaskDescription("Ammo Reload");
                        return action;
                    }
                }
        if (ai.getType() == AI_TYPE.CASTER) {
            return AiActionFactory.newAction(STD_MODE_ACTIONS.Meditate.toString(), ai);
        }

        return AiActionFactory.newAction(
                RandomWizard.random() ? RandomWizard.random() ?
                        STD_SPEC_ACTIONS.Wait.toString() : RandomWizard.random() ?
                        STD_SPEC_ACTIONS.Wait.toString() :
                        STD_MODE_ACTIONS.Defend.toString() : RandomWizard.random() ?
                        STD_SPEC_ACTIONS.Wait.toString() :
                        STD_MODE_ACTIONS.On_Alert.toString(), ai);
    }

    public Action getAtomicWait(Unit unit) {
        return AiActionFactory.newAction(unit.getAction(
                STD_SPEC_ACTIONS.Wait.toString()), Ref.getSelfTargetingRefCopy(unit));
    }

    private Action getReloadAction(UnitAI ai) {
        DC_QuickItemObj ammo = null;
        Integer maxCost = 0;
        for (DC_QuickItemObj a : ai.getUnit().getQuickItems()) {
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

    public Action getAtomicActionApproach(UnitAI ai) {
        return getAtomicActionMove(ai, true);
    }

    public Action getAtomicActionAttack(UnitAI ai) {
        return null;
    }

    public Action getAtomicActionTurn(UnitAI ai) {
        Coordinates pick =
                getApproachCoordinate(ai);
        if (pick == null) { //TODO igg demo fix
            return AiActionFactory.newAction(RandomWizard.random() ?
                    "Turn Clockwise" : "Turn Anticlockwise", ai);
        }
        List<Action> sequence = getTurnSequenceConstructor()
                .getTurnSequence(FACING_SINGLE.IN_FRONT, getUnit(), pick);
        if (ListMaster.isNotEmpty(sequence))
            return sequence.get(0);
        return null;
    }

    private Coordinates getApproachCoordinate(UnitAI ai) {
        Collection<Unit> units = getAnalyzer().getVisibleEnemies(ai);
        if (units.isEmpty())
            return null;
        FACING_DIRECTION facing = FacingMaster.
                getOptimalFacingTowardsUnits(getUnit().getCoordinates(),
                        units
                        , t -> getThreatAnalyzer().getThreat(ai, (Unit) t)
                );
        if (facing == null)
            return null;
        Coordinates c = getUnit().getCoordinates().getAdjacentCoordinate(facing.getDirection());

        if (new StackingRule(game).canBeMovedOnto(getUnit(), c)){
            return c;
        }
        return Positioner.adjustCoordinate(ai.getUnit(), c, ai.getUnit().getFacing());

    }

    public Action getAtomicActionMove(UnitAI ai, Boolean approach_retreat_search) {
        Coordinates pick = null;
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

        Action a = getAtomicMove(pick, ai.getUnit());
        return a;
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

    public Action getAtomicMove(Coordinates pick, Unit unit) {
        List<Action> sequence = getTurnSequenceConstructor().getTurnSequence(FACING_SINGLE.IN_FRONT, unit, pick);
        Action action = null;
        if (!sequence.isEmpty()) {
            action = sequence.get(0);
        }
        if (action == null) {
            try {
                action = DC_MovementManager.getFirstAction(unit, pick);
//                main.system.auxiliary.log.LogMaster.log(1, " ATOMIC ACTION " + action +
//                 "  CHOSEN TO GET TO " + pick);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
                //TODO what to return???
            }
        }
        return action;
    }

    private int getCellPriority(Coordinates c, UnitAI ai) {
        return 0;
    }

    private int getCellPriorityMod(Coordinates c, UnitAI ai) {
        int mod = 100;
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
        return mod;
    }

    private float getEnemyPriority(Coordinates c, Unit e, UnitAI ai, ATOMIC_LOGIC logic) {
        if (logic == ATOMIC_LOGIC.GEN_AGGRO || logic == ATOMIC_LOGIC.GROUP_AGGRO) {
            return new Float(DC_PriorityManager.getUnitPriority(ai, e, null))
                    / (1 + PositionMaster.getDistance(e.getCoordinates(), c));
        }
        return 0;
    }

    private float getAllyPriority(Coordinates c, Unit a, UnitAI ai, ATOMIC_LOGIC logic) {
        if (logic == ATOMIC_LOGIC.PROTECT || logic == ATOMIC_LOGIC.FORMATION) {
            return new Float(DC_PriorityManager.getUnitPriority(ai, a, null))
                    / (1 + PositionMaster.getDistance(a.getCoordinates(), c));
        }
        return 0;
    }

    private ATOMIC_LOGIC getAtomicLogic(UnitAI ai) {
        return ATOMIC_LOGIC.GEN_AGGRO;
    }

    public boolean checkAtomicActionRequired(UnitAI ai) {

        return checkAtomicActionCaseAny(ai); //also check power, danger, distance
    }

    public boolean checkAtomicActionCaseAny(UnitAI ai) {
        boolean canAttack = getSituationAnalyzer().canAttackNow(ai);
        for (ATOMIC_LOGIC_CASE C : ATOMIC_LOGIC_CASE.values()) {
            if (C != ATOMIC_LOGIC_CASE.PREPARE)
                if (canAttack)
                    continue;
            try {
                if (checkAtomicActionCase(C, ai)) {
                    return true;
                }
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        }
        return false;
    }

    public boolean checkAtomicActionCase(ATOMIC_LOGIC_CASE CASE, UnitAI ai) {

        switch (CASE) {
            case TURN:
                return checkAtomicActionTurn(ai);
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
        return false;
    }

    private boolean checkAtomicActionTurn(UnitAI ai) {
        //check that only enemy actions are needed
        boolean result = false;
        if (!ai.getType().isCaster()
        ) {
//          FacingMaster.getOptimalFacingTowardsUnits()
            BattleFieldObject enemy = getAnalyzer().getClosestEnemy(ai.getUnit());
//            for (BattleFieldObject enemy : Analyzer.getVisibleEnemies(ai))
            FACING_DIRECTION facing = ai.getUnit().getFacing();
            FACING_SINGLE relative = FacingMaster.getSingleFacing(facing, ai.getUnit(), enemy);
            if (relative == FACING_SINGLE.BEHIND) {
                return true;
            } else if (relative == FACING_SINGLE.TO_THE_SIDE) {
                if (!ai.getUnit().checkPassive(STANDARD_PASSIVES.BROAD_REACH))
                    result = true;
            } else
                result = false;
            //if we need to getVar to a cell that is not 'facing' the target?!
            if (!result)
                return false;
            if (!game.getVisionMaster().getSightMaster().getClearShotCondition().check(getUnit(), enemy))
                return false;
        }
        return result;
    }

    //TODO
    private boolean checkAtomicActionAttack(UnitAI ai) {
        return false;
    }


    private boolean checkAtomicActionRetreat(UnitAI ai) {
        if (ai.getType().isRanged()) {
            int threat = getSituationAnalyzer().getMeleeDangerFactor(ai.getUnit(), true, true);
            if (ai.checkMod(AI_MODIFIERS.COWARD)) {
                threat *= 2;
            }
            if (threat > ai.getUnit().calculatePower()) {
                return true;
            }

        }
        return false;
    }

    private boolean checkAtomicActionPrepare(UnitAI ai) {
        if (ai.getType() == AI_TYPE.ARCHER) {
            if (ai.getUnit().getRangedWeapon() != null) {
                if (ai.getUnit().getRangedWeapon().getAmmo() == null)
                    return true;
            }
        }
//        if (ai.isMinion())
//            return false;

        if (!Analyzer.getAdjacentEnemies(getUnit(), false).isEmpty())
            return false;
        VisionEnums.UNIT_VISION v = ai.getUnit().getUnitVisionStatus(Eidolons.getMainHero());
        float dst = v == VisionEnums.UNIT_VISION.BLOCKED ? 3.5f : 6 +
                ai.getUnit().getSightRangeTowards(Eidolons.getMainHero());
        if (ai.getType().isCaster())
            dst *= 1.5f;
        if (ai.getType().isRanged())
            dst *= 1.25f;
        if (v != VisionEnums.UNIT_VISION.IN_PLAIN_SIGHT) if (v != VisionEnums.UNIT_VISION.IN_SIGHT) {
            if (PositionMaster.getExactDistance(ai.getUnit(), Eidolons.getMainHero()) > dst) {
//           TODO      game.getVisionMaster().getVisionController().getPlayerVisionMapper().getVar(ai.getUnit().getOwner(),
//                        Eidolons.getMainHero());
//                game.getVisionMaster().getVisionController().getDetectionMapper().getVar(ai.getUnit().getOwner(),
//                        Eidolons.getMainHero());
                return true;
            }
        }

        boolean criticalOnly = ai.getType() == AI_TYPE.BRUTE;

        List<PARAMS> params = getParamAnalyzer().getRelevantParams(ai.getUnit());
        for (PARAMS p : params) { //only if critical

            if (getParamAnalyzer().checkStatus(
                    criticalOnly
                    , ai.getUnit(), p)) {
                if (getSituationAnalyzer().getDangerFactor(ai.getUnit()) < 50)
                    return true;
            }
        }
        return false;
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
        int maxDistance = getConstInt(AiConst.ATOMIC_APPROACH_DEFAULT_DISTANCE);
//if only one enemy
        // if unit is weak

//        if (!ai.getUnit().getActionMap().getVar(ACTION_TYPE.SPECIAL_MOVE).isEmpty()) {
//            maxDistance--;
//        }
//
//        if (ai.checkMod(AI_MODIFIERS.TRUE_BRUTE)) {
//            maxDistance++;
//        }
//        if (ai.getType() == AI_TYPE.BRUTE) {
//            maxDistance++;
//        }

        return maxDistance;
    }

    public boolean checkAtomicActionApproach(UnitAI ai) {
        if (ai.getCurrentOrder() != null)
            if (ai.getCurrentOrder().getStrictPriority() != ORDER_PRIORITY_MODS.ATTACK
                    && ai.getCurrentOrder().getStrictPriority() != ORDER_PRIORITY_MODS.APPROACH)
                return false;
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
            return false;
        }

        minDistance -= new Float(Analyzer.getVisibleEnemies(ai).size()) / 2;
        minDistance += new Float(ai.getGroup().getMembers().size()) / 2;

        Double average = ai.getGroup().getMembers().stream().collect(
                Collectors.averagingInt((t) -> t.getIntParam(PARAMS.POWER)));
        Integer p = ai.getUnit().getIntParam(PARAMS.POWER);
        minDistance -= p / average;
        minDistance += average / p;


        return minDistance > maxDistance;
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
        TURN, APPROACH, RETREAT, SEARCH, ATTACK, PREPARE, DOOR(true),
        ;

        boolean forced;

        ATOMIC_LOGIC_CASE() {

        }

        ATOMIC_LOGIC_CASE(boolean forced) {
            this.forced = forced;
        }
    }
}
