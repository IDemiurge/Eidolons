package main.game.module.dungeoncrawl.objects;

import main.content.DC_TYPE;
import main.content.enums.entity.BfObjEnums.BF_OBJECT_TAGS;
import main.content.enums.rules.VisionEnums.VISIBILITY_LEVEL;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.entity.Ref;
import main.entity.active.DC_ActiveObj;
import main.entity.active.DC_UnitAction;
import main.entity.item.DC_HeroItemObj;
import main.entity.item.DC_QuickItemObj;
import main.entity.item.ItemFactory;
import main.entity.obj.Structure;
import main.entity.obj.unit.Unit;
import main.entity.type.ObjType;
import main.game.battlecraft.logic.battle.universal.DC_Player;
import main.game.module.dungeoncrawl.objects.HungItemMaster.HUNG_ITEM_ACTION;
import main.game.battlecraft.logic.dungeon.universal.DungeonMaster;
import main.game.bf.Coordinates;
import main.game.core.game.MicroGame;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.StringMaster;
import main.system.math.PositionMaster;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 10/19/2017.
 */
public class HungItemMaster extends DungeonObjMaster<HUNG_ITEM_ACTION> {

    private static   final String SPRITE = " sprite";
    static  ObjType  dummyHungObjType;
    private static String typeName = "Dummy Hung Obj";

    public enum HUNG_ITEM_ACTION implements DUNGEON_OBJ_ACTION {
        TAKE,
        USE,
    }
    public HungItemMaster(DungeonMaster dungeonMaster) {
        super(dungeonMaster);
    }

    @Override
    protected boolean actionActivated(HUNG_ITEM_ACTION sub,
                                      Unit unit, DungeonObj obj) {
        if (!(obj instanceof HungItem)) return false;

            HungItem  hungObj = ((HungItem) obj);
        switch (sub) {
            case TAKE:
                DC_HeroItemObj item = hungObj.getItem();
                if (item == null) {
                    item = generateItem(hungObj);
                }
                //quick slot?
                if (!unit.isQuickSlotsFull()
                 && item instanceof DC_QuickItemObj){
                    unit.addQuickItem((DC_QuickItemObj) item);
                }
                else {
                    unit.addItemToInventory(item);
                }
                GuiEventManager.trigger(GuiEventType.ITEM_TAKEN, hungObj);
                //take animation
                obj.kill(obj, false, true);
                break;
            case USE:
                break;
        }
        return true;
    }

    private DC_HeroItemObj generateItem(HungItem hungObj) {
        Ref ref = new Ref();
        DC_HeroItemObj itemObj =
         ItemFactory.createItemObj(hungObj.getItemType(), DC_Player.NEUTRAL,
          (MicroGame) ref.getGame(), ref, false);
        return itemObj;
    }


    public static Structure createBfObjForItem(ObjType item, Coordinates c) {
//
        ObjType bfType = getOrCreateBfType(item);
        return new HungItem(bfType, c.x, c.y, item);
    }

    private static ObjType getOrCreateBfType(ObjType item) {
        if (dummyHungObjType == null)
            dummyHungObjType = DataManager.getType(typeName, DC_TYPE.BF_OBJ);
        ObjType type = new ObjType(dummyHungObjType);
        type.setName(item.getName());
        String imagePath = StringMaster.cropFormat(item.getImagePath()) +
         SPRITE + ".png";
        type.setImage(imagePath);
        type.setProperty(G_PROPS.BF_OBJECT_TAGS,
         BF_OBJECT_TAGS.ITEM.toString() + StringMaster.getSeparator()+
         BF_OBJECT_TAGS.OVERLAYING.toString());
// so the sprite will be 50% size as always?

/*
weapons
keys
potions
 */

        return type;
    }

    public List<DC_ActiveObj> getActions(DungeonObj obj, Unit unit) {
        if (!(obj instanceof HungItem))
            return new ArrayList<>();
        //check intelligence, mastery
        List<DC_ActiveObj> list = new ArrayList<>();
        DC_UnitAction action = null;
        for (HUNG_ITEM_ACTION sub : HUNG_ITEM_ACTION.values()) {
            if (checkAction(unit, (HungItem) obj, sub)) {
                String name = StringMaster.getWellFormattedString(sub.name()) + " Door";
                action = unit.getAction(name);
                if (action==null )
                    action = createAction(sub, unit,name, obj);
                if (action != null) {
                    list.add(action);

                }
            }
        }
        return list;
    }

    private boolean checkAction(Unit unit, HungItem hungItem, HUNG_ITEM_ACTION sub) {
        switch (sub) {
            case TAKE:
                if (PositionMaster.getExactDistance(unit.getCoordinates(),
                 hungItem.getCoordinates())>1)
                    return false;
                return !(hungItem.getVisibilityLevel() == VISIBILITY_LEVEL.CONCEALED
                 || hungItem.getVisibilityLevel() == VISIBILITY_LEVEL.BLOCKED);
            case USE:
                return false;
        }
        return false;
    }

    @Override
    public void open(DungeonObj obj, Ref ref) {

    }

    @Override
    public DC_ActiveObj getDefaultAction(Unit source, DungeonObj target) {
        return null;
    }
}
