package main.swing.components.panels.secondary;

import main.entity.obj.unit.DC_HeroObj;
import main.swing.generic.components.misc.GraphicComponent;
import main.swing.generic.components.panels.G_ListPanel;
import main.system.auxiliary.GuiManager;
import main.system.graphics.ImageTransformer;
import main.system.images.ImageManager;
import main.system.images.ImageManager.BORDER;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class DeadUnitPage extends G_ListPanel<DC_HeroObj> implements ListCellRenderer<DC_HeroObj> {

    public DeadUnitPage(List<DC_HeroObj> list) {
        super(list);
        getList().setCellRenderer(this);
    }

    @Override
    public void setInts() {
        rowsVisible = 2;
        minItems = DeadUnitPanel.PAGE_SIZE;
        int w = minItems / rowsVisible * getItemSize();
        int h = rowsVisible * getItemSize();
        sizeInfo = "w " + w + ", h " + h;
        setObj_size(96);
    }

    public int getItemSize() {
        return GuiManager.getObjSize();
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends DC_HeroObj> list,
                                                  DC_HeroObj value, int index, boolean isSelected, boolean cellHasFocus) {
        if (value == null) {
            return new GraphicComponent(ImageManager.getEmptyIcon(getItemSize()).getImage());
        }
        Image image = ImageManager.getSizedIcon(value.getImagePath(),
                new Dimension(getItemSize(), getItemSize())).getImage();
        image = ImageTransformer.getGrayScale(ImageManager.getBufferedImage(image));
        // TODO add emblem?
        if (isSelected) {
            image = ImageManager.applyBorder(image, BORDER.HIGHLIGHTED_96);
        } else if (value.isTargetHighlighted()) {
            if (value.isMine()) {
                image = ImageManager.applyBorder(image, BORDER.HIGHLIGHTED_BLUE);
            } else {
                image = ImageManager.applyBorder(image, BORDER.HIGHLIGHTED_RED);
            }
        }
        return new GraphicComponent(image);
    }

}
