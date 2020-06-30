package eidolons.game.battlecraft.logic.battlefield.vision.colormap;

import com.badlogic.gdx.graphics.Color;

public class LightHandler {
    public static Color applyNegative(Color c, float negative) {
       float add =  negative / 3;
       float mult = (0.85f + add);
        return new Color(c.r * mult+add, c.g * mult+add, c.b * mult+add, 1);
    }
    public static Color applyAdd(Color c, float screen) {
       float add =   screen / 3;
        float mult = (0.3f + add );
        return new Color(c.r * mult+add, c.g * mult+add, c.b *mult+add, 1);
    }

    public static Color applyLightnessToColor(Color c) {

        float screen = LightConsts.getScreen(c.a);
        if (screen > 0) {
            return LightHandler.applyAdd(c, screen);
        } else {

            float negative = LightConsts.getNegative(c.a);
            if (negative > 0) {
                return LightHandler.applyNegative(c, negative);
            }
        }
        return c;
    }
}
