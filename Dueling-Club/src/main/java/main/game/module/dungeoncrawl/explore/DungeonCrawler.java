package main.game.module.dungeoncrawl.explore;

import main.entity.obj.unit.Unit;
import main.game.module.dungeoncrawl.ai.AggroMaster;

import java.util.List;
import java.util.Set;

/**
 * Created by JustMe on 9/9/2017.
 */
public class DungeonCrawler extends ExplorationHandler {
    private Set<Unit> allies;
    private int minDistance=4;

    public DungeonCrawler(ExplorationMaster master) {
        super(master);
    }


    public void reset() {
        allies = master.getGame().getPlayer( true).getControlledUnits_();
    }

    private boolean checkDanger() {
        //range? potential vision?
//        for (Unit unit : allies) {
//            if (master. getGame().getAiManager().getAnalyzer().getClosestEnemyDistance(unit)
//                > minDistance)
//                return true;
//        }
        return false;

    }

    private boolean checkEngaged() {
        List<Unit> aggroGroup = AggroMaster.getAggroGroup();

        if (!aggroGroup.isEmpty())
        {
            for (Unit sub : aggroGroup) {
            sub.getAI().setStandingOrders(null );
                }
            for (Unit sub : master.getGame().getUnits()) {
                sub.getAI().setOutsideCombat(false);
                if (!aggroGroup.contains(sub))
                    if (!allies.contains(sub))
                        sub.getAI().setOutsideCombat(true);
            }
//            aggroGroup.forEach(unit -> unit.getAI().setEngaged(true));
//            master.getGame().getTurnManager().setUnitGroup(
//             new DequeImpl<>(aggroGroup, allies));
            return true;
        }
//        for (Unit ally :allies) {
            // check block if (unit.getCoordinates())
//         TODO    if (master. getGame().getVisionMaster().getDetectionMaster().checkDetected(ally))
//                return true;
//        }
        return false;
    }

    public boolean checkExplorationDefault() {
        return !checkEngaged();
    }
    public void checkStatusUpdate() {
        reset();
        if (checkEngaged()) {
            master.switchExplorationMode(false);
        } else {
            if (checkDanger()){
                //?
            } else {
                master.switchExplorationMode(true);
            }
        }

    }

    public enum CRAWL_STATUS {
        EXPLORE,
        DANGER, //rounds on? behaviors ?
        ENGAGED,
        TIME_RUN
    }

}
