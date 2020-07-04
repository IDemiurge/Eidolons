package eidolons.libgdx.bf.grid.cell;

import com.badlogic.gdx.graphics.Color;
import eidolons.game.battlecraft.logic.battlefield.vision.colormap.LightConsts;
import eidolons.game.battlecraft.logic.battlefield.vision.colormap.LightHandler;
import eidolons.libgdx.GdxColorMaster;

public interface Colored {

    default void applyColor(Color c) {
            float min =getMinLightness();
            //TODO for units too?
            if (c == null) {
                c = GdxColorMaster.NULL_COLOR;
            }
            float light = Math.max(min, c.a);
            float screen = LightConsts.getScreen(light);
            float negative = LightConsts.getNegative(light);
            if (negative > 0) {
                c = LightHandler.applyNegative(c, negative);
            }
            applyColor(screen, c);
    }

    void applyColor(float screen, Color c);

    float getMinLightness();
}
