package main.level_editor.backend.struct.module;

import eidolons.entity.obj.DC_Obj;
import main.level_editor.gui.tree.data.LayeredData;

import java.util.Set;

public class ObjNode implements LayeredData {

    DC_Obj obj;

    public ObjNode(DC_Obj obj) {
        this.obj = obj;
    }

    public DC_Obj getObj() {
        return obj;
    }

    @Override
    public Set getChildren() {
        return null;
    }

    @Override
    public String toString() {
        return obj.getNameAndCoordinate();
    }
}
