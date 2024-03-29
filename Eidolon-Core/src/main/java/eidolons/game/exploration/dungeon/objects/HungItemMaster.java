package eidolons.game.exploration.dungeon.objects;

import eidolons.entity.feat.active.ActiveObj;
import eidolons.entity.feat.active.UnitAction;
import eidolons.entity.item.HeroItem;
import eidolons.entity.item.QuickItem;
import eidolons.entity.item.ItemFactory;
import eidolons.entity.obj.Structure;
import eidolons.entity.unit.Unit;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonMaster;
import eidolons.game.battlecraft.logic.mission.universal.DC_Player;
import eidolons.game.exploration.dungeon.objects.HungItemMaster.HUNG_ITEM_ACTION;
import main.content.DC_TYPE;
import main.content.enums.entity.BfObjEnums.BF_OBJECT_TAGS;
import main.content.enums.rules.VisionEnums.VISIBILITY_LEVEL;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.entity.Ref;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.game.core.game.GenericGame;
import main.system.auxiliary.StringMaster;
import main.system.math.PositionMaster;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 10/19/2017.
 */
public class HungItemMaster extends DungeonObjMaster<HUNG_ITEM_ACTION> {

    private static final String SPRITE = " sprite";
    static ObjType dummyHungObjType;
    private static final String typeName = "Dummy Hung Obj";

    public HungItemMaster(DungeonMaster dungeonMaster) {
        super(dungeonMaster);
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
         BF_OBJECT_TAGS.ITEM.toString() + StringMaster.getSeparator() +
          BF_OBJECT_TAGS.OVERLAYING.toString());
// so the sprite will be 50% size as always?

/*
weapons
keys
potions
 */

        return type;
    }

    @Override
    protected boolean actionActivated(HUNG_ITEM_ACTION sub,
                                      Unit unit, DungeonObj obj) {
        if (!(obj instanceof HungItem)) return false;

        HungItem hungObj = ((HungItem) obj);
        switch (sub) {
            case TAKE:
                HeroItem item = hungObj.getItem();
                if (item == null) {
                    item = generateItem(hungObj);
                }
                //quick slot?
                if (!unit.isQuickSlotsFull()
                 && item instanceof QuickItem) {
                    unit.addQuickItem((QuickItem) item);
                } else {
                    unit.addItemToInventory(item);
                }
                // GuiEventManager.trigger(GuiEventType.ITEM_TAKEN, hungObj);
                //take animation
                obj.kill(obj, false, true);
                break;
            case USE:
                break;
        }
        return true;
    }

    private HeroItem generateItem(HungItem hungObj) {
        Ref ref = new Ref();
        return ItemFactory.createItemObj(hungObj.getItemType(), DC_Player.NEUTRAL,
         (GenericGame) ref.getGame(), ref, false);
    }

    public List<ActiveObj> getActions(DungeonObj obj, Unit unit) {
        if (!(obj instanceof HungItem))
            return new ArrayList<>();
        //check intelligence, mastery
        List<ActiveObj> list = new ArrayList<>();
        UnitAction action;
        for (HUNG_ITEM_ACTION sub : HUNG_ITEM_ACTION.values()) {
            if (checkAction(unit, (HungItem) obj, sub)) {
                String name = StringMaster.format(sub.name()) + " Door";
                action = unit.getAction(name);
                if (action == null)
                    action = createAction(sub, unit, name, obj);
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
                 hungItem.getCoordinates()) > 1)
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
    public ActiveObj getDefaultAction(Unit source, DungeonObj target) {
        return null;
    }

    public enum HUNG_ITEM_ACTION implements DUNGEON_OBJ_ACTION {
        TAKE,
        USE,
    }
}
