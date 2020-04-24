package main.swing.generic.components.editors;

import javax.swing.*;
import java.awt.event.MouseEvent;

public interface EDITOR {

    void launch(JTable table, int row, int column, String prevValue, MouseEvent e);

    String launch(String valueName, String preValue);

}
