package eidolons.game.battlecraft.logic.dungeon.module;

import eidolons.game.battlecraft.logic.dungeon.location.struct.LevelStructure;
import eidolons.game.battlecraft.logic.dungeon.location.struct.ModuleData;
import eidolons.game.module.dungeoncrawl.dungeon.LevelLayer;
import eidolons.game.module.dungeoncrawl.dungeon.LevelZone;
import eidolons.libgdx.particles.ambi.AmbienceDataSource.AMBIENCE_TEMPLATE;
import main.game.bf.Coordinates;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;

import java.util.List;

import static eidolons.system.audio.MusicMaster.AMBIENCE;

public class Module extends LevelLayer<LevelZone> {
    private String name;
    private Coordinates origin;
    private int width;
    private int height;

    private List<LevelZone> zones;
    ModuleData data;

    public Module(Coordinates origin, int width, int height, String name) {
        this.origin = origin;
        this.width = width;
        this.height = height;
        this.name = name;
    }

    public Module() {
        super();
    }

    public Module(ModuleData data) {
        this.data = data;
        name = data.getValue(LevelStructure.MODULE_VALUE.name);
    }

    @Override
    public String toXml() {
        return null;
    }


    public int getX() {
        return origin.x;
    }

    public int getY() {
        return origin.y;
    }

    public AMBIENCE getAmbi() {
        return new EnumMaster<AMBIENCE>().retrieveEnumConst(AMBIENCE.class,
                data.getValue(LevelStructure.MODULE_VALUE.ambience));
    }

    public AMBIENCE_TEMPLATE getVfx() {
        return new EnumMaster<AMBIENCE_TEMPLATE>().retrieveEnumConst(AMBIENCE_TEMPLATE.class,
                data.getValue(LevelStructure.MODULE_VALUE.vfx_template));
    }

    public Coordinates getOrigin() {
        return origin;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getName() {
        if (StringMaster.isEmpty(name)) {
            name = "Main";
        }
        return name;
    }

    public int getX2() {
        return getX() + getWidth();
    }

    public int getY2() {
        return getY() + getHeight();
    }


    public List<LevelZone> getZones() {
        return zones;
    }

    public void setZones(List<LevelZone> zones) {
        this.zones = zones;
    }

    public void setOrigin(Coordinates origin) {
        this.origin = origin;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ModuleData getData() {
        return data;
    }

    public void setData(ModuleData data) {
        this.data = data;
    }

    public String getValue(LevelStructure.MODULE_VALUE module_value) {
        return getData().getValue(module_value);
    }

    public String getValue(String name) {
        return getData().getValue(name);
    }

    @Override
    public String toString() {
        return "Module: " +
                "name=" + name +
                "origin=" + origin +
                ", width=" + width +
                ", height=" + height +
                ", zones=" + zones +
                ", data=" + data;
    }

    public int getEffectiveHeight() {
        if (getData() == null) {
            return getHeight();
        }
        return getHeight() +
                getData().getIntValue(LevelStructure.MODULE_VALUE.height_buffer) +
                getData().getIntValue(LevelStructure.MODULE_VALUE.border_width);
    }

    public int getEffectiveWidth() {
        if (getData() == null) {
            return getWidth();
        }
        return getWidth() +
                getData().getIntValue(LevelStructure.MODULE_VALUE.width_buffer) +
                getData().getIntValue(LevelStructure.MODULE_VALUE.border_width);
    }
}
