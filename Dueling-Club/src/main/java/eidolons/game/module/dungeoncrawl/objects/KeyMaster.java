package eidolons.game.module.dungeoncrawl.objects;

import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.entity.item.DC_HeroItemObj;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.dungeon.universal.Dungeon;
import eidolons.game.core.EUtils;
import eidolons.libgdx.anims.text.FloatingTextMaster;
import main.entity.Ref;
import main.game.bf.Coordinates;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.MapMaster;
import main.system.launch.CoreEngine;

import java.util.HashMap;
import java.util.Map;

public class KeyMaster {
    private static Map<Dungeon, Map<String, String>> pairMaps = new HashMap<>();
    private static Map<Coordinates, String> customKeyMap = new HashMap<>();

    private static Map<String, String> getPairMap(Dungeon dungeon) {
        Map<String, String> map = pairMaps.get(dungeon);
        if (map != null) {
            return map;
        }
        String data = dungeon.getProperty(PROPS.KEY_DOOR_PAIRS);
        map = MapMaster.createStringMap(data);
        return map;
    }

    public static boolean isSealedDoor(Door door) {
        String typeName = door.getName();
        Map<String, String> map = getPairMap(door.getGame().getDungeon());
        String type = map.get(typeName);
        if (type == null) {
            return false;
        }
        return true;
    }

    public static boolean hasKey(Unit unit, Door door) {
        if (CoreEngine.isKeyCheat()) {
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
        if (item != null) {
            return item;
        }
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
        if (CoreEngine.isKeyCheat()) {
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
