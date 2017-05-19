package main.game.battlecraft.ai.logic.types.atomic;

import main.content.CONTENT_CONSTS2.AI_MODIFIERS;
import main.content.PARAMS;
import main.content.enums.entity.ActionEnums.ACTION_TYPE;
import main.content.enums.entity.UnitEnums.FACING_SINGLE;
import main.content.enums.system.AiEnums.AI_TYPE;
import main.content.enums.system.AiEnums.ORDER_PRIORITY_MODS;
import main.entity.Ref;
import main.entity.active.DC_ActionManager.STD_MODE_ACTIONS;
import main.entity.item.DC_QuickItemObj;
import main.entity.obj.BattleFieldObject;
import main.entity.obj.unit.Unit;
import main.game.battlecraft.ai.UnitAI;
import main.game.battlecraft.ai.elements.actions.Action;
import main.game.battlecraft.ai.elements.actions.AiActionFactory;
import main.game.battlecraft.ai.elements.generic.AiHandler;
import main.game.battlecraft.ai.tools.Analyzer;
import main.game.battlecraft.ai.tools.priority.DC_PriorityManager;
import main.game.battlecraft.logic.battlefield.DC_MovementManager;
import main.game.battlecraft.logic.battlefield.FacingMaster;
import main.game.bf.Coordinates;
import main.game.bf.Coordinates.FACING_DIRECTION;
import main.system.auxiliary.secondary.BooleanMaster;
import main.system.math.FuncMaster;
import main.system.math.PositionMaster;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class AtomicAi extends AiHandler {

    private boolean hotzoneMode;

    public AtomicAi(AiHandler master) {
        super(master);
    }

    public Action getAtomicAction(UnitAI ai) {
        Action action = getAtomicActionPrepare(ai);
        if (action == null) {
            if (checkAtomicActionTurn(ai))
                action = getAtomicActionTurn(ai);
            else
                action = getAtomicActionApproach(ai);
        }

        return action;
    }

    private Action getAtomicActionPrepare(UnitAI ai) {
        //ammo
        //meditate
        if (!checkAtomicActionCase(ATOMIC_LOGIC_CASE.PREPARE, ai)) {
            return null;
        }
        if (ai.getType() == AI_TYPE.ARCHER) {
            return getReloadAction(ai);
        }
        if (ai.getType() == AI_TYPE.CASTER) {
            return AiActionFactory.newAction(STD_MODE_ACTIONS.Meditate.toString(), ai);
        }

        return null;
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

    public Action getAtomicActionTurn(UnitAI ai) {

        Coordinates pick =
         getApproachCoordinate(ai);
        if (pick==null )return null ;
        List<Action> sequence = getTurnSequenceConstructor()
         .getTurnSequence(FACING_SINGLE.IN_FRONT, unit, pick);
        return sequence.get(0);

    }

    private Coordinates getApproachCoordinate(UnitAI ai) {
        Collection<Unit> units = getAnalyzer().getVisibleEnemies(ai);
        if (units.isEmpty())
            return null ;
        FACING_DIRECTION facing = FacingMaster.
         getOptimalFacingTowardsUnits(unit.getCoordinates(),
          units
          , t ->getThreatAnalyzer().getThreat(ai, (Unit) t)
         );
        return unit.getCoordinates().getAdjacentCoordinate(facing.getDirection());

    }

    public Action getAtomicActionMove(UnitAI ai, Boolean approach_retreat_search) {
        ATOMIC_LOGIC logic = getAtomicLogic(ai);
        List<Unit> enemies = Analyzer.getVisibleEnemies(ai);
        List<Unit> allies = Analyzer.getAllies(ai);
        float greatest = 0;
        Coordinates pick = null;
        if (isHotzoneMode()) {
            for (Coordinates c : ai.getUnit().getCoordinates().getAdjacentCoordinates()) {
                float i = 0;
                i += getCellPriority(c, ai);
                if (BooleanMaster.isFalse(approach_retreat_search)) {
                    for (Unit a : allies) {
                        i += getAllyPriority(c, a, ai, logic);
                    }
                }
                for (Unit e : enemies) {
                    i = i + ((approach_retreat_search ? 1 : -1) * getEnemyPriority(c, e, ai, logic));
                }
                i = i * getCellPriorityMod(c, ai) / 100;
                if (i > greatest) {
                    greatest = i;
                    pick = c;
                }
            }
        } else {
            pick = getApproachCoordinate(ai);
        }
        Action action = null;
        List<Action> sequence = getTurnSequenceConstructor().getTurnSequence(FACING_SINGLE.IN_FRONT, unit, pick);
        if (!sequence.isEmpty()) {
            action = sequence.get(0);
        }
        if (action == null) {
            try {
                action = DC_MovementManager.getFirstAction(ai.getUnit(), pick);
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

    public boolean checkAtomicActionCaseAny(UnitAI ai) {
//        if (ai.getGroup().getEncounterType()== ENCOUNTER_TYPE.BOSS)
//            return false;
        for (ATOMIC_LOGIC_CASE C : ATOMIC_LOGIC_CASE.values()) {
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
        for (PARAMS p : params) {
            if (getParamAnalyzer().checkStatus(false, ai.getUnit(), p)) {
                return true;
            }
        }
        return false;
    }

    private int getDistanceForAtomicApproach(UnitAI ai) {

        if (ai.getType().isRanged()) {
            if (ai.getType() == AI_TYPE.ARCHER)
                return new FuncMaster().getGreatestValue(ai.getUnit().getRangedWeapon()
                 .getAttackActions(), PARAMS.RANGE).getIntParam(PARAMS.RANGE);
            return new FuncMaster().getGreatestValue(ai.getUnit().getSpells(),
             PARAMS.RANGE).getIntParam(PARAMS.RANGE);
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
        if (ai.getCurrentOrder()!=null )
            if (ai.getCurrentOrder().getStrictPriority()!= ORDER_PRIORITY_MODS.ATTACK
        && ai.getCurrentOrder().getStrictPriority()!= ORDER_PRIORITY_MODS.APPROACH)
                return false;
        int maxDistance = getDistanceForAtomicApproach(ai);

        int distance = getAnalyzer().getClosestEnemyDistance(ai.getUnit());

        if (distance > maxDistance && distance < 999) {
            return true;
        }
        if (ai.getGroup() != null) {
            if (ai.getGroup().getMembers().size() > 8) {
                return true;
            }
        }
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

    public enum ATOMIC_LOGIC {
        GEN_AGGRO, GROUP_AGGRO, RETREAT, PROTECT, FORMATION,
    }

    public enum ATOMIC_LOGIC_CASE {
        TURN, APPROACH, RETREAT, SEARCH, ATTACK, PREPARE
    }
}
