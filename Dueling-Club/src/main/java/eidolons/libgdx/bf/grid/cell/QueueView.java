package eidolons.libgdx.bf.grid.cell;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.libgdx.GdxImageMaster;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.anims.ActionMaster;
import eidolons.libgdx.bf.GridMaster;
import eidolons.libgdx.bf.generic.ImageContainer;
import eidolons.libgdx.bf.overlays.HpBar;
import eidolons.libgdx.gui.generic.btn.ButtonStyled;
import eidolons.libgdx.gui.panels.dc.topleft.atb.AtbPanel;
import eidolons.libgdx.gui.tooltips.SmartClickListener;
import eidolons.libgdx.gui.tooltips.UnitViewTooltipFactory;
import eidolons.libgdx.texture.Images;
import eidolons.libgdx.texture.TextureCache;
import main.content.enums.GenericEnums;
import main.content.enums.rules.VisionEnums.OUTLINE_TYPE;
import main.data.filesys.PathFinder;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.launch.Flags;

import java.util.function.Supplier;

import static eidolons.libgdx.anims.ActionMaster.*;
import static main.system.GuiEventType.ADD_OR_UPDATE_INITIATIVE;

/**
 * Created by JustMe on 4/6/2018.
 */
public class QueueView extends UnitView {
    private static final float LINE_Y_INACTIVE = -40;
    private static final float LINE_Y_ACTIVE = -75;
    private static final float VIEW_Y_INACTIVE = -30;
    private static final float VIEW_Y_ACTIVE = -14;
    private static final float PORTRAIT_Y_INACTIVE = -4;
    private static final float PORTRAIT_Y_ACTIVE = 14;
    private final Image activeMarker;
    private final Image emblemBg;
    protected int initiativeIntVal;
    protected Label initiativeLabel;
    protected ImageContainer atbBg;
    protected boolean queueMoving = true;//queueMoving, temporary. 
    protected Actor parentView;
    private ImageContainer roundBorder;
    private final ImageContainer verticalLine;

    protected QueueView(UnitViewOptions o, int curId) {
        super(o, curId);
        addActor(verticalLine = new ImageContainer(Images.SEPARATOR_NARROW_VERTICAL));
        verticalLine.setAlphaTemplate(GenericEnums.ALPHA_TEMPLATE.SUN);
        verticalLine.setColor(1, 1, 1, 0);
//        getTeamColor()
        GdxMaster.center(verticalLine);
        addActor(activeMarker = new Image(ButtonStyled.STD_BUTTON.PULL.getTexture()));
        activeMarker.getColor().a=0;
        init(o.getClockValue());
        addActor(emblemBg = new Image(TextureCache.getOrCreateR(Images.INTENT_ICON_BG)));
        emblemBg.setPosition( (AtbPanel.imageSize-30)/2,  80);

        initEmblem(o.getEmblem());
        emblemImage.setPosition(emblemBg.getX(),emblemBg.getY());
        verticalLine.setZIndex(0);

        emblemImage.setZIndex(9234);
    }

    @Override
    protected float getEmblemSize() {
        return 40;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        emblemLighting.setVisible(false);
        modeImage.setPosition(78, -20);
        modeImage.setZIndex(654);
        verticalLine.setX(GdxMaster.centerWidth(verticalLine)+5);
        highlight.setX(portrait.getX()-4);
        roundBorder.setY(portrait.getY());
        highlight.setY(portrait.getY()-3);
        if (!active){
            hpBar.setY(portrait.getY()+6);
        } else {
            hpBar.setY(portrait.getY()+1);
        }
        hpBar.setY(-5);
        hpBar.setX(-6);

        atbBg.setY(-24);
        activeMarker.setY(-32);
        activeMarker.setZIndex(6534);
        activeMarker.setX(GdxMaster.centerWidth(activeMarker)+4);

        initiativeLabel.setPosition(
                atbBg.getX() +
                        (atbBg.getWidth() / 2 - initiativeLabel.getWidth() * 1.63f - 11),
                atbBg.getY() +
                        (atbBg.getHeight() / 2 - initiativeLabel.getHeight() / 2));

    }

    protected void init(int clockVal) {
        this.initiativeIntVal = clockVal;
        initiativeLabel = new Label(
                "", getInitiativeFontStyle());
        atbBg = new ImageContainer(PathFinder.getComponentsPath()
                + "dc/atb/readiness bg.png");
        roundBorder = new ImageContainer(PathFinder.getComponentsPath()
                + "dc/borders/circle border down 96.png");
        addActor(atbBg);
        addActor(initiativeLabel);
        addActor(roundBorder);
        highlight.setZIndex(999);

        addListener(createListener());
        setInitialized(true);
    }

    public void hoverOff() {
        hover(false);
    }

    public void hoverOn() {
        hover(true);
    }

    public void hover(boolean on) {
        if (isHovered() != on) {
            return;
        }
        for (Actor allChild : GdxMaster.getAllChildren(this)) {
            for (Action action : allChild.getActions()) {
                if (action instanceof MoveToAction) {
                    allChild.removeAction(action);
                }
            }
        }
        float y = 0;
        float y1 = 0;
        if (active) {
            y = VIEW_Y_ACTIVE;
            y1 = LINE_Y_ACTIVE;
        } else {
            y = VIEW_Y_INACTIVE;
            y1 = LINE_Y_INACTIVE;
            if (on) {
                addFadeInAction(verticalLine, 1f * AtbPanel.getSpeedFactor());
            } else {
                addFadeOutAction(verticalLine, 1f * AtbPanel.getSpeedFactor());
            }
        }
        if (on) {
            y -= 20;
            y1 -= 30;
        } else {
            y += 20;
            y1 += 30;
        }
        MoveToAction move = getMoveToAction(0, y, 1f * AtbPanel.getSpeedFactor());
        ActionMaster.addAction(this, move);
        move = getMoveToAction(verticalLine.getX(), y1, 1f * AtbPanel.getSpeedFactor());
        ActionMaster.addAction(verticalLine, move);
    }

    public void setActive(boolean active) {
        //gdx review - why is this getting called more than one?
        if (this.active==active) {
            return;
        }
        // ActionMaster.addBlockingAction(this, ()-> queueMoving);
        this.active = active;
        //we would need a delay here? elsewise...
        if (active) {
            addFadeInAction(verticalLine, 1f * AtbPanel.getSpeedFactor());
            addFadeInAction(activeMarker, 1f * AtbPanel.getSpeedFactor());
            addMoveToAction(this, portrait.getX(), PORTRAIT_Y_ACTIVE, 1f * AtbPanel.getSpeedFactor());
            addMoveToAction(verticalLine, verticalLine.getX(), LINE_Y_ACTIVE, 1f * AtbPanel.getSpeedFactor());
            verticalLine.setVisible(true);
        } else {
            addFadeOutAction(verticalLine, 1f * AtbPanel.getSpeedFactor());
            addFadeOutAction(activeMarker, 1f * AtbPanel.getSpeedFactor());
            addMoveToAction(portrait, portrait.getX(), PORTRAIT_Y_INACTIVE, 1f * AtbPanel.getSpeedFactor());
            addMoveToAction(verticalLine, verticalLine.getX(), LINE_Y_INACTIVE, 1f * AtbPanel.getSpeedFactor());
        }
    }

    protected void updateVisible() {
//        if (getOutline() != null || isHovered()) {
//            initiativeLabel.setVisible(false);
//            atbBg.setVisible(false);
//        } else {
//            initiativeLabel.setVisible(true);
//            atbBg.setVisible(true);
//        }

    }

    @Override
    protected void sizeChanged() {
        super.sizeChanged();

        if (initiativeLabel != null) {
            if (AtbPanel.isLeftToRight())
                atbBg.setPosition(GdxMaster.right(atbBg) + 6, -5);
        }
        if (!GridMaster.isHpBarsOnTop())
            if (hpBar != null)
                hpBar.setPosition(GdxMaster.centerWidth(hpBar) - 5, -hpBar.getHeight() / 2);
        if (isInitialized())
            reset();

        //        Image image = clockImage;
        //        if (image != null) {
        //            image.setScaleX(getScaledWidth());
        //            image.setScaleY(getScaledHeight());
        //        }
    }

    public int getInitiativeIntVal() {
        return initiativeIntVal;
    }

    public boolean isQueueMoving() {
        return queueMoving;
    }

    public void setQueueMoving(boolean queueMoving) {
        this.queueMoving = queueMoving;
        GuiEventManager.trigger(ADD_OR_UPDATE_INITIATIVE, this);
    }

    public void updateInitiative(Integer val) {
        initiativeIntVal = val;
        if (getOutline() == null)
            initiativeLabel.setText(String.valueOf(val));
        else
            initiativeLabel.setText("?");

        initiativeLabel.setStyle(
                getInitiativeFontStyle());
        initiativeLabel.setPosition(
                atbBg.getX() + (atbBg.getWidth() / 2 - initiativeLabel.getWidth() / 2),
                atbBg.getY() + (atbBg.getHeight() / 2 - initiativeLabel.getHeight() / 2));

        GuiEventManager.trigger(ADD_OR_UPDATE_INITIATIVE, this);
    }

    public void setInitiative(int initiative) {
        //TODO need it due to some glitch in updates..
        initiativeLabel.setText(String.valueOf(initiative));
    }

    public LabelStyle getInitiativeFontStyle() {
        return StyleHolder.getSizedColoredLabelStyle(
                0.3f,
                StyleHolder.ALT_FONT, 18,
                getTeamColor());
    }

    protected void checkResetOutline(float delta) {
        if (!getParentView().isVisible()) {
            setDefaultTexture();
            return;
        }
        super.checkResetOutline(delta);
    }

    protected void setDefaultTexture() {
        if (getUserObject() == null) {
            return;
        }
        if (isMainHero()) {
            return;
        }
        if (getUserObject().isWater()) {
            return;
        }
        setOutline(getDefaultTextureSized());
        setPortraitTexture(getOutline());
    }


    protected boolean isHpBarVisible() {
        return true;
    }

    public void setPortraitTexture(TextureRegion textureRegion) {
        getPortrait().setTexture(TextureCache.getOrCreateTextureRegionDrawable(textureRegion));
    }

    public TextureRegion processPortraitTexture(String path) {
        TextureRegion  region = TextureCache.getOrCreateR(path);
        if (Flags.isIDE()) {
            GdxImageMaster.round(path, true);
        }
        Texture texture = GdxImageMaster.size(GdxImageMaster.getRoundedPath(path),
                AtbPanel.imageSize, Flags.isIDE());

        if (texture == null)
            return region;
        return new TextureRegion(texture);
    }

    public void setOutlineSupplier(Supplier<TextureRegion> outlineSupplier) {
        this.outlineSupplier = outlineSupplier;
    }

    public void setHpBar(HpBar hpBar) {
        if (this.hpBar != null) {
            this.hpBar.remove();
        }
        this.hpBar = hpBar;
        //        if (!GridPanel.isHpBarsOnTop()) {
        addActor(hpBar);
        hpBar.setPosition(GdxMaster.centerWidth(hpBar) - 5, -hpBar.getHeight() / 2);
        //        }
        hpBar.setQueue(true);
    }

    @Override
    public void setBorder(TextureRegion texture) {
        super.setBorder(texture);
        if (getHpBar() == null)
            getHpBar().setZIndex(Integer.MAX_VALUE);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " for " + name;
    }

    public Actor getParentView() {
        return parentView;
    }

    public void setParentView(Actor parentView) {
        this.parentView = parentView;
    }

    public void setInitiativeLabelText(String text) {
        initiativeLabel.setText(text);
    }

    public TextureRegion getDefaultTextureSized() {
        if (defaultTexture == null) {
            //TODO EA check
            String path = OUTLINE_TYPE.UNKNOWN.getImagePath();
            if (Flags.isIDE()) {
                GdxImageMaster.round(path, true);
            }
            return new TextureRegion(GdxImageMaster.size(GdxImageMaster.getRoundedPath(path),
                    AtbPanel.imageSize, Flags.isIDE()));
        }
        return defaultTexture;
    }


    @Override
    public BattleFieldObject getUserObject() {
        return (BattleFieldObject) parentView.getUserObject();
    }

    @Override
    public TextureRegion getDefaultTexture() {
        return TextureCache.getOrCreateR(OUTLINE_TYPE.UNKNOWN.getImagePath());
    }

    @Override
    public String getActiveHighlightImgPath() {
        return PathFinder.getComponentsPath()
                + "dc/borders/hl circle 96.png";
    }

    @Override
    protected void initSprite(UnitViewOptions o) {
        //TODO will it look good? Needs re-pos?
    }


    private EventListener createListener() {
        return new SmartClickListener(this) {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (getUserObject().isDetectedByPlayer()) {
                    getUserObject().getGame().getManager().setHighlightedObj(getUserObject());
                } else
                    GuiEventManager.trigger(GuiEventType.SHOW_TOOLTIP,
                            UnitViewTooltipFactory.create((UnitView) event.getListenerActor(), getUserObject()));
                super.enter(event, x, y, pointer, fromActor);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                getUserObject().getGame().getManager().setHighlightedObj(null);
                super.exit(event, x, y, pointer, toActor);
            }
        };
    }
}

