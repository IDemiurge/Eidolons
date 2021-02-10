package eidolons.content.data.datasource;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ObjectMap;
import eidolons.content.consts.VisualEnums;
import main.content.enums.DungeonEnums.DUNGEON_STYLE;
import main.content.enums.GenericEnums;
import main.content.enums.macro.MACRO_CONTENT_CONSTS.DAY_TIME;
import main.system.auxiliary.StringMaster;
import main.system.datatypes.WeightMap;

import java.util.ArrayList;
import java.util.List;

import static eidolons.content.consts.VisualEnums.VFX_TEMPLATE.*;

/**
 * Created by JustMe on 4/24/2018.
 */
public class AmbienceDataSource {


    private final ObjectMap<String, Integer> map = new ObjectMap<>(12);
    Color colorHue;
    boolean shadow;
    private List<String> emitters;


    public AmbienceDataSource(VisualEnums.VFX_TEMPLATE template, DAY_TIME time) {
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

    private void addVfx(float daynessCoef, boolean night, VisualEnums.VFX_TEMPLATE template) {
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


    public static VisualEnums.VFX_TEMPLATE getTemplate(DUNGEON_STYLE style) {
        return getTemplateMap(style).getRandomByWeight();
    }

    public static WeightMap<VisualEnums.VFX_TEMPLATE> getTemplateMap(DUNGEON_STYLE style) {
        WeightMap<VisualEnums.VFX_TEMPLATE> map = new WeightMap<>(VisualEnums.VFX_TEMPLATE.class);
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

}
