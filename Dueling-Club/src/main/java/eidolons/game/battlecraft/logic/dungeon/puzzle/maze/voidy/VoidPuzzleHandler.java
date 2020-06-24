package eidolons.game.battlecraft.logic.dungeon.puzzle.maze.voidy;

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
import eidolons.libgdx.bf.grid.DC_GridPanel;
import eidolons.libgdx.screens.ScreenMaster;
import main.content.CONTENT_CONSTS;
import main.content.enums.GenericEnums;
import main.content.enums.entity.BfObjEnums;
import main.game.bf.Coordinates;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.RandomWizard;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class VoidPuzzleHandler extends MazeHandler<VoidPuzzle> {

    protected VoidHandler voidHandler;
    float collapsePeriod;
    protected int actions;
    protected final Set<GridObject> holes = new LinkedHashSet<>();
    protected boolean collapsing;

    public VoidPuzzleHandler(VoidPuzzle mazePuzzle) {
        super(mazePuzzle);
    }

    @Override
    protected void beforeTip() {
        setup.reset();
        if (isFirstAttempt()) {

        Object c1 = RandomWizard.getRandomListObject(puzzle.falseExits);
        List<Coordinates> c = CoordinatesMaster.getCoordinatesBetween((Coordinates) c1, getExitCoordinates());
        GuiEventManager.trigger(GuiEventType.CAMERA_PAN_TO_COORDINATE, c);
        }
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
        if (puzzle.getFirstMoveScriptKey() != null) {
            ScriptLib.execute(puzzle.getFirstMoveScriptKey());
        }
        collapsing = true;
        updateQuest();
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
            Veil blackhole = new Veil(BfObjEnums.CUSTOM_OBJECT.GATE.spritePath,
                    getPuzzle(), getAbsoluteCoordinate(c), false, true) {
                protected boolean visible;

                @Override
                protected double getDefaultVisionRange() {
                    return 2;
                }

                @Override
                public boolean checkVisible() {
                    visible = (Eidolons.getGame().
                            getManager().getMainHeroCoordinates().dst_(c) <= getDefaultVisionRange());
                    return visible;
                }

                @Override
                public void init() {
                    super.init();
                    new Veil(getPuzzle(), c, false, false) {
                        @Override
                        public boolean checkVisible() {
                            if (!puzzle.isActive()) {
                                return false;
                            }
                            return !visible;
                        }
                    };
                }
            };
            blackhole.init();
            blackhole.getSprite().setBlending(GenericEnums.BLENDING.INVERT_SCREEN);
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
        voidHandler.collapseAll();
        voidHandler.setCollapsePeriod(0);
        collapsing = false;
        //shakes?
        for (GridObject hole : holes) {
            GuiEventManager.trigger(GuiEventType.REMOVE_GRID_OBJ, hole);
        }

        voidHandler.toggleAutoOff(Eidolons.getMainHero());
    }

}
