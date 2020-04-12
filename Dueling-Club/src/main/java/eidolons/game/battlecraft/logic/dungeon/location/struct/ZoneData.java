package eidolons.game.battlecraft.logic.dungeon.location.struct;

import eidolons.game.module.dungeoncrawl.dungeon.LevelZone;

public class ZoneData extends StructureData<LevelStructure.ZONE_VALUE, LevelZone> {

    public ZoneData(LevelZone structure) {
        super(structure);
    }

    protected void init() {
        levelStruct = getStructure() ;
        if (levelStruct.getData() != null) {
            setData(levelStruct.getData().getData());
        }
    }

    @Override
    public void apply() {
        LevelZone zone = getStructure() ;
        zone.setData(this);
//        zone.setStyle(new EnumMaster<DUNGEON_STYLE>().retrieveEnumConst(DUNGEON_STYLE.class,
//                getValue(LevelStructure.ZONE_VALUE.style)));
//        zone.setIllumination(getIntValue(LevelStructure.ZONE_VALUE.illumination));

    }

    @Override
    public Class<? extends LevelStructure.ZONE_VALUE> getEnumClazz() {
        return LevelStructure.ZONE_VALUE.class;
    }

}
