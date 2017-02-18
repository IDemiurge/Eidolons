package main.client.cc.gui.lists.dc;

import main.client.cc.gui.lists.HeroListPanel;
import main.client.cc.gui.lists.ItemListManager;
import main.client.cc.gui.lists.dc.InvListManager.OPERATIONS;
import main.content.OBJ_TYPE;
import main.content.DC_TYPE;
import main.content.PROPS;
import main.content.values.properties.PROPERTY;
import main.entity.obj.unit.Unit;
import main.game.core.game.DC_Game;

/**
 * Inventory, spellbook...
 *
 * @author JustMe
 */

public class DC_ItemListManager extends ItemListManager {

    protected Integer numberOfOperations;

    public DC_ItemListManager(DC_Game game, boolean inv) {
        this(game.getManager().getActiveObj(), (inv) ? DC_TYPE.ITEMS : DC_TYPE.SPELLS,
                (inv) ? PROPS.INVENTORY : PROPS.MEMORIZED_SPELLS);

    }

    public DC_ItemListManager(Unit hero, OBJ_TYPE TYPE, PROPERTY PROP) {
        super(hero, TYPE, PROP);
    }

    @Override
    protected boolean isHeroList(HeroListPanel hlp) {
        return true;
    }

    public boolean operationDone(OPERATIONS operation, String string) {
        setNumberOfOperations(getNumberOfOperations() - 1);
        return hasOperations();
    }

    public boolean operationDone(int n, OPERATIONS operation, String string) {
        setNumberOfOperations(getNumberOfOperations() - n);
        return hasOperations();
    }

    public boolean hasOperations(int n) {
        return getNumberOfOperations() >= n;
    }

    public boolean hasOperations() {
        return getNumberOfOperations() > 0;
    }

    public Integer getNumberOfOperations() {
        return numberOfOperations;
    }

    public void setNumberOfOperations(Integer numberOfOperations) {
        this.numberOfOperations = numberOfOperations;
    }

}
