package main.swing.generic.components.misc;

import main.swing.generic.components.G_Component;
import main.system.images.ImageManager;

import java.awt.*;

public class G_VisualComponent extends G_Component {

    private Image img;
    private Dimension size;
    private boolean horizontalFlip;
    private int rotation;

    public G_VisualComponent(String imgPath) {
        this.img = ImageManager.getImage(imgPath);
        size = new Dimension(img.getWidth(null), img.getHeight(null));
        setPreferredSize(size);
        if (!isTransparent()) {
            setOpaque(true);
        }
    }

    private boolean isTransparent() {
        return true;
    }

    @Override
    public void paint(Graphics g) {
        // if (!initialized) init();
        // super.paint(g);
        int y = 0;
        int x = 0;
        g.drawImage(img, x, y, null);
    }

    public int getImageHeight() {
        return img.getHeight(null);
    }

    public int getImageWidth() {
        return img.getWidth(null);
    }
}
