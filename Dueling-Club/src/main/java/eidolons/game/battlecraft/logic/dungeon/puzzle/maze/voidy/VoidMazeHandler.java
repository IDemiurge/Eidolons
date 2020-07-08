package eidolons.game.battlecraft.logic.dungeon.puzzle.maze.voidy;

import com.badlogic.gdx.math.Interpolation;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.game.battlecraft.logic.battlefield.CoordinatesMaster;
import eidolons.game.battlecraft.logic.dungeon.puzzle.PuzzleSetup;
import eidolons.game.battlecraft.logic.dungeon.puzzle.manipulator.GridObject;
import eidolons.game.battlecraft.logic.dungeon.puzzle.manipulator.Veil;
import eidolons.game.battlecraft.logic.dungeon.puzzle.maze.MazeHandler;
import eidolons.game.battlecraft.logic.dungeon.puzzle.maze.voidy.grid.PuzzleVoidHandler;
import eidolons.game.battlecraft.logic.dungeon.puzzle.maze.voidy.grid.VoidHandler;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech.ScriptLib;
import eidolons.game.core.Eidolons;
import eidolons.game.module.cinematic.Cinematics;
import eidolons.libgdx.anims.fullscreen.Screenshake;
import eidolons.libgdx.bf.grid.DC_GridPanel;
import eidolons.libgdx.screens.ScreenMaster;
import main.content.CONTENT_CONSTS;
import main.game.bf.Coordinates;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.threading.WaitMaster;

import java.util.LinkedHashSet;
import java.util.Set;

public class VoidMazeHandler extends MazeHandler<VoidMaze> {

    protected VoidHandler voidHandler;
    float collapsePeriod;
    protected int actions;
    protected final Set<GridObject> holes = new LinkedHashSet<>();
    protected boolean collapsing;

    public VoidMazeHandler(VoidMaze mazePuzzle) {
        super(mazePuzzle);
    }

    @Override
    protected PuzzleSetup createSetup() {
        return new VoidMazeSetup(getPuzzle());
    }

    protected float getTimePenalty(DC_ActiveObj action) {
        return collapsePeriod / getMaxMovesPeriod();// + getDefaultCollapsePeriod() * (0.01f + 0.0015f * actions);
    }
    protected float getMaxMovesPeriod() {
        return 5 + 10 * puzzle.getDifficultyCoef();
    }

    protected float getDefaultCollapsePeriod() {
        return 6 * puzzle.getDifficultyCoef();
    }

    @Override
    public void hideMaze() {
        super.hideMaze();
        if (puzzle.isAutoStart())
            firstMoveDone();
    }

    protected void resetHandler() {
        collapsePeriod = getDefaultCollapsePeriod();
        voidHandler.setCanDropHero(true);
        voidHandler.setCollapsePeriod(collapsePeriod);
        voidHandler.setUnmark(true);
    }

    protected void firstMoveDone() {
        resetHandler();
        cinematicFirstMove();
        collapsing = true;
        updateQuest();
    }

    protected void cinematicStart() {
    }
    protected void cinematicFirstCollapse() {
        Cinematics.doShake(Screenshake.ScreenShakeTemplate.MEDIUM, 2.3f, null);
    }

    protected void beforeTip() {
        setup.reset(); //show EXITS
        if (isFirstAttempt()) {
            //for now, we can just assume it's somewhere ABOVE...
            // Object c1 = RandomWizard.getRandomListObject(puzzle.falseExits);
            // List<Coordinates> c = CoordinatesMaster.getCoordinatesBetween(getAbsoluteCoordinate((Coordinates) c1),
            //         getExitCoordinates());
            Coordinates c = Eidolons.getPlayerCoordinates().getOffset(0, -9);
            GuiEventManager.trigger(GuiEventType.CAMERA_PAN_TO_COORDINATE, c);
        }
        Cinematics.doZoom(2, 2.25f, Interpolation.fade);
    }

    public void afterTipAction() {
        super.afterTipAction();
        WaitMaster.WAIT(3500);
        cinematicAfter();
    }

    @Override
    public void finished() {
        super.finished();
        cinematicAfter();
    }

    private void cinematicAfter() {
        GuiEventManager.trigger(GuiEventType.CAMERA_PAN_TO_UNIT, Eidolons.getMainHero());
        WaitMaster.WAIT(1500);
        Cinematics.doZoom(1.25f, 3.5f, Interpolation.fade);
        WaitMaster.WAIT(2500);
        ScriptLib.execute(ScriptLib.STD_SCRIPT.mini_explosion);
    }

    protected void cinematicFirstMove() {
        Cinematics.doZoom(1f, 2.25f, Interpolation.pow2In);
    }

    protected void cinematicFail() {

        Cinematics.doShake(Screenshake.ScreenShakeTemplate.HARD, 2, null);
        Cinematics.doZoom(0.1f, 2.25f, Interpolation.pow2In);
        getVoidHandler().getGridPanel().getGridManager().getAnimHandler().doFall(Eidolons.getMainHero());
    }

    protected void cinematicWin() {
        ScriptLib.execute(ScriptLib.STD_SCRIPT.gate_flash);
    }

    @Override
    public void activate() {
        VoidHandler.TEST_MODE = false;
        actions = 0;
        super.activate();
        getVoidHandler().toggleAutoOn(Eidolons.getMainHero());
        for (Coordinates c : getPuzzle().falseExits) {
            /*
            false exits are revealed as black holes upon approach!
             */
            Veil blackhole = new Veil(getPuzzle(), getAbsoluteCoordinate(c), true, true) {
                protected boolean visible;

                @Override
                protected double getDefaultVisionRange() {
                    return 2;
                }

                @Override
                public boolean checkVisible() {
                    visible = (Eidolons.getGame().
                            getManager().getMainHeroCoordinates().dst_(c) <= getDefaultVisionRange());
                    //blackness revealed only when close by
                    if (visible) {
                        return true;
                    }
                    return visible;
                }

                @Override
                public void init() {
                    super.init();
                    //this is the false exit that will disappear when we approach...
                    holes.add(new Veil(getPuzzle(), c, false, false) {
                        @Override
                        public boolean checkVisible() {
                            if (!puzzle.isActive()) {
                                return false;
                            }
                            return !visible;
                        }
                    });
                }
            };
            blackhole.init();
            blackhole.setUnder(true);
            GuiEventManager.trigger(GuiEventType.ADD_GRID_OBJ, blackhole);
            holes.add(blackhole);
        }
        Coordinates c = getExitCoordinates();
        Veil veil;
        puzzle.setExitVeil(veil = new Veil(puzzle, c, false, false));
        GuiEventManager.trigger(GuiEventType.ADD_GRID_OBJ, veil);
    }

    private VoidHandler getVoidHandler() {
        DC_GridPanel gridPanel = ScreenMaster.getDungeonGrid();
        if (voidHandler == null) {
            voidHandler = new PuzzleVoidHandler(gridPanel);
        }
        gridPanel.setVoidHandler(voidHandler);
        return voidHandler;
    }

    @Override
    public void playerActionDone(DC_ActiveObj action) {
        if (!collapsing) {
            firstMoveDone();
        }
        //time period reduced with each MOVE; Making it more turn-based/brainy than fast
        // if (CoreEngine.TEST_LAUNCH)
        {
            if (!CoordinatesMaster.getCoordinatesBetween(getCoordinates(),
                    getCoordinates().getOffset(getWidth(), getHeight())).contains(action.getOwnerUnit().getCoordinates())) {
                failed();
                return;
            }
        }
        if (action.isMove()) {
            //use interpolation?.. reduce slowly at first, more later
            actions++;

            // if (!collapsing) {
            //     firstMoveDone();
            // } else
            {
                collapsePeriod -= getTimePenalty(action);
                voidHandler.setCollapsePeriod(collapsePeriod);
            }
            updateQuest();
        }
    }

    public void ended() {
        super.ended();
        for (Coordinates markedCell : markedCells) {
            Eidolons.getGame().getCellByCoordinate(markedCell).
                    getMarks().remove(CONTENT_CONSTS.MARK.togglable);
        }
        // VoidHandler.TEST_MODE = true;
        //TODO collapse all?
        voidHandler.cleanUp();
        collapsing = false;
        //shakes?
        for (GridObject hole : holes) {
            GuiEventManager.trigger(GuiEventType.REMOVE_GRID_OBJ, hole);
        }


        voidHandler.toggleAutoOff(Eidolons.getMainHero());
    }

}
