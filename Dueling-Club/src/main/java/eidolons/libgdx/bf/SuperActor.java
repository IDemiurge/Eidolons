package eidolons.libgdx.bf;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import eidolons.libgdx.GdxColorMaster;
import eidolons.libgdx.bf.mouse.InputController;
import eidolons.libgdx.screens.CustomSpriteBatch;
import eidolons.libgdx.screens.ScreenMaster;
import main.content.enums.GenericEnums;

/**
 * Created by JustMe on 8/17/2017.
 */
public abstract class SuperActor extends Fluctuating implements
        CachedIgnoreActor, TeamColored {
    protected Image border = null;
    protected boolean teamColorBorder;
    protected Color teamColor;
    protected float scaledWidth = 1;
    protected float scaledHeight = 1;
    protected boolean hovered;
    protected boolean active;
    //    protected int blendDstFunc, blendSrcFunc, blendDstFuncAlpha, blendSrcFuncAlpha;
    protected boolean withinCamera;
    protected Runnable actionManger;
    private boolean hoverResponsive;


    // private boolean customDrawParams;
    // private boolean normalDrawDisabled;
    protected float screenOverlay;
    protected Color customColor;


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

    public float getScreenOverlay() {
        return screenOverlay;
    }

    public void setScreenOverlay(float screenOverlay) {
        this.screenOverlay = screenOverlay;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
//     TODO EA Check
//      if (!customDrawParams)
//            super.draw(batch, parentAlpha);
//        else {
//            if (!normalDrawDisabled)
        super.draw(batch, parentAlpha);

        if (screenOverlay > 0.01f) {
            Color c = getColor();
            if (customColor != null) {
                setColor(customColor);
            }
            float a = getColor().a;
            getColor().a = screenOverlay;
            try {
                ((CustomSpriteBatch) batch).setBlending(GenericEnums.BLENDING.SCREEN); //could do other blends too
                super.draw(batch, parentAlpha);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
            getColor().a = a;
            ((CustomSpriteBatch) batch).resetBlending();
            setColor(c);
        }
//        }
    }

    public SuperActor() {
    }

    public SuperActor(GenericEnums.ALPHA_TEMPLATE alphaTemplate) {
        setAlphaTemplate(alphaTemplate);
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
        return !isWithinCamera();
    }

    public boolean isWithinCameraCheck() {
        return getController().isWithinCamera(this);
    }

    public boolean isWithinCamera() {

        if (!InputController.cameraMoved)
            return withinCamera;
        if (isCachedPosition()) {
            withinCamera = isWithinCameraCheck();
            return withinCamera;
        }
        return isWithinCameraCheck();

    }

    @Override
    public boolean isCachedPosition() {
        return false;
    }

    public InputController getController() {
        return ScreenMaster.getScreen().controller;
    }

    @Override
    public void addAction(Action action) {
        if (getActions().size > 50) {
            getActions().clear(); //TODO EA check hack
        }
        if (!getActions().contains(action, true))
            super.addAction(action);
    }

//    protected void restoreBlendingFuncData(Batch batch) {
//        batch.setBlendFunctionSeparate(
//         blendSrcFunc,
//         blendDstFunc,
//         blendSrcFuncAlpha,
//         blendDstFuncAlpha);
//    }
//
//    protected void storeBlendingFuncData(Batch batch) {
//        blendDstFunc = batch.getBlendDstFunc();
//        blendSrcFunc = batch.getBlendSrcFunc();
//        blendDstFuncAlpha = batch.getBlendDstFuncAlpha();
//        blendSrcFuncAlpha = batch.getBlendSrcFuncAlpha();
//    }


    public void setActionManger(Runnable actionManger) {
        this.actionManger = actionManger;
    }

    protected boolean isTransformDisabled() {
        if (getScaleX() != 1f) {
            return false;
        }
        return getScaleY() == 1f;
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

}
