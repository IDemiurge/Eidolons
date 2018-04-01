package main.swing.generic.components.editors;

import main.swing.generic.components.misc.G_Table;
import main.system.graphics.FontMaster;

import javax.swing.*;

public class TextEditor implements EDITOR {

    private static final int MAX_WIDTH = 125;
    private static final int FONT_SIZE = 16;
    private static final int MIN_LENGTH = 55;
    static JTextField textField;
    // private G_Panel textPanel;
    private boolean forced;

    public TextEditor() {

    }

    public static String inputTextLargeField(String tip, String prevValue) {
        textField = new JTextField(prevValue);
        textField.setFont(FontMaster.getDefaultFont(FONT_SIZE));
        textField.setColumns(Math.min(prevValue.length() * 2, MAX_WIDTH));
        //

        textField.selectAll();
        // SwingUtilities.invokeLater(new Runnable() {
        // @Override
        // public void run() {
        // WaitMaster.WAIT(1225);
        // textField.requestFocus();
        // textField.selectAll();
        // }
        // });
        int result = JOptionPane.showConfirmDialog(null,
         // // new FormulaBuilder(
         textField
         // // ) .getPanel()
         , tip, JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) {
            return null;
        }
        return textField.getText();
    }

    public String launch(String value, String prevValue) {
        return launch(prevValue);
    }

    public String launch(String prevValue) {
        // textPanel = new G_Panel();
        // textPanel.add(lbl);
        // textPanel.add(textField);
        // textPanel.add(formulaBuilder);

        boolean small = prevValue.length() < MIN_LENGTH;
        if (forced && prevValue.length() > MIN_LENGTH / 2) {
            small = !small;
        }
        if (small) {
            return JOptionPane.showInputDialog("Input value", prevValue);
        }

        return inputTextLargeField("Input value", prevValue);
    }

    public void launch(G_Table table, int row, int column, String value, boolean forced) {
        this.forced = forced;
        try {
            launch(table, row, column, value);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        } finally {
            forced = false;
        }

    }

    @Override
    public void launch(JTable table, int row, int column, String prevValue) {
        String newVal = null;
        try {
            newVal = launch(prevValue);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        } finally {
            forced = false;
        }
        // JOptionPane.showInputDialog("Setting value for "
        // + table.getValueAt(row, 0), prevValue);
        // Window.getOwnerlessWindows()[0].getComponents()[0].
        if (newVal != null) {
            table.setValueAt(newVal, row, 1);
        }
    }

}
