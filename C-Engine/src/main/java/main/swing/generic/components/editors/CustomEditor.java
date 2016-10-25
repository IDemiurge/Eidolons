package main.swing.generic.components.editors;

import javax.swing.*;

public class CustomEditor/*<T extends HC_View<?>>*/ implements EDITOR {

    @Override
    public void launch(JTable table, int row, int column, String prevValue) {

    }

    @Override
    public String launch(String valueName, String preValue) {
        return null;
    }
}
