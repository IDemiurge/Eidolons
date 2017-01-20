package main.libgdx.old;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

/**
 * Created with IntelliJ IDEA.
 * Date: 26.10.2016
 * Time: 15:42
 * To change this template use File | Settings | File Templates.
 */
public class ItemPanel extends Group {

    private static final String emptySlotImagePath = "\\UI\\EMPTY_ITEM.jpg";
    private Image[] slots = new Image[3];
    private Texture emptySlotTexture;
    private String imagePath;

    public ItemPanel(String imagePath) {
        this.imagePath = imagePath;
    }

    public ItemPanel init() {
        emptySlotTexture = new Texture(imagePath + emptySlotImagePath);

        for (int i = 0; i < slots.length; i++) {
            Image im = new Image(emptySlotTexture);
            im.setX(i * im.getWidth());
            slots[i] = im;
            addActor(im);
        }

        setWidth(slots[0].getWidth() * 3);
        setHeight(slots[0].getHeight());
        return this;
    }

    public void setSlot(Object o, int slotId) {
        //temp signature
    }
}
