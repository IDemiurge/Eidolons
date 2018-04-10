package main.swing.generic.components.editors.lists;

import main.content.ContentValsManager;
import main.data.DataManager;
import main.entity.Entity;
import main.swing.generic.components.panels.G_InfoPanel;
import main.system.graphics.GuiManager;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.util.Vector;

public class ListInfoPanel extends G_InfoPanel implements ListSelectionListener {

    public ListInfoPanel(Entity infoObj) {
        super(infoObj);
        setInts();
        refresh();
    }

    @Override
    protected Vector<Vector<String>> initData() {
        if (infoObj != null) {
            this.displayedValues = ContentValsManager.getFullValueList(infoObj.getOBJ_TYPE(), true);
            // try {
            // displayedValues = null;
            // List<VALUE> values = ValuePageManager
            // .getValuePagesForAV(OBJ_TYPES.getType(obj_type));
            // displayedValues = StringMaster.convertToStringList(values);
            // } catch (Exception e) {
            //
            // main.system.ExceptionMaster.printStackTrace(e);
            // }
            // if (displayedValues == null)
            // this.displayedValues = ContentManager.getFullValueList(infoObj
            // .getOBJ_TYPE_ENUM().getName(), true);
        }

        return super.initData();
    }

    @Override
    public void refresh() {
        super.refresh();
    }

    @Override
    public void setInts() {
        sizeInfo = "w 4*" + GuiManager.getSquareCellSize() + "!, h (" + 8 + ")*"
         + GuiManager.getSmallObjSize() + "!";

    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        Object value = ((JList) e.getSource()).getSelectedValue();
        if (value == null) {
            return;
        }
        if (value instanceof Entity) {
            setInfoObj((Entity) value);
            return;
        }
        setInfoObj(DataManager.getType(value.toString(), obj_type));

        // infoObj = DataManager.getType(value.toString(), TYPE);
        refresh();
    }

}
