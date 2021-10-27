package eidolons.game.battlecraft.logic.dungeon.location.struct;

import eidolons.game.exploration.dungeon.struct.LevelZone;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ZoneData extends StructureData<LevelStructure.ZONE_VALUE, LevelZone> {


    public ZoneData(LevelZone structure) {
        super(structure);
    }

    public ZoneData(String s) {
        super(null);
        setData(s);
    }

    private static String[] cropped;
    @Override
    public String[] getValuesCropped() {
        if (cropped == null) {
            List<String> list = Arrays.stream(getEnumClazz().getEnumConstants()).map(Enum::toString).
                    collect(Collectors.toList());
            list.remove(LevelStructure.ZONE_VALUE.height.toString());
            list.remove(LevelStructure.ZONE_VALUE.width.toString());
            list.remove(LevelStructure.ZONE_VALUE.id.toString());
            list.remove(LevelStructure.ZONE_VALUE.illumination.toString());
            list.remove(LevelStructure.ZONE_VALUE.origin.toString());
            cropped = list.toArray(new String[0]);
        }
        return cropped;
    }

    protected void init() {
        levelStruct = getStructure();
        if (levelStruct != null)
            if (levelStruct.getData() != null) {
                setData(levelStruct.getData().getData());
            }
    }

    @Override
    public void apply() {
        LevelZone zone = getStructure();
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
