package eidolons.game.module.dungeoncrawl.objects;

import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.entity.item.DC_HeroItemObj;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.dungeon.universal.Floor;
import eidolons.game.core.EUtils;
import libgdx.anims.text.FloatingTextMaster;
import main.entity.Ref;
import main.game.bf.Coordinates;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.data.MapMaster;
import main.system.launch.Flags;

import java.util.HashMap;
import java.util.Map;

public class KeyMaster {
    private static final Map<Floor, Map<String, String>> pairMaps = new HashMap<>();
    private static final Map<Coordinates, String> customKeyMap = new HashMap<>();

    private static Map<String, String> getPairMap(Floor floor) {
        Map<String, String> map = pairMaps.get(floor);
        if (map != null) {
            return map;
        }
        //TODO dc init fix
        String data = floor.getProperty(PROPS.KEY_DOOR_PAIRS);
        map = MapMaster.createStringMap(data);
        return map;
    }

    public static boolean isSealedDoor(Door door) {
        String typeName = door.getName();
        Map<String, String> map = getPairMap(door.getGame().getDungeon());
        String type = map.get(typeName);
        return type != null;
    }

    public static boolean hasKey(Unit unit, Door door) {
        if (Flags.isKeyCheat()) {
            return true;
        }
        String type = getRequiredKey(door);
        if (getKey(unit, type) != null) {
            return true;
        }
        if (getKey(unit, "Master Key") != null) {
            return true;
        }
        FloatingTextMaster.getInstance().createFloatingText(FloatingTextMaster.TEXT_CASES.REQUIREMENT,
                "" + type + " is required to unlock this",
                door);
        return false;
    }

    public static String getRequiredKey(Door door) {
        String type = null;
        if (type == null) {
            type = customKeyMap.get(door.getCoordinates());
        }
        if (type != null) {
            return type;
        }
        Map<String, String> map = getPairMap(door.getGame().getDungeon());
        type = map.get(door.getName());
        if (type == null) {
            type = door.getProperty(PROPS.KEY_TYPE);
        }
        return type;
    }

    public static DC_HeroItemObj getKey(Unit unit, String type) {

        DC_HeroItemObj item = unit.findItem(type, true);
        if (item != null) {
            return item;
        }
        item = unit.findItem(type, false);
        if (item != null) {
            return item;
        }
        item = unit.findItem(type, null);
        if (item != null) {
            return item;
        }
        item = unit.findItem("Master Key", null);
        return item;
    }

    public static void initCustomKeyMap(String data) {
        Map<String, String> map = MapMaster.createStringMap(data);
        for (String s : map.keySet()) {
            if (map.get(s).contains("key==")) {
                customKeyMap.put(Coordinates.get(s), map.get(s).split("key==")[1]);
            }
        }
    }

    public static boolean addCustomKey(String coordinate, String data) {
        if (coordinate.contains("key==")) {
            customKeyMap.put(Coordinates.get(coordinate), data.split("key==")[1]);
            return true;
        }

        return false;
    }

    public static void initAnimRef(DungeonObj obj, Ref ref) {
    }

    public static void doorUnsealed(Door door, Unit unit) {
        if (Flags.isKeyCheat()) {
            return;
        }
        DC_HeroItemObj key = unit.findItem(getRequiredKey(door), true);
        if (key == null) {
            key = unit.findItem(getRequiredKey(door), false);
        }
        if (RandomWizard.chance(unit.getIntParam(PARAMS.SLEIGHT_OF_HAND))) {
            EUtils.showInfoText(true, unit.getName() + " uses sleight of hand to retain " + key.getName());
        } else {
            EUtils.showInfoText(true, unit.getName() + " uses " +
                    key.getName() + " to open " + door.getName());
            unit.removeFromInventory(key);
        }

    }

    public static boolean isKey(BattleFieldObject userObject) {
        return userObject.getName().contains(" Key"); //TODO
    }

}
