package main.swing.generic.components.editors.lists;

import main.content.OBJ_TYPE;
import main.data.DataManager;
import main.data.ability.construct.VariableManager;
import main.entity.Entity;
import main.entity.type.ObjType;
import main.system.auxiliary.FontMaster;
import main.system.images.ImageManager;
import main.system.images.ImageManager.BORDER;

import javax.swing.*;
import java.awt.*;

public class ListRenderer implements ListCellRenderer<String> {

    private boolean ENUM;
    private OBJ_TYPE TYPE;

    public ListRenderer(boolean ENUM, OBJ_TYPE TYPE) {
        this.TYPE = TYPE;
        this.ENUM = ENUM;
        // listener = new ListListener(this);
    }

    public static JLabel getImgComp(boolean isSelected, String value, Entity entity) {
        JLabel component = null;
        component = new JLabel(getCompIcon(entity, isSelected));
        component.setToolTipText(value);
        if (ImageManager.getImage(entity.getImagePath()) == null) {
            component.setText(value);
        }
        return component;
    }

    static ImageIcon getCompIcon(Entity entity, boolean isSelected) {
        String imgName = entity.getImagePath();
        Image img = ImageManager.getImage(imgName);
        boolean noImg = false;
        if (img == null) {
            noImg = true;
            img = ImageManager.getEmptyUnitIcon().getImage();
        }
        if (img.getWidth(null) > ImageManager.getMaxTypeIconSize()
                || img.getHeight(null) > ImageManager.getMaxTypeIconSize()) {
            img = ImageManager.getSizedVersion(img, new Dimension(ImageManager
                    .getMaxTypeIconSize(), ImageManager.getMaxTypeIconSize()));
        }

        ImageIcon icon = new ImageIcon(img);
        if (isSelected) {
            icon = new ImageIcon(
                    ImageManager.applyBorder(icon.getImage(), BORDER.HIGHLIGHTED));
        }
        return icon;
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends String> list, String value, int index, boolean isSelected, boolean cellHasFocus) {
        Component component = null;
        value = VariableManager.removeVarPart(value);
        if (!ENUM) {
            ObjType type = DataManager.getType(value, TYPE);
            // if (type == null) {
            // ((DefaultListModel<String>) list.getModel())
            // .removeElement(value);
            //
            // return new JLabel("No image");
            // }
            if (type == null) {
                return new JLabel(value);
            }
            component = getImgComp(isSelected, value, type);

        } else {

            component = new JLabel(value);
            if (isSelected) {
                component.setForeground(new Color(50, 70, 200));
                component.setFont(FontMaster.getDefaultFont());
            }
        }
        // component.addMouseListener(listener);
        return component;
    }

}
