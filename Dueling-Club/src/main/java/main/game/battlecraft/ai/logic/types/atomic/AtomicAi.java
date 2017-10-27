package main.game.battlecraft.ai.logic.types.atomic;

import main.content.CONTENT_CONSTS2.AI_MODIFIERS;
import main.content.PARAMS;
import main.content.enums.entity.ActionEnums.ACTION_TYPE;
import main.content.enums.entity.UnitEnums.FACING_SINGLE;
import main.content.enums.system.AiEnums.AI_TYPE;
import main.content.enums.system.AiEnums.ORDER_PRIORITY_MODS;
import main.entity.Ref;
import main.entity.active.DC_ActionManager.STD_MODE_ACTIONS;
import main.entity.active.DC_ActionManager.STD_SPEC_ACTIONS;
import main.entity.item.DC_QuickItemObj;
import main.entity.obj.BattleFieldObject;
import main.entity.obj.unit.Unit;
import main.game.battlecraft.ai.UnitAI;
import main.game.battlecraft.ai.elements.actions.Action;
import main.game.battlecraft.ai.elements.actions.AiActionFactory;
import main.game.battlecraft.ai.elements.generic.AiHandler;
import main.game.battlecraft.ai.elements.generic.AiMaster;
import main.game.battlecraft.ai.elements.task.Task.TASK_DESCRIPTION;
import main.game.battlecraft.ai.tools.Analyzer;
import main.game.battlecraft.ai.tools.ParamAnalyzer;
import main.game.battlecraft.ai.tools.priority.DC_PriorityManager;
import main.game.battlecraft.logic.battlefield.DC_MovementManager;
import main.game.battlecraft.logic.battlefield.FacingMaster;
import main.game.battlecraft.logic.dungeon.universal.Positioner;
import main.game.bf.Coordinates;
import main.game.bf.Coordinates.FACING_DIRECTION;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.data.ListMaster;
import main.system.auxiliary.secondary.BooleanMaster;
import main.system.math.FuncMaster;
import main.system.math.PositionMaster;

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
        if (ai.getType() == AI_TYPE.ARCHER) {
            action = getReloadAction(ai);
            action.setTaskDescription("Ammo Reload");
        }
        if (action != null)
            return action;
        action = getAtomicActionPrepare(ai);
        if (action == null) {
            if (checkAtomicActionTurn(ai)) {
                action = getAtomicActionTurn(ai);
                action.setTaskDescription("Facing Adjustment");
            } else {
                action = getAtomicActionApproach(ai);
                action.setTaskDescription("Approach");
            }
        } else {
            action.setTaskDescription(TASK_DESCRIPTION.RESTORATION.toString());
        }
        return action;
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

        if (ai.getType() == AI_TYPE.CASTER) {
            return AiActionFactory.newAction(STD_MODE_ACTIONS.Meditate.toString(), ai);
        }

        return null;
    }

    public Action getAtomicWait(Unit unit) {
        return AiActionFactory.newAction(unit.getAction(
         STD_SPEC_ACTIONS.Wait.toString()), Ref.getSelfTargetingRefCopy(unit)) ;
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
        if (pick == null) return null;
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
            return null  ;
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
            if (BooleanMaster.isFalse(approach_retreat_search)) {
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
        List<Action> sequence = getTurnSequenceConstructor().getTurnSequence(FACING_SINGLE.IN_FRONT, getUnit(), pick);
        Action action = null;
        if (!sequence.isEmpty()) {
            action = sequence.get(0);
        }
        if (action == null) {
            try {
                action = DC_MovementManager.getFirstAction(unit, pick);
                main.system.auxiliary.log.LogMaster.log(1, " ATOMIC ACTION " + action +
                 " CHOSEN TO GET TO " + pick);
            } catch (Exception e) {
                e.printStackTrace();
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
                e.printStackTrace();
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
        FACING_DIRECTION facing = ai.getUnit().getFacing();
        //check that only enemy actions are needed

        if (ai.getType() == AI_TYPE.BRUTE
         || ai.getType() == AI_TYPE.TANK
         || ai.getType() == AI_TYPE.ARCHER
         || ai.getType() == AI_TYPE.SNEAK
         ) {
//          FacingMaster.getOptimalFacingTowardsUnits()
            for (BattleFieldObject enemy : Analyzer.getVisibleEnemies(ai))
                if (FacingMaster.getSingleFacing(enemy, ai.getUnit()) == FACING_SINGLE.IN_FRONT) {
                    return false;
                }
            return true;
        }

        return false;
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
                return ai.getUnit().getRangedWeapon().getAmmo() == null;
            }
        }
        List<PARAMS> params = getParamAnalyzer().getRelevantParams(ai.getUnit());
        for (PARAMS p : params) { //only if critical
            if (getParamAnalyzer().checkStatus(false, ai.getUnit(), p)) {
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
        int maxDistance = 3;

        if (!ai.getUnit().getActionMap().get(ACTION_TYPE.SPECIAL_MOVE).isEmpty()) {
            maxDistance--;
        }

        if (ai.checkMod(AI_MODIFIERS.TRUE_BRUTE)) {
            maxDistance++;
        }
        if (ai.getType() == AI_TYPE.BRUTE) {
            maxDistance++;
        }

        return maxDistance;
    }

    public boolean checkAtomicActionApproach(UnitAI ai) {
        if (ai.getCurrentOrder() != null)
            if (ai.getCurrentOrder().getStrictPriority() != ORDER_PRIORITY_MODS.ATTACK
             && ai.getCurrentOrder().getStrictPriority() != ORDER_PRIORITY_MODS.APPROACH)
                return false;
        int maxDistance = getDistanceForAtomicApproach(ai);

        int distance = getAnalyzer().getClosestEnemyDistance(ai.getUnit());

        if (distance > maxDistance && distance < 999) {
            return true;
        }
        if (distance <= 2) return false;
        if (ai.getGroup() != null) {
            if (ai.getGroup().getMembers().size() > 8) {
                return true;
            }
        } // check unit is negligible
        Double average = ai.getGroup().getMembers().stream().collect(
         Collectors.averagingInt((t) -> t.getIntParam(PARAMS.POWER)));
        if (ai.getUnit().getIntParam(PARAMS.POWER) < average / 2) {
            return true;
        }

        return false;
    }


    public boolean isHotzoneMode() {
        return hotzoneMode;
    }

    public void setHotzoneMode(boolean hotzoneMode) {
        this.hotzoneMode = hotzoneMode;
    }

    public boolean isOn() {

        if (getUnitAI().getType() == AI_TYPE.ARCHER) {
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
        TURN, APPROACH, RETREAT, SEARCH, ATTACK, PREPARE
    }
}
