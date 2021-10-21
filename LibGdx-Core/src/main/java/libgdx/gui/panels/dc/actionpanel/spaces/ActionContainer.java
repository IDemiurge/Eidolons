package libgdx.gui.panels.dc.actionpanel.spaces;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import eidolons.content.consts.VisualEnums;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.handlers.active.Activator;
import eidolons.game.EidolonsGame;
import libgdx.anims.actions.ActionMasterGdx;
import libgdx.bf.mouse.BattleClickListener;
import libgdx.gui.UiMaster;
import libgdx.gui.controls.radial.RadialMenu;
import libgdx.gui.panels.dc.actionpanel.ActionPanel;
import libgdx.screens.dungeon.DungeonScreen;
import libgdx.shaders.DarkShader;
import libgdx.shaders.GrayscaleShader;
import libgdx.texture.TextureCache;
import main.system.images.ImageManager.BORDER;

import java.util.function.Supplier;

import static libgdx.gui.UiMaster.UI_ACTIONS.SCALE_ACTION_ICON;

public class ActionContainer extends FeatContainer {
//Gdx Review - are they all they could be? Used quite widely..

    protected static TextureRegion lightUnderlay = TextureCache.getOrCreateR(
            VisualEnums.SHADE_CELL.LIGHT_EMITTER.getTexturePath());
    static TextureRegion overlay = TextureCache.getOrCreateR
            (BORDER.NEO_INFO_SELECT_HIGHLIGHT_SQUARE_64.getImagePath());
    private static ActionContainer lastPressed;
    private static boolean darkened;

    private final float scaleByOnHover = (64f / ActionPanel.IMAGE_SIZE) - 1;

    private RadialMenu customRadialMenu;
    protected Runnable clickAction;
    private boolean valid = true;
    protected float underlayOffsetX;
    protected float underlayOffsetY;
    protected float size = UiMaster.getIconSize();
    protected TextureRegion underlay;

    //overlay!
    // public ActionContainer(boolean valid, TextureRegion texture, Runnable action) {
    //     this(texture, action);
    //     this.setValid(valid);
    // }

    // public ActionContainer(TextureRegion texture, Runnable action) {
    //     this(texture, null, action);
    // }

    // public ActionContainer(Supplier<Integer> charges,TextureRegion texture, String value, Runnable action) {
    //     super(charges, texture, value);
    //     bindAction(action);
    // }

    public ActionContainer(Supplier<Integer> charges, int size, boolean valid, String image, Runnable action) {
        super(charges, TextureCache.getSizedRegion(size, image), "name!");
        this.setValid(valid);
        this.size = size;
        bindAction(action);
    }

    public ActionContainer(TextureRegion texture, Runnable action) {
        super(()-> -1 , texture, "");
        bindAction(action);
    }
    // public ActionContainer(int size, boolean valid, String image, Runnable invokeClicked) {
    //     this(size, valid, TextureCache.getSizedRegion(size, image), invokeClicked);
    // }

    // public ActionContainer(int size, boolean valid, TextureRegion region,
    //                        Runnable runnable) {
    //     this(valid, region, runnable);
    //     this.size = size;
    // }


    public static boolean isDarkened() {
        return darkened;
    }

    public static void setDarkened(boolean darkened) {
        ActionContainer.darkened = darkened;
    }

    public static ActionContainer getLastPressed() {
        return lastPressed;
    }

    public static void setLastPressed(ActionContainer lastPressed) {
        ActionContainer.lastPressed = lastPressed;
    }

    protected void initSize() {
        overrideImageSize(getSize(), getSize());
        imageContainer.top().right();
    }

    public void bindAction(Runnable action) {
        if (action != null) {
            clickAction = action::run;
        }
        addListener(new BattleClickListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (!isValid())
                    return true;
                if (isScaledOnHover())
                    ActionMasterGdx.addScaleAction(imageContainer.getActor(), getImageScaleX() - scaleByOnHover, getImageScaleY() - scaleByOnHover,
                            UiMaster.getDuration(SCALE_ACTION_ICON));
                setLastPressed(ActionContainer.this);
                return super.touchDown(event, x, y, pointer, button);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (!isValid())
                    return;
                if (isScaledOnHover())
                    ActionMasterGdx.addScaleAction(imageContainer.getActor(), getImageScaleX(), getImageScaleY(),
                            UiMaster.getDuration(SCALE_ACTION_ICON));
                super.touchUp(event, x, y, pointer, button);
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!isValid())
                    return;
                super.clicked(event, x, y);
            }

            @Override
            public boolean mouseMoved(InputEvent event, float x, float y) {
                if (!isValid())
                    return super.mouseMoved(event, x, y);
                if (isHover())
                    return false;
                if (getLastPressed() == ActionContainer.this)
                    return true;
//                if (GdxMaster.getFirstParentOfClass(event.getTarget(),
//                        ActionValueContainer.class)!=null) {
//                    if (GdxMaster.getAncestors(event.getTarget()).size()>
//                            GdxMaster.getAncestors(event.getRelatedActor()).size()) {
//                        return false;
//                    }
//                }
                setLastPressed(null);
                setHover(true);
                setZIndex(Integer.MAX_VALUE);
                if (isScaledOnHover())
                    scaleUp();

                return super.mouseMoved(event, x, y);
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (!isValid()) return;
                if (!isHover()) {
                    setHover(true);
                    if (isScaledOnHover())
                        scaleUp();
                    if (getLastPressed() != ActionContainer.this)
                        setLastPressed(null);
                }
                super.enter(event, x, y, pointer, fromActor);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
//                if (GdxMaster.getFirstParentOfClass(event.getTarget(),
//                        ActionValueContainer.class)!=null) {
//                    if (GdxMaster.getAncestors(event.getTarget()).size()!=
//                            GdxMaster.getAncestors(event.getRelatedActor()).size()) {
//                        return;
//                    }
//                }
//                if (!event.getTarget().isTouchable()){
//                    return;
//                }
                setHover(false);
                super.exit(event, x, y, pointer, toActor);

                if (isScaledOnHover()) {
                    scaleDown();
                }
                if (getLastPressed() != ActionContainer.this)
                    setLastPressed(null);
            }
        });
    }

    public void scaleUp() {
        imageContainer.getActor().clearActions();
        ActionMasterGdx.addScaleAction(imageContainer.getActor(), getImageScaleX() + scaleByOnHover, getImageScaleY() + scaleByOnHover,
                UiMaster.getDuration(SCALE_ACTION_ICON));

        ActionMasterGdx.addMoveToAction(imageContainer.getActor(),
                imageContainer.getActor().getX(),
                6, 0.25f);
    }

    public void scaleDown() {
        imageContainer.getActor().clearActions();
        ActionMasterGdx.addScaleAction(imageContainer.getActor(), getImageScaleX(),
                getImageScaleY(),
                UiMaster.getDuration(SCALE_ACTION_ICON));

        ActionMasterGdx.addMoveToAction(imageContainer.getActor(),
                imageContainer.getActor().getX(),
                0, 0.25f);
    }

    protected boolean isScaledOnHover() {
        return true;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (isHover()) {
            drawLightUnderlay(batch);
        }
        if (underlay != null)
            if (underlayOffsetX != 0 || underlayOffsetY != 0) {
                batch.setColor(getRadial().getColor());
                batch.draw(underlay, getX() + underlayOffsetX, getY() + underlayOffsetY);
            }

        ShaderProgram shader = batch.getShader();
        if (isDarkened()) {
//            shader = batch.getShader();
            batch.setShader(DarkShader.getDarkShader());
        }
        if (!isValid()) {
//            shader = batch.getShader();
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

    public boolean isValid() {
        if (isBlocked())
            return false;
        return valid;
    }

    private boolean isBlocked() {
        return EidolonsGame.isActionBlocked((DC_ActiveObj) getUserObject());
    }

    @Override
    public void setUserObject(Object userObject) {
        super.setUserObject(userObject);
    }

    @Override
    protected Class<?> getUserObjectClass() {
        return DC_ActiveObj.class;
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
        if (isBlocked()) {
            Activator.cannotActivate_((DC_ActiveObj) getUserObject(), "Blocked");
            return null;
        }
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
                if (getClickAction() == null) {
                    return;
                }
                getClickAction().run();
            }
        });
    }

    public void reset(String img) {
        clearListeners();
        clearChildren();
        init(TextureCache.getOrCreateR(img), null, null);

    }

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
}
