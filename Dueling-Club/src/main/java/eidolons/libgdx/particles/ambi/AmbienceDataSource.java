package eidolons.libgdx.particles.ambi;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ObjectMap;
import main.content.enums.DungeonEnums.DUNGEON_STYLE;
import main.content.enums.GenericEnums;
import main.content.enums.macro.MACRO_CONTENT_CONSTS.DAY_TIME;
import main.system.auxiliary.StringMaster;
import main.system.datatypes.WeightMap;

import java.util.ArrayList;
import java.util.List;

import static eidolons.libgdx.particles.ambi.AmbienceDataSource.VFX_TEMPLATE.*;
import static main.content.enums.GenericEnums.VFX.*;

/**
 * Created by JustMe on 4/24/2018.
 */
public class AmbienceDataSource {


    private final ObjectMap<String, Integer> map = new ObjectMap<>(12);
    Color colorHue;
    boolean shadow;
    private List<String> emitters;


    public AmbienceDataSource(VFX_TEMPLATE template, DAY_TIME time) {
        emitters = new ArrayList<>();
        float daynessCoef = getDayness(time);
        if (daynessCoef != 0)
            addVfx(daynessCoef, false, template);
        if (daynessCoef != 1)
            addVfx(daynessCoef, true, template);

    }

    public int getShowChance(String vfx) {
        if (map == null) {
            return 0;
        }
        return map.get(vfx);
    }

    private void addVfx(float daynessCoef, boolean night, VFX_TEMPLATE template) {
        float priority = 1f;
        GenericEnums.VFX[] vfx = night ? template.nightly : template.daily;
        for (GenericEnums.VFX preset : vfx) {
            //invert chance for nightly emitters
            float chance = (1 - daynessCoef) * 6 / vfx.length; //default average
            if (shadow) {
                chance *= 2;
            }
            int baseChance = (int) (chance * 100 * priority);
            emitters.add(string(preset, baseChance));
            map.put(preset.getPath(), Math.round(chance * 100 * priority));
            //each next vfx is less likely
            priority -= 0.05f;
            priority = priority * 4 / 5;
        }
    }


    public static VFX_TEMPLATE getTemplate(DUNGEON_STYLE style) {
        return getTemplateMap(style).getRandomByWeight();
    }

    public static WeightMap<VFX_TEMPLATE> getTemplateMap(DUNGEON_STYLE style) {
        WeightMap<VFX_TEMPLATE> map = new WeightMap<>(VFX_TEMPLATE.class);
        switch (style) {
            case Knightly:
            case Holy:
                map.chain(HALL, 20);
                break;
            case Grimy:
                map.chain(POISON, 30);
            case Pagan:
                map.chain(COLD, 10);
            case Stony:
                map.chain(CAVE, 15);
                break;
            case Brimstone:
                map.chain(HELL, 20).chain(CAVE, 5);
                break;
            case PureEvil:
                map.chain(POISON, 10).chain(COLD, 10);
            case Somber:
                map.chain(DEEP_MIST, 10);
            case DarkElegance:
                map.chain(CRYPT, 25);
                break;
            case Arcane:
                map.chain(DEEP_MIST, 20);
                break;
            case Cold:
                map.chain(COLD, 20);
                break;
            case DWARF:

                map.chain(COLD, 20)
                        .chain(DEEP_MIST, 20)
                        .chain(HALL, 20)
                        .chain(POISON, 20)
                        .chain(HELL, 20)
                        .chain(CRYPT, 20)
                        .chain(CAVE, 20)
                ;
                break;
        }
        return map;
    }

    private float getDayness(DAY_TIME time) {
        switch (time) {
            case MIDNIGHT:
                return 0;
            case DAWN:
            case NIGHTFALL:
                return 0.3f;
            case MORNING:
            case DUSK:
                return 0.6f;
            case MIDDAY:
                return 1;
        }
        return 0;
    }

    public Color getColorHue() {
        return colorHue;
    }

    private String string(GenericEnums.VFX emitterPreset, int i) {
        return emitterPreset.getPath()
                + StringMaster.wrapInParenthesis("" + i / 3);
    }

    public List<String> getEmitters() {
        return emitters;
    }

    public void setEmitters(List<String> emitters) {
        this.emitters = emitters;
    }

    public ObjectMap<String, Integer> getMap() {
        return map;
    }

    public enum VFX_TEMPLATE {

        CAVE,
        COLD,
        POISON,
        DUNGEON,
        CRYPT,
        HELL,
        HALL,
        FOREST,
        DEEP_MIST,
        ;

        static {
            COLD.setDaily(
                    SNOW,
                    MIST_ARCANE,
                    MIST_WIND,
                    SNOWFALL,
                    //             MIST_CYAN,
                    //             DARK_MIST_LITE,
                    MIST_CYAN,
                    STARS,
                    SNOWFALL_THICK
            );
            COLD.setNightly(
                    //             MIST_CYAN,
                    MIST_WIND,
                    STARS,
                    MIST_ARCANE,
                    SNOW,
                    SNOWFALL,
                    MIST_WHITE2,
                    MIST_ARCANE,
                    //             DARK_MIST_LITE,
                    DARK_MIST_LITE
            );

            POISON.setDaily(
                    POISON_MIST,
                    POISON_MIST2,
                    ASH,
                    MIST_BLACK
            );
            POISON.setNightly(
                    POISON_MIST,
                    POISON_MIST2,
                    MIST_BLACK

            );


            CRYPT.setDaily(
                    MIST_WIND,
                    MIST_WHITE2,
                    MIST_ARCANE,
                    MIST_TRUE,
                    MIST_TRUE2
            );
            CRYPT.setNightly(
                    MIST_ARCANE,
                    MIST_WIND,
                    MIST_ARCANE,
                    DARK_MIST_LITE,
                    MIST_CYAN,
                    MIST_WIND
            );

            HELL.setDaily(
                    MIST_WIND,
                    POISON_MIST2,
                    ASH,
                    MIST_SAND_WIND,
                    CINDERS,
                    ASH
            );
            HELL.setNightly(
                    POISON_MIST,
                    POISON_MIST2,
                    ASH,
                    CINDERS,
                    ASH,
                    MIST_SAND_WIND
            );

            FOREST.setDaily(
                    FALLING_LEAVES
            );
            FOREST.setNightly(
                    FALLING_LEAVES_WINDY,
                    WISPS,
                    STARS
            );

            DEEP_MIST.setDaily(
                    MIST_WIND,
                    MIST_WHITE3,
                    MIST_BLACK,
                    DARK_MIST,
                    MIST_TRUE2
            );

            DEEP_MIST.setNightly(
                    MIST_WIND,
                    MIST_WHITE3,
                    MIST_ARCANE,
                    MIST_TRUE,
                    DARK_MIST,
                    MIST_TRUE2
            );
            DUNGEON.setDaily(
                    MIST_BLACK,
                    MIST_CYAN,
                    MIST_ARCANE
            );
            DUNGEON.setNightly(
                    MIST_BLACK,
                    WISPS,
                    STARS,
                    MOTHS_TIGHT2
            );
            HALL.setDaily(
                    MIST_WIND
            );
            HALL.setNightly(
                    MIST_WIND,
                    MIST_ARCANE,
                    MOTHS,
                    MOTHS_TIGHT2
            );

            CAVE.setDaily(
                    MIST_BLACK,
                    MIST_WIND,
                    MIST_TRUE2,
                    MIST_WHITE
            );
            CAVE.setNightly(
                    MIST_WIND,
                    MIST_WHITE,
                    MIST_ARCANE,
                    DARK_MIST_LITE,
                    MIST_TRUE2,
                    WISPS,
                    STARS

            );
        }

        public GenericEnums.VFX[] daily;
        public GenericEnums.VFX[] nightly;

        public void setDaily(GenericEnums.VFX... daily) {
            this.daily = daily;
        }
/*
        what will change with day-time?
        > chance
        > hue
        > some emitters will be exclusive or dependent

        0-100 for midnight - noon
        true/false for more/less

         */

        public void setNightly(GenericEnums.VFX... nightly) {
            this.nightly = nightly;
        }
    }
}
