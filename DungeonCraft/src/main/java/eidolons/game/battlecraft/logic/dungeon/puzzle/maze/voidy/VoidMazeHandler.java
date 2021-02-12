package eidolons.game.battlecraft.logic.dungeon.puzzle.maze.voidy;

import com.badlogic.gdx.math.Interpolation;
import eidolons.content.consts.VisualEnums;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.game.battlecraft.logic.battlefield.CoordinatesMaster;
import eidolons.game.battlecraft.logic.dungeon.puzzle.PuzzleSetup;
import eidolons.game.battlecraft.logic.dungeon.puzzle.maze.MazeHandler;
import eidolons.game.core.Eidolons;
import eidolons.game.module.cinematic.CinematicLib;
import eidolons.game.module.cinematic.Cinematics;
import main.game.bf.Coordinates;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.threading.WaitMaster;

public class VoidMazeHandler extends MazeHandler<VoidMaze> {

    public static  boolean TEST_MODE = false;
    protected IVoidGdxHandler voidHandler;
    float collapsePeriod;
    protected int actions;
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

    protected void beforeTip() {
        getVoidHandler();
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
        WaitMaster.WAIT(puzzle.getDelayAfterGlimpse());
        cinematicAfter();
    }

    @Override
    public void afterEndTip() {
        super.afterEndTip();
        cinematicAfter();
    }
    protected void cinematicStart() {
    }
    protected void cinematicFirstCollapse() {
        Cinematics.doShake( VisualEnums.ScreenShakeTemplate.MEDIUM, 2.3f, null);
    }


    protected void cinematicAfter() {
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
        TEST_MODE = false;
        actions = 0;
        super.started();
        initVisuals();
        getVoidHandler().toggleAutoOn(Eidolons.getMainHero());
    }

    private void initVisuals() {
        Cinematics.doParticles( VisualEnums.PARTICLES_SPRITE.ASH, 0.5f);
        voidHandler.initVisuals();

    }

    private IVoidGdxHandler getVoidHandler() {
        //TODO gdx sync important
        // DC_GridPanel gridPanel = ScreenMaster.getDungeonGrid();
        // if (voidHandler == null) {
        //     voidHandler = new PuzzleVoidHandler(gridPanel);
        // }
        // gridPanel.setCustomVoidHandler(voidHandler);
        return voidHandler;
    }

    @Override
    public void playerActionDone(DC_ActiveObj action) {
        if (!collapsing) {
            firstMoveDone();
        }
        //time period reduced with each MOVE; Making it more turn-based/brainy than fast
        {
            if (!CoordinatesMaster.getCoordinatesBetween(getCoordinates(),
                    getCoordinates().getOffset(getWidth(), getHeight())).contains(action.getOwnerUnit().getCoordinates())) {
                failed();
                return;
            }
        }
        if (action.isMove()) {
            actions++;
                collapsePeriod -= getTimePenalty(action);
                voidHandler.setCollapsePeriod(collapsePeriod);
            updateQuest();
        }
    }

    public void ended() {
        Cinematics.doParticles( VisualEnums.PARTICLES_SPRITE.ASH, 0);
        super.ended();
        // VoidHandler.TEST_MODE = true;
        //TODO collapse all?
        collapsing = false;
        voidHandler.ended();

    }

    @Override
    public void glimpse() {
        // voidHandler.glimpse();
        // if (Assets.isLoaded(FullscreenAnims.FULLSCREEN_ANIM.GATE_FLASH.getSpritePath())) {
        //     GuiEventManager.trigger(SHOW_FULLSCREEN_ANIM,
        //             new FullscreenAnimDataSource(FullscreenAnims.FULLSCREEN_ANIM.GATE_FLASH,
        //                     1, FACING_DIRECTION.NONE, GenericEnums.BLENDING.SCREEN));
        // }
    }
}
