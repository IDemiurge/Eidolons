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
import eidolons.game.core.Eidolons;
import eidolons.game.module.cinematic.CinematicLib;
import eidolons.game.module.cinematic.Cinematics;
import eidolons.libgdx.anims.fullscreen.Screenshake;
import eidolons.libgdx.bf.grid.DC_GridPanel;
import eidolons.libgdx.screens.ScreenMaster;
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
    public void afterEndTip() {
        super.afterEndTip();
        cinematicAfter();
    }

    private void cinematicAfter() {
        CinematicLib.run(CinematicLib.StdCinematic.VOID_MAZE_AFTER);
    }

    protected void cinematicFirstMove() {
        CinematicLib.run(CinematicLib.StdCinematic.VOID_MAZE_FIRST_MOVE);
    }

    protected void cinematicWin() {
        CinematicLib.run(CinematicLib.StdCinematic.VOID_MAZE_WIN);
    }
    protected void cinematicFail() {
        CinematicLib.run(CinematicLib.StdCinematic.VOID_MAZE_FAIL);
      }


    public void started() {
        VoidHandler.TEST_MODE = false;
        actions = 0;
        super.started();
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
                        sprite.setVisible(true);
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
            blackhole.setInitRequired(true);
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
        gridPanel.setCustomVoidHandler(voidHandler);
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
        // VoidHandler.TEST_MODE = true;
        //TODO collapse all?
        voidHandler.cleanUp();
        collapsing = false;
        //shakes?
        for (GridObject hole : holes) {
            GuiEventManager.trigger(GuiEventType.REMOVE_GRID_OBJ, hole);
        }

        ScreenMaster.getDungeonGrid().setCustomVoidHandler(null);

        voidHandler.toggleAutoOff(Eidolons.getMainHero());
    }

}
