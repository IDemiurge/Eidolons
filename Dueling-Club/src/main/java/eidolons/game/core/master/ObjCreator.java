package eidolons.game.core.master;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.Structure;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.objects.ContainerObj;
import eidolons.game.module.dungeoncrawl.objects.Door;
import eidolons.game.module.dungeoncrawl.objects.LockObj;
import main.content.DC_TYPE;
import main.content.enums.entity.BfObjEnums;
import main.content.enums.entity.BfObjEnums.BF_OBJECT_GROUP;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.entity.Ref;
import main.entity.obj.MicroObj;
import main.entity.type.ObjType;
import main.game.logic.battle.player.Player;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.RandomWizard;
import main.system.launch.CoreEngine;

import java.util.List;

/**
 * Created by JustMe on 2/16/2017.
 */
public class ObjCreator extends Master {

    public static final String PLACEHOLDER = "Placeholder";

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
        if (!CoreEngine.isLevelEditor())
            if (!CoreEngine.isArcaneVault())
                type = checkTypeSubstitution(type, ref);

        BattleFieldObject obj = null;

        if (type.checkProperty(G_PROPS.BF_OBJECT_GROUP, BfObjEnums.BF_OBJECT_GROUP.ENTRANCE.toString())) {
//       TODO      obj = new Entrance(x, y, type, getGame().getDungeon(), null);
            return null;
        } else if (type.getOBJ_TYPE_ENUM() == DC_TYPE.BF_OBJ) {
            obj = newStructure(type, x, y, owner, ref);
        } else {
            obj = new Unit(type, x, y, owner, getGame(), ref);
        }
        if (CoreEngine.isLevelEditor()) {
            return obj;
        }
        //if (WaitMaster.getCompleteOperations().contains(WAIT_OPERATIONS.DUNGEON_SCREEN_READY))
        GuiEventManager.trigger(GuiEventType.UNIT_CREATED, obj);
        game.getState().addObject(obj);

        if (obj instanceof Unit)
            game.getState().getManager().reset((Unit) obj);
        else {
            obj.toBase();
            obj.resetObjects();
            obj.afterEffects();
        }

        return obj;

    }

    private ObjType checkTypeSubstitution(ObjType type, Ref ref) {
        if (!type.getName().split(" ")[0].equalsIgnoreCase(PLACEHOLDER)) {
            return type;
        }
        String unitGroup = type.getProperty(
         type.getOBJ_TYPE_ENUM().getSubGroupingKey()
//         G_PROPS.UNIT_GROUP
        );
        List<ObjType> list = DataManager.getTypesSubGroup(type.getOBJ_TYPE_ENUM(), unitGroup);

        String group = type.getGroup();
        if (!group.isEmpty()) {
            list.removeIf(t -> !t.getGroup().equalsIgnoreCase(group));
        }
        //power constraints

        if (list.isEmpty())
            return type;
        type = new RandomWizard<ObjType>().getRandomListItem(list);
        return type;
    }


    private BattleFieldObject newStructure(ObjType type, int x, int y, Player owner,
                                           Ref ref) {
        BF_OBJECT_GROUP group = new EnumMaster<BF_OBJECT_GROUP>().retrieveEnumConst(BF_OBJECT_GROUP.class,
         type.getProperty(G_PROPS.BF_OBJECT_GROUP));
        if (group != null) {
            switch (group) {
                case DOOR:
                    return new Door(type, x, y, owner, getGame(), ref);
                case LOCK:
                    return new LockObj(type, x, y, owner, getGame(), ref);
                case TREASURE:
                case CONTAINER:
                    return new ContainerObj(type, x, y);
                case GRAVES:
                case REMAINS:
                case INTERIOR:
                    return new ContainerObj(type, x, y);
            }
        }

        return new Structure(type, x, y, owner, getGame(), ref);
    }

}
