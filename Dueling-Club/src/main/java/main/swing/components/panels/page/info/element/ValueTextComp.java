package main.swing.components.panels.page.info.element;

import main.content.VALUE;
import main.entity.Entity;
import main.system.auxiliary.FontMaster.FONT;

import java.awt.*;

public class ValueTextComp extends TextCompDC implements EntityValueComponent {
    protected VALUE value;
    protected Entity entity;

    public ValueTextComp(VALUE value, VISUALS V) {
        super(V);
        this.value = value;
        x = recalculateX();
        if (value != null)
            setToolTipText(value.toString());
    }

    public ValueTextComp(Entity entity, VISUALS V, VALUE value, int fontSize, FONT type,
                         Color textColor) {
        super(V, null, fontSize, type, textColor);
        this.entity = entity;
        this.value = value;
        x = recalculateX();
        if (value != null)
            setToolTipText(value.toString());

    }

    @Override
    public Component getComponent() {
        return this;
    }

    protected String getText() {
        return getPrefix() + getValue();
    }

    protected String getValue() {
        if (getEntity() == null)
            return "";
        return getEntity().getValue(value);
    }

    protected boolean isPaintBlocked() {
        return entity == null && !permanent;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }
    // @Override
    // protected void paintComponent(Graphics g) {
    // ??? }
}
