package main.system.graphics;

import main.data.filesys.PathFinder;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.launch.CoreEngine;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class FontMaster {
    public final static int SIZE = 16;
    public static final int MEDIUM_FONT_SIZE = 17;
    public static Font AVQ;
    public static Font SC;
    public static Font DARK;
    public static Font MAIN;
    public static Font NYALA;
    private static boolean initialized = false;

    public static void setUIFont(javax.swing.plaf.FontUIResource f) {
        java.util.Enumeration keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value != null && value instanceof javax.swing.plaf.FontUIResource) {
                UIManager.put(key, f);
            }
        }
    }

    public static void setUIFont(Font f) {
        try {
            UIManager.getLookAndFeelDefaults().put("defaultFont", f);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Font getFont(FONT f, float size, int style) {

        if (style == 0) {
            style = Font.PLAIN;
        }
        // if (f.font==null )
        // return PrismFontFactory.getSystemFont(null);
        return f.font.deriveFont(style, size);
    }

    public static Font getSizedFont(Font font, int size) {
        return font.deriveFont(font.getStyle(), size);
    }

    public static void init() {
        if (initialized) {
            return;
        }

        if (!CoreEngine.isArcaneVault()) {
            System.setProperty("awt.useSystemAAFontSettings", "on");
            System.setProperty("swing.aatext", "true");
        }
        String path = PathFinder.getFontPath();


        for (FONT F: FONT.values()){
            try {
                F.font=Font.createFont(Font.TRUETYPE_FONT, new File( path +F.path));
            } catch (FontFormatException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        initialized = true;
    }

    public static Font getAltFont(int size) {
        return getFont(getAltFontType(), size, Font.PLAIN);
    }

    public static FONT getAltFontType() {
        return FONT.MAIN;
    }

    public static Font getDefaultFont(int size) {

        return getFont(getDefaultFontType(), size, Font.PLAIN);
    }

    private static FONT getDefaultFontType() {
        return FONT.NYALA;
    }

    public static Font getDefaultFont() {
        return getFont(getDefaultFontType(), 12, Font.PLAIN);
    }

    public static int getDefaultStringWidth(String string) {
        return getStringWidth(getDefaultFontType(), Font.PLAIN, SIZE, string);
    }

    public static int getStringWidth(FONT f, int style, int size, String string) {
        return getFontMetrics(f, style, size).stringWidth(string);
    }

    public static FontMetrics getFontMetrics(FONT f, int style, int size) {
        return getFontMetrics(getFont(f, size, style));

    }

    private static FontMetrics getFontMetrics(Font font) {
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        return img.getGraphics().getFontMetrics(font);
    }

    public static int getStringWidth(Font font, String string) {
        try {
            return getFontMetrics(font).stringWidth(string);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static int getFontHeight(Font font) {
        return getFontMetrics(font).getHeight();
    }

    public static int getStringLengthForWidth(Font font, int width) {
        return Math.round(width / getFontMetrics(font).getMaxAdvance()
                * getFontWidthCoefficient(font.getFontName()));
    }

    private static float getFontWidthCoefficient(String fontName) {
        if (StringMaster.compare(fontName, "avq")) {
            return 5.0f;
        }
        if (StringMaster.compare(fontName, "nyala")) {

        }
        return 3.3f;
    }

    public static Integer getFontRenderedHeight(String string, Font font,
                                                Graphics2D targetGraphicsContext) {
        BufferedImage image;
        Graphics2D g;
        Color textColour = Color.white;

        // In the first instance; use a temporary BufferedImage object to render
        // the text and get the font metrics.
        image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        g = image.createGraphics();
        FontMetrics metrics = g.getFontMetrics(font);
        Rectangle2D rect = metrics.getStringBounds(string, g);

        // now set up the buffered Image with a canvas size slightly larger than
        // the font metrics - this guarantees that there is at least one row of
        // black pixels at the top and the bottom
        image = new BufferedImage((int) rect.getWidth() + 1, metrics.getHeight() + 2,
                BufferedImage.TYPE_INT_RGB);
        g = image.createGraphics();

        // take the rendering hints from the target graphics context to ensure
        // the results are accurate.
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, targetGraphicsContext
                .getRenderingHint(RenderingHints.KEY_ANTIALIASING));
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, targetGraphicsContext
                .getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING));

        g.setColor(textColour);
        g.setFont(font);
        g.drawString(string, 0, image.getHeight());

        // scan the bottom row - descenders will be cropped initially, so the
        // text will need to be moved up (down in the co-ordinates system) to
        // fit it in the canvas if it contains any. This may need to be done a
        // few times until there is a row of black pixels at the bottom.
        boolean foundBottom, foundTop = false;
        int offset = 0;
        do {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, image.getWidth(), image.getHeight());
            g.setColor(textColour);
            g.drawString(string, 0, image.getHeight() - offset);

            foundBottom = true;
            for (int x = 0; x < image.getWidth(); x++) {
                if (image.getRGB(x, image.getHeight() - 1) != Color.BLACK.getRGB()) {
                    foundBottom = false;
                }
            }
            offset++;
        } while (!foundBottom);

        System.out.println(image.getHeight());

        // Scan the top of the image downwards one line at a time until it
        // contains a non-black pixel. This loop uses the break statement to
        // stop the while loop as soon as a non-black pixel is found, this
        // avoids the need to scan the rest of the line
        int y = 0;
        do {
            for (int x = 0; x < image.getWidth(); x++) {
                if (image.getRGB(x, y) != Color.BLACK.getRGB()) {
                    foundTop = true;
                    break;
                }
            }
            y++;
        } while (!foundTop);

        return image.getHeight() - y;
    }

    public static void setUIFont() {
        if (!initialized) {
            init();
        }
        setUIFont(getDefaultFont(SIZE));

    }

    public static int getColumnsForText(String string) {
        return Math.round(getDefaultFont().getSize() * string.length() / 20);
    }

    public enum FONT {
        AVQ( "/Avqest.ttf"),
        SC( "/Starcraft.ttf"),
        DARK( "/Dark.ttf"),
        NYALA( "/nyala.ttf"),
        MAIN( "/main.otf");
        public Font font;
        public   String path;
        FONT(  String path) {
            this.path = path;
        }
    }

}
