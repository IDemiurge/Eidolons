package main.level_editor.backend.handlers.selection;

import main.content.enums.GenericEnums;
import main.entity.EntityCheckMaster;
import main.entity.type.ObjType;

import java.io.Serializable;

public class PaletteSelection implements Serializable {

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

    public void setType(ObjType objType) {
        if (EntityCheckMaster.isOverlaying(objType)) {
                  setOverlayingType(objType);
        } else
            this.objType = objType;
    }

    public void setOverlayingType(ObjType objTypeOverlaying) {
        this.objTypeOverlaying = objTypeOverlaying;
    }
//custom type?

    //templates of rooms, scripts,
}
