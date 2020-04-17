package main.level_editor.backend.handlers.selection;

import eidolons.game.core.EUtils;
import eidolons.game.module.generator.model.RoomModel;
import main.content.enums.GenericEnums;
import main.entity.EntityCheckMaster;
import main.entity.type.ObjType;
import main.level_editor.LevelEditor;

import java.io.Serializable;

public class PaletteSelection implements Serializable {

    private RoomModel template;

    public PaletteSelection( ) {
        this(LevelEditor.getManager().getObjHandler().getDefaultWallType());
    }
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
            EUtils.showInfoText("Palette overlaying type selected: " + objType);
        } else
        {
            this.objType = objType;
            EUtils.showInfoText("Palette type selected: " + objType);
        }
    }

    public void setOverlayingType(ObjType objTypeOverlaying) {
        this.objTypeOverlaying = objTypeOverlaying;
    }

    public void setTemplate(RoomModel template) {
        this.template = template;
    }

    public RoomModel getTemplate() {
        return template;
    }


//custom type?

    //templates of rooms, scripts,
}
