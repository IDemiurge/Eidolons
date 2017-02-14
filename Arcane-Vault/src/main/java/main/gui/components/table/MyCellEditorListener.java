package main.gui.components.table;

import main.system.auxiliary.log.LogMaster;

import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import java.awt.*;

public class MyCellEditorListener implements CellEditorListener {

    @Override
    public void editingStopped(ChangeEvent e) {
        Toolkit.getDefaultToolkit().beep();
        LogMaster.log(0, "" + e.getSource());

    }

    @Override
    public void editingCanceled(ChangeEvent e) {
        LogMaster.log(0, "" + e.getSource());

    }

}
