package main.libgdx.gui.panels.dc.inventory.container;

import main.client.cc.gui.lists.dc.DC_InventoryManager.OPERATIONS;
import main.entity.Entity;
import main.entity.item.DC_HeroItemObj;
import main.entity.obj.unit.Unit;
import main.game.module.dungeoncrawl.objects.ContainerObj;
import main.libgdx.anims.text.FloatingTextMaster;
import main.libgdx.anims.text.FloatingTextMaster.TEXT_CASES;
import main.libgdx.gui.panels.dc.inventory.InventoryClickHandlerImpl;
import main.libgdx.gui.panels.dc.inventory.datasource.InventoryDataSource;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.LinkedList;

/**
 * Created by JustMe on 11/16/2017.
 */
public class ContainerClickHandler extends InventoryClickHandlerImpl {
    private final ContainerObj container;

    public ContainerClickHandler(ContainerObj obj, Unit unit) {
        super(unit);
        this.container = obj;
    }

    @Override
    public boolean cellClicked(CELL_TYPE cell_type, int clickCount, boolean rightClick, boolean altClick, Entity cellContents) {
        boolean result = false;
//        if (Eidolons.game.getInventoryManager().tryExecuteOperation
//         (OPERATIONS.PICK_UP, cellContents)) {
//            dirty = true;
//            result = true;
//        }
        if (cellContents==null )
        {
            GuiEventManager.trigger(GuiEventType.SHOW_LOOT_PANEL,
             null );
            return false;
        }
        if (unit.isInventoryFull())
        {
            FloatingTextMaster.getInstance().createFloatingText(TEXT_CASES.DEFAULT,
             "Inventory is full!",unit);
            return false;
        }
        DC_HeroItemObj item = (DC_HeroItemObj) cellContents;
        container.getItems().remove(item);
        unit.addItemToInventory(item);
        result = true;

        if (result) {
            Pair<InventoryDataSource, ContainerDataSource> param =
             new ImmutablePair<>(new InventoryDataSource(unit), new ContainerDataSource(container, unit));
            GuiEventManager.trigger(GuiEventType.SHOW_LOOT_PANEL, param);
            GuiEventManager.trigger(GuiEventType.SHOW_LOOT_PANEL,
             param);
        }
        return result;
    }

    public void takeAllClicked() {
    for (DC_HeroItemObj item:         new LinkedList<>(container.getItems()) ){
        if (unit.isInventoryFull())
        {
            FloatingTextMaster.getInstance().createFloatingText(TEXT_CASES.DEFAULT,
             "Inventory is full!",unit);
            return;
        }
        container.getItems().remove(item);
        unit.addItemToInventory(item);
    }
    GuiEventManager.trigger(GuiEventType.SHOW_LOOT_PANEL,
         null );
    }

    @Override
    protected OPERATIONS getOperation(CELL_TYPE cell_type, int clickCount, boolean rightClick, boolean altClick, Entity cellContents) {
        return OPERATIONS.PICK_UP;//super.getOperation(cell_type, clickCount, rightClick, altClick, cellContents);
    }

    @Override
    protected String getArg(CELL_TYPE cell_type, int clickCount, boolean rightClick, boolean altClick, Entity cellContents) {
        return super.getArg(cell_type, clickCount, rightClick, altClick, cellContents);
    }

    public ContainerObj getContainer() {
        return container;
    }
}
