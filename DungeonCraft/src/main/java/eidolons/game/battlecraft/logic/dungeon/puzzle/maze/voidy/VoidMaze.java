package eidolons.game.battlecraft.logic.dungeon.puzzle.maze.voidy;

import eidolons.game.battlecraft.logic.dungeon.puzzle.PuzzleHandler;
import eidolons.game.battlecraft.logic.dungeon.puzzle.maze.MazePuzzle;
import eidolons.game.battlecraft.logic.dungeon.puzzle.sub.PuzzleEnums;
import eidolons.game.module.cinematic.flight.FlightData;
import eidolons.game.module.cinematic.flight.FlightHandler;
import eidolons.dungeons.generator.GeneratorEnums;
import eidolons.dungeons.generator.model.RoomModel;
import main.content.enums.GenericEnums;
import main.game.bf.Coordinates;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.NumberUtils;
import main.system.auxiliary.RandomWizard;
import main.system.launch.CoreEngine;

import java.util.List;

/**
 * failed when hero's cell becomes void 'wall' cells are MARKED
 * <p>
 * FALSE EXITS! E.g., there are 3 in the template, and only one random is TRUE. Veil vs <?>
 */
public class VoidMaze extends MazePuzzle {

    protected List<Coordinates> falseExits;
    protected Coordinates realExit;
    protected RoomModel template;

    public VoidMaze() {
        super(MazeType.LIGHT, MazeType.DARK);
        if (!CoreEngine.TEST_LAUNCH) {
            GuiEventManager.trigger(GuiEventType.UPDATE_DUNGEON_BACKGROUND, getOverrideBackground());
        }
    }

    @Override
    public PuzzleEnums.puzzle_type getType() {
        return PuzzleEnums.puzzle_type.voidmaze;
    }

    public boolean isTransform() {
        return true;
    }

    @Override
    public VoidMazeHandler getHandler() {
        return (VoidMazeHandler) super.getHandler();
    }

    @Override
    protected PuzzleHandler createHandler() {
        return new VoidMazeHandler(this);
    }


    @Override
    public void complete() {
        super.complete();
    }


    protected String getFirstMoveScriptKey() {
        // return "void maze start";
        return null;
    }


    @Override
    public String getQuestText() {
        return "Find a path to the light through the void." +
                "\n" +
                (getHandler().collapsing ? "Path segments collapse every " + NumberUtils.formatFloat(1, getHandler().collapsePeriod) +
                        " seconds. " : "") +
                "\nBeware of darkness.";
    }

    @Override
    protected String getFailCinematicScriptKey() {
        return "black hole";
    }

    @Override
    protected String getFailText() {
        return "Crimson shadows grasp for me in the void.\n I must retreat and try harder.";
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
    public int getTipDelay() {
        //only first time //TODO
        return 500;
    }

    protected int getWaitTimeBeforeEndMsg(boolean failed) {
        return 1100;
    }

    public int getDelayAfterGlimpse() {
        return 1250;
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
        return null;//Sprites.BG_DEFAULT;
    }

    @Override
    public FlightData getFlightData() {
        // String s = "";
        //getData().getValue() construct!
        // return new FlightData(s);
        return new FlightData(FlightHandler.FLIGHT_ENVIRON.voidmaze.data);
    }

    public int getMinExitDst() {
        return 8;
    }

    public long getMaxExits() {
        return RandomWizard.getRandomIntBetween(3, 4);
    }

    public boolean isAutoStart() {
        return false;
    }

    public boolean isMarkAroundEntrance() {
        return false;
    }

    protected int getDefaultHeight() {
        return 11;
    }

    protected int getDefaultWidth() {
        return 11;
    }

}
