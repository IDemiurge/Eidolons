package main.content;

import java.awt.*;

/**
 * EDITOR (which also has details (arg validity/type, default, tooltip, range,
 * numeric scale, color, font etc)
 * <p>
 * height of row default value boundaries
 * <p>
 * <p>
 * RENDERER ENUM GROUP
 *
 * @author JustMe
 */
public class Metainfo {

    private Color c;

    public Metainfo(Color c) {
        this.c = c;
    }

    public Color getColor() {
        return c;
    }
}
