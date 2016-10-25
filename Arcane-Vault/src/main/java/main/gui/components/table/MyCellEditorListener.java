package main.gui.components.table;

import java.awt.Toolkit;

import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;

public class MyCellEditorListener implements CellEditorListener {

	@Override
	public void editingStopped(ChangeEvent e) {
		Toolkit.getDefaultToolkit().beep();
		main.system.auxiliary.LogMaster.log(0, "" + e.getSource());

	}

	@Override
	public void editingCanceled(ChangeEvent e) {
		main.system.auxiliary.LogMaster.log(0, "" + e.getSource());

	}

}
