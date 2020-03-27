package main.level_editor.backend.struct.module;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.module.dungeoncrawl.dungeon.LevelBlock;
import main.content.DC_TYPE;
import main.level_editor.LevelEditor;
import main.level_editor.gui.tree.data.LayeredData;

import java.util.Set;
import java.util.stream.Collectors;

public class LE_Block implements LayeredData<LayeredData> {
    LevelBlock block;
    Set<LayeredData> objs;

    public LE_Block(LevelBlock block) {
            this.block = block;
            objs= LevelEditor.getCurrent().getGame().getBfObjects().stream().filter(
                    obj-> isWithinBlock(obj) && obj.getOBJ_TYPE_ENUM()== DC_TYPE.ENCOUNTERS).map(
                    obj-> new ObjNode(obj)).collect(Collectors.toSet());

        objs.add(new ObjsNode(block));
    }

    private boolean isWithinBlock(BattleFieldObject obj) {
        return block.getCoordinatesList().contains(obj.getCoordinates());
    }

    public LevelBlock getBlock() {
        return block;
    }

    @Override
    public Set<LayeredData> getChildren() {
        return objs;
    }

    public Set<LayeredData> getObjs() {
        //TODO sort
        //supports adding!
        return objs;
    }

}
