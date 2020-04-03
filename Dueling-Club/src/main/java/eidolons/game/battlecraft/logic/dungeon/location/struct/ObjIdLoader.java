package eidolons.game.battlecraft.logic.dungeon.location.struct;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.battlecraft.logic.battlefield.DC_ObjInitializer;
import eidolons.game.battlecraft.logic.dungeon.universal.Dungeon;
import eidolons.game.module.dungeoncrawl.dungeon.LevelBlock;
import main.entity.obj.Obj;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.system.auxiliary.ContainerUtils;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ObjIdLoader {
    public static Map<Integer, BattleFieldObject> processObjects(
            Map<Integer, ObjType> idMap,
            Dungeon dungeon, Node subNode) {
        //TODO   create player=> ids map and make multiple maps here!
        if (idMap == null) {
            throw new RuntimeException("No ID MAP FOR OBJECTS!");
        }
        Map<Integer, BattleFieldObject> objIdMap = new LinkedHashMap<>();
        //for LE only?

        for (String substring : ContainerUtils.openContainer(
                subNode.getTextContent())) {
            String objectsString = "";
            Coordinates c = Coordinates.get(true, substring.split("=")[0]);
            List<String> ids =    new ArrayList<>() ;
            try {
                 ids = ContainerUtils.openContainer(substring.split("=")[1], ",");
                for (String id : ids) {
                    objectsString += c + DC_ObjInitializer.COORDINATES_OBJ_SEPARATOR
                            + idMap.get(Integer.valueOf(id)).getName()
                            + DC_ObjInitializer.OBJ_SEPARATOR;
                }
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
                continue;
            }
            LevelBlock b = null;
            try {
                b = dungeon.getGame().getDungeonMaster().getDungeonLevel().getBlockForCoordinate(c);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
            Map<Coordinates, ? extends Obj> subMap =
                    DC_ObjInitializer.initMapBlockObjects(dungeon, b, objectsString);
            int i = 0;
            for (Obj value : subMap.values()) {
                if (value instanceof BattleFieldObject) {
                    Integer id = Integer.valueOf(ids.get(i++));
                    objIdMap.put(id, (BattleFieldObject) value);
                }
            }
        }

        return objIdMap;
    }
}
