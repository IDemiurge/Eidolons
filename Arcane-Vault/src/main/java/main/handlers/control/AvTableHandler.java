package main.handlers.control;

import main.handlers.AvHandler;
import main.handlers.AvManager;
import main.launch.ArcaneVault;
import main.swing.generic.components.misc.G_Table;

import javax.swing.event.TableModelEvent;
import java.util.LinkedHashMap;
import java.util.Map;

public class AvTableHandler extends AvHandler {

    Map<Integer, String> copied;

    public AvTableHandler(AvManager manager) {
        super(manager);
    }

    public boolean paste() {
        //if no selection, adjust to indices
        if (getTable().getSelectedRows().length == 1 || copied.size() > 1) {
            int row = getTable().getSelectedRow();
            for (String val : copied.values()) {
                getTable().setValueAt(val, row, 1);
                TableModelEvent e=new TableModelEvent(getTable().getModel(),row++);
                ArcaneVault.getMainBuilder().getEditViewPanel().tableChanged(e);
            }
        } else
            for (int selectedRow : getTable().getSelectedRows()) {
                String val = copied.values().toArray()[0].toString();
                getTable().setValueAt(val, selectedRow, 1);
                TableModelEvent e=new TableModelEvent(getTable().getModel(),selectedRow);
                ArcaneVault.getMainBuilder().getEditViewPanel().tableChanged(e);
            }
        return true;
    }

    public boolean copy() {
        copied = new LinkedHashMap<>();
        for (int selectedRow : getTable().getSelectedRows()) {
            String name = getTable().getValueAt(selectedRow, 0).toString();
            copied.put(selectedRow, getSelected().getValue(name));
        }
        return true;
    }

    protected G_Table getTable() {
        return ArcaneVault.getMainBuilder().getEditViewPanel().getTable();
    }
}
