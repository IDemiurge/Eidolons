package eidolons.game.battlecraft.logic.dungeon.puzzle.maze.voidy;

import eidolons.entity.active.DC_ActiveObj;
import eidolons.game.battlecraft.logic.battlefield.CoordinatesMaster;
import eidolons.game.battlecraft.logic.dungeon.puzzle.manipulator.GridObject;
import eidolons.game.battlecraft.logic.dungeon.puzzle.manipulator.Veil;
import eidolons.game.battlecraft.logic.dungeon.puzzle.manipulator.VoidHandler;
import eidolons.game.battlecraft.logic.dungeon.puzzle.maze.MazePuzzle;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech.ScriptLib;
import eidolons.game.core.Eidolons;
import eidolons.game.module.cinematic.flight.FlightData;
import eidolons.game.module.cinematic.flight.FlightHandler;
import eidolons.game.module.generator.GeneratorEnums;
import eidolons.game.module.generator.model.RoomModel;
import eidolons.libgdx.bf.grid.DC_GridPanel;
import eidolons.libgdx.screens.ScreenMaster;
import eidolons.libgdx.texture.Sprites;
import main.content.CONTENT_CONSTS;
import main.content.enums.GenericEnums;
import main.content.enums.entity.BfObjEnums;
import main.game.bf.Coordinates;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.NumberUtils;
import main.system.auxiliary.RandomWizard;
import main.system.launch.CoreEngine;

import java.util.*;
import java.util.stream.Collectors;

/**
 * failed when hero's cell becomes void 'wall' cells are MARKED
 * <p>
 * FALSE EXITS! E.g., there are 3 in the template, and only one random is TRUE. Veil vs <?>
 */
public class VoidPuzzle extends MazePuzzle {

    private VoidHandler handler;
    float collapsePeriod;
    private int actions;
    private List<Coordinates> falseExits;
    private Coordinates realExit;
    private final Set<GridObject> holes = new LinkedHashSet<>();
    private RoomModel template;
    private boolean collapsing;

    public VoidPuzzle() {
        super(MazeType.LIGHT, MazeType.DARK);
        if (!CoreEngine.TEST_LAUNCH) {
            GuiEventManager.trigger(GuiEventType.UPDATE_DUNGEON_BACKGROUND, getOverrideBackground());
        }
    }

    public boolean isTransform() {
        return false;
    }

    @Override
    public RoomModel resetMaze() {
        if (template != null) {
            return template;
        }
        template = super.resetMaze();
        if (isTransform()) {
            transformMaze();
        }
        resetHandler();
        falseExits = new ArrayList<>();
        for (int i = 0; i < template.getCells().length; i++) {
            for (int j = 0; j < template.getCells()[0].length; j++) {
                if (template.getCells()[i][j].equalsIgnoreCase(getExitSymbol().symbol)) {
                    Coordinates c = Coordinates.get(i, j);
                    if (!isExitToConvert(c))
                        falseExits.add(c);
                    markedCells.add(c);
                }
            }
        }
        for (Coordinates markedCell : markedCells) {
            Eidolons.getGame().getCellByCoordinate(getAbsoluteCoordinate(markedCell)).
                    getMarks().add(CONTENT_CONSTS.MARK.togglable);
        }
        markedCells.removeAll(falseExits);
        filterExits();
        int i = RandomWizard.getRandomIndex(falseExits);
        realExit = falseExits.remove(i);
        return template;
    }

    private boolean isExitToConvert(Coordinates exit) {
        Coordinates c = getEntranceCoordinates();
        return getAbsoluteCoordinate(exit).dst(c) < 3;
    }

    private void filterExits() {
        Coordinates c = getEntranceCoordinates();
        falseExits.removeIf(exit -> getAbsoluteCoordinate(exit).dst(c) < getMinExitDst());
        Collections.shuffle(falseExits);
        falseExits = falseExits.stream().limit(getMaxExits()).collect(Collectors.toList());
    }

    private void transformMaze() {
        if (RandomWizard.random()) {
            template.setRotations(RandomWizard.random(),
                    RandomWizard.random(),
                    RandomWizard.random());
        }
        template.setFlip(RandomWizard.random(), RandomWizard.random());
    }


    private int getMinExitDst() {
        return 8;
    }

    private long getMaxExits() {
        return RandomWizard.getRandomIntBetween(3, 4);
    }

    private void resetHandler() {
        collapsePeriod = getDefaultCollapsePeriod();
        handler.setCanDropHero(true);
        handler.setCollapsePeriod(collapsePeriod);
        handler.setUnmark(true);
    }

    @Override
    public void complete() {
        super.complete();
    }

    public void ended() {
        super.ended();
        for (Coordinates markedCell : markedCells) {
            Eidolons.getGame().getCellByCoordinate(markedCell).
                    getMarks().remove(CONTENT_CONSTS.MARK.togglable);
        }
        // VoidHandler.TEST_MODE = true;
        //TODO collapse all?
        handler.collapseAll();
        collapsing = false;
        //shakes?
        for (GridObject hole : holes) {
            GuiEventManager.trigger(GuiEventType.REMOVE_GRID_OBJ, hole);
        }
        template = null;
        handler.toggleAutoOff(Eidolons.getMainHero());
    }

    private void firstMoveDone() {
        resetHandler();
        ScriptLib.execute(getFirstMoveScriptKey());
        collapsing = true;
        updateQuest();
    }


    private String getFirstMoveScriptKey() {
        return "void maze start";
    }

    @Override
    public void activate() {
        VoidHandler.TEST_MODE = false;
        actions = 0;
        handler = ((DC_GridPanel) ScreenMaster.getDungeonGrid()).getVoidHandler();
        resetMaze();
        super.activate();
        handler.toggleAutoOn(Eidolons.getMainHero());
        for (Coordinates c : falseExits) {
            /*
            false exits are revealed as black holes upon approach!
             */
            Veil blackhole = new Veil(BfObjEnums.CUSTOM_OBJECT.GATE.spritePath,
                    this, getAbsoluteCoordinate(c), false, true) {
                private boolean visible;

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
                    new Veil(VoidPuzzle.this, c, false, false) {
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
        setExitVeil(veil = new Veil(this, c, false, false));
        GuiEventManager.trigger(GuiEventType.ADD_GRID_OBJ, veil);
    }

    @Override
    public void failed() {
        super.failed();
    }

    private float getDefaultCollapsePeriod() {
        return 10 * getDifficultyCoef();
    }

    @Override
    public void playerActionDone(DC_ActiveObj action) {
        //time period reduced with each MOVE; Making it more turn-based/brainy than fast
        // if (CoreEngine.TEST_LAUNCH)
        {
            if (!CoordinatesMaster.getCoordinatesBetween(coordinates,
                    coordinates.getOffset(getWidth(), getHeight())).contains(action.getOwnerUnit().getCoordinates())) {
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
                handler.setCollapsePeriod(collapsePeriod);
            }
            GuiEventManager.trigger(GuiEventType.QUEST_UPDATE, quest);
        }
    }

    @Override
    public void hideMaze() {
        super.hideMaze();
        firstMoveDone();
    }

    private float getTimePenalty(DC_ActiveObj action) {
        return collapsePeriod * 1f / getMaxMovesPeriod();// + getDefaultCollapsePeriod() * (0.01f + 0.0015f * actions);
    }

    private float getMaxMovesPeriod() {
        return 20 * getDifficultyCoef();
    }

    @Override
    public String getQuestText() {
        return "Find a path to the light through the void." +
                "\n" +
                (collapsing ? "Path segments collapse every " + NumberUtils.formatFloat(1, collapsePeriod) +
                        " seconds. " : "") +
                "Beware of darkness.";
    }

    @Override
    protected String getFailCinematicScriptKey() {
        return "black hole";
    }

    @Override
    protected String getWinCinematicScriptKey() {
        return "void maze win";
    }

    public String getStartSound() {
        return GenericEnums.SOUND_CUE.dark_knight.getPath();
    }

    public String getFailSound() {
        return GenericEnums.SOUND_CUE.mute_scream.getPath();
    }

    public String getCompleteSound() {
        return GenericEnums.SOUND_CUE.dream.getPath();
    }

    protected GeneratorEnums.ROOM_CELL getExitSymbol() {
        return GeneratorEnums.ROOM_CELL.VOID;
    }

    @Override
    public Coordinates getExitCoordinates() {
        if (realExit == null) {
            return null;
        }
        return getAbsoluteCoordinate(realExit);
    }

    @Override
    protected String getDefaultTitle() {
        return "Soul Maze";
    }

    @Override
    protected GeneratorEnums.EXIT_TEMPLATE getTemplateForPuzzle() {
        return super.getTemplateForPuzzle();
    }

    @Override
    protected GeneratorEnums.ROOM_TEMPLATE_GROUP getTemplateGroup() {
        return GeneratorEnums.ROOM_TEMPLATE_GROUP.VOID_MAZE;
    }

    public List<Coordinates> getFalseExits() {
        return falseExits;
    }

    public void setFalseExits(List<Coordinates> falseExits) {
        this.falseExits = falseExits;
    }

    @Override
    public boolean isMinimizeUI() {
        return true;
    }

    @Override
    public String getOverrideBackground() {
        return Sprites.BG_DEFAULT;
    }

    protected int getWaitTimeBeforeEndMsg(boolean failed) {
        return 2500;
    }

    @Override
    public FlightData getFlightData() {
        // String s = "";
        //getData().getValue() construct!
        // return new FlightData(s);
        return new FlightData(FlightHandler.FLIGHT_ENVIRON.astral.data);
    }
}
