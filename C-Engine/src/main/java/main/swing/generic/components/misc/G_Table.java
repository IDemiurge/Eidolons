package main.swing.generic.components.misc;

import main.system.auxiliary.log.LogMaster;
import main.system.graphics.FontMaster;
import main.system.graphics.FontMaster.FONT;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.*;

/**
 * as InfoPanel or for AV
 *
 * @author Regulus
 */

public class G_Table extends JTable {

    private Boolean editable;

    public G_Table(TableModel data, boolean editable) {
        super(data);
        this.editable = editable;
        setBackground(Color.white);
        setForeground(Color.black);
        // setOpaque(false);
        setFont(FontMaster.getFont(FONT.MAIN, 14, Font.PLAIN));
        setShowGrid(false);
        setShowVerticalLines(false);
        setShowHorizontalLines(false);
        // mouse listener for showing long values

    }

    public void setEditListener(int i, CellEditorListener l) {

        LogMaster.log(0, getColumnName(i) + ""
         + getColumn(getColumnName(i)).getCellEditor());

        getColumn(getColumnName(i)).getCellEditor().addCellEditorListener(l);

    }

    public void setEditor(int i, TableCellEditor editor) {
        getColumn(getColumnName(i)).setCellEditor(editor);
    }

    public void setRenderers(TableCellRenderer cellRenderer1, int i) {

        getColumn(getColumnName(i)).setCellRenderer(cellRenderer1);

        // getColumn(getColumnName(1)).setCellRenderer(cellRenderer);
    }

    // public class EditRenderMapper {
    // public enum EDITORS {
    // FILECHOOSER, LIST_
    // }
    //
    // }

    public boolean isCellEditable(int row, int col) {
        if (col == 0) {
            return false;
        }

        if (editable != null) {
            return (editable);
        }

        return true;
    }

}
