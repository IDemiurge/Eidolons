package main.client.cc.gui.tabs.operation;

import main.client.cc.CharacterCreator;
import main.client.cc.gui.lists.HeroListPanel;
import main.client.cc.gui.lists.dc.InvListManager;
import main.content.C_OBJ_TYPE;
import main.content.PROPS;
import main.content.values.properties.PROPERTY;
import main.entity.Entity;
import main.entity.item.DC_HeroItemObj;
import main.entity.type.ObjType;
import main.game.core.game.DC_Game;
import main.system.ObjUtilities;

//will it support Undo?

public class SwapItemManager extends InvListManager {

    public SwapItemManager(DC_Game game) {
        super(game);
    }

    @Override
    protected void removeType(Entity type, HeroListPanel hlp, PROPERTY p) {
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
    protected boolean addType(ObjType type, HeroListPanel hlp, boolean alt) {
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
