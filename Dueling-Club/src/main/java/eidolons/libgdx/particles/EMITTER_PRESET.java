package eidolons.libgdx.particles;

import main.system.auxiliary.StrPathBuilder;

/**
 * Created by JustMe on 6/28/2018.
 */
public enum EMITTER_PRESET {
    IMPACT_demonology,
    IMPACT_scare,
    IMPACT_psychic,


    CAST_darkness,
    CAST_black_hand,
    CAST_black_hand2,
    CAST_black_hand3,
    CAST_blindness,

    CAST_dark_shapes,

    CAST_drain_focus,
    CAST_drain_focus2,
    CAST_drain_focus3,

    CAST_celestial1,
    CAST_celestial2,
    CAST_celestial3,

    DARK_MIST("mist","dark mist"),
    DARK_MIST_LITE("mist","dark mist2 light"),
    SMOKE_TEST("Smoke_Test1.pt"),
    DARK_SOULS("dark souls"),
    DARK_SOULS2("dark souls2"),
    DARK_SOULS3("dark souls3"),
    SKULL("skulls"),
    SKULL2("skulls2"),
    SKULL3("skulls3"),

    //TODO sub-emitters

    SNOW("snow","snow"),
    SNOW_TIGHT("snow","snow tight"),
    SNOW_TIGHT2("snow","snow tight2"),
    SNOWFALL_SMALL("snow","snowfall small"),
    SNOWFALL("snow","snowfall"),
    SNOWFALL_THICK("snow","snowfall thick"),
    WISPS("woods","wisps"),
    LEAVES("woods","leaves"),
    STARS("woods","stars"),
    FALLING_LEAVES("woods","falling leaves"),
    LEAVES_LARGE("woods","leaves large"),
    FALLING_LEAVES_WINDY("woods","falling leaves windy2"),

    MIST_WHITE("mist","conceal west wind"),
    MIST_WHITE2("mist","conceal west wind2"),
    MIST_WHITE3("mist","conceal west wind3"),
    MIST_WIND("mist","white mist wind"),
    MIST_COLD("mist","cold wind"),
    MIST_CYAN("mist","cyan mist2"),
    MIST_SAND_WIND("mist","sand wind"),

    MIST_BLACK("black mist","clouds wind light2"),
    MIST_TRUE("mist","MIST TRUE"),
    MIST_ARCANE("ambient", "MIST ARCANE"),
    MIST_NEW("ambient", "MIST NEW2"),
    THUNDER_CLOUDS("ambient", "THUNDER CLOUDS"),
    THUNDER_CLOUDS_CRACKS("ambient", "thunder clouds with cracks"),
    FLIES("ambient", "flies2"),
    MOTHS("ambient", "MOTHS"),
    MOTHS_BLUE("ambient", "MOTHS BLUE TIGHT"),
    MOTHS_BLUE2("ambient", "MOTHS BLUE TIGHT2"),
    MOTHS_BLUE3("ambient", "MOTHS BLUE TIGHT3"),
    MOTHS_GREEN("ambient", "MOTHS GREEN"),
    MOTHS_TIGHT("ambient", "MOTHS TIGHT"),
    MOTHS_TIGHT2("ambient", "MOTHS TIGHT2"),
    POISON_MIST("ambient", "POISON MIST"),
    POISON_MIST2("ambient", "POISON MIST2"),
    ASH("ambient", "ash falling"),
    CINDERS("ambient", "CINDERS tight"),
    CINDERS2("ambient", "CINDERS tight2"),
    CINDERS3("ambient", "CINDERS tight3"),
    SMOKE,
    ;
    public String path;

    EMITTER_PRESET() {
        String[] parts = name().split("_");
        String realName = name().replace(parts[0], "").replace("_", " ").trim();
        this.setPath(StrPathBuilder.build(
         parts[0], realName));
    }

    EMITTER_PRESET(String... pathParts) {
        this.setPath(StrPathBuilder.build(pathParts));
    }


    public boolean isPreloaded() {
        return true;
    }

    public boolean isAtlas() {
        return ParticleEffectX.isEmitterAtlasesOn();
    }

    public String getPath() {
        if (isAtlas()) {
            return StrPathBuilder.build("atlas", path);
        }
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
