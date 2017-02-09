package main.swing.renderers;

import main.content.ContentManager;
import main.content.parameters.PARAMETER;
import main.content.properties.G_PROPS;
import main.content.properties.PROPERTY;
import main.entity.Entity;
import main.game.MicroGame;
import main.swing.renderers.SmartTextManager.VALUE_CASES;
import main.system.auxiliary.ColorManager;
import main.system.auxiliary.StringMaster;
import main.system.images.ImageManager;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * mouse listening: update full info on click! modified C_ tooltips: base value;
 * ...
 *
 * @author JustMe
 */
public class DC_InfoPanelRenderer implements TableCellRenderer {

    private SmartTextManager smartRender;
    private MicroGame game;
    private Entity obj;
    private ImageIcon icon;

    public DC_InfoPanelRenderer(Entity infoObj) {
        this.obj = infoObj;
        this.game = (MicroGame) infoObj.getGame();
        this.smartRender = new SmartTextManager();
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (!obj.isDirty()) {
            return getDefaultComp(table, value, isSelected, hasFocus, row, column);
        }

        if (column == 0) {
            return getDefaultComp(table, value, isSelected, hasFocus, row, column);
        }

        // if (VALUE_ICONS.valueOf(value.toString()) == null)
        // icon = getDefaultIcon(value.toString());
        // else
        // icon = VALUE_ICONS.valueOf(value.toString()).getImg();
        //
        // lbl = new JLabel(icon);
        if (StringMaster.isEmpty(value.toString())) {
            return getDefaultComp(table, value, isSelected, hasFocus, row, column);
        }
        String VALUE = table.getValueAt(row, 0).toString();
        if (VALUE == null) {
            return getDefaultComp(table, value, isSelected, hasFocus, row, column);
        }

        if (StringMaster.isEmpty(VALUE.toString())) {
            return getDefaultComp(table, value, isSelected, hasFocus, row, column);
        }

        PARAMETER p = ContentManager.getPARAM(VALUE.toString());
        if (p == null) {

            PROPERTY prop = ContentManager.getPROP(VALUE.toString());
            if (prop != null) {
                return getPropertyValueComp(prop, value.toString(), VALUE);
            }

            return getDefaultComp(table, value, isSelected, hasFocus, row, column);
        }
        return getParamValueComp(value.toString(), VALUE, p);

    }

    private Component getParamValueComp(String value, String VALUE, PARAMETER p) {
        VALUE_CASES CASE = SmartTextManager.getParamCase(p, obj);
        return getValueComp(CASE, value);
    }

    private Component getPropertyValueComp(PROPERTY prop, String value, String VALUE) {
        VALUE_CASES CASE = SmartTextManager.getPropCase(prop, obj);
        return getValueComp(CASE, value);

    }

    private Component getValueComp(VALUE_CASES CASE, String value) {
        JLabel lbl = new JLabel(value.toString());
        Color color = ColorManager.DEEP_GRAY;
        if (CASE != null) {
            color = CASE.getColor();
        }
        lbl.setForeground(color);

        return lbl;
    }

    private Component getDefaultComp(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component component = new DefaultTableCellRenderer()
                .getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (value.toString().equals(G_PROPS.EMPTY_VALUE.toString())
                || table.getValueAt(row, 0).toString()
                .equals(G_PROPS.EMPTY_VALUE.toString())) {
            component = new DefaultTableCellRenderer()
                    .getTableCellRendererComponent(table, "", false, hasFocus, row, column);
            component.setBackground(Color.BLACK);
            table.setRowHeight(row, table.getRowHeight() / 2);
            return component;
        }
        return component;
    }

    private ImageIcon getDefaultIcon(String string) {
        String def = "UI\\ATTRS\\";
        // TODO Auto-generated method stub
        return ImageManager.getIcon(def + string + ".jpg");
    }

    public enum VALUE_ICONS {
        TOUGHNESS,;
        private ImageIcon img;
        private String path;

        public ImageIcon getImg() {
            // TODO Auto-generated method stub
            return img;
        }

    }
}
