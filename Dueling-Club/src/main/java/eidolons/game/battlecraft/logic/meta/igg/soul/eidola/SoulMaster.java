package eidolons.game.battlecraft.logic.meta.igg.soul.eidola;

import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.game.battlecraft.logic.meta.igg.soul.EidolonLord;
import eidolons.game.battlecraft.logic.meta.igg.soul.SoulforceRule;
import javafx.beans.property.SetProperty;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.data.XLinkedMap;
import main.entity.type.ObjType;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.data.MapMaster;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SoulMaster {

    private static final boolean TEST_MODE = true;
    static Map<Integer, Soul> soulMap = new XLinkedMap<>();
    static Map<ObjType, Integer> soulTypeIntMap = new XLinkedMap<>();

    public static List<Soul> getSoulList() {
        soulTypeIntMap.clear();
        if (!EidolonLord.lord.checkProperty(PROPS.LORD_SOULS) && TEST_MODE) {
            EidolonLord.lord.setProperty(PROPS.LORD_SOULS, "Pirate;Troglodyte Mutant;Mycosa;Trickster Raven;Pale Weaver;Harpy;Bone Knight;");
        }
        List<ObjType> types = DataManager.toTypeList(EidolonLord.lord.getProperty(PROPS.LORD_SOULS),
                DC_TYPE.UNITS);

        return types.stream().map(type -> getOrCreate(type)).collect(Collectors.toList());
    }

    public static void clear() {
        soulMap.clear();
    }

    private static Soul getOrCreate(ObjType type) {
        //TODO wtf do we do when a soul is removed?!


        Integer id = null;
        Soul soul = soulMap.get(id = getId(type));
        if (soul == null) {
            soul = new Soul(type);
            soulMap.put(id, soul);
        }
        return soul;
    }

    private static Integer getId(ObjType type) {
        MapMaster.addToIntegerMap(soulTypeIntMap, type, 1);
        return soulTypeIntMap.get(type) * 10000 + type.getId();
    }

    public static void consume(Soul soul) {
        int force = SoulforceRule.getForce(soul.getUnitType());
//        GuiEventManager.trigger(GuiEventType. VFX_PLAY_LAST)
        // https://soundcloud
        EidolonLord.lord.addParam(PARAMS.SOULFORCE, force);
        EidolonLord.lord.soulsLost(soul);
        GuiEventManager.trigger(GuiEventType.UPDATE_LORD_PANEL);
    }
}
