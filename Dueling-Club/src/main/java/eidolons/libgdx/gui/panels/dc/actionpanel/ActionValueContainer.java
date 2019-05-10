package eidolons.libgdx.gui.panels.dc.actionpanel;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import eidolons.libgdx.GdxImageMaster;
import eidolons.libgdx.anims.ActorMaster;
import eidolons.libgdx.bf.light.ShadowMap.SHADE_CELL;
import eidolons.libgdx.bf.mouse.BattleClickListener;
import eidolons.libgdx.gui.UiMaster;
import eidolons.libgdx.gui.controls.radial.RadialMenu;
import eidolons.libgdx.gui.generic.ValueContainer;
import eidolons.libgdx.screens.DungeonScreen;
import eidolons.libgdx.shaders.DarkGrayscaleShader;
import eidolons.libgdx.shaders.DarkShader;
import eidolons.libgdx.texture.TextureCache;
import main.system.images.ImageManager.BORDER;

import static eidolons.libgdx.gui.UiMaster.UI_ACTIONS.SCALE_ACTION_ICON;

public class ActionValueContainer extends ValueContainer {

    protected static TextureRegion lightUnderlay = TextureCache.getOrCreateR(
            SHADE_CELL.LIGHT_EMITTER.getTexturePath());
    static TextureRegion overlay = TextureCache.getOrCreateR
            (BORDER.NEO_INFO_SELECT_HIGHLIGHT_SQUARE_64.getImagePath());
    private static ActionValueContainer lastPressed;
    private static boolean darkened;
    private final float scaleByOnHover = (new Float(64) / ActionPanel.IMAGE_SIZE) - 1;
    private   String path;
    protected Runnable clickAction;
    protected boolean valid = true;
    protected boolean hover;
    protected TextureRegion underlay;
    protected float underlayOffsetX;
    protected float underlayOffsetY;
    private float size = UiMaster.getIconSize();
    private RadialMenu customRadialMenu;
    private boolean scaledOnHover = true;

    //overlay!
    public ActionValueContainer(boolean valid, TextureRegion texture, Runnable action) {
        this(texture, action);
        this.valid = valid;
    }

    public ActionValueContainer(TextureRegion texture, Runnable action) {

        this(texture, null, action);
    }

    public ActionValueContainer(TextureRegion texture, String value, Runnable action) {
        super(texture, value);
        bindAction(action);

    }

    public ActionValueContainer(int size, boolean valid, String path,
                                Runnable runnable) {
        this(valid, TextureCache.getOrCreateR(path), runnable);
        this.size = size;
        this.path = path;
        initSize();
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
    }

    protected void initSize() {
        float size = getSize();
        if (imageContainer.getActor().getContent().getDrawable().getMinWidth() > 100) {
           if (path != null)
                if (path.contains("bf")) //TODO IGG_HACK
                {
                    imageContainer.getActor().setImage(GdxImageMaster.getSizedImagePath(path, (int) size));
                    scaledOnHover = false;

                }
        }
        if (scaledOnHover)
            overrideImageSize(size, size);
        imageContainer.top().right();
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
                            UiMaster.getDuration(SCALE_ACTION_ICON));
                setLastPressed(ActionValueContainer.this);
                return super.touchDown(event, x, y, pointer, button);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (!valid)
                    return;
                if (isScaledOnHover())
                    ActorMaster.addScaleAction(imageContainer.getActor(), getImageScaleX(), getImageScaleY(),
                            UiMaster.getDuration(SCALE_ACTION_ICON));
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
                    scaleUp();

                return super.mouseMoved(event, x, y);
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (!valid) return;
                if (!hover) {
                    hover = true;
                    if (isScaledOnHover())
                        scaleUp();
                    if (getLastPressed() != ActionValueContainer.this)
                        setLastPressed(null);
                }
                super.enter(event, x, y, pointer, fromActor);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                hover = false;
                super.exit(event, x, y, pointer, toActor);

                if (isScaledOnHover()) {
                    scaleDown();
                }
                if (getLastPressed() != ActionValueContainer.this)
                    setLastPressed(null);
            }
        });
    }

    public void scaleUp() {
        imageContainer.getActor().clearActions();
        ActorMaster.addScaleAction(imageContainer.getActor(), getImageScaleX() + scaleByOnHover, getImageScaleY() + scaleByOnHover,
                UiMaster.getDuration(SCALE_ACTION_ICON));

        ActorMaster.addMoveToAction(imageContainer.getActor(),
                imageContainer.getActor().getX(),
                6, 0.25f);
    }

    public void scaleDown() {
        imageContainer.getActor().clearActions();
        ActorMaster.addScaleAction(imageContainer.getActor(), getImageScaleX(),
                getImageScaleY(),
                UiMaster.getDuration(SCALE_ACTION_ICON));

        ActorMaster.addMoveToAction(imageContainer.getActor(),
                imageContainer.getActor().getX(),
                0, 0.25f);
    }

    protected boolean isScaledOnHover() {

        return scaledOnHover;
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

        ShaderProgram shader = batch.getShader();
        if (isDarkened()) {
            shader = batch.getShader();
            batch.setShader(DarkShader.getDarkShader());
        }
        if (!valid) {
            shader = batch.getShader();
            batch.setShader(
                    DarkShader.getDarkShader()
//             GrayscaleShader.getGrayscaleShader()
            );
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

    public void setCustomRadialMenu(RadialMenu customRadialMenu) {
        this.customRadialMenu = customRadialMenu;
    }

    public RadialMenu getRadial() {
        if (customRadialMenu != null)
            return customRadialMenu;
        if (DungeonScreen.getInstance().
                getGuiStage() == null)
            return null;
        return DungeonScreen.getInstance().
                getGuiStage().getRadial();
    }

    protected void drawLightUnderlay(Batch batch) {
        if (getParent() == null)
            return;
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

    public float getSize() {
        if (size == 0) {
            return UiMaster.getIconSize(false);
        }
        return size;
    }

    public void setSize(float size) {
        this.size = size;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
}