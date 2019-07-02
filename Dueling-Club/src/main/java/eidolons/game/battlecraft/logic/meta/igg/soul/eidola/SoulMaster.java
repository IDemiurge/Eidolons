package eidolons.game.battlecraft.logic.meta.igg.soul.eidola;

import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.game.battlecraft.logic.meta.igg.soul.EidolonLord;
import eidolons.game.battlecraft.logic.meta.igg.soul.SoulforceRule;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.entity.type.ObjType;

import java.util.List;
import java.util.stream.Collectors;

public class SoulMaster {

    private static final boolean TEST_MODE = true;

    public static List<Soul> getSoulList() {
        List<ObjType> types = DataManager.toTypeList(EidolonLord.lord.getProperty(PROPS.LORD_SOULS),
                DC_TYPE.UNITS);
       if (types.isEmpty() && TEST_MODE){
            types = DataManager.toTypeList( "Pirate;Dwarf Brawler;Vampire Bat;Black Wolf;Imp;Harpy;Skeleton;",
                    DC_TYPE.UNITS);
        }
        return types.stream().map(type -> new Soul(type)).collect(Collectors.toList());
    }

    public static void consume(Soul eidolon) {

        int force = SoulforceRule.getForce(eidolon.getUnitType());
//        GuiEventManager.trigger(GuiEventType. VFX_PLAY_LAST)
        // https://soundcloud
        EidolonLord.lord.addParam(PARAMS.SOULFORCE, force);
        EidolonLord.lord.removeProperty(PROPS.LORD_SOULS, eidolon.getUnitType().getName());
    }
}
