package eidolons.game.battlecraft.ai.tools;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Obj;
import eidolons.entity.unit.Unit;
import eidolons.game.battlecraft.ai.UnitAI;
import eidolons.game.battlecraft.ai.advanced.companion.Order;
import eidolons.game.battlecraft.ai.elements.actions.AiAction;
import eidolons.game.battlecraft.ai.elements.actions.AiActionFactory;
import eidolons.game.battlecraft.ai.elements.actions.sequence.ActionSequence;
import eidolons.game.battlecraft.ai.elements.generic.AiHandler;
import eidolons.game.battlecraft.ai.elements.generic.AiMaster;
import eidolons.game.battlecraft.ai.elements.task.Task;
import eidolons.game.battlecraft.ai.tools.path.ActionPath;
import eidolons.game.battlecraft.logic.meta.scenario.script.ScriptExecutor;
import eidolons.game.battlecraft.logic.mission.quest.CombatScriptExecutor.COMBAT_SCRIPT_FUNCTION;
import eidolons.game.core.Core;
import eidolons.game.exploration.story.cinematic.Cinematics;
import main.content.enums.system.AiEnums.GOAL_TYPE;
import main.data.DataManager;
import main.elements.targeting.SelectiveTargeting;
import main.entity.Ref;
import main.game.bf.Coordinates;
import main.system.auxiliary.EnumMaster;

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

    public BattleFieldObject findUnit(Ref ref, String unitData) {
        BattleFieldObject unit = (BattleFieldObject) ref.getObj(unitData);
        if (unit == null) {
            AI_ARG arg = new EnumMaster<AI_ARG>().retrieveEnumConst(AI_ARG.class, unitData);
            if (arg != null) {
                unit = getUnit(arg);
            }
            if (unit == null) {
                if (!DataManager.isTypeName(unitData)) {
                    return null;
                }

                Boolean power = null;// getPower(arg);
                Boolean distance = true; // getDistance(arg);
                Boolean ownership = false; // getOwnership(arg);
                unit = getGame().getObjMaster().getByName(unitData, ref,
                        ownership, distance, power);
            }
        }
        return unit;
    }

    @Override
    public boolean execute(COMBAT_SCRIPT_FUNCTION function, Ref ref, Object... args) {
        String unitData = args[0].toString();
        BattleFieldObject unit = findUnit(ref, unitData);

        String arg = null;
        boolean free = Cinematics.ON;
        boolean immediate = false;
//        String[] additionalArgs = new String[]{
//                args[0].toString() //TODO fix this
//        };
        if (arg == null) {
            arg = args[0].toString();
        }
        if (unit == null) {
            unit = (BattleFieldObject) ref.getSourceObj();
        }
        if (unit instanceof Unit) {
            executeCommand((Unit) unit, function, arg, free, immediate, args);
        }
        return true;
    }

    private void executeCommand(Unit unit, COMBAT_SCRIPT_FUNCTION function, String arg,
                                boolean free, boolean immediate, Object... args) {
        ActionSequence sequence = null;
        GOAL_TYPE goal = getGoalType(function);
        if (unit == null) {
            unit = (Unit) findUnit(Core.getMainHero().getRef(), arg);
        }
        Task task = new Task(true, unit.getAI(), goal, arg);
        UnitAI ai = unit.getAI();
        switch (function) {
            case MOVE_TO:
                //via a path!
                ActionPath path = getPathSequenceConstructor().getOptimalPathSequence(unit.getAI(),
                        Coordinates.get(arg));
                sequence = new ActionSequence(path.getActions(), task, unit.getAI());
                break;
            case ACTION:
            case ORDER:
                if (args.length > 1) {
                    if (args[1] != null) {
                        arg = args[1].toString();
                    }
                }
                AiAction aiAction = AiActionFactory.newAction(arg, ai);
                sequence = //new ActionSequence();
                        getActionSequenceConstructor().constructSingleActionSequence(aiAction,
                                new Task(ai, goal, null), true); //TODO target?

                if (aiAction.getTargeting() instanceof SelectiveTargeting) {
                    if (aiAction.getTarget() == null) {
                        aiAction.getRef().setTarget(selectTarget(
                                unit, function, aiAction, args));
                    }
                }

                if (function != COMBAT_SCRIPT_FUNCTION.ORDER) {
                    break;
                }
                Order a = //OrderFactory.getOrder();
                        new Order(arg);
                unit.getAI().setCurrentOrder(a);
                unit.getAI().setStandingOrders(sequence);
                return;
            case ATTACK:
            case UNFREEZE:
            case FREEZE:

                break;
        }
        if (immediate) {
            unit.getAI().setStandingOrders(sequence);
            unit.getAI().setFree(free);
        } else {
            if (sequence == null) {
                return;
            }
            sequence.getActions().forEach(
                    //TODO wait?
                    action -> {
//                        if (action.getTargeting() instanceof SelectiveTargeting) {
//                            if (action.getTarget() == null) {
//                                action.getRef().setTarget(selectTarget(
//                                        finalUnit, function, action, args));
//                            }
//                        }
//                        if (!ExplorationMaster.isExplorationOn()) {
//                            getGame().getLoop().actionInput(new ActionInput(action.getActive(),
//                                    new Context(action.getRef())));
//                        } else
                            getExecutor().execute(action, free, false);
                    });
        }

    }

    private Integer selectTarget(Unit unit, COMBAT_SCRIPT_FUNCTION function, AiAction aiAction, Object[] args) {
//        TODO other cases?
        if (aiAction.getActive().isMove()) {
            //string >> find unit
            for (Object arg : args) {
                if (arg instanceof DC_Obj) {
                    return ((DC_Obj) arg).getId();
                }
                if (arg instanceof Coordinates) {
                    return getGame().getCell((Coordinates) arg).getId();
                }

            }
        }
        if (args.length > 2) {
            BattleFieldObject target;
            if (args[2] instanceof BattleFieldObject) {
                target = (BattleFieldObject) args[2];
            } else
                target =  findUnit(unit.getRef(), args[2].toString());
            if (target != null) {
                return target.getId();
            }
        }
        return getAnalyzer().getClosestEnemy(unit).getId();
    }

    private GOAL_TYPE getGoalType(COMBAT_SCRIPT_FUNCTION function) {
        switch (function) {
            case ACTION:
                break;
            case ATTACK:
                return GOAL_TYPE.ATTACK;
        }
        return GOAL_TYPE.SUMMONING;
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
                return getAnalyzer().getClosestEnemy(Core.getMainHero());
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
