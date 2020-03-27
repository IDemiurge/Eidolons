package main.level_editor.backend.struct.module;

import eidolons.entity.obj.BattleFieldObject;
import main.level_editor.gui.tree.data.LayeredData;

import java.util.Set;

public class ObjNode implements LayeredData {

    BattleFieldObject obj;

    public ObjNode(BattleFieldObject obj) {
        this.obj = obj;
    }

    public BattleFieldObject getObj() {
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
