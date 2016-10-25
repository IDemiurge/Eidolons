package main.swing.generic.components.editors;

import javax.swing.*;

public interface EDITOR {

    void launch(JTable table, int row, int column, String prevValue);

    String launch(String valueName, String preValue);

}
