package eidolons.libgdx.gui.panels.dc.inventory;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import eidolons.entity.item.DC_WeaponObj;
import eidolons.libgdx.gui.UiMaster;
import eidolons.libgdx.gui.panels.dc.inventory.InventoryClickHandler.CELL_TYPE;
import eidolons.libgdx.texture.TextureCache;
import main.content.C_OBJ_TYPE;
import main.data.filesys.PathFinder;
import main.entity.Entity;
import main.system.auxiliary.StrPathBuilder;
import main.system.images.ImageManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by JustMe on 3/31/2017.
 */
public class InventoryFactory {
    private InventoryClickHandler handler;

    public InventoryFactory(InventoryClickHandler inventoryClickHandler) {
        this.handler = inventoryClickHandler;
    }

    public InventoryValueContainer get(Entity entity, CELL_TYPE cellType) {
        int size = UiMaster.getIconSize();
        String path = getWeaponIconPath(entity) ;
        if (entity != null) {
            if (!C_OBJ_TYPE.ITEMS.equals(entity.getOBJ_TYPE_ENUM())) {
                size = 128;
            }
        }
        InventoryValueContainer container = new InventoryValueContainer(

         entity == null ? getEmptyImageForCell(cellType) :
          TextureCache.getOrCreateSizedRegion(size, path)
         , entity == null ? "Empty" : entity.getName()
        );

        container.setEntity(entity);
        container.setCellType(cellType);
        container.setHandler(handler);
        return container;
    }

    public static String getWeaponIconPath(Entity entity) {
        if (entity == null) {
            return "";
        }
        String path = entity.getImagePath();
        if (entity instanceof DC_WeaponObj) {

            path = StrPathBuilder.build(PathFinder.getItemIconPath(),
             ((DC_WeaponObj) entity).getBaseTypeName() + ".png");
            if (!ImageManager.isImage(path))
                path = entity.getImagePath();
        }
        return path;
    }

    private TextureRegion getEmptyImageForCell(CELL_TYPE cellType) {

        return TextureCache.getOrCreateR(cellType.getSlotImagePath());
    }

    public List<InventoryValueContainer> getList(Collection<? extends Entity> items,
                                                 CELL_TYPE type) {
        List<InventoryValueContainer> list = new ArrayList<>();
        items.forEach(item -> list.add(
         get(item, type)));
        return list;
    }
}
