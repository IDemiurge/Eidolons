package eidolons.game.netherflame.igg.soul.eidola;

import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.Structure;
import eidolons.game.netherflame.igg.soul.EidolonLord;
import eidolons.game.netherflame.igg.soul.SoulforceMaster;
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
    private static List<Soul> lastList;

    public static List<Soul> getSoulList() {
        soulTypeIntMap.clear();
        if (!EidolonLord.lord.checkProperty(PROPS.LORD_SOULS) && TEST_MODE) {
            EidolonLord.lord.setProperty(PROPS.LORD_SOULS, "Pirate;Troglodyte Mutant;Mycosa;Trickster Raven;Pale Weaver;Harpy;Bone Knight;");
        }
        List<ObjType> types = DataManager.toTypeList(EidolonLord.lord.getProperty(PROPS.LORD_SOULS),
                DC_TYPE.UNITS);

        lastList = types.stream().map(type -> getOrCreate(type)).collect(Collectors.toList());
        return lastList;
    }

    public static void clear() {
        soulMap.clear();
    }

    public static void resetSouls() {
        if (lastList != null) {
            for (Soul soul : lastList) {
                soul.setBeingUsed(false);
            }
        }
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
        int force = SoulforceMaster.getForce(soul.getUnitType());
//        GuiEventManager.trigger(GuiEventType. VFX_PLAY_LAST)
        EidolonLord.lord.addParam(PARAMS.SOULFORCE, force);
        EidolonLord.lord.soulsLost(soul);
        GuiEventManager.trigger(GuiEventType.UPDATE_SOULS_PANEL);

    }

    private static boolean isSoulTrapOn(BattleFieldObject killed) {
        return !(killed instanceof Structure);
    }
    public static void gainSoul(BattleFieldObject killed) {
        Soul soul = getOrCreate(killed.getType());
        EidolonLord.lord.soulsGained(soul);
    }
    public static void slain(BattleFieldObject killed) {
        if(isSoulTrapOn(killed)){
            gainSoul(killed);

        }


    }

}
