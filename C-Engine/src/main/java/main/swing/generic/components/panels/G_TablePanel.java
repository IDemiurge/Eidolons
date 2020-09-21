package main.swing.generic.components.panels;

import main.swing.generic.components.G_Panel;
import main.swing.generic.components.misc.G_Table;
import main.system.graphics.GuiManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.Arrays;
import java.util.Vector;

public class G_TablePanel extends G_Panel {

    public static final String NAME = "Name";
    public static final String VALUE = "Value";
    private G_Table table;
    private String[] emptyNames = {" ", "  "};
    private String[] names;

    public G_TablePanel(Vector<?> data, boolean b) {
        this(data, b, "");

    }

    public G_TablePanel(Vector<?> data, boolean b, String sizeInfo) {
        if (b) {
            String[] AVNames = {NAME, VALUE};
            names = AVNames;
        } else {
            names = emptyNames;
        }
        table = new G_Table(new DefaultTableModel((Vector<? extends Vector>) data, new Vector<>(
         Arrays.asList(names))) {
            public java.lang.Class<?> getColumnClass(int arg0) {
                return String.class;
            }

        }

         , b

        );
        setColumnSizes();

        JScrollPane sp = new JScrollPane(table);
        sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(sp, "pos 0 0," + sizeInfo);
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
    }

    private void setColumnSizes() {
        // table.getColumn(names[0])
        // .setMinWidth(GuiManager.getInfoNamesColumnMinWidth());
        table.getColumn(names[0]).setPreferredWidth(GuiManager.getObjSize());
        table.getColumn(names[0])
         .setMaxWidth(GuiManager.getInfoNamesColumnMaxWidth());

        table.getColumnModel().getColumn(1)
         .setPreferredWidth(128);
        table.getColumnModel().getColumn(1)
         .setMaxWidth(GuiManager.getInfoNamesColumnMaxWidth());
        // table.getColumnModel().getColumn(1)
        // .setMinWidth(GuiManager.getInfoNamesColumnMinWidth());

        table.getColumn(names[0]).setCellEditor(null);
        table.getColumn(names[1]).setCellEditor(null);

        // table.setOpaque(false);
        // ((DefaultTableCellRenderer) table.getDefaultRenderer(Object.class))
        // .setOpaque(false);
        // ((DefaultTableCellRenderer) table.getDefaultRenderer(String.class))
        // .setOpaque(false);
    }

    public G_Table getTable() {
        return table;
    }

    public void setTable(G_Table table) {
        this.table = table;
    }

    public String[] getEmptyNames() {
        return emptyNames;
    }

    public void setEmptyNames(String[] emptyNames) {
        this.emptyNames = emptyNames;
    }

    public String[] getNames() {
        return names;
    }

    public void setNames(String[] names) {
        this.names = names;
    }

}
