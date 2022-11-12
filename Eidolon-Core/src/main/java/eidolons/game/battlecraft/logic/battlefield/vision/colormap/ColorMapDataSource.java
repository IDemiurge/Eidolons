package eidolons.game.battlecraft.logic.battlefield.vision.colormap;

import com.badlogic.gdx.graphics.Color;
import main.content.enums.GenericEnums;
import main.game.bf.Coordinates;
import java.util.*;

public class ColorMapDataSource {

    protected Map<Coordinates, Color> base;
    protected Map<Coordinates, Color> original;
    protected Set<LightDS> emitters;

    public ColorMapDataSource(Map<Coordinates, Color> original) {
        this.original = original;
        base = new HashMap<>();
    }

    public static class LightDS {
        public Color baseColor;
        public GenericEnums.ALPHA_TEMPLATE template;

        public Map<Coordinates, Float> lerp;
        public LightDS(Color baseColor, Map<Coordinates, Float> lerp, GenericEnums.ALPHA_TEMPLATE template) {
            this.baseColor = baseColor;
            this.lerp = lerp;
            this.template = template;
        }
        @Override
        public String toString() {
            return "Light: " +
                    "map=" + lerp ;
        }
    }

    public Map<Coordinates, Color> getOriginal() {
        return original;
    }

    public Map<Coordinates, Color> getBase() {
        return base;
    }

    public void setEmitters(Set<LightDS> emitters) {
        this.emitters = emitters;
        for (LightDS emitter : emitters) {
            for (Coordinates coordinates : emitter.lerp.keySet()) {
                base.put(coordinates, emitter.baseColor);
            }
        }
        // update();
    }



}


















