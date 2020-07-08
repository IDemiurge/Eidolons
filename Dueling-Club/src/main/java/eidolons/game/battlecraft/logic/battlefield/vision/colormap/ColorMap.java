package eidolons.game.battlecraft.logic.battlefield.vision.colormap;

import com.badlogic.gdx.graphics.Color;
import eidolons.libgdx.GdxColorMaster;
import eidolons.libgdx.bf.Fluctuating;
import eidolons.libgdx.screens.ScreenMaster;
import main.content.enums.GenericEnums;
import main.game.bf.Coordinates;
import main.system.datatypes.DequeImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ColorMap {

    Map<Coordinates, Color> original; //from ambient light only? more or less static then?
    Map<Coordinates, Color> base;
    Map<Coordinates, Color> output; //perhaps alpha will be our lightness?
    DequeImpl<Coordinates> updated = new DequeImpl<>();
    Set<Light> emitters;

    public ColorMap(Map<Coordinates, Color> original) {
        this.original = original;
        output = new HashMap<>();
        base = new HashMap<>( );
    }

    public Map<Coordinates, Color> getOriginal() {
        return original;
    }

    public static class Light {
        public Color color;
        public Color baseColor;
        public GenericEnums.ALPHA_TEMPLATE template;

        public Fluctuating fluctuating;

        public  Map<Coordinates, Float> lerp;
        public float lightness;

        public Light(Color baseColor,  Map<Coordinates, Float> lerp, GenericEnums.ALPHA_TEMPLATE template) {
            this.baseColor = baseColor;
            this.lerp = lerp;
            this.template = template;
            fluctuating = new Fluctuating(template);
        }

        public void act(float delta) {
            fluctuating.fluctuate(delta);
            lightness = fluctuating.getColor().a  ;
        }
    }

    public Map<Coordinates, Color> getBase() {
        return base;
    }

    public void setEmitters(Set<Light> emitters) {
        this.emitters = emitters;
        for (Light emitter : emitters) {
            for (Coordinates coordinates : emitter.lerp.keySet()) {
                base.put(coordinates , emitter.baseColor);
            }
        }
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
        update();

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
        GdxColorMaster.modify(key.color, key.baseColor, Math.min(1, key.lightness*2) );

        for (Coordinates c : key.lerp.keySet()) {
            if (!ScreenMaster.getGrid().isDrawn(c)) {
                continue;
            }
            Color orig = output.get(c);
            Color color = original.get(c);
            if (color == null) {
                continue;
            }
            if (orig == null) {
                output.put(c, orig =new Color(color));
            } else if (!updated(c))
                orig.set(color);

            float lerp = key.lerp.get(c);
            if (!GdxColorMaster.WHITE.equals(key.baseColor)) {
                // GdxColorMaster.modify(orig, key.color, 0.2f + lerp * lerp+lerp );
                // Color color = GdxColorMaster.lighter(key.color, 0.33f);
                orig.lerp( key.color, lerp * lerp  ).clamp();
                orig.a  += lerp *key.lightness * key.lightness*1.35f;
            } else {
                // orig.a += lerp  /3 ;
                orig.a  += lerp *key.lightness * key.lightness*0.85f;
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


















