package eidolons.game.battlecraft.logic.dungeon.location.struct.wrapper;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.core.game.DC_Game;
import eidolons.game.exploration.dungeons.struct.LevelBlock;
import main.content.DC_TYPE;
import main.data.tree.LayeredData;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class ObjsNode  implements LayeredData<ObjNode>{

    private final LevelBlock block;
    Set<ObjNode> objs;

    public ObjsNode(LevelBlock block) {
        this.block = block;
        objs= DC_Game.game.getBfObjects().stream().filter(obj->
              obj.getOBJ_TYPE_ENUM()!= DC_TYPE.ENCOUNTERS   && isWithinBlock(obj)).map(
                ObjNode::new).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public String toString() {
        return "--- Objects" ;
    }

    private boolean isWithinBlock(BattleFieldObject obj) {
        return block.getCoordinatesSet().contains(obj.getCoordinates());
    }
    @Override
    public Collection<ObjNode> getChildren() {
        return objs;
    }
}
