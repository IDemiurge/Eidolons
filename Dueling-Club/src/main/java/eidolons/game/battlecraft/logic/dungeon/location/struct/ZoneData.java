package eidolons.game.battlecraft.logic.dungeon.location.struct;

import eidolons.game.battlecraft.logic.dungeon.location.struct.wrapper.LE_Zone;
import eidolons.game.module.dungeoncrawl.dungeon.LevelZone;
import main.content.enums.DungeonEnums.DUNGEON_STYLE;
import main.system.auxiliary.EnumMaster;

public class ZoneData extends LevelStructure.StructureData<LevelStructure.ZONE_VALUE, LE_Zone> {

    public ZoneData(LE_Zone structure) {
        super(structure);
    }

    protected void init() {
        LevelZone zone = getStructure().getZone();
        setValue(LevelStructure.ZONE_VALUE.illumination, zone.getGlobalIllumination());
//        setValue( , zone.getGlobalIllumination());
    }

    @Override
    public void apply() {
        LevelZone zone = getStructure().getZone();
        zone.setData(this);
        zone.setStyle(new EnumMaster<DUNGEON_STYLE>().retrieveEnumConst(DUNGEON_STYLE.class,
                getValue(LevelStructure.ZONE_VALUE.style)));
        zone.setGlobalIllumination(getIntValue(LevelStructure.ZONE_VALUE.illumination));

    }

    @Override
    public Class<? extends LevelStructure.ZONE_VALUE> getEnumClazz() {
        return LevelStructure.ZONE_VALUE.class;
    }

}
