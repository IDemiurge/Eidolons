package main.swing.components.panels.secondary;

import main.entity.item.DC_HeroItemObj;
import main.swing.generic.components.misc.GraphicComponent;
import main.swing.generic.components.panels.G_ListPanel;
import main.system.auxiliary.GuiManager;
import main.system.images.ImageManager;
import main.system.images.ImageManager.BORDER;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class DroppedItemPage extends G_ListPanel<DC_HeroItemObj> implements
        ListCellRenderer<DC_HeroItemObj> {

    public DroppedItemPage(List<DC_HeroItemObj> list) {
        super(list);
        getList().setCellRenderer(this);
    }

    @Override
    public void setInts() {
        rowsVisible = 2;
        minItems = DroppedItemPanel.PAGE_SIZE;
        int w = minItems / rowsVisible * getItemSize();
        int h = rowsVisible * getItemSize();
        sizeInfo = "w " + w + ", h " + h;

    }

    public int getItemSize() {
        return GuiManager.getSmallObjSize();
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends DC_HeroItemObj> list,
                                                  DC_HeroItemObj value, int index, boolean isSelected, boolean cellHasFocus) {
        if (value == null) {
            return new GraphicComponent(ImageManager.getGraveyardEmptyItemIconPath());
        }
        Image image = value.getIcon().getImage();
        if (isSelected) {
            image = ImageManager.applyBorder(image, BORDER.HIGHLIGHTED_96);
        }
        return new GraphicComponent(image);
    }

}
