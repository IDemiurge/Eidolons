package eidolons.libgdx.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import eidolons.game.battlecraft.logic.meta.igg.IGG_Images;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech.Cinematics;
import eidolons.game.core.Eidolons;
import eidolons.game.core.game.DC_Game;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.anims.sprite.SpriteAnimationFactory;
import eidolons.libgdx.bf.BFDataCreatedEvent;
import eidolons.libgdx.bf.GridMaster;
import eidolons.libgdx.bf.grid.GridPanel;
import eidolons.libgdx.bf.mouse.InputController;
import eidolons.libgdx.gui.menu.selection.hero.HeroSelectionPanel;
import eidolons.libgdx.gui.panels.headquarters.town.TownPanel;
import eidolons.libgdx.stage.BattleGuiStage;
import eidolons.libgdx.stage.GenericGuiStage;
import eidolons.libgdx.stage.GuiStage;
import eidolons.libgdx.stage.StageX;
import eidolons.libgdx.texture.Sprites;
import eidolons.libgdx.texture.TextureCache;
import eidolons.libgdx.texture.TextureManager;
import eidolons.macro.MacroGame;
import eidolons.system.options.GraphicsOptions;
import eidolons.system.options.OptionsMaster;
import main.game.bf.Coordinates;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.log.Chronos;
import main.system.launch.CoreEngine;

import static com.badlogic.gdx.graphics.GL20.GL_NICEST;
import static eidolons.libgdx.texture.TextureCache.getOrCreateR;
import static main.system.GuiEventType.*;
import static org.lwjgl.opengl.GL11.*;

public abstract class GenericDungeonScreen extends GameScreen{
    protected StageX gridStage;
    protected GridPanel gridPanel;

    protected void preBindEvent() {

        GuiEventManager.bind(UPDATE_DUNGEON_BACKGROUND, param -> {
            final String path = (String) param.get();
            setBackground(path);
        });
    }

    protected abstract GenericGuiStage createGuiStage();

    protected abstract GridPanel createGrid(BFDataCreatedEvent param);

    protected void bindEvents() {
        GuiEventManager.bind(GuiEventType.CAMERA_LAPSE_TO, p -> {
            getCam().position.set(GridMaster.getCenteredPos((Coordinates) p.get()), 0);

        });
    }

    protected void checkGraphicsUpdates() {
        if (backTexture == null && backgroundSprite == null) {
            String path = null;
            try {
                path = DC_Game.game.getDungeonMaster().getDungeonWrapper().getMapBackground();
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
            main.system.auxiliary.log.LogMaster.dev("Setting dc background " + path);
            Chronos.mark("bg");
            setBackground(path);
            Chronos.logTimeElapsedForMark("bg");
        }
    }

    protected void setBackground(String path) {
        if (path == null) {
            return;
        }
        if (!TextureCache.isImage(path)) {
            if (path.endsWith(".txt")) {
                backgroundSprite = SpriteAnimationFactory.getSpriteAnimation(path, false);
            }
            return;
        }

        TextureRegion texture = getOrCreateR(path);
        if (texture.getTexture() != TextureCache.getEmptyTexture())
            backTexture = texture;

        if (OptionsMaster.getGraphicsOptions().getBooleanValue(GraphicsOptions.GRAPHIC_OPTION.SPRITE_CACHE_ON)) {
            TextureManager.initBackgroundCache(backTexture);
        }
    }

    protected void initBackground() {
        String path = IGG_Images.getBackground();
        setBackground(path);
    }

    public void updateGui() {
        checkGraphicsUpdates();
    }

    protected abstract InputProcessor createInputController();


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

    }

    @Override
    public void render(float delta) {

        Gdx.gl20.glEnable(GL_POINT_SMOOTH);
        Gdx.gl20.glHint(GL_POINT_SMOOTH_HINT, GL_NICEST);
        Gdx.gl20.glEnable(GL_LINE_SMOOTH);
        Gdx.gl20.glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        Gdx.gl20.glEnable(GL_POLYGON_SMOOTH);
        Gdx.gl20.glHint(GL_POLYGON_SMOOTH_HINT, GL_NICEST);

        batch.shaderFluctuation(delta);
        if (speed != null) {
            delta = delta * speed;
        }
        doFlagsOnInput();
        super.render(delta);
    }

    private void doFlagsOnInput() {
        if (CoreEngine.isDevEnabled() && !Cinematics.ON)
            if (DC_Game.game != null) {
                if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
                    if (Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT)) {
                        DC_Game.game.setDebugMode(!DC_Game.game.isDebugMode());
                    } else {
                        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                            CoreEngine.setCinematicMode(!CoreEngine.isFootageMode());
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

}
