package eidolons.game.battlecraft.logic.dungeon.location.struct;

import eidolons.game.battlecraft.logic.dungeon.location.struct.wrapper.LE_Zone;
import eidolons.game.module.dungeoncrawl.dungeon.LevelZone;
import main.content.enums.DungeonEnums.DUNGEON_STYLE;
import main.system.auxiliary.EnumMaster;

public class ZoneData extends LevelStructure.StructureData<ZoneData.ZONE_VALUE, LE_Zone> {

    public ZoneData(LE_Zone structure) {
        super(structure);
    }

    protected void init() {
        LevelZone zone = getStructure().getZone();
        setValue(ZONE_VALUE.illumination , zone.getGlobalIllumination());
//        setValue( , zone.getGlobalIllumination());
    }
    @Override
    public void apply() {
        LevelZone zone = getStructure().getZone();
        zone.setData(this);
        zone.setStyle( new EnumMaster<DUNGEON_STYLE>().retrieveEnumConst(DUNGEON_STYLE.class,
                getValue(ZONE_VALUE.style)));
        zone.setGlobalIllumination(getIntValue(ZONE_VALUE.illumination));

    }

    @Override
    public Class<? extends ZONE_VALUE> getEnumClazz() {
        return ZONE_VALUE.class;
    }

    public enum ZONE_VALUE{
name,
        id,
        illumination,
        style,
        color_theme,
        ambience,

    }
}
