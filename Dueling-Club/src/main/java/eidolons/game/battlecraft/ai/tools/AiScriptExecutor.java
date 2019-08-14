package eidolons.game.battlecraft.ai.tools;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.ai.UnitAI;
import eidolons.game.battlecraft.ai.advanced.companion.Order;
import eidolons.game.battlecraft.ai.elements.actions.Action;
import eidolons.game.battlecraft.ai.elements.actions.AiActionFactory;
import eidolons.game.battlecraft.ai.elements.actions.sequence.ActionSequence;
import eidolons.game.battlecraft.ai.elements.generic.AiHandler;
import eidolons.game.battlecraft.ai.elements.generic.AiMaster;
import eidolons.game.battlecraft.ai.elements.task.Task;
import eidolons.game.battlecraft.ai.tools.path.ActionPath;
import eidolons.game.battlecraft.logic.battle.mission.CombatScriptExecutor.COMBAT_SCRIPT_FUNCTION;
import eidolons.game.battlecraft.logic.meta.scenario.script.ScriptExecutor;
import eidolons.game.core.Eidolons;
import main.content.enums.entity.UnitEnums.FACING_SINGLE;
import main.content.enums.system.AiEnums.GOAL_TYPE;
import main.data.DataManager;
import main.elements.targeting.SelectiveTargeting;
import main.entity.Ref;
import main.game.bf.Coordinates;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.data.ArrayMaster;

/**
 * Created by JustMe on 5/21/2017.
 */
public class AiScriptExecutor extends AiHandler implements ScriptExecutor<COMBAT_SCRIPT_FUNCTION> {


    public AiScriptExecutor(AiMaster aiMaster) {
        super(aiMaster);
    }

    @Override
    public String getSeparator(COMBAT_SCRIPT_FUNCTION func) {
        return null;
    }

    public Unit findUnit(Ref ref, String unitData) {
        Unit unit = (Unit) ref.getObj(unitData);
        if (unit == null) {
            AI_ARG arg = new EnumMaster<AI_ARG>().retrieveEnumConst(AI_ARG.class, unitData);
            if (arg != null) {
                unit = getUnit(arg);
            }
            if (unit == null) {
                String name = unitData;
                if (!DataManager.isTypeName(name))
                    name = null;

                Boolean power = null;// getPower(arg);
                Boolean distance =true; // getDistance(arg);
                Boolean ownership =false; // getOwnership(arg);
                unit = (Unit) //TODO
                        getGame().getMaster().getByName(name, ref,
                        ownership, distance, power);
            }
        }
        return unit;
    }

    @Override
    public boolean execute(COMBAT_SCRIPT_FUNCTION function, Ref ref, String... args) {
        if (args[0].equalsIgnoreCase("group")) {

        }
        int i = 0;
        String unitData = args[0];
        Unit unit = findUnit(ref, unitData);
//        i++;
        //group?
        //with frozen gameLoop?

        String arg = null;
        boolean free = true;
        boolean immediate = false;
//        if (args.length > i) {
//            String options = args[i + 1];
//            i++;
////            if (StringMaster.contains(options, FREE)
//            free = true;
//            immediate = true;
//            arg = options;
//        }
        String[] additionalArgs = null;
//        if (args.length > i) {
//            additionalArgs = new String[args.length - i];
//            for (int j = 0; j < args.length; j++) {
//                additionalArgs[j] = args[i + j];
//            }
//        }
        if (arg == null) {
            arg = args[1];
        }
        if (unit == null) {
            unit = (Unit) ref.getSourceObj();
        }
        executeCommand(unit, function, arg, free, immediate, additionalArgs);

        return true;
    }

    private void executeCommand(Unit unit, COMBAT_SCRIPT_FUNCTION function, String arg,
                                boolean free, boolean immediate, String... args) {
        ActionSequence sequence = null;
        GOAL_TYPE goal = getGoalType(function);
        if (unit == null) {
            unit = findUnit(Eidolons.getMainHero().getRef(), arg);
        }
        Task task = new Task(true, unit.getAI(), goal, arg);
        UnitAI ai = unit.getAI();
        switch (function) {
            case MOVE_TO:
                //via a path!
                ActionPath path = getPathSequenceConstructor().getOptimalPathSequence(unit.getAI(),
                        Coordinates.get(arg.toString()));
                sequence = new ActionSequence(path.getActions(), task, unit.getAI());
                break;
            case TURN_TO:
                //cell id
                sequence = new ActionSequence(
                        getTurnSequenceConstructor().
                                getTurnSequence(FACING_SINGLE.IN_FRONT, unit,
                                        Coordinates.get(arg.toString())), task, unit.getAI());
                break;
            case ACTION:
                Action action = AiActionFactory.newAction(arg.toString(), ai);
                sequence = //new ActionSequence();
                        getActionSequenceConstructor().constructSingleActionSequence(action,
                                new Task(ai, goal, !ArrayMaster.isNotEmpty(args) ? null : args[0]));
                break;
            case ATTACK:

                break;
            case FREEZE:
                break;
            case UNFREEZE:
                break;
            case ORDER:
                Order a = //OrderFactory.getOrder();
                        new Order(arg.toString());
                unit.getAI().setCurrentOrder(a);
                return;
        }
        if (immediate) {
            unit.getAI().setStandingOrders(sequence);
            unit.getAI().setFree(free);
        } else {
            if (sequence == null) {
                return;
            }

            Unit finalUnit = unit;
            sequence.getActions().forEach(
                    //TODO wait?
                    action -> {
                        if (action.getTargeting() instanceof SelectiveTargeting) {
                            if (action.getTarget()==null) {
                                action.getRef().setTarget(selectTarget(finalUnit,function, action, args));
                            }
                        }
                        getExecutor().execute(action, free);
                    });
        }

    }

    private Integer selectTarget(Unit unit, COMBAT_SCRIPT_FUNCTION function, Action action, String[] args) {
//        TODO other cases?
        return getAnalyzer().getClosestEnemy(unit).getId();
    }

    private GOAL_TYPE getGoalType(COMBAT_SCRIPT_FUNCTION function) {
        switch (function) {
            case ACTION:
                break;
            case ATTACK:
                return GOAL_TYPE.ATTACK;
        }
        return GOAL_TYPE.MOVE;
    }

    private Boolean getOwnership(AI_ARG arg) {
        switch (arg) {
            case CLOSEST_ALLY:
            case RANDOM_ALLY:
                return true;
            case CLOSEST_ENEMY:
            case RANDOM_ENEMY:
                return false;
        }
        return null;
    }

    private Boolean getDistance(AI_ARG arg) {
        switch (arg) {
            case CLOSEST_ALLY:
            case CLOSEST_ENEMY:
                return true;
        }
        return null;
    }

    private Unit getUnit(AI_ARG arg) {
        switch (arg) {
            case BOSS:
            case AI_LEADER:
            case MAIN_HERO:

            case CLOSEST_ENEMY:
                return getAnalyzer().getClosestEnemy(Eidolons.getMainHero());
        }
        return null;
    }

    public enum AI_ARG {
        CLOSEST_ENEMY,
        CLOSEST_ALLY,
        BOSS,
        MAIN_HERO,
        AI_LEADER,
        RANDOM_ENEMY,
        RANDOM_ALLY,
    }


}
