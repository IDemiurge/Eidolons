package main.gui.components.table;

import main.content.OBJ_TYPE;
import main.gui.components.controls.AV_TableButtons;
import main.launch.AvConsts;
import main.swing.generic.components.G_Panel;
import main.swing.generic.components.misc.G_Table;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class AvTable extends G_Panel {
    AV_TableButtons buttons;
    boolean secondTable;
    G_Table table;

    public AvTable(DefaultTableModel model, boolean editable, boolean secondTable) {
        table = new G_Table(model, editable);
        this.secondTable = secondTable;
        buttons = new AV_TableButtons(secondTable);
        JScrollPane scroll = new JScrollPane(table);
        // scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        add(scroll, "id table, pos 0 0, w " +
                AvConsts.TABLE_WIDTH +                ",h " +
                AvConsts.TABLE_HEIGHT);
        add(buttons, "id buttons, pos 0 table.y2");

        table.setCellEditor(new MyCellEditor());
    }

    public void updateButtons(OBJ_TYPE selectedType) {
        buttons.update(selectedType);
    }

    public G_Table getTable() {
        return table;
    }
}
