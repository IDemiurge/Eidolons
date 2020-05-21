package eidolons.game.battlecraft.ai.explore;

import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.ai.UnitAI;
import eidolons.game.battlecraft.ai.advanced.engagement.EngageEvent;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.explore.ExplorationHandler;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import io.vertx.core.impl.ConcurrentHashSet;
import main.content.enums.rules.VisionEnums;
import main.entity.obj.Obj;
import main.system.auxiliary.data.ListMaster;
import main.system.math.PositionMaster;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AggroMaster extends ExplorationHandler {
    private static final int DEFAULT_ENGAGEMENT_DURATION = 2;
    private static final double MAX_AGGRO_DST = 5;
    private static List<Unit> lastAggroGroup;

    /*
    so the statuses of the units/groups are handled elsewhere now.
    This class could just give us a final, filtered set of units that should be in combat
    ... and maybe manage some aggro-related behavior/checks
     */
    public AggroMaster(ExplorationMaster master) {
        super(master);
    }

    public void checkStatusUpdate() {
        if (checkEngaged()) {
            if (!isCombat())
                 master.event(new EngageEvent(EngageEvent.ENGAGE_EVENT.combat_start,
                         AggroMaster.getAggroGroup().size()));
        } else {
            if (isCombat())
                master.event(new EngageEvent(EngageEvent.ENGAGE_EVENT.combat_end));
        }
    }

    public static List<Unit> getAggroGroup() {
        List<Unit> list = new ArrayList<>();
        Set<Unit> heroes = DC_Game.game.getPlayer(true).collectControlledUnits_();

        for (Unit ally : heroes) {
            for (Unit unit : getAggroGroup(ally)) {
                if (!list.contains(unit))
                    list.add(unit);
            }
        }

        if (ListMaster.isNotEmpty(list) || ListMaster.isNotEmpty(lastAggroGroup))
            main.system.auxiliary.log.LogMaster.log(1, "Aggro group: " + list +
                    "; last: " + lastAggroGroup);
        if (lastAggroGroup != null)
            if (lastAggroGroup.size() > list.size()) {
                main.system.auxiliary.log.LogMaster.log(1, "Aggro group reduced: " + lastAggroGroup + " last vs new: " + list);
            }
        lastAggroGroup = list;
        if (!ExplorationMaster.isExplorationOn())
            if (!list.isEmpty()) {
                logAggro(list);
            }
        return list;
    }

    public static List<Unit> getLastAggroGroup() {
        if (lastAggroGroup == null) {
            return new ArrayList<>();
        }
        return lastAggroGroup;
    }

    private static void logAggro(List<Unit> list) {
        if (!list.equals(lastAggroGroup)) {
            List<Unit> newUnits = new ArrayList<>(list);
            newUnits.removeIf(unit -> lastAggroGroup.contains(unit));
            if (!newUnits.isEmpty())
                list.get(0).getGame().getLogManager().logBattleJoined(newUnits);
        }
    }

    public static Set<Unit> getAggroGroup(Unit hero) {
        Set<Unit> set = new ConcurrentHashSet<>();

        boolean newAggro = false;
        for (Unit unit : DC_Game.game.getUnits()) {
            if (unit.isDead())
                continue;
            if (!unit.isEnemyTo(DC_Game.game.getPlayer(true)))
                continue;
            //?
            if (unit.isNamedUnit() || unit.isBoss() || unit.getAI().isEngagedOverride()) {
                set.add(unit);
                newAggro = true;
                continue;

            }
            if (isCriticalBreak(unit, hero))
                continue;
            //TODO  remove
            if (PositionMaster.getExactDistance(hero, unit) >=
                    5 + unit.getAI().getEngagementDuration()//+ unit.getSightRangeTowards(hero)
            )
                continue;

            if (unit.getAI().isEngaged()) {
                set.add(unit);
                newAggro = true;
            }
            if (unit.getAI().getEngagementDuration() > 0) {
                set.add(unit);
            }
//                  OLD LOGIC
//                if (!unit.getGame().getVisionMaster().getVisionRule().isAggro(hero, unit))
//                    continue;
            if (unit.getAI().getEngagementLevel() != VisionEnums.ENGAGEMENT_LEVEL.ENGAGED) {
                continue;
            }

            newAggro = true;
            //event?
            set.add(unit);

        }

        for (Unit unit : set) {
            //TODO remove now?
            unit.getAI().setEngaged(false);
            if (unit.getAI().getGroup() != null) {
                for (Unit sub : unit.getAI().getGroup().getMembers()) {
                    set.add(sub);
                    if (newAggro) {
                        int duration = getEngagementDuration(sub.getAI());
                        if (set.size() > 2) {
                            sub.getAI().setEngagementDuration(duration); //debug
                        }
                        sub.getAI().setEngagementDuration(duration);
                    }
                }
            }
        }
        return set;
    }

    //TODO remove !
    private static boolean isCriticalBreak(Unit unit, Unit hero) {
        double max = MAX_AGGRO_DST + unit.getAI().getEngagementDuration();
        return unit.getCoordinates().dst_(hero.getCoordinates()) >= max;
    }

    private static int getEngagementDuration(UnitAI ai) {
        return DEFAULT_ENGAGEMENT_DURATION;
    }

    public static void unitAttacked(DC_ActiveObj action, Obj targetObj) {
        if (targetObj.isMine()) {
            action.getOwnerUnit().
                    getAI().setEngaged(true);
        } else {
            if (checkAttackEngages(action, targetObj)) {
                ((Unit) targetObj).getAI().setEngaged(true);
            }
        }
    }

    private static boolean checkAttackEngages(DC_ActiveObj action, Obj targetObj) {
        return true;
    }

    public static int getBattleDifficulty() {
        return getLastAggroGroup().stream().mapToInt(u -> u.getPower()).sum();
    }

    public static void aggro(Unit unit, Unit mainHero) {
        unit.getAI().setEngaged(true);
        unit.getAI().setEngagementDuration(2);
        unit.getAI().setEngagedOverride(true);
    }

    private boolean checkEngaged() {
        List<Unit> aggroGroup = AggroMaster.getAggroGroup();

        if (!aggroGroup.isEmpty()) {
            for (Unit sub : master.getGame().getUnits()) {
                sub.getAI().setOutsideCombat(false);
                if (!aggroGroup.contains(sub))
                    if (!sub.isMine())
                        sub.getAI().setOutsideCombat(true);
            }
            return true;
        }
        return false;
    }

    public boolean checkExplorationDefault() {
        return !checkEngaged();
    }


}
