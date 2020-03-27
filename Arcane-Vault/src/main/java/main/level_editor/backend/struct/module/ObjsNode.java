package main.level_editor.backend.struct.module;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.module.dungeoncrawl.dungeon.LevelBlock;
import main.content.DC_TYPE;
import main.level_editor.LevelEditor;
import main.level_editor.gui.tree.data.LayeredData;

import java.util.Set;
import java.util.stream.Collectors;

public class ObjsNode  implements LayeredData<ObjNode>{

    private final LevelBlock block;
    Set<ObjNode> objs;

    public ObjsNode(LevelBlock block) {
        this.block = block;
        objs= LevelEditor.getCurrent().getGame().getBfObjects().stream().filter(obj->
              obj.getOBJ_TYPE_ENUM()!= DC_TYPE.ENCOUNTERS   && isWithinBlock(obj)).map(
                obj-> new ObjNode(obj)).collect(Collectors.toSet());
    }

    private boolean isWithinBlock(BattleFieldObject obj) {
        return block.getCoordinatesList().contains(obj.getCoordinates());
    }
    @Override
    public Set<ObjNode> getChildren() {
        return objs;
    }
}
