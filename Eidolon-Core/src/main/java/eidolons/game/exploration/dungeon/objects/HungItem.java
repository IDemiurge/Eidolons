package eidolons.game.exploration.dungeon.objects;

import eidolons.entity.item.HeroItem;
import main.entity.type.ObjType;

/**
 * Created by JustMe on 10/19/2017.
 */
public class HungItem extends DungeonObj {


    private HeroItem item;
    private ObjType itemType;

    public HungItem(ObjType type, int x, int y, ObjType item) {
        super(type, x, y);
        itemType = item;
    }

    @Override
    public DUNGEON_OBJ_TYPE getDungeonObjType() {
        return DUNGEON_OBJ_TYPE.ITEM;
    }

    public HeroItem getItem() {
        return item;
    }

    public void setItem(HeroItem item) {
        this.item = item;
    }

    public ObjType getItemType() {
        return itemType;
    }

    public void setItemType(ObjType itemType) {
        this.itemType = itemType;
    }
}
