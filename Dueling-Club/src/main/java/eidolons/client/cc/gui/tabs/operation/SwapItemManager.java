package eidolons.client.cc.gui.tabs.operation;

import eidolons.client.cc.gui.lists.dc.DC_InventoryManager;
import eidolons.game.core.game.DC_Game;
import eidolons.system.ObjUtilities;
import eidolons.client.cc.CharacterCreator;
import main.content.C_OBJ_TYPE;
import eidolons.content.PROPS;
import main.content.values.properties.PROPERTY;
import main.entity.Entity;
import eidolons.entity.item.DC_HeroItemObj;
import main.entity.type.ObjType;

//will it support Undo?

public class SwapItemManager extends DC_InventoryManager {

    public SwapItemManager(DC_Game game) {
        super(game);
    }

    @Override
    public void removeType(Entity type, PROPERTY p) {
        if (!hasOperations()) {
            return;
        }

        DC_HeroItemObj item = (DC_HeroItemObj) ObjUtilities.findObjByType(type, getHero()
         .getInventory());
        getHero().removeFromInventory(item);
        // getHero().setInventory(null);
        getHero().getGame().getDroppedItemManager().drop(item, getHero());
        operationDone(OPERATIONS.DROP, type.getName());

        CharacterCreator.getHeroManager().update(getHero());

    }

    @Override
    public boolean addType(ObjType type, boolean alt) {
        if (!hasOperations()) {
            return false;
        }
        boolean result = CharacterCreator.getHeroManager().addItem(getHero(), type,
         C_OBJ_TYPE.ITEMS, PROPS.INVENTORY);

        CharacterCreator.getHeroManager().update(getHero());
        operationDone(OPERATIONS.PICK_UP, type.getName());
        return result;
    }

}
