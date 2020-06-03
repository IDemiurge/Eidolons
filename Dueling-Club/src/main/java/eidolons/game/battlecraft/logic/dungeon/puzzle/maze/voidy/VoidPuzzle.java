package eidolons.game.battlecraft.logic.dungeon.puzzle.maze.voidy;

import eidolons.entity.active.DC_ActiveObj;
import eidolons.game.battlecraft.logic.dungeon.puzzle.manipulator.VoidHandler;
import eidolons.game.battlecraft.logic.dungeon.puzzle.maze.MazePuzzle;
import eidolons.libgdx.bf.grid.DC_GridPanel;
import eidolons.libgdx.screens.ScreenMaster;

/**
 * failed when hero's cell becomes void 'wall' cells are MARKED
 * <p>
 * FALSE EXITS! E.g., there are 3 in the template, and only one random is TRUE. Veil vs <?>
 */
public class VoidPuzzle extends MazePuzzle {

    private final VoidHandler handler;
    float timePassed;
    float collapsePeriod;
    private int actions;


    public VoidPuzzle() {
        super(MazeType.VOID);
        handler = ((DC_GridPanel) ScreenMaster.getDungeonGrid()).getVoidHandler();
    }
    @Override
    public void resetMaze() {
        super.resetMaze();
        resetHandler();
    }

    private void resetHandler() {
        collapsePeriod=getDefaultCollapsePeriod();
        handler.setCanDropHero(true);
        handler.setCollapsePeriod(collapsePeriod);
        handler.setUnmark(true);
    }

    @Override
    public void activate() {
        super.activate();
        resetHandler();
    }

    private float getDefaultCollapsePeriod() {
        return  10*getDifficultyCoef();
    }
    private float getMaxMovesPeriod() {
        return  10*getDifficultyCoef();
    }

    @Override
    public void playerActionDone(DC_ActiveObj action) {
        //time period reduced with each MOVE; Making it more turn-based/brainy than fast
        if (action.isMove()) {
           //use interpolation?.. reduce slowly at first, more later
            collapsePeriod -= getTimePenalty(action);
            actions++;
            handler.setCollapsePeriod(collapsePeriod);
        }
    }

    private float getTimePenalty(DC_ActiveObj action) {
       return  collapsePeriod*0.02f+getDefaultCollapsePeriod()*(0.02f+0.005f*actions);
    }


    public void act(float delta){

    }

    public boolean checkFailed() {
        getDifficultyCoef();
        return false;
    }

    @Override
    public String getQuestText() {
        return super.getQuestText();
    }

    @Override
    protected String getDefaultTitle() {
        return super.getDefaultTitle();
    }


}
