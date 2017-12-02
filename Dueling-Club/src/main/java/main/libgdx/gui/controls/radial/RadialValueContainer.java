package main.libgdx.gui.controls.radial;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import main.entity.active.DC_ActiveObj;
import main.entity.obj.DC_Obj;
import main.libgdx.StyleHolder;
import main.libgdx.anims.ActorMaster;
import main.libgdx.bf.mouse.BattleClickListener;
import main.libgdx.gui.panels.dc.actionpanel.ActionValueContainer;
import main.libgdx.gui.tooltips.ToolTip;
import main.libgdx.shaders.GrayscaleShader;
import main.system.auxiliary.data.ListMaster;
import main.system.graphics.FontMaster.FONT;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class RadialValueContainer extends ActionValueContainer {
    Runnable lazyChildInitializer;
    Label infoLabel;
    Supplier<String> infoTextSupplier;
    private List<RadialValueContainer> childNodes = new ArrayList<>();
    private RadialValueContainer parent;
    private Supplier<ToolTip> tooltipSupplier;
    private ToolTip tooltip;
    private boolean valid = true;

    public RadialValueContainer(TextureRegion texture, String name, String value, Runnable action) {
        super(texture, name, value, action);
    }

    public RadialValueContainer(TextureRegion texture, Runnable action) {
        super(texture, action);
    }

    public RadialValueContainer(TextureRegion texture, String value, Runnable action) {
        super(texture, value, action);
    }

    public RadialValueContainer(TextureRegion texture, String value, Runnable action, Runnable lazyChildInitializer) {
        super(texture, value, action);
        this.lazyChildInitializer = lazyChildInitializer;
//if (isAddListener())
        addListener(new BattleClickListener() {
            boolean hover;

            @Override
            public boolean mouseMoved(InputEvent event, float x, float y) {
                if (hover)
                    return false;
                hover = true;
                ActorMaster.addScaleAction(RadialValueContainer.this, 1.2f, 1.2f, 0.7f);
                return super.mouseMoved(event, x, y);
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (!hover) {
                    hover = true;
                    ActorMaster.addScaleAction(RadialValueContainer.this, 1.2f, 1.2f, 0.7f);
                }
                super.enter(event, x, y, pointer, fromActor);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                hover = false;
                ActorMaster.addScaleAction(RadialValueContainer.this, 1, 1, 0.7f);
                super.exit(event, x, y, pointer, toActor);
            }
        });
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

    public void setInfoTextSupplier(Supplier<String> infoTextSupplier) {
        this.infoTextSupplier = infoTextSupplier;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        ShaderProgram shader = null;
        if (!valid) {
            shader = batch.getShader();
            batch.setShader(GrayscaleShader.getGrayscaleShader());
        }
        super.draw(batch, parentAlpha);
        if (batch.getShader() == GrayscaleShader.getGrayscaleShader())
            batch.setShader(shader);
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
        super.setVisible(visible);
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
}
