package libgdx.bf.grid.handlers;

import com.badlogic.gdx.graphics.Color;
import eidolons.content.consts.libgdx.GdxColorMaster;
import eidolons.game.battlecraft.logic.battlefield.vision.colormap.ColorMapDataSource;
import libgdx.bf.Fluctuating;
import libgdx.screens.handlers.ScreenMaster;
import main.game.bf.Coordinates;
import main.system.datatypes.DequeImpl;

import java.util.*;

public class ColorMap {

    ColorMapDataSource dataSource;
    Map<Coordinates, Color> original; //from ambient light only? more or less static then?
    Map<Coordinates, Color> output; //perhaps alpha will be our lightness?
    Set<Light> emitters;
    DequeImpl<Coordinates> updated = new DequeImpl<>();

    public ColorMap(ColorMapDataSource dataSource) {
        this.dataSource = dataSource;
        this.original = dataSource.getOriginal(); //copy?
        output = new HashMap<>();

    }

    public void reset(Set<ColorMapDataSource.LightDS> set) {
        //TODO Gdx Review 2021
        Set<Light> updated = new LinkedHashSet<>();
       loop: for (ColorMapDataSource.LightDS lightDS : set) {
            for (Light emitter : emitters) {
                if (emitter.dataSource==lightDS) {
                    updated.add(emitter);
                    continue loop;
                }
            }
            updated.add(new Light(lightDS));
        }
        //remove non-active lights?
        emitters = updated;
    }

    public static class Light extends Fluctuating {
        public float lightness;
        ColorMapDataSource.LightDS dataSource;
        public Color color;

        public Light(ColorMapDataSource.LightDS dataSource) {
            super(dataSource.template);
            this.dataSource = dataSource;
        }

        public void act(float delta) {
            fluctuate(delta);
            lightness = getColor().a;
        }
    }

    public void act(float delta) {
        if (ColorHandler.isStaticColors()){
            return;
        }
        update();
        List<Light> broken = new ArrayList<>();
        if (emitters != null){
            for (Light key : emitters) {
                try {
                    fluctuate(key, delta);
                } catch (Exception e) {
                    main.system.ExceptionMaster.printStackTrace(e);
                    main.system.auxiliary.log.LogMaster.log(1,"LIGHT IS BROKEN: " +key);
                    broken.add(key);
                }
            }
            emitters.removeAll(broken);
        }
    }

    private void fluctuate(Light light, float delta) {
        ColorMapDataSource.LightDS ds = light.dataSource;
        light.act(delta);
        if (light.color == null) {
            light.color = new Color();
        }
        GdxColorMaster.modify(light.color, ds.baseColor, Math.min(1, light.lightness * 2));

        for (Coordinates c : ds.lerp.keySet()) {
            if (!ScreenMaster.getGrid().isDrawn(c)) {
                continue;
            }
            Color orig = output.get(c);
            Color color = original.get(c);
            if (color == null) {
                continue;
            }
            if (orig == null) {
                output.put(c, orig = new Color(color));
            } else if (!updated(c))
                orig.set(color);

            float lerp = ds.lerp.get(c);
            if (!GdxColorMaster.WHITE.equals(ds.baseColor)) {
                // GdxColorMaster.modify(orig, key.color, 0.2f + lerp * lerp+lerp );
                // Color color = GdxColorMaster.lighter(key.color, 0.33f);
                orig.lerp(light.color, lerp * lerp).clamp();
                orig.a += lerp * light.lightness * light.lightness * 1.35f;
            } else {
                // orig.a += lerp  /3 ;
                orig.a += lerp * light.lightness * light.lightness * 0.85f;
            }
            if (orig.a > 1) {
                orig.a = 1;
            }

        }
    }

    public void update() {
        //now this is interesting
        updated.clear();
    }

    public Map<Coordinates, Color> getOutput() {
        //TODO optimization Review
        return ColorHandler.isStaticColors()? dataSource.getBase() : output;
    }


    private boolean updated(Coordinates c) {
        if (updated.contains(c)) {
            return true;
        }
        updated.add(c);
        return false;
    }

    public Map<Coordinates, Color> getOriginal() {
        return original;
    }
}
