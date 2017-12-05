package main.libgdx.gui.panels.dc.actionpanel;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import main.libgdx.anims.ActorMaster;
import main.libgdx.bf.light.ShadowMap.SHADE_LIGHT;
import main.libgdx.bf.mouse.BattleClickListener;
import main.libgdx.gui.UiAnimator;
import main.libgdx.gui.controls.radial.RadialMenu;
import main.libgdx.gui.panels.dc.ValueContainer;
import main.libgdx.screens.DungeonScreen;
import main.libgdx.shaders.DarkShader;
import main.libgdx.shaders.GrayscaleShader;
import main.libgdx.texture.TextureCache;
import main.system.images.ImageManager.BORDER;

import static main.libgdx.gui.UiAnimator.UI_ACTIONS.SCALE_ACTION_ICON;

public class ActionValueContainer extends ValueContainer {

    protected static TextureRegion lightUnderlay = TextureCache.getOrCreateR(
     SHADE_LIGHT.LIGHT_EMITTER.getTexturePath());
    static TextureRegion overlay = TextureCache.getOrCreateR
     (BORDER.NEO_INFO_SELECT_HIGHLIGHT_SQUARE_64.getImagePath());
    private static ActionValueContainer lastPressed;
    private static boolean darkened;
    private final float scaleByOnHover = (new Float(64) / ActionPanelController.IMAGE_SIZE) - 1;
    protected Runnable clickAction;
    protected boolean valid = true;
    protected boolean hover;
    protected TextureRegion underlay;
    protected float underlayOffsetX;
    protected float underlayOffsetY;

    //overlay!
    public ActionValueContainer(boolean valid, TextureRegion texture, Runnable action) {
        super(texture);
        bindAction(action);
        this.valid = valid;
    }

    public ActionValueContainer(TextureRegion texture, Runnable action) {
        super(texture);
        bindAction(action);

    }

    public ActionValueContainer(TextureRegion texture, String value, Runnable action) {
        super(texture, value);
        bindAction(action);

    }

    public ActionValueContainer(String name, String value, Runnable action) {
        super(name, value);
        bindAction(action);

    }

    public static boolean isDarkened() {
        return darkened;
    }

    public static void setDarkened(boolean darkened) {
        ActionValueContainer.darkened = darkened;
    }

    public static ActionValueContainer getLastPressed() {
        return lastPressed;
    }

    public static void setLastPressed(ActionValueContainer lastPressed) {
        ActionValueContainer.lastPressed = lastPressed;
        if (lastPressed == null)
            main.system.auxiliary.log.LogMaster.log(1,
             "lastPressed to null ");
        else
            main.system.auxiliary.log.LogMaster.log(1, lastPressed.getName() +
             " pressed " + lastPressed);
    }

    public void bindAction(Runnable action) {
        if (action != null) {
            clickAction = action::run;
        }
        addListener(new BattleClickListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (!valid)
                    return true;
                if (isScaledOnHover())
                    ActorMaster.addScaleAction(imageContainer.getActor(), getImageScaleX() - scaleByOnHover, getImageScaleY() - scaleByOnHover,
                     UiAnimator.getDuration(SCALE_ACTION_ICON));
                setLastPressed(ActionValueContainer.this);
                return super.touchDown(event, x, y, pointer, button);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (!valid)
                    return;
                if (isScaledOnHover())
                    ActorMaster.addScaleAction(imageContainer.getActor(), getImageScaleX(), getImageScaleY(),
                     UiAnimator.getDuration(SCALE_ACTION_ICON));
                super.touchUp(event, x, y, pointer, button);
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!valid)
                    return;
                super.clicked(event, x, y);
            }

            @Override
            public boolean mouseMoved(InputEvent event, float x, float y) {
                if (!valid)
                    return super.mouseMoved(event, x, y);
                if (hover)
                    return false;
                if (getLastPressed() == ActionValueContainer.this)
                    return true;
                setLastPressed(null);
                hover = true;
                setZIndex(Integer.MAX_VALUE);
                if (isScaledOnHover())
                    ActorMaster.addScaleAction(imageContainer.getActor(), getImageScaleX() + scaleByOnHover, getImageScaleY() + scaleByOnHover,
                     UiAnimator.getDuration(SCALE_ACTION_ICON));

                return super.mouseMoved(event, x, y);
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (!valid) return;
                if (!hover) {
                    hover = true;
                    if (isScaledOnHover())
                        ActorMaster.addScaleAction(imageContainer.getActor(),
                         getImageScaleX() + scaleByOnHover, getImageScaleY() + scaleByOnHover, UiAnimator.getDuration(SCALE_ACTION_ICON));
                    if (getLastPressed() != ActionValueContainer.this)
                        setLastPressed(null);
                }
                super.enter(event, x, y, pointer, fromActor);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                hover = false;
                super.exit(event, x, y, pointer, toActor);
                if (isScaledOnHover())
                    ActorMaster.addScaleAction(imageContainer.getActor(), getImageScaleX(),
                     getImageScaleY(),
                     UiAnimator.getDuration(SCALE_ACTION_ICON));
                if (getLastPressed() != ActionValueContainer.this)
                    setLastPressed(null);
            }
        });
    }

    protected boolean isScaledOnHover() {
        return true;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (hover) {
            drawLightUnderlay(batch);

        }
        if (underlay != null)
            if (underlayOffsetX != 0 || underlayOffsetY != 0) {
                batch.setColor(getRadial().getColor());
                batch.draw(underlay, getX() + underlayOffsetX, getY() + underlayOffsetY);
            }

        ShaderProgram shader = null;
        if (isDarkened()) {
            shader = batch.getShader();
            batch.setShader(DarkShader.getShader());
        }
        if (!valid) {
            shader = batch.getShader();
            batch.setShader(GrayscaleShader.getGrayscaleShader());
        }
        super.draw(batch, parentAlpha);
        batch.setShader(shader);
//        if (hover) {
//            batch.draw(overlay, (getParent().getX() + getX() + (imageContainer.getActorWidth() -
//              overlay.getRegionWidth()) / 2),
//             (getY() + (imageContainer.getActorHeight() - overlay.getRegionHeight()) / 2)
//             , overlay.getRegionWidth() * getImageContainer().getActor().getScaleX(),
//             overlay.getRegionHeight() * getImageContainer().getActor().getScaleY()
//            );
//        }
    }

    public RadialMenu getRadial() {
        if (DungeonScreen.getInstance().
         getGuiStage() == null)
            return null;
        return DungeonScreen.getInstance().
         getGuiStage().getRadial();
    }

    protected void drawLightUnderlay(Batch batch) {
        float regionHeight = lightUnderlay.getRegionHeight() * getUnderlayScale() * getImageContainer().getActor().getScaleY()
         * getImageContainer().getActor().getScaleY();
        float regionWidth = lightUnderlay.getRegionWidth() * getUnderlayScale() *
         getImageContainer().getActor().getScaleX()
         * getImageContainer().getActor().getScaleX();
        batch.draw(lightUnderlay,
         (getParent().getX() + getX() + (imageContainer.getActorWidth() -
          regionWidth) / 2),
         (getY() + (imageContainer.getActorHeight() - regionHeight) / 2)

         , regionWidth,
         regionHeight
        );

    }

    public float getUnderlayScale() {
        return 1f;
    }

    public Runnable getClickAction() {
        return clickAction;
    }

    @Override
    protected void init(TextureRegion texture, String name, String value) {
        super.init(texture, name, value);
        clickAction = () -> {
        };
        addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                clickAction.run();
            }
        });
    }
}
