package eidolons.system.graphics;

import main.content.CONTENT_CONSTS.FLIP;
import main.system.auxiliary.log.LogMaster;
import main.system.images.ImageManager;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.*;
import java.util.HashMap;
import java.util.Map;

public class ImageTransformer {

    private static final int DEC = 255;
    static Map<Image, Map<Image, Map<Float, BufferedImage>>> blendedCache;

    public static BufferedImage getGrayScale(BufferedImage img) {
        ColorConvertOp op = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
        op.filter(img, img);
        return (img);
    }

    public static Image blendImages(Image image1, Image image2, float alpha, int finalTransparency) {
        if (alpha > 1) {
            alpha = 1;
        }
        if (alpha < 0) {
            alpha = 0;
        }
        if (blendedCache == null) {
            blendedCache = new HashMap<>();
        }
        Map<Image, Map<Float, BufferedImage>> map = blendedCache.get(image1);
        if (map == null) {
            map = new HashMap<>();
            blendedCache.put(image1, map);
        }
        Map<Float, BufferedImage> map2 = map.get(image2);
        if (map2 == null) {
            map2 = new HashMap<>();
            map.put(image2, map2);
        }

        BufferedImage image = map2.get(alpha);
        if (image == null) {
            image = new BufferedImage(image1.getWidth(null), image1.getHeight(null),
             BufferedImage.TYPE_INT_RGB);
            map2.put(alpha, image);
        }
        Graphics2D finalGraphics = (Graphics2D) image.getGraphics();
        BufferedImage buffer = new BufferedImage(image1.getWidth(null), image1.getHeight(null),
         BufferedImage.TYPE_INT_RGB);
        Graphics2D g = (Graphics2D) buffer.getGraphics();
        if (finalTransparency == 100) {
            g = finalGraphics;
        }
        int rule = AlphaComposite.SRC_OVER;
        // if (RandomWizard.chance(33))
        // rule = AlphaComposite.DST_OVER;
        // need another level of mapping, but it doesn't work anyway
        g.setComposite(AlphaComposite.getInstance(rule, alpha));
        g.drawImage(image1, 0, 0, null);
        g.setComposite(AlphaComposite.getInstance(rule, 1.0f - alpha));
        g.drawImage(image2, 0, 0, null);
        if (finalTransparency == 100) {
            return image;
        }
        float floatTransparent = new Float(finalTransparency) / 100;
        LogMaster.log(1, "**********finalTransparency==" + floatTransparent);
        finalGraphics.setComposite(AlphaComposite.getInstance(rule, floatTransparent));
        finalGraphics.drawImage(buffer, 0, 0, null);
        return image;
    }

    public static BufferedImage getTransparentImage(Image source, int alpha) {
        return getTransparentImage(ImageManager.getBufferedImage(source), new Double(alpha) / 100);
    }

    public static BufferedImage getTransparentImage(BufferedImage source, double alpha) {
        BufferedImage target = new BufferedImage(source.getWidth(), source.getHeight(),
         java.awt.Transparency.TRANSLUCENT);
        Graphics2D g = target.createGraphics();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) alpha));
        g.drawImage(source, null, 0, 0);
        g.dispose();
        return target;
    }

    public static Image toRgb(Image image) {
        // Create an RGB buffered image
        BufferedImage bimage = new BufferedImage(image.getWidth(null), image.getHeight(null),
         BufferedImage.TYPE_INT_RGB);
        // Copy non-RGB image to the RGB buffered image
        Graphics2D g = bimage.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return image;
    }

    public static void gammaScale(BufferedImage bufferedImage, float factor) {
        RescaleOp op = new RescaleOp(factor, 0, null);
        bufferedImage = op.filter(bufferedImage, bufferedImage);
    }

    public static BufferedImage flip(FLIP flip, BufferedImage image) {
        switch (flip) {
            case BOTH:
                image = flipHorizontally(image);
                image = flipVertically(image);
                break;
            case CCW180:
                break;
            case CCW90:
                break;
            case CW180:
                break;
            case CW90:
                break;
            case HORIZONTAL:
                image = flipHorizontally(image);
                break;
            case VERTICAL:
                image = flipVertically(image);
                break;
            default:
                break;

        }
        return image;
    }

    public static BufferedImage getGammaScaled(BufferedImage bufferedImage, float factor) {
        RescaleOp op = new RescaleOp(factor, 0, null);
        bufferedImage = op.filter(bufferedImage, null);
        return bufferedImage;
    }

    public static boolean isTransparent(int rgb) {
        return rgb >> 24 == 0x00;
    }

    public static BufferedImage flipVertically(BufferedImage image) {
        AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
        tx.translate(0, -image.getHeight(null));
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        image = op.filter(image, null);
        return image;

    }

    public static BufferedImage flipHorizontally(BufferedImage image) {
        AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
        tx.translate(-image.getWidth(null), 0);
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        image = op.filter(image, null);
        return image;

    }

    // public static Image getTransparent(Image image, final int trasparency) {
    // int a = (DEC - (DEC * trasparency / 100));
    // final int alpha = (int) (a << 32 + 0xFFFFFF);
    // main.system.auxiliary.LogMaster.log(1, image + " getTransparent, alpha= "
    // + a);
    // ImageFilter filter = new RGBImageFilter() {
    // public final int filterRGB(int x, int y, int rgb) {
    // return alpha & rgb;
    // // more on the edges
    // // return 0x88FFFFFF & rgb;
    // }
    // };
    //
    // ImageProducer ip = new FilteredImageSource(image.getSource(), filter);
    // return Toolkit.getDefaultToolkit().createImage(ip);
    // }

    public static Image getCircleCroppedImage(final Image img) {
        return getCircleImage(img, img.getWidth(null) / 4 + img.getHeight(null) / 4);
    }

    public static Image getCircleImage(final Image img, final int CIRCLE_ICON_OFFSET) {
        // ImageFilter filter = new RGBImageFilter() {
        // public final int filterRGB(int x, int y, int rgb) {
        // if (withinCircle(x, y, img, CIRCLE_ICON_OFFSET))
        // return 0xFFFFFFFF & rgb;
        // else if (withinGap(x, y, img, CIRCLE_ICON_OFFSET))
        // // return 0xAAFFFFFF & rgb;
        // return 0xFFFFFFFF & rgb;
        // return 0x00FFFFFF & rgb;
        // }
        // };
        // ImageManager.getBufferedImage(img).getGraphics().setClip(clip)
        // g.setClip(new Ellipse2D.Float(x, y, w, h));
        // g.drawImage(yourBufferedImage, x, y, w, h, null);
        // ImageProducer ip = new FilteredImageSource(img.getSource(), filter);
        // return Toolkit.getDefaultToolkit().createImage(ip);

        return makeRoundedCorner(ImageManager.getBufferedImage(img), CIRCLE_ICON_OFFSET);
    }

    public static BufferedImage makeRoundedCorner(BufferedImage image, int cornerRadius) {
        int w = image.getWidth();
        int h = image.getHeight();
        BufferedImage output = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2 = output.createGraphics();

        // This is what we want, but it only does hard-clipping, i.e. aliasing
        // g2.setClip(new RoundRectangle2D ...)

        // so instead fake soft-clipping by first drawing the desired clip shape
        // in fully opaque white with antialiasing enabled...
        g2.setComposite(AlphaComposite.Src);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fill(new RoundRectangle2D.Float(0, 0, w, h, cornerRadius, cornerRadius));

        // ... then compositing the image on top,
        // using the white shape from above as alpha source
        g2.setComposite(AlphaComposite.SrcIn);
        g2.drawImage(image, 0, 0, null);

        g2.dispose();

        return output;
    }

    protected static boolean withinGap(int x, int y, Image img, int gapSize) {
        double radius = img.getWidth(null) / 2;
        x = (x - img.getWidth(null) / 2);
        y = (y - img.getHeight(null) / 2);

        long result = Math.round((x * x) + (y * y)) - Math.round(radius * radius);
        // main.system.auxiliary.LogMaster.log(1,"x=" + X + "" + Y +
        return Math.abs(result) < gapSize;
    }

    protected static boolean withinCircle(int x, int y, Image img, int offset) {

        double radius = img.getWidth(null) / 2 - offset;
        x = (x - img.getWidth(null) / 2);
        y = (y - img.getHeight(null) / 2);

        boolean result = Math.round((x * x) + (y * y)) <= Math.round(radius * radius);
        return result;

    }

    public static Image rotate(Image image, int degrees) {
        BufferedImage result = ImageManager.getBufferedImage(image);
        Graphics g = result.getGraphics();
        AffineTransform identity = new AffineTransform();
        Graphics2D g2d = (Graphics2D) g;
        AffineTransform trans = new AffineTransform();
        trans.setTransform(identity);
        trans.rotate(Math.toRadians(degrees));

        g2d.drawImage(image, trans, null);
        return result;
    }

    /**
     * Clips the input image to the specified shape
     *
     * @param image     the input image
     * @param clipVerts list of x, y pairs defining the clip shape, normalised to
     *                  image dimensions (think texture coordinates)
     * @return The smallest image containing those pixels that fall inside the
     * clip shape
     */
    public static BufferedImage clip(BufferedImage image, float... clipVerts) {
        assert clipVerts.length >= 6;
        assert clipVerts.length % 2 == 0;

        int[] xp = new int[clipVerts.length / 2];
        int[] yp = new int[xp.length];

        int minX = image.getWidth(), minY = image.getHeight(), maxX = 0, maxY = 0;

        for (int j = 0; j < xp.length; j++) {
            xp[j] = Math.round(clipVerts[2 * j] * image.getWidth());
            yp[j] = Math.round(clipVerts[2 * j + 1] * image.getHeight());

            minX = Math.min(minX, xp[j]);
            minY = Math.min(minY, yp[j]);
            maxX = Math.max(maxX, xp[j]);
            maxY = Math.max(maxY, yp[j]);
        }

        for (int i = 0; i < xp.length; i++) {
            xp[i] -= minX;
            yp[i] -= minY;
        }

        Polygon clip = new Polygon(xp, yp, xp.length);
        BufferedImage out = new BufferedImage(maxX - minX, maxY - minY, image.getType());
        Graphics g = out.getGraphics();
        g.setClip(clip);

        g.drawImage(image, -minX, -minY, null);
        g.dispose();

        return out;
    }

    // This code can be simplified if you have a non-animated image, by creating
    // the BufferedImage only once and keeping it for each paint.
    // ImageObs ImageObserver obs = ...;

    public static BufferedImage getCircleImage(Image img, ImageObserver obs, Dimension size) {
        int w = (int) size.getWidth();
        int h = (int) size.getHeight();
        // any shape can be used
        Shape clipShape = new RoundRectangle2D.Double(0, 0, w, h, w / 2, h / 2);

        // create a BufferedImage with transparency
        BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D bg = bi.createGraphics();
        // make BufferedImage fully transparent
        bg.setComposite(AlphaComposite.Clear);
        bg.fillRect(0, 0, w, h);
        bg.setComposite(AlphaComposite.SrcOver);

        // copy/paint the actual image into the BufferedImage
        bg.drawImage(img, 0, 0, w, h, obs);

        // set the image to be used as TexturePaint on the target Graphics
        bg.setPaint(new TexturePaint(bi, new Rectangle2D.Float(0, 0, w, h)));

        // activate AntiAliasing
        bg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // translate the origin to where you want to paint the image
        bg.translate(0, 0);

        // draw the Image
        bg.fill(clipShape);

        // reset paint
        // bg.setPaint(null);

        bg.setClip(clipShape);

        bg.drawImage(img, 0, 0, null);
        bg.dispose();
        return bi;
    }

}
