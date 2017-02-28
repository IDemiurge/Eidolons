package main.system.graphics;

import static org.apache.commons.lang3.math.NumberUtils.max;
import static org.apache.commons.lang3.math.NumberUtils.min;

public class GreyscaleUtils {

    public static int lightness(int rgba) {
        int[] srgba = separate(rgba);
        return toGrayscaleRGBA((max(srgba[0], srgba[1], srgba[2]) + min(srgba[0], srgba[1], srgba[2])) / 2);
    }

    public static int average(int rgba) {
        int[] srgba = separate(rgba);
        return toGrayscaleRGBA((srgba[0] + srgba[1] + srgba[2]) / 3);
    }

    public static int luminosity(int rgba) {
        int[] srgba = separate(rgba);
        return toGrayscaleRGBA((int) (srgba[0] * .21 + srgba[1] * .72 + srgba[2] * .07));
    }

    private static int[] separate(int rgba) {
        int[] result = new int[4];
        result[0] = rgba >> 24;
        result[1] = (rgba >> 16) & 0xFF;
        result[2] = (rgba >> 8) & 0xFF;
        result[3] = rgba & 0xFF;

        return result;
    }

    private static int toGrayscaleRGBA(int gray) {
        return gray << 24 | gray << 16 | gray << 8 | 255;
    }
}
