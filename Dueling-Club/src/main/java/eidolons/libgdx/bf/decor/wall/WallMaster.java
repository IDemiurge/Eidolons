package eidolons.libgdx.bf.decor.wall;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.module.dungeoncrawl.dungeon.LevelStruct;
import eidolons.libgdx.bf.decor.pillar.Pillars;
import main.content.enums.DungeonEnums;
import main.data.filesys.PathFinder;
import main.game.bf.directions.DIRECTION;
import main.system.GuiEventManager;
import main.system.GuiEventType;

import java.util.List;
/*
ObjTypes:
crumbling
secret (dynamic)
normal (indestructible!)

alt vs main wall type?
well, just good to have a way there...
> Walls with alpha?

Placeholders will be unnecessary :)

And that's it - the image setting logic will be taken over by this class.

 */
public class WallMaster {

    public static void resetWall(BattleFieldObject wall, List<DIRECTION> list) {
        LevelStruct struct = wall.getStruct( );
        //how to know if it's alt?
        //what will be its name?

        DungeonEnums.WALL_SET set = struct.getWallSet();
        if (set == null) {
            set = DungeonEnums.WALL_SET.cave;
        }
        //derive from something else ?
        // what about object types? Perhaps we really should limit it to a few types!
        DungeonEnums.WALL_TYPE type = getType(list);
        String image=getImage(type, set);
        if (!wall.getImagePath().equalsIgnoreCase(image)) {
            wall.setImage(image);
            GuiEventManager.trigger(GuiEventType.BF_OBJ_RESET, wall );
        }
        //rotation/flip?

    }

    public static Pillars.PILLAR getPillar(DungeonEnums.WALL_SET set) {
        //really depends only on the set? must be something more...
        return null;
    }

    public static DungeonEnums.WALL_TYPE getType(List<DIRECTION> joints) {
        if (joints.isEmpty()) {
            return DungeonEnums.WALL_TYPE.diamond;
        }
        if (joints.size() >= 4) {
            return DungeonEnums.WALL_TYPE.cross;
        }


        return DungeonEnums.WALL_TYPE.normal;
    }

    public static String getImage(DungeonEnums.WALL_TYPE type, DungeonEnums.WALL_SET set) {
        return PathFinder.getWallSetsFolder() + set + "/" + type + ".png";
    }

}
