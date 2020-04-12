package eidolons.game.battlecraft.logic.dungeon.location.struct.wrapper;

import eidolons.entity.obj.BattleFieldObject;
import main.data.tree.LayeredData;

import java.util.Collection;

public class ObjNode implements LayeredData {

    BattleFieldObject obj;

    public ObjNode(BattleFieldObject obj) {
        this.obj = obj;
    }

    public BattleFieldObject getObj() {
        return obj;
    }

    @Override
    public Collection getChildren() {
        return null;
    }

    @Override
    public String toString() {
        return obj.getNameAndCoordinate();
    }
}
