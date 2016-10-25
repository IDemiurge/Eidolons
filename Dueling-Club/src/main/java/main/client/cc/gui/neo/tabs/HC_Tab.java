package main.client.cc.gui.neo.tabs;

import main.swing.generic.components.G_Component;

import java.awt.*;

public class HC_Tab {

    protected G_Component comp;
    protected boolean selected;
    protected String name;
    protected boolean centered = true;
    protected int index;
    protected HC_TabComp tabComp;
    protected String imgPath;
    protected Image image;

    public HC_Tab(Image image, G_Component comp, int index) {
        this.comp = comp;
        this.image = image;
        this.index = index;
    }

    public HC_Tab(String string, G_Component comp, int index) {
        this.comp = comp;
        this.name = string;
        this.index = index;
    }

    public HC_Tab(String string, G_Component comp) {
        this(string, comp, 0);
    }

    @Override
    public String toString() {
        return name;
    }

    public Component generateTabComp(HC_TabPanel tabPanel) {
        tabComp = new HC_TabComp(tabPanel.getTAB(), tabPanel.getTAB_SELECTED(), name, centered,
                selected, index);
        if (image != null) {
            tabComp.setImage(image);
            return tabComp;
        }
        tabComp.setImagePath(imgPath);
        tabComp.resetImage();
        return tabComp;
    }

    public G_Component getComponent() {
        return comp;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getName() {
        return name;
    }

    public void setComp(G_Component comp) {
        this.comp = comp;
    }

    public void setString(String string) {
        this.name = string;
    }

    public void setIndex(int j) {
        index = j;
        if (tabComp != null)
            tabComp.setIndex(j);

    }

    public void setImagePath(String imgPath) {
        this.imgPath = imgPath;
    }

}
