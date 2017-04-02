package main.libgdx.gui.panels.dc.inventory.containers;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import main.entity.Entity;
import main.libgdx.gui.panels.dc.inventory.InventoryClickHandler.CELL_TYPE;
import main.libgdx.gui.panels.dc.inventory.InventoryClickHandlerImpl;
import main.libgdx.texture.TextureCache;
import main.system.datatypes.DequeImpl;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by JustMe on 3/31/2017.
 */
public class InventoryValueContainerFactory {
    private InventoryClickHandlerImpl handler;

    public InventoryValueContainerFactory(InventoryClickHandlerImpl inventoryClickHandler) {
        this.handler = inventoryClickHandler;
    }

    public InventoryValueContainer get(Entity entity, CELL_TYPE cellType) {
        InventoryValueContainer container = new InventoryValueContainer(
        entity==null ? getEmptyImageForCell(cellType) :
         TextureCache.getOrCreateR(  entity.getImagePath()));
        container.setEntity(entity);
        container.setCellType(cellType);
        container.setHandler(handler);
        return container;
    }

    private TextureRegion getEmptyImageForCell(CELL_TYPE cellType) {

        return  TextureCache.getOrCreateR(  cellType.getSlotImagePath());
    }

    public List<InventoryValueContainer> getList(DequeImpl<? extends Entity> items,
                                                 CELL_TYPE type) {
        List<InventoryValueContainer> list = new LinkedList<>();
       items.forEach(item -> list.add(
          get(item, type)));
        return list;
    }
}
