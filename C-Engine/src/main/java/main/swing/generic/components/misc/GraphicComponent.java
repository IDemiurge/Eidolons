package main.swing.generic.components.misc;

import main.system.images.ImageManager;
import main.system.images.ImageManager.STD_IMAGES;

import javax.swing.*;
import java.awt.*;

public class GraphicComponent extends JComponent {

    int x = 0;
    int y = 0;
    private Image img;
    private Dimension compSize;
    private Object dataObject;

    public GraphicComponent(STD_IMAGES template) {
        this(template.getImage());
    }

    public GraphicComponent(STD_IMAGES template, String tooltip) {
        this(template);
        setToolTipText(tooltip);
    }

    public GraphicComponent(Image img, String tooltip) {
        this(img);
        setToolTipText(tooltip);
    }

    public GraphicComponent(STD_COMP_IMAGES template) {
        this(template.getImg());
    }

    public GraphicComponent(Image img) {
        this.setImg(img);
    }

    public GraphicComponent(String path) {
        this(ImageManager.getImage(path));
    }

    @Override
    public Dimension getPreferredSize() {
        return compSize;
    }

    @Override
    public Dimension getMinimumSize() {
        return compSize;
    }

    @Override
    public Dimension getMaximumSize() {
        return compSize;
    }

    @Override
    public void paint(Graphics g) {
        if (getImg() != null) {
            g.drawImage(getImg(), x, y, null);
        }
    }

    public Image getImg() {
        return img;
    }

    public void setImg(Image img) {
        this.img = img;
        if (img != null) {
            this.compSize = new Dimension(img.getWidth(null), img.getHeight(null));
        }
    }

    public Object getDataObject() {
        return dataObject;
    }

    public void setDataObject(Object object) {
        this.dataObject = object;

    }

    public Dimension getCompSize() {
        return compSize;
    }

    public void setCompSize(Dimension compSize) {
        this.compSize = compSize;
    }

    public enum STD_COMP_IMAGES {

        ARROW_2_DOWN("ui/components/down2.png"),
        ARROW_2_UP("ui/components/up2.png"),
        ARROW_2_LEFT("ui/components/left2.png"),
        ARROW_2_RIGHT("ui/components/right2.png"),

        ARROW_3_DOWN("ui/components/down3.png"),
        ARROW_3_UP("ui/components/up3.png"),
        ARROW_3_LEFT("ui/components/left3.png"),
        ARROW_3_RIGHT("ui/components/right3.png"),

        ARROW_4_DOWN("ui/components/down4.png"),
        ARROW_4_UP("ui/components/up4.png"),
        ARROW_4_LEFT("ui/components/left4.png"),
        ARROW_4_RIGHT("ui/components/right4.png"),

        GOLD("ui/components/small/crowns2.png"),
        XP("ui/components/small/xp.png"),
        LOCK("ui/components/small/locked.png"),
        QUESTION("ui/components/small/question.png"),
        GLORY("ui/components/small/glory.png"),;

        public Image img;
        private String s;

        STD_COMP_IMAGES(String path) {
            this.setS(path);
        }

        public Image getImg() {
            if (img == null) {
                img = ImageManager.getImage(getPath());
            }
            return img;
        }

        public JLabel getLabel() {
            if (img == null) {
                img = ImageManager.getIcon(getPath()).getImage();
            }
            return new JLabel(new ImageIcon(img));
        }

        public int getWidth() {
            return img.getWidth(null);
        }

        public int getHeight() {
            return img.getHeight(null);
        }

        public String getPath() {
            return s;
        }

        public void setS(String s) {
            this.s = s;
        }
    }

}
