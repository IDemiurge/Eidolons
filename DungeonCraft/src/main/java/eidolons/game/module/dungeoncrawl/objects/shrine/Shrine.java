package eidolons.game.module.dungeoncrawl.objects.shrine;

import eidolons.content.PARAMS;
import eidolons.game.module.dungeoncrawl.objects.DungeonObj;
import main.entity.type.ObjType;

import static eidolons.game.module.dungeoncrawl.objects.DungeonObj.DUNGEON_OBJ_TYPE.SHRINE;
@Deprecated
public class Shrine extends DungeonObj  {

    public Shrine(ObjType type, int x, int y) {
        super(type, x, y);
    }

    @Override
    public DUNGEON_OBJ_TYPE getDungeonObjType() {
        return SHRINE;
    }

    public int getCharges() {
        return getIntParam(PARAMS.C_CHARGES);
    }

    public enum SHRINE_TYPE{
        unseen_shrine,
        blood_shrine,
        soul_shrine,
        bone_shrine,
        ash_shrine,
        mystic_pool,
    }
}
