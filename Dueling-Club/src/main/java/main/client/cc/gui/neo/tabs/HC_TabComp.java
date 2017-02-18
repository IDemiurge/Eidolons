package main.client.cc.gui.neo.tabs;

import main.swing.generic.components.ComponentVisuals;
import main.swing.generic.components.G_Panel;
import main.system.graphics.ColorManager;
import main.system.graphics.FontMaster;
import main.system.graphics.FontMaster.FONT;
import main.system.graphics.MigMaster;
import main.system.images.ImageManager;

import java.awt.*;

public class HC_TabComp extends G_Panel {
    // public static final ComponentVisuals SMALL_TAB =
    // ComponentVisuals.SMALL_TAB;
    // public static final ComponentVisuals SMALL_TAB_SELECTED =
    // ComponentVisuals.SMALL_TAB_SELECTED;

    private FONT FONT_TYPE = FONT.MAIN;
    private int FONT_SIZE_SELECTED = 17; // small?
    private int FONT_SIZE = 16;
    private int OFFSET = 4;
    private int imgSize;
    private boolean centered;
    private boolean selected;
    private String string;
    private String imagePath;
    private Image image;
    private int index;
    private ComponentVisuals visualsSelected;
    private ComponentVisuals visualsNormal;
    private Font selectedFont;
    private Font font;

    public HC_TabComp(String imgPath, boolean centered, boolean selected, int index) {
        this(HC_TabPanel.TAB_DEFAULT, HC_TabPanel.TAB_SELECTED_DEFAULT, imgPath, centered,
                selected, index);
    }

    public HC_TabComp(ComponentVisuals visualsNormal, ComponentVisuals visualsSelected,
                      String imgPath, boolean centered, boolean selected, int index) {
        super((selected) ? visualsSelected : visualsNormal);
        this.string = imgPath;
        this.visualsNormal = visualsNormal;
        this.visualsSelected = visualsSelected;
        this.selected = selected;
        this.centered = centered;
        this.index = index;
        imgSize = getVisuals().getHeight() - OFFSET;
    }

    public void resetImage() {
        if (ImageManager.isImage(imagePath)) {
            image = ImageManager.getSizedVersion(imagePath, imgSize).getImage();
        } else if (ImageManager.isImage(string)) {
            image = ImageManager.getSizedVersion(string, imgSize).getImage();
        }

        // if (PartyManager.getParty().getLeader().getImagePath() ){
        // if (selected) {
        // image = ImageManager.applyBorder(image, BORDER.HIGHLIGHTED_96);
        // }
    }

    private Font getSelectedFont() {
        if (selectedFont == null) {
            selectedFont = FontMaster.getFont(FONT_TYPE, FONT_SIZE_SELECTED, Font.BOLD);
        }
        return selectedFont;
    }

    private Font getDefaultFont() {
        if (font == null) {
            font = FontMaster.getFont(FONT_TYPE, FONT_SIZE, Font.PLAIN);
        }
        return font;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    public void paint(Graphics g) {
        if (selected) {
            visuals = visualsSelected;
        } else {
            visuals = visualsNormal;
        }
        super.paint(g);
        if (image != null) {
            g.drawImage(image, getImgX(), getImgY(), null);
        } else {
            g.setFont(getTextFont());
            g.setColor(ColorManager.GOLDEN_WHITE);
            g.drawString(string, getTextX(), getTextY());
        }

    }

    private int getImgY() {
        return OFFSET / 2;
    }

    private int getImgX() {
        if (centered) {
            return MigMaster.getCenteredPosition(visuals.getWidth(), imgSize);
        }
        return OFFSET;
    }

    public Font getTextFont() {
        return selected ? getSelectedFont() : getDefaultFont();
    }

    private int getTextY() {
        return MigMaster.getCenteredTextPositionY(getTextFont(), visuals.getHeight()) + 2;
    }

    private int getTextX() {
        if (centered) {
            return MigMaster.getCenteredTextPosition(string, getTextFont(), visuals.getWidth());
        }
        return OFFSET;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int i) {
        index = i;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public void setFONT_TYPE(FONT fONT_TYPE) {
        FONT_TYPE = fONT_TYPE;
    }

    public void setFONT_SIZE_SELECTED(int fONT_SIZE_SELECTED) {
        FONT_SIZE_SELECTED = fONT_SIZE_SELECTED;
    }

    public void setFONT_SIZE(int fONT_SIZE) {
        FONT_SIZE = fONT_SIZE;
    }

    public void setOFFSET(int oFFSET) {
        OFFSET = oFFSET;
    }

}
