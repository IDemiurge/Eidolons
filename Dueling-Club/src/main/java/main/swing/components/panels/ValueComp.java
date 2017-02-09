package main.swing.components.panels;

import main.content.ContentManager;
import main.content.PARAMS;
import main.content.parameters.PARAMETER;
import main.entity.obj.Obj;
import main.swing.generic.components.G_Panel;
import main.system.auxiliary.ColorManager;
import main.system.auxiliary.FontMaster;
import main.system.auxiliary.FontMaster.FONT;
import main.system.images.ImageManager;
import main.system.math.MathMaster;

import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class ValueComp extends G_Panel {
    protected PARAMETER param;
    protected Color c;
    protected int percentage;
    protected Obj obj;
    protected PARAMETER c_param;
    protected Integer c_val;
    protected Integer max_val;
    protected PARAMETER perc_param;
    protected BufferedImage fillerImage;
    protected int length;

    public ValueComp(VISUALS v, PARAMS p) {
        super(v);
        this.param = p;
        this.c_param = getCurrentParam();
        this.perc_param = getPercentageParam();
        setToolTipText(p.toString());
    }

    protected PARAMETER getPercentageParam() {
        return ContentManager.getPercentageParam(param);
    }

    protected PARAMETER getCurrentParam() {
        return ContentManager.getCurrentParam(param);
    }

    public void refresh() {
        if (getObj() == null) {
            return;
        }

        c_val = getObj().getIntParam(c_param);
        max_val = getObj().getIntParam(param);

        percentage = initPercentage();
        super.refresh();
    }

    protected Integer initPercentage() {
        if (!(obj instanceof Obj)) {
            c_val = max_val;
            return MathMaster.PERCENTAGE;
        } else {
            // ((Obj) getObj()).resetPercentages();
            return getObj().getIntParam(perc_param);

        }
    }

    @Override
    public void paint(Graphics g) {
        if (getObj() == null) {
            return;
        }

        // TODO adjust for Visibility etc!
        g.drawImage(getVisuals().getImage(), 0, 0, null);

        if (fillerImage == null) {
            fillerImage = ImageManager.getBufferedImage(ImageManager.getImage(getCompPath()
                    + param.getName() + ".png"));
        }

        length = fillerImage.getHeight() * percentage / MathMaster.PERCENTAGE;
        // paintNumeric = true;
        // if (height < getDefaultTextY() +
        // FontMaster.getFontHeight(getNumbersFont())) {
        // paintNumeric = false;
        // paintNumbers(g);
        // }
        // paintLiquid(g);

        BufferedImage liquidImage = ImageManager.getNewBufferedImage(visuals.getWidth(), visuals
                .getHeight());
        paintLiquid(liquidImage.getGraphics());
        // if ()
        g.drawImage(liquidImage, 0, 0, null);
        // if (paintNumeric)
        paintNumbers(g);
    }

    protected int getDefaultTextY() {
        return visuals.getHeight() / 2 + getTextOffsetY();
    }

    protected int getTextOffsetY() {
        return 0;
    }

    protected void paintNumbers(Graphics g) {
        g.setFont(getNumbersFont());
        g.setColor(ColorManager.GOLDEN_WHITE);

        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        String string = c_val + "/" + max_val; // N\A
        int y = getDefaultTextY();

        // if (height < getDefaultTextY() +
        // FontMaster.getFontHeight(getNumbersFont()) / 2
        // && height > getDefaultTextY() -
        // FontMaster.getFontHeight(getNumbersFont()) / 2) {
        // y = visuals.getHeight() - height;
        // }
        // if (height != 0)
        // Math.max(getDefaultTextY(), visuals.getHeight() - height
        // - FontMaster.getFontHeight(getNumbersFont()));
        int x = getX(g, string);
        g.drawString(string, x,

                y

        );
    }

    protected int getX(Graphics g, String string) {
        return visuals.getWidth() / 2 - FontMaster.getStringWidth(g.getFont(), string) / 2;
    }

    protected Font getNumbersFont() {
        return FontMaster.getFont(FONT.AVQ, 14, Font.PLAIN);
    }

    protected void paintLiquid(Graphics g) {

        BufferedImage drawImg = getLiquidPaintImage();
        if (isVertical()) {
            g.setClip(0, fillerImage.getHeight() - length, fillerImage.getWidth(), length);
        } else {
            g.setClip(0, 0, fillerImage.getWidth() - length, fillerImage.getHeight());
        }
        g.drawImage(drawImg, 0, 0, null);
    }

    protected boolean isVertical() {
        return false;
    }

    protected BufferedImage getLiquidPaintImage() {
        BufferedImage drawImg = ImageManager.getNewBufferedImage(fillerImage.getWidth(),
                fillerImage.getHeight());

        drawImg.getGraphics().drawImage(fillerImage, 0, 0, null);
        return drawImg;
    }

    protected abstract String getCompPath();

    public Obj getObj() {
        return obj;
    }

    public void setObj(Obj obj) {
        this.obj = obj;

    }

}
