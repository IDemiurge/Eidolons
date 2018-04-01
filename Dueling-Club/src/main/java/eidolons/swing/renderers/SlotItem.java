package eidolons.swing.renderers;

import main.content.enums.entity.UnitEnums.COUNTER;
import main.entity.Entity;
import main.entity.Ref.KEYS;
import main.entity.obj.Obj;
import eidolons.entity.obj.attach.DC_HeroAttachedObj;
import eidolons.game.battlecraft.rules.mechanics.CoatingRule;
import main.swing.generic.components.list.ListItem;
import eidolons.system.graphics.ImageTransformer;
import main.system.images.ImageManager;
import main.system.images.ImageManager.BORDER;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class SlotItem extends ListItem<DC_HeroAttachedObj> {

    public SlotItem(DC_HeroAttachedObj value, boolean isSelected, boolean cellHasFocus) {
        super(value, isSelected, cellHasFocus, 64);
    }

    @Override
    protected ImageIcon getCompIcon(Entity entity, boolean isSelected) {
        Obj obj = getObj().getRef().getObj(KEYS.AMMO);
        ImageIcon compIcon = super.getCompIcon(entity, isSelected);
        if (obj == null) {
            return drawCounters(compIcon.getImage(), entity);
        }

        Image ammo = ImageTransformer.getCircleCroppedImage(ImageManager.applyBorder(ImageManager
          .getSizedVersion(obj.getImagePath(),
           BORDER.CIRCLE_GLOW_40.getImage().getWidth(null)).getImage(),
         BORDER.CIRCLE_GLOW_40));
        return drawCounters(ImageManager.applyImage(compIcon.getImage(), ammo, 38, 28, false), obj);
    }

    private ImageIcon drawCounters(Image image, Entity coatedObj) {
        int x = 0;
        int y = 0;
        BufferedImage bufferedImage = ImageManager.getBufferedImage(image);
        Graphics g = bufferedImage.getGraphics();
        for (COUNTER c : CoatingRule.COATING_COUNTERS) {
            if (coatedObj.getCounter(c.getName()) > 0) {
//                g.drawImage(c.getImage(), x, y, null);
                x++;
            }
        }
        return new ImageIcon(bufferedImage);
    }
}
