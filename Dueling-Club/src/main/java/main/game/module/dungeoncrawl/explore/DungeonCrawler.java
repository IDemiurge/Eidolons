package main.game.module.dungeoncrawl.explore;

import main.entity.obj.unit.Unit;

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
        for (Unit unit : allies) {
            if (master. getGame().getAiManager().getAnalyzer().getClosestEnemyDistance(unit)
                > minDistance)
                return true;
        }
        return false;

    }

    private boolean checkEngaged() {
//        master.getDungeon().
        for (Unit ally :allies) {
            // check block if (unit.getCoordinates())
//            VisionManager.
//         TODO    if (master. getGame().getVisionMaster().getDetectionMaster().checkDetected(ally))
//                return true;
        }
        return false;
    }

    public void checkStatusUpdate() {
        if (checkEngaged()) {
            ExplorationMaster.switchExplorationMode(false);
        } else {
            if (checkDanger()){
                //?
            } else {
                ExplorationMaster.switchExplorationMode(true);
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
