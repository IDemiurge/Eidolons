package eidolons.swing.components.panels;

import eidolons.content.PARAMS;
import eidolons.game.core.game.DC_Game;
import eidolons.system.text.ToolTipMaster;
import main.content.VALUE;
import main.content.values.parameters.PARAMETER;
import main.data.XLinkedMap;
import main.entity.Entity;
import main.swing.generic.components.G_Panel;

import java.awt.*;
import java.util.Map;

public class ValueIconPanel extends G_Panel {
    public static final VALUE[] I = {PARAMS.RESISTANCE, PARAMS.DAMAGE, PARAMS.ATTACK,};
    public static final VALUE[] II = {PARAMS.SPIRIT, PARAMS.ARMOR, PARAMS.DEFENSE,};
    public static final VALUE[] III = {
     // WEIGHT CARRYING CAPACITY
     // RESISTANCE_PENETRATION ARMOR_PENETRATION
     // ILLUMINATION CONCEALMENT STEALTH DETECTION
     PARAMS.FORTITUDE, PARAMS.OFF_HAND_DAMAGE, PARAMS.OFF_HAND_ATTACK,};
    public static final VALUE[] I_ = {PARAMS.RESISTANCE, PARAMS.DAMAGE, PARAMS.ATTACK,};
    public static final VALUE[] II_ = {PARAMS.SPIRIT, PARAMS.ARMOR, PARAMS.DEFENSE,};
    public static final VALUE[] III_ = {PARAMS.ENDURANCE_REGEN, PARAMS.OFF_HAND_DAMAGE,
     PARAMS.OFF_HAND_ATTACK,};
    private static final int MARGIN = 1;
    private static final VALUE[][] valueColumns = {I, II, III};
    private final int TEXT_WIDTH = VISUALS.VALUE_BOX_TINY.getImage().getWidth(null);
    private int ROW_HEIGHT = VISUALS.VALUE_BOX_TINY.getImage().getHeight(null) + MARGIN;
    private int COLUMN_WIDTH = TEXT_WIDTH + MARGIN;
    // public static final VALUE[] OFFENSIVE_VALUES = { PARAMS.DAMAGE,
    // PARAMS.OFF_HAND_DAMAGE, PARAMS.ATTACK, };
    // public static final VALUE[] DEFENSIVE_VALUES = { PARAMS.ARMOR,
    // PARAMS.RESISTANCE, PARAMS.DEFENSE, };
    // public static final VALUE[] MISC_VALUES = { PARAMS.SPIRIT,
    // PARAMS.ENDURANCE_REGEN, PARAMS.C_CARRYING_WEIGHT, };
    private Entity entity;
    private Map<VALUE, ValueBox> boxes;

    public ValueIconPanel(Entity hero) {
        setEntity(hero);
        if (isInitialized()) {
            createBoxes();
        }
    }

    public ValueIconPanel(DC_Game game) {
        if (isInitialized()) {
            createBoxes();
        }
    }

    @Override
    public void refresh() {
        if (entity == null) {
            // TODO
        }
        for (ValueBox box : boxes.values()) {
            box.setEntity(entity);
            box.refresh();
        }
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    @Override
    public boolean isAutoSizingOn() {
        return true;
    }

    public VALUE getValueAt(Point point) {
        ValueBox box = (ValueBox) getComponentAt(point);
        return box.getValue();
    }

    protected void createBoxes() {
        removeAll();
        boxes = new XLinkedMap<>();
        int x = 0;
        int y;
        for (VALUE[] values : getValueColumns()) {
            y = 0;
            for (VALUE value : values) {
                ValueBox box = getValueBox(value);
                boxes.put(value, box);
                String pos = "pos " + x + " " + y;
                add(box, pos);
                y += ROW_HEIGHT;
                box.addMouseListener(ToolTipMaster.getValueMouseListener(value));
            }
            x += COLUMN_WIDTH;
        }
        revalidate();
    }

    protected ValueBox getValueBox(VALUE value) {
        ValueBox box = new ValueBox(value) {
            protected String getText() {
                return "" + entity.getIntParam((PARAMETER) value);
            }
        };
        return box;
    }

    public VALUE[][] getValueColumns() {
        return valueColumns;
    }

}
