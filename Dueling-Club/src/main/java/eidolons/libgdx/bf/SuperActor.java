package eidolons.libgdx.bf;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.GdxColorMaster;
import eidolons.libgdx.anims.ActionMaster;
import eidolons.libgdx.bf.mouse.InputController;
import main.content.enums.GenericEnums;

/**
 * Created by JustMe on 8/17/2017.
 */
public abstract class SuperActor extends Fluctuating implements
       CachedIgnoreActor, Borderable {
    protected Image border = null;
    protected TextureRegion borderTexture;
    protected boolean teamColorBorder;
    protected Color teamColor;
    protected float scaledWidth = 1;
    protected float scaledHeight = 1;
    protected boolean hovered;
    protected boolean active;
    protected int blendDstFunc, blendSrcFunc, blendDstFuncAlpha, blendSrcFuncAlpha;
    protected Boolean withinCamera;
    protected Runnable actionManger;
    private boolean hoverResponsive;

    public SuperActor() {
    }

    public SuperActor(GenericEnums.ALPHA_TEMPLATE alphaTemplate) {
        setAlphaTemplate(alphaTemplate);
    }

    @Override
    public TextureRegion getBorder() {
        return borderTexture;
    }

    @Override
    public void setBorder(TextureRegion texture) {

        //        alphaGrowing = false;
        //        fluctuatingAlpha = 0.75f;

        if (texture == null) {
            ActionMaster.addFadeOutAction(border, 0.65f, true);
            //            border = null;
            borderTexture = null;
            setTeamColorBorder(false);
        } else {
            if (border != null) {
                removeActor(border);
            }
            addActor(border = new Image(texture));
            border.getColor().a = 0;
            ActionMaster.addFadeInAction(border, 0.65f);

            borderTexture = texture;
            updateBorderSize();
        }
    }

    protected void alphaFluctuation(float delta) {
        if (isTeamColorBorder())
            alphaFluctuation(border, delta);
    }

    @Override
    protected Color getDefaultColor(Actor image) {
        Color color;
        if (image != null)
            color = image.getColor();
        else color = GdxColorMaster.WHITE;

        if (isTeamColorBorder())
            if (getTeamColor() != null) {
                color = getTeamColor();
            }
        return color;
    }

    protected void updateBorderSize() {
        if (border != null) {
            border.setX(-6);
            border.setY(-6);
            if (getHeight() == 0)
                return;
            if (getWidth() == 0)
                return;
            border.setHeight(getHeight() + 12);
            border.setWidth(getWidth() + 12);
        }
    }

    protected boolean isIgnored() {
        if (!isVisible())
            return true;
        //        if (getColor().a == 0)
        //            return true; TODO why was this here?.. not to draw?
        return !isWithinCamera();
    }

    public boolean isWithinCameraCheck() {
        return getController().isWithinCamera(this);
    }
        public boolean isWithinCamera() {

        if (withinCamera != null)
            return withinCamera;
        if (isCachedPosition()) {
            withinCamera = isWithinCameraCheck();
            getController().addCachedPositionActor(this);
            return withinCamera;
        }
        return isWithinCameraCheck();

    }
    @Override
    public void cameraMoved() {
        this.withinCamera = null;
    }

    @Override
    public boolean isCachedPosition() {
        return false;
    }

    public InputController getController() {
        return Eidolons.getScreen().controller;
    }

    @Override
    public void addAction(Action action) {
        if (getActions().size>50) {
            getActions().clear(); //TODO igg demo hack
        }
        if (!getActions().contains(action, true))
            super.addAction(action);
    }

    protected void restoreBlendingFuncData(Batch batch) {
        batch.setBlendFunctionSeparate(
         blendSrcFunc,
         blendDstFunc,
         blendSrcFuncAlpha,
         blendDstFuncAlpha);
    }

    protected void storeBlendingFuncData(Batch batch) {
        blendDstFunc = batch.getBlendDstFunc();
        blendSrcFunc = batch.getBlendSrcFunc();
        blendDstFuncAlpha = batch.getBlendDstFuncAlpha();
        blendSrcFuncAlpha = batch.getBlendSrcFuncAlpha();
    }

    @Override
    public void act(float delta) {
        //        if (isIgnored())
        //            return; do it in implementations!
        if (isTransform()) {
            if (isTransformDisabled())
                setTransform(false);
        } else {
            if (!isTransformDisabled())
                setTransform(true);
        }
        super.act(delta);
        alphaFluctuation(delta);

        if (actionManger != null) {
            actionManger.run();
        }
    }

    public void setActionManger(Runnable actionManger) {
        this.actionManger = actionManger;
    }

    protected boolean isTransformDisabled() {
        if (getScaleX() != 1f) {
            return false;
        }
        if (getScaleY() != 1f) {
            return false;
        }
        return true;
    }

    @Override
    public boolean isTeamColorBorder() {
        return teamColorBorder;
    }

    @Override
    public void setTeamColorBorder(boolean teamColorBorder) {
        this.teamColorBorder = teamColorBorder;
    }

    @Override
    public Color getTeamColor() {
        return teamColor;
    }

    @Override
    public void setTeamColor(Color teamColor) {
        this.teamColor = teamColor;
    }

    public float getScaledWidth() {
        return scaledWidth;
    }

    public void setScaledWidth(float scaledWidth) {
        this.scaledWidth = scaledWidth;
    }

    public float getScaledHeight() {
        return scaledHeight;
    }

    public void setScaledHeight(float scaledHeight) {
        this.scaledHeight = scaledHeight;
    }

    public boolean isHovered() {
        return hovered;
    }

    public void setHovered(boolean hovered) {
        this.hovered = hovered;
    }

    public boolean isActive() {
        if (active)
            return active;
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isHoverResponsive() {
        return hoverResponsive;
    }

    public void setHoverResponsive(boolean hoverResponsive) {
        this.hoverResponsive = hoverResponsive;
    }

    public enum BLENDING {
        INVERT_SCREEN(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA),
        PREMULTIPLIED_ALPHA(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA),
        SCREEN(GL20.GL_DST_COLOR, GL20.GL_SRC_ALPHA),
        OVERLAY(GL20.GL_DST_COLOR, GL20.GL_SRC_ALPHA),
        MULTIPLY(GL20.GL_DST_COLOR, GL20.GL_ONE_MINUS_SRC_ALPHA),
        SATURATE(GL20.GL_DST_COLOR, GL20.GL_ONE),
        DARKEN(GL20.GL_DST_COLOR, GL20.GL_ONE_MINUS_SRC_ALPHA),
        SUBTRACT(GL20.GL_DST_COLOR, GL20.GL_SRC_ALPHA),;

        public int blendDstFunc, blendSrcFunc, blendDstFuncAlpha, blendSrcFuncAlpha;

        BLENDING(int blendDstFunc, int blendSrcFunc) {
            this.blendDstFunc = blendDstFunc;
            this.blendSrcFunc = blendSrcFunc;
        }

        BLENDING(int blendDstFunc, int blendSrcFunc, int blendDstFuncAlpha, int blendSrcFuncAlpha) {
            this.blendDstFunc = blendDstFunc;
            this.blendSrcFunc = blendSrcFunc;
            this.blendDstFuncAlpha = blendDstFuncAlpha;
            this.blendSrcFuncAlpha = blendSrcFuncAlpha;
        }
    }

}
