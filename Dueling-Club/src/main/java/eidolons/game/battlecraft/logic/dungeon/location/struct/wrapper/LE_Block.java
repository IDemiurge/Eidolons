package eidolons.game.battlecraft.logic.dungeon.location.struct.wrapper;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.dungeon.LevelBlock;
import main.content.DC_TYPE;
import main.data.tree.LayeredData;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class LE_Block implements LayeredData<LayeredData> {
    LevelBlock block;
    Set<LayeredData> objs;

    public LE_Block(LevelBlock block) {
            this.block = block;
    }

    private boolean isWithinBlock(BattleFieldObject obj) {
        return block.getCoordinatesSet().contains(obj.getCoordinates());
    }

    public LevelBlock getBlock() {
        return block;
    }

    @Override
    public Set<LayeredData> getChildren() {
        objs= DC_Game.game.getBfObjects().stream().filter(
                obj-> isWithinBlock(obj) && obj.getOBJ_TYPE_ENUM()== DC_TYPE.ENCOUNTERS).map(
                obj-> new ObjNode(obj)).collect(Collectors.toCollection(LinkedHashSet::new));
        objs.add(new ObjsNode(block));
        return objs;
    }

    public Set<LayeredData> getObjs() {
        //TODO sort
        //supports adding!
        return objs;
    }

}
