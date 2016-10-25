package main.swing.components.panels;

import main.content.C_OBJ_TYPE;
import main.content.OBJ_TYPE;
import main.content.OBJ_TYPES;
import main.content.VALUE;
import main.entity.Entity;
import main.entity.obj.Obj;
import main.swing.generic.components.G_Panel;
import main.swing.renderers.SmartTextManager;
import main.system.auxiliary.ColorManager;
import main.system.auxiliary.FontMaster;
import main.system.auxiliary.StringMaster;
import main.system.images.ImageManager;

import java.awt.*;

public class ValueBox extends G_Panel {
    protected static final int X_OFFSET = 7;
    protected static final int Y_OFFSET = 7;
    protected static final int X_OFFSET_TEXT = 8;
    protected static final int Y_OFFSET_TEXT = 24;
    protected static final int SIZE = 17;
    protected static final OBJ_TYPE[] VALID_OBJ_TYPES = {C_OBJ_TYPE.UNITS_CHARS, OBJ_TYPES.BF_OBJ,};
    protected Entity entity;
    protected String text;
    protected int x;
    protected int y;
    protected Font font;
    protected SmartTextManager smarty;
    protected Image valueIcon;
    VALUE value;

    public ValueBox(VALUE value) {
        super(VISUALS.VALUE_BOX_TINY);
        this.value = value;

        font = FontMaster.getDefaultFont(SIZE);
        smarty = new SmartTextManager();
        valueIcon = ImageManager.getValueIcon(value);
        if (valueIcon == null) {
            valueIcon = VISUALS.QUESTION.getImage();
        }
        x = valueIcon.getWidth(null) + X_OFFSET_TEXT;
        y = Y_OFFSET_TEXT;

        if (StringMaster.isEmpty(value.getDescription()))
            setToolTipText(value.getName());
        else
            setToolTipText(value.getDescription());
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    @Override
    public void refresh() {

        if (entity != null)
            text = getText();
        repaint();
    }

    protected String getText() {
        return entity.getValue(value);
    }

    @Override
    public void paint(Graphics g) {
        if (entity == null)
            return;
        if (isCheckType())
            if (!checkType())
                return;
        super.paint(g);
        g.drawImage(valueIcon, X_OFFSET, Y_OFFSET, null);
        g.setFont(font);
        Color c = ColorManager.WHITE;
        if (entity instanceof Obj) {
            boolean invert = false;
            if (!entity.getGame().isSimulation())
                try {
                    invert = (entity.getOwner() != entity.getGame().getManager().getActiveObj()
                            .getOwner());
                } catch (Exception e) {
                }
            c = (invert) ? SmartTextManager.getValueCase(value, (Obj) entity).getColor()
                    : SmartTextManager.getValueCase(value, (Obj) entity).getAltColor();
            if (invert)
                c = ColorManager.getInvertedColor(c);
        }

        g.setColor(c);
        g.drawString(text, x, y);

    }

    protected boolean isCheckType() {
        return true;
    }

    protected boolean checkType() {
        for (OBJ_TYPE type : VALID_OBJ_TYPES) {
            if (type.equals(entity.getOBJ_TYPE_ENUM()))
                return true;
        }

        return false;
    }

    public VALUE getValue() {
        return value;
    }

}
