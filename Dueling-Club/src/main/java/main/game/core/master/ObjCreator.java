package main.game.core.master;

import main.content.DC_TYPE;
import main.content.enums.entity.BfObjEnums;
import main.content.values.properties.G_PROPS;
import main.entity.Ref;
import main.entity.obj.BattleFieldObject;
import main.entity.obj.MicroObj;
import main.entity.obj.Structure;
import main.entity.obj.unit.Unit;
import main.entity.type.ObjType;
import main.game.core.game.DC_Game;
import main.game.logic.dungeon.Entrance;
import main.game.logic.battle.player.Player;
import main.system.launch.CoreEngine;

/**
 * Created by JustMe on 2/16/2017.
 */
public class ObjCreator extends Master{

    public ObjCreator(DC_Game game) {
        super(game);
    }


    public MicroObj createUnit(ObjType type, int x, int y, Player owner, Ref ref) {
        if (!CoreEngine.isArcaneVault()) {
            if (!CoreEngine.isLevelEditor()) {
                if (!type.isGenerated()) {
                    type = new ObjType(type);
                    game.initType(type);
                }
            }
        }
        BattleFieldObject obj;

        if (type.checkProperty(G_PROPS.BF_OBJECT_GROUP, BfObjEnums.BF_OBJECT_GROUP.ENTRANCE.toString())) {
            obj = new Entrance(x, y, type, getGame().getDungeon(), null);
        } else if (type.getOBJ_TYPE_ENUM() == DC_TYPE.BF_OBJ) {
            obj = new Structure(type, x, y, owner, getGame(), ref);
        } else {
            obj = new Unit(type, x, y, owner, getGame(), ref);
        }
        game.getState().addObject(obj);

        if (CoreEngine.isLevelEditor()) {
            return obj;
        }

        obj.toBase();
        obj.resetObjects();
        obj.afterEffects();

        return obj;

    }
}
