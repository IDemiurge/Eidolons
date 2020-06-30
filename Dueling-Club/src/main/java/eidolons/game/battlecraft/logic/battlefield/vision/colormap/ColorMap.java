package eidolons.game.battlecraft.logic.battlefield.vision.colormap;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ObjectMap;
import eidolons.libgdx.GdxColorMaster;
import eidolons.libgdx.bf.Fluctuating;
import main.content.enums.GenericEnums;
import main.game.bf.Coordinates;
import main.system.datatypes.DequeImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ColorMap {

    Map<Coordinates, Color> base; //from ambient light only? more or less static then?
    Map<Coordinates, Color> output; //perhaps alpha will be our lightness?
    DequeImpl<Coordinates> updated = new DequeImpl<>();
    Set<Light> emitters;

    public ColorMap(Map<Coordinates, Color> base) {
        this.base = base;
        output = new HashMap<>(base);
    }

    public static class Light {
        public Color color;
        public Color baseColor;
        public GenericEnums.ALPHA_TEMPLATE template;

        public Fluctuating fluctuating;

        public ObjectMap<Coordinates, Float> lerp;
        public float lightness;

        public Light(Color baseColor, ObjectMap<Coordinates, Float> lerp, GenericEnums.ALPHA_TEMPLATE template) {
            this.baseColor = baseColor;
            this.lerp = lerp;
            this.template = template;
            fluctuating = new Fluctuating(template);
        }

        public void act(float delta) {
            fluctuating.fluctuate(delta);
            lightness = fluctuating.getColor().a;
        }
    }

    public void setEmitters(Set<Light> emitters) {
        this.emitters = emitters;
        update();
    }

    public void update() {
        //now this is interesting
        updated.clear();
    }

    public void act(float delta) {
        /*
        fluctuate each emitters, then pass it to their area
         */
        // for (Coordinates c : base.keySet()) {
        //     output.get(c).a= base.get(c).a;
        // }
        if (emitters != null)
            for (Light key : emitters) {
                fluctuate(key, delta);
            }
    }

    public Map<Coordinates, Color> getOutput() {
        return output;
    }

    private void fluctuate(Light key, float delta) {
        key.act(delta);
        if (key.color == null) {
            key.color = new Color();
        }
        GdxColorMaster.modify(key.color, key.baseColor, key.lightness - 0.15f);

        for (Coordinates c : key.lerp.keys()) {
            Color orig = output.get(c);
            if (orig == null) {
                continue;
            }
            if (!updated(c))
                orig.set(base.get(c));

            float lerp = key.lerp.get(c);
            if (!GdxColorMaster.WHITE.equals(key.baseColor) && GdxColorMaster.isHued(key.color)) {
                GdxColorMaster.modify(orig, key.color, 0.2f + lerp * lerp + lerp);
                orig.lerp(key.color, lerp * lerp + 0.2f).clamp();
                orig.a  = lerp * key.lightness*1.4f;
            } else {
                orig.a += lerp  /3 ;
            }
            if (orig.a>1) {
                orig.a=1;
            }

        }
    }

    private boolean updated(Coordinates c) {
        if (updated.contains(c)) {
            return true;
        }
        updated.add(c);
        return false;
    }


}


















