package eidolons.game.core.master;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.Structure;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.DC_Engine;
import eidolons.game.battlecraft.logic.dungeon.module.Module;
import eidolons.game.core.game.DC_Game;
import eidolons.game.core.state.DC_GameState;
import eidolons.game.module.dungeoncrawl.dungeon.Entrance;
import eidolons.game.module.dungeoncrawl.objects.ContainerObj;
import eidolons.game.module.dungeoncrawl.objects.Door;
import eidolons.game.module.dungeoncrawl.objects.InteractiveObj;
import eidolons.game.module.dungeoncrawl.objects.LockObj;
import eidolons.game.module.dungeoncrawl.quest.advanced.Quest;
import eidolons.game.netherflame.boss.logic.entity.BossUnit;
import main.content.CONTENT_CONSTS;
import main.content.DC_TYPE;
import main.content.enums.entity.BfObjEnums;
import main.content.enums.entity.BfObjEnums.BF_OBJECT_GROUP;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.entity.EntityCheckMaster;
import main.entity.Ref;
import main.entity.type.ObjAtCoordinate;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
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


    public BattleFieldObject createUnit(ObjType type, int x, int y, Player owner, Ref ref) {
        if (!CoreEngine.isArcaneVault()) {
            if (!CoreEngine.isLevelEditor()) {
                if (!type.isGenerated() && DC_Engine.isUseCustomTypesAlways()) {
                    type = new ObjType(type);
                    game.initType(type);
                }
            }
        }
        if (!CoreEngine.isArcaneVault())
            type = checkTypeSubstitution(type, ref);

        BattleFieldObject obj = null;

        if (type.checkProperty(G_PROPS.BF_OBJECT_GROUP, BfObjEnums.BF_OBJECT_GROUP.ENTRANCE.toString())) {
            obj = new Entrance(x, y, type, game);
        } else if (type.getOBJ_TYPE_ENUM() == DC_TYPE.BF_OBJ) {
            obj = newStructure(type, x, y, owner, ref);
            game.getObjMaster().clearCache(obj.getCoordinates());
        } else {
            if (EntityCheckMaster.isBoss(type)) {
                obj = new BossUnit(type, x, y, owner, getGame(), ref);
            } else
                obj = new Unit(type, x, y, owner, getGame(), ref);
        }
        //if (WaitMaster.getCompleteOperations().contains(WAIT_OPERATIONS.DUNGEON_SCREEN_READY))
        if (!EntityCheckMaster.isBoss(type)) {
            GuiEventManager.triggerWithMinDelayBetween(GuiEventType.UNIT_CREATED, obj, 500);
        }
        game.getState().addObject(obj);
        if (game.isStarted()) {
            Coordinates coordinates = Coordinates.get(x, y);
            game.getObjMaster().clearCache(coordinates);
            game.getCellByCoordinate(coordinates).resetObjectArrays();
            getGame().getVisionMaster().getIllumination().setResetRequired(true);
            DC_GameState.gridChanged=true;
        }
        initObject(obj, type);

        if (getGame().getMetaMaster().isRngQuestsEnabled())
            for (Quest quest : game.getMetaMaster().getQuestMaster().getQuestsPool()) {
                if (quest.getArg() instanceof ObjAtCoordinate) {
                    if (((ObjAtCoordinate) quest.getArg()).getType().equalsAsBaseType(obj.getType())) {
                        if (((ObjAtCoordinate) quest.getArg()).getCoordinates().equals(obj.getCoordinates())) {
                            quest.setArg(obj);
                        }

                    }

                }
            }

        if (CoreEngine.isLevelEditor()) {
            Module module = getGame().getMetaMaster().getModuleMaster().getModule(obj.getCoordinates());
            obj.setModule(module);
        } else if (getGame().getModule() != null) {
            obj.setModule(getGame().getModule());
        }
        CONTENT_CONSTS.FLIP flip = game.getFlipMap().get(obj.getCoordinates());
        obj.setFlip(flip);
        return obj;

    }

    protected void initObject(BattleFieldObject obj, ObjType type) {

        if (!isUnitFullResetRequired(obj))
            dummyReset(obj);
        else if (obj instanceof Unit) {
            game.getState().getManager().reset((Unit) obj);
        } else {
            obj.toBase();
            obj.resetObjects();
            obj.afterEffects();
        }
        obj.setOriginalType(type.getType());
    }

    private void dummyReset(BattleFieldObject obj) {
        obj.resetPercentages();
    }

    protected boolean isUnitFullResetRequired(BattleFieldObject obj) {
        if (CoreEngine.isLevelEditor()) {
            return false;
        }
        return obj.isPlayerCharacter();
        //        if (!getGame().getMetaMaster().getModuleMaster().isWithinModule(obj.getCoordinates()))
        //            return false;

        //TODO check distance

    }

    private ObjType checkTypeSubstitution(ObjType type, Ref ref) {
        if (CoreEngine.isLevelEditor()) {
            if (type.getName().contains("Wall Placeholder")) {
                if (type.getName().contains("Alt"))
                    return DataManager.getType("Wall Alt", DC_TYPE.BF_OBJ);
                return DataManager.getType("Wall", DC_TYPE.BF_OBJ);
            }
            return type;
        }
        if (!type.getName().split(" ")[0].equalsIgnoreCase(PLACEHOLDER)) {
            return type;
        }
        String unitGroup = type.getProperty(
                type.getOBJ_TYPE_ENUM().getSubGroupingKey());
        List<ObjType> list = DataManager.getTypesSubGroup(type.getOBJ_TYPE_ENUM(), unitGroup);

        String group = type.getGroup();
        if (!group.isEmpty()) {
            list.removeIf(t -> !t.getGroup().equalsIgnoreCase(group));
        }
        //power constraints?

        if (list.isEmpty())
            return type;
        type = new RandomWizard<ObjType>().getRandomListItem(list);
        return type;
    }


    private BattleFieldObject newStructure(ObjType type, int x, int y, Player owner,
                                           Ref ref) {

        //TODO no more overlaying obj decor!!!
        if (EntityCheckMaster.isOverlaying(type)) {
            return new InteractiveObj(type, x, y);
        }
        BF_OBJECT_GROUP group = new EnumMaster<BF_OBJECT_GROUP>().retrieveEnumConst(BF_OBJECT_GROUP.class,
                type.getProperty(G_PROPS.BF_OBJECT_GROUP));
        if (!CoreEngine.isLevelEditor())
            if (group != null) {
                switch (group) {
                    case DUNGEON:
                        break;

                    case KEY:
                    case HANGING:
                        return new InteractiveObj(type, x, y);
                    case DOOR:
                        return new Door(type, x, y, owner, getGame(), ref);
                    case LOCK:
                        return new LockObj(type, x, y, owner, getGame(), ref);
                    case TREASURE:
                    case CONTAINER:
                    case GRAVES:
                    case REMAINS:
                    case INTERIOR:
                        return new ContainerObj(type, x, y);
                }
            }



        return new Structure(type, x, y, owner, getGame(), ref);
    }

}
