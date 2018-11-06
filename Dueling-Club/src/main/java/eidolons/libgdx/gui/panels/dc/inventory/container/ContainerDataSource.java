package eidolons.libgdx.gui.panels.dc.inventory.container;

import com.badlogic.gdx.graphics.Color;
import eidolons.content.PARAMS;
import eidolons.entity.item.DC_HeroItemObj;
import eidolons.entity.obj.DC_Cell;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.module.dungeoncrawl.objects.ContainerObj;
import eidolons.libgdx.GdxColorMaster;
import eidolons.libgdx.gui.panels.dc.inventory.InvItemActor;
import eidolons.libgdx.gui.panels.dc.inventory.InventoryClickHandler;
import eidolons.libgdx.gui.panels.dc.inventory.InventoryClickHandler.CELL_TYPE;
import eidolons.libgdx.gui.panels.dc.inventory.InventoryFactory;
import eidolons.libgdx.gui.panels.dc.inventory.InventorySlotsPanel;
import eidolons.libgdx.gui.panels.dc.inventory.container.ContainerPanel.ITEM_FILTERS;
import eidolons.libgdx.gui.panels.dc.inventory.datasource.InventoryTableDataSource;
import main.entity.obj.Obj;
import main.system.auxiliary.data.ListMaster;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 11/16/2017.
 */
public class ContainerDataSource implements InventoryTableDataSource {

    protected final Unit unit;
    protected List<DC_HeroItemObj> items;
    protected InventoryFactory factory;
    protected Obj obj;
    protected ContainerClickHandler handler;
    protected ITEM_FILTERS filter;

    public ContainerDataSource(Obj obj, Unit unit) {
        this.obj = obj;
        this.items = null;
        this.unit = unit;
        if (obj instanceof ContainerObj) {
            items = ((ContainerObj) obj).getItems();
        } else if (obj instanceof DC_Cell) {
            unit.getGame().getDroppedItemManager().reset(obj.getX(), obj.getY());
            items = unit.getGame().getDroppedItemManager().getDroppedItems(obj);
        }
        this.handler = createClickHandler();
        this.factory = new InventoryFactory(handler);
    }

    protected ContainerClickHandler createClickHandler() {
        return new ContainerClickHandler(obj.getImagePath(), items, unit, obj);// obj.getGame().getInventoryManager().getClickHandler();
    }


    public ContainerClickHandler getHandler() {
        return handler;
    }

    @Override
    public void setFilter(ITEM_FILTERS filter) {
        this.filter = filter;
    }

    @Override
    public InventoryClickHandler getClickHandler() {
        return handler;
    }

    @Override
    public List<InvItemActor> getInventorySlots() {
        if (items == null) {
            items = new ArrayList<>();
        }
        ListMaster.fillWithNullElements(items
         , InventorySlotsPanel.SIZE);
        return factory.getList(items, CELL_TYPE.CONTAINER);
    }

    public Color getPricesColor() {
        if (unit.getIntParam(PARAMS.GOLD_COST_REDUCTION) +
         obj.getIntParam(PARAMS.GOLD_COST_REDUCTION) < 0) {
            return GdxColorMaster.RED;
        }
        if (unit.getIntParam(PARAMS.GOLD_COST_REDUCTION) +
         obj.getIntParam(PARAMS.GOLD_COST_REDUCTION) > 25) {
            return Color.GREEN;
        }
        return GdxColorMaster.getDefaultTextColor();
    }

    public String getPricesInfo() {
        return "";
    }

    public String getWeightInfo() {
        return items.stream().mapToInt(item -> item == null ? 0 : item.getIntParam(PARAMS.WEIGHT)).sum() +
         "lb";
    }
    @Override
    public int getPrice(DC_HeroItemObj model, CELL_TYPE cellType) {
        return model.getIntParam(PARAMS.GOLD_COST);
    }
    public String getGoldInfo() {
        return obj.getIntParam(PARAMS.GOLD) + "gp";
    }
}
