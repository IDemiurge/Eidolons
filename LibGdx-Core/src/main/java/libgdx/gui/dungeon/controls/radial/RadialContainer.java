package libgdx.gui.dungeon.controls.radial;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import eidolons.entity.feat.active.ActiveObj;
import eidolons.entity.obj.DC_Obj;
import libgdx.StyleHolder;
import libgdx.bf.mouse.BattleClickListener;
import libgdx.gui.UiMaster;
import libgdx.gui.dungeon.panels.dc.actionpanel.spaces.ActionContainer;
import libgdx.gui.dungeon.tooltips.Tooltip;
import libgdx.shaders.ShaderDrawer;
import libgdx.assets.texture.TextureCache;
import main.content.DC_TYPE;
import main.entity.type.ObjType;
import main.system.ExceptionMaster;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.data.ListMaster;
import main.system.graphics.FontMaster.FONT;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class RadialContainer extends ActionContainer {
    protected List<RadialContainer> childNodes = new ArrayList<>();
    protected RadialContainer parent;
    protected Supplier<Tooltip> tooltipSupplier;
    protected Tooltip tooltip;
    Runnable lazyChildInitializer;
    Label infoLabel;
    Supplier<String> infoTextSupplier;
    private ShaderProgram shader;
    private boolean altUnderlay;
    private boolean textOverlayOn;


    public RadialContainer(TextureRegion texture, Runnable action) {
        this(UiMaster.getIconSize(), texture, action);
    }
    public RadialContainer(int size, TextureRegion texture, Runnable action) {
        super(texture, action);
        this.size = size;
        setUnderlay(
         isValid() ?
          getUnderlayDefault().getTextureRegion() :
         getUnderlayDisabled().getTextureRegion());


        addListener(new BattleClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!isValid())
                    return;
                setUnderlay(getUnderlayDisabled().getTextureRegion());
                super.clicked(event, x, y);
            }

            @Override
            public boolean mouseMoved(InputEvent event, float x, float y) {
                if (!isValid())
                    return super.mouseMoved(event, x, y);
                if (isHover())
                    return false;
                setHover(true);
                setZIndex(Integer.MAX_VALUE);
                setUnderlay(RADIAL_UNDERLAYS.BLACK_BEVEL_GLOW.getTextureRegion());
//                ActorMaster.addScaleAction(RadialValueContainer.this, 1.2f, 1.2f, 0.7f);
                return super.mouseMoved(event, x, y);
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (!isValid()) return;
                if (!isHover()) {
                    setHover(true);
                    setZIndex(Integer.MAX_VALUE);
                    setUnderlay(getUnderlayGlow().getTextureRegion());
                }
                super.enter(event, x, y, pointer, fromActor);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                setHover(false);
                setUnderlay(getUnderlayDefault().getTextureRegion());
                super.exit(event, x, y, pointer, toActor);
            }
        });
    }


    public RadialContainer(TextureRegion textureRegion, Runnable runnable, boolean valid, ActiveObj activeObj, DC_Obj target) {
        this(textureRegion, runnable);
        this.setValid(valid);
        try {
            infoTextSupplier = RadialManager.getInfoTextSupplier(valid, activeObj, target);
        } catch (Exception e) {
            ExceptionMaster.printStackTrace(e);
        }

    }

    @Override
    protected boolean isScaledOnHover() {
        return false;
    }

    protected void initSize() {
        if (getActor().getWidth()!=size)
        overrideImageSize(size, size);
    }

    private void setUnderlay(TextureRegion underlay) {
        if (!checkUnderlayRequired())
            return;
        setUnderlay_(underlay);
    }
        public void setUnderlay_(TextureRegion underlay) {

        if (underlay == null)
            return;
        setUnderlayOffsetX(
                (getImageContainer().getActor().getWidth()/2
                 - underlay.getRegionWidth()) /2+getUnderlayOffsetX() );
//                        / 3 * 2 + 3);
        setUnderlayOffsetY( (getImageContainer().getActor().getHeight()/2
                - underlay.getRegionHeight()) /2+getUnderlayOffsetY());
//                / 3 * 2 + 7);
        if (getRadial() != null)
            if (getRadial().getActions().size > 0)
                return;

//        main.system.auxiliary.log.LogMaster.log(1," underlay set " + underlay.getTexture().getTextureData());
        this.underlay = underlay;
    }

    private float getUnderlayOffsetY() {
        if (getUserObject() instanceof ObjType){
            if (((ObjType) getUserObject()).getOBJ_TYPE_ENUM()== DC_TYPE.CLASSES) {
                return -21;
            }
        }
        return -25;
    }
    private float getUnderlayOffsetX() {
        if (getUserObject() instanceof ObjType){
            if (((ObjType) getUserObject()).getOBJ_TYPE_ENUM()== DC_TYPE.CLASSES) {
                return 2;
            }
        }
        return 5;
    }

    @Override
    public void bindAction(Runnable action) {
        if (action != null) {
            clickAction = action::run;
        }
    }

    protected boolean checkUnderlayRequired() {
        return getParent() != null;
    }

    public void setInfoTextSupplier(Supplier<String> infoTextSupplier) {
        this.infoTextSupplier = infoTextSupplier;
    }

    @Override
    protected void drawLightUnderlay(Batch batch) {
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (parentAlpha== ShaderDrawer.SUPER_DRAW)
        {
            super.draw(batch, 1);
            return;
        }
            ShaderDrawer.drawWithCustomShader(this, batch,
         shader, true);

        if (underlayOffsetX == 0 || underlayOffsetY == 0)
            setUnderlay(underlay);

    }

    public List<RadialContainer> getChildNodes() {
        if (!ListMaster.isNotEmpty(childNodes))
            if (lazyChildInitializer != null)
                lazyChildInitializer.run();
        return childNodes;
    }

    public void setChildNodes(List<RadialContainer> childNodes) {
        this.childNodes = childNodes;
    }

    @Override
    public RadialContainer getParent() {
        return parent;
    }

    public void setParent(RadialContainer parent) {
        this.parent = parent;
    }

    public void setChildVisible(boolean visible) {
        childNodes.forEach(el -> el.setVisible(visible));
    }

    @Override
    public void setVisible(boolean visible) {
        setUnderlay(
         isValid() ?
          getUnderlayDefault().getTextureRegion() :
          getUnderlayDisabled().getTextureRegion());
        setUnderlayOffsetX(0);
        setUnderlayOffsetY(0);
        if (visible) {

        if (!isTextOverlayOn())
        {
            if (infoLabel != null) {
                infoLabel.setVisible(false);
            }
        } else
            if (infoTextSupplier != null) {
                if (infoLabel == null) {
                    infoLabel = new Label(infoTextSupplier.get(), StyleHolder.getSizedLabelStyle(FONT.RU, 18));
                    addActor(infoLabel);
                } else {
                    infoLabel.setText(infoTextSupplier.get());
                }
                infoLabel.setColor(isValid() ? new Color(1, 1, 1, 1) : new Color(1, 0.2f, 0.3f, 1));

                infoLabel.setPosition((64 - infoLabel.getWidth()) / 2,
                 (getHeight() + infoLabel.getHeight()) / 2);
            }

            if (tooltip == null)
                if (getTooltipSupplier() != null) {
                    try {
                        tooltip = tooltipSupplier.get();
                    } catch (Exception e) {
                        ExceptionMaster.printStackTrace(e);
                    }
                    if (tooltip != null)
                        addListener(tooltip.getController());

                }
        }
    }


    protected RADIAL_UNDERLAYS getUnderlayDefault() {
        if (isAltUnderlay())
            return RADIAL_UNDERLAYS.BLACK_BEVEL2;
        return RADIAL_UNDERLAYS.BLACK_BEVEL;
    }

    protected RADIAL_UNDERLAYS getUnderlayGlow() {
        if (isAltUnderlay())
            return RADIAL_UNDERLAYS.BLACK_BEVEL_GLOW2;
        return RADIAL_UNDERLAYS.BLACK_BEVEL_GLOW;
    }
    protected RADIAL_UNDERLAYS getUnderlayDisabled() {
        if (isAltUnderlay())
            return RADIAL_UNDERLAYS.BLACK_BEVEL_DISABLED2;
        return RADIAL_UNDERLAYS.BLACK_BEVEL_DISABLED;
    }


    public Supplier<Tooltip> getTooltipSupplier() {
        return tooltipSupplier;
    }

    public void setTooltipSupplier(Supplier<Tooltip> tooltipSupplier) {
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
    public void setShader(ShaderProgram shader) {
        this.shader = shader;
    }

    public ShaderProgram getShader() {
        return shader;
    }

    public boolean isAltUnderlay() {
        return altUnderlay;
    }

    public void setAltUnderlay(boolean altUnderlay) {
        this.altUnderlay = altUnderlay;
    }

    public boolean isTextOverlayOn() {
        return textOverlayOn;
    }

    public void setTextOverlayOn(boolean textOverlayOn) {
        this.textOverlayOn = textOverlayOn;
    }

    public enum RADIAL_UNDERLAYS {
        BLACK_BEVEL(StrPathBuilder.build(
         "ui", "components", "dc", "radial", "underlay bevel2.png")),
        BLACK_BEVEL_GLOW(StrPathBuilder.build(
         "ui", "components", "dc", "radial", "underlay bevel glow2.png")),
        BLACK_BEVEL_DISABLED(StrPathBuilder.build(
         "ui", "components", "dc", "radial", "underlay bevel disabled.2png")),

        BLACK_BEVEL2(StrPathBuilder.build(
         "ui", "components", "dc", "radial", "underlay bevel.png")),
        BLACK_BEVEL_GLOW2(StrPathBuilder.build(
         "ui", "components", "dc", "radial", "underlay bevel glow.png")),
        BLACK_BEVEL_DISABLED2(StrPathBuilder.build(
         "ui", "components", "dc", "radial", "underlay bevel disabled.png")),;
        String texturePath;
        TextureRegion textureRegion;

        RADIAL_UNDERLAYS(String texturePath) {
            this.texturePath = texturePath;
        }

        public TextureRegion getTextureRegion() {
            if (textureRegion == null)
                textureRegion = TextureCache.getRegionUI_DC(texturePath);
            return textureRegion;
        }

    }
}
