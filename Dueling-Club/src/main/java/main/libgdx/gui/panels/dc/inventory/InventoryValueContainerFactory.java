package main.libgdx.gui.panels.dc.inventory;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import main.entity.Entity;
import main.libgdx.gui.panels.dc.inventory.InventoryClickHandler.CELL_TYPE;
import main.libgdx.texture.TextureCache;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 3/31/2017.
 */
public class InventoryValueContainerFactory {
    private InventoryClickHandler handler;

    public InventoryValueContainerFactory(InventoryClickHandler  inventoryClickHandler) {
        this.handler = inventoryClickHandler;
    }

    public InventoryValueContainer get(Entity entity, CELL_TYPE cellType) {
        InventoryValueContainer container = new InventoryValueContainer(

        entity==null ? getEmptyImageForCell(cellType) :
         TextureCache.getOrCreateR(  entity.getImagePath())
        , entity==null ? "Empty" : entity.getName()
        );
        container.setEntity(entity);
        container.setCellType(cellType);
        container.setHandler(handler);
        return container;
    }

    private TextureRegion getEmptyImageForCell(CELL_TYPE cellType) {

        return  TextureCache.getOrCreateR(  cellType.getSlotImagePath());
    }

    public List<InventoryValueContainer> getList(Collection<? extends Entity> items,
                                                 CELL_TYPE type) {
        List<InventoryValueContainer> list = new ArrayList<>();
       items.forEach(item -> list.add(
          get(item, type)));
        return list;
    }
}
