package main.level_editor.backend.selection;

import main.content.enums.GenericEnums;
import main.entity.type.ObjType;

public class PaletteSelection {

    public PaletteSelection(ObjType type) {
        this(type, false);
    }
    public PaletteSelection(ObjType objType, boolean overlaying) {
        this.overlaying = overlaying;
        if (overlaying) {
            objTypeOverlaying = objType;
        } else
            this.objType = objType;
    }

    ObjType objType;
    ObjType objTypeOverlaying;
    GenericEnums.VFX vfx;
    boolean overlaying;

    public ObjType getObjType() {
        return objType;
    }

    public ObjType getObjTypeOverlaying() {
        return objTypeOverlaying;
    }

    public GenericEnums.VFX getVfx() {
        return vfx;
    }

    public boolean isOverlaying() {
        return overlaying;
    }

    //custom type?

    //templates of rooms, scripts,
}
