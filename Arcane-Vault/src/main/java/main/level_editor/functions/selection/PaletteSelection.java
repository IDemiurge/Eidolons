package main.level_editor.functions.selection;

import main.content.enums.GenericEnums;
import main.entity.type.ObjType;

public class PaletteSelection {

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
    //custom type?

    //templates of rooms, scripts,
}
