package main.swing.generic.components.editors;

import javax.swing.*;
import java.awt.event.MouseEvent;

public class NumberEditor implements EDITOR {
    JSpinner spinner = new JSpinner();

    @Override
    public void launch(JTable table, int row, int column, String prevValue, MouseEvent e) {
        String valueName = table.getValueAt(row, 0).toString();
        String newVal = launch(valueName, prevValue);
        table.setValueAt(newVal, row, 1);
    }

    @Override
    public String launch(String valueName, String prevValue) {
        int value = 0;
        try {
            value = Integer.valueOf(prevValue);
        } catch (Exception e) {

        }

        spinner.setValue(value);
        // ((DefaultEditor) spinner.getEditor()).getTextField().selectAll(); !!!

        int result = JOptionPane.showConfirmDialog(null, spinner, "Setting value for " + valueName,

         JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) {
            return null;
        }
        return spinner.getValue().toString();
    }

}
