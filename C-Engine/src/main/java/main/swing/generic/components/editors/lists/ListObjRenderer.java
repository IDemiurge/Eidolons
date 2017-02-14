package main.swing.generic.components.editors.lists;

import main.content.OBJ_TYPE;
import main.data.DataManager;
import main.entity.Entity;
import main.swing.SwingMaster;
import main.swing.generic.components.editors.lists.GenericListChooser.LC_MODS;
import main.swing.generic.components.list.ListItem;
import main.swing.generic.components.panels.G_InfoPanel;
import main.system.graphics.FontMaster;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ListObjRenderer<String> implements ListCellRenderer<String> {

    List<LC_MODS> mods;
    private OBJ_TYPE TYPE;
    private G_InfoPanel infoPanel;

    public ListObjRenderer(OBJ_TYPE TYPE) {
        this.TYPE = TYPE;
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends String> list, String value,
                                                  int index, boolean isSelected, boolean cellHasFocus) {
        if (TYPE == null) {
            return getLabel(value, isSelected);
        }
        Entity type = null;
        if (value != null) {
            type = DataManager.getType(value.toString(), TYPE);
        }
        if (type == null) {
            return new JLabel(value.toString());
        }
        ListItem<Entity> item = new ListItem<>(type, isSelected, cellHasFocus, 0);
        // item.setIcon(ListRenderer.getCompIcon(type, isSelected));
        if (mods != null) {
            Component component = item;
            for (LC_MODS sub : mods) {
                switch (sub) {
                    case TEXT_DISPLAYED:
                        component = SwingMaster.decorateWithText(type.getName(), Color.black,
                                component, "pos 0 20");
                        break;
                    default:
                        break;

                }
            }
            return component;
        }
        return item;
    }

    private Component getLabel(String value, boolean isSelected) {
        JLabel component = new JLabel(value.toString());
        if (isSelected) {
            component.setForeground(new Color(50, 70, 200));
            component.setFont(FontMaster.getDefaultFont(16));
        }
        return component;
    }

    public G_InfoPanel getInfoPanel() {
        return infoPanel;
    }

    public void setInfoPanel(G_InfoPanel infoPanel) {
        this.infoPanel = infoPanel;
    }

    public List<LC_MODS> getMods() {
        return mods;
    }

    public void setMods(List<LC_MODS> mods) {
        this.mods = mods;
    }

}
