package main.swing.generic.components.editors;

import javax.swing.*;
import java.awt.event.MouseEvent;

public class CustomEditor/*<T extends HC_View<?>>*/ implements EDITOR {

    @Override
    public void launch(JTable table, int row, int column, String prevValue, MouseEvent e) {

    }

    @Override
    public String launch(String valueName, String preValue) {
        return null;
    }
}
