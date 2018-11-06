package eidolons.libgdx.particles;

import com.badlogic.gdx.graphics.Color;
import eidolons.game.module.dungeoncrawl.dungeon.LevelBlock;
import main.content.enums.DungeonEnums.DUNGEON_STYLE;
import main.content.enums.macro.MACRO_CONTENT_CONSTS.DAY_TIME;
import main.system.auxiliary.StringMaster;
import main.system.datatypes.WeightMap;

import java.util.ArrayList;
import java.util.List;

import static eidolons.libgdx.particles.AmbienceDataSource.AMBIENCE_TEMPLATE.*;
import static eidolons.libgdx.particles.EMITTER_PRESET.*;

/**
 * Created by JustMe on 4/24/2018.
 */
public class AmbienceDataSource {


    Color colorHue;
    private AMBIENCE_TEMPLATE template;
    private List<String> emitters;
    private int showChance = 100;

    public AmbienceDataSource(AMBIENCE_TEMPLATE template, DAY_TIME time) {
        emitters = new ArrayList<>();
        this.template = template;

        float daynessCoef = getDayness(time);
        float priority = 1f;
        if (daynessCoef != 0)
            for (EMITTER_PRESET preset : template.daily) {
                float chance = daynessCoef;
                emitters.add(string(preset, (int) (chance * 100 * priority)));
                priority -= 0.2f;
            }
        priority = 1f;
        if (daynessCoef != 1)
            for (EMITTER_PRESET preset : template.nightly) {
                float chance = 1 - daynessCoef;
                emitters.add(string(preset, (int) (chance * 100 * priority)));
                priority -= 0.07f;
                priority = priority * 2 / 3;
            }

        if (emitters.isEmpty()) {
            switch (time) {
                case DAWN:
                    emitters.add(MIST_WHITE2.getPath());
                case MORNING:
                    emitters.add(SMOKE_TEST.getPath());
                    emitters.add(MIST_WHITE.getPath());
                    emitters.add(WISPS.getPath());
                    break;
                case MIDDAY:
                    emitters.add(MIST_WHITE3.getPath());
                case DUSK:
                    emitters.add(MIST_WHITE.getPath());
                    emitters.add(MIST_BLACK.getPath());
                    emitters.add(SNOW.getPath());

                    break;

                case NIGHTFALL:
                    emitters.add(MIST_CYAN.getPath());
                case MIDNIGHT:
                    emitters.add(WISPS.getPath());
                    emitters.add(STARS.getPath());
                    emitters.add(SMOKE_TEST.getPath());

                    emitters.add(DARK_MIST_LITE.getPath());
                    break;
            }
            showChance = 80 - 10 * emitters.size();


            switch (time) {
                case MORNING:
                    emitters.add(string(MIST_WHITE3, showChance / 3));
                    break;
                case DUSK:
                    emitters.add(string(MIST_SAND_WIND, showChance / 3));
                    break;
                case MIDNIGHT:
                    emitters.add(string(MIST_WIND, showChance / 3));

            }
        }
    }

    public AmbienceDataSource(LevelBlock block, DAY_TIME dayTime) {
        this(getTemplate(block.getStyle()),
         dayTime);

    }

    public static AMBIENCE_TEMPLATE getTemplate(DUNGEON_STYLE style) {
        return getTemplateMap(style).getRandomByWeight();
    }

    public static WeightMap<AMBIENCE_TEMPLATE> getTemplateMap(DUNGEON_STYLE style) {
        WeightMap<AMBIENCE_TEMPLATE> map = new WeightMap<>(AMBIENCE_TEMPLATE.class);
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
        }
        return map;
    }

    private float getDayness(DAY_TIME time) {
        switch (time) {
            case MIDNIGHT:
                return 0;
            case DAWN:
                return 0.3f;
            case MORNING:
                return 0.6f;
            case MIDDAY:
                return 1;
            case DUSK:
                return 0.6f;
            case NIGHTFALL:
                return 0.3f;
        }
        return 0;
    }

    public Color getColorHue() {
        return colorHue;
    }

    private String string(EMITTER_PRESET emitterPreset, int i) {
        return emitterPreset.getPath()
         + StringMaster.wrapInParenthesis("" + i / 3);
    }

    public List<String> getEmitters() {
        return emitters;
    }

    public void setEmitters(List<String> emitters) {
        this.emitters = emitters;
    }

    public int getShowChance() {
        return showChance;
    }

    public void setShowChance(int showChance) {
        this.showChance = showChance;
    }

    public enum AMBIENCE_TEMPLATE {

        CAVE,
        COLD,
        POISON,
        DUNGEON,
        CRYPT,
        HELL,
        HALL,
        FOREST,
        DEEP_MIST,;

        static {
            COLD.setDaily(
             SNOW,
             MIST_ARCANE,
             MIST_WIND,
             DARK_MIST_LITE,
             SNOWFALL,
             MIST_CYAN,
             MIST_CYAN,
             SNOWFALL_THICK
            );
            COLD.setNightly(
             MIST_CYAN,
             DARK_MIST_LITE,
             SNOW,
             SNOWFALL,
             DARK_MIST,
             STARS
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
             MIST_TRUE2,
             MIST_WHITE,
             DARK_MIST_LITE,
             WISPS,
             STARS

            );
        }

        EMITTER_PRESET[] daily;
        EMITTER_PRESET[] nightly;

        public void setDaily(EMITTER_PRESET... daily) {
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

        public void setNightly(EMITTER_PRESET... nightly) {
            this.nightly = nightly;
        }
    }
}
