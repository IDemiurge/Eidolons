package main.swing.generic.components;

import main.system.images.ImageManager;
import main.system.images.ImageManager.STD_IMAGES;

import javax.swing.*;
import java.awt.*;

public class CompVisuals implements ComponentVisuals {

    public Image img;
    private Dimension dimension;
    private String s;
    private JLabel label;

    public CompVisuals(String path) {
        this.s = path;
    }

    public CompVisuals(Image img) {
        this(new Dimension(img.getWidth(null), img.getHeight(null)), img);
    }

    public CompVisuals(Dimension dimension, Image img) {
        this.dimension = dimension;
        this.img = img;

    }

    public CompVisuals(Dimension dimension, String path) {
        this.dimension = dimension;
        this.s = path;
    }

    public CompVisuals(STD_IMAGES std_image) {
        this(std_image.getImage());
    }

    public int getWidth() {

        return getImage().getWidth(null);
    }

    public int getHeight() {
        return getImage().getHeight(null);
    }

    public String getImgPath() {
        return s;
    }

    public Image getImage() {
        if (img == null) {
            if (dimension != null)
                img = ImageManager.getSizedIcon(s, dimension).getImage();
            else
                img = ImageManager.getIcon(s).getImage();
        }
        return img;
    }

    public JLabel getLabel() {
        if (label == null)
            label = new JLabel(new ImageIcon(getImage()));
        return label;
    }

    public Dimension getSize() {
        if (dimension == null)
            dimension = new Dimension(getWidth(), getHeight());
        return dimension;
    }
}