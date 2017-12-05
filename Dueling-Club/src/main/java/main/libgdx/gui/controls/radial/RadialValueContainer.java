package main.libgdx.gui.controls.radial;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import main.entity.active.DC_ActiveObj;
import main.entity.obj.DC_Obj;
import main.libgdx.StyleHolder;
import main.libgdx.bf.mouse.BattleClickListener;
import main.libgdx.gui.UiAnimator;
import main.libgdx.gui.panels.dc.actionpanel.ActionValueContainer;
import main.libgdx.gui.tooltips.ToolTip;
import main.libgdx.screens.DungeonScreen;
import main.libgdx.texture.TextureCache;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.data.ListMaster;
import main.system.graphics.FontMaster.FONT;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class RadialValueContainer extends ActionValueContainer {
    Runnable lazyChildInitializer;
    Label infoLabel;
    Supplier<String> infoTextSupplier;
    protected List<RadialValueContainer> childNodes = new ArrayList<>();
    protected RadialValueContainer parent;
    protected Supplier<ToolTip> tooltipSupplier;
    protected ToolTip tooltip;


    public RadialValueContainer(TextureRegion texture, Runnable action) {
        super(texture, action);
        if (texture.getRegionHeight()>64)
            if (texture.getRegionWidth()>64){
                overrideImageSize(UiAnimator.getIconSize(), UiAnimator.getIconSize());
            }
//        initUnderlay();
        setUnderlay(
         valid?
          RADIAL_UNDERLAYS.BLACK_BEVEL.getTextureRegion() :
          RADIAL_UNDERLAYS.BLACK_BEVEL_DISABLED.getTextureRegion());
        addListener(new BattleClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!valid)
                    return ;
                setUnderlay(RADIAL_UNDERLAYS.BLACK_BEVEL_DISABLED.getTextureRegion());
                super.clicked(event, x, y);
            }

            @Override
            public boolean mouseMoved(InputEvent event, float x, float y) {
                if (!valid)
                    return super.mouseMoved(event, x, y);
                if (hover)
                    return false;
                hover = true;
                setZIndex(Integer.MAX_VALUE);
                setUnderlay(RADIAL_UNDERLAYS.BLACK_BEVEL_GLOW.getTextureRegion());
//                ActorMaster.addScaleAction(RadialValueContainer.this, 1.2f, 1.2f, 0.7f);
                return super.mouseMoved(event, x, y);
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (!valid) return;
                if (!hover) {
                    hover = true;
                    setZIndex(Integer.MAX_VALUE);
                    setUnderlay(RADIAL_UNDERLAYS.BLACK_BEVEL_GLOW.getTextureRegion());
                }
                super.enter(event, x, y, pointer, fromActor);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                hover = false;
                setUnderlay(RADIAL_UNDERLAYS.BLACK_BEVEL.getTextureRegion());
                super.exit(event, x, y, pointer, toActor);
            }
        });
    }

    public RadialValueContainer(TextureRegion texture, String value, Runnable action) {
        super(texture, value, action);
    }

    public RadialValueContainer(TextureRegion textureRegion, Runnable runnable, boolean valid, DC_ActiveObj activeObj, DC_Obj target) {
        this(textureRegion, runnable);
        this.valid = valid;
        try {
            infoTextSupplier = RadialManager.getInfoTextSupplier(valid, activeObj, target);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }

    }

    public void setUnderlay(TextureRegion underlay) {
        if (!checkUnderlayRequired())
            return ;
        if (underlay==null )
            return ;
        setUnderlayOffsetX((imageContainer.getActorX() +
         (imageContainer.getActorWidth() - underlay.getRegionWidth()))/3*2+3);
        setUnderlayOffsetY((imageContainer.getActorY() +
         (imageContainer.getActorHeight() - underlay.getRegionHeight()))/3*2 -2);
        if (getRadial() !=null )
        if (getRadial().getActions().size>0)
            return ;

//        main.system.auxiliary.log.LogMaster.log(1," underlay set " + underlay.getTexture().getTextureData());
        this.underlay = underlay;
    }

    @Override
    public void bindAction(Runnable action) {
        if (action != null) {
            clickAction = action::run;
        }
    }
    protected boolean checkUnderlayRequired() {
//        if (imageContainer.getActor().getImageWidth()==64)
//            if (imageContainer.getActor().getImageHeight()==64)
//                return true;
        if (getParent()==null )
            return false;
//        if (getParent().getParent()==null )
//            return false;

        return true;
    }

    public void setInfoTextSupplier(Supplier<String> infoTextSupplier) {
        this.infoTextSupplier = infoTextSupplier;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        if (underlayOffsetX==0|| underlayOffsetY==0)
            setUnderlay(underlay);
    }

    public List<RadialValueContainer> getChildNodes() {
        if (!ListMaster.isNotEmpty(childNodes))
            if (lazyChildInitializer != null)
                lazyChildInitializer.run();
        return childNodes;
    }

    public void setChildNodes(List<RadialValueContainer> childNodes) {
        this.childNodes = childNodes;
    }

    @Override
    public RadialValueContainer getParent() {
        return parent;
    }

    public void setParent(RadialValueContainer parent) {
        this.parent = parent;
    }

    public void setChildVisible(boolean visible) {
        childNodes.forEach(el -> el.setVisible(visible));
    }

    @Override
    public void setVisible(boolean visible) {
        setUnderlay(
         valid?
          RADIAL_UNDERLAYS.BLACK_BEVEL.getTextureRegion() :
          RADIAL_UNDERLAYS.BLACK_BEVEL_DISABLED.getTextureRegion());
        setUnderlayOffsetX(0);
        setUnderlayOffsetY(0);
        if (visible) {


            if (infoTextSupplier != null) {
                if (infoLabel == null) {
                    infoLabel = new Label(infoTextSupplier.get(), StyleHolder.getSizedLabelStyle(FONT.RU, 18));
                    addActor(infoLabel);
                } else {
                    infoLabel.setText(infoTextSupplier.get());
                }
                infoLabel.setColor(valid ? new Color(1, 1, 1, 1) : new Color(1, 0.2f, 0.3f, 1));

                infoLabel.setPosition((64 - infoLabel.getWidth()) / 2,
                 (imageContainer.getActor().getImageHeight() + infoLabel.getHeight()) / 2);
            }

            if (tooltip == null)
                if (getTooltipSupplier() != null) {
                    try {
                        tooltip = tooltipSupplier.get();
                    } catch (Exception e) {
                        main.system.ExceptionMaster.printStackTrace(e);
                    }
                    if (tooltip != null)
                        addListener(tooltip.getController());

                }
        }
    }

    public Supplier<ToolTip> getTooltipSupplier() {
        return tooltipSupplier;
    }

    public void setTooltipSupplier(Supplier<ToolTip> tooltipSupplier) {
        this.tooltipSupplier = tooltipSupplier;
    }

    public void setUnderlayOffsetX(float underlayOffsetX) {
//        main.system.auxiliary.log.LogMaster.log(1,"x from " + this.underlayOffsetX+" to " + underlayOffsetX);
        this.underlayOffsetX = underlayOffsetX;
    }

    public void setUnderlayOffsetY(float underlayOffsetY) {
//        main.system.auxiliary.log.LogMaster.log(1,"y from " + this.underlayOffsetX+" to " + underlayOffsetX);
        this.underlayOffsetY = underlayOffsetY;
    }

    public enum RADIAL_UNDERLAYS {
        BLACK_BEVEL(StrPathBuilder.build(
         "ui", "components", "2017", "radial", "underlay bevel.png")),
        BLACK_BEVEL_GLOW(StrPathBuilder.build(
         "ui", "components", "2017", "radial", "underlay bevel glow.png")),
        BLACK_BEVEL_DISABLED(StrPathBuilder.build(
         "ui", "components", "2017", "radial", "underlay bevel disabled.png")),
         ;
        String texturePath;
        TextureRegion textureRegion;

        RADIAL_UNDERLAYS(String texturePath) {
            this.texturePath = texturePath;
        }

        public TextureRegion getTextureRegion() {
            if (textureRegion == null)
                textureRegion = TextureCache.getOrCreateR(texturePath);
            return textureRegion;
        }

    }
}
