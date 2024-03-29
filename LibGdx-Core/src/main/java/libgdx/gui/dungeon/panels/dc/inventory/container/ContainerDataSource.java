package libgdx.gui.dungeon.panels.dc.inventory.container;

import com.badlogic.gdx.graphics.Color;
import eidolons.content.PARAMS;
import eidolons.content.consts.VisualEnums;
import eidolons.entity.item.HeroItem;
import eidolons.entity.obj.GridCell;
import eidolons.entity.unit.Unit;
import eidolons.game.exploration.dungeon.objects.ContainerObj;
import eidolons.content.consts.libgdx.GdxColorMaster;
import libgdx.gui.dungeon.panels.dc.inventory.InvItemActor;
import libgdx.gui.dungeon.panels.dc.inventory.InventoryClickHandler;
import eidolons.content.consts.VisualEnums.CELL_TYPE;
import libgdx.gui.dungeon.panels.dc.inventory.InventoryFactory;
import libgdx.gui.dungeon.panels.dc.inventory.InventorySlotsPanel;
import libgdx.gui.dungeon.panels.dc.inventory.datasource.InventoryTableDataSource;
import main.entity.obj.Obj;
import main.system.auxiliary.data.ListMaster;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 11/16/2017.
 */
public class ContainerDataSource implements InventoryTableDataSource {

    protected final Unit unit;
    protected List<HeroItem> items;
    protected InventoryFactory factory;
    protected Obj obj;
    protected ContainerClickHandler handler;
    protected VisualEnums.ITEM_FILTERS filter;

    public ContainerDataSource(Obj obj, Unit unit) {
        this.obj = obj;
        this.items = null;
        this.unit = unit;
        if (obj instanceof ContainerObj) {
            items = ((ContainerObj) obj).getItems();
        } else if (obj instanceof GridCell) {
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
    public void setFilter(VisualEnums.ITEM_FILTERS filter) {
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
        return factory.getList(items, VisualEnums.CELL_TYPE.CONTAINER);
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
    public int getPrice(HeroItem model, CELL_TYPE cellType) {
        return model.getIntParam(PARAMS.GOLD_COST);
    }
    public String getGoldInfo() {
        return obj.getIntParam(PARAMS.GOLD) + "gp";
    }
}
