package main.system.images;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by JustMe on 1/5/2017.
 */
public class CustomImage extends BufferedImage {
    private String imgPath;

    public CustomImage(String imgPath, Image image) {
        super(image.getWidth(null), image.getHeight(null),
                BufferedImage.TYPE_INT_ARGB);
        this.imgPath = imgPath;
        getGraphics().drawImage(image, 0, 0, null);
    }

    public String getImgPath() {
        return imgPath;
    }
}
