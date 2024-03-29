package libgdx.screens.dungeon;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.battlecraft.logic.dungeon.module.Module;
import eidolons.game.core.game.DC_Game;
import eidolons.game.exploration.story.cinematic.Cinematics;
import eidolons.content.consts.libgdx.GdxColorMaster;
import libgdx.GdxMaster;
import eidolons.content.consts.GridCreateData;
import libgdx.bf.GridMaster;
import libgdx.bf.grid.GridPanel;
import libgdx.bf.mouse.InputController;
import libgdx.gui.dungeon.panels.headquarters.town.TownPanel;
import libgdx.particles.ambi.ParticleManager;
import libgdx.screens.GameScreen;
import libgdx.screens.handlers.ScreenMaster;
import libgdx.stage.GenericGuiStage;
import libgdx.stage.GridStage;
import main.game.bf.Coordinates;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.log.Chronos;
import main.system.datatypes.DequeImpl;
import main.system.launch.CoreEngine;
import main.system.launch.Flags;
import org.lwjgl.opengl.GL11;

import static main.system.GuiEventType.UPDATE_GUI;

public abstract class GenericDungeonScreen extends GameScreen {
    protected GridStage gridStage;
    protected GridPanel gridPanel;
    protected ParticleManager particleManager;
    protected abstract GenericGuiStage createGuiStage();

    protected abstract GridPanel createGrid(GridCreateData param);

    protected void bindEvents() {
        GuiEventManager.bind(GuiEventType.CAMERA_LAPSE_TO, p -> {
            getCam().position.set(GridMaster.getCenteredPos((Coordinates) p.get()), 0);

        });
    }

    protected void checkGraphicsUpdates() {
        if (background== null &&backTexture == null && backgroundSprite == null) {
            String path = null;
            try {
                path = DC_Game.game.getDungeonMaster().getFloorWrapper().getMapBackground();
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
            main.system.auxiliary.log.LogMaster.devLog("Setting dc background " + path);
            Chronos.mark("bg");
            setBackground(path);
            Chronos.logTimeElapsedForMark("bg");
        }
    }


    protected void drawBg(float delta) {
        updateBackground(delta);
        Batch batch = guiStage.getCustomSpriteBatch();
        batch.begin();
        if (backgroundSprite == null && background != null) {
            background.act(delta);
            //scale?
            GdxMaster.center(background);
            background.draw(batch, 1f);
        } else if (backTexture != null) {
            float colorBits = GdxColorMaster.WHITE.toFloatBits();
            if (batch.getColor().toFloatBits() != colorBits)
                batch.setColor(colorBits); //gotta reset the alpha... if (backTexture == null)
            if (backgroundSprite != null) {
                drawSpriteBg(batch);
            } else if (isCenteredBackground()) {
                int w = backTexture.getRegionWidth();
                int h = backTexture.getRegionHeight();
                int x = (GdxMaster.getWidth() - w) / 2;
                int y = (GdxMaster.getHeight() - h) / 2;
                batch.draw(backTexture, x, y, w, h);
            } else {
                //TODO max
                batch.draw(backTexture, 0, 0, GdxMaster.getWidth(), GdxMaster.getHeight());
            }

        }
        batch.end();

    }

    private boolean isCenteredBackground() {
        if (backgroundSprite != null) {
            return true;
        }
        return !ScreenMaster.isFullscreen();
    }

    private void drawSpriteBg(Batch batch) {
        backgroundSprite.setOffsetY(
                Gdx.graphics.getHeight() / 2);
        backgroundSprite.setOffsetX(Gdx.graphics.getWidth() / 2);
        backgroundSprite.setSpeed(0.5f);
        // backgroundSprite.setOffsetY(getCam().position.y);
        // backgroundSprite.setOffsetX(getCam().position.x);
        backgroundSprite.draw(batch);
    }

    private void updateBackground(float delta) {
        if (backgroundSprite != null) {
            backgroundSprite.act(delta);
            backTexture = backgroundSprite.getCurrentFrame();
            if (backgroundSprite.getCurrentFrameNumber() == backgroundSprite.getFrameNumber() - 1) {
                if (backgroundSprite.getPlayMode() == Animation.PlayMode.LOOP_REVERSED)
                    backgroundSprite.setPlayMode(Animation.PlayMode.LOOP);
                else {
                    backgroundSprite.setPlayMode(Animation.PlayMode.LOOP_REVERSED);
                }
            }
            backTexture = backgroundSprite.getCurrentFrame();

        }
    }

    public void updateGui() {
        checkGraphicsUpdates();
    }


    @Override
    protected void renderLoaderAndOverlays(float delta) {
        super.renderLoaderAndOverlays(delta);
        if (selectionPanel != null) {
            if (selectionPanel.getStage() == guiStage) {
                if (!getBatch().isDrawing()) {
                    getBatch().begin();
                }
                selectionPanel.act(delta);
                selectionPanel.draw(getBatch(), 1f);
                getBatch().end();
            }
        }
    }

    @Override
    protected void afterLoad() {
        setCam((OrthographicCamera) viewPort.getCamera());
        //        final GridCreateData param = ((GridCreateData) data.getParams().get());
        //        createAndInitModuleGrid(param);
    }

    protected void createAndInitModuleGrid(GridCreateData param) {

        gridStage.addActor(gridPanel = createGrid(param));
        Module module = DC_Game.game.getModule();
        if (!CoreEngine.isLevelEditor())
            param.getObjects().removeIf(obj -> !module.getCoordinatesSet().
                    contains(obj.getCoordinates()));
        moduleEntered(module, param.getObjects()); //will be triggered, hopefully
        //do not chain - will fail ...
    }

    public void moduleEntered(Module module, DequeImpl<BattleFieldObject> objects) {
        gridPanel.setModule(module);
        gridPanel.initObjects(objects);
        gridPanel.afterInit();
    }

    @Override
    public void render(float delta) {

        Gdx.gl20.glEnable(GL11.GL_POINT_SMOOTH);
        Gdx.gl20.glHint(GL11.GL_POINT_SMOOTH_HINT, GL20.GL_NICEST);
        Gdx.gl20.glEnable(GL11.GL_LINE_SMOOTH);
        Gdx.gl20.glHint(GL11.GL_LINE_SMOOTH_HINT, GL20.GL_NICEST);
        Gdx.gl20.glEnable(GL11.GL_POLYGON_SMOOTH);
        Gdx.gl20.glHint(GL11.GL_POLYGON_SMOOTH_HINT, GL20.GL_NICEST);

        // getBatch().shaderFluctuation(delta);
        if (speed != null) {
            delta = delta * speed;
        }
        doFlagsOnInput();
        super.render(delta);

        InputController.cameraMoved = false;
    }

    private void doFlagsOnInput() {
        if (Flags.isDevEnabled() && !Cinematics.ON)
            if (DC_Game.game != null) {
                if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
                    if (Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT)) {
                        DC_Game.game.setDebugMode(!DC_Game.game.isDebugMode());

                    } else {
                        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                            //                            CoreEngine.setCinematicMode(!CoreEngine.isFootageMode());
                            boolean visionDebugMode = DC_Game.game.getVisionMaster().isVisionDebugMode();
                            DC_Game.game.getVisionMaster().setVisionDebugMode(!visionDebugMode);
                        }
                        if (Gdx.input.isKeyPressed(Input.Keys.TAB)) {
                            DC_Game.game.getVisionMaster().refresh();
                            GuiEventManager.trigger(UPDATE_GUI);
                        }
                    }
                }

            }
    }

    @Override
    protected boolean isTooltipsOn() {
        if (TownPanel.getActiveInstance() != null) {
            return false;
        }
        return super.isTooltipsOn();
    }

    public InputController getController() {
        return controller;
    }

    public abstract GridPanel getGridPanel();

    public ParticleManager getParticleManager() {
        return particleManager;
    }
}
