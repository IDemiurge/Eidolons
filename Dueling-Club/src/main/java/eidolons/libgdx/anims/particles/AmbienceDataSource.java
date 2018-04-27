package eidolons.libgdx.anims.particles;

import eidolons.game.battlecraft.logic.dungeon.universal.Dungeon;
import main.content.CONTENT_CONSTS2.EMITTER_PRESET;
import main.content.enums.macro.MACRO_CONTENT_CONSTS.DAY_TIME;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 4/24/2018.
 */
public class AmbienceDataSource {

    static boolean underground;
    static boolean dark;
    static boolean snow;
    private static Dungeon dungeon_;
    private List<String> emitters;
    private int showChance;

    public AmbienceDataSource(DAY_TIME time) {
        emitters = new ArrayList<>();
        switch (time) {
            case DAWN:
                emitters.add(EMITTER_PRESET.MIST_WHITE2.path);
            case MORNING:
                emitters.add(EMITTER_PRESET.SMOKE_TEST.path);
                emitters.add(EMITTER_PRESET.MIST_WHITE.path);

                emitters.add(EMITTER_PRESET.WISPS.path);

                break;
            case MIDDAY:
                emitters.add(EMITTER_PRESET.MIST_WHITE3.path);
            case DUSK:
                emitters.add(EMITTER_PRESET.MIST_WHITE.path);
                emitters.add(EMITTER_PRESET.MIST_BLACK.path);
                emitters.add(EMITTER_PRESET.SNOW.path);

                break;

            case NIGHTFALL:
                emitters.add(EMITTER_PRESET.MIST_COLD.path);
            case MIDNIGHT:
                emitters.add(EMITTER_PRESET.WISPS.path);
                emitters.add(EMITTER_PRESET.STARS.path);
                emitters.add(EMITTER_PRESET.SMOKE_TEST.path);

                emitters.add(EMITTER_PRESET.DARK_MIST_LITE.path);
                break;
        }
        switch (time) {
            case MORNING:
                emitters.add(EMITTER_PRESET.MIST_WHITE3.path);
            case DUSK:
                emitters.add(EMITTER_PRESET.MIST_SAND_WIND.path);
            case MIDNIGHT:
                emitters.add(EMITTER_PRESET.MIST_WIND.path);

        }
        showChance = 80 - 10 * emitters.size();


    }

    public static void init(Dungeon dungeon) {
        dungeon_ = dungeon;
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
}
