package eidolons.game.battlecraft.logic.dungeon.puzzle.maze.voidy;

import eidolons.entity.active.DC_ActiveObj;
import eidolons.game.battlecraft.logic.dungeon.puzzle.manipulator.GridObject;
import eidolons.game.battlecraft.logic.dungeon.puzzle.manipulator.Veil;
import eidolons.game.battlecraft.logic.dungeon.puzzle.manipulator.VoidHandler;
import eidolons.game.battlecraft.logic.dungeon.puzzle.maze.MazePuzzle;
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

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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


    public VoidPuzzle() {
        super(MazeType.VOID);
    }

    @Override
    public RoomModel resetMaze() {
        if (template != null) {
            return template;
        }
        template = super.resetMaze();
        resetHandler();
        falseExits = new ArrayList<>();
        for (int i = 0; i < template.getCells().length; i++) {
            for (int j = 0; j < template.getCells()[0].length; j++) {
                if (template.getCells()[i][j].equalsIgnoreCase(getExitSymbol().symbol)) {
                    Coordinates c = Coordinates.get(i, j);
                    falseExits.add(c);
                    markedCells.add(c);
                }
            }
        }
        for (Coordinates markedCell : markedCells) {
            Eidolons.getGame().getCellByCoordinate(getAbsoluteCoordinate(markedCell)).
                    getMarks().add(CONTENT_CONSTS.MARK.togglable);
        }
        int i = RandomWizard.getRandomIndex(falseExits);
        realExit = falseExits.remove(i);
        return template;
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
        VoidHandler.TEST_MODE = true;
        //TODO collapse all?
        handler.collapseAll();
        //shakes?
        for (GridObject hole : holes) {
            GuiEventManager.trigger(GuiEventType.REMOVE_GRID_OBJ, hole);
        }
        template = null;
        handler.toggleAutoOff(Eidolons.getMainHero());
    }

    @Override
    public void activate() {
        VoidHandler.TEST_MODE = false;
        handler = ((DC_GridPanel) ScreenMaster.getDungeonGrid()).getVoidHandler();
        resetMaze();
        super.activate();
        resetHandler();
        handler.toggleAutoOn(Eidolons.getMainHero());
        for (Coordinates c : falseExits) {
            Veil blackhole = new Veil(BfObjEnums.CUSTOM_OBJECT.GATE.spritePath,
                    this, getAbsoluteCoordinate(c), false, false);
            blackhole.init();
            blackhole.getSprite().setBlending(GenericEnums.BLENDING.INVERT_SCREEN);
            GuiEventManager.trigger(GuiEventType.ADD_GRID_OBJ, blackhole);
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

    private float getMaxMovesPeriod() {
        return 10 * getDifficultyCoef();
    }

    @Override
    public void playerActionDone(DC_ActiveObj action) {
        //time period reduced with each MOVE; Making it more turn-based/brainy than fast
        if (action.isMove()) {
            //use interpolation?.. reduce slowly at first, more later
            collapsePeriod -= getTimePenalty(action);
            actions++;
            handler.setCollapsePeriod(collapsePeriod);
            GuiEventManager.trigger(GuiEventType.QUEST_UPDATE, quest);
        }
    }

    private float getTimePenalty(DC_ActiveObj action) {
        return collapsePeriod * 0.03f + getDefaultCollapsePeriod() * (0.01f + 0.0015f * actions);
    }

    @Override
    public String getQuestText() {
        return "Cells collapse every " + NumberUtils.formatFloat(1, collapsePeriod) +
                "s";
    }

    protected GeneratorEnums.ROOM_CELL getExitSymbol() {
        return GeneratorEnums.ROOM_CELL.VOID;
    }

    @Override
    public Coordinates getExitCoordinates() {
        return getAbsoluteCoordinate( realExit);
    }

    @Override
    protected String getDefaultTitle() {
        return super.getDefaultTitle();
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

    @Override
    public FlightData getFlightData() {
        // String s = "";
        //getData().getValue() construct!
        // return new FlightData(s);
        return new FlightData(FlightHandler.TEST_DATA);
    }
}
