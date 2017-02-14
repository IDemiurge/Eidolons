package main.swing.components.panels.page.small;

import main.content.properties.G_PROPS;
import main.entity.Entity;
import main.system.graphics.GuiManager;
import main.system.images.ImageManager;

import javax.swing.*;
import java.awt.*;

@SuppressWarnings("serial")
public class SmallItem extends JLabel {
    private String name;
    private String imagePath;
    private String toolTip;
    private boolean empty;
    private Entity entity;
    private Object arg;

    public SmallItem() {
        empty = true;
        initLabel();
    }

    public SmallItem(Entity entity) {
        this(entity.getProperty(G_PROPS.IMAGE, true), entity.getName(), entity.getToolTip());
        this.entity = entity;
    }

    public SmallItem(String imagePath, String name, String toolTip) {
        this(null, imagePath, name, toolTip);
    }

    public SmallItem(Object arg, String imagePath, String name, String toolTip) {
        this.imagePath = imagePath;
        this.name = name;
        this.toolTip = toolTip;
        this.arg = arg;

        initLabel();

    }

    private void initLabel() {

        ImageIcon icon = ImageManager.getIcon(imagePath);
        if (!empty) {
            if (!ImageManager.isValidIcon(icon)) {
                imagePath = getDefaultImagePath();
                icon = ImageManager.getIcon(imagePath);
            }
        }
        if (!ImageManager.isValidIcon(icon)) {
            setIcon(ImageManager.getIcon(ImageManager.getEmptyListIconSmall())); // font?!

        } else {
            if (icon.getIconHeight() > GuiManager.getTinyObjSize()) {
                icon = new ImageIcon(ImageManager.getSizedVersion(icon.getImage(), new Dimension(
                        GuiManager.getTinyObjSize(), GuiManager.getTinyObjSize())));
            }
            setIcon(icon);
        }

        setToolTipText(toolTip);

    }

    private String getDefaultImagePath() {
        return ImageManager.getUnknownSmallItemIconPath();
    }

    @Override
    public String toString() {
        return "Small Icon Item: " + "" + name;
    }

    public String getName() {
        return name;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getToolTip() {
        return toolTip;
    }

    public boolean isEmpty() {
        return empty;
    }

    public Entity getEntity() {
        return entity;
    }

    public Object getArg() {
        return arg;
    }
}
