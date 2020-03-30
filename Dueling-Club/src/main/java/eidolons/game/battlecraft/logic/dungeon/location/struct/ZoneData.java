package eidolons.game.battlecraft.logic.dungeon.location.struct;

import eidolons.game.battlecraft.logic.dungeon.location.struct.wrapper.LE_Zone;
import eidolons.game.module.dungeoncrawl.dungeon.LevelZone;
import eidolons.system.audio.MusicMaster.AMBIENCE;
import main.content.CONTENT_CONSTS.COLOR_THEME;
import main.content.enums.DungeonEnums.DUNGEON_STYLE;
import main.system.auxiliary.EnumMaster;

public class ZoneData extends LevelStructure.StructureData<ZoneData.ZONE_VALUE, LE_Zone> {

    public ZoneData(LE_Zone structure) {
        super(structure);
    }

    @Override
    protected void init() {
        LevelZone zone = getStructure().getZone();
        zone.setStyle( new EnumMaster<DUNGEON_STYLE>().retrieveEnumConst(DUNGEON_STYLE.class,
                getValue(ZONE_VALUE.style)));
        zone.setColorTheme( new EnumMaster<COLOR_THEME>().retrieveEnumConst(COLOR_THEME.class,
                getValue(ZONE_VALUE.color_theme)));
        zone.setAmbience( new EnumMaster<AMBIENCE>().retrieveEnumConst(AMBIENCE.class,
                getValue(ZONE_VALUE.ambience)));
//        zone.setAmbience( new EnumMaster<AMBIENCE_TEMPLATE>().retrieveEnumConst(AMBIENCE_TEMPLATE.class,
//                getValue(ZONE_VALUE.ambience)));

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
