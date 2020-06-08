package main.level_editor.backend.handlers.selection;

import eidolons.game.core.EUtils;
import eidolons.game.module.generator.model.RoomModel;
import eidolons.libgdx.bf.datasource.GraphicData;
import main.content.enums.GenericEnums;
import main.entity.EntityCheckMaster;
import main.entity.type.ObjType;

import java.io.Serializable;

public class PaletteSelection implements Serializable {

    private RoomModel template;
    private static PaletteSelection instance;
    private GraphicData decorData;

    public static PaletteSelection getInstance() {
        if (instance == null) {
            instance = new PaletteSelection();
        }
        return instance;
    }

    private PaletteSelection() {
    }

    ObjType objType;
    ObjType objTypeOverlaying;
    GenericEnums.VFX vfx;

    public ObjType getObjType() {
        return objType;
    }

    public ObjType getObjTypeOverlaying() {
        return objTypeOverlaying;
    }

    public GenericEnums.VFX getVfx() {
        return vfx;
    }


    public void setType(ObjType objType) {
        if (EntityCheckMaster.isOverlaying(objType)) {
            setOverlayingType(objType);
            EUtils.showInfoText("Palette overlaying type selected: " + objType.getName());
        } else {
            this.objType = objType;
            setOverlayingType(null);
            EUtils.showInfoText("Palette type selected: " + objType.getName());
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

    public GraphicData getDecorData() {
        return decorData;
    }

    public void setDecorData(GraphicData decorData) {
        this.decorData = decorData;
    }

    //custom type?

    //templates of rooms, scripts,
}
