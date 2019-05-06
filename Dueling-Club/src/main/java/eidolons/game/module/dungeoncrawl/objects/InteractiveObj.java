package eidolons.game.module.dungeoncrawl.objects;

import eidolons.game.module.dungeoncrawl.objects.InteractiveObjMaster.INTERACTIVE_OBJ_TYPE;
import main.entity.type.ObjType;

/**
 * Created by JustMe on 10/10/2018.
 */
public class InteractiveObj extends DungeonObj {
    private boolean off;
    private INTERACTIVE_OBJ_TYPE TYPE;

    public InteractiveObj(ObjType type, int x, int y) {
        super(type, x, y);
        TYPE=InteractiveObjMaster.chooseTypeForInteractiveObj(type); //TODO
    }

    @Override
    public DUNGEON_OBJ_TYPE getDungeonObjType() {
        return DUNGEON_OBJ_TYPE.INTERACTIVE;
    }

    public boolean isOff() {
        return off;
    }

    public void setOff(boolean off) {
        this.off = off;
    }

    public INTERACTIVE_OBJ_TYPE getTYPE() {
        return TYPE;
    }

    public void setTYPE(INTERACTIVE_OBJ_TYPE TYPE) {
        this.TYPE = TYPE;
    }
}
