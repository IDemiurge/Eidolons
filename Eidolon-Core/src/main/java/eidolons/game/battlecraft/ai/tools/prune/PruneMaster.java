package eidolons.game.battlecraft.ai.tools.prune;

import eidolons.content.PARAMS;
import eidolons.entity.feat.active.ActiveObj;
import eidolons.entity.feat.active.Spell;
import eidolons.entity.obj.DC_Obj;
import eidolons.entity.unit.Unit;
import eidolons.game.battlecraft.ai.UnitAI;
import eidolons.game.battlecraft.ai.advanced.machine.AiConst;
import eidolons.game.battlecraft.ai.elements.actions.AiAction;
import eidolons.game.battlecraft.ai.elements.generic.AiHandler;
import eidolons.game.battlecraft.ai.elements.generic.AiMaster;
import eidolons.game.battlecraft.ai.elements.goal.GoalManager;
import eidolons.game.battlecraft.ai.tools.ParamAnalyzer;
import eidolons.game.battlecraft.ai.tools.priority.DC_PriorityManager;
import main.content.CONTENT_CONSTS2.AI_MODIFIERS;
import main.content.enums.system.AiEnums.GOAL_TYPE;
import main.game.bf.Coordinates;
import main.system.SortMaster;
import main.system.auxiliary.log.LOG_CHANNEL;
import main.system.math.PositionMaster;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by JustMe on 3/3/2017.
 */
public class PruneMaster extends AiHandler {
    // include non-targeted actions as well, zone and so on

    public PruneMaster(AiMaster master) {
        super(master);
    }

    public List<Coordinates> pruneTargetCells(AiAction targetAiAction, Collection<Coordinates> list) {
        TreeMap<Integer, Coordinates> map = new TreeMap<>(SortMaster
         .getNaturalIntegerComparator(false));

        Coordinates coordinates = targetAiAction.getSource().getCoordinates();
        for (Coordinates c : list) {
            int distance = 10 * PositionMaster.getDistance(coordinates, c);
            if (!PositionMaster.inLine(c, coordinates)) {
                distance += 5;
            }
            if (PositionMaster.inLineDiagonally(c, coordinates)) {
                distance += 2;
            }
            map.put(distance, c); //TODO KEYS WILL OVERLAP!
        }
        // if (distance<minDistance)
        // minDistance=distance;
        // }

        for (int i = map.size() - getConstInt(AiConst.DEFAULT_PRUNE_SIZE); i > 0; i--) {
            map.remove(map.lastKey());
        }

        // int factor=defaultDistancePruneFactor;
        // while (factor>1)
        // for (Coordinates c :list)
        // {
        // int distance =
        // PositionMaster.getDistance(targetAction.getSource().getCoordinates(),
        // c);
        // if (distance>factor+minDistance)
        // continue;
        // prunedList.add(c);
        // }
        return new ArrayList<>(map.values());
    }

    public void pruneTargetsForAction(List<? extends DC_Obj> targets, GOAL_TYPE goal, UnitAI ai,
                                      ActiveObj action) {
        /*
         * cache for each goal?
		 *
		 * the 'pruneSize' must be a minimum of targets to prune... and beyond
		 * that, there should be some default prune logic
		 */
        for (DC_Obj e : targets) {
            if (!(e instanceof Unit)) {
                return; // another method?
            }
        }
        int size = targets.size();
        int toPrune = size - 1;//getPruneSize(goal);
        if (toPrune <= 0) // this is only the max size, how to ensure pruning of
        // 'valid' targets too?
        {
            if (action.isRanged() || action instanceof Spell) {
                return; // TODO sometimes it's not the size, but the distance!
            }
        }
        // for melee...
        Boolean byCapacity = true;
        Boolean byHealth = true;
        Boolean byDistance = true;
        Boolean byDanger = false;
        Boolean byPower = true;
        Boolean byType = false;
        switch (goal) {
            case RESTORE:
                byHealth = false;
                break;
            case ATTACK:
                byHealth = false;
                break;
            case CUSTOM_HOSTILE:
            case CUSTOM_SUPPORT:

            case BUFF:
            case DEBILITATE:
                targets.removeIf(t -> t.getBuff(action.getName()) != null);
                break;
        }
        Boolean enemy = GoalManager.isGoalVsEnemies(goal);
        int minDistance =
         //TODO for ALLIES?
         getAnalyzer().getClosestEnemyDistance(ai.getUnit());
        List<DC_Obj> pruneList = new ArrayList<>();
        // TODO sort() first? to be sure to cut off the tail...
        int maxPower = ParamAnalyzer.getMaxParam(PARAMS.POWER, new ArrayList<>(targets));
        int limit = 0;
        boolean first = true;
        pruneLoop:
        while ((first || pruneList.size() < toPrune) && limit < 5) {
            first = false;
            for (DC_Obj t : targets) {
                if (pruneList.contains(t)) {
                    continue;
                }
                boolean result = false;
                while (true) {
                    if (byDistance) {
                        int distance = PositionMaster.getDistance(t, ai.getUnit());
//                        if (distance < minDistance) {
//                            minDistance = distance;
//                        } else {
                        result = (getDistancePruneFactor(limit, t, ai, action))
                         < distance
                         - minDistance;
//                        }
                        if (result) {
                            break;
                        }
                    }

                    if (byCapacity) {
                        float capacity = DC_PriorityManager.getCapacity((Unit) t);
                        // < 0.1f

                        if (result) {
                            break;
                        }
                    }

                    if (byHealth) {
                        int health = DC_PriorityManager.getHealthFactor(t, byHealth);// ?
                        result = (health < getHeathPruneFactor(limit, t, ai, action));
                        if (result) {
                            break;
                        }
                    }
                    if (byPower) {
                        result = t.getIntParam(PARAMS.POWER) * 100 / maxPower < getPowerFactor(
                         limit, ai, action);
                        if (result) {
                            break;
                        }
                    }
                    // by danger
                    break;
                }
                if (result) {
                    pruneList.add(t);
                    continue pruneLoop;
                }

            }
            // if nobody was pruned, increase limits
            limit++;
        }
        main.system.auxiliary.log.LogMaster.log(LOG_CHANNEL.AI_DEBUG, "PRUNING FOR " + action + " : " + pruneList);
        for (DC_Obj t : pruneList) {
            targets.remove(t);
        }

    }

    private int getPowerFactor(int limit, UnitAI ai, ActiveObj action) {
        // cruel vs merciful
        ai.checkMod(AI_MODIFIERS.CRUEL);
        if (action.isMelee()) {
            return 10 + limit * 3;
        }
        return 30 + limit * 2;
    }

    private int getHeathPruneFactor(int limit, DC_Obj t, UnitAI ai, ActiveObj action) {
        if (action.isMelee()) {
            return 15 + limit * 3;
        }
        return 10 + limit * 2;
        // TODO shouldn't it work both ways?.. sometimes too much
        // power/health/capacity/danger is prune factor!
    }

    private int getCapacityPruneFactor(int limit, DC_Obj t, UnitAI ai, ActiveObj action) {
        if (action.isMelee()) {
            return 15 + limit * 3;
        }
        return 10 + limit * 2;
    }

    private int getDistancePruneFactor(int limit, DC_Obj t, UnitAI ai, ActiveObj action) {
        if (action.isRanged()) {
            return action.getIntParam(PARAMS.RANGE) * 3 / 2 - limit;
        }
        // TODO for quick flyers?
        return 6 - limit;
    }


}
