package main.swing.generic.components.panels;

import main.content.ContentValsManager;
import main.entity.Entity;
import main.swing.generic.components.G_Panel;
import main.system.images.ImageManager;

import javax.swing.*;
import java.util.Collection;
import java.util.Vector;

public abstract class G_InfoPanel extends G_Panel {
    public G_TablePanel table;
    protected Collection<String> displayedValues;
    protected Collection<String> specialStrings;
    protected Collection<String> specialValues;
    protected Entity infoObj;
    protected ImageIcon pic;
    protected String sizeInfo = "";
    protected String obj_type;

    public G_InfoPanel(Entity infoObj) {
        setInts();
        this.infoObj = infoObj;
        refresh();

    }

    public abstract void setInts();

    protected Vector<Vector<String>> initData() {

        if (infoObj == null) {
            return getEmptyData();
        }
        Vector<Vector<String>> data = new Vector<>();

        for (String v : displayedValues) {
            Vector<String> vector = new Vector<>();
            vector.add(v);
            vector.add(infoObj.getValue(ContentValsManager.getValue(v)));
            data.add(vector);
        }

        return data;
    }

    protected Vector<Vector<String>> getEmptyData() {
        // Vector<Vector<String>> src.main.data = new Vector<Vector<String>>();
        // for (String s : displayedValues) {
        // Vector<String> line = new Vector<String>();
        // line.add(s);
        // line.add("");
        // src.main.data.add(line);
        // }
        // return src.main.data;
        return null;
    }

    @Override
    public void refresh() {
        if (infoObj != null) {
            pic = ImageManager.getIcon(infoObj.getImagePath());
        } else {
            pic = ImageManager.getEmptyUnitIcon();
        }

        removeAll();

        addLabel();

        // addSpecialValues();

        addTable();
    }

    private void addLabel() {
        add(new JLabel(pic), "id lbl, pos 0 0");
    }

    private void addSpecialValues() {
        // TODO instead, i can just make infopanel use something other than
        // Jtable!
        /**
         * smart render
         *
         */
        for (String valName : specialValues) {
            // infoObj.getValue(value);
            // i++;
            // specialStrings.get(i);
        }

    }

    @Override
    public void dataChanged() {
        // setInfoObj(infoObj.getGame().getManager().getInfoObj());
        super.dataChanged();
    }

    protected void addTable() {
        Vector<Vector<String>> data = initData();
        table = new G_TablePanel(data, isEditable(), sizeInfo);
        add(table, "pos lbl.x2 0 ");
        revalidate();
    }

    protected boolean isEditable() {
        return false;
    }

    public Collection<String> getDisplayedValues() {
        return displayedValues;
    }

    public void setDisplayedValues(Collection<String> displayedValues) {
        this.displayedValues = displayedValues;
        refresh();
    }

    public Entity getInfoObj() {
        return infoObj;
    }

    public void setInfoObj(Entity infoObj) {
        this.infoObj = infoObj;
        if (infoObj != null) {
            obj_type = infoObj.getOBJ_TYPE();
        }
        // refresh();
    }

}
