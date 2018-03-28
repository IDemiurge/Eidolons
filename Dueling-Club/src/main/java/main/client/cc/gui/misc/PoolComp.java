package main.client.cc.gui.misc;

import main.content.values.parameters.PARAMETER;
import main.entity.Entity;
import main.swing.components.panels.page.info.element.TextCompDC;
import main.system.auxiliary.secondary.BooleanMaster;
import main.system.graphics.ColorManager;

import java.awt.*;

public class PoolComp extends TextCompDC {
    private static final int Y_OFFSET = 7;
    private Entity entity;
    private PARAMETER param;

    public PoolComp(Entity entity, PARAMETER param, String tooltip, Boolean c_visuals) {
        super(c_visuals == null ? VISUALS.POOL_MECH
         : (BooleanMaster.isTrue(c_visuals)) ? VISUALS.POOL_C : VISUALS.POOL);
        setToolTipText(tooltip);
        this.setEntity(entity);
        this.param = param;
        update();
    }

    public PoolComp(String text) {
        super(VISUALS.POOL);
        this.text = text;
        this.permanent = true;
    }

    public PoolComp(String poolText, String poolTooltip, Boolean poolC) {
        this(null, null, poolTooltip, poolC);
        this.text = poolText;
        this.permanent = true;
    }

    @Override
    protected String getText() {
        return text;
    }

    public void update() {
        if (getEntity() != null) {
            text = getEntity().getParamRounded(param, false);
        }
        repaint();
    }

    @Override
    public void refresh() {
        update();
    }

    @Override
    protected Color getColor() {
        return ColorManager.getHC_DefaultColor();
    }

    @Override
    protected int getDefaultFontSize() {
        return 16;
    }

    @Override
    protected int getDefaultY() {
        return getCenteredY() + getOffsetY();
    }

    protected int getOffsetY() {
        return Y_OFFSET;
    }

    @Override
    protected int getDefaultX() {
        return getCenteredX(getText());
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }
}
