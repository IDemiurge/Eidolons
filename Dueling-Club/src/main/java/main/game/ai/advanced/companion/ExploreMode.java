package main.game.ai.advanced.companion;

import main.game.ai.UnitAI;
import main.game.ai.elements.actions.Action;
import main.game.battlefield.Coordinates;
import main.game.battlefield.Coordinates.DIRECTION;
import main.game.core.game.DC_Game;
import main.game.logic.dungeon.ai.DungeonCrawler.ENGAGEMENT_LEVEL;

public class ExploreMode {
    /*
     * unit is given a coordinate to go to
	 * build path...
	 * 
	 */

    float speed;
    boolean search;
    boolean stealth;
    boolean economic;

    boolean interrupted;
    boolean leader;
    boolean group;
    boolean stickTogether;
    UnitAI ai;
    Coordinates destination;
    DIRECTION direction;
    private boolean done;
    private DC_Game game;
    private boolean ignoreEnemies;

    public ExploreMode(UnitAI ai) {
        this.ai = ai;
        game = ai.getUnit().getGame();
    }

    public void editModeParams() {

    }

    public Action getNextAction() {
        checkDone();
        if (done) {
            return null;
        }
//        if (interrupted)
        return null;
//        return action;
    }

    public Boolean promptInterrupt() {
//        result = (DialogMaster.confirm("Interrupt exploration to " + destination + "?"));
//        if (result)
//            if (DialogMaster.confirm("Ignore enemies?"))
//                ignoreEnemies = true;
//
//        return result;
        return null;
    }

    private void checkDone() {
        if (!game.getRules().getStackingRule().canBeMovedOnto(ai.getUnit(), destination)) {

        }
        if (checkEnemySpotted()) {
            if (!isIgnoreEnemies()) {
                if (promptInterrupt()) {
                    interrupted = true;
                    return;
                }
            }
        }

        if (ai.getUnit().getCoordinates().equals(destination)) {
            done = true;
        }

//        if (path == null) {
//            failed = true;
//            done = true;
//        }

    }

    private boolean isIgnoreEnemies() {
        return ignoreEnemies;
    }

    private boolean checkEnemySpotted() {
        // enemy list per turn, compare to last one to determine if 'new enemies
        // spotted'?
//        for (DC_HeroObj enemy : Analyzer.getSpottedEnemies(ai))
//            // for group or unit only?
//            if (!enemy.isKnown()) {
//                enemy.setKnown(true);
//                return true;
//            }
        if (ai.getGroup().getEngagementLevel() == ENGAGEMENT_LEVEL.ALARMED) {

        }
        return false;
    }

    public boolean isDone() {
        return done;
    }

    public enum EXPLORE_PARAMS {

    }
}
