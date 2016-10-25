package main.swing.components;

import main.content.VALUE;
import main.entity.obj.Obj;
import main.swing.generic.components.G_Panel;
import main.swing.renderers.SmartTextManager;
import main.system.auxiliary.ColorManager;
import main.system.auxiliary.FontMaster;
import main.system.auxiliary.FontMaster.FONT;
import main.system.images.ImageManager;

import java.awt.*;

public class PropertyComponent extends G_Panel {

    public static final int SPEC_LBL_OFFSET_X = 24;
    public static final int SPEC_LBL_OFFSET_Y = 21;
    public static final int LBL_OFFSET_X = 24;
    public static final int LBL_OFFSET_Y = 21;
    private static final float VAL_COMP_FONT_SIZE = 14;
    boolean special;
    Font font;
    int x, y;
    private VALUE val;
    private Obj obj;
    private String text;
    private SmartTextManager srm;
    private Color color;

    public PropertyComponent(VALUE val, Obj obj, boolean special) {
        this.val = val;
        this.obj = obj;
        this.setVisuals((special) ? VISUALS.VALUE_BOX_SMALL : VISUALS.VALUE_BOX_SMALL);
        this.special = special;

        init();
        refresh();
    }

    private void init() {
        this.x = LBL_OFFSET_X;
        this.y = LBL_OFFSET_Y;
        if (special) {
            this.x = SPEC_LBL_OFFSET_X;
            this.y = SPEC_LBL_OFFSET_Y;
        }
        setPanelSize(ImageManager.getImgSize(getVisuals().getImage()));

        this.font = FontMaster
                .getFont(FONT.MAIN, VAL_COMP_FONT_SIZE, Font.PLAIN);
        srm = new SmartTextManager();
    }

    @Override
    public void setPanelSize(Dimension size) {
        super.setPanelSize(size);
        setPreferredSize(getPanelSize());
    }

    @Override
    public void refresh() {
        if (special)
            text = obj.getValue(val);
        else {
            text = val.getName() + ": " + getSpaces() + obj.getValue(val);
        }
        // JLabel lbl = new JLabel(value);
        // lbl.setForeground(fg)

        color = SmartTextManager.getValueCase(val, obj).getColor();
        color = ColorManager.getInvertedColor(color);
    }

    private String getSpaces() {
        return "";
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.setFont(font);
        g.setColor(color);
        // g.drawString(text, x, y);

    }

}
