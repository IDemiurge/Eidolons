package eidolons.libgdx.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.battlecraft.logic.dungeon.module.Module;
import eidolons.game.module.dungeoncrawl.explore.RealTimeGameLoop;
import eidolons.libgdx.anims.fullscreen.Screenshake;
import eidolons.libgdx.anims.sprite.SpriteAnimationFactory;
import eidolons.libgdx.bf.generic.FadeImageContainer;
import eidolons.libgdx.bf.mouse.InputController;
import eidolons.libgdx.stage.ChainedStage;
import eidolons.libgdx.stage.GenericGuiStage;
import eidolons.libgdx.stage.camera.CameraMan;
import eidolons.libgdx.texture.TextureCache;
import eidolons.libgdx.texture.TextureManager;
import eidolons.system.audio.DC_SoundMaster;
import eidolons.system.options.GraphicsOptions;
import eidolons.system.options.OptionsMaster;
import main.system.GuiEventManager;
import main.system.datatypes.DequeImpl;

import java.util.ArrayList;
import java.util.List;

import static eidolons.libgdx.texture.TextureCache.getOrCreateR;
import static main.system.GuiEventType.RESET_DUNGEON_BACKGROUND;
import static main.system.GuiEventType.UPDATE_DUNGEON_BACKGROUND;

/**
 * Created by JustMe on 2/3/2018.
 */
public abstract class GameScreen extends ScreenWithVideoLoader {

    public InputController controller;
    protected ChainedStage dialogsStage = null;
    protected DC_SoundMaster soundMaster;
    protected ShaderProgram bufferedShader;
    protected Float speed;
    protected TextureRegion backTexture;
    protected GenericGuiStage guiStage;
    protected RealTimeGameLoop realTimeGameLoop;
    protected List<Screenshake> shakes = new ArrayList<>();
    protected CameraMan cameraMan;
    protected String backgroundPath;
    protected String previousBg;
    protected FadeImageContainer background;

    public GameScreen() {

    }

    public String getBackgroundPath() {
        return backgroundPath;
    }

    protected void preBindEvent() {
        GuiEventManager.bind(UPDATE_DUNGEON_BACKGROUND, param -> {
            final String path = (String) param.get();
            setBackground(path);
        });
        GuiEventManager.bind(RESET_DUNGEON_BACKGROUND, param -> {
            resetBackground();
        });
    }

    @Override
    public void render(float delta) {
        if (!shakes.isEmpty()) {
            for (Screenshake shake : new ArrayList<>(shakes)) {
                try {
                    if (!shake.update(delta, getCam(), getCameraMan().getCameraCenter())) {
                        shakes.remove(shake);
                    }
                } catch (Exception e) {
                    main.system.ExceptionMaster.printStackTrace(e);
                    shakes.remove(shake);
                }
            }
            getCam().update();
        }
        super.render(delta);
    }

    protected void resetBackground() {
        setBackground(previousBg);
    }


    protected boolean isFadeImageBackground() {
        return true;
    }

    public void setBackground(String path) {
        if (path == null) {
            return;
        }
        previousBg = this.backgroundPath;
        this.backgroundPath = path.trim();

            if (backgroundPath.endsWith(".txt")) {
                backgroundSprite = SpriteAnimationFactory.getSpriteAnimation(backgroundPath, false);
            }
        // if (!ImageManager.isImageFile(path)) {
        //     return;
        // }
        if (isFadeImageBackground()) {
            if (background == null) {
                background = new FadeImageContainer(backgroundPath);
            } else
                background.setImage(backgroundPath);
            backgroundSprite = null;
            return;
        }

        TextureRegion texture = getOrCreateR(backgroundPath);
        if (texture.getTexture() != TextureCache.getMissingTexture()) {
            backTexture = texture;
            backgroundSprite = null;
        }

        if (OptionsMaster.getGraphicsOptions().

                getBooleanValue(GraphicsOptions.GRAPHIC_OPTION.SPRITE_CACHE_ON)) {
            TextureManager.initBackgroundCache(backTexture);
        }

    }

    public boolean isOpaque() {
        return false;
    }


    public InputController getController() {
        return controller;
    }

    protected void initGl() {
        GL30 gl = Gdx.graphics.getGL30();
        if (gl != null) {
            gl.glEnable(GL30.GL_BLEND);
            gl.glEnable(GL30.GL_TEXTURE_2D);
            gl.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
        } else {
            GL20 gl20 = Gdx.graphics.getGL20();
            gl20.glEnable(GL20.GL_BLEND);
            gl20.glEnable(GL20.GL_TEXTURE_2D);
            gl20.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        }
    }

    public TextureRegion getBackTexture() {
        return backTexture;
    }

    public RealTimeGameLoop getRealTimeGameLoop() {
        return realTimeGameLoop;
    }

    public void setRealTimeGameLoop(RealTimeGameLoop realTimeGameLoop) {
        this.realTimeGameLoop = realTimeGameLoop;
    }

    public OrthographicCamera getCamera() {
        return getCam();
    }

    public CameraMan getCameraMan() {
        return cameraMan;
    }

    public OrthographicCamera getCam() {
        if (cameraMan != null) {
            return cameraMan.getCam();
        }
        return null;
    }

    public void setCam(OrthographicCamera cam) {
        cameraMan = new CameraMan(cam, this);
    }

    public void cameraStop(boolean full) {
        cameraMan.cameraStop(full);
    }

    public GenericGuiStage getGuiStage() {
        return null;
    }

    public void moduleEntered(Module module, DequeImpl<BattleFieldObject> objects) {
    }
}
