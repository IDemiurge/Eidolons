package eidolons.game.exploration.dungeon.objects;

import main.content.enums.GenericEnums;
import main.entity.type.ObjType;
import main.system.GuiEventManager;
import main.system.GuiEventType;

/**
 * Created by JustMe on 10/10/2018.
 */
public class InteractiveObj extends DungeonObj {
    private boolean off;
    private boolean used;
    private InteractiveObjMaster.INTERACTIVE_OBJ_TYPE TYPE;

    public InteractiveObj(ObjType type, int x, int y) {
        super(type, x, y);
        TYPE = InteractiveObjMaster.chooseTypeForInteractiveObj(type); //TODO

        off = type.checkBool(GenericEnums.STD_BOOLS.OFF_DEFAULT);
    }

    @Override
    public DUNGEON_OBJ_TYPE getDungeonObjType() {
        return DUNGEON_OBJ_TYPE.INTERACTIVE;
    }

    public boolean isOff() {
        return off;
    }

    public void setOff(boolean off) {
        if ( this.off == off )
            return;

        this.off = off;
        GuiEventManager.trigger(GuiEventType.INTERACTIVE_OBJ_RESET, this);
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public InteractiveObjMaster.INTERACTIVE_OBJ_TYPE getTYPE() {
        return TYPE;
    }

    public void setTYPE(InteractiveObjMaster.INTERACTIVE_OBJ_TYPE TYPE) {
        this.TYPE = TYPE;
    }
}
