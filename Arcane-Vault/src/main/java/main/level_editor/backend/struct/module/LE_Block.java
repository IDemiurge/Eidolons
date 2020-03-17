package main.level_editor.backend.struct.module;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.module.dungeoncrawl.dungeon.LevelBlock;
import main.level_editor.LevelEditor;
import main.level_editor.gui.tree.data.LayeredData;

import java.util.Set;
import java.util.stream.Collectors;

public class LE_Block implements LayeredData<ObjNode> {
    LevelBlock block;
    Set<ObjNode> objs;

    public LE_Block(LevelBlock block) {
        this.block = block;
        objs= LevelEditor.getCurrent().getGame().getBfObjects().stream().filter(obj-> isWithinBlock(obj)).map(
                obj-> new ObjNode(obj)).collect(Collectors.toSet());

    }

    private boolean isWithinBlock(BattleFieldObject obj) {
        return block.getCoordinatesList().contains(obj.getCoordinates());
    }

    public LevelBlock getBlock() {
        return block;
    }

    @Override
    public Set<ObjNode> getChildren() {
        return objs;
    }

    public Set<ObjNode> getObjs() {
        //TODO sort
        //supports adding!
        return objs;
    }

    public void setObjs(Set<ObjNode> objs) {
        this.objs = objs;
    }
}
