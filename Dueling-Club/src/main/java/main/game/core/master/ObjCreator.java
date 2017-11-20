package main.game.core.master;

import main.content.DC_TYPE;
import main.content.enums.entity.BfObjEnums;
import main.content.enums.entity.BfObjEnums.BF_OBJECT_GROUP;
import main.content.values.properties.G_PROPS;
import main.entity.Ref;
import main.entity.obj.BattleFieldObject;
import main.entity.obj.MicroObj;
import main.entity.obj.Structure;
import main.entity.obj.unit.Unit;
import main.entity.type.ObjType;
import main.game.core.game.DC_Game;
import main.game.logic.battle.player.Player;
import main.game.module.dungeoncrawl.objects.ContainerObj;
import main.game.module.dungeoncrawl.objects.Door;
import main.game.module.dungeoncrawl.objects.LockObj;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.EnumMaster;
import main.system.launch.CoreEngine;

/**
 * Created by JustMe on 2/16/2017.
 */
public class ObjCreator extends Master {

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
        BattleFieldObject obj = null;

        if (type.checkProperty(G_PROPS.BF_OBJECT_GROUP, BfObjEnums.BF_OBJECT_GROUP.ENTRANCE.toString())) {
//       TODO      obj = new Entrance(x, y, type, getGame().getDungeon(), null);
       return null ;
        } else if (type.getOBJ_TYPE_ENUM() == DC_TYPE.BF_OBJ) {
            obj = newStructure(type, x, y, owner,   ref);
        } else {
            obj = new Unit(type, x, y, owner, getGame(), ref);
        }
        if (CoreEngine.isLevelEditor()) {
            return obj;
        }
        //if (WaitMaster.getCompleteOperations().contains(WAIT_OPERATIONS.GDX_READY))
            GuiEventManager.trigger(GuiEventType.UNIT_CREATED, obj);
        game.getState().addObject(obj);

        if (obj instanceof  Unit)
game.getState().getManager().reset((Unit) obj);
        else {
            obj.toBase();
            obj.resetObjects();
            obj.afterEffects();
        }

        return obj;

    }

    private BattleFieldObject newStructure(ObjType type, int x, int y, Player owner,
                                             Ref ref) {
        BF_OBJECT_GROUP group = new EnumMaster<BF_OBJECT_GROUP>().retrieveEnumConst(BF_OBJECT_GROUP.class,
         type.getProperty(G_PROPS.BF_OBJECT_GROUP));
       if (group!=null ){
           switch (group) {
               case DOOR:
                   return new Door(type, x, y, owner, getGame(), ref);
               case LOCK:
                   return new LockObj(type, x, y, owner, getGame(), ref);
               case TREASURE:
               case CONTAINER:
                   return new ContainerObj(type, x, y );
               case GRAVES:
               case REMAINS:
               case INTERIOR:
                   return new ContainerObj(type, x, y );
           }
       }

        return new Structure(type, x, y, owner, getGame(), ref);
    }

}
