package eidolons.game.battlecraft.logic.dungeon.puzzle.maze.voidy;

import eidolons.game.battlecraft.logic.dungeon.puzzle.PuzzleHandler;
import eidolons.game.battlecraft.logic.dungeon.puzzle.maze.MazePuzzle;
import eidolons.game.module.cinematic.flight.FlightData;
import eidolons.game.module.cinematic.flight.FlightHandler;
import eidolons.game.module.generator.GeneratorEnums;
import eidolons.game.module.generator.model.RoomModel;
import eidolons.libgdx.texture.Sprites;
import main.content.enums.GenericEnums;
import main.game.bf.Coordinates;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.NumberUtils;
import main.system.launch.CoreEngine;

import java.util.List;

/**
 * failed when hero's cell becomes void 'wall' cells are MARKED
 * <p>
 * FALSE EXITS! E.g., there are 3 in the template, and only one random is TRUE. Veil vs <?>
 */
public class VoidPuzzle extends MazePuzzle {

    protected List<Coordinates> falseExits;
    protected Coordinates realExit;
    protected RoomModel template;

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
    public VoidPuzzleHandler getHandler() {
        return (VoidPuzzleHandler) super.getHandler();
    }

    @Override
    protected PuzzleHandler createHandler() {
        return new VoidPuzzleHandler(this );
    }


    @Override
    public void complete() {
        super.complete();
    }



    protected String getFirstMoveScriptKey() {
        return "void maze start";
    }


  

    @Override
    public String getQuestText() {
        return "Find a path to the light through the void." +
                "\n" +
                (getHandler().collapsing ? "Path segments collapse every " + NumberUtils.formatFloat(1, getHandler().collapsePeriod) +
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
