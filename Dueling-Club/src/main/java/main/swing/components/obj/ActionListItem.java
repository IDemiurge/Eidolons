package main.swing.components.obj;

import main.content.CONTENT_CONSTS.FLIP;
import main.entity.Entity;
import main.entity.active.DC_UnitAction;
import main.entity.obj.ActiveObj;
import main.swing.generic.components.list.ListItem;
import main.system.auxiliary.StringMaster;
import main.system.graphics.GuiManager;
import main.system.graphics.ImageTransformer;
import main.system.images.ImageManager;
import main.system.images.ImageManager.BORDER;
import main.system.images.ImageManager.STD_IMAGES;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ActionListItem extends ListItem<Entity> {

    private DC_UnitAction action;
    private BORDER specialBorder;

    public ActionListItem(Entity item, boolean isSelected, boolean cellHasFocus) {
        super(item, isSelected, cellHasFocus, GuiManager.getSmallObjSize());
        if (item != null) {
            setToolTipText(item.getToolTip());
        }
        action = (DC_UnitAction) item;

    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (isSelected) {
            // g.drawImage(ImageManager.getSizedVersion(
            // BORDER.NEO_INFO_SELECT_HIGHLIGHT_SQUARE_64.getImagePath(),
            // 64).getEmitterPath(),
            // 0, 0, null);

        }
    }

    protected ImageIcon getCompIcon(Entity entity, boolean isSelected) {
        BufferedImage buffered = ImageManager.getBufferedImage(super
         .getCompIcon(entity, isSelected).getImage());
        if (action != null) {
            if (action.isThrow()) {
                buffered = ImageTransformer.flip(FLIP.CW90, buffered);
            }
            if (action.isOffhand()) {
                buffered = ImageTransformer.flipHorizontally(buffered);
            }
        }
        Graphics g = buffered.getGraphics();
        paintDecorations(g);
        return new ImageIcon(buffered);
    }

    private void paintDecorations(Graphics g) {
        if (action != null) {
            String mode = action.getOwnerObj().getActionMode(action);
            if (!StringMaster.isEmpty(mode)) {
                Image modeImage = ImageManager.getModeImage(mode, true, false);
                if (modeImage != null) {
                    Image img = ImageManager.getSizedVersion(modeImage, 50);
                    g.drawImage(img,
                     // getSize().width - img.getWidth(null)
                     0, getSize().height - img.getHeight(null), null);
                }
            }
            if (action.isOffhand()) {
                Image image = getImage();
                image = ImageTransformer.flipHorizontally(ImageManager.getBufferedImage(image));
                g.drawImage(image, 0, 0, null);
            }
            if (action.isThrow()) {
                Image image = STD_IMAGES.THROW.getImage();
                g.drawImage(image, obj_size - image.getWidth(null), obj_size
                 - image.getHeight(null), null);
            }
        }
    }

    @Override
    public BORDER getSpecialBorder() {
        if (specialBorder != null) {
            return specialBorder;
        }
        if (getObj() instanceof ActiveObj) {
            ActiveObj obj = (ActiveObj) getObj();
            if (obj.isBlocked()) {
                return BORDER.HIDDEN;
            }
            if (!obj.canBeActivated()) {
                return BORDER.HIDDEN;
            }
        }
        return super.getSpecialBorder();
    }

    public void setSpecialBorder(BORDER specialBorder) {
        this.specialBorder = specialBorder;
    }

    protected boolean isHighlightSelected() {
        return false;
    }

}
