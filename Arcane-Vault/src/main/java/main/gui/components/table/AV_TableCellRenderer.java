package main.gui.components.table;

import main.content.values.properties.G_PROPS;
import main.data.TableDataManager;
import main.gui.builders.EditViewPanel;
import main.v2_0.AV2;
import main.launch.ArcaneVault;
import main.system.graphics.ColorManager;
import main.system.graphics.FontMaster;
import main.system.graphics.FontMaster.FONT;
import main.system.auxiliary.StringMaster;
import main.system.images.ImageManager;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class AV_TableCellRenderer extends DefaultTableCellRenderer {

    private static final FONT FONT_TYPE = FONT.NYALA;
    private boolean widthSet = false;
    private Map<Object, Component> cache = new HashMap<>();
    private Map<Object, Component> cache2 = new HashMap<>();
    private EditViewPanel tablePanel;

    public AV_TableCellRenderer(EditViewPanel editViewPanel) {
        super();
        tablePanel = editViewPanel;
    }

    public Component getTableCellComponent(JTable table, Object value, boolean isSelected,
                                           boolean hasFocus, int row, int column) {
        Component component = null;

        // if (StringMaster.isEmpty(value.toString())) {
        // component.setBackground(Color.BLACK);
        // return component;
        // }

        boolean nameOrValue = column == TableDataManager.NAME_COLUMN;
        String name = (String) table.getValueAt(row, TableDataManager.NAME_COLUMN);
        if (!nameOrValue) {
            if (StringMaster.compare(name, G_PROPS.EMPTY_VALUE.toString())) {
                table.setRowHeight(row, table.getRowHeight() / 2);
                return renderEmpty(table, "", isSelected, hasFocus, row,
                        column);
            }
            if (StringMaster.compareByChar(name, G_PROPS.IMAGE.toString(), true)) {
                table.setRowHeight(row, TableDataManager.ROW_HEIGHT * 2);
                return renderImage(table, value, isSelected, hasFocus, row, column, component);
            }
        }

        if (value == null) {
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
                    column);
        }
        if (ArcaneVault.getSelectedType() == null) {
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
                    column);
        }
        if (value.toString().equals(G_PROPS.EMPTY_VALUE.toString())) {
            table.setRowHeight(row, table.getRowHeight() / 2);
            return renderEmpty(table, "", isSelected, hasFocus, row,
                    column);
        }


        value  =   table.getValueAt(row, TableDataManager.VALUE_COLUMN);
        return renderValueName(table, value, isSelected, hasFocus, row, column, name, nameOrValue);
        // return super.getTableCellRendererComponent(table, value, isSelected,
        // hasFocus, row, column);
    }

    private Component renderEmpty(JTable table, String s, boolean isSelected, boolean hasFocus, int row, int column) {
        Component component = super.getTableCellRendererComponent(table, "", isSelected, hasFocus, row,
                column);
        // component.setBackground(AV_ColorMaster.getDefaultBackgroundColor(tablePanel.isColorsInverted()));
        component.setBackground(AV_ColorMaster.getDefaultForegroundColor(tablePanel.isColorsInverted()));
        return component;
    }


    private Component renderValueName(JTable table, Object value, boolean isSelected,
                                      boolean hasFocus, int row, int column, String name, boolean nameOrValue) {
        JLabel component = new JLabel(nameOrValue? name :  value.toString());
        Color borderColor = AV_ColorMaster.getBorderColor(isSelected);
        if (borderColor!=null ) {
            component.setBorder(BorderFactory.createLineBorder(
                    borderColor));
        }

        // component.setBackground(AV_ColorMaster.getBackgroundColor(tablePanel.isColorsInverted(), ArcaneVault.getSelectedType(),
        //         value.toString(),name, isSelected));
        component.setForeground(AV_ColorMaster.getForegroundColor(tablePanel.isColorsInverted(), ArcaneVault.getSelectedType(),
                value.toString(),name,  isSelected));

        // if (tablePanel.isColorsInverted()) {
        //     color = ColorManager.getInvertedColor(color);
        //     color2 = ColorManager.getInvertedColor(color2);
        //
        //     color2 = color2.brighter();
        //     color = color.darker();
        //     color2 = color2.brighter();
        //     color = color.darker();
        // }
        // else {
        // color = color.brighter();
        // color2 = color2.darker();
        // color = color.brighter();
        // color2 = color2.darker();
        // }
        // component.setBackground(color);
        // component.setForeground(color2);

        component.setFont(FontMaster.getFont(FONT_TYPE, TableDataManager.FONT_SIZE, Font.ITALIC));

        return component;
    }

    private Component renderImage(JTable table, Object value, boolean isSelected, boolean hasFocus,
                                  int row, int column, Component component) {

        JLabel lbl = ImageManager.getLabel(value.toString(), TableDataManager.ROW_HEIGHT * 2,
                TableDataManager.ROW_HEIGHT * 2);
        lbl.setBackground(Color.black);

        return lbl;
    }


    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                   boolean hasFocus, int row, int column) {
        Component component = getCache(isSelected).get(value);
        if (component != null) {
            return component;
        }
        component = getTableCellComponent(table, value, isSelected, hasFocus, row, column);
        getCache(isSelected).put(value, component);
        return component;
    }

    private Map<Object, Component> getCache(boolean isSelected) {
        return isSelected ? cache : cache2;
    }
}
